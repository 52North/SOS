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
package org.n52.sos.ogc.sensorML;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.util.CollectionHelper;

/**
 * Implementation for sml:ResponsibleParty
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SmlResponsibleParty extends SmlContact {

    private String individualName;

    private String organizationName;

    private String positionName;

    private List<String> phoneVoice;

    private List<String> phoneFax;

    private List<String> deliveryPoints;

    private String city;

    private String administrativeArea;

    private String postalCode;

    private String country;

    private String email;

    private List<String> onlineResources;

    private String hoursOfService;

    private String contactInstructions;

    public SmlResponsibleParty() {
    }

    public SmlResponsibleParty(final String individualName, final String organizationName, final String positionName,
            final List<String> phoneVoice, final List<String> phoneFax, final List<String> deliveryPoint,
            final String city, final String administrativeArea, final String postalCode, final String country,
            final String email, final List<String> onlineResource, final String hoursOfService,
            final String contactInstructions) {
        this.individualName = individualName;
        this.organizationName = organizationName;
        this.positionName = positionName;
        this.phoneVoice = phoneVoice;
        this.phoneFax = phoneFax;
        this.deliveryPoints = deliveryPoint;
        this.city = city;
        this.administrativeArea = administrativeArea;
        this.postalCode = postalCode;
        this.country = country;
        this.email = email;
        this.onlineResources = onlineResource;
        this.hoursOfService = hoursOfService;
        this.contactInstructions = contactInstructions;
    }

    public boolean isSetIndividualName() {
        return individualName != null && !individualName.isEmpty();
    }

    public String getIndividualName() {
        return individualName;
    }

    public SmlResponsibleParty setIndividualName(final String invidualName) {
        individualName = invidualName;
        return this;
    }

    public boolean isSetOrganizationName() {
        return organizationName != null && !organizationName.isEmpty();
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public SmlResponsibleParty setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
        return this;
    }

    public boolean isSetPositionName() {
        return positionName != null && !positionName.isEmpty();
    }

    public String getPositionName() {
        return positionName;
    }

    public SmlResponsibleParty setPositionName(final String positionName) {
        this.positionName = positionName;
        return this;
    }

    public boolean isSetPhoneVoice() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(phoneVoice);
    }

    public List<String> getPhoneVoice() {
        return phoneVoice;
    }

    public SmlResponsibleParty setPhoneVoice(final List<String> phoneVoice) {
        if (isSetPhoneVoice()) {
            this.phoneVoice.addAll(phoneVoice);
        } else {
            this.phoneVoice = phoneVoice;
        }
        return this;
    }

    public SmlResponsibleParty addPhoneVoice(final String phoneVoice) {
        if (!isSetPhoneVoice()) {
            this.phoneVoice = new ArrayList<String>();
        }
        this.phoneVoice.add(phoneVoice);
        return this;
    }

    public boolean isSetPhoneFax() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(phoneFax);
    }

    public List<String> getPhoneFax() {
        return phoneFax;
    }

    public SmlResponsibleParty addPhoneFax(final String phoneFax) {
        if (!isSetPhoneFax()) {
            this.phoneFax = new ArrayList<String>();
        }
        this.phoneFax.add(phoneFax);
        return this;
    }

    public SmlResponsibleParty setPhoneFax(final List<String> phoneFax) {
        if (isSetPhoneFax()) {
            this.phoneFax.addAll(phoneFax);
        } else {
            this.phoneFax = phoneFax;
        }
        return this;
    }

    public boolean isSetDeliveryPoint() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(deliveryPoints);
    }

    public List<String> getDeliveryPoint() {
        return deliveryPoints;
    }

    public SmlResponsibleParty setDeliveryPoint(final List<String> deliveryPoints) {
        if (isSetDeliveryPoint()) {
            this.deliveryPoints.addAll(deliveryPoints);
        } else {
            this.deliveryPoints = deliveryPoints;
        }
        return this;
    }

    public SmlResponsibleParty addDeliveryPoint(final String deliveryPoint) {
        if (!isSetDeliveryPoint()) {
            deliveryPoints = new ArrayList<String>();
        }
        deliveryPoints.add(deliveryPoint);
        return this;
    }

    public boolean isSetCity() {
        return city != null && !city.isEmpty();
    }

    public String getCity() {
        return city;
    }

    public SmlResponsibleParty setCity(final String city) {
        this.city = city;
        return this;
    }

    public boolean isSetAdministrativeArea() {
        return administrativeArea != null && !administrativeArea.isEmpty();
    }

    public String getAdministrativeArea() {
        return administrativeArea;
    }

    public SmlResponsibleParty setAdministrativeArea(final String administrativeArea) {
        this.administrativeArea = administrativeArea;
        return this;
    }

    public boolean isSetPostalCode() {
        return postalCode != null && !postalCode.isEmpty();
    }

    public String getPostalCode() {
        return postalCode;
    }

    public SmlResponsibleParty setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public boolean isSetCountry() {
        return country != null && !country.isEmpty();
    }

    public String getCountry() {
        return country;
    }

    public SmlResponsibleParty setCountry(final String country) {
        this.country = country;
        return this;
    }

    public boolean isSetEmail() {
        return email != null && !email.isEmpty();
    }

    public String getEmail() {
        return email;
    }

    public SmlResponsibleParty setEmail(final String email) {
        this.email = email;
        return this;
    }

    public boolean isSetOnlineResources() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(onlineResources);
    }

    public List<String> getOnlineResources() {
        return onlineResources;
    }

    public SmlResponsibleParty setOnlineResource(final List<String> onlineResources) {
        if (isSetOnlineResources()) {
            this.onlineResources.addAll(onlineResources);
        } else {
            this.onlineResources = onlineResources;
        }
        return this;
    }

    public SmlResponsibleParty addOnlineResource(final String onlineResource) {
        if (!isSetOnlineResources()) {
            onlineResources = new ArrayList<String>();
        }
        onlineResources.add(onlineResource);
        return this;
    }

    public boolean isSetHoursOfService() {
        return hoursOfService != null && !hoursOfService.isEmpty();
    }

    public String getHoursOfService() {
        return hoursOfService;
    }

    public SmlResponsibleParty setHoursOfService(final String hoursOfService) {
        this.hoursOfService = hoursOfService;
        return this;
    }

    public boolean isSetContactInstructions() {
        return contactInstructions != null && !contactInstructions.isEmpty();
    }

    public String getContactInstructions() {
        return contactInstructions;
    }

    public SmlResponsibleParty setContactInstructions(final String contactInstructions) {
        this.contactInstructions = contactInstructions;
        return this;
    }

    public boolean isSetContactInfo() {
        return isSetPhone() || isSetAddress() || isSetOnlineResources() || isSetHoursOfService()
                || isSetContactInstructions();
    }

    public boolean isSetAddress() {
        return isSetDeliveryPoint() || isSetCity() || isSetAdministrativeArea() || isSetPostalCode() || isSetCountry()
                || isSetEmail();
    }

    public boolean isSetPhone() {
        return isSetPhoneFax() || isSetPhoneVoice();
    }
}
