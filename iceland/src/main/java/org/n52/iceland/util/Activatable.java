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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.MoreObjects;
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
        return MoreObjects.toStringHelper(getClass()).add("object", getInternal()).add("active", isActive()).toString();
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
