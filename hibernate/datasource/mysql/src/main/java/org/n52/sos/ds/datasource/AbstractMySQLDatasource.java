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
package org.n52.sos.ds.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.n52.hibernate.spatial.dialect.mysql.TimestampMySQL8SpatialDialect;
import org.n52.faroe.ConfigurationError;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class AbstractMySQLDatasource extends AbstractHibernateFullDBDatasource {

    protected static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";

    protected static final Pattern JDBC_URL_PATTERN = Pattern.compile("^jdbc:mysql://([^:]+):([0-9]+)/(.*)$");

    protected static final String USERNAME_DESCRIPTION =
            "Your database server user name. The default value for MySQL is \"root\".";

    protected static final String USERNAME_DEFAULT_VALUE = "root";

    protected static final String PASSWORD_DESCRIPTION =
            "Your database server password. The default value is \"mysql\".";

    protected static final String PASSWORD_DEFAULT_VALUE = "mysql";

    protected static final String HOST_DESCRIPTION =
            "Set this to the IP/net location of MySQL database server. The default value for MySQL is \"localhost\".";

    protected static final String PORT_DESCRIPTION =
            "Set this to the port number of your PostgreSQL server. The default value for MySQL is 3306.";

    protected static final int PORT_DEFAULT_VALUE = 3306;

    protected static final String SCHEMA_DEFAULT_VALUE = "sos";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMySQLDatasource.class);

    AbstractMySQLDatasource(boolean supportsSchema) {
        super(supportsSchema);
        setUsernameDefault(USERNAME_DEFAULT_VALUE);
        setUsernameDescription(USERNAME_DESCRIPTION);
        setPasswordDefault(PASSWORD_DEFAULT_VALUE);
        setPasswordDescription(PASSWORD_DESCRIPTION);
        setDatabaseDefault(DATABASE_DEFAULT_VALUE);
        setDatabaseDescription(DATABASE_DESCRIPTION);
        setHostDefault(HOST_DEFAULT_VALUE);
        setHostDescription(HOST_DESCRIPTION);
        setPortDefault(PORT_DEFAULT_VALUE);
        setPortDescription(PORT_DESCRIPTION);
    }

    @Override
    public boolean checkSchemaCreation(Map<String, Object> settings) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            stmt = conn.createStatement();
            final String schema = (String) settings.get(createSchemaDefinition().getKey());
            // mysql uses backticks (`) to quote
            final String schemaPrefix = schema == null ? "" : "`" + schema + "`.";
            final String testTable = schemaPrefix + "sos_installer_test_table";
            final String dropTestTable = String.format("DROP TABLE IF EXISTS %1$s;", testTable);
            final String createTestTable = String.format("CREATE TABLE %1$s (id integer NOT NULL);", testTable);
            stmt.execute(dropTestTable);
            stmt.execute(createTestTable);
            stmt.execute(dropTestTable);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            close(stmt);
            close(conn);
        }
    }

    @Override
    public void clear(Properties properties) {
        Map<String, Object> settings = parseDatasourceProperties(properties);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            List<String> names = getQuotedSchemaTableNames(settings, conn);
            if (!names.isEmpty()) {
                List<String> list = Lists.newArrayList();
                list.add("SET FOREIGN_KEY_CHECKS=0;");
                for (String name : names) {
                    list.add(String.format("truncate %s;", name));
                }
                list.add("SET FOREIGN_KEY_CHECKS=1;");
                stmt = conn.createStatement();
                for (String sql : list) {
                    LOGGER.debug("Executed clear datasource SQL statement: {}", sql);
                    stmt.execute(sql);
                }

            }
        } catch (SQLException ex) {
            throw new ConfigurationError(ex);
        } finally {
            close(stmt);
            close(conn);
        }
    }

    @Override
    public boolean supportsClear() {
        return true;
    }

    @Override
    protected String[] parseURL(String url) {
        Matcher matcher = JDBC_URL_PATTERN.matcher(url);
        matcher.find();
        return new String[] { matcher.group(1), matcher.group(2), matcher.group(3) };
    }

    @Override
    protected String toURL(Map<String, Object> settings) {
        String url = String.format("jdbc:mysql://%s:%d/%s", settings.get(HOST_KEY), settings.get(PORT_KEY),
                settings.get(DATABASE_KEY));
        return url;
    }

    @Override
    protected Dialect createDialect() {
        return new TimestampMySQL8SpatialDialect();
    }

    @Override
    protected String getDriverClass() {
        return MYSQL_DRIVER_CLASS;
    }

    @Override
    protected Connection openConnection(Map<String, Object> settings) throws SQLException {
        try {
            String jdbc = toURL(settings);
            Class.forName(getDriverClass());
            String pass = (String) settings.get(HibernateConstants.CONNECTION_PASSWORD);
            String user = (String) settings.get(HibernateConstants.CONNECTION_USERNAME);
            precheckDriver(jdbc, user, pass);
            return DriverManager.getConnection(jdbc, user, pass);
        } catch (ClassNotFoundException ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    protected void validatePrerequisites(Connection arg0, Metadata arg1, Map<String, Object> arg2) {
    }

    @Override
    protected String[] checkCreateSchema(String[] script) {
        String[] checkSchema = super.checkCreateSchema(script);
        for (int i = 0; i < checkSchema.length; i++) {
            // TODO find better solution. E.g. enhance
            // MySQLSpatial5InnoDBTimestampDialect to set explicitly "NULL" for
            // timestamp if defined as NULLable in mappings
            if (checkSchema[i].startsWith("create table")) {
                if (checkSchema[i].contains("observation")) {
                    String createObservationTable = checkSchema[i];
                    createObservationTable = createObservationTable.replaceAll("validTimeStart timestamp default NULL",
                            "validTimeStart timestamp NULL default NULL");
                    createObservationTable = createObservationTable.replaceAll("validTimeEnd timestamp default NULL",
                            "validTimeEnd timestamp NULL default NULL");
                    checkSchema[i] = createObservationTable;
                } else if (checkSchema[i].contains("validProcedureTime")) {
                    String createObservationTable = checkSchema[i];
                    createObservationTable = createObservationTable.replaceAll("endTime timestamp default NULL",
                            "endTime timestamp NULL default NULL");
                    checkSchema[i] = createObservationTable;
                }
            }
        }
        return checkSchema;
    }
}
