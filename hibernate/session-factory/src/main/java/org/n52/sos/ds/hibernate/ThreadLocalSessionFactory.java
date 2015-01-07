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
package org.n52.sos.ds.hibernate;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.Session;
import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ThreadLocalSessionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalSessionFactory.class);

    private final ConnectionProvider connectionProvider;

    private final Lock lock = new ReentrantLock();

    private final Set<Session> createdSessions = CollectionHelper.synchronizedSet();

    private boolean closed = false;

    private ThreadLocal<Session> threadLocal = new ThreadLocal<Session>() {
        @Override
        protected Session initialValue() {
            try {
                return (Session) getConnectionProvider().getConnection();
            } catch (ConnectionProviderException cpe) {
                LOGGER.error("Error while getting initialValue for ThreadLocalSessionFactory!", cpe);
            }
            return null;
        }
    };

    public ThreadLocalSessionFactory(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Session getSession() {
        lock.lock();
        try {
            if (isClosed()) {
                throw new IllegalStateException("factory already closed");
            }
            Session s = this.threadLocal.get();
            getCreatedSessions().add(s);
            return s;
        } finally {
            lock.unlock();
        }
    }

    public void close() throws ConnectionProviderException {
        setClosed();
        returnSessions();
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    protected Set<Session> getCreatedSessions() {
        return createdSessions;
    }

    protected void setClosed() {
        lock.lock();
        try {
            closed = true;
        } finally {
            lock.unlock();
        }
    }

    protected boolean isClosed() {
        lock.lock();
        try {
            return closed;
        } finally {
            lock.unlock();
        }
    }

    protected void returnSessions() throws ConnectionProviderException {
        for (Session s : getCreatedSessions()) {
            getConnectionProvider().returnConnection(s);
        }
    }
}
