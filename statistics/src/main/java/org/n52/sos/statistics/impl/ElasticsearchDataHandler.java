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

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.base.Joiner;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.joda.time.DateTimeZone;
import org.n52.sos.statistics.api.ElasticsearchSettings;
import org.n52.sos.statistics.api.ServiceEventDataMapping;
import org.n52.sos.statistics.api.interfaces.datahandler.IAdminDataHandler;
import org.n52.sos.statistics.api.interfaces.datahandler.IStatisticsDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ElasticsearchDataHandler implements IStatisticsDataHandler {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchDataHandler.class);

    private Node node;
    private Client client;
    @Inject
    private ElasticsearchSettings settings;
    private boolean isLoggingEnabled = false;
    private IAdminDataHandler adminHandler;

    public ElasticsearchDataHandler() {
    }

    @Override
    public IndexResponse persist(Map<String, Object> dataMap) throws ElasticsearchException {
        if (client == null) {
            throw new ElasticsearchException("Client is not initialized. Data will not be persisted.");
        }
        if (!settings.isLoggingEnabled()) {
            return null;
        }

        dataMap.put(ServiceEventDataMapping.TIMESTAMP_FIELD.getName(), Calendar.getInstance(DateTimeZone.UTC.toTimeZone()));
        dataMap.put(ServiceEventDataMapping.UUID_FIELD.getName(), settings.getUuid());
        logger.debug("Persisting {}", dataMap);
        IndexResponse response =
                client.prepareIndex(settings.getIndexId(), settings.getTypeId()).setOperationThreaded(false).setSource(dataMap).get();
        return response;
    }

    /**
     * Starts client mode in local LAN mode.
     */
    private void initLanMode() {
        Objects.requireNonNull(settings.getClusterName());
        Objects.requireNonNull(settings.getClusterNodes());

        Builder settingsBuilder = ImmutableSettings.settingsBuilder();
        settingsBuilder.put("discovery.zen.ping.unicast.hosts", Joiner.on(",").join(settings.getClusterNodes()));

        node = NodeBuilder.nodeBuilder().settings(settingsBuilder).client(true).clusterName(settings.getClusterName()).node();
        client = node.client();
        logger.info("ElasticSearch data handler starting in LAN mode");

    }

    /**
     * Starts client mode in {@link TransportClient} remote mode
     */
    private void initRemoteMode() {
        Objects.requireNonNull(settings.getClusterName());
        Objects.requireNonNull(settings.getClusterNodes());

        Builder tcSettings = ImmutableSettings.settingsBuilder();
        tcSettings.put("cluster.name", settings.getClusterName());

        TransportClient cl = new TransportClient(tcSettings);
        // nodes has format host[:port]
        settings.getClusterNodes().stream().forEach(i -> {
            if (i.contains(":")) {
                String[] split = i.split(":");
                cl.addTransportAddress(new InetSocketTransportAddress(split[0], Integer.valueOf(split[1])));
            } else {
                // default communication port
                cl.addTransportAddress(new InetSocketTransportAddress(i, 9300));
            }
        });
        this.client = cl;
        logger.info("ElasticSearch data handler starting in Remote mode");
    }

    @Override
    public void destroy() {
        try {
            if (client != null) {
                logger.info("Closing ElasticSearch client");
                client.close();
            }
            if (node != null) {
                if (!node.isClosed()) {
                    logger.info("Closing ElasticSearch node");
                    node.close();
                }
            }
        } catch (ElasticsearchException e) {
            logger.error("Error closing client", e);
        } finally {
            isLoggingEnabled = false;
            adminHandler = null;
        }
    }

    @Override
    public boolean isLoggingEnabled() {
        return isLoggingEnabled && client != null;
    }

    @Override
    public ElasticsearchSettings getCurrentSettings() {
        return settings;
    }

    @Override
    public void init() {
        Objects.requireNonNull(settings);

        isLoggingEnabled = settings.isLoggingEnabled();

        logger.info("Initializing ElasticSearch Statatistics connection");
        logger.info("Settings {}", settings.toString());

        Objects.requireNonNull(settings.getIndexId());
        Objects.requireNonNull(settings.getTypeId());
        Objects.requireNonNull(settings.isNodeConnectionMode());

        if (settings.isLoggingEnabled()) {
            // init client and local node
            if (settings.isNodeConnectionMode()) {
                initLanMode();
            } else {
                initRemoteMode();
            }
            if (client != null) {
                // create schema
                adminHandler = new ElasticsearchAdminHandler(client, settings);
                try {
                    adminHandler.createSchema();
                } catch (Exception e) {
                    logger.error("Error during schema creation", e);
                    destroy();
                }

                // deploy kibana configurations
                if (settings.isKibanaConfigEnable()) {
                    logger.info("Install preconfigured kibana settings");
                    try {
                        String json = null;
                        if (settings.getKibanaConfPath() == null || settings.getKibanaConfPath().trim().isEmpty()) {
                            logger.info("No path is defined. Use default settings values");
                            json = IOUtils.toString(this.getClass().getResourceAsStream("/kibana/kibana_config.json"));
                        } else {
                            logger.info("Use content of path {}", settings.getKibanaConfPath());
                            json = IOUtils.toString(new FileInputStream(settings.getKibanaConfPath()));
                        }
                        adminHandler.importPreconfiguredKibana(json);
                    } catch (Exception e) {
                        logger.error("Error during kibana config deployment", e);
                    }
                }
            }
        } else {
            logger.info("Statistics collection is not enabled. Data will not will be collected.");
        }
    }

    @Override
    public Client getClient() {
        return client;
    }
}
