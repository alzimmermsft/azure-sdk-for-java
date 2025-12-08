/*
 * Copyright (C) 2006 The Guava Authors
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

package com.azure.cosmos.implementation.guava25.escape;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Simple helper class to build a "sparse" array of objects based on the indexes that were added to
 * it. The array will be from 0 to the maximum index given. All non-set indexes will contain null
 * (so it's not really a sparse array, just a pseudo sparse array). The builder can also return a
 * CharEscaper based on the generated array.
 *
 * @author Sven Mawson
 * @since 15.0
 */

public final class CharEscaperBuilder {

    // Replacement mappings.
  private final Map<Character, String> map;

  // The highest index we've seen so far.
  private int max = -1;

  /** Construct a new sparse array builder. */
  public CharEscaperBuilder() {
    this.map = new HashMap<>();
  }

    /**
   * Convert this builder into an array of char[]s where the maximum index is the value of the
   * highest character that has been seen. The array will be sparse in the sense that any unseen
   * index will default to null.
   *
   * @return a "sparse" array that holds the replacement mappings.
   */
  public char[][] toArray() {
    char[][] result = new char[max + 1][];
    for (Entry<Character, String> entry : map.entrySet()) {
      result[entry.getKey()] = entry.getValue().toCharArray();
    }
    return result;
  }

}
