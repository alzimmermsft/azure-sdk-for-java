// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation;

import com.azure.cosmos.implementation.apachecommons.lang.builder.CompareToBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public final class Pair<L, R> implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable {
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

    /**
     * <p>Compares the pair based on the left element followed by the right element.
     * The types must be {@code Comparable}.</p>
     *
     * @param other  the other pair, not null
     * @return negative if this is less, zero if equal, positive if greater
     */
    @Override
    public int compareTo(Pair<L, R> other) {
        return new CompareToBuilder().append(getLeft(), other.getLeft())
            .append(getRight(), other.getRight()).toComparison();
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
