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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.spatial.dialect.sqlserver.SqlServer2008SpatialDialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class AbstractSqlServerDatasource extends AbstractHibernateFullDBDatasource {
    
    protected static final String URL_INSTANCE = "instance=";
    
    protected static final String URL_DATABASE_NAME = "databaseName=";

    protected static final String INSTANCE_KEY = "jdbc.instance";

    protected static final String INSTANCE_TITLE = "SQL Server instance";

    protected static final String INSTANCE_DESCRIPTION =
            "Your SQL Server instance. The default value for SQL Server is \"SQLEXPRESS\".";

    protected static final String INSTANCE_DEFAULT = "SQLEXPRESS";

    protected static final String SQL_SERVER_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    
    protected static final Pattern JDBC_URL_PATTERN = Pattern.compile("^jdbc:sqlserver://([^:]+):([0-9]+);" + URL_INSTANCE + "([^:]+);" + URL_DATABASE_NAME + "([^:]+)");

    protected static final String USERNAME_DESCRIPTION =
            "Your database server user name. The default value for SQL Server is \"sqlserver\".";

    protected static final String USERNAME_DEFAULT_VALUE = "sqlserver";

    protected static final String PASSWORD_DESCRIPTION =
            "Your database server password. The default value is \"sqlserver\".";

    protected static final String PASSWORD_DEFAULT_VALUE = "sqlserver";

    protected static final String HOST_DESCRIPTION =
            "Set this to the IP/net location of SQL Server database server. The default value for SQL Server is \"localhost\".";

    protected static final String PORT_DESCRIPTION =
            "Set this to the port number of your SQL Server server. The default value for SQL Server is 1433.";

    protected static final int PORT_DEFAULT_VALUE = 1433;

    private static final boolean PROVIDED_JDBC_DEFAULT_VALUE = true;

    protected static final String SCHEMA_DEFAULT_VALUE = "dbo";

    public AbstractSqlServerDatasource() {
        super();
        setUsernameDefault(USERNAME_DEFAULT_VALUE);
        setUsernameDescription(USERNAME_DESCRIPTION);
        setPasswordDefault(PASSWORD_DEFAULT_VALUE);
        setPasswordDescription(PASSWORD_DESCRIPTION);
        setDatabaseDefault(DATABASE_DEFAULT_VALUE);
        setDatabaseDescription(HOST_DESCRIPTION);
        setHostDefault(HOST_DEFAULT_VALUE);
        setHostDescription(HOST_DESCRIPTION);
        setPortDefault(PORT_DEFAULT_VALUE);
        setPortDescription(PORT_DESCRIPTION);
        setSchemaDefault(SCHEMA_DEFAULT_VALUE);
        setSchemaDescription(SCHEMA_DESCRIPTION);
        setProvidedJdbcDefault(PROVIDED_JDBC_DEFAULT_VALUE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        Set<SettingDefinition<?, ?>> settingDefinitions = super.getSettingDefinitions();
        return CollectionHelper.union(
                Sets.<SettingDefinition<?, ?>> newHashSet(createInstanceDefinition(INSTANCE_DEFAULT)),
                settingDefinitions);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(final Properties current) {
        final Map<String, Object> settings = parseDatasourceProperties(current);
        return CollectionHelper.union(Sets
                .<SettingDefinition<?, ?>> newHashSet(createInstanceDefinition((String) settings.get(INSTANCE_KEY))),
                super.getChangableSettingDefinitions(current));
    }

    protected StringSettingDefinition createInstanceDefinition(String instanceValue) {
        return new StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_2)
                .setKey(INSTANCE_KEY).setTitle(INSTANCE_TITLE).setDescription(INSTANCE_DESCRIPTION)
                .setDefaultValue(instanceValue);
    }

    @Override
    protected Dialect createDialect() {
        return new SqlServer2008SpatialDialect();
    }

    @Override
    protected String getDriverClass() {
        return SQL_SERVER_DRIVER_CLASS;
    }

    @Override
    public boolean checkSchemaCreation(Map<String, Object> settings) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            stmt = conn.createStatement();
            String schema = (String) settings.get(createSchemaDefinition().getKey());
            schema = schema == null ? "" : "." + schema;
            final String command =
                    String.format("BEGIN; " + "DROP TABLE IF EXISTS \"%1$ssos_installer_test_table\"; "
                            + "CREATE TABLE \"%1$ssos_installer_test_table\" (id integer NOT NULL); "
                            + "DROP TABLE \"%1$ssos_installer_test_table\"; " + "END;", schema);
            stmt.execute(command);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            close(stmt);
            close(conn);
        }
    }

    @Override
    protected void validatePrerequisites(Connection con, DatabaseMetadata metadata, Map<String, Object> settings) {
        checkClasspath();
    }
    
    private void checkClasspath() throws ConfigurationException {
        try {
            Class.forName(SQL_SERVER_DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("SQL Server jar file (sqljdbc.jar) must be "
                    + "included in the server classpath. ", e);
        }
    }

    @Override
    protected String toURL(Map<String, Object> settings) {
        StringBuilder builder = new StringBuilder("jdbc:sqlserver://");
        builder.append(settings.get(HOST_KEY)).append(Constants.COLON_CHAR);
        builder.append(settings.get(PORT_KEY)).append(Constants.SEMICOLON_CHAR);
        builder.append(URL_INSTANCE).append(settings.get(INSTANCE_KEY)).append(Constants.SEMICOLON_CHAR);
        builder.append(URL_DATABASE_NAME).append(settings.get(DATABASE_KEY));
//        String url =
//                String.format("jdbc:sqlserver://%s:%d;instance=%s;databaseName=%s", settings.get(HOST_KEY),
//                        settings.get(PORT_KEY), settings.get(INSTANCE_KEY), settings.get(DATABASE_KEY));
        return builder.toString();
    }
    
    @Override
    protected String[] parseURL(String url) {
        Matcher matcher = JDBC_URL_PATTERN.matcher(url);
        matcher.find();
        return new String[] { matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3) };
    }

    @Override
    protected Map<String, Object> parseDatasourceProperties(final Properties current) {
        super.parseDatasourceProperties(current);
        final Map<String, Object> settings = super.parseDatasourceProperties(current);
        // parse instance
        final String[] parsed = parseURL(current.getProperty(HibernateConstants.CONNECTION_URL));
        if (parsed.length == 4) {
            settings.put(INSTANCE_KEY, (String)parsed[3]);
        }
        return settings;
    }

    @Override
    public boolean supportsClear() {
        return true;
    }

    @Override
    public void clear(Properties properties) {
        Map<String, Object> settings = parseDatasourceProperties(properties);
        CustomConfiguration config = getConfig(settings);
        Iterator<Table> tables = config.getTableMappings();
        List<String> names = new LinkedList<String>();
        while (tables.hasNext()) {
            Table table = tables.next();
            if (table.isPhysicalTable()) {
                names.add(table.getName());
            }
        }
        if (!names.isEmpty()) {
            Connection conn = null;
            Statement stmt = null;
            try {
                conn = openConnection(settings);
                stmt = conn.createStatement();
                stmt.execute(String.format("truncate %s restart identity cascade", Joiner.on(", ").join(names)));
            } catch (SQLException ex) {
                throw new ConfigurationException(ex);
            } finally {
                close(stmt);
                close(conn);
            }
        }
    }

    @Override
    protected Connection openConnection(Map<String, Object> settings) throws SQLException {
        try {
            String jdbc = toURL(settings);
            Class.forName(getDriverClass());
            String pass = (String) settings.get(HibernateConstants.CONNECTION_PASSWORD);
            String user = (String) settings.get(HibernateConstants.CONNECTION_USERNAME);
            return DriverManager.getConnection(jdbc, user, pass);
        } catch (ClassNotFoundException ex) {
            throw new SQLException(ex);
        }
    }

}
