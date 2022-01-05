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

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;

import org.elasticsearch.ElasticsearchGenerationException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.junit.Assert;
import org.junit.Test;

import org.n52.iceland.event.events.RequestEvent;
import org.n52.iceland.statistics.api.interfaces.datahandler.IStatisticsDataHandler;
import org.n52.janmayen.net.IPAddress;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceRequestContext;

import basetest.ElasticsearchAwareTest;

//TODO these classes needs to be in the integration test
public class SosRequestEventResolverIt extends ElasticsearchAwareTest {

    @Inject
    private SosRequestEventResolver resolve;

    @Inject
    private IStatisticsDataHandler dataHandler;

    @Test
    public void persistExceptionToDb() throws InterruptedException, ElasticsearchGenerationException, IOException {

        OwsServiceRequestContext ctx = new OwsServiceRequestContext();
        ctx.setIPAddress(new IPAddress("172.168.22.53"));
        ctx.setToken("asdf-asdf-asdf");
        GetCapabilitiesRequest r = new GetCapabilitiesRequest("SOS");
        r.setRequestContext(ctx);
        r.setService("sos service");
        r.setVersion("1.0.0");
        r.setAcceptFormats(Arrays.asList("a", "b", "c"));
        r.setAcceptVersions(Arrays.asList("1.0", "1.1"));

        resolve.setEvent(new RequestEvent(r));
        new OwsServiceRequestContext().setIPAddress(Optional.<IPAddress> of(new IPAddress("172.168.22.54")));

        dataHandler.persist(resolve.resolve());
        // eventually realtime should be enough
        Thread.sleep(2000);

        SearchResponse resp = getEmbeddedClient().search(new SearchRequest(clientSettings.getIndexId())
                .types(clientSettings.getTypeId()), RequestOptions.DEFAULT);
        Assert.assertEquals(1, resp.getHits().getTotalHits());
    }
}
