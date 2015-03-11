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
package org.n52.sos.ogc.sos;

import java.util.Set;

import javax.xml.namespace.QName;

import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.ImmutableSet;

/**
 * SosConstants holds all important and often used constants (e.g. name of the
 * getCapabilities operation) that are specific to SOS 2.0
 * 
 * @since 4.0.0
 */
public interface Sos2Constants extends SosConstants{

    String NS_SOS_20 = "http://www.opengis.net/sos/2.0";
    
    String XPATH_PREFIX_SOS_20 = XmlHelper.getXPathPrefix(SosConstants.NS_SOS_PREFIX, NS_SOS_20);

    String SCHEMA_LOCATION_URL_SOS = "http://schemas.opengis.net/sos/2.0/sos.xsd";

    String SCHEMA_LOCATION_URL_SOS_GET_CAPABILITIES = "http://schemas.opengis.net/sos/2.0/sosGetCapabilities.xsd";

    String SCHEMA_LOCATION_URL_SOS_GET_FEATURE_OF_INTEREST =
            "http://schemas.opengis.net/sos/2.0/sosGetFeatureOfInterest.xsd";

    String SCHEMA_LOCATION_URL_SOS_GET_OBSERVATION = "http://schemas.opengis.net/sos/2.0/sosGetObservation.xsd";

    String SCHEMA_LOCATION_URL_SOS_GET_OBSERVATION_BY_ID =
            "http://schemas.opengis.net/sos/2.0/sosGetObservationById.xsd";

    String SCHEMA_LOCATION_URL_SOS_GET_RESULT = "http://schemas.opengis.net/sos/2.0/sosGetResult.xsd";

    String SCHEMA_LOCATION_URL_SOS_GET_RESULT_TEMPLATE = "http://schemas.opengis.net/sos/2.0/sosGetResultTemplate.xsd";

    String SCHEMA_LOCATION_URL_SOS_INSERT_OBSERVATION = "http://schemas.opengis.net/sos/2.0/sosInsertObservation.xsd";

    String SCHEMA_LOCATION_URL_SOS_INSERT_RESULT = "http://schemas.opengis.net/sos/2.0/sosInsertResult.xsd";

    String SCHEMA_LOCATION_URL_SOS_INSERT_RESULT_TEMPLATE =
            "http://schemas.opengis.net/sos/2.0/sosInsertResultTemplate.xsd";

    String SCHEMA_LOCATION_URL_SOS_INSERT_SENSOR = "http://schemas.opengis.net/sos/2.0/sosInsertSensor.xsd";

    String SCHEMA_LOCATION_URL_SOS_INSERTION_CAPABILITIES =
            "http://schemas.opengis.net/sos/2.0/sosInsertionCapabilities.xsd#InsertionCapabilities";

    SchemaLocation SOS_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20, SCHEMA_LOCATION_URL_SOS);

    SchemaLocation SOS_GET_CAPABILITIES_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_GET_CAPABILITIES);

    SchemaLocation SOS_GET_FEATURE_OF_INTEREST_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_GET_FEATURE_OF_INTEREST);

    SchemaLocation SOS_GET_OBSERVATION_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_GET_OBSERVATION);

    SchemaLocation SOS_GET_OBSERVATION_BY_ID_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_GET_OBSERVATION_BY_ID);

    SchemaLocation SOS_GET_RESULT_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20, SCHEMA_LOCATION_URL_SOS_GET_RESULT);

    SchemaLocation SOS_GET_RESULT_TEMPLATE_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_GET_RESULT_TEMPLATE);

    SchemaLocation SOS_INSERT_OBSERVATION_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_INSERT_OBSERVATION);

    SchemaLocation SOS_INSERT_RESULT_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_INSERT_RESULT);

    SchemaLocation SOS_INSERT_RESULT_TEMPLATE_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_INSERT_RESULT_TEMPLATE);

    SchemaLocation SOS_INSERT_SNSOR_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_INSERT_SENSOR);

    SchemaLocation SOS_INSERTION_CAPABILITIES_SCHEMA_LOCATION = new SchemaLocation(NS_SOS_20,
            SCHEMA_LOCATION_URL_SOS_INSERTION_CAPABILITIES);

    /**
     * Constant for the content types of the response formats
     */
    Set<String> RESPONSE_FORMATS = ImmutableSet.of(OmConstants.RESPONSE_FORMAT_OM_2,
            MediaTypes.APPLICATION_ZIP.toString());

    String EN_CAPABILITIES = "Capabilities";

    String EN_GET_CAPABILITIES = "GetCapabilities";

    String EN_GET_FEATURE_OF_INTEREST = "GetFeatureOfInterest";

    String EN_GET_FEATURE_OF_INTEREST_RESPONSE = "GetFeatureOfInterestResponse";

    String EN_GET_OBSERVATION = "GetObservation";

    String EN_GET_OBSERVATION_BY_ID = "GetObservationById";

    String EN_GET_OBSERVATION_BY_ID_RESPONSE = "GetObservationByIdResponse";

    String EN_GET_OBSERVATION_RESPONSE = "GetObservationResponse";

    String EN_GET_RESULT = "GetResult";

    String EN_GET_RESULT_RESPONSE = "GetResultResponse";

    String EN_GET_RESULT_TEMPLATE = "GetResultTemplate";

    String EN_GET_RESULT_TEMPLATE_RESPONSE = "GetResultTemplateResponse";

    String EN_INSERT_OBSERVATION = "InsertObservation";

    String EN_INSERT_OBSERVATION_RESPONSE = "InsertObservationResponse";

    String EN_INSERT_RESULT = "InsertResult";

    String EN_INSERT_RESULT_RESPONSE = "InsertResultResponse";

    String EN_INSERT_RESULT_TEMPLATE = "InsertResultTemplate";

    String EN_INSERT_RESULT_TEMPLATE_RESPONSE = "InsertResultTemplateResponse";

    String EN_OBSERVATION_OFFERING = "ObservationOffering";

    String EN_OBSERVED_AREA = "observedArea";

    String EN_PHENOMENON_TIME = "phenomenonTime";

    String EN_RESULT_TIME = "resultTime";

    String EN_RESPONSE_FORMAT = "responseFormat";

    String EN_OBSERVATION_TYPE = "observationType";

    String EN_FEATURE_OF_INTEREST_TYPE = "featureOfInterestType";

    String EN_SOS_INSERTION_METADATA = "SosInsertionMetadata";

    QName QN_CAPABILITIES = new QName(NS_SOS_20, EN_CAPABILITIES, SosConstants.NS_SOS_PREFIX);

    QName QN_GET_CAPABILITIES = new QName(NS_SOS_20, EN_GET_CAPABILITIES, SosConstants.NS_SOS_PREFIX);

    QName QN_GET_FEATURE_OF_INTEREST = new QName(NS_SOS_20, EN_GET_FEATURE_OF_INTEREST, SosConstants.NS_SOS_PREFIX);

    QName QN_GET_FEATURE_OF_INTEREST_RESPONSE = new QName(NS_SOS_20, EN_GET_FEATURE_OF_INTEREST_RESPONSE,
            SosConstants.NS_SOS_PREFIX);

    QName QN_GET_OBSERVATION = new QName(NS_SOS_20, EN_GET_OBSERVATION, SosConstants.NS_SOS_PREFIX);

    QName QN_GET_OBSERVATION_BY_ID = new QName(NS_SOS_20, EN_GET_OBSERVATION_BY_ID, SosConstants.NS_SOS_PREFIX);

    QName QN_GET_OBSERVATION_BY_ID_RESPONSE = new QName(NS_SOS_20, EN_GET_OBSERVATION_BY_ID_RESPONSE,
            SosConstants.NS_SOS_PREFIX);

    QName QN_GET_OBSERVATION_RESPONSE = new QName(NS_SOS_20, EN_GET_OBSERVATION_RESPONSE, SosConstants.NS_SOS_PREFIX);

    QName QN_GET_RESULT = new QName(NS_SOS_20, EN_GET_RESULT, SosConstants.NS_SOS_PREFIX);

    QName QN_GET_RESULT_RESPONSE = new QName(NS_SOS_20, EN_GET_RESULT_RESPONSE, SosConstants.NS_SOS_PREFIX);

    QName QN_GET_RESULT_TEMPLATE = new QName(NS_SOS_20, EN_GET_RESULT_TEMPLATE, SosConstants.NS_SOS_PREFIX);

    QName QN_GET_RESULT_TEMPLATE_RESPONSE = new QName(NS_SOS_20, EN_GET_RESULT_TEMPLATE_RESPONSE,
            SosConstants.NS_SOS_PREFIX);

    QName QN_INSERT_OBSERVATION = new QName(NS_SOS_20, EN_INSERT_OBSERVATION, SosConstants.NS_SOS_PREFIX);

    QName QN_INSERT_OBSERVATION_RESPONSE = new QName(NS_SOS_20, EN_INSERT_OBSERVATION_RESPONSE,
            SosConstants.NS_SOS_PREFIX);

    QName QN_INSERT_RESULT = new QName(NS_SOS_20, EN_INSERT_RESULT, SosConstants.NS_SOS_PREFIX);

    QName QN_INSERT_RESULT_RESPONSE = new QName(NS_SOS_20, EN_INSERT_RESULT_RESPONSE, SosConstants.NS_SOS_PREFIX);

    QName QN_INSERT_RESULT_TEMPLATE = new QName(NS_SOS_20, EN_INSERT_RESULT_TEMPLATE, SosConstants.NS_SOS_PREFIX);

    QName QN_INSERT_RESULT_TEMPLATE_RESPONSE = new QName(NS_SOS_20, EN_INSERT_RESULT_TEMPLATE_RESPONSE,
            SosConstants.NS_SOS_PREFIX);

    QName QN_OBSERVATION_OFFERING = new QName(NS_SOS_20, EN_OBSERVATION_OFFERING, SosConstants.NS_SOS_PREFIX);

    QName QN_SOS_INSERTION_METADATA = new QName(NS_SOS_20, EN_SOS_INSERTION_METADATA, SosConstants.NS_SOS_PREFIX);

    QName QN_SOS_OBSERVED_AREA = new QName(NS_SOS_20, EN_OBSERVED_AREA, SosConstants.NS_SOS_PREFIX);

    QName QN_SOS_PHENOMENON_TIME = new QName(NS_SOS_20, EN_PHENOMENON_TIME, SosConstants.NS_SOS_PREFIX);

    QName QN_SOS_RESULT_TIME = new QName(NS_SOS_20, EN_RESULT_TIME, SosConstants.NS_SOS_PREFIX);

    QName QN_SOS_RESPONSE_FORMAT = new QName(NS_SOS_20, EN_RESPONSE_FORMAT, SosConstants.NS_SOS_PREFIX);

    QName QN_SOS_OBSERVATION_TYPE = new QName(NS_SOS_20, EN_OBSERVATION_TYPE, SosConstants.NS_SOS_PREFIX);

    QName QN_SOS_FEATURE_OF_INTEREST_TYPE = new QName(NS_SOS_20, EN_FEATURE_OF_INTEREST_TYPE,
            SosConstants.NS_SOS_PREFIX);

    String VALUE_REFERENCE_SPATIAL_FILTERING_PROFILE = "http://www.opengis.net/req/omxml/2.0/data/samplingGeometry";

    String HREF_PARAMETER_SPATIAL_FILTERING_PROFILE =
            "http://www.opengis.net/def/param-name/OGC-OM/2.0/samplingGeometry";

    /**
     * The names of the extensions that we know off and might support
     */
    enum Extensions {
        SplitDataArrayIntoObservations, MergeObservationsIntoDataArray, Language;
    }

    /** Constant for actual implementing version */
    String SERVICEVERSION = "2.0.0";

    /**
     * the names of the SOS 2.0 operations that are not supported by all
     * versions
     */
    enum Operations {
        InsertSensor, DeleteSensor, InsertResult, InsertResultTemplate, GetResultTemplate, UpdateSensorDescription;
    }

    /**
     * enum with names of SOS 2.0 Capabilities sections not supported by all
     * versions
     */
    enum CapabilitiesSections {
        FilterCapabilities, InsertionCapabilities;
    }

    /**
     * enum with parameter names for getObservation request
     */
    enum GetObservationParams {
        temporalFilter, spatialFilter, namespaces;
    }

    /**
     * enum with parameter names for SOS 2.0 insertObservation request not
     * supported by all versions
     */
    enum InsertObservationParams {
        offering, observation, observationType, observedProperty, procedure, featureOfInterest, parameter;
    }

    /**
     * enum with parameter names for SOS 2.0 getObservation request not
     * supported by all versions
     */
    enum DescribeSensorParams {
        procedureDescriptionFormat, validTime;
    }

    /**
     * enum with parameter names for SOS 2.0 getFeatureOfInterest request not
     * supported by all versions
     */
    enum GetFeatureOfInterestParams {
        featureOfInterest, observedProperty, procedure, spatialFilter;
    }

    enum GetObservationByIdParams {
        observation;
    }

    enum GetResultTemplateParams {
        offering, observedProperty;
    }

    enum InsertResultTemplateParams {
        offering, observedProperty, procedure, observationType, resultStructure, resultEncoding, proposedTemplate, identifier;
    }

    enum GetResultParams {
        offering, observedProperty, featureOfInterest, temporalFilter, spatialFilter;
    }

    enum InsertResultParams {
        template, resultValues
    }

    /**
     * enum with parameter names for registerSensor request
     */
    enum InsertSensorParams {
        service, version, procedureDescriptionFormat, procedureDescription, observableProperty, metadata, featureOfInterestType, observationType, procedureIdentifier, offeringIdentifier;
    }

    enum DeleteSensorParams {
        service, version, procedure;
    }

    /**
     * enum with parameter names for registerSensor request
     */
    enum UpdateSensorDescriptionParams {
        service, version, procedure, procedureDescriptionFormat, description, validTime;
    }
}
