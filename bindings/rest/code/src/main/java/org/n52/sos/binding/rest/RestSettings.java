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

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.config.settings.UriSettingDefinition;

import com.google.common.collect.ImmutableSet;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class RestSettings implements SettingDefinitionProvider {
	
	private static final SettingDefinitionGroup SETTINGS_GROUP = new SettingDefinitionGroup().
			setTitle("RESTful Binding").
			setDescription("Configuration for the RESTful binding").
			setOrder(4.2f);
	
	public static final String REST_CONFORMANCE_CLASS = "rest.conformanceClass";
	public static final String REST_CONTENT_TYPE_DEFAULT = "rest.contentType.default";
	public static final String REST_CONTENT_TYPE_UNDEFINED = "rest.contentType.undefined";
	public static final String REST_BINDING_END_POINT_RESOURCE = "rest.bindingEndPointResource";
	public static final String REST_EPSG_CODE_DEFAULT = "rest.epsgcode.default";
	public static final String REST_URL_ENCODING = "rest.urlEncoding";
	public static final String REST_ENCODING_NAMESPACE = "rest.encodingNamspace";
	public static final String REST_ENCODING_PREFIX = "rest.encodingPrefix";
	public static final String REST_ERROR_MSG_BAD_GET_REQUEST = "rest.errorMsg.badGetRequest";
	public static final String REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID = "rest.errorMsg.badGetRequest.byId";
	public static final String REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE = "rest.errorMsg.badGetRequest.globalResource";
	public static final String REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER = "rest.errorMsg.badGetRequest.noValidKvpParameter";
	public static final String REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH = "rest.errorMsg.badGetRequest.search";
	public static final String REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE = "rest.errorMsg.HttpMethodNotAllowedForResource";
	public static final String REST_ERROR_MSG_WRONG_CONTENT_TYPE = "rest.errorMsg.wrongContentType";
	public static final String REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER = "rest.errorMSg.wrongContentType.inAcceptHeader";
	public static final String REST_HTTP_GET_PARAMETERNAME_FOI = "rest.http.get.parametername.foi";
	public static final String REST_HTTP_GET_PARAMETERNAME_NAMESPACES = "rest.http.get.parametername.namespaces";
	public static final String REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY = "rest.http.get.parametername.observedproperty";
	public static final String REST_HTTP_GET_PARAMETERNAME_OFFERING = "rest.http.get.parametername.offering";
	public static final String REST_HTTP_GET_PARAMETERNAME_PROCEDURES = "rest.http.get.parametername.procedures";
	public static final String REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER = "rest.http.get.parametername.spatialfilter";
	public static final String REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER = "rest.http.get.parametername.temporalfilter";
	public static final String REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID = "rest.http.header.identifier.XDeletedResourceId";
	public static final String REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START = "rest.http.operationNotAllowedForResourceType.message.start";
	public static final String REST_KVP_ENCODING_VALUESPLITTER = "rest.kvp.encoding.valuesplitter";
	public static final String REST_RESOURCE_CAPABILITIES = "rest.resource.capabilities";
	public static final String REST_RESOURCE_FEATURES = "rest.resource.features";
	public static final String REST_RESOURCE_OBSERVABLEPROPERTIES = "rest.resource.observableproperties";
	public static final String REST_RESOURCE_OBSERVATIONS = "rest.resource.observations";
	public static final String REST_RESOURCE_OFFERINGS = "rest.resource.offerings";
	public static final String REST_RESOURCE_RELATION_FEATURE_GET = "rest.resource.relation.feature.get";
	public static final String REST_RESOURCE_RELATION_FEATURES_GET = "rest.resource.relation.features.get";
	public static final String REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET = "rest.resource.relation.observableproperty.get";
	public static final String REST_RESOURCE_RELATION_OBSERVATION_CREATE = "rest.resource.relation.observation.create";
	public static final String REST_RESOURCE_RELATION_OBSERVATION_DELETE = "rest.resource.relation.observation.delete";
	public static final String REST_RESOURCE_RELATION_OBSERVATION_GET = "rest.resource.relation.observation.get";
	public static final String REST_RESOURCE_RELATION_OBSERVATIONS_GET = "rest.resource.relation.observations.get";
	public static final String REST_RESOURCE_RELATION_OFFERING_GET = "rest.resource.relation.offering.get";
	public static final String REST_RESOURCE_RELATION_OFFERINGS_GET = "rest.resource.relation.offerings.get";
	public static final String REST_RESOURCE_RELATION_SELF = "rest.resource.relation.self";
	public static final String REST_RESOURCE_RELATION_SENSOR_CREATE = "rest.resource.relation.sensor.create";
	public static final String REST_RESOURCE_RELATION_SENSOR_DELETE = "rest.resource.relation.sensor.delete";
	public static final String REST_RESOURCE_RELATION_SENSOR_GET = "rest.resource.relation.sensor.get";
	public static final String REST_RESOURCE_RELATION_SENSORS_GET = "rest.resource.relation.sensors.get";
	public static final String REST_RESOURCE_RELATION_SENSOR_UPDATE = "rest.resource.relation.sensor.update";
	public static final String REST_RESOURCE_SENSORS = "rest.resource.sensors";
	public static final String REST_RESOURCE_TYPE = "rest.resource.type";
	public static final String REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME = "rest.sml.capability.featureofinteresttype.name";
	public static final String REST_SML_CAPABILITY_INSERTIONMETADATA_NAME = "rest.sml.capability.insertionmetadata.name";
	public static final String REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME = "rest.sml.capability.observationtype.name";
	public static final String REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS = "rest.sos.capabilities.section.name.contents";
	public static final String REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END = "rest.sos.errormessage.operationNotSupported.end";
	public static final String REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START = "rest.sos.errormessage.operationNotSupported.start";
	public static final String REST_SOS_SERVICE = "rest.sos.service";
	public static final String REST_SOS_TERMS_PROCEDUREIDENTIFIER = "rest.sos.terms.procedureidentifier";
	public static final String REST_SOS_VERSION = "rest.sos.version";
	public static final String REST_URLPATTERN = "rest.urlpattern";
	public static final String REST_ENCODING_SCHEMA_URL = "rest.encodingSchemaUrl";
	
	private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet.<SettingDefinition<?,?>>of(
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_CONFORMANCE_CLASS).
			setTitle("Conformance Class").
			setDefaultValue("http://www.opengis.net/spec/SOS/2.0/conf/rest").
			setDescription("The conformance class for the RESTful binding.").
			setOptional(false).
			setOrder(0),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_CONTENT_TYPE_DEFAULT).
			setDefaultValue("application/gml+xml").
			setTitle("Content type - default").
			setDescription("Default content type returned by this binding").
			setOptional(false).
			setOrder(1),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_CONTENT_TYPE_UNDEFINED).
			setDefaultValue("unknown/unknown").
			setTitle("Content type - undefined").
			setDescription("Content type used in the case of not knowing the content type of a externally linked resource.").
			setOptional(false).
			setOrder(2),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_BINDING_END_POINT_RESOURCE).
			setDefaultValue("capabilities").
			setTitle("Binding Endpoint Resource").
			setDescription("The resource any client is redirected to when accessing the binding endpoint.").
			setOptional(false).
			setOrder(3),
			
			new IntegerSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_EPSG_CODE_DEFAULT).
			setDefaultValue(4326).
			setTitle("EPSG Code - default").
			setDescription("The default value of the EPSG code.").
			setOptional(false).
			setOrder(4),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_URL_ENCODING).
			setDefaultValue("UTF-8").
			setTitle("URL encoding - default").
			setDescription("The default value fo the URL encoding.").
			setOptional(false).
			setOrder(5),
			
			new UriSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ENCODING_NAMESPACE).
			setDefaultValue(URI.create("http://www.opengis.net/sosREST/1.0")).
			setTitle("RESTful encoding - namespace URI").
			setDescription("The URI of the RESTful encoding namespace.").
			setOptional(false).
			setOrder(6),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ENCODING_PREFIX).
			setDefaultValue("sosREST").
			setTitle("RESTful encoding - prefix").
			setDescription("The prefix of the RESTful encoding used within XML documents.").
			setOptional(false).
			setOrder(7),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST).
			setDefaultValue("Received GET request invalid for resource type \"%s\". Try ").
			setTitle("Error Message - Bad GET request").
			setDescription("The start of the bad GET request error message.").
			setOptional(false).
			setOrder(8),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID).
			setDefaultValue("\"../%s/$RESOURCE_ID\"").
			setTitle("Error Message - Bad GET request by id").
			setDescription("The bad GET request by id error message").
			setOptional(false).
			setOrder(9),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE).
			setDefaultValue("\"..%s\"").
			setTitle("Error Message - Bad GET request global resource").
			setDescription("The bad GET request global resource error message").
			setOptional(false).
			setOrder(10),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER).
			setDefaultValue("No valid parameter at all.").
			setTitle("Error Message - No valid KVP parameter").
			setDescription("The bad GET request error message when no valid KVP paramter is found.").
			setOptional(false).
			setOrder(11),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH).
			setDefaultValue("\"../%s?$SEARCH_REQUEST\". Please refer to the documentation regarding allowed parameters").
			setTitle("Error Message - Bad search request").
			setDescription("The bad GET request error message when the search request is not valid").
			setOptional(false).
			setOrder(12),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE).
			setDefaultValue("HTTP method \"%s\" not allowed for \"%s\" resources.").
			setTitle("Error Message - HTTP method not allowed for resource").
			setDescription("The error message when the used HTTP method is not allowed for the requested resource type.").
			setOptional(false).
			setOrder(13),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_WRONG_CONTENT_TYPE).
			setDefaultValue("request with wrong content type received.").
			setTitle("Error Message - wrong content type").
			setDescription("The error message when using the wrong content type in the request body.").
			setOptional(false).
			setOrder(14),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER).
			setDefaultValue("Requested content type as specified in Accept header not supported.").
			setTitle("Error Message - not supported content type in accept header").
			setDescription("The content type requested could not be supported.").
			setOptional(false).
			setOrder(15),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_FOI).
			setDefaultValue("feature").
			setTitle("Http Get Parametername Foi").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(18),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_NAMESPACES).
			setDefaultValue("namespaces").
			setTitle("Http Get Parametername Namespaces").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(19),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY).
			setDefaultValue("observedproperties").
			setTitle("Http Get Parametername Observedproperty").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(20),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_OFFERING).
			setDefaultValue("offering").
			setTitle("Http Get Parametername Offering").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(21),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_PROCEDURES).
			setDefaultValue("procedures").
			setTitle("Http Get Parametername Procedures").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(22),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER).
			setDefaultValue("spatialfilter").
			setTitle("Http Get Parametername Spatialfilter").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(23),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER).
			setDefaultValue("temporalfilter").
			setTitle("Http Get Parametername Temporalfilter").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(24),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID).
			setDefaultValue("X-Deleted-Resource-Id").
			setTitle("Http Header Identifier XDeletedResourceId").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(29),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START).
			setDefaultValue("operation is not allowed for the resource type").
			setTitle("Http OperationNotAllowedForResourceType Message Start").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(30),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_KVP_ENCODING_VALUESPLITTER).
			setDefaultValue(",").
			setTitle("Kvp Encoding Valuesplitter").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(34),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_CAPABILITIES).
			setDefaultValue("capabilities").
			setTitle("Resource Capabilities").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(35),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_FEATURES).
			setDefaultValue("features").
			setTitle("Resource Features").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(36),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_OBSERVABLEPROPERTIES).
			setDefaultValue("properties").
			setTitle("Resource Observableproperties").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(37),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_OBSERVATIONS).
			setDefaultValue("observations").
			setTitle("Resource Observations").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(38),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_OFFERINGS).
			setDefaultValue("offerings").
			setTitle("Resource Offerings").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(39),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_FEATURE_GET).
			setDefaultValue("feature-get").
			setTitle("Resource Relation Feature Get").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(40),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_FEATURES_GET).
			setDefaultValue("features-get").
			setTitle("Resource Relation Features Get").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(41),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET).
			setDefaultValue("property-get").
			setTitle("Resource Relation Observableproperty Get").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(42),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVATION_CREATE).
			setDefaultValue("observation-create").
			setTitle("Resource Relation Observation Create").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(43),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVATION_DELETE).
			setDefaultValue("observation-delete").
			setTitle("Resource Relation Observation Delete").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(44),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVATION_GET).
			setDefaultValue("observation-get").
			setTitle("Resource Relation Observation Get").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(45),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVATIONS_GET).
			setDefaultValue("observations-get").
			setTitle("Resource Relation Observations Get").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(46),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OFFERING_GET).
			setDefaultValue("offering-get").
			setTitle("Resource Relation Offering Get").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(47),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OFFERINGS_GET).
			setDefaultValue("offerings-get").
			setTitle("Resource Relation Offerings Get").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(48),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SELF).
			setDefaultValue("self").
			setTitle("Resource Relation Self").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(49),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSOR_CREATE).
			setDefaultValue("sensor-create").
			setTitle("Resource Relation Sensor Create").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(50),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSOR_DELETE).
			setDefaultValue("sensor-delete").
			setTitle("Resource Relation Sensor Delete").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(51),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSOR_GET).
			setDefaultValue("sensor-get").
			setTitle("Resource Relation Sensor Get").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(52),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSORS_GET).
			setDefaultValue("sensors-get").
			setTitle("Resource Relation Sensors Get").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(53),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSOR_UPDATE).
			setDefaultValue("sensor-update").
			setTitle("Resource Relation Sensor Update").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(54),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_SENSORS).
			setDefaultValue("sensors").
			setTitle("Resource Sensors").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(55),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_TYPE).
			setDefaultValue("resource type").
			setTitle("Resource Type").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(56),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SML_CAPABILITY_FEATUREOFINTERESTTYPE_NAME).
			setDefaultValue("sos:FeatureOfInterestType").
			setTitle("Sml Capability Featureofinteresttype Name").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(57),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SML_CAPABILITY_INSERTIONMETADATA_NAME).
			setDefaultValue("InsertionMetadata").
			setTitle("Sml Capability Insertionmetadata Name").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(58),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SML_CAPABILITY_OBSERVATIONTYPE_NAME).
			setDefaultValue("sos:ObservationType").
			setTitle("Sml Capability Observationtype Name").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(59),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_CAPABILITIES_SECTION_NAME_CONTENTS).
			setDefaultValue("Contents").
			setTitle("Sos Capabilities Section Name Contents").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(60),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END).
			setDefaultValue(" is not supported by this service!").
			setTitle("Sos Errormessage OperationNotSupported End").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(61),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START).
			setDefaultValue("The requested operation ").
			setTitle("Sos Errormessage OperationNotSupported Start").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(62),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_SERVICE).
			setDefaultValue("SOS").
			setTitle("Sos Service").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(63),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_TERMS_PROCEDUREIDENTIFIER).
			setDefaultValue("procedure").
			setTitle("Sos Terms Procedureidentifier").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(64),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_VERSION).
			setDefaultValue("2.0.0").
			setTitle("Sos Version").
			setDescription("TODO: Add description.").
			setOptional(false).
			setOrder(65),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_URLPATTERN).
			setDefaultValue("/rest").
			setTitle("Urlpattern").
			setDescription("The URL identifier of the RESTful binding.").
			setOptional(false).
			setOrder(66),
			
			new UriSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ENCODING_SCHEMA_URL).
			setDefaultValue(URI.create("https://raw.githubusercontent.com/52North/SOS/master/bindings/rest/xml/src/main/xsd/sosREST.xsd")).
			setTitle("Encoding Schema URL").
			setDescription("The URL to the encoding schema. Should be a web accessible URL returning a XSD file").
			setOptional(false).
			setOrder(67)
			
			);

	
	@Override
	public Set<SettingDefinition<?, ?>> getSettingDefinitions()
	{
		return Collections.unmodifiableSet(DEFINITIONS);
	}

}
