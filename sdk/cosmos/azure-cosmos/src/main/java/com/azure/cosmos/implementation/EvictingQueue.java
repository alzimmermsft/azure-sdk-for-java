// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.azure.cosmos.implementation.Utils.checkArgument;
import static com.azure.cosmos.implementation.Utils.checkNotNull;

public final class EvictingQueue<E> implements Queue<E> {
    private final int maxSize;
    private final Queue<E> delegate;

    private EvictingQueue(int maxSize) {
        this.maxSize = maxSize;
        this.delegate = new ArrayDeque<>(maxSize);
    }

    /**
     * Creates a first-in, first-out (FIFO) {@link Queue} where older are evicted from the queue is {@code maxSize} is
     * reached and new elements are being added.
     *
     * @param <E> Type of the elements.
     * @param maxSize The maximum size of the {@link Queue}.
     * @return A {@link Queue} that evicts older elements when new elements are added if {@code maxSize} has been
     * reached.
     * @throws IllegalArgumentException If {@code maxSize} is less than or equal to zero (<= 0).
     */
    public static <E> EvictingQueue<E> create(int maxSize) {
        checkArgument(maxSize >= 0, "'maxSize' must be greater than 0 (> 0).");
        return new EvictingQueue<>(maxSize);
    }

    @Override
    public boolean add(E e) {
        checkNotNull(e);
        if (delegate.size() == maxSize) {
            delegate.remove();
        }
        delegate.add(e);
        return true;
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        int size = c.size();
        if (size == 0) {
            // Collection being added is empty, does not modify the queue so return false based on API contract.
            return false;
        }

        Iterator<? extends E> iterator = c.iterator();
        for (int i = 0; i < size - maxSize; i++) {
            // If the collection is larger than the max size allowed, skip the initial elements.
            iterator.next();
        }

        while (iterator.hasNext()) {
            add(iterator.next());
        }

        return true;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        delegate.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return delegate.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Spliterator<E> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public Stream<E> stream() {
        return delegate.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return delegate.parallelStream();
    }

    @Override
    public E remove() {
        return delegate.remove();
    }

    @Override
    public E poll() {
        return delegate.poll();
    }

    @Override
    public E element() {
        return delegate.element();
    }

    @Override
    public E peek() {
        return delegate.peek();
    }
}
