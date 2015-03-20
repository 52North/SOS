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
package org.n52.sos.decode.json.impl;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.InsertSensorRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class InsertSensorRequestDecoderTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    private InsertSensorRequestDecoder decoder;

    private InsertSensorRequest req;

    @Before
    public void setUp() throws OwsExceptionReport, IOException {
        this.decoder = new InsertSensorRequestDecoder();
        final JsonNode json = JsonLoader.fromResource("/examples/sos/InsertSensorRequest.json");
        this.req = decoder.decode(json);
        assertThat(req, is(notNullValue()));
    }

    @Test
    public void testOperationName() throws OwsExceptionReport, IOException {
        assertThat(req.getOperationName(), is("InsertSensor"));
    }

    @Test
    public void testVersion() throws OwsExceptionReport, IOException {
        assertThat(req.getVersion(), is("2.0.0"));
    }

    @Test
    public void testService() throws OwsExceptionReport, IOException {
        assertThat(req.getService(), is("SOS"));
    }

    @Test
    public void testProcedureIdentifier() throws OwsExceptionReport, IOException {
        /* set in operator */
        assertThat(req.getAssignedProcedureIdentifier(), is(nullValue()));
    }

    @Test
    public void testProcedureDescriptionFormat() throws OwsExceptionReport, IOException {
        assertThat(req.getProcedureDescriptionFormat(), is("http://www.opengis.net/sensorML/1.0.1"));
    }

    @Test
    public void testObservableProperties() throws OwsExceptionReport, IOException {
        assertThat(req.getObservableProperty(), is(Arrays.asList("http://www.52north.org/test/observableProperty/9_1",
                "http://www.52north.org/test/observableProperty/9_2",
                "http://www.52north.org/test/observableProperty/9_3",
                "http://www.52north.org/test/observableProperty/9_4",
                "http://www.52north.org/test/observableProperty/9_5")));
    }

    @Test
    public void testRelatedFeatures() throws OwsExceptionReport, IOException {

        assertThat(req.getRelatedFeatures(), is(nullValue()));
    }

    @Test
    public void testFeatureOfInterstTypes() throws OwsExceptionReport, IOException {
        assertThat(req.getMetadata(), is(notNullValue()));
        assertThat(req.getMetadata().getFeatureOfInterestTypes(),
                is(singleton("http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint")));
    }

    @Test
    public void testObservationTypes() throws OwsExceptionReport, IOException {
        assertThat(req.getMetadata(), is(notNullValue()));
        assertThat(req.getMetadata().getObservationTypes(), is((Set<String>) Sets.newHashSet(
                "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation",
                "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation",
                "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation",
                "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation")));
    }

    @Test
    public void testProcedureDescription() throws OwsExceptionReport, IOException {
        assertThat(req.getProcedureDescription(), is(notNullValue()));
        assertThat(req.getProcedureDescription().getIdentifier(),
                   is("http://www.52north.org/test/procedure/9"));
    }
}
