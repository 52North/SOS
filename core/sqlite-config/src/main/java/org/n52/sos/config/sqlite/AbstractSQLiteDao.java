package org.n52.sos.config.sqlite;

import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.sos.config.sqlite.SQLiteSessionManager.HibernateAction;
import org.n52.sos.config.sqlite.SQLiteSessionManager.ThrowingHibernateAction;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class AbstractSQLiteDao {

    private SQLiteSessionManager manager;

    public void setManager(SQLiteSessionManager manager) {
        this.manager = manager;
    }

    protected <T> T execute(HibernateAction<T> action)
            throws ConnectionProviderException {
        return this.manager.execute(action);
    }

    protected <T> T throwingExecute(ThrowingHibernateAction<T> action)
            throws ConnectionProviderException, Exception {
        return this.manager.execute(action);
    }
}
