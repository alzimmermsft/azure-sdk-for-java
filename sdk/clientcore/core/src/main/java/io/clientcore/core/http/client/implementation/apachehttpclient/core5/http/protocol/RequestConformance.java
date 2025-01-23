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

package io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol;

import java.io.IOException;

import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.EntityDetails;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpException;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpRequest;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpRequestInterceptor;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.MisdirectedRequestException;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ProtocolException;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.URIScheme;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.net.URIAuthority;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.TextUtils;

/**
 * This request interceptor is responsible for execution of the protocol conformance
 * checks on incoming request messages.
 * <p>
 * This interceptor is essential for the HTTP protocol conformance and
 * the correct operation of the server-side message processing pipeline.
 * </p>
 *
 * @since 5.3
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestConformance implements HttpRequestInterceptor {

    public static final RequestConformance INSTANCE = new RequestConformance();

    public RequestConformance() {
        super();
    }

    @Override
    public void process(final HttpRequest request, final EntityDetails entity, final HttpContext localContext)
            throws HttpException, IOException {
        Args.notNull(request, "HTTP request");

        if (TextUtils.isBlank(request.getScheme())) {
            throw new ProtocolException("Request scheme is not set");
        }
        if (TextUtils.isBlank(request.getPath())) {
            throw new ProtocolException("Request path is not set");
        }
        final URIAuthority authority = request.getAuthority();
        if (authority != null && (URIScheme.HTTP.same(request.getScheme()) || URIScheme.HTTPS.same(request.getScheme()))) {
            final String hostName = authority.getHostName();
            if (TextUtils.isBlank(hostName)) {
                throw new ProtocolException("Request host is empty");
            }
        }
        final HttpCoreContext context = HttpCoreContext.cast(localContext);
        if (URIScheme.HTTPS.same(request.getScheme()) && context.getSSLSession() == null) {
            throw new MisdirectedRequestException("HTTPS request over non-secure connection");
        }
    }

}
