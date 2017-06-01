/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
import java.util.Map;
import java.util.Set;
import org.n52.shetland.ogc.sos.drt.DeleteResultTemplateConstants;
import org.n52.sos.ds.AbstractDeleteResultTemplateHandler;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.ResultTemplatesDeletion;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.DeleteResultTemplateRequest;
import org.n52.sos.response.DeleteResultTemplateResponse;

/**
 * {@code IRequestOperator} to handle {@link DeleteResultTemplateRequest}s.
 *
 * @author Eike Hinderk Jürrens
 *
 * @since 4.0.0
 */
public class DeleteResultTemplateOperator
        extends 
        AbstractTransactionalRequestOperator<AbstractDeleteResultTemplateHandler, DeleteResultTemplateRequest,
        DeleteResultTemplateResponse>
        implements RequestOperator {

    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet(
            DeleteResultTemplateConstants.CONFORMANCE_CLASS_INSERTION,
            DeleteResultTemplateConstants.CONFORMANCE_CLASS_RETRIEVAL);

    /**
     * Constructs a new {@code DeleteResultTemplateOperator}.
     */
    public DeleteResultTemplateOperator() {
        super(SosConstants.SOS,
                Sos2Constants.SERVICEVERSION,
                DeleteResultTemplateConstants.OPERATION_NAME,
                DeleteResultTemplateRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public DeleteResultTemplateResponse receive(
            DeleteResultTemplateRequest request) throws OwsExceptionReport {
        DeleteResultTemplateResponse response = 
                getDao().deleteResultTemplates(request);
        SosEventBus.fire(new ResultTemplatesDeletion(request, response));
        return response;
    }

    @Override
    protected void checkParameters(DeleteResultTemplateRequest request) 
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

        checkAnyParameter(request, exceptions);
        checkInvalidAllParameters(request, exceptions);
        checkResultTemplates(request, exceptions);
        checkObservedPropertyOfferingPairs(request, exceptions);
        
        exceptions.throwIfNotEmpty();
    }

    private void checkObservedPropertyOfferingPairs(DeleteResultTemplateRequest request, CompositeOwsException exceptions) {
        if (request.isSetObservedPropertyOfferingPairs()) {
            for (Map.Entry<String, String> propertyOfferingPair : request.getObservedPropertyOfferingPairs()) {
                if (!getCache().hasOffering(propertyOfferingPair.getValue())) {
                    exceptions.add(new InvalidParameterValueException(
                            DeleteResultTemplateConstants.PARAMETERS.offering,
                            propertyOfferingPair.getValue()));
                }
                if (!getCache().hasObservableProperty(propertyOfferingPair.getKey())) {
                    exceptions.add(new InvalidParameterValueException(
                            DeleteResultTemplateConstants.PARAMETERS.observableProperty,
                            propertyOfferingPair.getKey()));
                }
            }
        }
    }

    private void checkResultTemplates(DeleteResultTemplateRequest request, CompositeOwsException exceptions) {
        if (request.isSetResultTemplates()) {
            for (String resultTemplate : request.getResultTemplates()) {
                if (!getCache().hasResultTemplate(resultTemplate)) {
                    exceptions.add(new InvalidParameterValueException(
                            DeleteResultTemplateConstants.PARAMETERS.resultTemplate,
                            resultTemplate));
                }
            }
        }
    }

    private void checkAnyParameter(DeleteResultTemplateRequest request,
            CompositeOwsException exceptions) {
        if (!request.isSetResultTemplates() &&
                !request.isSetObservedPropertyOfferingPairs()) {
            exceptions.add(new MissingParameterValueException(
                    "resultTemplate XOR offering and observedProperty."));
        }
    }
    
    private void checkInvalidAllParameters(DeleteResultTemplateRequest request,
            CompositeOwsException exceptions) {
        if (request.isSetResultTemplates() &&
                request.isSetObservedPropertyOfferingPairs()) {
            exceptions.add(new MissingParameterValueException(
                    "only resultTemplate XOR offering and observedProperty."));
        }
    }

}
