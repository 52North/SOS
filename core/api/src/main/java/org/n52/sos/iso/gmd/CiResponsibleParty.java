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

import org.n52.sos.iso.gco.AbtractGmd;
import org.n52.sos.iso.gco.Role;
import org.n52.sos.w3c.xlink.AttributeSimpleAttrs;
import org.n52.sos.w3c.xlink.SimpleAttrs;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since
 *
 */
public class CiResponsibleParty extends AbtractGmd implements AttributeSimpleAttrs {

    private SimpleAttrs simpleAttrs;

    private String individualName;

    private String organizationName;

    private String positionName;
    
    private CiContact contactInfo;
    
    private Role role;

    public CiResponsibleParty(SimpleAttrs simpleAttrs) {
        this.simpleAttrs = simpleAttrs;
    }

    public CiResponsibleParty(Role role) {
        this.role = role;
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
    
    public boolean isSetIndividualName() {
        return individualName != null && !individualName.isEmpty();
    }

    public String getIndividualName() {
        return individualName;
    }

    public CiResponsibleParty setIndividualName(final String invidualName) {
        individualName = invidualName;
        return this;
    }

    public boolean isSetOrganizationName() {
        return organizationName != null && !organizationName.isEmpty();
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public CiResponsibleParty setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
        return this;
    }

    public boolean isSetPositionName() {
        return positionName != null && !positionName.isEmpty();
    }

    public String getPositionName() {
        return positionName;
    }

    public CiResponsibleParty setPositionName(final String positionName) {
        this.positionName = positionName;
        return this;
    }

    /**
     * @return the contactInfo
     */
    public CiContact getContactInfo() {
        return contactInfo;
    }

    /**
     * @param contactInfo the contactInfo to set
     */
    public void setContactInfo(CiContact contactInfo) {
        this.contactInfo = contactInfo;
    }

    public boolean isSetContactInfo() {
        return getContactInfo() != null;
    }

    /**
     * @return the role
     */
    public Role getRole() {
        return role;
    }
    
}
