/*
 * Copyright (C) 2015 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

/*
 * Portions Copyright (c) Microsoft Corporation
 */

package com.azure.cosmos.implementation.guava25.collect;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkState;


import com.azure.cosmos.implementation.guava25.math.LongMath;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Static utility methods related to {@code Stream} instances.
 *
 * @since 21.0
 */

public final class Streams {
  /**
   * Returns a sequential {@link Stream} of the contents of {@code iterable}, delegating to {@link
   * Collection#stream} if possible.
   */
  public static <T> Stream<T> stream(Iterable<T> iterable) {
    return (iterable instanceof Collection)
        ? ((Collection<T>) iterable).stream()
        : StreamSupport.stream(iterable.spliterator(), false);
  }

  /**
   * Returns a sequential {@link Stream} of the remaining contents of {@code iterator}. Do not use
   * {@code iterator} directly after passing it to this method.
   */
  public static <T> Stream<T> stream(Iterator<T> iterator) {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
  }

  /**
   * If a value is present in {@code optional}, returns a stream containing only that element,
   * otherwise returns an empty stream.
   */
  public static <T> Stream<T> stream(com.azure.cosmos.implementation.guava25.base.Optional<T> optional) {
    return optional.isPresent() ? Stream.of(optional.get()) : Stream.of();
  }

  /**
   * If a value is present in {@code optional}, returns a stream containing only that element,
   * otherwise returns an empty stream.
   *
   * <p><b>Java 9 users:</b> use {@code optional.stream()} instead.
   */
  public static <T> Stream<T> stream(java.util.Optional<T> optional) {
    return optional.isPresent() ? Stream.of(optional.get()) : Stream.of();
  }

  /**
   * If a value is present in {@code optional}, returns a stream containing only that element,
   * otherwise returns an empty stream.
   *
   * <p><b>Java 9 users:</b> use {@code optional.stream()} instead.
   */
  public static IntStream stream(OptionalInt optional) {
    return optional.isPresent() ? IntStream.of(optional.getAsInt()) : IntStream.empty();
  }

  /**
   * If a value is present in {@code optional}, returns a stream containing only that element,
   * otherwise returns an empty stream.
   *
   * <p><b>Java 9 users:</b> use {@code optional.stream()} instead.
   */
  public static LongStream stream(OptionalLong optional) {
    return optional.isPresent() ? LongStream.of(optional.getAsLong()) : LongStream.empty();
  }

  /**
   * If a value is present in {@code optional}, returns a stream containing only that element,
   * otherwise returns an empty stream.
   *
   * <p><b>Java 9 users:</b> use {@code optional.stream()} instead.
   */
  public static DoubleStream stream(OptionalDouble optional) {
    return optional.isPresent() ? DoubleStream.of(optional.getAsDouble()) : DoubleStream.empty();
  }

  /**
   * Returns a {@link Stream} containing the elements of the first stream, followed by the elements
   * of the second stream, and so on.
   *
   * <p>This is equivalent to {@code Stream.of(streams).flatMap(stream -> stream)}, but the returned
   * stream may perform better.
   *
   * @see Stream#concat(Stream, Stream)
   */
  @SuppressWarnings({"unchecked", "rawtypes", "varargs"})
  @SafeVarargs
  public static <T> Stream<T> concat(Stream<? extends T>... streams) {
    // TODO(lowasser): consider an implementation that can support SUBSIZED
    boolean isParallel = false;
    int characteristics = Spliterator.ORDERED | Spliterator.SIZED | Spliterator.NONNULL;
    long estimatedSize = 0L;
    ImmutableList.Builder<Spliterator<? extends T>> splitrsBuilder =
        new ImmutableList.Builder<>(streams.length);
    for (Stream<? extends T> stream : streams) {
      isParallel |= stream.isParallel();
      Spliterator<? extends T> splitr = stream.spliterator();
      splitrsBuilder.add(splitr);
      characteristics &= splitr.characteristics();
      estimatedSize = LongMath.saturatedAdd(estimatedSize, splitr.estimateSize());
    }
    return StreamSupport.stream(
            CollectSpliterators.flatMap(
                splitrsBuilder.build().spliterator(),
                splitr -> (Spliterator<T>) splitr,
                characteristics,
                estimatedSize),
            isParallel)
        .onClose(
            () -> {
              for (Stream<? extends T> stream : streams) {
                stream.close();
              }
            });
  }

  /**
   * Returns an {@link IntStream} containing the elements of the first stream, followed by the
   * elements of the second stream, and so on.
   *
   * <p>This is equivalent to {@code Stream.of(streams).flatMapToInt(stream -> stream)}, but the
   * returned stream may perform better.
   *
   * @see IntStream#concat(IntStream, IntStream)
   */
  public static IntStream concat(IntStream... streams) {
    // TODO(lowasser): optimize this later
    return Stream.of(streams).flatMapToInt(stream -> stream);
  }

  /**
   * Returns a {@link LongStream} containing the elements of the first stream, followed by the
   * elements of the second stream, and so on.
   *
   * <p>This is equivalent to {@code Stream.of(streams).flatMapToLong(stream -> stream)}, but the
   * returned stream may perform better.
   *
   * @see LongStream#concat(LongStream, LongStream)
   */
  public static LongStream concat(LongStream... streams) {
    // TODO(lowasser): optimize this later
    return Stream.of(streams).flatMapToLong(stream -> stream);
  }

  /**
   * Returns a {@link DoubleStream} containing the elements of the first stream, followed by the
   * elements of the second stream, and so on.
   *
   * <p>This is equivalent to {@code Stream.of(streams).flatMapToDouble(stream -> stream)}, but the
   * returned stream may perform better.
   *
   * @see DoubleStream#concat(DoubleStream, DoubleStream)
   */
  public static DoubleStream concat(DoubleStream... streams) {
    // TODO(lowasser): optimize this later
    return Stream.of(streams).flatMapToDouble(stream -> stream);
  }

  /**
   * Returns a stream in which each element is the result of passing the corresponding elementY of
   * each of {@code streamA} and {@code streamB} to {@code function}.
   *
   * <p>For example:
   *
   * <pre>{@code
   * Streams.zip(
   *   Stream.of("foo1", "foo2", "foo3"),
   *   Stream.of("bar1", "bar2"),
   *   (arg1, arg2) -> arg1 + ":" + arg2)
   * }</pre>
   *
   * <p>will return {@code Stream.of("foo1:bar1", "foo2:bar2")}.
   *
   * <p>The resulting stream will only be as long as the shorter of the two input streams; if one
   * stream is longer, its extra elements will be ignored.
   *
   * <p>Note that if you are calling {@link Stream#forEach} on the resulting stream, you might want
   * to consider using {@link #forEachPair} instead of this method.
   *
   * <p><b>Performance note:</b> The resulting stream is not <a
   * href="http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html">efficiently splittable</a>.
   * This may harm parallel performance.
   */
  public static <A, B, R> Stream<R> zip(
      Stream<A> streamA, Stream<B> streamB, BiFunction<? super A, ? super B, R> function) {
    checkNotNull(streamA);
    checkNotNull(streamB);
    checkNotNull(function);
    boolean isParallel = streamA.isParallel() || streamB.isParallel(); // same as Stream.concat
    Spliterator<A> splitrA = streamA.spliterator();
    Spliterator<B> splitrB = streamB.spliterator();
    int characteristics =
        splitrA.characteristics()
            & splitrB.characteristics()
            & (Spliterator.SIZED | Spliterator.ORDERED);
    Iterator<A> itrA = Spliterators.iterator(splitrA);
    Iterator<B> itrB = Spliterators.iterator(splitrB);
    return StreamSupport.stream(
            new AbstractSpliterator<R>(
                Math.min(splitrA.estimateSize(), splitrB.estimateSize()), characteristics) {
              @Override
              public boolean tryAdvance(Consumer<? super R> action) {
                if (itrA.hasNext() && itrB.hasNext()) {
                  action.accept(function.apply(itrA.next(), itrB.next()));
                  return true;
                }
                return false;
              }
            },
            isParallel)
        .onClose(streamA::close)
        .onClose(streamB::close);
  }

    /**
   * An analogue of {@link java.util.function.Function} also accepting an index.
   *
   * <p>This interface is only intended for use by callers of {@link #mapWithIndex(Stream,
   * FunctionWithIndex)}.
   *
   * @since 21.0
   */

  public interface FunctionWithIndex<T, R> {
    /** Applies this function to the given argument and its index within a stream. */
    R apply(T from, long index);
  }

    /**
   * An analogue of {@link java.util.function.IntFunction} also accepting an index.
   *
   * <p>This interface is only intended for use by callers of {@link #mapWithIndex(IntStream,
   * IntFunctionWithIndex)}.
   *
   * @since 21.0
   */

  public interface IntFunctionWithIndex<R> {
    /** Applies this function to the given argument and its index within a stream. */
    R apply(int from, long index);
  }

  /**
   * An analogue of {@link java.util.function.LongFunction} also accepting an index.
   *
   * <p>This interface is only intended for use by callers of {@link #mapWithIndex(LongStream,
   * LongFunctionWithIndex)}.
   *
   * @since 21.0
   */

  public interface LongFunctionWithIndex<R> {
    /** Applies this function to the given argument and its index within a stream. */
    R apply(long from, long index);
  }

  /**
   * An analogue of {@link java.util.function.DoubleFunction} also accepting an index.
   *
   * <p>This interface is only intended for use by callers of {@link #mapWithIndex(DoubleStream,
   * DoubleFunctionWithIndex)}.
   *
   * @since 21.0
   */

  public interface DoubleFunctionWithIndex<R> {
    /** Applies this function to the given argument and its index within a stream. */
    R apply(double from, long index);
  }

  /**
   * Returns the last element of the specified stream, or {@link java.util.Optional#empty} if the
   * stream is empty.
   *
   * <p>Equivalent to {@code stream.reduce((a, b) -> b)}, but may perform significantly better. This
   * method's runtime will be between O(log n) and O(n), performing better on <a
   * href="http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html">efficiently splittable</a>
   * streams.
   *
   * <p>If the stream has nondeterministic order, this has equivalent semantics to {@link
   * Stream#findAny} (which you might as well use).
   *
   * @see Stream#findFirst()
   * @throws NullPointerException if the last element of the stream is null
   */
  public static <T> java.util.Optional<T> findLast(Stream<T> stream) {
    class OptionalState {
      boolean set = false;
      T value = null;

      void set(T value) {
        this.set = true;
        this.value = value;
      }

      T get() {
        checkState(set);
        return value;
      }
    }
    OptionalState state = new OptionalState();

    Deque<Spliterator<T>> splits = new ArrayDeque<>();
    splits.addLast(stream.spliterator());

    while (!splits.isEmpty()) {
      Spliterator<T> spliterator = splits.removeLast();

      if (spliterator.getExactSizeIfKnown() == 0) {
        continue; // drop this split
      }

      // Many spliterators will have trySplits that are SUBSIZED even if they are not themselves
      // SUBSIZED.
      if (spliterator.hasCharacteristics(Spliterator.SUBSIZED)) {
        // we can drill down to exactly the smallest nonempty spliterator
        while (true) {
          Spliterator<T> prefix = spliterator.trySplit();
          if (prefix == null || prefix.getExactSizeIfKnown() == 0) {
            break;
          } else if (spliterator.getExactSizeIfKnown() == 0) {
            spliterator = prefix;
            break;
          }
        }

        // spliterator is known to be nonempty now
        spliterator.forEachRemaining(state::set);
        return java.util.Optional.of(state.get());
      }

      Spliterator<T> prefix = spliterator.trySplit();
      if (prefix == null || prefix.getExactSizeIfKnown() == 0) {
        // we can't split this any further
        spliterator.forEachRemaining(state::set);
        if (state.set) {
          return java.util.Optional.of(state.get());
        }
        // fall back to the last split
        continue;
      }
      splits.addLast(prefix);
      splits.addLast(spliterator);
    }
    return java.util.Optional.empty();
  }

  /**
   * Returns the last element of the specified stream, or {@link OptionalInt#empty} if the stream is
   * empty.
   *
   * <p>Equivalent to {@code stream.reduce((a, b) -> b)}, but may perform significantly better. This
   * method's runtime will be between O(log n) and O(n), performing better on <a
   * href="http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html">efficiently splittable</a>
   * streams.
   *
   * @see IntStream#findFirst()
   * @throws NullPointerException if the last element of the stream is null
   */
  public static OptionalInt findLast(IntStream stream) {
    // findLast(Stream) does some allocation, so we might as well box some more
    java.util.Optional<Integer> boxedLast = findLast(stream.boxed());
    return boxedLast.isPresent() ? OptionalInt.of(boxedLast.get()) : OptionalInt.empty();
  }

  /**
   * Returns the last element of the specified stream, or {@link OptionalLong#empty} if the stream
   * is empty.
   *
   * <p>Equivalent to {@code stream.reduce((a, b) -> b)}, but may perform significantly better. This
   * method's runtime will be between O(log n) and O(n), performing better on <a
   * href="http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html">efficiently splittable</a>
   * streams.
   *
   * @see LongStream#findFirst()
   * @throws NullPointerException if the last element of the stream is null
   */
  public static OptionalLong findLast(LongStream stream) {
    // findLast(Stream) does some allocation, so we might as well box some more
    java.util.Optional<Long> boxedLast = findLast(stream.boxed());
    return boxedLast.isPresent() ? OptionalLong.of(boxedLast.get()) : OptionalLong.empty();
  }

  /**
   * Returns the last element of the specified stream, or {@link OptionalDouble#empty} if the stream
   * is empty.
   *
   * <p>Equivalent to {@code stream.reduce((a, b) -> b)}, but may perform significantly better. This
   * method's runtime will be between O(log n) and O(n), performing better on <a
   * href="http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html">efficiently splittable</a>
   * streams.
   *
   * @see DoubleStream#findFirst()
   * @throws NullPointerException if the last element of the stream is null
   */
  public static OptionalDouble findLast(DoubleStream stream) {
    // findLast(Stream) does some allocation, so we might as well box some more
    java.util.Optional<Double> boxedLast = findLast(stream.boxed());
    return boxedLast.isPresent() ? OptionalDouble.of(boxedLast.get()) : OptionalDouble.empty();
  }

  private Streams() {}
}
