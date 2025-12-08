/*
 * Copyright (C) 2008 The Guava Authors
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

import com.azure.cosmos.implementation.guava25.primitives.Ints;

import java.util.Collection;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.guava25.collect.CollectPreconditions.checkNonnegative;


/**
 * Provides static methods for working with {@code Collection} instances.
 *
 * <p><b>Java 8 users:</b> several common uses for this class are now more comprehensively addressed
 * by the new {@link java.util.stream.Stream} library. Read the method documentation below for
 * comparisons. These methods are not being deprecated, but we gently encourage you to migrate to
 * streams.
 *
 * @author Chris Povirk
 * @author Mike Bostock
 * @author Jared Levy
 * @since 2.0
 */
public final class Collections2 {
  private Collections2() {}

  /**
   * Delegates to {@link Collection#contains}. Returns {@code false} if the {@code contains} method
   * throws a {@code ClassCastException} or {@code NullPointerException}.
   */
  static boolean safeContains(Collection<?> collection, Object object) {
    checkNotNull(collection);
    try {
      return collection.contains(object);
    } catch (ClassCastException | NullPointerException e) {
      return false;
    }
  }

  /**
   * Delegates to {@link Collection#remove}. Returns {@code false} if the {@code remove} method
   * throws a {@code ClassCastException} or {@code NullPointerException}.
   */
  static boolean safeRemove(Collection<?> collection, Object object) {
    checkNotNull(collection);
    try {
      return collection.remove(object);
    } catch (ClassCastException | NullPointerException e) {
      return false;
    }
  }

    /**
   * Returns {@code true} if the collection {@code self} contains all of the elements in the
   * collection {@code c}.
   *
   * <p>This method iterates over the specified collection {@code c}, checking each element returned
   * by the iterator in turn to see if it is contained in the specified collection {@code self}. If
   * all elements are so contained, {@code true} is returned, otherwise {@code false}.
   *
   * @param self a collection which might contain all elements in {@code c}
   * @param c a collection whose elements might be contained by {@code self}
   */
  static boolean containsAllImpl(Collection<?> self, Collection<?> c) {
    for (Object o : c) {
      if (!self.contains(o)) {
        return false;
      }
    }
    return true;
  }

  /** An implementation of {@link Collection#toString()}. */
  static String toStringImpl(final Collection<?> collection) {
    StringBuilder sb = newStringBuilderForCollection(collection.size()).append('[');
    boolean first = true;
    for (Object o : collection) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      if (o == collection) {
        sb.append("(this Collection)");
      } else {
        sb.append(o);
      }
    }
    return sb.append(']').toString();
  }

  /** Returns best-effort-sized StringBuilder based on the given collection size. */
  static StringBuilder newStringBuilderForCollection(int size) {
    checkNonnegative(size, "size");
    return new StringBuilder((int) Math.min(size * 8L, Ints.MAX_POWER_OF_TWO));
  }

  /** Used to avoid http://bugs.sun.com/view_bug.do?bug_id=6558557 */
  static <T> Collection<T> cast(Iterable<T> iterable) {
    return (Collection<T>) iterable;
  }

}
