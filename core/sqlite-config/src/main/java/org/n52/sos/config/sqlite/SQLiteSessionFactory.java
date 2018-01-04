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

import java.io.File;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.Stoppable;
import org.n52.faroe.ConfigurationError;
import org.n52.janmayen.ConfigLocationProvider;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.janmayen.lifecycle.Destroyable;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.sos.config.sqlite.hibernate.HibernateFileType;
import org.n52.sos.config.sqlite.hibernate.HibernateSQLiteDialect;
import org.n52.sos.config.sqlite.hibernate.HibernateTimeInstantType;
import org.n52.sos.config.sqlite.hibernate.HibernateUriType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class SQLiteSessionFactory implements Constructable, Destroyable {
    private static final Logger LOG = LoggerFactory
            .getLogger(SQLiteSessionFactory.class);

    public static final String HIBERNATE_DIALECT = "hibernate.dialect";
    public static final String HIBERNATE_CONNECTION_URL
            = "hibernate.connection.url";
    public static final String HIBERNATE_CONNECTION_DRIVER_CLASS
            = "hibernate.connection.driver_class";
    public static final String HIBERNATE_UPDATE_SCHEMA
            = "hibernate.hbm2ddl.auto";
    public static final String HIBERNATE_CONNECTION_USERNAME
            = "hibernate.connection.username";
    public static final String HIBERNATE_CONNECTION_PASSWORD
            = "hibernate.connection.password";
    public static final String HIBERNATE_CONNECTION_POOL_SIZE
            = "hibernate.connection.pool_size";
    public static final String HIBERNATE_CONNECTION_RELEASE_MODE
            = "hibernate.connection.release_mode";
    public static final String HIBERNATE_CURRENT_SESSION_CONTEXT
            = "hibernate.current_session_context_class";
    public static final String RELEASE_MODE_AFTER_TRANSACTION
            = "after_transaction";
    public static final String RELEASE_MODE_AFTER_STATEMENT = "after_statement";
    public static final String RELEASE_MODE_ON_CLOSE = "on_close";
    public static final String RELEASE_MODE_AUTO = "auto";
    public static final String THREAD_LOCAL_SESSION_CONTEXT = "thread";
    public static final int SQLITE_CONNECTION_POOL_SIZE = 1;
    public static final String CONNECTION_URL_TEMPLATE = "jdbc:sqlite:%s";
    public static final String SQLITE_HIBERNATE_DIALECT
            = HibernateSQLiteDialect.class.getName();
    public static final String UPDATE_SCHEMA_VALUE = "update";
    public static final String VALIDATE_SCHEMA_VALUE = "validate";
    public static final String CREATE_SCHEMA_VALUE = "create";
    public static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
    public static final String EMPTY = "";
    public static final String DEFAULT_DATABASE_NAME = "configuration";

    private final ReentrantLock lock = new ReentrantLock();

    @Inject
    private ConfigLocationProvider configLocationProvider;
    private String databaseName = DEFAULT_DATABASE_NAME;
    private String path;
    private SessionFactory sessionFactory;
    private Class<?>[] annotatedClasses;
    private Properties properties;

    public void setPath(String path) {
        this.path = path;
    }

    private String getPath() {
        return this.path == null ? this.configLocationProvider.get() : this.path;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setAnnotatedClasses(Class<?>[] annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    public Class<?>[] getAnnotatedClasses() {
        return annotatedClasses;
    }

    protected String getConnectionURL() {
        return String.format(CONNECTION_URL_TEMPLATE, getFile().getAbsolutePath());
    }

    protected File getFile() {
        return new File(getPath(), getDatabaseName() + ".db");
    }

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

    @Override
    public void destroy() {
        lock.lock();
        try {
            if (this.sessionFactory != null) {
                try {
                    if (this.sessionFactory instanceof SessionFactoryImpl) {
                        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) this.sessionFactory;
//                        if (sessionFactoryImpl.getConnectionProvider() instanceof Stoppable) {
//                            Stoppable stoppable = (Stoppable) sessionFactoryImpl.getConnectionProvider();
//                            stoppable.stop();
//                        }
                    }
                    this.sessionFactory.close();
                    LOG.info("Connection provider closed successfully!");
                } catch (HibernateException he) {
                    LOG.error("Error while closing connection provider!", he);
                }
            }
        } finally {
            sessionFactory = null;
            lock.unlock();
        }
    }

    @Override
    public void init() {
        this.sessionFactory = createSessionFactory(getProperties());
    }

    private SessionFactory createSessionFactory(Properties properties) {
        Configuration cfg = new Configuration();

        for (Class<?> clazz : getAnnotatedClasses()) {
            cfg.addAnnotatedClass(clazz);
        }

        cfg.registerTypeOverride(new HibernateFileType(),
                                 new String[] { "file", File.class.getName() });
        cfg.registerTypeOverride(new HibernateUriType(),
                                 new String[] { "uri", URI.class.getName() });
        cfg.registerTypeOverride(new HibernateTimeInstantType(),
                                 new String[] { "timeInstant", TimeInstant.class.getName() });

        if (properties != null) {
            cfg.mergeProperties(properties);
        }
        cfg.mergeProperties(getDefaultProperties());
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties()).build();
        return cfg.buildSessionFactory(serviceRegistry);
    }

    private Properties getDefaultProperties() {
        final String updateSchemaValue = UPDATE_SCHEMA_VALUE;
//        if (getFile().exists()) {
//            updateSchemaValue = VALIDATE_SCHEMA_VALUE;
//        } else {
//            updateSchemaValue = CREATE_SCHEMA_VALUE;
//        }

        return new Properties() {
            private static final long serialVersionUID = 3109256773218160485L;
            {
                put(HIBERNATE_CONNECTION_URL, getConnectionURL());
                put(HIBERNATE_UPDATE_SCHEMA, updateSchemaValue);
                put(HIBERNATE_DIALECT, SQLITE_HIBERNATE_DIALECT);
                put(HIBERNATE_CONNECTION_DRIVER_CLASS, SQLITE_JDBC_DRIVER);
                put(HIBERNATE_CONNECTION_USERNAME, EMPTY);
                put(HIBERNATE_CONNECTION_PASSWORD, EMPTY);
                put(HIBERNATE_CONNECTION_POOL_SIZE, String.valueOf(SQLITE_CONNECTION_POOL_SIZE));
                put(HIBERNATE_CONNECTION_RELEASE_MODE, RELEASE_MODE_AFTER_TRANSACTION);
                put(HIBERNATE_CURRENT_SESSION_CONTEXT, THREAD_LOCAL_SESSION_CONTEXT);
            }
        };
    }

    public Session getConnection() {
        try {
            return getSessionFactory().getCurrentSession();
        } catch (HibernateException e) {
            throw new ConfigurationError(e);
        }
    }

    public void returnConnection(Session session) {

    }
}
