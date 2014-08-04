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

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @since 4.0.0
 *
 */
public class RasdamanDatasourceTest extends TestCase {
	private static final String SOS_TEST_CONF = "SOS_TEST_CONF";
	private static final String RASDAMAN_SCHEMA = "public";
	private static final String RASDAMAN_USER = "SA";
	private static final String RASDAMAN_PASS = "";
	private static final String DATABASE_DEFAULT_VALUE = "jdbc:hsqldb:file:/var/hsqldb/db";

	private static String host, user, pass, schema, database;
	private static int port;

	static {
		initialize();
	}

	private static final void initialize() {

		Properties props = new Properties();
		schema = RASDAMAN_SCHEMA;
		user = RASDAMAN_USER;
		pass = RASDAMAN_PASS;
		database = DATABASE_DEFAULT_VALUE;
	}

	private RasdamanDatasource ds;
	private Connection conn, connNoRights;
	private Statement stmt, stmtNoRights;

	protected void setUp() throws Exception {
		ds = new RasdamanDatasource();
		Map<String, Object> settings = getDefaultSettings();
		conn = ds.openConnection(settings);
		stmt = conn.createStatement();
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

//	public void testSchemaCreationSuccess() throws Exception {
//		ds.doCheckSchemaCreation("rasdaman", stmt);
//	}
//
//	public void testSchemaCreationFailure() throws Exception {
//		try {
//			ds.doCheckSchemaCreation("", stmtNoRights);
//			fail();
//		} catch (SQLException e) {
//			// ignore
//		}
//	}

//	@SuppressWarnings("unchecked")
//	public void testCheckSchemaCreationSuccess() throws Exception {
//		ds = spy(ds);
//		Statement stmt = mock(Statement.class);
//		Connection conn = mock(Connection.class);
//		when(conn.createStatement()).thenReturn(stmt);
//		doReturn(conn).when(ds).openConnection(anyMap());
//		assertTrue(ds.checkSchemaCreation(new HashMap<String, Object>()));
//	}
//
//	@SuppressWarnings("unchecked")
//	public void testCheckSchemaCreationFailure() throws Exception {
//		ds = spy(ds);
//		Statement stmt = mock(Statement.class);
//		when(stmt.execute(anyString())).thenThrow(new SQLException());
//
//		Connection conn = mock(Connection.class);
//		when(conn.createStatement()).thenReturn(stmt);
//		doReturn(conn).when(ds).openConnection(anyMap());
//
//		assertFalse(ds.checkSchemaCreation(new HashMap<String, Object>()));
//		
//	}

	public void testToURL() throws Exception {
		Map<String, Object> settings = new HashMap<String, Object>();
		settings.put(AbstractHibernateDatasource.USERNAME_KEY, "SA");
		settings.put(AbstractHibernateDatasource.PASSWORD_KEY, "");
		settings.put(AbstractHibernateDatasource.DATABASE_KEY, "jdbc:hsqldb:file:/var/hsqldb/db");
		settings.put(AbstractHibernateDatasource.SCHEMA_KEY, "public");

		assertEquals("jdbc:hsqldb:file:/var/hsqldb/db", ds.toURL(settings));
	}
	
	public void testFromURL() throws Exception {
		String url = "jdbc:hsqldb:file:/var/hsqldb/db";
		String[] parsed = ds.parseURL(url);

		assertEquals(2, parsed.length);
		assertEquals("file:/var/hsqldb/db", parsed[1]);
	}

	private static Map<String, Object> getDefaultSettings() {
		Map<String, Object> settings = new HashMap<String, Object>();
		settings.put(AbstractHibernateDatasource.USERNAME_KEY, user);
		settings.put(AbstractHibernateDatasource.PASSWORD_KEY, pass);
		settings.put(AbstractHibernateDatasource.SCHEMA_KEY, schema);
		settings.put(AbstractHibernateDatasource.TRANSACTIONAL_KEY, true);
		settings.put(AbstractHibernateDatasource.DATABASE_KEY, database);
		return settings;
	}
}
