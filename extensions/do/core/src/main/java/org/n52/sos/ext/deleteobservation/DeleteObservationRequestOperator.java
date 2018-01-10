/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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


import java.util.Collections;
import java.util.Set;

import org.n52.iceland.request.operator.RequestOperator;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationResponse;
import org.n52.sos.request.operator.AbstractTransactionalRequestOperator;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 1.0.0
 */
public class DeleteObservationRequestOperator
        extends AbstractTransactionalRequestOperator<AbstractDeleteObservationHandler, DeleteObservationRequest, DeleteObservationResponse>
        implements RequestOperator {

    public DeleteObservationRequestOperator() {
        super(SosConstants.SOS,
              Sos2Constants.SERVICEVERSION,
              DeleteObservationConstants.Operations.DeleteObservation,
              DeleteObservationRequest.class);
    }

    @Override
    public DeleteObservationResponse receive(DeleteObservationRequest request) throws OwsExceptionReport {
        DeleteObservationResponse response = getOperationHandler().deleteObservation(request);
        getServiceEventBus().submit(new DeleteObservationEvent(request, response));
        return response;
    }

    @Override
    protected void checkParameters(DeleteObservationRequest sosRequest) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkObservationIDs(sosRequest.getObservationIdentifiers(), DeleteObservationConstants.PARAM_OBSERVATION);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if(SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(DeleteObservationConstants.CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

}
