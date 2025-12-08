/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * Portions Copyright (c) Microsoft Corporation
 */

package com.azure.cosmos.implementation.guava25.primitives;

import com.azure.cosmos.implementation.guava25.annotations.Beta;
import com.azure.cosmos.implementation.guava25.annotations.GwtIncompatible;

import java.util.Arrays;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkPositionIndexes;
//import sun.misc.Unsafe;

/**
 * Static utility methods pertaining to {@code byte} primitives that interpret values as
 * <i>unsigned</i> (that is, any negative value {@code b} is treated as the positive value {@code
 * 256 + b}). The corresponding methods that treat the values as signed are found in {@link
 * SignedBytes}, and the methods for which signedness is not an issue are in {@link Bytes}.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/PrimitivesExplained">primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @author Martin Buchholz
 * @author Hiroshi Yamauchi
 * @author Louis Wasserman
 * @since 1.0
 */
@GwtIncompatible
public final class UnsignedBytes {
    private UnsignedBytes() {}

    /**
     * The largest value that fits into an unsigned byte.
     *
     * @since 13.0
     */
    public static final byte MAX_VALUE = (byte) 0xFF;

    private static final int UNSIGNED_MASK = 0xFF;

    /**
     * Returns the value of the given byte as an integer, when treated as unsigned. That is, returns
     * {@code value + 256} if {@code value} is negative; {@code value} itself otherwise.
     *
     * <p><b>Java 8 users:</b> use {@link Byte#toUnsignedInt(byte)} instead.
     *
     * @since 6.0
     */
    public static int toInt(byte value) {
        return value & UNSIGNED_MASK;
    }

    /**
     * Compares the two specified {@code byte} values, treating them as unsigned values between 0 and
     * 255 inclusive. For example, {@code (byte) -127} is considered greater than {@code (byte) 127}
     * because it is seen as having the value of positive {@code 129}.
     *
     * @param a the first {@code byte} to compare
     * @param b the second {@code byte} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive value if {@code a} is
     *     greater than {@code b}; or zero if they are equal
     */
    public static int compare(byte a, byte b) {
        return toInt(a) - toInt(b);
    }

    /**
     * Returns the least value present in {@code array}.
     *
     * @param array a <i>nonempty</i> array of {@code byte} values
     * @return the value present in {@code array} that is less than or equal to every other value in
     *     the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static byte min(byte... array) {
        checkArgument(array.length > 0);
        int min = toInt(array[0]);
        for (int i = 1; i < array.length; i++) {
            int next = toInt(array[i]);
            if (next < min) {
                min = next;
            }
        }
        return (byte) min;
    }

    /**
     * Returns the greatest value present in {@code array}.
     *
     * @param array a <i>nonempty</i> array of {@code byte} values
     * @return the value present in {@code array} that is greater than or equal to every other value
     *     in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static byte max(byte... array) {
        checkArgument(array.length > 0);
        int max = toInt(array[0]);
        for (int i = 1; i < array.length; i++) {
            int next = toInt(array[i]);
            if (next > max) {
                max = next;
            }
        }
        return (byte) max;
    }

    /**
     * Returns a string representation of x, where x is treated as unsigned.
     *
     * @since 13.0
     */
    @Beta
    public static String toString(byte x) {
        return toString(x, 10);
    }

    /**
     * Returns a string representation of {@code x} for the given radix, where {@code x} is treated as
     * unsigned.
     *
     * @param x the value to convert to a string.
     * @param radix the radix to use while working with {@code x}
     * @throws IllegalArgumentException if {@code radix} is not between {@link Character#MIN_RADIX}
     *     and {@link Character#MAX_RADIX}.
     * @since 13.0
     */
    @Beta
    public static String toString(byte x, int radix) {
        checkArgument(
            radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX,
            "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX",
            radix);
        // Benchmarks indicate this is probably not worth optimizing.
        return Integer.toString(toInt(x), radix);
    }

    /**
     * Returns a string containing the supplied {@code byte} values separated by {@code separator}.
     * For example, {@code join(":", (byte) 1, (byte) 2, (byte) 255)} returns the string {@code
     * "1:2:255"}.
     *
     * @param separator the text that should appear between consecutive values in the resulting string
     *     (but not at the start or end)
     * @param array an array of {@code byte} values, possibly empty
     */
    public static String join(String separator, byte... array) {
        checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * (3 + separator.length()));
        builder.append(toInt(array[0]));
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(toString(array[i]));
        }
        return builder.toString();
    }

    private static byte flip(byte b) {
        return (byte) (b ^ 0x80);
    }

    /**
     * Sorts the array, treating its elements as unsigned bytes.
     *
     * @since 23.1
     */
    public static void sort(byte[] array) {
        checkNotNull(array);
        sort(array, 0, array.length);
    }

    /**
     * Sorts the array between {@code fromIndex} inclusive and {@code toIndex} exclusive, treating its
     * elements as unsigned bytes.
     *
     * @since 23.1
     */
    public static void sort(byte[] array, int fromIndex, int toIndex) {
        checkNotNull(array);
        checkPositionIndexes(fromIndex, toIndex, array.length);
        for (int i = fromIndex; i < toIndex; i++) {
            array[i] = flip(array[i]);
        }
        Arrays.sort(array, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            array[i] = flip(array[i]);
        }
    }

}
