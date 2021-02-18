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
package org.n52.sos.request.operator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.shetland.ogc.SupportedType;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.ObservationType;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.exception.ResponseExceedsSizeLimitException;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swes.SwesExtension;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.AbstractGetObservationHandler;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.wsdl.Metadata;
import org.n52.sos.wsdl.Metadatas;
import org.n52.svalbard.ConformanceClasses;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.ObservationEncoder;

import com.google.common.collect.Sets;

/**
 * class and forwards requests to the GetObservationDAO; after query of
 * Database, class encodes the ObservationResponse (thru using the IOMEncoder)
 *
 * @since 4.0.0
 */
@Configurable
public class SosGetObservationOperatorV20 extends
        AbstractV2RequestOperator<AbstractGetObservationHandler, GetObservationRequest, GetObservationResponse> {

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(ConformanceClasses.SOS_V2_CORE_PROFILE);

    private static final TemporalFilter TEMPORAL_FILTER_LATEST = new TemporalFilter(TimeOperator.TM_Equals,
            new TimeInstant(ExtendedIndeterminateTime.LATEST), "phenomenonTime");

    private boolean blockRequestsWithoutRestriction;

    public SosGetObservationOperatorV20() {
        super(SosConstants.Operations.GetObservation.name(), GetObservationRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public GetObservationResponse receive(GetObservationRequest request) throws OwsExceptionReport {
        final GetObservationResponse sosResponse = getOperationHandler().getObservation(request);
        setObservationResponseResponseFormatAndContentType(request, sosResponse);
        return sosResponse;
    }

    @Override
    protected void checkParameters(GetObservationRequest request) throws OwsExceptionReport {
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
            // add child offerings to request
            if (request.isSetOffering()) {
                request.setOfferings(addChildOfferings(request.getOfferings()));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkObservedProperties(request.getObservedProperties(),
                    SosConstants.GetObservationParams.observedProperty, false);
            // add child observedProperties if isInclude == true and requested
            // observedProperty is parent.
            if (request.isSetObservableProperty()) {
                request.setObservedProperties(addChildObservableProperties(request.getObservedProperties()));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkQueryableProcedures(request.getProcedures(), SosConstants.GetObservationParams.procedure.name());
            // add instance and child procedures to request
            if (request.isSetProcedure()) {
                request.setProcedures(addChildProcedures(addInstanceProcedures(request.getProcedures())));
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
            checkSpatialFilter(request.getSpatialFilter(),
                    SosConstants.GetObservationParams.featureOfInterest.name());
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
            if (!request.isSetResponseFormat()) {
                request.setResponseFormat(getActiveProfile().getObservationResponseFormat());
            }
            checkResponseFormat(request.getResponseFormat(), request.getService(),
                    request.getVersion());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        try {
            if (request.isSetResultModel()) {
                if (request.getResultModel() != null && !request.getResultModel().isEmpty()) {
                    throw new MissingParameterValueException(SosConstants.GetObservationParams.resultType);
                } else {

                    if (!getResponseFormatsForObservationType(request.getResultModel(),
                                    request.getService(), request.getVersion())
                            .contains(request.getResponseFormat())) {
                        throw new InvalidParameterValueException().withMessage(
                                "The requested resultType {} is not valid for the responseFormat {}!",
                                request.getResultModel(), request.getResponseFormat());
                    }
                }
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        if (getActiveProfile().isMergeValues()) {
            if (!request.getExtensions()
                    .containsExtension(Sos2Constants.Extensions.MergeObservationsIntoDataArray)) {
                Extensions extensions = new Extensions();
                extensions.addExtension(new SwesExtension<SweBoolean>()
                        .setDefinition(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name())
                        .setValue((SweBoolean) new SweBoolean()
                                .setValue(getProfileHandler().getActiveProfile()
                                        .isMergeValues())
                                .setDefinition(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name())));
                request.setExtensions(extensions);
            }
        }
        try {
            checkResultFilterExtension(request);
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
            Set<String> offerings = getCache().getOfferings();
            CompositeOwsException exceptions = new CompositeOwsException();
            offeringIds.forEach(offeringId -> {
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
            });
            exceptions.throwIfNotEmpty();
        }
    }

    protected Set<String> getResponseFormatsForObservationType(String observationType, String service,
            String version) {
        Set<String> responseFormats = Sets.newHashSet();
        for (Encoder<?, ?> e : getEncoderRepository().getEncoders()) {
            if (e instanceof ObservationEncoder) {
                final ObservationEncoder<?, ?> oe = (ObservationEncoder<?, ?>) e;
                Map<String, Set<SupportedType>> supportedResponseFormatObservationTypes =
                        oe.getSupportedResponseFormatObservationTypes();
                if (supportedResponseFormatObservationTypes != null
                        && !supportedResponseFormatObservationTypes.isEmpty()) {
                    for (final Entry<String, Set<SupportedType>> entry : supportedResponseFormatObservationTypes
                            .entrySet()) {
                        for (SupportedType st : entry.getValue()) {
                            if (st instanceof ObservationType
                                    && observationType.equals(((ObservationType) st).getValue())) {
                                responseFormats.add(entry.getKey());
                            }
                        }
                    }
                }
            }
        }
        return responseFormats;
    }

    @Override
    public Metadata getSosOperationDefinition() {
        return Metadatas.GET_OBSERVATION;
    }
}
