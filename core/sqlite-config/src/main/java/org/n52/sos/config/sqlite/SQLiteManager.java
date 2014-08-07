package org.n52.sos.config.sqlite;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.ConnectionProviderException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class SQLiteManager {
    private ConnectionProvider connectionProvider;

    protected ConnectionProvider getConnectionProvider() {
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
            } catch (final HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            } catch (final ConnectionProviderException cpe) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException(cpe);
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
