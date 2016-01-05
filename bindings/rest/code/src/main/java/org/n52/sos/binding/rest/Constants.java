/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import static org.n52.sos.binding.rest.RestSettings.REST_BINDING_END_POINT_RESOURCE;
import static org.n52.sos.binding.rest.RestSettings.REST_CONFORMANCE_CLASS;
import static org.n52.sos.binding.rest.RestSettings.REST_CONTENT_TYPE_DEFAULT;
import static org.n52.sos.binding.rest.RestSettings.REST_CONTENT_TYPE_UNDEFINED;
import static org.n52.sos.binding.rest.RestSettings.REST_ENCODING_NAMESPACE;
import static org.n52.sos.binding.rest.RestSettings.REST_ENCODING_PREFIX;
import static org.n52.sos.binding.rest.RestSettings.REST_ENCODING_SCHEMA_URL;
import static org.n52.sos.binding.rest.RestSettings.REST_EPSG_CODE_DEFAULT;
import static org.n52.sos.binding.rest.RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST;
import static org.n52.sos.binding.rest.RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID;
import static org.n52.sos.binding.rest.RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE;
import static org.n52.sos.binding.rest.RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER;
import static org.n52.sos.binding.rest.RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH;
import static org.n52.sos.binding.rest.RestSettings.REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE;
import static org.n52.sos.binding.rest.RestSettings.REST_ERROR_MSG_WRONG_CONTENT_TYPE;
import static org.n52.sos.binding.rest.RestSettings.REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER;
import static org.n52.sos.binding.rest.RestSettings.REST_HTTP_GET_PARAMETERNAME_FOI;
import static org.n52.sos.binding.rest.RestSettings.REST_HTTP_GET_PARAMETERNAME_NAMESPACES;
import static org.n52.sos.binding.rest.RestSettings.REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY;
import static org.n52.sos.binding.rest.RestSettings.REST_HTTP_GET_PARAMETERNAME_OFFERING;
import static org.n52.sos.binding.rest.RestSettings.REST_HTTP_GET_PARAMETERNAME_PROCEDURES;
import static org.n52.sos.binding.rest.RestSettings.REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER;
import static org.n52.sos.binding.rest.RestSettings.REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER;
import static org.n52.sos.binding.rest.RestSettings.REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID;
import static org.n52.sos.binding.rest.RestSettings.REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START;
import static org.n52.sos.binding.rest.RestSettings.REST_KVP_ENCODING_VALUESPLITTER;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_CAPABILITIES;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_FEATURES;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_OBSERVABLEPROPERTIES;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_OBSERVATIONS;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_OFFERINGS;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_FEATURES_GET;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_FEATURE_GET;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_OBSERVATIONS_GET;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_OBSERVATION_CREATE;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_OBSERVATION_DELETE;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_OBSERVATION_GET;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_OFFERINGS_GET;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_OFFERING_GET;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_SELF;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_SENSORS_GET;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_SENSOR_CREATE;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_SENSOR_DELETE;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_SENSOR_GET;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_RELATION_SENSOR_UPDATE;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_SENSORS;
import static org.n52.sos.binding.rest.RestSettings.REST_RESOURCE_TYPE;
import static org.n52.sos.binding.rest.RestSettings.REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME;
import static org.n52.sos.binding.rest.RestSettings.REST_SML_CAPABILITY_INSERTIONMETADATA_NAME;
import static org.n52.sos.binding.rest.RestSettings.REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_SERVICE;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_TERMS_PROCEDUREIDENTIFIER;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_VERSION;
import static org.n52.sos.binding.rest.RestSettings.REST_URL_ENCODING;

import java.net.URI;

import org.n52.iceland.config.annotation.Configurable;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.exception.ConfigurationError;
import org.n52.iceland.lifecycle.Constructable;
import org.n52.iceland.service.ServiceSettings;
import org.n52.iceland.util.Validation;
import org.n52.iceland.util.http.MediaType;
import org.n52.sos.ogc.sensorML.SensorMLConstants;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 * J&uuml;rrens</a>
 */
@Configurable
public class Constants implements Constructable {

    @Deprecated
    private static Constants instance;

    private String conformanceClass;
    private MediaType contentTypeDefault;
    private MediaType contentTypeUndefined;
    private int epsgCodeDefault;
    private String errorMessageBadGetRequest;
    private String errorMessageBadGetRequestById;
    private String errorMessageBadGetRequestGlobalResource;
    private String errorMessageBadGetRequestNoValidKvpParameter;
    private String errorMessageBadGetRequestSearch;
    private String errorMessageHttpMethodNotAllowedForResource;
    private String errorMessageWrongContentType;
    private String errorMessageWrongContentTypeInAcceptHeader;
    private String httpGetParameternameFoi;
    private String httpGetParameternameNamespaces;
    private String httpGetParameternameObservedproperty;
    private String httpGetParameternameOffering;
    private String httpGetParameternameProcedures;
    private String httpGetParameternameSpatialfilter;
    private String httpGetParameternameTemporalfilter;
    private String httpHeaderIdentifierXDeletedResourceId;
    private String httpOperationNotAllowedForResourceTypeMessageStart;
    private String kvpEncodingValuesplitter;
    private String landingPointResource;
    private String resourceCapabilities;
    private String resourceFeatures;
    private String resourceObservableproperties;
    private String resourceObservations;
    private String resourceOfferings;
    private String resourceRelationFeatureGet;
    private String resourceRelationFeaturesGet;
    private String resourceRelationObservablepropertyGet;
    private String resourceRelationObservationCreate;
    private String resourceRelationObservationDelete;
    private String resourceRelationObservationGet;
    private String resourceRelationObservationsGet;
    private String resourceRelationOfferingGet;
    private String resourceRelationOfferingsGet;
    private String resourceRelationSelf;
    private String resourceRelationSensorCreate;
    private String resourceRelationSensorDelete;
    private String resourceRelationSensorGet;
    private String resourceRelationSensorsGet;
    private String resourceRelationSensorUpdate;
    private String resourceSensors;
    private String resourceType;
    private String restEncodingNamespace;
    private String restEncodingPrefix;
    private String serviceUrl;
    private String smlCapabilityFeatureofinteresttypeName;
    private String smlCapabilityInsertionmetadataName;
    private String smlCapabilityObservationtypeName;
    private String sosCapabilitiesSectionNameContents;
    private String sosErrormessageOperationNotSupportedEnd;
    private String sosErrormessageOperationNotSupportedStart;
    private String sosService;
    private String sosTermsProcedureidentifier;
    private String sosVersion;
    private String urlEncoding;
    private URI encodingSchemaUrl;

    @Override
    public void init() {
        Constants.instance = this;
    }

    public String getBindingEndPointResource() {
        return landingPointResource;
    }

    @Setting(REST_BINDING_END_POINT_RESOURCE)
    public void setBindingEndPointResource(String val) {
        this.landingPointResource = Validation.notNullOrEmpty(REST_BINDING_END_POINT_RESOURCE, val);
    }

    public String getConformanceClass() {
        return conformanceClass;
    }

    @Setting(REST_CONFORMANCE_CLASS)
    public void setConformanceClass(String val) {
        this.conformanceClass = Validation.notNullOrEmpty(REST_CONFORMANCE_CLASS, val);
    }

    public MediaType getContentTypeDefault() {
        return contentTypeDefault;
    }

    @Setting(REST_CONTENT_TYPE_DEFAULT)
    public void setContentTypeDefault(String val) {
        contentTypeDefault = mediaType(REST_CONTENT_TYPE_DEFAULT, val);
    }

    public MediaType getContentTypeUndefined() {
        return contentTypeUndefined;
    }

    public void setContentTypeUndefined(String val) {
        this.contentTypeUndefined = mediaType(REST_CONTENT_TYPE_UNDEFINED, val);
    }

    public String getDefaultDescribeSensorOutputFormat() {
        return SensorMLConstants.NS_SML;
    }

    public String getDefaultUrlEncoding() {
        return urlEncoding;
    }

    public String getEncodingNamespace() {
        return restEncodingNamespace;
    }

    public String getEncodingPrefix() {
        return restEncodingPrefix;
    }

    public String getErrorMessageBadGetRequest() {
        return errorMessageBadGetRequest;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST)
    public void setErrorMessageBadGetRequest(String val) {
        this.errorMessageBadGetRequest = Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST, val);
    }

    public String getErrorMessageBadGetRequestById() {
        return errorMessageBadGetRequestById;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID)
    public void setErrorMessageBadGetRequestById(String val) {
        this.errorMessageBadGetRequestById = Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID, val);;
    }

    public String getErrorMessageBadGetRequestGlobalResource() {
        return errorMessageBadGetRequestGlobalResource;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE)
    public void setErrorMessageBadGetRequestGlobalResource(String val) {
        this.errorMessageBadGetRequestGlobalResource = Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE, val);
    }

    public String getErrorMessageBadGetRequestNoValidKvpParameter() {
        return errorMessageBadGetRequestNoValidKvpParameter;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER)
    public void setErrorMessageBadGetRequestNoValidKvpParameter(String val) {
        this.errorMessageBadGetRequestNoValidKvpParameter = Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER, val);
    }

    public String getErrorMessageBadGetRequestSearch() {
        return errorMessageBadGetRequestSearch;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH)
    public void setErrorMessageBadGetRequestSearch(String val) {
        this.errorMessageBadGetRequestSearch = Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH, val);
    }

    public String getErrorMessageHttpMethodNotAllowedForResource() {
        return errorMessageHttpMethodNotAllowedForResource;
    }

    @Setting(REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE)
    public void setErrorMessageHttpMethodNotAllowedForResource(String val) {
        this.errorMessageHttpMethodNotAllowedForResource = Validation.notNullOrEmpty(REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE, val);
    }

    public String getErrorMessageWrongContentType() {
        return errorMessageWrongContentType;
    }

    @Setting(REST_ERROR_MSG_WRONG_CONTENT_TYPE)
    public void setErrorMessageWrongContentType(String val) {
        this.errorMessageWrongContentType = Validation.notNullOrEmpty(REST_ERROR_MSG_WRONG_CONTENT_TYPE, val);
    }

    public String getErrorMessageWrongContentTypeInAcceptHeader() {
        return errorMessageWrongContentTypeInAcceptHeader;
    }

    @Setting(REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER)
    public void setErrorMessageWrongContentTypeInAcceptHeader(String val) {
        this.errorMessageWrongContentTypeInAcceptHeader = Validation.notNullOrEmpty(REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER, val);
    }

    public String getHttpGetParameterNameFoi() {
        return httpGetParameternameFoi;
    }

    public String getHttpGetParameterNameNamespaces() {
        return httpGetParameternameNamespaces;
    }

    public String getHttpGetParameterNameObservedProperty() {
        return httpGetParameternameObservedproperty;
    }

    public String getHttpGetParameterNameOffering() {
        return httpGetParameternameOffering;
    }

    public String getHttpGetParameterNameProcedure() {
        return httpGetParameternameProcedures;
    }

    public String getHttpGetParameterNameSpatialFilter() {
        return httpGetParameternameSpatialfilter;
    }

    public String getHttpGetParameterNameTemporalFilter() {
        return httpGetParameternameTemporalfilter;
    }

    public String getHttpHeaderIdentifierXDeletedResourceId() {
        return httpHeaderIdentifierXDeletedResourceId;
    }

    @Setting(REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID)
    public void setHttpHeaderIdentifierXDeletedResourceId(String val) {
        this.httpHeaderIdentifierXDeletedResourceId = Validation.notNullOrEmpty(REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID, val);
    }

    public String getHttpOperationNotAllowedForResourceTypeMessagePart() {
        return httpOperationNotAllowedForResourceTypeMessageStart;
    }

    public String getKvPEncodingValueSplitter() {
        return kvpEncodingValuesplitter;
    }

    public String getResourceCapabilities() {
        return resourceCapabilities;
    }

    @Setting(REST_RESOURCE_CAPABILITIES)
    public void setResourceCapabilities(String val) {
        this.resourceCapabilities = Validation.notNullOrEmpty(REST_RESOURCE_CAPABILITIES, val);
    }

    public String getResourceFeatures() {
        return resourceFeatures;
    }

    @Setting(REST_RESOURCE_FEATURES)
    public void setResourceFeatures(String val) {
        this.resourceFeatures = Validation.notNullOrEmpty(REST_RESOURCE_FEATURES, val);
    }

    public String getResourceObservableProperties() {
        return resourceObservableproperties;
    }

    public String getResourceObservations() {
        return resourceObservations;
    }

    @Setting(REST_RESOURCE_OBSERVATIONS)
    public void setResourceObservations(String val) {
        this.resourceObservations = Validation.notNullOrEmpty(REST_RESOURCE_OBSERVATIONS, val);
    }

    public String getResourceOfferings() {
        return resourceOfferings;
    }

    @Setting(REST_RESOURCE_OFFERINGS)
    public void setResourceOfferings(String val) {
        this.resourceOfferings = Validation.notNullOrEmpty(REST_RESOURCE_OFFERINGS, val);
    }

    public String getResourceRelationFeatureGet() {
        return resourceRelationFeatureGet;
    }

    @Setting(REST_RESOURCE_RELATION_FEATURE_GET)
    public void setResourceRelationFeatureGet(String val) {
        this.resourceRelationFeatureGet = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_FEATURE_GET, val);
    }

    public String getResourceRelationFeaturesGet() {
        return resourceRelationFeaturesGet;
    }

    @Setting(REST_RESOURCE_RELATION_FEATURES_GET)
    public void setResourceRelationFeaturesGet(String val) {
        this.resourceRelationFeaturesGet = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_FEATURES_GET, val);
    }

    public String getResourceRelationObservablePropertyGet() {
        return resourceRelationObservablepropertyGet;
    }

    public String getResourceRelationObservationCreate() {
        return resourceRelationObservationCreate;
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVATION_CREATE)
    public void setResourceRelationObservationCreate(String val) {
        this.resourceRelationObservationCreate = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATION_CREATE, val);
    }

    public String getResourceRelationObservationDelete() {
        return resourceRelationObservationDelete;
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVATION_DELETE)
    public void setResourceRelationObservationDelete(String val) {
        this.resourceRelationObservationDelete = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATION_DELETE, val);
    }

    public String getResourceRelationObservationGet() {
        return resourceRelationObservationGet;
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVATION_GET)
    public void setResourceRelationObservationGet(String val) {
        this.resourceRelationObservationGet = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATION_GET, val);
    }

    public String getResourceRelationObservationsGet() {
        return resourceRelationObservationsGet;
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVATIONS_GET)
    public void setResourceRelationObservationsGet(String val) {
        this.resourceRelationObservationsGet = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATIONS_GET, val);
    }

    public String getResourceRelationOfferingGet() {
        return resourceRelationOfferingGet;
    }

    @Setting(REST_RESOURCE_RELATION_OFFERING_GET)
    public void setResourceRelationOfferingGet(String val) {
        this.resourceRelationOfferingGet = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OFFERING_GET, val);
    }

    public String getResourceRelationOfferingsGet() {
        return resourceRelationOfferingsGet;
    }

    @Setting(REST_RESOURCE_RELATION_OFFERINGS_GET)
    public void setResourceRelationOfferingsGet(String val) {
        this.resourceRelationOfferingsGet = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OFFERINGS_GET, val);
    }

    public String getResourceRelationSelf() {
        return resourceRelationSelf;
    }

    @Setting(REST_RESOURCE_RELATION_SELF)
    public void setResourceRelationSelf(String val) {
        this.resourceRelationSelf = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SELF, val);
    }

    public String getResourceRelationSensorCreate() {
        return resourceRelationSensorCreate;
    }

    @Setting(REST_RESOURCE_RELATION_SENSOR_CREATE)
    public void setResourceRelationSensorCreate(String val) {
        this.resourceRelationSensorCreate = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_CREATE, val);
    }

    public String getResourceRelationSensorDelete() {
        return resourceRelationSensorDelete;
    }

    @Setting(REST_RESOURCE_RELATION_SENSOR_DELETE)
    public void setResourceRelationSensorDelete(String val) {
        this.resourceRelationSensorDelete = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_DELETE, val);
    }

    public String getResourceRelationSensorGet() {
        return resourceRelationSensorGet;
    }

    @Setting(REST_RESOURCE_RELATION_SENSOR_GET)
    public void setResourceRelationSensorGet(String val) {
        this.resourceRelationSensorGet = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_GET, val);
    }

    public String getResourceRelationSensorsGet() {
        return resourceRelationSensorsGet;
    }

    @Setting(REST_RESOURCE_RELATION_SENSORS_GET)
    public void setResourceRelationSensorsGet(String val) {
        this.resourceRelationSensorsGet = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSORS_GET, val);
    }

    public String getResourceRelationSensorUpdate() {
        return resourceRelationSensorUpdate;
    }

    @Setting(REST_RESOURCE_RELATION_SENSOR_UPDATE)
    public void setResourceRelationSensorUpdate(String val) {
        this.resourceRelationSensorUpdate = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_UPDATE, val);
    }

    public String getResourceSensors() {
        return resourceSensors;
    }

    @Setting(REST_RESOURCE_SENSORS)
    public void setResourceSensors(String val) {
        this.resourceSensors = Validation.notNullOrEmpty(REST_RESOURCE_SENSORS, val);
    }

    public String getResourceType() {
        return resourceType;
    }

    @Setting(REST_RESOURCE_TYPE)
    public void setResourceType(String val) {
        this.resourceType = Validation.notNullOrEmpty(REST_RESOURCE_TYPE, val);
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    @Setting(ServiceSettings.SERVICE_URL)
    public void setServiceUrl(URI serviceURL) {
        Validation.notNull(ServiceSettings.SERVICE_URL, serviceURL);
        String url = serviceURL.toString();
        if (url.contains("?")) {
            url = url.split("[?]")[0];
        }
        serviceUrl = url;
    }

    public String getSmlCapabilityFeatureOfInterestTypeName() {
        return smlCapabilityFeatureofinteresttypeName;
    }

    public String getSmlCapabilityInsertMetadataName() {
        return smlCapabilityInsertionmetadataName;
    }

    public String getSmlCapabilityObservationTypeName() {
        return smlCapabilityObservationtypeName;
    }

    public String getSosCapabilitiesSectionNameContents() {
        return sosCapabilitiesSectionNameContents;
    }

    @Setting(REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS)
    public void setSosCapabilitiesSectionNameContents(String val) {
        this.sosCapabilitiesSectionNameContents = Validation.notNullOrEmpty(REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS, val);
    }

    public String getSosErrorMessageOperationNotSupportedEnd() {
        return sosErrormessageOperationNotSupportedEnd;
    }

    public String getSosErrorMessageOperationNotSupportedStart() {
        return sosErrormessageOperationNotSupportedStart;
    }

    public String getSosService() {
        return sosService;
    }

    @Setting(REST_SOS_SERVICE)
    public void setSosService(String val) {
        this.sosService = Validation.notNullOrEmpty(REST_SOS_SERVICE, val);
    }

    public String getSosTermsProcedureIdentifier() {
        return sosTermsProcedureidentifier;
    }

    public String getSosVersion() {
        return sosVersion;
    }

    @Setting(REST_SOS_VERSION)
    public void setSosVersion(String val) {
        this.sosVersion = Validation.notNullOrEmpty(REST_SOS_VERSION, val);
    }

    public int getSpatialReferenceSystemEpsgIdDefault() {
        return epsgCodeDefault;
    }

    private MediaType mediaType(String setting, String mediaType) {
        Validation.notNullOrEmpty(setting, mediaType);
        try {
            return MediaType.parse(mediaType);
        } catch (IllegalArgumentException e) {
            throw new ConfigurationError(String
                    .format("%s is not a valid content type!", mediaType));
        }
    }

    @Setting(REST_EPSG_CODE_DEFAULT)
    public void setEpsgCodeDefault(int val) {
        this.epsgCodeDefault = Validation.greaterZero(REST_EPSG_CODE_DEFAULT, val);
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_FOI)
    public void setHttpGetParameternameFoi(String val) {
        this.httpGetParameternameFoi = Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_FOI, val);
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_NAMESPACES)
    public void setHttpGetParameternameNamespaces(String val) {
        this.httpGetParameternameNamespaces = Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_NAMESPACES, val);
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY)
    public void setHttpGetParameternameObservedproperty(String val) {
        this.httpGetParameternameObservedproperty = Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY, val);
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_OFFERING)
    public void setHttpGetParameternameOffering(String val) {
        this.httpGetParameternameOffering = Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_OFFERING, val);
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_PROCEDURES)
    public void setHttpGetParameternameProcedures(String val) {
        this.httpGetParameternameProcedures = Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_PROCEDURES, val);
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER)
    public void setHttpGetParameternameSpatialfilter(String val) {
        this.httpGetParameternameSpatialfilter = Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER, val);
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER)
    public void setHttpGetParameternameTemporalfilter(String val) {
        this.httpGetParameternameTemporalfilter = Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER, val);
    }

    @Setting(REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START)
    public void setHttpOperationNotAllowedForResourceTypeMessageStart(String val) {
        this.httpOperationNotAllowedForResourceTypeMessageStart = Validation.notNullOrEmpty(REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START, val);
    }

    @Setting(REST_KVP_ENCODING_VALUESPLITTER)
    public void setKvpEncodingValuesplitter(String val) {
        this.kvpEncodingValuesplitter = Validation.notNullOrEmpty(REST_KVP_ENCODING_VALUESPLITTER, val);
    }

    @Setting(REST_RESOURCE_OBSERVABLEPROPERTIES)
    public void setResourceObservableproperties(String val) {
        this.resourceObservableproperties = Validation.notNullOrEmpty(REST_RESOURCE_OBSERVABLEPROPERTIES, val);
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET)
    public void setResourceRelationObservablepropertyGet(String val) {
        this.resourceRelationObservablepropertyGet = Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET, val);
    }

    @Setting(REST_ENCODING_NAMESPACE)
    public void setRestEncodingNamespace(URI val) {
        this.restEncodingNamespace = Validation.notNull(REST_ENCODING_NAMESPACE, val).toString();
    }

    @Setting(REST_ENCODING_PREFIX)
    public void setRestEncodingPrefix(String val) {
        this.restEncodingPrefix = Validation.notNullOrEmpty(REST_ENCODING_PREFIX, val);
    }

    @Setting(REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME)
    public void setSmlCapabilityFeatureofinteresttypeName(String val) {
        this.smlCapabilityFeatureofinteresttypeName = Validation.notNullOrEmpty(REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME, val);
    }

    @Setting(REST_SML_CAPABILITY_INSERTIONMETADATA_NAME)
    public void setSmlCapabilityInsertionmetadataName(String val) {
        this.smlCapabilityInsertionmetadataName = Validation.notNullOrEmpty(REST_SML_CAPABILITY_INSERTIONMETADATA_NAME, val);
    }

    @Setting(REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME)
    public void setSmlCapabilityObservationtypeName(String val) {
        this.smlCapabilityObservationtypeName = Validation.notNullOrEmpty(REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME, val);
    }

    @Setting(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END)
    public void setSosErrormessageOperationNotSupportedEnd(String val) {
        this.sosErrormessageOperationNotSupportedEnd = Validation.notNullOrEmpty(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END, val);
    }

    @Setting(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START)
    public void setSosErrormessageOperationNotSupportedStart(String val) {
        this.sosErrormessageOperationNotSupportedStart = Validation.notNullOrEmpty(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START, val);
    }

    @Setting(REST_SOS_TERMS_PROCEDUREIDENTIFIER)
    public void setSosTermsProcedureidentifier(String val) {
        this.sosTermsProcedureidentifier = Validation.notNullOrEmpty(REST_SOS_TERMS_PROCEDUREIDENTIFIER, val);
    }

    @Setting(REST_URL_ENCODING)
    public void setUrlEncoding(String val) {
        this.urlEncoding = Validation.notNull(REST_URL_ENCODING, val);
    }

    @Setting(RestSettings.REST_ENCODING_SCHEMA_URL)
    public void setEncodingSchemaUrl(URI val) {
        this.encodingSchemaUrl = Validation.notNull(REST_ENCODING_SCHEMA_URL, val);
    }

    public URI getEncodingSchemaUrl() {
        return encodingSchemaUrl;
    }

    @Deprecated
    public static Constants getInstance() {
        return instance;
    }
}
