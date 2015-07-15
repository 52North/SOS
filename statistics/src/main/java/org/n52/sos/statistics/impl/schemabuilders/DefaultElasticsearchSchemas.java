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
package org.n52.sos.statistics.impl.schemabuilders;

import java.util.Map;

import org.n52.sos.statistics.api.ServiceEventDataMapping;

/**
 * Abstract class for further application specific Elasticsearch schema
 * creation.
 *
 */
public abstract class DefaultElasticsearchSchemas {

    private final ElasticsearchSchemaBuilder schema = ElasticsearchSchemaBuilder.builder();

    public final Map<String, Object> getSchema() {
        addDefaultFields(schema);
        icelandExceptions(schema);
        extensions(schema);

        appSpecificSchema(schema);

        return schema.build();
    }

    /**
     * Override this method and insert your application specific schemas to
     * Elasticsearch
     * 
     * @param schema
     */
    protected abstract void appSpecificSchema(final ElasticsearchSchemaBuilder schema);

    public abstract int getSchemaVersion();

    private void addDefaultFields(ElasticsearchSchemaBuilder schema) {
        schema.addDateField(ServiceEventDataMapping.TIMESTAMP_FIELD);
        schema.addStringField(ServiceEventDataMapping.UUID_FIELD);
        schema.addStringField(ServiceEventDataMapping.UNHANDLED_SERVICEEVENT_TYPE);
    }

    private void icelandExceptions(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(ServiceEventDataMapping.EX_STATUS);
        schema.addStringField(ServiceEventDataMapping.EX_VERSION);
        schema.addStringField(ServiceEventDataMapping.EX_MESSAGE);
        schema.addStringField(ServiceEventDataMapping.CEX_LOCATOR);
        schema.addStringField(ServiceEventDataMapping.CEX_SOAP_FAULT);
        schema.addStringField(ServiceEventDataMapping.OWSEX_NAMESPACE);
    }

    private void extensions(ElasticsearchSchemaBuilder schema) {
        ElasticsearchSchemaBuilder extension =
                ElasticsearchSchemaBuilder.builder().addStringField(ServiceEventDataMapping.EXT_DEFINITION)
                        .addStringField(ServiceEventDataMapping.EXT_IDENTIFIER).addStringField(ServiceEventDataMapping.EXT_VALUE);

        schema.addObject(ServiceEventDataMapping.SR_EXTENSIONS, extension.build());
    }

    public final Map<String, Object> getMetadataSchema() {
        ElasticsearchSchemaBuilder schema = ElasticsearchSchemaBuilder.builder();
        schema.addIntegerField(ServiceEventDataMapping.METADATA_VERSION_FIELD).addDateField(ServiceEventDataMapping.METADATA_CREATION_TIME_FIELD)
                .addDateField(ServiceEventDataMapping.METADATA_UPDATE_TIME_FIELD).addStringField(ServiceEventDataMapping.METADATA_UUIDS_FIELD);
        return schema.build();
    }
}
