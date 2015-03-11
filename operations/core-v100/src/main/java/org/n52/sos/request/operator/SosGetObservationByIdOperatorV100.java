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

import org.n52.sos.ds.AbstractGetObservationByIdDAO;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.MissingResponseFormatParameterException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.response.GetObservationByIdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
public class SosGetObservationByIdOperatorV100
        extends
        AbstractV1RequestOperator<AbstractGetObservationByIdDAO, GetObservationByIdRequest, GetObservationByIdResponse> {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SosGetObservationByIdOperatorV100.class.getName());

    private static final String OPERATION_NAME = SosConstants.Operations.GetObservationById.name();

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton("http://www.opengis.net/spec/SOS/1.0/conf/enhanced");

    /**
     * Constructor
     * 
     */
    public SosGetObservationByIdOperatorV100() {
        super(OPERATION_NAME, GetObservationByIdRequest.class);
    }

    @Override
    protected void checkParameters(GetObservationByIdRequest sosRequest) throws OwsExceptionReport {

        checkServiceParameter(sosRequest.getService());
        // check valid obs ids
        checkObservationIDs(sosRequest.getObservationIdentifier(),
                Sos1Constants.GetObservationByIdParams.ObservationId.name());
        // check responseFormat!
        String responseFormat = sosRequest.getResponseFormat();
        if (responseFormat == null || responseFormat.isEmpty()) {
            throw new MissingResponseFormatParameterException();
        }
        // srsName and resultModel (omObs, om:Meas, Swe:?, responseMode (inline
        // only)
        String responseMode = sosRequest.getResponseMode();
        if (responseMode != null && !responseMode.equalsIgnoreCase("inline")) {
            throw new NoApplicableCodeException().withMessage(
                    "Only responseMode inline is currently supported by this SOS 1.0.0 implementation").at(
                    SosConstants.GetObservationParams.responseMode);
        }

        String resultModel = sosRequest.getResultModel();
        if (resultModel != null) {
            throw new NoApplicableCodeException().at(SosConstants.GetObservationParams.resultModel).withMessage(
                    "resultModel is currently not supported by this SOS 1.0.0 implementation");
        }
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    protected GetObservationByIdResponse receive(GetObservationByIdRequest sosRequest) throws OwsExceptionReport {
        GetObservationByIdResponse sosResponse = getDao().getObservationById(sosRequest);
        setObservationResponseResponseFormatAndContentType(sosRequest, sosResponse);
        return sosResponse;
    }
}
