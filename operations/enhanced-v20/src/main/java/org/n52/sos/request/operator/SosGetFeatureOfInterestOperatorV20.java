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
import java.util.List;
import java.util.Set;

import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.ds.AbstractGetFeatureOfInterestHandler;
import org.n52.sos.wsdl.Metadata;
import org.n52.sos.wsdl.Metadatas;
import org.n52.svalbard.ConformanceClasses;

/**
 * @since 4.0.0
 *
 */
public class SosGetFeatureOfInterestOperatorV20 extends
        AbstractV2RequestOperator<AbstractGetFeatureOfInterestHandler,
        GetFeatureOfInterestRequest,
        GetFeatureOfInterestResponse> {

    private static final Set<String> CONFORMANCE_CLASSES =
            Collections.singleton(ConformanceClasses.SOS_V2_FEATURE_OF_INTEREST_RETRIEVAL);

    private static final String OPERATION_NAME = SosConstants.Operations.GetFeatureOfInterest.name();

    public SosGetFeatureOfInterestOperatorV20() {
        super(OPERATION_NAME, GetFeatureOfInterestRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public GetFeatureOfInterestResponse receive(GetFeatureOfInterestRequest request) throws OwsExceptionReport {
        return getOperationHandler().getFeatureOfInterest(request);
    }

    @Override
    protected void checkParameters(GetFeatureOfInterestRequest request) throws OwsExceptionReport {
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
            checkObservedProperties(request.getObservedProperties(),
                    Sos2Constants.GetFeatureOfInterestParams.observedProperty.name(), false);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkQueryableProcedures(request.getProcedures(),
                    Sos2Constants.GetFeatureOfInterestParams.procedure.name());
            // add instance and child procedures to request
            if (request.isSetProcedures()) {
                request.setProcedures(addChildProcedures(addInstanceProcedures(request.getProcedures())));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            // checkFeatureOfInterestAndRelatedFeatureIdentifier(sosRequest.getFeatureIdentifiers(),
            // Sos2Constants.GetFeatureOfInterestParams.featureOfInterest.name());
            checkFeatureOfInterestIdentifiers(request.getFeatureIdentifiers(),
                    Sos2Constants.GetFeatureOfInterestParams.featureOfInterest.name());
            if (request.isSetFeatureOfInterestIdentifiers()) {
                request.setFeatureIdentifiers(addChildFeatures(request.getFeatureIdentifiers()));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSpatialFilters(request.getSpatialFilters(),
                    Sos2Constants.GetFeatureOfInterestParams.spatialFilter.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        checkExtensions(request, exceptions);
        exceptions.throwIfNotEmpty();
    }

    private void checkFeatureOfInterestAndRelatedFeatureIdentifier(List<String> featureIdentifiers,
            String parameterName) throws OwsExceptionReport {
        if (featureIdentifiers != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            for (String featureOfInterest : featureIdentifiers) {
                try {
                    if (!getCache().hasRelatedFeature(featureOfInterest)) {
                        checkFeatureOfInterestIdentifier(featureOfInterest, parameterName);
                    }
                } catch (OwsExceptionReport e) {
                    exceptions.add(e);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    @Override
    public Metadata getSosOperationDefinition() {
        return Metadatas.GET_FEATURE_OF_INTEREST;
    }

}
