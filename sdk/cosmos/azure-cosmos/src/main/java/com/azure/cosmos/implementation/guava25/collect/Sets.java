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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.azure.cosmos.implementation.guava25.base.Preconditions.checkNotNull;



/**
 * Static utility methods pertaining to {@link Set} instances. Also see this class's counterparts
 * {@link Lists}, {@link Maps} and {@link Queues}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#sets"> {@code Sets}</a>.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 * @author Chris Povirk
 * @since 2.0
 */
public final class Sets {
  private Sets() {}

  /**
   * {@link AbstractSet} substitute without the potentially-quadratic {@code removeAll}
   * implementation.
   */
  abstract static class ImprovedAbstractSet<E> extends AbstractSet<E> {
    @Override
    public boolean removeAll(Collection<?> c) {
      return removeAllImpl(this, c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      return super.retainAll(checkNotNull(c)); // GWT compatibility
    }
  }

    /**
   * Returns an immutable set instance containing the given enum elements. Internally, the returned
   * set will be backed by an {@link EnumSet}.
   *
   * <p>The iteration order of the returned set follows the enum's iteration order, not the order in
   * which the elements appear in the given collection.
   *
   * @param elements the elements, all of the same {@code enum} type, that the set should contain
   * @return an immutable set containing those elements, minus duplicates
   */
  // http://code.google.com/p/google-web-toolkit/issues/detail?id=3028
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(Iterable<E> elements) {
    if (elements instanceof ImmutableEnumSet) {
      return (ImmutableEnumSet<E>) elements;
    } else if (elements instanceof Collection) {
      Collection<E> collection = (Collection<E>) elements;
      if (collection.isEmpty()) {
        return ImmutableSet.of();
      } else {
        return ImmutableEnumSet.asImmutable(EnumSet.copyOf(collection));
      }
    } else {
      Iterator<E> itr = elements.iterator();
      if (itr.hasNext()) {
        EnumSet<E> enumSet = EnumSet.of(itr.next());
        Iterators.addAll(enumSet, itr);
        return ImmutableEnumSet.asImmutable(enumSet);
      } else {
        return ImmutableSet.of();
      }
    }
  }

    // HashSet

  /**
   * Creates a <i>mutable</i>, initially empty {@code HashSet} instance.
   *
   * <p><b>Note:</b> if mutability is not required, use {@link ImmutableSet#of()} instead. If {@code
   * E} is an {@link Enum} type, use {@link EnumSet#noneOf} instead. Otherwise, strongly consider
   * using a {@code LinkedHashSet} instead, at the cost of increased memory footprint, to get
   * deterministic iteration behavior.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code HashSet} constructor directly, taking advantage of the new
   * <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   */
  public static <E> HashSet<E> newHashSet() {
    return new HashSet<E>();
  }

    /**
   * Returns a new hash set using the smallest initial table size that can hold {@code expectedSize}
   * elements without resizing. Note that this is not what {@link HashSet#HashSet(int)} does, but it
   * is what most users want and expect it to do.
   *
   * <p>This behavior can't be broadly guaranteed, but has been tested with OpenJDK 1.7 and 1.8.
   *
   * @param expectedSize the number of elements you expect to add to the returned set
   * @return a new, empty hash set with enough capacity to hold {@code expectedSize} elements
   *     without resizing
   * @throws IllegalArgumentException if {@code expectedSize} is negative
   */
  public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
    return new HashSet<E>(Maps.capacity(expectedSize));
  }

    // LinkedHashSet

    // TreeSet

    /**
   * An unmodifiable view of a set which may be backed by other sets; this view will change as the
   * backing sets do. Contains methods to copy the data into a new set which will then remain
   * stable. There is usually no reason to retain a reference of type {@code SetView}; typically,
   * you either use it as a plain {@link Set}, or immediately invoke {@link #immutableCopy} or
   * {@link #copyInto} and forget the {@code SetView} itself.
   *
   * @since 2.0
   */
  public abstract static class SetView<E> extends AbstractSet<E> {
    private SetView() {} // no subclasses but our own

      /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public final boolean add(E e) {
      throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public final boolean remove(Object object) {
      throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public final boolean addAll(Collection<? extends E> newElements) {
      throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public final boolean removeAll(Collection<?> oldElements) {
      throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public final boolean removeIf(java.util.function.Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public final boolean retainAll(Collection<?> elementsToKeep) {
      throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public final void clear() {
      throw new UnsupportedOperationException();
    }

    /**
     * Scope the return type to {@link UnmodifiableIterator} to ensure this is an unmodifiable view.
     *
     * @since 20.0 (present with return type {@link Iterator} since 2.0)
     */
    @Override
    public abstract UnmodifiableIterator<E> iterator();
  }

    /** An implementation for {@link Set#hashCode()}. */
  static int hashCodeImpl(Set<?> s) {
    int hashCode = 0;
    for (Object o : s) {
      hashCode += o != null ? o.hashCode() : 0;

      hashCode = ~~hashCode;
      // Needed to deal with unusual integer overflow in GWT.
    }
    return hashCode;
  }

  /** An implementation for {@link Set#equals(Object)}. */
  static boolean equalsImpl(Set<?> s, Object object) {
    if (s == object) {
      return true;
    }
    if (object instanceof Set) {
      Set<?> o = (Set<?>) object;

      try {
        return s.size() == o.size() && s.containsAll(o);
      } catch (NullPointerException | ClassCastException ignored) {
        return false;
      }
    }
    return false;
  }

    /** Remove each element in an iterable from a set. */
  static boolean removeAllImpl(Set<?> set, Iterator<?> iterator) {
    boolean changed = false;
    while (iterator.hasNext()) {
      changed |= set.remove(iterator.next());
    }
    return changed;
  }

  static boolean removeAllImpl(Set<?> set, Collection<?> collection) {
    checkNotNull(collection); // for GWT
    if (collection instanceof Multiset) {
      collection = ((Multiset<?>) collection).elementSet();
    }
    /*
     * AbstractSet.removeAll(List) has quadratic behavior if the list size
     * is just more than the set's size.  We augment the test by
     * assuming that sets have fast contains() performance, and other
     * collections don't.  See
     * http://code.google.com/p/guava-libraries/issues/detail?id=1013
     */
    if (collection instanceof Set && collection.size() > set.size()) {
      return Iterators.removeAll(set.iterator(), collection);
    } else {
      return removeAllImpl(set, collection.iterator());
    }
  }

}
