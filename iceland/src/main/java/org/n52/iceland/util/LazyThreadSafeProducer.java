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

import java.util.Locale;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.n52.iceland.exception.ConfigurationException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

public abstract class LazyThreadSafeProducer<T> implements Producer<T> {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final LoadingCache<Locale, T> cache = CacheBuilder.newBuilder()
            .build(new CacheLoader<Locale, T>() {
                @Override
                public T load(Locale key) {
                    return create(key);
                }
            });
    private T nullLocale = null;

    protected void setRecreate() {
        lock.writeLock().lock();
        try {
            this.nullLocale = create(null);
        } finally {
            lock.writeLock().unlock();
        }
        this.cache.invalidateAll();
    }

    @Override
    public T get()
            throws ConfigurationException {
        Locale l = null;
        return get(l);
    }

    @Override
    public T get(Locale language)
            throws ConfigurationException {
        if (language == null) {
            this.lock.readLock().lock();
            try {
                return this.nullLocale;
            } finally {
                this.lock.readLock().unlock();
            }
        } else {
            try {
                return this.cache.getUnchecked(language);
            } catch (UncheckedExecutionException ex) {
                if (ex.getCause() instanceof ConfigurationException) {
                    throw (ConfigurationException) ex.getCause();
                } else {
                    throw ex;
                }
            }
        }
    }
    
    @Override
    public T get(String identification) {
        return get();
    }

    protected abstract T create(Locale language)
            throws ConfigurationException;

}
