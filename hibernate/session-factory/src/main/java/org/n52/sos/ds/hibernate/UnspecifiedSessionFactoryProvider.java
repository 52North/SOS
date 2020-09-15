/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import java.util.Properties;

import javax.inject.Inject;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.n52.faroe.ConfigurationError;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.ds.DataConnectionProvider;
import org.n52.iceland.ds.Datasource;
import org.n52.iceland.ds.DatasourceCallback;
import org.n52.iceland.service.DatabaseSettingsHandler;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.service.DriverCleanupListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UnspecifiedSessionFactoryProvider
        extends AbstractSessionFactoryProvider
        implements DataConnectionProvider,
                   HibernateDatasourceConstants,
                   Constructable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryProvider.class);

    private static SessionFactory sessionFactory;
    private static Configuration configuration;
    private static StandardServiceRegistry serviceRegistry;

    private DriverCleanupListener driverCleanupListener;
    private DatabaseSettingsHandler databaseSettingsHandler;

    @Inject
    public void setDriverCleanupListener(DriverCleanupListener driverCleanupListener) {
        this.driverCleanupListener = driverCleanupListener;
    }

    @Inject
    public void setDatabaseSettingsHandler(DatabaseSettingsHandler databaseSettingsHandler) {
        this.databaseSettingsHandler = databaseSettingsHandler;
    }

    protected Configuration getConfiguration() {
        return configuration;
    }

    protected ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    protected abstract Configuration getConfiguration(Properties properties);

    @Override
    protected SessionFactory getSessionFactory() {
        return UnspecifiedSessionFactoryProvider.sessionFactory;
    }

    @Override
    public Session getConnection() throws ConnectionProviderException {
        try {
            if (sessionFactory == null) {
                return null;
            }
            Session session = sessionFactory.openSession();
            session.setCacheMode(CacheMode.IGNORE);
            session.setHibernateFlushMode(FlushMode.COMMIT);
            return session;
        } catch (HibernateException he) {
            String exceptionText = "Error while getting connection!";
            LOGGER.error(exceptionText, he);
            ConnectionProviderException cpe = new ConnectionProviderException(exceptionText, he);
            throw cpe;
        }

    }

    @Override
    public void returnConnection(Object connection) {
        try {
            if (connection instanceof Session) {
                Session session = (Session) connection;
                if (session.isOpen()) {
                    session.clear();
                    session.close();
                }
            }
        } catch (HibernateException he) {
            LOGGER.error("Error while returning connection!", he);
        }
    }

    protected DatasourceCallback getDatasourceCallback(Properties properties) {
        if (properties.containsKey(Datasource.class.getName())) {
            try {
                Class<?> c = Class.forName((String) properties.get(Datasource.class.getName()));
                Datasource datasource = (Datasource) c.newInstance();
                DatasourceCallback callback = datasource.getCallback();
                if (callback != null) {
                    return callback;
                }
            } catch (ClassNotFoundException |
                     InstantiationException |
                     IllegalAccessException ex) {
                LOGGER.warn("Error instantiating Datasource", ex);
            }
        }
        return DatasourceCallback.nullCallback();
    }

    @Override
    public void init() {
        String value = this.databaseSettingsHandler.get(HibernateDatasourceConstants.PROVIDED_JDBC);
        if (driverCleanupListener != null && (value == null || value.equals("true"))) {
            driverCleanupListener.addDriverClass(this.databaseSettingsHandler
                    .get(HibernateDatasourceConstants.HIBERNATE_DRIVER_CLASS));
        }
        this.initialize(this.databaseSettingsHandler.getAll());
    }

    private void initialize(Properties properties) throws ConfigurationError {

        final DatasourceCallback datasourceCallback = getDatasourceCallback(properties);
        datasourceCallback.onInit(properties);
        try {
            LOGGER.debug("Instantiating configuration and session factory");
            configuration = getConfiguration(properties);
            configuration.mergeProperties(properties);
            UnspecifiedSessionFactoryProvider.serviceRegistry =
                    new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            UnspecifiedSessionFactoryProvider.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            Session s = UnspecifiedSessionFactoryProvider.sessionFactory.openSession();
            try {
                HibernateMetadataCache.init(s);
                s.doWork(datasourceCallback::onFirstConnection);
            } finally {
                returnConnection(s);
            }
        } catch (HibernateException he) {
            String exceptionText = "An error occurs during instantiation of the database connection pool!";
            LOGGER.error(exceptionText, he);
            destroy();
            throw new ConfigurationError(exceptionText, he);
        }
    }

}
