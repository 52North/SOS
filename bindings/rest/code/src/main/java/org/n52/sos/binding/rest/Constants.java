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
package org.n52.sos.binding.rest;

import static org.n52.sos.binding.rest.RestSettings.*;

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
public final class Constants {

    private static Constants instance = null;

    public static synchronized Constants getInstance()
    {
        if (instance == null) {
            instance = new Constants();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    // configurable settings - for details see RestSettings class
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
	private String urlpattern;
	private URI encodingSchemaUrl;
    
	private Constants() {
    }

    public String getBindingEndPointResource()
    {
//        return properties.getProperty("default.landingpoint.resource","capabilities");
    	return landingPointResource;
    }

    public String getConformanceClass()
    {
        return conformanceClass; //properties.getProperty("conformanceclass", "http://www.opengis.net/spec/SOS/2.0/conf/rest");
    }
    
    public MediaType getContentTypeDefault()
    {
        return contentTypeDefault; // properties.getProperty("default.content.type", "application/gml+xml");
    }
    
    public MediaType getContentTypeUndefined()
    {
        return contentTypeUndefined;//properties.getProperty("default.content.type.undefined", "unknown/unknown");
    }

    public String getDefaultDescribeSensorOutputFormat()
    {
        return SensorMLConstants.NS_SML;
    }
    
    public String getDefaultUrlEncoding()
    {
//        return properties.getProperty("default.url.encoding","UTF-8");
		return urlEncoding;
    }

    public String getEncodingNamespace()
    {
		return restEncodingNamespace;
//        return properties.getProperty("encoding.namespace", "http://www.opengis.net/sosREST/1.0");
    }
    
    public String getEncodingPrefix()
    {
//        return properties.getProperty("encoding.prefix", "sosREST");
		return restEncodingPrefix;
    }
    
    public String getErrorMessageBadGetRequest()
    {
//        return properties.getProperty("error.message.badGetRequest", "Received GET request invalid for resource type \"%s\". Try ");
		return errorMessageBadGetRequest;
    }

	public String getErrorMessageBadGetRequestById()
    {
//        return properties.getProperty("error.message.badGetRequestById", "\"../%s/$RESOURCE_ID\"");
		return errorMessageBadGetRequestById;
    }
    
	public String getErrorMessageBadGetRequestGlobalResource()
    {
//        return properties.getProperty("error.message.badGetRequestGlobalResource", "\"..%s\"");
		return errorMessageBadGetRequestGlobalResource;
    }

	public String getErrorMessageBadGetRequestNoValidKvpParameter()
    {
//        return properties.getProperty("error.message.badGetRequestNoValidKvpParameter", "No valid parameter at all.");
		return errorMessageBadGetRequestNoValidKvpParameter;
    }

	public String getErrorMessageBadGetRequestSearch()
    {
//        return properties.getProperty("error.message.badGetRequestSearch", "\"../%s?$SEARCH_REQUEST\". Please refer to the documentation regarding allowed parameters");
		return errorMessageBadGetRequestSearch;
    }

	public String getErrorMessageHttpMethodNotAllowedForResource()
	{
//		return properties.getProperty("error.message.HttpMethodNotAllowedForResource","HTTP method \"%s\" not allowed for \"%s\" resources.");
		return errorMessageHttpMethodNotAllowedForResource;
	}
   
	public String getErrorMessageWrongContentType()
    {
//        return properties.getProperty("error.message.wrongContentType", "request with wrong content type received.");
		return errorMessageWrongContentType;
    }

	public String getErrorMessageWrongContentTypeInAcceptHeader()
    {
//        return properties.getProperty("error.message.wrongContentTypeInAcceptHeader", "Requested content type as specified in Accept header not supported.");
		return errorMessageWrongContentTypeInAcceptHeader;
    }
    
	public String getHttpGetParameterNameFoi()
    {
//        return properties.getProperty("http.get.parametername.foi", "feature");
    	return httpGetParameternameFoi;
    }

	public String getHttpGetParameterNameNamespaces()
    {
//        return properties.getProperty("http.get.parametername.namespaces","namespaces");
    	return httpGetParameternameNamespaces;
    }

	public String getHttpGetParameterNameObservedProperty()
    {
//        return properties.getProperty("http.get.parametername.observedproperty","observedproperties");
    	return httpGetParameternameObservedproperty;
    }

	public String getHttpGetParameterNameOffering()
    {
//        return properties.getProperty("http.get.parametername.offering","offering");
    	return httpGetParameternameOffering;
    }

	public String getHttpGetParameterNameProcedure()
    {
//        return properties.getProperty("http.get.parametername.procedures","procedures");
    	return httpGetParameternameProcedures;
    }

	public String getHttpGetParameterNameSpatialFilter()
    {
//        return properties.getProperty("http.get.parametername.spatialfilter","spatialfilter");
    	return httpGetParameternameSpatialfilter;
    }

	public String getHttpGetParameterNameTemporalFilter()
    {
//        return properties.getProperty("http.get.parametername.temporalfilter","temporalfilter");
    	return httpGetParameternameTemporalfilter;
    }

	public String getHttpHeaderIdentifierXDeletedResourceId()
    {
//        return properties.getProperty("http.header.identifier.XDeletedResourceId","X-Deleted-Resource-Id");
    	return httpHeaderIdentifierXDeletedResourceId;
    }

	public String getHttpOperationNotAllowedForResourceTypeMessagePart()
    {
//        return properties.getProperty("http.operationNotAllowedForResourceType.message.start", "operation is not allowed for the resource type");
    	return httpOperationNotAllowedForResourceTypeMessageStart;
    }

    public String getKvPEncodingValueSplitter()
    {
//        return properties.getProperty("kvp.encoding.valuesplitter", ",");
    	return kvpEncodingValuesplitter;
    }

    public String getResourceCapabilities()
    {
//        return properties.getProperty("resource.capabilities", "capabilities");
    	return resourceCapabilities;
    }

    public String getResourceFeatures()
    {
//        return properties.getProperty("resource.features", "features");
        return resourceFeatures;
    }

    public String getResourceObservableProperties()
    {
//        return properties.getProperty("resource.observableproperties", "properties");
    	return resourceObservableproperties;
    }
    
    public String getResourceObservations()
    {
//        return properties.getProperty("resource.observations", "observations");
    	return resourceObservations;
    }

    public String getResourceOfferings()
    {
//        return properties.getProperty("resource.offerings", "offerings");
    	return resourceOfferings;
    }

    public String getResourceRelationFeatureGet()
    {
//        return properties.getProperty("resource.relation.feature.get", "feature-get");
    	return resourceRelationFeatureGet;
    }

    public String getResourceRelationFeaturesGet()
    {
//        return properties.getProperty("resource.relation.features.get", "features-get");
    	return resourceRelationFeaturesGet;
    }

    public String getResourceRelationObservablePropertyGet()
    {
//        return properties.getProperty("resource.relation.observableproperty.get", "property-get");
    	return resourceRelationObservablepropertyGet;
    }

    public String getResourceRelationObservationCreate()
    {
//        return properties.getProperty("resource.relation.observation.create", "observation-create");
    	return resourceRelationObservationCreate;
    }

    public String getResourceRelationObservationDelete()
    {
//        return properties.getProperty("resource.relation.observation.delete", "observation-delete");
    	return resourceRelationObservationDelete;
    }

    public String getResourceRelationObservationGet()
    {
//        return properties.getProperty("resource.relation.observation.get", "observation-get");
    	return resourceRelationObservationGet;
    }
    
    public String getResourceRelationObservationsGet()
    {
//        return properties.getProperty("resource.relation.observations.get", "observations-get");
    	return resourceRelationObservationsGet;
    }

    public String getResourceRelationOfferingGet()
    {
//        return properties.getProperty("resource.relation.offering.get", "offering-get");
    	return resourceRelationOfferingGet;
    }

    public String getResourceRelationOfferingsGet()
    {
//        return properties.getProperty("resource.relation.offerings.get", "offerings-get");
    	return resourceRelationOfferingsGet;
    }

    public String getResourceRelationSelf()
    {
//        return properties.getProperty("resource.relation.self", "self");
    	return resourceRelationSelf;
    }

    public String getResourceRelationSensorCreate()
    {
//        return properties.getProperty("resource.relation.sensor.create", "sensor-create");
    	return resourceRelationSensorCreate;
    }

    public String getResourceRelationSensorDelete()
    {
//        return properties.getProperty("resource.relation.sensor.delete", "sensor-delete");
    	return resourceRelationSensorDelete;
    }
    
    public String getResourceRelationSensorGet()
    {
//        return properties.getProperty("resource.relation.sensor.get", "sensor-get");
    	return resourceRelationSensorGet;
    }
    
    public String getResourceRelationSensorsGet()
    {
//        return properties.getProperty("resource.relation.sensors.get", "sensors-get");
    	return resourceRelationSensorsGet;
    }
    
    public String getResourceRelationSensorUpdate()
    {
//        return properties.getProperty("resource.relation.sensor.update", "sensor-update");
    	return resourceRelationSensorUpdate;
    }

    public String getResourceSensors()
    {
//        return properties.getProperty("resource.sensors", "sensors");
    	return resourceSensors;
    }

    public String getResourceType()
    {
//        return properties.getProperty("resource.type", "resource type");
    	return resourceType;
    }

    public String getServiceUrl()
    {
        return serviceUrl;//properties.getProperty("service.url", "http://localhost:8080/SDC");
    }

    public String getSmlCapabilityFeatureOfInterestTypeName()
    {
//        return properties.getProperty("sml.capability.featureofinteresttype.name", "sos:FeatureOfInterestType");
    	return smlCapabilityFeatureofinteresttypeName;
    }

    public String getSmlCapabilityInsertMetadataName()
    {
//        return properties.getProperty("sml.capability.insertionmetadata.name", "InsertionMetadata");
    	return smlCapabilityInsertionmetadataName;
    }
    
    public String getSmlCapabilityObservationTypeName()
    {
//        return properties.getProperty("sml.capability.observationtype.name", "sos:ObservationType");
    	return smlCapabilityObservationtypeName;
    }

    public String getSosCapabilitiesSectionNameContents()
    {
//        return properties.getProperty("sos.capabilities.section.name.contents", "Contents");
    	return sosCapabilitiesSectionNameContents;
    }

    public String getSosErrorMessageOperationNotSupportedEnd()
    {
//        return properties.getProperty("sos.errormessage.operationNotSupported.end"," is not supported by this service!");
    	return sosErrormessageOperationNotSupportedEnd;
    }

    public String getSosErrorMessageOperationNotSupportedStart()
    {
//        return properties.getProperty("sos.errormessage.operationNotSupported.start","The requested operation ");
    	return sosErrormessageOperationNotSupportedStart;
    }

    public String getSosService()
    {
//        return properties.getProperty("sos.service", "SOS");
    	return sosService;
    }

    public String getSosTermsProcedureIdentifier()
    {
//        return properties.getProperty("sos.terms.procedureidentifier", "procedure");
    	return sosTermsProcedureidentifier;
    }

    public String getSosVersion()
    {
//        return properties.getProperty("sos.version", "2.0.0");
    	return sosVersion;
    }

    public int getSpatialReferenceSystemEpsgIdDefault()
    {
//        int result;
//        String entryKey = "default.spatialreferencesystem.epsgid";
//        String idString = properties.getProperty(entryKey, "4326");
//        try {
//            result = Integer.parseInt(idString);    
//        } catch (NumberFormatException nfe) {
//            LOGGER.debug(String.format("Could not parse value \"%s\" of entry with key \"%s\". Using default EPSG:4326. Exception: %s",
//                    idString,
//                    entryKey,
//                    nfe.getLocalizedMessage()),
//                    nfe);
//            result = 4326;
//        }
//        return result;
		return epsgCodeDefault;
    }

    public String getUrlPattern()
    {
//        return properties.getProperty("urlpattern", "/sos/rest");
		return urlpattern;
    }

    @Setting(REST_BINDING_END_POINT_RESOURCE)
    public void setBindingEndPointResource(final String landingPointResource)
	{
    	Validation.notNullOrEmpty(REST_BINDING_END_POINT_RESOURCE, landingPointResource);
		this.landingPointResource = landingPointResource;
	}

    @Setting(REST_CONFORMANCE_CLASS)
	public void setConformanceClass(final String conformanceClass)
	{
    	Validation.notNullOrEmpty(REST_CONFORMANCE_CLASS, conformanceClass);
		this.conformanceClass = conformanceClass;
	}

    @Setting(REST_CONTENT_TYPE_DEFAULT)
    public void setContentTypeDefault(final String defaultContentType)
    {
        contentTypeDefault= mediaType(REST_CONTENT_TYPE_DEFAULT, defaultContentType);
    }

    private MediaType mediaType(final String setting, final String mediaType) {
        Validation.notNullOrEmpty(setting, mediaType);
        try {
            return MediaType.parse(mediaType);
        } catch(final IllegalArgumentException e) {
            throw new ConfigurationException(String.format("%s is not a valid content type!", mediaType));
        }
    }

    public void setContentTypeUndefined(final String contentTypeUndefined)
    {
    	this.contentTypeUndefined = mediaType(REST_CONTENT_TYPE_UNDEFINED, contentTypeUndefined);
    }

    @Setting(REST_EPSG_CODE_DEFAULT)
    public void setEpsgCodeDefault(final int epsgCodeDefault)
	{
		Validation.greaterZero(REST_EPSG_CODE_DEFAULT, epsgCodeDefault);
		this.epsgCodeDefault = epsgCodeDefault;
	}
    
    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST)
    public void setErrorMessageBadGetRequest(final String errorMessageBadGetRequest)
	{
		Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST, errorMessageBadGetRequest);
		this.errorMessageBadGetRequest = errorMessageBadGetRequest;
	}

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID)
    public void setErrorMessageBadGetRequestById(final String errorMessageBadGetRequestById)
	{
		Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID, errorMessageBadGetRequestById);
		this.errorMessageBadGetRequestById = errorMessageBadGetRequestById;
	}

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE)
    public void setErrorMessageBadGetRequestGlobalResource(final String errorMessageBadGetRequestGlobalResource)
	{
		Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE, errorMessageBadGetRequestGlobalResource);
		this.errorMessageBadGetRequestGlobalResource = errorMessageBadGetRequestGlobalResource;
	}

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER)
    public void setErrorMessageBadGetRequestNoValidKvpParameter(final String errorMessageBadGetRequestNoValidKvpParameter)
	{
		Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER, errorMessageBadGetRequestNoValidKvpParameter);
		this.errorMessageBadGetRequestNoValidKvpParameter = errorMessageBadGetRequestNoValidKvpParameter;
	}

    @Setting(REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH)
	public void setErrorMessageBadGetRequestSearch(final String errorMessageBadGetRequestSearch)
	{
		Validation.notNullOrEmpty(REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH, errorMessageBadGetRequestSearch);
		this.errorMessageBadGetRequestSearch = errorMessageBadGetRequestSearch;
	}

    @Setting(REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE)
    public void setErrorMessageHttpMethodNotAllowedForResource(final String errorMessageHttpMethodNotAllowedForResource)
	{
		Validation.notNullOrEmpty(REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE, errorMessageHttpMethodNotAllowedForResource);
		this.errorMessageHttpMethodNotAllowedForResource = errorMessageHttpMethodNotAllowedForResource;
	}

    @Setting(REST_ERROR_MSG_WRONG_CONTENT_TYPE)
    public void setErrorMessageWrongContentType(final String errorMessageWrongContentType)
	{
		Validation.notNullOrEmpty(REST_ERROR_MSG_WRONG_CONTENT_TYPE, errorMessageWrongContentType);
		this.errorMessageWrongContentType = errorMessageWrongContentType;
	}

    @Setting(REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER)
    public void setErrorMessageWrongContentTypeInAcceptHeader(final String errorMessageWrongContentTypeInAcceptHeader)
	{
		Validation.notNullOrEmpty(REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER, errorMessageWrongContentTypeInAcceptHeader);
		this.errorMessageWrongContentTypeInAcceptHeader = errorMessageWrongContentTypeInAcceptHeader;
	}

    @Setting(REST_HTTP_GET_PARAMETERNAME_FOI)
	public void setHttpGetParameternameFoi(final String httpGetParameternameFoi)
	{
		Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_FOI, httpGetParameternameFoi);
		this.httpGetParameternameFoi = httpGetParameternameFoi;
	}

	@Setting(REST_HTTP_GET_PARAMETERNAME_NAMESPACES)
	public void setHttpGetParameternameNamespaces(final String httpGetParameternameNamespaces)
	{
		Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_NAMESPACES, httpGetParameternameNamespaces);
		this.httpGetParameternameNamespaces = httpGetParameternameNamespaces;
	}
	
	@Setting(REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY)
	public void setHttpGetParameternameObservedproperty(final String httpGetParameternameObservedproperty)
	{
		Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY, httpGetParameternameObservedproperty);
		this.httpGetParameternameObservedproperty = httpGetParameternameObservedproperty;
	}

	@Setting(REST_HTTP_GET_PARAMETERNAME_OFFERING)
	public void setHttpGetParameternameOffering(final String httpGetParameternameOffering)
	{
		Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_OFFERING, httpGetParameternameOffering);
		this.httpGetParameternameOffering = httpGetParameternameOffering;
	}

	@Setting(REST_HTTP_GET_PARAMETERNAME_PROCEDURES)
	public void setHttpGetParameternameProcedures(final String httpGetParameternameProcedures)
	{
		Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_PROCEDURES, httpGetParameternameProcedures);
		this.httpGetParameternameProcedures = httpGetParameternameProcedures;
	}

	@Setting(REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER)
	public void setHttpGetParameternameSpatialfilter(final String httpGetParameternameSpatialfilter)
	{
		Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER, httpGetParameternameSpatialfilter);
		this.httpGetParameternameSpatialfilter = httpGetParameternameSpatialfilter;
	}

	@Setting(REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER)
	public void setHttpGetParameternameTemporalfilter(final String httpGetParameternameTemporalfilter)
	{
		Validation.notNullOrEmpty(REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER, httpGetParameternameTemporalfilter);
		this.httpGetParameternameTemporalfilter = httpGetParameternameTemporalfilter;
	}

	@Setting(REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID)
	public void setHttpHeaderIdentifierXDeletedResourceId(final String httpHeaderIdentifierXDeletedResourceId)
	{
		Validation.notNullOrEmpty(REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID, httpHeaderIdentifierXDeletedResourceId);
		this.httpHeaderIdentifierXDeletedResourceId = httpHeaderIdentifierXDeletedResourceId;
	}

	@Setting(REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START)
	public void setHttpOperationNotAllowedForResourceTypeMessageStart(final String httpOperationNotAllowedForResourceTypeMessageStart)
	{
		Validation.notNullOrEmpty(REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START, httpOperationNotAllowedForResourceTypeMessageStart);
		this.httpOperationNotAllowedForResourceTypeMessageStart = httpOperationNotAllowedForResourceTypeMessageStart;
	}

	@Setting(REST_KVP_ENCODING_VALUESPLITTER)
	public void setKvpEncodingValuesplitter(final String kvpEncodingValuesplitter)
	{
		Validation.notNullOrEmpty(REST_KVP_ENCODING_VALUESPLITTER, kvpEncodingValuesplitter);
		this.kvpEncodingValuesplitter = kvpEncodingValuesplitter;
	}

	@Setting(REST_RESOURCE_CAPABILITIES)
	public void setResourceCapabilities(final String resourceCapabilities)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_CAPABILITIES, resourceCapabilities);
		this.resourceCapabilities = resourceCapabilities;
	}

	@Setting(REST_RESOURCE_FEATURES)
	public void setResourceFeatures(final String resourceFeatures)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_FEATURES, resourceFeatures);
		this.resourceFeatures = resourceFeatures;
	}

	@Setting(REST_RESOURCE_OBSERVABLEPROPERTIES)
	public void setResourceObservableproperties(final String resourceObservableproperties)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_OBSERVABLEPROPERTIES, resourceObservableproperties);
		this.resourceObservableproperties = resourceObservableproperties;
	}

	@Setting(REST_RESOURCE_OBSERVATIONS)
	public void setResourceObservations(final String resourceObservations)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_OBSERVATIONS, resourceObservations);
		this.resourceObservations = resourceObservations;
	}

	@Setting(REST_RESOURCE_OFFERINGS)
	public void setResourceOfferings(final String resourceOfferings)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_OFFERINGS, resourceOfferings);
		this.resourceOfferings = resourceOfferings;
	}

	@Setting(REST_RESOURCE_RELATION_FEATURE_GET)
	public void setResourceRelationFeatureGet(final String resourceRelationFeatureGet)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_FEATURE_GET, resourceRelationFeatureGet);
		this.resourceRelationFeatureGet = resourceRelationFeatureGet;
	}

	@Setting(REST_RESOURCE_RELATION_FEATURES_GET)
	public void setResourceRelationFeaturesGet(final String resourceRelationFeaturesGet)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_FEATURES_GET, resourceRelationFeaturesGet);
		this.resourceRelationFeaturesGet = resourceRelationFeaturesGet;
	}

	@Setting(REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET)
	public void setResourceRelationObservablepropertyGet(final String resourceRelationObservablepropertyGet)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET, resourceRelationObservablepropertyGet);
		this.resourceRelationObservablepropertyGet = resourceRelationObservablepropertyGet;
	}

	@Setting(REST_RESOURCE_RELATION_OBSERVATION_CREATE)
	public void setResourceRelationObservationCreate(final String resourceRelationObservationCreate)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATION_CREATE, resourceRelationObservationCreate);
		this.resourceRelationObservationCreate = resourceRelationObservationCreate;
	}

	@Setting(REST_RESOURCE_RELATION_OBSERVATION_DELETE)
	public void setResourceRelationObservationDelete(final String resourceRelationObservationDelete)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATION_DELETE, resourceRelationObservationDelete);
		this.resourceRelationObservationDelete = resourceRelationObservationDelete;
	}

	@Setting(REST_RESOURCE_RELATION_OBSERVATION_GET)
	public void setResourceRelationObservationGet(final String resourceRelationObservationGet)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATION_GET, resourceRelationObservationGet);
		this.resourceRelationObservationGet = resourceRelationObservationGet;
	}

	@Setting(REST_RESOURCE_RELATION_OBSERVATIONS_GET)
	public void setResourceRelationObservationsGet(final String resourceRelationObservationsGet)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OBSERVATIONS_GET, resourceRelationObservationsGet);
		this.resourceRelationObservationsGet = resourceRelationObservationsGet;
	}

	@Setting(REST_RESOURCE_RELATION_OFFERING_GET)
	public void setResourceRelationOfferingGet(final String resourceRelationOfferingGet)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OFFERING_GET, resourceRelationOfferingGet);
		this.resourceRelationOfferingGet = resourceRelationOfferingGet;
	}

	@Setting(REST_RESOURCE_RELATION_OFFERINGS_GET)
	public void setResourceRelationOfferingsGet(final String resourceRelationOfferingsGet)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_OFFERINGS_GET, resourceRelationOfferingsGet);
		this.resourceRelationOfferingsGet = resourceRelationOfferingsGet;
	}

	@Setting(REST_RESOURCE_RELATION_SELF)
	public void setResourceRelationSelf(final String resourceRelationSelf)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SELF, resourceRelationSelf);
		this.resourceRelationSelf = resourceRelationSelf;
	}

	@Setting(REST_RESOURCE_RELATION_SENSOR_CREATE)
	public void setResourceRelationSensorCreate(final String resourceRelationSensorCreate)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_CREATE, resourceRelationSensorCreate);
		this.resourceRelationSensorCreate = resourceRelationSensorCreate;
	}

	@Setting(REST_RESOURCE_RELATION_SENSOR_DELETE)
	public void setResourceRelationSensorDelete(final String resourceRelationSensorDelete)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_DELETE, resourceRelationSensorDelete);
		this.resourceRelationSensorDelete = resourceRelationSensorDelete;
	}

	@Setting(REST_RESOURCE_RELATION_SENSOR_GET)
	public void setResourceRelationSensorGet(final String resourceRelationSensorGet)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_GET, resourceRelationSensorGet);
		this.resourceRelationSensorGet = resourceRelationSensorGet;
	}

	@Setting(REST_RESOURCE_RELATION_SENSORS_GET)
	public void setResourceRelationSensorsGet(final String resourceRelationSensorsGet)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSORS_GET, resourceRelationSensorsGet);
		this.resourceRelationSensorsGet = resourceRelationSensorsGet;
	}

	@Setting(REST_RESOURCE_RELATION_SENSOR_UPDATE)
	public void setResourceRelationSensorUpdate(final String resourceRelationSensorUpdate)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_RELATION_SENSOR_UPDATE, resourceRelationSensorUpdate);
		this.resourceRelationSensorUpdate = resourceRelationSensorUpdate;
	}

	@Setting(REST_RESOURCE_SENSORS)
	public void setResourceSensors(final String resourceSensors)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_SENSORS, resourceSensors);
		this.resourceSensors = resourceSensors;
	}

	@Setting(REST_RESOURCE_TYPE)
	public void setResourceType(final String resourceType)
	{
		Validation.notNullOrEmpty(REST_RESOURCE_TYPE, resourceType);
		this.resourceType = resourceType;
	}

	@Setting(REST_ENCODING_NAMESPACE)
    public void setRestEncodingNamespace(final URI restEncodingNamespace)
	{
		Validation.notNull(REST_ENCODING_NAMESPACE, restEncodingNamespace);
		this.restEncodingNamespace = restEncodingNamespace.toString();
	}

	@Setting(REST_ENCODING_PREFIX)
    public void setRestEncodingPrefix(final String restEncodingPrefix)
	{
		Validation.notNullOrEmpty(REST_ENCODING_PREFIX, restEncodingPrefix);
		this.restEncodingPrefix = restEncodingPrefix;
	}

	@Setting(ServiceSettings.SERVICE_URL)
    public void setServiceUrl(final URI serviceURL)
    {
        Validation.notNull("Service URL", serviceURL);
        String url = serviceURL.toString();
        if (url.contains("?")) {
            url = url.split("[?]")[0];
        }
        serviceUrl = url;
    }

	@Setting(REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME)
	public void setSmlCapabilityFeatureofinteresttypeName(final String smlCapabilityFeatureofinteresttypeName)
	{
		Validation.notNullOrEmpty(REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME, smlCapabilityFeatureofinteresttypeName);
		this.smlCapabilityFeatureofinteresttypeName = smlCapabilityFeatureofinteresttypeName;
	}

	@Setting(REST_SML_CAPABILITY_INSERTIONMETADATA_NAME)
	public void setSmlCapabilityInsertionmetadataName(final String smlCapabilityInsertionmetadataName)
	{
		Validation.notNullOrEmpty(REST_SML_CAPABILITY_INSERTIONMETADATA_NAME, smlCapabilityInsertionmetadataName);
		this.smlCapabilityInsertionmetadataName = smlCapabilityInsertionmetadataName;
	}

	@Setting(REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME)
	public void setSmlCapabilityObservationtypeName(final String smlCapabilityObservationtypeName)
	{
		Validation.notNullOrEmpty(REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME, smlCapabilityObservationtypeName);
		this.smlCapabilityObservationtypeName = smlCapabilityObservationtypeName;
	}

	@Setting(REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS)
	public void setSosCapabilitiesSectionNameContents(final String sosCapabilitiesSectionNameContents)
	{
		Validation.notNullOrEmpty(REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS, sosCapabilitiesSectionNameContents);
		this.sosCapabilitiesSectionNameContents = sosCapabilitiesSectionNameContents;
	}

	@Setting(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END)
	public void setSosErrormessageOperationNotSupportedEnd(final String sosErrormessageOperationNotSupportedEnd)
	{
		Validation.notNullOrEmpty(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END, sosErrormessageOperationNotSupportedEnd);
		this.sosErrormessageOperationNotSupportedEnd = sosErrormessageOperationNotSupportedEnd;
	}

	@Setting(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START)
	public void setSosErrormessageOperationNotSupportedStart(final String sosErrormessageOperationNotSupportedStart)
	{
		Validation.notNullOrEmpty(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START, sosErrormessageOperationNotSupportedStart);
		this.sosErrormessageOperationNotSupportedStart = sosErrormessageOperationNotSupportedStart;
	}

	@Setting(REST_SOS_SERVICE)
	public void setSosService(final String sosService)
	{
		Validation.notNullOrEmpty(REST_SOS_SERVICE, sosService);
		this.sosService = sosService;
	}

	@Setting(REST_SOS_TERMS_PROCEDUREIDENTIFIER)
	public void setSosTermsProcedureidentifier(final String sosTermsProcedureidentifier)
	{
		Validation.notNullOrEmpty(REST_SOS_TERMS_PROCEDUREIDENTIFIER, sosTermsProcedureidentifier);
		this.sosTermsProcedureidentifier = sosTermsProcedureidentifier;
	}

	@Setting(REST_SOS_VERSION)
	public void setSosVersion(final String sosVersion)
	{
		Validation.notNullOrEmpty(REST_SOS_VERSION, sosVersion);
		this.sosVersion = sosVersion;
	}

	@Setting(REST_URL_ENCODING)
    public void setUrlEncoding(final String urlEncoding)
	{
		Validation.notNull(REST_URL_ENCODING, urlEncoding);
		this.urlEncoding = urlEncoding;
	}

	@Setting(REST_URLPATTERN)
	public void setUrlpattern(final String urlpattern)
	{
		Validation.notNullOrEmpty(REST_URLPATTERN, urlpattern);
		this.urlpattern = urlpattern;
	}
	
	@Setting(RestSettings.REST_ENCODING_SCHEMA_URL)
	public void setEncodingSchemaUrl(final URI encodingSchemaUrl)
	{
		Validation.notNull(REST_ENCODING_SCHEMA_URL, encodingSchemaUrl);
		this.encodingSchemaUrl = encodingSchemaUrl; 
	}

	public URI getEncodingSchemaUrl()
	{
		// https://svn.52north.org/svn/swe/main/SOS/Extensions/Binding/RESTful/trunk/xml/src/main/xsd/sosREST.xsd
		return encodingSchemaUrl;
	}

}
