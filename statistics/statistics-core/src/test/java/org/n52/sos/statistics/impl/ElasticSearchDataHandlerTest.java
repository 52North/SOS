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
package org.n52.sos.statistics.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchResponse;
import org.junit.Assert;
import org.junit.Test;
import org.n52.sos.statistics.api.interfaces.datahandler.IStatisticsDataHandler;

import basetest.ElasticsearchAwareTest;

public class ElasticSearchDataHandlerTest extends ElasticsearchAwareTest {

    @Inject
    private IStatisticsDataHandler dataHandler;

    @Test
    public void persistBasicData() throws InterruptedException {
        Map<String, Object> data = new HashMap<>();
        data.put("alma", "körte");
        dataHandler.persist(data);

        Thread.sleep(2000);

        SearchResponse response = getEmbeddedClient().prepareSearch(clientSettings.getIndexId()).setTypes(clientSettings.getTypeId()).get();
        Assert.assertEquals("körte", response.getHits().getHits()[0].getSource().get("alma"));
    }
}
