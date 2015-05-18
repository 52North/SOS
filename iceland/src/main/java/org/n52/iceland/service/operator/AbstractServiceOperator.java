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

import org.n52.iceland.exception.ows.OperationNotSupportedException;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.iceland.request.operator.RequestOperator;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.iceland.response.AbstractServiceResponse;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class AbstractServiceOperator implements ServiceOperator {
    private final ServiceOperatorKey key;

    public AbstractServiceOperator(String service, String version) {
        this.key = new ServiceOperatorKey(service, version);
    }

    @Override
    public ServiceOperatorKey getServiceOperatorKey() {
        return key;
    }

    @Override
    public AbstractServiceResponse receiveRequest(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        RequestOperator ro =
                RequestOperatorRepository.getInstance().getRequestOperator(getServiceOperatorKey(),
                        request.getOperationName());
        if (ro != null) {
            AbstractServiceResponse response = ro.receiveRequest(request);
            if (response != null) {
                return response;
            }
        }
        throw new OperationNotSupportedException(request.getOperationName());
    }
}
