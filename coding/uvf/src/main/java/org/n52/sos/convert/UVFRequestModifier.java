/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import java.util.Collections;
import java.util.Set;

import org.n52.schetland.uvf.UVFConstants;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.convert.RequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.Validation;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.uvf.UVFSettings;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
@Configurable
public class UVFRequestModifier implements RequestResponseModifier<AbstractObservationRequest, AbstractServiceResponse> {


    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = Sets.newHashSet(
            new RequestResponseModifierKeyType(
                    SosConstants.SOS,
                    Sos2Constants.SERVICEVERSION,
                    new GetObservationRequest()),
            new RequestResponseModifierKeyType(
                    SosConstants.SOS,
                    Sos2Constants.SERVICEVERSION,
                    new GetObservationByIdRequest()));

    private String defaultCRS;
    
    @Override
    public Set<RequestResponseModifierKeyType> getRequestResponseModifierKeyTypes() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public AbstractObservationRequest modifyRequest(AbstractObservationRequest request) throws OwsExceptionReport {
        // check for response format to avoid incomprehensible exception
        if (request.isSetResponseFormat()) {
            SosHelper.checkResponseFormat(request.getResponseFormat(), request.getService(),
                    request.getVersion());
        }
        if ((request.getRequestContext().getAcceptType().isPresent()
                && request.getRequestContext().getAcceptType().get().contains(UVFConstants.CONTENT_TYPE_UVF))
                || (request.isSetResponseFormat() 
                        && request.getResponseFormat().contains(UVFConstants.CONTENT_TYPE_UVF.getSubtype())
                        && UVFConstants.CONTENT_TYPE_UVF.isCompatible(MediaType.parse(request.getResponseFormat())))) {
            if (request.hasExtension(OWSConstants.AdditionalRequestParams.crs)
                    && request.getExtension(OWSConstants.AdditionalRequestParams.crs).getValue() instanceof SweText) {
                String requestedCRS =
                        ((SweText) request.getExtension(OWSConstants.AdditionalRequestParams.crs).getValue())
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
            request.addExtension(new SwesExtensionImpl<SweText>().setValue(crsExtension));
        }
        return request;
    }

    public String getDefaultCRS() {
        return defaultCRS;
    }
    
    @Setting(UVFSettings.DEFAULT_CRS_SETTING_KEY)
    public void setDefaultCRS(String defaultCRS) {
        Validation.notNullOrEmpty(UVFSettings.DEFAULT_CRS_SETTING_KEY, defaultCRS);
        final int minimum = UVFConstants.MINIMUM_EPSG_CODE;
        final int maximum = UVFConstants.MAXIMUM_EPSG_CODE;
        try {
            final int newDefaultCRS = Integer.parseInt(defaultCRS);
            if (newDefaultCRS < minimum || newDefaultCRS > maximum) {
                throw new ConfigurationException(String.format("Setting with key '%s': '%s' outside allowed interval "
                        + "]%s, %s[.",
                        UVFSettings.DEFAULT_CRS_SETTING_KEY, defaultCRS, minimum, maximum));
            }
        } catch (NumberFormatException e) {
            throw new ConfigurationException(String.format("Could not parse given new default CRS EPSG code '%s'. "
                    + "Choose an integer of the interval ]%d, %d[.",
                    defaultCRS, minimum, maximum));
        }
        this.defaultCRS = defaultCRS;
    }


    @Override
    public AbstractServiceResponse modifyResponse(AbstractObservationRequest request, AbstractServiceResponse response)
            throws OwsExceptionReport {
        return response;
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return new RequestResponseModifierFacilitator();
    }
}
