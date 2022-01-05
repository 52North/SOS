/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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

import java.util.Optional;

import javax.inject.Inject;

import org.n52.iceland.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.iceland.request.handler.OperationHandler;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.EReportObligationRepository;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.aqd.ReportObligations;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.MissingServiceParameterException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;

public abstract class AbstractAqdRequestOperator<D extends OperationHandler,
                                                    Q extends OwsServiceRequest,
                                                    A extends OwsServiceResponse>
        extends AbstractRequestOperator<D, Q, A> {

    private EReportObligationRepository reportObligationRepository;

    public AbstractAqdRequestOperator(String operationName, Class<Q> requestType) {
        super(AqdConstants.AQD, AqdConstants.VERSION, operationName, requestType);
    }

    public EReportObligationRepository getReportObligationRepository() {
        return reportObligationRepository;
    }

    @Inject
    public void setReportObligationRepository(EReportObligationRepository repo) {
        this.reportObligationRepository = repo;
    }

    @Override
    protected Optional<D> getOptionalOperationHandler(String service, String operationName) {
        return super.getOptionalOperationHandler(SosConstants.SOS, operationName);
    }

    protected void checkExtensions(OwsServiceRequest request, CompositeOwsException exceptions)
            throws OwsExceptionReport {
        if (ReportObligations.hasFlow(request.getExtensions())) {
            ReportObligations.getFlow(request.getExtensions());
        }
    }

    protected OwsServiceRequest changeRequestServiceVersion(OwsServiceRequest request) {
        request.setService(SosConstants.SOS);
        request.setVersion(Sos2Constants.SERVICEVERSION);
        return request;
    }

    protected OwsServiceRequest changeRequestServiceVersionToAqd(OwsServiceRequest request) {
        request.setService(AqdConstants.AQD);
        request.setVersion(AqdConstants.VERSION);
        return request;
    }

    protected OwsServiceResponse changeResponseServiceVersion(OwsServiceResponse response) {
        response.setService(AqdConstants.AQD);
        response.setVersion(AqdConstants.VERSION);
        return response;
    }

    protected void checkReportingHeader(ReportObligationType type) throws OwsExceptionReport {
        this.reportObligationRepository.createHeader(type);
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
