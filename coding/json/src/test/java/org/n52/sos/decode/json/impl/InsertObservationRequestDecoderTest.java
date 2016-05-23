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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.InsertObservationRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class InsertObservationRequestDecoderTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    private InsertObservationRequestDecoder decoder;

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Before
    public void before() {
        this.decoder = new InsertObservationRequestDecoder();
    }

    @Test
    public void singleObservation() throws IOException, OwsExceptionReport {
        final JsonNode json =
                JsonLoader.fromResource("/examples/sos/InsertObservationRequest-single-observation.json");
        final InsertObservationRequest req = decoder.decodeJSON(json, true);
        errors.checkThat(req.getService(), is(equalTo("SOS")));
        errors.checkThat(req.getVersion(), is(equalTo("2.0.0")));
        errors.checkThat(req.getOperationName(), is(equalTo("InsertObservation")));
        assertThat(req.getOfferings(), is(notNullValue()));
        errors.checkThat(req.getOfferings(), hasSize(1));
        assertThat(req.getOfferings().get(0), is(equalTo("offering2")));
        assertThat(req.getObservations(), is(notNullValue()));
        assertThat(req.getObservations(), hasSize(1));
        assertThat(req.getObservations().get(0), is(notNullValue()));
        assertThat(req.getObservations().get(0).getValue().getValue(), is(instanceOf(TextValue.class)));
    }

    @Test
    public void multipleObservation() throws IOException, OwsExceptionReport {
        final JsonNode json =
                JsonLoader.fromResource("/examples/sos/InsertObservationRequest-multiple-observations.json");
        final InsertObservationRequest req = decoder.decodeJSON(json, true);
        assertThat(req, is(notNullValue()));
        errors.checkThat(req.getService(), is(equalTo("SOS")));
        errors.checkThat(req.getVersion(), is(equalTo("2.0.0")));
        errors.checkThat(req.getOperationName(), is(equalTo("InsertObservation")));
        assertThat(req.getOfferings(), is(notNullValue()));
        errors.checkThat(req.getOfferings(), hasSize(2));
        assertThat(req.getOfferings().get(0), is(equalTo("offering1")));
        assertThat(req.getOfferings().get(1), is(equalTo("offering2")));
        assertThat(req.getObservations(), is(notNullValue()));
        assertThat(req.getObservations(), hasSize(2));
        assertThat(req.getObservations().get(0), is(notNullValue()));
        assertThat(req.getObservations().get(0).getValue().getValue(), is(instanceOf(TextValue.class)));
        assertThat(req.getObservations().get(1), is(notNullValue()));
        assertThat(req.getObservations().get(1).getValue().getValue(), is(instanceOf(TextValue.class)));
    }

    @Test
    public void singleOffering() throws IOException, OwsExceptionReport {
        final JsonNode json = JsonLoader.fromResource("/examples/sos/InsertObservationRequest-single-offering.json");
        final InsertObservationRequest req = decoder.decodeJSON(json, true);
        errors.checkThat(req.getService(), is(equalTo("SOS")));
        errors.checkThat(req.getVersion(), is(equalTo("2.0.0")));
        errors.checkThat(req.getOperationName(), is(equalTo("InsertObservation")));
        assertThat(req.getOfferings(), is(notNullValue()));
        errors.checkThat(req.getOfferings(), hasSize(1));
        assertThat(req.getOfferings().get(0), is(equalTo("offering2")));
        assertThat(req.getObservations(), is(notNullValue()));
        assertThat(req.getObservations(), hasSize(1));
        assertThat(req.getObservations().get(0), is(notNullValue()));
        assertThat(req.getObservations().get(0).getValue().getValue(), is(instanceOf(TextValue.class)));
    }

    @Test
    public void multipleOfferings() throws IOException, OwsExceptionReport {
        final JsonNode json =
                JsonLoader.fromResource("/examples/sos/InsertObservationRequest-multiple-offerings.json");
        final InsertObservationRequest req = decoder.decodeJSON(json, true);
        assertThat(req, is(notNullValue()));
        errors.checkThat(req.getService(), is(equalTo("SOS")));
        errors.checkThat(req.getVersion(), is(equalTo("2.0.0")));
        errors.checkThat(req.getOperationName(), is(equalTo("InsertObservation")));
        assertThat(req.getOfferings(), is(notNullValue()));
        errors.checkThat(req.getOfferings(), hasSize(2));
        assertThat(req.getOfferings().get(0), is(equalTo("offering1")));
        assertThat(req.getOfferings().get(1), is(equalTo("offering2")));
        assertThat(req.getObservations(), is(notNullValue()));
        assertThat(req.getObservations(), hasSize(1));
        assertThat(req.getObservations().get(0), is(notNullValue()));
        assertThat(req.getObservations().get(0).getValue().getValue(), is(instanceOf(TextValue.class)));
    }
}
