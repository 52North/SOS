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
package org.n52.sos.config.sqlite;

import java.io.File;
import java.net.URI;
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

import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.sqlite.entities.AdminUser;
import org.n52.sos.config.sqlite.entities.Binding;
import org.n52.sos.config.sqlite.entities.BooleanSettingValue;
import org.n52.sos.config.sqlite.entities.CapabilitiesExtensionImpl;
import org.n52.sos.config.sqlite.entities.ChoiceSettingValue;
import org.n52.sos.config.sqlite.entities.DynamicOfferingExtension;
import org.n52.sos.config.sqlite.entities.DynamicOwsExtendedCapabilities;
import org.n52.sos.config.sqlite.entities.FileSettingValue;
import org.n52.sos.config.sqlite.entities.IntegerSettingValue;
import org.n52.sos.config.sqlite.entities.MultilingualStringSettingValue;
import org.n52.sos.config.sqlite.entities.NumericSettingValue;
import org.n52.sos.config.sqlite.entities.ObservationEncoding;
import org.n52.sos.config.sqlite.entities.OfferingExtensionImpl;
import org.n52.sos.config.sqlite.entities.Operation;
import org.n52.sos.config.sqlite.entities.ProcedureEncoding;
import org.n52.sos.config.sqlite.entities.StaticCapabilitiesImpl;
import org.n52.sos.config.sqlite.entities.StringSettingValue;
import org.n52.sos.config.sqlite.entities.TimeInstantSettingValue;
import org.n52.sos.config.sqlite.entities.UriSettingValue;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.hibernate.AbstractSessionFactoryProvider;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.service.SosContextListener;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class SQLiteSessionFactory extends AbstractSessionFactoryProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SQLiteSessionFactory.class);

    public static final String HIBERNATE_DIALECT = "hibernate.dialect";

    public static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";

    public static final String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";

    public static final String HIBERNATE_UPDATE_SCHEMA = "hibernate.hbm2ddl.auto";

    public static final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";

    public static final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";

    public static final String HIBERNATE_CONNECTION_POOL_SIZE = "hibernate.connection.pool_size";

    public static final String HIBERNATE_CONNECTION_RELEASE_MODE = "hibernate.connection.release_mode";

    public static final String HIBERNATE_CURRENT_SESSION_CONTEXT = "hibernate.current_session_context_class";

    public static final String RELEASE_MODE_AFTER_TRANSACTION = "after_transaction";

    public static final String RELEASE_MODE_AFTER_STATEMENT = "after_statement";

    public static final String RELEASE_MODE_ON_CLOSE = "on_close";

    public static final String RELEASE_MODE_AUTO = "auto";

    public static final String THREAD_LOCAL_SESSION_CONTEXT = "thread";

    public static final int SQLITE_CONNECTION_POOL_SIZE = 1;

    public static final String CONNECTION_URL_TEMPLATE = "jdbc:sqlite:%s.db";

    protected static final String DEFAULT_DATABASE_NAME = "configuration";

    public static final String SQLITE_HIBERNATE_DIALECT = HibernateSQLiteDialect.class.getName();

    public static final String UPDATE_SCHEMA_VALUE = "update";

    public static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";

    public static final String EMPTY = "";

    private final Properties defaultProperties = new Properties() {
        private static final long serialVersionUID = 3109256773218160485L;
        {
            put(HIBERNATE_CONNECTION_URL, getFilename());
            put(HIBERNATE_UPDATE_SCHEMA, UPDATE_SCHEMA_VALUE);
            put(HIBERNATE_DIALECT, SQLITE_HIBERNATE_DIALECT);
            put(HIBERNATE_CONNECTION_DRIVER_CLASS, SQLITE_JDBC_DRIVER);
            put(HIBERNATE_CONNECTION_USERNAME, EMPTY);
            put(HIBERNATE_CONNECTION_PASSWORD, EMPTY);
            put(HIBERNATE_CONNECTION_POOL_SIZE, String.valueOf(SQLITE_CONNECTION_POOL_SIZE));
            put(HIBERNATE_CONNECTION_RELEASE_MODE, RELEASE_MODE_AFTER_TRANSACTION);
            put(HIBERNATE_CURRENT_SESSION_CONTEXT, THREAD_LOCAL_SESSION_CONTEXT);
        }
    };

    protected String getFilename() {
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
        return String.format(CONNECTION_URL_TEMPLATE, path);
    }

    private final ReentrantLock lock = new ReentrantLock();

    private SessionFactory sessionFactory;

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
                .addAnnotatedClass(BooleanSettingValue.class)
                .addAnnotatedClass(FileSettingValue.class)
                .addAnnotatedClass(IntegerSettingValue.class)
                .addAnnotatedClass(NumericSettingValue.class)
                .addAnnotatedClass(StringSettingValue.class)
                .addAnnotatedClass(UriSettingValue.class)
                .addAnnotatedClass(ChoiceSettingValue.class)
                .addAnnotatedClass(AdminUser.class)
                .addAnnotatedClass(CapabilitiesExtensionImpl.class)
                .addAnnotatedClass(OfferingExtensionImpl.class)
                .addAnnotatedClass(StaticCapabilitiesImpl.class)
                .addAnnotatedClass(Operation.class)
                .addAnnotatedClass(ProcedureEncoding.class)
                .addAnnotatedClass(Binding.class)
                .addAnnotatedClass(ObservationEncoding.class)
                .addAnnotatedClass(DynamicOfferingExtension.class)
                .addAnnotatedClass(DynamicOwsExtendedCapabilities.class)
                .addAnnotatedClass(TimeInstantSettingValue.class)
                .addAnnotatedClass(MultilingualStringSettingValue.class);

        cfg.registerTypeOverride(new HibernateFileType(), new String[] { "file", File.class.getName() });
        cfg.registerTypeOverride(new HibernateUriType(), new String[] { "uri", URI.class.getName() });
        cfg.registerTypeOverride(new HibernateTimeInstantType(), new String[] { "timeInstant", TimeInstant.class.getName() });

        if (properties != null) {
            cfg.mergeProperties(properties);
        }
        cfg.mergeProperties(defaultProperties);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
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
        return "sqLiteHibernate";
    }
}
