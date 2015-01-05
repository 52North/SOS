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

import junit.framework.TestCase;

import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.util.HibernateConstants;

/**
 * @since 4.0.0
 *
 */
public class AbstractHibernateFullDBDatasourceTest extends TestCase {
    private AbstractHibernateFullDBDatasource ds;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ds = new MockDatasource();
    }

    public void testGetSettingDefinitions() throws Exception {
        final Set<SettingDefinition<?, ?>> settings = ds.getSettingDefinitions();
        checkSettingDefinitionsTransactional(settings);
    }

    public void testGetChangableSettingDefinitions() throws Exception {
        final Set<SettingDefinition<?, ?>> settings = ds.getChangableSettingDefinitions(new Properties());
        checkSettingDefinitionsChangableSetting(settings);
    }

    public void testParseDatasourceProperties() throws Exception {
        final Properties current = new Properties();
        current.put(HibernateConstants.DEFAULT_CATALOG, "public");
        current.put(HibernateConstants.CONNECTION_USERNAME, "postgres");
        current.put(HibernateConstants.CONNECTION_PASSWORD, "postgres");
        current.put(HibernateConstants.CONNECTION_URL, "jdbc:postgresql://localhost:5432/test");
        current.put(HibernateConstants.C3P0_MIN_SIZE, "10");
        current.put(HibernateConstants.C3P0_MAX_SIZE, "30");
        current.put(HibernateConstants.JDBC_BATCH_SIZE, "20");
        current.put(HibernateDatasourceConstants.PROVIDED_JDBC, "true");

        final Map<String, Object> settings = ds.parseDatasourceProperties(current);
        checkSettingKeysTransactional(settings.keySet());
    }

    private void checkSettingDefinitionsTransactional(final Set<SettingDefinition<?, ?>> settings) {
        checkSettingDefinitions(settings, false);
    }

    private void checkSettingDefinitionsChangableSetting(final Set<SettingDefinition<?, ?>> settings) {
        checkSettingDefinitions(settings, true);

    }

    private void checkSettingDefinitions(final Set<SettingDefinition<?, ?>> settings, final boolean changeable) {
        final List<String> keys = new ArrayList<String>();
        final Iterator<SettingDefinition<?, ?>> iterator = settings.iterator();
        while (iterator.hasNext()) {
            keys.add(iterator.next().getKey());
        }
        checkSettingKeys(keys, changeable);
    }

    private void checkSettingKeysTransactional(final Collection<String> keys) {
        checkSettingKeys(keys, false);
    }

    private void checkSettingKeys(final Collection<String> keys, final boolean changeable) {
        boolean transactional = keys.contains(AbstractHibernateDatasource.TRANSACTIONAL_KEY);
        boolean concept = keys.contains(AbstractHibernateDatasource.DATABASE_CONCEPT_KEY);
        boolean multiLanguage = keys.contains(AbstractHibernateDatasource.MULTILINGUALISM_KEY);

        assertTrue(keys.contains(AbstractHibernateDatasource.HOST_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.PORT_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.DATABASE_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.USERNAME_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.PASSWORD_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.SCHEMA_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.MIN_POOL_SIZE_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.MAX_POOL_SIZE_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.BATCH_SIZE_KEY));
        assertTrue(changeable || keys.contains(AbstractHibernateDatasource.PROVIDED_JDBC_DRIVER_KEY));
        assertTrue(!transactional || keys.contains(AbstractHibernateDatasource.TRANSACTIONAL_KEY));
        assertTrue(!concept || keys.contains(AbstractHibernateDatasource.DATABASE_CONCEPT_KEY));
        assertTrue(!multiLanguage || keys.contains(AbstractHibernateDatasource.MULTILINGUALISM_KEY));

        if (changeable) {
            assertEquals(9, keys.size());
        } else {
            final int maxCount = 13;
            int counter = maxCount;
            if (!transactional) { counter--; }
            if (!concept) { counter--; }
            if (!multiLanguage){ counter--; }
            assertEquals(counter, keys.size());
        }
    }

    private class MockDatasource extends AbstractHibernateFullDBDatasource {
        @Override
        protected Dialect createDialect() {
            return null;
        }

        @Override
        public String getDialectName() {
            return null;
        }

        @Override
        public boolean checkSchemaCreation(final Map<String, Object> settings) {
            return false;
        }

        @Override
        protected String toURL(final Map<String, Object> settings) {
            return null;
        }

        @Override
        protected String[] parseURL(final String url) {
            return new String[] { "localhost", "5432", "db" };
        }

        @Override
        protected String getDriverClass() {
            return null;
        }

        @Override
        public void clear(final Properties settings) {
        }

        @Override
        public boolean supportsClear() {
            return false;
        }

        @Override
        protected void validatePrerequisites(final Connection con, final DatabaseMetadata metadata, final Map<String, Object> settings) {
        }

        @Override
        protected Connection openConnection(final Map<String, Object> settings) throws SQLException {
            return null;
        }

        @Override
        protected String[] checkDropSchema(final String[] dropSchema) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
