/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;
import javax.naming.ConfigurationException;

import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.RequestResponseModifierFacilitator;
import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.ows.service.ResponseFormat;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swes.SwesExtension;
import org.n52.shetland.uvf.UVFConstants;
import org.n52.shetland.uvf.UVFSettingsProvider;
import org.n52.sos.coding.encode.ResponseFormatRepository;
import org.n52.sos.exception.ows.concrete.InvalidResponseFormatParameterException;
import org.n52.sos.exception.ows.concrete.MissingResponseFormatParameterException;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 */
@Configurable
public class UVFRequestModifier
        extends AbstractRequestResponseModifier {

    private static final Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = Sets.newHashSet(
            new RequestResponseModifierKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                    GetObservationRequest.class),
            new RequestResponseModifierKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                    GetObservationByIdRequest.class));

    private String defaultCRS;

    private ResponseFormatRepository responseFormatRepository;

    @Inject
    public void setResponseFormatRepository(ResponseFormatRepository responseFormatRepository) {
        this.responseFormatRepository = responseFormatRepository;
    }

    public ResponseFormatRepository getResponseFormatRepository() {
        return responseFormatRepository;
    }

    @Override
    public Set<RequestResponseModifierKey> getKeys() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public OwsServiceRequest modifyRequest(OwsServiceRequest request)
            throws OwsExceptionReport {
        // check for response format to avoid incomprehensible exception
        if (request instanceof AbstractObservationRequest
                && ((AbstractObservationRequest) request).isSetResponseFormat()) {
            checkResponseFormat(((AbstractObservationRequest) request).getResponseFormat(), request.getService(),
                    request.getVersion());
        }
        if ((request.getRequestContext().getAcceptType().isPresent()
                && request.getRequestContext().getAcceptType().get().contains(UVFConstants.CONTENT_TYPE_UVF))
                || (request instanceof ResponseFormat && ((ResponseFormat) request).isSetResponseFormat()
                        && ((ResponseFormat) request).getResponseFormat()
                                .contains(UVFConstants.CONTENT_TYPE_UVF.getSubtype())
                        && UVFConstants.CONTENT_TYPE_UVF
                                .isCompatible(MediaType.parse(((ResponseFormat) request).getResponseFormat())))) {
            if (request.hasExtension(OWSConstants.AdditionalRequestParams.crs)
                    && request.getExtension(OWSConstants.AdditionalRequestParams.crs).isPresent()
                    && request.getExtension(OWSConstants.AdditionalRequestParams.crs).get()
                            .getValue() instanceof SweText) {
                String requestedCRS =
                        ((SweText) request.getExtension(OWSConstants.AdditionalRequestParams.crs).get().getValue())
                                .getValue();
                if (UVFConstants.ALLOWED_CRS.contains(requestedCRS)) {
                    return request;
                } else {
                    throw new NoApplicableCodeException().withMessage(
                            "When requesting UVF format, the request MUST have "
                                    + "a CRS of the German GK bands, e.g. '%s'. Requested was: '%s'.",
                            UVFConstants.ALLOWED_CRS.toString(), requestedCRS);
                }
            }
            // add default CRS as swe text extension
            SweText crsExtension = (SweText) new SweText().setValue(getDefaultCRS())
                    .setIdentifier(OWSConstants.AdditionalRequestParams.crs.name());
            request.addExtension(new SwesExtension<SweText>().setValue(crsExtension)
                    .setIdentifier(OWSConstants.AdditionalRequestParams.crs.name()));
        }
        return request;
    }

    public String getDefaultCRS() {
        return defaultCRS;
    }

    @Setting(UVFSettingsProvider.DEFAULT_CRS_SETTING_KEY)
    public void setDefaultCRS(String defaultCRS)
            throws ConfigurationException {
        Validation.notNullOrEmpty(UVFSettingsProvider.DEFAULT_CRS_SETTING_KEY, defaultCRS);
        final int minimum = UVFConstants.MINIMUM_EPSG_CODE;
        final int maximum = UVFConstants.MAXIMUM_EPSG_CODE;
        try {
            final int newDefaultCRS = Integer.parseInt(defaultCRS);
            if (newDefaultCRS < minimum || newDefaultCRS > maximum) {
                throw new ConfigurationException(
                        String.format("Setting with key '%s': '%s' outside allowed interval " + "]%s, %s[.",
                                UVFSettingsProvider.DEFAULT_CRS_SETTING_KEY, defaultCRS, minimum, maximum));
            }
        } catch (NumberFormatException e) {
            throw new ConfigurationException(String.format("Could not parse given new default CRS EPSG code '%s'. "
                    + "Choose an integer of the interval ]%d, %d[.", defaultCRS, minimum, maximum));
        }
        this.defaultCRS = defaultCRS;
    }


    /**
     * help method to check the result format parameter. If the application/zip
     * result format is set, true is returned. If not and the value is text/xml;
     * subtype="OM" false is returned. If neither zip nor OM is set, a
     * ServiceException with InvalidParameterValue as its code is thrown.
     *
     * @param responseFormat
     *            String containing the value of the result format parameter
     * @param service The service
     * @param version the version
     *
     * @throws OwsExceptionReport
     *             * if the parameter value is incorrect
     */
    protected void checkResponseFormat(final String responseFormat, final String service, final String version)
            throws OwsExceptionReport {
        if (Strings.isNullOrEmpty(responseFormat)) {
            throw new MissingResponseFormatParameterException();
        } else {
            final Collection<String> supportedResponseFormats =
                    getResponseFormatRepository().getSupportedResponseFormats(service, version);
            if (!supportedResponseFormats.contains(responseFormat)) {
                throw new InvalidResponseFormatParameterException(responseFormat);
            }
        }
    }

    @Override
    public OwsServiceResponse modifyResponse(OwsServiceRequest request, OwsServiceResponse response)
            throws OwsExceptionReport {
        return response;
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return new RequestResponseModifierFacilitator();
    }
}
