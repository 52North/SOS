/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.cache;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.series.db.HibernateSessionStore;
import org.n52.shetland.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class ThreadLocalSessionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalSessionFactory.class);
    private final HibernateSessionStore sessionStore;
    private final Lock lock = new ReentrantLock();
    private final Set<Session> createdSessions = CollectionHelper.synchronizedSet();
    private final ThreadLocal<Session> threadLocal;
    private boolean closed;

    public ThreadLocalSessionFactory(HibernateSessionStore sessionStore) {
        this.sessionStore = Objects.requireNonNull(sessionStore);
        this.threadLocal = ThreadLocal.withInitial(this::createConnection);
    }

    private Session createConnection() {
        try {
            return this.sessionStore.getSession();
        } catch (HibernateException he) {
            LOGGER.error("Error while getting initialValue for ThreadLocalSessionFactory!", he);
            throw new RuntimeException(he);
        }
    }

    public Session getSession() {
        lock.lock();
        try {
            if (isClosed()) {
                throw new IllegalStateException("factory already closed");
            }
            Session session = this.threadLocal.get();
            this.createdSessions.add(session);
            return session;
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        setClosed();
        returnSessions();
    }

    protected void setClosed() {
        this.lock.lock();
        try {
            this.closed = true;
        } finally {
            this.lock.unlock();
        }
    }

    protected boolean isClosed() {
        this.lock.lock();
        try {
            return this.closed;
        } finally {
            this.lock.unlock();
        }
    }

    protected void returnSessions() {
        this.lock.lock();
        try {
            this.createdSessions
                .forEach(this.sessionStore::returnSession);
        } catch (Exception e) {
            LOGGER.error("Error while returning connection after cache update!", e);
        } finally {
            this.lock.unlock();
        }
    }
}
