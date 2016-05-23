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

import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.AbstractGetObservationDAO;
import org.n52.sos.exception.ows.concrete.InvalidObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.exception.sos.ResponseExceedsSizeLimitException;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.wsdl.WSDLConstants;
import org.n52.sos.wsdl.WSDLOperation;

/**
 * class and forwards requests to the GetObservationDAO; after query of
 * Database, class encodes the ObservationResponse (thru using the IOMEncoder)
 * 
 * @since 4.0.0
 */
@Configurable
public class SosGetObservationOperatorV20 extends
        AbstractV2RequestOperator<AbstractGetObservationDAO, GetObservationRequest, GetObservationResponse> {

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(ConformanceClasses.SOS_V2_CORE_PROFILE);

    private static final TemporalFilter TEMPORAL_FILTER_LATEST = new TemporalFilter(TimeOperator.TM_Equals,
            new TimeInstant(SosIndeterminateTime.latest), "phenomenonTime");

    private boolean blockRequestsWithoutRestriction;

    public SosGetObservationOperatorV20() {
        super(SosConstants.Operations.GetObservation.name(), GetObservationRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public GetObservationResponse receive(final GetObservationRequest sosRequest) throws OwsExceptionReport {
        final GetObservationResponse sosResponse = getDao().getObservation(sosRequest);
        setObservationResponseResponseFormatAndContentType(sosRequest, sosResponse);
        return sosResponse;
    }

    @Override
    protected void checkParameters(final GetObservationRequest sosRequest) throws OwsExceptionReport {
        final CompositeOwsException exceptions = new CompositeOwsException();
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

        try {
            checkOfferingId(sosRequest.getOfferings());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkObservedProperties(sosRequest.getObservedProperties());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureIDs(sosRequest.getProcedures(), SosConstants.GetObservationParams.procedure.name());
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

            if (sosRequest.isSetTemporalFilter()) {
                checkTemporalFilter(sosRequest.getTemporalFilters(),
                        Sos2Constants.GetObservationParams.temporalFilter.name());
            } else if (getActiveProfile().isReturnLatestValueIfTemporalFilterIsMissingInGetObservation()) {
                sosRequest.setTemporalFilters(CollectionHelper.list(TEMPORAL_FILTER_LATEST));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        try {
            if (sosRequest.getResponseFormat() == null) {
                sosRequest.setResponseFormat(getActiveProfile().getObservationResponseFormat());
            }
            SosHelper.checkResponseFormat(sosRequest.getResponseFormat(), sosRequest.getService(),
                    sosRequest.getVersion());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        checkExtensions(sosRequest, exceptions);
        exceptions.throwIfNotEmpty();

        // check if parameters are set, if not throw ResponseExceedsSizeLimit
        // exception
        // TODO remove after finishing CITE tests
        if (sosRequest.isEmpty() && isBlockRequestsWithoutRestriction()) {
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

    @Override
    public WSDLOperation getSosOperationDefinition() {
        return WSDLConstants.Operations.GET_OBSERVATION;
    }
}
