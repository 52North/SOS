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

import org.n52.sos.cache.ContentCache;
import org.n52.sos.ds.AbstractInsertObservationDAO;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.InvalidObservationTypeException;
import org.n52.sos.exception.ows.concrete.InvalidObservationTypeForOfferingException;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingObservationParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.wsdl.WSDLConstants;
import org.n52.sos.wsdl.WSDLOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class SosInsertObservationOperatorV20 extends
        AbstractV2TransactionalRequestOperator<AbstractInsertObservationDAO, InsertObservationRequest, InsertObservationResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosInsertObservationOperatorV20.class);

    private static final String OPERATION_NAME = SosConstants.Operations.InsertObservation.name();

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(ConformanceClasses.SOS_V2_OBSERVATION_INSERTION);

    public SosInsertObservationOperatorV20() {
        super(OPERATION_NAME, InsertObservationRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public InsertObservationResponse receive(final InsertObservationRequest request) throws OwsExceptionReport {
        if (isSetExtensionSplitDataArrayIntoObservations(request)) {
            splitDataArrayIntoObservations(request);
        }
        InsertObservationResponse response = getDao().insertObservation(request);
        SosEventBus.fire(new ObservationInsertion(request, response));
        return response;
    }

    private void splitDataArrayIntoObservations(final InsertObservationRequest request) throws OwsExceptionReport {
        LOGGER.debug("Start splitting observations. Count: {}", request.getObservations().size());
        final Collection<OmObservation> finalObservationCollection = Sets.newHashSet();
        for (final OmObservation observation : request.getObservations()) {
            if (isSweArrayObservation(observation)) {
                LOGGER.debug("Found SweArrayObservation to split.");
                final SweDataArrayValue sweDataArrayValue = (SweDataArrayValue) observation.getValue().getValue();
                final OmObservationConstellation observationConstellation = observation.getObservationConstellation();
                int counter = 0;
                final int resultTimeIndex =
                        getResultTimeIndex((SweDataRecord) sweDataArrayValue.getValue().getElementType());
                final int phenomenonTimeIndex =
                        getPhenomenonTimeIndex((SweDataRecord) sweDataArrayValue.getValue().getElementType());
                final int resultValueIndex =
                        getResultValueIndex((SweDataRecord) sweDataArrayValue.getValue().getElementType(),
                                observationConstellation.getObservableProperty());
                observationConstellation.setObservationType(getObservationTypeFromElementType(
                        (SweDataRecord) sweDataArrayValue.getValue().getElementType(),
                        observationConstellation.getObservableProperty()));
                // split into single observation
                for (final List<String> block : sweDataArrayValue.getValue().getValues()) {
                    LOGGER.debug("Processing block {}/{}", ++counter, sweDataArrayValue.getValue().getValues().size());
                    final OmObservation newObservation = new OmObservation();
                    newObservation.setObservationConstellation(observationConstellation);
                    // identifier
                    if (observation.isSetIdentifier()) {
                        final CodeWithAuthority identifier = observation.getIdentifierCodeWithAuthority();
                        identifier.setValue(identifier.getValue() + counter);
                        newObservation.setIdentifier(identifier);
                    }
                    // phen time
                    Time phenomenonTime;
                    if (phenomenonTimeIndex == -1) {
                        phenomenonTime = observation.getPhenomenonTime();
                    } else {
                        phenomenonTime = DateTimeHelper.parseIsoString2DateTime2Time(block.get(phenomenonTimeIndex));
                    }
                    // result time
                    if (resultTimeIndex == -1) {
                        // use phenomenon time if outer observation's resultTime value
                        // or nilReason is "template"
                        if ((!observation.isSetResultTime()
                                || observation.isTemplateResultTime())
                                && phenomenonTime instanceof TimeInstant) {
                            newObservation.setResultTime((TimeInstant) phenomenonTime);
                        } else {
                            newObservation.setResultTime(observation.getResultTime());
                        }
                    } else {
                        newObservation.setResultTime(new TimeInstant(DateTimeHelper.parseIsoString2DateTime(block
                                .get(resultTimeIndex))));
                    }
                    if (observation.isSetParameter()) {
                    	newObservation.setParameter(observation.getParameter());
                    }
                    // value
                    final ObservationValue<?> value =
                            createObservationResultValue(observationConstellation.getObservationType(),
                                    block.get(resultValueIndex), phenomenonTime, ((SweDataRecord) sweDataArrayValue
                                            .getValue().getElementType()).getFields().get(resultValueIndex));
                    newObservation.setValue(value);
                    finalObservationCollection.add(newObservation);
                }
            } else {
                LOGGER.debug("Found non splittable observation");
                finalObservationCollection.add(observation);
            }
        }
        request.setObservation(Lists.newArrayList(finalObservationCollection));
    }

    private ObservationValue<?> createObservationResultValue(final String observationType, final String valueString,
            final Time phenomenonTime, final SweField resultDefinitionField) throws OwsExceptionReport {
        ObservationValue<?> value = null;

        if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_TRUTH_OBSERVATION)) {
            value = new SingleObservationValue<Boolean>(new BooleanValue(Boolean.parseBoolean(valueString)));
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_COUNT_OBSERVATION)) {
            value = new SingleObservationValue<Integer>(new CountValue(Integer.parseInt(valueString)));
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_MEASUREMENT)) {
            final QuantityValue quantity = new QuantityValue(Double.parseDouble(valueString));
            quantity.setUnit(getUom(resultDefinitionField));
            value = new SingleObservationValue<Double>(quantity);
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION)) {
            final CategoryValue cat = new CategoryValue(valueString);
            cat.setUnit(getUom(resultDefinitionField));
            value = new SingleObservationValue<String>(cat);
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_TEXT_OBSERVATION)) {
            value = new SingleObservationValue<String>(new TextValue(valueString));
        }
        // TODO Check for missing types
        if (value != null) {
            value.setPhenomenonTime(phenomenonTime);
        } else {
            throw new NoApplicableCodeException().withMessage("Observation type '{}' not supported.", observationType)
                    .setStatus(HTTPStatus.BAD_REQUEST);
        }
        return value;
    }

    private String getUom(final SweField resultDefinitionField) {
        return ((SweAbstractUomType<?>) resultDefinitionField.getElement()).getUom();
    }

    private int getResultValueIndex(final SweDataRecord elementTypeDataRecord,
            final AbstractPhenomenon observableProperty) {
        return elementTypeDataRecord.getFieldIndexByIdentifier(observableProperty.getIdentifier());
    }

    private int getPhenomenonTimeIndex(final SweDataRecord elementTypeDataRecord) {
        return elementTypeDataRecord.getFieldIndexByIdentifier(OmConstants.PHENOMENON_TIME);
    }

    private int getResultTimeIndex(final SweDataRecord elementTypeDataRecord) {
        return elementTypeDataRecord.getFieldIndexByIdentifier(OmConstants.PHEN_SAMPLING_TIME);
    }

    private String getObservationTypeFromElementType(final SweDataRecord elementTypeDataRecord,
            final AbstractPhenomenon observableProperty) throws OwsExceptionReport {
        for (final SweField sweField : elementTypeDataRecord.getFields()) {
            if (sweField.getElement() != null && sweField.getElement().isSetDefinition()
                    && sweField.getElement().getDefinition().equalsIgnoreCase(observableProperty.getIdentifier())) {
                return OMHelper.getObservationTypeFrom(sweField.getElement());
            }
        }
        throw new NoApplicableCodeException().withMessage(
                "Not able to derive observation type from elementType element '{}' for observable property '{}'.",
                elementTypeDataRecord, observableProperty).setStatus(HTTPStatus.BAD_REQUEST);
    }

    private boolean isSweArrayObservation(final OmObservation observation) {
        return observation.getObservationConstellation().getObservationType()
                .equalsIgnoreCase(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION)
                && observation.getValue().getValue() instanceof SweDataArrayValue
                && ((SweDataArrayValue) observation.getValue().getValue()).isSetValue();
    }

    private boolean isSetExtensionSplitDataArrayIntoObservations(final InsertObservationRequest request) {
        return request.isSetExtensions()
                && request.getExtensions().isBooleanExtensionSet(
                        Sos2Constants.Extensions.SplitDataArrayIntoObservations.name());
    }

    @Override
    protected void checkParameters(final InsertObservationRequest request) throws OwsExceptionReport {
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
    private void checkParameterForSpatialFilteringProfile(InsertObservationRequest request) throws CodedException {
        for (OmObservation observation : request.getObservations()) {
            if (observation.isSetParameter()) {
                int count = 0;
                for (NamedValue<?> namedValue : observation.getParameter()) {
                    if (Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
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
            final ContentCache cache = Configurator.getInstance().getCache();
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
                        if ((allowedObservationTypes == null || !allowedObservationTypes.contains(obsConstallation
                                .getObservationType())) && !isSetExtensionSplitDataArrayIntoObservations(request)) {
                            exceptions.add(new InvalidObservationTypeForOfferingException(obsConstallation
                                    .getObservationType(), offeringID));
                        }
                    }
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    private boolean isSplitObservations(final SwesExtensions swesExtensions) {
        return swesExtensions != null
                && !swesExtensions.isEmpty()
                && swesExtensions
                        .isBooleanExtensionSet(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name());
    }

    private void checkObservationConstellationParameter(final OmObservationConstellation obsConstallation)
            throws OwsExceptionReport {
        checkProcedureID(obsConstallation.getProcedure().getIdentifier(),
                Sos2Constants.InsertObservationParams.procedure.name());
        checkObservedProperty(obsConstallation.getObservableProperty().getIdentifier(),
                Sos2Constants.InsertObservationParams.observedProperty.name());
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
                throw new NoApplicableCodeException()
                        .withMessage(
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
}
