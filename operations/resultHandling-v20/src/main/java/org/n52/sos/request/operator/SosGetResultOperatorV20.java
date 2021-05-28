/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.sos.ds.AbstractGetResultHandler;
import org.n52.sos.wsdl.Metadata;
import org.n52.sos.wsdl.Metadatas;
import org.n52.svalbard.ConformanceClasses;

/**
 * @since 4.0.0
 *
 */
public class SosGetResultOperatorV20
        extends AbstractV2RequestOperator<AbstractGetResultHandler, GetResultRequest, GetResultResponse> {
    private static final Set<String> CONFORMANCE_CLASSES =
            Collections.singleton(ConformanceClasses.SOS_V2_RESULT_RETRIEVAL);

    public SosGetResultOperatorV20() {
        super(SosConstants.Operations.GetResult.name(), GetResultRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public GetResultResponse receive(GetResultRequest request) throws OwsExceptionReport {
        return getOperationHandler().getResult(request);
    }

    @Override
    protected void checkParameters(GetResultRequest request) throws OwsExceptionReport {
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
        try {
            checkOffering(request.getOffering(), Sos2Constants.GetResultTemplateParams.offering);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkObservedProperty(request.getObservedProperty(),
                    Sos2Constants.GetResultTemplateParams.observedProperty, false);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSpatialFilter(request.getSpatialFilter(), SosConstants.GetObservationParams.featureOfInterest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkTemporalFilter(request.getTemporalFilter(), Sos2Constants.GetObservationParams.temporalFilter);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        exceptions.throwIfNotEmpty();
    }

    @Override
    public Metadata getSosOperationDefinition() {
        return Metadatas.GET_RESULT;
    }
}
