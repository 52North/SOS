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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.n52.sos.coding.json.matchers.ValidationMatchers.validSchema;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@RunWith(Parameterized.class)
public class JSONSchemaValidationTest {
    private static final String[] SCHEMATA = { "BaseObservation", "CategoryObservation", "CodeType",
            "ComplexObservation", "CountObservation", "Envelope", "ExceptionReport", "FeatureOfInterest", "Field",
            "FieldWithValue", "GenericObservation", "Geometry", "GeometryObservation", "Measurement", "Observation",
            "ObservationWithResult", "SWEArrayObservation", "SpatialFilter", "TemplateObservation", "TemporalFilter",
            "TextObservation", "TimeInstant", "TimePeriod", "TimePrimitive", "TruthObservation", "sos/request/Batch",
            "sos/request/GetObservation", "sos/request/GetObservationById", "sos/request/GetFeatureOfInterest",
            "sos/request/InsertObservation", "sos/request/InsertResultTemplate", "sos/request/InsertSensor",
            "sos/request/GetCapabilities", "sos/request/DeleteSensor", "sos/request/DescribeSensor",
            "sos/request/UpdateSensorDescription", "sos/request/InsertResult", "sos/request/GetResult",
            "sos/request/GetResultTemplate", "sos/request/Request", "sos/response/Response", "sos/response/Batch",
            "sos/response/InsertSensor", "sos/response/GetObservation", "sos/response/GetObservationById",
            "sos/response/InsertObservation", "sos/response/GetFeatureOfInterest", "sos/response/InsertResult",
            "sos/response/GetResult", "sos/response/GetResultTemplate", "sos/response/UpdateSensorDescription",
            "sos/response/DeleteSensor", "sos/response/DescribeSensor", "sos/response/GetCapabilities" };

    private String name;

    private JsonNode schema;

    public JSONSchemaValidationTest(String name) {
        this.name = name;
    }

    @Before
    public void setUp() throws IOException {
        schema = JsonLoader.fromResource("/schema/" + name + ".json");
    }

    @Test
    public void isValidSchema() throws IOException {
        assertThat(name + " is not valid", schema, is(validSchema()));
    }

    @Parameters(name = "{0}")
    public static List<String[]> schemata() {
        String[][] params = new String[SCHEMATA.length][];
        for (int i = 0; i < params.length; ++i) {
            params[i] = new String[] { SCHEMATA[i] };
        }
        return Arrays.asList(params);
    }
}
