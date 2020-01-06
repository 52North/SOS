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
package org.n52.sos.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.series.wml.ObservationProcess;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.elements.AbstractSmlDocumentation;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilities;
import org.n52.shetland.ogc.sensorML.elements.SmlCharacteristics;
import org.n52.shetland.ogc.sensorML.elements.SmlClassifier;
import org.n52.shetland.ogc.sensorML.elements.SmlDocumentation;
import org.n52.shetland.ogc.sensorML.elements.SmlDocumentationList;
import org.n52.shetland.ogc.sensorML.elements.SmlDocumentationListMember;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sensorML.elements.SmlParameter;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.shetland.ogc.swe.simpleType.SweObservableProperty;

/**
 * Abstract converter for WaterML {@link ObservationProcess} and SensorML
 * {@link AbstractProcess}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public abstract class AbstractWaterMLv20SensorMLConverter
        extends
        ProcedureDescriptionConverter {

    protected AbstractProcess convertObservationProcessToAbstractProcess(ObservationProcess observationProcess,
            AbstractProcess abstractProcess) {
        abstractProcess.addIdentifier(createUniqueIDIdentifier(observationProcess.getIdentifier()));
        // duration is not valid for validTime element
        // observationProcess.getAggregationDuration();
        if (observationProcess.isSetComments()) {
            abstractProcess.addDocumentation(convertCommentsToDocumentation(observationProcess.getComments()));
        }
        if (observationProcess.isSetInputs()) {
            abstractProcess.setInputs(convertObservationProcessInputsToSMLInputs(observationProcess.getInputs()));
        }

        // observationProcess.getOriginatingProcess();
        // observationProcess.getParameters();
        if (observationProcess.isSetProcessReference()) {
            abstractProcess.addDocumentation(
                    convertProcessReferenceToDocumentation(observationProcess.getProcessReference()));
        }
        // observationProcess.getVerticalDatum();
        abstractProcess.setClassifications(convertProcessTypeToClassification(observationProcess.getProcessType()));
        return abstractProcess;
    }

    protected boolean checkProcessType(final ReferenceType processType, final String processTypeName) {
        if (processType.isSetHref()) {
            return processType.getHref().equals(processTypeName);
        }
        return false;
    }

    protected SmlIdentifier createUniqueIDIdentifier(final String procedureIdentifier) {
        return new SmlIdentifier("uniqueID", "urn:ogc:def:identifier:OGC:uniqueID", procedureIdentifier);
    }

    private List<SmlClassifier> convertProcessTypeToClassification(final ReferenceType processType) {
        final String definition = "urn:ogc:def:classifier:OGC:1.0:sensorType";
        final SmlClassifier sosSMLClassifier =
                new SmlClassifier(processType.getTitle(), definition, null, processType.getHref());
        return Collections.singletonList(sosSMLClassifier);
    }

    private AbstractSmlDocumentation convertCommentsToDocumentation(final List<String> comments) {
        // TODO check for correctness
        if (comments.size() > 1) {
            final SmlDocumentation documentation = new SmlDocumentation();
            documentation.setDescription(comments.get(0));
            return documentation;
        } else {
            final SmlDocumentationList documentationList = new SmlDocumentationList();
            for (final String comment : comments) {
                final SmlDocumentationListMember member = new SmlDocumentationListMember();
                final SmlDocumentation documentation = new SmlDocumentation();
                documentation.setDescription(comment);
                member.setDocumentation(documentation);
                documentationList.addMember(member);
            }
            return documentationList;
        }
    }

    private List<SmlIo> convertObservationProcessInputsToSMLInputs(final List<ReferenceType> inputs) {
        final List<SmlIo> smlInputs = new ArrayList<>(inputs.size());
        for (final ReferenceType referenceType : inputs) {
            final SmlIo io = new SmlIo();
            if (referenceType.isSetTitle()) {
                io.setIoName(referenceType.getTitle());
            }
            final SweObservableProperty ioValue = new SweObservableProperty();
            ioValue.setDefinition(referenceType.getHref());
            io.setIoValue(ioValue);
            smlInputs.add(io);
        }
        return smlInputs;
    }

    private AbstractSmlDocumentation convertProcessReferenceToDocumentation(final ReferenceType processReference) {
        final SmlDocumentation documentation = new SmlDocumentation();
        final StringBuilder builder = new StringBuilder();
        builder.append(processReference.getHref());
        builder.append(";");
        builder.append(processReference.getTitle());
        documentation.setDescription(builder.toString());
        return documentation;
    }

    protected void convertAbstractSensorMLToObservationProcess(final ObservationProcess observationProcess,
            final AbstractSensorML abstractSensorML) {
        if (abstractSensorML.isSetCapabilities()) {
            convertSMLCapabilitiesToObservationProcessParameter(observationProcess,
                    abstractSensorML.getCapabilities());
        }
        if (abstractSensorML.isSetCharacteristics()) {
            convertSMLCharacteristicsToObservationProcessParameter(observationProcess,
                    abstractSensorML.getCharacteristics());
        }
        if (abstractSensorML.isSetClassifications()) {
            convertSMLClassificationsToObservationProcessParameter(observationProcess,
                    abstractSensorML.getClassifications());
        }
        if (abstractSensorML.isSetDocumentation()) {
            convertSMLDocumentationToObservationProcessComment(observationProcess,
                    abstractSensorML.getDocumentation());
        }
        if (abstractSensorML.isSetIdentifications()) {
            convertSMLIdentificationsToObservationProcessParameter(observationProcess,
                    abstractSensorML.getIdentifications());
        }

    }

    protected void convertAbstractProcessToObservationProcess(final ObservationProcess observationProcess,
            final AbstractProcess abstractProces) {
        if (abstractProces.isSetParameters()) {
            convertSMLParametersToObservationProcessParameter(observationProcess, abstractProces.getParameters());
        }
        if (abstractProces.isSetInputs()) {
            observationProcess.setInputs(convertSMLInputsToObservationProcessInputs(abstractProces.getInputs()));
        }
        if (abstractProces.isSetOutputs()) {
            convertSMLOutputsToObservationProcessParameter(observationProcess, abstractProces.getOutputs());
        }
    }

    private void convertSMLCharacteristicsToObservationProcessParameter(final ObservationProcess observationProcess,
            final List<SmlCharacteristics> characteristics) {
        for (final SmlCharacteristics characteristic : characteristics) {
            if (characteristic.isSetAbstractDataRecord() && characteristic.getDataRecord().isSetFields()) {
                for (final SweField field : characteristic.getDataRecord().getFields()) {
                    final NamedValue<String> namedValueProperty = convertSMLFieldToNamedValuePair(field);
                    if (namedValueProperty != null) {
                        observationProcess.addParameter(namedValueProperty);
                    }
                }
            }
        }
    }

    private void convertSMLClassificationsToObservationProcessParameter(final ObservationProcess observationProcess,
            final List<SmlClassifier> classifications) {
        for (final SmlClassifier classifier : classifications) {
            final NamedValue<String> namedValueProperty = new NamedValue<String>();
            // TODO What to do if optional value is not available?
            final ReferenceType refType = new ReferenceType(classifier.isSetDefinition() ? classifier.getDefinition()
                    : "http://example.com/error/classfier_definition_not_set");
            refType.setTitle(classifier.getName());
            namedValueProperty.setName(refType);
            namedValueProperty.setValue(new TextValue(classifier.getValue()));
            observationProcess.addParameter(namedValueProperty);
        }

    }

    private void convertSMLIdentificationsToObservationProcessParameter(final ObservationProcess observationProcess,
            final List<SmlIdentifier> identifications) {
        for (final SmlIdentifier identifier : identifications) {
            final NamedValue<String> namedValueProperty = new NamedValue<String>();
            final ReferenceType refType = new ReferenceType(identifier.getDefinition());
            refType.setTitle(identifier.getName());
            // TODO uncomment if supported
            // if (identifier.getDefinition().contains("name")) {
            // CodeType codeType = new CodeType(identifier.getValue());
            // codeType.setCodeSpace(identifier.getDefinition());
            // observationProcess.addName(codeType);
            // }
            namedValueProperty.setName(refType);
            namedValueProperty.setValue(new TextValue(identifier.getValue()));
            observationProcess.addParameter(namedValueProperty);
        }
    }

    private void convertSMLDocumentationToObservationProcessComment(final ObservationProcess observationProcess,
            final List<AbstractSmlDocumentation> documentation) {
        // TODO Auto-generated method stub
    }

    private void convertSMLParametersToObservationProcessParameter(final ObservationProcess observationProcess,
            final List<SmlParameter> parameters) {
        // TODO Auto-generated method stub
    }

    private void convertSMLOutputsToObservationProcessParameter(final ObservationProcess observationProcess,
            final List<SmlIo> outputs) {
        for (final SmlIo sosSMLIo : outputs) {
            final ReferenceType referenceType = new ReferenceType("output");
            final NamedValue<String> namedValueProperty = new NamedValue<String>();
            namedValueProperty.setName(referenceType);
            namedValueProperty.setValue(new TextValue(sosSMLIo.getIoValue().getDefinition()));
            observationProcess.addParameter(namedValueProperty);
        }
    }

    private List<ReferenceType> convertSMLInputsToObservationProcessInputs(final List<SmlIo> inputs) {
        final List<ReferenceType> oPInputs = new ArrayList<ReferenceType>(inputs.size());
        for (final SmlIo sosSMLIo : inputs) {
            final ReferenceType refType = new ReferenceType(sosSMLIo.getIoValue().getDefinition());
            refType.setTitle(sosSMLIo.getIoName());
            oPInputs.add(refType);
        }
        return oPInputs;
    }

    private void convertSMLCapabilitiesToObservationProcessParameter(final ObservationProcess observationProcess,
            final List<SmlCapabilities> capabilities) {
        for (final SmlCapabilities capability : capabilities) {
            if (capability.isSetAbstractDataRecord() && capability.getDataRecord().isSetFields()) {
                for (final SweField field : capability.getDataRecord().getFields()) {
                    final NamedValue<String> namedValueProperty = convertSMLFieldToNamedValuePair(field);
                    if (namedValueProperty != null) {
                        observationProcess.addParameter(namedValueProperty);
                    }
                }
            }
        }
    }

    private NamedValue<String> convertSMLFieldToNamedValuePair(final SweField field) {
        if (field.getElement() instanceof SweAbstractSimpleType) {
            final NamedValue<String> namedValueProperty = getNamedValuePairForSosSweAbstractSimpleType(
                    (SweAbstractSimpleType<?>) field.getElement(), field.getName().getValue());
            namedValueProperty.getName().setTitle(field.getName().getValue());
            return namedValueProperty;
        }
        return null;
    }

    private NamedValue<String> getNamedValuePairForSosSweAbstractSimpleType(final SweAbstractSimpleType<?> element,
            String name) {
        final NamedValue<String> namedValueProperty = new NamedValue<String>();
        final ReferenceType refType;
        if (element.isSetDefinition()) {
            refType = new ReferenceType(element.getDefinition());
        } else {
            refType = new ReferenceType(name);
        }
        namedValueProperty.setName(refType);
        if (element.isSetValue()) {
            namedValueProperty.setValue(new TextValue(element.getStringValue()));
        }
        return namedValueProperty;
    }

    protected void convertSensorMLToObservationProcess(ObservationProcess observationProcess,
            AbstractProcess abstractProcess) {
        convertAbstractSensorMLToObservationProcess(observationProcess, abstractProcess);
        convertAbstractProcessToObservationProcess(observationProcess, abstractProcess);
        // TODO the rest
    }
}
