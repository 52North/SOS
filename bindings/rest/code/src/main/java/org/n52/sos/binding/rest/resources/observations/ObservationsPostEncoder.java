/*
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
package org.n52.sos.binding.rest.resources.observations;

import net.opengis.sosREST.x10.ObservationDocument;

import org.n52.iceland.response.ServiceResponse;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.binding.rest.Constants;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.svalbard.util.XmlOptionsHelper;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class ObservationsPostEncoder extends AObservationsEncoder {
    public ObservationsPostEncoder(Constants constants, XmlOptionsHelper xmlOptionsHelper) {
        super(constants, xmlOptionsHelper);
    }
    @Override
    public ServiceResponse encodeRestResponse(RestResponse restResponse) throws OwsExceptionReport {
        if (restResponse != null && restResponse instanceof ObservationsPostResponse) {
            ObservationsPostResponse observationsPostResponse = (ObservationsPostResponse) restResponse;
            ObservationDocument xb_ObservationRestDoc = createRestObservationDocumentFrom(observationsPostResponse
                    .getXb_OMObservation());

            ServiceResponse response
                    = createServiceResponseFromXBDocument(xb_ObservationRestDoc, Constants.REST_RESOURCE_RELATION_OBSERVATIONS,
                                                          HTTPStatus.CREATED, false, true);

            addLocationHeader(response,
                              observationsPostResponse.getObservationIdentifier(), Constants.REST_RESOURCE_RELATION_OBSERVATIONS);

            return response;
        }
        return null;
    }

}
