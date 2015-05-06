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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList.Component;
import net.opengis.sensorML.x101.ContactDocument.Contact;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo;
import net.opengis.sensorML.x101.ContactListDocument.ContactList;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.InputsDocument.Inputs.InputList;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.PersonDocument.Person;
import net.opengis.sensorML.x101.ResponsiblePartyDocument.ResponsibleParty;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.TermDocument.Term;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.DataArrayType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.SimpleDataRecordType;

import org.junit.Test;
import org.n52.sos.AbstractBeforeAfterClassSettingsManagerTest;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.SmlContactList;
import org.n52.sos.ogc.sensorML.SmlPerson;
import org.n52.sos.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;

/**
 * @author Shane StClair
 * 
 * @since 4.0.0
 */
public class SensorMLDecoderV101Test extends AbstractBeforeAfterClassSettingsManagerTest {
    private static final String TEST_ID_1 = "test-id-1";

    private static final String TEST_NAME_1 = "test-name-1";

    private static final String TEST_ID_2 = "test-id-2";

    private static final String TEST_NAME_2 = "test-name-2";

    @Test
    public void should_set_identifier_by_identifier_name() throws OwsExceptionReport {
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        IdentifierList xbIdentifierList = xbSystem.addNewIdentification().addNewIdentifierList();
        addIdentifier(xbIdentifierList, OGCConstants.URN_UNIQUE_IDENTIFIER_END, null, TEST_ID_1);
        addIdentifier(xbIdentifierList, "any name", null, TEST_ID_2);
        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getIdentifier(), is(TEST_ID_1));
        assertThat(absProcess.getIdentifications().size(), is(2));
    }

    private SensorMLDocument getSensorMLDoc() {
        SensorMLDocument xbSmlDoc =
                SensorMLDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        net.opengis.sensorML.x101.SensorMLDocument.SensorML xbSensorML = xbSmlDoc.addNewSensorML();
        xbSensorML.setVersion(SensorMLConstants.VERSION_V101);
        return xbSmlDoc;
    }

    @Test
    public void should_set_identifier_by_identifier_definition() throws OwsExceptionReport {
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        IdentifierList xbIdentifierList = xbSystem.addNewIdentification().addNewIdentifierList();
        addIdentifier(xbIdentifierList, "any name", OGCConstants.URN_UNIQUE_IDENTIFIER, TEST_ID_1);
        addIdentifier(xbIdentifierList, "any other name", null, TEST_ID_2);
        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getIdentifier(), is(TEST_ID_1));
        assertThat(absProcess.getIdentifications().size(), is(2));
    }

    @Test
    public void should_set_identifier_by_identifier_prefix_and_suffix() throws OwsExceptionReport {
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        IdentifierList xbIdentifierList = xbSystem.addNewIdentification().addNewIdentifierList();
        String definiton =
                OGCConstants.URN_UNIQUE_IDENTIFIER_START + "anything" + OGCConstants.URN_UNIQUE_IDENTIFIER_END;
        addIdentifier(xbIdentifierList, "any name", definiton, TEST_ID_1);
        addIdentifier(xbIdentifierList, "any other name", null, TEST_ID_2);
        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getIdentifier(), is(TEST_ID_1));
        assertThat(absProcess.getIdentifications().size(), is(2));
    }

    private void addIdentifier(IdentifierList xbIdentifierList, String name, String definition, String value) {
        Identifier xbIdentifier = xbIdentifierList.addNewIdentifier();
        xbIdentifier.setName(name);
        Term xbTerm = xbIdentifier.addNewTerm();
        xbTerm.setDefinition(definition);
        xbTerm.setValue(value);
    }

    @Test
    public void should_decode_offerings_from_sml() throws OwsExceptionReport {
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        Capabilities xbCapabilities = xbSystem.addNewCapabilities();
        xbCapabilities.setName(SensorMLConstants.ELEMENT_NAME_OFFERINGS);
        SimpleDataRecordType xbSimpleDataRecord =
                (SimpleDataRecordType) xbCapabilities.addNewAbstractDataRecord().substitute(
                        SweConstants.QN_SIMPLEDATARECORD_SWE_101, SimpleDataRecordType.type);
        addCapabilitiesInsertionMetadata(xbSimpleDataRecord, TEST_ID_1, TEST_NAME_1);
        addCapabilitiesInsertionMetadata(xbSimpleDataRecord, TEST_ID_2, TEST_NAME_2);
        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getOfferings().size(), is(2));
        assertThat(absProcess.getCapabilities().size(), is(1));
        List<SosOffering> sosOfferings = new ArrayList<SosOffering>(absProcess.getOfferings());
        Collections.sort(sosOfferings);
        assertThat(sosOfferings.get(0).getIdentifier(), is(TEST_ID_1));
        assertThat(sosOfferings.get(0).getOfferingName(), is(TEST_NAME_1));
        assertThat(sosOfferings.get(1).getIdentifier(), is(TEST_ID_2));
        assertThat(sosOfferings.get(1).getOfferingName(), is(TEST_NAME_2));
    }

    @Test
    public void should_decode_parent_procedures_from_sml() throws OwsExceptionReport {
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        Capabilities xbCapabilities = xbSystem.addNewCapabilities();
        xbCapabilities.setName(SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES);
        SimpleDataRecordType xbSimpleDataRecord =
                (SimpleDataRecordType) xbCapabilities.addNewAbstractDataRecord().substitute(
                        SweConstants.QN_SIMPLEDATARECORD_SWE_101, SimpleDataRecordType.type);
        addCapabilitiesInsertionMetadata(xbSimpleDataRecord, TEST_ID_1, TEST_NAME_1);
        addCapabilitiesInsertionMetadata(xbSimpleDataRecord, TEST_ID_2, TEST_NAME_2);
        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getParentProcedures().size(), is(2));
        assertThat(absProcess.getCapabilities().size(), is(1));
        List<String> parentProcedures = new ArrayList<String>(absProcess.getParentProcedures());
        Collections.sort(parentProcedures);
        assertThat(parentProcedures.get(0), is(TEST_ID_1));
        assertThat(parentProcedures.get(1), is(TEST_ID_2));
    }

    @Test
    public void should_decode_features_of_interest_from_sml() throws OwsExceptionReport {
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        Capabilities xbCapabilities = xbSystem.addNewCapabilities();
        xbCapabilities.setName(SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST);
        SimpleDataRecordType xbSimpleDataRecord =
                (SimpleDataRecordType) xbCapabilities.addNewAbstractDataRecord().substitute(
                        SweConstants.QN_SIMPLEDATARECORD_SWE_101, SimpleDataRecordType.type);
        addCapabilitiesInsertionMetadata(xbSimpleDataRecord, TEST_ID_1, TEST_NAME_1);
        addCapabilitiesInsertionMetadata(xbSimpleDataRecord, TEST_ID_2, TEST_NAME_2);
        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getFeaturesOfInterest().size(), is(2));
        assertThat(absProcess.getCapabilities().size(), is(1));
        List<String> featuresOfInterest = new ArrayList<String>(absProcess.getFeaturesOfInterest());
        Collections.sort(featuresOfInterest);
        assertThat(featuresOfInterest.get(0), is(TEST_ID_1));
        assertThat(featuresOfInterest.get(1), is(TEST_ID_2));
    }

    private void addCapabilitiesInsertionMetadata(SimpleDataRecordType xbSimpleDataRecord, String value, String name) {
        AnyScalarPropertyType xbField = xbSimpleDataRecord.addNewField();
        xbField.setName(name);
        xbField.addNewText().setValue(value);
        xbField.getText().addNewName().setStringValue(name);
    }

    private AbstractProcess decodeAbstractProcess(SensorMLDocument xbSmlDoc) throws OwsExceptionReport {
        Object decoded = CodingHelper.decodeXmlObject(xbSmlDoc);
        assertThat(decoded, instanceOf(SensorML.class));
        SensorML sml = (SensorML) decoded;
        assertThat(sml.getMembers().size(), is(1));
        return sml.getMembers().get(0);
    }

    @Test
    public void should_decode_child_procedure_from_sml() throws OwsExceptionReport {
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        IdentifierList xbIdentifierList = xbSystem.addNewIdentification().addNewIdentifierList();
        addIdentifier(xbIdentifierList, "anyname", OGCConstants.URN_UNIQUE_IDENTIFIER, TEST_ID_1);

        ComponentList xbComponentList = xbSystem.addNewComponents().addNewComponentList();
        addChildProcedure(xbComponentList, TEST_ID_2);
        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getIdentifier(), is(TEST_ID_1));
        assertThat(absProcess.getChildProcedures().size(), is(1));
        SosProcedureDescription childProcedure = absProcess.getChildProcedures().iterator().next();
        assertThat(childProcedure, instanceOf(System.class));
        assertThat(childProcedure.getIdentifier(), is(TEST_ID_2));
    }

    private void addChildProcedure(ComponentList xbComponentList, String identifier) {
        Component xbComponent = xbComponentList.addNewComponent();
        xbComponent.setName(SensorMLConstants.ELEMENT_NAME_CHILD_PROCEDURES);
        SystemType xbSystem =
                (SystemType) xbComponent.addNewProcess().substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        IdentifierList xbIdentifierList = xbSystem.addNewIdentification().addNewIdentifierList();
        addIdentifier(xbIdentifierList, "anyname", OGCConstants.URN_UNIQUE_IDENTIFIER, identifier);
    }

    @Test
    public void should_decode_io_from_sml() throws OwsExceptionReport {
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        InputList xbInputList = xbSystem.addNewInputs().addNewInputList();
        OutputList xbOutputList = xbSystem.addNewOutputs().addNewOutputList();
        IoComponentPropertyType input1 = xbInputList.addNewInput();
        input1.setName("input1");
        input1.addNewBoolean();
        IoComponentPropertyType input2 = xbInputList.addNewInput();
        input2.setName("input2");
        input2.addNewCount();

        IoComponentPropertyType output1 = xbOutputList.addNewOutput();
        output1.setName("output1");
        output1.addNewQuantity();
        IoComponentPropertyType output2 = xbOutputList.addNewOutput();
        output2.setName("output2");
        DataArrayType dataArray = getDataArray();
        output2.setAbstractDataArray1(dataArray);
        XmlHelper.substituteElement(output2.getAbstractDataArray1(), dataArray);
        IoComponentPropertyType output3 = xbOutputList.addNewOutput();
        output3.setName("output3");
        DataRecordType dataRecord = DataRecordType.Factory.newInstance();
        output3.setAbstractDataRecord(dataRecord);
        XmlHelper.substituteElement(output3.getAbstractDataRecord(), dataRecord);

        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getInputs().get(0).getIoValue().getDataComponentType(), is(SweDataComponentType.Boolean));
        // assertThat(
        // ((SweAbstractSimpleType)absProcess.getInputs().get(0).getIoValue()).getDataComponentType(),
        // is (SweDataComponentType.Boolean));
        assertThat(absProcess.getInputs().get(1).getIoValue().getDataComponentType(), is(SweDataComponentType.Count));
        // assertThat(
        // ((SweAbstractSimpleType)absProcess.getInputs().get(1).getIoValue()).getDataComponentType(),
        // is (SweDataComponentType.Count));

        assertThat(absProcess.getOutputs().get(0).getIoValue().getDataComponentType(),
                is(SweDataComponentType.Quantity));
        // assertThat(
        // ((SweAbstractSimpleType)absProcess.getOutputs().get(0).getIoValue()).getDataComponentType(),
        // is (SweDataComponentType.Quantity));
        assertThat(absProcess.getOutputs().get(1).getIoValue().getDataComponentType(),
                is(SweDataComponentType.DataArray));
        assertThat(absProcess.getOutputs().get(2).getIoValue().getDataComponentType(),
                is(SweDataComponentType.DataRecord));
    }

    @Test
    public void should_decode_contact_from_sml() throws OwsExceptionReport {
        String role1 = "role1";
        String role2 = "role2";
        String orgName = "orgName";
        String individualName = "individualName";
        String posName = "posName";
        String contactInstructions = "contactInstructions";
        String hoursOfService = "hoursOfService";
        String adminArea = "adminArea";
        String city = "city";
        String country = "country";
        String email = "email";
        String postalCode = "postalCode";
        String deliveryPoint1 = "deliveryPoint1";
        String deliveryPoint2 = "deliveryPoint2";
        String onlineResource1 = "onlineResource1";
        String onlineResource2 = "onlineResource2";
        String phoneVoice1 = "phoneVoice1";
        String phoneVoice2 = "phoneVoice2";
        String phoneFax1 = "phoneFax1";
        String phoneFax2 = "phoneFax2";
        String affiliation = "affiliation";
        String name = "name";
        String phoneNumber = "phoneNumber";
        String surname = "surname";
        String userID = "userID";
        
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        Contact xbContact = xbSystem.addNewContact();
        ContactList xbContactList = xbContact.addNewContactList();
        
        //responsible party
        ContactList.Member xbMember1 = xbContactList.addNewMember();
        xbMember1.setRole(role1);
        ResponsibleParty xbRespPart = xbMember1.addNewResponsibleParty();
        xbRespPart.setOrganizationName(orgName);
        xbRespPart.setIndividualName(individualName);
        xbRespPart.setPositionName(posName);
        ContactInfo xbContactInfo = xbRespPart.addNewContactInfo();
        xbContactInfo.setContactInstructions(contactInstructions);
        xbContactInfo.setHoursOfService(hoursOfService);
        ContactInfo.Address xbAddress = xbContactInfo.addNewAddress();
        xbAddress.setAdministrativeArea(adminArea);
        xbAddress.setCity(city);
        xbAddress.setCountry(country);
        xbAddress.setElectronicMailAddress(email);
        xbAddress.setPostalCode(postalCode);
        xbAddress.addDeliveryPoint(deliveryPoint1);
        xbAddress.addDeliveryPoint(deliveryPoint2);
        xbContactInfo.addNewOnlineResource().setHref(onlineResource1);
        xbContactInfo.addNewOnlineResource().setHref(onlineResource2);
        ContactInfo.Phone phone = xbContactInfo.addNewPhone();
        phone.addVoice(phoneVoice1);
        phone.addVoice(phoneVoice2);
        phone.addFacsimile(phoneFax1);
        phone.addFacsimile(phoneFax2);

        //person
        ContactList.Member xbMember2 = xbContactList.addNewMember();
        xbMember2.setRole(role2);
        Person xbPerson = xbMember2.addNewPerson();
        xbPerson.setAffiliation(affiliation);
        xbPerson.setEmail(email);
        xbPerson.setName(name);
        xbPerson.setPhoneNumber(phoneNumber);
        xbPerson.setSurname(surname);
        xbPerson.setUserID(userID);
        
        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getContact(), notNullValue());
        assertThat(absProcess.getContact().size(), is(1));
        assertThat(absProcess.getContact().get(0), instanceOf(SmlContactList.class));
        SmlContactList smlContactList = (SmlContactList) absProcess.getContact().get(0);
        assertThat(smlContactList.getMembers(), notNullValue());
        assertThat(smlContactList.getMembers().size(), is(2));
        
        assertThat(smlContactList.getMembers().get(0).getRole(), is(role1));
        assertThat(smlContactList.getMembers().get(0), instanceOf(SmlResponsibleParty.class));
        SmlResponsibleParty smlRespParty = (SmlResponsibleParty) smlContactList.getMembers().get(0);
        assertThat(smlRespParty.getAdministrativeArea(), is(adminArea));
        assertThat(smlRespParty.getCity(), is(city));
        assertThat(smlRespParty.getContactInstructions(), is(contactInstructions));
        assertThat(smlRespParty.getCountry(), is(country));
        assertThat(smlRespParty.getDeliveryPoint().size(), is(2));
        assertThat(smlRespParty.getDeliveryPoint().get(0), is(deliveryPoint1));
        assertThat(smlRespParty.getDeliveryPoint().get(1), is(deliveryPoint2));
        assertThat(smlRespParty.getEmail(), is(email));
        assertThat(smlRespParty.getHoursOfService(), is(hoursOfService));
        assertThat(smlRespParty.getIndividualName(), is(individualName));
        assertThat(smlRespParty.getOnlineResources().size(), is(2));
        assertThat(smlRespParty.getOnlineResources().get(0), is(onlineResource1));
        assertThat(smlRespParty.getOnlineResources().get(1), is(onlineResource2));
        assertThat(smlRespParty.getOrganizationName(), is(orgName));
        assertThat(smlRespParty.getPhoneFax().size(), is(2));
        assertThat(smlRespParty.getPhoneFax().get(0), is(phoneFax1));
        assertThat(smlRespParty.getPhoneFax().get(1), is(phoneFax2));
        assertThat(smlRespParty.getPhoneVoice().size(), is(2));
        assertThat(smlRespParty.getPhoneVoice().get(0), is(phoneVoice1));
        assertThat(smlRespParty.getPhoneVoice().get(1), is(phoneVoice2));
        assertThat(smlRespParty.getPositionName(), is(posName));
        assertThat(smlRespParty.getPostalCode(), is(postalCode));
        
        assertThat(smlContactList.getMembers().get(1).getRole(), is(role2));
        assertThat(smlContactList.getMembers().get(1), instanceOf(SmlPerson.class));
        SmlPerson smlPerson = (SmlPerson) smlContactList.getMembers().get(1);
        assertThat(smlPerson.getAffiliation(), is(affiliation));
        assertThat(smlPerson.getEmail(), is(email));
        assertThat(smlPerson.getName(), is(name));
        assertThat(smlPerson.getPhoneNumber(), is(phoneNumber));
        assertThat(smlPerson.getSurname(), is(surname));
        assertThat(smlPerson.getUserID(), is(userID));
    }
    
    private DataArrayType getDataArray() {
        DataArrayType dataArray = DataArrayType.Factory.newInstance();
        dataArray.addNewElementCount().addNewCount().setValue(new BigInteger("1"));
        DataComponentPropertyType addNewElementType = dataArray.addNewElementType();
        addNewElementType.setName("elementType");
        addNewElementType.addNewAbstractDataRecord();
        return dataArray;
    }

    @Test
    public void should_set_gml_id() throws OwsExceptionReport {
        SensorMLDocument xbSmlDoc = getSensorMLDoc();
        SystemType xbSystem =
                (SystemType) xbSmlDoc.getSensorML().addNewMember().addNewProcess()
                        .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        xbSystem.setId(TEST_ID_1);
        AbstractProcess absProcess = decodeAbstractProcess(xbSmlDoc);
        assertThat(absProcess.getGmlId(), is(TEST_ID_1));
    }    
}
