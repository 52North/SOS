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
package org.n52.sos.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.ProcessModel;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sensorML.elements.AbstractSmlDocumentation;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCharacteristics;
import org.n52.sos.ogc.sensorML.elements.SmlClassifier;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentation;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentationList;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentationListMember;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.wml.ObservationProcess;
import org.n52.sos.ogc.wml.WaterMLConstants;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @since 4.0.0
 * 
 */
public class WaterMLv20SensorMLv101Converter implements Converter<SosProcedureDescription, SosProcedureDescription> {

    /*
     * TODO - Add function to read mapping information
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMLv20SensorMLv101Converter.class);

    private static final List<ConverterKeyType> CONVERTER_KEY_TYPES = CollectionHelper.list(
            new ConverterKeyType(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL), 
            new ConverterKeyType(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKeyType(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL,WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING),
            new ConverterKeyType(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE,WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));

    public WaterMLv20SensorMLv101Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public List<ConverterKeyType> getConverterKeyTypes() {
        return Collections.unmodifiableList(CONVERTER_KEY_TYPES);
    }

    @Override
    public SosProcedureDescription convert(final SosProcedureDescription objectToConvert) throws ConverterException {
        if (objectToConvert.getDescriptionFormat().equals(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING)) {
            return convertWML2ObservationProcessToSensorML101(objectToConvert);
        } else if (objectToConvert.getDescriptionFormat().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL)
                || objectToConvert.getDescriptionFormat().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE)) {
            return convertSensorML101ToWML2ObservationProcess(objectToConvert);
        }
        return null;
    }

    private SosProcedureDescription convertSensorML101ToWML2ObservationProcess(final SosProcedureDescription objectToConvert) {
        final ObservationProcess observationProcess = new ObservationProcess();

        if (objectToConvert instanceof SensorML) {
            final SensorML sensorML = (SensorML) objectToConvert;
            if (sensorML.isWrapper()) {
                for (final AbstractProcess member : sensorML.getMembers()) {
                    // TODO get values and add to obsProcess
                    if (member.isSetIdentifier()) {
                        observationProcess.setIdentifier(member.getIdentifierCodeWithAuthority());
                    }
                    if (member instanceof System) {
                        convertSystemToObservationProcess(observationProcess, (System) member);
                    } else if (member instanceof ProcessModel) {
                        convertProcessModelToObservationProcess(observationProcess, (ProcessModel) member);
                    }
                }
            }
            // TODO add 'else' to get values and add to obsProcess from sensorML
        } else {
            observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_UNKNOWN));
        }
        observationProcess.setIdentifier(objectToConvert.getIdentifierCodeWithAuthority());
        observationProcess.setDescriptionFormat(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING);
        return observationProcess;
    }

    private SosProcedureDescription convertWML2ObservationProcessToSensorML101(final SosProcedureDescription objectToConvert) {
        final SensorML sensorML = new SensorML();
        if (objectToConvert instanceof ObservationProcess) {
            final ObservationProcess observationProcess = new ObservationProcess();
            if (observationProcess.isSetProcessType()) {
                AbstractProcess process;
                if (checkProcessType(observationProcess.getProcessType(), WaterMLConstants.PROCESS_TYPE_SENSOR)) {
                    process = convertObservationProcessToSystem(observationProcess);
                } else {
                    process = convertObservationProcessToProcessModel(observationProcess);
                }
                process.setClassifications(convertProcessTypeToClassification(observationProcess.getProcessType()));
                sensorML.addMember(process);
            }
        } else {
            sensorML.addIdentifier(createUniqueIDIdentifier(objectToConvert.getIdentifier()));
        }
        return sensorML;
    }

    private System convertObservationProcessToSystem(final ObservationProcess observationProcess) {
        final System system = new System();
        system.addIdentifier(createUniqueIDIdentifier(observationProcess.getIdentifier()));
        // TODO add all other stuff
        // if (observationProcess.isSetVerticalDatum()) {
        // }

        return system;
    }

    private ProcessModel convertObservationProcessToProcessModel(final ObservationProcess observationProcess) {
        final ProcessModel processModel = new ProcessModel();
        processModel.addIdentifier(createUniqueIDIdentifier(observationProcess.getIdentifier()));
        // duration is not valid for validTime element
        observationProcess.getAggregationDuration();
        if (observationProcess.isSetComments()) {
            processModel.addDocumentation(convertCommentsToDocumentation(observationProcess.getComments()));
        }
        if (observationProcess.isSetInputs()) {
            processModel.setInputs(convertObservationProcessInputsToSMLInputs(observationProcess.getInputs()));
        }

        observationProcess.getOriginatingProcess();
        observationProcess.getParameters();
        if (observationProcess.isSetProcessReference()) {
            processModel.addDocumentation(convertProcessReferenceToDocumentation(observationProcess
                    .getProcessReference()));
        }

        observationProcess.getVerticalDatum();

        // TODO add all other stuff
        return processModel;
    }

    private boolean checkProcessType(final ReferenceType processType, final String processTypeName) {
        if (processType.isSetHref()) {
            return processType.getHref().equals(processTypeName);
        }
        return false;
    }

    private SmlIdentifier createUniqueIDIdentifier(final String procedureIdentifier) {
        return new SmlIdentifier("uniqueID", "urn:ogc:def:identifier:OGC:uniqueID", procedureIdentifier);
    }

    private List<SmlClassifier> convertProcessTypeToClassification(final ReferenceType processType) {
        final String definition = "urn:ogc:def:classifier:OGC:1.0:sensorType";
        final SmlClassifier sosSMLClassifier = new SmlClassifier(processType.getTitle(), definition, null, processType.getHref());
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

    private List<SmlIo<?>> convertObservationProcessInputsToSMLInputs(final List<ReferenceType> inputs) {
        final List<SmlIo<?>> smlInputs = new ArrayList<SmlIo<?>>(inputs.size());
        for (final ReferenceType referenceType : inputs) {
            final SmlIo<String> io = new SmlIo<String>();
            if (referenceType.isSetTitle()) {
                io.setIoName(referenceType.getTitle());
            }
            final SweObservableProperty ioValue = new SweObservableProperty();
            ioValue.setDefinition(referenceType.getHref());
            io.setIoValue(ioValue);
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

    private void convertAbstractSensorMLToObservationProcess(final ObservationProcess observationProcess,
            final AbstractSensorML abstractSensorML) {
        if (abstractSensorML.isSetCapabilities()) {
            convertSMLCapabilitiesToObservationProcessParameter(observationProcess, abstractSensorML.getCapabilities());
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
            convertSMLDocumentationToObservationProcessComment(observationProcess, abstractSensorML.getDocumentation());
        }
        if (abstractSensorML.isSetIdentifications()) {
            convertSMLIdentificationsToObservationProcessParameter(observationProcess,
                    abstractSensorML.getIdentifications());
        }

    }

    private void convertAbstractProcessToObservationProcess(final ObservationProcess observationProcess,
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

    private void convertSystemToObservationProcess(final ObservationProcess observationProcess, final System system) {
        observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_SENSOR));
        convertAbstractSensorMLToObservationProcess(observationProcess, system);
        convertAbstractProcessToObservationProcess(observationProcess, system);
        // TODO the rest
    }

    private void convertProcessModelToObservationProcess(final ObservationProcess observationProcess,
            final ProcessModel processModel) {
        observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
        convertAbstractSensorMLToObservationProcess(observationProcess, processModel);
        convertAbstractProcessToObservationProcess(observationProcess, processModel);
        // TODO the rest
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
            final ReferenceType refType = new ReferenceType(
            		classifier.isSetDefinition()?
            				classifier.getDefinition():
            					"http://example.com/error/classfier_definition_not_set");
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
//            if (identifier.getDefinition().contains("name")) {
//                CodeType codeType = new CodeType(identifier.getValue());
//                codeType.setCodeSpace(identifier.getDefinition());
//                observationProcess.addName(codeType);
//            }
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
            final List<String> parameters) {
        // TODO Auto-generated method stub
    }

    private void convertSMLOutputsToObservationProcessParameter(final ObservationProcess observationProcess,
            final List<SmlIo<?>> outputs) {
        for (final SmlIo<?> sosSMLIo : outputs) {
            final ReferenceType referenceType = new ReferenceType("output");
            final NamedValue<String> namedValueProperty = new NamedValue<String>();
            namedValueProperty.setName(referenceType);
            namedValueProperty.setValue(new TextValue(sosSMLIo.getIoValue().getDefinition()));
            // NamedValuePair namedValueProperty =
            // getNamedValuePairForSosSweAbstractSimpleType(sosSMLIo.getIoValue());
            // namedValueProperty.getName().setTitle(sosSMLIo.getIoName());
            observationProcess.addParameter(namedValueProperty);
        }
    }

    private List<ReferenceType> convertSMLInputsToObservationProcessInputs(final List<SmlIo<?>> inputs) {
        final List<ReferenceType> oPInputs = new ArrayList<ReferenceType>(inputs.size());
        for (final SmlIo<?> sosSMLIo : inputs) {
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
            final NamedValue<String> namedValueProperty =
                    getNamedValuePairForSosSweAbstractSimpleType((SweAbstractSimpleType) field.getElement(), field.getName().getValue());
            namedValueProperty.getName().setTitle(field.getName().getValue());
            return namedValueProperty;
        }
        return null;
    }

    private NamedValue<String> getNamedValuePairForSosSweAbstractSimpleType(final SweAbstractSimpleType<?> element, String name) {
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

}
