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

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.event.events.ExceptionEvent;
import org.n52.sos.statistics.api.interfaces.datahandler.IStatisticsDataHandler;
import org.n52.sos.statistics.api.mappings.ServiceEventDataMapping;
import org.n52.sos.statistics.impl.resolvers.DefaultServiceEventResolver;

import basetest.ElasticsearchAwareTest;

//TODO these classes needs to be in the integration test
public class DefaultServiceEventResolverIt extends ElasticsearchAwareTest {

    @Inject
    private DefaultServiceEventResolver resolver;

    @Inject
    private IStatisticsDataHandler dataHandler;

    @Test
    public void saveSosEvent() throws InterruptedException {

        ExceptionEvent evt = new ExceptionEvent(new NullPointerException("sos event exception"));
        resolver.setEvent(evt);

        dataHandler.persist(resolver.resolve());
        Thread.sleep(2000);

        Client client = getEmbeddedClient();
        SearchResponse resp =
                client.prepareSearch(clientSettings.getIndexId()).setTypes(clientSettings.getTypeId()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                        .setQuery(QueryBuilders.matchQuery(ServiceEventDataMapping.UNHANDLED_SERVICEEVENT_TYPE.getName(), evt.getClass().toString()))
                        .get();

        Assert.assertEquals(1, resp.getHits().getTotalHits());
    }

}
