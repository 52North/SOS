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
package org.n52.sos.converter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.convert.RequestResponseModifierFacilitator;
import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.sos.convert.AbstractRequestResponseModifier;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SeriesTypeChecker extends AbstractRequestResponseModifier {

    private static final Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

    private static Set<RequestResponseModifierKey> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<OwsServiceRequest, OwsServiceResponse> requestResponseMap = Maps.newHashMap();

        requestResponseMap.put(new InsertObservationRequest(), new InsertObservationResponse());
        Set<RequestResponseModifierKey> keys = Sets.newHashSet();
        for (String service : services) {
            for (String version : versions) {
                for (OwsServiceRequest request : requestResponseMap.keySet()) {
                    keys.add(new RequestResponseModifierKey(service, version, request));
                    keys.add(new RequestResponseModifierKey(service, version, request,
                            requestResponseMap.get(request)));
                }
            }
        }
        return keys;
    }

    @Override
    public Set<RequestResponseModifierKey> getKeys() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public OwsServiceRequest modifyRequest(OwsServiceRequest request) throws OwsExceptionReport {
//       if (request instanceof InsertObservationRequest) {
//           return checkForSeriesType((InsertObservationRequest)request);
//       }
       return request;
    }

//    private OwsServiceRequest checkForSeriesType(InsertObservationRequest request) {
//        if (request.hasExtension(Sos2Constants.Extensions.SeriesType)) {
//            Extension<?> seriesType = request.getExtension(Sos2Constants.Extensions.SeriesType).get();
//            for (OmObservation observation : request.getObservations()) {
//                if (seriesType.getValue() instanceof SweText) {
//                    observation.setSeriesType(((SweText)seriesType.getValue()).getStringValue());
//                } else if (seriesType.getValue() instanceof SweCategory) {
//                    observation.setSeriesType(((SweText)seriesType.getValue()).getStringValue());
//                }
//            }
//        }
//        return request;
//    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return super.getFacilitator().setAdderRemover(true);
    }

}
