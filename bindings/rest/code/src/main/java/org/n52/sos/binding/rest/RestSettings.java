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
	public static final String REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE = "rest.errorMsg.HTTPMethodNotAllowedForResource";
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
			setOrder(0),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_CONTENT_TYPE_DEFAULT).
			setDefaultValue("application/gml+xml").
			setTitle("Content type - default").
			setDescription("Default content type returned by this binding").
			setOrder(1),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_CONTENT_TYPE_UNDEFINED).
			setDefaultValue("unknown/unknown").
			setTitle("Content type - undefined").
			setDescription("Content type used in the case of not knowing the content type of a externally linked resource.").
			setOrder(2),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_BINDING_END_POINT_RESOURCE).
			setDefaultValue("capabilities").
			setTitle("Binding Endpoint Resource").
			setDescription("The resource any client is redirected to when accessing the binding endpoint.").
			setOrder(3),
			
			new IntegerSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_EPSG_CODE_DEFAULT).
			setDefaultValue(4326).
			setTitle("EPSG Code - default").
			setDescription("The default value of the EPSG code.").
			setOrder(4),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_URL_ENCODING).
			setDefaultValue("UTF-8").
			setTitle("URL encoding - default").
			setDescription("The default value fo the URL encoding.").
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
			setOrder(7),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST).
			setDefaultValue("Received GET request invalid for resource type \"%s\". Try ").
			setTitle("Error Message - Bad GET request").
			setDescription("The start of the bad GET request error message.").
			setOrder(8),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST_BY_ID).
			setDefaultValue("\"../%s/$RESOURCE_ID\"").
			setTitle("Error Message - Bad GET request by id").
			setDescription("The bad GET request by id error message").
			setOrder(9),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST_GLOBAL_RESOURCE).
			setDefaultValue("\"..%s\"").
			setTitle("Error Message - Bad GET request global resource").
			setDescription("The bad GET request global resource error message").
			setOrder(10),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST_NO_VALID_KVP_PARAMETER).
			setDefaultValue("No valid parameter at all.").
			setTitle("Error Message - No valid KVP parameter").
			setDescription("The bad GET request error message when no valid KVP paramter is found.").
			setOrder(11),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_BAD_GET_REQUEST_SEARCH).
			setDefaultValue("\"../%s?$SEARCH_REQUEST\". Please refer to the documentation regarding allowed parameters").
			setTitle("Error Message - Bad search request").
			setDescription("The bad GET request error message when the search request is not valid").
			setOrder(12),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_HTTP_METHOD_NOT_ALLOWED_FOR_RESOURCE).
			setDefaultValue("HTTP method \"%s\" not allowed for \"%s\" resources.").
			setTitle("Error Message - HTTP method not allowed for resource").
			setDescription("The error message when the used HTTP method is not allowed for the requested resource type.").
			setOrder(13),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_WRONG_CONTENT_TYPE).
			setDefaultValue("request with wrong content type received.").
			setTitle("Error Message - wrong content type").
			setDescription("The error message when using the wrong content type in the request body.").
			setOrder(14),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ERROR_MSG_WRONG_CONTENT_TYPE_IN_ACCEPT_HEADER).
			setDefaultValue("Requested content type as specified in Accept header not supported.").
			setTitle("Error Message - not supported content type in accept header").
			setDescription("The content type requested could not be supported.").
			setOrder(15),
			
			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_FOI).
			setDefaultValue("feature").
			setTitle("HTTP GET Parametername <tt>Foi</tt>").
			setDescription("The parameter name for <i>features of interest</i> in HTTP GET requests.").
			setOrder(18),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_NAMESPACES).
			setDefaultValue("namespaces").
			setTitle("HTTP GET Parametername <tt>Namespaces</tt>").
			setDescription("The parameter name for <i>namespaces</i> in HTTP GET requests.").
			setOrder(19),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_OBSERVEDPROPERTY).
			setDefaultValue("observedproperties").
			setTitle("HTTP GET Parametername <tt>Observedproperty</tt>").
			setDescription("The parameter name for <i>observed properties</i> in HTTP GET requests.").
			setOrder(20),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_OFFERING).
			setDefaultValue("offering").
			setTitle("HTTP GET Parametername <tt>Offering</tt>").
			setDescription("The parameter name for <i>offerings</i> in HTTP GET requests.").
			setOrder(21),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_PROCEDURES).
			setDefaultValue("procedures").
			setTitle("HTTP GET Parametername <tt>Procedures</tt>").
			setDescription("The parameter name for <i>procedures</i> in HTTP GET requests.").
			setOrder(22),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_SPATIALFILTER).
			setDefaultValue("spatialfilter").
			setTitle("HTTP GET Parametername <tt>Spatialfilter</tt>").
			setDescription("The parameter name for <i>spatial filters</i> in HTTP GET requests.").
			setOrder(23),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_GET_PARAMETERNAME_TEMPORALFILTER).
			setDefaultValue("temporalfilter").
			setTitle("HTTP GET Parametername <tt>Temporalfilter</tt>").
			setDescription("The parameter name for <i>temporal filter</i> in HTTP GET requests.").
			setOrder(24),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_HEADER_IDENTIFIER_XDELETEDRESOURCEID).
			setDefaultValue("X-Deleted-Resource-Id").
			setTitle("HTTP Header Identifier <tt>XDeletedResourceId</tt>").
			setDescription("The identifier for the <i>deleted resource id</i>.").
			setOrder(29),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_HTTP_OPERATIONNOTALLOWEDFORRESOURCETYPE_MESSAGE_START).
			setDefaultValue("operation is not allowed for the resource type").
			setTitle("HTTP <tt>OperationNotAllowedForResourceType</tt> Message Start").
			setDescription("Identifier for HTTP method not allowed errors (<tt>HTTP error 405</tt>).").
			setOrder(30),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_KVP_ENCODING_VALUESPLITTER).
			setDefaultValue(",").
			setTitle("KVP Encoding Valuesplitter").
			setDescription("Specify the character used for splitting KVP encoded values, e.g. <code>,</code>.").
			setOrder(34),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_CAPABILITIES).
			setDefaultValue("capabilities").
			setTitle("Resource <tt>Capabilities</tt>").
			setDescription("The name of the root resource, service endpoint. Default is: <code>capabilities</code>.").
			setOrder(35),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_FEATURES).
			setDefaultValue("features").
			setTitle("Resource <tt>Features</tt>").
			setDescription("The name of the <tt>features</tt> resource. Default is: <code>features</code>.").
			setOrder(36),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_OBSERVABLEPROPERTIES).
			setDefaultValue("properties").
			setTitle("Resource <tt>Observableproperties</tt>").
			setDescription("The name of the <tt>observable properties</tt> resource. Default is <code>properties</code>.").
			setOrder(37),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_OBSERVATIONS).
			setDefaultValue("observations").
			setTitle("Resource <tt>Observations</tt>").
			setDescription("The name of the <tt>observations</tt> resource. Default is <code>observations</code>.").
			setOrder(38),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_OFFERINGS).
			setDefaultValue("offerings").
			setTitle("Resource <tt>Offerings</tt>").
			setDescription("The name of the <tt>offerings</tt> resource. Default is <code>offerings</code>.").
			setOrder(39),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_FEATURE_GET).
			setDefaultValue("feature-get").
			setTitle("Resource Relation <tt>Feature Get</tt>").
			setDescription("The name of the relation between any resource instance and the related <tt>feature</tt>. "
			        + "Default is <code>feature-get</code>.").
			setOrder(40),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_FEATURES_GET).
			setDefaultValue("features-get").
			setTitle("Resource Relation <tt>Features Get</tt>").
			setDescription("The name of the relation between any resource instance and the related "
			        + "<tt>feature<strong>s</strong></tt>. Default is <code>feature<strong>s</strong>-get</code>.").
			setOrder(41),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET).
			setDefaultValue("property-get").
			setTitle("Resource Relation <tt>Observableproperty Get</tt>").
			setDescription("The name of the relation between any resource instance and the related <tt>observable "
			        + "property</tt>. Default is <code>property-get</code>.").
			setOrder(42),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVATION_CREATE).
			setDefaultValue("observation-create").
			setTitle("Link <tt>Observation Create</tt>").
			setDescription("The name of the link to the resource for creating new <tt>observations</tt>. Default is "
			        + "<code>observation-create</code>.").
			setOrder(43),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVATION_DELETE).
			setDefaultValue("observation-delete").
			setTitle("Link <tt>Observation Delete</tt>").
			setDescription("The name of the link to the resource for deleting existing <tt>observations</tt>. Default "
			        + "is <code>observation-delete</code>.").
			setOrder(44),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVATION_GET).
			setDefaultValue("observation-get").
			setTitle("Resource Relation <tt>Observation Get</tt>").
			setDescription("The name of the relation between any resource instance and the related "
			        + "<tt>observations</tt>. Default is <code>observation-get</code>.").
			setOrder(45),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OBSERVATIONS_GET).
			setDefaultValue("observations-get").
			setTitle("Resource Relation <tt>Observations Get</tt>").
			setDescription("The name of the relation between any resource instance and the related "
			        + "<tt>observation<strong>s</strong></tt>. Default is "
			        + "<code>observation<strong>s</strong>-get</code>.").
			setOrder(46),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OFFERING_GET).
			setDefaultValue("offering-get").
			setTitle("Resource Relation <tt>Offering Get</tt>").
			setDescription("The name of the relation between any resource instance and the related <tt>offering</tt>. "
			        + "Default is <code>offering-get</code>.").
			setOrder(47),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_OFFERINGS_GET).
			setDefaultValue("offerings-get").
			setTitle("Resource Relation <tt>Offerings Get</tt>").
			setDescription("The name of the relation between any resource instance and the related "
			        + "<tt>offering<strong>s</strong></tt>. Default is <code>offering<strong>s</strong>-get</code>.").
			setOrder(48),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SELF).
			setDefaultValue("self").
			setTitle("Resource Relation <tt>Self</tt>").
			setDescription("The name of the relation between any resource instance and the <tt>resource itself</tt>. "
			        + "Default is <code>self</code>.").
			setOrder(49),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSOR_CREATE).
			setDefaultValue("sensor-create").
			setTitle("Resource Relation <tt>Sensor Create</tt>").
			setDescription("The name of the link to the resource for creating new <tt>sensors</tt>. Default "
                    + "is <code>sensor-create</code>.").
			setOrder(50),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSOR_DELETE).
			setDefaultValue("sensor-delete").
			setTitle("Resource Relation <tt>Sensor Delete</tt>").
			setDescription("The name of the link to the resource for deleting existing <tt>sensors</tt>. Default "
                    + "is <code>sensor-delete</code>.").
			setOrder(51),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSOR_GET).
			setDefaultValue("sensor-get").
			setTitle("Resource Relation <tt>Sensor Get</tt>").
			setDescription("The name of the relation between any resource instance and the related <tt>sensor</tt>. "
                    + "Default is <code>sensor-get</code>.").
			setOrder(52),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSORS_GET).
			setDefaultValue("sensors-get").
			setTitle("Resource Relation <tt>Sensors Get</tt>").
			setDescription("The name of the relation between any resource instance and the related <tt>sensor<strong>s</strong></tt>. "
                    + "Default is <code>sensor<strong>s</strong>-get</code>.").
			setOrder(53),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_RELATION_SENSOR_UPDATE).
			setDefaultValue("sensor-update").
			setTitle("Resource Relation <tt>Sensor Update</tt>").
			setDescription("The name of the link to the resource for updating existing <tt>sensors</tt>. Default "
                    + "is <code>sensor-update</code>.").
			setOrder(54),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_RESOURCE_SENSORS).
			setDefaultValue("sensors").
			setTitle("Resource <tt>Sensors</tt>").
			setDescription("The name of the <tt>sensors</tt> resource. Default is <code>sensors</code>.").
			setOrder(55),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_END).
			setDefaultValue(" is not supported by this service!").
			setTitle("Service Errormessage <tt>OperationNotSupported</tt> End").
			setDescription("TODO: Add description.").
			setOrder(61),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_ERRORMESSAGE_OPERATIONNOTSUPPORTED_START).
			setDefaultValue("The requested operation ").
			setTitle("Service Errormessage <tt>OperationNotSupported</tt> Start").
			setDescription("TODO: Add description.").
			setOrder(62),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_SERVICE).
			setDefaultValue("SOS").
			setTitle("<tt>Service Identifier</tt>").
			setDescription("TODO: Add description.").
			setOrder(63),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_TERMS_PROCEDUREIDENTIFIER).
			setDefaultValue("procedure").
			setTitle("Sos Terms <tt>Procedure Identifier</tt>").
			setDescription("TODO: Add description.").
			setOrder(64),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_SOS_VERSION).
			setDefaultValue("2.0.0").
			setTitle("<tt>Service Version</tt>").
			setDescription("TODO: Add description.").
			setOrder(65),

			new StringSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_URLPATTERN).
			setDefaultValue("/rest").
			setTitle("<tt>Urlpattern</tt>").
			setDescription("The URL identifier of the RESTful binding.").
			setOrder(66),
			
			new UriSettingDefinition().
			setGroup(SETTINGS_GROUP).
			setKey(REST_ENCODING_SCHEMA_URL).
			setDefaultValue(URI.create("https://raw.githubusercontent.com/52North/SOS/master/bindings/rest/xml/src/main/xsd/sosREST.xsd")).
			setTitle("Encoding Schema URL").
			setDescription("The URL to the encoding schema. Should be a web accessible URL returning a XSD file").
			setOrder(67)
			
			);

	
	@Override
	public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
		return Collections.unmodifiableSet(DEFINITIONS);
	}

}
