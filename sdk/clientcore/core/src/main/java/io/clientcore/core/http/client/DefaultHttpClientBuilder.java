// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package io.clientcore.core.http.client;

import io.clientcore.core.http.models.ProxyOptions;
import io.clientcore.core.instrumentation.logging.ClientLogger;
import io.clientcore.core.util.SharedExecutorService;
import io.clientcore.core.util.configuration.Configuration;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;

import static io.clientcore.core.util.configuration.Configuration.PROPERTY_REQUEST_CONNECT_TIMEOUT;
import static io.clientcore.core.util.configuration.Configuration.PROPERTY_REQUEST_READ_TIMEOUT;
import static io.clientcore.core.util.configuration.Configuration.PROPERTY_REQUEST_RESPONSE_TIMEOUT;
import static io.clientcore.core.util.configuration.Configuration.PROPERTY_REQUEST_WRITE_TIMEOUT;

/**
 * Builder to configure and build an instance of the core {@link HttpClient} type using the JDK
 * HttpURLConnection, first introduced in JDK 1.1.
 */
public class DefaultHttpClientBuilder {
    private static final ClientLogger LOGGER = new ClientLogger(DefaultHttpClientBuilder.class);

    private static final Duration MINIMUM_TIMEOUT = Duration.ofMillis(1);
    private static final Duration DEFAULT_CONNECTION_TIMEOUT;
    private static final Duration DEFAULT_WRITE_TIMEOUT;
    private static final Duration DEFAULT_RESPONSE_TIMEOUT;
    private static final Duration DEFAULT_READ_TIMEOUT;

    static {
        Configuration configuration = Configuration.getGlobalConfiguration();

        DEFAULT_CONNECTION_TIMEOUT
            = getDefaultTimeoutFromEnvironment(configuration, PROPERTY_REQUEST_CONNECT_TIMEOUT, Duration.ofSeconds(10));
        DEFAULT_WRITE_TIMEOUT
            = getDefaultTimeoutFromEnvironment(configuration, PROPERTY_REQUEST_WRITE_TIMEOUT, Duration.ofSeconds(60));
        DEFAULT_RESPONSE_TIMEOUT = getDefaultTimeoutFromEnvironment(configuration, PROPERTY_REQUEST_RESPONSE_TIMEOUT,
            Duration.ofSeconds(60));
        DEFAULT_READ_TIMEOUT
            = getDefaultTimeoutFromEnvironment(configuration, PROPERTY_REQUEST_READ_TIMEOUT, Duration.ofSeconds(60));
    }

    /**
     * Attempts to load an environment configured default timeout.
     * <p>
     * If the environment default timeout isn't configured, {@code defaultTimeout} will be returned. If the environment
     * default timeout is a string that isn't parseable by {@link Long#parseLong(String)}, {@code defaultTimeout} will
     * be returned. If the environment default timeout is less than 0, {@link Duration#ZERO} will be returned indicated
     * that there is no timeout period.
     *
     * @param configuration The environment configurations.
     * @param timeoutPropertyName The default timeout property name.
     * @param defaultTimeout The fallback timeout to be used.
     * @return Either the environment configured default timeout, {@code defaultTimeoutMillis}, or 0.
     */
    private static Duration getDefaultTimeoutFromEnvironment(Configuration configuration, String timeoutPropertyName,
        Duration defaultTimeout) {
        String environmentTimeout = configuration.get(timeoutPropertyName);

        // Environment wasn't configured with the timeout property.
        if (environmentTimeout == null || environmentTimeout.isEmpty()) {
            return defaultTimeout;
        }

        try {
            long timeoutMillis = Long.parseLong(environmentTimeout);
            if (timeoutMillis < 0) {
                DefaultHttpClientBuilder.LOGGER.atVerbose()
                    .addKeyValue(timeoutPropertyName, timeoutMillis)
                    .log("Negative timeout values are not allowed. Using 'Duration.ZERO' to indicate no timeout.");
                return Duration.ZERO;
            }

            return Duration.ofMillis(timeoutMillis);
        } catch (NumberFormatException ex) {
            DefaultHttpClientBuilder.LOGGER.atInfo()
                .addKeyValue(timeoutPropertyName, environmentTimeout)
                .addKeyValue("defaultTimeout", defaultTimeout)
                .log("Timeout is not valid number. Using default value.", ex);

            return defaultTimeout;
        }
    }

    private Object httpClientBuilder;
    private ProxyOptions proxyOptions;
    private Configuration configuration;
    private Executor executor;
    private SSLContext sslContext;

    private Duration connectionTimeout;
    private Duration writeTimeout;
    private Duration responseTimeout;
    private Duration readTimeout;

    /**
     * Creates DefaultHttpClientBuilder.
     */
    public DefaultHttpClientBuilder() {
        this.executor = SharedExecutorService.getInstance();
    }

    /**
     * Sets the executor to be used for asynchronous and dependent tasks. This cannot be null.
     * <p>
     * If this method is not invoked prior to {@link #build() building}, handling for a default will be based on whether
     * the builder was created with the default constructor or the constructor that accepts an existing
     * {@link java.net.http.HttpClient.Builder}. If the default constructor was used, the default executor will be
     * {@link SharedExecutorService#getInstance()}. If the constructor that accepts an existing
     * {@link java.net.http.HttpClient.Builder} was used, the executor from the existing builder will be used.
     *
     * @param executor the executor to be used for asynchronous and dependent tasks
     * @return the updated {@link DefaultHttpClientBuilder} object
     * @throws NullPointerException if {@code executor} is null
     */
    public DefaultHttpClientBuilder executor(Executor executor) {
        this.executor = Objects.requireNonNull(executor, "executor can not be null");
        return this;
    }

    /**
     * Sets the connection timeout.
     *
     * <p><strong>Code Samples</strong></p>
     *
     * <!-- src_embed io.clientcore.core.http.client.DefaultHttpClientBuilder.connectionTimeout#Duration -->
     * <pre>
     * HttpClient client = new DefaultHttpClientBuilder&#40;&#41;
     *         .connectionTimeout&#40;Duration.ofSeconds&#40;250&#41;&#41; &#47;&#47; connection timeout of 250 seconds
     *         .build&#40;&#41;;
     * </pre>
     * <!-- end io.clientcore.core.http.client.DefaultHttpClientBuilder.connectionTimeout#Duration -->
     *
     * The default connection timeout is 10 seconds.
     *
     * @param connectionTimeout the connection timeout
     * @return the updated {@link DefaultHttpClientBuilder} object
     */
    public DefaultHttpClientBuilder connectionTimeout(Duration connectionTimeout) {
        // setConnectionTimeout can be null
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    /**
     * Sets the writing timeout for a request to be sent.
     * <p>
     * The writing timeout does not apply to the entire request but to the request being sent over the wire. For example
     * a request body which emits {@code 10} {@code 8KB} buffers will trigger {@code 10} write operations, the last
     * write tracker will update when each operation completes and the outbound buffer will be periodically checked to
     * determine if it is still draining.
     * <p>
     * If {@code writeTimeout} is null either {@link Configuration#PROPERTY_REQUEST_WRITE_TIMEOUT} or a 60-second
     * timeout will be used, if it is a {@link Duration} less than or equal to zero then no write timeout will be
     * applied. When applying the timeout the greatest of one millisecond and the value of {@code writeTimeout} will be
     * used.
     *
     * @param writeTimeout Write operation timeout duration.
     * @return The updated {@link DefaultHttpClientBuilder} object.
     */
    public DefaultHttpClientBuilder writeTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    /**
     * Sets the response timeout duration used when waiting for a server to reply.
     * <p>
     * The response timeout begins once the request write completes and finishes once the first response read is
     * triggered when the server response is received.
     * <p>
     * If {@code responseTimeout} is null either {@link Configuration#PROPERTY_REQUEST_RESPONSE_TIMEOUT} or a
     * 60-second timeout will be used, if it is a {@link Duration} less than or equal to zero then no timeout will be
     * applied to the response. When applying the timeout the greatest of one millisecond and the value of {@code
     * responseTimeout} will be used.
     *
     * @param responseTimeout Response timeout duration.
     * @return The updated {@link DefaultHttpClientBuilder} object.
     */
    public DefaultHttpClientBuilder responseTimeout(Duration responseTimeout) {
        this.responseTimeout = responseTimeout;
        return this;
    }

    /**
     * Sets the read timeout duration used when reading the server response.
     * <p>
     * The read timeout begins once the first response read is triggered after the server response is received. This
     * timeout triggers periodically but won't fire its operation if another read operation has completed between when
     * the timeout is triggered and completes.
     * <p>
     * If {@code readTimeout} is null or {@link Configuration#PROPERTY_REQUEST_READ_TIMEOUT} or a 60-second
     * timeout will be used, if it is a {@link Duration} less than or equal to zero then no timeout period will be
     * applied to response read. When applying the timeout the greatest of one millisecond and the value of {@code
     * readTimeout} will be used.
     *
     * @param readTimeout Read timeout duration.
     * @return The updated {@link DefaultHttpClientBuilder} object.
     */
    public DefaultHttpClientBuilder readTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Sets the proxy.
     *
     * <p><strong>Code Samples</strong></p>
     *
     * <!-- src_embed io.clientcore.core.http.client.DefaultHttpClientBuilder.proxy#ProxyOptions -->
     * <pre>
     * final String proxyHost = &quot;&lt;proxy-host&gt;&quot;; &#47;&#47; e.g. localhost
     * final int proxyPort = 9999; &#47;&#47; Proxy port
     * ProxyOptions proxyOptions = new ProxyOptions&#40;ProxyOptions.Type.HTTP,
     *     new InetSocketAddress&#40;proxyHost, proxyPort&#41;&#41;;
     * HttpClient client = new DefaultHttpClientBuilder&#40;&#41;
     *     .proxy&#40;proxyOptions&#41;
     *     .build&#40;&#41;;
     * </pre>
     * <!-- end io.clientcore.core.http.client.DefaultHttpClientBuilder.proxy#ProxyOptions -->
     *
     * @param proxyOptions The proxy configuration to use.
     * @return the updated {@link DefaultHttpClientBuilder} object
     * @throws NullPointerException If {@code proxyOptions} is not null and the proxy type or address is not set.
     */
    public DefaultHttpClientBuilder proxy(ProxyOptions proxyOptions) {
        if (proxyOptions != null) {
            Objects.requireNonNull(proxyOptions.getType(), "Proxy type is required.");
            Objects.requireNonNull(proxyOptions.getAddress(), "Proxy address is required.");
        }

        // proxyOptions can be null
        this.proxyOptions = proxyOptions;
        return this;
    }

    /**
     * Sets the {@link SSLContext} to be used when opening secure connections.
     *
     * @param sslContext The SSL context to be used.
     * @return The updated {@link DefaultHttpClientBuilder} object.
     */
    public DefaultHttpClientBuilder sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * Sets the configuration store that is used during construction of the HTTP client.
     *
     * @param configuration The configuration store used to
     * @return The updated {@link DefaultHttpClientBuilder} object.
     */
    public DefaultHttpClientBuilder configuration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * Build a HttpClient with current configurations.
     *
     * @return a {@link HttpClient}.
     */
    public HttpClient build() {
        //        java.net.http.HttpClient.Builder httpClientBuilder
        //            = this.httpClientBuilder == null ? java.net.http.HttpClient.newBuilder() : this.httpClientBuilder;
        //
        //        // Client Core JDK http client supports HTTP 1.1 by default.
        //        httpClientBuilder.version(java.net.http.HttpClient.Version.HTTP_1_1);
        //
        //        httpClientBuilder = httpClientBuilder.connectTimeout(getTimeout(connectionTimeout, DEFAULT_CONNECTION_TIMEOUT));

        Duration writeTimeout = getTimeout(this.writeTimeout, DEFAULT_WRITE_TIMEOUT);
        Duration responseTimeout = getTimeout(this.responseTimeout, DEFAULT_RESPONSE_TIMEOUT);
        Duration readTimeout = getTimeout(this.readTimeout, DEFAULT_READ_TIMEOUT);

        Configuration buildConfiguration
            = (configuration == null) ? Configuration.getGlobalConfiguration() : configuration;

        ProxyOptions buildProxyOptions
            = (proxyOptions == null) ? ProxyOptions.fromConfiguration(buildConfiguration) : proxyOptions;

        //        if (executor != null) {
        //            httpClientBuilder.executor(executor);
        //        }
        //
        //        if (sslContext != null) {
        //            httpClientBuilder.sslContext(sslContext);
        //        }

        if (buildProxyOptions != null) {
            // TODO (alzimmer): Add proxying back
            //            httpClientBuilder
            //                = httpClientBuilder.proxy(new JdkHttpClientProxySelector(buildProxyOptions.getType().toProxyType(),
            //                    buildProxyOptions.getAddress(), buildProxyOptions.getNonProxyHosts()));
            //
            //            if (buildProxyOptions.getUsername() != null) {
            //                httpClientBuilder.authenticator(
            //                    new ProxyAuthenticator(buildProxyOptions.getUsername(), buildProxyOptions.getPassword()));
            //            }
        }

        return new DefaultHttpClient(null, writeTimeout, responseTimeout, readTimeout);
    }

    private static Duration getTimeout(Duration configuredTimeout, Duration defaultTimeout) {
        if (configuredTimeout == null) {
            return defaultTimeout;
        }

        return configuredTimeout.compareTo(MINIMUM_TIMEOUT) < 0 ? MINIMUM_TIMEOUT : configuredTimeout;
    }
}
