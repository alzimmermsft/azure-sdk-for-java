// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation;

import com.azure.core.util.CoreUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * While this class is public, but it is not part of our published public APIs.
 * This is meant to be internally used only by our sdk.
 */
public class Strings {
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String[] EMPTY_ARRAY = new String[0];

    private final static String UTF8_CHARSET = StandardCharsets.UTF_8.name();

    public static boolean isNullOrWhiteSpace(String str) {
        if (isEmpty(str)) {
            return true;
        }

        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }
    public static boolean isNullOrEmpty(String str) {
        return isEmpty(str);
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return CoreUtils.isNullOrEmpty(charSequence);
    }

    public static boolean isNotEmpty(CharSequence charSequence) {
        return !isEmpty(charSequence);
    }

    public static String trimToNull(String str) {
        return isNullOrEmpty(str) ? null : str;
    }

    public static String upperCase(String str) {
        return (str == null) ? null : str.toUpperCase();
    }

    /**
     * Strips the {@code str} of all {@code toStrip} characters it ends with.
     *
     * @param str The string to strip characters from at the end.
     * @param toStrip The character to strip.
     * @return The string with characters stripped, or the string as-is if nothing was stripped.
     */
    public static String stripEnd(String str, char toStrip) {
        if (isEmpty(str)) {
            return str;
        }

        int end = str.length();
        while (str.charAt(end - 1) == toStrip) {
            end--;
        }

        return str.substring(0, end);
    }

    /**
     * Removes the {@code end} character from the {@code str} if, and only if, the string ends with that character.
     * <p>
     * If the string doesn't end with {@code end} the string {@code str} is returned as-is.
     * <p>
     * If the string ends with multiple {@code end} characters only one is removed.
     *
     * @param str The string to remove the character from the end.
     * @param end The character to remove from the end.
     * @return The string with one instance of {@code end} removed, or the string as-is.
     */
    public static String removeEnd(String str, char end) {
        if (isEmpty(str)) {
            return str;
        }

        return str.charAt(str.length() - 1) == end
            ? str.substring(0, str.length() - 1)
            : str;
    }

    public static String join(List<String> strings, String join) {
        if (CoreUtils.isNullOrEmpty(strings)) {
            return null;
        }

        return CoreUtils.stringJoin(join, strings);
    }

    /**
     * Creates a string of the given {@code length} comprising {@code a} to {@code z} characters.
     *
     * @param length The length of the string.
     * @return A random alphabetic string.
     * @throws IllegalArgumentException If {@code length} is less than zero.
     */
    public static String randomAlphabetic(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("'length' must be zero or greater.");
        } else if (length == 0) {
            return EMPTY;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append('a' + random.nextInt(26));
        }

        return builder.toString();
    }

    /**
     * Repeats the given {@code str} the {@code count} number of times.
     * <p>
     * If {@code count} is 0 an empty string is returned. If {@code str} is null, null is returned.
     *
     * @param str The string to repeat.
     * @param count The number of times to repeat.
     * @return The repeated string.
     */
    public static String repeat(String str, int count) {
        if (str == null) {
            return null;
        }

        if (count == 0) {
            return EMPTY;
        } else if (count == 1) {
            return str;
        }

        StringBuilder builder = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            builder.append(str);
        }

        return builder.toString();
    }

    public static String repeat(char c, int count) {
        return repeat("" + c, count);
    }

    public static String stripFirstCharacter(String str) {
        if (str == null) {
            return null;
        }

        if (str.length() <= 1) {
            return EMPTY;
        }

        return str.substring(1);
    }

    public static String toString(boolean value) {
        return Boolean.toString(value);
    }

    public static String toString(int value) {
        return Integer.toString(value);
    }

    public static boolean areEqual(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    public static boolean areEqualIgnoreCase(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        } else if (str1 == str2) {
            return true;
        }

        return str1.equalsIgnoreCase(str2);
    }

    public static boolean containsIgnoreCase(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }

        int searchLength = str2.length();
        int searchCutOff = str1.length() - searchLength;
        for (int i = 0; i < searchCutOff; i++) {
            if (str1.regionMatches(true, i, str2, 0, searchLength)) {
                return true;
            }
        }

        return false;
    }

    public static String fromCamelCaseToUpperCase(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder result = new StringBuilder(str);

        int i = 1;
        while (i < result.length()) {
            if (Character.isUpperCase(result.charAt(i))) {
                result.insert(i, '_');
                i += 2;
            } else {
                result.replace(i, i + 1, Character.toString(Character.toUpperCase(result.charAt(i))));
                i ++;
            }
        }

        return result.toString();
    }

    public static String encodeURIComponent(String text) {
        String result;
        try {
            result = URLEncoder.encode(text, UTF8_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // after URLEncoding - the following transformations need to be applied
        // to get to encodeUriComponent consistent behavior
        //  "+" -> "%20"
        //  "%21" -> "!"
        //  "%27" -> "'"
        //  "%28" -> "("
        //  "%29" -> ")"
        //  "%7E" -> "~"

        final int len = result.length();
        final StringBuilder buf = new StringBuilder(
            result.length() + 4); // leaving enough buffer for two '+' replacements
                                          // without having to allocate new buffer
        for (int i = 0; i < len; i++) {
            char currentChar = result.charAt(i);

            if (currentChar == '+') {
                buf.append("%20");
            } else if (currentChar == '%' && i < len - 2) {
                char nextChar = result.charAt(i + 1);
                char secondToNextChar = result.charAt(i + 2);
                if (nextChar == '7' && secondToNextChar == 'E') {
                    i += 2;
                    buf.append('~');
                } else if (nextChar == '2') {
                    switch (secondToNextChar) {
                        case '1':
                            buf.append('!');
                            i += 2;
                            break;
                        case '7':
                            buf.append('\'');
                            i += 2;
                            break;
                        case '8':
                            buf.append('(');
                            i += 2;
                            break;
                        case '9':
                            buf.append(')');
                            i += 2;
                            break;
                        default:
                            buf.append(currentChar);
                    }
                } else {
                    buf.append(currentChar);
                }
            } else {
                buf.append(currentChar);
            }
        }

        return buf.toString();
    }

    /**
     * Splits the {@code toSplit} string on a fixed {@code length}.
     * <p>
     * If the length of the {@code toSplit} string isn't a multiple of {@code length} the last string will be smaller
     * than {@code length}.
     *
     * @param toSplit The string to split.
     * @param length The length of the split.
     * @return A list of substrings.
     * @throws NullPointerException If {@code toSplit} is null.
     * @throws IllegalArgumentException If {@code length} is less than or equal to zero.
     */
    public static List<String> splitFixedLength(String toSplit, int length) {
        Utils.checkNotNull(toSplit, "'toSplit' cannot be null.");
        Utils.checkArgument(length > 0, "'length' must be greater than zero (> 0).");

        int toSplitLength = toSplit.length();
        if (toSplitLength == 0) {
            // Special case where the to split string is empty.
            return Collections.singletonList("");
        }

        List<String> splitList = new ArrayList<>((int) Math.ceil(toSplitLength / (double) length));
        for (int i = 0; i < toSplitLength; i += length) {
            splitList.add(toSplit.substring(i, Math.min(length, toSplitLength - i)));
        }

        return Collections.unmodifiableList(splitList);
    }

    /**
     * Pads the start of {@code toPad} string with {@code padChar} if its length is less than {@code minLength}.
     * <p>
     * If {@code toPad} is longer than {@code minLength} this method is effectively a no-op.
     *
     * @param toPad The string to pad the start of.
     * @param minLength The minimum length of the returned string.
     * @param padChar The character to pad with.
     * @return A string where its start is padded with {@code padChar} until {@code minLength} is reached, or the string
     * as-is if it is already longer than {@code minLength}.
     * @throws NullPointerException If {@code toPad} is null.
     */
    public static String padStart(String toPad, int minLength, char padChar) {
        Utils.checkNotNull(toPad, "'toPad' cannot be null.");
        if (toPad.length() >= minLength) {
            return toPad;
        }

        StringBuilder builder = new StringBuilder(minLength);
        for (int i = 0; i < minLength - toPad.length(); i++) {
            builder.append(padChar);
        }
        return builder.append(toPad).toString();
    }
}
