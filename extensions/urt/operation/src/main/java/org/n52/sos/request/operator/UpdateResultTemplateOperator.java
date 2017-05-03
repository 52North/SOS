/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import org.n52.shetland.ogc.sos.urt.UpdateResultTemplateConstants;
import org.n52.sos.ds.AbstractUpdateResultTemplateHandler;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.ResultTemplateUpdate;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.UpdateResultTemplateRequest;
import org.n52.sos.response.UpdateResultTemplateResponse;

/**
 * {@code IRequestOperator} to handle {@link UpdateResultTemplateRequest}s.
 *
 * @author Eike Hinderk Jürrens
 *
 * @since 4.0.0
 */
public class UpdateResultTemplateOperator
        extends 
        AbstractTransactionalRequestOperator
        <AbstractUpdateResultTemplateHandler,
        UpdateResultTemplateRequest,
        UpdateResultTemplateResponse>
        implements RequestOperator {

    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet(
            UpdateResultTemplateConstants.CONFORMANCE_CLASS_INSERTION,
            UpdateResultTemplateConstants.CONFORMANCE_CLASS_RETRIEVAL);

    /**
     * Constructs a new {@code DeleteResultTemplateOperator}.
     */
    public UpdateResultTemplateOperator() {
        super(SosConstants.SOS,
                Sos2Constants.SERVICEVERSION,
                UpdateResultTemplateConstants.OPERATION_NAME,
                UpdateResultTemplateRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public UpdateResultTemplateResponse receive(
            UpdateResultTemplateRequest request) throws OwsExceptionReport {
        UpdateResultTemplateResponse response = 
                getDao().deleteResultTemplates(request);
        SosEventBus.fire(new ResultTemplateUpdate(request, response));
        return response;
    }

    @Override
    protected void checkParameters(UpdateResultTemplateRequest request) 
            throws OwsExceptionReport {
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

        checkResultTemplate(request, exceptions);
        checkAdditionalParameter(request, exceptions);
        
        exceptions.throwIfNotEmpty();
    }

    private void checkResultTemplate(UpdateResultTemplateRequest request,
            CompositeOwsException exceptions) {
        if (!request.isSetResultTemplate()) {
            exceptions.add(
                    new MissingParameterValueException(
                        UpdateResultTemplateConstants.PARAMS.resultTemplate));
        }
        if (!getCache().hasResultTemplate(request.getResultTemplate())) {
            exceptions.add(
                    new InvalidParameterValueException(
                            UpdateResultTemplateConstants.PARAMS.resultTemplate,
                            request.getResultTemplate()));
        }
    }

    private void checkAdditionalParameter(UpdateResultTemplateRequest request,
            CompositeOwsException exceptions) {
        if (!request.isSetResultStructure() && !request.isSetResultEncoding()) {
            exceptions.add(new MissingParameterValueException(
                    String.format("Missing one of %s XORT %s!",
                            UpdateResultTemplateConstants.PARAMS.resultStructure
                            ,UpdateResultTemplateConstants.PARAMS.resultEncoding
                    )));
        }
    }
}
