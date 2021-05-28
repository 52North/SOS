/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

    @SuppressWarnings("unchecked")
    @Override
    protected Configuration getConfiguration(Properties properties) throws ConfigurationError {
        try {
            Configuration configuration = new Configuration().configure("/hibernate.cfg.xml");
            if (properties.containsKey(HibernateConstants.C3P0_MAX_SIZE)) {
                this.maxConnections = Integer.parseInt(properties.getProperty(HibernateConstants.C3P0_MAX_SIZE, "-1"));
            }
            if (properties.containsKey(HIBERNATE_RESOURCES)) {
                List<String> resources = (List<String>) properties.get(HIBERNATE_RESOURCES);
                for (String resource : resources) {
                    configuration.addURL(SessionFactoryProvider.class.getResource(resource));
                }
                properties.remove(HIBERNATE_RESOURCES);
            } else if (properties.containsKey(HIBERNATE_DIRECTORY)) {
                String directories = (String) properties.get(HIBERNATE_DIRECTORY);
                for (String directory : directories.split(PATH_SEPERATOR)) {
                    File hibernateDir = new File(directory);
                    if (!hibernateDir.exists()) {
                        // try to configure from classpath (relative path)
                        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                        URL dirUrl = classLoader.getResource(directory);
                        if (dirUrl != null) {
                            try {
                                hibernateDir = new File(
                                        URLDecoder.decode(dirUrl.getPath(), Charset.defaultCharset().toString()));
                            } catch (UnsupportedEncodingException e) {
                                throw new ConfigurationError("Unable to encode directory URL " + dirUrl + "!");
                            }
                        }
                    }
                    if (!hibernateDir.exists()) {
                        throw new ConfigurationError("Hibernate directory " + directory + " doesn't exist!");
                    }
                    configuration.addDirectory(hibernateDir);
                }
            } else {
                // keep this as default/fallback
                configuration
                        .addDirectory(new File(getClass().getResource(HIBERNATE_MAPPING_SIMPLE_CORE_PATH).toURI()));
                configuration
                        .addDirectory(new File(getClass().getResource(HIBERNATE_MAPPING_SIMPLE_DATASET_PATH).toURI()));
            }
            return configuration;
        } catch (HibernateException | URISyntaxException he) {
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
