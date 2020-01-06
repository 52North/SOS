/*
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
package org.n52.sos.request.operator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.exception.ResponseExceedsSizeLimitException;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.OMHelper;
import org.n52.sos.ds.AbstractGetObservationHandler;
import org.n52.sos.exception.ows.concrete.InvalidObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.InvalidResponseFormatParameterException;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingResponseFormatParameterException;
import org.n52.sos.util.SosHelper;
import org.opengis.parameter.InvalidParameterCardinalityException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * class and forwards requests to the GetObservationDAO; after query of
 * Database, class encodes the ObservationResponse (thru using the IOMEncoder)
 *
 * @since 4.0.0
 */
public class SosGetObservationOperatorV100 extends
        AbstractV1RequestOperator<AbstractGetObservationHandler, GetObservationRequest, GetObservationResponse> {

    private static final String OPERATION_NAME = SosConstants.Operations.GetObservation.name();

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton("http://www.opengis.net/spec/SOS/1.0/conf/core");

    private static final TemporalFilter TEMPORAL_FILTER_LATEST =
            new TemporalFilter(FilterConstants.TimeOperator.TM_Equals,
                    new TimeInstant(ExtendedIndeterminateTime.LATEST), "phenomenonTime");

    public SosGetObservationOperatorV100() {
        super(OPERATION_NAME, GetObservationRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos1Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public GetObservationResponse receive(GetObservationRequest sosRequest) throws OwsExceptionReport {
        GetObservationResponse sosResponse = getOperationHandler().getObservation(sosRequest);
        if (sosRequest.isSetResponseFormat()) {
            setObservationResponseResponseFormatAndContentType(sosRequest, sosResponse);
        }
        return sosResponse;
    }

    @Override
    protected void checkParameters(GetObservationRequest sosRequest) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(sosRequest.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(sosRequest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        // check if parameters are set, if not throw ResponseExceedsSizeLimit
        // exception
        checkQueryParametersIfAllEmpty(sosRequest);

        try {
            checkResponseFormat(sosRequest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        try {
            sosRequest.setOfferings(checkOfferingId(sosRequest.getOfferings()));
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkObservedProperties(sosRequest.getObservedProperties());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedures(sosRequest.getProcedures(), SosConstants.GetObservationParams.procedure.name());
            // add child procedures to request
            if (sosRequest.isSetProcedure()) {
                sosRequest.setProcedures(addChildProcedures(sosRequest.getProcedures()));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkFeatureOfInterestIdentifiers(sosRequest.getFeatureIdentifiers(),
                    SosConstants.GetObservationParams.featureOfInterest.name());
            if (sosRequest.isSetFeatureOfInterest()) {
                sosRequest.setFeatureIdentifiers(addChildFeatures(sosRequest.getFeatureIdentifiers()));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSpatialFilter(sosRequest.getSpatialFilter(),
                    SosConstants.GetObservationParams.featureOfInterest.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            if (sosRequest.getTemporalFilters() != null && !sosRequest.getTemporalFilters().isEmpty()) {
                checkTemporalFilter(sosRequest.getTemporalFilters(),
                        Sos1Constants.GetObservationParams.eventTime.name());
            } else if (getActiveProfile().isReturnLatestValueIfTemporalFilterIsMissingInGetObservation()) {
                sosRequest.setTemporalFilters(CollectionHelper.list(TEMPORAL_FILTER_LATEST));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        if (sosRequest.isSetResultModel()
                && sosRequest.getOfferings().stream().map(getCache()::getObservationTypesForOffering)
                        .anyMatch(observationTypes -> !observationTypes.contains(sosRequest.getResultModel()))) {
            exceptions.add(new InvalidParameterValueException().at(Sos1Constants.GetObservationParams.resultModel)
                    .withMessage("The value '%s' is invalid for the requested offering!",
                            OMHelper.getEncodedResultModelFor(sosRequest.getResultModel())));
        }
        exceptions.throwIfNotEmpty();
    }

    /**
     * checks if mandatory parameter observed property is correct
     *
     * @param observedProperties
     *            List containing the observed properties of the request
     *
     * @throws OwsExceptionReport
     *             if the parameter does not containing any matching
     *             observedProperty for the requested offering
     */
    private void checkObservedProperties(List<String> observedProperties) throws OwsExceptionReport {
        if (observedProperties != null) {
            CompositeOwsException exceptions = new CompositeOwsException();

            if (observedProperties.isEmpty()) {
                throw new MissingObservedPropertyParameterException();
            }
            Collection<String> validObservedProperties =
                    getCache().getObservableProperties();
            for (String obsProp : observedProperties) {
                if (obsProp.isEmpty()) {
                    throw new MissingObservedPropertyParameterException();
                } else {
                    if (!validObservedProperties.contains(obsProp)) {
                        throw new InvalidObservedPropertyParameterException(obsProp);
                    }
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    /**
     * checks if the passed offeringId is supported
     *
     * @param offeringIds
     *            the offeringIds to be checked
     * @return the checked offering ids
     *
     * @throws OwsExceptionReport
     *             if the passed offeringId is not supported
     */
    // one / mandatory
    private List<String> checkOfferingId(List<String> offeringIds) throws OwsExceptionReport {
        List<String> validOfferings = Lists.newLinkedList();
        if (offeringIds != null) {

            Set<String> offerings = getCache().getOfferings();
            Map<String, String> ncOfferings = SosHelper.getNcNameResolvedOfferings(offerings);
            CompositeOwsException exceptions = new CompositeOwsException();

            //SOS 1.0 GetObservation requires exactly one offering
            if (offeringIds.isEmpty()) {
                throw new MissingOfferingParameterException();
            } else if (offeringIds.size() > 1) {
                throw new InvalidParameterCardinalityException(
                        "Exactly one offering is required",
                        SosConstants.GetObservationParams.offering.name());
            }

            for (String offeringId : offeringIds) {
                if (Strings.isNullOrEmpty(offeringId)) {
                    throw new MissingOfferingParameterException();
                }
                if (offerings.contains(offeringId)) {
                    validOfferings.add(offeringId);
                } else if (ncOfferings.containsKey(offeringId)) {
                    validOfferings.add(ncOfferings.get(offeringId));
                } else {
                    throw new InvalidOfferingParameterException(offeringId);
                }
            }
            exceptions.throwIfNotEmpty();
        }
        return validOfferings;
    }

    // TODO check for SOS 1.0.0 one / mandatory
    private boolean checkResponseFormat(GetObservationRequest request) throws OwsExceptionReport {
        boolean zipCompression = false;
        if (request.getResponseFormat() == null) {
            request.setResponseFormat(OmConstants.CONTENT_TYPE_OM.toString());
        } else if (request.getResponseFormat() != null && request.getResponseFormat().isEmpty()) {
            throw new MissingResponseFormatParameterException();
        } else {

            Collection<String> supportedResponseFormats =
                    getResponseFormatRepository().getSupportedResponseFormats(request.getService(),
                            request.getVersion());
            if (!supportedResponseFormats.contains(request.getResponseFormat())) {
                throw new InvalidResponseFormatParameterException(request.getResponseFormat());
            }
        }
        return zipCompression;
    }

    private void checkQueryParametersIfAllEmpty(GetObservationRequest request) throws OwsExceptionReport {
        if (!request.isSetOffering() && !request.isSetObservableProperty() && !request.isSetProcedure()
                && !request.isSetFeatureOfInterest() && !request.isSetTemporalFilter()
                && !request.isSetSpatialFilter()) {
            throw new ResponseExceedsSizeLimitException()
                    .withMessage("The response exceeds the size limit! Please define some filtering parameters.");
        }

    }
}
