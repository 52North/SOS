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
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.base.Joiner;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.joda.time.DateTimeZone;
import org.n52.iceland.exception.ConfigurationError;
import org.n52.sos.statistics.api.ElasticsearchSettings;
import org.n52.sos.statistics.api.ElasticsearchSettingsKeys;
import org.n52.sos.statistics.api.interfaces.datahandler.IAdminDataHandler;
import org.n52.sos.statistics.api.mappings.MetadataDataMapping;
import org.n52.sos.statistics.api.utils.KibanaImporter;
import org.n52.sos.statistics.impl.server.EmbeddedElasticsearch;
import org.n52.sos.statistics.sos.schema.SosElasticsearchSchemas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ElasticsearchAdminHandler implements IAdminDataHandler {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchAdminHandler.class);

    private Client client;
    private Node node;
    @Inject
    private ElasticsearchSettings settings;
    private EmbeddedElasticsearch embeddedServer = null;
    @Inject
    private ServletContext context;

    @Override
    public synchronized void deleteIndex(String index) {
        client.admin().indices().prepareDelete(index).get();
    }

    @Override
    public synchronized void createSchema() throws ElasticsearchException {
        IndicesAdminClient indices = client.admin().indices();
        SosElasticsearchSchemas schemas = new SosElasticsearchSchemas();

        if (indices.prepareExists(settings.getIndexId()).get().isExists()) {
            logger.info("Index {} already exists", settings.getIndexId());

            // update mapping
            Integer version = getCurrentVersion();
            logger.info("Elasticsearch schema version is {}", version);
            if (version == null) {
                throw new ConfigurationError("Database inconsistency. Metadata version not found in type %s", MetadataDataMapping.METADATA_TYPE_NAME);
            }
            if (version != schemas.getSchemaVersion()) {
                throw new ConfigurationError(
                        "Database schema version inconsistency. Version numbers don't match. Database version number %d <-> Application version number %d",
                        version, schemas.getSchemaVersion());
            }
            addUuidToMetadataIfNeeded(settings.getUuid());
        } else {
            logger.info("Index {} not exists creating a new one now.", settings.getIndexId());
            // create metadata table and index table table

            indices.prepareCreate(settings.getIndexId()).addMapping(MetadataDataMapping.METADATA_TYPE_NAME, schemas.getMetadataSchema())
                    .addMapping(settings.getTypeId(), schemas.getSchema()).get();
            // insert metadata values
            createMetadataType(schemas.getSchemaVersion());
        }
    }

    public void importPreconfiguredKibana() throws JsonParseException, JsonMappingException, IOException {
        String json = null;
        if (settings.getKibanaConfPath() == null || settings.getKibanaConfPath().trim().isEmpty()) {
            logger.info("No path is defined. Use default settings values");
            json = IOUtils.toString(this.getClass().getResourceAsStream("/kibana/kibana_config.json"));
        } else {
            logger.info("Use content of path {}", settings.getKibanaConfPath());
            json = IOUtils.toString(new FileInputStream(settings.getKibanaConfPath()));
        }
        new KibanaImporter(client, ".kibana", settings.getIndexId()).importJson(json);

    }

    private Integer getCurrentVersion() {
        GetResponse resp = client.prepareGet(settings.getIndexId(), MetadataDataMapping.METADATA_TYPE_NAME, MetadataDataMapping.METADATA_ROW_ID)
                .setOperationThreaded(false).get();
        if (resp.isExists()) {
            Object versionString = resp.getSourceAsMap().get(MetadataDataMapping.METADATA_VERSION_FIELD.getName());
            if (versionString == null) {
                throw new ElasticsearchException(String.format("Database inconsistency. Version can't be found in row %s/%s/%s",
                        settings.getIndexId(), MetadataDataMapping.METADATA_TYPE_NAME, MetadataDataMapping.METADATA_ROW_ID));
            }
            return Integer.valueOf(versionString.toString());
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void addUuidToMetadataIfNeeded(String uuid) throws ElasticsearchException {
        GetResponse resp = client.prepareGet(settings.getIndexId(), MetadataDataMapping.METADATA_TYPE_NAME, MetadataDataMapping.METADATA_ROW_ID)
                .setOperationThreaded(false).get();

        Object retValues = resp.getSourceAsMap().get(MetadataDataMapping.METADATA_UUIDS_FIELD.getName());
        List<String> values;

        if (retValues instanceof String) {
            values = new LinkedList<>();
            values.add((String) retValues);
        } else if (retValues instanceof List<?>) {
            values = (List<String>) retValues;
        } else {
            throw new ConfigurationError("Invalid %s field type %s should have String or java.util.Collection<String>",
                    MetadataDataMapping.METADATA_UUIDS_FIELD, retValues.getClass());
        }

        // add new uuid if needed
        if (!values.stream().anyMatch(m -> m.equals(uuid))) {
            Map<String, Object> uuids = new HashMap<String, Object>();
            values.add(uuid);
            uuids.put(MetadataDataMapping.METADATA_UUIDS_FIELD.getName(), values);
            uuids.put(MetadataDataMapping.METADATA_UPDATE_TIME_FIELD.getName(), Calendar.getInstance(DateTimeZone.UTC.toTimeZone()));
            client.prepareUpdate(settings.getIndexId(), MetadataDataMapping.METADATA_TYPE_NAME, "1").setDoc(uuids).get();
            logger.info("UUID {} is added to the {} type", uuid, MetadataDataMapping.METADATA_TYPE_NAME);
        }
    }

    private void createMetadataType(int version) {
        Map<String, Object> data = new HashMap<>();
        Calendar time = Calendar.getInstance(DateTimeZone.UTC.toTimeZone());
        data.put(MetadataDataMapping.METADATA_CREATION_TIME_FIELD.getName(), time);
        data.put(MetadataDataMapping.METADATA_UPDATE_TIME_FIELD.getName(), time);
        data.put(MetadataDataMapping.METADATA_VERSION_FIELD.getName(), version);
        data.put(MetadataDataMapping.METADATA_UUIDS_FIELD.getName(), settings.getUuid());
        client.prepareIndex(settings.getIndexId(), MetadataDataMapping.METADATA_TYPE_NAME, MetadataDataMapping.METADATA_ROW_ID).setSource(data).get();
        logger.info("Initial metadata is created ceated in {}/{}", settings.getIndexId(), MetadataDataMapping.METADATA_TYPE_NAME);
    }

    /**
     * Starts client mode in local Node mode.
     */
    private void initNodeMode() {
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
    private void initTransportMode() {
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

    private void initEmbeddedMode() {
        embeddedServer = new EmbeddedElasticsearch();
        embeddedServer.setHomePath(context.getRealPath("/WEB-INF").concat("/elasticsearch"));
        embeddedServer.init();
        client = embeddedServer.getClient();
        logger.info("ElasticSearch data handler starting in EMBEDDED mode");
    }

    @Override
    public void init() {
        Objects.requireNonNull(settings);

        logger.info("Initializing ElasticSearch Statatistics connection");
        logger.info("Settings {}", settings.toString());

        Objects.requireNonNull(settings.getIndexId());
        Objects.requireNonNull(settings.getTypeId());
        Objects.requireNonNull(settings.getNodeConnectionMode());

        if (settings.isLoggingEnabled()) {
            // init client and local node or embedded mode
            if (settings.getNodeConnectionMode().equalsIgnoreCase(ElasticsearchSettingsKeys.CONNECTION_MODE_NODE)) {
                initNodeMode();
            } else if (settings.getNodeConnectionMode().equalsIgnoreCase(ElasticsearchSettingsKeys.CONNECTION_MODE_TRANSPORT_CLIENT)) {
                initTransportMode();
            } else {
                initEmbeddedMode();
            }

            if (client != null) {
                // create schema
                try {
                    createSchema();
                } catch (Exception e) {
                    logger.error("Error during schema creation", e);
                    destroy();
                }

                // deploy kibana configurations
                if (settings.isKibanaConfigEnable()) {
                    logger.info("Install preconfigured kibana settings");
                    try {
                        importPreconfiguredKibana();
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
    public void destroy() {
        try {
            if (embeddedServer != null) {
                embeddedServer.destroy();
            }
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
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public ElasticsearchSettings getElasticsearchSettings() {
        return settings;
    }

    @Override
    public Client getElasticsearchClient() {
        return client;
    }

}
