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
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

import org.n52.shetland.ogc.sos.SosInsertionMetadata;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class InsertSensorRequestHandlerTest extends HandlerBaseTest {

    @InjectMocks
    InsertSensorRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() {
        InsertSensorRequest request = new InsertSensorRequest();
        request.setAssignedOfferings(Arrays.asList(new SosOffering("p")));
        request.setAssignedProcedureIdentifier("proc");
        request.setObservableProperty(Arrays.asList("op1", "op2"));
        request.setProcedureDescription(new SosProcedureDescriptionUnknownType("id", "format", "xml"));
        request.setProcedureDescriptionFormat("solo-format");
        request.setMetadata(new SosInsertionMetadata());
        request.getMetadata().setFeatureOfInterestTypes(Arrays.asList("foi1", "foi2"));
        request.getMetadata().setObservationTypes(Arrays.asList("ot1", "ot2"));

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertThat(map.get(SosDataMapping.IS_ASSIGNED_OFFERINGS.getName()), CoreMatchers.instanceOf(List.class));
        Assert.assertThat(map.get(SosDataMapping.IS_ASSIGNED_PROCEDURE_IDENTIFIERS.getName()), CoreMatchers.is("proc"));
        Assert.assertThat((List<String>) map.get(SosDataMapping.IS_OBSERVABLE_PROPERTY.getName()), CoreMatchers.hasItems("op1", "op2"));
        Assert.assertThat(map.get(SosDataMapping.IS_PROCEDURE_DESCRIPTION.getName()), CoreMatchers.instanceOf(SosProcedureDescription.class));
        Assert.assertThat(map.get(SosDataMapping.IS_PROCEDURE_DESCRIPTION_FORMAT.getName()), CoreMatchers.is("solo-format"));
        Assert.assertThat((Set<String>) map.get(SosDataMapping.IS_FEATURE_OF_INTEREST_TYPES.getName()), CoreMatchers.hasItems("foi1", "foi2"));
        Assert.assertThat((Set<String>) map.get(SosDataMapping.IS_OBSERVATION_TYPES.getName()), CoreMatchers.hasItems("ot1", "ot2"));

    }
}
