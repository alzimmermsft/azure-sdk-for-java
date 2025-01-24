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

package io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.validator;

import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Internal;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.Header;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpHeaders;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.MessageHeaders;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.CharArrayBuffer;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.LangUtils;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Tokenizer;

/**
 * Represents ETag value.
 *
 * @since 5.4
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class ETag {

    private final String value;
    private final ValidatorType type;

    public ETag(final String value, final ValidatorType type) {
        this.value = Args.notNull(value, "Value");
        this.type = Args.notNull(type, "Validator type");
    }

    public ValidatorType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    static ETag parse(final CharSequence buf, final Tokenizer.Cursor cursor) {
        final Tokenizer tokenizer = Tokenizer.INSTANCE;
        tokenizer.skipWhiteSpace(buf, cursor);

        ValidatorType type = ValidatorType.STRONG;

        if (!cursor.atEnd() && buf.charAt(cursor.getPos()) == 'W') {
            cursor.updatePos(cursor.getPos() + 1);
            if (!cursor.atEnd() && buf.charAt(cursor.getPos()) == '/') {
                type = ValidatorType.WEAK;
                cursor.updatePos(cursor.getPos() + 1);
            } else {
                return null;
            }
        }

        String value = null;
        if (!cursor.atEnd() && buf.charAt(cursor.getPos()) == '"') {
            cursor.updatePos(cursor.getPos() + 1);
            final StringBuilder sb = new StringBuilder();
            for (;;) {
                if (cursor.atEnd()) {
                    return null;
                }
                final char ch = buf.charAt(cursor.getPos());
                cursor.updatePos(cursor.getPos() + 1);
                if (ch == '"') {
                    break;
                }
                sb.append(ch);
            }
            value = sb.toString();
        }

        tokenizer.skipWhiteSpace(buf, cursor);
        if (!cursor.atEnd()) {
            return null;
        }

        return new ETag(value, type);
    }

    public static ETag parse(final String s) {
        if (s == null) {
            return null;
        }
        final Tokenizer.Cursor cursor = new Tokenizer.Cursor(0, s.length());
        return parse(s, cursor);
    }

    public static ETag parse(final Header h) {
        if (h == null) {
            return null;
        }
        final String value = h.getValue();
        if (value == null) {
            return null;
        }
        return parse(value);
    }

    public static ETag get(final MessageHeaders message) {
        if (message == null) {
            return null;
        }
        return parse(message.getFirstHeader(HttpHeaders.ETAG));
    }

    @Internal
    public void format(final CharArrayBuffer buf) {
        if (buf == null) {
            return;
        }
        if (type == ValidatorType.WEAK) {
            buf.append("W/");
        }
        buf.append('"');
        buf.append(value);
        buf.append('"');
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ETag) {
            final ETag that = (ETag) o;
            return this.type == that.type && this.value.equals(that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, type);
        hash = LangUtils.hashCode(hash, value);
        return hash;
    }

    @Override
    public String toString() {
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        format(buf);
        return buf.toString();
    }

}
