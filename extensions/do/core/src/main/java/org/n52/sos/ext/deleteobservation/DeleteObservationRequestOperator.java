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
package org.n52.sos.ext.deleteobservation;

import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.CONFORMANCE_CLASSES;
import static org.n52.sos.ogc.sos.SosConstants.SOS;

import java.util.Set;

import org.n52.sos.event.SosEventBus;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
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
        if (DeleteObservationConstants.NS_SOSDO_1_0.equals(sosRequest.getResponseFormat())) {
            try {
                checkObservationIDs(sosRequest.getObservationIdentifiers(), DeleteObservationConstants.PARAM_OBSERVATION);
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        } else {
            if (sosRequest.isSetObservationIdentifiers()) {
                try {
                    checkObservationIDs(sosRequest.getObservationIdentifiers(), DeleteObservationConstants.PARAM_OBSERVATION);
                } catch (OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            }
            try {
                checkOfferingId(sosRequest.getOfferings());
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
            try {
                checkObservedProperties(sosRequest.getObservedProperties(), DeleteObservationConstants.PARAM_OBSERVED_PROPERTY, false);
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
            try {
                checkProcedureIDs(sosRequest.getProcedures(), DeleteObservationConstants.PARAM_PROCEDURE);
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
            try {
                checkFeatureOfInterestIdentifiers(sosRequest.getFeatureIdentifiers(), DeleteObservationConstants.PARAM_FEATURE_OF_INTEREST);
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
            
        }
        exceptions.throwIfNotEmpty();
    }
    
    /**
     * checks if the passed offeringId is supported
     * 
     * @param offeringIds
     *            the offeringId to be checked
     * 
     * 
     * @throws OwsExceptionReport
     *             if the passed offeringId is not supported
     */
    private void checkOfferingId(final Set<String> offeringIds) throws OwsExceptionReport {
        if (offeringIds != null) {
            final Set<String> offerings = Configurator.getInstance().getCache().getOfferings();
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String offeringId : offeringIds) {
                if (offeringId == null || offeringId.isEmpty()) {
                    exceptions.add(new MissingOfferingParameterException());
                } else if (offeringId.contains(SosConstants.SEPARATOR_4_OFFERINGS)) {
                    final String[] offArray = offeringId.split(SosConstants.SEPARATOR_4_OFFERINGS);
                    if (!offerings.contains(offArray[0])
                            || !getCache().getProceduresForOffering(offArray[0]).contains(offArray[1])) {
                        exceptions.add(new InvalidOfferingParameterException(offeringId));
                    }

                } else if (!offerings.contains(offeringId)) {
                    exceptions.add(new InvalidOfferingParameterException(offeringId));
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    @Override
    public Set<String> getConformanceClasses() {
        return CONFORMANCE_CLASSES;
    }

}
