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

import java.net.URI;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.service.ServiceSettings;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.svalbard.Validation;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 * J&uuml;rrens</a>
 */
@Configurable
public class Constants implements Constructable {

    @Deprecated
    private static Constants instance;
    public static final String REST_RESOURCE_RELATION_FEATURES_GET = "features-get";
    public static final String REST_RESOURCE_RELATION_OBSERVATION_CREATE = "observation-create";
    public static final String REST_RESOURCE_RELATION_OBSERVATION_DELETE = "observation-delete";
    public static final String REST_RESOURCE_RELATION_OBSERVATION_GET = "observation-get";
    public static final String REST_RESOURCE_RELATION_OBSERVATIONS_GET = "observations-get";
    public static final String REST_RESOURCE_RELATION_OFFERING_GET = "offering-get";
    public static final String REST_RESOURCE_RELATION_OFFERINGS_GET = "offerings-get";
    public static final String REST_RESOURCE_RELATION_SELF = "self";
    public static final String REST_RESOURCE_RELATION_SENSOR_CREATE = "sensor-create";
    public static final String REST_RESOURCE_RELATION_SENSOR_DELETE = "sensor-delete";
    public static final String REST_RESOURCE_RELATION_SENSOR_GET = "sensor-get";
    public static final String REST_RESOURCE_RELATION_SENSORS_GET = "sensors-get";
    public static final String REST_RESOURCE_RELATION_SENSOR_UPDATE = "sensor-update";
    public static final String REST_RESOURCE_SENSORS = "sensors";
    public static final String REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET = "property-get";
    public static final String REST_RESOURCE_RELATION_FEATURE_GET = "feature-get";
    public static final String REST_RESOURCE_RELATION_OFFERINGS = "offerings";
    public static final String REST_RESOURCE_RELATION_OBSERVATIONS = "observations";
    public static final String REST_RESOURCE_RELATION_OBSERVABLE_PROPERTIES = "properties";
    public static final String REST_RESOURCE_RELATION_FEATURES = "features";
    public static final String REST_RESOURCE_RELATION_CAPABILITIES = "capabilities";
    public static final String REST_HTTP_GET_PARAMETERNAME_FEATURE = "feature";
    public static final String REST_HTTP_GET_PARAMETERNAME_NAMESPACES = "namespaces";
    public static final String REST_HTTP_GET_PARAMETERNAME_OBSERVED_PROPERTIES = "observedproperties";
    public static final String REST_HTTP_GET_PARAMETERNAME_OFFERING = "offering";
    public static final String REST_HTTP_GET_PARAMETERNAME_PROCEDURES = "procedures";
    public static final String REST_HTTP_GET_PARAMETERNAME_SPATIAL_FILTER = "spatialfilter";
    public static final String REST_HTTP_GET_PARAMETERNAME_TEMPORAL_FILTER = "temporalfilter";

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
    private String httpHeaderIdentifierXDeletedResourceId;
    private String httpOperationNotAllowedForResourceTypeMessageStart;
    private String kvpEncodingValuesplitter;
    private String landingPointResource;
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

    @Setting(RestSettings.REST_BINDING_END_POINT_RESOURCE)
    public void setBindingEndPointResource(String val) {
        this.landingPointResource = Validation.notNullOrEmpty(RestSettings.REST_BINDING_END_POINT_RESOURCE, val);
    }

    public String getConformanceClass() {
        return conformanceClass;
    }

    @Setting(RestSettings.REST_CONFORMANCE_CLASS)
    public void setConformanceClass(String val) {
        this.conformanceClass = Validation.notNullOrEmpty(RestSettings.REST_CONFORMANCE_CLASS, val);
    }

    public MediaType getContentTypeDefault() {
        return contentTypeDefault;
    }

    @Setting(RestSettings.REST_CONTENT_TYPE_DEFAULT)
    public void setContentTypeDefault(String val) {
        contentTypeDefault = mediaType(RestSettings.REST_CONTENT_TYPE_DEFAULT, val);
    }

    public MediaType getContentTypeUndefined() {
        return contentTypeUndefined;
    }

    public void setContentTypeUndefined(String val) {
        this.contentTypeUndefined = mediaType(RestSettings.REST_CONTENT_TYPE_UNDEFINED, val);
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

    @Setting(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST)
    public void setErrorMessageBadGetRequest(String val) {
        this.errorMessageBadGetRequest = Validation.notNullOrEmpty(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST, val);
    }

    public String getErrorMessageBadGetRequestById() {
        return errorMessageBadGetRequestById;
    }

    @Setting(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID)
    public void setErrorMessageBadGetRequestById(String val) {
        this.errorMessageBadGetRequestById = Validation.notNullOrEmpty(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID, val);
    }

    public String getErrorMessageBadGetRequestGlobalResource() {
        return errorMessageBadGetRequestGlobalResource;
    }

    @Setting(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE)
    public void setErrorMessageBadGetRequestGlobalResource(String val) {
        this.errorMessageBadGetRequestGlobalResource = Validation.notNullOrEmpty(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE, val);
    }

    public String getErrorMessageBadGetRequestNoValidKvpParameter() {
        return errorMessageBadGetRequestNoValidKvpParameter;
    }

    @Setting(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER)
    public void setErrorMessageBadGetRequestNoValidKvpParameter(String val) {
        this.errorMessageBadGetRequestNoValidKvpParameter = Validation.notNullOrEmpty(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER, val);
    }

    public String getErrorMessageBadGetRequestSearch() {
        return errorMessageBadGetRequestSearch;
    }

    @Setting(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH)
    public void setErrorMessageBadGetRequestSearch(String val) {
        this.errorMessageBadGetRequestSearch = Validation.notNullOrEmpty(RestSettings.REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH, val);
    }

    public String getErrorMessageHttpMethodNotAllowedForResource() {
        return errorMessageHttpMethodNotAllowedForResource;
    }

    @Setting(RestSettings.REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE)
    public void setErrorMessageHttpMethodNotAllowedForResource(String val) {
        this.errorMessageHttpMethodNotAllowedForResource = Validation.notNullOrEmpty(RestSettings.REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE, val);
    }

    public String getErrorMessageWrongContentType() {
        return errorMessageWrongContentType;
    }

    @Setting(RestSettings.REST_ERROR_MSG_WRONG_CONTENT_TYPE)
    public void setErrorMessageWrongContentType(String val) {
        this.errorMessageWrongContentType = Validation.notNullOrEmpty(RestSettings.REST_ERROR_MSG_WRONG_CONTENT_TYPE, val);
    }

    public String getErrorMessageWrongContentTypeInAcceptHeader() {
        return errorMessageWrongContentTypeInAcceptHeader;
    }

    @Setting(RestSettings.REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER)
    public void setErrorMessageWrongContentTypeInAcceptHeader(String val) {
        this.errorMessageWrongContentTypeInAcceptHeader = Validation.notNullOrEmpty(RestSettings.REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER, val);
    }

    public String getHttpHeaderIdentifierXDeletedResourceId() {
        return httpHeaderIdentifierXDeletedResourceId;
    }

    @Setting(RestSettings.REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID)
    public void setHttpHeaderIdentifierXDeletedResourceId(String val) {
        this.httpHeaderIdentifierXDeletedResourceId = Validation.notNullOrEmpty(RestSettings.REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID, val);
    }

    public String getHttpOperationNotAllowedForResourceTypeMessagePart() {
        return httpOperationNotAllowedForResourceTypeMessageStart;
    }

    public String getKvPEncodingValueSplitter() {
        return kvpEncodingValuesplitter;
    }

    public String getResourceType() {
        return resourceType;
    }

    @Setting(RestSettings.REST_RESOURCE_TYPE)
    public void setResourceType(String val) {
        this.resourceType = Validation.notNullOrEmpty(RestSettings.REST_RESOURCE_TYPE, val);
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

    @Setting(RestSettings.REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS)
    public void setSosCapabilitiesSectionNameContents(String val) {
        this.sosCapabilitiesSectionNameContents = Validation.notNullOrEmpty(RestSettings.REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS, val);
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

    @Setting(RestSettings.REST_SOS_SERVICE)
    public void setSosService(String val) {
        this.sosService = Validation.notNullOrEmpty(RestSettings.REST_SOS_SERVICE, val);
    }

    public String getSosTermsProcedureIdentifier() {
        return sosTermsProcedureidentifier;
    }

    public String getSosVersion() {
        return sosVersion;
    }

    @Setting(RestSettings.REST_SOS_VERSION)
    public void setSosVersion(String val) {
        this.sosVersion = Validation.notNullOrEmpty(RestSettings.REST_SOS_VERSION, val);
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

    @Setting(RestSettings.REST_EPSG_CODE_DEFAULT)
    public void setEpsgCodeDefault(int val) {
        this.epsgCodeDefault = Validation.greaterZero(RestSettings.REST_EPSG_CODE_DEFAULT, val);
    }

    @Setting(RestSettings.REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START)
    public void setHttpOperationNotAllowedForResourceTypeMessageStart(String val) {
        this.httpOperationNotAllowedForResourceTypeMessageStart = Validation.notNullOrEmpty(RestSettings.REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START, val);
    }

    @Setting(RestSettings.REST_KVP_ENCODING_VALUESPLITTER)
    public void setKvpEncodingValuesplitter(String val) {
        this.kvpEncodingValuesplitter = Validation.notNullOrEmpty(RestSettings.REST_KVP_ENCODING_VALUESPLITTER, val);
    }

    @Setting(RestSettings.REST_ENCODING_NAMESPACE)
    public void setRestEncodingNamespace(URI val) {
        this.restEncodingNamespace = Validation.notNull(RestSettings.REST_ENCODING_NAMESPACE, val).toString();
    }

    @Setting(RestSettings.REST_ENCODING_PREFIX)
    public void setRestEncodingPrefix(String val) {
        this.restEncodingPrefix = Validation.notNullOrEmpty(RestSettings.REST_ENCODING_PREFIX, val);
    }

    @Setting(RestSettings.REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME)
    public void setSmlCapabilityFeatureofinteresttypeName(String val) {
        this.smlCapabilityFeatureofinteresttypeName = Validation.notNullOrEmpty(RestSettings.REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME, val);
    }

    @Setting(RestSettings.REST_SML_CAPABILITY_INSERTIONMETADATA_NAME)
    public void setSmlCapabilityInsertionmetadataName(String val) {
        this.smlCapabilityInsertionmetadataName = Validation.notNullOrEmpty(RestSettings.REST_SML_CAPABILITY_INSERTIONMETADATA_NAME, val);
    }

    @Setting(RestSettings.REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME)
    public void setSmlCapabilityObservationtypeName(String val) {
        this.smlCapabilityObservationtypeName = Validation.notNullOrEmpty(RestSettings.REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME, val);
    }

    @Setting(RestSettings.REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END)
    public void setSosErrormessageOperationNotSupportedEnd(String val) {
        this.sosErrormessageOperationNotSupportedEnd = Validation.notNullOrEmpty(RestSettings.REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END, val);
    }

    @Setting(RestSettings.REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START)
    public void setSosErrormessageOperationNotSupportedStart(String val) {
        this.sosErrormessageOperationNotSupportedStart = Validation.notNullOrEmpty(RestSettings.REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START, val);
    }

    @Setting(RestSettings.REST_SOS_TERMS_PROCEDUREIDENTIFIER)
    public void setSosTermsProcedureidentifier(String val) {
        this.sosTermsProcedureidentifier = Validation.notNullOrEmpty(RestSettings.REST_SOS_TERMS_PROCEDUREIDENTIFIER, val);
    }

    @Setting(RestSettings.REST_URL_ENCODING)
    public void setUrlEncoding(String val) {
        this.urlEncoding = Validation.notNull(RestSettings.REST_URL_ENCODING, val);
    }

    @Setting(RestSettings.REST_ENCODING_SCHEMA_URL)
    public void setEncodingSchemaUrl(URI val) {
        this.encodingSchemaUrl = Validation.notNull(RestSettings.REST_ENCODING_SCHEMA_URL, val);
    }

    public URI getEncodingSchemaUrl() {
        return encodingSchemaUrl;
    }

    @Deprecated
    public static Constants getInstance() {
        return instance;
    }
}
