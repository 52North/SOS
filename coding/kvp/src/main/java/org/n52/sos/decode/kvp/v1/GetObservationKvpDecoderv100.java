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
package org.n52.sos.decode.kvp.v1;

import java.util.regex.Pattern;

import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.sos.decode.kvp.AbstractSosKvpDecoder;
import org.n52.svalbard.decode.exception.DecodingException;

/**
 * @since 4.0.0
 *
 */
public class GetObservationKvpDecoderv100 extends AbstractSosKvpDecoder<GetObservationRequest> {

    private static final Pattern SPATIAL_FILTER_REGEX =
            Pattern.compile("^om:featureOfInterest.*(,\\s*[-+]?\\d*\\.?\\d+){4}(,.*)?$");

    public GetObservationKvpDecoderv100() {
        super(GetObservationRequest::new, Sos1Constants.SERVICEVERSION, SosConstants.Operations.GetObservation);
    }

    @Override
    protected void getRequestParameterDefinitions(Builder<GetObservationRequest> builder) {
        builder.add(SosConstants.GetObservationParams.responseMode, GetObservationRequest::setResponseMode);
        builder.add(SosConstants.GetObservationParams.resultModel, GetObservationRequest::setResultModel);
        builder.add(SosConstants.GetObservationParams.responseFormat,
                normalizeMediaType(GetObservationRequest::setResponseFormat));
        builder.add(SosConstants.GetObservationParams.observedProperty,
                decodeList(GetObservationRequest::setObservedProperties));
        builder.add(SosConstants.GetObservationParams.procedure, decodeList(GetObservationRequest::setProcedures));
        builder.add(SosConstants.GetObservationParams.offering, decodeList(GetObservationRequest::setOfferings));
        builder.add(SosConstants.GetObservationParams.featureOfInterest, this::decodeFeatureOfInterest);
        builder.add(Sos1Constants.GetObservationParams.eventTime,
                decodeList(decodeTemporalFilter(asList(GetObservationRequest::setTemporalFilters)))
                        .mapThird(this::sanitizeTemporalFilter));
    }

    private String sanitizeTemporalFilter(String value) {
        // for v1, prepend om:phenomenonTime if not present
        if (!value.contains(",")) {
            return "om:phenomenonTime," + value;
        } else {
            return value;
        }
    }

    private void decodeFeatureOfInterest(GetObservationRequest request, String name, String value)
            throws DecodingException {
        if (SPATIAL_FILTER_REGEX.matcher(value).matches()) {
            request.setSpatialFilter(decodeSpatialFilter(name, decodeList(value)));
        } else {
            request.setFeatureIdentifiers(decodeList(value));
        }
    }
}
