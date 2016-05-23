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
package org.n52.sos.config.sqlite;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.util.Cleanupable;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class SQLiteManager implements Cleanupable {
    private ConnectionProvider connectionProvider;

    public ConnectionProvider getConnectionProvider() {
        synchronized (this) {
            if (!isSetConnectionProvider()) {
                this.connectionProvider = createDefaultConnectionProvider();
            }
        }
        return connectionProvider;
    }

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        synchronized (this) {
            this.connectionProvider = connectionProvider;
        }
    }

    protected boolean isSetConnectionProvider() {
        return this.connectionProvider != null;
    }

    @Override
    public void cleanup() {
        synchronized (this) {
            if (this.connectionProvider != null) {
                this.connectionProvider.cleanup();
            }
        }
    }

    public <T> T execute(HibernateAction<T> action)
            throws ConnectionProviderException {
        synchronized (this) {
            Session session = null;
            Transaction transaction = null;
            try {
                session = (Session) getConnectionProvider().getConnection();
                transaction = session.beginTransaction();
                T result = action.call(session);
                session.flush();
                transaction.commit();
                return result;
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            } catch (ConnectionProviderException cpe) {
                throw cpe;
            } finally {
                getConnectionProvider().returnConnection(session);
            }
        }
    }

    public <T> T execute(final ThrowingHibernateAction<T> action)
            throws Exception {
        synchronized (this) {
            Session session = null;
            Transaction transaction = null;
            try {
                session = (Session) getConnectionProvider().getConnection();
                transaction = session.beginTransaction();
                final T result = action.call(session);
                session.flush();
                transaction.commit();
                return result;
            } catch (ConnectionProviderException cpe) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException(cpe);
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            } finally {
                getConnectionProvider().returnConnection(session);
            }
        }
    }

    protected abstract ConnectionProvider createDefaultConnectionProvider();

    public static interface HibernateAction<T> {
        T call(Session session);
    }

    public static abstract class VoidHibernateAction
            implements HibernateAction<Void> {
        @Override
        public Void call(Session session) {
            run(session);
            return null;
        }

        protected abstract void run(Session session);
    }

    public static interface ThrowingHibernateAction<T> {
        T call(Session session)
                throws Exception;
    }

    public static abstract class ThrowingVoidHibernateAction
            implements ThrowingHibernateAction<Void> {
        @Override
        public Void call(final Session session)
                throws Exception {
            run(session);
            return null;
        }

        protected abstract void run(Session session)
                throws Exception;
    }

}
