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

import com.azure.cosmos.implementation.guava25.base.Converter;
import com.azure.cosmos.implementation.guava25.base.Function;
import com.azure.cosmos.implementation.guava25.base.Objects;
import com.azure.cosmos.implementation.guava25.primitives.Ints;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collector;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.guava25.collect.CollectPreconditions.checkNonnegative;



/**
 * Static utility methods pertaining to {@link Map} instances (including instances of {@link
 * SortedMap}, {@link BiMap}, etc.). Also see this class's counterparts {@link Lists}, {@link Sets}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#maps"> {@code Maps}</a>.
 *
 * @author Kevin Bourrillion
 * @author Mike Bostock
 * @author Isaac Shum
 * @author Louis Wasserman
 * @since 2.0
 */
public final class Maps {
  private Maps() {}

  private enum EntryFunction implements Function<Entry<?, ?>, Object> {
    KEY {
      @Override

      public Object apply(Entry<?, ?> entry) {
        return entry.getKey();
      }
    },
    VALUE {
      @Override

      public Object apply(Entry<?, ?> entry) {
        return entry.getValue();
      }
    };
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static <K> Function<Entry<K, ?>, K> keyFunction() {
    return (Function) EntryFunction.KEY;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static <V> Function<Entry<?, V>, V> valueFunction() {
    return (Function) EntryFunction.VALUE;
  }

  static <K, V> Iterator<K> keyIterator(Iterator<Entry<K, V>> entryIterator) {
    return new TransformedIterator<Entry<K, V>, K>(entryIterator) {
      @Override
      K transform(Entry<K, V> entry) {
        return entry.getKey();
      }
    };
  }

  static <K, V> Iterator<V> valueIterator(Iterator<Entry<K, V>> entryIterator) {
    return new TransformedIterator<Entry<K, V>, V>(entryIterator) {
      @Override
      V transform(Entry<K, V> entry) {
        return entry.getValue();
      }
    };
  }

    private static class Accumulator<K extends Enum<K>, V> {
    private final BinaryOperator<V> mergeFunction;
    private EnumMap<K, V> map = null;

    Accumulator(BinaryOperator<V> mergeFunction) {
      this.mergeFunction = mergeFunction;
    }

    void put(K key, V value) {
      if (map == null) {
        map = new EnumMap<>(key.getDeclaringClass());
      }
      map.merge(key, value, mergeFunction);
    }

    Accumulator<K, V> combine(Accumulator<K, V> other) {
      if (this.map == null) {
        return other;
      } else if (other.map == null) {
        return this;
      } else {
        other.map.forEach(this::put);
        return this;
      }
    }

    ImmutableMap<K, V> toImmutableMap() {
      return (map == null) ? ImmutableMap.<K, V>of() : ImmutableEnumMap.asImmutable(map);
    }
  }

  /**
   * Returns a {@link Collector} that accumulates elements into an {@code ImmutableMap} whose keys
   * and values are the result of applying the provided mapping functions to the input elements. The
   * resulting implementation is specialized for enum key types. The returned map and its views will
   * iterate over keys in their enum definition order, not encounter order.
   *
   * <p>If the mapped keys contain duplicates, an {@code IllegalArgumentException} is thrown when
   * the collection operation is performed. (This differs from the {@code Collector} returned by
   * {@link java.util.stream.Collectors#toMap(java.util.function.Function,
   * java.util.function.Function) Collectors.toMap(Function, Function)}, which throws an {@code
   * IllegalStateException}.)
   *
   * @since 21.0
   */

  public static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(
      java.util.function.Function<? super T, ? extends K> keyFunction,
      java.util.function.Function<? super T, ? extends V> valueFunction) {
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    return Collector.of(
        () ->
            new Accumulator<K, V>(
                (v1, v2) -> {
                  throw new IllegalArgumentException("Multiple values for key: " + v1 + ", " + v2);
                }),
        (accum, t) -> {
          K key = checkNotNull(keyFunction.apply(t), "Null key for input %s", t);
          V newValue = checkNotNull(valueFunction.apply(t), "Null value for input %s", t);
          accum.put(key, newValue);
        },
        Accumulator::combine,
        Accumulator::toImmutableMap,
        Collector.Characteristics.UNORDERED);
  }

    /**
   * Creates a <i>mutable</i>, empty {@code HashMap} instance.
   *
   * <p><b>Note:</b> if mutability is not required, use {@link ImmutableMap#of()} instead.
   *
   * <p><b>Note:</b> if {@code K} is an {@code enum} type, use {@link #newEnumMap} instead.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code HashMap} constructor directly, taking advantage of the new
   * <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @return a new, empty {@code HashMap}
   */
  public static <K, V> HashMap<K, V> newHashMap() {
    return new HashMap<>();
  }

    /**
   * Creates a {@code HashMap} instance, with a high enough "initial capacity" that it <i>should</i>
   * hold {@code expectedSize} elements without growth. This behavior cannot be broadly guaranteed,
   * but it is observed to be true for OpenJDK 1.7. It also can't be guaranteed that the method
   * isn't inadvertently <i>oversizing</i> the returned map.
   *
   * @param expectedSize the number of entries you expect to add to the returned map
   * @return a new, empty {@code HashMap} with enough capacity to hold {@code expectedSize} entries
   *     without resizing
   * @throws IllegalArgumentException if {@code expectedSize} is negative
   */
  public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
    return new HashMap<>(capacity(expectedSize));
  }

  /**
   * Returns a capacity that is sufficient to keep the map from being resized as long as it grows no
   * larger than expectedSize and the load factor is ≥ its default (0.75).
   */
  static int capacity(int expectedSize) {
    if (expectedSize < 3) {
      checkNonnegative(expectedSize, "expectedSize");
      return expectedSize + 1;
    }
    if (expectedSize < Ints.MAX_POWER_OF_TWO) {
      // This is the calculation used in JDK8 to resize when a putAll
      // happens; it seems to be the most conservative calculation we
      // can make.  0.75 is the default load factor.
      return (int) ((float) expectedSize / 0.75F + 1.0F);
    }
    return Integer.MAX_VALUE; // any large value
  }

  /**
   * Creates a <i>mutable</i>, empty, insertion-ordered {@code LinkedHashMap} instance.
   *
   * <p><b>Note:</b> if mutability is not required, use {@link ImmutableMap#of()} instead.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code LinkedHashMap} constructor directly, taking advantage of
   * the new <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @return a new, empty {@code LinkedHashMap}
   */
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
    return new LinkedHashMap<>();
  }

    /**
   * Creates a {@code LinkedHashMap} instance, with a high enough "initial capacity" that it
   * <i>should</i> hold {@code expectedSize} elements without growth. This behavior cannot be
   * broadly guaranteed, but it is observed to be true for OpenJDK 1.7. It also can't be guaranteed
   * that the method isn't inadvertently <i>oversizing</i> the returned map.
   *
   * @param expectedSize the number of entries you expect to add to the returned map
   * @return a new, empty {@code LinkedHashMap} with enough capacity to hold {@code expectedSize}
   *     entries without resizing
   * @throws IllegalArgumentException if {@code expectedSize} is negative
   * @since 19.0
   */
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
    return new LinkedHashMap<>(capacity(expectedSize));
  }

    static <K, V> Iterator<Entry<K, V>> asMapEntryIterator(
      Set<K> set, final Function<? super K, V> function) {
    return new TransformedIterator<K, Entry<K, V>>(set.iterator()) {
      @Override
      Entry<K, V> transform(final K key) {
        return immutableEntry(key, function.apply(key));
      }
    };
  }

    /**
   * Returns an immutable map whose keys are the distinct elements of {@code keys} and whose value
   * for each key was computed by {@code valueFunction}. The map's iteration order is the order of
   * the first appearance of each key in {@code keys}.
   *
   * <p>When there are multiple instances of a key in {@code keys}, it is unspecified whether {@code
   * valueFunction} will be applied to more than one instance of that key and, if it is, which
   * result will be mapped to that key in the returned map.
   *
   * <p>If {@code keys} is a {@link Set}, a live view can be obtained instead of a copy using {@link
   * Maps#asMap(Set, Function)}.
   *
   * @throws NullPointerException if any element of {@code keys} is {@code null}, or if {@code
   *     valueFunction} produces {@code null} for any key
   * @since 14.0
   */
  public static <K, V> ImmutableMap<K, V> toMap(
      Iterable<K> keys, Function<? super K, V> valueFunction) {
    return toMap(keys.iterator(), valueFunction);
  }

  /**
   * Returns an immutable map whose keys are the distinct elements of {@code keys} and whose value
   * for each key was computed by {@code valueFunction}. The map's iteration order is the order of
   * the first appearance of each key in {@code keys}.
   *
   * <p>When there are multiple instances of a key in {@code keys}, it is unspecified whether {@code
   * valueFunction} will be applied to more than one instance of that key and, if it is, which
   * result will be mapped to that key in the returned map.
   *
   * @throws NullPointerException if any element of {@code keys} is {@code null}, or if {@code
   *     valueFunction} produces {@code null} for any key
   * @since 14.0
   */
  public static <K, V> ImmutableMap<K, V> toMap(
      Iterator<K> keys, Function<? super K, V> valueFunction) {
    checkNotNull(valueFunction);
    // Using LHM instead of a builder so as not to fail on duplicate keys
    Map<K, V> builder = newLinkedHashMap();
    while (keys.hasNext()) {
      K key = keys.next();
      builder.put(key, valueFunction.apply(key));
    }
    return ImmutableMap.copyOf(builder);
  }

    /**
   * Returns an immutable map entry with the specified key and value. The {@link Entry#setValue}
   * operation throws an {@link UnsupportedOperationException}.
   *
   * <p>The returned entry is serializable.
   *
   * @param key the key to be associated with the returned entry
   * @param value the value to be associated with the returned entry
   */
  public static <K, V> Entry<K, V> immutableEntry(K key, V value) {
    return new ImmutableEntry<>(key, value);
  }

    /**
   * Returns an unmodifiable view of the specified map entry. The {@link Entry#setValue} operation
   * throws an {@link UnsupportedOperationException}. This also has the side-effect of redefining
   * {@code equals} to comply with the Entry contract, to avoid a possible nefarious implementation
   * of equals.
   *
   * @param entry the entry for which to return an unmodifiable view
   * @return an unmodifiable view of the entry
   */
  static <K, V> Entry<K, V> unmodifiableEntry(final Entry<? extends K, ? extends V> entry) {
    checkNotNull(entry);
    return new AbstractMapEntry<K, V>() {
      @Override
      public K getKey() {
        return entry.getKey();
      }

      @Override
      public V getValue() {
        return entry.getValue();
      }
    };
  }

  static <K, V> UnmodifiableIterator<Entry<K, V>> unmodifiableEntryIterator(
      final Iterator<Entry<K, V>> entryIterator) {
    return new UnmodifiableIterator<Entry<K, V>>() {
      @Override
      public boolean hasNext() {
        return entryIterator.hasNext();
      }

      @Override
      public Entry<K, V> next() {
        return unmodifiableEntry(entryIterator.next());
      }
    };
  }

    /**
   * Returns a {@link Converter} that converts values using {@link BiMap#get bimap.get()}, and whose
   * inverse view converts values using {@link BiMap#inverse bimap.inverse()}{@code .get()}.
   *
   * <p>To use a plain {@link Map} as a {@link Function}, see {@link
   * com.azure.cosmos.implementation.guava25.base.Functions#forMap(Map)} or {@link
   * com.azure.cosmos.implementation.guava25.base.Functions#forMap(Map, Object)}.
   *
   * @since 16.0
   */

  public static <A, B> Converter<A, B> asConverter(final BiMap<A, B> bimap) {
    return new BiMapConverter<>(bimap);
  }

  private static final class BiMapConverter<A, B> extends Converter<A, B> implements Serializable {
    private final BiMap<A, B> bimap;

    BiMapConverter(BiMap<A, B> bimap) {
      this.bimap = checkNotNull(bimap);
    }

    @Override
    protected B doForward(A a) {
      return convert(bimap, a);
    }

    @Override
    protected A doBackward(B b) {
      return convert(bimap.inverse(), b);
    }

    private static <X, Y> Y convert(BiMap<X, Y> bimap, X input) {
      Y output = bimap.get(input);
      checkArgument(output != null, "No non-null mapping present for input: %s", input);
      return output;
    }

    @Override
    public boolean equals(Object object) {
      if (object instanceof BiMapConverter) {
        BiMapConverter<?, ?> that = (BiMapConverter<?, ?>) object;
        return this.bimap.equals(that.bimap);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return bimap.hashCode();
    }

    // There's really no good way to implement toString() without printing the entire BiMap, right?
    @Override
    public String toString() {
      return "Maps.asConverter(" + bimap + ")";
    }

    private static final long serialVersionUID = 0L;
  }

  /**
   * Returns a synchronized (thread-safe) bimap backed by the specified bimap. In order to guarantee
   * serial access, it is critical that <b>all</b> access to the backing bimap is accomplished
   * through the returned bimap.
   *
   * <p>It is imperative that the user manually synchronize on the returned map when accessing any
   * of its collection views:
   *
   * <pre>{@code
   * BiMap<Long, String> map = Maps.synchronizedBiMap(
   *     HashBiMap.<Long, String>create());
   * ...
   * Set<Long> set = map.keySet();  // Needn't be in synchronized block
   * ...
   * synchronized (map) {  // Synchronizing on map, not set!
   *   Iterator<Long> it = set.iterator(); // Must be in synchronized block
   *   while (it.hasNext()) {
   *     foo(it.next());
   *   }
   * }
   * }</pre>
   *
   * <p>Failure to follow this advice may result in non-deterministic behavior.
   *
   * <p>The returned bimap will be serializable if the specified bimap is serializable.
   *
   * @param bimap the bimap to be wrapped in a synchronized view
   * @return a synchronized view of the specified bimap
   */
  public static <K, V> BiMap<K, V> synchronizedBiMap(BiMap<K, V> bimap) {
    return Synchronized.biMap(bimap, null);
  }

    /**
   * Returns a view of a sorted map whose values are derived from the original sorted map's entries.
   * In contrast to {@link #transformValues}, this method's entry-transformation logic may depend on
   * the key as well as the value.
   *
   * <p>All other properties of the transformed map, such as iteration order, are left intact. For
   * example, the code:
   *
   * <pre>{@code
   * Map<String, Boolean> options =
   *     ImmutableSortedMap.of("verbose", true, "sort", false);
   * EntryTransformer<String, Boolean, String> flagPrefixer =
   *     new EntryTransformer<String, Boolean, String>() {
   *       public String transformEntry(String key, Boolean value) {
   *         return value ? key : "yes" + key;
   *       }
   *     };
   * SortedMap<String, String> transformed =
   *     Maps.transformEntries(options, flagPrefixer);
   * System.out.println(transformed);
   * }</pre>
   *
   * ... prints {@code {sort=yessort, verbose=verbose}}.
   *
   * <p>Changes in the underlying map are reflected in this view. Conversely, this view supports
   * removal operations, and these are reflected in the underlying map.
   *
   * <p>It's acceptable for the underlying map to contain null keys and null values provided that
   * the transformer is capable of accepting null inputs. The transformed map might contain null
   * values if the transformer sometimes gives a null result.
   *
   * <p>The returned map is not thread-safe or serializable, even if the underlying map is.
   *
   * <p>The transformer is applied lazily, invoked when needed. This is necessary for the returned
   * map to be a view, but it means that the transformer will be applied many times for bulk
   * operations like {@link Map#containsValue} and {@link Object#toString}. For this to perform
   * well, {@code transformer} should be fast. To avoid lazy evaluation when the returned map
   * doesn't need to be a view, copy the returned map into a new map of your choosing.
   *
   * <p><b>Warning:</b> This method assumes that for any instance {@code k} of {@code
   * EntryTransformer} key type {@code K}, {@code k.equals(k2)} implies that {@code k2} is also of
   * type {@code K}. Using an {@code EntryTransformer} key type for which this may not hold, such as
   * {@code ArrayList}, may risk a {@code ClassCastException} when calling methods on the
   * transformed map.
   *
   * @since 11.0
   */
  public static <K, V1, V2> SortedMap<K, V2> transformEntries(
      SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return new TransformedEntriesSortedMap<>(fromMap, transformer);
  }

  /**
   * Returns a view of a navigable map whose values are derived from the original navigable map's
   * entries. In contrast to {@link #transformValues}, this method's entry-transformation logic may
   * depend on the key as well as the value.
   *
   * <p>All other properties of the transformed map, such as iteration order, are left intact. For
   * example, the code:
   *
   * <pre>{@code
   * NavigableMap<String, Boolean> options = Maps.newTreeMap();
   * options.put("verbose", false);
   * options.put("sort", true);
   * EntryTransformer<String, Boolean, String> flagPrefixer =
   *     new EntryTransformer<String, Boolean, String>() {
   *       public String transformEntry(String key, Boolean value) {
   *         return value ? key : ("yes" + key);
   *       }
   *     };
   * NavigableMap<String, String> transformed =
   *     LabsMaps.transformNavigableEntries(options, flagPrefixer);
   * System.out.println(transformed);
   * }</pre>
   *
   * ... prints {@code {sort=yessort, verbose=verbose}}.
   *
   * <p>Changes in the underlying map are reflected in this view. Conversely, this view supports
   * removal operations, and these are reflected in the underlying map.
   *
   * <p>It's acceptable for the underlying map to contain null keys and null values provided that
   * the transformer is capable of accepting null inputs. The transformed map might contain null
   * values if the transformer sometimes gives a null result.
   *
   * <p>The returned map is not thread-safe or serializable, even if the underlying map is.
   *
   * <p>The transformer is applied lazily, invoked when needed. This is necessary for the returned
   * map to be a view, but it means that the transformer will be applied many times for bulk
   * operations like {@link Map#containsValue} and {@link Object#toString}. For this to perform
   * well, {@code transformer} should be fast. To avoid lazy evaluation when the returned map
   * doesn't need to be a view, copy the returned map into a new map of your choosing.
   *
   * <p><b>Warning:</b> This method assumes that for any instance {@code k} of {@code
   * EntryTransformer} key type {@code K}, {@code k.equals(k2)} implies that {@code k2} is also of
   * type {@code K}. Using an {@code EntryTransformer} key type for which this may not hold, such as
   * {@code ArrayList}, may risk a {@code ClassCastException} when calling methods on the
   * transformed map.
   *
   * @since 13.0
   */
  public static <K, V1, V2> NavigableMap<K, V2> transformEntries(
      final NavigableMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return new TransformedEntriesNavigableMap<>(fromMap, transformer);
  }

  /**
   * A transformation of the value of a key-value pair, using both key and value as inputs. To apply
   * the transformation to a map, use {@link Maps#transformEntries(Map, EntryTransformer)}.
   *
   * @param <K> the key type of the input and output entries
   * @param <V1> the value type of the input entry
   * @param <V2> the value type of the output entry
   * @since 7.0
   */
  @FunctionalInterface
  public interface EntryTransformer<K, V1, V2> {
    /**
     * Determines an output value based on a key-value pair. This method is <i>generally
     * expected</i>, but not absolutely required, to have the following properties:
     *
     * <ul>
     *   <li>Its execution does not cause any observable side effects.
     *   <li>The computation is <i>consistent with equals</i>; that is, {@link Objects#equal
     *       Objects.equal}{@code (k1, k2) &&} {@link Objects#equal}{@code (v1, v2)} implies that
     *       {@code Objects.equal(transformer.transform(k1, v1), transformer.transform(k2, v2))}.
     * </ul>
     *
     * @throws NullPointerException if the key or value is null and this transformer does not accept
     *     null arguments
     */
    V2 transformEntry(K key, V1 value);
  }

    /** Returns a view of an entry transformed by the specified transformer. */
  static <V2, K, V1> Entry<K, V2> transformEntry(
      final EntryTransformer<? super K, ? super V1, V2> transformer, final Entry<K, V1> entry) {
    checkNotNull(transformer);
    checkNotNull(entry);
    return new AbstractMapEntry<K, V2>() {
      @Override
      public K getKey() {
        return entry.getKey();
      }

      @Override
      public V2 getValue() {
        return transformer.transformEntry(entry.getKey(), entry.getValue());
      }
    };
  }

  /** Views an entry transformer as a function from entries to entries. */
  static <K, V1, V2> Function<Entry<K, V1>, Entry<K, V2>> asEntryToEntryFunction(
      final EntryTransformer<? super K, ? super V1, V2> transformer) {
    checkNotNull(transformer);
    return new Function<Entry<K, V1>, Entry<K, V2>>() {
      @Override
      public Entry<K, V2> apply(final Entry<K, V1> entry) {
        return transformEntry(transformer, entry);
      }
    };
  }

  static class TransformedEntriesMap<K, V1, V2> extends IteratorBasedAbstractMap<K, V2> {
    final Map<K, V1> fromMap;
    final EntryTransformer<? super K, ? super V1, V2> transformer;

    TransformedEntriesMap(
        Map<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
      this.fromMap = checkNotNull(fromMap);
      this.transformer = checkNotNull(transformer);
    }

    @Override
    public int size() {
      return fromMap.size();
    }

    @Override
    public boolean containsKey(Object key) {
      return fromMap.containsKey(key);
    }

    @Override

    public V2 get(Object key) {
      return getOrDefault(key, null);
    }

    // safe as long as the user followed the <b>Warning</b> in the javadoc
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override

    public V2 getOrDefault(Object key, V2 defaultValue) {
      V1 value = fromMap.get(key);
      return (value != null || fromMap.containsKey(key))
          ? transformer.transformEntry((K) key, value)
          : defaultValue;
    }

    // safe as long as the user followed the <b>Warning</b> in the javadoc
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public V2 remove(Object key) {
      return fromMap.containsKey(key)
          ? transformer.transformEntry((K) key, fromMap.remove(key))
          : null;
    }

    @Override
    public void clear() {
      fromMap.clear();
    }

    @Override
    public Set<K> keySet() {
      return fromMap.keySet();
    }

    @Override
    Iterator<Entry<K, V2>> entryIterator() {
      return Iterators.transform(
          fromMap.entrySet().iterator(), Maps.<K, V1, V2>asEntryToEntryFunction(transformer));
    }

    @Override
    Spliterator<Entry<K, V2>> entrySpliterator() {
      return CollectSpliterators.map(
          fromMap.entrySet().spliterator(), Maps.<K, V1, V2>asEntryToEntryFunction(transformer));
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V2> action) {
      checkNotNull(action);
      // avoids creating new Entry<K, V2> objects
      fromMap.forEach((k, v1) -> action.accept(k, transformer.transformEntry(k, v1)));
    }

    @Override
    public Collection<V2> values() {
      return new Values<>(this);
    }
  }

  static class TransformedEntriesSortedMap<K, V1, V2> extends TransformedEntriesMap<K, V1, V2>
      implements SortedMap<K, V2> {

    protected SortedMap<K, V1> fromMap() {
      return (SortedMap<K, V1>) fromMap;
    }

    TransformedEntriesSortedMap(
        SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
      super(fromMap, transformer);
    }

    @Override
    public Comparator<? super K> comparator() {
      return fromMap().comparator();
    }

    @Override
    public K firstKey() {
      return fromMap().firstKey();
    }

    @Override
    public SortedMap<K, V2> headMap(K toKey) {
      return transformEntries(fromMap().headMap(toKey), transformer);
    }

    @Override
    public K lastKey() {
      return fromMap().lastKey();
    }

    @Override
    public SortedMap<K, V2> subMap(K fromKey, K toKey) {
      return transformEntries(fromMap().subMap(fromKey, toKey), transformer);
    }

    @Override
    public SortedMap<K, V2> tailMap(K fromKey) {
      return transformEntries(fromMap().tailMap(fromKey), transformer);
    }
  }

  private static class TransformedEntriesNavigableMap<K, V1, V2>
      extends TransformedEntriesSortedMap<K, V1, V2> implements NavigableMap<K, V2> {

    TransformedEntriesNavigableMap(
        NavigableMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
      super(fromMap, transformer);
    }

    @Override
    public Entry<K, V2> ceilingEntry(K key) {
      return transformEntry(fromMap().ceilingEntry(key));
    }

    @Override
    public K ceilingKey(K key) {
      return fromMap().ceilingKey(key);
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
      return fromMap().descendingKeySet();
    }

    @Override
    public NavigableMap<K, V2> descendingMap() {
      return transformEntries(fromMap().descendingMap(), transformer);
    }

    @Override
    public Entry<K, V2> firstEntry() {
      return transformEntry(fromMap().firstEntry());
    }

    @Override
    public Entry<K, V2> floorEntry(K key) {
      return transformEntry(fromMap().floorEntry(key));
    }

    @Override
    public K floorKey(K key) {
      return fromMap().floorKey(key);
    }

    @Override
    public NavigableMap<K, V2> headMap(K toKey) {
      return headMap(toKey, false);
    }

    @Override
    public NavigableMap<K, V2> headMap(K toKey, boolean inclusive) {
      return transformEntries(fromMap().headMap(toKey, inclusive), transformer);
    }

    @Override
    public Entry<K, V2> higherEntry(K key) {
      return transformEntry(fromMap().higherEntry(key));
    }

    @Override
    public K higherKey(K key) {
      return fromMap().higherKey(key);
    }

    @Override
    public Entry<K, V2> lastEntry() {
      return transformEntry(fromMap().lastEntry());
    }

    @Override
    public Entry<K, V2> lowerEntry(K key) {
      return transformEntry(fromMap().lowerEntry(key));
    }

    @Override
    public K lowerKey(K key) {
      return fromMap().lowerKey(key);
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
      return fromMap().navigableKeySet();
    }

    @Override
    public Entry<K, V2> pollFirstEntry() {
      return transformEntry(fromMap().pollFirstEntry());
    }

    @Override
    public Entry<K, V2> pollLastEntry() {
      return transformEntry(fromMap().pollLastEntry());
    }

    @Override
    public NavigableMap<K, V2> subMap(
        K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
      return transformEntries(
          fromMap().subMap(fromKey, fromInclusive, toKey, toInclusive), transformer);
    }

    @Override
    public NavigableMap<K, V2> subMap(K fromKey, K toKey) {
      return subMap(fromKey, true, toKey, false);
    }

    @Override
    public NavigableMap<K, V2> tailMap(K fromKey) {
      return tailMap(fromKey, true);
    }

    @Override
    public NavigableMap<K, V2> tailMap(K fromKey, boolean inclusive) {
      return transformEntries(fromMap().tailMap(fromKey, inclusive), transformer);
    }


    private Entry<K, V2> transformEntry(Entry<K, V1> entry) {
      return (entry == null) ? null : Maps.transformEntry(transformer, entry);
    }

    @Override
    protected NavigableMap<K, V1> fromMap() {
      return (NavigableMap<K, V1>) super.fromMap();
    }
  }

    /**
   * {@code AbstractMap} extension that makes it easy to cache customized keySet, values, and
   * entrySet views.
   */
  abstract static class ViewCachingAbstractMap<K, V> extends AbstractMap<K, V> {
    /**
     * Creates the entry set to be returned by {@link #entrySet()}. This method is invoked at most
     * once on a given map, at the time when {@code entrySet} is first called.
     */
    abstract Set<Entry<K, V>> createEntrySet();

    private transient Set<Entry<K, V>> entrySet;

    @Override
    public Set<Entry<K, V>> entrySet() {
      Set<Entry<K, V>> result = entrySet;
      return (result == null) ? entrySet = createEntrySet() : result;
    }

    private transient Set<K> keySet;

    @Override
    public Set<K> keySet() {
      Set<K> result = keySet;
      return (result == null) ? keySet = createKeySet() : result;
    }

    Set<K> createKeySet() {
      return new KeySet<>(this);
    }

    private transient Collection<V> values;

    @Override
    public Collection<V> values() {
      Collection<V> result = values;
      return (result == null) ? values = createValues() : result;
    }

    Collection<V> createValues() {
      return new Values<>(this);
    }
  }

  abstract static class IteratorBasedAbstractMap<K, V> extends AbstractMap<K, V> {
    @Override
    public abstract int size();

    abstract Iterator<Entry<K, V>> entryIterator();

    Spliterator<Entry<K, V>> entrySpliterator() {
      return Spliterators.spliterator(
          entryIterator(), size(), Spliterator.SIZED | Spliterator.DISTINCT);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
      return new EntrySet<K, V>() {
        @Override
        Map<K, V> map() {
          return IteratorBasedAbstractMap.this;
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
          return entryIterator();
        }

        @Override
        public Spliterator<Entry<K, V>> spliterator() {
          return entrySpliterator();
        }

        @Override
        public void forEach(Consumer<? super Entry<K, V>> action) {
          forEachEntry(action);
        }
      };
    }

    void forEachEntry(Consumer<? super Entry<K, V>> action) {
      entryIterator().forEachRemaining(action);
    }

    @Override
    public void clear() {
      Iterators.clear(entryIterator());
    }
  }

  /**
   * Delegates to {@link Map#get}. Returns {@code null} on {@code ClassCastException} and {@code
   * NullPointerException}.
   */
  static <V> V safeGet(Map<?, V> map, Object key) {
    checkNotNull(map);
    try {
      return map.get(key);
    } catch (ClassCastException | NullPointerException e) {
      return null;
    }
  }

  /**
   * Delegates to {@link Map#containsKey}. Returns {@code false} on {@code ClassCastException} and
   * {@code NullPointerException}.
   */
  static boolean safeContainsKey(Map<?, ?> map, Object key) {
    checkNotNull(map);
    try {
      return map.containsKey(key);
    } catch (ClassCastException | NullPointerException e) {
      return false;
    }
  }

  /**
   * Delegates to {@link Map#remove}. Returns {@code null} on {@code ClassCastException} and {@code
   * NullPointerException}.
   */
  static <V> V safeRemove(Map<?, V> map, Object key) {
    checkNotNull(map);
    try {
      return map.remove(key);
    } catch (ClassCastException | NullPointerException e) {
      return null;
    }
  }

    /** An implementation of {@link Map#equals}. */
  static boolean equalsImpl(Map<?, ?> map, Object object) {
    if (map == object) {
      return true;
    } else if (object instanceof Map) {
      Map<?, ?> o = (Map<?, ?>) object;
      return map.entrySet().equals(o.entrySet());
    }
    return false;
  }

  /** An implementation of {@link Map#toString}. */
  static String toStringImpl(Map<?, ?> map) {
    StringBuilder sb = Collections2.newStringBuilderForCollection(map.size()).append('{');
    boolean first = true;
    for (Entry<?, ?> entry : map.entrySet()) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      sb.append(entry.getKey()).append('=').append(entry.getValue());
    }
    return sb.append('}').toString();
  }

    static class KeySet<K, V> extends Sets.ImprovedAbstractSet<K> {
    final Map<K, V> map;

    KeySet(Map<K, V> map) {
      this.map = checkNotNull(map);
    }

    Map<K, V> map() {
      return map;
    }

    @Override
    public Iterator<K> iterator() {
      return keyIterator(map().entrySet().iterator());
    }

    @Override
    public void forEach(Consumer<? super K> action) {
      checkNotNull(action);
      // avoids entry allocation for those maps that allocate entries on iteration
      map.forEach((k, v) -> action.accept(k));
    }

    @Override
    public int size() {
      return map().size();
    }

    @Override
    public boolean isEmpty() {
      return map().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
      return map().containsKey(o);
    }

    @Override
    public boolean remove(Object o) {
      if (contains(o)) {
        map().remove(o);
        return true;
      }
      return false;
    }

    @Override
    public void clear() {
      map().clear();
    }
  }


  static <K> K keyOrNull(Entry<K, ?> entry) {
    return (entry == null) ? null : entry.getKey();
  }


  static <V> V valueOrNull(Entry<?, V> entry) {
    return (entry == null) ? null : entry.getValue();
  }

    static class Values<K, V> extends AbstractCollection<V> {
    final Map<K, V> map;

    Values(Map<K, V> map) {
      this.map = checkNotNull(map);
    }

    final Map<K, V> map() {
      return map;
    }

    @Override
    public Iterator<V> iterator() {
      return valueIterator(map().entrySet().iterator());
    }

    @Override
    public void forEach(Consumer<? super V> action) {
      checkNotNull(action);
      // avoids allocation of entries for those maps that generate fresh entries on iteration
      map.forEach((k, v) -> action.accept(v));
    }

    @Override
    public boolean remove(Object o) {
      try {
        return super.remove(o);
      } catch (UnsupportedOperationException e) {
        for (Entry<K, V> entry : map().entrySet()) {
          if (Objects.equal(o, entry.getValue())) {
            map().remove(entry.getKey());
            return true;
          }
        }
        return false;
      }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      try {
        return super.removeAll(checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        Set<K> toRemove = Sets.newHashSet();
        for (Entry<K, V> entry : map().entrySet()) {
          if (c.contains(entry.getValue())) {
            toRemove.add(entry.getKey());
          }
        }
        return map().keySet().removeAll(toRemove);
      }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      try {
        return super.retainAll(checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        Set<K> toRetain = Sets.newHashSet();
        for (Entry<K, V> entry : map().entrySet()) {
          if (c.contains(entry.getValue())) {
            toRetain.add(entry.getKey());
          }
        }
        return map().keySet().retainAll(toRetain);
      }
    }

    @Override
    public int size() {
      return map().size();
    }

    @Override
    public boolean isEmpty() {
      return map().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
      return map().containsValue(o);
    }

    @Override
    public void clear() {
      map().clear();
    }
  }

  abstract static class EntrySet<K, V> extends Sets.ImprovedAbstractSet<Entry<K, V>> {
    abstract Map<K, V> map();

    @Override
    public int size() {
      return map().size();
    }

    @Override
    public void clear() {
      map().clear();
    }

    @Override
    public boolean contains(Object o) {
      if (o instanceof Entry) {
        Entry<?, ?> entry = (Entry<?, ?>) o;
        Object key = entry.getKey();
        V value = Maps.safeGet(map(), key);
        return Objects.equal(value, entry.getValue()) && (value != null || map().containsKey(key));
      }
      return false;
    }

    @Override
    public boolean isEmpty() {
      return map().isEmpty();
    }

    @Override
    public boolean remove(Object o) {
      if (contains(o)) {
        Entry<?, ?> entry = (Entry<?, ?>) o;
        return map().keySet().remove(entry.getKey());
      }
      return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      try {
        return super.removeAll(checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        // if the iterators don't support remove
        return Sets.removeAllImpl(this, c.iterator());
      }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      try {
        return super.retainAll(checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        // if the iterators don't support remove
        Set<Object> keys = Sets.newHashSetWithExpectedSize(c.size());
        for (Object o : c) {
          if (contains(o)) {
            Entry<?, ?> entry = (Entry<?, ?>) o;
            keys.add(entry.getKey());
          }
        }
        return map().keySet().retainAll(keys);
      }
    }
  }

}
