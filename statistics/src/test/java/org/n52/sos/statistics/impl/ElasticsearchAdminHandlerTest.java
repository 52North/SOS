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
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.n52.sos.statistics.api.ServiceEventDataMapping;
import org.n52.sos.statistics.sos.schema.SosElasticsearchSchemas;

import basetest.ElasticsearchAwareTest;

public class ElasticsearchAdminHandlerTest extends ElasticsearchAwareTest {

    @Test
    public void createNewDatabase() throws InterruptedException {
        ElasticsearchAdminHandler adminHandler = new ElasticsearchAdminHandler(getEmbeddedClient(), clientSettings);
        adminHandler.createSchema();

        IndicesAdminClient indices = getEmbeddedClient().admin().indices();

        IndicesExistsResponse index = indices.prepareExists(clientSettings.getIndexId()).get();
        Assert.assertTrue(index.isExists());

        GetResponse resp =
                getEmbeddedClient()
                        .prepareGet(clientSettings.getIndexId(), ServiceEventDataMapping.METADATA_TYPE_NAME, ServiceEventDataMapping.METADATA_ROW_ID)
                        .setOperationThreaded(false).get();

        Assert.assertEquals(new SosElasticsearchSchemas().getSchemaVersion(),
                resp.getSourceAsMap().get(ServiceEventDataMapping.METADATA_VERSION_FIELD));
    }

    @Test
    public void createSchemaTwiceWithoutError() {
        ElasticsearchAdminHandler adminHandler = new ElasticsearchAdminHandler(getEmbeddedClient(), clientSettings);
        adminHandler.createSchema();
        adminHandler.createSchema();
    }

    @Test
    public void addnewUuidOnConnect() {
        ElasticsearchAdminHandler adminHandler = new ElasticsearchAdminHandler(getEmbeddedClient(), clientSettings);
        adminHandler.createSchema();
        clientSettings.setUuid("lofasz janos");

        adminHandler = new ElasticsearchAdminHandler(getEmbeddedClient(), clientSettings);
        adminHandler.createSchema();

        GetResponse resp =
                getEmbeddedClient()
                        .prepareGet(clientSettings.getIndexId(), ServiceEventDataMapping.METADATA_TYPE_NAME, ServiceEventDataMapping.METADATA_ROW_ID)
                        .setOperationThreaded(false).get();

        Map<String, Object> map = resp.getSourceAsMap();
        Assert.assertNotNull(map.get(ServiceEventDataMapping.METADATA_CREATION_TIME_FIELD));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.METADATA_UUIDS_FIELD));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.METADATA_UPDATE_TIME_FIELD));
        List<String> object = (List<String>) map.get(ServiceEventDataMapping.METADATA_UUIDS_FIELD);
        Assert.assertEquals(2, object.size());
        Assert.assertThat(object, CoreMatchers.hasItem("lofasz janos"));
    }

    @Test(
            expected = ElasticsearchException.class)
    public void failOnVersionMismatch() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException,
            InterruptedException {
        Map<String, Object> data = new HashMap<>();
        data.put(ServiceEventDataMapping.METADATA_VERSION_FIELD, 123456);
        getEmbeddedClient()
                .prepareIndex(clientSettings.getIndexId(), ServiceEventDataMapping.METADATA_TYPE_NAME, ServiceEventDataMapping.METADATA_ROW_ID)
                .setSource(data).get();

        Thread.sleep(1500);

        ElasticsearchAdminHandler adminHandler = new ElasticsearchAdminHandler(getEmbeddedClient(), clientSettings);
        adminHandler.createSchema();
    }
}
