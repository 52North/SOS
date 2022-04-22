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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.n52.faroe.annotation.Configurable;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.ProcessChain;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.System;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilities;
import org.n52.shetland.ogc.sensorML.v20.AbstractPhysicalProcess;
import org.n52.shetland.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.shetland.ogc.sensorML.v20.PhysicalSystem;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.sos.response.InsertSensorResponse;
import org.n52.shetland.ogc.swe.DataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.IdGenerator;
import org.n52.sos.ds.AbstractInsertSensorHandler;
import org.n52.sos.event.events.SensorInsertion;
import org.n52.sos.exception.ows.concrete.InvalidFeatureOfInterestTypeException;
import org.n52.sos.exception.ows.concrete.MissingFeatureOfInterestTypeException;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.wsdl.Metadata;
import org.n52.sos.wsdl.Metadatas;
import org.n52.svalbard.ConformanceClasses;
import org.n52.svalbard.decode.exception.DecodingException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
@Configurable
public class SosInsertSensorOperatorV20 extends
        AbstractV2TransactionalRequestOperator<AbstractInsertSensorHandler,
        InsertSensorRequest,
        InsertSensorResponse> {

    private static final Set<String> CONFORMANCE_CLASSES = Sets
            .newHashSet(ConformanceClasses.SOS_V2_INSERTION_CAPABILITIES, ConformanceClasses.SOS_V2_SENSOR_INSERTION);

    private static final String ABSTRACT_PROCESS_TYPE_OF_TITLE  = "sml:AbstractProcess.typeOf.title";

    private static SosOffering sensorTypeDummyOffering = new SosOffering("sensorTypeDummyOffering", "");

    public SosInsertSensorOperatorV20() {
        super(Sos2Constants.Operations.InsertSensor.name(), InsertSensorRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public Metadata getSosOperationDefinition() {
        return Metadatas.INSERT_SENSOR;
    }

    @Override
    public InsertSensorResponse receive(InsertSensorRequest request) throws OwsExceptionReport {
        InsertSensorResponse response = getOperationHandler().insertSensor(request);
        getServiceEventBus().submit(new SensorInsertion(request, response));
        return response;
    }

    @Override
    protected void checkParameters(InsertSensorRequest request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        String generatedId = IdGenerator.generate(request.getProcedureDescription().toString());
        // check parameters with variable content
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
            checkProcedureDescriptionFormat(request.getProcedureDescriptionFormat(), request.getService(),
                    request.getVersion());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkAndSetAssignedProcedureID(request, generatedId);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        /*
         * If the sensor to insert is a sensor type which does not make any
         * observations, set the dummy offering and do not any further checks.
         */
        if (request.isType()) {
            request.setAssignedOfferings(Lists.newArrayList(sensorTypeDummyOffering));
        } else {
            try {
                checkObservableProperty(request.getObservableProperty());
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
            checkAndSetAssignedOfferings(request, generatedId);
            try {
                checkProcedureAndOfferingCombination(request);
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
            try {
                checkParentChildProcedures(request.getProcedureDescription(),
                        request.getAssignedProcedureIdentifier());
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
            try {
                checkTypeOf(request.getProcedureDescription());
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
            if (request.getMetadata() != null) {
                try {
                    checkObservationTypes(request.getMetadata().getObservationTypes());
                } catch (OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
                try {
                    checkFeatureOfInterestTypes(request.getMetadata().getFeatureOfInterestTypes());
                } catch (OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            } else {
                exceptions.add(new MissingParameterValueException(Sos2Constants.InsertSensorParams.observationType));
                exceptions.add(
                        new MissingParameterValueException(Sos2Constants.InsertSensorParams.featureOfInterestType));
            }
        }
        exceptions.throwIfNotEmpty();
    }

    @Override
    protected void preProcessRequest(InsertSensorRequest request) {
        if (request.isSetProcedureDescription()) {
            SosProcedureDescription<?> procedureDescription = request.getProcedureDescription();
            if (request.hasExtension(SensorMLConstants.ELEMENT_NAME_OFFERINGS)) {
                Optional<Extension<?>> extension = request.getExtension(SensorMLConstants.ELEMENT_NAME_OFFERINGS);
                if (extension.isPresent()) {
                    if (extension.get().getValue() instanceof DataRecord) {
                        procedureDescription.addOfferings(SosOffering.fromSet(((DataRecord) extension.get().getValue())
                                .getSweAbstractSimpleTypeFromFields(SweText.class)));
                    } else if (extension.get().getValue() instanceof SweText) {
                        procedureDescription.addOffering(SosOffering.from((SweText) extension.get().getValue()));
                    }
                }
            }
            if (request.hasExtension(SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES)) {
                Optional<Extension<?>> extension =
                        request.getExtension(SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES);
                if (extension.isPresent()) {
                    if (extension.get().getValue() instanceof SweText) {
                        SweText sweText = (SweText) extension.get().getValue();
                        if (sweText.isSetName()) {
                            procedureDescription.setParentProcedure(
                                    new ReferenceType(sweText.getValue(), sweText.getName().getValue()));
                        } else {
                            procedureDescription.setParentProcedure(new ReferenceType(sweText.getValue()));
                        }
                    }
                }
            }
            if (request.hasExtension(SensorMLConstants.INSITU)) {
                procedureDescription.setInsitu(request.getBooleanExtension(SensorMLConstants.INSITU));
            } else if (request.hasExtension(SensorMLConstants.REMOTE)) {
                procedureDescription.setInsitu(!request.getBooleanExtension(SensorMLConstants.REMOTE));
            }
            if (request.hasExtension(SensorMLConstants.MOBILE)) {
                procedureDescription.setMobile(request.getBooleanExtension(SensorMLConstants.MOBILE));
            } else if (request.hasExtension(SensorMLConstants.FIXED)) {
                procedureDescription.setMobile(!request.getBooleanExtension(SensorMLConstants.FIXED));
            } else if (request.hasExtension(SensorMLConstants.STATIONARY)) {
                procedureDescription.setMobile(!request.getBooleanExtension(SensorMLConstants.STATIONARY));
            }
            if (request.getProcedureDescription().getProcedureDescription() instanceof AbstractSensorML) {
                AbstractSensorML abstractSensorML =
                        (AbstractSensorML) request.getProcedureDescription().getProcedureDescription();
                if (abstractSensorML instanceof SensorML && ((SensorML) abstractSensorML).isWrapper()) {
                    for (AbstractProcess abstractProcess : ((SensorML) abstractSensorML).getMembers()) {
                        checkCapabilities(request.getProcedureDescription(), abstractProcess);
                        checkForMobileAndInsitu(request.getProcedureDescription(), abstractProcess);
                    }
                } else {
                    checkCapabilities(request.getProcedureDescription(), abstractSensorML);
                    checkForMobileAndInsitu(request.getProcedureDescription(), abstractSensorML);
                }

                if (abstractSensorML instanceof AbstractProcessV20) {
                    AbstractProcessV20 abstractProcessV20 = (AbstractProcessV20) abstractSensorML;
                    if (abstractProcessV20.isSetTypeOf()) {
                        request.getProcedureDescription().setTypeOf(abstractProcessV20.getTypeOf());
                    }
                    if (abstractProcessV20 instanceof AbstractPhysicalProcess
                            && ((AbstractPhysicalProcess) abstractProcessV20).isSetAttachedTo()) {
                        procedureDescription
                                .setParentProcedure(((AbstractPhysicalProcess) abstractProcessV20).getAttachedTo());
                        Set<String> offeringsForProcedure = getCache().getOfferingsForProcedure(
                                ((AbstractPhysicalProcess) abstractProcessV20).getAttachedTo().getHref());
                        Map<String, Boolean> containedOfferings = new LinkedHashMap<>();
                        for (String string : offeringsForProcedure) {
                            containedOfferings.put(string, false);
                        }
                        if (procedureDescription.isSetOfferings()) {
                            for (SosOffering off : procedureDescription.getOfferings()) {
                                if (offeringsForProcedure.contains(off.getIdentifier())) {
                                    off.setParentOfferingFlag(true);
                                    containedOfferings.put(off.getIdentifier(), true);
                                }
                            }
                            containedOfferings.entrySet().forEach(e -> {
                                if (e.getValue()) {
                                    SosOffering sosOff = new SosOffering(e.getKey(),
                                            getCache().getOfferingHumanReadableNameForIdentifier(e.getKey()));
                                    sosOff.setParentOfferingFlag(true);
                                    procedureDescription.addOffering(sosOff);
                                }
                            });
                        } else {
                            offeringsForProcedure.forEach(o -> {
                                SosOffering sosOff = new SosOffering(o,
                                        getCache().getOfferingHumanReadableNameForIdentifier(
                                                ((AbstractPhysicalProcess) abstractProcessV20).getAttachedTo()
                                                        .getHref()));
                                sosOff.setParentOfferingFlag(true);
                                procedureDescription.addOffering(sosOff);
                            });
                        }
                    }
                    if (abstractProcessV20.isSetSmlFeatureOfInterest()) {
                        if (abstractProcessV20.getSmlFeatureOfInterest().isSetFeaturesOfInterest()) {
                            procedureDescription.addFeaturesOfInterest(
                                    abstractProcessV20.getSmlFeatureOfInterest().getFeaturesOfInterest());
                        }
                        if (abstractProcessV20.getSmlFeatureOfInterest().isSetFeaturesOfInterestMap()) {
                            procedureDescription.addFeaturesOfInterestMap(
                                    abstractProcessV20.getSmlFeatureOfInterest().getFeaturesOfInterestMap());
                        }
                    }
                }
                if (abstractSensorML instanceof System || abstractSensorML instanceof ProcessChain
                        || abstractSensorML instanceof PhysicalSystem
                        || abstractSensorML instanceof AggregateProcess) {
                    procedureDescription.setIsAggregation(true);
                }
            }
        }
        super.preProcessRequest(request);
    }

    private void checkForMobileAndInsitu(SosProcedureDescription<?> procedureDescription,
            AbstractSensorML abstractSensorML) {
        if (abstractSensorML.isSetMobile()) {
            procedureDescription.setMobile(abstractSensorML.getMobile());
        }
        if (abstractSensorML.isSetInsitu()) {
            procedureDescription.setInsitu(abstractSensorML.getInsitu());
        }
    }

    private void checkCapabilities(SosProcedureDescription<?> procedureDescription,
            AbstractSensorML abstractSensorML) {
        if (abstractSensorML.isSetCapabilities()) {
            for (SmlCapabilities caps : abstractSensorML.getCapabilities()) {
                if (SensorMLConstants.ELEMENT_NAME_OFFERINGS.equals(caps.getName())) {
                    if (caps.isSetAbstractDataRecord()) {
                        procedureDescription.addOfferings(SosOffering
                                .fromSet(caps.getDataRecord().getSweAbstractSimpleTypeFromFields(SweText.class)));
                    } else if (caps.isSetAbstractDataComponents()) {
                        procedureDescription.addOfferings(
                                SosOffering.fromSet(caps.getSweAbstractSimpleTypeFromFields(SweText.class)));
                    }
                } else if (SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES.equals(caps.getName())) {
                    procedureDescription.setParentProcedure(
                            new ReferenceType(parseCapabilitiesMetadata(caps).keySet().iterator().next()));
                } else if (SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST.equals(caps.getName())) {
                    procedureDescription.addFeaturesOfInterest(parseCapabilitiesMetadata(caps).keySet());
                }
            }
        }
    }

    private void checkObservableProperty(List<String> observableProperty) throws OwsExceptionReport {
        if (observableProperty == null || observableProperty.isEmpty()) {
            throw new MissingObservedPropertyParameterException();
            // } else {
            // TODO: check with existing and/or defined in outputs
        }
        checkReservedCharacter(observableProperty, Sos2Constants.InsertSensorParams.observableProperty.name());
    }

    private void checkFeatureOfInterestTypes(Set<String> featureOfInterestTypes) throws OwsExceptionReport {
        if (featureOfInterestTypes != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            Collection<String> validFeatureOfInterestTypes = getCache().getFeatureOfInterestTypes();
            for (String featureOfInterestType : featureOfInterestTypes) {
                if (featureOfInterestType.isEmpty()) {
                    exceptions.add(new MissingFeatureOfInterestTypeException());
                } else if (!validFeatureOfInterestTypes.contains(featureOfInterestType)) {
                    exceptions.add(new InvalidFeatureOfInterestTypeException(featureOfInterestType));
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    private void checkObservationTypes(Set<String> observationTypes) throws OwsExceptionReport {
        if (observationTypes != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            for (String observationType : observationTypes) {
                try {
                    checkObservationType(observationType, Sos2Constants.InsertSensorParams.observationType.name());
                } catch (OwsExceptionReport e) {
                    exceptions.add(e);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    private void checkAndSetAssignedProcedureID(InsertSensorRequest request, String generatedId)
            throws OwsExceptionReport {
        if (request.getProcedureDescription().isSetIdentifier()) {
            request.setAssignedProcedureIdentifier(request.getProcedureDescription().getIdentifier());
        } else {
            request.setAssignedProcedureIdentifier(
                    IdGenerator.generate(request.getProcedureDescription().toString()));
        }
        // check for reserved character
        checkReservedCharacter(request.getAssignedProcedureIdentifier(),
                Sos2Constants.InsertSensorParams.procedureIdentifier);
    }

    private void checkAndSetAssignedOfferings(InsertSensorRequest request, String generatedId)
            throws OwsExceptionReport {
        Set<SosOffering> sosOfferings = request.getProcedureDescription().getOfferings();

        // if no offerings are assigned, generate one
        if (CollectionHelper.isEmpty(sosOfferings)) {
            sosOfferings = new HashSet<>(0);
            sosOfferings.add(new SosOffering(request.getAssignedProcedureIdentifier()));
        }
        // check for reserved character
        for (SosOffering offering : sosOfferings) {
            checkReservedCharacter(offering.getIdentifier(), Sos2Constants.InsertSensorParams.offeringIdentifier);
        }
        request.setAssignedOfferings(new ArrayList<>(sosOfferings));
    }

    private void checkProcedureAndOfferingCombination(InsertSensorRequest request) throws OwsExceptionReport {
        for (SosOffering offering : request.getAssignedOfferings()) {
            if (!offering.isParentOffering()
                    && getCache().getPublishedOfferings().contains(offering.getIdentifier())) {
                throw new InvalidParameterValueException().at(Sos2Constants.InsertSensorParams.offeringIdentifier)
                        .withMessage(
                                "The offering with the identifier '%s' still exists in this service "
                                + "and it is not allowed to insert more than one procedure to an offering!",
                                offering.getIdentifier());
            }
        }
    }

    private void checkParentChildProcedures(SosProcedureDescription<?> procedureDescription, String assignedIdentifier)
            throws CodedException {
        if (procedureDescription.isSetChildProcedures()) {
            for (AbstractSensorML child : procedureDescription.getChildProcedures()) {
                if (child.getIdentifier().equalsIgnoreCase(assignedIdentifier)) {
                    throw new InvalidParameterValueException().at("childProcdureIdentifier").withMessage(
                            "The procedure with the identifier '%s' is linked to itself as child procedure !",
                            procedureDescription.getIdentifier());
                }
            }
        }
        if (procedureDescription.isSetParentProcedure()) {
            if (procedureDescription.getParentProcedure().getHref().equals(assignedIdentifier)) {
                throw new InvalidParameterValueException().at("parentProcdureIdentifier").withMessage(
                        "The procedure with the identifier '%s' is linked to itself as parent procedure !",
                        procedureDescription.getIdentifier());
            }
        }

    }

    private void checkTypeOf(SosProcedureDescription<?> procedureDescription) throws OwsExceptionReport {
        // if href is URL, remove typeOf
        // else href empty/title.xml/PREFIX/title.xml check title if exists
        if (procedureDescription.isSetTypeOf()) {
            ReferenceType typeOf = procedureDescription.getTypeOf();
            boolean referenced = false;
            if (typeOf.isSetHref()) {
                if (typeOf.getHref().startsWith("http") && !typeOf.getHref().equals(typeOf.getTitle())) {
                    procedureDescription.setTypeOf(null);
                    referenced = true;
                }
            }
            if (!referenced) {
                if (typeOf.isSetTitle()) {
                    String title = typeOf.getTitle();
                    if (!getCache().hasProcedure(title)) {
                        throw new InvalidParameterValueException(ABSTRACT_PROCESS_TYPE_OF_TITLE, title);
                    }
                } else {
                    throw new MissingParameterValueException(ABSTRACT_PROCESS_TYPE_OF_TITLE);
                }
            }
        }
    }

    /**
     * Process standard formatted capabilities insertion metadata into a map
     * (key=identifier, value=name)
     *
     * @param caps
     *            The capabilities to examine
     * @return Map of insertion metadata (key=identifier, value=name)
     * @throws DecodingException
     *             thrown if the DataRecord fields are in an incorrect format
     */
    private Map<String, String> parseCapabilitiesMetadata(SmlCapabilities caps) {
        final Map<String, String> map = Maps.newHashMapWithExpectedSize(caps.getDataRecord().getFields().size());
        for (final SweField sosSweField : caps.getDataRecord().getFields()) {
            if (sosSweField.getElement() instanceof SweText) {
                final SweText sosSweText = (SweText) sosSweField.getElement();
                if (sosSweText.isSetValue()) {
                    map.put(sosSweText.getValue(), sosSweField.getName().getValue());
                }
            }
        }
        return map;
    }
}
