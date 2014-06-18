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
package org.n52.sos.ds.hibernate.ogm;

import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.ogm.cfg.OgmConfiguration;
import org.hibernate.ogm.datastore.mongodb.MongoDBDialect;
import org.hibernate.ogm.datastore.mongodb.MongoDBProperties;
import org.hibernate.ogm.dialect.NoopDialect;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.UnspecifiedSessionFactoryProvider;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OgmSessionFactoryProvider extends UnspecifiedSessionFactoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OgmSessionFactoryProvider.class);

    public OgmSessionFactoryProvider() {
    }

    // @Override
    // public void initialize(Properties properties) throws
    // ConfigurationException {
    // final DatasourceCallback datasourceCallback
    // = getDatasourceCallback(properties);
    // datasourceCallback.onInit(properties);
    // try {
    // LOGGER.debug("Instantiating configuration and session factory");
    // configuration = getConfiguration(properties);
    // ServiceRegistry serviceRegistry =
    // new
    // StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
    // this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    // Session s = this.sessionFactory.openSession();
    // try {
    // s.doWork(new Work() {
    // @Override
    // public void execute(Connection connection)
    // throws SQLException {
    // datasourceCallback.onFirstConnection(connection);
    // }
    // });
    // } finally {
    // returnConnection(s);
    // }
    // } catch (HibernateException he) {
    // String exceptionText =
    // "An error occurs during instantiation of the database connection pool!";
    // LOGGER.error(exceptionText, he);
    // cleanup();
    // throw new ConfigurationException(exceptionText, he);
    // }
    // }

    @SuppressWarnings("unchecked")
    @Override
    protected Configuration getConfiguration(Properties properties) {
        try {
            Configuration configuration = new OgmConfiguration().configure("/ogm-hibernate.cfg.xml");
            // transactional currently not required
            // configuration.setProperty(AvailableSettings.TRANSACTION_STRATEGY,
            // JtaTransactionFactory.class.getName());
            // configuration.setProperty(AvailableSettings.JTA_PLATFORM,
            // JBossStandAloneJtaPlatform.class.getName());
            // configuration.setProperty(MongoDBProperties.DATASTORE_PROVIDER,
            // (String)properties.get(MongoDBProperties.DATASTORE_PROVIDER));
            // configuration.setProperty(MongoDBProperties.HOST,
            // (String)properties.get(MongoDBProperties.HOST));
            // configuration.setProperty(MongoDBProperties.PORT,
            // (String)properties.get(MongoDBProperties.PORT));
            // configuration.setProperty(MongoDBProperties.DATABASE,
            // (String)properties.get(MongoDBProperties.DATABASE));
            configuration.setProperty(AvailableSettings.DIALECT, NoopDialect.class.getName());
            configuration.setProperty(MongoDBProperties.GRID_DIALECT, MongoDBDialect.class.getName());
            if (properties.containsKey(HIBERNATE_ANNOTADED_CLASSES)) {
                String annotadedClasses = properties.getProperty(HIBERNATE_ANNOTADED_CLASSES);
                for (String annotadedClass : annotadedClasses.split(Constants.COMMA_STRING)) {
                    configuration.addAnnotatedClass(Class.forName(annotadedClass));
                }
                properties.remove(HIBERNATE_ANNOTADED_CLASSES);
            }
            return configuration;
        } catch (HibernateException he) {
            String exceptionText = "An error occurs during instantiation of the database connection pool!";
            LOGGER.error(exceptionText, he);
            cleanup();
            throw new ConfigurationException(exceptionText, he);
        } catch (ClassNotFoundException cnfe) {
            String exceptionText = "An error occurs during instantiation of the database connection pool!";
            LOGGER.error(exceptionText, cnfe);
            cleanup();
            throw new ConfigurationException(exceptionText, cnfe);
        }
    }

    @Override
    public String getConnectionProviderIdentifier() {
        return HibernateDatasourceConstants.OGM_CONNECTION_PROVIDER_IDENTIFIER;
    }

}
