/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.svalbard.inspire.base2;

import java.util.List;

import org.n52.sos.iso.gmd.PT_FreeText;
import org.n52.sos.w3c.Nillable;
import org.n52.svalbard.inspire.ad.AddressRepresentation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class Contact {

    /**
     * 0..1
     */
    private Nillable<AddressRepresentation> address = Nillable.missing();

    /**
     * 0..1
     */
    private Nillable<PT_FreeText> contactInstructions = Nillable.missing();

    /**
     * 0..1
     */
    private Nillable<String> electronicMailAddress = Nillable.missing();

    /**
     * 0..*
     */
    private Nillable<List<String>> telephoneFacsimile = Nillable.missing();

    /**
     * 0..*
     */
    private Nillable<List<String>> telephoneVoice = Nillable.missing();

    /**
     * 0..1
     */
    private Nillable<String> website;

    /**
     * @return the address
     */
    public Nillable<AddressRepresentation> getAddress() {
        return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public Contact setAddress(AddressRepresentation address) {
        return setAddress(Nillable.of(address));
    }
    
    public Contact setAddress(Nillable<AddressRepresentation> address) {
        this.address = Preconditions.checkNotNull(address);
        return this;
    }

    /**
     * @return the contactInstructions
     */
    public Nillable<PT_FreeText> getContactInstructions() {
        return contactInstructions;
    }

    /**
     * @param contactInstructions
     *            the contactInstructions to set
     */
    public Contact setContactInstructions(PT_FreeText contactInstructions) {
        return setContactInstructions(Nillable.of(contactInstructions));
    }
    
    /**
     * @param contactInstructions
     *            the contactInstructions to set
     */
    public Contact setContactInstructions(Nillable<PT_FreeText> contactInstructions) {
        this.contactInstructions = Preconditions.checkNotNull(contactInstructions);
        return this;
    }

    /**
     * @return the electronicMailAddress
     */
    public Nillable<String> getElectronicMailAddress() {
        return electronicMailAddress;
    }

    /**
     * @param electronicMailAddress
     *            the electronicMailAddress to set
     */
    public Contact setElectronicMailAddress(String electronicMailAddress) {
        return setElectronicMailAddress(Nillable.of(electronicMailAddress));
    }
    
    /**
     * @param electronicMailAddress
     *            the electronicMailAddress to set
     */
    public Contact setElectronicMailAddress(Nillable<String> electronicMailAddress) {
        this.electronicMailAddress = Preconditions.checkNotNull(electronicMailAddress);
        return this;
    }

    /**
     * @return the telephoneFacsimile
     */
    public Nillable<List<String>> getTelephoneFacsimile() {
        return telephoneFacsimile;
    }

    /**
     * @param telephoneFacsimile
     *            the telephoneFacsimile to set
     */
    public Contact setTelephoneFacsimile(List<String> telephoneFacsimile) {
        return setTelephoneFacsimile(Nillable.of(telephoneFacsimile));
    }
    
    /**
     * @param telephoneFacsimile
     *            the telephoneFacsimile to set
     */
    public Contact setTelephoneFacsimile(Nillable<List<String>> telephoneFacsimile) {
        this.telephoneFacsimile = Preconditions.checkNotNull(telephoneFacsimile);
        return this;
    }

    /**
     * @param telephoneFacsimile
     *            the telephoneFacsimile to add
     */
    public Contact addTelephoneFacsimile(String telephoneFacsimile) {
        if (this.telephoneFacsimile.isAbsent() || this.telephoneFacsimile.isNil()) {
            this.telephoneFacsimile = Nillable.of((List<String>)Lists.<String>newArrayList());
        }
        this.telephoneFacsimile.get().add(Preconditions.checkNotNull(telephoneFacsimile));
        return this;
    }

    /**
     * @return the telephoneVoice
     */
    public Nillable<List<String>> getTelephoneVoice() {
        return telephoneVoice;
    }

    /**
     * @param telephoneVoice
     *            the telephoneVoice to set
     */
    public Contact setTelephoneVoice(List<String> telephoneVoice) {
        return setTelephoneVoice(Nillable.of(telephoneVoice));
    }
    
    /**
     * @param telephoneVoice
     *            the telephoneVoice to set
     */
    public Contact setTelephoneVoice(Nillable<List<String>> telephoneVoice) {
        this.telephoneVoice = Preconditions.checkNotNull(telephoneVoice);
        return this;
    }

    /**
     * @param telephoneVoice
     *            the telephoneVoice to add
     */
    public Contact addTelephoneVoice(String telephoneVoice) {
        if (this.telephoneVoice.isAbsent() || this.telephoneVoice.isNil()) {
            this.telephoneVoice = Nillable.of((List<String>)Lists.<String>newArrayList());
        }
        this.telephoneVoice.get().add(Preconditions.checkNotNull(telephoneVoice));
        return this;
    }

    /**
     * @return the website
     */
    public Nillable<String> getWebsite() {
        return website;
    }

    /**
     * @param website
     *            the website to set
     */
    public Contact setWebsite(String website) {
        return setWebsite(Nillable.of(website));
    }
    
    /**
     * @param website
     *            the website to set
     */
    public Contact setWebsite(Nillable<String> website) {
        this.website = Preconditions.checkNotNull(website);
        return this;
    }

}