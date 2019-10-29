package org.n52.sos.ds.datasource;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AbstractOracleDatasourceTest {
    
    private AbstractOracleDatasource datasource = new TestOracleDatasource();
    
    
    @Test
    public void test_oci_localhost_url() {
        String[] parseURL = datasource.parseURL("jdbc:oracle:oci:@localhost:1521/XE");
        assertEquals(3, parseURL.length);
    }
    
    @Test
    public void test_oci_ip_url() {
        String[] parseURL = datasource.parseURL("jdbc:oracle:oci:@192.168.1.1:1521/XE");
        assertEquals(3, parseURL.length);
    }
    
    @Test
    public void test_thin_sid_host_url() {
        String[] parseURL = datasource.parseURL("jdbc:oracle:thin:@localhost:1521:T10A");
        assertEquals(3, parseURL.length);
    }
    
    @Test
    public void test_thin_sid_ip_url() {
        String[] parseURL = datasource.parseURL("jdbc:oracle:thin:@127.0.0.1:1521:T10A");
        assertEquals(3, parseURL.length);
    }
    
    @Test
    public void test_thin_localhost_url() {
        String[] parseURL = datasource.parseURL("jdbc:oracle:thin:@//localhost:1521/XE");
        assertEquals(3, parseURL.length);
    }
    
    @Test
    public void test_thin_dns_url() {
        String[] parseURL = datasource.parseURL("jdbc:oracle:thin:@neptune.acme.com:1521:T10A");
        assertEquals(3, parseURL.length);
    }
    
    @Test
    public void test_thin_ip_url() {
        String[] parseURL = datasource.parseURL("jdbc:oracle:thin:@127.0.0.1:1521:T10A");
        assertEquals(3, parseURL.length);
    }

    public class TestOracleDatasource extends AbstractOracleDatasource {
    
        private static final String DIALECT_NAME = "Oracle Spatial";
    
        public TestOracleDatasource() {
            super();
        }
    
        @Override
        public String getDialectName() {
            return DIALECT_NAME;
        }
    
    }

}
