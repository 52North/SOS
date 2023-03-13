/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class GetObservationRequestHandlerTest extends HandlerBaseTest {

    private static final String OF_1 = "of1";
    private static final String OF_2 = "of2";
    private static final String OB_1 = "ob1";
    private static final String OB_2 = "ob2";
    private static final String ID_1 = "id1";
    private static final String ID_2 = "id2";
    private static final String P_1 = "p1";
    private static final String P_2 = "p2";
    private static final String RESPONSE_FORMAT = "batman arkham night";

    @InjectMocks
    private GetObservationRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void hasAllFilterRequest() throws OwsExceptionReport {
        GetObservationRequest request = new GetObservationRequest();
        request.setRequestContext(requestContext);

        request.setOfferings(Arrays.asList(OF_1, OF_2));
        request.setProcedures(Arrays.asList(P_1, P_2));
        request.setObservedProperties(Arrays.asList(OB_1, OB_2));
        request.setFeatureIdentifiers(Arrays.asList(ID_1, ID_2));
        request.setMergeObservationValues(true);
        request.setResponseFormat(RESPONSE_FORMAT);

        request.setSpatialFilter(spatialFilter);
        request.setTemporalFilters(Arrays.asList(temporalFilter));

        Map<String, Object> map = handler.resolveAsMap(request);

        MatcherAssert.assertThat((List<String>) map.get(SosDataMapping.GO_OFFERINGS.getName()),
                CoreMatchers.hasItems(OF_1, OF_2));
        MatcherAssert.assertThat((List<String>) map.get(SosDataMapping.GO_PROCEDURES.getName()),
                CoreMatchers.hasItems(P_1, P_2));
        MatcherAssert.assertThat((List<String>) map.get(SosDataMapping.GO_OBSERVED_PROPERTIES.getName()),
                CoreMatchers.hasItems(OB_1, OB_2));
        MatcherAssert.assertThat((List<String>) map.get(SosDataMapping.GO_FEATURE_OF_INTERESTS.getName()),
                CoreMatchers.hasItems(ID_1, ID_2));
        MatcherAssert.assertThat(map.get(SosDataMapping.GO_RESPONSE_FORMAT.getName()),
                CoreMatchers.is(RESPONSE_FORMAT));
        MatcherAssert.assertThat(map.get(SosDataMapping.GO_IS_MERGED_OBSERVATION_VALUES.getName()), CoreMatchers.is(true));
        MatcherAssert.assertThat(map.get(SosDataMapping.GO_SPATIAL_FILTER.getName()), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(map.get(SosDataMapping.GO_TEMPORAL_FILTER.getName()),
                CoreMatchers.allOf(CoreMatchers.instanceOf(List.class)));
    }
}
