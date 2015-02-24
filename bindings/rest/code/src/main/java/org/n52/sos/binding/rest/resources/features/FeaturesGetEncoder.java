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


import net.opengis.sosREST.x10.FeatureCollectionDocument;
import net.opengis.sosREST.x10.FeatureDocument;
import net.opengis.sosREST.x10.FeatureType;
import net.opengis.sosREST.x10.ResourceCollectionType;

import org.n52.sos.binding.rest.encode.ResourceEncoder;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.util.http.HTTPStatus;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class FeaturesGetEncoder extends ResourceEncoder {
    
    @Override
    public ServiceResponse encodeRestResponse(RestResponse objectToEncode) throws OwsExceptionReport
    {
        if (objectToEncode != null) {
            
            if (objectToEncode instanceof FeatureByIdResponse) {
                return encodeFeatureByIdResponse((FeatureByIdResponse)objectToEncode);
                
            } else if (objectToEncode instanceof FeaturesResponse) {
                return encodeFeaturesResponse((FeaturesResponse)objectToEncode);
            }
            
        }
        return null;
    }

    private ServiceResponse encodeFeaturesResponse(FeaturesResponse featuresResponse) throws OwsExceptionReport
    {
        String[] featureIds = featuresResponse.getFeatureIds();
        // add feature links
        if (featureIds != null && featureIds.length > 0) {
            FeatureCollectionDocument xb_FeatureCollectionDoc = FeatureCollectionDocument.Factory.newInstance();
            ResourceCollectionType xb_FeatureCollection = xb_FeatureCollectionDoc.addNewFeatureCollection();
            
            for (String featureId : featureIds) {
                addFeatureLink(xb_FeatureCollection, featureId);
            }
            // add self link
            if (featuresResponse instanceof FeaturesSearchResponse) {
                // Case A: search -> link with query string
                setValuesOfLinkToDynamicResource(xb_FeatureCollection.addNewLink(),
                        ((FeaturesSearchResponse)featuresResponse).getQueryString(),
                        bindingConstants.getResourceRelationSelf(),
                        bindingConstants.getResourceFeatures());
            } else {
                // Case B: global resource
                setValuesOfLinkToGlobalResource(xb_FeatureCollection.addNewLink(),
                        bindingConstants.getResourceRelationSelf(),
                        bindingConstants.getResourceFeatures());
            }
            return createServiceResponseFromXBDocument(
                    xb_FeatureCollectionDoc,
                    bindingConstants.getResourceFeatures(),
                    HTTPStatus.OK, true, true);
        }
        else
        {
            return createNoContentResponse(bindingConstants.getResourceFeatures(),true,true);
        }
    }

    private void addFeatureLink(ResourceCollectionType xb_FeatureCollection,
            String featureId)
    {
        setValuesOfLinkToUniqueResource(xb_FeatureCollection.addNewLink(),
                featureId,
                bindingConstants.getResourceRelationFeatureGet(),
                bindingConstants.getResourceFeatures());
    }

    private ServiceResponse encodeFeatureByIdResponse(FeatureByIdResponse featureByIdResponse) throws OwsExceptionReport
    {
        FeatureDocument xb_feature = FeatureDocument.Factory.newInstance();
        FeatureType xb_RestFeature = xb_feature.addNewFeature();
        
        // add Feature from DeleteObservationResponse
        xb_RestFeature.set(featureByIdResponse.getAbstractFeature());
        
        // add selflink
        setValuesOfLinkToUniqueResource(xb_RestFeature.addNewLink(),
                featureByIdResponse.getFeatureResourceIdentifier(),
                bindingConstants.getResourceRelationSelf(),
                bindingConstants.getResourceFeatures());
        
        return createServiceResponseFromXBDocument(
                xb_feature, bindingConstants.getResourceFeatures(),
                HTTPStatus.OK, false, false);
    }

}
