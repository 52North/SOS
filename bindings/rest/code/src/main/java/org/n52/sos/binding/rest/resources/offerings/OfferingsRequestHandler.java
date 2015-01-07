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
package org.n52.sos.binding.rest.resources.offerings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.opengis.sos.x20.CapabilitiesDocument;
import net.opengis.sos.x20.CapabilitiesType;
import net.opengis.sos.x20.ObservationOfferingType;
import net.opengis.swes.x20.AbstractContentsType.Offering;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.RequestHandler;
import org.n52.sos.binding.rest.requests.ResourceNotFoundResponse;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetCapabilitiesRequest;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class OfferingsRequestHandler extends RequestHandler {

    @Override
    public RestResponse handleRequest(RestRequest request) throws OwsExceptionReport, XmlException, IOException
    {
        if (request != null && request instanceof OfferingsRequest){
            // 0 submit request to SOS core
            GetCapabilitiesRequest getCapabilitiesRequest = ((OfferingsRequest) request).getGetCapabilitiesRequest();
            // 1 handle core response
            XmlObject xb_getCapabilitiesResponse = executeSosRequest(getCapabilitiesRequest);
            if (xb_getCapabilitiesResponse instanceof CapabilitiesDocument)
            {
                CapabilitiesDocument xb_capaCapabilitiesDocument = (CapabilitiesDocument) xb_getCapabilitiesResponse;
                CapabilitiesType xb_sosCapabilities = xb_capaCapabilitiesDocument.getCapabilities();
                boolean isByIdRequest = request instanceof OfferingByIdRequest;
                List<String> offeringIdentifiers = new ArrayList<String>();

                if (isOfferingArrayAvailable(xb_sosCapabilities))
                {
                    // 1.1 if by id: get observation offering for id
                    Offering[] xb_offerings = xb_sosCapabilities.getContents().getContents().getOfferingArray();
                    
                    for (Offering xb_offering : xb_offerings)
                    {
                        ObservationOfferingType xb_observationOffering = getObservationOfferingFromOffering(xb_offering);

                        if (xb_observationOffering.isSetIdentifier())
                        {

                            if (isByIdRequest && hasOfferingTheCorrectIdForByIdRequest(request, xb_observationOffering))
                            {
                                return new OfferingByIdResponse(getObservationOfferingFromOffering(xb_offering));
                            }
                            else if (!isByIdRequest)
                            {
                                offeringIdentifiers.add(xb_observationOffering.getIdentifier());
                            }
                        }
                    }
                }
                // 2 return response
                if (isByIdRequest && offeringIdentifiers.isEmpty())
                {
                    return new ResourceNotFoundResponse(bindingConstants.getResourceOfferings(),
                            ((OfferingByIdRequest)request).getOfferingIdentifier());    
                }
                return new OfferingsResponse(offeringIdentifiers);
            }
        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(request,this.getClass().getName());
    }

    private boolean hasOfferingTheCorrectIdForByIdRequest(RestRequest request,
            ObservationOfferingType xb_observationOffering)
    {
        return xb_observationOffering.getIdentifier().equalsIgnoreCase(((OfferingByIdRequest)request).getOfferingIdentifier());
    }

    private boolean isOfferingArrayAvailable(CapabilitiesType xb_sosCapabilities)
    {
        return xb_sosCapabilities.isSetContents() &&
                xb_sosCapabilities.getContents() != null
                && xb_sosCapabilities.getContents().getContents() != null
                && xb_sosCapabilities.getContents().getContents().getOfferingArray() != null;
    }

}
