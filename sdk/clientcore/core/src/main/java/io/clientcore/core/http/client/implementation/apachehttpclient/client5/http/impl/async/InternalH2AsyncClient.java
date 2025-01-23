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

import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.HttpRoute;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.async.AsyncExecRuntime;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.auth.AuthSchemeFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.auth.CredentialsProvider;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.config.RequestConfig;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.cookie.CookieSpecFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.cookie.CookieStore;
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
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.reactor.DefaultConnectingIOReactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal implementation of HTTP/2 only {@link CloseableHttpAsyncClient}.
 * <p>
 * Concurrent message exchanges with the same connection route executed by
 * this client will get automatically multiplexed over a single physical HTTP/2
 * connection.
 * </p>
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
@Internal
public final class InternalH2AsyncClient extends InternalAbstractHttpAsyncClient {

    private static final Logger LOG = LoggerFactory.getLogger(InternalH2AsyncClient.class);
    private final HttpRoutePlanner routePlanner;
    private final InternalH2ConnPool connPool;

    InternalH2AsyncClient(
            final DefaultConnectingIOReactor ioReactor,
            final AsyncExecChainElement execChain,
            final AsyncPushConsumerRegistry pushConsumerRegistry,
            final ThreadFactory threadFactory,
            final InternalH2ConnPool connPool,
            final HttpRoutePlanner routePlanner,
            final Lookup<CookieSpecFactory> cookieSpecRegistry,
            final Lookup<AuthSchemeFactory> authSchemeRegistry,
            final CookieStore cookieStore,
            final CredentialsProvider credentialsProvider,
            final RequestConfig defaultConfig,
            final List<Closeable> closeables) {
        super(ioReactor, pushConsumerRegistry, threadFactory, execChain,
                cookieSpecRegistry, authSchemeRegistry, cookieStore, credentialsProvider, HttpClientContext::castOrCreate,
                defaultConfig, closeables);
        this.connPool = connPool;
        this.routePlanner = routePlanner;
    }

    @Override
    AsyncExecRuntime createAsyncExecRuntime(final HandlerFactory<AsyncPushConsumer> pushHandlerFactory) {
        return new InternalH2AsyncExecRuntime(LOG, connPool, pushHandlerFactory);
    }

    @Override
    HttpRoute determineRoute(final HttpHost httpHost, final HttpRequest request, final HttpClientContext clientContext) throws HttpException {
        final HttpRoute route = routePlanner.determineRoute(httpHost, request, clientContext);
        if (route.isTunnelled()) {
            throw new HttpException("HTTP/2 tunneling not supported");
        }
        return route;
    }

}
