/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.util;

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
