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

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdHelper;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.ds.OperationDAO;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.inspire.aqd.ReportObligationRepository;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;

public abstract class AbstractAqdRequestOperator<D extends OperationDAO, Q extends AbstractServiceRequest<?>, A extends AbstractServiceResponse>
        extends AbstractRequestOperator<D, Q, A> {
    public AbstractAqdRequestOperator(String operationName, Class<Q> requestType) {
        super(AqdConstants.AQD, AqdConstants.VERSION, operationName, requestType);
    }

    @Override
    protected D initDAO(String service, String operationName) {
        return super.initDAO(SosConstants.SOS, operationName);
    }

    protected void checkExtensions(final AbstractServiceRequest<?> request, final CompositeOwsException exceptions) {
        if (request.isSetExtensions() && AqdHelper.getInstance().hasFlowExtension(request.getExtensions())) {
            try {
                AqdHelper.getInstance().getFlow(request.getExtensions());
            } catch (InvalidParameterValueException e) {
                exceptions.add(e);
            }
        }

    }

    protected AbstractServiceRequest<?> changeRequestServiceVersion(AbstractServiceRequest<?> request) {
        request.setService(SosConstants.SOS);
        request.setVersion(Sos2Constants.SERVICEVERSION);
        return request;
    }

    protected AbstractServiceRequest<?> changeRequestServiceVersionToAqd(AbstractServiceRequest<?> request) {
        request.setService(AqdConstants.AQD);
        request.setVersion(AqdConstants.VERSION);
        return request;
    }

    protected AbstractServiceResponse changeResponseServiceVersion(AbstractServiceResponse response) {
        response.setService(AqdConstants.AQD);
        response.setVersion(AqdConstants.VERSION);
        return response;
    }

    protected void checkReportingHeader(ReportObligationType type) throws CodedException {
        ReportObligationRepository.getInstance().createHeader(type);
    }

    @Override
    protected void checkServiceParameter(String service) throws OwsExceptionReport {
        if (service == null || service.equalsIgnoreCase("NOT_SET")) {
            throw new MissingServiceParameterException();
        } else if (!service.equals(AqdConstants.AQD)) {
            throw new InvalidServiceParameterException(service);
        }
    }
}
