/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class GetResultRequestHandlerTest extends HandlerBaseTest {

    private static final String FI_1 = "fi1";
    private static final String FI_2 = "fi2";
    private static final String TEMPLATE_1 = "template1";
    private static final String OBP = "obp";
    private static final String OFF = "off";

    @InjectMocks
    private GetResultRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() {
        GetResultRequest request = new GetResultRequest();
        request.setFeatureIdentifiers(Arrays.asList(FI_1, FI_2));
        request.setObservationTemplateIdentifier(TEMPLATE_1);
        request.setObservedProperty(OBP);
        request.setOffering(OFF);
        request.setSpatialFilter(spatialFilter);
        request.setTemporalFilter(Arrays.asList(temporalFilter));

        Map<String, Object> map = handler.resolveAsMap(request);

        MatcherAssert.assertThat((List<String>) map.get(SosDataMapping.GR_FEATURE_IDENTIFIERS.getName()),
                CoreMatchers.hasItems(FI_1, FI_2));
        Assert.assertEquals(TEMPLATE_1, map.get(SosDataMapping.GR_OBSERVATION_TEMPLATE_IDENTIFIER.getName()));
        Assert.assertEquals(OBP, map.get(SosDataMapping.GR_OBSERVATION_PROPERTY.getName()));
        Assert.assertEquals(OFF, map.get(SosDataMapping.GR_OFFERING.getName()));
        Assert.assertNotNull(map.get(SosDataMapping.GR_SPATIAL_FILTER.getName()));
        Assert.assertNotNull(map.get(SosDataMapping.GR_TEMPORAL_FILTER.getName()));
    }

}
