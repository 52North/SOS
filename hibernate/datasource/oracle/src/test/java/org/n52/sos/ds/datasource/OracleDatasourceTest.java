/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import junit.framework.TestCase;

/**
 * @since 4.0.0
 *
 */
public class OracleDatasourceTest extends TestCase {
    private static final String SOS_TEST_CONF = "SOS_TEST_CONF";
    private static final String ORACLE_HOST = "oracle_host";
    private static final String ORACLE_PORT = "oracle_port";
    private static final String ORACLE_SCHEMA = "oracle_schema";
    private static final String ORACLE_USER = "oracle_user";
    private static final String ORACLE_PASS = "oracle_pass";
    private static final String ORACLE_USER_NO_RIGHTS = "oracle_user_no_rights";
    private static final String ORACLE_PASS_NO_RIGHTS = "oracle_pass_no_rights";

    private static final String ORACLE = "oracle";
    private static final String LOCALHOST = "localhost";
    private static final String DB = "db";
    private static final String JDBC_URL = "jdbc:oracle:thin://localhost:1521/db";

    private static String host;
    private static String user;
    private static String pass;
    private static String schema;
    private static String userNoRights;
    private static String passNoRights;
    private static int port;
    private AbstractOracleDatasource ds;
    private Connection conn;
    private Connection connNoRights;
    private Statement stmt;
    private Statement stmtNoRights;

    static {
        initialize();
    }

    private static void initialize() {
        String conf = System.getenv(SOS_TEST_CONF);
        if (conf == null) {
            throw new RuntimeException(
                    "SOS_TEST_CONF environment variable not set!!");
        }

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(conf));
        } catch (IOException e) {
            throw new RuntimeException("Invalid SOS_TEST_CONF file: " + conf, e);
        }

        host = props.getProperty(ORACLE_HOST);
        port = Integer.parseInt(props.getProperty(ORACLE_PORT));
        schema = props.getProperty(ORACLE_SCHEMA);
        user = props.getProperty(ORACLE_USER);
        pass = props.getProperty(ORACLE_PASS);
        userNoRights = props.getProperty(ORACLE_USER_NO_RIGHTS);
        passNoRights = props.getProperty(ORACLE_PASS_NO_RIGHTS);
    }

    protected void setUp() throws Exception {
        ds = new OracleDatasource();
        Map<String, Object> settings = getDefaultSettings();
        conn = ds.openConnection(settings);
        stmt = conn.createStatement();

        settings = getDefaultSettings();
        settings.put(AbstractHibernateDatasource.USERNAME_KEY, userNoRights);
        settings.put(AbstractHibernateDatasource.PASSWORD_KEY, passNoRights);
        connNoRights = ds.openConnection(settings);
        stmtNoRights = connNoRights.createStatement();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        if (stmt != null && !stmt.isClosed()) {
            stmt.close();
        }
        if (stmtNoRights != null && !stmtNoRights.isClosed()) {
            stmtNoRights.close();
        }
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
        if (connNoRights != null && !connNoRights.isClosed()) {
            connNoRights.close();
        }
    }

    public void testSchemaCreationSuccess() throws Exception {
        ds.doCheckSchemaCreation(ORACLE, stmt);
    }

    public void testSchemaCreationFailure() throws Exception {
        try {
            ds.doCheckSchemaCreation("", stmtNoRights);
            fail();
        } catch (SQLException e) {
            // ignore
        }
    }

    @SuppressWarnings("unchecked")
    public void testCheckSchemaCreationSuccess() throws Exception {
        ds = Mockito.spy(ds);
        Statement s = Mockito.mock(Statement.class);
        Connection c = Mockito.mock(Connection.class);
        Mockito.when(c.createStatement()).thenReturn(s);
        Mockito.doReturn(c).when(ds).openConnection(ArgumentMatchers.anyMap());
        assertTrue(ds.checkSchemaCreation(new HashMap<String, Object>()));
    }

    public void testCheckSchemaCreationFailure() throws Exception {
        ds = Mockito.spy(ds);
        Statement s = Mockito.mock(Statement.class);
        Mockito.when(s.execute(ArgumentMatchers.anyString())).thenThrow(new SQLException());

        Connection c = Mockito.mock(Connection.class);
        Mockito.when(c.createStatement()).thenReturn(stmt);
        Mockito.doReturn(c).when(ds).openConnection(ArgumentMatchers.anyMap());

        assertFalse(ds.checkSchemaCreation(new HashMap<String, Object>()));
    }

    public void testToURL() throws Exception {
        Map<String, Object> settings = new HashMap<String, Object>();
        settings.put(AbstractHibernateDatasource.HOST_KEY, LOCALHOST);
        settings.put(AbstractHibernateDatasource.PORT_KEY, 1521);
        settings.put(AbstractHibernateDatasource.USERNAME_KEY, ORACLE);
        settings.put(AbstractHibernateDatasource.PASSWORD_KEY, ORACLE);
        settings.put(AbstractHibernateDatasource.DATABASE_KEY, DB);
        settings.put(AbstractHibernateDatasource.SCHEMA_KEY, "schema");

        assertEquals(JDBC_URL, ds.toURL(settings));
    }

    public void testFromURL() throws Exception {
        String[] parsed = ds.parseURL(JDBC_URL);

        assertEquals(3, parsed.length);
        assertEquals(LOCALHOST, parsed[0]);
        assertEquals("1521", parsed[1]);
        assertEquals(DB, parsed[2]);
    }

    private static Map<String, Object> getDefaultSettings() {
        Map<String, Object> settings = new HashMap<String, Object>();
        settings.put(AbstractHibernateDatasource.HOST_KEY, host);
        settings.put(AbstractHibernateDatasource.PORT_KEY, port);
        settings.put(AbstractHibernateDatasource.USERNAME_KEY, user);
        settings.put(AbstractHibernateDatasource.PASSWORD_KEY, pass);
        settings.put(AbstractHibernateDatasource.SCHEMA_KEY, schema);
        return settings;
    }
}
