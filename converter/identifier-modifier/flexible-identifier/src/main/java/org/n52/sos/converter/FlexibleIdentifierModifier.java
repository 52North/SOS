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
package org.n52.sos.converter;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.convert.RequestResponseModifierFacilitator;
import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.sos.convert.AbstractIdentifierModifier;
import org.n52.sos.converter.util.FlexibleIdentifierHelper;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Modifier for flexible identifier.
 *
 * If the requests contain flexible identifier, the identifier would be replaced
 * with the default identifier.
 *
 * If the global setting for flexible identifier is enabled or the request
 * contains the flexible identifier flag, the identifiers in the responses would
 * be replaced with the flexible identifier.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public class FlexibleIdentifierModifier extends AbstractIdentifierModifier {

    private static final Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

    @Inject
    private FlexibleIdentifierHelper flexibleIdentifierHelper;

    /**
     * Get the keys
     *
     * @return Set of keys
     */
    private static Set<RequestResponseModifierKey> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<OwsServiceRequest, OwsServiceResponse> requestResponseMap = Maps.newHashMap();
        requestResponseMap.put(new GetCapabilitiesRequest(SosConstants.SOS), new GetCapabilitiesResponse());
        requestResponseMap.put(new GetObservationRequest(), new GetObservationResponse());
        requestResponseMap.put(new GetObservationByIdRequest(), new GetObservationByIdResponse());
        requestResponseMap.put(new GetFeatureOfInterestRequest(), new GetFeatureOfInterestResponse());
        requestResponseMap.put(new DescribeSensorRequest(), new DescribeSensorResponse());
        requestResponseMap.put(new GetDataAvailabilityRequest(), new GetDataAvailabilityResponse());
        requestResponseMap.put(new GetResultTemplateRequest(), new GetResultTemplateResponse());
        requestResponseMap.put(new GetResultRequest(), new GetResultResponse());
        Set<RequestResponseModifierKey> keys = Sets.newHashSet();
        for (String service : services) {
            for (String version : versions) {
                for (Entry<OwsServiceRequest, OwsServiceResponse> entry : requestResponseMap.entrySet()) {
                    keys.add(new RequestResponseModifierKey(service, version, entry.getKey()));
                    keys.add(new RequestResponseModifierKey(service, version, entry.getKey(), requestResponseMap
                            .get(entry.getKey())));
                }
            }
        }
        return keys;
    }

    @Override
    public Set<RequestResponseModifierKey> getKeys() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    protected boolean checkForFlag(OwsServiceRequest request,
            OwsServiceResponse response) throws InvalidParameterValueException {
        if (getFlexibleIdentifierHelper()
                .checkIsReturnHumanReadableIdentifierFlagExtensionSet(request.getExtensions())
                || getFlexibleIdentifierHelper().checkIsReturnHumanReadableIdentifierFlagExtensionSet(
                        response.getExtensions())) {
            return checkResponseForReturnHumanReadableIdentifierFlag(response)
                    || checkRequestForReturnHumanReadableIdentifierFlag(request);
        }
        return getFlexibleIdentifierHelper().isSetReturnHumanReadableIdentifier();
    }



    private boolean checkResponseForReturnHumanReadableIdentifierFlag(OwsServiceResponse response)
            throws InvalidParameterValueException {
        return getFlexibleIdentifierHelper().checkForReturnHumanReadableIdentifierFlagExtension(
                response.getExtensions());
    }

    private boolean checkRequestForReturnHumanReadableIdentifierFlag(OwsServiceRequest request)
            throws InvalidParameterValueException {
        return getFlexibleIdentifierHelper().checkForReturnHumanReadableIdentifierFlagExtension(
                request.getExtensions());
    }

    @Override
    protected String checkOfferingParameterValue(String parameterValue) {
        return getCache().getOfferingIdentifierForHumanReadableName(parameterValue);
    }

    @Override
    protected String checkFeatureOfInterestParameterValue(String parameterValue) {
        return getCache().getFeatureOfInterestIdentifierForHumanReadableName(parameterValue);
    }

    @Override
    protected String checkObservablePropertyParameterValue(String parameterValue) {
        return getCache().getObservablePropertyIdentifierForHumanReadableName(parameterValue);
    }

    @Override
    protected String checkProcedureParameterValue(String parameterValue) {
        return getCache().getProcedureIdentifierForHumanReadableName(parameterValue);
    }

    @Override
    protected String checkFeatureOfInterestIdentifier(String identifier) {
        if (getFlexibleIdentifierHelper().isSetIncludeFeatureOfInterest()) {
            return getCache().getFeatureOfInterestHumanReadableNameForIdentifier(identifier);
        }
        return identifier;
    }

    @Override
    protected String checkObservablePropertyIdentifier(String identifier) {
        if (getFlexibleIdentifierHelper().isSetIncludeObservableProperty()) {
            return getCache().getObservablePropertyHumanReadableNameForIdentifier(identifier);
        }
        return identifier;
    }


    @Override
    protected String checkProcedureIdentifier(String identifier) {
        if (getFlexibleIdentifierHelper().isSetIncludeProcedure()) {
            return getCache().getProcedureHumanReadableNameForIdentifier(identifier);
        }
        return identifier;
    }

    @Override
    protected ReferenceType checkProcedureIdentifier(ReferenceType procedure) {
        return new ReferenceType(checkProcedureIdentifier(procedure.getHref()));
    }

    @Override
    protected String checkOfferingIdentifier(String identifier) {
        if (getFlexibleIdentifierHelper().isSetIncludeOffering()) {
            return getCache().getOfferingHumanReadableNameForIdentifier(identifier);
        }
        return identifier;
    }

    @Override
    protected void checkAndChangeFeatureOfInterestIdentifier(AbstractFeature abstractFeature) {
        if (getFlexibleIdentifierHelper().isSetIncludeFeatureOfInterest()) {
            checkAndChangeIdentifierOfAbstractFeature(abstractFeature);
        }
    }

    @Override
    protected void checkAndChangeProcedureIdentifier(AbstractFeature abstractFeature) {
        if (getFlexibleIdentifierHelper().isSetIncludeProcedure()) {
            if (!abstractFeature.isSetHumanReadableIdentifier()) {
                abstractFeature.setHumanReadableIdentifier(checkProcedureIdentifier(abstractFeature.getIdentifier()));
            }
            abstractFeature.setHumanReadableIdentifierAsIdentifier();
        }
    }

    @Override
    protected void checkAndChangeObservablePropertyIdentifier(AbstractFeature abstractFeature) {
        if (getFlexibleIdentifierHelper().isSetIncludeObservableProperty()) {
            if (!abstractFeature.isSetHumanReadableIdentifier()) {
                abstractFeature.setHumanReadableIdentifier(checkObservablePropertyIdentifier(abstractFeature
                        .getIdentifier()));
            }
            abstractFeature.setHumanReadableIdentifierAsIdentifier();
        }
    }

    @Override
    protected void checkAndChangOfferingIdentifier(SosOffering offering) {
        if (offering != null && getFlexibleIdentifierHelper().isSetIncludeOffering()) {
            if (!offering.isSetHumanReadableIdentifier()) {
                offering.setHumanReadableIdentifier(checkOfferingIdentifier(offering.getIdentifier()));
            }
            offering.setHumanReadableIdentifierAsIdentifier();
        }
    }

    private void checkAndChangeIdentifierOfAbstractFeature(AbstractFeature abstractFeature) {
        if (!abstractFeature.isSetHumanReadableIdentifier()) {
            abstractFeature.setHumanReadableIdentifier(checkFeatureOfInterestIdentifier(abstractFeature
                    .getIdentifier()));
        }
        abstractFeature.setHumanReadableIdentifierAsIdentifier();
        if (abstractFeature.isSetXml()) {
            abstractFeature.setXml(null);
        }

    }

    protected FlexibleIdentifierHelper getFlexibleIdentifierHelper() {
        return flexibleIdentifierHelper;
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return super.getFacilitator().setAdderRemover(false);
    }

}
