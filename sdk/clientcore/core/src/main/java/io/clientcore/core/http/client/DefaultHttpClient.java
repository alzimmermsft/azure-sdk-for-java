// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package io.clientcore.core.http.client;

import io.clientcore.core.http.models.HttpRequest;
import io.clientcore.core.http.models.Response;
import io.clientcore.core.instrumentation.logging.ClientLogger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;

/**
 * HttpClient implementation using {@link HttpURLConnection} to send requests and receive responses.
 */
class DefaultHttpClient implements HttpClient {
    private static final ClientLogger LOGGER = new ClientLogger(DefaultHttpClient.class);

    private final Duration writeTimeout;
    private final Duration responseTimeout;
    private final Duration readTimeout;
    private final boolean hasReadTimeout;

    final Object jdkHttpClient;

    DefaultHttpClient(Object httpClient, Duration writeTimeout, Duration responseTimeout, Duration readTimeout) {
        this.jdkHttpClient = httpClient;

        // Set the write and response timeouts to null if they are negative or zero.
        // The writeTimeout is used with 'Flux.timeout(Duration)' which uses thread switching, always. When the timeout
        // is zero or negative it's treated as an infinite timeout. So, setting this to null will prevent that thread
        // switching with the same runtime behavior.
        this.writeTimeout
            = (writeTimeout != null && !writeTimeout.isNegative() && !writeTimeout.isZero()) ? writeTimeout : null;

        // The responseTimeout is used by JDK 'HttpRequest.timeout()' which will throw an exception when the timeout
        // is non-null and is zero or negative. We treat zero or negative as an infinite timeout, so reset to null to
        // prevent the exception from being thrown and have the behavior we want.
        this.responseTimeout = (responseTimeout != null && !responseTimeout.isNegative() && !responseTimeout.isZero())
            ? responseTimeout
            : null;
        this.readTimeout = readTimeout;
        this.hasReadTimeout = readTimeout != null && !readTimeout.isNegative() && !readTimeout.isZero();
    }

    @Override
    public Response<?> send(HttpRequest request) throws IOException {
        throw new UnsupportedOperationException();
    }
}
