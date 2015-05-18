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
package org.n52.iceland.service.operator;

import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.iceland.response.AbstractServiceResponse;

/**
 * Interface for the request listeners.
 * 
 * @since 4.0.0
 */
public interface ServiceOperator {

    /**
     * method handles the incoming operation request and returns a matching
     * response or an ServiceExceptionReport if the SOS was not able to build a
     * response
     * 
     * @param request
     *            the operation request
     * 
     * @return Returns the response of the request (e.g. CapabilitiesResponse
     * 
     * 
     * @throws OwsExceptionReport
     */
    AbstractServiceResponse receiveRequest(AbstractServiceRequest<?> request) throws OwsExceptionReport;

    ServiceOperatorKey getServiceOperatorKey();

}
