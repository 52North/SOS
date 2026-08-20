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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.h2gis.functions.factory.H2GISFunctions;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jdbc.Work;
import org.hibernate.mapping.Table;
import org.n52.faroe.ConfigurationError;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.ds.Datasource;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.util.DefaultHibernateConstants;
import org.n52.sos.ds.hibernate.util.EntityScanner;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @since 4.0.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE"})
public final class H2Configuration implements ConnectionProvider {
    private static final Logger LOG = LoggerFactory.getLogger(H2Configuration.class);

    private static final String HIBERNATE_CONNECTION_URL = HibernateConstants.CONNECTION_URL;

    private static final String HIBERNATE_CONNECTION_DRIVER_CLASS = DefaultHibernateConstants.DRIVER_PROPERTY;

    private static final String HIBERNATE_DIALECT = HibernateConstants.DIALECT;

    private static final String H2_DRIVER = "org.h2.Driver";

    private static final String H2_CONNECTION_URL = "jdbc:h2:mem:sos;DB_CLOSE_DELAY=-1";

    private static final String DB_INITIALIZED = "Database is not initialized";

    private static final String SET_REFERENTIAL_INTEGRITY_FALSE = "SET REFERENTIAL_INTEGRITY FALSE";

    private static final String SET_REFERENTIAL_INTEGRITY_TRUE = "SET REFERENTIAL_INTEGRITY TRUE";

    private static Metadata metadata;

    private static SessionFactory sessionFactory;

    private static Properties properties = new Properties() {
        private static final long serialVersionUID = 3109256773218160485L;

        {
            put(HIBERNATE_CONNECTION_URL, H2_CONNECTION_URL);
            put(HIBERNATE_CONNECTION_DRIVER_CLASS, H2_DRIVER);
            put(HIBERNATE_DIALECT, H2Dialect.class.getName());
            put(Datasource.class.getCanonicalName(), MockDatasource.class.getCanonicalName());
        }
    };

    private static final Object LOCK = new Object();

    private static H2Configuration instance;

    private File tempDir;

    private Configuration configuration;

    // private List<String> createScript;
    //
    // private List<String> dropScript;

    private H2Configuration() throws IOException, OwsExceptionReport, ConnectionProviderException {
        init();
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    public static void assertInitialized() {
        synchronized (LOCK) {
            if (instance == null) {
                try {
                    instance = new H2Configuration();
                } catch (IOException | OwsExceptionReport | ConnectionProviderException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public static Session getSession() {
        H2Configuration.assertInitialized();
        try {
            if (sessionFactory == null) {
                return null;
            }
            Session session = sessionFactory.openSession();
            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    H2GISFunctions.load(connection);
                }
            });
            return session;
        } catch (final HibernateException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void returnSession(final Session session) {
        try {
            if (session != null) {
                if (session.isOpen()) {
                    session.clear();
                    session.close();
                }
            }
        } catch (final HibernateException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void recreate() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException(DB_INITIALIZED);
            }
            Session session = null;
            Transaction transaction = null;
            try {
                session = getSession();
                transaction = session.beginTransaction();
                session.createNativeQuery("DROP ALL OBJECTS", Integer.class).executeUpdate();
                sessionFactory.getSchemaManager().exportMappedObjects(false);
                transaction.commit();
            } catch (final Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            } finally {
                returnSession(session);
            }
        }
    }

    public static void truncate() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException(DB_INITIALIZED);
            }
            final Collection<Table> tableMappings = metadata.collectTableMappings();
            final List<String> tableNames = new LinkedList<>();
            H2Dialect dialect = new H2Dialect();
            for (Table table : tableMappings) {
                tableNames.add(table.getQuotedName(dialect));
            }
            Session session = null;
            Transaction transaction = null;
            try {
                session = getSession();
                transaction = session.beginTransaction();
                session.doWork(connection -> {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.addBatch(SET_REFERENTIAL_INTEGRITY_FALSE);
                        for (String table : tableNames) {
                            stmt.addBatch("DELETE FROM " + table);
                        }
                        stmt.addBatch(SET_REFERENTIAL_INTEGRITY_TRUE);
                        stmt.executeBatch();
                    }
                });
                transaction.commit();
            } catch (final HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            } finally {
                returnSession(session);
            }
        }
    }

    private void cleanup() {
        try {
            final File directory = getTempDir();
            if (directory != null && directory.exists()) {
                for (File file : directory.listFiles()) {
                    if (file.exists()) {
                        FileUtils.forceDelete(file);
                    }
                }
                FileUtils.forceDelete(directory);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private File getTempDir() {
        return tempDir;
    }

    private void setTempDir(final File aTempDir) {
        tempDir = aTempDir;
    }

    private void createTempDir() throws IOException {
        setTempDir(File.createTempFile("hibernate-test-case", ""));
        FileUtils.deleteQuietly(getTempDir());
        FileUtils.forceMkdir(getTempDir());
    }

    private void prepareDatabase() {
        try {
            Class.forName(H2_DRIVER);
            try (Connection conn = DriverManager.getConnection(H2_CONNECTION_URL)) {
                H2GISFunctions.load(conn);
                try (Statement stmt = conn.createStatement()) {
                    Configuration builder = new Configuration();
                    builder.configure("/hibernate.cfg.xml");
                    builder.setProperty(HibernateConstants.CONNECTION_URL, H2_CONNECTION_URL);
                    builder.setProperty(HibernateConstants.DIALECT, H2Dialect.class.getName());
                    builder.addProperties(properties);
                    EntityScanner.applyTo(builder, HibernateDatasourceConstants.ENTITY_PACKAGE);
                    configuration = builder;

                    sessionFactory = builder.buildSessionFactory();

                    StandardServiceRegistry serviceRegistry = builder.getStandardServiceRegistryBuilder()
                            .applySettings(builder.getProperties()).build();
                    MetadataSources metadataSources = new MetadataSources(serviceRegistry);
                    EntityScanner.applyTo(metadataSources, HibernateDatasourceConstants.ENTITY_PACKAGE);
                    metadata = metadataSources.getMetadataBuilder().build();

                    sessionFactory.getSchemaManager().exportMappedObjects(false);
                }
            }
        } catch (ClassNotFoundException | SQLException | MappingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void init() throws ConfigurationError, IOException {
        createTempDir();
        prepareDatabase();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public Object getConnection() throws ConnectionProviderException {
        return getSession();
    }

    @Override
    public void returnConnection(Object connection) {
        returnSession((Session) connection);
    }

    @Override
    public int getMaxConnections() {
        return 0;
    }
}
