/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.SosServiceProvider;

/**
 * Thread safe producer that creates a object only if it is null or if it should
 * be recreated explicitly.
 * 
 * @param <T>
 *            the type to produce
 * 
 * @since 4.0.0
 * @author Christian Autermann <c.autermann@52north.org>
 */
public abstract class LazyThreadSafeProducer<T> implements Producer<T> {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private T object;

    private boolean recreate = false;

    private void recreate() throws ConfigurationException {
        this.object = create();
        this.recreate = false;
    }

    private boolean shouldRecreate() {
        return this.object == null || this.recreate;
    }

    protected void setRecreate() {
        lock.writeLock().lock();
        try {
            this.recreate = true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public T get() throws ConfigurationException {
        lock.readLock().lock();
        try {
            if (!shouldRecreate()) {
                return this.object;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            if (shouldRecreate()) {
                recreate();
            }
            return this.object;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public T get(String identification) {
        return get();
    }

    protected abstract T create() throws ConfigurationException;
}
