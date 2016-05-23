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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.spatial.dialect.postgis.PostgisDialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.exception.ConfigurationException;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 *
 */
public abstract class AbstractPostgresDatasource extends AbstractHibernateFullDBDatasource {

    protected static final String POSTGRES_DRIVER_CLASS = "org.postgresql.Driver";

    protected static final Pattern JDBC_URL_PATTERN = Pattern.compile("^jdbc:postgresql://([^:]+):([0-9]+)/(.*)$");

    protected static final String USERNAME_DESCRIPTION =
            "Your database server user name. The default value for PostgreSQL is \"postgres\".";

    protected static final String USERNAME_DEFAULT_VALUE = "postgres";

    protected static final String PASSWORD_DESCRIPTION =
            "Your database server password. The default value is \"postgres\".";

    protected static final String PASSWORD_DEFAULT_VALUE = "postgres";

    protected static final String HOST_DESCRIPTION =
            "Set this to the IP/net location of PostgreSQL database server. The default value for PostgreSQL is \"localhost\".";

    protected static final String PORT_DESCRIPTION =
            "Set this to the port number of your PostgreSQL server. The default value for PostgreSQL is 5432.";

    protected static final int PORT_DEFAULT_VALUE = 5432;

    // public static final String CATALOG_DEFAULT_VALUE = "public";

    protected static final String SCHEMA_DEFAULT_VALUE = "public";

    protected static final String FUNC_POSTGIS_VERSION = "postgis_version()";

    protected static final String TAB_SPATIAL_REF_SYS = "spatial_ref_sys";

    public AbstractPostgresDatasource() {
        super();
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
        setSchemaDefault(SCHEMA_DEFAULT_VALUE);
        setSchemaDescription(SCHEMA_DESCRIPTION);
    }

    @Override
    protected Dialect createDialect() {
        return new PostgisDialect();
    }

    @Override
    protected String getDriverClass() {
        return POSTGRES_DRIVER_CLASS;
    }

    @Override
    public boolean checkSchemaCreation(Map<String, Object> settings) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            stmt = conn.createStatement();
            final String schema = (String) settings.get(createSchemaDefinition().getKey());
            final String schemaPrefix = schema == null ? "" : "\"" + schema + "\".";
            final String testTable = schemaPrefix + "sos_installer_test_table";
            final String command =
                    String.format("BEGIN; " + "DROP TABLE IF EXISTS %1$s; "
                            + "CREATE TABLE %1$s (id integer NOT NULL); "
                            + "DROP TABLE %1$s; " + "END;", testTable);
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
        checkPostgis(con, settings);
        checkSpatialRefSys(con, metadata, settings);
    }

    protected void checkPostgis(Connection con, Map<String, Object> settings) {
        Statement stmt = null;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(SELECT);
            builder.append(BLANK_CHAR);
            builder.append(FUNC_POSTGIS_VERSION);
            builder.append(SEMICOLON_CHAR);
            stmt = con.createStatement();
            stmt.execute(builder.toString());
            // TODO check PostGIS version
        } catch (SQLException ex) {
            throw new ConfigurationException("PostGIS does not seem to be installed.", ex);
        } finally {
            close(stmt);
        }
    }

    protected void checkSpatialRefSys(Connection con, DatabaseMetadata metadata, Map<String, Object> settings) {
        Statement stmt = null;
        try {
            if (!metadata.isTable("spatial_ref_sys")) {
                throw new ConfigurationException("Missing 'spatial_ref_sys' table.");
            }
            StringBuilder builder = new StringBuilder();
            builder.append(SELECT);
            builder.append(BLANK_CHAR);
            builder.append(DEFAULT_COUNT);
            builder.append(BLANK_CHAR);
            builder.append(FROM);
            builder.append(BLANK_CHAR);
            builder.append(TAB_SPATIAL_REF_SYS);
            builder.append(SEMICOLON_CHAR);
            stmt = con.createStatement();
            stmt.execute(builder.toString());
        } catch (SQLException ex) {
            throw new ConfigurationException("Can not read from table 'spatial_ref_sys'", ex);
        } finally {
            close(stmt);
        }
    }

    @Override
    protected String toURL(Map<String, Object> settings) {
        String url =
                String.format("jdbc:postgresql://%s:%d/%s", settings.get(HOST_KEY), settings.get(PORT_KEY),
                        settings.get(DATABASE_KEY));
        return url;
    }

    @Override
    protected String[] parseURL(String url) {
        Matcher matcher = JDBC_URL_PATTERN.matcher(url);
        matcher.find();
        return new String[] { matcher.group(1), matcher.group(2), matcher.group(3) };
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

    @Override
    protected String[] checkDropSchema(String[] dropSchema) {
        List<String> checkedSchema = Lists.newLinkedList();
        for (String string : dropSchema) {
            if (!string.startsWith("alter")) {
                checkedSchema.add(string);
            }
        }
        return checkScriptForGeneratedAndDuplicatedEntries(checkedSchema.toArray(new String[checkedSchema.size()]));
    }

    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        Properties p = super.getDatasourceProperties(settings);
        p.put(HibernateConstants.C3P0_PREFERRED_TEST_QUERY, "SELECT 1");
        return p;
    }


}
