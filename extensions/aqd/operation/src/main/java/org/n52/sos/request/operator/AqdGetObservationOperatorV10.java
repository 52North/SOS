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
package org.n52.sos.request.operator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdHelper;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.AbstractGetObservationDAO;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.exception.ows.concrete.InvalidObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.InvalidResponseFormatParameterException;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.exception.sos.ResponseExceedsSizeLimitException;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.SosHelper;

import com.google.common.collect.Lists;

public class AqdGetObservationOperatorV10 extends
        AbstractAqdRequestOperator<AbstractGetObservationDAO, GetObservationRequest, GetObservationResponse> {

    private static final TemporalFilter TEMPORAL_FILTER_LATEST = new TemporalFilter(TimeOperator.TM_Equals,
            new TimeInstant(SosIndeterminateTime.latest), OmConstants.EN_PHENOMENON_TIME);

    private static final String OPERATION_NAME = SosConstants.Operations.GetObservation.name();

    private boolean blockRequestsWithoutRestriction;

    public AqdGetObservationOperatorV10() {
        super(OPERATION_NAME, GetObservationRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public GetObservationResponse receive(GetObservationRequest request) throws OwsExceptionReport {
        ReportObligationType flow = AqdHelper.getInstance().getFlow(request.getExtensions());
        checkReportingHeader(flow);
        checkRequestForFlowAndTemporalFilter(request, flow);
        boolean checkForMergeObservationsInResponse = checkForMergeObservationsInResponse(request);
        request.setMergeObservationValues(checkForMergeObservationsInResponse);
        final GetObservationResponse response =
                (GetObservationResponse) changeResponseServiceVersion(getDao().getObservation(
                        (GetObservationRequest) changeRequestServiceVersion(request)));
        changeRequestServiceVersionToAqd(request);
        response.setExtensions(request.getExtensions());
        setObservationResponseResponseFormatAndContentType(request, response);
        // TODO check for correct merging, add merge if swes:extension is set
        if (checkForMergeObservationsInResponse) {
            response.setMergeObservations(true);
        }
        return response;
    }

    private boolean checkForMergeObservationsInResponse(GetObservationRequest request) {
        if (getActiveProfile().isMergeValues() || isSetExtensionMergeObservationsToSweDataArray(request)) {
            return true;
        }
        return false;
    }

    private void checkRequestForFlowAndTemporalFilter(GetObservationRequest request, ReportObligationType flow) throws CodedException {
        try {
            if (!request.isSetTemporalFilter()) {
                DateTime start = null;
                DateTime end = null;
                DateTime dateTime = new DateTime();
                if (ReportObligationType.E2A.equals(flow)) {
                    String timeString;
                    timeString = DateTimeHelper.formatDateTime2YearMonthDayDateStringYMD(dateTime.minusDays(1));
                    start = DateTimeHelper.parseIsoString2DateTime(timeString);
                    int timeLength = DateTimeHelper.getTimeLengthBeforeTimeZone(timeString);
                    DateTime origEnd = DateTimeHelper.parseIsoString2DateTime(timeString);
                    end = DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition(origEnd, timeLength);
                } else if (ReportObligationType.E1A.equals(flow) || ReportObligationType.E1B.equals(flow)) {
                    String year = Integer.toString(dateTime.minusYears(1).getYear());
                    start = DateTimeHelper.parseIsoString2DateTime(year);
                    int timeLength = DateTimeHelper.getTimeLengthBeforeTimeZone(year);
                    end =
                            DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition(
                                    DateTimeHelper.parseIsoString2DateTime(year), timeLength);
                }
                if (start != null && end != null) {
                    request.setTemporalFilters(getTemporalFilter(new TimePeriod(start.minusMillis(1), end
                            .plusMillis(2))));
                }
            }
        } catch (DateTimeFormatException | DateTimeParseException e) {
            throw new NoApplicableCodeException()
                    .causedBy(e)
                    .withMessage(
                            "The request does not contain a temporal filter and the temporal filter creation for the flow fails!");
        }
    }

    private List<TemporalFilter> getTemporalFilter(TimePeriod tp) {
        TemporalFilter tf = new TemporalFilter(TimeOperator.TM_During, tp, "phenomenonTime");
        return Lists.newArrayList(tf);
    }

    private boolean isSetExtensionMergeObservationsToSweDataArray(final GetObservationRequest request) {
        return request.isSetExtensions()
                && request.getExtensions().isBooleanExtensionSet(
                        Sos2Constants.Extensions.MergeObservationsIntoDataArray.name());
    }

    @Override
    protected void checkParameters(final GetObservationRequest request) throws OwsExceptionReport {
        final CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(request.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(request);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        try {
            checkOfferingId(request.getOfferings());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkObservedProperties(request.getObservedProperties());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureIDs(request.getProcedures(), SosConstants.GetObservationParams.procedure.name());
            // add child procedures to request
            if (request.isSetProcedure()) {
                request.setProcedures(addChildProcedures(request.getProcedures()));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkFeatureOfInterestIdentifiers(request.getFeatureIdentifiers(),
                    SosConstants.GetObservationParams.featureOfInterest.name());
            if (request.isSetFeatureOfInterest()) {
                request.setFeatureIdentifiers(addChildFeatures(request.getFeatureIdentifiers()));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSpatialFilter(request.getSpatialFilter(), SosConstants.GetObservationParams.featureOfInterest.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {

            if (request.isSetTemporalFilter()) {
                checkTemporalFilter(request.getTemporalFilters(),
                        Sos2Constants.GetObservationParams.temporalFilter.name());
            } else if (getActiveProfile().isReturnLatestValueIfTemporalFilterIsMissingInGetObservation()) {
                request.setTemporalFilters(CollectionHelper.list(TEMPORAL_FILTER_LATEST));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        try {
            if (request.getResponseFormat() == null) {
                request.setResponseFormat(AqdConstants.NS_AQD);
            } else {
                SosHelper.checkResponseFormat(request.getResponseFormat(), request.getService(), request.getVersion());
                if (!AqdConstants.NS_AQD.equals(request.getResponseFormat())) {
                    throw new InvalidResponseFormatParameterException(request.getResponseFormat());
                }
            }

        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        checkExtensions(request, exceptions);
        exceptions.throwIfNotEmpty();

        // check if parameters are set, if not throw ResponseExceedsSizeLimit
        // exception
        // TODO remove after finishing CITE tests
        if (request.isEmpty() && isBlockRequestsWithoutRestriction()) {
            throw new ResponseExceedsSizeLimitException()
                    .withMessage("The response exceeds the size limit! Please define some filtering parameters.");
        }
    }

    private boolean isBlockRequestsWithoutRestriction() {
        return blockRequestsWithoutRestriction;
    }

    @Setting(CoreProfileOperatorSettings.BLOCK_GET_OBSERVATION_REQUESTS_WITHOUT_RESTRICTION)
    public void setBlockRequestsWithoutRestriction(boolean flag) {
        this.blockRequestsWithoutRestriction = flag;
    }

    /**
     * checks if mandatory parameter observed property is correct
     * 
     * @param observedProperties
     *            list containing the observed properties of the request
     * 
     * @throws OwsExceptionReport
     *             if the parameter does not containing any matching
     *             observedProperty for the requested offering
     */
    private void checkObservedProperties(final List<String> observedProperties) throws OwsExceptionReport {
        if (observedProperties != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            final Collection<String> validObservedProperties =
                    Configurator.getInstance().getCache().getObservableProperties();
            for (final String obsProp : observedProperties) {
                if (obsProp.isEmpty()) {
                    exceptions.add(new MissingObservedPropertyParameterException());
                } else {
                    if (!validObservedProperties.contains(obsProp)) {
                        exceptions.add(new InvalidObservedPropertyParameterException(obsProp));
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
     *            the offeringId to be checked
     * 
     * 
     * @throws OwsExceptionReport
     *             if the passed offeringId is not supported
     */
    private void checkOfferingId(final List<String> offeringIds) throws OwsExceptionReport {
        if (offeringIds != null) {
            final Set<String> offerings = Configurator.getInstance().getCache().getOfferings();
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String offeringId : offeringIds) {
                if (offeringId == null || offeringId.isEmpty()) {
                    exceptions.add(new MissingOfferingParameterException());
                } else if (offeringId.contains(SosConstants.SEPARATOR_4_OFFERINGS)) {
                    final String[] offArray = offeringId.split(SosConstants.SEPARATOR_4_OFFERINGS);
                    if (!offerings.contains(offArray[0])
                            || !getCache().getProceduresForOffering(offArray[0]).contains(offArray[1])) {
                        exceptions.add(new InvalidOfferingParameterException(offeringId));
                    }

                } else if (!offerings.contains(offeringId)) {
                    exceptions.add(new InvalidOfferingParameterException(offeringId));
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

}