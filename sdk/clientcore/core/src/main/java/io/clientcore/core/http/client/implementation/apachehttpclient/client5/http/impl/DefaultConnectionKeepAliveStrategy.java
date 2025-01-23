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
package io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl;

import java.util.Iterator;

import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.ConnectionKeepAliveStrategy;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.config.RequestConfig;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.protocol.HttpClientContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HeaderElement;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HeaderElements;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpResponse;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.message.MessageSupport;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.protocol.HttpContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.TimeValue;

/**
 * Default implementation of a strategy deciding duration
 * that a connection can remain idle.
 * <p>
 * The default implementation looks solely at the 'Keep-Alive'
 * header's timeout token.
 * </p>
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

    /**
     * Default instance of {@link DefaultConnectionKeepAliveStrategy}.
     */
    public static final DefaultConnectionKeepAliveStrategy INSTANCE = new DefaultConnectionKeepAliveStrategy();

    @Override
    public TimeValue getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        final Iterator<HeaderElement> it = MessageSupport.iterate(response, HeaderElements.KEEP_ALIVE);
        while (it.hasNext()) {
            final HeaderElement he = it.next();
            final String param = he.getName();
            final String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return TimeValue.ofSeconds(Long.parseLong(value));
                } catch(final NumberFormatException ignore) {
                }
            }
        }
        final HttpClientContext clientContext = HttpClientContext.cast(context);
        final RequestConfig requestConfig = clientContext.getRequestConfigOrDefault();
        return requestConfig.getConnectionKeepAlive();
    }

}
