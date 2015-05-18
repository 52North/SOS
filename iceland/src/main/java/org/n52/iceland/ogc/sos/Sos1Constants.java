/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.sos;

import java.util.Set;

import org.n52.iceland.ogc.om.OmConstants;
import org.n52.iceland.util.http.MediaTypes;
import org.n52.iceland.w3c.SchemaLocation;

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
