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

package io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.classic;

import java.io.IOException;

import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.AuthenticationStrategy;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.EndpointInfo;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.HttpRoute;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.RouteTracker;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.SchemePortResolver;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.auth.AuthExchange;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.auth.ChallengeType;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.classic.ExecChain;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.classic.ExecChainHandler;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.classic.ExecRuntime;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.config.RequestConfig;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.auth.AuthCacheKeeper;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.auth.HttpAuthenticator;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.routing.BasicRouteDirector;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.protocol.HttpClientContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.routing.HttpRouteDirector;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Internal;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ClassicHttpRequest;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ClassicHttpResponse;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ConnectionReuseStrategy;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ContentType;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpEntity;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpException;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpHeaders;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpHost;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpRequest;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpStatus;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpVersion;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.Method;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.io.entity.ByteArrayEntity;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.io.entity.EntityUtils;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.message.BasicClassicHttpRequest;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.message.StatusLine;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.HttpProcessor;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request execution handler in the classic request execution chain
 * that is responsible for establishing connection to the target
 * origin server as specified by the current connection route.
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
@Internal
public final class ConnectExec implements ExecChainHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectExec.class);

    private final ConnectionReuseStrategy reuseStrategy;
    private final HttpProcessor proxyHttpProcessor;
    private final AuthenticationStrategy proxyAuthStrategy;
    private final HttpAuthenticator authenticator;
    private final AuthCacheKeeper authCacheKeeper;
    private final HttpRouteDirector routeDirector;

    public ConnectExec(
            final ConnectionReuseStrategy reuseStrategy,
            final HttpProcessor proxyHttpProcessor,
            final AuthenticationStrategy proxyAuthStrategy,
            final SchemePortResolver schemePortResolver,
            final boolean authCachingDisabled) {
        Args.notNull(reuseStrategy, "Connection reuse strategy");
        Args.notNull(proxyHttpProcessor, "Proxy HTTP processor");
        Args.notNull(proxyAuthStrategy, "Proxy authentication strategy");
        this.reuseStrategy = reuseStrategy;
        this.proxyHttpProcessor = proxyHttpProcessor;
        this.proxyAuthStrategy = proxyAuthStrategy;
        this.authenticator = new HttpAuthenticator();
        this.authCacheKeeper = authCachingDisabled ? null : new AuthCacheKeeper(schemePortResolver);
        this.routeDirector = BasicRouteDirector.INSTANCE;
    }

    @Override
    public ClassicHttpResponse execute(
            final ClassicHttpRequest request,
            final ExecChain.Scope scope,
            final ExecChain chain) throws IOException, HttpException {
        Args.notNull(request, "HTTP request");
        Args.notNull(scope, "Scope");

        final String exchangeId = scope.exchangeId;
        final HttpRoute route = scope.route;
        final HttpClientContext context = scope.clientContext;
        final ExecRuntime execRuntime = scope.execRuntime;

        if (!execRuntime.isEndpointAcquired()) {
            final Object userToken = context.getUserToken();
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} acquiring connection with route {}", exchangeId, route);
            }
            execRuntime.acquireEndpoint(exchangeId, route, userToken, context);
        }
        try {
            if (!execRuntime.isEndpointConnected()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} opening connection {}", exchangeId, route);
                }

                final RouteTracker tracker = new RouteTracker(route);
                int step;
                do {
                    final HttpRoute fact = tracker.toRoute();
                    step = this.routeDirector.nextStep(route, fact);

                    switch (step) {

                        case HttpRouteDirector.CONNECT_TARGET:
                            execRuntime.connectEndpoint(context);
                            tracker.connectTarget(route.isSecure());
                            break;
                        case HttpRouteDirector.CONNECT_PROXY:
                            execRuntime.connectEndpoint(context);
                            final HttpHost proxy  = route.getProxyHost();
                            tracker.connectProxy(proxy, route.isSecure() && !route.isTunnelled());
                            break;
                        case HttpRouteDirector.TUNNEL_TARGET: {
                            final ClassicHttpResponse finalResponse = createTunnelToTarget(
                                    exchangeId, route, request, execRuntime, context);
                            if (finalResponse != null) {
                                return finalResponse;
                            }
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("{} tunnel to target created.", exchangeId);
                            }
                            tracker.tunnelTarget(false);
                        }   break;

                        case HttpRouteDirector.TUNNEL_PROXY: {
                            // The most simple example for this case is a proxy chain
                            // of two proxies, where P1 must be tunnelled to P2.
                            // route: Source -> P1 -> P2 -> Target (3 hops)
                            // fact:  Source -> P1 -> Target       (2 hops)
                            final int hop = fact.getHopCount()-1; // the hop to establish
                            final boolean secure = createTunnelToProxy(route, hop, context);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("{} tunnel to proxy created.", exchangeId);
                            }
                            tracker.tunnelProxy(route.getHopTarget(hop), secure);
                        }   break;

                        case HttpRouteDirector.LAYER_PROTOCOL:
                            execRuntime.upgradeTls(context);
                            tracker.layerProtocol(route.isSecure());
                            break;

                        case HttpRouteDirector.UNREACHABLE:
                            throw new HttpException("Unable to establish route: " +
                                    "planned = " + route + "; current = " + fact);
                        case HttpRouteDirector.COMPLETE:
                            break;
                        default:
                            throw new IllegalStateException("Unknown step indicator "
                                    + step + " from RouteDirector.");
                    }

                } while (step > HttpRouteDirector.COMPLETE);
            }
            final EndpointInfo endpointInfo = execRuntime.getEndpointInfo();
            if (endpointInfo != null) {
                context.setProtocolVersion(endpointInfo.getProtocol());
                context.setSSLSession(endpointInfo.getSslSession());
            }
            return chain.proceed(request, scope);

        } catch (final IOException | HttpException | RuntimeException ex) {
            execRuntime.discardEndpoint();
            throw ex;
        }
    }

    /**
     * Creates a tunnel to the target server.
     * The connection must be established to the (last) proxy.
     * A CONNECT request for tunnelling through the proxy will
     * be created and sent, the response received and checked.
     * This method does <i>not</i> processChallenge the connection with
     * information about the tunnel, that is left to the caller.
     */
    private ClassicHttpResponse createTunnelToTarget(
            final String exchangeId,
            final HttpRoute route,
            final HttpRequest request,
            final ExecRuntime execRuntime,
            final HttpClientContext context) throws HttpException, IOException {

        final RequestConfig config = context.getRequestConfigOrDefault();

        final HttpHost target = route.getTargetHost();
        final HttpHost proxy = route.getProxyHost();
        final AuthExchange proxyAuthExchange = context.getAuthExchange(proxy);

        if (authCacheKeeper != null) {
            authCacheKeeper.loadPreemptively(proxy, null, proxyAuthExchange, context);
        }

        ClassicHttpResponse response = null;

        final String authority = target.toHostString();
        final ClassicHttpRequest connect = new BasicClassicHttpRequest(Method.CONNECT, target, authority);
        connect.setVersion(HttpVersion.HTTP_1_1);

        this.proxyHttpProcessor.process(connect, null, context);

        while (response == null) {
            connect.removeHeaders(HttpHeaders.PROXY_AUTHORIZATION);
            this.authenticator.addAuthResponse(proxy, ChallengeType.PROXY, connect, proxyAuthExchange, context);

            response = execRuntime.execute(exchangeId, connect, context);
            this.proxyHttpProcessor.process(response, response.getEntity(), context);

            final int status = response.getCode();
            if (status < HttpStatus.SC_SUCCESS) {
                throw new HttpException("Unexpected response to CONNECT request: " + new StatusLine(response));
            }

            if (config.isAuthenticationEnabled()) {
                final boolean proxyAuthRequested = authenticator.isChallenged(proxy, ChallengeType.PROXY, response, proxyAuthExchange, context);

                if (authCacheKeeper != null) {
                    if (proxyAuthRequested) {
                        authCacheKeeper.updateOnChallenge(proxy, null, proxyAuthExchange, context);
                    } else {
                        authCacheKeeper.updateOnNoChallenge(proxy, null, proxyAuthExchange, context);
                    }
                }

                if (proxyAuthRequested) {
                    final boolean updated = authenticator.updateAuthState(proxy, ChallengeType.PROXY, response,
                            proxyAuthStrategy, proxyAuthExchange, context);

                    if (authCacheKeeper != null) {
                        authCacheKeeper.updateOnResponse(proxy, null, proxyAuthExchange, context);
                    }
                    if (updated) {
                        // Retry request
                        if (this.reuseStrategy.keepAlive(connect, response, context)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("{} connection kept alive", exchangeId);
                            }
                            // Consume response content
                            final HttpEntity entity = response.getEntity();
                            EntityUtils.consume(entity);
                        } else {
                            execRuntime.disconnectEndpoint();
                        }
                        response = null;
                    }
                }
            }
        }

        final int status = response.getCode();
        if (status != HttpStatus.SC_OK) {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                response.setEntity(new ByteArrayEntity(
                        EntityUtils.toByteArray(entity, 4096),
                        ContentType.parseLenient(entity.getContentType())));
                execRuntime.disconnectEndpoint();
            }
            return response;
        }
        return null;
    }

    /**
     * Creates a tunnel to an intermediate proxy.
     * This method is <i>not</i> implemented in this class.
     * It just throws an exception here.
     */
    private boolean createTunnelToProxy(
            final HttpRoute route,
            final int hop,
            final HttpClientContext context) throws HttpException {

        // Have a look at createTunnelToTarget and replicate the parts
        // you need in a custom derived class. If your proxies don't require
        // authentication, it is not too hard. But for the stock version of
        // HttpClient, we cannot make such simplifying assumptions and would
        // have to include proxy authentication code. The HttpComponents team
        // is currently not in a position to support rarely used code of this
        // complexity. Feel free to submit patches that refactor the code in
        // createTunnelToTarget to facilitate re-use for proxy tunnelling.

        throw new HttpException("Proxy chains are not supported.");
    }

}
