package org.n52.sos.inspire.aqd.persistence;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.config.sqlite.SQLiteSessionFactory;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.hibernate.AbstractSessionFactoryProvider;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.service.SosContextListener;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ReportingHeaderSQLiteSessionFactory extends AbstractSessionFactoryProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SQLiteSessionFactory.class);

    protected static final String DEFAULT_DATABASE_NAME = "eReportingHeader";

    private final Properties defaultProperties = new Properties() {
        private static final long serialVersionUID = 3109256773218160485L;
        {
            put(SQLiteSessionFactory.HIBERNATE_CONNECTION_URL, getFilename());
            put(SQLiteSessionFactory.HIBERNATE_UPDATE_SCHEMA, SQLiteSessionFactory.UPDATE_SCHEMA_VALUE);
            put(SQLiteSessionFactory.HIBERNATE_DIALECT, SQLiteSessionFactory.SQLITE_HIBERNATE_DIALECT);
            put(SQLiteSessionFactory.HIBERNATE_CONNECTION_DRIVER_CLASS, SQLiteSessionFactory.SQLITE_JDBC_DRIVER);
            put(SQLiteSessionFactory.HIBERNATE_CONNECTION_USERNAME, SQLiteSessionFactory.EMPTY);
            put(SQLiteSessionFactory.HIBERNATE_CONNECTION_PASSWORD, SQLiteSessionFactory.EMPTY);
            put(SQLiteSessionFactory.HIBERNATE_CONNECTION_POOL_SIZE, String.valueOf(SQLiteSessionFactory.SQLITE_CONNECTION_POOL_SIZE));
            put(SQLiteSessionFactory.HIBERNATE_CONNECTION_RELEASE_MODE, SQLiteSessionFactory.RELEASE_MODE_AFTER_TRANSACTION);
            put(SQLiteSessionFactory.HIBERNATE_CURRENT_SESSION_CONTEXT, SQLiteSessionFactory.THREAD_LOCAL_SESSION_CONTEXT);
        }
    };


    private final ReentrantLock lock = new ReentrantLock();

    private SessionFactory sessionFactory;

    private String getFilename() {
        String path = null;
        try {
            path = SosContextListener.getPath();
        } catch (Throwable t) {
        }
        if (path == null) {
            path = System.getProperty("user.dir");
            LOG.warn("Context path is not set; using {} instead", path);
        } else {
            path = new File(path).getAbsolutePath();
        }
        path = path + File.separator + DEFAULT_DATABASE_NAME;
        return String.format(SQLiteSessionFactory.CONNECTION_URL_TEMPLATE, path);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        lock.lock();
        try {
            if (this.sessionFactory == null) {
                this.sessionFactory = createSessionFactory(null);
            }
        } finally {
            lock.unlock();
        }
        return this.sessionFactory;
    }

    private SessionFactory createSessionFactory(Properties properties) {
        Configuration cfg = new Configuration()
                .addAnnotatedClass(JSONFragment.class);
        if (properties != null) {
            cfg.mergeProperties(properties);
        }
        cfg.mergeProperties(defaultProperties);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties()).build();
        return cfg.buildSessionFactory(serviceRegistry);
    }

    @Override
    public Session getConnection() throws ConnectionProviderException {
        try {
            return getSessionFactory().getCurrentSession();
        } catch (HibernateException e) {
            throw new ConnectionProviderException(e);
        }
    }

    @Override
    public void returnConnection(Object connection) {
    }

    @Override
    public void initialize(Properties properties) throws ConfigurationException {
        lock.lock();
        try {
            this.sessionFactory = createSessionFactory(properties);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getConnectionProviderIdentifier() {
        return "SQLiteHibernateReportingHeader";
    }
}
