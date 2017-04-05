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
package org.n52.sos.binding.rest.resources.features;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.sampling.x20.SFSamplingFeatureDocument;
import net.opengis.sampling.x20.SFSamplingFeatureType;
import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionCode;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.sos.binding.rest.Constants;
import org.n52.sos.binding.rest.requests.RequestHandler;
import org.n52.sos.binding.rest.requests.ResourceNotFoundResponse;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.exception.EncodingException;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class FeaturesRequestHandler extends RequestHandler {
    public FeaturesRequestHandler(Constants bindingConstants, EncoderRepository encoderRepository,
                                  ServiceOperatorRepository serviceOperatorRepository) {
        super(bindingConstants, encoderRepository, serviceOperatorRepository);
    }

    @Override
    public RestResponse handleRequest(RestRequest request) throws OwsExceptionReport, XmlException, IOException {
        if (request instanceof FeatureByIdRequest) {
            return handleFeatureByIdRequest((FeatureByIdRequest) request);
        } else if (request instanceof FeaturesSearchRequest) {
            return handleFeaturesSearchRequest((FeaturesSearchRequest) request);
        } else if (request instanceof FeaturesRequest) {
            return handleFeaturesRequest((FeaturesRequest) request);
        } else {
            throw logRequestTypeNotSupportedByThisHandlerAndCreateException(request, this.getClass().getName());
        }
    }

    private FeaturesSearchResponse handleFeaturesSearchRequest(FeaturesSearchRequest request)
            throws OwsExceptionReport, XmlException, IOException {
        FeaturesResponse featuresResponse = handleFeaturesRequest(request);
        if (featuresResponse != null && featuresResponse.getFeatureIds() != null) {
            return new FeaturesSearchResponse(featuresResponse.getFeatureIds(), request.getQueryString());
        }
        return null;
    }

    private FeaturesResponse handleFeaturesRequest(FeaturesRequest request)
            throws OwsExceptionReport, XmlException, IOException {
        GetFeatureOfInterestResponseDocument doc = getFeatureOfInterestFromSosCore(request);
        Set<String> featureIds = new HashSet<>();

        if (isFOIMemberArrayAvailable(doc)) {
            for (FeaturePropertyType xb_feature : doc.getGetFeatureOfInterestResponse().getFeatureMemberArray()) {
                SFSamplingFeatureType xb_SFFeature = SFSamplingFeatureDocument.Factory
                        .parse(xb_feature.newInputStream()).getSFSamplingFeature();
                if (xb_SFFeature.isSetIdentifier() &&
                    xb_SFFeature.getIdentifier().getStringValue() != null &&
                    !xb_SFFeature.getIdentifier().getStringValue().isEmpty()) {
                    featureIds.add(xb_SFFeature.getIdentifier().getStringValue());
                }
            }
        }

        return new FeaturesResponse(featureIds.toArray(new String[featureIds.size()]));
    }

    private RestResponse handleFeatureByIdRequest(FeatureByIdRequest request) throws OwsExceptionReport, XmlException {
        String featureId = request.getFeatureResourceIdentifier();
        try {
            GetFeatureOfInterestResponseDocument doc = getFeatureOfInterestFromSosCore(request);
            return new FeatureByIdResponse(featureId, getFeatureFromSosCoreResponse(doc));
        } catch (OwsExceptionReport owsER) {
            if (!owsER.getExceptions().isEmpty()) {
                for (CodedException owsE : owsER.getExceptions()) {
                    if (owsE.getCode().equals(OwsExceptionCode.InvalidParameterValue) &&
                        owsE.getLocator().equals(Sos2Constants.GetFeatureOfInterestParams.featureOfInterest.toString())) {
                        return new ResourceNotFoundResponse((Constants.REST_RESOURCE_RELATION_FEATURES), featureId);
                    }
                }
            }
            throw owsER;
        }
    }

    private boolean isFeatureArrayAvailableAndContains1Feature(GetFeatureOfInterestResponseDocument doc) {
        return isFOIMemberArrayAvailable(doc) &&
               doc.getGetFeatureOfInterestResponse().getFeatureMemberArray().length == 1;
    }

    private boolean isFOIMemberArrayAvailable(GetFeatureOfInterestResponseDocument doc) {
        return doc != null && doc.getGetFeatureOfInterestResponse() != null &&
               doc.getGetFeatureOfInterestResponse().getFeatureMemberArray() != null;
    }

    private GetFeatureOfInterestResponseDocument getFeatureOfInterestFromSosCore(FeaturesRequest request)
            throws OwsExceptionReport, XmlException {
        try {
            XmlObject xb_getFeatureOfInterestResponse = executeSosRequest(request.getGetFeatureOfInterestRequest());
            if (xb_getFeatureOfInterestResponse instanceof GetFeatureOfInterestResponseDocument) {
                return (GetFeatureOfInterestResponseDocument) xb_getFeatureOfInterestResponse;
            } else {
                return null;
            }
        } catch (EncodingException ee) {
            throw new NoApplicableCodeException().causedBy(ee);
        }
    }

    private FeaturePropertyType getFeatureFromSosCoreResponse(GetFeatureOfInterestResponseDocument doc) {
        if (isFeatureArrayAvailableAndContains1Feature(doc)) {
            return doc.getGetFeatureOfInterestResponse().getFeatureMemberArray(0);
        } else {
            return null;
        }
    }

}
