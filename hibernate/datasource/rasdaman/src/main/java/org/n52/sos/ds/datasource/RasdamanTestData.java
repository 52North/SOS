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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.hsqldb.ras.RasUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RasdamanTestData {
	private static final Logger	LOGGER = LoggerFactory.getLogger(RasdamanTestData.class);

	public static final String 	DEFAULT_DB_FILE = "/var/hsqldb/dbbun";
	public static final String	DEFAULT_SQL_SCRIPT_FILE = "src/main/resources/asqldb_batch_example.sql";
	private String 				dbFile = DEFAULT_DB_FILE;
	private String				sqlScriptFile = DEFAULT_SQL_SCRIPT_FILE;

	private boolean setUp(final Connection conn) throws SQLException {
		boolean success = dropTables(conn);
		dropRasCollections();
		success = success && createTables(conn);
		success = success && insertValues(conn);
		return success;
	}
	
	public String readSqlScript() {
		String everything = null;
		try(BufferedReader br = new BufferedReader(new FileReader(sqlScriptFile))) {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        everything = sb.toString();
	    } catch (IOException e) {
	    	LOGGER.debug("The file " + sqlScriptFile + " can't be found!");
			e.printStackTrace();
		}
		return everything;
	}

	public boolean createTables(final Connection conn) throws SQLException {
		boolean success = true;
		LOGGER.info("Creating tables...");
		String sqlBatchExample = readSqlScript();
		String createString =
				"create table ARRAYVALUE (" +
						"ObservationId integer NOT NULL, " +
						"value varchar(40) ARRAY NOT NULL, " +
						"FOREIGN KEY (ObservationId) REFERENCES observation (ObservationId))";

//		success = success && executeQuery(conn, sqlBatchExample, 0);
		success = success && executeQuery(conn, createString, 0);

		return success;
	}

	public boolean insertValues(final Connection conn) throws SQLException {
		RasUtil.openDatabase(RasUtil.adminUsername, RasUtil.adminPassword, true);
		RasUtil.executeRasqlQuery("create collection rastest GreySet",
				false, false);
		RasUtil.executeRasqlQuery("insert into rastest values " +
				"marray x in [0:250, 0:225] values 0c",
				false, false);
		RasUtil.executeRasqlQuery("create collection rastest2 GreySet",
				false, false);
		RasUtil.executeRasqlQuery("insert into rastest2 values " +
				"marray x in [0:225, 0:225] values 2c",
				false, false);
		RasUtil.executeRasqlQuery("create collection rastest3 GreySet",
				false, false);
		RasUtil.executeRasqlQuery("insert into rastest3 values " +
				"marray x in [0:225, 0:225] values 3c",
				true, false);
		String oidQuery = "select oid(c) from rastest as c";
		String oid = RasUtil.executeRasqlQuery(oidQuery, true, false).toString();
		oid = oid.replaceAll("[\\[\\]]", "");
		oidQuery = "select oid(c) from rastest2 as c";
		String oid2 = RasUtil.executeRasqlQuery(oidQuery, true, false).toString();
		oid2 = oid2.replaceAll("[\\[\\]]", "");
		oidQuery = "select oid(c) from rastest3 as c";
		String oid3 = RasUtil.executeRasqlQuery(oidQuery, true, false).toString();
		oid3 = oid3.replaceAll("[\\[\\]]", "");
		
		String[] insertQueries = new String[]{
				"INSERT INTO ARRAYVALUE VALUES(1, ARRAY['rastest:" + Double.valueOf(oid).intValue() + "'])",
				"INSERT INTO ARRAYVALUE VALUES(2, ARRAY['rastest2:" + Double.valueOf(oid2).intValue() + "'])",
				"INSERT INTO ARRAYVALUE VALUES(3, ARRAY['rastest:" + Double.valueOf(oid).intValue() + "'])",
				"INSERT INTO ARRAYVALUE VALUES(4, ARRAY['rastest2:" + Double.valueOf(oid2).intValue() + "'])",
				"INSERT INTO ARRAYVALUE VALUES(5, ARRAY['rastest3:" + Double.valueOf(oid3).intValue() + "'])",
				"INSERT INTO ARRAYVALUE VALUES(6, ARRAY['rastest3:" + Double.valueOf(oid3).intValue() + "'])"
		};
		for (String query : insertQueries) {
			if (!executeQuery(conn, query, 0))
				return false;
		}
		return true;
	}

	private boolean executeQuery(final Connection conn, final String query, final int line) throws SQLException{
		LOGGER.info("Executing query on line {}: {}... ", line, query);
		final boolean errorExpected = query.startsWith("/*e");
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeQuery(query);
		} catch (SQLException e) {
			if (!errorExpected) {
				LOGGER.debug("\n>>>> Query failed! <<<<");
				e.printStackTrace();
				return false;
			}
			LOGGER.info("Success!");
			return true;
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		LOGGER.info("Success!");
		return true;
	}

	public boolean dropTables(final Connection conn) throws SQLException {
		String dropString = "drop table if exists ARRAYVALUE";
		return executeQuery(conn, dropString, 0);
	}

	public void dropRasCollections() {
		RasUtil.openDatabase(RasUtil.adminUsername, RasUtil.adminPassword, true);
		RasUtil.executeRasqlQuery("drop collection rastest",
				false, true);
		RasUtil.executeRasqlQuery("drop collection rastest2",
				false, true);
		RasUtil.executeRasqlQuery("drop collection rastest3",
				true, true);
	}

	private boolean tearDown(final Connection conn) throws SQLException {
		dropRasCollections();
		return conn == null || dropTables(conn);
	}

	// make hsqldb connection
	public Connection getConnection() throws SQLException {
		Connection conn;
		Properties connectionProps = new Properties();
		connectionProps.put("user", "SA");
		connectionProps.put("password", "");
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load the hsqldb JDBCDriver", e);
		}
		final String jdbcUrl = "jdbc:hsqldb:file:" + dbFile;
		conn = DriverManager.getConnection(
				jdbcUrl,
				connectionProps
				);
		return conn;
	}

	public void insertTestData() throws SQLException {
		boolean success = true;
		Connection conn = null;
		try {
			conn = getConnection();
			success = setUp(conn);
		} catch (final SQLException e) {
			throw new RuntimeException("Tests FAILED. SQLException occurred while performing tests.", e);
		} finally {
			if (conn != null)
				conn.close();
		}
		if (success) {
			LOGGER.info("All the tables were created");
		} else {
			LOGGER.debug("Tables creation failed");
		}
	}

	public static void main(String[] args) throws SQLException {
		RasdamanTestData rasdamanTestData = new RasdamanTestData();
		rasdamanTestData.insertTestData();
	}
}
