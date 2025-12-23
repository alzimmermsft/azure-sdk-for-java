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

package com.azure.cosmos.implementation.guava25.primitives;

import com.azure.cosmos.implementation.guava25.base.Preconditions;

import java.util.Arrays;

/**
 * Static utility methods pertaining to {@code int} primitives, that are not already found in either
 * {@link Integer} or {@link Arrays}.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/PrimitivesExplained">primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Ints {
  private Ints() {}

  /**
   * Returns the {@code int} value that is equal to {@code value}, if possible.
   *
   * @param value any value in the range of the {@code int} type
   * @return the {@code int} value that equals {@code value}
   * @throws IllegalArgumentException if {@code value} is greater than {@link Integer#MAX_VALUE} or
   *     less than {@link Integer#MIN_VALUE}
   */
  public static int checkedCast(long value) {
    int result = (int) value;
    Preconditions.checkArgument(result == value, "Out of range: %s", value);
    return result;
  }
}
