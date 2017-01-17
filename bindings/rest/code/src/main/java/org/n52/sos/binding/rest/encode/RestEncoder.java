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
package org.n52.sos.binding.rest.encode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.response.ServiceResponse;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.binding.rest.Constants;
import org.n52.sos.binding.rest.requests.ResourceNotFoundResponse;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.binding.rest.resources.OptionsRestEncoder;
import org.n52.sos.binding.rest.resources.OptionsRestResponse;
import org.n52.sos.binding.rest.resources.ServiceEndpointEncoder;
import org.n52.sos.binding.rest.resources.ServiceEndpointResponse;
import org.n52.sos.binding.rest.resources.capabilities.CapabilitiesGetEncoder;
import org.n52.sos.binding.rest.resources.capabilities.CapabilitiesGetResponse;
import org.n52.sos.binding.rest.resources.features.FeatureByIdResponse;
import org.n52.sos.binding.rest.resources.features.FeaturesGetEncoder;
import org.n52.sos.binding.rest.resources.features.FeaturesResponse;
import org.n52.sos.binding.rest.resources.observations.ObservationsDeleteEncoder;
import org.n52.sos.binding.rest.resources.observations.ObservationsDeleteRespone;
import org.n52.sos.binding.rest.resources.observations.ObservationsGetByIdResponse;
import org.n52.sos.binding.rest.resources.observations.ObservationsGetEncoder;
import org.n52.sos.binding.rest.resources.observations.ObservationsPostEncoder;
import org.n52.sos.binding.rest.resources.observations.ObservationsPostResponse;
import org.n52.sos.binding.rest.resources.observations.ObservationsSearchResponse;
import org.n52.sos.binding.rest.resources.offerings.OfferingByIdResponse;
import org.n52.sos.binding.rest.resources.offerings.OfferingsGetEncoder;
import org.n52.sos.binding.rest.resources.offerings.OfferingsResponse;
import org.n52.sos.binding.rest.resources.sensors.GetSensorByIdResponse;
import org.n52.sos.binding.rest.resources.sensors.SensorsGetEncoder;
import org.n52.sos.binding.rest.resources.sensors.SensorsGetResponse;
import org.n52.sos.binding.rest.resources.sensors.SensorsPostEncoder;
import org.n52.sos.binding.rest.resources.sensors.SensorsPostResponse;
import org.n52.sos.binding.rest.resources.sensors.SensorsPutEncoder;
import org.n52.sos.binding.rest.resources.sensors.SensorsPutResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.EncodingContext;
import org.n52.svalbard.encode.SchemaAwareEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.util.XmlOptionsHelper;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class RestEncoder implements Constructable, SchemaAwareEncoder<ServiceResponse, RestResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestEncoder.class);
    private final Constants constants;
    private final XmlOptionsHelper xmlOptionsHelper;
    private Set<EncoderKey> encoderKeys;

    public RestEncoder(Constants constants, XmlOptionsHelper xmlOptionsHelper) {
        this.constants = constants;
        this.xmlOptionsHelper = xmlOptionsHelper;
    }

    public Constants getConstants() {
        return constants;
    }

    public XmlOptionsHelper getXmlOptionsHelper() {
        return xmlOptionsHelper;
    }

    @Override
    public void init() {
        String namespace = this.constants.getEncodingNamespace();
        this.encoderKeys = CodingHelper.encoderKeysForElements(namespace, RestResponse.class);
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(encoderKeys));
    }

    @Override
    public ServiceResponse encode(final RestResponse restResponse)
            throws EncodingException {
        try {
            // 0 variables
            ServiceResponse encodedResponse;

            // 1 get decoder for response
            final ResourceEncoder encoder = getRestEncoderForBindingResponse(restResponse);
            LOGGER.debug("RestEncoder found for RestResponse {}: {}", restResponse.getClass().getName(), encoder
                         .getClass()
                         .getName());
            // 2 decode
            encodedResponse = encoder.encodeRestResponse(restResponse);

            // 3 return the results
            return encodedResponse;
        } catch (OwsExceptionReport owse) {
            throw new EncodingException(owse);
        }
    }

    private ResourceEncoder getRestEncoderForBindingResponse(final RestResponse restResponse) throws EncodingException {
        if (restResponse != null) {
            if (isSensorsGetResponse(restResponse)) {
                return new SensorsGetEncoder(constants, xmlOptionsHelper);
            } else if (isObservationsGetResponse(restResponse)) {
                return new ObservationsGetEncoder(constants, xmlOptionsHelper);
            } else if (restResponse instanceof CapabilitiesGetResponse) {
                return new CapabilitiesGetEncoder(constants, xmlOptionsHelper);
            } else if (restResponse instanceof ObservationsPostResponse) {
                return new ObservationsPostEncoder(constants, xmlOptionsHelper);
            } else if (restResponse instanceof SensorsPostResponse) {
                return new SensorsPostEncoder(constants, xmlOptionsHelper);
            } else if (restResponse instanceof SensorsPutResponse) {
                return new SensorsPutEncoder(constants, xmlOptionsHelper);
            } else if (isOfferingsGetResponse(restResponse)) {
                return new OfferingsGetEncoder(constants, xmlOptionsHelper);
            } else if (isFeatureResponse(restResponse)) {
                return new FeaturesGetEncoder(constants, xmlOptionsHelper);
            } else if (restResponse instanceof ObservationsDeleteRespone) {
                return new ObservationsDeleteEncoder(constants, xmlOptionsHelper);
            } else if (restResponse instanceof OptionsRestResponse) {
                return new OptionsRestEncoder(constants, xmlOptionsHelper);
            } else if (restResponse instanceof ResourceNotFoundResponse) {
                return new GenericRestEncoder(constants, xmlOptionsHelper);
            } else if (restResponse instanceof ServiceEndpointResponse) {
                return new ServiceEndpointEncoder(constants, xmlOptionsHelper);
            }
        }
        final String exceptionText = String
                .format("No encoder is available for response type '%s' by this encoder '%s'!",
                        restResponse != null ? restResponse.getClass().getName() : "null",
                        this.getClass().getName());
        LOGGER.debug(exceptionText);
        throw new EncodingException(exceptionText);
    }

    protected boolean isFeatureResponse(final RestResponse restResponse) {
        return restResponse instanceof FeatureByIdResponse || restResponse instanceof FeaturesResponse;
    }

    private boolean isObservationsGetResponse(final RestResponse restResponse) {
        return restResponse instanceof ObservationsGetByIdResponse || restResponse instanceof ObservationsSearchResponse;
    }

    private boolean isSensorsGetResponse(final RestResponse restResponse) {
        return restResponse instanceof GetSensorByIdResponse || restResponse instanceof SensorsGetResponse;
    }

    private boolean isOfferingsGetResponse(final RestResponse restResponse) {
        return restResponse instanceof OfferingsResponse || restResponse instanceof OfferingByIdResponse;
    }

    @Override
    public ServiceResponse encode(final RestResponse objectToEncode,
                                  EncodingContext iGNOREDadditionalValues) throws EncodingException {
        return encode(objectToEncode);
    }

    @Override
    public Set<EncoderKey> getKeys() {
        return Collections.unmodifiableSet(encoderKeys);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        if (nameSpacePrefixMap != null) {
            nameSpacePrefixMap.put(constants.getEncodingNamespace(), constants.getEncodingPrefix());
        }
    }

    @Override
    public MediaType getContentType() {
        return constants.getContentTypeDefault();
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(new SchemaLocation(constants.getEncodingNamespace(),
                                                  constants.getEncodingSchemaUrl().toString()));
    }

}
