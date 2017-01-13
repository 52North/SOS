/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.iso.gmd;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.iso.gco.AbtractGmd;
import org.n52.sos.util.CollectionHelper;

public class CiContact extends AbtractGmd {

    private List<String> phoneVoice;

    private List<String> phoneFax;

    private List<String> deliveryPoints;

    private String city;

    private String administrativeArea;

    private String postalCode;

    private String country;

    private String email;

    private String onlineResource;

    private String hoursOfService;

    private String contactInstructions;
    
    public boolean isSetPhoneVoice() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(phoneVoice);
    }

    public List<String> getPhoneVoice() {
        return phoneVoice;
    }

    public CiContact setPhoneVoice(final List<String> phoneVoice) {
        if (isSetPhoneVoice()) {
            this.phoneVoice.addAll(phoneVoice);
        } else {
            this.phoneVoice = phoneVoice;
        }
        return this;
    }

    public CiContact addPhoneVoice(final String phoneVoice) {
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

    public CiContact addPhoneFax(final String phoneFax) {
        if (!isSetPhoneFax()) {
            this.phoneFax = new ArrayList<String>();
        }
        this.phoneFax.add(phoneFax);
        return this;
    }

    public CiContact setPhoneFax(final List<String> phoneFax) {
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

    public CiContact setDeliveryPoint(final List<String> deliveryPoints) {
        if (isSetDeliveryPoint()) {
            this.deliveryPoints.addAll(deliveryPoints);
        } else {
            this.deliveryPoints = deliveryPoints;
        }
        return this;
    }

    public CiContact addDeliveryPoint(final String deliveryPoint) {
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

    public CiContact setCity(final String city) {
        this.city = city;
        return this;
    }

    public boolean isSetAdministrativeArea() {
        return administrativeArea != null && !administrativeArea.isEmpty();
    }

    public String getAdministrativeArea() {
        return administrativeArea;
    }

    public CiContact setAdministrativeArea(final String administrativeArea) {
        this.administrativeArea = administrativeArea;
        return this;
    }

    public boolean isSetPostalCode() {
        return postalCode != null && !postalCode.isEmpty();
    }

    public String getPostalCode() {
        return postalCode;
    }

    public CiContact setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public boolean isSetCountry() {
        return country != null && !country.isEmpty();
    }

    public String getCountry() {
        return country;
    }

    public CiContact setCountry(final String country) {
        this.country = country;
        return this;
    }

    public boolean isSetEmail() {
        return email != null && !email.isEmpty();
    }

    public String getEmail() {
        return email;
    }

    public CiContact setEmail(final String email) {
        this.email = email;
        return this;
    }

    public boolean isSetOnlineResource() {
        return onlineResource != null && !onlineResource.isEmpty();
    }

    public String getOnlineResource() {
        return onlineResource;
    }

    public CiContact setOnlineResource(final String onlineResource) {
        this.onlineResource = onlineResource;
        return this;
    }

    public boolean isSetHoursOfService() {
        return hoursOfService != null && !hoursOfService.isEmpty();
    }

    public String getHoursOfService() {
        return hoursOfService;
    }

    public CiContact setHoursOfService(final String hoursOfService) {
        this.hoursOfService = hoursOfService;
        return this;
    }

    public boolean isSetContactInstructions() {
        return contactInstructions != null && !contactInstructions.isEmpty();
    }

    public String getContactInstructions() {
        return contactInstructions;
    }

    public CiContact setContactInstructions(final String contactInstructions) {
        this.contactInstructions = contactInstructions;
        return this;
    }

    public boolean isSetContactInfo() {
        return isSetPhone() || isSetAddress() || isSetOnlineResource() || isSetHoursOfService()
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
