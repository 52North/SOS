/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.procedure.enrich;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

import org.n52.iceland.util.LocalizedProducer;
import org.n52.shetland.iso.CodeList;
import org.n52.shetland.ogc.ows.OwsAddress;
import org.n52.shetland.ogc.ows.OwsContact;
import org.n52.shetland.ogc.ows.OwsOnlineResource;
import org.n52.shetland.ogc.ows.OwsPhone;
import org.n52.shetland.ogc.ows.OwsResponsibleParty;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.Role;
import org.n52.shetland.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public class ContactsEnrichment
        extends
        SensorMLEnrichment {
    private final LocalizedProducer<OwsServiceProvider> serviceProvider;

    public ContactsEnrichment(
            LocalizedProducer<OwsServiceProvider> serviceProvider,
            AbstractProcedureCreationContext ctx) {
        super(ctx);
        this.serviceProvider = serviceProvider;
    }

    @Override
    protected void enrich(AbstractSensorML description)
            throws OwsExceptionReport {
        // set contacts --> take from service information
        if (!description.isSetContact()) {
            Optional<SmlResponsibleParty> contact = createContactFromServiceContact();
            if (contact.isPresent()) {
                description.addContact(contact.get());
            }
        }
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable() && procedureSettings().isEnrichWithDiscoveryInformation()
                && procedureSettings().isUseServiceContactAsProcedureContact();
    }

    /**
     * Get SerivceProvider object,
     *
     * @return SerivceProvider object
     */
    @VisibleForTesting
    Optional<OwsServiceProvider> getServiceProvider() {
        return Optional.of(this.serviceProvider.get());
    }

    /**
     * Create SensorML Contact form service contact informations
     *
     * @return SensorML Contact
     */
    private Optional<SmlResponsibleParty> createContactFromServiceContact() {
        if (!getServiceProvider().isPresent()) {
            return Optional.absent();
        }
        SmlResponsibleParty rp = new SmlResponsibleParty();
        OwsServiceProvider sp = getServiceProvider().get();
        OwsResponsibleParty serviceContact = sp.getServiceContact();
        java.util.Optional<OwsContact> contactInfo = serviceContact.getContactInfo();
        java.util.Optional<OwsAddress> address = contactInfo.flatMap(OwsContact::getAddress);
        serviceContact.getIndividualName().ifPresent(rp::setIndividualName);
        serviceContact.getOrganisationName().ifPresent(rp::setOrganizationName);
        serviceContact.getPositionName().ifPresent(rp::setPositionName);
        contactInfo.flatMap(OwsContact::getOnlineResource).flatMap(OwsOnlineResource::getHref).map(URI::toString)
                .map(Collections::singletonList).ifPresent(rp::setOnlineResource);
        address.flatMap(OwsAddress::getAdministrativeArea).ifPresent(rp::setAdministrativeArea);
        address.flatMap(OwsAddress::getCity).ifPresent(rp::setCity);
        address.flatMap(OwsAddress::getCountry).ifPresent(rp::setCountry);
        address.flatMap(OwsAddress::getPostalCode).ifPresent(rp::setPostalCode);
        address.map(OwsAddress::getElectronicMailAddress).map(it -> Iterables.getFirst(it, null))
                .ifPresent(rp::setEmail);
        address.map(OwsAddress::getDeliveryPoint).ifPresent(rp::setDeliveryPoint);
        contactInfo.flatMap(OwsContact::getContactInstructions).ifPresent(rp::setContactInstructions);
        contactInfo.flatMap(OwsContact::getHoursOfService).ifPresent(rp::setHoursOfService);
        contactInfo.flatMap(OwsContact::getPhone).map(OwsPhone::getFacsimile).map(ArrayList::new)
                .ifPresent(rp::setPhoneFax);
        contactInfo.flatMap(OwsContact::getPhone).map(OwsPhone::getVoice).map(ArrayList::new)
                .ifPresent(rp::setPhoneVoice);
        rp.setRole(createRole());
        return Optional.of(rp);
    }

    private Role createRole() {
        return (Role) new Role("Point of Contact").setCodeList(CodeList.CI_ROLE_CODE_URL)
                .setCodeListValue(CodeList.CiRoleCodes.CI_RoleCode_pointOfContact.name());
    }
}
