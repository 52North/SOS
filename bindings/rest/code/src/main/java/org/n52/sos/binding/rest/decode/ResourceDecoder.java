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
package org.n52.sos.binding.rest.decode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.RandomAccess;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.n52.sos.binding.rest.Constants;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.OperationNotSupportedException;
import org.n52.sos.exception.ows.concrete.DateTimeException;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.http.HTTPMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * TODO Use KVP helper from 52n-sos-api module
 */
public abstract class ResourceDecoder extends RestDecoder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDecoder.class);
    
    protected Constants bindingConstants = Constants.getInstance();

    protected abstract RestRequest decodeGetRequest(HttpServletRequest httpRequest, String pathPayload) throws OwsExceptionReport, DateTimeException;
    
    protected abstract RestRequest decodeDeleteRequest(HttpServletRequest httpRequest, String pathPayload) throws OwsExceptionReport;
    
    protected abstract RestRequest decodePostRequest(HttpServletRequest httpRequest, String pathPayload) throws OwsExceptionReport;
    
    protected abstract RestRequest decodePutRequest(HttpServletRequest httpRequest, String pathPayload) throws OwsExceptionReport;
    
    protected abstract RestRequest decodeOptionsRequest(HttpServletRequest httpRequest, String pathPayload);

    protected RestRequest decodeRestRequest(final HttpServletRequest httpRequest) throws OwsExceptionReport, DateTimeException
    {
        String resourceType = null;
        String pathPayload = null;
    
        if (httpRequest != null && httpRequest.getPathInfo() != null) {
    
            final String resourceTypeWithOrWithoutId = getResourceTypeFromPathInfoWithWorkingUrl(httpRequest.getPathInfo());
            final int indexOfPotentialSecondSlash = resourceTypeWithOrWithoutId.indexOf("/");
    
            if (indexOfPotentialSecondSlash > 1) {
                resourceType = resourceTypeWithOrWithoutId.substring(0,indexOfPotentialSecondSlash);
                pathPayload = resourceTypeWithOrWithoutId.substring(indexOfPotentialSecondSlash + 1);
            } else {
                resourceType = resourceTypeWithOrWithoutId;
            }
            
            LOGGER.debug("resourceType: {}; pathPayload: {} ",resourceType,pathPayload);
    
            // delegate to HTTP method specific decoders for parsing this resource's request
            if (httpRequest.getMethod().equalsIgnoreCase(HTTPMethods.GET) ||
                httpRequest.getMethod().equalsIgnoreCase(HTTPMethods.HEAD)) {
                return decodeGetRequest(httpRequest, pathPayload);
            } else if (httpRequest.getMethod().equalsIgnoreCase(HTTPMethods.DELETE)) {
                return decodeDeleteRequest(httpRequest,pathPayload);
            } else if (httpRequest.getMethod().equalsIgnoreCase(HTTPMethods.POST)) {
                return decodePostRequest(httpRequest,pathPayload);
            } else if (httpRequest.getMethod().equalsIgnoreCase(HTTPMethods.PUT)) {
                return decodePutRequest(httpRequest,pathPayload);
            } else if (httpRequest.getMethod().equalsIgnoreCase(HTTPMethods.OPTIONS)) {
                return decodeOptionsRequest(httpRequest,pathPayload);
            }
        }
    
        final String exceptionText = String.format("The resource type \"%s\" via HTTP method \"%s\" is not supported by this IDecoder implementiation.", 
                resourceType,
                httpRequest.getMethod());
        LOGGER.debug(exceptionText);
        throw new OperationNotSupportedException(resourceType);
    
    }
    
    protected String getRelationIdentifierWithNamespace(final String resourceRelationIdentifier)
    {
        return bindingConstants.getEncodingNamespace()
                .concat("/")
                .concat(resourceRelationIdentifier);
    }
    
    protected GetCapabilitiesRequest createGetCapabilitiesRequest()
    {
        final GetCapabilitiesRequest getCapabilitiesRequest = new GetCapabilitiesRequest();
        getCapabilitiesRequest.setVersion(bindingConstants.getSosVersion());
        getCapabilitiesRequest.setService(bindingConstants.getSosService());
        final String[] acceptedVersions = { bindingConstants.getSosVersion() };
        getCapabilitiesRequest.setAcceptVersions(Arrays.asList(acceptedVersions));

        return getCapabilitiesRequest;
    }
    
    protected String getResourceIdFromRestfulHref(final String restfulHref)
    {
        return restfulHref.substring(restfulHref.lastIndexOf("/")+1);
    }
    
    protected GetCapabilitiesRequest createGetCapabilitiesRequestWithContentSectionOnly()
    {
        final GetCapabilitiesRequest getCapabilitiesRequestOnlyContents = createGetCapabilitiesRequest();

        final ArrayList<String> sections = new ArrayList<String>();
        sections.add(bindingConstants.getSosCapabilitiesSectionNameContents());
        getCapabilitiesRequestOnlyContents.setSections(sections);

        return getCapabilitiesRequestOnlyContents;
    }
    
    // TODO use this to return operation not allowed response
    protected OwsExceptionReport createHttpMethodForThisResourceNotSupportedException(final String httpMethod, final String resourceType)
    {
        final String exceptionText = String.format("The HTTP-%s %s \"%s\"!",
                httpMethod,
                bindingConstants.getHttpOperationNotAllowedForResourceTypeMessagePart(),
                resourceType);
        final OperationNotSupportedException onse = new OperationNotSupportedException(exceptionText);
        return onse;
    }
    
    protected Map<String, String> getKvPEncodedParameters(final HttpServletRequest httpRequest)
    {
        final Map<String, String> kvp = new HashMap<String, String>();
        final Enumeration<?> parameterNames = httpRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            // all key names to lower case
            final String key = (String) parameterNames.nextElement();
            kvp.put(key.toLowerCase(), httpRequest.getParameter(key));
        }
        return kvp;
    }
    
    protected String checkParameterSingleValue(final String parameterValue, final String parameterName)
            throws OwsExceptionReport {
        if (!parameterValue.isEmpty() && (parameterValue.split(",").length == 1)) {
            return parameterValue;
        } else {
        	final InvalidParameterValueException ipve = new InvalidParameterValueException(parameterName, parameterValue); 
            LOGGER.debug(ipve.getMessage());
            throw ipve;
        }
    }

    protected List<String> splitKvpParameterValueToList(final String value)
    {
        return Arrays.asList(value.split(bindingConstants.getKvPEncodingValueSplitter()));
    }

    
    /**
     * {@link org.n52.sos.decode.SosKvpDecoderv20#parseNamespaces(String)}
     */
    protected Map<String, String> parseNamespaces(final String values) {
        final Map<String, String> namespaces = new HashMap<String, String>();
        final List<String> array =
                Arrays.asList(values.replaceAll("\\),", "").replaceAll("\\)", "").split("xmlns\\("));
        for (final String string : array) {
            if ((string != null) && !string.isEmpty()) {
                final String[] s = string.split(",");
                namespaces.put(s[0], s[1]);
            }
        }
        return namespaces;
    }

    /*
     * {@link org.n52.sos.decode.kvp.v2.AbstractKvpDecoder#parseTemporalFilter(List<String>, String)}
     * TODO move to KVP map decoder to share code
     */
    protected List<TemporalFilter> parseTemporalFilter(final List<String> parameterValues)
            throws DateTimeException, InvalidParameterValueException {
        final List<TemporalFilter> filterList = new ArrayList<TemporalFilter>(1);
        if (parameterValues.size() != 2) {
            throw new InvalidParameterValueException(
            		bindingConstants.getHttpGetParameterNameTemporalFilter(),
            		Arrays.toString(parameterValues.toArray()));
        }
        filterList.add(createTemporalFilterFromValue(parameterValues.get(1), parameterValues.get(0)));
        return filterList;
    }
    
    
    /*
     * {@link org.n52.sos.decode.kvp.v2.AbstractKvpDecoder#createTemporalFilterFromValue(String, String)}
     * TODO move to KVP map decoder to share code
     */
    private TemporalFilter createTemporalFilterFromValue(final String value, final String valueReference) throws DateTimeException, InvalidParameterValueException {
    	final TemporalFilter temporalFilter = new TemporalFilter();
    	temporalFilter.setValueReference(valueReference);
    	final String[] times = value.split("/");

    	if (times.length == 1) {
    		final TimeInstant ti = new TimeInstant();
    		if (SosIndeterminateTime.contains(times[0])) {
    		    ti.setSosIndeterminateTime(SosIndeterminateTime.getEnumForString(times[0]));
    		} else {
    			final DateTime instant = DateTimeHelper.parseIsoString2DateTime(times[0]);
    			ti.setValue(instant);
                ti.setRequestedTimeLength(DateTimeHelper.getTimeLengthBeforeTimeZone(times[0]));
    		}
    		temporalFilter.setOperator(TimeOperator.TM_Equals);
    		temporalFilter.setTime(ti);
    	} else if (times.length == 2) {
    		final DateTime start = DateTimeHelper.parseIsoString2DateTime(times[0]);
    		// check if end time is a full ISO 8106 string
            int timeLength = DateTimeHelper.getTimeLengthBeforeTimeZone(times[1]);
            DateTime origEnd = DateTimeHelper.parseIsoString2DateTime(times[1]);
            DateTime end = DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition(
                    origEnd, timeLength);
    		final TimePeriod tp = new TimePeriod(start, end);
    		temporalFilter.setOperator(TimeOperator.TM_During);
    		temporalFilter.setTime(tp);
    	} else {
    		throw new InvalidParameterValueException(bindingConstants.getHttpGetParameterNameTemporalFilter(),value);
    	}
    	return temporalFilter;
    }

    /**
     * {@link org.n52.sos.decode.kvp.v2.AbstractKvpDecoder#parseSpatialFilter(List<String>, String)} 
     * TODO move to KVP map decoder to share code
     */
    protected SpatialFilter parseSpatialFilter(List<String> parameterValues, final String parameterName)
            throws OwsExceptionReport {
        if (!parameterValues.isEmpty()) {
            if (!(parameterValues instanceof RandomAccess)) {
                parameterValues = new ArrayList<String>(parameterValues);
            }
            final SpatialFilter spatialFilter = new SpatialFilter();

            boolean hasSrid = false;

            spatialFilter.setValueReference(parameterValues.get(0));

            int srid = 4326;
            if (parameterValues.get(parameterValues.size() - 1).startsWith(getSrsNamePrefixSosV2())
                    || parameterValues.get(parameterValues.size() - 1).startsWith(getSrsNamePrefix())) {
                hasSrid = true;
                srid = SosHelper.parseSrsName(parameterValues.get(parameterValues.size() - 1));
            }

            List<String> coordinates;
            if (hasSrid) {
                coordinates = parameterValues.subList(1, parameterValues.size() - 1);
            } else {
                coordinates = parameterValues.subList(1, parameterValues.size());
            }

            if (coordinates.size() != 4) {
                throw new InvalidParameterValueException().
                at(parameterName).
                withMessage("The parameter value of '%s' is not valid!", parameterName);
            }
            final String lowerCorner = String.format(Locale.US, "%f %f", new Float(coordinates.get(0)), new Float(coordinates.get(1)));
            final String upperCorner = String.format(Locale.US, "%f %f", new Float(coordinates.get(2)), new Float(coordinates.get(3)));
            spatialFilter.setGeometry(JTSHelper.createGeometryFromWKT(JTSHelper.createWKTPolygonFromEnvelope(lowerCorner, upperCorner), srid));
            spatialFilter.setOperator(SpatialOperator.BBOX);
            return spatialFilter;
        }
        return null;
    }

    protected String getSrsNamePrefix() {
        return ServiceConfiguration.getInstance().getSrsNamePrefix();
    }

    protected String getSrsNamePrefixSosV2() {
        return ServiceConfiguration.getInstance().getSrsNamePrefixSosV2();
    }


    protected boolean isContentOfPostRequestValid(final HttpServletRequest httpRequest) throws OwsExceptionReport
    {
        if ((httpRequest == null) || (httpRequest.getContentType() == null))
        {
            final String errorMessage = "HTTP header 'Content-Type'";
            LOGGER.debug("{} is missing", errorMessage);
            throw new MissingParameterValueException(errorMessage);
        }
        if (!httpRequest.getContentType().contains(bindingConstants.getContentTypeDefault().toString()))
        {
            final String errorMessage = String.format("POST %s Type \"%s\" is not supported. Please use type \"%s\"",
                    bindingConstants.getErrorMessageWrongContentType(),
                    httpRequest.getContentType(),
                    bindingConstants.getContentTypeDefault());
            LOGGER.debug(errorMessage);
            throw new InvalidParameterValueException("Content-Type", httpRequest.getContentType()).
            	withMessage(errorMessage);
        }
        return true;
    }

    protected String createBadGetRequestMessage(final String resourceType,
            final boolean globalResoureAllowed,
            final boolean byIdAllowed,
            final boolean searchAllowed)
    {
        final StringBuilder errorMsgBuilder = new StringBuilder();
        errorMsgBuilder.append(String.format(bindingConstants.getErrorMessageBadGetRequest(), resourceType));
        if (globalResoureAllowed)
        {
            errorMsgBuilder.append(String.format(bindingConstants.getErrorMessageBadGetRequestGlobalResource(), resourceType));
        }
        if (byIdAllowed)
        {
            if (globalResoureAllowed)
            {
                errorMsgBuilder.append(" or ");
            }
            errorMsgBuilder.append(String.format(bindingConstants.getErrorMessageBadGetRequestById(), resourceType));
        }
        if (searchAllowed)
        {
            if (globalResoureAllowed || byIdAllowed)
            {
                errorMsgBuilder.append(" or ");
            }
            errorMsgBuilder.append(String.format(bindingConstants.getErrorMessageBadGetRequestSearch(), resourceType));
        }
        errorMsgBuilder.append('.');
        return errorMsgBuilder.toString();
    }
}
