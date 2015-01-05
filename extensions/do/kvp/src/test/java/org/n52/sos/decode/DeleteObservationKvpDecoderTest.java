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
package org.n52.sos.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.PARAMETER_NAME;
import static org.n52.sos.ogc.sos.SosConstants.SOS;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.ext.deleteobservation.DeleteObservationConstants;
import org.n52.sos.ext.deleteobservation.DeleteObservationRequest;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.util.http.MediaTypes;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 1.0.0
 */
public class DeleteObservationKvpDecoderTest {

    private static DeleteObservationKvpDecoder instance;

    private static Set<OperationDecoderKey> correctDecoderKey;

    @BeforeClass
    public static void initGlobalFixtures() {
        correctDecoderKey =
                Collections.singleton(new OperationDecoderKey(SOS, Sos2Constants.SERVICEVERSION,
                        DeleteObservationConstants.Operations.DeleteObservation, MediaTypes.APPLICATION_KVP));
    }

    final private String OPERATION_NAME = DeleteObservationConstants.Operations.DeleteObservation.name();

    @Before
    public void initInstance() {
        instance = new DeleteObservationKvpDecoder();
    }

    @Test
    public void should_return_correct_set_of_decoder_keys() {
        assertTrue(instance.getDecoderKeyTypes().equals(correctDecoderKey));
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters() throws OwsExceptionReport {
        instance.decode(new HashMap<String, String>(0));
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_null_parameter() throws OwsExceptionReport {
        instance.decode(null);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters2() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(1);
        evolvingMap.put("service", SOS);
        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters3() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(2);
        evolvingMap.put("service", SOS);
        evolvingMap.put("version", Sos2Constants.SERVICEVERSION);

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters4() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(3);
        evolvingMap.put("service", SOS);
        evolvingMap.put("version", Sos2Constants.SERVICEVERSION);
        evolvingMap.put("request", OPERATION_NAME);

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters5() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(2);
        evolvingMap.put("service", SOS);
        evolvingMap.put("request", OPERATION_NAME);

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters6() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(3);
        evolvingMap.put("service", SOS);
        evolvingMap.put("request", OPERATION_NAME);
        evolvingMap.put(PARAMETER_NAME, "something");

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters7() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(3);
        evolvingMap.put("version", Sos2Constants.SERVICEVERSION);
        evolvingMap.put("request", OPERATION_NAME);
        evolvingMap.put(PARAMETER_NAME, "something");

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters8() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(3);
        evolvingMap.put("version", Sos2Constants.SERVICEVERSION);
        evolvingMap.put("request", OPERATION_NAME);
        evolvingMap.put("request", OPERATION_NAME + "2");
        evolvingMap.put(PARAMETER_NAME, "something");

        instance.decode(evolvingMap);
    }

    @Test
    public void should_decode_valid_request() throws OwsExceptionReport {
        final String observationIdentifier = "test-observation-identifier";
        HashMap<String, String> parameters = new HashMap<String, String>(4);
        parameters.put("service", SOS);
        parameters.put("version", Sos2Constants.SERVICEVERSION);
        parameters.put("request", OPERATION_NAME);
        parameters.put(PARAMETER_NAME, observationIdentifier);

        DeleteObservationRequest decodedRequest = instance.decode(parameters);

        assertThat(decodedRequest, is(not(nullValue())));
        assertThat(decodedRequest.getVersion(), is(Sos2Constants.SERVICEVERSION));
        assertThat(decodedRequest.getService(), is(SOS));
        assertThat(decodedRequest.getOperationName(), is(OPERATION_NAME));
        assertThat(decodedRequest.getObservationIdentifier(), is(observationIdentifier));
    }

}
