/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.statistics.sos.resolvers;

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.junit.Assert;
import org.junit.Test;

import org.n52.iceland.event.events.ExceptionEvent;
import org.n52.iceland.statistics.api.interfaces.datahandler.IStatisticsDataHandler;
import org.n52.iceland.statistics.impl.resolvers.ExceptionEventResolver;
import org.n52.svalbard.decode.json.JSONDecodingException;

import basetest.ElasticsearchAwareTest;

//TODO these classes needs to be in the integration test
public class SosExceptionEventResolverIt extends ElasticsearchAwareTest {

    @Inject
    private ExceptionEventResolver resolve;

    @Inject
    private IStatisticsDataHandler dataHandler;

    @Test
    public void persistRequestToDb() throws Exception {
        JSONDecodingException exp = new JSONDecodingException("message");
        resolve.setEvent(new ExceptionEvent(exp));

        dataHandler.persist(resolve.resolve());
        // eventually realtime should be enough
        Thread.sleep(2500);

        SearchResponse resp = getEmbeddedClient().search(new SearchRequest(clientSettings.getIndexId())
                .types(clientSettings.getTypeId()), RequestOptions.DEFAULT);
        Assert.assertEquals(1L, resp.getHits().getTotalHits());

    }
}
