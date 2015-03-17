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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.DataConnectionProvider;
import org.n52.sos.ds.Datasource;
import org.n52.sos.ds.DatasourceCallback;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.type.UtcTimestampType;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UnspecifiedSessionFactoryProvider extends AbstractSessionFactoryProvider implements DataConnectionProvider,
HibernateDatasourceConstants {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryProvider.class);

    /**
     * SessionFactory instance
     */
    protected SessionFactory sessionFactory = null;

    /**
     * Configuration instance
     */
    protected Configuration configuration = null;
    
    /*
     * (non-Javadoc)
     *
     * @see org.n52.sos.ds.ConnectionProvider#getConnection()
     */
    @Override
    public Session getConnection() throws ConnectionProviderException {
        try {
            if (sessionFactory == null) {
                return null;
            }
            return sessionFactory.openSession();
        } catch (HibernateException he) {
            String exceptionText = "Error while getting connection!";
            LOGGER.error(exceptionText, he);
            ConnectionProviderException cpe = new ConnectionProviderException(exceptionText, he);
            throw cpe;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.n52.sos.ds.ConnectionProvider#returnConnection(java.lang.Object)
     */
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
                Class<?> c = Class.forName((String) properties
                        .get(Datasource.class.getName()));
                Datasource datasource = (Datasource) c.newInstance();
                DatasourceCallback callback = datasource.getCallback();
                if (callback != null) {
                    return callback;
                }
            } catch (ClassNotFoundException ex) {
                LOGGER.warn("Error instantiating Datasource", ex);
            } catch (InstantiationException ex) {
                LOGGER.warn("Error instantiating Datasource", ex);
            } catch (IllegalAccessException ex) {
                LOGGER.warn("Error instantiating Datasource", ex);
            }
        }
        return DatasourceCallback.nullCallback();
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
    
    @Override
    public void initialize(Properties properties) throws ConfigurationException {
        final DatasourceCallback datasourceCallback
                = getDatasourceCallback(properties);
        datasourceCallback.onInit(properties);
        try {
            LOGGER.debug("Instantiating configuration and session factory");
            configuration = getConfiguration(properties);
            configuration.mergeProperties(properties);

            // set timestamp mapping to a special type to ensure time is always
            // queried in UTC
            configuration.registerTypeOverride(new UtcTimestampType());
            ServiceRegistry serviceRegistry =
                    new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            Session s = this.sessionFactory.openSession();
            try {
                HibernateMetadataCache.init(s);
                s.doWork(new Work() {
                    @Override
                    public void execute(Connection connection)
                            throws SQLException {
                        datasourceCallback.onFirstConnection(connection);
                    }
                });
            } finally {
                returnConnection(s);
            }
        } catch (HibernateException he) {
            String exceptionText = "An error occurs during instantiation of the database connection pool!";
            LOGGER.error(exceptionText, he);
            cleanup();
            throw new ConfigurationException(exceptionText, he);
        }
    }
    
    protected abstract Configuration getConfiguration(Properties properties);

}
