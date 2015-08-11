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
package org.n52.sos.statistics.api.utils;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.statistics.api.ElasticsearchSettings;
import org.n52.iceland.statistics.api.utils.KibanaImporter;
import org.n52.iceland.statistics.impl.ElasticsearchAdminHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import basetest.ElasticsearchAwareTest;

public class KibanaImporterTest extends ElasticsearchAwareTest {

    @Inject
    private ElasticsearchSettings settings;

    @Inject
    private ElasticsearchAdminHandler admin;

    @Test
    public void importValidJson() throws IOException, InterruptedException {
        String json = IOUtils.toString(KibanaImporter.class.getResourceAsStream("/kibana/kibana_config.json"));
        new KibanaImporter(getEmbeddedClient(), ".kibana", "my-index").importJson(json);
        Thread.sleep(1500);
        Assert.assertTrue(getEmbeddedClient().prepareExists(".kibana").get().exists());

        SearchResponse resp = getEmbeddedClient().prepareSearch(".kibana").setTypes("visualization").get();
        Assert.assertTrue(resp.getHits().getTotalHits() > 0);

        for (SearchHit hit : resp.getHits().getHits()) {
            Assert.assertFalse(hit.getSourceAsString().contains(KibanaImporter.INDEX_NEEDLE));
        }

        SearchResponse resp2 = getEmbeddedClient().prepareSearch(".kibana").setTypes("dashboard").get();
        Assert.assertTrue(resp2.getHits().getTotalHits() > 0);

        for (SearchHit hit : resp2.getHits().getHits()) {
            Assert.assertFalse(hit.getSourceAsString().contains(KibanaImporter.INDEX_NEEDLE));
        }
    }

    @Test(
            expected = JsonParseException.class)
    public void importInvalidJson() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        new KibanaImporter(getEmbeddedClient(), "local-index", "").importJson("semmi latnivali nincs itt");
        Thread.sleep(1500);
        Assert.assertFalse(getEmbeddedClient().prepareExists(".kibana").get().exists());
    }

    @Test
    public void setKibanaImporterFalseAfterRun() {
        settings.setKibanaConfigEnable(true);
        admin.init();

        Assert.assertFalse(settings.isKibanaConfigEnable());
    }

}
