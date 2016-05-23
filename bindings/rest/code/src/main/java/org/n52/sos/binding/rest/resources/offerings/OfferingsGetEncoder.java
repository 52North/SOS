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


import net.opengis.sosREST.x10.ObservationOfferingDocument;
import net.opengis.sosREST.x10.ObservationOfferingType;
import net.opengis.sosREST.x10.OfferingCollectionDocument;
import net.opengis.sosREST.x10.ResourceCollectionType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.encode.ResourceEncoder;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.util.http.HTTPStatus;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class OfferingsGetEncoder extends ResourceEncoder {
    
    @Override
    public ServiceResponse encodeRestResponse(RestResponse response) throws OwsExceptionReport
    {
        if (response instanceof OfferingsResponse) {
            
            return encodeOfferingsResponse((OfferingsResponse) response);
            
        } else if (response instanceof OfferingByIdResponse) {
            
            return encodeOfferingByIdResponse((OfferingByIdResponse) response);
            
        }
        return null;
    }

    private ServiceResponse encodeOfferingByIdResponse(OfferingByIdResponse response) throws OwsExceptionReport
    {
        // 0 create observation offering document
        ObservationOfferingDocument xb_ObservationOfferingDoc = ObservationOfferingDocument.Factory.newInstance();
        ObservationOfferingType xb_ObservationOfferingRest = xb_ObservationOfferingDoc.addNewObservationOffering();
        
        // 1 add observation offering from response
        net.opengis.sos.x20.ObservationOfferingType xb_offering = response.getObservationOfferingXB();
        String offeringIdentifier = xb_offering.getIdentifier();
        String procedureIdentifier = xb_offering.getProcedure();
        xb_ObservationOfferingRest.setObservationOffering(xb_offering);
        
        // 2 add self link
        setValuesOfLinkToUniqueResource(xb_ObservationOfferingRest.addNewLink(),
                offeringIdentifier,
                bindingConstants.getResourceRelationSelf(),
                bindingConstants.getResourceOfferings());
        
        // 2.1 add link to sensor
        setValuesOfLinkToUniqueResource(xb_ObservationOfferingRest.addNewLink(),
                procedureIdentifier,
                bindingConstants.getResourceRelationSensorGet(),
                bindingConstants.getResourceSensors());
        
        // 3 create result and return
        return createOfferingResponseWithStatusOK(xb_ObservationOfferingDoc,false,false);
    }

    private ServiceResponse encodeOfferingsResponse(OfferingsResponse response) throws OwsExceptionReport
    {
        // 0 create offering collection
        OfferingCollectionDocument xb_OfferingCollectionDoc = OfferingCollectionDocument.Factory.newInstance();
        ResourceCollectionType xb_OfferingCollection = xb_OfferingCollectionDoc.addNewOfferingCollection();
        
        // 1 add self link
        setValuesOfLinkToGlobalResource(xb_OfferingCollection.addNewLink(),
                bindingConstants.getResourceRelationSelf(),
                bindingConstants.getResourceOfferings());
        
        // 2 add offering links
        setOfferingLinks(xb_OfferingCollection, response.getOfferingIdentifiers());
        
        // 3 create result and return
        return createOfferingResponseWithStatusOK(xb_OfferingCollectionDoc,true,true);
    }

    private ServiceResponse createOfferingResponseWithStatusOK(XmlObject xb_AnyXml, boolean isResourceCollection, boolean isGlobalResource) throws OwsExceptionReport
    {
        return createServiceResponseFromXBDocument(
                xb_AnyXml,
                bindingConstants.getResourceOfferings(),
                HTTPStatus.OK,
                isResourceCollection,
                isGlobalResource);
    }

}
