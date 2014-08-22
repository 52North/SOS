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
package org.n52.sos.ds.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public abstract class AbstractHibernateFullDBDatasource extends AbstractHibernateDatasource {
//    private String usernameDefault, usernameDescription;
//
//    private String passwordDefault, passwordDescription;
//
//    private String databaseDefault, databaseDescription;
//
//    private String hostDefault, hostDescription;
//
//    private int portDefault,minPoolSizeDefault, maxPoolSizeDefault, ;
//
//    private String portDescription;

    private String schemaDefault, schemaDescription;

    private int batchSizeDefault;

    private boolean providedJdbc;

    AbstractHibernateFullDBDatasource() {
        super();
        setMinPoolSizeDefault(MIN_POOL_SIZE_DEFAULT_VALUE);
        setMaxPoolSizeDefault(MAX_POOL_SIZE_DEFAULT_VALUE);
        setBatchSizeDefault(BATCH_SIZE_DEFAULT_VALUE);
    }

    @Deprecated
    public AbstractHibernateFullDBDatasource(final String usernameDefault, final String usernameDescription,
            final String passwordDefault, final String passwordDescription, final String databaseDefault,
            final String databaseDescription, final String hostDefault, final String hostDescription,
            final int portDefault, final String portDescription, final String schemaDefault,
            final String schemaDescription) {
        super();
    }

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        Set<SettingDefinition<?, ?>> set = super.getSettingDefinitions();
        set.add(createSchemaDefinition(schemaDefault));
        set.add(createBatchSizeDefinition(batchSizeDefault));
        set.add(createProvidedJdbcDriverDefinition(providedJdbc));
        set.add(getOldConceptDefiniton());
        if (isTransactionalDatasource()) {
            set.add(getTransactionalDefiniton());
        }
        if (isSpatialFilteringProfileDatasource()) {
            set.add(getSpatialFilteringProfileDefiniton());
        }
        return set;
    }

    @Override
    public Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(final Properties current) {
        final Map<String, Object> settings = parseDatasourceProperties(current);
        return Sets.<SettingDefinition<?, ?>> newHashSet(
                createUsernameDefinition((String) settings.get(USERNAME_KEY)),
                createPasswordDefinition((String) settings.get(PASSWORD_KEY)),
                createDatabaseDefinition((String) settings.get(DATABASE_KEY)),
                createHostDefinition((String) settings.get(HOST_KEY)),
                createPortDefinition(JavaHelper.asInteger(settings.get(PORT_KEY))),
                createSchemaDefinition((String) settings.get(SCHEMA_KEY)),
                createMinPoolSizeDefinition(JavaHelper.asInteger(settings.get(MIN_POOL_SIZE_KEY))),
                createMaxPoolSizeDefinition(JavaHelper.asInteger(settings.get(MAX_POOL_SIZE_KEY))),
                createBatchSizeDefinition(JavaHelper.asInteger(settings.get(BATCH_SIZE_KEY))));
    }

//    protected StringSettingDefinition createUsernameDefinition(final String defaultValue) {
//        return createUsernameDefinition().setDescription(usernameDescription).setDefaultValue(defaultValue);
//    }
//
//    protected StringSettingDefinition createPasswordDefinition(final String defaultValue) {
//        return createPasswordDefinition().setDescription(passwordDescription).setDefaultValue(defaultValue);
//    }
//
//    protected StringSettingDefinition createDatabaseDefinition(final String defaultValue) {
//        return createDatabaseDefinition().setDescription(databaseDescription).setDefaultValue(defaultValue);
//    }
//
//    protected StringSettingDefinition createHostDefinition(final String defaultValue) {
//        return createHostDefinition().setDescription(hostDescription).setDefaultValue(defaultValue);
//    }
//
//    protected IntegerSettingDefinition createPortDefinition(final int defaultValue) {
//        return createPortDefinition().setDescription(portDescription).setDefaultValue(defaultValue);
//    }
//
//    protected SettingDefinition<?, ?> createMinPoolSizeDefinition(final Integer defaultValue) {
//        return createMinPoolSizeDefinition().setDefaultValue(defaultValue);
//    }
//
//    protected SettingDefinition<?, ?> createMaxPoolSizeDefinition(final Integer defaultValue) {
//        return createMaxPoolSizeDefinition().setDefaultValue(defaultValue);
//    }

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
        p.put(HibernateConstants.DEFAULT_SCHEMA, settings.get(SCHEMA_KEY));
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
        p.put(HibernateConstants.JDBC_BATCH_SIZE, settings.get(BATCH_SIZE_KEY).toString());
        p.put(HibernateConstants.CONNECTION_AUTO_RECONNECT, "true");
        p.put(HibernateConstants.CONNECTION_AUTO_RECONNECT_FOR_POOLS, "true");
        p.put(HibernateConstants.CONNECTION_TEST_ON_BORROW, "true");
        p.put(PROVIDED_JDBC, settings.get(PROVIDED_JDBC_DRIVER_KEY).toString());
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
        settings.put(SCHEMA_KEY, current.getProperty(HibernateConstants.DEFAULT_SCHEMA));
        settings.put(USERNAME_KEY, current.getProperty(HibernateConstants.CONNECTION_USERNAME));
        settings.put(PASSWORD_KEY, current.getProperty(HibernateConstants.CONNECTION_PASSWORD));
        settings.put(MIN_POOL_SIZE_KEY, current.getProperty(HibernateConstants.C3P0_MIN_SIZE));
        settings.put(MAX_POOL_SIZE_KEY, current.getProperty(HibernateConstants.C3P0_MAX_SIZE));
        settings.put(BATCH_SIZE_KEY, current.getProperty(HibernateConstants.JDBC_BATCH_SIZE));
        settings.put(TRANSACTIONAL_KEY, isTransactional(current));
        settings.put(SPATIAL_FILTERING_PROFILE_KEY, isSpatialFilteringProfile(current));
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

//    /**
//     * Converts the given connection settings into a valid JDBC string.
//     *
//     * @param settings
//     *            the connection settings, containing keys from
//     *            {@link AbstractHibernateDatasource} (<code>HOST_KEY</code>,
//     *            <code>PORT_KEY</code>, ...).
//     * @return a valid JDBC connection string
//     */
//    protected abstract String toURL(Map<String, Object> settings);
//
//    /**
//     * Parses the given JDBC string searching for host, port and database
//     *
//     * @param url
//     *            the JDBC string to parse
//     * @return an array with three strings:
//     *         <ul>
//     *         <li>[0] - Host
//     *         <li>[1] - Port (parseable int as string)
//     *         <li>[2] - Database
//     *         </ul>
//     */
//    protected abstract String[] parseURL(String url);
//
//    /**
//     * @param usernameDefault
//     *            the usernameDefault to set
//     */
//    public void setUsernameDefault(final String usernameDefault) {
//        this.usernameDefault = usernameDefault;
//    }
//
//    /**
//     * @param usernameDescription
//     *            the usernameDescription to set
//     */
//    public void setUsernameDescription(final String usernameDescription) {
//        this.usernameDescription = usernameDescription;
//    }
//
//    /**
//     * @param passwordDefault
//     *            the passwordDefault to set
//     */
//    public void setPasswordDefault(final String passwordDefault) {
//        this.passwordDefault = passwordDefault;
//    }
//
//    /**
//     * @param passwordDescription
//     *            the passwordDescription to set
//     */
//    public void setPasswordDescription(final String passwordDescription) {
//        this.passwordDescription = passwordDescription;
//    }
//
//    /**
//     * @param databaseDefault
//     *            the databaseDefault to set
//     */
//    public void setDatabaseDefault(final String databaseDefault) {
//        this.databaseDefault = databaseDefault;
//    }
//
//    /**
//     * @param databaseDescription
//     *            the databaseDescription to set
//     */
//    public void setDatabaseDescription(final String databaseDescription) {
//        this.databaseDescription = databaseDescription;
//    }
//
//    /**
//     * @param hostDefault
//     *            the hostDefault to set
//     */
//    public void setHostDefault(final String hostDefault) {
//        this.hostDefault = hostDefault;
//    }
//
//    /**
//     * @param hostDescription
//     *            the hostDescription to set
//     */
//    public void setHostDescription(final String hostDescription) {
//        this.hostDescription = hostDescription;
//    }
//
//    /**
//     * @param portDefault
//     *            the portDefault to set
//     */
//    public void setPortDefault(final int portDefault) {
//        this.portDefault = portDefault;
//    }
//
//    /**
//     * @param portDescription
//     *            the portDescription to set
//     */
//    public void setPortDescription(final String portDescription) {
//        this.portDescription = portDescription;
//    }

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

//    public void setMinPoolSizeDefault(final int minPoolSizeDefault) {
//        this.minPoolSizeDefault = minPoolSizeDefault;
//    }
//
//    public void setMaxPoolSizeDefault(final int maxPoolSizeDefault) {
//        this.maxPoolSizeDefault = maxPoolSizeDefault;
//    }
//
//    public void setBatchSizeDefault(final int batchSizeDefault) {
//        this.batchSizeDefault = batchSizeDefault;
//    }
}
