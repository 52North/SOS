/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.util.SosHelper;

/**
 * Renamed, in version 4.x called AbstractGetFeatureOfInterestDAO
 *
 * @since 5.0.0
 *
 */
public abstract class AbstractGetFeatureOfInterestHandler extends AbstractSosOperationHandler {

    public AbstractGetFeatureOfInterestHandler(String service) {
        super(service, SosConstants.Operations.GetFeatureOfInterest.name());
    }

    public abstract GetFeatureOfInterestResponse getFeatureOfInterest(GetFeatureOfInterestRequest request)
            throws OwsExceptionReport;

    protected boolean isRelatedFeature(String featureIdentifier) {
        return getCache().getRelatedFeatures().contains(featureIdentifier);
    }

    protected Set<String> getFeatureIdentifiers(List<String> featureIdentifiers) {
        return featureIdentifiers.stream().flatMap(id -> {
            if (isRelatedFeature(id)) {
                return getCache().getChildFeatures(id, true, true).stream();
            } else {
                return Stream.of(id);
            }
        }).collect(toSet());
    }

    @Override
    protected Set<OwsDomain> getOperationParameters(String service, String version) throws OwsExceptionReport {
        Collection<String> featureIDs = SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version);

        // TODO constraint srid
        Enum<?> parameterName = Sos2Constants.GetFeatureOfInterestParams.spatialFilter;
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            parameterName = Sos1Constants.GetFeatureOfInterestParams.location;
        }

        return new HashSet<>(Arrays.asList(getQueryableProcedureParameter(service, version),
                                           getFeatureOfInterestParameter(service, version),
                                           getObservablePropertyParameter(service, version),
                                           getEnvelopeParameter(parameterName, featureIDs)));
    }
}
