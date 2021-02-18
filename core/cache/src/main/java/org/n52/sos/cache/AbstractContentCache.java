/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.cache;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.locationtech.jts.geom.Envelope;

import org.n52.iceland.cache.ContentCache;
import org.n52.janmayen.stream.Streams;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.ReferencedEnvelope;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class AbstractContentCache implements ContentCache {
    private static final long serialVersionUID = -5233383843446821643L;

    protected AbstractContentCache() {
    }

    /**
     * Creates a new synchronized map from the specified map.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map
     *
     * @return the synchronized map
     */
    protected static <K, V> Map<K, V> newSynchronizedMap(Map<K, V> map) {
        if (map == null) {
            return CollectionHelper.synchronizedMap(0);
        } else {
            return Collections.synchronizedMap(new HashMap<>(map));
        }
    }

    /**
     * Creates a new empty synchronized map.
     *
     * @param <K> the key type
     * @param <V> the value type
     *
     * @return the synchronized map
     */
    protected static <K, V> Map<K, V> newSynchronizedMap() {
        return newSynchronizedMap(null);
    }

    /**
     * Creates a new synchronized set from the specified elements.
     *
     * @param <T>      the element type
     * @param elements the elements
     *
     * @return the synchronized set
     */
    protected static <T> Set<T> newSynchronizedSet(Iterable<T> elements) {
        if (elements == null) {
            return CollectionHelper.synchronizedSet(0);
        } else {
            if (elements instanceof Collection) {
                return Collections.synchronizedSet(new HashSet<>((Collection<T>) elements));
            } else {
                return Collections.synchronizedSet(Streams.stream(elements).collect(toSet()));
            }
        }
    }

    /**
     * Creates a new empty synchronized set.
     *
     * @param <T> the element type
     *
     * @return a synchronized set
     */
    protected static <T> Set<T> newSynchronizedSet() {
        return newSynchronizedSet(null);
    }

    /**
     * Creates a unmodifiable copy of the specified set.
     *
     * @param <T> the element type
     * @param set the set
     *
     * @return a unmodifiable copy
     */
    protected static <T> Set<T> copyOf(Set<T> set) {
        if (set == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(new HashSet<>(set));
        }
    }

    /**
     * Creates a unmodifiable copy of the specified collection of sets.
     *
     * @param <T> the element type
     * @param set the set
     *
     * @return a unmodifiable copy
     */
    protected static <T> Set<T> copyOf(Collection<Set<T>> set) {
        if (set == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(set.stream()
                    .flatMap(Set::stream).collect(toSet()));
        }
    }

    /**
     * Creates a copy of the specified envelope.
     *
     * @param e the envelope
     *
     * @return a copy
     */
    protected static ReferencedEnvelope copyOf(ReferencedEnvelope e) {
        if (e == null) {
            // TODO empty envelope
            return null;
        } else {
            return new ReferencedEnvelope(e.getEnvelope() == null ? null : new Envelope(e.getEnvelope()), e.getSrid());
        }
    }

    protected static boolean isNull(Entry<?, ?> e) {
        return e == null || e.getKey() == null || e.getValue() == null;
    }

    /**
     * Creates a new empty synchronized {@link BiMap}.
     *
     * @param <K> the key type
     * @param <V> the value type
     *
     * @return the synchronized map
     */
    protected static <K, V> BiMap<K, V> newSynchronizedBiMap() {
        return newSynchronizedBiMap(null);
    }

    /**
     * Creates a new synchronized map from the specified map.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map
     *
     * @return the synchronized map
     */
    protected static <K, V> BiMap<K, V> newSynchronizedBiMap(BiMap<K, V> map) {
        if (map == null) {
            return Maps.synchronizedBiMap(HashBiMap.<K, V>create());
        } else {
            return Maps.synchronizedBiMap(map);
        }
    }

    /**
     * Remove value from map or complete entry if values for key are empty.
     *
     * @param <K>   the key type
     * @param <V>   the value type
     * @param map   map to check
     * @param value the value to remove
     */
    protected static <K, V> void removeValue(Map<K, Set<V>> map, V value) {
        Iterator<K> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            Set<V> set = map.get(iterator.next());
            if (set.remove(value) && set.isEmpty()) {
                iterator.remove();
            }
        }
    }

}
