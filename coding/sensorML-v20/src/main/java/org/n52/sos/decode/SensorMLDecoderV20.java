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

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.sensorml.x20.AbstractPhysicalProcessType;
import net.opengis.sensorml.x20.AbstractProcessDocument;
import net.opengis.sensorml.x20.AbstractProcessType;
import net.opengis.sensorml.x20.AbstractProcessType.FeaturesOfInterest;
import net.opengis.sensorml.x20.AbstractProcessType.Inputs;
import net.opengis.sensorml.x20.AbstractProcessType.Outputs;
import net.opengis.sensorml.x20.AggregateProcessDocument;
import net.opengis.sensorml.x20.AggregateProcessPropertyType;
import net.opengis.sensorml.x20.AggregateProcessType;
import net.opengis.sensorml.x20.CapabilityListType;
import net.opengis.sensorml.x20.CapabilityListType.Capability;
import net.opengis.sensorml.x20.CharacteristicListPropertyType;
import net.opengis.sensorml.x20.CharacteristicListType;
import net.opengis.sensorml.x20.CharacteristicListType.Characteristic;
import net.opengis.sensorml.x20.ClassifierListPropertyType;
import net.opengis.sensorml.x20.ClassifierListType.Classifier;
import net.opengis.sensorml.x20.ComponentListPropertyType;
import net.opengis.sensorml.x20.ComponentListType;
import net.opengis.sensorml.x20.ComponentListType.Component;
import net.opengis.sensorml.x20.ContactListPropertyType;
import net.opengis.sensorml.x20.DataComponentOrObservablePropertyType;
import net.opengis.sensorml.x20.DataInterfaceType;
import net.opengis.sensorml.x20.DescribedObjectDocument;
import net.opengis.sensorml.x20.DescribedObjectType;
import net.opengis.sensorml.x20.DescribedObjectType.Capabilities;
import net.opengis.sensorml.x20.IdentifierListPropertyType;
import net.opengis.sensorml.x20.IdentifierListType.Identifier;
import net.opengis.sensorml.x20.InputListType.Input;
import net.opengis.sensorml.x20.KeywordListPropertyType;
import net.opengis.sensorml.x20.ObservablePropertyType;
import net.opengis.sensorml.x20.OutputListType.Output;
import net.opengis.sensorml.x20.PhysicalComponentDocument;
import net.opengis.sensorml.x20.PhysicalComponentPropertyType;
import net.opengis.sensorml.x20.PhysicalComponentType;
import net.opengis.sensorml.x20.PhysicalSystemDocument;
import net.opengis.sensorml.x20.PhysicalSystemPropertyType;
import net.opengis.sensorml.x20.PhysicalSystemType;
import net.opengis.sensorml.x20.PositionUnionPropertyType;
import net.opengis.sensorml.x20.SimpleProcessDocument;
import net.opengis.sensorml.x20.SimpleProcessPropertyType;
import net.opengis.sensorml.x20.SimpleProcessType;
import net.opengis.sensorml.x20.TermType;
import net.opengis.swe.x20.DataStreamPropertyType;

import org.apache.xmlbeans.XmlObject;
import org.isotc211.x2005.gmd.CIResponsiblePartyPropertyType;
import org.n52.sos.encode.AbstractSensorMLDecoder;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.SmlContact;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCapability;
import org.n52.sos.ogc.sensorML.elements.SmlCharacteristic;
import org.n52.sos.ogc.sensorML.elements.SmlCharacteristics;
import org.n52.sos.ogc.sensorML.elements.SmlClassifier;
import org.n52.sos.ogc.sensorML.elements.SmlComponent;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;
import org.n52.sos.ogc.sensorML.v20.AbstractPhysicalProcess;
import org.n52.sos.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.sos.ogc.sensorML.v20.AggregateProcess;
import org.n52.sos.ogc.sensorML.v20.DescribedObject;
import org.n52.sos.ogc.sensorML.v20.PhysicalComponent;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sensorML.v20.SimpleProcess;
import org.n52.sos.ogc.sensorML.v20.SmlDataInterface;
import org.n52.sos.ogc.sensorML.v20.SmlDataStreamPropertyType;
import org.n52.sos.ogc.sensorML.v20.SmlFeatureOfInterest;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweVector;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * {@link AbstractSensorMLDecoder} class to decode OGC SensorML 2.0
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class SensorMLDecoderV20 extends AbstractSensorMLDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMLDecoderV20.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(
            SensorML20Constants.NS_SML_20, DescribedObjectDocument.class, SimpleProcessDocument.class,
            PhysicalComponentDocument.class, PhysicalSystemDocument.class, AbstractProcessDocument.class);

    private static final Set<String> SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS = Collections
            .singleton(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL);

    private static final Set<String> REMOVABLE_CAPABILITIES_NAMES = Sets
            .newHashSet(SensorMLConstants.ELEMENT_NAME_OFFERINGS);

    private static final Set<String> REMOVABLE_COMPONENTS_ROLES = Collections
            .singleton(SensorMLConstants.ELEMENT_NAME_CHILD_PROCEDURES);

    public SensorMLDecoderV20() {
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
    public AbstractSensorML decode(XmlObject element) throws OwsExceptionReport, UnsupportedDecoderInputException {
        AbstractSensorML sml = null;
        if (element instanceof PhysicalSystemDocument) {
            sml = parsePhysicalSystem(((PhysicalSystemDocument) element).getPhysicalSystem());
        } else if (element instanceof PhysicalSystemPropertyType) {
            sml = parsePhysicalSystem(((PhysicalSystemPropertyType) element).getPhysicalSystem());
        } else if (element instanceof PhysicalSystemType) {
            sml = parsePhysicalSystem((PhysicalSystemType) element);
        } else if (element instanceof PhysicalComponentDocument) {
            sml = parsePhysicalComponent(((PhysicalComponentDocument) element).getPhysicalComponent());
        } else if (element instanceof PhysicalComponentPropertyType) {
            sml = parsePhysicalComponent(((PhysicalComponentPropertyType) element).getPhysicalComponent());
        } else if (element instanceof PhysicalComponentType) {
            sml = parsePhysicalComponent((PhysicalComponentType) element);
        } else if (element instanceof SimpleProcessDocument) {
            sml = parseSimpleProcess(((SimpleProcessDocument) element).getSimpleProcess());
        } else if (element instanceof SimpleProcessPropertyType) {
            sml = parseSimpleProcess(((SimpleProcessPropertyType) element).getSimpleProcess());
        } else if (element instanceof SimpleProcessPropertyType) {
            sml = parseSimpleProcess((SimpleProcessType) element);
        } else if (element instanceof AggregateProcessDocument) {
            sml = parseAggregateProcess(((AggregateProcessDocument) element).getAggregateProcess());
        } else if (element instanceof AggregateProcessPropertyType) {
            sml = parseAggregateProcess(((AggregateProcessPropertyType) element).getAggregateProcess());
        } else if (element instanceof AggregateProcessPropertyType) {
            sml = parseAggregateProcess((AggregateProcessType) element);
        } else {
            throw new UnsupportedDecoderInputException(this, element);
        }
        if (sml != null) {
            setXmlDescription(element, sml);
        }
        return sml;
    }

    private void setXmlDescription(XmlObject xml, AbstractSensorML sml) {
        sml.setSensorDescriptionXmlString(xml.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
    }

    private DescribedObject parsePhysicalSystem(PhysicalSystemType describedObject) throws OwsExceptionReport {
        PhysicalSystem ps = new PhysicalSystem();
        parseAbstractPhysicalProcess(describedObject, ps);
        parseAbstractProcess(describedObject, ps);
        parseDescribedObject(describedObject, ps);
        if (describedObject.isSetComponents()) {
            ps.addComponents(parseComponents(describedObject.getComponents()));
            final List<Integer> compsToRemove =
                    checkComponentsForRemoval(describedObject.getComponents().getComponentList());
            for (final Integer integer : compsToRemove) {
                describedObject.getComponents().getComponentList().removeComponent(integer);
            }
            if (removeEmptyComponents(describedObject.getComponents())) {
                describedObject.unsetComponents();
            }
        }
        return ps;
    }

    private DescribedObject parsePhysicalComponent(PhysicalComponentType describedObject) throws OwsExceptionReport {
        PhysicalComponent pc = new PhysicalComponent();
        parseAbstractPhysicalProcess(describedObject, pc);
        parseAbstractProcess(describedObject, pc);
        parseDescribedObject(describedObject, pc);
        return pc;
    }

    private DescribedObject parseSimpleProcess(SimpleProcessType describedObject) throws OwsExceptionReport {
        SimpleProcess sp = new SimpleProcess();
        parseAbstractProcess(describedObject, sp);
        parseDescribedObject(describedObject, sp);
        return sp;
    }

    private DescribedObject parseAggregateProcess(AggregateProcessType describedObject) throws OwsExceptionReport {
        AggregateProcess ap = new AggregateProcess();
        parseAbstractProcess(describedObject, ap);
        parseDescribedObject(describedObject, ap);
        if (describedObject.isSetComponents()) {
            ap.addComponents(parseComponents(describedObject.getComponents()));
            final List<Integer> compsToRemove =
                    checkComponentsForRemoval(describedObject.getComponents().getComponentList());
            for (final Integer integer : compsToRemove) {
                describedObject.getComponents().getComponentList().removeComponent(integer);
            }
            if (removeEmptyComponents(describedObject.getComponents())) {
                describedObject.unsetComponents();
            }
        }
        return ap;
    }

    private void parseDescribedObject(DescribedObjectType dot, DescribedObject describedObject)
            throws OwsExceptionReport {
        if (dot.isSetIdentifier()) {
            describedObject.setIdentifier((CodeWithAuthority) CodingHelper.decodeXmlElement(dot.getIdentifier()));
            checkIdentifierCodeSpace(describedObject);
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getExtensionArray())) {

        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getKeywordsArray())) {
            parseKeywords(dot.getKeywordsArray());
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getIdentificationArray())) {
            parseIdentifications(describedObject, dot.getIdentificationArray());
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getClassificationArray())) {
            parseClassification(dot.getClassificationArray());
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getCharacteristicsArray())) {
            parseCharacteristics(dot.getCharacteristicsArray());
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getValidTimeArray())) {
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getSecurityConstraintsArray())) {

        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getLegalConstraintsArray())) {

        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getCharacteristicsArray())) {

        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getCapabilitiesArray())) {
            parseCapabilities(describedObject, dot.getCapabilitiesArray());
            final List<Integer> capsToRemove = checkCapabilitiesForRemoval(dot.getCapabilitiesArray());
            for (final Integer integer : capsToRemove) {
                dot.removeCapabilities(integer);
            }
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getContactsArray())) {
            parseContact(dot.getContactsArray());
        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getDocumentationArray())) {

        }
        if (CollectionHelper.isNotNullOrEmpty(dot.getHistoryArray())) {

        }
        if (dot.isSetLocation()) {

        }
    }

    private void parseAbstractProcess(AbstractProcessType apt, AbstractProcessV20 abstractProcess)
            throws OwsExceptionReport {
        if (apt.isSetTypeOf()) {

        }
        if (apt.isSetConfiguration()) {

        }
        if (apt.isSetFeaturesOfInterest()) {
            parseFeatureOfInterest(apt.getFeaturesOfInterest(), abstractProcess);
        }
        if (apt.isSetInputs()) {
            abstractProcess.setInputs(parseInputs(apt.getInputs()));
        }
        if (apt.isSetOutputs()) {
            abstractProcess.setOutputs(parseOutputs(apt.getOutputs()));
        }
        if (CollectionHelper.isNotNullOrEmpty(apt.getModesArray())) {

        }
    }

    private void parseAbstractPhysicalProcess(AbstractPhysicalProcessType appt,
            AbstractPhysicalProcess abstractPhysicalProcess) throws OwsExceptionReport {

        if (appt.isSetAttachedTo()) {
            Object decodeXmlElement = CodingHelper.decodeXmlElement(appt.getAttachedTo());
            if (decodeXmlElement != null && decodeXmlElement instanceof ReferenceType) {
                abstractPhysicalProcess.setAttachedTo((ReferenceType) decodeXmlElement);
            }
        }
        if (CollectionHelper.isNotNullOrEmpty(appt.getLocalReferenceFrameArray())) {

        }
        if (CollectionHelper.isNotNullOrEmpty(appt.getLocalTimeFrameArray())) {

        }
        if (CollectionHelper.isNotNullOrEmpty(appt.getPositionArray())) {
            for (PositionUnionPropertyType pupt : appt.getPositionArray()) {
                abstractPhysicalProcess.setPosition(parsePositionFrom(pupt));
                // TODO remove break if AbstractPhysicalProcess is extended
                break;
            }
        }
        if (CollectionHelper.isNotNullOrEmpty(appt.getTimePositionArray())) {

        }
    }

    private List<String> parseKeywords(final KeywordListPropertyType[] keywordsArray) {
        final Set<String> keywords = Sets.newHashSet();
        if (keywordsArray != null && keywordsArray.length > 0) {
            for (final KeywordListPropertyType keyword : keywordsArray) {
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

    private void parseIdentifications(DescribedObject describedObject, IdentifierListPropertyType[] identificationArray) {
        for (final IdentifierListPropertyType ilpt : identificationArray) {
            if (ilpt.isSetIdentifierList()
                    && CollectionHelper.isNotNullOrEmpty(ilpt.getIdentifierList().getIdentifier2Array())) {
                for (final Identifier i : ilpt.getIdentifierList().getIdentifier2Array()) {
                    if (i.getTerm() != null) {
                        TermType term = i.getTerm();
                        final SmlIdentifier identifier =
                                new SmlIdentifier(term.getLabel(), term.getDefinition(), term.getValue());
                        describedObject.addIdentifier(identifier);
                        if (isIdentificationProcedureIdentifier(identifier)) {
                            describedObject.setIdentifier(identifier.getValue());
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
    private List<SmlClassifier> parseClassification(final ClassifierListPropertyType[] clpts) {
        final List<SmlClassifier> sosClassifiers = new ArrayList<SmlClassifier>(clpts.length);
        for (final ClassifierListPropertyType clpt : clpts) {
            for (final Classifier c : clpt.getClassifierList().getClassifierArray()) {
                final TermType term = c.getTerm();
                final SmlClassifier smlClassifier =
                        new SmlClassifier(term.getLabel(), term.isSetDefinition() ? term.getDefinition() : null,
                                term.isSetCodeSpace() ? term.getCodeSpace().getHref() : null, term.getValue());
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
    private List<SmlCharacteristics> parseCharacteristics(final CharacteristicListPropertyType[] clpts)
            throws OwsExceptionReport {
        final List<SmlCharacteristics> sosCharacteristicsList = new ArrayList<SmlCharacteristics>(clpts.length);
        for (final CharacteristicListPropertyType clpt : clpts) {
            final SmlCharacteristics sosCharacteristics = new SmlCharacteristics();
            if (clpt.isSetCharacteristicList()) {
                CharacteristicListType clt = clpt.getCharacteristicList();
                if (CollectionHelper.isNotNullOrEmpty(clt.getCharacteristicArray())) {
                    for (Characteristic c : clt.getCharacteristicArray()) {
                        final Object o = CodingHelper.decodeXmlElement(c.getAbstractDataComponent());
                        if (o instanceof SweAbstractDataComponent) {
                            final SmlCharacteristic characteristic =
                                    new SmlCharacteristic(c.getName(), (SweAbstractDataComponent) o);
                            sosCharacteristics.addCharacteristic(characteristic);
                        } else {
                            throw new InvalidParameterValueException()
                                    .at(XmlHelper.getLocalName(clpt))
                                    .withMessage(
                                            "Error while parsing the characteristics of the SensorML (the characteristics' data record is not of type DataRecordPropertyType)!");
                        }
                    }
                }
            }
            sosCharacteristicsList.add(sosCharacteristics);
        }
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
        for (final Capabilities cs : capabilitiesArray) {
            final SmlCapabilities capabilities = new SmlCapabilities(cs.getName());
            if (cs.isSetCapabilityList()) {
                CapabilityListType cl = cs.getCapabilityList();
                if (CollectionHelper.isNotNullOrEmpty(cl.getCapabilityArray())) {
                    for (Capability c : cl.getCapabilityArray()) {
                        if (c.isSetAbstractDataComponent()) {
                            final Object o = CodingHelper.decodeXmlElement(c.getAbstractDataComponent());
                            if (o instanceof SweAbstractDataComponent) {
                                final SmlCapability capability =
                                        new SmlCapability(c.getName(), (SweAbstractDataComponent) o);
                                // check if this capabilities is insertion
                                // metadata
                                if (SensorMLConstants.ELEMENT_NAME_OFFERINGS.equals(cs.getName())) {
                                    if (o instanceof DataRecord) {
                                        abstractProcess.addOfferings(SosOffering.fromSet(((DataRecord) o)
                                                .getSweAbstractSimpleTypeFromFields(SweText.class)));
                                    } else if (o instanceof SweAbstractSimpleType<?>) {
                                        abstractProcess.addOffering(SosOffering.from((SweAbstractSimpleType<?>) o));
                                    }
                                    // } else if
                                    // (SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES.equals(cs.getName()))
                                    // {
                                    // abstractProcess.addParentProcedures(parseCapabilitiesMetadata(caps,
                                    // xbcaps).keySet());
                                    // } else if
                                    // (SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST.equals(cs.getName()))
                                    // {
                                    // abstractProcess.addFeaturesOfInterest(parseCapabilitiesMetadata(caps,
                                    // xbcaps).keySet());
                                }
                                capabilities.addCapability(capability);
                            } else {
                                throw new InvalidParameterValueException().at(XmlHelper.getLocalName(cs)).withMessage(
                                        "Error while parsing the capabilities of "
                                                + "the SensorML (the capabilities data record "
                                                + "is not of type DataRecordPropertyType)!");
                            }
                        }
                    }
                }
            }
            abstractProcess.addCapabilities(capabilities);
        }
    }

    private List<SmlContact> parseContact(final ContactListPropertyType[] clpts) throws OwsExceptionReport {
        List<SmlContact> smlContacts = Lists.newArrayList();
        for (ContactListPropertyType clpt : clpts) {
            if (clpt.isSetContactList() && CollectionHelper.isNotNullOrEmpty(clpt.getContactList().getContactArray())) {
                for (CIResponsiblePartyPropertyType c : clpt.getContactList().getContactArray()) {
                    final Object o = CodingHelper.decodeXmlElement(c);
                    if (o instanceof SmlContact) {
                        smlContacts.add((SmlContact) o);
                    } else {
                        throw new InvalidParameterValueException().at(XmlHelper.getLocalName(c)).withMessage(
                                "Error while parsing the contacts of " + "the SensorML!");
                    }
                }
            }
            // if (clpt.getContactList() != null) {
            // smlContacts.add(parseContactListMembers(contact.getContactList()));
            // } else if (contact.getPerson() != null) {
            // smlContacts.add(parsePerson(contact.getPerson()));
            // } else if (contact.getResponsibleParty() != null) {
            // smlContacts.add(parseResponsibleParty(contact.getResponsibleParty()));
            // }
        }
        return smlContacts;
    }

    private List<SmlIo<?>> parseInputs(Inputs inputs) throws OwsExceptionReport {
        if (CollectionHelper.isNotNullOrEmpty(inputs.getInputList().getInputArray())) {
            final List<SmlIo<?>> sosInputs = new ArrayList<SmlIo<?>>(inputs.getInputList().getInputArray().length);
            for (final Input xbInput : inputs.getInputList().getInputArray()) {
                sosInputs.add(parseInput(xbInput));
            }
            return sosInputs;
        }
        return Collections.emptyList();
    }

    private List<SmlIo<?>> parseOutputs(Outputs outputs) throws OwsExceptionReport {
        if (CollectionHelper.isNotNullOrEmpty(outputs.getOutputList().getOutputArray())) {
            final List<SmlIo<?>> sosOutputs = new ArrayList<SmlIo<?>>(outputs.getOutputList().getOutputArray().length);
            for (final Output xbOutput : outputs.getOutputList().getOutputArray()) {
                sosOutputs.add(parseOutput(xbOutput));
            }
            return sosOutputs;
        }
        return Collections.emptyList();
    }

    private void parseFeatureOfInterest(FeaturesOfInterest featuresOfInterest, AbstractProcessV20 abstractProcess)
            throws OwsExceptionReport {
        if (CollectionHelper.isNotNullOrEmpty(featuresOfInterest.getFeatureList().getFeatureArray())) {
            SmlFeatureOfInterest smlFeatureOfInterest = new SmlFeatureOfInterest();
            for (FeaturePropertyType fpt : featuresOfInterest.getFeatureList().getFeatureArray()) {
                Object o = CodingHelper.decodeXmlElement(fpt);
                if (o instanceof AbstractFeature) {
                    smlFeatureOfInterest.addFeatureOfInterest((AbstractFeature) o);
                }
            }
            abstractProcess.setSmlFeatureOfInterest(smlFeatureOfInterest);
        }
    }

    private SmlPosition parsePositionFrom(PositionUnionPropertyType pupt) throws OwsExceptionReport {
        SmlPosition position = new SmlPosition();
        if (pupt.isSetVector()) {
            Object decodeXmlElement = CodingHelper.decodeXmlElement(pupt.getVector());
            if (decodeXmlElement != null && decodeXmlElement instanceof SweVector) {
                position.setVector((SweVector) decodeXmlElement);
            }
            // } else if (pupt.isSetAbstractProcess()) {
            // AbstractSensorML decode = decode(pupt.getAbstractProcess());
            // } else if (pupt.isSetDataArray1()) {
            // Object decodeXmlElement =
            // CodingHelper.decodeXmlElement(pupt.getDataArray1());
        } else if (pupt.isSetDataRecord()) {
            Object decodeXmlElement = CodingHelper.decodeXmlElement(pupt.getDataRecord());
            if (decodeXmlElement != null && decodeXmlElement instanceof SweVector) {
                position.setAbstractDataComponent((SweDataRecord) decodeXmlElement);
            }
            // } else if (pupt.isSetPoint()){
            // Object decodeXmlElement =
            // CodingHelper.decodeXmlElement(pupt.getPoint());
            // } else if (pupt.isSetText()) {
            // Object decodeXmlElement =
            // CodingHelper.decodeXmlElement(pupt.getText());
        } else {
            throw new UnsupportedDecoderInputException(this, pupt);
        }
        return position;
    }

    // private List<SmlComponent> parseComponents(final Components components)
    // throws OwsExceptionReport {
    // final List<SmlComponent> sosSmlComponents = Lists.newLinkedList();
    // if (components.isSetComponentList() &&
    // components.getComponentList().getComponentArray() != null) {
    // for (final Component component :
    // components.getComponentList().getComponentArray()) {
    // if (component.isSetProcess() || component.isSetHref()) {
    // final SmlComponent sosSmlcomponent = new
    // SmlComponent(component.getName());
    // AbstractProcess abstractProcess = null;
    // if (component.isSetProcess()) {
    // if (component.getProcess() instanceof SystemType) {
    // abstractProcess = new System();
    // parseSystem((SystemType) component.getProcess(), (System)
    // abstractProcess);
    // } else {
    // abstractProcess = new AbstractProcess();
    // parseAbstractProcess(component.getProcess(), abstractProcess);
    // }
    // } else {
    // abstractProcess = new AbstractProcess();
    // abstractProcess.setIdentifier(component.getHref());
    // }
    // sosSmlcomponent.setProcess(abstractProcess);
    // sosSmlComponents.add(sosSmlcomponent);
    // }
    // }
    // }
    // return sosSmlComponents;
    // }

    private List<SmlComponent> parseComponents(ComponentListPropertyType components)
            throws UnsupportedDecoderInputException, OwsExceptionReport {
        final List<SmlComponent> sosSmlComponents = Lists.newLinkedList();
        if (components.isSetComponentList() && components.getComponentList().getComponentArray() != null) {
            for (final Component component : components.getComponentList().getComponentArray()) {
                if (component.isSetAbstractProcess() || component.isSetHref()) {
                    final SmlComponent sosSmlcomponent = new SmlComponent(component.getName());
                    AbstractSensorML abstractProcess = null;
                    if (component.isSetAbstractProcess()) {
                        abstractProcess = decode(component.getAbstractProcess());
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

    private boolean checkIdentifierCodeSpace(AbstractProcessV20 ap) throws InvalidParameterValueException {
        if (ap.getIdentifierCodeWithAuthority().isSetCodeSpace()
                && OGCConstants.UNIQUE_ID.equals(ap.getIdentifierCodeWithAuthority().getCodeSpace())) {
            return true;
        } else {
            throw new InvalidParameterValueException("gml:identifier[@codesSpace]", ap
                    .getIdentifierCodeWithAuthority().getCodeSpace());
        }
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

    private List<Integer> checkComponentsForRemoval(ComponentListType componentList) {
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

    private boolean removeEmptyComponents(ComponentListPropertyType components) {
        boolean removeComponents = false;
        if (components != null) {
            if (components.getComponentList() == null) {
                removeComponents = true;
            } else if (components.getComponentList().getComponentArray() == null
                    || ((components.getComponentList().getComponentArray() != null && components.getComponentList()
                            .getComponentArray().length == 0))) {
                removeComponents = true;
            }
        }
        return removeComponents;
    }
    
    @SuppressWarnings({ "rawtypes" })
    private SmlIo<?> parseInput(Input xbInput) throws OwsExceptionReport {
        final SmlIo<?> sosIo = new SmlIo();
        sosIo.setIoName(xbInput.getName());
        sosIo.setIoValue(parseDataComponentOrObservablePropertyType(xbInput));
        return sosIo;
    }
    
    @SuppressWarnings({ "rawtypes" })
    private SmlIo<?> parseOutput(Output xbOutput) throws OwsExceptionReport {
        final SmlIo<?> sosIo = new SmlIo();
        sosIo.setIoName(xbOutput.getName());
        sosIo.setIoValue(parseDataComponentOrObservablePropertyType(xbOutput));
        return sosIo;
    }

    /**
     * Parses the components
     * 
     * @param adcpt
     *            XML components
     * @return SOS component
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs
     */
    private SweAbstractDataComponent parseDataComponentOrObservablePropertyType(final DataComponentOrObservablePropertyType adcpt)
            throws OwsExceptionReport {
        XmlObject toDecode = null;
        if (adcpt.isSetObservableProperty()) {
           return parseObservableProperty(adcpt.getObservableProperty());
        } else if (adcpt.isSetAbstractDataComponent()) {
            final Object decodedObject = CodingHelper.decodeXmlElement(adcpt.getAbstractDataComponent());
            if (decodedObject instanceof SweAbstractDataComponent) {
               return (SweAbstractDataComponent) decodedObject;
            } else {
                throw new InvalidParameterValueException().at(XmlHelper.getLocalName(adcpt)).withMessage(
                        "The 'DataComponentOrObservablePropertyType' with type '%s' as value for '%s' is not supported.",
                        XmlHelper.getLocalName(toDecode), XmlHelper.getLocalName(adcpt));
            }
        } else if (adcpt.isSetDataInterface()) {
        	return parseDataInterfaceType(adcpt.getDataInterface());
        } else {
            throw new InvalidParameterValueException().at(XmlHelper.getLocalName(adcpt)).withMessage(
                    "An 'DataComponentOrObservablePropertyType' is not supported");
        }
    }
    
    protected SmlDataInterface parseDataInterfaceType(DataInterfaceType xbDataInterface) throws OwsExceptionReport {
		SmlDataInterface dataInterface = new SmlDataInterface();
		// TODO implement- no funding at the moment available
		// When starting implementation: Do not forget to activate the already available unit tests
//		dataInterface.setData(parseDataStreamPropertyType(xbDataInterface.getData()));
//		if (xbDataInterface.isSetInterfaceParameters()) {
//			final Object decodedObject = CodingHelper.decodeXmlElement(xbDataInterface.getInterfaceParameters());
//			if (decodedObject instanceof SweDataRecord) {
//				dataInterface.setInputParameters((SweDataRecord)decodedObject);
//			}
//			// TODO throw exception if not instance of SweDataRecord
//		}
		return dataInterface;
	}

	protected SmlDataStreamPropertyType parseDataStreamPropertyType(
			DataStreamPropertyType data) {
		return new SmlDataStreamPropertyType();
	}

	/**
     * Parse {@link ObservablePropertyType} 
     * @param opt Object to parse
     * @return Parsed {@link SweObservableProperty}
     */
    private SweObservableProperty parseObservableProperty(ObservablePropertyType opt) {
        SweObservableProperty observableProperty = new SweObservableProperty();
        observableProperty.setDefinition(opt.getDefinition());
        if (opt.isSetDescription()) {
            observableProperty.setDescription(opt.getDescription());
        }
        if (opt.isSetIdentifier()) {
            observableProperty.setIdentifier(opt.getIdentifier());
        }
        if (opt.isSetLabel()) {
            observableProperty.setLabel(opt.getLabel());
        }
        return observableProperty;
    }

}
