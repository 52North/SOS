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
package org.n52.sos.ds.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;

import org.n52.faroe.SettingDefinition;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.util.HibernateConstants;

import junit.framework.TestCase;

/**
 * @since 4.0.0
 *
 */
public class AbstractHibernateFullDBDatasourceTest
        extends TestCase {
    private static final String POSTGRES = "postgres";

    private static int CHANGEABLE_COUNT = 10;

    private static int MAX_COUNT = 17;

    private AbstractHibernateFullDBDatasource ds;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ds = new MockDatasource();
    }

    public void testGetSettingDefinitions() throws Exception {
        Set<SettingDefinition<?>> settings = ds.getSettingDefinitions();
        checkSettingDefinitionsTransactional(settings);
    }

    public void testGetChangableSettingDefinitions() throws Exception {
        Set<SettingDefinition<?>> settings = ds.getChangableSettingDefinitions(new Properties());
        checkSettingDefinitionsChangableSetting(settings);
    }

    public void testParseDatasourceProperties() throws Exception {
        Properties current = new Properties();
        current.put(HibernateConstants.DEFAULT_CATALOG, "public");
        current.put(HibernateConstants.CONNECTION_USERNAME, POSTGRES);
        current.put(HibernateConstants.CONNECTION_PASSWORD, POSTGRES);
        current.put(HibernateConstants.CONNECTION_URL, "jdbc:postgresql://localhost:5432/test");
        current.put(HibernateConstants.C3P0_MIN_SIZE, "10");
        current.put(HibernateConstants.C3P0_MAX_SIZE, "30");
        current.put(HibernateConstants.JDBC_BATCH_SIZE, "20");
        current.put(HibernateDatasourceConstants.PROVIDED_JDBC, "true");
        current.put(HibernateDatasourceConstants.HIBERNATE_DIRECTORY, "some-directory-stuff-to-test");

        Map<String, Object> settings = ds.parseDatasourceProperties(current);
        checkSettingKeys(settings.keySet(), false, false, false);
    }

    private void checkSettingDefinitionsTransactional(Set<SettingDefinition<?>> settings) {
        checkSettingDefinitions(settings, false, true, true);
    }

    private void checkSettingDefinitionsChangableSetting(Set<SettingDefinition<?>> settings) {
        checkSettingDefinitions(settings, true, false, false);

    }

    private void checkSettingDefinitions(Set<SettingDefinition<?>> settings, boolean changeable,
            boolean settingsDefinitions, boolean timeFormat) {
        List<String> keys = new ArrayList<>();
        Iterator<SettingDefinition<?>> iterator = settings.iterator();
        while (iterator.hasNext()) {
            keys.add(iterator.next().getKey());
        }
        checkSettingKeys(keys, changeable, settingsDefinitions, timeFormat);
    }

    private void checkSettingKeys(Collection<String> keys, boolean changeable, boolean settingsDefinitions,
            boolean timeFormat) {
        boolean concept = keys.contains(AbstractHibernateDatasource.DATABASE_CONCEPT_KEY);
        boolean featureConcept = keys.contains(AbstractHibernateDatasource.FEATURE_CONCEPT_KEY);

        assertTrue(keys.contains(AbstractHibernateCoreDatasource.HOST_KEY));
        assertTrue(keys.contains(AbstractHibernateCoreDatasource.PORT_KEY));
        assertTrue(keys.contains(AbstractHibernateCoreDatasource.DATABASE_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.USERNAME_KEY));
        assertTrue(keys.contains(AbstractHibernateCoreDatasource.PASSWORD_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.SCHEMA_KEY));
        assertTrue(keys.contains(AbstractHibernateCoreDatasource.MIN_POOL_SIZE_KEY));
        assertTrue(keys.contains(AbstractHibernateCoreDatasource.MAX_POOL_SIZE_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.BATCH_SIZE_KEY));
        assertTrue(
                changeable || settingsDefinitions || keys.contains(HibernateDatasourceConstants.HIBERNATE_DIRECTORY));
        assertTrue(changeable || keys.contains(AbstractHibernateDatasource.PROVIDED_JDBC_DRIVER_KEY));
        assertTrue(!concept || keys.contains(AbstractHibernateDatasource.DATABASE_CONCEPT_KEY));
        assertTrue(!featureConcept || keys.contains(AbstractHibernateDatasource.FEATURE_CONCEPT_KEY));
        assertTrue(keys.contains(AbstractHibernateCoreDatasource.TIMEZONE_KEY));
        assertTrue(!timeFormat || keys.contains(AbstractHibernateCoreDatasource.TIME_STRING_FORMAT_KEY));
        assertTrue(!timeFormat || keys.contains(AbstractHibernateCoreDatasource.TIME_STRING_Z_KEY));

        if (changeable) {
            assertEquals(CHANGEABLE_COUNT, keys.size());
        } else {
            int counter = MAX_COUNT;
            if (!concept) {
                counter--;
            }
            if (!featureConcept) {
                counter--;
            }
            if (settingsDefinitions) {
                counter--;
            }
            if (!timeFormat) {
                counter -= 3;
            }
            assertEquals(counter, keys.size());
        }
    }

    private class MockDatasource
            extends AbstractHibernateFullDBDatasource {

        @Override
        protected Dialect createDialect() {
            return null;
        }

        @Override
        public String getDialectName() {
            return null;
        }

        @Override
        public boolean checkSchemaCreation(Map<String, Object> settings) {
            return false;
        }

        @Override
        protected String toURL(Map<String, Object> settings) {
            return null;
        }

        @Override
        protected String[] parseURL(String url) {
            return new String[] { "localhost", "5432", "db" };
        }

        @Override
        protected String getDriverClass() {
            return null;
        }

        @Override
        public void clear(Properties settings) {
        }

        @Override
        public boolean supportsClear() {
            return false;
        }

        @Override
        protected void validatePrerequisites(Connection con, Metadata metadata, Map<String, Object> settings) {
        }

        @Override
        protected Connection openConnection(Map<String, Object> settings) throws SQLException {
            return null;
        }

        @Override
        protected String[] checkDropSchema(String[] dropSchema) {
            return null;
        }

    }
}
