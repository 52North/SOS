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
package org.n52.sos.request.operator;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.ds.BatchOperationDAO;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.BatchRequest;
import org.n52.sos.response.BatchResponse;
import org.n52.sos.util.BatchConstants;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class BatchRequestOperator extends AbstractRequestOperator<BatchOperationDAO, BatchRequest, BatchResponse> {
    public BatchRequestOperator() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, BatchConstants.OPERATION_NAME, BatchRequest.class);
    }

    @Override
    protected BatchResponse receive(BatchRequest request) throws OwsExceptionReport {
        for (AbstractServiceRequest<?> r : request) {
            r.setRequestContext(request.getRequestContext());
        }
        return getDao().executeRequests(request);
    }

    @Override
    protected void checkParameters(BatchRequest request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(request.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(request);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        if (request.isEmpty()) {
            exceptions.add(new MissingParameterValueException(BatchConstants.PARAMETER_NAME));
        }
        exceptions.throwIfNotEmpty();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }
}
