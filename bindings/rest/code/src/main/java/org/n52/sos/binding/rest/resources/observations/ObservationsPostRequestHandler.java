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

import net.opengis.sos.x20.InsertObservationResponseDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.RequestHandler;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.InsertObservationRequest;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class ObservationsPostRequestHandler extends RequestHandler {
    
    @Override
    public RestResponse handleRequest(RestRequest req) throws OwsExceptionReport, XmlException
    {
        if (req != null && req instanceof ObservationsPostRequest) {
            InsertObservationRequest ioReq = ((ObservationsPostRequest) req).getInsertObservationRequest();
            
            // 2 handle core response
            XmlObject xb_InsertObservationResponse = executeSosRequest(ioReq);
            
            if (xb_InsertObservationResponse instanceof InsertObservationResponseDocument) {
                // 3 return response
                // no interesting content, just check the class to be sure that the insertion was successful
                // the restful response requires the link to the newly created observation
            	// FIXME we are always using only the first observation in the list without checking
                return new ObservationsPostResponse(
                        ioReq.getObservations().get(0).getIdentifierCodeWithAuthority().getValue(),
                        ((ObservationsPostRequest) req).getXb_OMObservation());
            } 
        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(req,this.getClass().getName());
    }

}
