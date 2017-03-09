/**
 * ﻿Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.converter;

import java.util.Collections;
import java.util.Set;

import org.n52.schetland.uvf.UVFConstants;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.convert.RequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.Validation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
@Configurable
public class UVFRequestModifier implements RequestResponseModifier<GetObservationRequest, AbstractServiceResponse>, SettingDefinitionProvider {

    private static final String DEFAULT_CRS_SETTING_KEY = "uvf.default.crs";

    private static final Set<SettingDefinition<?, ?>> DEFAULT_CRS_SETTING_DEFINITION = ImmutableSet.<SettingDefinition<?,?>>of(
            new IntegerSettingDefinition()
            .setGroup(org.n52.sos.service.MiscSettings.GROUP)
            .setOrder(66)
            .setKey(DEFAULT_CRS_SETTING_KEY)
            .setMinimum(31466)
            .setMaximum(31469)
            .setDefaultValue(31466)
            .setTitle("The default CRS EPSG code used in UVF response")
            .setDescription(String.format("The default CRS EPSG code that is used if no swe extension is present in "
                    + "the request that specifies one. Allowed values are: %s", UVFConstants.ALLOWED_CRS)));

    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = Sets.newHashSet(
            new RequestResponseModifierKeyType(
                    SosConstants.SOS,
                    Sos2Constants.SERVICEVERSION,
                    new GetObservationRequest()));

    private int defaultCRS;
    
    @Override
    public Set<RequestResponseModifierKeyType> getRequestResponseModifierKeyTypes() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public GetObservationRequest modifyRequest(GetObservationRequest request) throws OwsExceptionReport {
        if (request.getRequestContext().getAcceptType().isPresent() && 
                request.getRequestContext().getAcceptType().get().contains(UVFConstants.CONTENT_TYPE_UVF)) {
            if (request.isSetFeatureOfInterest() && request.getFeatureIdentifiers().size() == 1 &&
                    request.isSetObservableProperty() && request.getObservedProperties().size() == 1 &&
                    request.isSetProcedure() && request.getProcedures().size() == 1) {
                if (request.hasExtension(OWSConstants.AdditionalRequestParams.crs) &&
                        // TODO Switch back to SweText
                        // Check coordinate modifier for methode to get the epsg code from the request
                        request.getExtension(OWSConstants.AdditionalRequestParams.crs).getValue() instanceof SweCount) {
                    int requestedCRS = ((SweCount)request.getExtension(OWSConstants.AdditionalRequestParams.crs).getValue()).getValue();
                    if (UVFConstants.ALLOWED_CRS.contains(requestedCRS)) {
                        return request;
                    } else {
                        throw new NoApplicableCodeException().withMessage("When requesting UVF format, the request MUST have "
                                + "a CRS of the German GK bands, e.g. '%s'. Requested was: '%s'.",
                                UVFConstants.ALLOWED_CRS.toString(), requestedCRS);
                    }
                }
                // TODO switch back to swe text
                // add default CRS as swe text extension
                SweCount crsExtension = (SweCount) new SweCount()
                        .setValue(getDefaultCRS())
                        .setIdentifier(OWSConstants.AdditionalRequestParams.crs.name());
                request.addExtension(new SwesExtensionImpl<SweCount>().setValue(crsExtension));
                return request;
            } else {
                throw new NoApplicableCodeException().withMessage("When requesting UVF format, the request MUST have "
                        + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");
            }
        }
        return request;
    }

    public int getDefaultCRS() {
        return defaultCRS;
    }
    
    @Setting(DEFAULT_CRS_SETTING_KEY)
    public void setDefaultCRS(int defaultCRS) {
        Validation.greaterZero(DEFAULT_CRS_SETTING_KEY, defaultCRS);
        final IntegerSettingDefinition settingDefinition = (IntegerSettingDefinition)DEFAULT_CRS_SETTING_DEFINITION.iterator().next();
        final int minimum = settingDefinition.getMinimum();
        final int maximum = settingDefinition.getMaximum();
        if (defaultCRS < minimum || defaultCRS > maximum) {
            throw new ConfigurationException(String.format("Setting with key '%s': '%d' outside allowed interval ]%d, %d[.",
                    DEFAULT_CRS_SETTING_KEY, defaultCRS, minimum, maximum));
        }
        this.defaultCRS = defaultCRS;
    }


    @Override
    public AbstractServiceResponse modifyResponse(GetObservationRequest request, AbstractServiceResponse response)
            throws OwsExceptionReport {
        return response;
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return new RequestResponseModifierFacilitator();
    }

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return DEFAULT_CRS_SETTING_DEFINITION;
    }

}
