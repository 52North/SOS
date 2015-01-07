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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.ds.AbstractGetCapabilitiesDAO;
import org.n52.sos.exception.ows.concrete.InvalidAcceptVersionsParameterException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.DCP;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.sos.ogc.ows.SosServiceIdentification;
import org.n52.sos.ogc.sos.SosCapabilities;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosObservationOffering;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.response.GetCapabilitiesResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AqdGetCapabilitiesOperatorV10 extends
        AbstractAqdRequestOperator<AbstractGetCapabilitiesDAO, GetCapabilitiesRequest, GetCapabilitiesResponse> {

    private static final String OPERATION_NAME = SosConstants.Operations.GetCapabilities.name();

    public AqdGetCapabilitiesOperatorV10() {
        super(OPERATION_NAME, GetCapabilitiesRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public GetCapabilitiesResponse receive(GetCapabilitiesRequest request) throws OwsExceptionReport {
        return modifyCapabilities((GetCapabilitiesResponse) changeResponseServiceVersion(getDao().getCapabilities(
                (GetCapabilitiesRequest) changeRequestServiceVersion(request))));
    }

    private GetCapabilitiesResponse modifyCapabilities(GetCapabilitiesResponse response) {
        SosCapabilities capabilities = response.getCapabilities();
        capabilities.setVersion(AqdConstants.VERSION);
        capabilities.setService(AqdConstants.AQD);
        if (capabilities.isSetServiceIdentification()) {
            SosServiceIdentification serviceIdentification = capabilities.getServiceIdentification();
            serviceIdentification.setVersions(Lists.newArrayList(AqdConstants.VERSION));
        }
        if (capabilities.isSetOperationsMetadata()) {
            for (String key : capabilities.getOperationsMetadata().getCommonValues().keySet()) {
                if (key.equals(OWSConstants.RequestParams.service.name())) {
                    capabilities.getOperationsMetadata().overrideCommonValue(
                            OWSConstants.RequestParams.service.name(),
                            new OwsParameterValuePossibleValues(AqdConstants.AQD));
                } else if (key.equals(OWSConstants.RequestParams.version.name())) {
                    capabilities.getOperationsMetadata().overrideCommonValue(
                            OWSConstants.RequestParams.version.name(),
                            new OwsParameterValuePossibleValues(AqdConstants.VERSION));
                }
            }
            Set<OwsOperation> aqdOperations = Sets.newHashSetWithExpectedSize(2);
            for (OwsOperation operation : capabilities.getOperationsMetadata().getOperations()) {
                if (operation.getOperationName().equals(SosConstants.Operations.GetCapabilities.name())) {
                    if (operation.getParameterValues().containsKey(
                            SosConstants.GetCapabilitiesParams.AcceptVersions.name())) {
                        operation.overrideParameter(SosConstants.GetCapabilitiesParams.AcceptVersions,
                                new OwsParameterValuePossibleValues(AqdConstants.VERSION));
                    }
                    aqdOperations.add(operation);
                    checkDCP(operation);
                }
                if (operation.getOperationName().equals(SosConstants.Operations.GetObservation.name())) {
                    if (operation.getParameterValues().containsKey(
                            SosConstants.GetObservationParams.responseFormat.name())) {
                        operation.overrideParameter(SosConstants.GetObservationParams.responseFormat,
                                new OwsParameterValuePossibleValues(AqdConstants.NS_AQD));
                    }
                    aqdOperations.add(operation);
                    checkDCP(operation);
                    Set<String> flows =
                            Sets.newHashSet(ReportObligationType.E1A.name(), ReportObligationType.E1B.name(),
                                    ReportObligationType.E2A.name());
                    operation.addParameterValue(AqdConstants.EXTENSION_FLOW,
                            new OwsParameterValuePossibleValues(flows));
                }
            }
            capabilities.getOperationsMetadata().setOperations(aqdOperations);
        }
        if (capabilities.isSetContents()) {
            ArrayList<String> responseFormats = Lists.newArrayList(AqdConstants.NS_AQD);
            for (SosObservationOffering observationOffering : capabilities.getContents()) {
                observationOffering.setResponseFormats(responseFormats);
            }
        }
        return response;
    }

    private void checkDCP(OwsOperation operation) {
        DCP toRemove = null;
        for (DCP dcp : operation.getDcp().get("POST")) {
            if (dcp.getUrl().endsWith("/json")) {
                toRemove = dcp;
            }
        }
        if (toRemove != null) {
            operation.getDcp().get("POST").remove(toRemove);
        }
    }

    @Override
    protected void checkParameters(GetCapabilitiesRequest request) throws OwsExceptionReport {
        final CompositeOwsException exceptions = new CompositeOwsException();
        if (request.isSetAcceptVersions()) {
            boolean contains = false;
            for (String version : request.getAcceptVersions()) {
                if (AqdConstants.VERSION.equals(version)) {
                    contains = true;
                }
            }
            if (!contains) {
                exceptions.add(new InvalidAcceptVersionsParameterException(request.getAcceptVersions()));
            }
        }
        checkExtensions(request, exceptions);
        exceptions.throwIfNotEmpty();
    }

}