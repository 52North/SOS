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
