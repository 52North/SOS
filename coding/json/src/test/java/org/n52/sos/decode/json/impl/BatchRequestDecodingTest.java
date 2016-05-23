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

import static com.github.fge.jackson.JsonLoader.fromResource;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.BatchRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertResultTemplateRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.util.BatchConstants;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class BatchRequestDecodingTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    private static JsonNode json;

    private BatchRequestDecoder decoder;

    private BatchRequest request;

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @BeforeClass
    public static void beforeClass() {
        try {
            json = fromResource("/examples/sos/BatchRequest.json");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void before() throws OwsExceptionReport {
        this.decoder = new BatchRequestDecoder();
        this.request = decoder.decodeJSON(json, true);
    }

    @Test
    public void testService() {
        assertThat(request.getService(), is(SosConstants.SOS));
    }

    @Test
    public void testVersion() {
        assertThat(request.getVersion(), is(Sos2Constants.SERVICEVERSION));
    }

    @Test
    public void testOperationName() {
        assertThat(request.getOperationName(), is(BatchConstants.OPERATION_NAME));
    }

    @Test
    public void testParsedRequests() {
        assertThat(request.getRequests(), is(notNullValue()));
        assertThat(request.getRequests(), hasSize(19));
        errors.checkThat(request.getRequests().get(0), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(1), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(2), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(3), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(4), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(5), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(6), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(7), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(8), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(9), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(10), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(11), is(instanceOf(InsertResultTemplateRequest.class)));
        errors.checkThat(request.getRequests().get(12), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(13), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(14), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(15), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(16), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(17), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(18), is(instanceOf(InsertObservationRequest.class)));
    }
}
