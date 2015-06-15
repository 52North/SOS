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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ds.Datasource;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.util.JavaHelper;

import com.google.common.collect.Sets;

public abstract class AbstractHibernateCoreDatasource implements Datasource, HibernateDatasourceConstants {


    protected static final String USERNAME_TITLE = "User Name";

    protected static final String PASSWORD_TITLE = "Password";

    protected static final String DATABASE_KEY = "jdbc.database";

    protected static final String DATABASE_TITLE = "Database";

    protected static final String DATABASE_DESCRIPTION =
            "Set this to the name of the database you want to use for SOS.";

    protected static final String DATABASE_DEFAULT_VALUE = "sos";

    protected static final String HOST_KEY = "jdbc.host";

    protected static final String HOST_TITLE = "Host";

    protected static final String HOST_DESCRIPTION =
            "Set this to the IP/net location of the database server. The default value for is \"localhost\".";

    protected static final String HOST_DEFAULT_VALUE = "localhost";

    protected static final String PORT_KEY = "jdbc.port";

    protected static final String PORT_TITLE = "Database Port";

    protected static final String USERNAME_KEY = HibernateConstants.CONNECTION_USERNAME;

    protected static final String PASSWORD_KEY = HibernateConstants.CONNECTION_PASSWORD;

    protected static final String C3P0_CONNECTION_POOL =
            "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider";

//    protected static final Boolean PROVIDED_JDBC_DRIVER_DEFAULT_VALUE = false;

//    protected static final String PROVIDED_JDBC_DRIVER_TITLE = "Provided JDBC driver";
//
//    protected static final String PROVIDED_JDBC_DRIVER_DESCRIPTION =
//            "Is the JDBC driver provided and should not be derigistered during shutdown?";
//
//    protected static final String PROVIDED_JDBC_DRIVER_KEY = "sos.jdbc.provided";

    protected static final String MIN_POOL_SIZE_KEY = "jdbc.pool.min";

    protected static final String MIN_POOL_SIZE_TITLE = "Minimum ConnectionPool size";

    protected static final String MIN_POOL_SIZE_DESCRIPTION = "Minimum size of the ConnectionPool";

    protected static final Integer MIN_POOL_SIZE_DEFAULT_VALUE = 10;

    protected static final String MAX_POOL_SIZE_KEY = "jdbc.pool.max";

    protected static final String MAX_POOL_SIZE_TITLE = "Maximum ConnectionPool size";

    protected static final String MAX_POOL_SIZE_DESCRIPTION = "Maximum size of the ConnectionPool";

    protected static final Integer MAX_POOL_SIZE_DEFAULT_VALUE = 30;
    private String usernameDefault;
    private String usernameDescription;
    private String passwordDefault;
    private String passwordDescription;
    private String databaseDefault;
    private String databaseDescription;
    private String hostDefault;
    private String hostDescription;
    private int portDefault;
    private String portDescription;
    private int minPoolSizeDefault;
    private int maxPoolSizeDefault;

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Sets.<SettingDefinition<?, ?>> newHashSet(
                        createUsernameDefinition(usernameDefault),
                        createPasswordDefinition(passwordDefault),
                        createDatabaseDefinition(databaseDefault),
                        createHostDefinition(hostDefault),
                        createPortDefinition(portDefault),
                        createMinPoolSizeDefinition(minPoolSizeDefault),
                        createMaxPoolSizeDefinition(maxPoolSizeDefault));
    }

    @Override
    public Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(Properties current) {
        Map<String, Object> settings = parseDatasourceProperties(current);
        return Sets.<SettingDefinition<?, ?>> newHashSet(
                createUsernameDefinition((String) settings.get(USERNAME_KEY)),
                createPasswordDefinition((String) settings.get(PASSWORD_KEY)),
                createDatabaseDefinition((String) settings.get(DATABASE_KEY)),
                createHostDefinition((String) settings.get(HOST_KEY)),
                createPortDefinition(JavaHelper.asInteger(settings.get(PORT_KEY))),
                createMinPoolSizeDefinition(JavaHelper.asInteger(settings.get(MIN_POOL_SIZE_KEY))),
                createMaxPoolSizeDefinition(JavaHelper.asInteger(settings.get(MAX_POOL_SIZE_KEY))));
    }

    /**
     * Create settings definition for username
     *
     * @return Username settings definition
     */
    protected StringSettingDefinition createUsernameDefinition() {
        return new StringSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_1)
                .setKey(USERNAME_KEY)
                .setTitle(USERNAME_TITLE);
    }

    /**
     * Create settings definition for password
     *
     * @return Password settings definition
     */
    protected StringSettingDefinition createPasswordDefinition() {
        return new StringSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_2)
                .setKey(PASSWORD_KEY)
                .setTitle(PASSWORD_TITLE);
    }

    /**
     * Create settings definition for database name
     *
     * @return database name settings definition
     */
    protected StringSettingDefinition createDatabaseDefinition() {
        return new StringSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_3)
                .setKey(DATABASE_KEY)
                .setTitle(DATABASE_TITLE)
                .setDescription(DATABASE_DESCRIPTION)
                .setDefaultValue(DATABASE_DEFAULT_VALUE);
    }

    /**
     * Create settings definition for host
     *
     * @return Host settings definition
     */
    protected StringSettingDefinition createHostDefinition() {
        return new StringSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_4)
                .setKey(HOST_KEY)
                .setTitle(HOST_TITLE)
                .setDescription(HOST_DESCRIPTION)
                .setDefaultValue(HOST_DEFAULT_VALUE);
    }

    /**
     * Create settings definition for port
     *
     * @return Port settings definition
     */
    protected IntegerSettingDefinition createPortDefinition() {
        return new IntegerSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_5)
                .setKey(PORT_KEY)
                .setTitle(PORT_TITLE);
    }

    /**
     * Create settings definition for minimal connection pool size
     *
     * @return Minimal connection pool size settings definition
     */
    protected IntegerSettingDefinition createMinPoolSizeDefinition() {
        return new IntegerSettingDefinition()
                .setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_6)
                .setKey(MIN_POOL_SIZE_KEY)
                .setTitle(MIN_POOL_SIZE_TITLE)
                .setDescription(MIN_POOL_SIZE_DESCRIPTION)
                .setDefaultValue(MIN_POOL_SIZE_DEFAULT_VALUE);
    }

    /**
     * Create settings definition for maximal connection pool size
     *
     * @return Maximal connection pool size settings definition
     */
    protected IntegerSettingDefinition createMaxPoolSizeDefinition() {
        return new IntegerSettingDefinition()
                .setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_7)
                .setKey(MAX_POOL_SIZE_KEY)
                .setTitle(MAX_POOL_SIZE_TITLE)
                .setDescription(MAX_POOL_SIZE_DESCRIPTION)
                .setDefaultValue(MAX_POOL_SIZE_DEFAULT_VALUE);
    }

    protected StringSettingDefinition createUsernameDefinition(String defaultValue) {
        return createUsernameDefinition()
                .setDescription(usernameDescription)
                .setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createPasswordDefinition(String defaultValue) {
        return createPasswordDefinition()
                .setDescription(passwordDescription)
                .setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createDatabaseDefinition(String defaultValue) {
        return createDatabaseDefinition()
                .setDescription(databaseDescription)
                .setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createHostDefinition(String defaultValue) {
        return createHostDefinition()
                .setDescription(hostDescription)
                .setDefaultValue(defaultValue);
    }

    protected IntegerSettingDefinition createPortDefinition(int defaultValue) {
        return createPortDefinition()
                .setDescription(portDescription)
                .setDefaultValue(defaultValue);
    }

    protected SettingDefinition<?, ?> createMinPoolSizeDefinition(Integer defaultValue) {
        return createMinPoolSizeDefinition()
                .setDefaultValue(defaultValue);
    }

    protected SettingDefinition<?, ?> createMaxPoolSizeDefinition(Integer defaultValue) {
        return createMaxPoolSizeDefinition()
                .setDefaultValue(defaultValue);
    }

    /**
     * @param usernameDefault
     *            the usernameDefault to set
     */
    public void setUsernameDefault(String usernameDefault) {
        this.usernameDefault = usernameDefault;
    }

    /**
     * @param usernameDescription
     *            the usernameDescription to set
     */
    public void setUsernameDescription(String usernameDescription) {
        this.usernameDescription = usernameDescription;
    }

    /**
     * @param passwordDefault
     *            the passwordDefault to set
     */
    public void setPasswordDefault(String passwordDefault) {
        this.passwordDefault = passwordDefault;
    }

    /**
     * @param passwordDescription
     *            the passwordDescription to set
     */
    public void setPasswordDescription(String passwordDescription) {
        this.passwordDescription = passwordDescription;
    }

    /**
     * @param databaseDefault
     *            the databaseDefault to set
     */
    public void setDatabaseDefault(String databaseDefault) {
        this.databaseDefault = databaseDefault;
    }

    /**
     * @param databaseDescription
     *            the databaseDescription to set
     */
    public void setDatabaseDescription(String databaseDescription) {
        this.databaseDescription = databaseDescription;
    }

    /**
     * @param hostDefault
     *            the hostDefault to set
     */
    public void setHostDefault(String hostDefault) {
        this.hostDefault = hostDefault;
    }

    /**
     * @param hostDescription
     *            the hostDescription to set
     */
    public void setHostDescription(String hostDescription) {
        this.hostDescription = hostDescription;
    }

    /**
     * @param portDefault
     *            the portDefault to set
     */
    public void setPortDefault(int portDefault) {
        this.portDefault = portDefault;
    }

    /**
     * @param portDescription
     *            the portDescription to set
     */
    public void setPortDescription(String portDescription) {
        this.portDescription = portDescription;
    }

    public void setMinPoolSizeDefault(int minPoolSizeDefault) {
        this.minPoolSizeDefault = minPoolSizeDefault;
    }

    public void setMaxPoolSizeDefault(int maxPoolSizeDefault) {
        this.maxPoolSizeDefault = maxPoolSizeDefault;
    }

    @Override
    public Properties getDatasourceProperties(Properties current, Map<String, Object> changed) {
        return getDatasourceProperties(mergeProperties(current, changed));
    }

    /**
     * Merge current properties with changed settings
     *
     * @param current
     *            Current properties
     * @param changed
     *            Changed settings
     * @return Updated settings
     */
    protected Map<String, Object> mergeProperties(Properties current, Map<String, Object> changed) {
        Map<String, Object> settings = parseDatasourceProperties(current);
        settings.putAll(changed);
        return settings;
    }

    /**
     * Parse datasource properties to map
     *
     * @param current
     *            Current datasource properties
     * @return Map with String key and Object value
     */
    protected abstract Map<String, Object> parseDatasourceProperties(Properties current);

    /**
     * Converts the given connection settings into a valid JDBC string.
     *
     * @param settings
     *            the connection settings, containing keys from
     *            {@link AbstractHibernateDatasource} (<code>HOST_KEY</code>,
     *            <code>PORT_KEY</code>, ...).
     * @return a valid JDBC connection string
     */
    protected abstract String toURL(Map<String, Object> settings);

    /**
     * Parses the given JDBC string searching for host, port and database
     *
     * @param url
     *            the JDBC string to parse
     * @return an array with three strings:
     *         <ul>
     *         <li>[0] - Host
     *         <li>[1] - Port (parseable int as string)
     *         <li>[2] - Database
     *         </ul>
     */
    protected abstract String[] parseURL(String url);

}
