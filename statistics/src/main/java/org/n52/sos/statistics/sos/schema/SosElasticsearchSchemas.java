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
package org.n52.sos.statistics.sos.schema;

import java.util.Map;

import org.n52.sos.statistics.api.ServiceEventDataMapping;
import org.n52.sos.statistics.impl.schemabuilders.DefaultElasticsearchSchemas;
import org.n52.sos.statistics.impl.schemabuilders.ElasticsearchSchemaBuilder;
import org.n52.sos.statistics.sos.SosDataMapping;

public class SosElasticsearchSchemas extends DefaultElasticsearchSchemas {

    @Override
    protected void appSpecificSchema(ElasticsearchSchemaBuilder schema) {

        sosDefaultFields(schema);
        getCapabilities(schema);
        describeSensor(schema);
        getObservation(schema);
        getObservationById(schema);
        getFeatureOfInterest(schema);
        insertSensor(schema);
        updateSensor(schema);
        deleteSensor(schema);
        insertObservation(schema);
        inesertResultTemplate(schema);
        insertResult(schema);
        getResultTemplate(schema);
        getResult(schema);
        getDataAvailability(schema);
    }

    private void sosDefaultFields(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(ServiceEventDataMapping.SR_VERSION_FIELD).addStringField(ServiceEventDataMapping.SR_SERVICE_FIELD);
        schema.addStringField(ServiceEventDataMapping.SR_LANGUAGE_FIELD).addStringField(ServiceEventDataMapping.SR_OPERATION_NAME_FIELD);
        schema.addStringField(ServiceEventDataMapping.SR_IP_ADDRESS_FIELD).addStringField(ServiceEventDataMapping.SR_CONTENT_TYPE);
        schema.addStringField(ServiceEventDataMapping.SR_ACCEPT_TYPES).addBooleanField(ServiceEventDataMapping.PROXIED_REQUEST_FIELD);

        Map<String, Object> geoloc =
                ElasticsearchSchemaBuilder.builder().addStringField(ServiceEventDataMapping.GEO_LOC_CITY_CODE)
                        .addStringField(ServiceEventDataMapping.GEO_LOC_COUNTRY_CODE).addGeoPointField(ServiceEventDataMapping.GEO_LOC_GEOPOINT)
                        .build();
        schema.addObject(ServiceEventDataMapping.SR_GEO_LOC_FIELD, geoloc);
    }

    private void getCapabilities(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.GC_VERSIONS_FIELD);
        schema.addStringField(SosDataMapping.GC_FORMATS_FIELD);
        schema.addStringField(SosDataMapping.GC_SECTIONS);
        schema.addStringField(SosDataMapping.GC_UPDATE_SEQUENCE);
    }

    private void describeSensor(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.DS_PROCEDURE);
        schema.addStringField(SosDataMapping.DS_PROCEDURE_DESC_FORMAT);
        schema.addStringField(SosDataMapping.GC_SECTIONS);

        ElasticsearchSchemaBuilder builder = ElasticsearchSchemaBuilder.builder();
        builder.addLongField(SosDataMapping.DS_VALID_TIME_DURATION);
        builder.addDateField(SosDataMapping.DS_VALID_TIME_START);
        builder.addDateField(SosDataMapping.DS_VALID_TIME_END);
        builder.addDateField(SosDataMapping.DS_VALID_TIME_TIMEINSTANT);

        schema.addObject(SosDataMapping.DS_VALID_TIME, builder.build());
    }

    private void getObservation(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.GO_PROCEDURES);
        schema.addStringField(SosDataMapping.GO_SPATIAL_FILTER);
        schema.addStringField(SosDataMapping.GO_OBSERVED_PROPERTIES);
        schema.addStringField(SosDataMapping.GO_OFFERINGS);
        schema.addStringField(SosDataMapping.GO_FEATURE_OF_INTERESTS);
    }

    private void getObservationById(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.GOBID_OBSERVATION_IDENTIFIER);
    }

    private void getFeatureOfInterest(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.GFOI_FEATURE_IDENTIFIERS);
        schema.addStringField(SosDataMapping.GFOI_NAMESPACES);
        schema.addStringField(SosDataMapping.GFOI_OBSERVED_PROPERTIES);
        schema.addStringField(SosDataMapping.GFOI_PROCEDURES);
    }

    private void insertSensor(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.IS_ASSIGNED_OFFERINGS);
        schema.addStringField(SosDataMapping.IS_ASSIGNED_PROCEDURE_IDENTIFIERS);
        schema.addStringField(SosDataMapping.IS_OBSERVABLE_PROPERTY);
        schema.addStringField(SosDataMapping.IS_PROCEDURE_DESCRIPTION);
        schema.addStringField(SosDataMapping.IS_PROCEDURE_DESCRIPTION_FORMAT);
    }

    private void updateSensor(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.US_PROCEDURE_IDENTIFIER);
        schema.addStringField(SosDataMapping.US_PROCEDURE_DESCRIPTION_FORMAT);
    }

    private void deleteSensor(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.DELS_PROCEDURE_IDENTIFIER);
    }

    private void insertObservation(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.IO_ASSIGNED_SENSORID);
        schema.addStringField(SosDataMapping.IO_OFFERINGS);
    }

    private void inesertResultTemplate(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.IRT_IDENTIFIER);
        schema.addStringField(SosDataMapping.IRT_RESULT_ENCODING);
    }

    private void insertResult(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.IR_TEMPLATE_IDENTIFIER);
    }

    private void getResultTemplate(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.GRT_OBSERVED_PROPERTY);
        schema.addStringField(SosDataMapping.GRT_OFFERING);
    }

    private void getResult(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.GR_FEATURE_IDENTIFIERS);
        schema.addStringField(SosDataMapping.GR_NAMSPACES);
        schema.addStringField(SosDataMapping.GR_OBSERVATION_TEMPLATE_IDENTIFIER);
        schema.addStringField(SosDataMapping.GR_OBSERVATION_PROPERTY);
        schema.addStringField(SosDataMapping.GR_OFFERING);
    }

    private void getDataAvailability(ElasticsearchSchemaBuilder schema) {
        schema.addStringField(SosDataMapping.GDA_FEATURES_OF_INTEREST);
        schema.addStringField(SosDataMapping.GDA_NAMESPACE);
        schema.addStringField(SosDataMapping.GDA_OBSERVED_PROPERTIES);
        schema.addStringField(SosDataMapping.GDA_OFFERINGS);
        schema.addStringField(SosDataMapping.GDA_PROCEDURES);
    }

    @Override
    public int getSchemaVersion() {
        return 1;
    }

}
