/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.convert.RequestResponseModifier;
import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
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
import org.n52.sos.convert.AbstractIdentifierModifier;
import org.n52.sos.converter.util.PrefixedIdentifierHelper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * {@link RequestResponseModifier} to dynamically added/removed to/from the
 * identifier in the responses/requests.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class PrefixedIdentifierModifier extends AbstractIdentifierModifier {

    private Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES;


    private PrefixedIdentifierHelper helper;

    @Inject
    public void setHelper(PrefixedIdentifierHelper helper) {
        this.helper = helper;
    }

    /**
     * Get the keys
     *
     * @return Set of keys
     */
    private Set<RequestResponseModifierKey> getKeyTypes() {
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

        services.stream().forEach(service -> versions.stream()
                .forEach(version -> requestResponseMap.keySet().stream().forEach(request -> {
                    keys.add(new RequestResponseModifierKey(service, version, request));
                    keys.add(new RequestResponseModifierKey(service, version, request,
                            requestResponseMap.get(request)));
                })));
        return keys;
    }

    @Override
    public Set<RequestResponseModifierKey> getKeys() {
        if (REQUEST_RESPONSE_MODIFIER_KEY_TYPES == null) {
            REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();
        }
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    protected boolean checkForFlag(OwsServiceRequest request, OwsServiceResponse response)
            throws InvalidParameterValueException {
        return this.helper.isSetAnyPrefix();
    }

    private String checkGlobalPrefixForParameterValue(String parameterValue) {
        if (this.helper.isSetGlobalPrefix()) {
            return parameterValue.replace(getGlobalPrefix(), "");
        }
        return parameterValue;
    }

    private String getGlobalPrefix() {
        if (this.helper.isSetGlobalPrefix()) {
            StringBuilder builder = new StringBuilder();
            String globalPrefix = this.helper.getGlobalPrefix();
            builder.append(globalPrefix);
            if (globalPrefix.toLowerCase(Locale.ROOT).startsWith("http") && !globalPrefix.endsWith("/")) {
                builder.append('/');
            } else if (globalPrefix.toLowerCase(Locale.ROOT).startsWith("urn") && !globalPrefix.endsWith("/")) {
                builder.append(':');
            }
            return builder.toString();
        }
        return "";
    }

    @Override
    protected String checkOfferingParameterValue(String parameterValue) {
        String globalModified = checkGlobalPrefixForParameterValue(parameterValue);
        if (this.helper.isSetOfferingPrefix()) {
            return globalModified.replace(this.helper.getOfferingPrefix(), "");
        }
        return globalModified;
    }

    @Override
    protected String checkFeatureOfInterestParameterValue(String parameterValue) {
        String globalModified = checkGlobalPrefixForParameterValue(parameterValue);
        if (helper.isSetFeatureOfInterestPrefix()) {
            return globalModified.replace(helper.getFeatureOfInterestPrefix(), "");
        }
        return globalModified;
    }



    @Override
    protected String checkObservablePropertyParameterValue(String parameterValue) {
        String globalModified = checkGlobalPrefixForParameterValue(parameterValue);
        return helper.isSetObservablePropertyPrefix()
                ? globalModified.replace(helper.getObservablePropertyPrefix(), "")
                : globalModified;
    }

    @Override
    protected String checkProcedureParameterValue(String parameterValue) {
        String globalModified = checkGlobalPrefixForParameterValue(parameterValue);
        return helper.isSetProcedurePrefix() ? globalModified.replace(helper.getProcedurePrefix(), "")
                : globalModified;
    }

    @Override
    protected ReferenceType checkProcedureIdentifier(ReferenceType procedure) {
        return new ReferenceType(checkProcedureIdentifier(procedure.getHref()));
    }

    @Override
    protected String checkProcedureIdentifier(String identifier) {
        return helper.isSetProcedurePrefix() ? checkGlobalPrefix(helper.getProcedurePrefix() + identifier)
                : checkGlobalPrefix(identifier);
    }

    @Override
    protected String checkFeatureOfInterestIdentifier(String identifier) {
        return helper.isSetFeatureOfInterestPrefix()
                ? checkGlobalPrefix(helper.getFeatureOfInterestPrefix() + identifier)
                : checkGlobalPrefix(identifier);
    }

    @Override
    protected String checkObservablePropertyIdentifier(String identifier) {
        return helper.isSetObservablePropertyPrefix()
                ? checkGlobalPrefix(helper.getObservablePropertyPrefix() + identifier)
                : checkGlobalPrefix(identifier);
    }

    @Override
    protected String checkOfferingIdentifier(String identifier) {
        return helper.isSetOfferingPrefix() ? checkGlobalPrefix(helper.getOfferingPrefix() + identifier)
                : checkGlobalPrefix(identifier);
    }

    private String checkGlobalPrefix(String identifier) {
        if (helper.isSetGlobalPrefix()) {
            StringBuilder builder = new StringBuilder(getGlobalPrefix());
            builder.append(identifier);
            return builder.toString();
        }
        return identifier;
    }

    @Override
    protected void checkAndChangeFeatureOfInterestIdentifier(AbstractFeature abstractFeature) {
        if (helper.isSetFeatureOfInterestPrefix()) {
            checkAndChangeIdentifierOfAbstractFeature(abstractFeature);
        }
    }

    private void checkAndChangeIdentifierOfAbstractFeature(AbstractFeature abstractFeature) {
        if (helper.isSetFeatureOfInterestPrefix()) {
            abstractFeature.setIdentifier(checkFeatureOfInterestIdentifier(abstractFeature
                    .getIdentifier()));
        }
        if (abstractFeature.isSetXml()) {
            abstractFeature.setXml(null);
        }
    }

    @Override
    protected void checkAndChangeProcedureIdentifier(AbstractFeature abstractFeature) {
        if (helper.isSetProcedurePrefix()) {
            if (!abstractFeature.isSetHumanReadableIdentifier()) {
                abstractFeature.setIdentifier(checkProcedureIdentifier(abstractFeature.getIdentifier()));
            }
        }
    }

    @Override
    protected void checkAndChangeObservablePropertyIdentifier(AbstractFeature abstractFeature) {
        if (helper.isSetObservablePropertyPrefix()) {
            if (!abstractFeature.isSetHumanReadableIdentifier()) {
                abstractFeature.setIdentifier(checkObservablePropertyIdentifier(abstractFeature
                        .getIdentifier()));
            }
        }
    }

    @Override
    protected void checkAndChangOfferingIdentifier(SosOffering offering) {
        if (offering != null && helper.isSetOfferingPrefix()) {
            offering.setIdentifier(checkOfferingIdentifier(offering.getIdentifier()));
        }
    }

}
