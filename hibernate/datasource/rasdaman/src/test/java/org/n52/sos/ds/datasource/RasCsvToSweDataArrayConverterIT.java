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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hsqldb.ras.RasUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.ds.datasource.AbstractHibernateDatasource;
import org.n52.sos.ds.datasource.RasdamanDatasource;
import org.n52.sos.ds.datasource.RasCsvToSweDataArrayConverter;
import org.n52.sos.ogc.swe.SweDataArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rasj.RasMArrayByte;

/**
 * 
 * @author Simona Badoiu <s.a.badoiu@52north.org>
 *
 */
public class RasCsvToSweDataArrayConverterIT {
	private static final Logger	LOGGER = LoggerFactory.getLogger(RasdamanTestData.class);
	
	public static final String 	DEFAULT_DB_LOCATION = "src/test/resources/hsqldb";
	public static final String	DEFAULT_DB_NAME = "db";
	public static final String	DEFAULT_DB_FILE = DEFAULT_DB_LOCATION + "/" + DEFAULT_DB_NAME;
	
	private static String 		dbFile = DEFAULT_DB_FILE;
	
	private static final String RASDAMAN_SCHEMA = "public";
	private static final String RASDAMAN_USER = "SA";
	private static final String RASDAMAN_PASS = "";
	private static final String DATABASE_DEFAULT_VALUE = "jdbc:hsqldb:file:" + DEFAULT_DB_FILE;
	
	private static String  user, pass, schema, database;
	private static Connection conn;
	
	@BeforeClass
	public static void initialize() {
		schema = RASDAMAN_SCHEMA;
		user = RASDAMAN_USER;
		pass = RASDAMAN_PASS;
		database = DATABASE_DEFAULT_VALUE;
	}
	
	@BeforeClass
	public static void insertTestData() throws SQLException {
		boolean success = false;
		try {
			conn = getConnection();
			success = setUp();
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
	
	@AfterClass
	public static void dropTables() {
		String dropString = "drop table if exists TESTVALUE";
		
		try (Connection conn = getConnection()) {
			executeQuery(conn, dropString, 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void dropRasCollections() {
		RasUtil.openDatabase(RasUtil.adminUsername, RasUtil.adminPassword, true);
		RasUtil.executeRasqlQuery("drop collection rastestvalue",
				false, true);
		RasUtil.executeRasqlQuery("drop collection rastestvalue1",
				true, true);
	}
	
	@AfterClass
	public static void deleteDatabase() {
		File arraydir = new File(DEFAULT_DB_LOCATION);
        final File[] arrayFiles = arraydir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.startsWith(DEFAULT_DB_NAME + ".");
            }
        });
        
        for (int i = 0; i < arrayFiles.length; i++) {
        	arrayFiles[i].delete();
        }
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
	
	public ArrayList<SweDataArray> makeHsqldbCsvQuery() {
		String query = "SELECT csv(VALUE) from TESTVALUE";
		try {
			Statement stmt = null;
			RasdamanDatasource ds = new RasdamanDatasource();
			Map<String, Object> settings = getDefaultSettings();
			Connection conn = ds.openConnection(settings);
			stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(query);
			
			ArrayList<SweDataArray> sweRes = new ArrayList<SweDataArray>();
			while (result.next()) {
				Object obj = result.getObject(1);
				if (obj instanceof RasMArrayByte) {
					RasMArrayByte byteResult = (RasMArrayByte) result.getObject(1);
					SweDataArray swe = RasCsvToSweDataArrayConverter.rasByteArrayToSweDataArray(byteResult);
				    sweRes.add(swe);
				} else {
					throw new ClassCastException();
				}
			}
			
			return sweRes;
			
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Connection getConnection() throws SQLException {
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
	
	public static boolean createTables() throws SQLException {
		boolean success = true;
		String createString =
				"create table TESTVALUE (" +
						"value varchar(40) ARRAY NOT NULL )";

		success = success && executeQuery(conn, createString, 0);

		return success;
	}
	
	public static boolean insertValues() throws SQLException {
		RasUtil.openDatabase(RasUtil.adminUsername, RasUtil.adminPassword, true);
		RasUtil.executeRasqlQuery("create collection rastestvalue GreySet",
				false, false);
		RasUtil.executeRasqlQuery("insert into rastestvalue values " +
				"marray x in [0:250, 0:225] values 0c",
				false, false);
		RasUtil.executeRasqlQuery("create collection rastestvalue1 GreySet",
				false, false);
		RasUtil.executeRasqlQuery("insert into rastestvalue1 values " +
				"marray x in [0:225, 0:225] values 2c",
				true, false);
		String oidQuery = "select oid(c) from rastestvalue as c";
		String oid = RasUtil.executeRasqlQuery(oidQuery, true, false).toString();
		oid = oid.replaceAll("[\\[\\]]", "");
		oidQuery = "select oid(c) from rastestvalue1 as c";
		String oid2 = RasUtil.executeRasqlQuery(oidQuery, true, false).toString();
		oid2 = oid2.replaceAll("[\\[\\]]", "");
		
		String[] insertQueries = new String[]{
				"INSERT INTO TESTVALUE VALUES(ARRAY['rastestvalue:" + Double.valueOf(oid).intValue() + "'])",
				"INSERT INTO TESTVALUE VALUES(ARRAY['rastestvalue1:" + Double.valueOf(oid2).intValue() + "'])"
		};
		
		for (String query : insertQueries) {
			if (!executeQuery(conn, query, 0))
				return false;
		}
		return true;
	}
	
	private static boolean executeQuery(final Connection conn, final String query, final int line) throws SQLException{
		try (Statement stmt = conn.createStatement()) {
			stmt.executeQuery(query);
		} catch (SQLException e) {
			LOGGER.debug("\n>>>> Query failed! <<<<");
			e.printStackTrace();
			return false;
		}
		LOGGER.info("Success!");
		return true;
	}
	
	private static boolean setUp() throws SQLException {
		boolean success = true;
		dropTables();
		success = success && createTables();
		success = success && insertValues();
		return success;
	}

	
	
	@Test
	public void testCsvToSweDataArray1() {
		List<List<String>> expected1 = new ArrayList<List<String>>();
		List<List<String>> expected2 = new ArrayList<List<String>>();
		
		List<String> list = new ArrayList<String>();
		for (int i = 0; i <= 250; i++) {
			list = new ArrayList<String>();
			for (int j = 0; j <= 225; j++) {
				list.add("0");
			}
			expected1.add(list);
		}
		
		for (int i = 0; i <= 225; i++) {
			list = new ArrayList<String>();
			for (int j = 0; j <= 225; j++) {
				list.add("2");
			}
			expected2.add(list);
		}
		
		SweDataArray expectedSwe1 = new SweDataArray();
		expectedSwe1.setValues(expected1);
		SweDataArray expectedSwe2 = new SweDataArray();
		expectedSwe2.setValues(expected2);
		
		ArrayList<SweDataArray> result = makeHsqldbCsvQuery();
		assertEquals(result.get(0), expectedSwe1);
		assertEquals(result.get(1), expectedSwe2);
	}
	
	@Test
	public void testCsvToSweDataArray() {
		String csv = "{0,0,0,1}, {0,0,0,0,0}, {0,0,0,0}";
		
		List<List<String>> expected = new ArrayList<List<String>>();
		List<String> first = Arrays.asList("0", "0", "0", "1");
		List<String> second = Arrays.asList("0", "0", "0", "0", "0");
		List<String> third = Arrays.asList("0", "0", "0", "0");
		expected.add(first);
		expected.add(second);
		expected.add(third);
		
		SweDataArray swedata = new SweDataArray();
		swedata.setValues(expected);
		
		RasCsvToSweDataArrayConverterIT a = new RasCsvToSweDataArrayConverterIT();
		SweDataArray sweresult = RasCsvToSweDataArrayConverter.csvToSweDataArray(csv);
		
		assertEquals(swedata, sweresult);
	}
	
}
