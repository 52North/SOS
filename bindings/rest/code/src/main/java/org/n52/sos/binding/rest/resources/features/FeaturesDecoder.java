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
package org.n52.sos.binding.rest.resources.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.n52.sos.binding.rest.decode.ResourceDecoder;
import org.n52.sos.binding.rest.requests.BadRequestException;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.resources.OptionsRestRequest;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.util.http.HTTPMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class FeaturesDecoder extends ResourceDecoder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FeaturesDecoder.class);

    @Override
    protected RestRequest decodeGetRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport, DateTimeException
    {
        // variables
        RestRequest result = null;
        
        LOGGER.debug("pathpayload: {}; querystring: {}",pathPayload,httpRequest.getQueryString());
        
        // Case A: By ID
        if (isByIdRequest(httpRequest, pathPayload))
        {
             result = decodeFeatureByIdRequest(pathPayload);
          
        } 
        // Case B: Search
        else if (pathPayload == null && httpRequest.getQueryString() != null)
        {
            result = decodeFeaturesSearchRequest(httpRequest);

        }
        // Case C: Global Resource
        else if (pathPayload == null && httpRequest.getQueryString() == null)
        {
            result = decodeFeaturesRequest();
        }
        else
        {
            String errorMsg = createBadGetRequestMessage(bindingConstants.getResourceFeatures(),true,true,true);
            BadRequestException bR = new BadRequestException(errorMsg);
            throw new NoApplicableCodeException().causedBy(bR);
        }

        return result;
    }


    private boolean isByIdRequest(HttpServletRequest httpRequest,
            String pathPayload)
    {
        return pathPayload != null && !pathPayload.isEmpty() && httpRequest != null && httpRequest.getQueryString() == null;
    }

    private RestRequest decodeFeaturesRequest()
    {
        return new FeaturesRequest(createBasicGetFeatureOfInterestRequest());
    }

    private FeaturesRequest decodeFeaturesSearchRequest(HttpServletRequest httpRequest) throws OwsExceptionReport, DateTimeException
    {
        GetFeatureOfInterestRequest featureOfInterestRequest = createBasicGetFeatureOfInterestRequest();
        
        Map<String,String> parameterMap = getKvPEncodedParameters(httpRequest);
        
        boolean parameterMapValid = false; // if at least one parameter is valid

        for (String parameter : parameterMap.keySet()) {

            String value = parameterMap.get(parameter);
            if (parameter.equalsIgnoreCase(bindingConstants.getHttpGetParameterNameFoi()) &&
                    value != null &&  value.length() > 0)
            {
                featureOfInterestRequest.setFeatureIdentifiers(splitKvpParameterValueToList(value));
                parameterMapValid = true;
            }
            else if (parameter.equalsIgnoreCase(bindingConstants.getHttpGetParameterNameObservedProperty()) &&
                    value != null &&  value.length() > 0)
            {
                featureOfInterestRequest.setObservedProperties(splitKvpParameterValueToList(value));
                parameterMapValid = true;
            }
            else if (parameter.equalsIgnoreCase(bindingConstants.getHttpGetParameterNameProcedure()) &&
                    value != null &&  value.length() > 0)
            {
                featureOfInterestRequest.setProcedures(splitKvpParameterValueToList(value));
                parameterMapValid = true;
            }
            else if (parameter.equalsIgnoreCase(bindingConstants.getHttpGetParameterNameSpatialFilter()) &&
                    value != null &&  value.length() > 0)
            {
                featureOfInterestRequest.setSpatialFilters(parseSpatialFilters(splitKvpParameterValueToList(value),parameter));
                parameterMapValid = true;
            }
            else if (parameter.equalsIgnoreCase(bindingConstants.getHttpGetParameterNameTemporalFilter()) &&
                    value != null &&  value.length() > 0)
            {
                featureOfInterestRequest.setTemporalFilters(parseTemporalFilter(splitKvpParameterValueToList(value)));
                parameterMapValid = true;
            } 
            else if (parameter.equalsIgnoreCase(bindingConstants.getHttpGetParameterNameNamespaces()) &&
                    value != null &&  value.length() > 0)
            {
                featureOfInterestRequest.setNamespaces(parseNamespaces(value));
                parameterMapValid = true;
            }
            else 
            {
                throw new InvalidParameterValueException(parameter, value);
            }
        }
        
        if (!parameterMapValid)
        {
        	throw new InvalidParameterValueException().withMessage(bindingConstants.getErrorMessageBadGetRequestNoValidKvpParameter());
        }
        
        return new FeaturesSearchRequest(featureOfInterestRequest,httpRequest.getQueryString());

    }

    private List<SpatialFilter> parseSpatialFilters(List<String> splitKvpParameterValueToList, String parameterName) throws OwsExceptionReport
    {
        List<SpatialFilter> spatialFilters = new ArrayList<SpatialFilter>(1);
        
        spatialFilters.add(parseSpatialFilter(splitKvpParameterValueToList, parameterName));
        
        return spatialFilters;
    }

    private FeatureByIdRequest decodeFeatureByIdRequest(String pathPayload)
    {
        List<String> featureIDs = new ArrayList<String>(1);
        
        featureIDs.add(pathPayload);
        GetFeatureOfInterestRequest featureOfInterestRequest = createBasicGetFeatureOfInterestRequest();
        featureOfInterestRequest.setFeatureIdentifiers(featureIDs);
        
        return new FeatureByIdRequest(featureOfInterestRequest, pathPayload);
    }

    private GetFeatureOfInterestRequest createBasicGetFeatureOfInterestRequest()
    {
        GetFeatureOfInterestRequest featureOfInterestRequest = new GetFeatureOfInterestRequest();
        
        featureOfInterestRequest.setService(bindingConstants.getSosService());
        featureOfInterestRequest.setVersion(bindingConstants.getSosVersion());
        
        return featureOfInterestRequest;
    }

    @Override
    protected RestRequest decodeDeleteRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.DELETE,
                bindingConstants.getResourceFeatures());
    }

    @Override
    protected RestRequest decodePostRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.POST,
                bindingConstants.getResourceFeatures());
    }

    @Override
    protected RestRequest decodePutRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.PUT,
                bindingConstants.getResourceFeatures());
    }
    
    @Override
    protected RestRequest decodeOptionsRequest(HttpServletRequest httpRequest, String pathPayload)
    {
        boolean isGlobal = false, isCollection = false;
        if (httpRequest != null && httpRequest.getQueryString() != null)
        {
            isGlobal = true;
            isCollection = true;
        }
        return new OptionsRestRequest(bindingConstants.getResourceFeatures(), isGlobal,isCollection);
    }

}
