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
package org.n52.sos.encode;

import static java.util.Collections.singletonMap;
import static org.n52.sos.util.CodingHelper.encoderKeysForElements;
import static org.n52.sos.util.CollectionHelper.union;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import net.opengis.gml.PointType;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.CharacteristicsDocument.Characteristics;
import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList.Classifier;
import net.opengis.sensorML.x101.ComponentsDocument.Components;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList.Component;
import net.opengis.sensorML.x101.ContactDocument.Contact;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo.Address;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo.Phone;
import net.opengis.sensorML.x101.ContactListDocument.ContactList;
import net.opengis.sensorML.x101.DocumentDocument.Document;
import net.opengis.sensorML.x101.DocumentListDocument.DocumentList;
import net.opengis.sensorML.x101.DocumentationDocument.Documentation;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.InputsDocument.Inputs;
import net.opengis.sensorML.x101.InputsDocument.Inputs.InputList;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.MethodPropertyType;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.PersonDocument.Person;
import net.opengis.sensorML.x101.PositionDocument.Position;
import net.opengis.sensorML.x101.ProcessMethodType;
import net.opengis.sensorML.x101.ProcessMethodType.Rules.RulesDefinition;
import net.opengis.sensorML.x101.ProcessModelDocument;
import net.opengis.sensorML.x101.ProcessModelType;
import net.opengis.sensorML.x101.ResponsiblePartyDocument.ResponsibleParty;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SmlLocation.SmlLocation2;
import net.opengis.sensorML.x101.SystemDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.TermDocument.Term;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.DataArrayDocument;
import net.opengis.swe.x101.DataArrayType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.VectorType;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.ProcessMethod;
import org.n52.sos.ogc.sensorML.ProcessModel;
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
import org.n52.sos.ogc.sensorML.elements.SmlDocumentation;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentationList;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentationListMember;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sensorML.elements.SmlLocation;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweConstants.SweAggregateType;
import org.n52.sos.ogc.swe.SweCoordinate;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweSimpleDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class SensorMLEncoderv101 extends AbstractSensorMLEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMLEncoderv101.class);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = singletonMap(
            SupportedTypeKey.ProcedureDescriptionFormat, (Set<String>) ImmutableSet.of(
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL, SensorMLConstants.SENSORML_CONTENT_TYPE.toString()));

    private static final Map<String, ImmutableMap<String, Set<String>>> SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS =
            ImmutableMap.of(
                    SosConstants.SOS,
                    ImmutableMap
                            .<String, Set<String>> builder()
                            .put(Sos2Constants.SERVICEVERSION,
                                    ImmutableSet.of(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL))
                            .put(Sos1Constants.SERVICEVERSION,
                                    ImmutableSet.of(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE)).build());
    
    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = union(
            encoderKeysForElements(SensorMLConstants.NS_SML, SosProcedureDescription.class, AbstractSensorML.class),
            encoderKeysForElements(SensorMLConstants.SENSORML_CONTENT_TYPE.toString(), SosProcedureDescription.class,
                    AbstractSensorML.class));

    public SensorMLEncoderv101() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(SensorMLConstants.NS_SML, SensorMLConstants.NS_SML_PREFIX);
    }

    @Override
    public MediaType getContentType() {
        return SensorMLConstants.SENSORML_CONTENT_TYPE;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SensorMLConstants.SML_101_SCHEMA_LOCATION);
    }

    @Override
    public Set<String> getSupportedProcedureDescriptionFormats(final String service, final String version) {
        if (SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.containsKey(service)
                && SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.get(service).containsKey(version)) {
            return SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.get(service).get(version);
        }
        return Collections.emptySet();
    }

    @Override
    public XmlObject encode(final Object response, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject encodedObject = null;
        if (response instanceof AbstractSensorML) {
            encodedObject = createSensorDescription((AbstractSensorML) response);
        }
        // FIXME workaround? if of type UnknowProcedureType try to parse the
        // description string, UNIT is missing "NOT_DEFINED"?!
        else if (response instanceof SosProcedureDescriptionUnknowType) {
            final String procDescXMLString = ((SosProcedureDescription) response).getSensorDescriptionXmlString();
            final AbstractSensorML sensorDesc = new AbstractSensorML();
            sensorDesc.setSensorDescriptionXmlString(procDescXMLString);
            encodedObject = createSensorDescriptionFromString(sensorDesc);
        } else {
            throw new UnsupportedEncoderInputException(this, response);
        }
        LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                XmlHelper.validateDocument(encodedObject));
        return encodedObject;

    }

    /**
     * creates sml:System
     * 
     * @param sensorDesc
     *            SensorML encoded system description
     * 
     * @return Returns XMLBeans representation of sml:System
     * 
     * 
     * @throws OwsExceptionReport
     */
    private XmlObject createSensorDescription(final AbstractSensorML sensorDesc) throws OwsExceptionReport {
        if (sensorDesc.isSetSensorDescriptionXmlString()) {
            return createSensorDescriptionFromString(sensorDesc);
        } else {
            return createSensorDescriptionFromObject(sensorDesc);
        }
    }

    protected XmlObject createSensorDescriptionFromString(final AbstractSensorML sensorDesc) throws OwsExceptionReport {
        try {
            final XmlObject xmlObject = XmlObject.Factory.parse(sensorDesc.getSensorDescriptionXmlString());
            if (xmlObject instanceof SensorMLDocument) {
                final SensorMLDocument sensorML = (SensorMLDocument) xmlObject;
                for (final Member member : sensorML.getSensorML().getMemberArray()) {
                    if (sensorDesc instanceof SensorML) {
                        for (final AbstractProcess absProcess : ((SensorML) sensorDesc).getMembers()) {
                            addAbstractProcessValues(member.getProcess(), absProcess);
                            if (member.getProcess() instanceof SystemType && absProcess instanceof System) {
                                addSystemValues((SystemType) member.getProcess(), (System) absProcess);
                            } else if (member.getProcess() instanceof ProcessModelType
                                    && absProcess instanceof ProcessModel) {
                                addProcessModelValues((ProcessModelType) member.getProcess(),
                                        (ProcessModel) absProcess);
                            }
                        }
                    } else if (sensorDesc instanceof AbstractProcess) {
                        addAbstractProcessValues(member.getProcess(), (AbstractProcess) sensorDesc);
                        if (member.getProcess() instanceof SystemType && sensorDesc instanceof System) {
                            addSystemValues((SystemType) member.getProcess(), (System) sensorDesc);
                        }
                    }
                }

            } else if (xmlObject instanceof AbstractProcessType) {
                final AbstractProcessType abstractProcess = (AbstractProcessType) xmlObject;
                addAbstractProcessValues(abstractProcess, (AbstractProcess) sensorDesc);
                if (abstractProcess instanceof SystemType && sensorDesc instanceof System) {
                    addSystemValues((SystemType) abstractProcess, (System) sensorDesc);
                } else if (abstractProcess instanceof ProcessModelType && sensorDesc instanceof ProcessModel) {
                    addProcessModelValues((ProcessModelType) abstractProcess, (ProcessModel) sensorDesc);
                }
            }
            return xmlObject;
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle);
        }
    }

    private XmlObject createSensorDescriptionFromObject(final AbstractSensorML sensorDesc) throws OwsExceptionReport {
        if (sensorDesc instanceof SensorML) {
            return createSensorMLDescription((SensorML) sensorDesc);
        } else if (sensorDesc instanceof AbstractProcess) {
            return createProcessDescription((AbstractProcess) sensorDesc);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("The sensor description type is not supported by this service!");
        }

    }

    private XmlObject createProcessDescription(final AbstractProcess sensorDesc) throws OwsExceptionReport {
        // TODO Review: System -> return doc; ProcessModel -> return type
        if (sensorDesc instanceof System) {
            final System system = (System) sensorDesc;
            final SystemDocument xbSystemDoc =
                    SystemDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            final SystemType xbSystem = xbSystemDoc.addNewSystem();
            addAbstractProcessValues(xbSystem, system);
            addSystemValues(xbSystem, system);
            return xbSystem;
        } else if (sensorDesc instanceof ProcessModel) {
            // TODO: set values
            final ProcessModel processModel = (ProcessModel) sensorDesc;
            final ProcessModelDocument xbProcessModelDoc =
                    ProcessModelDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            final ProcessModelType xbProcessModel = xbProcessModelDoc.addNewProcessModel();
            addAbstractProcessValues(xbProcessModel, processModel);
            addProcessModelValues(xbProcessModel, processModel);
            return xbProcessModel;
        } else {
            throw new NoApplicableCodeException().withMessage(
                    "The sensor description type is not supported by this service!").setStatus(
                    HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected SensorMLDocument createSensorMLDescription(final SensorML smlSensorDesc) throws OwsExceptionReport {
        final SensorMLDocument sensorMLDoc =
                SensorMLDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final net.opengis.sensorML.x101.SensorMLDocument.SensorML xbSensorML = sensorMLDoc.addNewSensorML();
        xbSensorML.setVersion(SensorMLConstants.VERSION_V101);
        if (smlSensorDesc.isSetMembers()) {
            for (final AbstractProcess sml : smlSensorDesc.getMembers()) {
                if (sml instanceof System) {
                    final SystemType xbSystem =
                            (SystemType) xbSensorML
                                    .addNewMember()
                                    .addNewProcess()
                                    .substitute(new QName(SensorMLConstants.NS_SML, SensorMLConstants.EN_SYSTEM),
                                            SystemType.type);
                    final System smlSystem = (System) sml;
                    addAbstractProcessValues(xbSystem, smlSystem);
                    addSystemValues(xbSystem, smlSystem);
                } else if (sml instanceof ProcessModel) {
                    final ProcessModelType xbProcessModel =
                            (ProcessModelType) xbSensorML
                                    .addNewMember()
                                    .addNewProcess()
                                    .substitute(
                                            new QName(SensorMLConstants.NS_SML, SensorMLConstants.EN_PROCESS_MODEL),
                                            ProcessModelType.type);
                    final ProcessModel smlProcessModel = (ProcessModel) sml;
                    addAbstractProcessValues(xbProcessModel, smlProcessModel);
                    addProcessModelValues(xbProcessModel, smlProcessModel);
                }
            }
        }
        return sensorMLDoc;
    }

    private ContactList createContactList(final List<SmlContact> contacts) {
        final ContactList xbContacts = ContactList.Factory.newInstance();
        for (final SmlContact smlContact : contacts) {
            if (smlContact instanceof SmlPerson) {
                ContactList.Member member = xbContacts.addNewMember();
                member.addNewPerson().set(createPerson((SmlPerson) smlContact));
                if (!Strings.isNullOrEmpty(smlContact.getRole())) {
                    member.setRole(smlContact.getRole());
                }
            } else if (smlContact instanceof SmlResponsibleParty) {
                ContactList.Member member = xbContacts.addNewMember();
                member.addNewResponsibleParty().set(createResponsibleParty((SmlResponsibleParty) smlContact));
                if (!Strings.isNullOrEmpty(smlContact.getRole())) {
                    member.setRole(smlContact.getRole());
                }
            } else if (smlContact instanceof SmlContactList) {
                SmlContactList contactList = (SmlContactList) smlContact;
                ContactList innerContactList = createContactList(contactList.getMembers());
                int innerContactLength = innerContactList.getMemberArray().length;
                for (int i = 0; i < innerContactLength; i++) {
                    xbContacts.addNewMember().set(innerContactList.getMemberArray(i));
                }
            }
        }
        return xbContacts;
    }

    private XmlObject createResponsibleParty(final SmlResponsibleParty smlRespParty) {
        final ResponsibleParty xbRespParty = ResponsibleParty.Factory.newInstance();
        if (smlRespParty.isSetIndividualName()) {
            xbRespParty.setIndividualName(smlRespParty.getIndividualName());
        }
        if (smlRespParty.isSetOrganizationName()) {
            xbRespParty.setOrganizationName(smlRespParty.getOrganizationName());
        }
        if (smlRespParty.isSetPositionName()) {
            xbRespParty.setPositionName(smlRespParty.getPositionName());
        }
        if (smlRespParty.isSetContactInfo()) {
            xbRespParty.setContactInfo(createContactInfo(smlRespParty));
        }
        return xbRespParty;
    }

    private ContactInfo createContactInfo(final SmlResponsibleParty smlRespParty) {
        final ContactInfo xbContactInfo = ContactInfo.Factory.newInstance();
        if (smlRespParty.isSetHoursOfService()) {
            xbContactInfo.setHoursOfService(smlRespParty.getHoursOfService());
        }
        if (smlRespParty.isSetContactInstructions()) {
            xbContactInfo.setContactInstructions(smlRespParty.getContactInstructions());
        }
        if (smlRespParty.isSetOnlineResources()) {
            for (final String onlineResouce : smlRespParty.getOnlineResources()) {
                xbContactInfo.addNewOnlineResource().setHref(onlineResouce);
            }
        }
        if (smlRespParty.isSetPhone()) {
            final Phone xbPhone = xbContactInfo.addNewPhone();
            if (smlRespParty.isSetPhoneFax()) {
                for (final String fax : smlRespParty.getPhoneFax()) {
                    xbPhone.addFacsimile(fax);
                }
            }
            if (smlRespParty.isSetPhoneVoice()) {
                for (final String voice : smlRespParty.getPhoneVoice()) {
                    xbPhone.addVoice(voice);
                }
            }
        }
        if (smlRespParty.isSetAddress()) {
            final Address xbAddress = xbContactInfo.addNewAddress();
            if (smlRespParty.isSetDeliveryPoint()) {
                for (final String deliveryPoint : smlRespParty.getDeliveryPoint()) {
                    xbAddress.addDeliveryPoint(deliveryPoint);
                }
            }
            if (smlRespParty.isSetCity()) {
                xbAddress.setCity(smlRespParty.getCity());
            }
            if (smlRespParty.isSetAdministrativeArea()) {
                xbAddress.setAdministrativeArea(smlRespParty.getAdministrativeArea());
            }
            if (smlRespParty.isSetPostalCode()) {
                xbAddress.setPostalCode(smlRespParty.getPostalCode());
            }
            if (smlRespParty.isSetCountry()) {
                xbAddress.setCountry(smlRespParty.getCountry());
            }
            if (smlRespParty.isSetEmail()) {
                xbAddress.setElectronicMailAddress(smlRespParty.getEmail());
            }
        }
        return xbContactInfo;
    }

    private Person createPerson(final SmlPerson smlPerson) {
        final Person xbPerson = Person.Factory.newInstance();
        if (smlPerson.isSetAffiliation()) {
            xbPerson.setAffiliation(smlPerson.getAffiliation());
        }
        if (smlPerson.isSetEmail()) {
            xbPerson.setEmail(smlPerson.getEmail());
        }
        if (smlPerson.isSetName()) {
            xbPerson.setName(smlPerson.getName());
        }
        if (smlPerson.isSetPhoneNumber()) {
            xbPerson.setPhoneNumber(smlPerson.getPhoneNumber());
        }
        if (smlPerson.isSetSurname()) {
            xbPerson.setSurname(smlPerson.getSurname());
        }
        if (smlPerson.isSetUserID()) {
            xbPerson.setUserID(smlPerson.getUserID());
        }
        return xbPerson;
    }

    // TODO refactor/rename
    private void addAbstractProcessValues(final AbstractProcessType abstractProcess,
            final AbstractProcess sosAbstractProcess) throws OwsExceptionReport {
        if (sosAbstractProcess.isSetGmlId()) {
            abstractProcess.setId(sosAbstractProcess.getGmlId());
        }

        addSpecialCapabilities(sosAbstractProcess);
        if (sosAbstractProcess.isSetCapabilities()) {
            final Capabilities[] existing = abstractProcess.getCapabilitiesArray();
            final Set<String> names = Sets.newHashSetWithExpectedSize(existing.length);
            for (final Capabilities element : existing) {
                if (element.getName() != null) {
                    names.add(element.getName());
                }
            }
            for (final SmlCapabilities sosCapability : sosAbstractProcess.getCapabilities()) {
                final Capabilities c = createCapability(sosCapability);
                // replace existing capability with the same name
                if (names.contains(c.getName())) {
                    removeCapability(abstractProcess, c);
                }
                abstractProcess.addNewCapabilities().set(c);
            }
        }

        // set description
        if (sosAbstractProcess.isSetDescription() && !abstractProcess.isSetDescription()) {
            abstractProcess.addNewDescription().setStringValue(sosAbstractProcess.getDescription());
        }
        if (sosAbstractProcess.isSetName() && CollectionHelper.isNullOrEmpty(abstractProcess.getNameArray())) {
            // TODO check if override existing names
            addNamesToAbstractProcess(abstractProcess, sosAbstractProcess.getNames());
        }
        // set identification
        if (sosAbstractProcess.isSetIdentifications()) {
            abstractProcess.setIdentificationArray(createIdentification(sosAbstractProcess.getIdentifications()));
        }
        // set classification
        if (sosAbstractProcess.isSetClassifications()) {
            abstractProcess.setClassificationArray(createClassification(sosAbstractProcess.getClassifications()));
        }
        // set characteristics
        if (sosAbstractProcess.isSetCharacteristics()) {
            abstractProcess.setCharacteristicsArray(createCharacteristics(sosAbstractProcess.getCharacteristics()));
        }
        // set documentation
        if (sosAbstractProcess.isSetDocumentation()) {
            abstractProcess.setDocumentationArray(createDocumentationArray(sosAbstractProcess.getDocumentation()));
        }
        // set contacts if contacts aren't already present in the abstract
        // process
        if (sosAbstractProcess.isSetContact()
                && (abstractProcess.getContactArray() == null || abstractProcess.getContactArray().length == 0)) {
            ContactList contactList = createContactList(sosAbstractProcess.getContact());
            if (contactList != null && contactList.getMemberArray().length > 0) {
                abstractProcess.addNewContact().setContactList(contactList);
            }
        }
        // set keywords
        if (sosAbstractProcess.isSetKeywords()) {
            final List<String> keywords = sosAbstractProcess.getKeywords();
            final int length = abstractProcess.getKeywordsArray().length;
            for (int i = 0; i < length; ++i) {
                abstractProcess.removeKeywords(i);
            }
            abstractProcess.addNewKeywords().addNewKeywordList()
                    .setKeywordArray(keywords.toArray(new String[keywords.size()]));
        }

        if (sosAbstractProcess.isSetValidTime()) {
            if (abstractProcess.isSetValidTime()) {
                // remove existing validTime element
                final XmlCursor newCursor = abstractProcess.getValidTime().newCursor();
                newCursor.removeXml();
                newCursor.dispose();
            }
            final Time time = sosAbstractProcess.getValidTime();
            final XmlObject xbtime = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, time);
            if (time instanceof TimeInstant) {
                abstractProcess.addNewValidTime().addNewTimeInstant().set(xbtime);
            } else if (time instanceof TimePeriod) {
                abstractProcess.addNewValidTime().addNewTimePeriod().set(xbtime);
            }
        }
    }

    private void addNamesToAbstractProcess(AbstractProcessType abstractProcess, List<CodeType> names)
            throws OwsExceptionReport {
        for (CodeType codeType : names) {
            abstractProcess.addNewName().set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, codeType));
        }
    }

    @SuppressWarnings("unused")
    private Contact[] mergeContacts(final Contact[] contacts, final ContactList additionalContactsList) {
        final Set<Person> mergedPersons = Sets.newHashSet();
        final Set<ResponsibleParty> mergedResponsibleParties = Sets.newHashSet();
        for (final Contact contact : contacts) {
            if (isContactListSetAndContainingElements(contact)) {
                for (final net.opengis.sensorML.x101.ContactListDocument.ContactList.Member member : contact
                        .getContactList().getMemberArray()) {
                    if (member.isSetPerson()) {
                        mergedPersons.add(member.getPerson());
                    } else if (member.isSetResponsibleParty()) {
                        mergedResponsibleParties.add(member.getResponsibleParty());
                    }
                }
            } else if (contact.isSetPerson()) {
                mergedPersons.add(contact.getPerson());
            } else if (contact.isSetResponsibleParty()) {
                mergedResponsibleParties.add(contact.getResponsibleParty());
            }
        }
        for (final net.opengis.sensorML.x101.ContactListDocument.ContactList.Member member : additionalContactsList
                .getMemberArray()) {
            if (member.isSetPerson() && !isContained(member.getPerson(), mergedPersons)) {
                mergedPersons.add(member.getPerson());
            } else if (member.isSetResponsibleParty()
                    && !isContained(member.getResponsibleParty(), mergedResponsibleParties)) {
                mergedResponsibleParties.add(member.getResponsibleParty());
            }
        }
        final Contact newContact = Contact.Factory.newInstance();
        final ContactList newContactList = ContactList.Factory.newInstance();
        for (final ResponsibleParty responsibleParty : mergedResponsibleParties) {
            newContactList.addNewMember().addNewResponsibleParty().set(responsibleParty);
        }
        for (final Person person : mergedPersons) {
            newContactList.addNewMember().addNewPerson().set(person);
        }
        if (newContactList.sizeOfMemberArray() == 1) {
            if (newContactList.getMemberArray(0).isSetPerson()) {
                newContact.addNewPerson().set(newContactList.getMemberArray(0).getPerson());
            } else if (newContactList.getMemberArray(0).isSetResponsibleParty()) {
                newContact.addNewResponsibleParty().set(newContactList.getMemberArray(0).getResponsibleParty());
            }
        } else {
            newContact.addNewContactList().set(newContactList);
        }
        final Contact[] result = { newContact };
        return result;
    }

    private boolean isContained(final ResponsibleParty responsibleParty,
            final Set<ResponsibleParty> mergedResponsibleParties) {
        final XmlOptions xmlOptions = XmlOptionsHelper.getInstance().getXmlOptions();
        for (final ResponsibleParty responsibleParty2 : mergedResponsibleParties) {
            if (isIdentical(responsibleParty, xmlOptions, responsibleParty2)) {
                return true;
            }
        }
        return false;
    }

    private boolean isContained(final Person person, final Set<Person> mergedPersons) {
        final XmlOptions xmlOptions = XmlOptionsHelper.getInstance().getXmlOptions();
        for (final Person anotherPerson : mergedPersons) {
            if (isIdentical(person, xmlOptions, anotherPerson)) {
                return true;
            }
        }
        return false;
    }

    private boolean isContactListSetAndContainingElements(final Contact contact) {
        return contact.getContactList() != null && contact.getContactList().getMemberArray() != null
                && contact.getContactList().getMemberArray().length > 0;
    }

    private void removeCapability(final AbstractProcessType abstractProcess, final Capabilities c) {
        // get current index of element with this name
        for (int i = 0; i < abstractProcess.getCapabilitiesArray().length; i++) {
            if (abstractProcess.getCapabilitiesArray(i).getName().equals(c.getName())) {
                abstractProcess.removeCapabilities(i);
                return;
            }
        }
    }

    private Capabilities createCapability(final SmlCapabilities capabilities) throws OwsExceptionReport {
        final Capabilities xbCapabilities =
                Capabilities.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (capabilities.isSetName()) {
            xbCapabilities.setName(capabilities.getName());
        }
        if (capabilities.isSetAbstractDataRecord() && capabilities.getDataRecord().isSetFields()) {
            final XmlObject encodedDataRecord =
                    CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_101, capabilities.getDataRecord());
            final XmlObject substituteElement =
                    XmlHelper.substituteElement(xbCapabilities.addNewAbstractDataRecord(), encodedDataRecord);
            substituteElement.set(encodedDataRecord);
        }
        return xbCapabilities;
    }

    private void addSystemValues(final SystemType xbSystem, final System system) throws OwsExceptionReport {
        // set inputs
        if (system.isSetInputs()) {
            xbSystem.setInputs(createInputs(system.getInputs()));
        }
        // set position
        if (system.isSetPosition()) {
            xbSystem.setPosition(createPosition(system.getPosition()));
        }
        // set location
        if (system.isSetLocation()) {
            xbSystem.setSmlLocation(createLocation(system.getLocation()));
        }
        // set components
        final List<SmlComponent> smlComponents = Lists.newArrayList();
        if (system.isSetComponents() || system.isSetChildProcedures()) {
            if (system.isSetComponents()) {
                smlComponents.addAll(system.getComponents());
            }
            if (system.isSetChildProcedures()) {
                smlComponents.addAll(createComponentsForChildProcedures(system.getChildProcedures()));
            }
            if (!smlComponents.isEmpty()) {
                final Components components = createComponents(smlComponents);
                if (components != null && components.getComponentList() != null
                        && components.getComponentList().sizeOfComponentArray() > 0) {
                    xbSystem.setComponents(components);
                }
            }
        }
        if (!smlComponents.isEmpty()) {
            // TODO check for duplicated outputs
            system.getOutputs().addAll(getOutputsFromChilds(smlComponents));
            // TODO check if necessary
            // system.addFeatureOfInterest(getFeaturesFromChild(smlComponents));
        }
        // set outputs
        if (system.isSetOutputs()) {
            extendOutputs(system);
            xbSystem.setOutputs(createOutputs(system.getOutputs()));
        }
    }

    private void extendOutputs(AbstractProcess abstractProcess) {
        if (abstractProcess.isSetPhenomenon()) {
            for (SmlIo<?> output : abstractProcess.getOutputs()) {
                if (abstractProcess.hasPhenomenonFor(output.getIoValue().getDefinition())) {
                    output.getIoValue().setName(
                            abstractProcess.getPhenomenonFor(output.getIoValue().getDefinition()).getName());
                }
            }
        }
    }

    private void addProcessModelValues(final ProcessModelType processModel, final ProcessModel sosProcessModel)
            throws OwsExceptionReport {
        // set inputs
        if (sosProcessModel.isSetInputs()) {
            processModel.setInputs(createInputs(sosProcessModel.getInputs()));
        }
        // set outputs
        if (sosProcessModel.isSetOutputs()) {
            extendOutputs(sosProcessModel);
            processModel.setOutputs(createOutputs(sosProcessModel.getOutputs()));
        }
        // set method
        processModel.setMethod(createMethod(sosProcessModel.getMethod()));
    }

    private MethodPropertyType createMethod(final ProcessMethod method) throws CodedException {
        final MethodPropertyType xbMethod =
                MethodPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (method.isSetHref()) {
            xbMethod.setHref(method.getHref());
            if (method.isSetTitle()) {
                xbMethod.setTitle(method.getTitle());
            }
            if (method.isSetRole()) {
                xbMethod.setRole(method.getRole());
            }
        } else if (method.isSetRulesDefinition()) {
            final ProcessMethodType xbProcessMethod = xbMethod.addNewProcessMethod();
            final RulesDefinition xbRulesDefinition = xbProcessMethod.addNewRules().addNewRulesDefinition();
            if (method.getRulesDefinition().isSetDescription()) {
                xbRulesDefinition.addNewDescription().setStringValue(method.getRulesDefinition().getDescription());
            }
        } else {
            throw new NoApplicableCodeException().at("method").withMessage(
                    "The ProcessMethod should contain a href string or a RulesDefinition!");
        }
        return xbMethod;
    }

    /**
     * Creates the valueentification section of the SensorML description.
     * 
     * @param identifications
     *            SOS valueentifications
     * @return XML Identification array
     */
    protected Identification[] createIdentification(final List<SmlIdentifier> identifications) {
        final Identification xbIdentification =
                Identification.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final IdentifierList xbIdentifierList = xbIdentification.addNewIdentifierList();
        for (final SmlIdentifier sosSMLIdentifier : identifications) {
            final Identifier xbIdentifier = xbIdentifierList.addNewIdentifier();
            if (sosSMLIdentifier.getName() != null) {
                xbIdentifier.setName(sosSMLIdentifier.getName());
            }
            final Term xbTerm = xbIdentifier.addNewTerm();
            xbTerm.setDefinition(sosSMLIdentifier.getDefinition());
            xbTerm.setValue(sosSMLIdentifier.getValue());
        }
        return new Identification[] { xbIdentification };
    }

    /**
     * Creates the classification section of the SensorML description.
     * 
     * @param classifications
     *            SOS classifications
     * @return XML Classification array
     */
    private Classification[] createClassification(final List<SmlClassifier> classifications) {
        final Classification xbClassification =
                Classification.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final ClassifierList xbClassifierList = xbClassification.addNewClassifierList();
        for (final SmlClassifier sosSMLClassifier : classifications) {
            final Classifier xbClassifier = xbClassifierList.addNewClassifier();
            if (sosSMLClassifier.getName() != null) {
                xbClassifier.setName(sosSMLClassifier.getName());
            }
            final Term xbTerm = xbClassifier.addNewTerm();
            xbTerm.setValue(sosSMLClassifier.getValue());
            if (sosSMLClassifier.isSetDefinition()) {
                xbTerm.setDefinition(sosSMLClassifier.getDefinition());
            }
            if (sosSMLClassifier.isSetCodeSpace()) {
                xbTerm.addNewCodeSpace().setHref(sosSMLClassifier.getCodeSpace());
            }

        }
        return new Classification[] { xbClassification };
    }

    /**
     * Creates the characteristics section of the SensorML description.
     * 
     * @param smlCharacteristics
     *            SOS characteristics list
     * @return XML Characteristics array
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private Characteristics[] createCharacteristics(final List<SmlCharacteristics> smlCharacteristics)
            throws OwsExceptionReport {
        final List<Characteristics> characteristicsList =
                Lists.newArrayListWithExpectedSize(smlCharacteristics.size());
        for (final SmlCharacteristics sosSMLCharacteristics : smlCharacteristics) {
            if (sosSMLCharacteristics.isSetAbstractDataRecord()) {
                final Characteristics xbCharacteristics =
                        Characteristics.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                if (sosSMLCharacteristics.getDataRecord() instanceof SweSimpleDataRecord) {
                    final SimpleDataRecordType xbSimpleDataRecord =
                            (SimpleDataRecordType) xbCharacteristics.addNewAbstractDataRecord().substitute(
                                    SweConstants.QN_SIMPLEDATARECORD_SWE_101, SimpleDataRecordType.type);
                    if (sosSMLCharacteristics.isSetTypeDefinition()) {
                        xbSimpleDataRecord.setDefinition(sosSMLCharacteristics.getTypeDefinition());
                    }
                    if (sosSMLCharacteristics.getDataRecord().isSetFields()) {
                        for (final SweField field : sosSMLCharacteristics.getDataRecord().getFields()) {
                            final AnyScalarPropertyType xbField = xbSimpleDataRecord.addNewField();
                            xbField.setName(field.getName().getValue());
                            addSweSimpleTypeToField(xbField, field.getElement());
                        }
                    }
                } else if (sosSMLCharacteristics.getDataRecord() instanceof SweDataRecord) {
                    throw new NoApplicableCodeException()
                            .withMessage(
                                    "The SWE characteristics type '%s' is not supported by this SOS for SensorML characteristics!",
                                    SweAggregateType.DataRecord);
                } else {
                    throw new NoApplicableCodeException()
                            .withMessage(
                                    "The SWE characteristics type '%s' is not supported by this SOS for SensorML characteristics!",
                                    sosSMLCharacteristics.getDataRecord().getClass().getName());
                }
                characteristicsList.add(xbCharacteristics);
            }
        }
        return characteristicsList.toArray(new Characteristics[characteristicsList.size()]);
    }

    /**
     * Create XML Documentation array from SOS documentations
     * 
     * @param sosDocumentation
     *            SOS documentation list
     * @return XML Documentation array
     */
    protected Documentation[] createDocumentationArray(final List<AbstractSmlDocumentation> sosDocumentation) {
        final List<Documentation> documentationList = Lists.newArrayListWithExpectedSize(sosDocumentation.size());
        for (final AbstractSmlDocumentation abstractSosSMLDocumentation : sosDocumentation) {
            final Documentation documentation = Documentation.Factory.newInstance();
            if (abstractSosSMLDocumentation instanceof SmlDocumentation) {
                documentation.setDocument(createDocument((SmlDocumentation) abstractSosSMLDocumentation));
            } else if (abstractSosSMLDocumentation instanceof SmlDocumentationList) {
                documentation
                        .setDocumentList(createDocumentationList((SmlDocumentationList) abstractSosSMLDocumentation));
            }
            documentationList.add(documentation);
        }
        return documentationList.toArray(new Documentation[documentationList.size()]);
    }

    /**
     * Create a XML Documentation element from SOS documentation
     * 
     * @param sosDocumentation
     *            SOS documentation
     * @return XML Documentation element
     */
    private Document createDocument(final SmlDocumentation sosDocumentation) {
        final Document document = Document.Factory.newInstance();
        if (sosDocumentation.isSetDescription()) {
            document.addNewDescription().setStringValue(sosDocumentation.getDescription());
        } else {
            document.addNewDescription().setStringValue("");
        }
        if (sosDocumentation.isSetDate()) {
            document.setDate(sosDocumentation.getDate().getValue().toDate());
        }
        if (sosDocumentation.isSetContact()) {
            document.addNewContact().addNewResponsibleParty().setIndividualName(sosDocumentation.getContact());
        }
        if (sosDocumentation.isSetFormat()) {
            document.setFormat(sosDocumentation.getFormat());
        }
        if (sosDocumentation.isSetVersion()) {
            document.setVersion(sosDocumentation.getVersion());
        }
        return document;
    }

    /**
     * Create a XML DocuemntList from SOS documentList
     * 
     * @param sosDocumentationList
     *            SOS documentList
     * @return XML DocumentList element
     */
    private DocumentList createDocumentationList(final SmlDocumentationList sosDocumentationList) {
        final DocumentList documentList = DocumentList.Factory.newInstance();
        if (sosDocumentationList.isSetDescription()) {
            documentList.addNewDescription().setStringValue(sosDocumentationList.getDescription());
        }
        if (sosDocumentationList.isSetMembers()) {
            for (final SmlDocumentationListMember sosMmember : sosDocumentationList.getMember()) {
                final net.opengis.sensorML.x101.DocumentListDocument.DocumentList.Member member =
                        documentList.addNewMember();
                member.setName(sosMmember.getName());
                member.setDocument(createDocument(sosMmember.getDocumentation()));
            }
        }
        return documentList;
    }

    /**
     * Creates the position section of the SensorML description.
     * 
     * @param position
     *            SOS position
     * @return XML Position element
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    private Position createPosition(final SmlPosition position) throws OwsExceptionReport {
        final Position xbPosition = Position.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (position.isSetName()) {
            xbPosition.setName(position.getName().getValue());
        } else {
            xbPosition.setName("position");
        }
        final PositionType xbSwePosition = xbPosition.addNewPosition();
        xbSwePosition.setFixed(position.isFixed());
        xbSwePosition.setReferenceFrame(position.getReferenceFrame());
        final VectorType xbVector = xbSwePosition.addNewLocation().addNewVector();
        for (final SweCoordinate<?> coordinate : position.getPosition()) {
            if (coordinate.getValue().getValue() != null
                    && (!coordinate.getValue().isSetValue() || !coordinate.getValue().getValue().equals(Double.NaN))) {
                // FIXME: SWE Common NS
                xbVector.addNewCoordinate().set(CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_101, coordinate));
            }
        }
        return xbPosition;
    }

    /**
     * Creates the location section of the SensorML description.
     * 
     * @param location
     *            SOS location representation.
     * @return XML SmlLocation2 element
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    private SmlLocation2 createLocation(final SmlLocation location) throws OwsExceptionReport {
        final SmlLocation2 xbLocation =
                SmlLocation2.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (location.isSetPoint()) {
            final XmlObject xbPoint = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, location.getPoint());
            if (xbPoint instanceof PointType) {
                xbLocation.setPoint((PointType) xbPoint);
            }
        }
        return xbLocation;
    }

    /**
     * Creates the inputs section of the SensorML description.
     * 
     * @param inputs
     *            SOS SWE representation.
     * @return XML Inputs element
     * 
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    private Inputs createInputs(final List<SmlIo<?>> inputs) throws OwsExceptionReport {
        final Inputs xbInputs = Inputs.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final InputList xbInputList = xbInputs.addNewInputList();
        int counter = 1;
        for (final SmlIo<?> sosSMLIo : inputs) {
            if (!sosSMLIo.isSetName()) {
                sosSMLIo.setIoName("input_" + counter++);
            }
            addIoComponentPropertyType(xbInputList.addNewInput(), sosSMLIo);
        }
        return xbInputs;
    }

    /**
     * Creates the outputs section of the SensorML description.
     * 
     * @param sosOutputs
     *            SOS SWE representation.
     * @return XML Outputs element
     * 
     * @throws OwsExceptionReport
     */
    private Outputs createOutputs(final List<SmlIo<?>> sosOutputs) throws OwsExceptionReport {
        final Outputs outputs = Outputs.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final OutputList outputList = outputs.addNewOutputList();
        final Set<String> definitions = Sets.newHashSet();
        int counter = 1;
        final Set<String> outputNames = Sets.newHashSet();
        for (final SmlIo<?> sosSMLIo : sosOutputs) {
            if (sosSMLIo.isSetValue() && !definitions.contains(sosSMLIo.getIoValue().getDefinition())) {
                if (!sosSMLIo.isSetName() || outputNames.contains(sosSMLIo.getIoName())) {
                    sosSMLIo.setIoName(getValidOutputName(counter++, outputNames));
                }
                outputNames.add(sosSMLIo.getIoName());
                addIoComponentPropertyType(outputList.addNewOutput(), sosSMLIo);
                definitions.add(sosSMLIo.getIoValue().getDefinition());
            }
        }
        return outputs;
    }

    /**
     * Creates the components section of the SensorML description.
     * 
     * @param sosComponents
     *            SOS SWE representation.
     * @return encoded sml:components
     * @throws OwsExceptionReport
     */
    private Components createComponents(final List<SmlComponent> sosComponents) throws OwsExceptionReport {
        final Components components = Components.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final ComponentList componentList = components.addNewComponentList();
        for (final SmlComponent sosSMLComponent : sosComponents) {
            final Component component = componentList.addNewComponent();
            if (sosSMLComponent.getName() != null) {
                component.setName(sosSMLComponent.getName());
            }
            if (sosSMLComponent.getTitle() != null) {
                component.setTitle(sosSMLComponent.getTitle());
            }
            if (sosSMLComponent.getHref() != null) {
                component.setHref(sosSMLComponent.getHref());
            }
            if (sosSMLComponent.getProcess() != null) {
                XmlObject xmlObject = null;
                if (sosSMLComponent.getProcess().getSensorDescriptionXmlString() != null
                        && !sosSMLComponent.getProcess().getSensorDescriptionXmlString().isEmpty()) {
                    try {
                        xmlObject =
                                XmlObject.Factory.parse(sosSMLComponent.getProcess().getSensorDescriptionXmlString());

                    } catch (final XmlException xmle) {
                        throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                                "Error while encoding SensorML child procedure description "
                                        + "from stored SensorML encoded sensor description with XMLBeans");
                    }
                } else {
                    xmlObject = createSensorDescriptionFromObject(sosSMLComponent.getProcess());
                }
                if (xmlObject != null) {
                    AbstractProcessType xbProcess = null;
                    if (xmlObject instanceof SensorMLDocument) {
                        final SensorMLDocument smlDoc = (SensorMLDocument) xmlObject;
                        for (final Member member : smlDoc.getSensorML().getMemberArray()) {
                            xbProcess = member.getProcess();
                            break;
                        }
                    } else if (xmlObject instanceof AbstractProcessType) {
                        xbProcess = (AbstractProcessType) xmlObject;
                    } else {
                        throw new NoApplicableCodeException()
                                .withMessage("The sensor type is not supported by this SOS");
                    }
                    // TODO add feature/parentProcs/childProcs to component - is
                    // this already done?

                    // TODO xbProcess may be null
                    final SchemaType schemaType = xbProcess.schemaType();
                    component.addNewProcess().substitute(getQnameForType(schemaType), schemaType).set(xbProcess);
                }
            }
        }
        return components;
    }

    /**
     * Adds a SOS SWE simple type to a XML SWE field.
     * 
     * @param xbField
     *            XML SWE field
     * @param sosSweData
     *            SOS field element content
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    private void addSweSimpleTypeToField(final AnyScalarPropertyType xbField, final SweAbstractDataComponent sosSweData)
            throws OwsExceptionReport {
        final Encoder<?, SweAbstractDataComponent> encoder =
                CodingRepository.getInstance().getEncoder(
                        new XmlEncoderKey(SweConstants.NS_SWE_101, SweDataArray.class));
        if (encoder != null) {
            final XmlObject encoded = (XmlObject) encoder.encode(sosSweData);
            if (sosSweData instanceof SweAbstractSimpleType) {
                final SweAbstractSimpleType<?> sosSweSimpleType = (SweAbstractSimpleType<?>) sosSweData;
                switch (sosSweSimpleType.getDataComponentType()) {
                case Boolean:
                    xbField.addNewBoolean().set(encoded);
                    break;
                case Category:
                    xbField.addNewCategory().set(encoded);
                    break;
                case Count:
                    xbField.addNewCount().set(encoded);
                    break;
                case Quantity:
                    xbField.addNewQuantity().set(encoded);
                    break;
                case Text:
                    xbField.addNewText().set(encoded);
                    break;
                case Time:
                    xbField.addNewTime().set(encoded);
                    break;
                default:
                    throw new NoApplicableCodeException().withMessage(
                            "The SWE simpleType '%s' is not supported by this SOS SensorML encoder!", sosSweSimpleType
                                    .getDataComponentType().name());
                }
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "The SosSweAbstractDataComponent '%s' is not supported by this SOS SensorML encoder!",
                        sosSweData);
            }
        } else {
            throw new NoApplicableCodeException().withMessage("The %s is not supported by this SOS for SWE fields!",
                    sosSweData.getClass().getSimpleName());
        }
    }

    /**
     * Adds a SOS SWE simple type to a XML SML IO component.
     * 
     * @param ioComponentPropertyType
     *            SML IO component
     * @param sosSMLIO
     *            SOS SWE simple type.
     * 
     * @throws OwsExceptionReport
     */
    private void addIoComponentPropertyType(final IoComponentPropertyType ioComponentPropertyType,
            final SmlIo<?> sosSMLIO) throws OwsExceptionReport {
        ioComponentPropertyType.setName(sosSMLIO.getIoName());
        final XmlObject encodeObjectToXml =
                CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_101, sosSMLIO.getIoValue());
        switch (sosSMLIO.getIoValue().getDataComponentType()) {
        case Boolean:
            ioComponentPropertyType.addNewBoolean().set(encodeObjectToXml);
            break;
        case Category:
            ioComponentPropertyType.addNewCategory().set(encodeObjectToXml);
            break;
        case Count:
            ioComponentPropertyType.addNewCount().set(encodeObjectToXml);
            break;
        case CountRange:
            ioComponentPropertyType.addNewCountRange().set(encodeObjectToXml);
            break;
        case ObservableProperty:
            ioComponentPropertyType.addNewObservableProperty().set(encodeObjectToXml);
            break;
        case Quantity:
            ioComponentPropertyType.addNewQuantity().set(encodeObjectToXml);
            break;
        case QuantityRange:
            ioComponentPropertyType.addNewQuantityRange().set(encodeObjectToXml);
            break;
        case Text:
            ioComponentPropertyType.addNewText().set(encodeObjectToXml);
            break;
        case Time:
            ioComponentPropertyType.addNewTime().set(encodeObjectToXml);
            break;
        case TimeRange:
            ioComponentPropertyType.addNewTimeRange().set(encodeObjectToXml);
            break;
        case DataArray:
        	if (encodeObjectToXml instanceof DataArrayDocument) {
        		ioComponentPropertyType.addNewAbstractDataArray1().set(((DataArrayDocument)encodeObjectToXml).getDataArray1()).substitute(SweConstants.QN_DATA_ARRAY_SWE_101, DataArrayType.type);
        	} else {
        		ioComponentPropertyType.addNewAbstractDataArray1().set(encodeObjectToXml).substitute(SweConstants.QN_DATA_ARRAY_SWE_101, DataArrayType.type);
        	}
            break;
        case DataRecord:
            ioComponentPropertyType.addNewAbstractDataRecord().set(encodeObjectToXml).substitute(SweConstants.QN_DATA_RECORD_SWE_101, DataRecordType.type);
            break;
        default:

        }
    }

    /**
     * Get the QName for the SchemaType
     * 
     * @param type
     *            Schema type
     * @return Related QName
     */
    private QName getQnameForType(final SchemaType type) {
        if (type == SystemType.type) {
            return SensorMLConstants.SYSTEM_QNAME;
        } else if (type == ProcessModelType.type) {
            return SensorMLConstants.PROCESS_MODEL_QNAME;
        }
        return SensorMLConstants.ABSTRACT_PROCESS_QNAME;
    }

   
}
