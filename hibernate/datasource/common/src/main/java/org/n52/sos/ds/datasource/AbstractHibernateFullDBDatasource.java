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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Sets;


public abstract class AbstractHibernateFullDBDatasource extends AbstractHibernateDatasource {
    private String schemaDefault, schemaDescription;
    private int batchSizeDefault;
    private boolean providedJdbc;
    private boolean supportsSchema;

    AbstractHibernateFullDBDatasource(boolean supportsSchema) {
        this.supportsSchema = supportsSchema;
        setMinPoolSizeDefault(MIN_POOL_SIZE_DEFAULT_VALUE);
        setMaxPoolSizeDefault(MAX_POOL_SIZE_DEFAULT_VALUE);
        setBatchSizeDefault(BATCH_SIZE_DEFAULT_VALUE);
    }

    public AbstractHibernateFullDBDatasource() {
        this(true);
    }

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        Set<SettingDefinition<?, ?>> set = super.getSettingDefinitions();
        if (supportsSchema) {
            set.add(createSchemaDefinition(schemaDefault));
        }
        set.add(createBatchSizeDefinition(batchSizeDefault));
        set.add(createProvidedJdbcDriverDefinition(providedJdbc));
        set.add(getDatabaseConceptDefinition());
        if (isTransactionalDatasource()) {
            set.add(getTransactionalDefiniton());
        }
        if (isMultiLanguageDatasource()) {
            set.add(getMulitLanguageDefiniton());
        }
        return set;
    }

    @Override
    public Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(final Properties current) {
        final Map<String, Object> settings = parseDatasourceProperties(current);
        StringSettingDefinition schemaSetting = createSchemaDefinition((String) settings.get(SCHEMA_KEY));
        HashSet<SettingDefinition<?, ?>> settingDefinitions
                = Sets.<SettingDefinition<?, ?>>newHashSet(
                        createUsernameDefinition((String) settings.get(USERNAME_KEY)),
                        createPasswordDefinition((String) settings.get(PASSWORD_KEY)),
                        createDatabaseDefinition((String) settings.get(DATABASE_KEY)),
                        createHostDefinition((String) settings.get(HOST_KEY)),
                        createPortDefinition(JavaHelper.asInteger(settings.get(PORT_KEY))),
                        createMinPoolSizeDefinition(JavaHelper.asInteger(settings.get(MIN_POOL_SIZE_KEY))),
                        createMaxPoolSizeDefinition(JavaHelper.asInteger(settings.get(MAX_POOL_SIZE_KEY))),
                        createBatchSizeDefinition(JavaHelper.asInteger(settings.get(BATCH_SIZE_KEY))));
        if (supportsSchema) {
            settingDefinitions.add(schemaSetting);
        }
        return settingDefinitions;
    }

    protected StringSettingDefinition createSchemaDefinition(final String defaultValue) {
        return createSchemaDefinition().setDescription(schemaDescription).setDefaultValue(defaultValue);
    }

    protected SettingDefinition<?, ?> createBatchSizeDefinition(final Integer defaultValue) {
        return createBatchSizeDefinition().setDefaultValue(defaultValue);
    }

    protected SettingDefinition<?, ?> createProvidedJdbcDriverDefinition(final Boolean defaultValue) {
        return createProvidedJdbcDriverDefinition().setDefaultValue(defaultValue);
    }

    @Override
    public Properties getDatasourceProperties(final Map<String, Object> settings) {
        final Properties p = new Properties();
        if (supportsSchema) {
            p.put(HibernateConstants.DEFAULT_SCHEMA, settings.get(SCHEMA_KEY));
        }
        p.put(HibernateConstants.CONNECTION_USERNAME, settings.get(USERNAME_KEY));
        p.put(HibernateConstants.CONNECTION_PASSWORD, settings.get(PASSWORD_KEY));
        p.put(HibernateConstants.CONNECTION_URL, toURL(settings));
        p.put(HibernateConstants.CONNECTION_PROVIDER_CLASS, C3P0_CONNECTION_POOL);
        p.put(HibernateConstants.DIALECT, getDialectClass());
        p.put(HibernateConstants.DRIVER_CLASS, getDriverClass());
        p.put(HibernateConstants.C3P0_MIN_SIZE, settings.get(MIN_POOL_SIZE_KEY).toString());
        p.put(HibernateConstants.C3P0_MAX_SIZE, settings.get(MAX_POOL_SIZE_KEY).toString());
        p.put(HibernateConstants.C3P0_IDLE_TEST_PERIOD, "30");
        p.put(HibernateConstants.C3P0_ACQUIRE_INCREMENT, "1");
        p.put(HibernateConstants.C3P0_TIMEOUT, "0");
        p.put(HibernateConstants.C3P0_MAX_STATEMENTS, "0");
        if (settings.containsKey(BATCH_SIZE_KEY)) {
            p.put(HibernateConstants.JDBC_BATCH_SIZE, settings.get(BATCH_SIZE_KEY).toString());
        }
        p.put(HibernateConstants.CONNECTION_AUTO_RECONNECT, "true");
        p.put(HibernateConstants.CONNECTION_AUTO_RECONNECT_FOR_POOLS, "true");
        p.put(HibernateConstants.CONNECTION_TEST_ON_BORROW, "true");
        p.put(PROVIDED_JDBC, settings.get(PROVIDED_JDBC_DRIVER_KEY).toString());
        p.put(DATABASE_CONCEPT_KEY, settings.get(DATABASE_CONCEPT_KEY));
        addMappingFileDirectories(settings, p);

        return p;
    }

    private void checkAndPut(Properties p, String key, Object value) {
        if (value != null) {
            if (value instanceof String) {
                if (StringHelper.isNotEmpty(((String) value))) {
                    p.put(key, value);
                }
            } else {
                p.put(key, value);
            }
        }
    }

    protected Map<String, Object> parseDatasourceProperties(final Properties current) {
        final Map<String, Object> settings = new HashMap<String, Object>(current.size());
        if (supportsSchema) {
            settings.put(SCHEMA_KEY, current.getProperty(HibernateConstants.DEFAULT_SCHEMA));
        }
        settings.put(USERNAME_KEY, current.getProperty(HibernateConstants.CONNECTION_USERNAME));
        settings.put(PASSWORD_KEY, current.getProperty(HibernateConstants.CONNECTION_PASSWORD));
        settings.put(MIN_POOL_SIZE_KEY, current.getProperty(HibernateConstants.C3P0_MIN_SIZE));
        settings.put(MAX_POOL_SIZE_KEY, current.getProperty(HibernateConstants.C3P0_MAX_SIZE));
        if (current.containsKey(HibernateConstants.JDBC_BATCH_SIZE)) {
            settings.put(BATCH_SIZE_KEY, current.getProperty(HibernateConstants.JDBC_BATCH_SIZE));
        }
        settings.put(TRANSACTIONAL_KEY, isTransactional(current));
        settings.put(DATABASE_CONCEPT_KEY,  current.getProperty(DATABASE_CONCEPT_KEY));
        settings.put(PROVIDED_JDBC_DRIVER_KEY,
                current.getProperty(PROVIDED_JDBC, PROVIDED_JDBC_DRIVER_DEFAULT_VALUE.toString()));
        final String url = current.getProperty(HibernateConstants.CONNECTION_URL);

        final String[] parsed = parseURL(url);
        final String host = parsed[0];
        final String port = parsed[1];
        final String db = parsed[2];

        settings.put(createHostDefinition().getKey(), host);
        settings.put(createPortDefinition().getKey(), JavaHelper.asInteger(port));
        settings.put(createDatabaseDefinition().getKey(), db);
        return settings;
    }

    private String getDialectClass() {
        return createDialect().getClass().getCanonicalName();
    }

    /**
     * @param schemaDefault
     *            the schemaDefault to set
     */
    public void setSchemaDefault(final String schemaDefault) {
        this.schemaDefault = schemaDefault;
    }

    /**
     * @param schemaDescription
     *            the schemaDescription to set
     */
    public void setSchemaDescription(final String schemaDescription) {
        this.schemaDescription = schemaDescription;
    }

    public void setBatchSizeDefault(Integer batchSizeDefaultValue) {
       this.batchSizeDefault = batchSizeDefaultValue;
    }

    public void setProvidedJdbcDefault(final boolean providedJdbc) {
        this.providedJdbc = providedJdbc;
    }
}
