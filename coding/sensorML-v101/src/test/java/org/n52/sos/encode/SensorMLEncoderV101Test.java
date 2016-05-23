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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList.Component;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo.Address;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo.Phone;
import net.opengis.sensorML.x101.ContactListDocument.ContactList;
import net.opengis.sensorML.x101.ContactListDocument.ContactList.Member;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.PersonDocument.Person;
import net.opengis.sensorML.x101.ResponsiblePartyDocument.ResponsibleParty;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.SimpleDataRecordType;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sos.AbstractBeforeAfterClassSettingsManagerTest;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.SmlPerson;
import org.n52.sos.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;

import com.google.common.collect.Lists;

/**
 * @author Shane StClair
 * 
 * @since 4.0.0
 */
public class SensorMLEncoderV101Test extends AbstractBeforeAfterClassSettingsManagerTest {
	
    private static final String TEST_ID_1 = "test-id-1";

    private static final String TEST_NAME_1 = "test-name-1";

    private static final String TEST_ID_2 = "test-id-2";

    private static final String TEST_NAME_2 = "test-name-2";
    
    private static final String TEST_CHILD_1 = "test-id-child-1";

    @Test
    public void should_set_identifier() throws OwsExceptionReport {
        final SensorML sensorMl = new SensorML();
        final System system = new System();
        sensorMl.addMember(system);
        system.addIdentifier(new SmlIdentifier(TEST_NAME_1, OGCConstants.URN_UNIQUE_IDENTIFIER, TEST_ID_1));
        final SystemType xbSystem = encodeSystem(sensorMl);
        assertThat(xbSystem.getIdentificationArray().length, is(1));
        final IdentifierList xbIdentifierList = xbSystem.getIdentificationArray()[0].getIdentifierList();
        assertThat(xbIdentifierList.sizeOfIdentifierArray(), is(1));
        final Identifier xbIdentifier = xbIdentifierList.getIdentifierArray(0);
        assertThat(xbIdentifier.getName(), is(TEST_NAME_1));
        assertThat(xbIdentifier.getTerm().getDefinition(), is(OGCConstants.URN_UNIQUE_IDENTIFIER));
        assertThat(xbIdentifier.getTerm().getValue(), is(TEST_ID_1));
    }

    private SystemType encodeSystem(final SensorML sensorMl) throws OwsExceptionReport {
        final XmlObject encodedSml = CodingHelper.encodeObjectToXml(SensorMLConstants.NS_SML, sensorMl);
        assertThat(encodedSml, instanceOf(SensorMLDocument.class));
        final net.opengis.sensorML.x101.SensorMLDocument.SensorML xbSml = ((SensorMLDocument) encodedSml).getSensorML();
        assertThat(xbSml.getMemberArray().length, is(1));
        assertThat(xbSml.getMemberArray()[0].getProcess(), instanceOf(SystemType.class));
        return (SystemType) xbSml.getMemberArray()[0].getProcess();
    }

    private SimpleDataRecordType encodeSimpleDataRecord(final SensorML sensorMl, final String capName, final int fields)
            throws OwsExceptionReport {
        return encodeSimpleDataRecord(encodeSystem(sensorMl), capName, fields);
    }

    private SimpleDataRecordType encodeSimpleDataRecord(final SystemType xbSystem, final String capName, final int fields) {
        assertThat(xbSystem.getCapabilitiesArray().length, is(1));
        final Capabilities xbCapabilities = xbSystem.getCapabilitiesArray()[0];
        assertThat(xbCapabilities.getName(), is(capName));
        assertThat(xbCapabilities.getAbstractDataRecord(), notNullValue());
        assertThat(xbCapabilities.getAbstractDataRecord(), instanceOf(SimpleDataRecordType.class));
        final SimpleDataRecordType xbSimpleDataRecord = (SimpleDataRecordType) xbCapabilities.getAbstractDataRecord();
        assertThat(xbSimpleDataRecord.getFieldArray().length, is(fields));
        return xbSimpleDataRecord;
    }

    private void validateField(final AnyScalarPropertyType field, final String name, final String definition, final String value) {
        assertThat(field.getName(), is(name));
        assertThat(field.isSetText(), is(true));
        assertThat(field.getText().getDefinition(), is(definition));
        assertThat(field.getText().getValue(), is(value));
    }

    @Test
    public void should_encode_features_of_interest() throws OwsExceptionReport {
        final SensorML sensorMl = new SensorML();
        final System system = new System();
        sensorMl.addMember(system);
        system.addFeatureOfInterest(TEST_ID_1);
        system.addFeatureOfInterest(TEST_ID_2);
        final SimpleDataRecordType xbSimpleDataRecord =
                encodeSimpleDataRecord(sensorMl, SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST, 2);
        validateField(xbSimpleDataRecord.getFieldArray()[0], SensorMLConstants.FEATURE_OF_INTEREST_FIELD_NAME + 1,
                SensorMLConstants.FEATURE_OF_INTEREST_FIELD_DEFINITION, TEST_ID_1);
        validateField(xbSimpleDataRecord.getFieldArray()[1], SensorMLConstants.FEATURE_OF_INTEREST_FIELD_NAME + 2,
                SensorMLConstants.FEATURE_OF_INTEREST_FIELD_DEFINITION, TEST_ID_2);
    }

    @Test
    public void should_encode_offerings() throws OwsExceptionReport {
        final SensorML sensorMl = new SensorML();
        final System system = new System();
        sensorMl.addMember(system);
        system.addOffering(new SosOffering(TEST_ID_1, TEST_NAME_1));
        system.addOffering(new SosOffering(TEST_ID_2, TEST_NAME_2));
        final SimpleDataRecordType xbSimpleDataRecord =
                encodeSimpleDataRecord(sensorMl, SensorMLConstants.ELEMENT_NAME_OFFERINGS, 2);
        validateField(xbSimpleDataRecord.getFieldArray()[0], TEST_NAME_1, SensorMLConstants.OFFERING_FIELD_DEFINITION,
                TEST_ID_1);
        validateField(xbSimpleDataRecord.getFieldArray()[1], TEST_NAME_2, SensorMLConstants.OFFERING_FIELD_DEFINITION,
                TEST_ID_2);
    }

    @Test
    public void should_encode_parent_procedures() throws OwsExceptionReport {
        final SensorML sensorMl = new SensorML();
        final System system = new System();
        sensorMl.addMember(system);
        system.addParentProcedure(TEST_ID_1);
        system.addParentProcedure(TEST_ID_2);
        final SimpleDataRecordType xbSimpleDataRecord =
                encodeSimpleDataRecord(sensorMl, SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES, 2);
        validateField(xbSimpleDataRecord.getFieldArray()[0], SensorMLConstants.PARENT_PROCEDURE_FIELD_NAME + 1,
                SensorMLConstants.PARENT_PROCEDURE_FIELD_DEFINITION, TEST_ID_1);
        validateField(xbSimpleDataRecord.getFieldArray()[1], SensorMLConstants.PARENT_PROCEDURE_FIELD_NAME + 2,
                SensorMLConstants.PARENT_PROCEDURE_FIELD_DEFINITION, TEST_ID_2);
    }

    @Test
    public void should_encode_child_procedures() throws OwsExceptionReport {
        final SensorML sensorMl = new SensorML();
        final System system = new System();
        sensorMl.addMember(system);
        final System childProcedure = new System();
        childProcedure.setIdentifier(TEST_CHILD_1);
        system.addChildProcedure(childProcedure);
        childProcedure.addFeatureOfInterest(TEST_ID_1);
        final SystemType xbSystemType = encodeSystem(sensorMl);
        assertThat(xbSystemType.getComponents().getComponentList().sizeOfComponentArray(), is(1));
        final Component xbComponent = xbSystemType.getComponents().getComponentList().getComponentArray(0);
        assertThat(xbComponent.getProcess(), instanceOf(SystemType.class));
        final SystemType xbComponentSystem = (SystemType) xbComponent.getProcess();
        final SimpleDataRecordType xbSimpleDataRecord =
                encodeSimpleDataRecord(xbComponentSystem, SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST, 1);
        validateField(xbSimpleDataRecord.getFieldArray(0), SensorMLConstants.FEATURE_OF_INTEREST_FIELD_NAME,
                SensorMLConstants.FEATURE_OF_INTEREST_FIELD_DEFINITION, TEST_ID_1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_aggregate_child_outputs() throws OwsExceptionReport {
        final SweQuantity q1 = new SweQuantity();
        q1.setDefinition("def1");
        q1.setUom("uom1");
        final SmlIo<?> output1 = new SmlIo<SweQuantity>(q1);

        final SweQuantity q2 = new SweQuantity();
        q2.setDefinition("def2");
        q2.setUom("uom2");
        final SmlIo<?> output2 = new SmlIo<SweQuantity>(q2);

        final SweQuantity q3 = new SweQuantity();
        q3.setDefinition("def3");
        q3.setUom("uom3");
        final SmlIo<?> output3 = new SmlIo<SweQuantity>(q3);

        final SensorML sensorMl = new SensorML();
        sensorMl.setIdentifier("sensorMl");
        final System system = new System();
        system.setIdentifier("system");
        sensorMl.addMember(system);
        system.getOutputs().add(output1);

        final SensorML childSml = new SensorML();
        childSml.setIdentifier("childSml");
        final System childSystem = new System();
        childSystem.setIdentifier("childSystem");
        childSml.addMember(childSystem);
        system.addChildProcedure(childSml);
        childSystem.getOutputs().add(output2);

        final SensorML grandchildSml = new SensorML();
        grandchildSml.setIdentifier("grandchildSml");
        final System grandchildSystem = new System();
        grandchildSystem.setIdentifier("grandchildSystem");
        grandchildSml.addMember(grandchildSystem);
        childSystem.addChildProcedure(grandchildSml);
        grandchildSystem.getOutputs().add(output3);

        encodeSystem(sensorMl);

        assertThat(system.getOutputs(), hasItems(output1, output2, output3));
        assertThat(childSystem.getOutputs(), hasItems(output2, output3));
        assertThat(grandchildSystem.getOutputs(), hasItem(output3));
    }
    
    @Test public void
    should_encode_single_contact_person() 
    		throws OwsExceptionReport {
    	final SensorML sensorML = new SensorML();
    	final System system = new System();
    	sensorML.addMember(system);
		final SmlPerson contact = createPerson("");
		system.addContact(contact);
		final SystemType xbSystem = encodeSystem(sensorML);
		
		assertThat(xbSystem.sizeOfContactArray(), is(1));
		assertThat(xbSystem.getContactArray(0).getContactList().getMemberArray(0).isSetPerson(), is(true));
		checkPerson(contact, xbSystem.getContactArray(0).getContactList().getMemberArray(0).getPerson());
    }

	@Test public void
    should_encode_single_contact_responsibleParty()
    		throws OwsExceptionReport {
    	final SensorML sensorML = new SensorML();
    	final System system = new System();
    	sensorML.addMember(system);
    	
    	final SmlResponsibleParty responsibleParty = createResponsibleParty("");
    	
		system.addContact(responsibleParty);
		
		final SystemType xbSystem = encodeSystem(sensorML);
		
		assertThat(xbSystem.sizeOfContactArray(), is(1));
		assertThat(xbSystem.getContactArray(0).getContactList().getMemberArray(0).isSetResponsibleParty(), is(true));
		final ResponsibleParty xbResponsibleParty = xbSystem.getContactArray(0).getContactList().getMemberArray(0).getResponsibleParty();
		
		checkResponsibleParty(responsibleParty, xbResponsibleParty);
    }
	
	@Test public void
	should_merge_and_encode_multiple_contacts()
			throws OwsExceptionReport{
		final SensorML sensorML = new SensorML();
    	final System system = new System();
    	sensorML.addMember(system);
    	final SmlPerson p1 = createPerson("1");
    	final SmlResponsibleParty rp1 = createResponsibleParty("1");
    	system.addContact(p1);
    	system.addContact(rp1);
    	final SensorMLDocument xbSensorML = SensorMLDocument.Factory.newInstance();
    	final SystemType xbSystem = SystemType.Factory.newInstance();
    	final ContactList xbContactList = xbSystem.addNewContact().addNewContactList();
    	final Person xbP1 = xbContactList.addNewMember().addNewPerson();
    	setPersonValues(p1, xbP1);
    	final ResponsibleParty xbRP1 = xbContactList.addNewMember().addNewResponsibleParty();
    	setResponsiblePartyValues(rp1, xbRP1);
    	final XmlObject xbProcess = xbSensorML.addNewSensorML().addNewMember().addNewProcess().set(xbSystem);
    	XmlHelper.substituteElement(xbProcess, xbSystem);
    	xbSensorML.getSensorML().setVersion("1.0.1");
    	sensorML.setSensorDescriptionXmlString(xbSensorML.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
    	final SystemType xbEncodedSystem = encodeSystem(sensorML);
    	
    	assertThat(xbEncodedSystem.sizeOfContactArray(), is(1));
    	assertThat(xbEncodedSystem.getContactArray(0).isSetContactList(), is(true));
    	assertThat(xbEncodedSystem.getContactArray(0).getContactList().sizeOfMemberArray(), is(2));
    	boolean personChecked = false, responsiblePartyChecked = false;
    	for (final Member member : xbEncodedSystem.getContactArray(0).getContactList().getMemberArray()) {
			if (member.isSetPerson()) {
				checkPerson(p1, member.getPerson());
				personChecked = true;
			} else if (member.isSetResponsibleParty()) {
				checkResponsibleParty(rp1, member.getResponsibleParty());
				responsiblePartyChecked = true;
			}
		}
    	if (!personChecked) {
    		fail("sml:Person not found in contact/contactList");
    	}
    	if (!responsiblePartyChecked) {
    		fail("sml:ResponsibleParty not found in contact/ContactList");
    	}
	}
	
	@Test public void
    should_encode_multiple_contacts_in_contactList()
    	throws OwsExceptionReport {
    	final SensorML sensorML = new SensorML();
    	final System system = new System();
    	sensorML.addMember(system);
    	
    	final SmlPerson contact1 = createPerson("1");
		final SmlPerson contact2 = createPerson("2");
		
		system.addContact(contact1);
		system.addContact(contact2);
		final SystemType xbSystem = encodeSystem(sensorML);
		
		assertThat(xbSystem.sizeOfContactArray(), is(1));
		assertThat(xbSystem.getContactArray(0).isSetContactList(), is(true));
		final ContactList xbContactList = xbSystem.getContactArray(0).getContactList();
		assertThat(xbContactList.sizeOfMemberArray(), is(2));
		final Member member = xbContactList.getMemberArray(0);
		final Member member2 = xbContactList.getMemberArray(1);
		assertThat(member.isSetPerson(), is(true));
		assertThat(member2.isSetPerson(), is(true));
		if (member.getPerson().getName().equals(contact1.getName())) {
			checkPerson(contact1, member.getPerson());
			checkPerson(contact2, member2.getPerson());
		} else {
			checkPerson(contact1, member2.getPerson());
			checkPerson(contact2, member.getPerson());
		}
    }
	
	@Test public void
	should_merge_and_encode_two_same_contact_person_only_once()
			throws OwsExceptionReport {
		final SensorML sensorML = new SensorML();
    	final System system = new System();
    	sensorML.addMember(system);
    	final SmlPerson p1 = createPerson("1");
    	system.addContact(p1);
    	final SensorMLDocument xbSensorML = SensorMLDocument.Factory.newInstance();
    	final SystemType xbSystem = SystemType.Factory.newInstance();
    	final Person xbP1 = xbSystem.addNewContact().addNewPerson();
    	setPersonValues(p1, xbP1);
    	final XmlObject xbProcess = xbSensorML.addNewSensorML().addNewMember().addNewProcess().set(xbSystem);
    	XmlHelper.substituteElement(xbProcess, xbSystem);
    	xbSensorML.getSensorML().setVersion("1.0.1");
    	sensorML.setSensorDescriptionXmlString(xbSensorML.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
    	final SystemType xbEncodedSystem = encodeSystem(sensorML);
    	
    	assertThat(xbEncodedSystem.sizeOfContactArray(), is(1));
    	assertThat(xbEncodedSystem.getContactArray(0).isSetPerson(), is(true));
    	checkPerson(p1, xbEncodedSystem.getContactArray(0).getPerson());
	}
	
	@Test public void
	should_merge_and_encode_two_same_contact_responsibleParty_only_once()
			throws OwsExceptionReport {
		final SensorML sensorML = new SensorML();
    	final System system = new System();
    	sensorML.addMember(system);
    	final SmlResponsibleParty p1 = createResponsibleParty("1");
    	system.addContact(p1);
    	final SensorMLDocument xbSensorML = SensorMLDocument.Factory.newInstance();
    	final SystemType xbSystem = SystemType.Factory.newInstance();
    	final ResponsibleParty xbP1 = xbSystem.addNewContact().addNewResponsibleParty();
    	setResponsiblePartyValues(p1, xbP1);
    	final XmlObject xbProcess = xbSensorML.addNewSensorML().addNewMember().addNewProcess().set(xbSystem);
    	XmlHelper.substituteElement(xbProcess, xbSystem);
    	xbSensorML.getSensorML().setVersion("1.0.1");
    	sensorML.setSensorDescriptionXmlString(xbSensorML.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
    	final SystemType xbEncodedSystem = encodeSystem(sensorML);
    	
    	assertThat(xbEncodedSystem.sizeOfContactArray(), is(1));
    	assertThat(xbEncodedSystem.getContactArray(0).isSetResponsibleParty(), is(true));
    	checkResponsibleParty(p1, xbEncodedSystem.getContactArray(0).getResponsibleParty());
	}
	
	private void setResponsiblePartyValues(final SmlResponsibleParty rp1,
			final ResponsibleParty xbRP1) {
		xbRP1.setIndividualName(rp1.getIndividualName());
    	xbRP1.setOrganizationName(rp1.getOrganizationName());
    	xbRP1.setPositionName(rp1.getPositionName());
    	final ContactInfo xbContactInfo = xbRP1.addNewContactInfo();
    	xbContactInfo.setContactInstructions(rp1.getContactInstructions());
    	xbContactInfo.setHoursOfService(rp1.getHoursOfService());
    	xbContactInfo.addNewOnlineResource().setHref(rp1.getOnlineResources().get(0));
    	final Address xbAddress = xbContactInfo.addNewAddress();
    	xbAddress.setAdministrativeArea(rp1.getAdministrativeArea());
    	xbAddress.setCity(rp1.getCity());
    	xbAddress.setCountry(rp1.getCountry());
    	xbAddress.addNewDeliveryPoint().setStringValue(rp1.getDeliveryPoint().get(0));
    	xbAddress.setElectronicMailAddress(rp1.getEmail());
    	xbAddress.setPostalCode(rp1.getPostalCode());
    	final Phone xbPhone = xbContactInfo.addNewPhone();
    	xbPhone.addNewFacsimile().setStringValue(rp1.getPhoneFax().get(0));
    	xbPhone.addNewVoice().setStringValue(rp1.getPhoneVoice().get(0));
	}

	private void setPersonValues(final SmlPerson p1,
			final Person xbP1) {
		xbP1.setAffiliation(p1.getAffiliation());
    	xbP1.setEmail(p1.getEmail());
    	xbP1.setName(p1.getName());
    	xbP1.setPhoneNumber(p1.getPhoneNumber());
    	xbP1.setSurname(p1.getSurname());
    	xbP1.setUserID(p1.getUserID());
	}

    private void checkPerson(final SmlPerson contact,
			final Person xbPerson) {
		assertThat(xbPerson.getAffiliation(), is(contact.getAffiliation()));
		assertThat(xbPerson.getEmail(), is(contact.getEmail()));
		assertThat(xbPerson.getName(), is(contact.getName()));
		assertThat(xbPerson.getPhoneNumber(), is(contact.getPhoneNumber()));
		assertThat(xbPerson.getSurname(), is(contact.getSurname()));
		assertThat(xbPerson.getUserID(), is(contact.getUserID()));
	}
    
    private SmlPerson createPerson(String postfix) {
		if (postfix == null) {
			postfix = "";
		}
		final String surname = "surname" + postfix;
		final String name = "name" + postfix;
		final String userID = "userID" + postfix;
		final String affiliation = "affiliation" + postfix;
		final String phoneNumber = "phoneNumber" + postfix;
		final String email = "email" + postfix;
		return new SmlPerson(surname, name, userID, affiliation, phoneNumber, email);
	}

	private SmlResponsibleParty createResponsibleParty(final String string) {
		final String invidualName = "invidualName";
		final String organizationName = "organizationName";
		final String positionName = "positionName";
		final List<String> phoneVoice = Lists.newArrayList("phoneVoice");
		final List<String> phoneFax = Lists.newArrayList("phoneFax");
		final List<String> deliveryPoint = Lists.newArrayList("deliveryPoint");
		final String city = "city";
		final String administrativeArea = "administrativeArea";
		final String postalCode = "postalCode";
		final String country = "country";
		final String email = "email";
		final List<String> onlineResource = Lists.newArrayList("onlineResource");
		final String hoursOfService = "hoursOfService";
		final String contactInstructions = "contactInstructions";
		return new SmlResponsibleParty(invidualName, organizationName, positionName, phoneVoice, phoneFax, deliveryPoint, city, administrativeArea, postalCode, country, email, onlineResource, hoursOfService, contactInstructions);
	}
	
	private void checkResponsibleParty(final SmlResponsibleParty responsibleParty,
			final ResponsibleParty xbResponsibleParty) {
		assertThat(xbResponsibleParty.getIndividualName(), is(responsibleParty.getIndividualName()));
		assertThat(xbResponsibleParty.getOrganizationName(), is(responsibleParty.getOrganizationName()));
		assertThat(xbResponsibleParty.getPositionName(), is(responsibleParty.getPositionName()));
		final ContactInfo xbContactInfo = xbResponsibleParty.getContactInfo();
		assertThat(xbContactInfo.getContactInstructions(), is(responsibleParty.getContactInstructions()));
		assertThat(xbContactInfo.getHoursOfService(), is(responsibleParty.getHoursOfService()));
		assertThat(xbContactInfo.getOnlineResourceArray(0).getHref(), is(responsibleParty.getOnlineResources().get(0)));
		final Phone xbPhone = xbContactInfo.getPhone();
		assertThat(xbPhone.getVoiceArray(0), is(responsibleParty.getPhoneVoice().get(0)));
		assertThat(xbPhone.getFacsimileArray(0), is(responsibleParty.getPhoneFax().get(0)));
		final Address xbAddress = xbContactInfo.getAddress();
		assertThat(xbAddress.getAdministrativeArea(), is(responsibleParty.getAdministrativeArea()));
		assertThat(xbAddress.getCity(), is(responsibleParty.getCity()));
		assertThat(xbAddress.getCountry(), is(responsibleParty.getCountry()));
		assertThat(xbAddress.getElectronicMailAddress(), is(responsibleParty.getEmail()));
		assertThat(xbAddress.getDeliveryPointArray(0), is(responsibleParty.getDeliveryPoint().get(0)));
		assertThat(xbAddress.getPostalCode(), is(responsibleParty.getPostalCode()));
	}

    @Test
    public void should_set_gml_id() throws OwsExceptionReport {
        final SensorML sensorMl = new SensorML();
        final System system = new System();
        sensorMl.addMember(system);
        system.setGmlId(TEST_ID_1);
        final SystemType xbSystem = encodeSystem(sensorMl);
        assertThat(xbSystem.getId(), is(TEST_ID_1));
    }
}
