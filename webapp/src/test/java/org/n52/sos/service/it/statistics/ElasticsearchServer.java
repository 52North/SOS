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

import java.io.File; 

import org.apache.commons.io.FileUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchServer extends ExternalResource {
	
	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchServer.class);
	
	private Node embeddedNode;
	private Client client;

	@Override
	protected void before() throws Throwable {
        Settings settings = ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch_embedded.yml").build();
        embeddedNode = NodeBuilder.nodeBuilder().settings(settings).build();
        embeddedNode.start();
        client = embeddedNode.client();
        logger.info("Elasticsearch server started");
        try 
        {
        client.admin().indices().prepareDelete(AbstractStatisticsBase.ES_INDEX).get();
        } catch (ElasticsearchException e) {
        	
        }
	}

	@Override
	protected void after() {
		embeddedNode.close();
		logger.info("Elasticsearch server closed");
		
		FileUtils.deleteQuietly(new File("data"));
	}
	
	public Client getClient() {
		return client;
	}

	
}
