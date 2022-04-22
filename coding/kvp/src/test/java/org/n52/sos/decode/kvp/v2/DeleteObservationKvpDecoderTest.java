/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.decode.kvp.v2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.svalbard.decode.OperationDecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 1.0.0
 */
public class DeleteObservationKvpDecoderTest {

    private static final String SERVICE = "service";
    private static final String VERSION = "version";
    private static final String REQUEST = "request";
    private static final String PROCEDURE = "procedure";
    private static final String FEATURE = "feature";
    private static final String OFFERING = "offering";
    private static final String OBSERVED_PROPERTY = "observedProperty";

    private static DeleteObservationKvpDecoder instance;

    private static Set<OperationDecoderKey> correctDecoderKey;

    private final String OPERATION_NAME = DeleteObservationConstants.Operations.DeleteObservation.name();

    @BeforeClass
    public static void initGlobalFixtures() {
        correctDecoderKey =
                Collections.singleton(new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                        DeleteObservationConstants.Operations.DeleteObservation, MediaTypes.APPLICATION_KVP));
    }

    @Before
    public void initInstance() {
        instance = new DeleteObservationKvpDecoder();
    }

    @Test
    public void should_return_correct_set_of_decoder_keys() {
        Assert.assertTrue(instance.getKeys().equals(correctDecoderKey));
    }

    @Test(expected = DecodingException.class)
    public void should_throw_DecodingException_in_case_of_null_parameter() throws DecodingException {
        instance.decode(null);
    }
    @Test
    public void should_decode_valid_request() throws DecodingException {
        final String observationIdentifier = "test-observation-identifier";
        HashMap<String, String> parameters = new HashMap<String, String>(4);
        parameters.put(SERVICE, SosConstants.SOS);
        parameters.put(VERSION, Sos2Constants.SERVICEVERSION);
        parameters.put(REQUEST, OPERATION_NAME);
        parameters.put(DeleteObservationConstants.PARAM_OBSERVATION, observationIdentifier);

        DeleteObservationRequest decodedRequest = instance.decode(parameters);

        MatcherAssert.assertThat(decodedRequest, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(decodedRequest.getVersion(), CoreMatchers.is(Sos2Constants.SERVICEVERSION));
        MatcherAssert.assertThat(decodedRequest.getService(), CoreMatchers.is(SosConstants.SOS));
        MatcherAssert.assertThat(decodedRequest.getOperationName(), CoreMatchers.is(OPERATION_NAME));
        MatcherAssert.assertThat(decodedRequest.getObservationIdentifiers().iterator().next(),
                CoreMatchers.is(observationIdentifier));
    }

    @Test
    public void should_decode_valid_request_2() throws DecodingException {
        HashMap<String, String> parameters = new HashMap<String, String>(4);
        parameters.put(SERVICE, SosConstants.SOS);
        parameters.put(VERSION, Sos2Constants.SERVICEVERSION);
        parameters.put(REQUEST, OPERATION_NAME);
        parameters.put(DeleteObservationConstants.PARAM_PROCEDURE, PROCEDURE);
        parameters.put(DeleteObservationConstants.PARAM_FEATURE_OF_INTEREST, FEATURE);
        parameters.put(DeleteObservationConstants.PARAM_OBSERVED_PROPERTY, OBSERVED_PROPERTY);
        parameters.put(DeleteObservationConstants.PARAM_OFFERING, OFFERING);
        parameters.put(DeleteObservationConstants.PARAM_TEMPORAL_FILTER,
                "om:phenomeonTime,2012-11-19T14:00:00+01:00/2012-11-19T14:15:00+01:00");

        DeleteObservationRequest decodedRequest = instance.decode(parameters);

        MatcherAssert.assertThat(decodedRequest, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(decodedRequest.getVersion(), CoreMatchers.is(Sos2Constants.SERVICEVERSION));
        MatcherAssert.assertThat(decodedRequest.getService(), CoreMatchers.is(SosConstants.SOS));
        MatcherAssert.assertThat(decodedRequest.getOperationName(), CoreMatchers.is(OPERATION_NAME));
        MatcherAssert.assertThat(decodedRequest.getProcedures().iterator().next(), CoreMatchers.is(PROCEDURE));
        MatcherAssert.assertThat(decodedRequest.getFeatureIdentifiers().iterator().next(), CoreMatchers.is(FEATURE));
        MatcherAssert.assertThat(decodedRequest.getObservedProperties().iterator().next(),
                CoreMatchers.is(OBSERVED_PROPERTY));
        MatcherAssert.assertThat(decodedRequest.getOfferings().iterator().next(), CoreMatchers.is(OFFERING));
    }

}
