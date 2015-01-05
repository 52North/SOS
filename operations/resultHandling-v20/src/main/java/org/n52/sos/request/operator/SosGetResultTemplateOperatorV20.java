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

import org.n52.sos.ds.AbstractGetResultTemplateDAO;
import org.n52.sos.exception.ows.concrete.InvalidObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.GetResultTemplateRequest;
import org.n52.sos.response.GetResultTemplateResponse;
import org.n52.sos.wsdl.WSDLConstants;
import org.n52.sos.wsdl.WSDLOperation;

/**
 * @since 4.0.0
 *
 */
public class SosGetResultTemplateOperatorV20 extends AbstractV2RequestOperator<AbstractGetResultTemplateDAO, GetResultTemplateRequest, GetResultTemplateResponse> {
    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(ConformanceClasses.SOS_V2_RESULT_RETRIEVAL);

    public SosGetResultTemplateOperatorV20() {
        super(Sos2Constants.Operations.GetResultTemplate.name(), GetResultTemplateRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public GetResultTemplateResponse receive(GetResultTemplateRequest request) throws OwsExceptionReport {
        return getDao().getResultTemplate(request);
    }

    @Override
    protected void checkParameters(GetResultTemplateRequest request) throws OwsExceptionReport {
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
            checkOffering(request.getOffering());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkObservedProperty(request.getObservedProperty());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    private void checkOffering(String offering) throws OwsExceptionReport {
        if (offering == null || offering.isEmpty()) {
            throw new MissingOfferingParameterException();
        } else if (!getCache().getOfferings().contains(offering)) {
            throw new InvalidOfferingParameterException(offering);
        }
    }

    private void checkObservedProperty(String observedProperty) throws OwsExceptionReport {
        if (observedProperty == null || observedProperty.isEmpty()) {
            throw new MissingObservedPropertyParameterException();
        } else if (!getCache().getObservableProperties().contains(observedProperty)) {
            throw new InvalidObservedPropertyParameterException(observedProperty);
        }
    }

    @Override
    public WSDLOperation getSosOperationDefinition() {
        return WSDLConstants.Operations.GET_RESULT_TEMPLATE;
    }
}
