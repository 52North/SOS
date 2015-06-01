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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oracle.jdbc.OracleDriver;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.spatial.dialect.oracle.OracleSpatial10gDialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.n52.sos.ds.Datasource;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract class for Oracle datasources
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public abstract class AbstractOracleDatasource extends AbstractHibernateFullDBDatasource {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractOracleDatasource.class);

    protected static final String ORACLE_DRIVER_CLASS = "oracle.jdbc.OracleDriver";

    protected static final Pattern JDBC_THIN_URL_PATTERN = Pattern
            .compile("^jdbc:oracle:thin:@//([^:]+):([0-9]+)/(.*)$");

    protected static final Pattern JDBC_OCI_URL_PATTERN = Pattern.compile("^jdbc:oracle:oci:@([^:]+):([0-9]+)/(.*)$");

    protected static final String USERNAME_DESCRIPTION = "Your database server user name. "
            + "The default value for Oracle Spatial is \"oracle\".";

    protected static final String USERNAME_DEFAULT_VALUE = "oracle";

    protected static final String PASSWORD_DESCRIPTION = "Your database server password. "
            + "The default value is \"oracle\".";

    protected static final String PASSWORD_DEFAULT_VALUE = "oracle";

    protected static final String HOST_DESCRIPTION = "Set this to the IP/net location of "
            + "Oracle Spatial database server. The default value for Oracle is " + "\"localhost\".";

    protected static final String PORT_DESCRIPTION = "Set this to the port number of your "
            + "Oracle Spatial server. The default value for Oracle is 1521.";

    protected static final int PORT_DEFAULT_VALUE = 1521;

    protected static final boolean PROVIDED_JDBC_DEFAULT_VALUE = true;

    protected static final String SCHEMA_DEFAULT_VALUE = "oracle";

    protected enum Mode {
        THIN, OCI
    }

    private Mode mode = Mode.OCI;

    public AbstractOracleDatasource() {
        super();
        setUsernameDefault(USERNAME_DEFAULT_VALUE);
        setUsernameDescription(USERNAME_DESCRIPTION);
        setPasswordDefault(PASSWORD_DEFAULT_VALUE);
        setPasswordDescription(PASSWORD_DESCRIPTION);
        setDatabaseDefault(DATABASE_DEFAULT_VALUE);
        setHostDefault(HOST_DEFAULT_VALUE);
        setHostDescription(HOST_DESCRIPTION);
        setPortDefault(PORT_DEFAULT_VALUE);
        setPortDescription(PORT_DESCRIPTION);
        setSchemaDefault(SCHEMA_DEFAULT_VALUE);
        setSchemaDescription(SCHEMA_DESCRIPTION);
        setProvidedJdbcDefault(PROVIDED_JDBC_DEFAULT_VALUE);
    }
    
    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
         Properties p = super.getDatasourceProperties(settings);
         p.put(HibernateConstants.CONNECION_FINDER, OracleC3P0ConnectionFinder.class.getName());
         return p;
    }

    @Override
    public boolean checkSchemaCreation(Map<String, Object> settings) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            stmt = conn.createStatement();
            doCheckSchemaCreation((String) settings.get(SCHEMA_KEY), stmt);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            close(stmt);
            close(conn);
        }
    }

    @Override
    protected String[] getPreSchemaScript() {
        return new String[] { "ALTER SESSION SET deferred_segment_creation=false" };
    }

    /**
     * A statement provided version of
     * {@link Datasource#checkSchemaCreation(Map)} for testing
     */
    void doCheckSchemaCreation(String schema, Statement stmt) throws SQLException {
        final String schemaPrefix = schema == null ? "" : "" + schema + "."; 
        final String testTable = schemaPrefix + "sos_test"; 
        final String command =
                String.format("BEGIN\n" + "  BEGIN\n" + "    EXECUTE IMMEDIATE 'DROP TABLE %1$s';\n"
                        + "  EXCEPTION\n" + "    WHEN OTHERS THEN\n" + "      IF SQLCODE != -942 THEN\n"
                        + "        RAISE;\n" + "      END IF;\n" + "  END;\n"
                        + "  EXECUTE IMMEDIATE 'CREATE TABLE %1$s (id integer NOT NULL)';\n"
                        + "  EXECUTE IMMEDIATE 'DROP TABLE %1$s';\n" + "END;\n", testTable);
        stmt.execute(command);
    }

    @Override
    public void clear(Properties properties) {
        Map<String, Object> settings = parseDatasourceProperties(properties);
        CustomConfiguration config = getConfig(settings);

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            stmt = conn.createStatement();

            Iterator<Table> tables = config.getTableMappings();
            List<String> names = new ArrayList<String>();
            while (tables.hasNext()) {
                Table table = tables.next();
                if (table.isPhysicalTable()) {
                    names.add(table.getName());
                }
            }

            while (names.size() > 0) {
                int clearedThisPass = 0;
                for (int i = names.size() - 1; i >= 0; i--) {
                    try {
                        stmt.execute("DELETE FROM " + names.get(i));
                        names.remove(i);
                        clearedThisPass++;
                    } catch (SQLException ex) {
                        // ignore
                    }
                }

                if (clearedThisPass == 0) {
                    throw new RuntimeException("Cannot clear!");
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot clear!", e);
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
    protected void validatePrerequisites(Connection con, DatabaseMetadata metadata, Map<String, Object> settings)
            throws ConfigurationException {
        checkClasspath();
    }
    
    @Override
    public void validateSchema(Map<String,Object> settings) {
        Connection conn = null;
        String schema = null;
        try {
            conn = openConnection(settings);
            DatabaseMetadata metadata = getDatabaseMetadata(conn, getConfig(settings));
            // fix problem with quoted tables
            schema = (String)settings.get(SCHEMA_KEY);
            settings.put(SCHEMA_KEY, null);
            getConfig(settings).validateSchema(getDialectInternal(), metadata);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } catch (HibernateException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
            settings.put(SCHEMA_KEY, schema);
        }
    }

    @Override
    protected Dialect createDialect() {
        return new OracleSpatial10gDialect();
    }

    @Override
    protected Connection openConnection(Map<String, Object> settings) throws SQLException {
        String pass = (String) settings.get(HibernateConstants.CONNECTION_PASSWORD);
        String user = (String) settings.get(HibernateConstants.CONNECTION_USERNAME);
        try {
            Class.forName(getDriverClass());
        } catch (ClassNotFoundException ex) {
            throw new SQLException(ex);
        }

        OracleDriver driver = new OracleDriver();
        Properties props = new Properties();
        props.put("user", user);
        props.put("password", pass);

        // Try OCI if it never failed previously
        if (mode == Mode.OCI) {
            try {
                return driver.connect(toOciUrl(settings), props);
            } catch (UnsatisfiedLinkError e) {
                LOG.error("Failed to use OCI driver. Falling back to thin.", e);
                mode = Mode.THIN;
            } catch (SQLException e) {
                LOG.error("Failed to use OCI driver. Falling back to thin.", e);
                mode = Mode.THIN;
            }
        }

        return driver.connect(toThinUrl(settings), props);
    }

    @Override
    protected String toURL(Map<String, Object> settings) {
        if (mode == Mode.OCI) {
            return toOciUrl(settings);
        } else {
            return toThinUrl(settings);
        }
    }

    private String toThinUrl(Map<String, Object> settings) {
        return String.format("jdbc:oracle:thin:@//%s:%d/%s", settings.get(HOST_KEY), settings.get(PORT_KEY),
                settings.get(DATABASE_KEY));
    }

    private String toOciUrl(Map<String, Object> settings) {
        return String.format("jdbc:oracle:oci:@%s:%d/%s", settings.get(HOST_KEY), settings.get(PORT_KEY),
                settings.get(DATABASE_KEY));
    }

    @Override
    protected String[] parseURL(String url) {
        // Try OCI
        Matcher matcher = JDBC_OCI_URL_PATTERN.matcher(url);
        if (matcher.find() && matcher.groupCount() == 3) {
            return new String[] { matcher.group(1), matcher.group(2), matcher.group(3) };
        } else {
            // If OCI fails, use THIN
            matcher = JDBC_THIN_URL_PATTERN.matcher(url);
            matcher.find();
            return new String[] { matcher.group(1), matcher.group(2), matcher.group(3) };
        }
    }

    @Override
    protected String getDriverClass() {
        return ORACLE_DRIVER_CLASS;
    }

    @Override
    public void validateConnection(Map<String, Object> settings) {
        checkClasspath();
        super.validateConnection(settings);
    }

    private void checkClasspath() throws ConfigurationException {
        try {
            Class.forName(ORACLE_DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("Oracle jar file (ojdbc6.jar) must be "
                    + "included in the server classpath. ", e);
        }
    }
}
