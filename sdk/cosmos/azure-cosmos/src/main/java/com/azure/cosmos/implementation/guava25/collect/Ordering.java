/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Portions Copyright (c) Microsoft Corporation
 */

package com.azure.cosmos.implementation.guava25.collect;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.guava25.collect.CollectPreconditions.checkNonnegative;


import com.azure.cosmos.implementation.guava25.base.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

/**
 * A comparator, with additional methods to support common operations. This is an "enriched" version
 * of {@code Comparator} for pre-Java-8 users, in the same sense that {@link FluentIterable} is an
 * enriched {@link Iterable} for pre-Java-8 users.
 *
 * <h3>Three types of methods</h3>
 *
 * Like other fluent types, there are three types of methods present: methods for <i>acquiring</i>,
 * <i>chaining</i>, and <i>using</i>.
 *
 * <h4>Acquiring</h4>
 *
 * <p>The common ways to get an instance of {@code Ordering} are:
 *
 * <ul>
 *   <li>Subclass it and implement {@link #compare} instead of implementing {@link Comparator}
 *       directly
 *   <li>Pass a <i>pre-existing</i> {@link Comparator} instance to {@link #from(Comparator)}
 *   <li>Use the natural ordering, {@link Ordering#natural}
 * </ul>
 *
 * <h4>Chaining</h4>
 *
 * <p>Then you can use the <i>chaining</i> methods to get an altered version of that {@code
 * Ordering}, including:
 *
 * <ul>
 *   <li>{@link #reverse}
 *   <li>{@link #compound(Comparator)}
 *   <li>{@link #onResultOf(Function)}
 *   <li>{@link #nullsFirst} / {@link #nullsLast}
 * </ul>
 *
 * <h4>Using</h4>
 *
 * <p>Finally, use the resulting {@code Ordering} anywhere a {@link Comparator} is required, or use
 * any of its special operations, such as:
 *
 * <ul>
 *   <li>{@link #immutableSortedCopy}
 *   <li>{@link #isOrdered} / {@link #isStrictlyOrdered}
 *   <li>{@link #min} / {@link #max}
 * </ul>
 *
 * <h3>Understanding complex orderings</h3>
 *
 * <p>Complex chained orderings like the following example can be challenging to understand.
 *
 * <pre>{@code
 * Ordering<Foo> ordering =
 *     Ordering.natural()
 *         .nullsFirst()
 *         .onResultOf(getBarFunction)
 *         .nullsLast();
 * }</pre>
 *
 * Note that each chaining method returns a new ordering instance which is backed by the previous
 * instance, but has the chance to act on values <i>before</i> handing off to that backing instance.
 * As a result, it usually helps to read chained ordering expressions <i>backwards</i>. For example,
 * when {@code compare} is called on the above ordering:
 *
 * <ol>
 *   <li>First, if only one {@code Foo} is null, that null value is treated as <i>greater</i>
 *   <li>Next, non-null {@code Foo} values are passed to {@code getBarFunction} (we will be
 *       comparing {@code Bar} values from now on)
 *   <li>Next, if only one {@code Bar} is null, that null value is treated as <i>lesser</i>
 *   <li>Finally, natural ordering is used (i.e. the result of {@code Bar.compareTo(Bar)} is
 *       returned)
 * </ol>
 *
 * <p>Alas, {@link #reverse} is a little different. As you read backwards through a chain and
 * encounter a call to {@code reverse}, continue working backwards until a result is determined, and
 * then reverse that result.
 *
 * <h3>Additional notes</h3>
 *
 * <p>Except as noted, the orderings returned by the factory methods of this class are serializable
 * if and only if the provided instances that back them are. For example, if {@code ordering} and
 * {@code function} can themselves be serialized, then {@code ordering.onResultOf(function)} can as
 * well.
 *
 * <h3>For Java 8 users</h3>
 *
 * <p>If you are using Java 8, this class is now obsolete. Most of its functionality is now provided
 * by {@link java.util.stream.Stream Stream} and by {@link Comparator} itself, and the rest can now
 * be found as static methods in our new {@link Comparators} class. See each method below for
 * further instructions. Whenever possible, you should change any references of type {@code
 * Ordering} to be of type {@code Comparator} instead. However, at this time we have no plan to
 * <i>deprecate</i> this class.
 *
 * <p>Many replacements involve adopting {@code Stream}, and these changes can sometimes make your
 * code verbose. Whenever following this advice, you should check whether {@code Stream} could be
 * adopted more comprehensively in your code; the end result may be quite a bit simpler.
 *
 * <h3>See also</h3>
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/OrderingExplained">{@code Ordering}</a>.
 *
 * @author Jesse Wilson
 * @author Kevin Bourrillion
 * @since 2.0
 */
public abstract class Ordering<T> implements Comparator<T> {
  // Natural order

  /**
   * Returns a serializable ordering that uses the natural order of the values. The ordering throws
   * a {@link NullPointerException} when passed a null parameter.
   *
   * <p>The type specification is {@code <C extends Comparable>}, instead of the technically correct
   * {@code <C extends Comparable<? super C>>}, to support legacy types from before Java 5.
   *
   * <p><b>Java 8 users:</b> use {@link Comparator#naturalOrder} instead.
   */
  @SuppressWarnings({"unchecked", "rawtypes"}) // TODO(kevinb): right way to explain this??
  public static <C extends Comparable> Ordering<C> natural() {
    return (Ordering<C>) NaturalOrdering.INSTANCE;
  }

  // Static factories

  /**
   * Returns an ordering based on an <i>existing</i> comparator instance. Note that it is
   * unnecessary to create a <i>new</i> anonymous inner class implementing {@code Comparator} just
   * to pass it in here. Instead, simply subclass {@code Ordering} and implement its {@code compare}
   * method directly.
   *
   * <p><b>Java 8 users:</b> this class is now obsolete as explained in the class documentation, so
   * there is no need to use this method.
   *
   * @param comparator the comparator that defines the order
   * @return comparator itself if it is already an {@code Ordering}; otherwise an ordering that
   *     wraps that comparator
   */
  public static <T> Ordering<T> from(Comparator<T> comparator) {
    return (comparator instanceof Ordering)
        ? (Ordering<T>) comparator
        : new ComparatorOrdering<T>(comparator);
  }

  // Ordering<Object> singletons

    // Constructor

  /**
   * Constructs a new instance of this class (only invokable by the subclass constructor, typically
   * implicit).
   */
  protected Ordering() {}

  // Instance-based factories (and any static equivalents)

  /**
   * Returns the reverse of this ordering; the {@code Ordering} equivalent to {@link
   * Collections#reverseOrder(Comparator)}.
   *
   * <p><b>Java 8 users:</b> Use {@code thisComparator.reversed()} instead.
   */
  // type parameter <S> lets us avoid the extra <String> in statements like:
  // Ordering<String> o = Ordering.<String>natural().reverse();
  public <S extends T> Ordering<S> reverse() {
    return new ReverseOrdering<S>(this);
  }

  /**
   * Returns an ordering that treats {@code null} as less than all other values and uses {@code
   * this} to compare non-null values.
   *
   * <p><b>Java 8 users:</b> Use {@code Comparator.nullsFirst(thisComparator)} instead.
   */
  // type parameter <S> lets us avoid the extra <String> in statements like:
  // Ordering<String> o = Ordering.<String>natural().nullsFirst();
  public <S extends T> Ordering<S> nullsFirst() {
    return new NullsFirstOrdering<S>(this);
  }

  /**
   * Returns an ordering that treats {@code null} as greater than all other values and uses this
   * ordering to compare non-null values.
   *
   * <p><b>Java 8 users:</b> Use {@code Comparator.nullsLast(thisComparator)} instead.
   */
  // type parameter <S> lets us avoid the extra <String> in statements like:
  // Ordering<String> o = Ordering.<String>natural().nullsLast();
  public <S extends T> Ordering<S> nullsLast() {
    return new NullsLastOrdering<S>(this);
  }

  /**
   * Returns a new ordering on {@code F} which orders elements by first applying a function to them,
   * then comparing those results using {@code this}. For example, to compare objects by their
   * string forms, in a case-insensitive manner, use:
   *
   * <pre>{@code
   * Ordering.from(String.CASE_INSENSITIVE_ORDER)
   *     .onResultOf(Functions.toStringFunction())
   * }</pre>
   *
   * <p><b>Java 8 users:</b> Use {@code Comparator.comparing(function, thisComparator)} instead (you
   * can omit the comparator if it is the natural order).
   */
  public <F> Ordering<F> onResultOf(Function<F, ? extends T> function) {
    return new ByFunctionOrdering<>(function, this);
  }

  <T2 extends T> Ordering<Entry<T2, ?>> onKeys() {
    return onResultOf(Maps.<T2>keyFunction());
  }

    // Regular instance methods

  // Override to add
  @Override
  public abstract int compare(T left, T right);

  /**
   * Returns the least of the specified values according to this ordering. If there are multiple
   * least values, the first of those is returned. The iterator will be left exhausted: its {@code
   * hasNext()} method will return {@code false}.
   *
   * <p><b>Java 8 users:</b> Continue to use this method for now. After the next release of Guava,
   * use {@code Streams.stream(iterator).min(thisComparator).get()} instead (but note that it does
   * not guarantee which tied minimum element is returned).
   *
   * @param iterator the iterator whose minimum element is to be determined
   * @throws NoSuchElementException if {@code iterator} is empty
   * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
   *     ordering.
   * @since 11.0
   */
  public <E extends T> E min(Iterator<E> iterator) {
    // let this throw NoSuchElementException as necessary
    E minSoFar = iterator.next();

    while (iterator.hasNext()) {
      minSoFar = min(minSoFar, iterator.next());
    }

    return minSoFar;
  }

  /**
   * Returns the least of the specified values according to this ordering. If there are multiple
   * least values, the first of those is returned.
   *
   * <p><b>Java 8 users:</b> If {@code iterable} is a {@link Collection}, use {@code
   * Collections.min(collection, thisComparator)} instead. Otherwise, continue to use this method
   * for now. After the next release of Guava, use {@code
   * Streams.stream(iterable).min(thisComparator).get()} instead. Note that these alternatives do
   * not guarantee which tied minimum element is returned)
   *
   * @param iterable the iterable whose minimum element is to be determined
   * @throws NoSuchElementException if {@code iterable} is empty
   * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
   *     ordering.
   */
  public <E extends T> E min(Iterable<E> iterable) {
    return min(iterable.iterator());
  }

  /**
   * Returns the lesser of the two values according to this ordering. If the values compare as 0,
   * the first is returned.
   *
   * <p><b>Implementation note:</b> this method is invoked by the default implementations of the
   * other {@code min} overloads, so overriding it will affect their behavior.
   *
   * <p><b>Java 8 users:</b> Use {@code Collections.min(Arrays.asList(a, b), thisComparator)}
   * instead (but note that it does not guarantee which tied minimum element is returned).
   *
   * @param a value to compare, returned if less than or equal to b.
   * @param b value to compare.
   * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
   *     ordering.
   */
  public <E extends T> E min(E a, E b) {
    return (compare(a, b) <= 0) ? a : b;
  }

  /**
   * Returns the least of the specified values according to this ordering. If there are multiple
   * least values, the first of those is returned.
   *
   * <p><b>Java 8 users:</b> Use {@code Collections.min(Arrays.asList(a, b, c...), thisComparator)}
   * instead (but note that it does not guarantee which tied minimum element is returned).
   *
   * @param a value to compare, returned if less than or equal to the rest.
   * @param b value to compare
   * @param c value to compare
   * @param rest values to compare
   * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
   *     ordering.
   */
  @SuppressWarnings("unchecked")
  public <E extends T> E min(E a, E b, E c, E... rest) {
    E minSoFar = min(min(a, b), c);

    for (E r : rest) {
      minSoFar = min(minSoFar, r);
    }

    return minSoFar;
  }

  /**
   * Returns the greatest of the specified values according to this ordering. If there are multiple
   * greatest values, the first of those is returned. The iterator will be left exhausted: its
   * {@code hasNext()} method will return {@code false}.
   *
   * <p><b>Java 8 users:</b> Continue to use this method for now. After the next release of Guava,
   * use {@code Streams.stream(iterator).max(thisComparator).get()} instead (but note that it does
   * not guarantee which tied maximum element is returned).
   *
   * @param iterator the iterator whose maximum element is to be determined
   * @throws NoSuchElementException if {@code iterator} is empty
   * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
   *     ordering.
   * @since 11.0
   */
  public <E extends T> E max(Iterator<E> iterator) {
    // let this throw NoSuchElementException as necessary
    E maxSoFar = iterator.next();

    while (iterator.hasNext()) {
      maxSoFar = max(maxSoFar, iterator.next());
    }

    return maxSoFar;
  }

  /**
   * Returns the greatest of the specified values according to this ordering. If there are multiple
   * greatest values, the first of those is returned.
   *
   * <p><b>Java 8 users:</b> If {@code iterable} is a {@link Collection}, use {@code
   * Collections.max(collection, thisComparator)} instead. Otherwise, continue to use this method
   * for now. After the next release of Guava, use {@code
   * Streams.stream(iterable).max(thisComparator).get()} instead. Note that these alternatives do
   * not guarantee which tied maximum element is returned)
   *
   * @param iterable the iterable whose maximum element is to be determined
   * @throws NoSuchElementException if {@code iterable} is empty
   * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
   *     ordering.
   */
  public <E extends T> E max(Iterable<E> iterable) {
    return max(iterable.iterator());
  }

  /**
   * Returns the greater of the two values according to this ordering. If the values compare as 0,
   * the first is returned.
   *
   * <p><b>Implementation note:</b> this method is invoked by the default implementations of the
   * other {@code max} overloads, so overriding it will affect their behavior.
   *
   * <p><b>Java 8 users:</b> Use {@code Collections.max(Arrays.asList(a, b), thisComparator)}
   * instead (but note that it does not guarantee which tied maximum element is returned).
   *
   * @param a value to compare, returned if greater than or equal to b.
   * @param b value to compare.
   * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
   *     ordering.
   */
  public <E extends T> E max(E a, E b) {
    return (compare(a, b) >= 0) ? a : b;
  }

  /**
   * Returns the greatest of the specified values according to this ordering. If there are multiple
   * greatest values, the first of those is returned.
   *
   * <p><b>Java 8 users:</b> Use {@code Collections.max(Arrays.asList(a, b, c...), thisComparator)}
   * instead (but note that it does not guarantee which tied maximum element is returned).
   *
   * @param a value to compare, returned if greater than or equal to the rest.
   * @param b value to compare
   * @param c value to compare
   * @param rest values to compare
   * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
   *     ordering.
   */
  @SuppressWarnings("unchecked")
  public <E extends T> E max(E a, E b, E c, E... rest) {
    E maxSoFar = max(max(a, b), c);

    for (E r : rest) {
      maxSoFar = max(maxSoFar, r);
    }

    return maxSoFar;
  }

  /**
   * Returns the {@code k} least elements of the given iterable according to this ordering, in order
   * from least to greatest. If there are fewer than {@code k} elements present, all will be
   * included.
   *
   * <p>The implementation does not necessarily use a <i>stable</i> sorting algorithm; when multiple
   * elements are equivalent, it is undefined which will come first.
   *
   * <p><b>Java 8 users:</b> Use {@code Streams.stream(iterable).collect(Comparators.least(k,
   * thisComparator))} instead.
   *
   * @return an immutable {@code RandomAccess} list of the {@code k} least elements in ascending
   *     order
   * @throws IllegalArgumentException if {@code k} is negative
   * @since 8.0
   */
  public <E extends T> List<E> leastOf(Iterable<E> iterable, int k) {
    if (iterable instanceof Collection) {
      Collection<E> collection = (Collection<E>) iterable;
      if (collection.size() <= 2L * k) {
        // In this case, just dumping the collection to an array and sorting is
        // faster than using the implementation for Iterator, which is
        // specialized for k much smaller than n.

        @SuppressWarnings({"unchecked", "rawtypes"}) // c only contains E's and doesn't escape
        E[] array = (E[]) collection.toArray();
        Arrays.sort(array, this);
        if (array.length > k) {
          array = Arrays.copyOf(array, k);
        }
        return Collections.unmodifiableList(Arrays.asList(array));
      }
    }
    return leastOf(iterable.iterator(), k);
  }

  /**
   * Returns the {@code k} least elements from the given iterator according to this ordering, in
   * order from least to greatest. If there are fewer than {@code k} elements present, all will be
   * included.
   *
   * <p>The implementation does not necessarily use a <i>stable</i> sorting algorithm; when multiple
   * elements are equivalent, it is undefined which will come first.
   *
   * <p><b>Java 8 users:</b> Continue to use this method for now. After the next release of Guava,
   * use {@code Streams.stream(iterator).collect(Comparators.least(k, thisComparator))} instead.
   *
   * @return an immutable {@code RandomAccess} list of the {@code k} least elements in ascending
   *     order
   * @throws IllegalArgumentException if {@code k} is negative
   * @since 14.0
   */
  public <E extends T> List<E> leastOf(Iterator<E> iterator, int k) {
    checkNotNull(iterator);
    checkNonnegative(k, "k");

    if (k == 0 || !iterator.hasNext()) {
      return Collections.emptyList();
    } else if (k >= Integer.MAX_VALUE / 2) {
      // k is really large; just do a straightforward sorted-copy-and-sublist
      ArrayList<E> list = Lists.newArrayList(iterator);
      Collections.sort(list, this);
      if (list.size() > k) {
        list.subList(k, list.size()).clear();
      }
      list.trimToSize();
      return Collections.unmodifiableList(list);
    } else {
      TopKSelector<E> selector = TopKSelector.least(k, this);
      selector.offerAll(iterator);
      return selector.topK();
    }
  }

    /**
   * Returns an <b>immutable</b> list containing {@code elements} sorted by this ordering. The input
   * is not modified.
   *
   * <p>Unlike {@link Sets#newTreeSet(Iterable)}, this method does not discard elements that are
   * duplicates according to the comparator. The sort performed is <i>stable</i>, meaning that such
   * elements will appear in the returned list in the same order they appeared in {@code elements}.
   *
   * <p><b>Performance note:</b> According to our
   * benchmarking
   * on Open JDK 7, this method is the most efficient way to make a sorted copy of a collection.
   *
   * @throws NullPointerException if any element of {@code elements} is {@code null}
   * @since 3.0
   */
  // TODO(kevinb): rerun benchmarks including new options
  public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> elements) {
    return ImmutableList.sortedCopyOf(this, elements);
  }

  /**
   * Exception thrown by a {@link Ordering#explicit(List)} or {@link Ordering#explicit(Object,
   * Object[])} comparator when comparing a value outside the set of values it can compare.
   * Extending {@link ClassCastException} may seem odd, but it is required.
   */

  static class IncomparableValueException extends ClassCastException {
    final Object value;

    IncomparableValueException(Object value) {
      super("Cannot compare value: " + value);
      this.value = value;
    }

    private static final long serialVersionUID = 0;
  }

  // Never make these public
  static final int LEFT_IS_GREATER = 1;
  static final int RIGHT_IS_GREATER = -1;
}
