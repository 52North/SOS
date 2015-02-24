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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Abstract implementation that delegates to a existing map implementation.
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
 */
public abstract class AbstractDelegatingMultiMap<K, V, C extends Collection<V>> implements MultiMap<K, V, C> {
    private static final long serialVersionUID = 7065617676463608631L;

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getDelegate().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getDelegate().containsValue(value);
    }

    @Override
    public C get(Object key) {
        return getDelegate().get(key);
    }

    @Override
    public C put(K key, C value) {
        return getDelegate().put(key, value);
    }

    @Override
    public C add(K key, V value) {
        C c = get(key);
        if (c == null) {
            c = newCollection();
            put(key, c);
        }
        c.add(value);
        return c;
    }

    @Override
    public C remove(Object key) {
        return getDelegate().remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object k, Object v) {
        C c = get((K) k);
        if (c != null) {
            return c.remove((V) v);
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends C> m) {
        getDelegate().putAll(m);
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public Set<K> keySet() {
        return getDelegate().keySet();
    }

    @Override
    public Collection<C> values() {
        return getDelegate().values();
    }

    @Override
    public C allValues() {
        C c = newCollection();
        for (C v : values()) {
            c.addAll(v);
        }
        return c;
    }

    @Override
    public Set<Entry<K, C>> entrySet() {
        return getDelegate().entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return getDelegate().equals(o);
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }

    @Override
    public boolean containsCollectionValue(V v) {
        for (C c : values()) {
            if (c.contains(v)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(K key, Iterable<V> value) {
        boolean altered = false;
        C c = get(key);
        if (c != null) {
            for (V v : value) {
                if (c.remove(v)) {
                    altered = true;
                }
            }
        }
        return altered;
    }

    @Override
    public boolean hasValues(K key) {
        return containsKey(key) && !get(key).isEmpty();
    }

    @Override
    public C addAll(K key, Collection<? extends V> values) {
        C c = get(key);
        if (c == null) {
            c = newCollection();
            put(key, c);
        }
        c.addAll(values);
        return c;
    }

    @Override
    public boolean removeWithKey(K key, V value) {
        boolean altered = remove(key, value);
        if (!hasValues(key)) {
            remove(key);
        }
        return altered;
    }

    @Override
    public boolean removeWithKey(K key, Iterable<V> value) {
        boolean altered = remove(key, value);
        if (!hasValues(key)) {
            remove(key);
        }
        return altered;
    }

    /**
     * @return the delegate to operate on
     */
    protected abstract Map<K, C> getDelegate();

    /**
     * @return a new collection to save values
     */
    protected abstract C newCollection();
}
