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
package org.n52.sos.service.it.statistics;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.n52.sos.statistics.api.ServiceEventDataMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractStatisticsBase {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public static final String ES_INDEX = "ogc-statistics-index";
	public static final String ES_TYPE = "ogc-type";
	public static final int JETTY_PORT = 10101;
	public static final String SOS_ENDPOINT = "http://localhost:"+JETTY_PORT+"/service";
	private static final int SLEEP = 3500;
	
	private static final CloseableHttpClient httpClient = HttpClients.createDefault();
	
	@ClassRule
	public static RuleChain chain = RuleChain.outerRule(elasticsearch = new ElasticsearchServer()).around(jettyServer = new JettyServer());
	
	public static ElasticsearchServer elasticsearch;
	public static JettyServer jettyServer;

	public SearchResponse getLatestElasticsearchEntries() {
		return elasticsearch.getClient().prepareSearch(ES_INDEX).setTypes(ES_TYPE).addSort(SortBuilders.fieldSort(ServiceEventDataMapping.TIMESTAMP_FIELD).order(SortOrder.DESC)).get();
	}
	
	public Map<String, Object> getLastElasticsearchEntry() {
		SearchResponse searchResponse = elasticsearch.getClient().prepareSearch(ES_INDEX).setTypes(ES_TYPE).setSize(1).addSort(SortBuilders.fieldSort(ServiceEventDataMapping.TIMESTAMP_FIELD).order(SortOrder.DESC)).get();
		if(searchResponse.getHits().hits().length == 0) {
			return null;
		}
		return searchResponse.getHits().hits()[0].getSource();
	}
	
	public void postJsonAsString(String json){

		HttpPost post = new HttpPost(SOS_ENDPOINT);
		CloseableHttpResponse response = null;
		try {
			post.setEntity(new StringEntity(json,ContentType.APPLICATION_JSON));
			response = httpClient.execute(post);
			EntityUtils.consume(response.getEntity());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			IOUtils.closeQuietly(response);
		}
	}
	
	public Map<String, Object> sendAndWaitUntilRequestIsProcessed(String jsonFile) throws URISyntaxException, IOException, InterruptedException {
		String json = ExampleReaderUtil.readExample(jsonFile);
		postJsonAsString(json);
		Thread.sleep(SLEEP);
		return getLastElasticsearchEntry();
	}

}
