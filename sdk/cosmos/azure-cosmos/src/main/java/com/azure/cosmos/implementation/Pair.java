// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public final class Pair<L, R> implements Map.Entry<L, R>, Serializable {
    private static final long serialVersionUID = 8627696763659768762L;

    private final L left;
    private final R right;

    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Creates a new {@link Pair}.
     *
     * @param <L> Type of the left value.
     * @param <R> Type of the right value.
     * @param left The left value.
     * @param right The right value.
     * @return The new {@link Pair}.
     */
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public L getKey() {
        return left;
    }

    @Override
    public R getValue() {
        return right;
    }

    @Override
    public R setValue(R value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map.Entry<?, ?>) {
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            return Objects.equals(getKey(), other.getKey())
                && Objects.equals(getValue(), other.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        // see Map.Entry API specification
        return Objects.hashCode(left) ^ Objects.hashCode(right);
    }

    @Override
    public String toString() {
        return "(" + left + ',' + right + ')';
    }
}
