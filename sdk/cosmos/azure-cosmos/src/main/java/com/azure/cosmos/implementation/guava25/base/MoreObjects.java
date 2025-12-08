/*
 * Copyright (C) 2014 The Guava Authors
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
 * Helper functions that operate on any {@code Object}, and are not already provided in {@link
 * java.util.Objects}.
 *
 * <p>See the Guava User Guide on <a
 * href="https://github.com/google/guava/wiki/CommonObjectUtilitiesExplained">writing {@code Object}
 * methods with {@code MoreObjects}</a>.
 *
 * @author Laurence Gonsalves
 * @since 18.0 (since 2.0 as {@code Objects})
 */
public final class MoreObjects {
  /**
   * Returns the first of two given parameters that is not {@code null}, if either is, or otherwise
   * throws a {@link NullPointerException}.
   *
   * <p>To find the first non-null element in an iterable, use {@code Iterables.find(iterable,
   * Predicates.notNull())}. For varargs, use {@code Iterables.find(Arrays.asList(a, b, c, ...),
   * Predicates.notNull())}, static importing as necessary.
   *
   * <p><b>Note:</b> if {@code first} is represented as an {@link Optional}, this can be
   * accomplished with {@link Optional#or(Object) first.or(second)}. That approach also allows for
   * lazy evaluation of the fallback instance, using {@link Optional#or(Supplier)
   * first.or(supplier)}.
   *
   * @return {@code first} if it is non-null; otherwise {@code second} if it is non-null
   * @throws NullPointerException if both {@code first} and {@code second} are null
   * @since 18.0 (since 3.0 as {@code Objects.firstNonNull()}).
   */
  public static <T> T firstNonNull(T first, T second) {
    if (first != null) {
      return first;
    }
    if (second != null) {
      return second;
    }
    throw new NullPointerException("Both parameters are null");
  }

  private MoreObjects() {}
}
