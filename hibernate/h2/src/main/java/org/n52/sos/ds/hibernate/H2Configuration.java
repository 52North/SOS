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
package org.n52.sos.ds.hibernate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Table;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.schema.TargetType;
import org.n52.faroe.ConfigurationError;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.ds.Datasource;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.util.DefaultHibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.service.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import geodb.GeoDB;

/**
 * @since 4.0.0
 *
 */
public class H2Configuration implements ConnectionProvider {
    private static final Logger LOG = LoggerFactory.getLogger(H2Configuration.class);

    private static final String HIBERNATE_CONNECTION_URL = HibernateConstants.CONNECTION_URL;

    private static final String HIBERNATE_CONNECTION_DRIVER_CLASS =
            DefaultHibernateConstants.DRIVER_PROPERTY;

    private static final String HIBERNATE_DIALECT = HibernateConstants.DIALECT;

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
            resources.add("/hbm/core/CategoryResource.hbm.xml");
            resources.add("/hbm/core/CodespaceResource.hbm.xml");
            resources.add("/hbm/core/FeatureResource.hbm.xml");
            resources.add("/hbm/core/FormatResource.hbm.xml");
            resources.add("/hbm/core/OfferingResource.hbm.xml");
            resources.add("/hbm/core/ParameterResource.hbm.xml");
            resources.add("/hbm/core/PhenomenonResource.hbm.xml");
            resources.add("/hbm/core/PlatformResource.hbm.xml");
            resources.add("/hbm/core/ProcedureHistoryResource.hbm.xml");
            resources.add("/hbm/core/ProcedureResource.hbm.xml");
            resources.add("/hbm/core/RelatedFeatureResource.hbm.xml");
            resources.add("/hbm/core/RelatedFeatureRoleResource.hbm.xml");
            resources.add("/hbm/core/ResultTemplateResource.hbm.xml");
            resources.add("/hbm/core/SamplingGeometryResource.hbm.xml");
            resources.add("/hbm/core/ServiceResource.hbm.xml");
            resources.add("/hbm/core/UnitResource.hbm.xml");
            // dataset
            resources.add("/hbm/dataset/DataResource.hbm.xml");
            resources.add("/hbm/dataset/DatasetResource.hbm.xml");
            resources.add("/hbm/dataset/RelatedDataResource.hbm.xml");
            resources.add("/hbm/dataset/RelatedDatasetResource.hbm.xml");
//            // core
//            resources.add("/mapping/core/Codespace.hbm.xml");
//            resources.add("/mapping/core/FeatureOfInterest.hbm.xml");
//            resources.add("/mapping/core/FeatureOfInterestType.hbm.xml");
//            resources.add("/mapping/core/FeatureOfInterest.hbm.xml");
//            resources.add("/mapping/core/ObservableProperty.hbm.xml");
//            resources.add("/mapping/core/Offering.hbm.xml");
//            resources.add("/mapping/core/Parameter.hbm.xml");
//            resources.add("/mapping/core/Procedure.hbm.xml");
//            resources.add("/mapping/core/ProcedureDescriptionFormat.hbm.xml");
//            resources.add("/mapping/core/Unit.hbm.xml");
//            resources.add("/mapping/core/ObservationConstellation.hbm.xml");
//            resources.add("/mapping/core/ObservationType.hbm.xml");
//            // transactional module
//            resources.add("/mapping/transactional/RelatedFeature.hbm.xml");
//            resources.add("/mapping/transactional/RelatedFeatureRole.hbm.xml");
//            resources.add("/mapping/transactional/ResultTemplate.hbm.xml");
//            resources.add("/mapping/transactional/ValidProcedureTime.hbm.xml");
//            resources.add("/mapping/transactional/TFeatureOfInterest.hbm.xml");
//            resources.add("/mapping/transactional/TObservableProperty.hbm.xml");
//            resources.add("/mapping/transactional/TOffering.hbm.xml");
//            resources.add("/mapping/transactional/TProcedure.hbm.xml");
//            // observation/values
//            resources.add("/mapping/series/base/ValuedObservation.hbm.xml");
//            resources.add("/mapping/series/base/Series.hbm.xml");
//            resources.add("/mapping/series/base/Observation.hbm.xml");
//            resources.add("/mapping/series/base/TemporalReferencedObservation.hbm.xml");
//            resources.add("/mapping/series/base/ContextualReferencedObservation.hbm.xml");
//            resources.add("/mapping/series/observation/Blob.hbm.xml");
//            resources.add("/mapping/series/observation/Boolean.hbm.xml");
//            resources.add("/mapping/series/observation/Category.hbm.xml");
//            resources.add("/mapping/series/observation/Complex.hbm.xml");
//            resources.add("/mapping/series/observation/Count.hbm.xml");
//            resources.add("/mapping/series/observation/Geometry.hbm.xml");
//            resources.add("/mapping/series/observation/Numeric.hbm.xml");
//            resources.add("/mapping/series/observation/SweDataArray.hbm.xml");
//            resources.add("/mapping/series/observation/Text.hbm.xml");
//            resources.add("/mapping/series/value/Blob.hbm.xml");
//            resources.add("/mapping/series/value/Boolean.hbm.xml");
//            resources.add("/mapping/series/value/Category.hbm.xml");
//            resources.add("/mapping/series/value/Complex.hbm.xml");
//            resources.add("/mapping/series/value/Count.hbm.xml");
//            resources.add("/mapping/series/value/Geometry.hbm.xml");
//            resources.add("/mapping/series/value/Numeric.hbm.xml");
//            resources.add("/mapping/series/value/SweDataArray.hbm.xml");
//            resources.add("/mapping/series/value/Text.hbm.xml");
//            resources.add("/mapping/parameter/feature/FeatureParameterValues.hbm.xml");
//            resources.add("/mapping/parameter/observation/ParameterValues.hbm.xml");
            return resources;
        }
    };

    private static final Object LOCK = new Object();

    private static H2Configuration instance;

    private File tempDir;

    private Configuration configuration;

//    private List<String> createScript;
//
//    private List<String> dropScript;

    private static SchemaExport schemaExport;

    private static Metadata metadata;

    private static SessionFactory sessionFactory;

    public static void assertInitialized() {
        synchronized (LOCK) {
            if (instance == null) {
                try {
                    instance = new H2Configuration();
                } catch (IOException | OwsExceptionReport |
                        ConnectionProviderException ex) {
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
            return sessionFactory.openSession();
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
                throw new IllegalStateException("Database is not initialized");
            }
            Session session = null;
            Transaction transaction = null;
            try {
                session = getSession();
                transaction = session.beginTransaction();
//                session.doWork(connection -> {
//                    try (Statement stmt = connection.createStatement()) {
//                        for (String cmd : instance.getDropScript()) {
//                            stmt.addBatch(cmd);
//                        }
//                        for (String cmd : instance.getCreateScript()) {
//                            stmt.addBatch(cmd);
//                        }
//                        stmt.executeBatch();
//                    }
//                });
                schemaExport.execute(EnumSet.of(TargetType.DATABASE), Action.DROP, metadata);
                schemaExport.execute(EnumSet.of(TargetType.DATABASE), Action.CREATE, metadata);
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
            Metadata metadata = new MetadataSources().buildMetadata(instance.getConfiguration().getStandardServiceRegistryBuilder().build());
            final Collection<Table> tableMappings = metadata.collectTableMappings();
            final List<String> tableNames = new LinkedList<>();
            GeoDBDialect dialect = new GeoDBDialect();
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
                        stmt.addBatch("SET REFERENTIAL_INTEGRITY FALSE");
                        for (String table : tableNames) {
                            stmt.addBatch("DELETE FROM " + table);
                        }
                        stmt.addBatch("SET REFERENTIAL_INTEGRITY TRUE");
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

    private H2Configuration() throws IOException, OwsExceptionReport, ConnectionProviderException {
        init();
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
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
        getTempDir().delete();
        FileUtils.forceMkdir(getTempDir());
    }

    private void createConfigurator() throws ConfigurationError {
        Configurator.createInstance(properties, getTempDir().getAbsolutePath());
    }

    private void prepareDatabase() {
        try {
            Class.forName(H2_DRIVER);
            try (Connection conn = DriverManager.getConnection(H2_CONNECTION_URL)) {
                GeoDB.InitGeoDB(conn);
//                Path createTempFile = null;
//                Path dropTempFile = null;
                try (Statement stmt = conn.createStatement()) {
                    configuration = new Configuration().configure("/hibernate.cfg.xml");
                    configuration.setProperty(HibernateConstants.CONNECTION_URL, H2_CONNECTION_URL);
                    configuration.setProperty(HibernateConstants.DIALECT, GeoDBDialect.class.getName());
                    configuration.addProperties(properties);
                    @SuppressWarnings("unchecked")
                    List<String> resources = (List<String>) properties
                            .get(SessionFactoryProvider.HIBERNATE_RESOURCES);
                    for (String resource : resources) {
                        configuration.addInputStream(getClass().getResourceAsStream(resource));
                    }
//                    final GeoDBDialect dialect = new GeoDBDialect();
//                    createTempFile = Files.createTempFile("create", ".tmp");
//                    dropTempFile = Files.createTempFile("drop", ".tmp");
                    schemaExport = new SchemaExport();
                    schemaExport.setDelimiter(";").setFormat(false).setHaltOnError(true);

                    sessionFactory = configuration.buildSessionFactory();

                    StandardServiceRegistry serviceRegistry = configuration.getStandardServiceRegistryBuilder()
                            .applySettings(configuration.getProperties()).build();
                    MetadataSources metadataSources = new MetadataSources(serviceRegistry);
                    for (String resource : resources) {
                        metadataSources.addInputStream(getClass().getResourceAsStream(resource));
                    }
                    Metadata metadata = metadataSources.getMetadataBuilder().build();

//                    Metadata metadata = new MetadataSources(configuration.getStandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build()).buildMetadata();
                    schemaExport.execute(EnumSet.of(TargetType.DATABASE), Action.CREATE, metadata);
//                    createScript = Files.readAllLines(createTempFile);
//                    schemaExport.setOutputFile(dropTempFile.toString());
//                    schemaExport.execute(EnumSet.of(TargetType.DATABASE), Action.DROP, metadata);
//                    dropScript = Files.readAllLines(dropTempFile);
//                    for (final String s : createScript) {
//                        LOG.debug("Executing {}", s);
//                        stmt.execute(s);
//                    }
                } catch (Exception e) {
                    new RuntimeException(e);
                } finally {
//                    try {
//                        if (createTempFile != null) {
//                            Files.deleteIfExists(createTempFile);
//                        }
//
//                    } catch (IOException e) {
//                        LOG.info("Unable to delete temp file {}", createTempFile.toString());
//                    }
//                    try {
//                        if (dropTempFile != null) {
//                            Files.deleteIfExists(dropTempFile);
//                        }
//
//                    } catch (IOException e) {
//                        LOG.info("Unable to delete temp file {}", dropTempFile.toString());
//                    }
                }
            }
        } catch (ClassNotFoundException | SQLException | MappingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String[] getCreateSrcipt(String[] generateSchemaCreationScript) {
        List<String> finalScript = Lists.newArrayList();
        Set<String> nonDublicates = Sets.newHashSet();
        Set<String> nonDuplicateCreate = Sets.newHashSet();
        for (final String s : generateSchemaCreationScript) {
            if (!nonDublicates.contains(s)) {
                if (s.toLowerCase(Locale.ROOT).startsWith("create table")) {
                    String substring = s.substring(0, s.indexOf('('));
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

    private void init() throws ConfigurationError, IOException {
        createTempDir();
        prepareDatabase();
        createConfigurator();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public Object getConnection()
            throws ConnectionProviderException {
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

//    public List<String> getCreateScript() {
//        return createScript;
//    }
//
//    public List<String> getDropScript() {
//        return dropScript;
//    }
}
