// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation;

import com.azure.core.util.CoreUtils;
import com.azure.cosmos.implementation.apachecommons.lang.StringUtils;
import com.azure.cosmos.implementation.guava25.base.Objects;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * While this class is public, but it is not part of our published public APIs.
 * This is meant to be internally used only by our sdk.
 */
public class Strings {
    public static final String EMPTY = "";

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

    public static String toString(boolean value) {
        return Boolean.toString(value);
    }

    public static String toString(int value) {
        return Integer.toString(value);
    }

    public static boolean areEqual(String str1, String str2) {
        return Objects.equal(str1, str2);
    }

    public static boolean areEqualIgnoreCase(String str1, String str2) {
        return StringUtils.equalsIgnoreCase(str1, str2);
    }

    public static boolean containsIgnoreCase(String str1, String str2) {
        return StringUtils.containsIgnoreCase(str1, str2);
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
}
