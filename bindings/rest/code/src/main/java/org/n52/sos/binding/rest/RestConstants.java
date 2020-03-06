/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_SERVICE;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_TERMS_PROCEDUREIDENTIFIER;
import static org.n52.sos.binding.rest.RestSettings.REST_SOS_VERSION;
import static org.n52.sos.binding.rest.RestSettings.REST_URLPATTERN;
import static org.n52.sos.binding.rest.RestSettings.REST_URL_ENCODING;

import java.net.URI;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.service.ServiceSettings;
import org.n52.sos.util.Validation;
import org.n52.sos.util.http.MediaType;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 */
@Configurable
public final class RestConstants {

    private static RestConstants instance = null;

    public static synchronized RestConstants getInstance() {
        if (instance == null) {
            instance = new RestConstants();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    // configurable settings - for details see RestSettings class
    private String conformanceClass;
    private MediaType contentTypeDefault;
    private MediaType contentTypeUndefined;
    private URI encodingSchemaUrl;
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
    private String restEncodingNamespace;
    private String restEncodingPrefix;
    private String serviceUrl;
    private String sosErrormessageOperationNotSupportedEnd;
    private String sosErrormessageOperationNotSupportedStart;
    private String sosService;
    private String sosTermsProcedureidentifier;
    private String sosVersion;
    private String urlEncoding;
    private String urlpattern;
    public static final String SECTION_IDENTIFIER_CONTENTS = "Contents";

    private RestConstants() {
    }

    public String getBindingEndPointResource() {
        return landingPointResource;
    }

    public String getConformanceClass() {
        return conformanceClass;
    }

    public MediaType getContentTypeDefault() {
        return contentTypeDefault;
    }

    public MediaType getContentTypeUndefined() {
        return contentTypeUndefined;
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

    public URI getEncodingSchemaUrl() {
        return encodingSchemaUrl;
    }

    public String getErrorMessageBadGetRequest() {
        return errorMessageBadGetRequest;
    }

    public String getErrorMessageBadGetRequestById() {
        return errorMessageBadGetRequestById;
    }

    public String getErrorMessageBadGetRequestGlobalResource() {
        return errorMessageBadGetRequestGlobalResource;
    }

    public String getErrorMessageBadGetRequestNoValidKvpParameter() {
        return errorMessageBadGetRequestNoValidKvpParameter;
    }

    public String getErrorMessageBadGetRequestSearch() {
        return errorMessageBadGetRequestSearch;
    }

    public String getErrorMessageHttpMethodNotAllowedForResource() {
        return errorMessageHttpMethodNotAllowedForResource;
    }

    public String getErrorMessageWrongContentType() {
        return errorMessageWrongContentType;
    }

    public String getErrorMessageWrongContentTypeInAcceptHeader() {
        return errorMessageWrongContentTypeInAcceptHeader;
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

    public String getHttpOperationNotAllowedForResourceTypeMessagePart() {
        return httpOperationNotAllowedForResourceTypeMessageStart;
    }

    public String getKvPEncodingValueSplitter() {
        return kvpEncodingValuesplitter;
    }

    public String getResourceCapabilities() {
        return resourceCapabilities;
    }

    public String getResourceFeatures() {
        return resourceFeatures;
    }

    public String getResourceObservableProperties() {
        return resourceObservableproperties;
    }

    public String getResourceObservations() {
        return resourceObservations;
    }

    public String getResourceOfferings() {
        return resourceOfferings;
    }

    public String getResourceRelationFeatureGet() {
        return resourceRelationFeatureGet;
    }

    public String getResourceRelationFeaturesGet() {
        return resourceRelationFeaturesGet;
    }

    public String getResourceRelationObservablePropertyGet() {
        return resourceRelationObservablepropertyGet;
    }

    public String getResourceRelationObservationCreate() {
        return resourceRelationObservationCreate;
    }

    public String getResourceRelationObservationDelete() {
        return resourceRelationObservationDelete;
    }

    public String getResourceRelationObservationGet() {
        return resourceRelationObservationGet;
    }

    public String getResourceRelationObservationsGet() {
        return resourceRelationObservationsGet;
    }

    public String getResourceRelationOfferingGet() {
        return resourceRelationOfferingGet;
    }

    public String getResourceRelationOfferingsGet() {
        return resourceRelationOfferingsGet;
    }

    public String getResourceRelationSelf() {
        return resourceRelationSelf;
    }

    public String getResourceRelationSensorCreate() {
        return resourceRelationSensorCreate;
    }

    public String getResourceRelationSensorDelete() {
        return resourceRelationSensorDelete;
    }

    public String getResourceRelationSensorGet() {
        return resourceRelationSensorGet;
    }

    public String getResourceRelationSensorsGet() {
        return resourceRelationSensorsGet;
    }

    public String getResourceRelationSensorUpdate() {
        return resourceRelationSensorUpdate;
    }

    public String getResourceSensors() {
        return resourceSensors;
    }

    public String getServiceUrl() {
        return serviceUrl;
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

    public String getSosTermsProcedureIdentifier() {
        return sosTermsProcedureidentifier;
    }

    public String getSosVersion() {
        return sosVersion;
    }

    public int getSpatialReferenceSystemEpsgIdDefault() {
        return epsgCodeDefault;
    }

    public String getUrlPattern() {
        return urlpattern;
    }

    @Setting(REST_BINDING_END_POINT_RESOURCE)
    public void setBindingEndPointResource(final String landingPointResource) {
        Validation.notNullOrEmpty(REST_BINDING_END_POINT_RESOURCE, landingPointResource);
        this.landingPointResource = landingPointResource;
    }

    @Setting(REST_CONFORMANCE_CLASS)
    public void setConformanceClass(final String conformanceClass) {
        Validation.notNullOrEmpty(REST_CONFORMANCE_CLASS, conformanceClass);
        this.conformanceClass = conformanceClass;
    }

    @Setting(REST_CONTENT_TYPE_DEFAULT)
    public void setContentTypeDefault(final String defaultContentType) {
        contentTypeDefault = mediaType(REST_CONTENT_TYPE_DEFAULT, defaultContentType);
    }

    public void setContentTypeUndefined(final String contentTypeUndefined) {
        this.contentTypeUndefined = mediaType(REST_CONTENT_TYPE_UNDEFINED, contentTypeUndefined);
    }

    @Setting(RestSettings.REST_ENCODING_SCHEMA_URL)
    public void setEncodingSchemaUrl(final URI encodingSchemaUrl) {
        Validation.notNull(REST_ENCODING_SCHEMA_URL, encodingSchemaUrl);
        this.encodingSchemaUrl = encodingSchemaUrl;
    }

    @Setting(REST_EPSG_CODE_DEFAULT)
    public void setEpsgCodeDefault(final int epsgCodeDefault) {
        Validation.greaterZero(REST_EPSG_CODE_DEFAULT, epsgCodeDefault);
        this.epsgCodeDefault = epsgCodeDefault;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST)
    public void setErrorMessageBadGetRequest(final String errorMessageBadGetRequest) {
        Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST, errorMessageBadGetRequest);
        this.errorMessageBadGetRequest = errorMessageBadGetRequest;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID)
    public void setErrorMessageBadGetRequestById(final String errorMessageBadGetRequestById) {
        Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID, errorMessageBadGetRequestById);
        this.errorMessageBadGetRequestById = errorMessageBadGetRequestById;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE)
    public void setErrorMessageBadGetRequestGlobalResource(final String errorMessageBadGetRequestGlobalResource) {
        Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE,
                errorMessageBadGetRequestGlobalResource);
        this.errorMessageBadGetRequestGlobalResource = errorMessageBadGetRequestGlobalResource;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER)
    public void setErrorMessageBadGetRequestNoValidKvpParameter(
            final String errorMessageBadGetRequestNoValidKvpParameter) {
        Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER,
                errorMessageBadGetRequestNoValidKvpParameter);
        this.errorMessageBadGetRequestNoValidKvpParameter = errorMessageBadGetRequestNoValidKvpParameter;
    }

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH)
    public void setErrorMessageBadGetRequestSearch(final String errorMessageBadGetRequestSearch) {
        Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH, errorMessageBadGetRequestSearch);
        this.errorMessageBadGetRequestSearch = errorMessageBadGetRequestSearch;
    }

    @Setting(REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE)
    public void setErrorMessageHttpMethodNotAllowedForResource(
            final String errorMessageHttpMethodNotAllowedForResource) {
        Validation.notNullOrEmpty(REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE,
                errorMessageHttpMethodNotAllowedForResource);
        this.errorMessageHttpMethodNotAllowedForResource = errorMessageHttpMethodNotAllowedForResource;
    }

    @Setting(REST_ERROR_MSG_WRONG_CONTENT_TYPE)
    public void setErrorMessageWrongContentType(final String errorMessageWrongContentType) {
        Validation.notNullOrEmpty(REST_ERROR_MSG_WRONG_CONTENT_TYPE, errorMessageWrongContentType);
        this.errorMessageWrongContentType = errorMessageWrongContentType;
    }

    @Setting(REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER)
    public void setErrorMessageWrongContentTypeInAcceptHeader(final String errorMessageWrongContentTypeInAcceptHeader) {
        Validation.notNullOrEmpty(REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER,
                errorMessageWrongContentTypeInAcceptHeader);
        this.errorMessageWrongContentTypeInAcceptHeader = errorMessageWrongContentTypeInAcceptHeader;
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_FOI)
    public void setHttpGetParameternameFoi(final String httpGetParameternameFoi) {
        Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_FOI, httpGetParameternameFoi);
        this.httpGetParameternameFoi = httpGetParameternameFoi;
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_NAMESPACES)
    public void setHttpGetParameternameNamespaces(final String httpGetParameternameNamespaces) {
        Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_NAMESPACES, httpGetParameternameNamespaces);
        this.httpGetParameternameNamespaces = httpGetParameternameNamespaces;
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY)
    public void setHttpGetParameternameObservedproperty(final String httpGetParameternameObservedproperty) {
        Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY, httpGetParameternameObservedproperty);
        this.httpGetParameternameObservedproperty = httpGetParameternameObservedproperty;
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_OFFERING)
    public void setHttpGetParameternameOffering(final String httpGetParameternameOffering) {
        Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_OFFERING, httpGetParameternameOffering);
        this.httpGetParameternameOffering = httpGetParameternameOffering;
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_PROCEDURES)
    public void setHttpGetParameternameProcedures(final String httpGetParameternameProcedures) {
        Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_PROCEDURES, httpGetParameternameProcedures);
        this.httpGetParameternameProcedures = httpGetParameternameProcedures;
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER)
    public void setHttpGetParameternameSpatialfilter(final String httpGetParameternameSpatialfilter) {
        Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER, httpGetParameternameSpatialfilter);
        this.httpGetParameternameSpatialfilter = httpGetParameternameSpatialfilter;
    }

    @Setting(REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER)
    public void setHttpGetParameternameTemporalfilter(final String httpGetParameternameTemporalfilter) {
        Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER, httpGetParameternameTemporalfilter);
        this.httpGetParameternameTemporalfilter = httpGetParameternameTemporalfilter;
    }

    @Setting(REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID)
    public void setHttpHeaderIdentifierXDeletedResourceId(final String httpHeaderIdentifierXDeletedResourceId) {
        Validation.notNullOrEmpty(REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID,
                httpHeaderIdentifierXDeletedResourceId);
        this.httpHeaderIdentifierXDeletedResourceId = httpHeaderIdentifierXDeletedResourceId;
    }

    @Setting(REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START)
    public void setHttpOperationNotAllowedForResourceTypeMessageStart(
            final String httpOperationNotAllowedForResourceTypeMessageStart) {
        Validation.notNullOrEmpty(REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START,
                httpOperationNotAllowedForResourceTypeMessageStart);
        this.httpOperationNotAllowedForResourceTypeMessageStart = httpOperationNotAllowedForResourceTypeMessageStart;
    }

    @Setting(REST_KVP_ENCODING_VALUESPLITTER)
    public void setKvpEncodingValuesplitter(final String kvpEncodingValuesplitter) {
        Validation.notNullOrEmpty(REST_KVP_ENCODING_VALUESPLITTER, kvpEncodingValuesplitter);
        this.kvpEncodingValuesplitter = kvpEncodingValuesplitter;
    }

    @Setting(REST_RESOURCE_CAPABILITIES)
    public void setResourceCapabilities(final String resourceCapabilities) {
        Validation.notNullOrEmpty(REST_RESOURCE_CAPABILITIES, resourceCapabilities);
        this.resourceCapabilities = resourceCapabilities;
    }

    @Setting(REST_RESOURCE_FEATURES)
    public void setResourceFeatures(final String resourceFeatures) {
        Validation.notNullOrEmpty(REST_RESOURCE_FEATURES, resourceFeatures);
        this.resourceFeatures = resourceFeatures;
    }

    @Setting(REST_RESOURCE_OBSERVABLEPROPERTIES)
    public void setResourceObservableproperties(final String resourceObservableproperties) {
        Validation.notNullOrEmpty(REST_RESOURCE_OBSERVABLEPROPERTIES, resourceObservableproperties);
        this.resourceObservableproperties = resourceObservableproperties;
    }

    @Setting(REST_RESOURCE_OBSERVATIONS)
    public void setResourceObservations(final String resourceObservations) {
        Validation.notNullOrEmpty(REST_RESOURCE_OBSERVATIONS, resourceObservations);
        this.resourceObservations = resourceObservations;
    }

    @Setting(REST_RESOURCE_OFFERINGS)
    public void setResourceOfferings(final String resourceOfferings) {
        Validation.notNullOrEmpty(REST_RESOURCE_OFFERINGS, resourceOfferings);
        this.resourceOfferings = resourceOfferings;
    }

    @Setting(REST_RESOURCE_RELATION_FEATURE_GET)
    public void setResourceRelationFeatureGet(final String resourceRelationFeatureGet) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_FEATURE_GET, resourceRelationFeatureGet);
        this.resourceRelationFeatureGet = resourceRelationFeatureGet;
    }

    @Setting(REST_RESOURCE_RELATION_FEATURES_GET)
    public void setResourceRelationFeaturesGet(final String resourceRelationFeaturesGet) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_FEATURES_GET, resourceRelationFeaturesGet);
        this.resourceRelationFeaturesGet = resourceRelationFeaturesGet;
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET)
    public void setResourceRelationObservablepropertyGet(final String resourceRelationObservablepropertyGet) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET, resourceRelationObservablepropertyGet);
        this.resourceRelationObservablepropertyGet = resourceRelationObservablepropertyGet;
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVATION_CREATE)
    public void setResourceRelationObservationCreate(final String resourceRelationObservationCreate) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATION_CREATE, resourceRelationObservationCreate);
        this.resourceRelationObservationCreate = resourceRelationObservationCreate;
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVATION_DELETE)
    public void setResourceRelationObservationDelete(final String resourceRelationObservationDelete) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATION_DELETE, resourceRelationObservationDelete);
        this.resourceRelationObservationDelete = resourceRelationObservationDelete;
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVATION_GET)
    public void setResourceRelationObservationGet(final String resourceRelationObservationGet) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATION_GET, resourceRelationObservationGet);
        this.resourceRelationObservationGet = resourceRelationObservationGet;
    }

    @Setting(REST_RESOURCE_RELATION_OBSERVATIONS_GET)
    public void setResourceRelationObservationsGet(final String resourceRelationObservationsGet) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATIONS_GET, resourceRelationObservationsGet);
        this.resourceRelationObservationsGet = resourceRelationObservationsGet;
    }

    @Setting(REST_RESOURCE_RELATION_OFFERING_GET)
    public void setResourceRelationOfferingGet(final String resourceRelationOfferingGet) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OFFERING_GET, resourceRelationOfferingGet);
        this.resourceRelationOfferingGet = resourceRelationOfferingGet;
    }

    @Setting(REST_RESOURCE_RELATION_OFFERINGS_GET)
    public void setResourceRelationOfferingsGet(final String resourceRelationOfferingsGet) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OFFERINGS_GET, resourceRelationOfferingsGet);
        this.resourceRelationOfferingsGet = resourceRelationOfferingsGet;
    }

    @Setting(REST_RESOURCE_RELATION_SELF)
    public void setResourceRelationSelf(final String resourceRelationSelf) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SELF, resourceRelationSelf);
        this.resourceRelationSelf = resourceRelationSelf;
    }

    @Setting(REST_RESOURCE_RELATION_SENSOR_CREATE)
    public void setResourceRelationSensorCreate(final String resourceRelationSensorCreate) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_CREATE, resourceRelationSensorCreate);
        this.resourceRelationSensorCreate = resourceRelationSensorCreate;
    }

    @Setting(REST_RESOURCE_RELATION_SENSOR_DELETE)
    public void setResourceRelationSensorDelete(final String resourceRelationSensorDelete) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_DELETE, resourceRelationSensorDelete);
        this.resourceRelationSensorDelete = resourceRelationSensorDelete;
    }

    @Setting(REST_RESOURCE_RELATION_SENSOR_GET)
    public void setResourceRelationSensorGet(final String resourceRelationSensorGet) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_GET, resourceRelationSensorGet);
        this.resourceRelationSensorGet = resourceRelationSensorGet;
    }

    @Setting(REST_RESOURCE_RELATION_SENSORS_GET)
    public void setResourceRelationSensorsGet(final String resourceRelationSensorsGet) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSORS_GET, resourceRelationSensorsGet);
        this.resourceRelationSensorsGet = resourceRelationSensorsGet;
    }

    @Setting(REST_RESOURCE_RELATION_SENSOR_UPDATE)
    public void setResourceRelationSensorUpdate(final String resourceRelationSensorUpdate) {
        Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_UPDATE, resourceRelationSensorUpdate);
        this.resourceRelationSensorUpdate = resourceRelationSensorUpdate;
    }

    @Setting(REST_RESOURCE_SENSORS)
    public void setResourceSensors(final String resourceSensors) {
        Validation.notNullOrEmpty(REST_RESOURCE_SENSORS, resourceSensors);
        this.resourceSensors = resourceSensors;
    }

    @Setting(REST_ENCODING_NAMESPACE)
    public void setRestEncodingNamespace(final URI restEncodingNamespace) {
        Validation.notNull(REST_ENCODING_NAMESPACE, restEncodingNamespace);
        this.restEncodingNamespace = restEncodingNamespace.toString();
    }

    @Setting(REST_ENCODING_PREFIX)
    public void setRestEncodingPrefix(final String restEncodingPrefix) {
        Validation.notNullOrEmpty(REST_ENCODING_PREFIX, restEncodingPrefix);
        this.restEncodingPrefix = restEncodingPrefix;
    }

    @Setting(ServiceSettings.SERVICE_URL)
    public void setServiceUrl(final URI serviceURL) {
        Validation.notNull("Service URL", serviceURL);
        String url = serviceURL.toString();
        if (url.contains("?")) {
            url = url.split("[?]")[0];
        }
        serviceUrl = url;
    }

    @Setting(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END)
    public void setSosErrormessageOperationNotSupportedEnd(final String sosErrormessageOperationNotSupportedEnd) {
        Validation.notNullOrEmpty(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END,
                sosErrormessageOperationNotSupportedEnd);
        this.sosErrormessageOperationNotSupportedEnd = sosErrormessageOperationNotSupportedEnd;
    }

    @Setting(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START)
    public void setSosErrormessageOperationNotSupportedStart(final String sosErrormessageOperationNotSupportedStart) {
        Validation.notNullOrEmpty(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START,
                sosErrormessageOperationNotSupportedStart);
        this.sosErrormessageOperationNotSupportedStart = sosErrormessageOperationNotSupportedStart;
    }

    @Setting(REST_SOS_SERVICE)
    public void setSosService(final String sosService) {
        Validation.notNullOrEmpty(REST_SOS_SERVICE, sosService);
        this.sosService = sosService;
    }

    @Setting(REST_SOS_TERMS_PROCEDUREIDENTIFIER)
    public void setSosTermsProcedureidentifier(final String sosTermsProcedureidentifier) {
        Validation.notNullOrEmpty(REST_SOS_TERMS_PROCEDUREIDENTIFIER, sosTermsProcedureidentifier);
        this.sosTermsProcedureidentifier = sosTermsProcedureidentifier;
    }

    @Setting(REST_SOS_VERSION)
    public void setSosVersion(final String sosVersion) {
        Validation.notNullOrEmpty(REST_SOS_VERSION, sosVersion);
        this.sosVersion = sosVersion;
    }

    @Setting(REST_URL_ENCODING)
    public void setUrlEncoding(final String urlEncoding) {
        Validation.notNull(REST_URL_ENCODING, urlEncoding);
        this.urlEncoding = urlEncoding;
    }

    @Setting(REST_URLPATTERN)
    public void setUrlpattern(final String urlpattern) {
        Validation.notNullOrEmpty(REST_URLPATTERN, urlpattern);
        this.urlpattern = urlpattern;
    }

    private MediaType mediaType(final String setting, final String mediaType) {
        Validation.notNullOrEmpty(setting, mediaType);
        try {
            return MediaType.parse(mediaType);
        } catch (final IllegalArgumentException e) {
            throw new ConfigurationException(String.format("%s is not a valid content type!", mediaType));
        }
    }

}
