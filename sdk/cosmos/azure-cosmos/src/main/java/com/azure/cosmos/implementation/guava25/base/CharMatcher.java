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

package com.azure.cosmos.implementation.guava25.base;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkPositionIndex;

import java.util.BitSet;

/**
 * Determines a true or false value for any Java {@code char} value, just as {@link Predicate} does
 * for any {@link Object}. Also offers basic text processing methods based on this function.
 * Implementations are strongly encouraged to be side-effect-free and immutable.
 *
 * <p>Throughout the documentation of this class, the phrase "matching character" is used to mean
 * "any {@code char} value {@code c} for which {@code this.matches(c)} returns {@code true}".
 *
 * <p><b>Warning:</b> This class deals only with {@code char} values, that is, <a
 * href="http://www.unicode.org/glossary/#BMP_character">BMP characters</a>. It does not understand
 * <a href="http://www.unicode.org/glossary/#supplementary_code_point">supplementary Unicode code
 * points</a> in the range {@code 0x10000} to {@code 0x10FFFF} which includes the majority of
 * assigned characters, including important CJK characters and emoji.
 *
 * <p>Supplementary characters are <a
 * href="https://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#supplementary">encoded
 * into a {@code String} using surrogate pairs</a>, and a {@code CharMatcher} treats these just as
 * two separate characters. {@link #countIn} counts each supplementary character as 2 {@code char}s.
 *
 * <p>For up-to-date Unicode character properties (digit, letter, etc.) and support for
 * supplementary code points, use ICU4J UCharacter and UnicodeSet (freeze() after building). For
 * basic text processing based on UnicodeSet use the ICU4J UnicodeSetSpanner.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/StringsExplained#charmatcher">{@code CharMatcher}
 * </a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public abstract class CharMatcher implements Predicate<Character> {
  /*
   *           N777777777NO
   *         N7777777777777N
   *        M777777777777777N
   *        $N877777777D77777M
   *       N M77777777ONND777M
   *       MN777777777NN  D777
   *     N7ZN777777777NN ~M7778
   *    N777777777777MMNN88777N
   *    N777777777777MNZZZ7777O
   *    DZN7777O77777777777777
   *     N7OONND7777777D77777N
   *      8$M++++?N???$77777$
   *       M7++++N+M77777777N
   *        N77O777777777777$                              M
   *          DNNM$$$$777777N                              D
   *         N$N:=N$777N7777M                             NZ
   *        77Z::::N777777777                          ODZZZ
   *       77N::::::N77777777M                         NNZZZ$
   *     $777:::::::77777777MN                        ZM8ZZZZZ
   *     777M::::::Z7777777Z77                        N++ZZZZNN
   *    7777M:::::M7777777$777M                       $++IZZZZM
   *   M777$:::::N777777$M7777M                       +++++ZZZDN
   *     NN$::::::7777$$M777777N                      N+++ZZZZNZ
   *       N::::::N:7$O:77777777                      N++++ZZZZN
   *       M::::::::::::N77777777+                   +?+++++ZZZM
   *       8::::::::::::D77777777M                    O+++++ZZ
   *        ::::::::::::M777777777N                      O+?D
   *        M:::::::::::M77777777778                     77=
   *        D=::::::::::N7777777777N                    777
   *       INN===::::::=77777777777N                  I777N
   *      ?777N========N7777777777787M               N7777
   *      77777$D======N77777777777N777N?         N777777
   *     I77777$$$N7===M$$77777777$77777777$MMZ77777777N
   *      $$$$$$$$$$$NIZN$$$$$$$$$M$$7777777777777777ON
   *       M$$$$$$$$M    M$$$$$$$$N=N$$$$7777777$$$ND
   *      O77Z$$$$$$$     M$$$$$$$$MNI==$DNNNNM=~N
   *   7 :N MNN$$$$M$      $$$777$8      8D8I
   *     NMM.:7O           777777778
   *                       7777777MN
   *                       M NO .7:
   *                       M   :   M
   *                            8
   */

  // Constant matcher factory methods

  /**
   * Matches any character.
   *
   * @since 19.0 (since 1.0 as constant {@code ANY})
   */
  public static CharMatcher any() {
    return Any.INSTANCE;
  }

  /**
   * Matches no characters.
   *
   * @since 19.0 (since 1.0 as constant {@code NONE})
   */
  public static CharMatcher none() {
    return None.INSTANCE;
  }

    // Static factories

  /** Returns a {@code char} matcher that matches only one specified BMP character. */
  public static CharMatcher is(final char match) {
    return new Is(match);
  }

  /**
   * Returns a {@code char} matcher that matches any character except the BMP character specified.
   *
   * <p>To negate another {@code CharMatcher}, use {@link #negate()}.
   */
  public static CharMatcher isNot(final char match) {
    return new IsNot(match);
  }

    /**
   * Returns a {@code char} matcher that matches any character in a given BMP range (both endpoints
   * are inclusive). For example, to match any lowercase letter of the English alphabet, use {@code
   * CharMatcher.inRange('a', 'z')}.
   *
   * @throws IllegalArgumentException if {@code endInclusive < startInclusive}
   */
  public static CharMatcher inRange(final char startInclusive, final char endInclusive) {
    return new InRange(startInclusive, endInclusive);
  }

    // Constructors

  /**
   * Constructor for use by subclasses. When subclassing, you may want to override {@code
   * toString()} to provide a useful description.
   */
  protected CharMatcher() {}

  // Abstract methods

  /** Determines a true or false value for the given character. */
  public abstract boolean matches(char c);

  // Non-static factories

  /** Returns a matcher that matches any character not matched by this matcher. */
  // @Override under Java 8 but not under Java 7
  public CharMatcher negate() {
    return new Negated(this);
  }

  /**
   * Returns a matcher that matches any character matched by both this matcher and {@code other}.
   */
  public CharMatcher and(CharMatcher other) {
    return new And(this, other);
  }

  /**
   * Returns a matcher that matches any character matched by either this matcher or {@code other}.
   */
  public CharMatcher or(CharMatcher other) {
    return new Or(this, other);
  }

    /** Sets bits in {@code table} matched by this matcher. */
  void setBits(BitSet table) {
    for (int c = Character.MAX_VALUE; c >= Character.MIN_VALUE; c--) {
      if (matches((char) c)) {
        table.set(c);
      }
    }
  }

  // Text processing routines

    /**
   * Returns {@code true} if a character sequence contains only matching BMP characters.
   *
   * <p>The default implementation iterates over the sequence, invoking {@link #matches} for each
   * character, until this returns {@code false} or the end is reached.
   *
   * @param sequence the character sequence to examine, possibly empty
   * @return {@code true} if this matcher matches every character in the sequence, including when
   *     the sequence is empty
   */
  public boolean matchesAllOf(CharSequence sequence) {
    for (int i = sequence.length() - 1; i >= 0; i--) {
      if (!matches(sequence.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns {@code true} if a character sequence contains no matching BMP characters. Equivalent to
   * {@code !matchesAnyOf(sequence)}.
   *
   * <p>The default implementation iterates over the sequence, invoking {@link #matches} for each
   * character, until this returns {@code true} or the end is reached.
   *
   * @param sequence the character sequence to examine, possibly empty
   * @return {@code true} if this matcher matches no characters in the sequence, including when the
   *     sequence is empty
   */
  public boolean matchesNoneOf(CharSequence sequence) {
    return indexIn(sequence) == -1;
  }

  /**
   * Returns the index of the first matching BMP character in a character sequence, or {@code -1} if
   * no matching character is present.
   *
   * <p>The default implementation iterates over the sequence in forward order calling {@link
   * #matches} for each character.
   *
   * @param sequence the character sequence to examine from the beginning
   * @return an index, or {@code -1} if no character matches
   */
  public int indexIn(CharSequence sequence) {
    return indexIn(sequence, 0);
  }

  /**
   * Returns the index of the first matching BMP character in a character sequence, starting from a
   * given position, or {@code -1} if no character matches after that position.
   *
   * <p>The default implementation iterates over the sequence in forward order, beginning at {@code
   * start}, calling {@link #matches} for each character.
   *
   * @param sequence the character sequence to examine
   * @param start the first index to examine; must be nonnegative and no greater than {@code
   *     sequence.length()}
   * @return the index of the first matching character, guaranteed to be no less than {@code start},
   *     or {@code -1} if no character matches
   * @throws IndexOutOfBoundsException if start is negative or greater than {@code
   *     sequence.length()}
   */
  public int indexIn(CharSequence sequence, int start) {
    int length = sequence.length();
    checkPositionIndex(start, length);
    for (int i = start; i < length; i++) {
      if (matches(sequence.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

    /**
   * Returns the number of matching {@code char}s found in a character sequence.
   *
   * <p>Counts 2 per supplementary character.
   */
  public int countIn(CharSequence sequence) {
    int count = 0;
    for (int i = 0; i < sequence.length(); i++) {
      if (matches(sequence.charAt(i))) {
        count++;
      }
    }
    return count;
  }

    /**
   * @deprecated Provided only to satisfy the {@link Predicate} interface; use {@link #matches}
   *     instead.
   */
  @Deprecated
  @Override
  public boolean apply(Character character) {
    return matches(character);
  }

  /**
   * Returns a string representation of this {@code CharMatcher}, such as {@code
   * CharMatcher.or(WHITESPACE, JAVA_DIGIT)}.
   */
  @Override
  public String toString() {
    return super.toString();
  }

  /**
   * Returns the Java Unicode escape sequence for the given {@code char}, in the form "\u12AB" where
   * "12AB" is the four hexadecimal digits representing the 16-bit code unit.
   */
  private static String showCharacter(char c) {
    String hex = "0123456789ABCDEF";
    char[] tmp = {'\\', 'u', '\0', '\0', '\0', '\0'};
    for (int i = 0; i < 4; i++) {
      tmp[5 - i] = hex.charAt(c & 0xF);
      c = (char) (c >> 4);
    }
    return String.copyValueOf(tmp);
  }

  // Fast matchers

  /** A matcher for which precomputation will not yield any significant benefit. */
  abstract static class FastMatcher extends CharMatcher {

      @Override
    public CharMatcher negate() {
      return new NegatedFastMatcher(this);
    }
  }

  /** {@link FastMatcher} which overrides {@code toString()} with a custom name. */
  abstract static class NamedFastMatcher extends FastMatcher {

    private final String description;

    NamedFastMatcher(String description) {
      this.description = checkNotNull(description);
    }

    @Override
    public final String toString() {
      return description;
    }
  }

  /** Negation of a {@link FastMatcher}. */
  static class NegatedFastMatcher extends Negated {

    NegatedFastMatcher(CharMatcher original) {
      super(original);
    }

  }

    // Static constant implementation classes

  /** Implementation of {@link #any()}. */
  private static final class Any extends NamedFastMatcher {

    static final Any INSTANCE = new Any();

    private Any() {
      super("CharMatcher.any()");
    }

    @Override
    public boolean matches(char c) {
      return true;
    }

    @Override
    public int indexIn(CharSequence sequence) {
      return (sequence.length() == 0) ? -1 : 0;
    }

    @Override
    public int indexIn(CharSequence sequence, int start) {
      int length = sequence.length();
      checkPositionIndex(start, length);
      return (start == length) ? -1 : start;
    }

      @Override
    public boolean matchesAllOf(CharSequence sequence) {
      checkNotNull(sequence);
      return true;
    }

    @Override
    public boolean matchesNoneOf(CharSequence sequence) {
      return sequence.length() == 0;
    }

      @Override
    public int countIn(CharSequence sequence) {
      return sequence.length();
    }

    @Override
    public CharMatcher and(CharMatcher other) {
      return checkNotNull(other);
    }

    @Override
    public CharMatcher or(CharMatcher other) {
      checkNotNull(other);
      return this;
    }

    @Override
    public CharMatcher negate() {
      return none();
    }
  }

  /** Implementation of {@link #none()}. */
  private static final class None extends NamedFastMatcher {

    static final None INSTANCE = new None();

    private None() {
      super("CharMatcher.none()");
    }

    @Override
    public boolean matches(char c) {
      return false;
    }

    @Override
    public int indexIn(CharSequence sequence) {
      checkNotNull(sequence);
      return -1;
    }

    @Override
    public int indexIn(CharSequence sequence, int start) {
      int length = sequence.length();
      checkPositionIndex(start, length);
      return -1;
    }

      @Override
    public boolean matchesAllOf(CharSequence sequence) {
      return sequence.length() == 0;
    }

    @Override
    public boolean matchesNoneOf(CharSequence sequence) {
      checkNotNull(sequence);
      return true;
    }

      @Override
    public int countIn(CharSequence sequence) {
      checkNotNull(sequence);
      return 0;
    }

    @Override
    public CharMatcher and(CharMatcher other) {
      checkNotNull(other);
      return this;
    }

    @Override
    public CharMatcher or(CharMatcher other) {
      return checkNotNull(other);
    }

    @Override
    public CharMatcher negate() {
      return any();
    }
  }

    // Non-static factory implementation classes

  /** Implementation of {@link #negate()}. */
  private static class Negated extends CharMatcher {

    final CharMatcher original;

    Negated(CharMatcher original) {
      this.original = checkNotNull(original);
    }

    @Override
    public boolean matches(char c) {
      return !original.matches(c);
    }

    @Override
    public boolean matchesAllOf(CharSequence sequence) {
      return original.matchesNoneOf(sequence);
    }

    @Override
    public boolean matchesNoneOf(CharSequence sequence) {
      return original.matchesAllOf(sequence);
    }

    @Override
    public int countIn(CharSequence sequence) {
      return sequence.length() - original.countIn(sequence);
    }

    @Override
    void setBits(BitSet table) {
      BitSet tmp = new BitSet();
      original.setBits(tmp);
      tmp.flip(Character.MIN_VALUE, Character.MAX_VALUE + 1);
      table.or(tmp);
    }

    @Override
    public CharMatcher negate() {
      return original;
    }

    @Override
    public String toString() {
      return original + ".negate()";
    }
  }

  /** Implementation of {@link #and(CharMatcher)}. */
  private static final class And extends CharMatcher {

    final CharMatcher first;
    final CharMatcher second;

    And(CharMatcher a, CharMatcher b) {
      first = checkNotNull(a);
      second = checkNotNull(b);
    }

    @Override
    public boolean matches(char c) {
      return first.matches(c) && second.matches(c);
    }

    @Override
    void setBits(BitSet table) {
      BitSet tmp1 = new BitSet();
      first.setBits(tmp1);
      BitSet tmp2 = new BitSet();
      second.setBits(tmp2);
      tmp1.and(tmp2);
      table.or(tmp1);
    }

    @Override
    public String toString() {
      return "CharMatcher.and(" + first + ", " + second + ")";
    }
  }

  /** Implementation of {@link #or(CharMatcher)}. */
  private static final class Or extends CharMatcher {

    final CharMatcher first;
    final CharMatcher second;

    Or(CharMatcher a, CharMatcher b) {
      first = checkNotNull(a);
      second = checkNotNull(b);
    }

    @Override
    void setBits(BitSet table) {
      first.setBits(table);
      second.setBits(table);
    }

    @Override
    public boolean matches(char c) {
      return first.matches(c) || second.matches(c);
    }

    @Override
    public String toString() {
      return "CharMatcher.or(" + first + ", " + second + ")";
    }
  }

  // Static factory implementations

  /** Implementation of {@link #is(char)}. */
  private static final class Is extends FastMatcher {

    private final char match;

    Is(char match) {
      this.match = match;
    }

    @Override
    public boolean matches(char c) {
      return c == match;
    }

      @Override
    public CharMatcher and(CharMatcher other) {
      return other.matches(match) ? this : none();
    }

    @Override
    public CharMatcher or(CharMatcher other) {
      return other.matches(match) ? other : super.or(other);
    }

    @Override
    public CharMatcher negate() {
      return isNot(match);
    }

    @Override
    void setBits(BitSet table) {
      table.set(match);
    }

    @Override
    public String toString() {
      return "CharMatcher.is('" + showCharacter(match) + "')";
    }
  }

  /** Implementation of {@link #isNot(char)}. */
  private static final class IsNot extends FastMatcher {

    private final char match;

    IsNot(char match) {
      this.match = match;
    }

    @Override
    public boolean matches(char c) {
      return c != match;
    }

    @Override
    public CharMatcher and(CharMatcher other) {
      return other.matches(match) ? super.and(other) : other;
    }

    @Override
    public CharMatcher or(CharMatcher other) {
      return other.matches(match) ? any() : this;
    }

    @Override
    void setBits(BitSet table) {
      table.set(0, match);
      table.set(match + 1, Character.MAX_VALUE + 1);
    }

    @Override
    public CharMatcher negate() {
      return is(match);
    }

    @Override
    public String toString() {
      return "CharMatcher.isNot('" + showCharacter(match) + "')";
    }
  }

    /** Implementation of {@link #inRange(char, char)}. */
  private static final class InRange extends FastMatcher {

    private final char startInclusive;
    private final char endInclusive;

    InRange(char startInclusive, char endInclusive) {
      checkArgument(endInclusive >= startInclusive);
      this.startInclusive = startInclusive;
      this.endInclusive = endInclusive;
    }

    @Override
    public boolean matches(char c) {
      return startInclusive <= c && c <= endInclusive;
    }

    @Override
    void setBits(BitSet table) {
      table.set(startInclusive, endInclusive + 1);
    }

    @Override
    public String toString() {
      return "CharMatcher.inRange('"
          + showCharacter(startInclusive)
          + "', '"
          + showCharacter(endInclusive)
          + "')";
    }
  }

}
