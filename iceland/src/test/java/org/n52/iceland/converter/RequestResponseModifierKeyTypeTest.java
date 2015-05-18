/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.n52.iceland.convert.RequestResponseModifierKeyType;
import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.iceland.request.TestRequest;
import org.n52.iceland.response.AbstractServiceResponse;
import org.n52.iceland.response.TestResponse;

public class RequestResponseModifierKeyTypeTest {

    private static final String service = "SOS";

    private static final String version = "2.0.0";

    AbstractServiceRequest<?> request = new TestRequest();

    AbstractServiceResponse response = new TestResponse();

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
        TestRequest request = new TestRequest();
        request.setService(service);
        request.setVersion(version);
        return request;
    }

    private AbstractServiceResponse getModifiedResponse() {
        TestResponse response = new TestResponse();
        response.setService(service);
        response.setVersion(version);
        return response;
    }
    
}
