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

package com.azure.cosmos.implementation.guava25.collect;

import com.azure.cosmos.implementation.guava25.base.Function;
import com.azure.cosmos.implementation.guava25.base.Optional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;


/**
 * A discouraged (but not deprecated) precursor to Java's superior {@link Stream} library.
 *
 * <p>The following types of methods are provided:
 *
 * <ul>
 *   <li>chaining methods which return a new {@code FluentIterable} based in some way on the
 *       contents of the current one
 *   <li>element extraction methods which facilitate the retrieval of certain elements
 *   <li>query methods which answer questions about the {@code FluentIterable}'s contents
 *   <li>conversion methods which copy the {@code FluentIterable}'s contents into a new collection
 *       or array (for example {@link #toList})
 * </ul>
 *
 * <p>Several lesser-used features are currently available only as static methods on the {@link
 * Iterables} class.
 *
 * <p><a name="streams"></a>
 *
 * <h3>Comparison to streams</h3>
 *
 * <p>{@link Stream} is similar to this class, but generally more powerful, and certainly more
 * standard. Key differences include:
 *
 * <ul>
 *   <li>A stream is <i>single-use</i>; it becomes invalid as soon as any "terminal operation" such
 *       as {@code findFirst()} or {@code iterator()} is invoked. (Even though {@code Stream}
 *       contains all the right method <i>signatures</i> to implement {@link Iterable}, it does not
 *       actually do so, to avoid implying repeat-iterability.) {@code FluentIterable}, on the other
 *       hand, is multiple-use, and does implement {@link Iterable}.
 *   <li>Streams offer many features not found here, including {@code min/max}, {@code distinct},
 *       {@code reduce}, {@code sorted}, the very powerful {@code collect}, and built-in support for
 *       parallelizing stream operations.
 *   <li>{@code FluentIterable} contains several features not available on {@code Stream}, which are
 *       noted in the method descriptions below.
 *   <li>Streams include primitive-specialized variants such as {@code IntStream}, the use of which
 *       is strongly recommended.
 *   <li>Streams are standard Java, not requiring a third-party dependency.
 * </ul>
 *
 * <h3>Example</h3>
 *
 * <p>Here is an example that accepts a list from a database call, filters it based on a predicate,
 * transforms it by invoking {@code toString()} on each element, and returns the first 10 elements
 * as a {@code List}:
 *
 * <pre>{@code
 * ImmutableList<String> results =
 *     FluentIterable.from(database.getClientList())
 *         .filter(Client::isActiveInLastMonth)
 *         .transform(Object::toString)
 *         .limit(10)
 *         .toList();
 * }</pre>
 *
 * The approximate stream equivalent is:
 *
 * <pre>{@code
 * List<String> results =
 *     database.getClientList()
 *         .stream()
 *         .filter(Client::isActiveInLastMonth)
 *         .map(Object::toString)
 *         .limit(10)
 *         .collect(Collectors.toList());
 * }</pre>
 *
 * @author Marcin Mikosik
 * @since 12.0
 */
public abstract class FluentIterable<E> implements Iterable<E> {
  // We store 'iterable' and use it instead of 'this' to allow Iterables to perform instanceof
  // checks on the _original_ iterable when FluentIterable.from is used.
  // To avoid a self retain cycle under j2objc, we store Optional.absent() instead of
  // Optional.of(this). To access the iterator delegate, call #getDelegate(), which converts to
  // absent() back to 'this'.
  private final Optional<Iterable<E>> iterableDelegate;

  /** Constructor for use by subclasses. */
  protected FluentIterable() {
    this.iterableDelegate = Optional.absent();
  }

  FluentIterable(Iterable<E> iterable) {
    checkNotNull(iterable);
    this.iterableDelegate = Optional.fromNullable(this != iterable ? iterable : null);
  }

  private Iterable<E> getDelegate() {
    return iterableDelegate.or(this);
  }

  /**
   * Returns a fluent iterable that wraps {@code iterable}, or {@code iterable} itself if it is
   * already a {@code FluentIterable}.
   *
   * <p><b>{@code Stream} equivalent:</b> {@link Collection#stream} if {@code iterable} is a {@link
   * Collection}; {@link Streams#stream(Iterable)} otherwise.
   */
  public static <E> FluentIterable<E> from(final Iterable<E> iterable) {
    return (iterable instanceof FluentIterable)
        ? (FluentIterable<E>) iterable
        : new FluentIterable<E>(iterable) {
          @Override
          public Iterator<E> iterator() {
            return iterable.iterator();
          }
        };
  }

  /**
   * Returns a fluent iterable that combines two iterables. The returned iterable has an iterator
   * that traverses the elements in {@code a}, followed by the elements in {@code b}. The source
   * iterators are not polled until necessary.
   *
   * <p>The returned iterable's iterator supports {@code remove()} when the corresponding input
   * iterator supports it.
   *
   * <p><b>{@code Stream} equivalent:</b> {@link Stream#concat}.
   *
   * @since 20.0
   */

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> FluentIterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b) {
    return concatNoDefensiveCopy(a, b);
  }

  /**
   * Returns a fluent iterable that combines three iterables. The returned iterable has an iterator
   * that traverses the elements in {@code a}, followed by the elements in {@code b}, followed by
   * the elements in {@code c}. The source iterators are not polled until necessary.
   *
   * <p>The returned iterable's iterator supports {@code remove()} when the corresponding input
   * iterator supports it.
   *
   * <p><b>{@code Stream} equivalent:</b> use nested calls to {@link Stream#concat}, or see the
   * advice in {@link #concat(Iterable...)}.
   *
   * @since 20.0
   */

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> FluentIterable<T> concat(
      Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c) {
    return concatNoDefensiveCopy(a, b, c);
  }

  /**
   * Returns a fluent iterable that combines four iterables. The returned iterable has an iterator
   * that traverses the elements in {@code a}, followed by the elements in {@code b}, followed by
   * the elements in {@code c}, followed by the elements in {@code d}. The source iterators are not
   * polled until necessary.
   *
   * <p>The returned iterable's iterator supports {@code remove()} when the corresponding input
   * iterator supports it.
   *
   * <p><b>{@code Stream} equivalent:</b> use nested calls to {@link Stream#concat}, or see the
   * advice in {@link #concat(Iterable...)}.
   *
   * @since 20.0
   */

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> FluentIterable<T> concat(
      Iterable<? extends T> a,
      Iterable<? extends T> b,
      Iterable<? extends T> c,
      Iterable<? extends T> d) {
    return concatNoDefensiveCopy(a, b, c, d);
  }

  /**
   * Returns a fluent iterable that combines several iterables. The returned iterable has an
   * iterator that traverses the elements of each iterable in {@code inputs}. The input iterators
   * are not polled until necessary.
   *
   * <p>The returned iterable's iterator supports {@code remove()} when the corresponding input
   * iterator supports it.
   *
   * <p><b>{@code Stream} equivalent:</b> to concatenate an arbitrary number of streams, use {@code
   * Stream.of(stream1, stream2, ...).flatMap(s -> s)}. If the sources are iterables, use {@code
   * Stream.of(iter1, iter2, ...).flatMap(Streams::stream)}.
   *
   * @throws NullPointerException if any of the provided iterables is {@code null}
   * @since 20.0
   */

  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T> FluentIterable<T> concat(Iterable<? extends T>... inputs) {
    return concatNoDefensiveCopy(Arrays.copyOf(inputs, inputs.length));
  }

  /**
   * Returns a fluent iterable that combines several iterables. The returned iterable has an
   * iterator that traverses the elements of each iterable in {@code inputs}. The input iterators
   * are not polled until necessary.
   *
   * <p>The returned iterable's iterator supports {@code remove()} when the corresponding input
   * iterator supports it. The methods of the returned iterable may throw {@code
   * NullPointerException} if any of the input iterators is {@code null}.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code streamOfStreams.flatMap(s -> s)} or {@code
   * streamOfIterables.flatMap(Streams::stream)}. (See {@link Streams#stream}.)
   *
   * @since 20.0
   */

  public static <T> FluentIterable<T> concat(
      final Iterable<? extends Iterable<? extends T>> inputs) {
    checkNotNull(inputs);
    return new FluentIterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return Iterators.concat(Iterators.transform(inputs.iterator(), Iterables.<T>toIterator()));
      }
    };
  }

  /** Concatenates a varargs array of iterables without making a defensive copy of the array. */
  @SafeVarargs
  @SuppressWarnings("varargs")
  private static <T> FluentIterable<T> concatNoDefensiveCopy(
      final Iterable<? extends T>... inputs) {
    for (Iterable<? extends T> input : inputs) {
      checkNotNull(input);
    }
    return new FluentIterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return Iterators.concat(
            /* lazily generate the iterators on each input only as needed */
            new AbstractIndexedListIterator<Iterator<? extends T>>(inputs.length) {
              @Override
              public Iterator<? extends T> get(int i) {
                return inputs[i].iterator();
              }
            });
      }
    };
  }

  /**
   * Returns a string representation of this fluent iterable, with the format {@code [e1, e2, ...,
   * en]}.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code stream.collect(Collectors.joining(", ", "[", "]"))}
   * or (less efficiently) {@code stream.collect(Collectors.toList()).toString()}.
   */
  @Override
  public String toString() {
    return Iterables.toString(getDelegate());
  }

  /**
   * Returns the number of elements in this fluent iterable.
   *
   * <p><b>{@code Stream} equivalent:</b> {@link Stream#count}.
   */
  public final int size() {
    return Iterables.size(getDelegate());
  }

  /**
   * Returns {@code true} if this fluent iterable contains any object for which {@code
   * equals(target)} is true.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code stream.anyMatch(Predicate.isEqual(target))}.
   */
  public final boolean contains(Object target) {
    return Iterables.contains(getDelegate(), target);
  }

  /**
   * Returns a fluent iterable whose {@code Iterator} cycles indefinitely over the elements of this
   * fluent iterable.
   *
   * <p>That iterator supports {@code remove()} if {@code iterable.iterator()} does. After {@code
   * remove()} is called, subsequent cycles omit the removed element, which is no longer in this
   * fluent iterable. The iterator's {@code hasNext()} method returns {@code true} until this fluent
   * iterable is empty.
   *
   * <p><b>Warning:</b> Typical uses of the resulting iterator may produce an infinite loop. You
   * should use an explicit {@code break} or be certain that you will eventually remove all the
   * elements.
   *
   * <p><b>{@code Stream} equivalent:</b> if the source iterable has only a single element {@code
   * e}, use {@code Stream.generate(() -> e)}. Otherwise, collect your stream into a collection and
   * use {@code Stream.generate(() -> collection).flatMap(Collection::stream)}.
   */
  public final FluentIterable<E> cycle() {
    return from(Iterables.cycle(getDelegate()));
  }

  /**
   * Returns a fluent iterable whose iterators traverse first the elements of this fluent iterable,
   * followed by those of {@code other}. The iterators are not polled until necessary.
   *
   * <p>The returned iterable's {@code Iterator} supports {@code remove()} when the corresponding
   * {@code Iterator} supports it.
   *
   * <p><b>{@code Stream} equivalent:</b> {@link Stream#concat}.
   *
   * @since 18.0
   */

  public final FluentIterable<E> append(Iterable<? extends E> other) {
    return FluentIterable.concat(getDelegate(), other);
  }

  /**
   * Returns a fluent iterable whose iterators traverse first the elements of this fluent iterable,
   * followed by {@code elements}.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code Stream.concat(thisStream, Stream.of(elements))}.
   *
   * @since 18.0
   */

  @SafeVarargs
  @SuppressWarnings("varargs")
  public final FluentIterable<E> append(E... elements) {
    return FluentIterable.concat(getDelegate(), Arrays.asList(elements));
  }

  /**
   * Returns a view of this fluent iterable that skips its first {@code numberToSkip} elements. If
   * this fluent iterable contains fewer than {@code numberToSkip} elements, the returned fluent
   * iterable skips all of its elements.
   *
   * <p>Modifications to this fluent iterable before a call to {@code iterator()} are reflected in
   * the returned fluent iterable. That is, the its iterator skips the first {@code numberToSkip}
   * elements that exist when the iterator is created, not when {@code skip()} is called.
   *
   * <p>The returned fluent iterable's iterator supports {@code remove()} if the {@code Iterator} of
   * this fluent iterable supports it. Note that it is <i>not</i> possible to delete the last
   * skipped element by immediately calling {@code remove()} on the returned fluent iterable's
   * iterator, as the {@code Iterator} contract states that a call to {@code * remove()} before a
   * call to {@code next()} will throw an {@link IllegalStateException}.
   *
   * <p><b>{@code Stream} equivalent:</b> {@link Stream#skip} (same).
   */
  public final FluentIterable<E> skip(int numberToSkip) {
    return from(Iterables.skip(getDelegate(), numberToSkip));
  }

  /**
   * Creates a fluent iterable with the first {@code size} elements of this fluent iterable. If this
   * fluent iterable does not contain that many elements, the returned fluent iterable will have the
   * same behavior as this fluent iterable. The returned fluent iterable's iterator supports {@code
   * remove()} if this fluent iterable's iterator does.
   *
   * <p><b>{@code Stream} equivalent:</b> {@link Stream#limit} (same).
   *
   * @param maxSize the maximum number of elements in the returned fluent iterable
   * @throws IllegalArgumentException if {@code size} is negative
   */
  public final FluentIterable<E> limit(int maxSize) {
    return from(Iterables.limit(getDelegate(), maxSize));
  }

  /**
   * Determines whether this fluent iterable is empty.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code !stream.findAny().isPresent()}.
   */
  public final boolean isEmpty() {
    return !getDelegate().iterator().hasNext();
  }

  /**
   * Returns an {@code ImmutableList} containing all of the elements from this fluent iterable in
   * proper sequence.
   *
   * @throws NullPointerException if any element is {@code null}
   * @since 14.0 (since 12.0 as {@code toImmutableList()}).
   */
  public final ImmutableList<E> toList() {
    return ImmutableList.copyOf(getDelegate());
  }

    /**
   * Returns an {@code ImmutableSet} containing all of the elements from this fluent iterable with
   * duplicates removed.
   *
   * @throws NullPointerException if any element is {@code null}
   * @since 14.0 (since 12.0 as {@code toImmutableSet()}).
   */
  public final ImmutableSet<E> toSet() {
    return ImmutableSet.copyOf(getDelegate());
  }

    /**
   * Returns an immutable map whose keys are the distinct elements of this {@code FluentIterable}
   * and whose value for each key was computed by {@code valueFunction}. The map's iteration order
   * is the order of the first appearance of each key in this iterable.
   *
   * <p>When there are multiple instances of a key in this iterable, it is unspecified whether
   * {@code valueFunction} will be applied to more than one instance of that key and, if it is,
   * which result will be mapped to that key in the returned map.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code stream.collect(ImmutableMap.toImmutableMap(k -> k,
   * valueFunction))}.
   *
   * @throws NullPointerException if any element of this iterable is {@code null}, or if {@code
   *     valueFunction} produces {@code null} for any key
   * @since 14.0
   */
  public final <V> ImmutableMap<E, V> toMap(Function<? super E, V> valueFunction) {
    return Maps.toMap(getDelegate(), valueFunction);
  }

  /**
   * Creates an index {@code ImmutableListMultimap} that contains the results of applying a
   * specified function to each item in this {@code FluentIterable} of values. Each element of this
   * iterable will be stored as a value in the resulting multimap, yielding a multimap with the same
   * size as this iterable. The key used to store that value in the multimap will be the result of
   * calling the function on that value. The resulting multimap is created as an immutable snapshot.
   * In the returned multimap, keys appear in the order they are first encountered, and the values
   * corresponding to each key appear in the same order as they are encountered.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code stream.collect(Collectors.groupingBy(keyFunction))}
   * behaves similarly, but returns a mutable {@code Map<K, List<E>>} instead, and may not preserve
   * the order of entries).
   *
   * @param keyFunction the function used to produce the key for each value
   * @throws NullPointerException if any element of this iterable is {@code null}, or if {@code
   *     keyFunction} produces {@code null} for any key
   * @since 14.0
   */
  public final <K> ImmutableListMultimap<K, E> index(Function<? super E, K> keyFunction) {
    return Multimaps.index(getDelegate(), keyFunction);
  }

    /**
   * Returns an array containing all of the elements from this fluent iterable in iteration order.
   *
   * <p><b>{@code Stream} equivalent:</b> if an object array is acceptable, use {@code
   * stream.toArray()}; if {@code type} is a class literal such as {@code MyType.class}, use {@code
   * stream.toArray(MyType[]::new)}. Otherwise use {@code stream.toArray( len -> (E[])
   * Array.newInstance(type, len))}.
   *
   * @param type the type of the elements
   * @return a newly-allocated array into which all the elements of this fluent iterable have been
   *     copied
   */
  public final E[] toArray(Class<E> type) {
    return Iterables.toArray(getDelegate(), type);
  }

  /**
   * Returns the element at the specified position in this fluent iterable.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code stream.skip(position).findFirst().get()} (but note
   * that this throws different exception types, and throws an exception if {@code null} would be
   * returned).
   *
   * @param position position of the element to return
   * @return the element at the specified position in this fluent iterable
   * @throws IndexOutOfBoundsException if {@code position} is negative or greater than or equal to
   *     the size of this fluent iterable
   */
  // TODO(kevinb): add ?
  public final E get(int position) {
    return Iterables.get(getDelegate(), position);
  }

  /**
   * Returns a stream of this fluent iterable's contents (similar to calling {@link
   * Collection#stream} on a collection).
   *
   * <p><b>Note:</b> the earlier in the chain you can switch to {@code Stream} usage (ideally not
   * going through {@code FluentIterable} at all), the more performant and idiomatic your code will
   * be. This method is a transitional aid, to be used only when really necessary.
   *
   * @since 21.0
   */
  public final Stream<E> stream() {
    return Streams.stream(getDelegate());
  }

}
