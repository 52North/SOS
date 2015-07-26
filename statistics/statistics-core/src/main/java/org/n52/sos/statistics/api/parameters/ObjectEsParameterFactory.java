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
package org.n52.sos.statistics.api.parameters;

import java.util.Objects;

import org.n52.sos.statistics.api.parameters.Description.InformationOrigin;
import org.n52.sos.statistics.api.parameters.Description.Operation;

public class ObjectEsParameterFactory {

    // ----------------- OBJECTS DETEILS -----------------//
    // ---------------- COUNTRY CODE ---------------------//
    public static final SingleEsParameter GEOLOC_COUNTRY_CODE = new SingleEsParameter("country-code", new Description(InformationOrigin.None,
            Operation.None, "[ISO-3166-1](https://en.wikipedia.org/wiki/ISO_3166-1) two letter country code"), ElasticsearchTypeRegistry.stringField);

    public static final SingleEsParameter GEOLOC_CITY_NAME = new SingleEsParameter("city-name", new Description(InformationOrigin.None,
            Operation.None, "name of the nearest city based on the IP address"), ElasticsearchTypeRegistry.stringField);

    public static final SingleEsParameter GEOLOC_GEO_POINT = new SingleEsParameter("geopoint", new Description(InformationOrigin.None,
            Operation.None, "latitude and longitude coordinates of the client"), ElasticsearchTypeRegistry.stringField);

    // ---------------- BYTES WRITTEN --------------------//
    public static final SingleEsParameter BYTES = new SingleEsParameter("bytes", new Description(InformationOrigin.None, Operation.None,
            "Size in bytes"), ElasticsearchTypeRegistry.longField);

    public static final SingleEsParameter DISPLAY_BYTES = new SingleEsParameter("display", new Description(InformationOrigin.None, Operation.None,
            "Size in human readable form"), ElasticsearchTypeRegistry.stringField);

    // ----------------- EXTENSION -----------------------//
    public static final SingleEsParameter EXTENSION_DEFINITION = new SingleEsParameter("extension-definition", new Description(
            InformationOrigin.None, Operation.None, "Definition"), ElasticsearchTypeRegistry.stringField);

    public static final SingleEsParameter EXTENSION_IDENTIFIER = new SingleEsParameter("extension-identifier", new Description(
            InformationOrigin.None, Operation.None, "Identifier"), ElasticsearchTypeRegistry.stringField);

    public static final SingleEsParameter EXTENSION_VALUE = new SingleEsParameter("extension-value", new Description(InformationOrigin.None,
            Operation.None, "Value object `toString()` version"), ElasticsearchTypeRegistry.stringAnalyzedField);

    // --------------- SPATIAL FILTER --------------------//

    public static final SingleEsParameter SPATIAL_FILTER_OPERATOR = new SingleEsParameter("operation", new Description(InformationOrigin.None,
            Operation.None, "Operator"), ElasticsearchTypeRegistry.stringField);

    public static final SingleEsParameter SPATIAL_FILTER_SHAPE = new SingleEsParameter("shape", new Description(InformationOrigin.None,
            Operation.None, "Elasticsearch shape"), ElasticsearchTypeRegistry.geoShapeField);

    public static final SingleEsParameter SPATIAL_FILTER_VALUE_REF = new SingleEsParameter("value-reference", new Description(InformationOrigin.None,
            Operation.None, "Value reference"), ElasticsearchTypeRegistry.stringField);

    // ---------------- TIME -------------------//
    public static final SingleEsParameter TIME_DURARTION = new SingleEsParameter("duration", new Description(InformationOrigin.None, Operation.None,
            "Duration between the END-START timestamp in milliseconds"), ElasticsearchTypeRegistry.longField);

    public static final SingleEsParameter TIME_START = new SingleEsParameter("start", new Description(InformationOrigin.None, Operation.None,
            "Start timestamp"), ElasticsearchTypeRegistry.dateField);

    public static final SingleEsParameter TIME_END = new SingleEsParameter("end", new Description(InformationOrigin.None, Operation.None,
            "End timestamp"), ElasticsearchTypeRegistry.dateField);

    public static final SingleEsParameter TIME_TIMEINSTANT = new SingleEsParameter("timeInstant", new Description(InformationOrigin.None,
            Operation.None, "Timestamp if the value is TimeInstant type"), ElasticsearchTypeRegistry.dateField);

    public static final SingleEsParameter TIME_SPAN_AS_DAYS = new SingleEsParameter("span-days", new Description(InformationOrigin.None,
            Operation.None, "This is a computed field based on the start timestamp and end timestamp of the TimePeriod instances."
                    + "The value is the date for each days which are  between the start/end timestamps interval. Intarval ends are included."),
            ElasticsearchTypeRegistry.dateField);

    // ---------------- TEMPORAL FILTER INC TIME -------------------//
    public static final SingleEsParameter TEMPORAL_FILTER_OPERATOR = new SingleEsParameter("operator", new Description(InformationOrigin.None,
            Operation.None, "Operator"), ElasticsearchTypeRegistry.stringField);

    public static final SingleEsParameter TEMPORAL_FILTER_VALUE_REF = new SingleEsParameter("value-reference", new Description(
            InformationOrigin.None, Operation.None, "Value reference"), ElasticsearchTypeRegistry.stringField);

    // ----------- OmObservationConstellation -----------//
    public static final SingleEsParameter OMOCONSTELL_PROCEDURE = new SingleEsParameter("procedure", new Description(InformationOrigin.None,
            Operation.None, "Procedure"), ElasticsearchTypeRegistry.stringField);

    public static final SingleEsParameter OMOCONSTELL_OBSERVABLE_PROPERTY = new SingleEsParameter("observable-property", new Description(
            InformationOrigin.None, Operation.None, "Observable property"), ElasticsearchTypeRegistry.stringField);

    public static final SingleEsParameter OMOCONSTELL_FEATURE_OF_INTEREST = new SingleEsParameter("feature-of-interest", new Description(
            InformationOrigin.None, Operation.None, "Feature of interest"), ElasticsearchTypeRegistry.stringField);

    public static final SingleEsParameter OMOCONSTELL_OBSERVATION_TYPE = new SingleEsParameter("observation-type", new Description(
            InformationOrigin.None, Operation.None, "Observation type"), ElasticsearchTypeRegistry.stringField);

    // ----------- OmObservation -----------//
    public static final ObjectEsParameter OMOBS_CONSTELLATION = omObservationConstellation("constellation", new Description(InformationOrigin.None,
            Operation.None, "Observation constellation"));

    public static final ObjectEsParameter OMOBS_SAMPLING_GEOMETRY = spatialFilter("sampling-geometry", new Description(InformationOrigin.None,
            Operation.None, "Observation geometry"));

    public static final ObjectEsParameter OMOBS_PHENOMENON_TIME = time("phenomenon-time", null);

    public static final ObjectEsParameter OMOBS_RESULT_TIME = time("result-time", null);

    public static final ObjectEsParameter OMOBS_VALID_TIME = time("valid-time", null);

    // ---------------------------------------//
    // --------- COMPOSITE PARAMETERS --------//
    // ---------------------------------------//
    public static ObjectEsParameter geoLocation(String objectName, Description description) {
        Objects.requireNonNull(objectName);

        return new ObjectEsParameter(objectName, description, GEOLOC_COUNTRY_CODE, GEOLOC_CITY_NAME, GEOLOC_GEO_POINT);
    }

    public static ObjectEsParameter bytesWritten(String objectName, Description description) {
        Objects.requireNonNull(objectName);

        return new ObjectEsParameter(objectName, description, BYTES, DISPLAY_BYTES);
    }

    public static ObjectEsParameter extension(String objectName, Description description) {
        Objects.requireNonNull(objectName);

        return new ObjectEsParameter(objectName, description, EXTENSION_DEFINITION, EXTENSION_IDENTIFIER, EXTENSION_VALUE);
    }

    public static ObjectEsParameter spatialFilter(String objectName, Description description) {
        Objects.requireNonNull(objectName);

        return new ObjectEsParameter(objectName, description, SPATIAL_FILTER_OPERATOR, SPATIAL_FILTER_SHAPE, SPATIAL_FILTER_VALUE_REF);
    }

    public static ObjectEsParameter time(String objectName, Description description) {
        Objects.requireNonNull(objectName);

        return new ObjectEsParameter(objectName, description, TIME_DURARTION, TIME_START, TIME_END, TIME_TIMEINSTANT, TIME_SPAN_AS_DAYS);
    }

    public static ObjectEsParameter temporalFilter(String objectName, Description description) {
        Objects.requireNonNull(objectName);

        return new ObjectEsParameter(objectName, description, TIME_DURARTION, TIME_START, TIME_END, TIME_TIMEINSTANT, TIME_SPAN_AS_DAYS,
                TEMPORAL_FILTER_OPERATOR, TEMPORAL_FILTER_VALUE_REF);
    }

    public static ObjectEsParameter omObservationConstellation(String objectName, Description description) {
        Objects.requireNonNull(objectName);

        return new ObjectEsParameter(objectName, description, OMOCONSTELL_PROCEDURE, OMOCONSTELL_OBSERVABLE_PROPERTY,
                OMOCONSTELL_FEATURE_OF_INTEREST, OMOCONSTELL_OBSERVATION_TYPE);
    }

    public static ObjectEsParameter omObservation(String objectName, Description description) {
        Objects.requireNonNull(objectName);

        return new ObjectEsParameter(objectName, description, OMOBS_CONSTELLATION, OMOBS_SAMPLING_GEOMETRY, OMOBS_PHENOMENON_TIME, OMOBS_RESULT_TIME,
                OMOBS_VALID_TIME);
    }

    private ObjectEsParameterFactory() {
    }

}
