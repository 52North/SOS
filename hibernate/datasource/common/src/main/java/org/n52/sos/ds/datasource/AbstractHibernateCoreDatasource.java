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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.n52.faroe.SettingDefinition;
import org.n52.faroe.settings.BooleanSettingDefinition;
import org.n52.faroe.settings.ChoiceSettingDefinition;
import org.n52.faroe.settings.IntegerSettingDefinition;
import org.n52.faroe.settings.StringSettingDefinition;
import org.n52.iceland.ds.Datasource;
import org.n52.shetland.util.JavaHelper;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.util.HibernateConstants;

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

    protected static final String C3P0_CONNECTION_POOL = "org.hibernate.c3p0.internal.C3P0ConnectionProvider";

    protected static final String MIN_POOL_SIZE_KEY = "jdbc.pool.min";

    protected static final String MIN_POOL_SIZE_TITLE = "Minimum ConnectionPool size";

    protected static final String MIN_POOL_SIZE_DESCRIPTION = "Minimum size of the ConnectionPool";

    protected static final Integer MIN_POOL_SIZE_DEFAULT_VALUE = 10;

    protected static final String MAX_POOL_SIZE_KEY = "jdbc.pool.max";

    protected static final String MAX_POOL_SIZE_TITLE = "Maximum ConnectionPool size";

    protected static final String MAX_POOL_SIZE_DESCRIPTION = "Maximum size of the ConnectionPool";

    protected static final Integer MAX_POOL_SIZE_DEFAULT_VALUE = 30;

    protected static final String TIMEZONE_TITLE = "Datasource time zone";

    protected static final String TIMEZONE_DESCRIPTION =
            "Define the time zone of the datasource to ensure time is always queried in the defined time zone. "
                    + "Valid values are see "
                    + "<a href=\"http://docs.oracle.com/javase/8/docs/api/java/util/TimeZone.html\" "
                    + "target=\"_blank\">Java TimeZone</a>."
                    + " Default is UTC.";

    protected static final String TIMEZONE_KEY = "datasource.timezone";

    protected static final String TIMEZONE_DEFAULT_VALUE = "+00:00";

    protected static final String TIME_STRING_FORMAT_KEY = "datasource.timeStringFormat";

    protected static final String TIME_STRING_FORMAT_TITLE = "Datasource time string format";

    protected static final String TIME_STRING_FORMAT_DESCRIPTION =
            "Define the time string format of the datasource to ensure time is always "
            + "parsed in the defined time format";

    protected static final String TIME_STRING_FORMAT_DEFAULT_VALUE = "";

    protected static final String TIME_STRING_Z_KEY = "datasource.timeStringZt";

    protected static final String TIME_STRING_Z_TITLE = "Has the datasource time string a 'Z'";

    protected static final String TIME_STRING_Z_DESCRIPTION =
            "Define if the datasoucre time string uses a 'Z' instead of '+00:00'.";

    protected static final boolean TIME_STRING_Z_DEFAULT_VALUE = false;

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
    public Set<SettingDefinition<?>> getSettingDefinitions() {
        return Sets.<SettingDefinition<?>> newHashSet(createUsernameDefinition(usernameDefault),
                createPasswordDefinition(passwordDefault), createDatabaseDefinition(databaseDefault),
                createHostDefinition(hostDefault), createPortDefinition(portDefault),
                createMinPoolSizeDefinition(minPoolSizeDefault), createMaxPoolSizeDefinition(maxPoolSizeDefault),
                createTimeZoneDefinition(TIMEZONE_DEFAULT_VALUE),
                createTimeStringFormatDefinition(TIME_STRING_FORMAT_DEFAULT_VALUE),
                createTimeStringZDefinition(TIME_STRING_Z_DEFAULT_VALUE));
    }

    @Override
    public Set<SettingDefinition<?>> getChangableSettingDefinitions(Properties current) {
        Map<String, Object> settings = parseDatasourceProperties(current);
        return Sets.<SettingDefinition<?>> newHashSet(createUsernameDefinition((String) settings.get(USERNAME_KEY)),
                createPasswordDefinition((String) settings.get(PASSWORD_KEY)),
                createDatabaseDefinition((String) settings.get(DATABASE_KEY)),
                createHostDefinition((String) settings.get(HOST_KEY)),
                createPortDefinition(JavaHelper.asInteger(settings.get(PORT_KEY))),
                createMinPoolSizeDefinition(JavaHelper.asInteger(settings.get(MIN_POOL_SIZE_KEY))),
                createMaxPoolSizeDefinition(JavaHelper.asInteger(settings.get(MAX_POOL_SIZE_KEY))),
                createTimeZoneDefinition((String) settings.get(TIMEZONE_KEY)),
                createTimeStringFormatDefinition((String) settings.get(TIME_STRING_FORMAT_KEY)),
                createTimeStringZDefinition((boolean) settings.get(TIME_STRING_Z_KEY)));
    }

    /**
     * Create settings definition for username
     *
     * @return Username settings definition
     */
    protected StringSettingDefinition createUsernameDefinition() {
        StringSettingDefinition def = new StringSettingDefinition();
        def.setGroup(BASE_GROUP);
        def.setOrder(1);
        def.setKey(USERNAME_KEY);
        def.setTitle(USERNAME_TITLE);
        return def;
    }

    protected StringSettingDefinition createUsernameDefinition(String defaultValue) {
        StringSettingDefinition def = createUsernameDefinition();
        def.setDescription(usernameDescription);
        def.setDefaultValue(defaultValue);
        return def;
    }

    /**
     * Create settings definition for password
     *
     * @return Password settings definition
     */
    protected StringSettingDefinition createPasswordDefinition() {
        StringSettingDefinition def = new StringSettingDefinition();
        def.setGroup(BASE_GROUP);
        def.setOrder(2);
        def.setKey(PASSWORD_KEY);
        def.setTitle(PASSWORD_TITLE);
        return def;
    }

    protected StringSettingDefinition createPasswordDefinition(String defaultValue) {
        StringSettingDefinition def = createPasswordDefinition();
        def.setDescription(passwordDescription);
        def.setDefaultValue(defaultValue);
        return def;
    }

    /**
     * Create settings definition for database name
     *
     * @return database name settings definition
     */
    protected StringSettingDefinition createDatabaseDefinition() {
        StringSettingDefinition def = new StringSettingDefinition();
        def.setGroup(BASE_GROUP);
        def.setOrder(3);
        def.setKey(DATABASE_KEY);
        def.setTitle(DATABASE_TITLE);
        def.setDescription(DATABASE_DESCRIPTION);
        def.setDefaultValue(DATABASE_DEFAULT_VALUE);
        return def;
    }

    protected StringSettingDefinition createDatabaseDefinition(String defaultValue) {
        StringSettingDefinition def = createDatabaseDefinition();
        def.setDescription(databaseDescription);
        def.setDefaultValue(defaultValue);
        return def;
    }

    /**
     * Create settings definition for host
     *
     * @return Host settings definition
     */
    protected StringSettingDefinition createHostDefinition() {
        StringSettingDefinition def = new StringSettingDefinition();
        def.setGroup(BASE_GROUP);
        def.setOrder(4);
        def.setKey(HOST_KEY);
        def.setTitle(HOST_TITLE);
        def.setDescription(HOST_DESCRIPTION);
        def.setDefaultValue(HOST_DEFAULT_VALUE);
        return def;
    }

    protected StringSettingDefinition createHostDefinition(String defaultValue) {
        StringSettingDefinition def = createHostDefinition();
        def.setDescription(hostDescription);
        def.setDefaultValue(defaultValue);
        return def;
    }

    /**
     * Create settings definition for port
     *
     * @return Port settings definition
     */
    protected IntegerSettingDefinition createPortDefinition() {
        IntegerSettingDefinition def = new IntegerSettingDefinition();
        def.setGroup(BASE_GROUP);
        def.setOrder(5);
        def.setKey(PORT_KEY);
        def.setTitle(PORT_TITLE);
        return def;
    }

    protected IntegerSettingDefinition createPortDefinition(int defaultValue) {
        IntegerSettingDefinition def = createPortDefinition();
        def.setDescription(portDescription);
        def.setDefaultValue(defaultValue);
        return def;
    }

    /**
     * Create settings definition for minimal connection pool size
     *
     * @return Minimal connection pool size settings definition
     */
    protected IntegerSettingDefinition createMinPoolSizeDefinition() {
        IntegerSettingDefinition def = new IntegerSettingDefinition();
        def.setGroup(ADVANCED_GROUP);
        def.setOrder(6);
        def.setKey(MIN_POOL_SIZE_KEY);
        def.setTitle(MIN_POOL_SIZE_TITLE);
        def.setDescription(MIN_POOL_SIZE_DESCRIPTION);
        def.setDefaultValue(MIN_POOL_SIZE_DEFAULT_VALUE);
        return def;
    }

    protected SettingDefinition<?> createMinPoolSizeDefinition(Integer defaultValue) {
        IntegerSettingDefinition def = createMinPoolSizeDefinition();
        def.setDefaultValue(defaultValue);
        return def;
    }

    /**
     * Create settings definition for maximal connection pool size
     *
     * @return Maximal connection pool size settings definition
     */
    protected IntegerSettingDefinition createMaxPoolSizeDefinition() {
        IntegerSettingDefinition def = new IntegerSettingDefinition();
        def.setGroup(ADVANCED_GROUP);
        def.setOrder(7);
        def.setKey(MAX_POOL_SIZE_KEY);
        def.setTitle(MAX_POOL_SIZE_TITLE);
        def.setDescription(MAX_POOL_SIZE_DESCRIPTION);
        def.setDefaultValue(MAX_POOL_SIZE_DEFAULT_VALUE);
        return def;
    }

    protected SettingDefinition<?> createMaxPoolSizeDefinition(Integer defaultValue) {
        IntegerSettingDefinition def = createMaxPoolSizeDefinition();
        def.setDefaultValue(defaultValue);
        return def;
    }

    /**
     * Create settings definition for time zone
     *
     * @return Time zone settings definition
     */
    protected ChoiceSettingDefinition createTimeZoneDefinition() {
        ChoiceSettingDefinition def = new ChoiceSettingDefinition();
        def.setGroup(ADVANCED_GROUP);
        def.setOrder(8);
        def.setKey(TIMEZONE_KEY);
        def.setTitle(TIMEZONE_TITLE);
        def.setDescription(TIMEZONE_DESCRIPTION);
        def.setDefaultValue(TIMEZONE_DEFAULT_VALUE);
        def.setOptions(getTimeZoneValues());
        def.setOptional(true);
        return def;
    }

    protected ChoiceSettingDefinition createTimeZoneDefinition(String defaultValue) {
        ChoiceSettingDefinition def = createTimeZoneDefinition();
        def.setDefaultValue(defaultValue);
        return def;
    }

    protected Map<String, String> getTimeZoneValues() {
        Set<String> offsets = new TreeSet<String>();
        LocalDateTime dt = LocalDateTime.now();
        for (String zone : ZoneId.getAvailableZoneIds()) {
            ZoneId id = ZoneId.of(zone);
            ZonedDateTime zdt = dt.atZone(id);
            ZoneOffset offset = zdt.getOffset();
            offsets.add(offset.getId().replaceAll("Z", TIMEZONE_DEFAULT_VALUE));
        }
        return offsets.stream().collect(Collectors.toMap(o -> o, o -> o));
    }

    protected StringSettingDefinition createTimeStringFormatDefinition() {
        StringSettingDefinition def = new StringSettingDefinition();
        def.setDefaultValue(TIME_STRING_FORMAT_DEFAULT_VALUE).setGroup(ADVANCED_GROUP).setKey(TIME_STRING_FORMAT_KEY)
                .setTitle(TIME_STRING_FORMAT_TITLE).setDescription(TIME_STRING_FORMAT_DESCRIPTION).setOptional(true);
        def.setOrder(100);
        return def;
    }

    protected StringSettingDefinition createTimeStringFormatDefinition(String defaultValue) {
        StringSettingDefinition def = createTimeStringFormatDefinition();
        def.setDefaultValue(defaultValue);
        return def;
    }

    protected BooleanSettingDefinition createTimeStringZDefinition() {
        BooleanSettingDefinition def = new BooleanSettingDefinition();
        def.setDefaultValue(TIME_STRING_Z_DEFAULT_VALUE).setGroup(ADVANCED_GROUP).setKey(TIME_STRING_Z_KEY)
                .setTitle(TIME_STRING_Z_TITLE).setDescription(TIME_STRING_Z_DESCRIPTION).setOptional(true);
        def.setOrder(11);
        return def;
    }

    protected BooleanSettingDefinition createTimeStringZDefinition(boolean defaultValue) {
        BooleanSettingDefinition def = createTimeStringZDefinition();
        def.setDefaultValue(defaultValue);
        return def;
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
