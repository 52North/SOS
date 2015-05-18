/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 *
 * Map that encapsulates access to multiple values per key.
 *
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @param <C>
 *            the collection type
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 *
 */
public interface MultiMap<K, V, C extends Collection<V>> extends Map<K, C>, Serializable {
    /**
     * Checks if any collection of any key contains {@code v}.
     *
     * @param v
     *            the element to check
     *
     * @return if it is contained
     */
    boolean containsCollectionValue(V v);

    /**
     * Adds the specified value to the key. If the collection for the key was
     * {@code null} it will be created.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     *
     * @return the collection the value was added to
     */
    C add(K key, V value);

    /**
     * Adds the specified values to the key. If the collection for the key was
     * {@code null} it will be created.
     *
     * @param key
     *            the key
     * @param values
     *            the values
     *
     * @return the collection the values were added to
     */
    C addAll(K key, Collection<? extends V> values);

    /**
     * Removes the value of the collection for the specified key (if it exists).
     *
     * @param key
     *            the key
     * @param value
     *            the value to remove
     *
     * @return if the map was altered
     */
    // @Override will compile in Java 8 but not in Java 6/7
    boolean remove(Object key, Object value);

    /**
     * Removes the specified value of the collection for the specified key (if
     * it exists). If the collection for the key is empty after the removal the
     * key is removed from the map.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     *
     * @return if the map was altered
     */
    boolean removeWithKey(K key, V value);

    /**
     * Removes the values of the collection for the specified key (if it
     * exists).
     *
     * @param key
     *            the key
     * @param value
     *            the values to remove
     *
     * @return if the map was altered
     */
    boolean remove(K key, Iterable<V> value);

    /**
     * Removes the specified values of the collection for the specified key (if
     * it exists). If the collection for the key is empty after the removal the
     * key is removed from the map.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     *
     * @return if the map was altered
     */
    boolean removeWithKey(K key, Iterable<V> value);

    /**
     * Checks if the specified key is contained in this map and if the
     * associated collection is not empty.
     *
     * @param key
     *            the key
     *
     * @return if the key has at least one value
     */
    boolean hasValues(K key);

    /**
     * @return the values of all keys
     */
    C allValues();
}
