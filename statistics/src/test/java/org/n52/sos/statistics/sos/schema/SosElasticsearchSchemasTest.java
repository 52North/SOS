/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.statistics.sos.schema;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import basetest.ElasticsearchAwareTest;

public class SosElasticsearchSchemasTest extends ElasticsearchAwareTest {

    private final String idx = "schema-creator-index";
    private final String type = "mytpe";

    @Before
    public void setUp() throws InterruptedException {
        try {
            getEmbeddedClient().admin().indices().prepareDelete(idx).get();
            Thread.sleep(3000);
        } catch (ElasticsearchException e) {
        }
    }

    @Test
    public void createSchema() {
        SosElasticsearchSchemas sch = new SosElasticsearchSchemas();
        getEmbeddedClient().admin().indices().prepareCreate(idx).addMapping(type, sch.getSchema()).get();

        GetMappingsResponse resp = getEmbeddedClient().admin().indices().prepareGetMappings(idx).addTypes(type).get();
        Assert.assertNotNull(resp.getMappings());
    }

    @Test
    public void createMetaDataSchema() {
        SosElasticsearchSchemas sch = new SosElasticsearchSchemas();
        getEmbeddedClient().admin().indices().prepareCreate(idx).addMapping(type, sch.getMetadataSchema()).get();

        GetMappingsResponse resp = getEmbeddedClient().admin().indices().prepareGetMappings(idx).addTypes(type).get();
        Assert.assertNotNull(resp.getMappings());
    }
}
