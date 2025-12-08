/*
 * Copyright (C) 2009 The Guava Authors
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;

/**
 * Extracts non-overlapping substrings from an input string, typically by recognizing appearances of
 * a <i>separator</i> sequence. A splitter can extract adjacent substrings of a given
 * {@linkplain #fixedLength fixed length}.
 *
 * <p>For example, this expression:
 *
 * <pre>{@code
 * Splitter.on(',').split("foo,bar,qux")
 * }</pre>
 *
 * ... produces an {@code Iterable} containing {@code "foo"}, {@code "bar"} and {@code "qux"}, in
 * that order.
 *
 * <p>By default, {@code Splitter}'s behavior is simplistic and unassuming. The following
 * expression:
 *
 * <pre>{@code
 * Splitter.on(',').split(" foo,,,  bar ,")
 * }</pre>
 *
 * ... yields the substrings {@code [" foo", "", "", " bar ", ""]}. If this is not the desired
 * behavior, use configuration methods to obtain a <i>new</i> splitter instance with modified
 * behavior:
 *
 * <pre>{@code
 * private static final Splitter MY_SPLITTER = Splitter.on(',')
 *     .trimResults()
 *     .omitEmptyStrings();
 * }</pre>
 *
 * <p>Now {@code MY_SPLITTER.split("foo,,, bar ,")} returns just {@code ["foo", "bar"]}. Note that
 * the order in which these configuration methods are called is never significant.
 *
 * <p><b>Warning:</b> Splitter instances are immutable. Invoking a configuration method has no
 * effect on the receiving instance; you must store and use the new splitter instance it returns
 * instead.
 *
 * <pre>{@code
 * // Do NOT do this
 * Splitter splitter = Splitter.on('/');
 * splitter.trimResults(); // does nothing!
 * return splitter.split("wrong / wrong / wrong");
 * }</pre>
 *
 * <p>For separator-based splitters that do not use {@code omitEmptyStrings}, an input string
 * containing {@code n} occurrences of the separator naturally yields an iterable of size {@code n +
 * 1}. So if the separator does not occur anywhere in the input, a single substring is returned
 * containing the entire input. Consequently, all splitters split the empty string to {@code [""]}
 * (note: even fixed-length splitters).
 *
 * <p>Splitter instances are thread-safe immutable, and are therefore safe to store as {@code static
 * final} constants.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/StringsExplained#splitter">{@code Splitter}</a>.
 *
 * @author Julien Silland
 * @author Jesse Wilson
 * @author Kevin Bourrillion
 * @author Louis Wasserman
 * @since 1.0
 */
public final class Splitter {
  private final CharMatcher trimmer;
  private final boolean omitEmptyStrings;
  private final Strategy strategy;
  private final int limit;

  private Splitter(Strategy strategy) {
    this(strategy, false, CharMatcher.none(), Integer.MAX_VALUE);
  }

  private Splitter(Strategy strategy, boolean omitEmptyStrings, CharMatcher trimmer, int limit) {
    this.strategy = strategy;
    this.omitEmptyStrings = omitEmptyStrings;
    this.trimmer = trimmer;
    this.limit = limit;
  }

    /**
   * Returns a splitter that divides strings into pieces of the given length. For example, {@code
   * Splitter.fixedLength(2).split("abcde")} returns an iterable containing {@code ["ab", "cd",
   * "e"]}. The last piece can be smaller than {@code length} but will never be empty.
   *
   * <p><b>Exception:</b> for consistency with separator-based splitters, {@code split("")} does not
   * yield an empty iterable, but an iterable containing {@code ""}. This is the only case in which
   * {@code Iterables.size(split(input))} does not equal {@code IntMath.divide(input.length(),
   * length, CEILING)}. To avoid this behavior, use {@code omitEmptyStrings}.
   *
   * @param length the desired length of pieces after splitting, a positive integer
   * @return a splitter, with default settings, that can split into fixed sized pieces
   * @throws IllegalArgumentException if {@code length} is zero or negative
   */
  public static Splitter fixedLength(final int length) {
    checkArgument(length > 0, "The length may not be less than 1");

    return new Splitter(
        new Strategy() {
          @Override
          public SplittingIterator iterator(final Splitter splitter, CharSequence toSplit) {
            return new SplittingIterator(splitter, toSplit) {
              @Override
              public int separatorStart(int start) {
                int nextChunkStart = start + length;
                return (nextChunkStart < toSplit.length() ? nextChunkStart : -1);
              }

              @Override
              public int separatorEnd(int separatorPosition) {
                return separatorPosition;
              }
            };
          }
        });
  }

    /**
   * Returns a splitter that behaves equivalently to {@code this} splitter but stops splitting after
   * it reaches the limit. The limit defines the maximum number of items returned by the iterator,
   * or the maximum size of the list returned by {@link #splitToList}.
   *
   * <p>For example, {@code Splitter.on(',').limit(3).split("a,b,c,d")} returns an iterable
   * containing {@code ["a", "b", "c,d"]}. When omitting empty strings, the omitted strings do not
   * count. Hence, {@code Splitter.on(',').limit(3).omitEmptyStrings().split("a,,,b,,,c,d")} returns
   * an iterable containing {@code ["a", "b", "c,d"}. When trim is requested, all entries are
   * trimmed, including the last. Hence {@code Splitter.on(',').limit(3).trimResults().split(" a , b
   * , c , d ")} results in {@code ["a", "b", "c , d"]}.
   *
   * @param limit the maximum number of items returned
   * @return a splitter with the desired configuration
   * @since 9.0
   */
  public Splitter limit(int limit) {
    checkArgument(limit > 0, "must be greater than zero: %s", limit);
    return new Splitter(strategy, omitEmptyStrings, trimmer, limit);
  }

  private Iterator<String> splittingIterator(CharSequence sequence) {
    return strategy.iterator(this, sequence);
  }

  /**
   * Splits {@code sequence} into string components and returns them as an immutable list.
   *
   * @param sequence the sequence of characters to split
   * @return an immutable list of the segments split from the parameter
   * @since 15.0
   */

  public List<String> splitToList(CharSequence sequence) {
    checkNotNull(sequence);

    Iterator<String> iterator = splittingIterator(sequence);
    List<String> result = new ArrayList<>();

    while (iterator.hasNext()) {
      result.add(iterator.next());
    }

    return Collections.unmodifiableList(result);
  }

  private interface Strategy {
    Iterator<String> iterator(Splitter splitter, CharSequence toSplit);
  }

  private abstract static class SplittingIterator extends AbstractIterator<String> {
    final CharSequence toSplit;
    final CharMatcher trimmer;
    final boolean omitEmptyStrings;

    /**
     * Returns the first index in {@code toSplit} at or after {@code start} that contains the
     * separator.
     */
    abstract int separatorStart(int start);

    /**
     * Returns the first index in {@code toSplit} after {@code separatorPosition} that does not
     * contain a separator. This method is only invoked after a call to {@code separatorStart}.
     */
    abstract int separatorEnd(int separatorPosition);

    int offset = 0;
    int limit;

    protected SplittingIterator(Splitter splitter, CharSequence toSplit) {
      this.trimmer = splitter.trimmer;
      this.omitEmptyStrings = splitter.omitEmptyStrings;
      this.limit = splitter.limit;
      this.toSplit = toSplit;
    }

    @Override
    protected String computeNext() {
      /*
       * The returned string will be from the end of the last match to the beginning of the next
       * one. nextStart is the start position of the returned substring, while offset is the place
       * to start looking for a separator.
       */
      int nextStart = offset;
      while (offset != -1) {
        int start = nextStart;
        int end;

        int separatorPosition = separatorStart(offset);
        if (separatorPosition == -1) {
          end = toSplit.length();
          offset = -1;
        } else {
          end = separatorPosition;
          offset = separatorEnd(separatorPosition);
        }
        if (offset == nextStart) {
          /*
           * This occurs when some pattern has an empty match, even if it doesn't match the empty
           * string -- for example, if it requires lookahead or the like. The offset must be
           * increased to look for separators beyond this point, without changing the start position
           * of the next returned substring -- so nextStart stays the same.
           */
          offset++;
          if (offset > toSplit.length()) {
            offset = -1;
          }
          continue;
        }

        while (start < end && trimmer.matches(toSplit.charAt(start))) {
          start++;
        }
        while (end > start && trimmer.matches(toSplit.charAt(end - 1))) {
          end--;
        }

        if (omitEmptyStrings && start == end) {
          // Don't include the (unused) separator in next split string.
          nextStart = offset;
          continue;
        }

        if (limit == 1) {
          // The limit has been reached, return the rest of the string as the
          // final item. This is tested after empty string removal so that
          // empty strings do not count towards the limit.
          end = toSplit.length();
          offset = -1;
          // Since we may have changed the end, we need to trim it again.
          while (end > start && trimmer.matches(toSplit.charAt(end - 1))) {
            end--;
          }
        } else {
          limit--;
        }

        return toSplit.subSequence(start, end).toString();
      }
      return endOfData();
    }
  }
}
