// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.azure.cosmos.implementation.Utils.checkNotNull;

/**
 * Class containing utility methods for dealing with {@link Collection} types.
 */
public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <V> V firstOrDefault(List<V> list, V defaultValue) {
        return list.isEmpty() ? defaultValue : list.get(0);
    }

    /**
     * Converts the {@link Iterable} into a {@link List}.
     * <p>
     * The returned list is a mutable {@link ArrayList}.
     *
     * @param <T> The type of element.
     * @param iterable The {@link Iterable} to convert.
     * @return The {@link List} created from the {@link Iterable}.
     * @throws NullPointerException If {@link Iterable} is null.
     */
    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        checkNotNull(iterable, "'iterable' cannot be null.");

        if (iterable instanceof Collection) {
            return new ArrayList<>((Collection<T>) iterable);
        }

        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);

        return list;
    }

    /**
     * Creates an immutable list from {@code values}.
     *
     * @param <T> The value type.
     * @param values The values to turn into an immutable list.
     * @return An immutable list using {@code values}.
     * @throws NullPointerException If {@code values} is null or if any value in {@code values} is null.
     */
    @SafeVarargs
    public static <T> List<T> immutableList(T... values) {
        Objects.requireNonNull(values, "'values' cannot be null.");
        if (values.length == 0) {
            return Collections.emptyList();
        } else if (values.length == 1) {
            return Collections.singletonList(checkNotNull(values[0], "null value found at index 0"));
        }

        List<T> list = new ArrayList<>(values.length);

        for (int i = 0; i < values.length; i++) {
            list.add(Objects.requireNonNull(values[i], "Null value found at index " + i));
        }

        return Collections.unmodifiableList(list);
    }

    /**
     * Creates an immutable list copy of {@code values}.
     *
     * @param <T> The value type.
     * @param values The values to copy into a new immutable list.
     * @return An immutable list using {@code values}.
     * @throws NullPointerException If {@code values} is null or if any value in {@code values} is null.
     */
    public static <T> List<T> immutableCopyOf(Collection<T> values) {
        Objects.requireNonNull(values, "'values' cannot be null.");
        if (values.isEmpty()) {
            return Collections.emptyList();
        } else if (values.size() == 1) {
            return Collections.singletonList(checkNotNull(values.iterator().next(), "null value found at index 0"));
        }
        List<T> list = new ArrayList<>(values.size());

        int i = 0;
        for (T value : values) {
            list.add(Objects.requireNonNull(value, "null value found at index " + i));
            i++;
        }

        return Collections.unmodifiableList(list);
    }

    /**
     * Gets the element at the {@code index} of the {@link Iterable}.
     *
     * @param <T> The type of the element.
     * @param iterable The {@link Iterable} to retrieve from.
     * @param index The index to get.
     * @return The element at the index.
     * @throws IndexOutOfBoundsException If {@code index} is negative or greater than or equal to the size of the
     * {@link Iterable}.
     */
    public static <T> T getIterableIndex(Iterable<T> iterable, int index) {
        if (iterable instanceof List) {
            return ((List<T>) iterable).get(index);
        }

        Iterator<T> iterator = iterable.iterator();
        while (index > 0 && iterator.hasNext()) {
            // Iterator through the iterable until the desired index is reached.
            iterator.next();
        }

        if (index != 0 || !iterator.hasNext()) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }

        return iterator.next();
    }

    /**
     * Creates an immutable set from {@code values}.
     *
     * @param <T> The value type.
     * @param values The values to turn into an immutable set.
     * @return An immutable set using {@code values}.
     * @throws NullPointerException If {@code values} is null or if any value in {@code values} is null.
     */
    @SafeVarargs
    public static <T> Set<T> immutableSet(T... values) {
        checkNotNull(values, "'values' cannot be null.");
        if (values.length == 0) {
            return Collections.emptySet();
        } else if (values.length == 1) {
            return Collections.singleton(checkNotNull(values[0], "null value found at index 0"));
        }

        List<T> list = new ArrayList<>(values.length);

        for (int i = 0; i < values.length; i++) {
            list.add(Objects.requireNonNull(values[i], "null value found at index " + i));
        }

        return Collections.unmodifiableSet(new HashSet<>(list));
    }

    /**
     * Creates an immutable map from the {@code key} and {@code value}.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     * @param key The key.
     * @param value The value.
     * @return An immutable singleton map from the {@code key} and {@code value}.
     * @throws NullPointerException If the {@code key} or {@code value} is null.
     */
    public static <K, V> Map<K, V> immutableMap(K key, V value) {
        return Collections.singletonMap(checkNotNull(key, "'key' cannot be null"),
            checkNotNull(value, "'value' cannot be null."));
    }

    /**
     * Creates an immutable map from the {@code keys} and {@code values}.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     * @param key The first key.
     * @param value The first value.
     * @param key2 The second key.
     * @param value2 The second value.
     * @return An immutable singleton map from the {@code keys} and {@code values}.
     * @throws NullPointerException If any {@code key} or {@code value} is null.
     */
    public static <K, V> Map<K, V> immutableMap(K key, V value, K key2, V value2) {
        Map<K, V> map = new HashMap<>(4);
        map.put(checkNotNull(key, "'key' cannot be null"), checkNotNull(value, "'value' cannot be null."));
        map.put(checkNotNull(key2, "'key2' cannot be null"), checkNotNull(value2, "'value2' cannot be null."));

        return Collections.unmodifiableMap(map);
    }

    /**
     * Creates an immutable map from the {@code keys} and {@code values}.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     * @param key The first key.
     * @param value The first value.
     * @param key2 The second key.
     * @param value2 The second value.
     * @param key3 The third key.
     * @param value3 The third value.
     * @return An immutable singleton map from the {@code keys} and {@code values}.
     * @throws NullPointerException If any {@code key} or {@code value} is null.
     */
    public static <K, V> Map<K, V> immutableMap(K key, V value, K key2, V value2, K key3, V value3) {
        Map<K, V> map = new HashMap<>(4);
        map.put(checkNotNull(key, "'key' cannot be null"), checkNotNull(value, "'value' cannot be null."));
        map.put(checkNotNull(key2, "'key2' cannot be null"), checkNotNull(value2, "'value2' cannot be null."));
        map.put(checkNotNull(key3, "'key3' cannot be null"), checkNotNull(value3, "'value3' cannot be null."));

        return Collections.unmodifiableMap(map);
    }

    /**
     * Creates an immutable map from the {@code keys} and {@code values}.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     * @param key The first key.
     * @param value The first value.
     * @param key2 The second key.
     * @param value2 The second value.
     * @param key3 The third key.
     * @param value3 The third value.
     * @param key4 The fourth key.
     * @param value4 The fourth value.
     * @return An immutable singleton map from the {@code keys} and {@code values}.
     * @throws NullPointerException If any {@code key} or {@code value} is null.
     */
    public static <K, V> Map<K, V> immutableMap(K key, V value, K key2, V value2, K key3, V value3, K key4, V value4) {
        Map<K, V> map = new HashMap<>(4);
        map.put(checkNotNull(key, "'key' cannot be null"), checkNotNull(value, "'value' cannot be null."));
        map.put(checkNotNull(key2, "'key2' cannot be null"), checkNotNull(value2, "'value2' cannot be null."));
        map.put(checkNotNull(key3, "'key3' cannot be null"), checkNotNull(value3, "'value3' cannot be null."));
        map.put(checkNotNull(key4, "'key4' cannot be null"), checkNotNull(value4, "'value4' cannot be null."));

        return Collections.unmodifiableMap(map);
    }
}
