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
package org.n52.sos.binding.rest.resources.capabilities;


import net.opengis.sosREST.x10.CapabilitiesDocument;
import net.opengis.sosREST.x10.CapabilitiesType;

import org.n52.sos.binding.rest.encode.ResourceEncoder;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.util.http.HTTPStatus;


/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class CapabilitiesGetEncoder extends ResourceEncoder {
    
    @Override
    public ServiceResponse encodeRestResponse(RestResponse objectToEncode) throws OwsExceptionReport
    {
        if (objectToEncode != null && objectToEncode instanceof CapabilitiesGetResponse) {
            CapabilitiesGetResponse capabilitiesGetResponse = (CapabilitiesGetResponse) objectToEncode;
            CapabilitiesDocument xb_CapabilitiesRestDoc = CapabilitiesDocument.Factory.newInstance();
            CapabilitiesType xb_CapabilitiesRest = xb_CapabilitiesRestDoc.addNewCapabilities();
            
            xb_CapabilitiesRest.setCapabilities(capabilitiesGetResponse.getSosCapabilitiesXB());
            
            // rel:self
            addSelfLink(xb_CapabilitiesRest);
            
            // rel:offering(s)
            addOfferingsLink(xb_CapabilitiesRest);
            addOfferingLinks(capabilitiesGetResponse, xb_CapabilitiesRest);
            
            // rel:features
            addFeaturesLink(xb_CapabilitiesRest);
            
            // rel:sensor
            addSensorCreateLink(xb_CapabilitiesRest);
            addSensorsLink(xb_CapabilitiesRest);
            
            // rel:observation
            addObservationCreateLink(xb_CapabilitiesRest);
            addObservationLink(xb_CapabilitiesRest);
            
            return createServiceResponseFromXBDocument(
                    xb_CapabilitiesRestDoc,
                    bindingConstants.getResourceCapabilities(),
                    HTTPStatus.OK,
                    false,
                    true);
                
        }
        return null;
    }

    private void addObservationLink(CapabilitiesType xb_CapabilitiesRest)
    {
        setValuesOfLinkToGlobalResource(xb_CapabilitiesRest.addNewLink(),
                bindingConstants.getResourceRelationObservationGet(),
                bindingConstants.getResourceObservations());
    }

    private void addObservationCreateLink(CapabilitiesType xb_CapabilitiesRest)
    {
        setValuesOfLinkToGlobalResource(xb_CapabilitiesRest.addNewLink(),
                bindingConstants.getResourceRelationObservationCreate(),
                bindingConstants.getResourceObservations());
    }

    private void addSensorCreateLink(CapabilitiesType xb_CapabilitiesRest)
    {
        setValuesOfLinkToGlobalResource(xb_CapabilitiesRest.addNewLink(),
                bindingConstants.getResourceRelationSensorCreate(),
                bindingConstants.getResourceSensors());
    }

    private void addSensorsLink(CapabilitiesType xb_CapabilitiesRest)
    {
        setValuesOfLinkToGlobalResource(xb_CapabilitiesRest.addNewLink(),
                bindingConstants.getResourceRelationSensorsGet(),
                bindingConstants.getResourceSensors());
    }

    private void addFeaturesLink(CapabilitiesType xb_CapabilitiesRest)
    {
        setValuesOfLinkToGlobalResource(xb_CapabilitiesRest.addNewLink(),
                bindingConstants.getResourceRelationFeaturesGet(),
                bindingConstants.getResourceFeatures());
    }

    private void addOfferingsLink(CapabilitiesType xb_CapabilitiesRest)
    {
        setValuesOfLinkToGlobalResource(xb_CapabilitiesRest.addNewLink(),
                bindingConstants.getResourceRelationOfferingsGet(),
                bindingConstants.getResourceOfferings());
    }

    protected void addOfferingLinks(CapabilitiesGetResponse capabilitiesGetResponse,
            CapabilitiesType xb_CapabilitiesRest)
    {
        if (capabilitiesGetResponse.getOfferingIdentifiers() != null) {
            setOfferingLinks(xb_CapabilitiesRest,capabilitiesGetResponse.getOfferingIdentifiers());
        }
    }

    protected void addSelfLink(CapabilitiesType xb_CapabilitiesRest)
    {
        setValuesOfLinkToGlobalResource(xb_CapabilitiesRest.addNewLink(),
                bindingConstants.getResourceRelationSelf(),
                bindingConstants.getResourceCapabilities());
    }

}
