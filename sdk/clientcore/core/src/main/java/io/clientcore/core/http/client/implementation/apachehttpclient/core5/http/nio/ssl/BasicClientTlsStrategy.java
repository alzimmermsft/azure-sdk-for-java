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

package io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.nio.ssl;

import java.net.SocketAddress;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import io.clientcore.core.http.client.implementation.apachehttpclient.core5.concurrent.FutureCallback;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpHost;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.URIScheme;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.net.NamedEndpoint;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.reactor.ssl.SSLBufferMode;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.reactor.ssl.SSLSessionInitializer;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.reactor.ssl.SSLSessionVerifier;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.reactor.ssl.TransportSecurityLayer;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.ssl.SSLContexts;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Timeout;

/**
 * Basic client-side implementation of {@link TlsStrategy} that upgrades to TLS for all endpoints
 * with {@code HTTPS} scheme.
 *
 * @since 5.0
 */
public class BasicClientTlsStrategy implements TlsStrategy {

    private final SSLContext sslContext;
    private final SSLBufferMode sslBufferMode;
    private final SSLSessionInitializer initializer;
    private final SSLSessionVerifier verifier;

    public BasicClientTlsStrategy(
            final SSLContext sslContext,
            final SSLBufferMode sslBufferMode,
            final SSLSessionInitializer initializer,
            final SSLSessionVerifier verifier) {
        this.sslContext = Args.notNull(sslContext, "SSL context");
        this.sslBufferMode = sslBufferMode;
        this.initializer = initializer;
        this.verifier = verifier;
    }

    public BasicClientTlsStrategy(
            final SSLContext sslContext,
            final SSLSessionInitializer initializer,
            final SSLSessionVerifier verifier) {
        this(sslContext, null, initializer, verifier);
    }

    public BasicClientTlsStrategy(
            final SSLContext sslContext,
            final SSLSessionVerifier verifier) {
        this(sslContext, null, null, verifier);
    }

    public BasicClientTlsStrategy(final SSLContext sslContext) {
        this(sslContext, null, null, null);
    }

    public BasicClientTlsStrategy() {
        this(SSLContexts.createSystemDefault());
    }

    /**
     * Constructor with the default SSL context based on system properties and custom {@link  SSLSessionVerifier} verifier.
     * @param verifier the custom {@link SSLSessionVerifier}.
     * @see SSLContext
     * @since 5.2
     */
    public BasicClientTlsStrategy(final SSLSessionVerifier verifier) {
        this(SSLContexts.createSystemDefault(), verifier);
    }

    @Override
    public void upgrade(
            final TransportSecurityLayer tlsSession,
            final NamedEndpoint endpoint,
            final Object attachment,
            final Timeout handshakeTimeout,
            final FutureCallback<TransportSecurityLayer> callback) {
        tlsSession.startTls(
                sslContext,
                endpoint,
                sslBufferMode,
                (e, sslEngine) -> {
                    final SSLParameters sslParameters = sslEngine.getSSLParameters();
                    sslParameters.setEndpointIdentificationAlgorithm(URIScheme.HTTPS.id);
                    sslEngine.setSSLParameters(TlsSupport.enforceStrongSecurity(sslParameters));
                    if (initializer != null) {
                        initializer.initialize(e, sslEngine);
                    }
                },
                verifier,
                handshakeTimeout,
                callback);
    }

    /**
     * @deprecated use {@link #upgrade(TransportSecurityLayer, NamedEndpoint, Object, Timeout, FutureCallback)}
     */
    @Deprecated
    @Override
    public boolean upgrade(
            final TransportSecurityLayer tlsSession,
            final HttpHost host,
            final SocketAddress localAddress,
            final SocketAddress remoteAddress,
            final Object attachment,
            final Timeout handshakeTimeout) {
        final String scheme = host != null ? host.getSchemeName() : null;
        if (URIScheme.HTTPS.same(scheme)) {
            upgrade(tlsSession, host, attachment, handshakeTimeout, null);
            return true;
        }
        return false;
    }

}
