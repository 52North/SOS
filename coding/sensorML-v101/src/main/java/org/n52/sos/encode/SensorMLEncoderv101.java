/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import static org.n52.shetland.util.CollectionHelper.union;
import static org.n52.sos.util.CodingHelper.encoderKeysForElements;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import net.opengis.gml.PointType;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.CharacteristicsDocument.Characteristics;
import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList.Classifier;
import net.opengis.sensorML.x101.ComponentDocument;
import net.opengis.sensorML.x101.ComponentType;
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
import net.opengis.swe.x101.AbstractDataComponentType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.coding.encode.XmlEncoderKey;
import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.SupportedType;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.ProcessMethod;
import org.n52.shetland.ogc.sensorML.ProcessModel;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.SmlContact;
import org.n52.shetland.ogc.sensorML.SmlContactList;
import org.n52.shetland.ogc.sensorML.SmlPerson;
import org.n52.shetland.ogc.sensorML.SmlResponsibleParty;
import org.n52.shetland.ogc.sensorML.System;
import org.n52.shetland.ogc.sensorML.elements.AbstractSmlDocumentation;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilities;
import org.n52.shetland.ogc.sensorML.elements.SmlCharacteristics;
import org.n52.shetland.ogc.sensorML.elements.SmlClassifier;
import org.n52.shetland.ogc.sensorML.elements.SmlComponent;
import org.n52.shetland.ogc.sensorML.elements.SmlDocumentation;
import org.n52.shetland.ogc.sensorML.elements.SmlDocumentationList;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sensorML.elements.SmlLocation;
import org.n52.shetland.ogc.sensorML.elements.SmlPosition;
import org.n52.shetland.ogc.sos.ProcedureDescriptionFormat;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.swe.AbstractOptionalSweDataComponentVisitor;
import org.n52.shetland.ogc.swe.AbstractVoidSweDataComponentVisitor;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.ogc.swe.SweConstants.SweAggregateType;
import org.n52.shetland.ogc.swe.SweCoordinate;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.SweSimpleDataRecord;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweCountRange;
import org.n52.shetland.ogc.swe.simpleType.SweObservableProperty;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweQuantityRange;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swe.simpleType.SweTime;
import org.n52.shetland.ogc.swe.simpleType.SweTimeRange;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.EncodingContext;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;

import com.google.common.base.Joiner;
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

    private static final ImmutableSet<SupportedType> SUPPORTED_TYPES
            = ImmutableSet.<SupportedType>builder()
                    .add(new ProcedureDescriptionFormat(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL))
                    .add(new ProcedureDescriptionFormat(SensorMLConstants.SENSORML_CONTENT_TYPE.toString()))
                    .build();

    private static final Map<String, ImmutableMap<String, Set<String>>> SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS =
            ImmutableMap.of(SosConstants.SOS, ImmutableMap.<String, Set<String>> builder()
                            .put(Sos2Constants.SERVICEVERSION, ImmutableSet.of(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL))
                            .put(Sos1Constants.SERVICEVERSION, ImmutableSet.of(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE))
                            .build());

    private static final Set<EncoderKey> ENCODER_KEYS = union(
            encoderKeysForElements(SensorMLConstants.NS_SML, AbstractSensorML.class),
            encoderKeysForElements(SensorMLConstants.SENSORML_CONTENT_TYPE.toString(), AbstractSensorML.class));

    public SensorMLEncoderv101() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getKeys() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Set<SupportedType> getSupportedTypes() {
        return Collections.unmodifiableSet(SUPPORTED_TYPES);
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
        return SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS
                .getOrDefault(service, ImmutableMap.of())
                .getOrDefault(version, Collections.emptySet());
    }

    @Override
    public XmlObject encode(final Object response, final EncodingContext additionalValues)
            throws EncodingException {
        XmlObject encodedObject = null;
        if (response instanceof AbstractSensorML) {
            encodedObject = createSensorDescription((AbstractSensorML) response);
//        }
        // FIXME workaround? if of type UnknowProcedureType try to parse the
        // description string, UNIT is missing "NOT_DEFINED"?!
//        else if (response instanceof SosProcedureDescriptionUnknownType) {
//            final String procDescXMLString = ((SosProcedureDescription) response).getXml();
//            final AbstractSensorML sensorDesc = new AbstractSensorML();
//            sensorDesc.setXml(procDescXMLString);
//            encodedObject = createSensorDescriptionFromString(sensorDesc);
        } else {
            throw new UnsupportedEncoderInputException(this, response);
        }
        // check if all gml:id are unique
        XmlHelper.makeGmlIdsUnique(encodedObject.getDomNode());
        XmlHelper.validateDocument(encodedObject, EncodingException::new);
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
    private XmlObject createSensorDescription(final AbstractSensorML sensorDesc) throws EncodingException {
        if (sensorDesc.isSetXml()) {
            return createSensorDescriptionFromString(sensorDesc);
        } else {
            return createSensorDescriptionFromObject(sensorDesc);
        }
    }

    protected XmlObject createSensorDescriptionFromString(final AbstractSensorML sensorDesc)
            throws EncodingException {
        try {
            final XmlObject xmlObject = XmlObject.Factory.parse(sensorDesc.getXml());
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
                            } else if  (member.getProcess() instanceof ComponentType
                                    && absProcess instanceof org.n52.shetland.ogc.sensorML.Component) {
                                addComponentValues((ComponentType) member.getProcess(),
                                        (org.n52.shetland.ogc.sensorML.Component) absProcess);
                            }
                        }
                    } else if (sensorDesc instanceof AbstractProcess) {
                        addAbstractProcessValues(member.getProcess(), (AbstractProcess) sensorDesc);
                        if (member.getProcess() instanceof SystemType && sensorDesc instanceof System) {
                            addSystemValues((SystemType) member.getProcess(), (System) sensorDesc);
                        }  else if (member.getProcess() instanceof ProcessModelType
                                && sensorDesc instanceof ProcessModel) {
                            addProcessModelValues((ProcessModelType) member.getProcess(),
                                    (ProcessModel) sensorDesc);
                        } else if  (member.getProcess() instanceof ComponentType
                                && sensorDesc instanceof org.n52.shetland.ogc.sensorML.Component) {
                            addComponentValues((ComponentType) member.getProcess(),
                                    (org.n52.shetland.ogc.sensorML.Component) sensorDesc);
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
                } else if (abstractProcess instanceof ComponentType && sensorDesc instanceof org.n52.shetland.ogc.sensorML.Component) {
                    addComponentValues((ComponentType)abstractProcess, (org.n52.shetland.ogc.sensorML.Component)sensorDesc);
                }
            }
            return xmlObject;
        } catch (final XmlException xmle) {
            throw new EncodingException(xmle);
        }
    }

    private XmlObject createSensorDescriptionFromObject(final AbstractSensorML sensorDesc) throws EncodingException {
        if (sensorDesc instanceof SensorML) {
            return createSensorMLDescription((SensorML) sensorDesc);
        } else if (sensorDesc instanceof AbstractProcess) {
            return createProcessDescription((AbstractProcess) sensorDesc);
        } else {
            throw new EncodingException("The sensor description type is not supported by this service!");
        }

    }

    private XmlObject createProcessDescription(final AbstractProcess sensorDesc) throws EncodingException {
        // TODO Review: System -> return doc; ProcessModel -> return type
        if (sensorDesc instanceof System) {
            System system = (System) sensorDesc;
            SystemDocument xbSystemDoc = SystemDocument.Factory.newInstance(getXmlOptions());
            SystemType xbSystem = xbSystemDoc.addNewSystem();
            addAbstractProcessValues(xbSystem, system);
            addSystemValues(xbSystem, system);
            return xbSystem;
        } else if (sensorDesc instanceof ProcessModel) {
            // TODO: set values
            ProcessModel processModel = (ProcessModel) sensorDesc;
            ProcessModelDocument xbProcessModelDoc = ProcessModelDocument.Factory.newInstance(getXmlOptions());
            ProcessModelType xbProcessModel = xbProcessModelDoc.addNewProcessModel();
            addAbstractProcessValues(xbProcessModel, processModel);
            addProcessModelValues(xbProcessModel, processModel);
            return xbProcessModel;
        } else if (sensorDesc instanceof org.n52.shetland.ogc.sensorML.Component) {
            org.n52.shetland.ogc.sensorML.Component component = (org.n52.shetland.ogc.sensorML.Component)sensorDesc;
            ComponentDocument cd = ComponentDocument.Factory.newInstance(getXmlOptions());
            ComponentType ct = cd.addNewComponent();
            addAbstractProcessValues(ct, component);
            return ct;
        } else {
            throw new EncodingException("The sensor description type is not supported by this service!");
        }
    }

    protected SensorMLDocument createSensorMLDescription(final SensorML smlSensorDesc) throws EncodingException {
        final SensorMLDocument sensorMLDoc = SensorMLDocument.Factory.newInstance(getXmlOptions());
        final net.opengis.sensorML.x101.SensorMLDocument.SensorML xbSensorML = sensorMLDoc.addNewSensorML();
        xbSensorML.setVersion(SensorMLConstants.VERSION_V101);
        if (smlSensorDesc.isSetMembers()) {
            for (final AbstractProcess sml : smlSensorDesc.getMembers()) {
                if (sml instanceof System) {
                    final SystemType xbSystem = (SystemType) xbSensorML.addNewMember().addNewProcess().substitute(
                            new QName(SensorMLConstants.NS_SML, SensorMLConstants.EN_SYSTEM), SystemType.type);
                    final System smlSystem = (System) sml;
                    addAbstractProcessValues(xbSystem, smlSystem);
                    addSystemValues(xbSystem, smlSystem);
                } else if (sml instanceof ProcessModel) {
                    final ProcessModelType xbProcessModel =
                            (ProcessModelType) xbSensorML.addNewMember().addNewProcess().substitute(
                                    new QName(SensorMLConstants.NS_SML, SensorMLConstants.EN_PROCESS_MODEL),
                                    ProcessModelType.type);
                    final ProcessModel smlProcessModel = (ProcessModel) sml;
                    addAbstractProcessValues(xbProcessModel, smlProcessModel);
                    addProcessModelValues(xbProcessModel, smlProcessModel);
                } else if (sml instanceof org.n52.shetland.ogc.sensorML.Component) {
                    final ComponentType xbCompontent =  (ComponentType) xbSensorML.addNewMember().addNewProcess().substitute(
                            new QName(SensorMLConstants.NS_SML, SensorMLConstants.EN_COMPONENT),
                            ComponentType.type);
                    final org.n52.shetland.ogc.sensorML.Component smlComponent = (org.n52.shetland.ogc.sensorML.Component) sml;
                    addAbstractProcessValues(xbCompontent, smlComponent);
                    addComponentValues(xbCompontent, smlComponent);
                }
            }
        }
        return sensorMLDoc;
    }

    private ContactList createContactList(final List<SmlContact> contacts) {
        final ContactList xbContacts = ContactList.Factory.newInstance();
        contacts.forEach((smlContact) -> {
            if (smlContact.isSetHref()) {
                ContactList.Member member = xbContacts.addNewMember();
                member.setHref(smlContact.getHref());
                if (smlContact.isSetTitle()) {
                    member.setTitle(smlContact.getTitle());
                }
                if (smlContact.isSetRole()) {
                    member.setRole(smlContact.getRole());
                }
            } else if (smlContact instanceof SmlPerson) {
                ContactList.Member member = xbContacts.addNewMember();
                member.addNewPerson().set(createPerson((SmlPerson) smlContact));
                if (smlContact.isSetRole()) {
                    member.setRole(smlContact.getRole());
                }
            } else if (smlContact instanceof SmlResponsibleParty) {
                ContactList.Member member = xbContacts.addNewMember();
                member.addNewResponsibleParty().set(createResponsibleParty((SmlResponsibleParty) smlContact));
                if (smlContact.isSetRole()) {
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
        });
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
                smlRespParty.getPhoneFax().forEach(xbPhone::addFacsimile);
            }
            if (smlRespParty.isSetPhoneVoice()) {
                smlRespParty.getPhoneVoice().forEach(xbPhone::addVoice);
            }
        }
        if (smlRespParty.isSetAddress()) {
            final Address xbAddress = xbContactInfo.addNewAddress();
            if (smlRespParty.isSetDeliveryPoint()) {
                smlRespParty.getDeliveryPoint().forEach(xbAddress::addDeliveryPoint);
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
            final AbstractProcess sosAbstractProcess) throws EncodingException {
        if (sosAbstractProcess.isSetGmlID()) {
            abstractProcess.setId(sosAbstractProcess.getGmlId());
        }

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
            final Time time = sosAbstractProcess.getMergedValidTime();
            final XmlObject xbtime = encodeObjectToXml(GmlConstants.NS_GML, time);
            if (time instanceof TimeInstant) {
                abstractProcess.addNewValidTime().addNewTimeInstant().set(xbtime);
            } else if (time instanceof TimePeriod) {
                abstractProcess.addNewValidTime().addNewTimePeriod().set(xbtime);
            }
        }
    }

    private void addNamesToAbstractProcess(AbstractProcessType abstractProcess, List<CodeType> names)
            throws EncodingException {
        for (CodeType codeType : names) {
            abstractProcess.addNewName().set(encodeObjectToXml(GmlConstants.NS_GML, codeType));
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
            } else if (member.isSetResponsibleParty() &&
                     !isContained(member.getResponsibleParty(), mergedResponsibleParties)) {
                mergedResponsibleParties.add(member.getResponsibleParty());
            }
        }
        final Contact newContact = Contact.Factory.newInstance();
        final ContactList newContactList = ContactList.Factory.newInstance();
        mergedResponsibleParties.forEach(responsibleParty
                -> newContactList.addNewMember().addNewResponsibleParty().set(responsibleParty));
        mergedPersons.forEach(person
                -> newContactList.addNewMember().addNewPerson().set(person));
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

    private boolean isContained(ResponsibleParty rp, Set<ResponsibleParty> mergedResponsibleParties) {
        final XmlOptions xmlOptions = getXmlOptions();
        return mergedResponsibleParties.stream().anyMatch(rp2 -> isIdentical(rp, xmlOptions, rp2));
    }

    private boolean isContained(Person person, Set<Person> mergedPersons) {
        final XmlOptions xmlOptions = getXmlOptions();
        return mergedPersons.stream().anyMatch(p2 -> isIdentical(person, xmlOptions, p2));
    }

    private boolean isContactListSetAndContainingElements(Contact contact) {
        return contact.getContactList() != null && contact.getContactList().getMemberArray() != null
                && contact.getContactList().getMemberArray().length > 0;
    }

    private void removeCapability(AbstractProcessType abstractProcess, Capabilities c) {
        // get current index of element with this name
        for (int i = 0; i < abstractProcess.getCapabilitiesArray().length; i++) {
            if (abstractProcess.getCapabilitiesArray(i).getName().equals(c.getName())) {
                abstractProcess.removeCapabilities(i);
                return;
            }
        }
    }

    private Capabilities createCapability(final SmlCapabilities capabilities) throws EncodingException {
        final Capabilities xbCapabilities = Capabilities.Factory.newInstance(getXmlOptions());
        if (capabilities.isSetName()) {
            xbCapabilities.setName(capabilities.getName());
        }
        if (capabilities.isSetAbstractDataRecord() && capabilities.getDataRecord().isSetFields()) {
            final XmlObject encodedDataRecord = encodeObjectToXml(SweConstants.NS_SWE_101, capabilities.getDataRecord());
            final XmlObject substituteElement =
                    XmlHelper.substituteElement(xbCapabilities.addNewAbstractDataRecord(), encodedDataRecord);
            substituteElement.set(encodedDataRecord);
        } else if (capabilities.isSetHref()) {
            xbCapabilities.setHref(capabilities.getHref());
            if (capabilities.isSetTitle()) {
                xbCapabilities.setTitle(capabilities.getTitle());
            }
        }
        return xbCapabilities;
    }

    private void addSystemValues(final SystemType xbSystem, final System system) throws EncodingException {
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
        if (system.isSetComponents()) {
            if (system.isSetComponents()) {
                smlComponents.addAll(system.getComponents());
            }
            if (!smlComponents.isEmpty()) {
                final Components components = createComponents(smlComponents);
                if (components != null && components.getComponentList() != null
                        && components.getComponentList().sizeOfComponentArray() > 0) {
                    xbSystem.setComponents(components);
                }
            }
        }
        // set outputs
        if (system.isSetOutputs()) {
            xbSystem.setOutputs(createOutputs(system.getOutputs()));
        }
    }

    private void addComponentValues(final ComponentType ct, final org.n52.shetland.ogc.sensorML.Component component) throws EncodingException {
        // set inputs
        if (component.isSetInputs()) {
            ct.setInputs(createInputs(component.getInputs()));
        }
        // set position
        if (component.isSetPosition()) {
            ct.setPosition(createPosition(component.getPosition()));
        }
        // set location
        if (component.isSetLocation()) {
            ct.setSmlLocation(createLocation(component.getLocation()));
        }
        // set outputs
        if (component.isSetOutputs()) {
            ct.setOutputs(createOutputs(component.getOutputs()));
        }
    }

    private void addProcessModelValues(final ProcessModelType processModel, final ProcessModel sosProcessModel)
            throws EncodingException {
        // set inputs
        if (sosProcessModel.isSetInputs()) {
            processModel.setInputs(createInputs(sosProcessModel.getInputs()));
        }
        // set outputs
        if (sosProcessModel.isSetOutputs()) {
            processModel.setOutputs(createOutputs(sosProcessModel.getOutputs()));
        }
        // set method
        processModel.setMethod(createMethod(sosProcessModel.getMethod()));
    }

    private MethodPropertyType createMethod(final ProcessMethod method) throws EncodingException {
        final MethodPropertyType xbMethod = MethodPropertyType.Factory.newInstance(getXmlOptions());
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
            throw new EncodingException("method",
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
    protected Identification[] createIdentification(List<SmlIdentifier> identifications) {
        Identification xbIdentification = Identification.Factory.newInstance(getXmlOptions());
        IdentifierList xbIdentifierList = xbIdentification.addNewIdentifierList();
        identifications.forEach(sosSMLIdentifier -> {
            Identifier xbIdentifier = xbIdentifierList.addNewIdentifier();
            if (sosSMLIdentifier.getName() != null) {
                xbIdentifier.setName(sosSMLIdentifier.getName());
            }
            Term xbTerm = xbIdentifier.addNewTerm();
            xbTerm.setDefinition(sosSMLIdentifier.getDefinition());
            xbTerm.setValue(sosSMLIdentifier.getValue());
        });
        return new Identification[] { xbIdentification };
    }

    /**
     * Creates the classification section of the SensorML description.
     *
     * @param classifications
     *            SOS classifications
     * @return XML Classification array
     */
    private Classification[] createClassification(List<SmlClassifier> classifications) {
        Classification xbClassification = Classification.Factory.newInstance(getXmlOptions());
        ClassifierList xbClassifierList = xbClassification.addNewClassifierList();
        classifications.forEach(sosSMLClassifier -> {
            Classifier xbClassifier = xbClassifierList.addNewClassifier();
            if (sosSMLClassifier.getName() != null) {
                xbClassifier.setName(sosSMLClassifier.getName());
            }
            Term xbTerm = xbClassifier.addNewTerm();
            xbTerm.setValue(sosSMLClassifier.getValue());
            if (sosSMLClassifier.isSetDefinition()) {
                xbTerm.setDefinition(sosSMLClassifier.getDefinition());
            }
            if (sosSMLClassifier.isSetCodeSpace()) {
                xbTerm.addNewCodeSpace().setHref(sosSMLClassifier.getCodeSpace());
            }
        });
        return new Classification[] { xbClassification };
    }

    /**
     * Creates the characteristics section of the SensorML description.
     *
     * @param smlCharacteristics
     *            SOS characteristics list
     * @return XML Characteristics array
     * @throws EncodingException
     *             If an error occurs
     */
    private Characteristics[] createCharacteristics(final List<SmlCharacteristics> smlCharacteristics)
            throws EncodingException {
        final List<Characteristics> characteristicsList =
                Lists.newArrayListWithExpectedSize(smlCharacteristics.size());
        for (final SmlCharacteristics sosSMLCharacteristics : smlCharacteristics) {
            final Characteristics xbCharacteristics = Characteristics.Factory.newInstance(getXmlOptions());
            if (sosSMLCharacteristics.isSetName()) {
                xbCharacteristics.setName(sosSMLCharacteristics.getName());
            }
            if (sosSMLCharacteristics.isSetAbstractDataRecord()) {
                if (sosSMLCharacteristics.getDataRecord() instanceof SweSimpleDataRecord) {
                    final SimpleDataRecordType xbSimpleDataRecord =
                            (SimpleDataRecordType) xbCharacteristics.addNewAbstractDataRecord()
                                    .substitute(SweConstants.QN_SIMPLEDATARECORD_SWE_101, SimpleDataRecordType.type);
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
                    throw new EncodingException(
                            "The SWE characteristics type '%s' is not supported by this SOS for SensorML characteristics!",
                            SweAggregateType.DataRecord);
                } else {
                    throw new EncodingException(
                            "The SWE characteristics type '%s' is not supported by this SOS for SensorML characteristics!",
                            sosSMLCharacteristics.getDataRecord().getClass().getName());
                }
            } else if (sosSMLCharacteristics.isSetHref()) {
                if (sosSMLCharacteristics.isSetName()) {
                    xbCharacteristics.setName(sosSMLCharacteristics.getName());
                }
                xbCharacteristics.setHref(sosSMLCharacteristics.getHref());
                if (sosSMLCharacteristics.isSetTitle()) {
                    xbCharacteristics.setTitle(sosSMLCharacteristics.getTitle());
                }
            }
            characteristicsList.add(xbCharacteristics);
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
        return sosDocumentation.stream().map((abstractSosSMLDocumentation) -> {
                    Documentation documentation = Documentation.Factory.newInstance();
                    if (abstractSosSMLDocumentation instanceof SmlDocumentation) {
                        documentation.setDocument(createDocument((SmlDocumentation) abstractSosSMLDocumentation));
                    } else if (abstractSosSMLDocumentation instanceof SmlDocumentationList) {
                        documentation.setDocumentList(createDocumentationList((SmlDocumentationList) abstractSosSMLDocumentation));
                    }
                    return documentation;
                }).toArray(l -> new Documentation[l]);
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
            sosDocumentationList.getMember()
                    .forEach((sosMmember) -> {
                        net.opengis.sensorML.x101.DocumentListDocument.DocumentList.Member member =
                                documentList.addNewMember();
                        member.setName(sosMmember.getName());
                        member.setDocument(createDocument(sosMmember.getDocumentation()));
            });
        }
        return documentList;
    }

    /**
     * Creates the position section of the SensorML description.
     *
     * @param position
     *            SOS position
     * @return XML Position element
     * @throws EncodingException
     *             if an error occurs
     */
    private Position createPosition(final SmlPosition position) throws EncodingException {
        Position xbPosition = Position.Factory.newInstance(getXmlOptions());
        if (position.isSetName()) {
            xbPosition.setName(position.getName().getValue());
        } else {
            xbPosition.setName("position");
        }
        PositionType xbSwePosition = xbPosition.addNewPosition();
        xbSwePosition.setFixed(position.isFixed());
        xbSwePosition.setReferenceFrame(position.getReferenceFrame());
        final VectorType xbVector = xbSwePosition.addNewLocation().addNewVector();
        for (SweCoordinate<?> coordinate : position.getPosition()) {
            if (coordinate.getValue().getValue() != null
                    && (!coordinate.getValue().isSetValue() || !coordinate.getValue().getValue().equals(Double.NaN))) {
                // FIXME: SWE Common NS
                xbVector.addNewCoordinate().set(encodeObjectToXml(SweConstants.NS_SWE_101, coordinate));
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
     * @throws EncodingException
     *             if an error occurs
     */
    private SmlLocation2 createLocation(SmlLocation location) throws EncodingException {
        final SmlLocation2 xbLocation = SmlLocation2.Factory.newInstance(getXmlOptions());
        if (location.isSetPoint()) {
            XmlObject xbPoint = encodeObjectToXml(GmlConstants.NS_GML, location.getPoint());
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
     * @throws EncodingException
     *             if an error occurs
     */
    private Inputs createInputs(List<SmlIo> inputs) throws EncodingException {
        Inputs xbInputs = Inputs.Factory.newInstance(getXmlOptions());
        InputList xbInputList = xbInputs.addNewInputList();
        int counter = 1;
        for (SmlIo sosSMLIo : inputs) {
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
     * @throws EncodingException
     */
    private Outputs createOutputs(final List<SmlIo> sosOutputs) throws EncodingException {
        Outputs outputs = Outputs.Factory.newInstance(getXmlOptions());
        OutputList outputList = outputs.addNewOutputList();
        Set<String> definitions = Sets.newHashSet();
        int counter = 1;
        Set<String> outputNames = Sets.newHashSet();
        for (SmlIo sosSMLIo : sosOutputs) {
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
     * @throws EncodingException
     */
    private Components createComponents(final List<SmlComponent> sosComponents) throws EncodingException {
        Components components = Components.Factory.newInstance(getXmlOptions());
        ComponentList componentList = components.addNewComponentList();
        for (SmlComponent sosSMLComponent : sosComponents) {
            Component component = componentList.addNewComponent();
            if (sosSMLComponent.getName() != null) {
                component.setName(sosSMLComponent.getName());
            }
            if (sosSMLComponent.getHref() != null) {
                component.setHref(sosSMLComponent.getHref());
                if (sosSMLComponent.getTitle() != null) {
                    component.setTitle(sosSMLComponent.getTitle());
                }
            } else if (sosSMLComponent.getProcess() != null) {
                XmlObject xmlObject = null;
                if (sosSMLComponent.getProcess().getXml() != null
                        && !sosSMLComponent.getProcess().getXml().isEmpty()) {
                    try {
                        xmlObject =
                                XmlObject.Factory.parse(sosSMLComponent.getProcess().getXml());

                    } catch (final XmlException xmle) {
                        throw new EncodingException("Error while encoding SensorML child procedure description "
                                        + "from stored SensorML encoded sensor description with XMLBeans", xmle);
                    }
                } else {
                    if (sosSMLComponent.getProcess() instanceof SensorML) {
                        xmlObject = createSensorDescriptionFromObject(((SensorML)sosSMLComponent.getProcess()).getMembers().iterator().next());
                    } else if (sosSMLComponent.getProcess() instanceof AbstractProcess) {
                        xmlObject = createSensorDescriptionFromObject(sosSMLComponent.getProcess());
                    }
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
                    }

                    if (xbProcess == null) {
                        throw new EncodingException("The sensor type is not supported by this SOS");
                    }

                    // TODO add feature/parentProcs/childProcs to component - is
                    // this already done?

                    SchemaType schemaType = xbProcess.schemaType();
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
     * @throws EncodingException
     *             if an error occurs
     */
    private void addSweSimpleTypeToField(AnyScalarPropertyType xbField, SweAbstractDataComponent sosSweData) throws EncodingException {
        Encoder<?, SweAbstractDataComponent> encoder = getEncoder(new XmlEncoderKey(SweConstants.NS_SWE_101, SweDataArray.class));
        if (encoder == null) {
            throw new EncodingException("The %s is not supported by this SOS for SWE fields!", sosSweData.getClass().getSimpleName());
        }
        XmlObject encoded = (XmlObject) encoder.encode(sosSweData);

        if (!(sosSweData instanceof SweAbstractSimpleType)) {
            throw new EncodingException("The SosSweAbstractDataComponent '%s' is not supported by this SOS SensorML encoder!", sosSweData);
        }

        SweAbstractSimpleType<?> sosSweSimpleType = (SweAbstractSimpleType<?>) sosSweData;

        sosSweSimpleType.accept(new ScalarSweDataComponentAdder(xbField))
                .orElseThrow(() -> new EncodingException("The SWE simpleType '%s' is not supported by this SOS SensorML encoder!", sosSweSimpleType.getDataComponentType().name()))
                .set(encoded);

    }

    /**
     * Adds a SOS SWE simple type to a XML SML IO component.
     *
     * @param ioComponentPropertyType
     *            SML IO component
     * @param sosSMLIO
     *            SOS SWE simple type.
     *
     * @throws EncodingException
     */
    private void addIoComponentPropertyType(final IoComponentPropertyType ioComponentPropertyType,
            final SmlIo sosSMLIO) throws EncodingException {
        ioComponentPropertyType.setName(sosSMLIO.getIoName());
        if (sosSMLIO.isSetHref()) {
            ioComponentPropertyType.setHref(sosSMLIO.getTitle());
            if (sosSMLIO.isSetTitle()) {
                ioComponentPropertyType.setTitle(sosSMLIO.getTitle());
            }
        } else {
            XmlObject encodeObjectToXml;
            XmlObject xml = encodeObjectToXml(SweConstants.NS_SWE_101, sosSMLIO.getIoValue());

            if (xml instanceof DataArrayDocument) {
                encodeObjectToXml = ((DataArrayDocument) xml).getDataArray1();
            } else {
                encodeObjectToXml = xml;
            }

            sosSMLIO.getIoValue().accept(new SweDataComponentAdder(ioComponentPropertyType))
                    .map(h -> (AbstractDataComponentType) h.set(encodeObjectToXml))
                    .ifPresent(h -> sosSMLIO.getIoValue().accept(new SweDataComponentSubstituter(h)));
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
        } else if (type == ComponentType.type) {
            return SensorMLConstants.COMPONENT_QNAME;
        }
        return SensorMLConstants.ABSTRACT_PROCESS_QNAME;
    }

    private static class ScalarSweDataComponentAdder extends AbstractOptionalSweDataComponentVisitor<AbstractDataComponentType, RuntimeException> {
        private final AnyScalarPropertyType parent;

        ScalarSweDataComponentAdder(AnyScalarPropertyType parent) {
            this.parent = Objects.requireNonNull(parent);
        }

        @Override
        protected AbstractDataComponentType _visit(SweBoolean component) throws RuntimeException {
            return parent.addNewBoolean();
        }

        @Override
        protected AbstractDataComponentType _visit(SweCategory component) throws RuntimeException {
            return parent.addNewCategory();
        }

        @Override
        protected AbstractDataComponentType _visit(SweCount component) throws RuntimeException {
            return parent.addNewCount();
        }

        @Override
        protected AbstractDataComponentType _visit(SweText component) throws RuntimeException {
            return parent.addNewText();
        }

        @Override
        protected AbstractDataComponentType _visit(SweQuantity component) throws RuntimeException {
            return parent.addNewQuantity();
        }

        @Override
        protected AbstractDataComponentType _visit(SweTime component) throws RuntimeException {
            return parent.addNewTime();
        }


    }

    private static class SweDataComponentAdder extends AbstractOptionalSweDataComponentVisitor<AbstractDataComponentType, RuntimeException> {
        private final IoComponentPropertyType parent;

        SweDataComponentAdder(IoComponentPropertyType parent) {
            this.parent = Objects.requireNonNull(parent);
        }

        @Override
        public AbstractDataComponentType _visit(SweDataRecord component)  {
            return parent.addNewAbstractDataRecord();
        }

        @Override
        public AbstractDataComponentType _visit(SweDataArray component)  {
            return parent.addNewAbstractDataArray1();
        }

        @Override
        public AbstractDataComponentType _visit(SweCount component)  {
            return parent.addNewCount();
        }

        @Override
        public AbstractDataComponentType _visit(SweCountRange component)  {
            return parent.addNewCountRange();
        }

        @Override
        public AbstractDataComponentType _visit(SweBoolean component)  {
            return parent.addNewBoolean();
        }

        @Override
        public AbstractDataComponentType _visit(SweCategory component)  {
            return parent.addNewCategory();
        }

        @Override
        public AbstractDataComponentType _visit(SweObservableProperty component)  {
            return parent.addNewObservableProperty();
        }

        @Override
        public AbstractDataComponentType _visit(SweQuantity component)  {
            return parent.addNewQuantity();
        }

        @Override
        public AbstractDataComponentType _visit(SweQuantityRange component)  {
            return parent.addNewQuantityRange();
        }

        @Override
        public AbstractDataComponentType _visit(SweText component)  {
            return parent.addNewText();
        }

        @Override
        public AbstractDataComponentType _visit(SweTime component)  {
            return parent.addNewTime();
        }

        @Override
        public AbstractDataComponentType _visit(SweTimeRange component)  {
            return parent.addNewTimeRange();
        }

        @Override
        public AbstractDataComponentType _visit(SweSimpleDataRecord component)  {
            return parent.addNewAbstractDataRecord();
        }
    }

    private static class SweDataComponentSubstituter extends AbstractVoidSweDataComponentVisitor<RuntimeException> {
        private final AbstractDataComponentType dataComponentType;

        SweDataComponentSubstituter(AbstractDataComponentType dataComponentType) {
            this.dataComponentType = dataComponentType;
        }

        @Override
        protected void _visit(SweDataArray component) {
            dataComponentType.substitute(SweConstants.QN_DATA_ARRAY_SWE_101, DataArrayType.type);
        }

        @Override
        protected void _visit(SweSimpleDataRecord component) {
            dataComponentType.substitute(SweConstants.QN_SIMPLEDATARECORD_SWE_101, SimpleDataRecordType.type);
        }

        @Override
        protected void _visit(SweDataRecord component) {
            dataComponentType.substitute(SweConstants.QN_DATA_RECORD_SWE_101, DataRecordType.type);
        }
    }


}
