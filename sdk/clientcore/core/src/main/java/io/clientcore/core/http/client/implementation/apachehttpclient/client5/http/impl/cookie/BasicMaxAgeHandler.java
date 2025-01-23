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
package io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.impl.cookie;

import java.time.Instant;

import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.cookie.CommonCookieAttributeHandler;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.cookie.Cookie;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.cookie.MalformedCookieException;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.cookie.SetCookie;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;

/**
 * Cookie {@code max-age} attribute handler.
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
public class BasicMaxAgeHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler {

    /**
     * Default instance of {@link BasicMaxAgeHandler}.
     *
     * @since 5.2
     */
    public static final BasicMaxAgeHandler INSTANCE = new BasicMaxAgeHandler();

    public BasicMaxAgeHandler() {
        super();
    }

    @Override
    public void parse(final SetCookie cookie, final String value)
            throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for 'max-age' attribute");
        }
        final int age;
        try {
            age = Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new MalformedCookieException ("Invalid 'max-age' attribute: "
                    + value);
        }
        if (age < 0) {
            throw new MalformedCookieException ("Negative 'max-age' attribute: "
                    + value);
        }
        cookie.setExpiryDate(Instant.now().plusSeconds(age));
    }

    @Override
    public String getAttributeName() {
        return Cookie.MAX_AGE_ATTR;
    }

}
