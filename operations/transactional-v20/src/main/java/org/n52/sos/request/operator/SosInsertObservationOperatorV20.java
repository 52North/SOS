/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
import java.util.Set;

import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.ows.HasExtension;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.OMHelper;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.AbstractInsertObservationHandler;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.sos.exception.ows.concrete.InvalidObservationTypeException;
import org.n52.sos.exception.ows.concrete.InvalidObservationTypeForOfferingException;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingObservationParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.wsdl.Metadata;
import org.n52.sos.wsdl.Metadatas;
import org.n52.svalbard.ConformanceClasses;

public class SosInsertObservationOperatorV20 extends
        AbstractV2TransactionalRequestOperator<AbstractInsertObservationHandler,
        InsertObservationRequest,
        InsertObservationResponse> {

    private static final String OPERATION_NAME = SosConstants.Operations.InsertObservation.name();

    private static final Set<String> CONFORMANCE_CLASSES =
            Collections.singleton(ConformanceClasses.SOS_V2_OBSERVATION_INSERTION);

    public SosInsertObservationOperatorV20() {
        super(OPERATION_NAME, InsertObservationRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public InsertObservationResponse receive(final InsertObservationRequest request) throws OwsExceptionReport {
        InsertObservationResponse response = getOperationHandler().insertObservation(request);
        getServiceEventBus().submit(new ObservationInsertion(request, response));
        return response;
    }

    @Override
    protected void checkParameters(final InsertObservationRequest request) throws OwsExceptionReport {
        createCompositePhenomenons(request);
        final CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(request.getService());
        } catch (final OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(request);
        } catch (final OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // offering [1..*]
        try {
            checkAndAddOfferingToObservationConstallation(request);
        } catch (final OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkParameterForSpatialFilteringProfile(request);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // observation [1..*]
        try {
            checkObservations(request);
        } catch (final OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    /**
     * Check if the observation contains more than one sampling geometry
     * definitions.
     *
     * @param request
     *            Request
     *
     * @throws CodedException
     *             If more than one sampling geometry is defined
     */
    private void checkParameterForSpatialFilteringProfile(InsertObservationRequest request) throws OwsExceptionReport {
        for (OmObservation observation : request.getObservations()) {
            if (observation.isSetParameter()) {
                long count = observation.getParameter().stream().map(NamedValue::getName).map(ReferenceType::getHref)
                        .filter(Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE::equals).count();
                if (count > 1) {
                    throw new InvalidParameterValueException().at("om:parameter").withMessage(
                            "The observation contains more than one ({}) sampling geometry definitions!", count);
                }
            }
        }
    }

    private void checkAndAddOfferingToObservationConstallation(final InsertObservationRequest request)
            throws OwsExceptionReport {
        // TODO: Check requirement for this case in SOS 2.0 specification
        if (request.getOfferings() == null || request.getOfferings() != null && request.getOfferings().isEmpty()) {
            throw new MissingOfferingParameterException();
        } else {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String offering : request.getOfferings()) {
                if (offering == null || offering.isEmpty()) {
                    exceptions.add(new MissingOfferingParameterException());
                } else if (!getCache().getOfferings().contains(offering)) {
                    exceptions.add(new InvalidOfferingParameterException(offering));
                } else {
                    request.getObservations()
                            .forEach(observation -> observation.getObservationConstellation().addOffering(offering));
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    private void checkObservations(final InsertObservationRequest request) throws OwsExceptionReport {
        if (CollectionHelper.isEmpty(request.getObservations())) {
            throw new MissingObservationParameterException();
        } else {
            final SosContentCache cache = getCache();
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final OmObservation observation : request.getObservations()) {
                final OmObservationConstellation obsConstallation = observation.getObservationConstellation();
                checkObservationConstellationParameter(obsConstallation);
                // Requirement 67
                checkOrSetObservationType(observation, isSplitObservations(request));
                if (!cache.getObservationTypes().contains(obsConstallation.getObservationType())) {
                    exceptions.add(new InvalidObservationTypeException(obsConstallation.getObservationType()));
                } else if (obsConstallation.isSetOfferings()) {
                    for (final String offeringID : obsConstallation.getOfferings()) {
                        final Collection<String> allowedObservationTypes =
                                cache.getAllowedObservationTypesForOffering(offeringID);
                        if ((allowedObservationTypes == null
                                || !allowedObservationTypes.contains(obsConstallation.getObservationType()))
                                && !request.isSetExtensionSplitDataArrayIntoObservations()) {
                            exceptions.add(new InvalidObservationTypeForOfferingException(
                                    obsConstallation.getObservationType(), offeringID));
                        }
                    }
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    private boolean isSplitObservations(HasExtension<?> extensions) {
        return extensions.getBooleanExtension(Sos2Constants.Extensions.SplitDataArrayIntoObservations);

    }

    private void checkObservationConstellationParameter(final OmObservationConstellation obsConstallation)
            throws OwsExceptionReport {
        AbstractPhenomenon observableProperty = obsConstallation.getObservableProperty();
        String observablePropertyIdentifier = observableProperty.getIdentifier();

        checkForCompositeObservableProperty(observableProperty, obsConstallation.getOfferings(),
                Sos2Constants.InsertObservationParams.observedProperty);

        checkTransactionalProcedure(obsConstallation.getProcedure().getIdentifier(),
                Sos2Constants.InsertObservationParams.procedure.name());
        checkObservedProperty(observablePropertyIdentifier, Sos2Constants.InsertObservationParams.observedProperty,
                true);
        checkReservedCharacter(obsConstallation.getFeatureOfInterest().getIdentifier(),
                Sos2Constants.InsertObservationParams.featureOfInterest);
    }

    private void checkOrSetObservationType(final OmObservation sosObservation, final boolean isSplitObservations)
            throws OwsExceptionReport {
        final OmObservationConstellation observationConstellation = sosObservation.getObservationConstellation();
        final String obsTypeFromValue = OMHelper.getObservationTypeFor(sosObservation.getValue().getValue());
        if (observationConstellation.isSetObservationType() && !isSplitObservations) {
            checkObservationType(observationConstellation.getObservationType(),
                    Sos2Constants.InsertObservationParams.observationType.name());
            if (obsTypeFromValue != null
                    && !sosObservation.getObservationConstellation().getObservationType().equals(obsTypeFromValue)) {
                throw new NoApplicableCodeException().withMessage(
                        "The requested observation is invalid! "
                        + "The result element does not comply with the defined type (%s)!",
                        sosObservation.getObservationConstellation().getObservationType());
            }
        } else if (!isSplitObservations) {
            sosObservation.getObservationConstellation().setObservationType(obsTypeFromValue);
        }
        /*
         * if isSplitObservations is true, the observation type will be set in
         * the method splitDataArrayIntoObservations and if the value is not
         * allowed the database insertion will fail at last.
         */
    }

    @Override
    public Metadata getSosOperationDefinition() {
        return Metadatas.INSERT_OBSERVATION;
    }

    private void createCompositePhenomenons(InsertObservationRequest request) {
        request.getObservations().forEach(this::createCompositePhenomenon);
    }

}
