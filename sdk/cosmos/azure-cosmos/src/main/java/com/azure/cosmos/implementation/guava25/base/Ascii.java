/*
 * Copyright (C) 2010 The Guava Authors
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

package com.azure.cosmos.implementation.guava25.base;

/**
 * Static methods pertaining to ASCII characters (those in the range of values {@code 0x00} through
 * {@code 0x7F}), and to strings containing such characters.
 *
 * <p>ASCII utilities also exist in other classes of this package:
 *
 * <ul>
 *   <!-- TODO(kevinb): how can we make this not produce a warning when building gwt javadoc? -->
 *   <li>{@link Charsets#US_ASCII} specifies the {@code Charset} of ASCII characters.
 * </ul>
 *
 * @author Catherine Berry
 * @author Gregory Kick
 * @since 7.0
 */
public final class Ascii {

  private Ascii() {}

  /* The ASCII control characters, per RFC 20. */

  /**
   * Form Feed ('\f'): A format effector which controls the movement of the printing position to the
   * first pre-determined printing line on the next form or page. (Applicable also to display
   * devices.)
   *
   * @since 8.0
   */
  public static final byte FF = 12;

  /**
   * Unit Separator: These four information separators may be used within data in optional fashion,
   * except that their hierarchical relationship shall be: FS is the most inclusive, then GS, then
   * RS, and US is least inclusive. (The content and length of a File, Group, Record, or Unit are
   * not specified.)
   *
   * @since 8.0
   */
  public static final byte US = 31;

    /**
   * The maximum value of an ASCII character.
   *
   * @since 9.0 (was type {@code int} before 12.0)
   */
  public static final char MAX = 127;

  /** A bit mask which selects the bit encoding ASCII character case. */
  private static final char CASE_MASK = 0x20;

  /**
   * Returns a copy of the input string in which all {@linkplain #isUpperCase(char) uppercase ASCII
   * characters} have been converted to lowercase. All other characters are copied without
   * modification.
   */
  public static String toLowerCase(String string) {
    int length = string.length();
    for (int i = 0; i < length; i++) {
      if (isUpperCase(string.charAt(i))) {
        char[] chars = string.toCharArray();
        for (; i < length; i++) {
          char c = chars[i];
          if (isUpperCase(c)) {
            chars[i] = (char) (c ^ CASE_MASK);
          }
        }
        return String.valueOf(chars);
      }
    }
    return string;
  }

  /**
   * Returns a copy of the input string in which all {@linkplain #isLowerCase(char) lowercase ASCII
   * characters} have been converted to uppercase. All other characters are copied without
   * modification.
   */
  public static String toUpperCase(String string) {
    int length = string.length();
    for (int i = 0; i < length; i++) {
      if (isLowerCase(string.charAt(i))) {
        char[] chars = string.toCharArray();
        for (; i < length; i++) {
          char c = chars[i];
          if (isLowerCase(c)) {
            chars[i] = (char) (c ^ CASE_MASK);
          }
        }
        return String.valueOf(chars);
      }
    }
    return string;
  }

  /**
   * If the argument is a {@linkplain #isLowerCase(char) lowercase ASCII character} returns the
   * uppercase equivalent. Otherwise returns the argument.
   */
  public static char toUpperCase(char c) {
    return isLowerCase(c) ? (char) (c ^ CASE_MASK) : c;
  }

  /**
   * Indicates whether {@code c} is one of the twenty-six lowercase ASCII alphabetic characters
   * between {@code 'a'} and {@code 'z'} inclusive. All others (including non-ASCII characters)
   * return {@code false}.
   */
  public static boolean isLowerCase(char c) {
    // Note: This was benchmarked against the alternate expression "(char)(c - 'a') < 26" (Nov '13)
    // and found to perform at least as well, or better.
    return (c >= 'a') && (c <= 'z');
  }

  /**
   * Indicates whether {@code c} is one of the twenty-six uppercase ASCII alphabetic characters
   * between {@code 'A'} and {@code 'Z'} inclusive. All others (including non-ASCII characters)
   * return {@code false}.
   */
  public static boolean isUpperCase(char c) {
    return (c >= 'A') && (c <= 'Z');
  }
}
