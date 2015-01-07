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
package org.n52.sos.inspire.aqd;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.util.Nillable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class Contact {

    private Nillable<Address> address = Nillable.missing();
    private Nillable<String> contactInstructions = Nillable.missing();
    private Nillable<String> electronicMailAddress = Nillable.missing();
    private Nillable<String> hoursOfService = Nillable.missing();
    private final List<Nillable<String>> telephoneFacsimile = new LinkedList<>();
    private final List<Nillable<String>> telephoneVoice = new LinkedList<>();
    private Nillable<String> website = Nillable.missing();

    public Nillable<Address> getAddress() {
        return address;
    }

    public Contact setAddress(Nillable<Address> address) {
        this.address = Preconditions.checkNotNull(address);
        return this;
    }

    public Contact setAddress(Address address) {
        return setAddress(Nillable.of(address));
    }

    public Nillable<String> getContactInstructions() {
        return contactInstructions;
    }

    public Contact setContactInstructions(Nillable<String> contactInstructions) {
        this.contactInstructions
                = Preconditions.checkNotNull(contactInstructions);
        return this;
    }

    public Contact setContactInstructions(String contactInstructions) {
        return setContactInstructions(Nillable.of(contactInstructions));
    }

    public Nillable<String> getElectronicMailAddress() {
        return electronicMailAddress;
    }

    public Contact setElectronicMailAddress(
            Nillable<String> electronicMailAddress) {
        this.electronicMailAddress = Preconditions
                .checkNotNull(electronicMailAddress);
        return this;
    }

    public Contact setElectronicMailAddress(String electronicMailAddress) {
        return setElectronicMailAddress(Nillable.of(electronicMailAddress));
    }

    public Nillable<String> getHoursOfService() {
        return hoursOfService;
    }

    public Contact setHoursOfService(Nillable<String> hoursOfService) {
        this.hoursOfService = Preconditions.checkNotNull(hoursOfService);
        return this;
    }

    public Contact setHoursOfService(String hoursOfService) {
        return setHoursOfService(Nillable.of(hoursOfService));
    }

    public List<Nillable<String>> getTelephoneFacsimile() {
        return Collections.unmodifiableList(telephoneFacsimile);
    }

    public Contact addTelephoneFacsimile(Nillable<String> telephoneFacsimile) {
        this.telephoneFacsimile.add(Preconditions
                .checkNotNull(telephoneFacsimile));
        return this;
    }

    public Contact addTelephoneFacsimile(String telephoneFacsimile) {
        return this.addTelephoneFacsimile(Nillable.of(telephoneFacsimile));
    }

    public List<Nillable<String>> getTelephoneVoice() {
        return Collections.unmodifiableList(telephoneVoice);
    }

    public Contact addTelephoneVoice(Nillable<String> telephoneVoice) {
        this.telephoneVoice.add(Preconditions.checkNotNull(telephoneVoice));
        return this;
    }

    public Contact addTelephoneVoice(String telephoneVoice) {
        return addTelephoneVoice(Nillable.of(telephoneVoice));
    }

    public Nillable<String> getWebsite() {
        return website;
    }

    public Contact setWebsite(Nillable<String> website) {
        this.website = Preconditions.checkNotNull(website);
        return this;
    }

    public Contact setWebsite(String website) {
        return setWebsite(Nillable.of(website));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAddress(), getContactInstructions(),
                                getElectronicMailAddress(), getHoursOfService(),
                                getTelephoneFacsimile(), getTelephoneVoice(),
                                getWebsite());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Contact) {
            Contact that = (Contact) obj;
            return Objects.equal(getAddress(), that.getAddress()) &&
                   Objects.equal(getContactInstructions(), that.getContactInstructions()) &&
                   Objects.equal(getElectronicMailAddress(), that.getElectronicMailAddress()) &&
                   Objects.equal(getHoursOfService(), that.getHoursOfService()) &&
                   Objects.equal(getTelephoneFacsimile(), that.getTelephoneFacsimile()) &&
                   Objects.equal(getTelephoneVoice(), that.getTelephoneVoice()) &&
                   Objects.equal(getWebsite(), getWebsite());
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("address", getAddress())
                .add("contactInstructions", getContactInstructions())
                .add("electronicMailAddress", getElectronicMailAddress())
                .add("hoursOfService", getHoursOfService())
                .add("telephoneFacsimile", getTelephoneFacsimile())
                .add("telephoneVoice", getTelephoneVoice())
                .add("website", getWebsite())
                .toString();
    }
}
