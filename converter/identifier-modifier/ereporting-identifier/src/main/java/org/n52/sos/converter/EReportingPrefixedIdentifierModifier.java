/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.convert.RequestResponseModifierFacilitator;
import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.AqdConstants.ProcessParameter;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.om.values.HrefAttributeValue;
import org.n52.shetland.ogc.om.values.ReferenceValue;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
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
import org.n52.shetland.ogc.sos.response.AbstractObservationResponse;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.shetland.w3c.xlink.W3CHrefAttribute;
import org.n52.sos.convert.AbstractIdentifierModifier;
import org.n52.sos.converter.util.EReportingPrefixedIdentifierHelper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class EReportingPrefixedIdentifierModifier extends AbstractIdentifierModifier {

    private Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES;

    @Inject
    private EReportingPrefixedIdentifierHelper eReportingPrefixedIdentifierHelper;

    /**
     * Get the keys
     *
     * @return Set of keys
     */
    private Set<RequestResponseModifierKey> getKeyTypes() {
        Set<String> services = Sets.newHashSet(AqdConstants.AQD);
        Set<String> versions = Sets.newHashSet(AqdConstants.VERSION);
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
        services.forEach(service -> versions.forEach(version -> requestResponseMap.forEach((request, response) -> {
            keys.add(new RequestResponseModifierKey(service, version, request));
            keys.add(new RequestResponseModifierKey(service, version, request, response));
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
    protected OwsServiceResponse changeAbstractObservationResponseIdentifier(AbstractObservationResponse response) {
        return changeOmParameterValues(super.changeAbstractObservationResponseIdentifier(response));
    }

    private OwsServiceResponse changeOmParameterValues(
            OwsServiceResponse response) {
        if (response instanceof AbstractObservationResponse) {
            AbstractObservationResponse observationResponse = (AbstractObservationResponse) response;
            observationResponse.setObservationCollection(observationResponse.getObservationCollection().modify(o -> {
                checkOmParameterForEReporting(o.getParameter());
            }));
        }
        return null;
    }

    private void checkOmParameterForEReporting(Collection<NamedValue<?>> parameter) {
        Optional.ofNullable(parameter).orElseGet(Collections::emptyList).forEach(namedValue -> {
            ProcessParameter processParameter = ProcessParameter.fromOrUnknown(namedValue.getName().getHref());
            checkOmParameterFor(namedValue, processParameter);
        });
    }

    private void checkOmParameterFor(NamedValue<?> namedValue, ProcessParameter processParameter) {
        if (namedValue.getValue() instanceof ReferenceValue) {
            ReferenceType referenceType = ((ReferenceValue) namedValue.getValue()).getValue();
            referenceType.setHref(checkFor(namedValue, referenceType.getHref(), processParameter));
        } else if (namedValue.getValue() instanceof HrefAttributeValue) {
            W3CHrefAttribute hrefAttribute = ((HrefAttributeValue) namedValue.getValue()).getValue();
            hrefAttribute.setHref(checkFor(namedValue, hrefAttribute.getHref(), processParameter));
        }
    }

    private String checkFor(NamedValue<?> namedValue, String identifier, ProcessParameter processParameter) {
        switch (processParameter) {
            case SamplingPoint:
                return checkOmParameterForSamplingPoint(namedValue, identifier);
            case MonitoringStation:
                return checkOmParameterForMonitoringStation(namedValue, identifier);
            case Network:
                return checkOmParameterForNetwork(namedValue, identifier);
            default:
                return identifier;
        }
    }

    private String checkOmParameterForSamplingPoint(NamedValue<?> namedValue, String identifier) {
        if (ProcessParameter.SamplingPoint.getConceptURI().equals(namedValue.getName().getHref())) {
            if (getEReportingPrefixedIdentifierHelper().isSetSamplingPointPrefix()) {
                return checkNamespacePrefix(
                        getEReportingPrefixedIdentifierHelper().getSamplingPointPrefix() + identifier);
            }
            return checkNamespacePrefix(identifier);
        }
        return identifier;
    }

    private String checkOmParameterForMonitoringStation(NamedValue<?> namedValue, String identifier) {
        if (ProcessParameter.MonitoringStation.getConceptURI().equals(namedValue.getName().getHref())) {
            if (getEReportingPrefixedIdentifierHelper().isSetStationPrefix()) {
                return checkNamespacePrefix(getEReportingPrefixedIdentifierHelper().getStationPrefix() + identifier);
            }
            return checkNamespacePrefix(identifier);
        }
        return identifier;
    }

    private String checkOmParameterForNetwork(NamedValue<?> namedValue, String identifier) {
        if (ProcessParameter.Network.getConceptURI().equals(namedValue.getName().getHref())) {
            if (getEReportingPrefixedIdentifierHelper().isSetNetworkPrefix()) {
                return checkNamespacePrefix(getEReportingPrefixedIdentifierHelper().getNetworkPrefix() + identifier);
            }
            return checkNamespacePrefix(identifier);
        }
        return identifier;
    }

    @Override
    protected boolean checkForFlag(OwsServiceRequest request, OwsServiceResponse response)
            throws InvalidParameterValueException {
        return getEReportingPrefixedIdentifierHelper().isSetAnyPrefix();
    }

    private String checkNamespacePrefixForParameterValue(String parameterValue) {
        if (getEReportingPrefixedIdentifierHelper().isSetNamespacePrefix()) {
            return parameterValue.replace(getPrefix(), "");
        }
        return parameterValue;
    }

    private String getPrefix() {
        if (getEReportingPrefixedIdentifierHelper().isSetNamespacePrefix()) {
            StringBuilder builder = new StringBuilder();
            builder.append(getEReportingPrefixedIdentifierHelper().getNamespacePrefix());
            if (getEReportingPrefixedIdentifierHelper().getNamespacePrefix().toLowerCase(Locale.ROOT).startsWith(
                    "http") && !getEReportingPrefixedIdentifierHelper().getNamespacePrefix().endsWith("/")) {
                builder.append("/");
            } else if (getEReportingPrefixedIdentifierHelper().getNamespacePrefix().toLowerCase(Locale.ROOT)
                    .startsWith("urn")
                    && !getEReportingPrefixedIdentifierHelper().getNamespacePrefix().endsWith(":")) {
                builder.append(":");
            }
            return builder.toString();
        }
        return "";
    }

    @Override
    protected String checkOfferingParameterValue(String parameterValue) {
        String globalModified = checkNamespacePrefixForParameterValue(parameterValue);
        if (getEReportingPrefixedIdentifierHelper().isSetOfferingPrefix()) {
            return globalModified.replace(getEReportingPrefixedIdentifierHelper().getOfferingPrefix(), "");
        }
        return globalModified;
    }

    @Override
    protected String checkFeatureOfInterestParameterValue(String parameterValue) {
        String globalModified = checkNamespacePrefixForParameterValue(parameterValue);
        if (getEReportingPrefixedIdentifierHelper().isSetFeatureOfInterestPrefix()) {
            return globalModified.replace(getEReportingPrefixedIdentifierHelper().getFeatureOfInterestPrefix(), "");
        }
        return globalModified;
    }

    @Override
    protected String checkProcedureParameterValue(String parameterValue) {
        String globalModified = checkNamespacePrefixForParameterValue(parameterValue);
        if (getEReportingPrefixedIdentifierHelper().isSetProcedurePrefix()) {
            return globalModified.replace(getEReportingPrefixedIdentifierHelper().getProcedurePrefix(), "");
        }
        return globalModified;
    }

    @Override
    protected String checkObservablePropertyParameterValue(String parameterValue) {
        // String globalModified =
        // checkNamespacePrefixForParameterValue(parameterValue);
        // if
        // (getEReportingPrefixedIdentifierHelper().isSetObservablePropertyPrefix())
        // {
        // return
        // globalModified.replace(getEReportingPrefixedIdentifierHelper().getObservablePropertyPrefix(),
        // "");
        // }
        return parameterValue;
    }

    private String checkNamespacePrefix(String identifier) {
        if (getEReportingPrefixedIdentifierHelper().isSetNamespacePrefix()) {
            StringBuilder builder = new StringBuilder(getPrefix());
            builder.append(identifier);
            return builder.toString();
        }
        return identifier;
    }

    @Override
    protected String checkFeatureOfInterestIdentifier(String identifier) {
        if (getEReportingPrefixedIdentifierHelper().isSetFeatureOfInterestPrefix()) {
            return checkNamespacePrefix(
                    getEReportingPrefixedIdentifierHelper().getFeatureOfInterestPrefix() + identifier);
        }
        return checkNamespacePrefix(identifier);
    }

    @Override
    protected String checkObservablePropertyIdentifier(String identifier) {
        // if
        // (getEReportingPrefixedIdentifierHelper().isSetObservablePropertyPrefix())
        // {
        // return
        // checkNamespacePrefix(getEReportingPrefixedIdentifierHelper().getObservablePropertyPrefix()
        // + identifier);
        // }
        // return checkNamespacePrefix(identifier);
        return identifier;
    }

    @Override
    protected String checkProcedureIdentifier(String identifier) {
        if (getEReportingPrefixedIdentifierHelper().isSetProcedurePrefix()) {
            return checkNamespacePrefix(getEReportingPrefixedIdentifierHelper().getProcedurePrefix() + identifier);
        }
        return checkNamespacePrefix(identifier);
    }

    @Override
    protected ReferenceType checkProcedureIdentifier(ReferenceType procedure) {
        return new ReferenceType(checkProcedureIdentifier(procedure.getHref()));
    }

    @Override
    protected String checkOfferingIdentifier(String identifier) {
        if (getEReportingPrefixedIdentifierHelper().isSetOfferingPrefix()) {
            return checkNamespacePrefix(getEReportingPrefixedIdentifierHelper().getOfferingPrefix() + identifier);
        }
        return checkNamespacePrefix(identifier);
    }

    @Override
    protected void checkAndChangeFeatureOfInterestIdentifier(AbstractFeature abstractFeature) {
        checkAndChangeIdentifierOfAbstractFeature(abstractFeature);
    }

    private void checkAndChangeIdentifierOfAbstractFeature(AbstractFeature abstractFeature) {
        abstractFeature.setIdentifier(checkFeatureOfInterestIdentifier(abstractFeature.getIdentifier()));
        if (abstractFeature instanceof AbstractSamplingFeature
                && ((AbstractSamplingFeature) abstractFeature).isSetXml()) {
            abstractFeature.setXml(null);
        }
    }

    @Override
    protected void checkAndChangeProcedureIdentifier(AbstractFeature abstractFeature) {
        abstractFeature.setIdentifier(checkProcedureIdentifier(abstractFeature.getIdentifier()));
    }

    @Override
    protected void checkAndChangeObservablePropertyIdentifier(AbstractFeature abstractFeature) {
        abstractFeature.setIdentifier(checkObservablePropertyIdentifier(abstractFeature.getIdentifier()));
    }

    @Override
    protected void checkAndChangOfferingIdentifier(SosOffering offering) {
        if (offering != null) {
            offering.setIdentifier(checkOfferingIdentifier(offering.getIdentifier()));
        }
    }

    protected EReportingPrefixedIdentifierHelper getEReportingPrefixedIdentifierHelper() {
        return eReportingPrefixedIdentifierHelper;
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return super.getFacilitator();
    }

}
