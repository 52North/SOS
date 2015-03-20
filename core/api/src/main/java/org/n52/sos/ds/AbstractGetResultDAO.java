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
package org.n52.sos.ds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.exception.ows.OperationNotSupportedException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetResultRequest;
import org.n52.sos.response.GetResultResponse;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractGetResultDAO extends AbstractOperationDAO {
    public AbstractGetResultDAO(String service) {
        super(service, SosConstants.Operations.GetResult.name());
    }

    @Override
    protected void setOperationsMetadata(OwsOperation opsMeta, String service, String version)
            throws OwsExceptionReport {
        Set<String> resultTemplateIdentifier = getCache().getResultTemplates();
        Set<String> offerings = new HashSet<String>(0);
        Collection<String> observableProperties = new ArrayList<String>(0);
        Collection<String> featureOfInterest = new ArrayList<String>(0);
        if (resultTemplateIdentifier != null && !resultTemplateIdentifier.isEmpty()) {
            offerings = getCache().getOfferingsWithResultTemplate();
            observableProperties = getCache().getObservablePropertiesWithResultTemplate();
            featureOfInterest = getCache().getFeaturesOfInterestWithResultTemplate();
        }
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            throw new OperationNotSupportedException().at(SosConstants.Operations.GetResult).withMessage(
                    "This operation is not supported for SOS {}!", Sos1Constants.SERVICEVERSION);
        } else if (version.equals(Sos2Constants.SERVICEVERSION)) {
            addOfferingParameter(opsMeta, offerings);
            addObservablePropertyParameter(opsMeta, observableProperties);
            addFeatureOfInterestParameter(opsMeta, featureOfInterest);
            // TODO get the values for temporal and spatial filtering
            // set param temporalFilter
            // opsMeta.addParameterValue(Sos2Constants.GetResultParams.temporalFilter.name(),
            // new OWSParameterValuePossibleValues(null));
            // // set param spatialFilter
            // opsMeta.addParameterValue(Sos2Constants.GetResultParams.spatialFilter.name(),
            // new OWSParameterValuePossibleValues(null));
        }
    }

    public abstract GetResultResponse getResult(GetResultRequest request) throws OwsExceptionReport;

}
