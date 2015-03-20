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
package org.n52.sos.service.operator;

import org.n52.sos.exception.ows.OperationNotSupportedException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.operator.RequestOperator;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.response.AbstractServiceResponse;

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
