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
package org.n52.sos.coding.json.impl;

import static com.github.fge.jackson.JsonLoader.fromResource;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.n52.sos.coding.json.matchers.ValidationMatchers.validObservation;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.n52.sos.coding.json.JSONConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ObservationValidationTest {
    @Test
    public void testMeasurementGeometryInline() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-inline.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testMeasuremenetGeometryRef() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testMeasurementMissingUOM() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json.path(JSONConstants.RESULT)).remove(JSONConstants.UOM);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingValue() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json.path(JSONConstants.RESULT)).remove(JSONConstants.VALUE);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingProcedure() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.PROCEDURE);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingObservedProperty() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.OBSERVED_PROPERTY);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingPhenomenonTime() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.PHENOMENON_TIME);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingResultTime() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.RESULT_TIME);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementTimePeriodResultTime() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ArrayNode resultTime = ((ObjectNode) json).putArray(JSONConstants.RESULT_TIME);
        resultTime.add("2013-01-01T00:00:00+02:00").add("2013-01-01T01:00:00+02:00");
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingValidTime() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.VALID_TIME);
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testMeasurementMissingFeatureOfInterest() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.FEATURE_OF_INTEREST);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingResult() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.RESULT);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasuremenetPhenomenonTimePeriod() throws IOException {
        JsonNode json = fromResource("/examples/measurement-phenomenon-time-period.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testTruthObservation() throws IOException {
        JsonNode json = fromResource("/examples/truth-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testCategoryObservation() throws IOException {
        JsonNode json = fromResource("/examples/category-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testCountObservation() throws IOException {
        JsonNode json = fromResource("/examples/count-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testCountObservationWithFloatingPointNumber() throws IOException {
        JsonNode json = fromResource("/examples/count-observation.json");
        ((ObjectNode) json).put(JSONConstants.RESULT, Math.PI);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testGeometryObservation() throws IOException {
        JsonNode json = fromResource("/examples/geometry-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testTextObservation() throws IOException {
        JsonNode json = fromResource("/examples/text-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testComplexObservation() throws IOException {
        JsonNode json = fromResource("/examples/complex-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testSWEArrayObservation() throws IOException {
        JsonNode json = fromResource("/examples/swearray-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }
}
