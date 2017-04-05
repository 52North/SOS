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
package org.n52.sos.binding.rest.resources.offerings;

import javax.servlet.http.HttpServletRequest;

import org.n52.janmayen.http.HTTPMethods;
import org.n52.sos.binding.rest.Constants;
import org.n52.sos.binding.rest.decode.ResourceDecoder;
import org.n52.sos.binding.rest.requests.BadRequestException;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.resources.OptionsRestRequest;
import org.n52.svalbard.decode.exception.DecodingException;

import com.google.common.base.Strings;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class OfferingsDecoder extends ResourceDecoder {

    public OfferingsDecoder(Constants constants) {
        super(constants);
    }

    @Override
    protected RestRequest decodeGetRequest(HttpServletRequest httpRequest, String pathPayload) throws DecodingException {
        // 0 variables
        RestRequest result = null;

        // 1 identify type of request: by id OR search (OR atom feed)
        if (pathPayload != null && !pathPayload.isEmpty() && httpRequest.getQueryString() == null) {
            result = decodeOfferingByIdRequest(pathPayload);

        } else if (pathPayload == null && Strings.isNullOrEmpty(httpRequest.getQueryString())) {
            // 2.2 global resource
            result = decodeOfferingsGetRequest(httpRequest);
        } else {
            String errorMsg
                    = createBadGetRequestMessage(org.n52.sos.binding.rest.Constants.REST_RESOURCE_RELATION_OFFERINGS, true, true, false);
            BadRequestException bR = new BadRequestException(errorMsg);
            throw new DecodingException(bR);
        }

        // 3 return result
        return result;
    }

    private RestRequest decodeOfferingsGetRequest(HttpServletRequest httpRequest) {
        return new OfferingsRequest(createGetCapabilitiesRequestWithContentSectionOnly());
    }

    private RestRequest decodeOfferingByIdRequest(String pathPayload) {
        return new OfferingByIdRequest(createGetCapabilitiesRequestWithContentSectionOnly(), pathPayload);
    }

    @Override
    protected RestRequest decodeDeleteRequest(HttpServletRequest httpRequest, String pathPayload) throws DecodingException {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.DELETE, Constants.REST_RESOURCE_RELATION_OFFERINGS);
    }

    @Override
    protected RestRequest decodePostRequest(HttpServletRequest httpRequest, String pathPayload) throws DecodingException {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.POST, Constants.REST_RESOURCE_RELATION_OFFERINGS);
    }

    @Override
    protected RestRequest decodePutRequest(HttpServletRequest httpRequest, String pathPayload) throws DecodingException {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.PUT, Constants.REST_RESOURCE_RELATION_OFFERINGS);
    }

    @Override
    protected RestRequest decodeOptionsRequest(HttpServletRequest httpRequest, String pathPayload) {
        return new OptionsRestRequest((Constants.REST_RESOURCE_RELATION_OFFERINGS), false, false);
    }

}
