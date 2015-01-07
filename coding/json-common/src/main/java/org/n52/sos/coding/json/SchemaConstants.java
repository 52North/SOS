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
package org.n52.sos.coding.json;

import com.github.fge.jsonschema.SchemaVersion;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public interface SchemaConstants {
    String SCHEMA_URI = SchemaVersion.DRAFTV4.getLocation().toASCIIString();

    interface Request {
        String INSERT_OBSERVATION = "http://www.52north.org/schema/json/sos/request/InsertObservation#";

        String GET_OBSERVATION = "http://www.52north.org/schema/json/sos/request/GetObservation#";

        String GET_OBSERVATION_BY_ID = "http://www.52north.org/schema/json/sos/request/GetObservationById#";

        String BULK_REQUEST = "http://www.52north.org/schema/json/sos/request/Batch#";

        String INSERT_SENSOR = "http://www.52north.org/schema/json/sos/request/InsertSensor#";

        String INSERT_RESULT_TEMPLATE = "http://www.52north.org/schema/json/sos/request/InsertResultTemplate#";

        String GET_DATA_AVAILABILITY = "http://www.52north.org/schema/json/sos/request/GetDataAvailability#";

        String DELETE_OBSERVATION = "http://www.52north.org/schema/json/sos/request/DeleteObservation#";

        String UPDATE_SENSOR_DESCRIPTION = "http://www.52north.org/schema/json/sos/request/UpdateSensorDescription#";

        String GET_CAPABILITIES = "http://www.52north.org/schema/json/sos/request/GetCapabilities#";

        String DELETE_SENSOR = "http://www.52north.org/schema/json/sos/request/DeleteSensor#";

        String DESCRIBE_SENSOR = "http://www.52north.org/schema/json/sos/request/DescribeSensor#";

        String GET_FEATURE_OF_INTEREST = "http://www.52north.org/schema/json/sos/request/GetFeatureOfInterest#";

        String INSERT_RESULT = "http://www.52north.org/schema/json/sos/request/InsertResult#";

        String GET_RESULT = "http://www.52north.org/schema/json/sos/request/GetResult#";

        String GET_RESULT_TEMPLATE = "http://www.52north.org/schema/json/sos/request/GetResultTemplate#";
    }

    interface Response {
    }

    interface Observation {
        String OBSERVATION = "http://www.52north.org/schema/json/Observation#";

        String CATEGORY_OBSERVATION = "http://www.52north.org/schema/json/CategoryObservation#";

        String COUNT_OBSERVATION = "http://www.52north.org/schema/json/CountObservation#";

        String COMPLEX_OBSERVATION = "http://www.52north.org/schema/json/ComplexObservation#";

        String TRUTH_OBSERVATION = "http://www.52north.org/schema/json/TruthObservation#";

        String TEXT_OBSERVATION = "http://www.52north.org/schema/json/TextObservation#";

        String GEOMETRY_OBSERVATION = "http://www.52north.org/schema/json/GeometryObservation#";

        String MEASUREMENT = "http://www.52north.org/schema/json/Measurement#";

        String SWE_ARRAY_OBSERVATION = "http://www.52north.org/schema/json/SWEArrayObservation#";

        String TEMPLATE_OBSERVATION = "http://www.52north.org/schema/json/TemplateObservation#";
    }

    interface Common {
        String GEOMETRY = "http://www.52north.org/schema/json/Geometry#";

        String FEATURE_OF_INTEREST = "http://www.52north.org/schema/json/FeatureOfInterest#";

        String EXCEPTION_REPORT = "http://www.52north.org/schema/json/ExceptionReport#";

        String FIELD = "http://www.52north.org/schema/json/Field#";

        String SPATIAL_FILTER = "http://www.52north.org/schema/json/SpatialFilter#";

        String TEMPORAL_FILTER = "http://www.52north.org/schema/json/TemporalFilter#";

        String FIELD_WITH_VALUE = "http://www.52north.org/schema/json/FieldWithValue#";

        String TIME_PRIMITIVE = "http://www.52north.org/schema/json/TimePrimitive#";

        String TIME_PERIOD = "http://www.52north.org/schema/json/TimePeriod#";

        String TIME_INSTANT = "http://www.52north.org/schema/json/TimeInstant#";

        String ENVELOPE = "http://www.52north.org/schema/json/Envelope#";

    }
}
