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

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.joda.time.DateTimeZone;
import org.n52.iceland.exception.ConfigurationError;
import org.n52.sos.statistics.api.ElasticsearchSettings;
import org.n52.sos.statistics.api.ServiceEventDataMapping;
import org.n52.sos.statistics.api.interfaces.datahandler.IAdminDataHandler;
import org.n52.sos.statistics.api.utils.KibanaImporter;
import org.n52.sos.statistics.sos.schema.SosElasticsearchSchemas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ElasticsearchAdminHandler implements IAdminDataHandler {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchAdminHandler.class);
    /**
     * Version of the database schema.
     */
    private final Client client;
    private final ElasticsearchSettings settings;

    public ElasticsearchAdminHandler(Client client, ElasticsearchSettings settings) {
        this.client = client;
        this.settings = settings;
    }

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
                throw new ConfigurationError("Database inconsistency metadata version not found in type %s",
                        ServiceEventDataMapping.METADATA_TYPE_NAME);
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

            indices.prepareCreate(settings.getIndexId()).addMapping(ServiceEventDataMapping.METADATA_TYPE_NAME, schemas.getMetadataSchema())
                    .addMapping(settings.getTypeId(), schemas.getSchema()).get();
            // insert metadata values
            createMetadataType(schemas.getSchemaVersion());
        }
    }

    @Override
    public void importPreconfiguredKibana(String configAsJson) throws JsonParseException, JsonMappingException, IOException {
        new KibanaImporter(client, ".kibana", settings.getIndexId()).importJson(configAsJson);

    }

    private Integer getCurrentVersion() {
        GetResponse resp =
                client.prepareGet(settings.getIndexId(), ServiceEventDataMapping.METADATA_TYPE_NAME, ServiceEventDataMapping.METADATA_ROW_ID)
                        .setOperationThreaded(false).get();
        if (resp.isExists()) {
            Object versionString = resp.getSourceAsMap().get(ServiceEventDataMapping.METADATA_VERSION_FIELD);
            if (versionString == null) {
                throw new ElasticsearchException(String.format("Database inconsistency. Version can't be found in row %s/%s/%s",
                        settings.getIndexId(), ServiceEventDataMapping.METADATA_TYPE_NAME, ServiceEventDataMapping.METADATA_ROW_ID));
            }
            return Integer.valueOf(versionString.toString());
        } else {
            return null;
        }
    }

    private void addUuidToMetadataIfNeeded(String uuid) throws ElasticsearchException {
        GetResponse resp =
                client.prepareGet(settings.getIndexId(), ServiceEventDataMapping.METADATA_TYPE_NAME, ServiceEventDataMapping.METADATA_ROW_ID)
                        .setOperationThreaded(false).get();

        Object retValues = resp.getSourceAsMap().get(ServiceEventDataMapping.METADATA_UUIDS_FIELD);
        List<String> values;

        if (retValues instanceof String) {
            values = new LinkedList<>();
            values.add((String) retValues);
        } else if (retValues instanceof List<?>) {
            values = (List<String>) retValues;
        } else {
            throw new ConfigurationError("Invalid %s field type %s should have String or java.util.Collection<String>",
                    ServiceEventDataMapping.METADATA_UUIDS_FIELD, retValues.getClass());
        }

        // add new uuid if needed
        if (!values.stream().anyMatch(m -> m.equals(uuid))) {
            Map<String, Object> uuids = new HashMap<String, Object>();
            values.add(uuid);
            uuids.put(ServiceEventDataMapping.METADATA_UUIDS_FIELD, values);
            uuids.put(ServiceEventDataMapping.METADATA_UPDATE_TIME_FIELD, Calendar.getInstance(DateTimeZone.UTC.toTimeZone()));
            client.prepareUpdate(settings.getIndexId(), ServiceEventDataMapping.METADATA_TYPE_NAME, "1").setDoc(uuids).get();
            logger.info("UUID {} is added to the {} type", uuid, ServiceEventDataMapping.METADATA_TYPE_NAME);
        }
    }

    private void createMetadataType(int version) {
        Map<String, Object> data = new HashMap<>();
        Calendar time = Calendar.getInstance(DateTimeZone.UTC.toTimeZone());
        data.put(ServiceEventDataMapping.METADATA_CREATION_TIME_FIELD, time);
        data.put(ServiceEventDataMapping.METADATA_UPDATE_TIME_FIELD, time);
        data.put(ServiceEventDataMapping.METADATA_VERSION_FIELD, version);
        data.put(ServiceEventDataMapping.METADATA_UUIDS_FIELD, settings.getUuid());
        client.prepareIndex(settings.getIndexId(), ServiceEventDataMapping.METADATA_TYPE_NAME, ServiceEventDataMapping.METADATA_ROW_ID)
                .setSource(data).get();
        logger.info("Initial metadata is created ceated in {}/{}", settings.getIndexId(), ServiceEventDataMapping.METADATA_TYPE_NAME);
    }
}
