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
package org.n52.sos.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.GetObservationResponse;

public class RequestResponseModifierKeyTypeTest {

    private static final String service = "SOS";

    private static final String version = "2.0.0";

    AbstractServiceRequest<?> request = new GetObservationRequest();

    AbstractServiceResponse response = new GetObservationResponse();

    @Test
    public void testHashCode() {
        assertEquals(new RequestResponseModifierKeyType(service, version, request).hashCode(), new RequestResponseModifierKeyType(service, version, request).hashCode());
        assertEquals(new RequestResponseModifierKeyType(service, version, request, response).hashCode(), new RequestResponseModifierKeyType(service, version, request, response).hashCode());

        assertEquals(new RequestResponseModifierKeyType(service, version, request).hashCode(), new RequestResponseModifierKeyType(service, version, getModifiedRequest()).hashCode());
        assertEquals(new RequestResponseModifierKeyType(service, version, request, response).hashCode(), new RequestResponseModifierKeyType(service, version, getModifiedRequest(), getModifiedResponse()).hashCode());

    }

    @Test
    public void testEquals() {
        assertEquals(new RequestResponseModifierKeyType(service, version, request), new RequestResponseModifierKeyType(service, version, request));
        assertEquals(new RequestResponseModifierKeyType(service, version, request, response), new RequestResponseModifierKeyType(service, version, request, response));
        // for production
        
        
        
        assertEquals(new RequestResponseModifierKeyType(service, version, request, response), new RequestResponseModifierKeyType(service, version, request));
        assertEquals(new RequestResponseModifierKeyType(service, version, request, response), new RequestResponseModifierKeyType(service, version, request, response));
    }

    private AbstractServiceRequest<?> getModifiedRequest() {
        GetObservationRequest request = new GetObservationRequest();
        request.setService(service);
        request.setVersion(version);
        return request;
    }

    private AbstractServiceResponse getModifiedResponse() {
        GetObservationResponse response = new GetObservationResponse();
        response.setService(service);
        response.setVersion(version);
        return response;
    }
    
}
