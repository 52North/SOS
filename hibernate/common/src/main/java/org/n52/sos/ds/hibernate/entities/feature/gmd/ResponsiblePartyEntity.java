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
package org.n52.sos.ds.hibernate.entities.feature.gmd;

import com.google.common.base.Strings;

/**
 * Hibernate entity for responsibleParty.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class ResponsiblePartyEntity extends AbstractCiEntity {

    private String individualName;
    private String organizationName;
    private String positionName;
    private ContactEntity contactInfo;
    private RoleEntity role;

    /**
     * @return the individualName
     */
    public String getIndividualName() {
        return individualName;
    }

    /**
     * @param individualName
     *            the individualName to set
     */
    public void setIndividualName(String individualName) {
        this.individualName = individualName;
    }
    
    public boolean isSetIndividualName() {
        return !Strings.isNullOrEmpty(getIndividualName());
    }

    /**
     * @return the organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * @param organizationName
     *            the organizationName to set
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    
    public boolean isSetOrganizationName() {
        return !Strings.isNullOrEmpty(getOrganizationName());
    }

    /**
     * @return the positionName
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * @param positionName
     *            the positionName to set
     */
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
    
    public boolean isSetPositionName() {
        return !Strings.isNullOrEmpty(getPositionName());
    }

    /**
     * @return the contactInfo
     */
    public ContactEntity getContactInfo() {
        return contactInfo;
    }

    /**
     * @param contactInfo
     *            the contactInfo to set
     */
    public void setContactInfo(ContactEntity contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public boolean isSetContactInfo() {
        return getContactInfo() != null;
    }

    /**
     * @return the role
     */
    public RoleEntity getCiRole() {
        return role;
    }

    /**
     * @param role
     *            the role to set
     */
    public void setCiRole(RoleEntity role) {
        this.role = role;
    }
}
