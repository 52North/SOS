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
package org.n52.sos.binding.rest.resources.observations;

import java.io.IOException;

import net.opengis.sosdo.x10.DeleteObservationResponseDocument;
import net.opengis.sosdo.x10.DeleteObservationResponseType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import org.n52.sos.binding.rest.requests.RequestHandler;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ext.deleteobservation.DeleteObservationRequest;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class ObservationsDeleteRequestHandler extends RequestHandler {

    @Override
    public RestResponse handleRequest(RestRequest req) throws OwsExceptionReport, XmlException, IOException
    {
        if (req instanceof ObservationsDeleteRequest) {
            DeleteObservationRequest doReq = ((ObservationsDeleteRequest) req).getDeleteObservationRequest();
            XmlObject xb_deleteObservationResponse = executeSosRequest(doReq);
            if (xb_deleteObservationResponse instanceof DeleteObservationResponseDocument) {
                DeleteObservationResponseType xb_delObsResponse = ((DeleteObservationResponseDocument) xb_deleteObservationResponse).getDeleteObservationResponse();
                if (xb_delObsResponse.getDeletedObservation().equalsIgnoreCase(doReq.getObservationIdentifier())) {
                    return new ObservationsDeleteRespone(xb_delObsResponse.getDeletedObservation());
                }
            }
        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(req,this.getClass().getName());
    }

}
