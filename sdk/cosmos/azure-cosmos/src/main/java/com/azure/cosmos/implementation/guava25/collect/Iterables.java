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

import com.azure.cosmos.implementation.guava25.base.Function;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.stream.Stream;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.guava25.collect.CollectPreconditions.checkRemove;


/**
 * An assortment of mainly legacy static utility methods that operate on or return objects of type
 * {@code Iterable}. Except as noted, each method has a corresponding {@link Iterator}-based method
 * in the {@link Iterators} class.
 *
 * <p><b>Java 8 users:</b> several common uses for this class are now more comprehensively addressed
 * by the new {@link java.util.stream.Stream} library. Read the method documentation below for
 * comparisons. This class is not being deprecated, but we gently encourage you to migrate to
 * streams.
 *
 * <p><i>Performance notes:</i> Unless otherwise noted, all of the iterables produced in this class
 * are <i>lazy</i>, which means that their iterators only advance the backing iteration when
 * absolutely necessary.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#iterables"> {@code
 * Iterables}</a>.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 * @since 2.0
 */
public final class Iterables {
  private Iterables() {}

    /** Returns the number of elements in {@code iterable}. */
  public static int size(Iterable<?> iterable) {
    return (iterable instanceof Collection)
        ? ((Collection<?>) iterable).size()
        : Iterators.size(iterable.iterator());
  }

  /**
   * Returns {@code true} if {@code iterable} contains any element {@code o} for which {@code
   * Objects.equals(o, element)} would return {@code true}. Otherwise returns {@code false}, even in
   * cases where {@link Collection#contains} might throw {@link NullPointerException} or {@link
   * ClassCastException}.
   */
  public static boolean contains(Iterable<?> iterable, Object element) {
    if (iterable instanceof Collection) {
      Collection<?> collection = (Collection<?>) iterable;
      return Collections2.safeContains(collection, element);
    }
    return Iterators.contains(iterable.iterator(), element);
  }

    /**
   * Returns a string representation of {@code iterable}, with the format {@code [e1, e2, ..., en]}
   * (that is, identical to {@link java.util.Arrays Arrays}{@code
   * .toString(Iterables.toArray(iterable))}). Note that for <i>most</i> implementations of {@link
   * Collection}, {@code collection.toString()} also gives the same result, but that behavior is not
   * generally guaranteed.
   */
  public static String toString(Iterable<?> iterable) {
    return Iterators.toString(iterable.iterator());
  }

  /**
   * Returns the single element contained in {@code iterable}.
   *
   * <p><b>Java 8 users:</b> the {@code Stream} equivalent to this method is {@code
   * stream.collect(MoreCollectors.onlyElement())}.
   *
   * @throws NoSuchElementException if the iterable is empty
   * @throws IllegalArgumentException if the iterable contains multiple elements
   */
  public static <T> T getOnlyElement(Iterable<T> iterable) {
    return Iterators.getOnlyElement(iterable.iterator());
  }

    /**
   * Copies an iterable's elements into an array.
   *
   * @param iterable the iterable to copy
   * @param type the type of the elements
   * @return a newly-allocated array into which all the elements of the iterable have been copied
   */
  public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type) {
    return toArray(iterable, ObjectArrays.newArray(type, 0));
  }

  static <T> T[] toArray(Iterable<? extends T> iterable, T[] array) {
    Collection<? extends T> collection = castOrCopyToCollection(iterable);
    return collection.toArray(array);
  }

  /**
   * Copies an iterable's elements into an array.
   *
   * @param iterable the iterable to copy
   * @return a newly-allocated array into which all the elements of the iterable have been copied
   */
  static Object[] toArray(Iterable<?> iterable) {
    return castOrCopyToCollection(iterable).toArray();
  }

  /**
   * Converts an iterable into a collection. If the iterable is already a collection, it is
   * returned. Otherwise, an {@link java.util.ArrayList} is created with the contents of the
   * iterable in the same iteration order.
   */
  private static <E> Collection<E> castOrCopyToCollection(Iterable<E> iterable) {
    return (iterable instanceof Collection)
        ? (Collection<E>) iterable
        : Lists.newArrayList(iterable.iterator());
  }

  /**
   * Adds all elements in {@code iterable} to {@code collection}.
   *
   * @return {@code true} if {@code collection} was modified as a result of this operation.
   */
  public static <T> boolean addAll(Collection<T> addTo, Iterable<? extends T> elementsToAdd) {
    if (elementsToAdd instanceof Collection) {
      Collection<? extends T> c = Collections2.cast(elementsToAdd);
      return addTo.addAll(c);
    }
    return Iterators.addAll(addTo, checkNotNull(elementsToAdd).iterator());
  }

  /**
   * Returns an iterable whose iterators cycle indefinitely over the elements of {@code iterable}.
   *
   * <p>That iterator supports {@code remove()} if {@code iterable.iterator()} does. After {@code
   * remove()} is called, subsequent cycles omit the removed element, which is no longer in {@code
   * iterable}. The iterator's {@code hasNext()} method returns {@code true} until {@code iterable}
   * is empty.
   *
   * <p><b>Warning:</b> Typical uses of the resulting iterator may produce an infinite loop. You
   * should use an explicit {@code break} or be certain that you will eventually remove all the
   * elements.
   *
   * <p>To cycle over the iterable {@code n} times, use the following: {@code
   * Iterables.concat(Collections.nCopies(n, iterable))}
   *
   * <p><b>Java 8 users:</b> The {@code Stream} equivalent of this method is {@code
   * Stream.generate(() -> iterable).flatMap(Streams::stream)}.
   */
  public static <T> Iterable<T> cycle(final Iterable<T> iterable) {
    checkNotNull(iterable);
    return new FluentIterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return Iterators.cycle(iterable);
      }

      @Override
      public Spliterator<T> spliterator() {
        return Stream.generate(() -> iterable).flatMap(Streams::stream).spliterator();
      }

      @Override
      public String toString() {
        return iterable.toString() + " (cycled)";
      }
    };
  }

  /**
   * Combines two iterables into a single iterable. The returned iterable has an iterator that
   * traverses the elements in {@code a}, followed by the elements in {@code b}. The source
   * iterators are not polled until necessary.
   *
   * <p>The returned iterable's iterator supports {@code remove()} when the corresponding input
   * iterator supports it.
   *
   * <p><b>Java 8 users:</b> The {@code Stream} equivalent of this method is {@code Stream.concat(a,
   * b)}.
   */
  public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b) {
    return FluentIterable.concat(a, b);
  }

  /**
   * Combines four iterables into a single iterable. The returned iterable has an iterator that
   * traverses the elements in {@code a}, followed by the elements in {@code b}, followed by the
   * elements in {@code c}, followed by the elements in {@code d}. The source iterators are not
   * polled until necessary.
   *
   * <p>The returned iterable's iterator supports {@code remove()} when the corresponding input
   * iterator supports it.
   *
   * <p><b>Java 8 users:</b> The {@code Stream} equivalent of this method is {@code
   * Streams.concat(a, b, c, d)}.
   */
  public static <T> Iterable<T> concat(
      Iterable<? extends T> a,
      Iterable<? extends T> b,
      Iterable<? extends T> c,
      Iterable<? extends T> d) {
    return FluentIterable.concat(a, b, c, d);
  }

  /**
   * Returns the element at the specified position in an iterable.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code stream.skip(position).findFirst().get()} (throws
   * {@code NoSuchElementException} if out of bounds)
   *
   * @param position position of the element to return
   * @return the element at the specified position in {@code iterable}
   * @throws IndexOutOfBoundsException if {@code position} is negative or greater than or equal to
   *     the size of {@code iterable}
   */
  public static <T> T get(Iterable<T> iterable, int position) {
    checkNotNull(iterable);
    return (iterable instanceof List)
        ? ((List<T>) iterable).get(position)
        : Iterators.get(iterable.iterator(), position);
  }

  /**
   * Returns the first element in {@code iterable} or {@code defaultValue} if the iterable is empty.
   * The {@link Iterators} analog to this method is {@link Iterators#getNext}.
   *
   * <p>If no default value is desired (and the caller instead wants a {@link
   * NoSuchElementException} to be thrown), it is recommended that {@code
   * iterable.iterator().next()} is used instead.
   *
   * <p>To get the only element in a single-element {@code Iterable}, consider using {@link
   * #getOnlyElement(Iterable)} or {@link #getOnlyElement(Iterable, Object)} instead.
   *
   * <p><b>{@code Stream} equivalent:</b> {@code stream.findFirst().orElse(defaultValue)}
   *
   * @param defaultValue the default value to return if the iterable is empty
   * @return the first element of {@code iterable} or the default value
   * @since 7.0
   */

  public static <T> T getFirst(Iterable<? extends T> iterable, T defaultValue) {
    return Iterators.getNext(iterable.iterator(), defaultValue);
  }

  /**
   * Returns the last element of {@code iterable}. If {@code iterable} is a {@link List} with {@link
   * RandomAccess} support, then this operation is guaranteed to be {@code O(1)}.
   *
   * <p><b>{@code Stream} equivalent:</b> {@link Streams#findLast Streams.findLast(stream).get()}
   *
   * @return the last element of {@code iterable}
   * @throws NoSuchElementException if the iterable is empty
   */
  public static <T> T getLast(Iterable<T> iterable) {
    // TODO(kevinb): Support a concurrently modified collection?
    if (iterable instanceof List) {
      List<T> list = (List<T>) iterable;
      if (list.isEmpty()) {
        throw new NoSuchElementException();
      }
      return getLastInNonemptyList(list);
    }

    return Iterators.getLast(iterable.iterator());
  }

    private static <T> T getLastInNonemptyList(List<T> list) {
    return list.get(list.size() - 1);
  }

  /**
   * Returns a view of {@code iterable} that skips its first {@code numberToSkip} elements. If
   * {@code iterable} contains fewer than {@code numberToSkip} elements, the returned iterable skips
   * all of its elements.
   *
   * <p>Modifications to the underlying {@link Iterable} before a call to {@code iterator()} are
   * reflected in the returned iterator. That is, the iterator skips the first {@code numberToSkip}
   * elements that exist when the {@code Iterator} is created, not when {@code skip()} is called.
   *
   * <p>The returned iterable's iterator supports {@code remove()} if the iterator of the underlying
   * iterable supports it. Note that it is <i>not</i> possible to delete the last skipped element by
   * immediately calling {@code remove()} on that iterator, as the {@code Iterator} contract states
   * that a call to {@code remove()} before a call to {@code next()} will throw an {@link
   * IllegalStateException}.
   *
   * <p><b>{@code Stream} equivalent:</b> {@link Stream#skip}
   *
   * @since 3.0
   */
  public static <T> Iterable<T> skip(final Iterable<T> iterable, final int numberToSkip) {
    checkNotNull(iterable);
    checkArgument(numberToSkip >= 0, "number to skip cannot be negative");

    return new FluentIterable<T>() {
      @Override
      public Iterator<T> iterator() {
        if (iterable instanceof List) {
          final List<T> list = (List<T>) iterable;
          int toSkip = Math.min(list.size(), numberToSkip);
          return list.subList(toSkip, list.size()).iterator();
        }
        final Iterator<T> iterator = iterable.iterator();

        Iterators.advance(iterator, numberToSkip);

        /*
         * We can't just return the iterator because an immediate call to its
         * remove() method would remove one of the skipped elements instead of
         * throwing an IllegalStateException.
         */
        return new Iterator<T>() {
          boolean atStart = true;

          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public T next() {
            T result = iterator.next();
            atStart = false; // not called if next() fails
            return result;
          }

          @Override
          public void remove() {
            checkRemove(!atStart);
            iterator.remove();
          }
        };
      }

      @Override
      public Spliterator<T> spliterator() {
        if (iterable instanceof List) {
          final List<T> list = (List<T>) iterable;
          int toSkip = Math.min(list.size(), numberToSkip);
          return list.subList(toSkip, list.size()).spliterator();
        } else {
          return Streams.stream(iterable).skip(numberToSkip).spliterator();
        }
      }
    };
  }

  /**
   * Returns a view of {@code iterable} containing its first {@code limitSize} elements. If {@code
   * iterable} contains fewer than {@code limitSize} elements, the returned view contains all of its
   * elements. The returned iterable's iterator supports {@code remove()} if {@code iterable}'s
   * iterator does.
   *
   * <p><b>{@code Stream} equivalent:</b> {@link Stream#limit}
   *
   * @param iterable the iterable to limit
   * @param limitSize the maximum number of elements in the returned iterable
   * @throws IllegalArgumentException if {@code limitSize} is negative
   * @since 3.0
   */
  public static <T> Iterable<T> limit(final Iterable<T> iterable, final int limitSize) {
    checkNotNull(iterable);
    checkArgument(limitSize >= 0, "limit is negative");
    return new FluentIterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return Iterators.limit(iterable.iterator(), limitSize);
      }

      @Override
      public Spliterator<T> spliterator() {
        return Streams.stream(iterable).limit(limitSize).spliterator();
      }
    };
  }

    // Methods only in Iterables, not in Iterators

  /**
   * Determines if the given iterable contains no elements.
   *
   * <p>There is no precise {@link Iterator} equivalent to this method, since one can only ask an
   * iterator whether it has any elements <i>remaining</i> (which one does using {@link
   * Iterator#hasNext}).
   *
   * <p><b>{@code Stream} equivalent:</b> {@code !stream.findAny().isPresent()}
   *
   * @return {@code true} if the iterable contains no elements
   */
  public static boolean isEmpty(Iterable<?> iterable) {
    if (iterable instanceof Collection) {
      return ((Collection<?>) iterable).isEmpty();
    }
    return !iterable.iterator().hasNext();
  }

    // TODO(user): Is this the best place for this? Move to fluent functions?
  // Useful as a public method?
  static <T> Function<Iterable<? extends T>, Iterator<? extends T>> toIterator() {
    return new Function<Iterable<? extends T>, Iterator<? extends T>>() {
      @Override
      public Iterator<? extends T> apply(Iterable<? extends T> iterable) {
        return iterable.iterator();
      }
    };
  }
}
