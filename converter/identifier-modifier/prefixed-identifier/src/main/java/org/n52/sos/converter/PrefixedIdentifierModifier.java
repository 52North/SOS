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
package org.n52.sos.converter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.convert.AbstractIdentifierModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.converter.util.PrefixedIdentifierHelper;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.gda.GetDataAvailabilityRequest;
import org.n52.sos.gda.GetDataAvailabilityResponse;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.GetResultRequest;
import org.n52.sos.request.GetResultTemplateRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.response.GetObservationByIdResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.response.GetResultResponse;
import org.n52.sos.response.GetResultTemplateResponse;
import org.n52.sos.util.Constants;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PrefixedIdentifierModifier extends AbstractIdentifierModifier {
 // TODO add this class to org.n52.sos.convert.RequestResponseModifier file

    private Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES;

    /**
     * Get the keys
     * 
     * @return Set of keys
     */
    private Set<RequestResponseModifierKeyType> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<AbstractServiceRequest<?>, AbstractServiceResponse> requestResponseMap = Maps.newHashMap();
        requestResponseMap.put(new GetCapabilitiesRequest(), new GetCapabilitiesResponse());
        requestResponseMap.put(new GetObservationRequest(), new GetObservationResponse());
        requestResponseMap.put(new GetObservationByIdRequest(), new GetObservationByIdResponse());
        requestResponseMap.put(new GetFeatureOfInterestRequest(), new GetFeatureOfInterestResponse());
        requestResponseMap.put(new DescribeSensorRequest(), new DescribeSensorResponse());
        requestResponseMap.put(new GetDataAvailabilityRequest(), new GetDataAvailabilityResponse());
        requestResponseMap.put(new GetResultTemplateRequest(), new GetResultTemplateResponse());
        requestResponseMap.put(new GetResultRequest(), new GetResultResponse());
        Set<RequestResponseModifierKeyType> keys = Sets.newHashSet();
        for (String service : services) {
            for (String version : versions) {
                for (AbstractServiceRequest<?> request : requestResponseMap.keySet()) {
                    keys.add(new RequestResponseModifierKeyType(service, version, request));
                    keys.add(new RequestResponseModifierKeyType(service, version, request, requestResponseMap
                            .get(request)));
                }
            }
        }
        return keys;
    }

    @Override
    public Set<RequestResponseModifierKeyType> getRequestResponseModifierKeyTypes() {
        if (REQUEST_RESPONSE_MODIFIER_KEY_TYPES == null) {
            REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();
        }
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    protected boolean checkForFlag(AbstractServiceRequest<?> request, AbstractServiceResponse response)
            throws InvalidParameterValueException {
        return getPrefixedIdentifierHelper().isSetAnyPrefix();
    }

    private String checkGlobalPrefixForParameterValue(String parameterValue) {
        if (getPrefixedIdentifierHelper().isSetGlobalPrefix()) {
            StringBuilder builder = new StringBuilder();
            builder.append(getPrefixedIdentifierHelper().getGlobalPrefix());
            if (getPrefixedIdentifierHelper().getGlobalPrefix().toLowerCase().startsWith("http") && !getPrefixedIdentifierHelper().getGlobalPrefix().endsWith(Constants.SLASH_STRING)) {
                builder.append(Constants.SLASH_STRING);
            } else if (getPrefixedIdentifierHelper().getGlobalPrefix().toLowerCase().startsWith("urn") && !getPrefixedIdentifierHelper().getGlobalPrefix().endsWith(Constants.COLON_STRING)) {
                builder.append(Constants.COLON_STRING) ;
            }
            return parameterValue.replace(builder.toString(), "");
        }
        return parameterValue;
    }

    @Override
    protected String checkOfferingParameterValue(String parameterValue) {
        String globalModified = checkGlobalPrefixForParameterValue(parameterValue);
        if (getPrefixedIdentifierHelper().isSetOfferingPrefix()) {
            return globalModified.replace(getPrefixedIdentifierHelper().getOfferingPrefix(), "");
        }
        return globalModified;
    }

    @Override
    protected String checkFeatureOfInterestParameterValue(String parameterValue) {
        String globalModified = checkGlobalPrefixForParameterValue(parameterValue);
        if (getPrefixedIdentifierHelper().isSetFeatureOfInterestPrefix()) {
            return globalModified.replace(getPrefixedIdentifierHelper().getFeatureOfInterestPrefix(), "");
        }
        return globalModified;
    }

    @Override
    protected String checkProcedureParameterValue(String parameterValue) {
        String globalModified = checkGlobalPrefixForParameterValue(parameterValue);
        if (getPrefixedIdentifierHelper().isSetProcedurePrefix()) {
            return globalModified.replace(getPrefixedIdentifierHelper().getProcedurePrefix(), "");
        }
        return globalModified;
    }

    @Override
    protected String checkObservablePropertyParameterValue(String parameterValue) {
        String globalModified = checkGlobalPrefixForParameterValue(parameterValue);
        if (getPrefixedIdentifierHelper().isSetObservablePropertyPrefix()) {
            return globalModified.replace(getPrefixedIdentifierHelper().getObservablePropertyPrefix(), "");
        }
        return globalModified;
    }
    
    private String checkGlobalPrefix(String identifier) {
        if (getPrefixedIdentifierHelper().isSetGlobalPrefix()) {
               StringBuilder builder = new StringBuilder();
               builder.append(getPrefixedIdentifierHelper().getGlobalPrefix());
               if (getPrefixedIdentifierHelper().getGlobalPrefix().toLowerCase().startsWith("http") && !getPrefixedIdentifierHelper().getGlobalPrefix().endsWith(Constants.SLASH_STRING)) {
                   builder.append(Constants.SLASH_STRING);
               } else if (getPrefixedIdentifierHelper().getGlobalPrefix().toLowerCase().startsWith("urn") && !getPrefixedIdentifierHelper().getGlobalPrefix().endsWith(Constants.COLON_STRING)) {
                   builder.append(Constants.COLON_STRING) ;
               }
               builder.append(identifier);
               return builder.toString();
        }
        return identifier;
    }

    @Override
    protected String checkFeatureOfInterestIdentifier(String identifier) {
        if (getPrefixedIdentifierHelper().isSetFeatureOfInterestPrefix()) {
            checkGlobalPrefix(getPrefixedIdentifierHelper().getFeatureOfInterestPrefix() + identifier);
        }
        return checkGlobalPrefix(identifier);
    }

    @Override
    protected String checkObservablePropertyIdentifier(String identifier) {
        if (getPrefixedIdentifierHelper().isSetObservablePropertyPrefix()) {
            checkGlobalPrefix(getPrefixedIdentifierHelper().getObservablePropertyPrefix() + identifier);
        }
        return checkGlobalPrefix(identifier);
    }

    @Override
    protected String checkProcedureIdentifier(String identifier) {
        if (getPrefixedIdentifierHelper().isSetProcedurePrefix()) {
            checkGlobalPrefix(getPrefixedIdentifierHelper().getProcedurePrefix() + identifier);
        }
        return checkGlobalPrefix(identifier);
    }

    @Override
    protected String checkOfferingIdentifier(String identifier) {
        if (getPrefixedIdentifierHelper().isSetOfferingPrefix()) {
            checkGlobalPrefix(getPrefixedIdentifierHelper().getOfferingPrefix() + identifier);
        }
        return checkGlobalPrefix(identifier);
    }

    @Override
    protected void checkAndChangeFeatureOfInterestIdentifier(AbstractFeature abstractFeature) {
        if (getPrefixedIdentifierHelper().isSetFeatureOfInterestPrefix()) {
            checkAndChangeIdentifierOfAbstractFeature(abstractFeature);
        }
    }
    
    private void checkAndChangeIdentifierOfAbstractFeature(AbstractFeature abstractFeature) {
        if (getPrefixedIdentifierHelper().isSetFeatureOfInterestPrefix()) {
            abstractFeature.setIdentifier(checkFeatureOfInterestIdentifier(abstractFeature
                    .getIdentifier()));
        }
        if (abstractFeature instanceof SamplingFeature && ((SamplingFeature) abstractFeature).isSetXmlDescription()) {
            ((SamplingFeature) abstractFeature).setXmlDescription(null);
        }

    }

    @Override
    protected void checkAndChangeProcedureIdentifier(AbstractFeature abstractFeature) {
        if (getPrefixedIdentifierHelper().isSetProcedurePrefix()) {
            if (!abstractFeature.isSetHumanReadableIdentifier()) {
                abstractFeature.setIdentifier(checkProcedureIdentifier(abstractFeature.getIdentifier()));
            }
        }
    }

    @Override
    protected void checkAndChangeObservablePropertyIdentifier(AbstractFeature abstractFeature) {
        if (getPrefixedIdentifierHelper().isSetObservablePropertyPrefix()) {
            if (!abstractFeature.isSetHumanReadableIdentifier()) {
                abstractFeature.setIdentifier(checkObservablePropertyIdentifier(abstractFeature
                        .getIdentifier()));
            }
        }
    }

    @Override
    protected void checkAndChangOfferingIdentifier(SosOffering offering) {
        if (offering != null && getPrefixedIdentifierHelper().isSetOfferingPrefix()) {
            offering.setIdentifier(checkOfferingIdentifier(offering.getIdentifier()));
        }
    }

    protected PrefixedIdentifierHelper getPrefixedIdentifierHelper() {
        return PrefixedIdentifierHelper.getInstance();
    }
    
    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return super.getFacilitator();
    }
}
