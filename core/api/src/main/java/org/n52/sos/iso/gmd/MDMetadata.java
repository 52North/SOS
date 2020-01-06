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
package org.n52.sos.iso.gmd;

import java.util.Set;

import org.joda.time.DateTime;
import org.n52.sos.w3c.xlink.AttributeSimpleAttrs;
import org.n52.sos.w3c.xlink.SimpleAttrs;

import com.google.common.collect.Sets;

public class MDMetadata extends AbstractObject implements AttributeSimpleAttrs {

    private SimpleAttrs simpleAttrs;
    
    private Set<CiResponsibleParty> contact = Sets.newHashSet();

    private DateTime dateStamp;

    private Set<AbstractMDIdentification> identificationInfo = Sets.newHashSet();

    public MDMetadata(SimpleAttrs simpleAttrs) {
        this.simpleAttrs = simpleAttrs;
    }
    
    public MDMetadata(CiResponsibleParty contact, DateTime dateStamp, AbstractMDIdentification identificationInfo) {
        this(Sets.newHashSet(contact), dateStamp, Sets.newHashSet(identificationInfo));
    }

    public MDMetadata(Set<CiResponsibleParty> contact, DateTime dateStamp, Set<AbstractMDIdentification> identificationInfo) {
        super();
        this.contact = contact;
        this.dateStamp = dateStamp;
        this.identificationInfo = identificationInfo;
    }


    @Override
    public void setSimpleAttrs(SimpleAttrs simpleAttrs) {
       this.simpleAttrs = simpleAttrs;
    }

    @Override
    public SimpleAttrs getSimpleAttrs() {
        return simpleAttrs;
    }

    @Override
    public boolean isSetSimpleAttrs() {
        return getSimpleAttrs() != null && getSimpleAttrs().isSetHref();
    }

    /**
     * @return the contact
     */
    public Set<CiResponsibleParty> getContact() {
        return contact;
    }

    public MDMetadata addContact(CiResponsibleParty contact) {
        this.contact.add(contact);
        return this;
    }

    public MDMetadata addContacts(Set<CiResponsibleParty> contacts) {
        this.contact.addAll(contacts);
        return this;
    }

    /**
     * @return the dateStamp
     */
    public DateTime getDateStamp() {
        return dateStamp;
    }

    /**
     * @return the identificationInfo
     */
    public Set<AbstractMDIdentification> getIdentificationInfo() {
        return identificationInfo;
    }

    public MDMetadata addIdentificationInfo(AbstractMDIdentification identificationInfo) {
        this.identificationInfo.add(identificationInfo);
        return this;
    }

    public MDMetadata addIdentificationInfos(Set<AbstractMDIdentification> identificationInfos) {
        this.identificationInfo.addAll(identificationInfos);
        return this;
    }

}
