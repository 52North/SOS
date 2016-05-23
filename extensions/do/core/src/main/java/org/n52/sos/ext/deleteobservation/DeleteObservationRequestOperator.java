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
package org.n52.sos.ext.deleteobservation;

import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.CONFORMANCE_CLASSES;
import static org.n52.sos.ogc.sos.SosConstants.SOS;

import java.util.Set;

import org.n52.sos.event.SosEventBus;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.operator.AbstractTransactionalRequestOperator;
import org.n52.sos.request.operator.RequestOperator;
import org.n52.sos.service.Configurator;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 1.0.0
 */
public class DeleteObservationRequestOperator
        extends
        AbstractTransactionalRequestOperator<DeleteObservationAbstractDAO, DeleteObservationRequest, DeleteObservationResponse>
        implements RequestOperator {

    public DeleteObservationRequestOperator() {
        super(SOS, Sos2Constants.SERVICEVERSION, DeleteObservationConstants.Operations.DeleteObservation.name(),
                DeleteObservationRequest.class);
    }

    @Override
    public DeleteObservationResponse receive(DeleteObservationRequest request) throws OwsExceptionReport {
        DeleteObservationResponse response = getDao().deleteObservation(request);
        SosEventBus.fire(new DeleteObservationEvent(request, response));
        return response;
    }

    protected Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    @Override
    protected void checkParameters(DeleteObservationRequest sosRequest) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
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
            checkObservationID(sosRequest.getObservationIdentifier(), DeleteObservationConstants.PARAMETER_NAME);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return CONFORMANCE_CLASSES;
    }

}
