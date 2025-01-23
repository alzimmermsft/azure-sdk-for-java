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
import java.io.InterruptedIOException;

import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.ClientProtocolException;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.HttpRoute;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.SchemePortResolver;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.classic.ExecRuntime;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.config.Configurable;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.config.RequestConfig;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.ConnectionShutdownException;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.DefaultClientConnectionReuseStrategy;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.DefaultSchemePortResolver;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.ExecSupport;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.io.HttpClientConnectionManager;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.protocol.HttpClientContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.protocol.RequestClientConnControl;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.routing.RoutingSupport;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.concurrent.CancellableDependency;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ClassicHttpRequest;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ClassicHttpResponse;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ConnectionReuseStrategy;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpEntity;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpException;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpHost;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.impl.io.HttpRequestExecutor;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.DefaultHttpProcessor;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.HttpContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.HttpProcessor;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.RequestContent;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.RequestTargetHost;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.RequestUserAgent;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.io.CloseMode;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.net.URIAuthority;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.TimeValue;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal implementation of {@link CloseableHttpClient}. This client is
 * optimized for HTTP/1.1 message transport and does not support advanced
 * HTTP protocol functionality such as request execution via a proxy, state
 * management, authentication and request redirects.
 * <p>
 * Concurrent message exchanges executed by this client will get assigned to
 * separate connections leased from the connection pool.
 * </p>
 *
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class MinimalHttpClient extends CloseableHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(MinimalHttpClient.class);

    private final HttpClientConnectionManager connManager;
    private final ConnectionReuseStrategy reuseStrategy;
    private final SchemePortResolver schemePortResolver;
    private final HttpRequestExecutor requestExecutor;
    private final HttpProcessor httpProcessor;

    MinimalHttpClient(final HttpClientConnectionManager connManager) {
        super();
        this.connManager = Args.notNull(connManager, "HTTP connection manager");
        this.reuseStrategy = DefaultClientConnectionReuseStrategy.INSTANCE;
        this.schemePortResolver = DefaultSchemePortResolver.INSTANCE;
        this.requestExecutor = new HttpRequestExecutor(this.reuseStrategy);
        this.httpProcessor = new DefaultHttpProcessor(
                new RequestContent(),
                new RequestTargetHost(),
                new RequestClientConnControl(),
                new RequestUserAgent(VersionInfo.getSoftwareInfo(
                        "Apache-HttpClient", "io.clientcore.core.http.client.implementation.apachehttpclient.client5", getClass())));
    }

    @Override
    protected CloseableHttpResponse doExecute(
            final HttpHost target,
            final ClassicHttpRequest request,
            final HttpContext context) throws IOException {
        Args.notNull(target, "Target host");
        Args.notNull(request, "HTTP request");
        if (request.getScheme() == null) {
            request.setScheme(target.getSchemeName());
        }
        if (request.getAuthority() == null) {
            request.setAuthority(new URIAuthority(target));
        }
        final HttpClientContext clientContext = HttpClientContext.castOrCreate(context);
        RequestConfig config = null;
        if (request instanceof Configurable) {
            config = ((Configurable) request).getConfig();
        }
        if (config != null) {
            clientContext.setRequestConfig(config);
        }

        final HttpRoute route = new HttpRoute(RoutingSupport.normalize(target, schemePortResolver));
        final String exchangeId = ExecSupport.getNextExchangeId();
        clientContext.setExchangeId(exchangeId);
        final ExecRuntime execRuntime = new InternalExecRuntime(LOG, connManager, requestExecutor,
                request instanceof CancellableDependency ? (CancellableDependency) request : null);
        try {
            if (!execRuntime.isEndpointAcquired()) {
                execRuntime.acquireEndpoint(exchangeId, route, null, clientContext);
            }
            if (!execRuntime.isEndpointConnected()) {
                execRuntime.connectEndpoint(clientContext);
            }

            clientContext.setRequest(request);
            clientContext.setRoute(route);

            httpProcessor.process(request, request.getEntity(), clientContext);
            final ClassicHttpResponse response = execRuntime.execute(exchangeId, request, clientContext);
            httpProcessor.process(response, response.getEntity(), clientContext);

            if (reuseStrategy.keepAlive(request, response, clientContext)) {
                execRuntime.markConnectionReusable(null, TimeValue.NEG_ONE_MILLISECOND);
            } else {
                execRuntime.markConnectionNonReusable();
            }

            // check for entity, release connection if possible
            final HttpEntity entity = response.getEntity();
            if (entity == null || !entity.isStreaming()) {
                // connection not needed and (assumed to be) in re-usable state
                execRuntime.releaseEndpoint();
                return new CloseableHttpResponse(response, null);
            }
            ResponseEntityProxy.enhance(response, execRuntime);
            return new CloseableHttpResponse(response, execRuntime);
        } catch (final ConnectionShutdownException ex) {
            final InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
            ioex.initCause(ex);
            execRuntime.discardEndpoint();
            throw ioex;
        } catch (final HttpException httpException) {
            execRuntime.discardEndpoint();
            throw new ClientProtocolException(httpException);
        } catch (final RuntimeException | IOException ex) {
            execRuntime.discardEndpoint();
            throw ex;
        } catch (final Error error) {
            connManager.close(CloseMode.IMMEDIATE);
            throw error;
        }
    }

    @Override
    public void close() throws IOException {
        this.connManager.close();
    }

    @Override
    public void close(final CloseMode closeMode) {
        this.connManager.close(closeMode);
    }

}
