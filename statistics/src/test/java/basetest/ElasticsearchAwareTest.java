/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.NodeValidationException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.n52.iceland.statistics.api.ElasticsearchSettings;
import org.n52.iceland.statistics.impl.ElasticsearchAdminHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:testContext.xml" })
public abstract class ElasticsearchAwareTest extends SpringBaseTest {

    @ClassRule
    public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    private static EmbeddedElastic embeddedNode;

    @Inject
    protected ElasticsearchSettings clientSettings;

    @Inject
    protected ElasticsearchAdminHandler adminHandler;

    @BeforeClass
    public static void init() throws IOException, InterruptedException, NodeValidationException {
        logger.debug("Starting embedded node");
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch")
                // = Settings.builder().put("cluster.name", "elasiticsearch")
                .put("http.cors.enabled", Boolean.FALSE.toString())
                .put("path.data", TEMP_FOLDER.getRoot().toPath().resolve("data").toString()).put("path.home", "/")
                .put("node.data", Boolean.TRUE.toString()).put("node.master", Boolean.TRUE.toString())
                .put("node.name", "Embedded Elasticsearch").build();
        // Settings settings =
        // Settings.settingsBuilder()
        // .loadFromStream("elasticsearch_embedded.yml",
        // ElasticsearchAwareTest.class.getResourceAsStream("/elasticsearch_embedded.yml"))
        // .build();
        // embeddedNode = new Node(new Environment(settings,
        // TEMP_FOLDER.getRoot().toPath()));

        pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic.Builder builder =
                EmbeddedElastic.builder().withElasticVersion("6.3.0").withPlugin("groovy");
        builder.withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9300);
        for (String key : settings.keySet()) {
            builder.withSetting(key, settings.get(key));
        }
        embeddedNode = builder.build();
        embeddedNode.start();

        logger.debug("Started embedded node");

    }

    @Before
    public void setUp() throws InterruptedException, IOException {

        try {
            logger.info("Deleting {} index", clientSettings.getIndexId());
            Thread.sleep(2000);
            getEmbeddedClient().indices()
                    .create(new CreateIndexRequest(clientSettings.getIndexId()), RequestOptions.DEFAULT)
                    .isAcknowledged();
            Thread.sleep(2000);
        } catch (ElasticsearchException e) {
            logger.error("Error when setting up the test!", e);
        }
        setUpHook();
    }

    protected void setUpHook() {
    }

    @AfterClass
    public static void destroy() throws IOException {
        logger.debug("Closing embedded node");
        embeddedNode.stop();

        FileUtils.deleteDirectory(new File(".\\data"));

    }

    protected static RestHighLevelClient getEmbeddedClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", embeddedNode.getTransportTcpPort())));
    }

}
