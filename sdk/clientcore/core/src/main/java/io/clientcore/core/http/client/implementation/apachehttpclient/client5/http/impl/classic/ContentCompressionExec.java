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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.classic.ExecChain;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.classic.ExecChainHandler;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.config.RequestConfig;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.entity.BrotliDecompressingEntity;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.entity.BrotliInputStreamFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.entity.DecompressingEntity;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.entity.DeflateInputStream;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.entity.DeflateInputStreamFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.entity.GZIPInputStreamFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.entity.InputStreamFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.protocol.HttpClientContext;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Internal;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ClassicHttpRequest;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ClassicHttpResponse;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.Header;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HeaderElement;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpEntity;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpException;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.HttpHeaders;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.config.Lookup;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.config.RegistryBuilder;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.message.BasicHeaderValueParser;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.message.MessageSupport;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.message.ParserCursor;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;
import org.brotli.dec.BrotliInputStream;

/**
 * Request execution handler in the classic request execution chain
 * that is responsible for automatic response content decompression.
 * <p>
 * Further responsibilities such as communication with the opposite
 * endpoint is delegated to the next executor in the request execution
 * chain.
 * </p>
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.STATELESS)
@Internal
public final class ContentCompressionExec implements ExecChainHandler {

    private final Header acceptEncoding;
    private final Lookup<InputStreamFactory> decoderRegistry;
    private final boolean ignoreUnknown;

    public ContentCompressionExec(
            final List<String> acceptEncoding,
            final Lookup<InputStreamFactory> decoderRegistry,
            final boolean ignoreUnknown) {

        final boolean brotliSupported = BrotliDecompressingEntity.isAvailable();
        final List<String> encodings = new ArrayList<>(4);
        encodings.add("gzip");
        encodings.add("x-gzip");
        encodings.add("deflate");
        if (brotliSupported) {
            encodings.add("br");
        }
        this.acceptEncoding = MessageSupport.headerOfTokens(HttpHeaders.ACCEPT_ENCODING, encodings);

        if (decoderRegistry != null) {
            this.decoderRegistry = decoderRegistry;
        } else {
            final RegistryBuilder<InputStreamFactory> builder = RegistryBuilder.<InputStreamFactory>create()
                .register("gzip", GZIPInputStreamFactory.getInstance())
                .register("x-gzip", GZIPInputStreamFactory.getInstance())
                .register("deflate", DeflateInputStreamFactory.getInstance());
            if (brotliSupported) {
                builder.register("br", BrotliInputStreamFactory.getInstance());
            }
            this.decoderRegistry = builder.build();
        }


        this.ignoreUnknown = ignoreUnknown;
    }

    public ContentCompressionExec(final boolean ignoreUnknown) {
        this(null, null, ignoreUnknown);
    }

    /**
     * Handles {@code gzip} and {@code deflate} compressed entities by using the following
     * decoders:
     * <ul>
     * <li>gzip - see {@link GZIPInputStream}</li>
     * <li>deflate - see {@link DeflateInputStream}</li>
     * <li>brotli - see {@link BrotliInputStream}</li>
     * </ul>
     */
    public ContentCompressionExec() {
        this(null, null, true);
    }


    @Override
    public ClassicHttpResponse execute(
            final ClassicHttpRequest request,
            final ExecChain.Scope scope,
            final ExecChain chain) throws IOException, HttpException {
        Args.notNull(request, "HTTP request");
        Args.notNull(scope, "Scope");

        final HttpClientContext clientContext = scope.clientContext;
        final RequestConfig requestConfig = clientContext.getRequestConfigOrDefault();

        /* Signal support for Accept-Encoding transfer encodings. */
        if (!request.containsHeader(HttpHeaders.ACCEPT_ENCODING) && requestConfig.isContentCompressionEnabled()) {
            request.addHeader(acceptEncoding);
        }

        final ClassicHttpResponse response = chain.proceed(request, scope);

        final HttpEntity entity = response.getEntity();
        // entity can be null in case of 304 Not Modified, 204 No Content or similar
        // check for zero length entity.
        if (requestConfig.isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0) {
            final String contentEncoding = entity.getContentEncoding();
            if (contentEncoding != null) {
                final ParserCursor cursor = new ParserCursor(0, contentEncoding.length());
                final HeaderElement[] codecs = BasicHeaderValueParser.INSTANCE.parseElements(contentEncoding, cursor);
                for (final HeaderElement codec : codecs) {
                    final String codecname = codec.getName().toLowerCase(Locale.ROOT);
                    final InputStreamFactory decoderFactory = decoderRegistry.lookup(codecname);
                    if (decoderFactory != null) {
                        response.setEntity(new DecompressingEntity(response.getEntity(), decoderFactory));
                        response.removeHeaders(HttpHeaders.CONTENT_LENGTH);
                        response.removeHeaders(HttpHeaders.CONTENT_ENCODING);
                        response.removeHeaders(HttpHeaders.CONTENT_MD5);
                    } else {
                        if (!"identity".equals(codecname) && !ignoreUnknown) {
                            throw new HttpException("Unsupported Content-Encoding: " + codec.getName());
                        }
                    }
                }
            }
        }
        return response;
    }

}
