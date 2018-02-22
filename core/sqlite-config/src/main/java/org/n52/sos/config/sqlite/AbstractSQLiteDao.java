/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.config.sqlite;

import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.n52.janmayen.function.ThrowingConsumer;
import org.n52.janmayen.function.ThrowingFunction;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class AbstractSQLiteDao {

    private SQLiteSessionFactory sessionFactory;

    public void setSessionFactory(SQLiteSessionFactory connectionProvider) {
        this.sessionFactory = connectionProvider;
    }

    public SQLiteSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected boolean isSetSessionFactory() {
        return this.sessionFactory != null;
    }

    protected void execute(Consumer<Session> action) {
        execute((session) -> {
            action.accept(session);
            return null;
        });
    }

    protected <T> T execute(Function<Session, T> action) {
        synchronized (this) {
            Session session = null;
            Transaction transaction = null;
            try {
                session = getSessionFactory().getConnection();
                transaction = session.beginTransaction();
                T result = action.apply(session);
                session.flush();
                transaction.commit();
                return result;
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            } finally {
                getSessionFactory().returnConnection(session);
            }
        }
    }

    protected void throwingExecute(ThrowingConsumer<Session, ? extends Exception> action) throws Exception {
        throwingExecute((session) -> {
            action.accept(session);
            return null;
        });
    }

    protected <T> T throwingExecute(ThrowingFunction<Session, T, ? extends Exception> action) throws Exception {
        synchronized (this) {
            Session session = null;
            Transaction transaction = null;
            try {
                session = getSessionFactory().getConnection();
                transaction = session.beginTransaction();
                final T result = action.apply(session);
                session.flush();
                transaction.commit();
                return result;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            } finally {
                getSessionFactory().returnConnection(session);
            }
        }
    }

    @Deprecated
    public static interface HibernateAction<T> extends Function<Session, T> {

        default T call(Session session) {
            return apply(session);
        }
    }

    @Deprecated
    public static interface VoidHibernateAction extends Consumer<Session> {
        default void call(Session session) {
            accept(session);
        }
    }

    @Deprecated
    public static interface ThrowingHibernateAction<T> extends ThrowingFunction<Session, T, Exception> {
        default T call(Session session) throws Exception {
            return apply(session);
        }
    }

    @Deprecated
    public static interface ThrowingVoidHibernateAction extends ThrowingConsumer<Session, Exception> {
        default void run(Session session) throws Exception {
            accept(session);
        }
    }

}
