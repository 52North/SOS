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

import javax.inject.Inject;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.exception.ConfigurationError;
import org.n52.sos.statistics.api.ElasticsearchSettings;
import org.n52.sos.statistics.api.ElasticsearchSettingsKeys;
import org.n52.sos.statistics.api.mappings.MetadataDataMapping;
import org.n52.sos.statistics.sos.schema.SosElasticsearchSchemas;

import basetest.ElasticsearchAwareTest;

public class ElasticsearchAdminHandlerTest extends ElasticsearchAwareTest {

    @Inject
    private ElasticsearchAdminHandler adminHandler;

    @Inject
    private ElasticsearchDataHandler dataHandler;

    @Inject
    private ElasticsearchSettings settings;

    @Test
    public void createNewDatabase() throws InterruptedException {
        adminHandler.createSchema();

        IndicesAdminClient indices = getEmbeddedClient().admin().indices();

        IndicesExistsResponse index = indices.prepareExists(clientSettings.getIndexId()).get();
        Assert.assertTrue(index.isExists());

        GetResponse resp = getEmbeddedClient()
                .prepareGet(clientSettings.getIndexId(), MetadataDataMapping.METADATA_TYPE_NAME, MetadataDataMapping.METADATA_ROW_ID)
                .setOperationThreaded(false).get();

        Assert.assertEquals(new SosElasticsearchSchemas().getSchemaVersion(),
                resp.getSourceAsMap().get(MetadataDataMapping.METADATA_VERSION_FIELD.getName()));
    }

    @Test
    public void createSchemaTwiceWithoutError() {
        adminHandler.createSchema();
        adminHandler.createSchema();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addnewUuidOnConnect() {
        adminHandler.createSchema();
        clientSettings.setUuid("lofasz janos");

        adminHandler.createSchema();

        GetResponse resp = getEmbeddedClient()
                .prepareGet(clientSettings.getIndexId(), MetadataDataMapping.METADATA_TYPE_NAME, MetadataDataMapping.METADATA_ROW_ID)
                .setOperationThreaded(false).get();

        Map<String, Object> map = resp.getSourceAsMap();
        Assert.assertNotNull(map.get(MetadataDataMapping.METADATA_CREATION_TIME_FIELD.getName()));
        Assert.assertNotNull(map.get(MetadataDataMapping.METADATA_UUIDS_FIELD.getName()));
        Assert.assertNotNull(map.get(MetadataDataMapping.METADATA_UPDATE_TIME_FIELD.getName()));

        List<String> object = (List<String>) map.get(MetadataDataMapping.METADATA_UUIDS_FIELD.getName());
        Assert.assertEquals(2, object.size());
        Assert.assertThat(object, CoreMatchers.hasItem("lofasz janos"));
    }

    @Test(expected = ConfigurationError.class)
    public void failOnVersionMismatch()
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException {
        Map<String, Object> data = new HashMap<>();
        data.put(MetadataDataMapping.METADATA_VERSION_FIELD.getName(), 123456);
        getEmbeddedClient().prepareIndex(clientSettings.getIndexId(), MetadataDataMapping.METADATA_TYPE_NAME, MetadataDataMapping.METADATA_ROW_ID)
                .setSource(data).get();

        Thread.sleep(1500);

        adminHandler.createSchema();
    }

    @Test
    public void connectNodeMode() throws Exception {
        settings.setNodeConnectionMode(ElasticsearchSettingsKeys.CONNECTION_MODE_NODE);
        adminHandler.init();

        Map<String, Object> data = new HashMap<>();
        data.put("test", "test-string");
        IndexResponse idx = dataHandler.persist(data);

        Thread.sleep(2000);

        String ret = getEmbeddedClient().prepareGet(idx.getIndex(), idx.getType(), idx.getId()).get().getSourceAsString();
        Assert.assertNotNull(ret);
    }

    @Test
    public void connectTransportMode() throws InterruptedException {
        settings.setNodeConnectionMode(ElasticsearchSettingsKeys.CONNECTION_MODE_TRANSPORT_CLIENT);
        adminHandler.init();

        Map<String, Object> data = new HashMap<>();
        data.put("test", "test-string");
        IndexResponse idx = dataHandler.persist(data);

        Thread.sleep(2000);

        String ret = getEmbeddedClient().prepareGet(idx.getIndex(), idx.getType(), idx.getId()).get().getSourceAsString();
        Assert.assertNotNull(ret);
    }

    @Test
    public void enableKibanaPreConfLoadingFromDefaultFile() throws InterruptedException {
        settings.setKibanaConfigEnable(true);
        settings.setKibanaConfPath(null);

        adminHandler.init();

        Thread.sleep(1500);

        Assert.assertTrue(getEmbeddedClient().prepareExists(".kibana").get().exists());
    }
}
