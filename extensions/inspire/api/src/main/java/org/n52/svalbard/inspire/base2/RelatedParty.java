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

import java.util.Set;

import org.n52.sos.iso.gmd.PT_FreeText;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

public class RelatedParty {

    /**
     * 0..1
     */
    private PT_FreeText individualName;

    /**
     * 0..1
     */
    private PT_FreeText organisationName;
    
    /**
     * 0..1
     */
    private PT_FreeText positionName;
    
    /**
     * 0..1
     */
    private Contact contact;
    
    /**
     * 0..*
     */
    private Set<ReferenceType> role = Sets.newHashSet();

    /**
     * @return the individualName
     */
    public PT_FreeText getIndividualName() {
        return individualName;
    }

    /**
     * @param individualName the individualName to set
     */
    public void setIndividualName(PT_FreeText individualName) {
        this.individualName = individualName;
    }
    
    public boolean isSetIndividualName() {
        return getIndividualName() != null;
    }

    /**
     * @return the organisationName
     */
    public PT_FreeText getOrganisationName() {
        return organisationName;
    }

    /**
     * @param organisationName the organisationName to set
     */
    public void setOrganisationName(PT_FreeText organisationName) {
        this.organisationName = organisationName;
    }
    
    public boolean isSetOrganisationName() {
        return getOrganisationName() != null;
    }

    /**
     * @return the positionName
     */
    public PT_FreeText getPositionName() {
        return positionName;
    }

    /**
     * @param positionName the positionName to set
     */
    public void setPositionName(PT_FreeText positionName) {
        this.positionName = positionName;
    }
    
    public boolean isSetPositionName() {
        return getPositionName() != null;
    }

    /**
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }
    
    public boolean isSetContact() {
        return getContact() != null;
    }

    /**
     * @return the role
     */
    public Set<ReferenceType> getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(Set<ReferenceType> role) {
        this.role = role;
    }
    
    public boolean isSetRole() {
        return CollectionHelper.isNotEmpty(getRole());
    }
    
}
