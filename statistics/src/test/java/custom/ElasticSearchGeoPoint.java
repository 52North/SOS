/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package custom;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.statistics.api.ElasticsearchSettings;
import org.n52.iceland.statistics.api.interfaces.geolocation.IAdminStatisticsLocation.LocationDatabaseType;
import org.n52.iceland.statistics.api.mappings.ServiceEventDataMapping;
import org.n52.iceland.statistics.impl.ElasticsearchAdminHandler;
import org.n52.iceland.statistics.impl.ElasticsearchDataHandler;
import org.n52.iceland.statistics.impl.geolocation.StatisticsLocationUtil;

import basetest.SpringBaseTest;

public class ElasticSearchGeoPoint extends SpringBaseTest {

    @Inject
    private ElasticsearchDataHandler handler;

    @Inject
    private StatisticsLocationUtil loc;

    @Inject
    private ElasticsearchSettings settings;

    @Inject
    private ElasticsearchAdminHandler admin;

    @Before
    public void init() throws URISyntaxException {
        String absoluteFile =
                new File(ElasticSearchGeoPoint.class.getResource("/geolite/city.mmdb").toURI()).getAbsolutePath();
        loc.setEnabled(true);
        loc.initDatabase(LocationDatabaseType.CITY, absoluteFile);

        admin.init();

        // --- Modify and uncomment this to connect to the remote ES ----
        // settings.getClusterNodes().add("111.222.333.444");
        // handler.init();

    }

    @Test
    public void addGeoPointToDatabase() throws Exception {
        List<String> ips = Arrays.asList("173.244.177.114", "217.20.130.99", "121.78.127.249", "157.166.239.102",
                "130.63.127.20", "202.218.223.210");
        Random r = new Random();
        for (int i = 0; i < 367; i++) {
            insertIp(ips.get(r.nextInt(ips.size())));
        }
    }

    private void insertIp(String ip) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> geomap = loc.ip2SpatialData(ip);
        data.put(ServiceEventDataMapping.SR_GEO_LOC_FIELD.getName(), geomap);

        handler.persist(data);
    }

    @After
    public void down() {
        loc.destroy();
    }

}
