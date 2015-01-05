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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract implementation that delegates to synchronized {@link HashMap}
 *
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @param <C>
 *            the collection type
 *
 * @see Collections#synchronizedMap(java.util.Map)
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 *
 */
public abstract class AbstractSynchronizedMultiMap<K, V, C extends Collection<V>> extends
        AbstractDelegatingMultiMap<K, V, C> {
    private static final long serialVersionUID = -805751685536396275L;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock read = lock.readLock();

    private final Lock write = lock.writeLock();

    private final Map<K, C> delegate;

    public AbstractSynchronizedMultiMap(Map<? extends K, ? extends C> m) {
        this.delegate = new HashMap<K, C>(m);
    }

    public AbstractSynchronizedMultiMap(int initialCapacity) {
        this.delegate = new HashMap<K, C>(initialCapacity);
    }

    public AbstractSynchronizedMultiMap(int initialCapacity, float loadFactor) {
        this.delegate = new HashMap<K, C>(initialCapacity, loadFactor);
    }

    public AbstractSynchronizedMultiMap() {
        this.delegate = new HashMap<K, C>();
    }

    @Override
    protected Map<K, C> getDelegate() {
        return this.delegate;
    }

    @Override
    public int size() {
        read.lock();
        try {
            return super.size();
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        read.lock();
        try {
            return super.isEmpty();
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        read.lock();
        try {
            return super.containsKey(key);
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        read.lock();
        try {
            return super.containsValue(value);
        } finally {
            read.unlock();
        }
    }

    @Override
    public C get(Object key) {
        read.lock();
        try {
            return super.get(key);
        } finally {
            read.unlock();
        }
    }

    @Override
    public C put(K key, C value) {
        write.lock();
        try {
            return super.put(key, value);
        } finally {
            write.unlock();
        }
    }

    @Override
    public C add(K key, V value) {
        write.lock();
        try {
            return super.add(key, value);
        } finally {
            write.unlock();
        }
    }

    @Override
    public C remove(Object key) {
        write.lock();
        try {
            return super.remove(key);
        } finally {
            write.unlock();
        }
    }

    @Override
    public boolean remove(Object k, Object v) {
        write.lock();
        try {
            return super.remove(k, v);
        } finally {
            write.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends C> m) {
        write.lock();
        try {
            super.putAll(m);
        } finally {
            write.unlock();
        }
    }

    @Override
    public void clear() {
        write.lock();
        try {
            super.clear();
        } finally {
            write.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        read.lock();
        try {
            return super.keySet();
        } finally {
            read.unlock();
        }
    }

    @Override
    public Collection<C> values() {
        read.lock();
        try {
            return super.values();
        } finally {
            read.unlock();
        }
    }

    @Override
    public C allValues() {
        read.lock();
        try {
            return super.allValues();
        } finally {
            read.unlock();
        }
    }

    @Override
    public Set<Entry<K, C>> entrySet() {
        read.lock();
        try {
            return super.entrySet();
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        read.lock();
        try {
            return super.equals(o);
        } finally {
            read.unlock();
        }
    }

    @Override
    public int hashCode() {
        read.lock();
        try {
            return super.hashCode();
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean containsCollectionValue(V v) {
        read.lock();
        try {
            return super.containsCollectionValue(v);
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean remove(K key, Iterable<V> value) {
        write.lock();
        try {
            return super.remove(key, value);
        } finally {
            write.unlock();
        }
    }

    @Override
    public boolean hasValues(K key) {
        read.lock();
        try {
            return super.hasValues(key);
        } finally {
            read.unlock();
        }
    }

    @Override
    public C addAll(K key, Collection<? extends V> values) {
        write.lock();
        try {
            return super.addAll(key, values);
        } finally {
            write.unlock();
        }
    }

    @Override
    public boolean removeWithKey(K key, V value) {
        write.lock();
        try {
            return super.removeWithKey(key, value);
        } finally {
            write.unlock();
        }
    }

    @Override
    public boolean removeWithKey(K key, Iterable<V> value) {
        write.lock();
        try {
            return super.removeWithKey(key, value);
        } finally {
            write.unlock();
        }
    }
}
