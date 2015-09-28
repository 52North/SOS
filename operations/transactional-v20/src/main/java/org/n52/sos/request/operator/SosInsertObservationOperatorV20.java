/*
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
import java.util.Set;

import org.n52.iceland.exception.CodedException;
import org.n52.iceland.exception.ows.CompositeOwsException;
import org.n52.iceland.exception.ows.InvalidParameterValueException;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.om.OmConstants;
import org.n52.iceland.ogc.ows.Extensions;
import org.n52.iceland.ogc.sos.ConformanceClasses;
import org.n52.iceland.ogc.sos.Sos2Constants;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.util.CollectionHelper;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.AbstractInsertObservationHandler;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.sos.exception.ows.concrete.InvalidObservationTypeException;
import org.n52.sos.exception.ows.concrete.InvalidObservationTypeForOfferingException;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingObservationParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmCompositePhenomenon;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweAbstractDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.OMHelper;
import org.n52.sos.wsdl.WSDLConstants;
import org.n52.sos.wsdl.WSDLOperation;


public class SosInsertObservationOperatorV20 extends
        AbstractV2TransactionalRequestOperator<AbstractInsertObservationHandler, InsertObservationRequest, InsertObservationResponse> {

    private static final String OPERATION_NAME = SosConstants.Operations.InsertObservation.name();

    private static final Set<String> CONFORMANCE_CLASSES =
            Collections.singleton(ConformanceClasses.SOS_V2_OBSERVATION_INSERTION);

    public SosInsertObservationOperatorV20() {
        super(OPERATION_NAME, InsertObservationRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if(SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
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
     * @throws CodedException
     *             If more than one sampling geometry is defined
     */
    private void checkParameterForSpatialFilteringProfile(InsertObservationRequest request) throws OwsExceptionReport {
        for (OmObservation observation : request.getObservations()) {
            if (observation.isSetParameter()) {
                int count = 0;
                for (NamedValue<?> namedValue : observation.getParameter()) {
                    if (Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE
                            .equals(namedValue.getName().getHref())) {
                        count++;
                    }
                }
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
        if (request.getOfferings() == null || (request.getOfferings() != null && request.getOfferings().isEmpty())) {
            throw new MissingOfferingParameterException();
        } else {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String offering : request.getOfferings()) {
                if (offering == null || offering.isEmpty()) {
                    exceptions.add(new MissingOfferingParameterException());
                } else if (!Configurator.getInstance().getCache().getOfferings().contains(offering)) {
                    exceptions.add(new InvalidOfferingParameterException(offering));
                } else {
                    for (final OmObservation observation : request.getObservations()) {
                        observation.getObservationConstellation().addOffering(offering);
                    }
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    private void checkObservations(final InsertObservationRequest request) throws OwsExceptionReport {
        if (CollectionHelper.isEmpty(request.getObservations())) {
            throw new MissingObservationParameterException();
        } else {
            final SosContentCache cache = Configurator.getInstance().getCache();
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final OmObservation observation : request.getObservations()) {
                final OmObservationConstellation obsConstallation = observation.getObservationConstellation();
                checkObservationConstellationParameter(obsConstallation);
                // Requirement 67
                checkOrSetObservationType(observation, isSplitObservations(request.getExtensions()));
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

    private boolean isSplitObservations(final Extensions extensions) {
        return extensions != null
                && !extensions.isEmpty()
                && extensions
                        .isBooleanExtensionSet(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name());

    }

    private void checkObservationConstellationParameter(final OmObservationConstellation obsConstallation)
            throws OwsExceptionReport {
        AbstractPhenomenon observableProperty = obsConstallation.getObservableProperty();
        String observablePropertyIdentifier = observableProperty.getIdentifier();

        if (hasObservations(observablePropertyIdentifier, obsConstallation.getOfferings()) &&
            observableProperty.isComposite() != getCache().isCompositePhenomenon(observablePropertyIdentifier)) {
            throw new InvalidParameterValueException(Sos2Constants.InsertObservationParams.observedProperty, observablePropertyIdentifier);
        }

        checkProcedureID(obsConstallation.getProcedure().getIdentifier(), Sos2Constants.InsertObservationParams.procedure);
        checkObservedProperty(observablePropertyIdentifier, Sos2Constants.InsertObservationParams.observedProperty, true);
        checkReservedCharacter(obsConstallation.getFeatureOfInterest().getIdentifier(),
                Sos2Constants.InsertObservationParams.featureOfInterest);
    }

    private boolean hasObservations(String observableProperty, Set<String> offerings) {
        for (String offering : getCache().getOfferingsForObservableProperty(observableProperty)) {
            if (offerings.contains(offering) && getCache().hasMaxPhenomenonTimeForOffering(offering)) {
                return true;
            }
        }
        return false;
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
                        "The requested observation is invalid! The result element does not comply with the defined type (%s)!",
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
    public WSDLOperation getSosOperationDefinition() {
        return WSDLConstants.Operations.INSERT_OBSERVATION;
    }

    private void createCompositePhenomenons(InsertObservationRequest request) {
        for (OmObservation observation : request.getObservations()) {
            if (isComplexObservation(observation)) {
                createCompositePhenomenon(observation);
            }
        }
    }

    protected void createCompositePhenomenon(OmObservation observation) {
        OmObservationConstellation oc = observation.getObservationConstellation();
        AbstractPhenomenon observableProperty = oc.getObservableProperty();

        if (!(observableProperty instanceof  OmCompositePhenomenon)) {
            final OmCompositePhenomenon parent;
            parent = new OmCompositePhenomenon(observableProperty.getIdentifier());
            parent.setDefaultElementEncoding(observableProperty.getDefaultElementEncoding());
            parent.setHumanReadableIdentifier(observableProperty.getHumanReadableIdentifierCodeWithAuthority());
            parent.setIdentifier(observableProperty.getIdentifierCodeWithAuthority());
            parent.setDescription(observableProperty.getDescription());
            parent.setName(observableProperty.getName());

            ComplexValue value = (ComplexValue) observation.getValue().getValue();
            SweAbstractDataRecord dataRecord = value.getValue();
            for (SweField field : dataRecord.getFields()) {
                SweAbstractDataComponent element = field.getElement();
                OmObservableProperty child = new OmObservableProperty(element.getDefinition());
                child.setName(element.getNames());
                child.setDescription(element.getDescription());
                parent.addPhenomenonComponent(child);
            }

            oc.setObservableProperty(parent);
        }
    }

    protected static boolean isComplexObservation(OmObservation observation) {
        return observation.getObservationConstellation().getObservationType()
                .equalsIgnoreCase(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION) &&
               observation.getValue().getValue() instanceof ComplexValue;
    }
}
