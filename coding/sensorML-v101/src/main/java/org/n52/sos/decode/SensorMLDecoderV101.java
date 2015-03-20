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
package org.n52.sos.decode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import net.opengis.sensorML.x101.AbstractComponentType;
import net.opengis.sensorML.x101.AbstractDerivableComponentType;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.AbstractPureProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.CharacteristicsDocument.Characteristics;
import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList.Classifier;
import net.opengis.sensorML.x101.ComponentType;
import net.opengis.sensorML.x101.ComponentsDocument.Components;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList.Component;
import net.opengis.sensorML.x101.ContactDocument.Contact;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo.Address;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo.Phone;
import net.opengis.sensorML.x101.ContactListDocument.ContactList;
import net.opengis.sensorML.x101.DocumentationDocument.Documentation;
import net.opengis.sensorML.x101.HistoryDocument.History;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.InputsDocument.Inputs;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.KeywordsDocument.Keywords;
import net.opengis.sensorML.x101.MethodPropertyType;
import net.opengis.sensorML.x101.OnlineResourceDocument.OnlineResource;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.ParametersDocument.Parameters;
import net.opengis.sensorML.x101.PersonDocument.Person;
import net.opengis.sensorML.x101.PositionDocument.Position;
import net.opengis.sensorML.x101.ProcessModelType;
import net.opengis.sensorML.x101.ResponsiblePartyDocument.ResponsibleParty;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SmlLocation.SmlLocation2;
import net.opengis.sensorML.x101.SystemDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.TermDocument.Term;
import net.opengis.sensorML.x101.ValidTimeDocument.ValidTime;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractSensorMLDecoder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractComponent;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.ProcessMethod;
import org.n52.sos.ogc.sensorML.ProcessModel;
import org.n52.sos.ogc.sensorML.RulesDefinition;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.SmlContact;
import org.n52.sos.ogc.sensorML.SmlContactList;
import org.n52.sos.ogc.sensorML.SmlPerson;
import org.n52.sos.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sensorML.elements.AbstractSmlDocumentation;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCharacteristics;
import org.n52.sos.ogc.sensorML.elements.SmlClassifier;
import org.n52.sos.ogc.sensorML.elements.SmlComponent;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sensorML.elements.SmlLocation;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Point;

/**
 * @since 4.0.0
 * 
 */
public class SensorMLDecoderV101 extends AbstractSensorMLDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMLDecoderV101.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(SensorMLConstants.NS_SML,
            SensorMLDocument.class, SystemDocument.class, SystemType.class, ProcessModelType.class);

    private static final Set<String> SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS = Collections
            .singleton(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);

    private static final Set<String> REMOVABLE_CAPABILITIES_NAMES = Sets.newHashSet(
            SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES, SensorMLConstants.ELEMENT_NAME_OFFERINGS);

    private static final Set<String> REMOVABLE_COMPONENTS_ROLES = Collections
            .singleton(SensorMLConstants.ELEMENT_NAME_CHILD_PROCEDURES);

    public SensorMLDecoderV101() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.singletonMap(SupportedTypeKey.ProcedureDescriptionFormat,
                SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS);
    }

    @Override
    public AbstractSensorML decode(final XmlObject element) throws OwsExceptionReport {
        if (element instanceof SensorMLDocument) {
            return parseSensorML((SensorMLDocument) element);
        } else if (element instanceof SystemDocument) {
            return parseSystem(((SystemDocument) element).getSystem());
        } else if (element instanceof SystemType) {
            return parseSystem((SystemType) element);
        } else if (element instanceof ProcessModelType) {
            return parseProcessModel((ProcessModelType) element);
        } else {
            throw new UnsupportedDecoderInputException(this, element);
        }
    }

    private SensorML parseSensorML(final SensorMLDocument xbSensorML) throws OwsExceptionReport {
        final SensorML sensorML = new SensorML();
        // get member process
        for (final Member xbMember : xbSensorML.getSensorML().getMemberArray()) {
            if (xbMember.getProcess() != null) {
                if (xbMember.getProcess() instanceof AbstractProcessType) {
                    final AbstractProcessType xbAbstractProcess = xbMember.getProcess();
                    AbstractProcess abstractProcess = null;
                    if (xbAbstractProcess.schemaType() == SystemType.type) {
                        abstractProcess = parseSystem((SystemType) xbAbstractProcess);
                    } else if (xbAbstractProcess.schemaType() == ProcessModelType.type) {
                        abstractProcess = parseProcessModel((ProcessModelType) xbAbstractProcess);
                    } else if (xbAbstractProcess.schemaType() == ComponentType.type) {
                        abstractProcess = parseComponent((ComponentType) xbAbstractProcess);
                    } else {
                        throw new InvalidParameterValueException().at(XmlHelper.getLocalName(xbMember)).withMessage(
                                "The process of a member of the SensorML Document (%s) is not supported!",
                                xbMember.getProcess().getDomNode().getNodeName());
                    }
                    sensorML.addMember(abstractProcess);
                } else {
                    throw new InvalidParameterValueException().at(XmlHelper.getLocalName(xbMember)).withMessage(
                            "The process of a member of the SensorML Document (%s) is not supported!",
                            xbMember.getProcess().getDomNode().getNodeName());
                }
            } else {
                throw new InvalidParameterValueException().at(XmlHelper.getLocalName(xbMember)).withMessage(
                        "The process of a member of the SensorML Document is null (%s)!", xbMember.getProcess());
            }
        }
        sensorML.setSensorDescriptionXmlString(xbSensorML.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
        return sensorML;
    }

    private void parseAbstractProcess(final AbstractProcessType xbAbstractProcess,
            final AbstractProcess abstractProcess) throws OwsExceptionReport {
        if (xbAbstractProcess.getId() != null) {
            abstractProcess.setGmlId(xbAbstractProcess.getId());
        }
        if (xbAbstractProcess.getIdentificationArray() != null) {
            parseIdentifications(abstractProcess, xbAbstractProcess.getIdentificationArray());
        }
        if (xbAbstractProcess.getClassificationArray() != null) {
            abstractProcess.setClassifications(parseClassification(xbAbstractProcess.getClassificationArray()));
        }
        if (xbAbstractProcess.getCharacteristicsArray() != null) {
            abstractProcess.setCharacteristics(parseCharacteristics(xbAbstractProcess.getCharacteristicsArray()));
        }
        if (xbAbstractProcess.getCapabilitiesArray() != null) {
            parseCapabilities(abstractProcess, xbAbstractProcess.getCapabilitiesArray());
            final List<Integer> capsToRemove = checkCapabilitiesForRemoval(xbAbstractProcess.getCapabilitiesArray());
            for (final Integer integer : capsToRemove) {
                xbAbstractProcess.removeCapabilities(integer);
            }
        }
        if (xbAbstractProcess.isSetDescription()) {
            abstractProcess.addDescription(xbAbstractProcess.getDescription().getStringValue());
        }
        if (xbAbstractProcess.isSetValidTime()) {
            abstractProcess.setValidTime(parseValidTime(xbAbstractProcess.getValidTime()));
        }
        if (xbAbstractProcess.getContactArray() != null) {
            abstractProcess.setContact(parseContact(xbAbstractProcess.getContactArray()));
        }
        if (xbAbstractProcess.getDocumentationArray() != null) {
            abstractProcess.setDocumentation(parseDocumentation(xbAbstractProcess.getDocumentationArray()));
        }
        if (xbAbstractProcess.getHistoryArray() != null) {
            abstractProcess.setHistory(parseHistory(xbAbstractProcess.getHistoryArray()));
        }
        if (xbAbstractProcess.getKeywordsArray() != null) {
            abstractProcess.setKeywords(parseKeywords(xbAbstractProcess.getKeywordsArray()));
        }
        if (xbAbstractProcess.getNameArray() != null) {
            final int length = xbAbstractProcess.getNameArray().length;
            for (int i = 0; i < length; i++) {
                final Object decodedElement = CodingHelper.decodeXmlElement(xbAbstractProcess.getNameArray(i));
                if (decodedElement instanceof CodeType) {
                    abstractProcess.addName((CodeType) decodedElement);
                }
            }
        }
    }

    private void parseAbstractDerivableComponent(final AbstractDerivableComponentType xbAbstractDerivableComponent,
            final AbstractComponent abstractComponent) throws OwsExceptionReport {
        if (xbAbstractDerivableComponent.isSetPosition()) {
            abstractComponent.setPosition(parsePosition(xbAbstractDerivableComponent.getPosition()));
        }
        if (xbAbstractDerivableComponent.isSetSmlLocation()) {
            abstractComponent.setLocation(parseLocation(xbAbstractDerivableComponent.getSmlLocation()));
        }
        // TODO ...
    }

    private void parseAbstractComponent(final AbstractComponentType xbAbstractComponent,
            final AbstractProcess abstractProcess) throws OwsExceptionReport {
        if (xbAbstractComponent.isSetInputs()) {
            abstractProcess.setInputs(parseInputs(xbAbstractComponent.getInputs()));
        }
        if (xbAbstractComponent.isSetOutputs()) {
            abstractProcess.setOutputs(parseOutputs(xbAbstractComponent.getOutputs()));
        }
        if (xbAbstractComponent.isSetParameters()) {
            abstractProcess.setParameters(parseParameters(xbAbstractComponent.getParameters()));
        }
    }

    private void parseAbstractPureProcess(final AbstractPureProcessType xbAbstractPureProcess,
            final ProcessModel processModel) throws OwsExceptionReport {
        if (xbAbstractPureProcess.isSetInputs()) {
            processModel.setInputs(parseInputs(xbAbstractPureProcess.getInputs()));
        }
        if (xbAbstractPureProcess.isSetOutputs()) {
            processModel.setOutputs(parseOutputs(xbAbstractPureProcess.getOutputs()));
        }
        if (xbAbstractPureProcess.isSetParameters()) {
            processModel.setParameters(parseParameters(xbAbstractPureProcess.getParameters()));
        }

    }

    private System parseSystem(final SystemType xbSystemType) throws OwsExceptionReport {
        return parseSystem(xbSystemType, new System());
    }

    private System parseSystem(final SystemType xbSystemType, final System system) throws OwsExceptionReport {
        parseAbstractProcess(xbSystemType, system);
        parseAbstractComponent(xbSystemType, system);
        parseAbstractDerivableComponent(xbSystemType, system);
        if (xbSystemType.isSetComponents() && xbSystemType.getComponents().isSetComponentList()) {
            system.addComponents(parseComponents(xbSystemType.getComponents()));
            final List<Integer> compsToRemove =
                    checkComponentsForRemoval(xbSystemType.getComponents().getComponentList());
            for (final Integer integer : compsToRemove) {
                xbSystemType.getComponents().getComponentList().removeComponent(integer);
            }
            checkAndRemoveEmptyComponents(xbSystemType);
        }
        final String xmlDescription = addSensorMLWrapperForXmlDescription(xbSystemType);
        system.setSensorDescriptionXmlString(xmlDescription);
        return system;
    }

    private AbstractProcess parseComponent(final ComponentType componentType) throws OwsExceptionReport {
        final org.n52.sos.ogc.sensorML.Component component = new org.n52.sos.ogc.sensorML.Component();
        parseAbstractProcess(componentType, component);
        parseAbstractDerivableComponent(componentType, component);
        parseAbstractComponent(componentType, component);
        if (componentType.isSetPosition()) {
            component.setPosition(parsePosition(componentType.getPosition()));
        }
        component.setSensorDescriptionXmlString(addSensorMLWrapperForXmlDescription(componentType));
        return component;
    }

    private ProcessModel parseProcessModel(final ProcessModelType xbProcessModel) throws OwsExceptionReport {
        final ProcessModel processModel = new ProcessModel();
        parseAbstractProcess(xbProcessModel, processModel);
        parseAbstractPureProcess(xbProcessModel, processModel);
        if (xbProcessModel.getMethod() != null) {
            processModel.setMethod(parseProcessMethod(xbProcessModel.getMethod()));
        }
        processModel.setSensorDescriptionXmlString(addSensorMLWrapperForXmlDescription(xbProcessModel));
        return processModel;
    }

    private ProcessMethod parseProcessMethod(final MethodPropertyType method) throws CodedException {
        if (method.isSetHref()) {
            final ProcessMethod processMethod = new ProcessMethod(method.getHref());
            if (method.isSetTitle()) {
                processMethod.setTitle(method.getTitle());
            }
            if (method.isSetRole()) {
                processMethod.setRole(method.getRole());
            }
            return processMethod;
        } else if (method.isSetProcessMethod()) {
            final ProcessMethod processMethod =
                    new ProcessMethod(parseRulesDefinition(method.getProcessMethod().getRules().getRulesDefinition()));
            // TODO implement parsing of sml:ProcessMethod
            return processMethod;
        }
        throw new NoApplicableCodeException().at("method").withMessage(
                "The sml:method should contain a xlink:href attribut or a sml:ProcessMethod element!");
    }

    private RulesDefinition parseRulesDefinition(
            final net.opengis.sensorML.x101.ProcessMethodType.Rules.RulesDefinition xbRulesDefinition) {
        final RulesDefinition rulesDefinition = new RulesDefinition();
        if (xbRulesDefinition.isSetDescription()) {
            rulesDefinition.setDescription(xbRulesDefinition.getDescription().getStringValue());
        }
        // TODO add other options if required
        return rulesDefinition;
    }

    /**
     * Parses the identifications and sets the AbstractProcess' identifiers
     * 
     * @param abstractProcess
     *            The AbstractProcess to which identifiers are added
     * @param identificationArray
     *            XML identification
     */
    private void parseIdentifications(final AbstractProcess abstractProcess, final Identification[] identificationArray) {
        for (final Identification xbIdentification : identificationArray) {
            if (xbIdentification.getIdentifierList() != null) {
                for (final Identifier xbIdentifier : xbIdentification.getIdentifierList().getIdentifierArray()) {
                    if (xbIdentifier.getName() != null && xbIdentifier.getTerm() != null) {
                        final SmlIdentifier identifier =
                                new SmlIdentifier(xbIdentifier.getName(), xbIdentifier.getTerm().getDefinition(),
                                        xbIdentifier.getTerm().getValue());
                        abstractProcess.addIdentifier(identifier);
                        if (isIdentificationProcedureIdentifier(identifier)) {
                            abstractProcess.setIdentifier(identifier.getValue());
                        }
                    }
                }
            }
        }
    }

    /**
     * Parses the classification
     * 
     * @param classificationArray
     *            XML classification
     * @return SOS classification
     */
    private List<SmlClassifier> parseClassification(final Classification[] classificationArray) {
        final List<SmlClassifier> sosClassifiers = new ArrayList<SmlClassifier>(classificationArray.length);
        for (final Classification xbClassification : classificationArray) {
            for (final Classifier xbClassifier : xbClassification.getClassifierList().getClassifierArray()) {
                final Term term = xbClassifier.getTerm();
                final SmlClassifier smlClassifier =
                        new SmlClassifier(xbClassifier.getName(),
                                term.isSetDefinition() ? term.getDefinition() : null, term.isSetCodeSpace() ? term
                                        .getCodeSpace().getHref() : null, term.getValue());
                sosClassifiers.add(smlClassifier);
            }
        }
        return sosClassifiers;
    }

    /**
     * Parses the characteristics
     * 
     * @param characteristicsArray
     *            XML characteristics
     * @return SOS characteristics
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs
     */
    private List<SmlCharacteristics> parseCharacteristics(final Characteristics[] characteristicsArray)
            throws OwsExceptionReport {
        final List<SmlCharacteristics> sosCharacteristicsList =
                new ArrayList<SmlCharacteristics>(characteristicsArray.length);
        final SmlCharacteristics sosCharacteristics = new SmlCharacteristics();
        for (final Characteristics xbCharacteristics : characteristicsArray) {
            final Object decodedObject = CodingHelper.decodeXmlElement(xbCharacteristics.getAbstractDataRecord());
            if (decodedObject instanceof DataRecord) {
                sosCharacteristics.setDataRecord((DataRecord) decodedObject);
            } else {
                throw new InvalidParameterValueException()
                        .at(XmlHelper.getLocalName(xbCharacteristics))
                        .withMessage(
                                "Error while parsing the characteristics of the SensorML (the characteristics' data record is not of type DataRecordPropertyType)!");
            }
        }
        sosCharacteristicsList.add(sosCharacteristics);
        return sosCharacteristicsList;
    }

    /**
     * Parses the capabilities, processing and removing special insertion
     * metadata
     * 
     * @param abstractProcess
     *            The AbstractProcess to which capabilities and insertion
     *            metadata are added
     * @param capabilitiesArray
     *            XML capabilities
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs
     */
    private void parseCapabilities(final AbstractProcess abstractProcess, final Capabilities[] capabilitiesArray)
            throws OwsExceptionReport {
        for (final Capabilities xbcaps : capabilitiesArray) {
            final Object o = CodingHelper.decodeXmlElement(xbcaps.getAbstractDataRecord());
            if (o instanceof DataRecord) {
                final DataRecord record = (DataRecord) o;
                final SmlCapabilities caps = new SmlCapabilities();
                caps.setDataRecord(record).setName(xbcaps.getName());
                abstractProcess.addCapabilities(caps);
                // check if this capabilities is insertion metadata
                if (SensorMLConstants.ELEMENT_NAME_OFFERINGS.equals(caps.getName())) {
                    abstractProcess.addOfferings(SosOffering.fromSet(caps.getDataRecord().getSweAbstractSimpleTypeFromFields(SweText.class)));
                } else if (SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES.equals(caps.getName())) {
                    abstractProcess.addParentProcedures(parseCapabilitiesMetadata(caps, xbcaps).keySet());
                } else if (SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST.equals(caps.getName())) {
                    abstractProcess.addFeaturesOfInterest(parseCapabilitiesMetadata(caps, xbcaps).keySet());
                }
            } else {
                throw new InvalidParameterValueException().at(XmlHelper.getLocalName(xbcaps)).withMessage(
                        "Error while parsing the capabilities of " + "the SensorML (the capabilities data record "
                                + "is not of type DataRecordPropertyType)!");
            }
        }
    }

    /**
     * Process standard formatted capabilities insertion metadata into a map
     * (key=identifier, value=name)
     * 
     * @param dataRecord
     *            The DataRecord to examine
     * @param xbCapabilities
     *            The original capabilites xml object, used for exception
     *            throwing
     * @return Map of insertion metadata (key=identifier, value=name)
     * @throws CodedException
     *             thrown if the DataRecord fields are in an incorrect format
     */
    private Map<String, String> parseCapabilitiesMetadata(final SmlCapabilities caps, final Capabilities xbCapabilities)
            throws OwsExceptionReport {
        final Map<String, String> map = Maps.newHashMapWithExpectedSize(caps.getDataRecord().getFields().size());
        for (final SweField sosSweField : caps.getDataRecord().getFields()) {
            if (sosSweField.getElement() instanceof SweText) {
                final SweText sosSweText = (SweText) sosSweField.getElement();
                if (sosSweText.isSetValue()) {
                    map.put(sosSweText.getValue(), sosSweField.getName().getValue());
                } else {
                    throw new UnsupportedDecoderInputException(this, xbCapabilities).withMessage(
                            "Removable capabilities element %s contains a field with no value",
                            xbCapabilities.getName());
                }
            } else {
                throw new UnsupportedDecoderInputException(this, xbCapabilities).withMessage(
                        "Removable capabilities element %s contains a non-Text field", xbCapabilities.getName());
            }

        }
        return map;
    }
    
    /**
     * Parses the position
     * 
     * @param position
     *            XML position
     * @return SOS position
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs
     */
    private SmlPosition parsePosition(final Position position) throws OwsExceptionReport {
        SmlPosition sosSMLPosition = null;
        if (position.isSetPosition()) {
            final Object pos = CodingHelper.decodeXmlElement(position.getPosition());
            if (pos instanceof SmlPosition) {
                sosSMLPosition = (SmlPosition) pos;
            }
        } else {
            throw new InvalidParameterValueException().at(XmlHelper.getLocalName(position)).withMessage(
                    "Error while parsing the position of the SensorML (the position is not set)!");
        }
        if (position.getName() != null) {
            sosSMLPosition.setName(position.getName());
        }
        return sosSMLPosition;
    }

    /**
     * Parses the location
     * 
     * @param location
     *            XML location
     * @return SOS location
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs
     */
    private SmlLocation parseLocation(final SmlLocation2 location) throws OwsExceptionReport {
        SmlLocation sosSmlLocation = null;
        if (location.isSetPoint()) {
            final Object point = CodingHelper.decodeXmlElement(location.getPoint());
            if (point instanceof Point) {
                sosSmlLocation = new SmlLocation((Point) point);
            }
        } else {
            throw new InvalidParameterValueException()
                    .at(XmlHelper.getLocalName(location))
                    .withMessage(
                            "Error while parsing the sml:location of the SensorML (point is not set, only point is supported)!");
        }
        return sosSmlLocation;
    }

    private Time parseValidTime(final ValidTime validTime) {
        // TODO Auto-generated method stub
        return null;
    }

    private List<String> parseParameters(final Parameters parameters) {
        final List<String> sosParameters = new ArrayList<String>(0);
        // TODO Auto-generated method stub
        return sosParameters;
    }

    private List<SmlContact> parseContact(final Contact[] contactArray) {
        List<SmlContact> smlContacts = Lists.newArrayList();
        if (contactArray != null && contactArray.length > 0) {
            for (Contact contact : contactArray) {
                if (contact.getContactList() != null) {
                    smlContacts.add(parseContactListMembers(contact.getContactList()));
                } else if (contact.getPerson() != null) {
                    smlContacts.add(parsePerson(contact.getPerson()));
                } else if (contact.getResponsibleParty() != null) {
                    smlContacts.add(parseResponsibleParty(contact.getResponsibleParty()));
                }
            }
        }
        return smlContacts;
    }

    private SmlContact parseContactListMembers(final ContactList contactList) {
        SmlContactList smlContactList = new SmlContactList();
        if (contactList.getMemberArray() != null && contactList.getMemberArray().length > 0) {
            for (ContactList.Member member : contactList.getMemberArray()) {
                SmlContact thisSmlContact = null;
                if (member.getPerson() != null) {
                    thisSmlContact = parsePerson(member.getPerson());
                } else if (member.getResponsibleParty() != null) {
                    thisSmlContact = parseResponsibleParty(member.getResponsibleParty());
                }
                if (thisSmlContact != null) {
                    if (member.getRole() != null) {
                        thisSmlContact.setRole(member.getRole());
                    }
                    smlContactList.addMember(thisSmlContact);
                }
            }
        }
        return smlContactList;
    }

    private SmlPerson parsePerson(Person person) {
        SmlPerson smlPerson = new SmlPerson();
        if (person != null) {
            if (!Strings.isNullOrEmpty(person.getAffiliation())) {
                smlPerson.setAffiliation(person.getAffiliation());
            }
            if (!Strings.isNullOrEmpty(person.getEmail())) {
                smlPerson.setEmail(person.getEmail());
            }
            if (!Strings.isNullOrEmpty(person.getName())) {
                smlPerson.setName(person.getName());
            }
            if (!Strings.isNullOrEmpty(person.getPhoneNumber())) {
                smlPerson.setPhoneNumber(person.getPhoneNumber());
            }
            if (!Strings.isNullOrEmpty(person.getSurname())) {
                smlPerson.setSurname(person.getSurname());
            }
            if (!Strings.isNullOrEmpty(person.getUserID())) {
                smlPerson.setUserID(person.getUserID());
            }
        }
        return smlPerson;
    }

    private SmlResponsibleParty parseResponsibleParty(ResponsibleParty responsibleParty) {
        SmlResponsibleParty smlRespParty = new SmlResponsibleParty();
        if (responsibleParty != null) {
            if (!Strings.isNullOrEmpty(responsibleParty.getIndividualName())) {
                smlRespParty.setIndividualName(responsibleParty.getIndividualName());
            }
            if (!Strings.isNullOrEmpty(responsibleParty.getOrganizationName())) {
                smlRespParty.setOrganizationName(responsibleParty.getOrganizationName());
            }
            if (!Strings.isNullOrEmpty(responsibleParty.getPositionName())) {
                smlRespParty.setPositionName(responsibleParty.getPositionName());
            }
            if (responsibleParty.getContactInfo() != null) {
                ContactInfo contactInfo = responsibleParty.getContactInfo();
                if (contactInfo.getAddress() != null) {
                    Address address = contactInfo.getAddress();
                    if (!Strings.isNullOrEmpty(address.getAdministrativeArea())) {
                        smlRespParty.setAdministrativeArea(address.getAdministrativeArea());
                    }
                    if (!Strings.isNullOrEmpty(address.getCity())) {
                        smlRespParty.setCity(address.getCity());
                    }
                    if (!Strings.isNullOrEmpty(address.getCountry())) {
                        smlRespParty.setCountry(address.getCountry());
                    }
                    if (address.getDeliveryPointArray() != null && address.getDeliveryPointArray().length > 0) {
                        for (String deliveryPoint : address.getDeliveryPointArray()) {
                            smlRespParty.addDeliveryPoint(deliveryPoint);
                        }
                    }
                    if (!Strings.isNullOrEmpty(address.getElectronicMailAddress())) {
                        smlRespParty.setEmail(address.getElectronicMailAddress());
                    }
                    if (!Strings.isNullOrEmpty(address.getPostalCode())) {
                        smlRespParty.setPostalCode(address.getPostalCode());
                    }
                }
                if (!Strings.isNullOrEmpty(contactInfo.getContactInstructions())) {
                    smlRespParty.setContactInstructions(contactInfo.getContactInstructions());
                }
                if (!Strings.isNullOrEmpty(contactInfo.getHoursOfService())) {
                    smlRespParty.setHoursOfService(contactInfo.getHoursOfService());
                }
                if (contactInfo.getOnlineResourceArray() != null && contactInfo.getOnlineResourceArray().length > 0) {
                    for (OnlineResource onlineResource : contactInfo.getOnlineResourceArray()) {
                        if (!Strings.isNullOrEmpty(onlineResource.getHref())) {
                            smlRespParty.addOnlineResource(onlineResource.getHref());
                        }
                    }
                }
                if (contactInfo.getPhone() != null) {
                    Phone phone = contactInfo.getPhone();
                    if (phone.getVoiceArray() != null && phone.getVoiceArray().length > 0) {
                        for (String phoneVoice : phone.getVoiceArray()) {
                            smlRespParty.addPhoneVoice(phoneVoice);
                        }
                    }
                    if (phone.getFacsimileArray() != null && phone.getFacsimileArray().length > 0) {
                        for (String phoneFax : phone.getFacsimileArray()) {
                            smlRespParty.addPhoneFax(phoneFax);
                        }
                    }
                }
            }
        }
        return smlRespParty;
    }

    private List<AbstractSmlDocumentation> parseDocumentation(final Documentation[] documentationArray) {
        final List<AbstractSmlDocumentation> abstractDocumentation = new ArrayList<AbstractSmlDocumentation>(0);
        // TODO Auto-generated method stub
        return abstractDocumentation;
    }

    private List<String> parseKeywords(final Keywords[] keywordsArray) {
        final Set<String> keywords = Sets.newHashSet();
        if (keywordsArray != null && keywordsArray.length > 0) {
            for (final Keywords keyword : keywordsArray) {
                if (keyword.isSetKeywordList()) {
                    final String[] array = keyword.getKeywordList().getKeywordArray();
                    if (array != null && array.length > 0) {
                        keywords.addAll(Arrays.asList(array));
                    }
                }
            }
        }
        return Lists.newArrayList(keywords);
    }

    private String parseHistory(final History[] historyArray) {
        // TODO Auto-generated method stub
        return "";
    }

    /**
     * Parses the inputs
     * 
     * @param inputs
     *            XML inputs
     * @return SOS inputs
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs
     */
    private List<SmlIo<?>> parseInputs(final Inputs inputs) throws OwsExceptionReport {
        final List<SmlIo<?>> sosInputs = new ArrayList<SmlIo<?>>(inputs.getInputList().getInputArray().length);
        for (final IoComponentPropertyType xbInput : inputs.getInputList().getInputArray()) {
            sosInputs.add(parseIoComponentPropertyType(xbInput));
        }
        return sosInputs;
    }

    /**
     * Parses the outputs
     * 
     * @param outputs
     *            XML outputs
     * @return SOS outputs
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs
     */
    private List<SmlIo<?>> parseOutputs(final Outputs outputs) throws OwsExceptionReport {
        final List<SmlIo<?>> sosOutputs = new ArrayList<SmlIo<?>>(outputs.getOutputList().getOutputArray().length);
        for (final IoComponentPropertyType xbOutput : outputs.getOutputList().getOutputArray()) {
            sosOutputs.add(parseIoComponentPropertyType(xbOutput));
        }
        return sosOutputs;
    }

    private List<SmlComponent> parseComponents(final Components components) throws OwsExceptionReport {
        final List<SmlComponent> sosSmlComponents = Lists.newLinkedList();
        if (components.isSetComponentList() && components.getComponentList().getComponentArray() != null) {
            for (final Component component : components.getComponentList().getComponentArray()) {
                if (component.isSetProcess() || component.isSetHref()) {
                    final SmlComponent sosSmlcomponent = new SmlComponent(component.getName());
                    AbstractProcess abstractProcess = null;
                    if (component.isSetProcess()) {
                        if (component.getProcess() instanceof SystemType) {
                            abstractProcess = new System();
                            parseSystem((SystemType) component.getProcess(), (System) abstractProcess);
                        } else {
                            abstractProcess = new AbstractProcess();
                            parseAbstractProcess(component.getProcess(), abstractProcess);
                        }
                    } else {
                        abstractProcess = new AbstractProcess();
                        abstractProcess.setIdentifier(component.getHref());
                    }
                    sosSmlcomponent.setProcess(abstractProcess);
                    sosSmlComponents.add(sosSmlcomponent);
                }
            }
        }
        return sosSmlComponents;
    }

    /**
     * Parses the components
     * 
     * @param xbIoCompPropType
     *            XML components
     * @return SOS components
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs
     */
    @SuppressWarnings({ "rawtypes" })
    private SmlIo<?> parseIoComponentPropertyType(final IoComponentPropertyType xbIoCompPropType)
            throws OwsExceptionReport {
        final SmlIo<?> sosIo = new SmlIo();
        sosIo.setIoName(xbIoCompPropType.getName());
        XmlObject toDecode = null;
        if (xbIoCompPropType.isSetBoolean()) {
            toDecode = xbIoCompPropType.getBoolean();
        } else if (xbIoCompPropType.isSetCategory()) {
            toDecode = xbIoCompPropType.getCategory();
        } else if (xbIoCompPropType.isSetCount()) {
            toDecode = xbIoCompPropType.getCount();
        } else if (xbIoCompPropType.isSetCountRange()) {
            toDecode = xbIoCompPropType.getCountRange();
        } else if (xbIoCompPropType.isSetObservableProperty()) {
            toDecode = xbIoCompPropType.getObservableProperty();
        } else if (xbIoCompPropType.isSetQuantity()) {
            toDecode = xbIoCompPropType.getQuantity();
        } else if (xbIoCompPropType.isSetQuantityRange()) {
            toDecode = xbIoCompPropType.getQuantityRange();
        } else if (xbIoCompPropType.isSetText()) {
            toDecode = xbIoCompPropType.getText();
        } else if (xbIoCompPropType.isSetTime()) {
            toDecode = xbIoCompPropType.getTime();
        } else if (xbIoCompPropType.isSetTimeRange()) {
            toDecode = xbIoCompPropType.getTimeRange();
        } else if (xbIoCompPropType.isSetAbstractDataArray1()) {
            toDecode = xbIoCompPropType.getAbstractDataArray1();
        } else if (xbIoCompPropType.isSetAbstractDataRecord()) {
            toDecode = xbIoCompPropType.getAbstractDataRecord();
        } else {
            throw new InvalidParameterValueException().at(XmlHelper.getLocalName(xbIoCompPropType)).withMessage(
                    "An 'IoComponentProperty' is not supported");
        }

        final Object decodedObject = CodingHelper.decodeXmlElement(toDecode);
        if (decodedObject instanceof SweAbstractDataComponent) {
            sosIo.setIoValue((SweAbstractDataComponent) decodedObject);
        } else {
            throw new InvalidParameterValueException().at(XmlHelper.getLocalName(xbIoCompPropType)).withMessage(
                    "The 'IoComponentProperty' with type '%s' as value for '%s' is not supported.",
                    XmlHelper.getLocalName(toDecode), XmlHelper.getLocalName(xbIoCompPropType));
        }
        return sosIo;
    }

    private String addSensorMLWrapperForXmlDescription(final AbstractProcessType xbProcessType) {
        final SensorMLDocument xbSensorMLDoc =
                SensorMLDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final net.opengis.sensorML.x101.SensorMLDocument.SensorML xbSensorML = xbSensorMLDoc.addNewSensorML();
        xbSensorML.setVersion(SensorMLConstants.VERSION_V101);
        final Member member = xbSensorML.addNewMember();
        final AbstractProcessType xbAbstractProcessType =
                (AbstractProcessType) member.addNewProcess().substitute(getQnameForType(xbProcessType.schemaType()),
                        xbProcessType.schemaType());
        xbAbstractProcessType.set(xbProcessType);
        return xbSensorMLDoc.xmlText(XmlOptionsHelper.getInstance().getXmlOptions());
    }

    private QName getQnameForType(final SchemaType type) {
        if (type == SystemType.type) {
            return SensorMLConstants.SYSTEM_QNAME;
        } else if (type == ProcessModelType.type) {
            return SensorMLConstants.PROCESS_MODEL_QNAME;
        } else if (type == ComponentType.type) {
            return SensorMLConstants.COMPONENT_QNAME;
        }
        return SensorMLConstants.ABSTRACT_PROCESS_QNAME;
    }

    private List<Integer> checkCapabilitiesForRemoval(final Capabilities[] capabilitiesArray) {
        final List<Integer> removeableCaps = new ArrayList<Integer>(capabilitiesArray.length);
        for (int i = 0; i < capabilitiesArray.length; i++) {
            final String name = capabilitiesArray[i].getName();
            if (name != null && REMOVABLE_CAPABILITIES_NAMES.contains(name)) {
                removeableCaps.add(i);
            }
        }
        Collections.sort(removeableCaps);
        Collections.reverse(removeableCaps);
        return removeableCaps;
    }

    private List<Integer> checkComponentsForRemoval(final ComponentList componentList) {
        final List<Integer> removeableComponents = new ArrayList<Integer>(0);
        if (componentList != null && componentList.getComponentArray() != null) {
            final Component[] componentArray = componentList.getComponentArray();
            for (int i = 0; i < componentArray.length; i++) {
                if (componentArray[i].getRole() != null
                        && REMOVABLE_COMPONENTS_ROLES.contains(componentArray[i].getRole())) {
                    removeableComponents.add(i);
                }
            }
        }
        return removeableComponents;
    }

    private void checkAndRemoveEmptyComponents(final SystemType system) {
        boolean removeComponents = false;
        final Components components = system.getComponents();
        if (components != null) {
            if (components.getComponentList() == null) {
                removeComponents = true;
            } else if (components.getComponentList().getComponentArray() == null
                    || ((components.getComponentList().getComponentArray() != null && components.getComponentList()
                            .getComponentArray().length == 0))) {
                removeComponents = true;
            }
        }
        if (removeComponents) {
            system.unsetComponents();
        }
    }
}
