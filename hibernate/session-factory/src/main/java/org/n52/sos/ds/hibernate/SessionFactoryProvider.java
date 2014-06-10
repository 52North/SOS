/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.DataConnectionProvider;
import org.n52.sos.ds.Datasource;
import org.n52.sos.ds.DatasourceCallback;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.type.UtcTimestampType;
import org.n52.sos.exception.ConfigurationException;

/**
 *
 * Implementation of the SessionFactory.
 *
 * @since 4.0.0
 */
public class SessionFactoryProvider extends AbstractSessionFactoryProvider implements DataConnectionProvider,
        HibernateDatasourceConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryProvider.class);

    /**
     * SessionFactory instance
     */
    private SessionFactory sessionFactory = null;

    /**
     * Configuration instance
     */
    private Configuration configuration = null;

    /**
     * constructor. Opens a new Hibernate SessionFactory
     */
    public SessionFactoryProvider() {

    }

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

    // /*
    // * (non-Javadoc)
    // *
    // * @see org.n52.sos.ds.ConnectionProvider#cleanup()
    // */
    // @Override
    // public void cleanup() {
    // if (this.sessionFactory != null) {
    // try {
    // if (this.sessionFactory instanceof SessionFactoryImpl) {
    // SessionFactoryImpl sf = (SessionFactoryImpl) this.sessionFactory;
    // ConnectionProvider conn = sf.getConnectionProvider();
    // if (conn instanceof C3P0ConnectionProvider) {
    // ((C3P0ConnectionProvider) conn).close();
    // }
    // }
    // this.sessionFactory.close();
    // LOGGER.info("Connection provider closed successfully!");
    // } catch (HibernateException he) {
    // LOGGER.error("Error while closing connection provider!", he);
    // }
    // }
    // }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(Properties properties) throws ConfigurationException {
        final DatasourceCallback datasourceCallback
                = getDatasourceCallback(properties);
        datasourceCallback.onInit(properties);
        try {
            LOGGER.debug("Instantiating configuration and session factory");
            configuration = new Configuration().configure("/sos-hibernate.cfg.xml");
            if (properties.containsKey(HIBERNATE_RESOURCES)) {
                List<String> resources = (List<String>) properties.get(HIBERNATE_RESOURCES);
                for (String resource : resources) {
                    configuration.addResource(resource);
                }
                properties.remove(HIBERNATE_RESOURCES);
            } else if (properties.containsKey(HIBERNATE_DIRECTORY)) {
                String directories = (String) properties.get(HIBERNATE_DIRECTORY);
                for (String directory : directories.split(PATH_SEPERATOR)) {
                    File hibernateDir = new File(directory);
                    if (!hibernateDir.exists()) {
                        //try to configure from classpath (relative path)
                        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                        URL dirUrl = classLoader.getResource(directory);
                        if (dirUrl != null) {
                            try {
                                hibernateDir = new File(URLDecoder.decode(dirUrl.getPath(), Charset.defaultCharset().toString()));
                            } catch (UnsupportedEncodingException e) {
                                throw new ConfigurationException("Unable to encode directory URL " + dirUrl + "!");
                            }
                        }
                    }
                    if (!hibernateDir.exists()) {
                        throw new ConfigurationException("Hibernate directory " + directory + " doesn't exist!");
                    }
                    configuration.addDirectory(hibernateDir);
                }
            } else {
                // keep this as default/fallback
                configuration.addDirectory(new File(getClass().getResource(HIBERNATE_MAPPING_CORE_PATH).toURI()));
                configuration.addDirectory(new File(getClass().getResource(HIBERNATE_MAPPING_TRANSACTIONAL_PATH)
                        .toURI()));
                configuration.addDirectory(new File(getClass().getResource(HIBERNATE_MAPPING_SERIES_CONCEPT_OBSERVATION_PATH)
                        .toURI()));
                configuration.addDirectory(new File(getClass().getResource(HIBERNATE_MAPPING_SERIES_CONCEPT_SPATIAL_FILTERING_PROFILE_PATH)
                        .toURI()));
            }
            configuration.mergeProperties(properties);

            // set timestamp mapping to a special type to ensure time is always
            // queried in UTC

            configuration.registerTypeOverride(new UtcTimestampType());
            ServiceRegistry serviceRegistry =
                    new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            Session s = this.sessionFactory.openSession();
            try {
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
        } catch (URISyntaxException urise) {
            String exceptionText = "An error occurs during instantiation of the database connection pool!";
            LOGGER.error(exceptionText, urise);
            cleanup();
            throw new ConfigurationException(exceptionText, urise);
        }
    }

    private DatasourceCallback getDatasourceCallback(Properties properties) {
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

    public String getUpdateScript() throws ConnectionProviderException, SQLException {
        if (configuration == null) {
            throw new ConfigurationException("configuration is null");
        }
        if (sessionFactory == null) {
            throw new ConfigurationException("sessionFactory is null");
        }
        Dialect dialect = ((SessionFactoryImplementor) sessionFactory).getDialect();
        if (dialect == null) {
            throw new ConfigurationException("dialect is null");
        }
        Session session = getConnection();
        Connection conn = ((SessionImplementor) session).connection();
        DatabaseMetadata databaseMetadata = new DatabaseMetadata(conn, dialect);
        String[] udpateSql = configuration.generateSchemaUpdateScript(dialect, databaseMetadata);
        returnConnection(session);
        StringBuilder updateSqlString = new StringBuilder();
        for (String sqlLine : udpateSql) {
            updateSqlString.append(sqlLine + ";\n\n");
        }
        return updateSqlString.toString();
    }

    @Override
    public String getDatasourceIdentifier() {
        return HibernateDatasourceConstants.DATASOURCE_IDENTIFIER;
    }
}
