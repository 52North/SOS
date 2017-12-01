/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package basetest;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.n52.iceland.statistics.api.ElasticsearchSettings;
import org.n52.iceland.statistics.impl.ElasticsearchAdminHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {  "classpath:testContext.xml" })
public abstract class ElasticsearchAwareTest extends SpringBaseTest {

    private static Node embeddedNode;

    @ClassRule
    public static final TemporaryFolder tempFolder = new TemporaryFolder();

    @Inject
    protected ElasticsearchSettings clientSettings;

    @Inject
    protected ElasticsearchAdminHandler adminHandler;

    @BeforeClass
    public static void init() throws IOException, InterruptedException {
        logger.debug("Starting embedded node");
        Settings settings
                = Settings.settingsBuilder().put("cluster.name", "elasiticsearch")
                        .put("discovery.zen.ping.multicast.enabled", "false")
                        .put("http.cors.enabled", "false")
                        .put("path.data", tempFolder.getRoot().toPath().resolve("data").toString())
                        .put("path.home", "/")
                        .put("node.data", "true")
                        .put("node.master", "true")
                        .put("node.name", "Embedded Elasticsearch")
                        .build();
//        Settings settings =
//                Settings.settingsBuilder()
//                        .loadFromStream("elasticsearch_embedded.yml",
//                                ElasticsearchAwareTest.class.getResourceAsStream("/elasticsearch_embedded.yml"))
//                        .build();
        embeddedNode = NodeBuilder.nodeBuilder().settings(settings).build();
        embeddedNode.start();



        logger.debug("Started embedded node");

    }

    @Before
    public void setUp() throws InterruptedException {

        try {
            logger.info("Deleting {} index", clientSettings.getIndexId());
            Thread.sleep(2000);
            embeddedNode.client().admin().indices().prepareDelete(clientSettings.getIndexId()).get().isAcknowledged();
            Thread.sleep(2000);
        } catch (ElasticsearchException e) {
        }
        setUpHook();
    }

    protected void setUpHook() {
    }

    @AfterClass
    public static void destroy() throws IOException {
        logger.debug("Closing embedded node");
        embeddedNode.close();

        FileUtils.deleteDirectory(new File(".\\data"));

    }

    protected static Client getEmbeddedClient() {
        return embeddedNode.client();
    }

}
