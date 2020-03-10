/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class GetFeatureOfInterestRequestHandlerTest extends HandlerBaseTest {

    private static final String OB_1 = "ob1";
    private static final String OB_2 = "ob2";
    private static final String ID_1 = "id1";
    private static final String ID_2 = "id2";
    private static final String P_1 = "p1";
    private static final String P_2 = "p2";

    @InjectMocks
    private GetFeatureOfInterestRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() {
        GetFeatureOfInterestRequest request = new GetFeatureOfInterestRequest();

        request.setFeatureIdentifiers(Arrays.asList(ID_1, ID_2));
        request.setObservedProperties(Arrays.asList(OB_1, OB_2));
        request.setProcedures(Arrays.asList(P_1, P_2));
        request.setSpatialFilters(Arrays.asList(spatialFilter));
        request.setTemporalFilters(Arrays.asList(temporalFilter));

        Map<String, Object> map = handler.resolveAsMap(request);

       MatcherAssert.assertThat((List<String>) map.get(SosDataMapping.GFOI_FEATURE_IDENTIFIERS.getName()),
                CoreMatchers.hasItems(ID_1, ID_2));
       MatcherAssert.assertThat((List<String>) map.get(SosDataMapping.GFOI_OBSERVED_PROPERTIES.getName()),
                CoreMatchers.hasItems(OB_1, OB_2));
       MatcherAssert.assertThat((List<String>) map.get(SosDataMapping.GFOI_PROCEDURES.getName()),
                CoreMatchers.hasItems(P_1, P_2));
        Assert.assertNotNull(map.get(SosDataMapping.GFOI_SPATIAL_FILTER.getName()));
        Assert.assertNotNull(map.get(SosDataMapping.GFOI_TEMPORAL_FILTER.getName()));
    }
}
