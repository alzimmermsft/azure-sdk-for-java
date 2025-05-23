// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.management.implementation.polling;

import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.polling.PollResult;
import com.azure.core.util.Context;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.FluxUtil;
import com.azure.core.util.logging.ClientLogger;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.PollingContext;
import com.azure.core.util.serializer.SerializerAdapter;
import com.azure.core.util.serializer.SerializerEncoding;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Poll operation for Azure resource manager (ARM) long-running-operation (LRO).
 */
public final class PollOperation {
    private static final ClientLogger LOGGER = new ClientLogger(PollOperation.class);
    private static final LongRunningOperationStatus LRO_CANCELLED
        = LongRunningOperationStatus.fromString("Cancelled", true);

    /**
     * Get a Function that polls provisioning state of ARM resource.
     *
     * @param serializerAdapter the serializer for any encoding and decoding
     * @param pipeline the HttpPipeline for making poll request
     * @param pollResultType the type of the poll result
     * @param context the context
     * @param <T> the type of poll result type
     * @return the ARM poll function
     */
    public static <T> Function<PollingContext<PollResult<T>>, Mono<PollResponse<PollResult<T>>>>
        pollFunction(SerializerAdapter serializerAdapter, HttpPipeline pipeline, Type pollResultType, Context context) {
        return pollingContext -> {
            PollingState pollingState = PollingState.from(serializerAdapter, pollingContext);
            if (pollingState.getOperationStatus().isComplete()) {
                return Mono.defer(
                    () -> Mono.just(pollResponseFromPollingState(serializerAdapter, pollResultType, pollingState)));
            } else {
                // InProgress|NonTerminal-Status
                return doSinglePoll(pipeline, pollingState, context).flatMap(updatedState -> {
                    updatedState.store(pollingContext);
                    return Mono.defer(
                        () -> Mono.just(pollResponseFromPollingState(serializerAdapter, pollResultType, updatedState)));
                });
            }
        };
    }

    /**
     * Currently there is no option to cancel an ARM LRO in generic way, this is NOP.
     *
     * @param context the context
     * @param <T> the type of poll result type
     * @return cancel Function
     */
    public static <T> BiFunction<PollingContext<PollResult<T>>, PollResponse<PollResult<T>>, Mono<PollResult<T>>>
        cancelFunction(Context context) {
        return (pollingContext, response) -> Mono.empty();
    }

    /**
     * Get a Function that retrieves final result of a LRO.
     *
     * @param serializerAdapter the serializer for any encoding and decoding
     * @param pipeline the HttpPipeline for fetching final result
     * @param finalResultType the final result type
     * @param context the context
     * @param <T> the final result type
     * @param <U> the poll result type
     * @return retrieve final LRO result Function
     */
    public static <T, U> Function<PollingContext<PollResult<T>>, Mono<U>> fetchResultFunction(
        SerializerAdapter serializerAdapter, HttpPipeline pipeline, Type finalResultType, Context context) {
        return pollingContext -> {
            PollingState pollingState = PollingState.from(serializerAdapter, pollingContext);
            FinalResult finalResult = pollingState.getFinalResult();
            if (finalResult == null) {
                return Mono.empty();
            } else {
                String value = finalResult.getResult();
                if (value != null) {
                    U result = deserialize(serializerAdapter, value, finalResultType);
                    return result != null ? Mono.just(result) : Mono.empty();
                } else {
                    return FluxUtil.fluxContext(fluxContext -> {
                        fluxContext = CoreUtils.mergeContexts(fluxContext, context);

                        return pipeline
                            .send(decorateRequest(new HttpRequest(HttpMethod.GET, finalResult.getResultUri())),
                                fluxContext)
                            .flux();
                    })
                        .next()
                        .flatMap((Function<HttpResponse, Mono<String>>) response -> response.getBodyAsString())
                        .flatMap(body -> {
                            U result = deserialize(serializerAdapter, body, finalResultType);
                            return result != null ? Mono.just(result) : Mono.empty();
                        });
                }
            }
        };
    }

    /**
     * Do a poll to retrieve the LRO status.
     *
     * @param pipeline the HttpPipeline for making poll request
     * @param pollingState the current PollingState
     * @param context the context
     * @return a Mono emitting PollingState updated from the poll operation response
     */
    private static Mono<PollingState> doSinglePoll(HttpPipeline pipeline, PollingState pollingState, Context context) {
        return FluxUtil.fluxContext(fluxContext -> {
            fluxContext = CoreUtils.mergeContexts(fluxContext, context);

            return pipeline
                .send(decorateRequest(new HttpRequest(HttpMethod.GET, pollingState.getPollUrl())), fluxContext)
                .flux();
        })
            .next()
            .flatMap((Function<HttpResponse, Mono<PollingState>>) response -> response.getBodyAsString()
                .map(body -> pollingState.update(response.getStatusCode(), response.getHeaders(), body))
                .switchIfEmpty(Mono
                    .fromSupplier(() -> pollingState.update(response.getStatusCode(), response.getHeaders(), null))));
    }

    /**
     * Gets the latest poll response from PollingState.
     *
     * @param serializerAdapter the serializer for any encoding and decoding
     * @param pollResultType the poll result type
     * @param pollingState the polling state
     * @param <T> the poll result type
     * @return the latest poll response
     */
    static <T> PollResponse<PollResult<T>> pollResponseFromPollingState(SerializerAdapter serializerAdapter,
        Type pollResultType, PollingState pollingState) {
        if (pollingState.getOperationStatus().isComplete()) {
            if (pollingState.getOperationStatus() == LongRunningOperationStatus.FAILED
                || pollingState.getOperationStatus() == LRO_CANCELLED) {
                // Failed|Cancelled
                Error lroInitError = pollingState.getSynchronouslyFailedLroError();
                if (lroInitError != null) {
                    return errorPollResponse(pollingState.getOperationStatus(), lroInitError);
                }
                Error pollError = pollingState.getPollError();
                if (pollError != null) {
                    return errorPollResponse(pollingState.getOperationStatus(), pollError);
                }
                throw new IllegalStateException(
                    "Either LroError or PollError must" + "be set when OperationStatus is in Failed|Cancelled State.");
            } else {
                // Succeeded
                return pollResponse(serializerAdapter, pollingState.getOperationStatus(),
                    pollingState.getLastResponseBody(), pollResultType, pollingState.getPollDelay());
            }
        } else {
            // InProgress|NonTerminal-Status
            return pollResponse(serializerAdapter, pollingState.getOperationStatus(),
                pollingState.getLastResponseBody(), pollResultType, pollingState.getPollDelay());
        }
    }

    /**
     * Create a PollResponse indicating succeeded or in-progress LRO.
     *
     * @param serializerAdapter the serializer for any encoding and decoding
     * @param operationStatus the long-running-operation succeeded or in-progress status
     * @param pollResponseBody the poll response body
     * @param pollResultType the poll result type
     * @param <T> the poll result type
     * @return PollResponse
     */
    static <T> PollResponse<PollResult<T>> pollResponse(SerializerAdapter serializerAdapter,
        LongRunningOperationStatus operationStatus, String pollResponseBody, Type pollResultType, Duration pollDelay) {
        T result = deserialize(serializerAdapter, pollResponseBody, pollResultType);
        return new PollResponse<>(operationStatus, new PollResult<>(result), pollDelay);
    }

    /**
     * Create a PollResponse indicating service error.
     *
     * @param operationStatus the long-running-operation errored status
     * @param error the error description
     * @param <T> the poll result type
     * @return PollResponse
     */
    static <T> PollResponse<PollResult<T>> errorPollResponse(LongRunningOperationStatus operationStatus, Error error) {
        return new PollResponse<>(operationStatus, new PollResult<>(new PollResult.Error(error.getMessage(),
            error.getResponseStatusCode(), new HttpHeaders(error.getResponseHeaders()), error.getResponseBody())));
    }

    /**
     * Decorate the request.
     *
     * @param httpRequest the HttpRequest
     * @return the HttpRequest with decoration.
     */
    private static HttpRequest decorateRequest(HttpRequest httpRequest) {
        return httpRequest.setHeader(HttpHeaderName.ACCEPT, "application/json");
    }

    /**
     * Decode a string value.
     *
     * @param serializerAdapter the serializer for any encoding and decoding
     * @param value the value to decode
     * @param type the type to decode to
     * @param <U> the type to decode to
     * @return decoded value
     */
    @SuppressWarnings("unchecked")
    public static <U> U deserialize(SerializerAdapter serializerAdapter, String value, Type type) {
        if (CoreUtils.isNullOrEmpty(value)) {
            LOGGER.verbose("Ignoring decoding of null or empty value to:" + type);
            return null;
        } else {
            try {
                return (U) serializerAdapter.deserialize(value, type, SerializerEncoding.JSON);
            } catch (IOException | RuntimeException ioe) {
                LOGGER.logExceptionAsWarning(
                    new IllegalArgumentException("Unable to decode '" + value + "' to: " + type, ioe));
                return null;
            }
        }
    }
}
