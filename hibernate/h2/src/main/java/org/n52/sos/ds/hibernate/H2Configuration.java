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

import geodb.GeoDB;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.mapping.Table;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;
import org.n52.sos.cache.ctrl.ScheduledContentCacheControllerSettings;
import org.n52.sos.config.sqlite.SQLiteSessionFactory;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.Datasource;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.SosContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class H2Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(H2Configuration.class);

    private static final String HIBERNATE_CONNECTION_URL = SQLiteSessionFactory.HIBERNATE_CONNECTION_URL;

    private static final String HIBERNATE_CONNECTION_DRIVER_CLASS =
            SQLiteSessionFactory.HIBERNATE_CONNECTION_DRIVER_CLASS;

    private static final String HIBERNATE_DIALECT = SQLiteSessionFactory.HIBERNATE_DIALECT;

    private static final String H2_DRIVER = "org.h2.Driver";

    private static final String H2_CONNECTION_URL = "jdbc:h2:mem:sos;DB_CLOSE_DELAY=-1;MULTI_THREADED=true";

    private static Properties properties = new Properties() {
        private static final long serialVersionUID = 3109256773218160485L;

        {
            put(HIBERNATE_CONNECTION_URL, H2_CONNECTION_URL);
            put(HIBERNATE_CONNECTION_DRIVER_CLASS, H2_DRIVER);
            put(HIBERNATE_DIALECT, GeoDBDialect.class.getName());
            put(SessionFactoryProvider.HIBERNATE_RESOURCES, getResources());
            put(Datasource.class.getCanonicalName(), MockDatasource.class.getCanonicalName());
        }

        private List<String> getResources() {
            List<String> resources = Lists.newLinkedList();
            // core
            resources.add("mapping/core/Codespace.hbm.xml");
            resources.add("mapping/core/FeatureOfInterest.hbm.xml");
            resources.add("mapping/core/FeatureOfInterestType.hbm.xml");
            resources.add("mapping/core/ObservableProperty.hbm.xml");
            resources.add("mapping/core/Offering.hbm.xml");
            resources.add("mapping/core/Procedure.hbm.xml");
            resources.add("mapping/core/ProcedureDescriptionFormat.hbm.xml");
            resources.add("mapping/core/Unit.hbm.xml");
            resources.add("mapping/core/ObservationConstellation.hbm.xml");
            resources.add("mapping/core/ObservationType.hbm.xml");
            // transactional module
            resources.add("mapping/transactional/RelatedFeature.hbm.xml");
            resources.add("mapping/transactional/RelatedFeatureRole.hbm.xml");
            resources.add("mapping/transactional/ResultTemplate.hbm.xml");
            resources.add("mapping/transactional/ValidProcedureTime.hbm.xml");
            resources.add("mapping/transactional/TFeatureOfInterest.hbm.xml");
            resources.add("mapping/transactional/TObservableProperty.hbm.xml");
            resources.add("mapping/transactional/TOffering.hbm.xml");
            resources.add("mapping/transactional/TProcedure.hbm.xml");
            // old observation concept
            // resources.add("mapping/old/observation/Observation.hbm.xml");
            // resources.add("mapping/old/observation/ObservationInfo.hbm.xml");
            // resources.add("mapping/old/spatialFilteringProfile/SpatialFitleringProfile.hbm.xml");
            // series observation concept, needs changes in tests
            resources.add("mapping/series/observation/Series.hbm.xml");
            resources.add("mapping/series/observation/SeriesObservation.hbm.xml");
            resources.add("mapping/series/observation/SeriesObservationInfo.hbm.xml");
            resources.add("mapping/series/observation/SeriesObservationTime.hbm.xml");
            resources.add("mapping/series/observation/SeriesValue.hbm.xml");
            resources.add("mapping/series/observation/SeriesValueTime.hbm.xml");
            return resources;
        }
    };

    private static final Object LOCK = new Object();

    private static H2Configuration instance;

    private File tempDir;

    private Configuration configuration;

    private String[] createScript;

    private String[] dropScript;

    public static void assertInitialized() {
        synchronized (LOCK) {
            if (instance == null) {
                try {
                    instance = new H2Configuration();
                } catch (final IOException ex) {
                    throw new RuntimeException(ex);
                } catch (final OwsExceptionReport ex) {
                    throw new RuntimeException(ex);
                } catch (final ConnectionProviderException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public static Session getSession() {
        H2Configuration.assertInitialized();
        try {
            return (Session) Configurator.getInstance().getDataConnectionProvider().getConnection();
        } catch (final ConnectionProviderException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void returnSession(final Session session) {
        if (session != null) {
            Configurator.getInstance().getDataConnectionProvider().returnConnection(session);
        }
    }

    public static void recreate() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("Database is not initialized");
            }
            Session session = null;
            Transaction transaction = null;
            try {
                session = getSession();
                transaction = session.beginTransaction();
                session.doWork(new Work() {
                    @Override
                    public void execute(final Connection connection) throws SQLException {
                        Statement stmt = null;
                        try {
                            stmt = connection.createStatement();
                            for (final String cmd : instance.getDropScript()) {
                                stmt.addBatch(cmd);
                            }
                            for (final String cmd : instance.getCreateScript()) {
                                stmt.addBatch(cmd);
                            }
                            stmt.executeBatch();
                        } finally {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
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

    public static void truncate() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("Database is not initialized");
            }
            final Iterator<Table> tableMappings = instance.getConfiguration().getTableMappings();
            final List<String> tableNames = new LinkedList<String>();
            GeoDBDialect dialect = new GeoDBDialect();
            while (tableMappings.hasNext()) {
                tableNames.add(tableMappings.next().getQuotedName(dialect));
            }
            Session session = null;
            Transaction transaction = null;
            try {
                session = getSession();
                transaction = session.beginTransaction();
                session.doWork(new Work() {
                    @Override
                    public void execute(final Connection connection) throws SQLException {
                        Statement stmt = null;
                        try {
                            stmt = connection.createStatement();
                            stmt.addBatch("SET REFERENTIAL_INTEGRITY FALSE");
                            for (final String table : tableNames) {
                                stmt.addBatch("DELETE FROM " + table);
                            }
                            stmt.addBatch("SET REFERENTIAL_INTEGRITY TRUE");
                            stmt.executeBatch();
                        } finally {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
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

    private H2Configuration() throws IOException, OwsExceptionReport, ConnectionProviderException {
        init();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                cleanup();
            }
        }));
    }

    private void cleanup() {
        try {
            final Configurator configurator = Configurator.getInstance();
            if (configurator != null) {
                configurator.cleanup();
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
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
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setDefaultSettings() {
        ScheduledContentCacheControllerSettings.CACHE_UPDATE_INTERVAL_DEFINITION.setDefaultValue(0);
    }

    private File getTempDir() {
        return tempDir;
    }

    private void setTempDir(final File aTempDir) {
        tempDir = aTempDir;
    }

    private void createTempDir() throws IOException {
        setTempDir(File.createTempFile("hibernate-test-case", ""));
        getTempDir().delete();
        FileUtils.forceMkdir(getTempDir());
        SosContextListener.setPath(getTempDir().getAbsolutePath());
    }

    private void createConfigurator() throws ConfigurationException {
        Configurator.createInstance(properties, getTempDir().getAbsolutePath());
    }

    private void prepareDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(H2_DRIVER);
            conn = DriverManager.getConnection(H2_CONNECTION_URL);
            GeoDB.InitGeoDB(conn);
            stmt = conn.createStatement();
            configuration = new Configuration().configure("/sos-hibernate.cfg.xml");
            @SuppressWarnings("unchecked")
            List<String> resources = (List<String>) properties.get(SessionFactoryProvider.HIBERNATE_RESOURCES);
            for (String resource : resources) {
                configuration.addResource(resource);
            }
            final GeoDBDialect dialect = new GeoDBDialect();
            createScript = getCreateSrcipt(configuration.generateSchemaCreationScript(dialect));
            dropScript = getDropScript(configuration.generateDropSchemaScript(dialect));
            for (final String s : createScript) {
                LOG.debug("Executing {}", s);
                stmt.execute(s);
            }
        } catch (final ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } catch (MappingException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (final SQLException ex) {
                }
            }
        }
    }

    private String[] getCreateSrcipt(String[] generateSchemaCreationScript) {
        List<String> finalScript = Lists.newArrayList(); 
        Set<String> nonDublicates = Sets.newHashSet();
        Set<String> nonDuplicateCreate = Sets.newHashSet();
        for (final String s : generateSchemaCreationScript) {
            if (!nonDublicates.contains(s)) {
                if (s.toLowerCase().startsWith("create table")) {
                    String substring = s.substring(0, s.indexOf("("));
                    if (!nonDuplicateCreate.contains(substring)) {
                        nonDuplicateCreate.add(substring);
                        LOG.debug("Executing {}", s);
                        finalScript.add(s);
                    }
                } else {
                    LOG.debug("Executing {}", s);
                    finalScript.add(s);
                }
                nonDublicates.add(s);
            }
        }
        return finalScript.toArray(new String[finalScript.size()]);
    }

    private String[] getDropScript(String[] generateDropSchemaScript) {
        Set<String> nonDuplicates = Sets.newHashSet();
        List<String> finalScript = Lists.newArrayList();
        for (String string : generateDropSchemaScript) {
            if (!nonDuplicates.contains(string)) {
                finalScript.add(string);
                nonDuplicates.add(string);
            }
        }
        return finalScript.toArray(new String[finalScript.size()]);
    }

    private void init() throws ConfigurationException, IOException {
        setDefaultSettings();
        createTempDir();
        prepareDatabase();
        createConfigurator();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String[] getCreateScript() {
        return createScript;
    }

    public String[] getDropScript() {
        return dropScript;
    }
}
