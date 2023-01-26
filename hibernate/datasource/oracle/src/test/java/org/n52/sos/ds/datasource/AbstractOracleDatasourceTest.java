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
