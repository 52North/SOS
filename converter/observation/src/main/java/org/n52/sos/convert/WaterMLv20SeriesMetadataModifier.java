/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.ows.service.ResponseFormat;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.AbstractObservationResponse;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 */
public class WaterMLv20SeriesMetadataModifier
        extends AbstractRequestResponseModifier {

    private static final Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = Sets.newHashSet(
            new RequestResponseModifierKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, new GetObservationRequest(),
                    new GetObservationResponse()),
            new RequestResponseModifierKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                    new GetObservationByIdRequest(), new GetObservationByIdResponse()));

    @Override
    public Set<RequestResponseModifierKey> getKeys() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public OwsServiceResponse modifyResponse(OwsServiceRequest request, OwsServiceResponse response)
            throws OwsExceptionReport {
        if (isWaterMLResponse(response) && response instanceof AbstractObservationResponse) {
            List<OmObservation> observations = new LinkedList<OmObservation>();
            while (((AbstractObservationResponse) response).getObservationCollection().hasNext()) {
                OmObservation o = ((AbstractObservationResponse) response).getObservationCollection().next();
                observations.add(o);
                if (!o.isSetValue()) {
                    continue;
                }
                if (o.getObservationConstellation().isSetDefaultPointMetadata()) {
                    o.getValue().setDefaultPointMetadata(o.getObservationConstellation().getDefaultPointMetadata());
                }
                if (o.getObservationConstellation().isSetMetadata()) {
                    o.getValue().setMetadata(o.getObservationConstellation().getMetadata());
                }
            }
            return ((AbstractObservationResponse) response)
                    .setObservationCollection(ObservationStream.of(observations));
        }
        return response;
    }

    private boolean isWaterMLResponse(OwsServiceResponse response) {
        return response instanceof ResponseFormat && ((ResponseFormat) response).isSetResponseFormat()
                && ((ResponseFormat) response).getResponseFormat().equals(WaterMLConstants.NS_WML_20);
    }

}
