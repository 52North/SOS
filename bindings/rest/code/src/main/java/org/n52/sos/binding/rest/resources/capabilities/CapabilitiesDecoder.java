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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.n52.sos.binding.rest.decode.ResourceDecoder;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.resources.OptionsRestRequest;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.util.http.HTTPMethods;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class CapabilitiesDecoder extends ResourceDecoder {
    
    @Override
    protected RestRequest decodeGetRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        GetCapabilitiesRequest getCapabilitiesRequest = createGetCapabilitiesRequestWithoutOperationsMetadata();
        
        return new CapabilitiesRequestImpl(getCapabilitiesRequest);
    }

    private GetCapabilitiesRequest createGetCapabilitiesRequestWithoutOperationsMetadata()
    {
        GetCapabilitiesRequest getCapabilitiesRequest = createGetCapabilitiesRequest();
        
        List<String> sections = new ArrayList<String>(4);
        sections.add("ServiceIdentification");
        sections.add("ServiceProvider");
        sections.add("FilterCapabilities");
        sections.add("Contents");
        getCapabilitiesRequest.setSections(sections);
        
        return getCapabilitiesRequest;
    }

    @Override
    protected RestRequest decodeDeleteRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.DELETE,
                bindingConstants.getResourceCapabilities());
    }

    @Override
    protected RestRequest decodePostRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.POST,
                bindingConstants.getResourceCapabilities());
    }

    @Override
    protected RestRequest decodePutRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.PUT,
                bindingConstants.getResourceCapabilities());
    }

    @Override
    protected RestRequest decodeOptionsRequest(HttpServletRequest httpRequest, String pathPayload)
    {
        return new OptionsRestRequest(bindingConstants.getResourceCapabilities(),true,false);
    }
    
}
