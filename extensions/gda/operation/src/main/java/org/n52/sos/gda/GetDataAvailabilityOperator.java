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
package org.n52.sos.gda;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.gda.GetDataAvailabilityConstants.GetDataAvailabilityParams;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.operator.AbstractRequestOperator;
import org.n52.sos.request.operator.WSDLAwareRequestOperator;
import org.n52.sos.wsdl.WSDLOperation;

/**
 * {@code IRequestOperator} to handle {@link GetDataAvailabilityRequest}s.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityOperator
        extends
        AbstractRequestOperator<AbstractGetDataAvailabilityDAO, GetDataAvailabilityRequest, GetDataAvailabilityResponse>
        implements WSDLAwareRequestOperator {

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(GetDataAvailabilityConstants.CONFORMANCE_CLASS);

    /**
     * Constructs a new {@code GetDataAvailabilityOperator}.
     */
    public GetDataAvailabilityOperator() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, GetDataAvailabilityConstants.OPERATION_NAME,
                GetDataAvailabilityRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public GetDataAvailabilityResponse receive(GetDataAvailabilityRequest sosRequest) throws OwsExceptionReport {
        return getDao().getDataAvailability(sosRequest);
    }

    @Override
    protected void checkParameters(GetDataAvailabilityRequest sosRequest) throws OwsExceptionReport {
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
            checkObservedProperties(sosRequest.getObservedProperties(),
                    GetDataAvailabilityParams.observedProperty.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureIDs(sosRequest.getProcedures(), GetDataAvailabilityParams.procedure.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkFeatureOfInterestIdentifiers(sosRequest.getFeaturesOfInterest(),
                    GetDataAvailabilityParams.featureOfInterest.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkOfferings(sosRequest.getOfferings(),
                    GetDataAvailabilityParams.offering);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    @Override
    public WSDLOperation getSosOperationDefinition() {
//       TODO no schema available
//        return GetDataAvailabilityConstants.WSDL_OPERATION;
        return null;
    }

    @Override
    public Map<String, String> getAdditionalSchemaImports() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getAdditionalPrefixes() {
        return Collections.emptyMap();
    }
}
