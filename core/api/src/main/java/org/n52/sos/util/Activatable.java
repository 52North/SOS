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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @param <T>
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public class Activatable<T> {
    private T object;

    private boolean active;

    public Activatable(T object) {
        this(object, true);
    }

    public Activatable(T object, boolean active) {
        this.object = object;
        this.active = active;
    }

    /**
     * @return isActive() ? getInternal() : null
     */
    public T get() {
        return isActive() ? getInternal() : null;
    }

    public T getInternal() {
        return object;
    }

    public boolean isActive() {
        return active;
    }

    public Activatable<T> setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getInternal(), isActive());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Activatable) {
            Activatable<?> a = (Activatable) obj;
            return Objects.equal(isActive(), a.isActive()) && Objects.equal(getInternal(), a.getInternal());

        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass()).add("object", getInternal()).add("active", isActive()).toString();
    }

    public static <K, V> Map<K, V> filter(Map<K, Activatable<V>> map) {
        if (map == null) {
            return Maps.newHashMap();
        }
        Map<K, V> filtered = Maps.newHashMapWithExpectedSize(map.size());
        for (K k : map.keySet()) {
            if (map.get(k) != null && map.get(k).get() != null) {
                filtered.put(k, map.get(k).get());
            }
        }
        return filtered;
    }

    public static <E> Set<E> filter(Set<Activatable<E>> set) {
        if (set == null) {
            return Sets.newHashSet();
        }
        Set<E> filtered = new HashSet<E>(set.size());
        for (Activatable<E> a : set) {
            if (a.isActive()) {
                filtered.add(a.get());
            }
        }
        return filtered;
    }

    public static <E> Set<E> unfiltered(Set<Activatable<E>> set) {
        if (set == null) {
            return Sets.newHashSet();
        }
        Set<E> unfiltered = new HashSet<E>(set.size());
        for (Activatable<E> a : set) {
            unfiltered.add(a.getInternal());
        }
        return unfiltered;
    }

    public static <K, V> Map<K, V> unfiltered(Map<K, Activatable<V>> map) {
        if (map == null) {
            return Maps.newHashMap();
        }
        Map<K, V> filtered = new HashMap<K, V>(map.size());
        for (K k : map.keySet()) {
            if (map.get(k) != null) {
                filtered.put(k, map.get(k).getInternal());
            }
        }
        return filtered;
    }

    public static <E> Set<Activatable<E>> from(Set<E> set) {
        Set<Activatable<E>> a = new HashSet<Activatable<E>>(set.size());
        for (E t : set) {
            a.add(from(t));
        }
        return a;
    }

    public static <T> Activatable<T> from(T t) {
        return from(t, true);
    }

    public static <T> Activatable<T> from(T t, boolean active) {
        return new Activatable<T>(t, active);
    }
}
