/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.async;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.HttpRoute;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.async.AsyncExecCallback;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.async.AsyncExecChain;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.async.AsyncExecChainHandler;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.async.AsyncExecRuntime;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.protocol.HttpClientContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Internal;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.concurrent.CancellableDependency;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.EntityDetails;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.Header;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpException;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpRequest;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpResponse;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.message.RequestLine;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.nio.AsyncClientExchangeHandler;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.nio.AsyncDataConsumer;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.nio.AsyncEntityProducer;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.nio.CapacityChannel;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.nio.DataStreamChannel;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.nio.RequestChannel;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.HttpContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.HttpProcessor;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usually the last HTTP/2 request execution handler in the asynchronous
 * request execution chain that is responsible for execution of
 * request / response exchanges with the opposite endpoint.
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
@Internal
public class H2AsyncMainClientExec implements AsyncExecChainHandler {

    private static final Logger LOG = LoggerFactory.getLogger(H2AsyncMainClientExec.class);

    private final HttpProcessor httpProcessor;

    H2AsyncMainClientExec(final HttpProcessor httpProcessor) {
        this.httpProcessor = Args.notNull(httpProcessor, "HTTP protocol processor");
    }

    @Override
    public void execute(
            final HttpRequest request,
            final AsyncEntityProducer entityProducer,
            final AsyncExecChain.Scope scope,
            final AsyncExecChain chain,
            final AsyncExecCallback asyncExecCallback) throws HttpException, IOException {
        final String exchangeId = scope.exchangeId;
        final HttpRoute route = scope.route;
        final CancellableDependency operation = scope.cancellableDependency;
        final HttpClientContext clientContext = scope.clientContext;
        final AsyncExecRuntime execRuntime = scope.execRuntime;

        if (LOG.isDebugEnabled()) {
            LOG.debug("{} executing {}", exchangeId, new RequestLine(request));
        }

        final AsyncClientExchangeHandler internalExchangeHandler = new AsyncClientExchangeHandler() {

            private final AtomicReference<AsyncDataConsumer> entityConsumerRef = new AtomicReference<>();

            @Override
            public void releaseResources() {
                final AsyncDataConsumer entityConsumer = entityConsumerRef.getAndSet(null);
                if (entityConsumer != null) {
                    entityConsumer.releaseResources();
                }
            }

            @Override
            public void failed(final Exception cause) {
                final AsyncDataConsumer entityConsumer = entityConsumerRef.getAndSet(null);
                if (entityConsumer != null) {
                    entityConsumer.releaseResources();
                }
                execRuntime.markConnectionNonReusable();
                asyncExecCallback.failed(cause);
            }

            @Override
            public void cancel() {
                failed(new InterruptedIOException());
            }

            @Override
            public void produceRequest(final RequestChannel channel, final HttpContext context) throws HttpException, IOException {
                clientContext.setRequest(request);
                clientContext.setRoute(route);
                httpProcessor.process(request, entityProducer, clientContext);

                channel.sendRequest(request, entityProducer, context);
            }

            @Override
            public int available() {
                return entityProducer.available();
            }

            @Override
            public void produce(final DataStreamChannel channel) throws IOException {
                entityProducer.produce(channel);
            }

            @Override
            public void consumeInformation(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
            }

            @Override
            public void consumeResponse(
                    final HttpResponse response,
                    final EntityDetails entityDetails,
                    final HttpContext context) throws HttpException, IOException {

                clientContext.setResponse(response);
                httpProcessor.process(response, entityDetails, clientContext);

                entityConsumerRef.set(asyncExecCallback.handleResponse(response, entityDetails));
                if (entityDetails == null) {
                    execRuntime.validateConnection();
                    asyncExecCallback.completed();
                }
            }

            @Override
            public void updateCapacity(final CapacityChannel capacityChannel) throws IOException {
                final AsyncDataConsumer entityConsumer = entityConsumerRef.get();
                if (entityConsumer != null) {
                    entityConsumer.updateCapacity(capacityChannel);
                } else {
                    capacityChannel.update(Integer.MAX_VALUE);
                }
            }

            @Override
            public void consume(final ByteBuffer src) throws IOException {
                final AsyncDataConsumer entityConsumer = entityConsumerRef.get();
                if (entityConsumer != null) {
                    entityConsumer.consume(src);
                }
            }

            @Override
            public void streamEnd(final List<? extends Header> trailers) throws HttpException, IOException {
                final AsyncDataConsumer entityConsumer = entityConsumerRef.getAndSet(null);
                if (entityConsumer != null) {
                    entityConsumer.streamEnd(trailers);
                } else {
                    execRuntime.validateConnection();
                }
                asyncExecCallback.completed();
            }

        };

        if (LOG.isDebugEnabled()) {
            operation.setDependency(execRuntime.execute(
                    exchangeId,
                    new LoggingAsyncClientExchangeHandler(LOG, exchangeId, internalExchangeHandler),
                    clientContext));
        } else {
            operation.setDependency(execRuntime.execute(exchangeId, internalExchangeHandler, clientContext));
        }
    }

}
