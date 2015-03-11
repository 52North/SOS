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
package org.n52.sos.binding.rest.encode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.opengis.sosREST.x10.LinkType;
import net.opengis.sosREST.x10.ResourceCollectionType;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.exception.ows.concrete.EncoderResponseUnsupportedException;
import org.n52.sos.exception.ows.concrete.ErrorWhileSavingResponseToOutputStreamException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.HTTPHeaders;
import org.n52.sos.util.http.HTTPMethods;
import org.n52.sos.util.http.HTTPStatus;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public abstract class ResourceEncoder extends RestEncoder {

    public  abstract ServiceResponse encodeRestResponse(RestResponse objectToEncode) throws OwsExceptionReport;
    
    protected String createHrefForResourceType(String resourceType)
    {
        return bindingConstants.getServiceUrl()
                .concat(bindingConstants.getUrlPattern())
                .concat("/")
                .concat(resourceType);
    }
    
    protected String createHrefForResourceAndIdentifier(String resourceType,
            String identifier)
    {
        return createHrefForResourceType(resourceType)
                .concat("/")
                .concat(identifier);
    }
    
    protected String createHrefForResourceTypeAndQueryString(String resourceType, String queryString) {
        return createHrefForResourceType(resourceType)
                .concat("?")
                .concat(queryString);
    }
    
    protected void setValuesOfLinkToDynamicResource(LinkType xb_RestLink, String resourceQueryString, String relation, String resourceTypeIdentifier)
    {
        setLinkValues(xb_RestLink,
                createRelationWithNamespace(relation),
                createHrefForResourceTypeAndQueryString(resourceTypeIdentifier, resourceQueryString),
                bindingConstants.getContentTypeDefault().toString());
    }
    
    protected void setValuesOfLinkToGlobalResource(LinkType xb_Link,
            String relationIdentifier,
            String resourceType)
    {
        setLinkValues(xb_Link,
                createRelationWithNamespace(relationIdentifier),
                createHrefForResourceType(resourceType),
                bindingConstants.getContentTypeDefault().toString());
    }

    protected void setValuesOfLinkToUniqueResource(LinkType xb_RestLink, String resourceId, String relation, String resourceType)
    {
        setLinkValues(xb_RestLink,
                createRelationWithNamespace(relation),
                createHrefForResourceAndIdentifier(resourceType, resourceId),
                bindingConstants.getContentTypeDefault().toString());
    }
    
    protected void setLinkValues(LinkType xb_Link, String rel, String href, String type) {
        xb_Link.setRel(rel);
        xb_Link.setHref(href);
        xb_Link.setType(type);
    }
    
    protected String createRelationWithNamespace(String relation)
    {
        return bindingConstants.getEncodingNamespace().concat("/").concat(relation);
    }
    
    protected void setOfferingLinks(XmlObject xb_AnyType,
            List<String> offeringIdentifiers)
    {
        if (xb_AnyType instanceof net.opengis.sosREST.x10.CapabilitiesType || xb_AnyType instanceof ResourceCollectionType) {
            LinkType xb_RestLink = null;

            for (String offeringIdentifier : offeringIdentifiers) {
                if (xb_AnyType instanceof net.opengis.sosREST.x10.CapabilitiesType) {
                    xb_RestLink = ((net.opengis.sosREST.x10.CapabilitiesType)xb_AnyType).addNewLink();
                } else {
                    xb_RestLink = ((ResourceCollectionType)xb_AnyType).addNewLink();
                }
                setValuesOfLinkToUniqueResource(xb_RestLink,
                        offeringIdentifier,
                        bindingConstants.getResourceRelationOfferingGet(),
                        bindingConstants.getResourceOfferings());
            }
            
        }
    }
    
    protected ServiceResponse createServiceResponseFromXBDocument(XmlObject xb_RestDoc,
            String resourceType,
            HTTPStatus httpResponseCode,
            boolean isResourceCollection,
            boolean isGlobalResource) throws OwsExceptionReport
    {
        return createServiceResponseFromXBDocument(xb_RestDoc,
                resourceType,
                null,
                httpResponseCode,
                isResourceCollection,
                isGlobalResource);
    }

    protected ServiceResponse createServiceResponseFromXBDocument(
            XmlObject xb_RestDoc,
            String resourceType,
            Map<String, String> additionalHeaders,
            HTTPStatus status,
            boolean isResourceCollection,
            boolean isGlobalResource) throws OwsExceptionReport {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            xb_RestDoc.save(baos, getDefaultXMLOptions());
        } catch (IOException ex) {
            throw new ErrorWhileSavingResponseToOutputStreamException(ex);
        }
        ServiceResponse response = new ServiceResponse(
                baos,
                bindingConstants.getContentTypeDefault(),
                status);
        setAllowHeadersForResourceType(response, resourceType, isResourceCollection, isGlobalResource);

        if (additionalHeaders != null && additionalHeaders.size() > 0) {
            for (String headerIdentifier : additionalHeaders.keySet()) {
                if (headerIdentifier != null && !headerIdentifier.isEmpty()) {
                    String value = additionalHeaders.get(headerIdentifier);
                    if (value != null) {
                        response.setHeader(headerIdentifier, value);
                    }
                }
            }
        }

        return response;
    }

    private void setAllowHeadersForResourceType(ServiceResponse response,
            String resourceType,
            boolean isResourceCollection,
            boolean isGlobalResource)
    {
        String allowedMethods = getAllowedHttpMethodsForResourceType(
                resourceType, isResourceCollection, isGlobalResource);
        response.setHeader(HTTPHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods);
        response.setHeader(HTTPHeaders.ALLOW, allowedMethods);
    }
    
    private String getAllowedHttpMethodsForResourceType(String bindingOperation,
            boolean isResourceCollection,
            boolean isGlobalResource)
    {
        StringBuffer allowedOperations = new StringBuffer("");
        allowHttpGet(allowedOperations);
        appendAllowHttpOptions(allowedOperations);
        
        if (bindingOperation.equalsIgnoreCase(bindingConstants.getResourceObservations()))
        {
            if (isGlobalResource && !isResourceCollection)
            {
                appendAllowHttpPost(allowedOperations);
            }
            else if (!isGlobalResource && !isResourceCollection)
            {
                appendAllowHttpDelete(allowedOperations);
            }
        }
        else if (bindingOperation.equalsIgnoreCase(bindingConstants.getResourceSensors()))
        {
            if (!isGlobalResource && !isResourceCollection)
            {
                appendAllowHttpPut(allowedOperations);
                appendAllowHttpDelete(allowedOperations);
            }
            else if (isGlobalResource && isResourceCollection)
            {
                appendAllowHttpPost(allowedOperations);
            }
        }
        return allowedOperations.toString();
    }
    
    private void appendAllowHttpDelete(StringBuffer allowedOperations) {
        allowedOperations.append(", ").append(HTTPMethods.DELETE);
    }

    private void appendAllowHttpOptions(StringBuffer allowedOperations) {
        allowedOperations.append(", ").append(HTTPMethods.OPTIONS);
    }

    private void appendAllowHttpPost(StringBuffer allowedOperations) {
        allowedOperations.append(", ").append(HTTPMethods.POST);
    }

    private void appendAllowHttpPut(StringBuffer allowedOperations) {
        allowedOperations.append(", ").append(HTTPMethods.PUT);
    }

    private void allowHttpGet(StringBuffer allowedOperations) {
        allowedOperations.append(HTTPMethods.GET);
    }
    
    protected XmlOptions getDefaultXMLOptions()
    {

        XmlOptions bindingXmlOptions = new XmlOptions(XmlOptionsHelper.getInstance().getXmlOptions());
        @SuppressWarnings("unchecked")
        Map<String,String> prefixes =
                (Map<String,String>) bindingXmlOptions.get(XmlOptions.SAVE_SUGGESTED_PREFIXES);
        prefixes.put(bindingConstants.getEncodingNamespace(), bindingConstants.getEncodingPrefix());
        bindingXmlOptions.setSaveSuggestedPrefixes(prefixes);
        bindingXmlOptions.setSaveImplicitNamespaces(prefixes);
        
        return bindingXmlOptions;
                
    }

    protected OwsExceptionReport createResponseNotSupportedException(String expectedClassString, Object receivedObject)
    {
    	return new EncoderResponseUnsupportedException().
    			withMessage("Received RestResponse is not a '%s' but of type '%s'", 
                        expectedClassString,
                        receivedObject!=null?receivedObject.getClass().getName():"null");
    }

    protected void addLocationHeader(ServiceResponse response,
            String resourceType)
    {
        addLocationHeader(response, null, resourceType);
    }

    protected void addLocationHeader(ServiceResponse response,
            String resourceId,
            String resourceType)
    {
        final String location;
        if (resourceId == null) {
            location = createHrefForResourceType(resourceType);
        } else {
            location = createHrefForResourceAndIdentifier(resourceType,
                                                          resourceId);
        }
        response.setHeader(HTTPHeaders.LOCATION, location);
    }

    protected ServiceResponse createNoContentResponse(String resourceType,
            boolean isResourceCollection,
            boolean isGlobalResource)
    {
        return createContentlessResponse(resourceType, HTTPStatus.NO_CONTENT,isResourceCollection,isGlobalResource);
    }
    
    protected ServiceResponse createContentlessResponse(String resourceType,
            HTTPStatus httpStatusCode,
            boolean isResourceCollection,
            boolean isGlobalResource)
    {
        ServiceResponse response = new ServiceResponse(bindingConstants.getContentTypeDefault(),httpStatusCode);
        setAllowHeadersForResourceType(response, resourceType, isResourceCollection, isGlobalResource);
        return response; 
    }

}
