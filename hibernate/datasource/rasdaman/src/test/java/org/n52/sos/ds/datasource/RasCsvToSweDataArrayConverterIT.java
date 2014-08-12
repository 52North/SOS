package org.n52.sos.ds.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.hsqldb.ras.RasUtil;
import org.n52.sos.ds.datasource.AbstractHibernateDatasource;
import org.n52.sos.ds.datasource.RasdamanDatasource;
import org.n52.sos.ogc.swe.SweDataArray;

import rasj.RasMArrayByte;

/**
 * 
 * @author Simona Badoiu <s.a.badoiu@52north.org>
 *
 */
public class RasCsvToSweDataArrayConverterIT extends TestCase {
	public static final String 	DEFAULT_DB_FILE = "/var/hsqldb/testdb";
	private String 				dbFile = DEFAULT_DB_FILE;
	
	private static final String SOS_TEST_CONF = "SOS_TEST_CONF";
	private static final String RASDAMAN_SCHEMA = "public";
	private static final String RASDAMAN_USER = "SA";
	private static final String RASDAMAN_PASS = "";
	private static final String DATABASE_DEFAULT_VALUE = "jdbc:hsqldb:file:/var/hsqldb/testdb";
	
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
				RasMArrayByte csv = (RasMArrayByte) result.getObject(1);
				String csvString = new String(csv.getArray(), "UTF-8");
				SweDataArray swe = csvToSweDataArray(csvString);
			    sweRes.add(swe);
			}
			
			return sweRes;
			
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String rasCsvToTupleList(String csv) {
		return csv.replace("{", "").replace("}","").replace("\"", "");
	}
	
	public List<List<String>> getLines(String csvRes) {
		Pattern p = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
		Matcher m = p.matcher(csvRes);
		List<List<String>> result = new ArrayList<>();
		
		while(m.find()) {
			List<String> auxList = new ArrayList<>();
			String auxString = m.group(1);
			String[] splitted = auxString.split(",");
			for (int i = 0; i < splitted.length; i++) {
				auxList.add(splitted[i]);
			}
			result.add(auxList);
		}
		
		return result;
		
	}
	
	public SweDataArray csvToSweDataArray(String csvRes) {
		List<List<String>> arr = this.getLines(csvRes);
		SweDataArray sweArr = new SweDataArray();
		sweArr.setValues(arr);
		return sweArr;
	}
	
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
	
	public boolean createTables(final Connection conn) throws SQLException {
		boolean success = true;
		String createString =
				"create table TESTVALUE (" +
						"value varchar(40) ARRAY NOT NULL )";

		success = success && executeQuery(conn, createString, 0);

		return success;
	}
	
	public boolean insertValues(final Connection conn) throws SQLException {
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
	
	private boolean executeQuery(final Connection conn, final String query, final int line) throws SQLException{
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeQuery(query);
		} catch (SQLException e) {
			return true;
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		return true;
	}
	
	public boolean dropTables(final Connection conn) throws SQLException {
		String dropString = "drop table if exists TESTVALUE";
		return executeQuery(conn, dropString, 0);
	}
	
	public void dropRasCollections() {
		RasUtil.openDatabase(RasUtil.adminUsername, RasUtil.adminPassword, true);
		RasUtil.executeRasqlQuery("drop collection rastestvalue",
				false, true);
		RasUtil.executeRasqlQuery("drop collection rastestvalue1",
				true, true);
	}
	
	private boolean setUp(final Connection conn) throws SQLException {
		boolean success = dropTables(conn);
		dropRasCollections();
		success = success && createTables(conn);
		success = success && insertValues(conn);
		return success;
	}

	public void insertTestData() throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			setUp(conn);
		} catch (final SQLException e) {
			throw new RuntimeException("Tests FAILED. SQLException occurred while performing tests.", e);
		} finally {
			if (conn != null)
				conn.close();
		}
	}
	
	public void testCsvToSweDataArray1() {
		try {
			insertTestData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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
		SweDataArray sweresult = csvToSweDataArray(csv);
		
		assertEquals(swedata, sweresult);
	}
	
}
