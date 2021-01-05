/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.sos.ds.AbstractGetObservationByIdHandler;
import org.n52.sos.exception.ows.concrete.MissingResponseFormatParameterException;
import org.n52.sos.wsdl.Metadata;
import org.n52.sos.wsdl.Metadatas;
import org.n52.svalbard.ConformanceClasses;

/**
 * @since 4.0.0
 *
 */
public class SosGetObservationByIdOperatorV20 extends
        AbstractV2RequestOperator<AbstractGetObservationByIdHandler,
        GetObservationByIdRequest,
        GetObservationByIdResponse> {

    private static final Set<String> CONFORMANCE_CLASSES =
            Collections.singleton(ConformanceClasses.SOS_V2_OBSERVATION_BY_ID_RETRIEVAL);

    public SosGetObservationByIdOperatorV20() {
        super(SosConstants.Operations.GetObservationById.name(), GetObservationByIdRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public GetObservationByIdResponse receive(GetObservationByIdRequest sosRequest) throws OwsExceptionReport {
        if (!sosRequest.isSetResponseFormat()) {
            sosRequest.setResponseFormat(getActiveProfile().getObservationResponseFormat());
        }
        GetObservationByIdResponse response = getOperationHandler().getObservationById(sosRequest);
        if (response.getResponseFormat() == null) {
            throw new MissingResponseFormatParameterException();
        }
        return response;
    }

    @Override
    protected void checkParameters(GetObservationByIdRequest sosRequest) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        // check parameters with variable content
        try {
            checkServiceParameter(sosRequest.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(sosRequest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkObservationIDs(sosRequest.getObservationIdentifier(),
                    Sos2Constants.GetObservationByIdParams.observation.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        checkExtensions(sosRequest, exceptions);
        exceptions.throwIfNotEmpty();
    }

    @Override
    public Metadata getSosOperationDefinition() {
        return Metadatas.GET_OBSERVATION_BY_ID;
    }
}
