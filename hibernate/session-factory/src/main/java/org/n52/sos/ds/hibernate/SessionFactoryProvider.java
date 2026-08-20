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
package org.n52.sos.ds.hibernate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.n52.faroe.ConfigurationError;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.ds.UpdateableConnectionProvider;
import org.n52.sos.ds.hibernate.util.EntityScanner;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Implementation of the SessionFactory.
 *
 * @since 4.0.0
 */
public class SessionFactoryProvider extends UnspecifiedSessionFactoryProvider implements UpdateableConnectionProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryProvider.class);

    private int maxConnections;

    @Override
    public String getUpdateScript() throws ConnectionProviderException {
        Configuration configuration = getConfiguration();
        if (configuration == null) {
            throw new ConfigurationError("configuration is null");
        }
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory == null) {
            throw new ConfigurationError("sessionFactory is null");
        }
        Dialect dialect = ((SessionFactoryImplementor) sessionFactory).getServiceRegistry()
                .getService(JdbcServices.class).getDialect();
        if (dialect == null) {
            throw new ConfigurationError("dialect is null");
        }
        Session session = getConnection();

        Path createTempFile = null;
        try {
            returnConnection(session);
            StringBuilder updateSqlString = new StringBuilder();
            for (String sqlLine : Files.readAllLines(createTempFile)) {
                updateSqlString.append(sqlLine).append(";\n\n");
            }
            return updateSqlString.toString();
        } catch (IOException e) {
            throw new ConnectionProviderException("Error while creating update script!", e);
        } finally {
            try {
                if (createTempFile != null) {
                    Files.deleteIfExists(createTempFile);
                }
            } catch (IOException e) {
                LOGGER.info("Unable to delete temp file {}", createTempFile.toString());
            }
        }
    }

    @Override
    public boolean supportsUpdateScript() {
        return true;
    }

    @Override
    protected Configuration getConfiguration(Properties properties) throws ConfigurationError {
        try {
            if (properties.containsKey(HIBERNATE_DIRECTORY)) {
                LOGGER.warn("Datasource property '{}' is no longer used; every @Entity in '{}' is now mapped "
                        + "unconditionally. Please remove it from your datasource.properties.", HIBERNATE_DIRECTORY,
                        ENTITY_PACKAGE);
            }
            Configuration configuration = new Configuration();
            configuration.configure("/hibernate.cfg.xml");
            if (properties.containsKey(HibernateConstants.C3P0_MAX_SIZE)) {
                this.maxConnections = Integer.parseInt(properties.getProperty(HibernateConstants.C3P0_MAX_SIZE, "-1"));
            }
            EntityScanner.applyTo(configuration, ENTITY_PACKAGE);
            if (DatabaseConcept.PROXY.name().equals(properties.getProperty(DATABASE_CONCEPT_KEY))) {
                configuration.addResource(HIBERNATE_ORM_PROFILE_PROXY);
            }
            if (FeatureConcept.EXTENDED_FEATURE_CONCEPT.name().equals(properties.getProperty(FEATURE_CONCEPT_KEY))) {
                configuration.addResource(HIBERNATE_ORM_FEATURE_MONITORING_POINT);
            }
            return configuration;
        } catch (HibernateException he) {
            String exceptionText = "An error occurs during instantiation of the database connection pool!";
            LOGGER.error(exceptionText, he);
            destroy();
            throw new ConfigurationError(exceptionText, he);
        }
    }

    @Override
    public int getMaxConnections() {
        return maxConnections;
    }
}
