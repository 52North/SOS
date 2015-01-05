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

import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.ImmutableSet;

/**
 * SosConstants holds all important and often used constants (e.g. name of the
 * getCapabilities operation) that are specific to SOS 1.0
 * 
 * @since 4.0.0
 */
public interface Sos1Constants extends SosConstants {

    String NS_SOS = "http://www.opengis.net/sos/1.0";

    /** Constant for the schema repository of the SOS */
    String SCHEMA_LOCATION_SOS = "http://schemas.opengis.net/sos/1.0.0/sosAll.xsd";
    
    String SCHEMA_LOCATION_URL_SOS1_GET_CAPBABILITIES = "http://schemas.opengis.net/sos/1.0.0/sosAll.xsd";

    SchemaLocation SOS1_SCHEMA_LOCATION = new SchemaLocation(NS_SOS, SCHEMA_LOCATION_SOS);

    SchemaLocation GET_CAPABILITIES_SOS1_SCHEMA_LOCATION = new SchemaLocation(NS_SOS, SCHEMA_LOCATION_URL_SOS1_GET_CAPBABILITIES);

    /** Constant for the content types of the response formats */
    // TODO use MediaType
    Set<String> RESPONSE_FORMATS = ImmutableSet.of(OmConstants.CONTENT_TYPE_OM.toString(),
            MediaTypes.APPLICATION_ZIP.toString());

    /** Constant for actual implementing version */
    String SERVICEVERSION = "1.0.0";

    /**
     * the names of the SOS 1.0 operations that are not supported by all
     * versions
     */
    enum Operations {
        GetFeatureOfInterestTime, DescribeFeatureType, DescribeObservationType, DescribeResultModel, RegisterSensor;
    }

    /**
     * enum with names of SOS 1.0 Capabilities sections not supported by all
     * versions
     */
    enum CapabilitiesSections {
        Filter_Capabilities;
    }

    /**
     * enum with parameter names for SOS 1.0 getObservation request not
     * supported by all versions
     */
    enum GetObservationParams {
        eventTime, resultModel;
    }

    /**
     * enum with parameter names for SOS 1.0 insertObservation request not
     * supported by all versions
     */
    enum InsertObservationParams {
        AssignedSensorId, Observation;
    }

    /**
     * enum with parameter names for SOS 1.0 getObservation request not
     * supported by all versions
     */
    enum DescribeSensorParams {
        outputFormat, time;
    }

    /**
     * enum with parameter names for SOS 1.0 getFeatureOfInterest request not
     * supported by all versions
     */
    enum GetFeatureOfInterestParams {
        featureOfInterestID, location;
    }

    /**
     * enum with parameter names for getFeatureOfInterestTime request
     */
    enum GetFeatureOfInterestTimeParams {
        featureOfInterestID, location, observedProperty, procedure;
    }

    /**
     * enum with parameter names for registerSensor request
     */
    enum RegisterSensorParams {
        SensorDescription, ObservationTemplate;
    }

    /**
     * enum with parameter names for SOS 1.0 getObservationById request not
     * supported by all versions
     */
    enum GetObservationByIdParams {
        srsName, ObservationId, responseFormat, resultModel, responseMode, SortBy;
    }
}
