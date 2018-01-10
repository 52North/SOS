/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class GetFeatureOfInterestRequestHandlerTest extends HandlerBaseTest {

    @InjectMocks
    GetFeatureOfInterestRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() {
        GetFeatureOfInterestRequest request = new GetFeatureOfInterestRequest();

        request.setFeatureIdentifiers(Arrays.asList("id1", "id2"));
        request.setObservedProperties(Arrays.asList("ob1", "ob2"));
        request.setProcedures(Arrays.asList("p1", "p2"));
        request.setSpatialFilters(Arrays.asList(spatialFilter));
        request.setTemporalFilters(Arrays.asList(temporalFilter));

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertThat((List<String>) map.get(SosDataMapping.GFOI_FEATURE_IDENTIFIERS.getName()), CoreMatchers.hasItems("id1", "id2"));
        Assert.assertThat((List<String>) map.get(SosDataMapping.GFOI_OBSERVED_PROPERTIES.getName()), CoreMatchers.hasItems("ob1", "ob2"));
        Assert.assertThat((List<String>) map.get(SosDataMapping.GFOI_PROCEDURES.getName()), CoreMatchers.hasItems("p1", "p2"));
        Assert.assertNotNull(map.get(SosDataMapping.GFOI_SPATIAL_FILTER.getName()));
        Assert.assertNotNull(map.get(SosDataMapping.GFOI_TEMPORAL_FILTER.getName()));
    }
}
