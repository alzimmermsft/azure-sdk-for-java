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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.guava25.collect.CollectPreconditions.checkNonnegative;

/**
 * Provides static methods acting on or generating a {@code Multimap}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#multimaps"> {@code
 * Multimaps}</a>.
 *
 * @author Jared Levy
 * @author Robert Konigsberg
 * @author Mike Bostock
 * @author Louis Wasserman
 * @since 2.0
 */
public final class Multimaps {
  private Multimaps() {}

    /**
   * Creates an index {@code ImmutableListMultimap} that contains the results of applying a
   * specified function to each item in an {@code Iterable} of values. Each value will be stored as
   * a value in the resulting multimap, yielding a multimap with the same size as the input
   * iterable. The key used to store that value in the multimap will be the result of calling the
   * function on that value. The resulting multimap is created as an immutable snapshot. In the
   * returned multimap, keys appear in the order they are first encountered, and the values
   * corresponding to each key appear in the same order as they are encountered.
   *
   * <p>For example,
   *
   * <pre>{@code
   * List<String> badGuys =
   *     Arrays.asList("Inky", "Blinky", "Pinky", "Pinky", "Clyde");
   * Function<String, Integer> stringLengthFunction = ...;
   * Multimap<Integer, String> index =
   *     Multimaps.index(badGuys, stringLengthFunction);
   * System.out.println(index);
   * }</pre>
   *
   * <p>prints
   *
   * <pre>{@code
   * {4=[Inky], 6=[Blinky], 5=[Pinky, Pinky, Clyde]}
   * }</pre>
   *
   * <p>The returned multimap is serializable if its keys and values are all serializable.
   *
   * @param values the values to use when constructing the {@code ImmutableListMultimap}
   * @param keyFunction the function used to produce the key for each value
   * @return {@code ImmutableListMultimap} mapping the result of evaluating the function {@code
   *     keyFunction} on each value in the input collection to that value
   * @throws NullPointerException if any element of {@code values} is {@code null}, or if {@code
   *     keyFunction} produces {@code null} for any key
   */
  public static <K, V> ImmutableListMultimap<K, V> index(
      Iterable<V> values, Function<? super V, K> keyFunction) {
    return index(values.iterator(), keyFunction);
  }

  /**
   * Creates an index {@code ImmutableListMultimap} that contains the results of applying a
   * specified function to each item in an {@code Iterator} of values. Each value will be stored as
   * a value in the resulting multimap, yielding a multimap with the same size as the input
   * iterator. The key used to store that value in the multimap will be the result of calling the
   * function on that value. The resulting multimap is created as an immutable snapshot. In the
   * returned multimap, keys appear in the order they are first encountered, and the values
   * corresponding to each key appear in the same order as they are encountered.
   *
   * <p>For example,
   *
   * <pre>{@code
   * List<String> badGuys =
   *     Arrays.asList("Inky", "Blinky", "Pinky", "Pinky", "Clyde");
   * Function<String, Integer> stringLengthFunction = ...;
   * Multimap<Integer, String> index =
   *     Multimaps.index(badGuys.iterator(), stringLengthFunction);
   * System.out.println(index);
   * }</pre>
   *
   * <p>prints
   *
   * <pre>{@code
   * {4=[Inky], 6=[Blinky], 5=[Pinky, Pinky, Clyde]}
   * }</pre>
   *
   * <p>The returned multimap is serializable if its keys and values are all serializable.
   *
   * @param values the values to use when constructing the {@code ImmutableListMultimap}
   * @param keyFunction the function used to produce the key for each value
   * @return {@code ImmutableListMultimap} mapping the result of evaluating the function {@code
   *     keyFunction} on each value in the input collection to that value
   * @throws NullPointerException if any element of {@code values} is {@code null}, or if {@code
   *     keyFunction} produces {@code null} for any key
   * @since 10.0
   */
  public static <K, V> ImmutableListMultimap<K, V> index(
      Iterator<V> values, Function<? super V, K> keyFunction) {
    checkNotNull(keyFunction);
    ImmutableListMultimap.Builder<K, V> builder = ImmutableListMultimap.builder();
    while (values.hasNext()) {
      V value = values.next();
      checkNotNull(value, values);
      builder.put(keyFunction.apply(value), value);
    }
    return builder.build();
  }

  static class Keys<K, V> extends AbstractMultiset<K> {
    final Multimap<K, V> multimap;

    Keys(Multimap<K, V> multimap) {
      this.multimap = multimap;
    }

    @Override
    Iterator<Multiset.Entry<K>> entryIterator() {
      return new TransformedIterator<Map.Entry<K, Collection<V>>, Multiset.Entry<K>>(
          multimap.asMap().entrySet().iterator()) {
        @Override
        Multiset.Entry<K> transform(final Map.Entry<K, Collection<V>> backingEntry) {
          return new Multisets.AbstractEntry<K>() {
            @Override
            public K getElement() {
              return backingEntry.getKey();
            }

            @Override
            public int getCount() {
              return backingEntry.getValue().size();
            }
          };
        }
      };
    }

    @Override
    public Spliterator<K> spliterator() {
      return CollectSpliterators.map(multimap.entries().spliterator(), Map.Entry::getKey);
    }

    @Override
    public void forEach(Consumer<? super K> consumer) {
      checkNotNull(consumer);
      multimap.entries().forEach(entry -> consumer.accept(entry.getKey()));
    }

    @Override
    int distinctElements() {
      return multimap.asMap().size();
    }

    @Override
    public int size() {
      return multimap.size();
    }

    @Override
    public boolean contains(Object element) {
      return multimap.containsKey(element);
    }

    @Override
    public Iterator<K> iterator() {
      return Maps.keyIterator(multimap.entries().iterator());
    }

    @Override
    public int count(Object element) {
      Collection<V> values = Maps.safeGet(multimap.asMap(), element);
      return (values == null) ? 0 : values.size();
    }

    @Override
    public int remove(Object element, int occurrences) {
      checkNonnegative(occurrences, "occurrences");
      if (occurrences == 0) {
        return count(element);
      }

      Collection<V> values = Maps.safeGet(multimap.asMap(), element);

      if (values == null) {
        return 0;
      }

      int oldCount = values.size();
      if (occurrences >= oldCount) {
        values.clear();
      } else {
        Iterator<V> iterator = values.iterator();
        for (int i = 0; i < occurrences; i++) {
          iterator.next();
          iterator.remove();
        }
      }
      return oldCount;
    }

    @Override
    public void clear() {
      multimap.clear();
    }

    @Override
    public Set<K> elementSet() {
      return multimap.keySet();
    }

    @Override
    Iterator<K> elementIterator() {
      throw new AssertionError("should never be called");
    }
  }

  /** A skeleton implementation of {@link Multimap#entries()}. */
  abstract static class Entries<K, V> extends AbstractCollection<Map.Entry<K, V>> {
    abstract Multimap<K, V> multimap();

    @Override
    public int size() {
      return multimap().size();
    }

    @Override
    public boolean contains(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
        return multimap().containsEntry(entry.getKey(), entry.getValue());
      }
      return false;
    }

    @Override
    public boolean remove(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
        return multimap().remove(entry.getKey(), entry.getValue());
      }
      return false;
    }

    @Override
    public void clear() {
      multimap().clear();
    }
  }

  /** A skeleton implementation of {@link Multimap#asMap()}. */
  static final class AsMap<K, V> extends Maps.ViewCachingAbstractMap<K, Collection<V>> {
    private final Multimap<K, V> multimap;

    AsMap(Multimap<K, V> multimap) {
      this.multimap = checkNotNull(multimap);
    }

    @Override
    public int size() {
      return multimap.keySet().size();
    }

    @Override
    protected Set<Entry<K, Collection<V>>> createEntrySet() {
      return new EntrySet();
    }

    void removeValuesForKey(Object key) {
      multimap.keySet().remove(key);
    }

    class EntrySet extends Maps.EntrySet<K, Collection<V>> {
      @Override
      Map<K, Collection<V>> map() {
        return AsMap.this;
      }

      @Override
      public Iterator<Entry<K, Collection<V>>> iterator() {
        return Maps.asMapEntryIterator(
            multimap.keySet(),
            new Function<K, Collection<V>>() {
              @Override
              public Collection<V> apply(K key) {
                return multimap.get(key);
              }
            });
      }

      @Override
      public boolean remove(Object o) {
        if (!contains(o)) {
          return false;
        }
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
        removeValuesForKey(entry.getKey());
        return true;
      }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Collection<V> get(Object key) {
      return containsKey(key) ? multimap.get((K) key) : null;
    }

    @Override
    public Collection<V> remove(Object key) {
      return containsKey(key) ? multimap.removeAll(key) : null;
    }

    @Override
    public Set<K> keySet() {
      return multimap.keySet();
    }

    @Override
    public boolean isEmpty() {
      return multimap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
      return multimap.containsKey(key);
    }

    @Override
    public void clear() {
      multimap.clear();
    }
  }

    static boolean equalsImpl(Multimap<?, ?> multimap, Object object) {
    if (object == multimap) {
      return true;
    }
    if (object instanceof Multimap) {
      Multimap<?, ?> that = (Multimap<?, ?>) object;
      return multimap.asMap().equals(that.asMap());
    }
    return false;
  }

  // TODO(jlevy): Create methods that filter a SortedSetMultimap.
}
