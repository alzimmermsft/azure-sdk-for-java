// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation.directconnectivity.TcpServerMock.rntbd;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.ReferenceCounted;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.azure.cosmos.implementation.Utils.checkNotNull;

/**
 * Methods included in this class are copied from com.azure.cosmos.implementation.directconnectivity.rntbd.RntbdTokenStream.
 * @param <T>
 */
abstract class ServerRntbdTokenStream <T extends Enum<T> & ServerRntbdConstants.RntbdHeader> implements ReferenceCounted {
    final ByteBuf in;
    final Map<Short, T> headers;
    final Map<T, ServerRntbdToken> tokens;

    ServerRntbdTokenStream(final Set<T> headers, final Map<Short, T> ids, final ByteBuf in, Class<T> enumType) {
        checkNotNull(headers, "expected non-null headers");
        checkNotNull(ids, "expected non-null ids");
        checkNotNull(in, "expected non-null in");

        EnumMap<T, ServerRntbdToken> tokens = new EnumMap<>(enumType);
        for (T header : headers) {
            tokens.put(header, ServerRntbdToken.create(header));
        }
        this.tokens = Collections.unmodifiableMap(tokens);
        this.headers = Collections.unmodifiableMap(ids);
        this.in = in;
    }

    // region Methods

    final int computeLength() {

        int total = 0;

        for (final ServerRntbdToken token : this.tokens.values()) {
            total += token.computeLength();
        }

        return total;
    }

    static <T extends ServerRntbdTokenStream<?>> T decode(final T stream) {

        final ByteBuf in = stream.in;

        while (in.readableBytes() > 0) {

            final short id = in.readShortLE();
            final ServerRntbdTokenType type = ServerRntbdTokenType.fromId(in.readByte());

            ServerRntbdToken token = stream.tokens.get(stream.headers.get(id));

            if (token == null) {
                token = ServerRntbdToken.create(new ServerRntbdTokenStream.UndefinedHeader(id, type));
            }

            token.decode(in);
        }

        for (final ServerRntbdToken token : stream.tokens.values()) {
            if (!token.isPresent() && token.isRequired()) {
                final String message = String.format("Required header not found on token stream: %s", token);
                throw new CorruptedFrameException(message);
            }
        }

        return stream;
    }

    final void encode(final ByteBuf out) {
        for (final ServerRntbdToken token : this.tokens.values()) {
            token.encode(out);
        }
    }

    final ServerRntbdToken get(final T header) {
        return this.tokens.get(header);
    }

    @Override
    public final int refCnt() {
        return this.in.refCnt();
    }

    @Override
    public final boolean release() {
        return this.release(1);
    }

    @Override
    public final boolean release(final int count) {
        return this.in.release(count);
    }

    @Override
    public final ServerRntbdTokenStream<T> retain() {
        return this.retain(1);
    }

    @Override
    public final ServerRntbdTokenStream<T> retain(final int count) {
        this.in.retain(count);
        return this;
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return this;
    }

    @Override
    public ReferenceCounted touch() {
        return this;
    }

    // endregion

    // region Types

    private static final class UndefinedHeader implements ServerRntbdConstants.RntbdHeader {

        private final short id;
        private final ServerRntbdTokenType type;

        UndefinedHeader(final short id, final ServerRntbdTokenType type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public boolean isRequired() {
            return false;
        }

        @Override
        public short id() {
            return this.id;
        }

        @Override
        public String name() {
            return "Undefined";
        }

        @Override
        public ServerRntbdTokenType type() {
            return this.type;
        }
    }

    // endregion
}
