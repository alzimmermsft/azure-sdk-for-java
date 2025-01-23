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

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.HttpRoute;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.async.AsyncExecRuntime;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.auth.AuthSchemeFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.auth.CredentialsProvider;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.config.RequestConfig;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.config.TlsConfig;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.cookie.CookieSpecFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.cookie.CookieStore;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.nio.AsyncClientConnectionManager;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.protocol.HttpClientContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.routing.HttpRoutePlanner;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Internal;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpException;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpHost;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpRequest;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.config.Lookup;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.nio.AsyncPushConsumer;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.nio.HandlerFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.HttpContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.reactor.DefaultConnectingIOReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal implementation of {@link CloseableHttpAsyncClient} that can negotiate
 * the most optimal HTTP protocol version during during the {@code TLS} handshake
 * with {@code ALPN} extension if supported by the Java runtime.
 * <p>
 * Concurrent message exchanges executed by this client will get assigned to
 * separate connections leased from the connection pool.
 * </p>
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
@Internal
public final class InternalHttpAsyncClient extends InternalAbstractHttpAsyncClient {

    private static final Logger LOG = LoggerFactory.getLogger(InternalHttpAsyncClient.class);
    private final AsyncClientConnectionManager manager;
    private final HttpRoutePlanner routePlanner;
    private final TlsConfig tlsConfig;

    InternalHttpAsyncClient(
            final DefaultConnectingIOReactor ioReactor,
            final AsyncExecChainElement execChain,
            final AsyncPushConsumerRegistry pushConsumerRegistry,
            final ThreadFactory threadFactory,
            final AsyncClientConnectionManager manager,
            final HttpRoutePlanner routePlanner,
            final TlsConfig tlsConfig,
            final Lookup<CookieSpecFactory> cookieSpecRegistry,
            final Lookup<AuthSchemeFactory> authSchemeRegistry,
            final CookieStore cookieStore,
            final CredentialsProvider credentialsProvider,
            final Function<HttpContext, HttpClientContext> contextAdaptor,
            final RequestConfig defaultConfig,
            final List<Closeable> closeables) {
        super(ioReactor, pushConsumerRegistry, threadFactory, execChain,
                cookieSpecRegistry, authSchemeRegistry, cookieStore, credentialsProvider, contextAdaptor,
                defaultConfig, closeables);
        this.manager = manager;
        this.routePlanner = routePlanner;
        this.tlsConfig = tlsConfig;
    }

    @Override
    AsyncExecRuntime createAsyncExecRuntime(final HandlerFactory<AsyncPushConsumer> pushHandlerFactory) {
        return new InternalHttpAsyncExecRuntime(LOG, manager, getConnectionInitiator(), pushHandlerFactory, tlsConfig);
    }

    @Override
    HttpRoute determineRoute(final HttpHost httpHost, final HttpRequest request, final HttpClientContext clientContext) throws HttpException {
        return routePlanner.determineRoute(httpHost, request, clientContext);
    }

}
