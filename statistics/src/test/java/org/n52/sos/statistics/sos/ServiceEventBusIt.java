/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.statistics.sos;

import java.io.IOException;

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.event.events.CountingOutputStreamEvent;
import org.n52.iceland.event.events.ExceptionEvent;
import org.n52.iceland.event.events.OutgoingResponseEvent;
import org.n52.iceland.event.events.RequestEvent;
import org.n52.iceland.event.events.ResponseEvent;
import org.n52.iceland.statistics.impl.AbstractStatisticsServiceEventListener;
import org.n52.janmayen.event.EventBus;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.net.IPAddress;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.service.OwsServiceRequestContext;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;

import basetest.ElasticsearchAwareTest;

@Deprecated
public class ServiceEventBusIt extends ElasticsearchAwareTest {

    private static final String IP = "241.56.199.99";
    private static final String PROCEDURE = "http://www.test.ru/producer1";
    private static final String SERVICE = "sos";
    private static final String VERSION = "10.1";
    private static final String FORAMT = "my-format";


    @Inject
    private AbstractStatisticsServiceEventListener listener;

    @Inject
    private EventBus serviceBus;

    @Test
    public void sendSosNormalFlowToElasticSearch() throws InterruptedException, IOException {

        OwsServiceRequestContext ctx = new OwsServiceRequestContext();
        ctx.setIPAddress(new IPAddress(IP));

        DescribeSensorRequest request = new DescribeSensorRequest();
        request.setRequestContext(ctx);
        request.setProcedure(PROCEDURE);
        request.setService(SERVICE);
        request.setVersion(VERSION);
        request.setProcedureDescriptionFormat(FORAMT);

        RequestEvent evt = new RequestEvent(request);

        // ExceptionEvent exceptionEvent = new ExceptionEvent(new
        // NoApplicableCodeException());
        DescribeSensorResponse resp = new DescribeSensorResponse();
        resp.setContentType(new MediaType("text", "plain"));

        ResponseEvent respEvent = new ResponseEvent(resp);

        OutgoingResponseEvent outgoingResponseEvent = new OutgoingResponseEvent(null, null, 100L, 1234L);

        CountingOutputStreamEvent countingEvent = new CountingOutputStreamEvent(1234L);

        serviceBus.submit(evt);
        serviceBus.submit(respEvent);
        serviceBus.submit(countingEvent);
        serviceBus.submit(outgoingResponseEvent);

        // wait for the other thread to stop, hopefully
        Thread.sleep(9000);

        SearchResponse response = getEmbeddedClient().search(new SearchRequest(clientSettings.getIndexId())
                .types(clientSettings.getTypeId()), RequestOptions.DEFAULT);

        logger.info(response.toString());
        SearchHit hit = response.getHits().getAt(0);
        Assert.assertNotNull(hit);
        // MatcherAssert.assertThat(hit.getSourceAsMap().values(),
        // CoreMatchers.hasItem(request.getOperationName()));
        MatcherAssert.assertThat(hit.getSourceAsMap().values(), CoreMatchers.hasItem(request.getOperationName()));

    }

    @Test
    public void sendSosExceptionFlowTriadtToElasticSearch() throws InterruptedException, IOException {
        OwsServiceRequestContext ctx = new OwsServiceRequestContext();
        ctx.setIPAddress(new IPAddress(IP));

        DescribeSensorRequest request = new DescribeSensorRequest();
        request.setRequestContext(ctx);
        request.setProcedure(PROCEDURE);
        request.setService(SERVICE);
        request.setVersion(VERSION);
        request.setProcedureDescriptionFormat(FORAMT);

        RequestEvent evt = new RequestEvent(request);

        ExceptionEvent exceptionEvent = new ExceptionEvent(new NoApplicableCodeException());

        OutgoingResponseEvent outgoingResponseEvent = new OutgoingResponseEvent(null, null, 100L, 1234L);

        serviceBus.submit(evt);
        serviceBus.submit(exceptionEvent);
        serviceBus.submit(outgoingResponseEvent);

        // wait for the other thread to stop, hopefully
        Thread.sleep(9000);

        SearchResponse response = getEmbeddedClient().search(new SearchRequest(clientSettings.getIndexId())
                .types(clientSettings.getTypeId()), RequestOptions.DEFAULT);

        logger.info(response.toString());
        SearchHit hit = response.getHits().getAt(0);
        Assert.assertNotNull(hit);
        // MatcherAssert.assertThat(hit.getSourceAsMap().values(),
        // CoreMatchers.hasItem(request.getOperationName()));
        MatcherAssert.assertThat(hit.getSourceAsMap().values(), CoreMatchers.hasItem(request.getOperationName()));
    }

    @Override
    protected void setUpHook() {
        serviceBus.register(listener);
    }

    @After
    public void tearDown() {
        serviceBus.unregister(listener);
    }
}
