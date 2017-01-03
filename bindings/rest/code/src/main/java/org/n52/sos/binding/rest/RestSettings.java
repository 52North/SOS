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
package org.n52.sos.binding.rest;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 * J&uuml;rrens</a>
 *
 */
public interface RestSettings {

    String REST_CONFORMANCE_CLASS = "rest.conformanceClass";
    String REST_CONTENT_TYPE_DEFAULT = "rest.contentType.default";
    String REST_CONTENT_TYPE_UNDEFINED = "rest.contentType.undefined";
    String REST_BINDING_END_POINT_RESOURCE = "rest.bindingEndPointResource";
    String REST_EPSG_CODE_DEFAULT = "rest.epsgcode.default";
    String REST_URL_ENCODING = "rest.urlEncoding";
    String REST_ENCODING_NAMESPACE = "rest.encodingNamspace";
    String REST_ENCODING_PREFIX = "rest.encodingPrefix";
    String REST_ERROR_MSG_BAD_GET_REQUEST = "rest.errorMsg.badGetRequest";
    String REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID
            = "rest.errorMsg.badGetRequest.byId";
    String REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE
            = "rest.errorMsg.badGetRequest.globalResource";
    String REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER
            = "rest.errorMsg.badGetRequest.noValidKvpParameter";
    String REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH
            = "rest.errorMsg.badGetRequest.search";
    String REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE
            = "rest.errorMsg.HttpMethodNotAllowedForResource";
    String REST_ERROR_MSG_WRONG_CONTENT_TYPE = "rest.errorMsg.wrongContentType";
    String REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER
            = "rest.errorMSg.wrongContentType.inAcceptHeader";
    String REST_HTTP_GET_PARAMETERNAME_FOI = "rest.http.get.parametername.foi";
    String REST_HTTP_GET_PARAMETERNAME_NAMESPACES
            = "rest.http.get.parametername.namespaces";
    String REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY
            = "rest.http.get.parametername.observedproperty";
    String REST_HTTP_GET_PARAMETERNAME_OFFERING
            = "rest.http.get.parametername.offering";
    String REST_HTTP_GET_PARAMETERNAME_PROCEDURES
            = "rest.http.get.parametername.procedures";
    String REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER
            = "rest.http.get.parametername.spatialfilter";
    String REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER
            = "rest.http.get.parametername.temporalfilter";
    String REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID
            = "rest.http.header.identifier.XDeletedResourceId";
    String REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START
            = "rest.http.operationNotAllowedForResourceType.message.start";
    String REST_KVP_ENCODING_VALUESPLITTER = "rest.kvp.encoding.valuesplitter";
    String REST_RESOURCE_CAPABILITIES = "rest.resource.capabilities";
    String REST_RESOURCE_FEATURES = "rest.resource.features";
    String REST_RESOURCE_OBSERVABLEPROPERTIES
            = "rest.resource.observableproperties";
    String REST_RESOURCE_OBSERVATIONS = "rest.resource.observations";
    String REST_RESOURCE_OFFERINGS = "rest.resource.offerings";
    String REST_RESOURCE_RELATION_FEATURE_GET
            = "rest.resource.relation.feature.get";
    String REST_RESOURCE_RELATION_FEATURES_GET
            = "rest.resource.relation.features.get";
    String REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET
            = "rest.resource.relation.observableproperty.get";
    String REST_RESOURCE_RELATION_OBSERVATION_CREATE
            = "rest.resource.relation.observation.create";
    String REST_RESOURCE_RELATION_OBSERVATION_DELETE
            = "rest.resource.relation.observation.delete";
    String REST_RESOURCE_RELATION_OBSERVATION_GET
            = "rest.resource.relation.observation.get";
    String REST_RESOURCE_RELATION_OBSERVATIONS_GET
            = "rest.resource.relation.observations.get";
    String REST_RESOURCE_RELATION_OFFERING_GET
            = "rest.resource.relation.offering.get";
    String REST_RESOURCE_RELATION_OFFERINGS_GET
            = "rest.resource.relation.offerings.get";
    String REST_RESOURCE_RELATION_SELF = "rest.resource.relation.self";
    String REST_RESOURCE_RELATION_SENSOR_CREATE
            = "rest.resource.relation.sensor.create";
    String REST_RESOURCE_RELATION_SENSOR_DELETE
            = "rest.resource.relation.sensor.delete";
    String REST_RESOURCE_RELATION_SENSOR_GET
            = "rest.resource.relation.sensor.get";
    String REST_RESOURCE_RELATION_SENSORS_GET
            = "rest.resource.relation.sensors.get";
    String REST_RESOURCE_RELATION_SENSOR_UPDATE
            = "rest.resource.relation.sensor.update";
    String REST_RESOURCE_SENSORS = "rest.resource.sensors";
    String REST_RESOURCE_TYPE = "rest.resource.type";
    String REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME
            = "rest.sml.capability.featureofinteresttype.name";
    String REST_SML_CAPABILITY_INSERTIONMETADATA_NAME
            = "rest.sml.capability.insertionmetadata.name";
    String REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME
            = "rest.sml.capability.observationtype.name";
    String REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS
            = "rest.sos.capabilities.section.name.contents";
    String REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END
            = "rest.sos.errormessage.operationNotSupported.end";
    String REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START
            = "rest.sos.errormessage.operationNotSupported.start";
    String REST_SOS_SERVICE = "rest.sos.service";
    String REST_SOS_TERMS_PROCEDUREIDENTIFIER
            = "rest.sos.terms.procedureidentifier";
    String REST_SOS_VERSION = "rest.sos.version";
    String REST_ENCODING_SCHEMA_URL = "rest.encodingSchemaUrl";

}
