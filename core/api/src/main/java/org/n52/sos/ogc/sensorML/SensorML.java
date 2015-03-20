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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.sos.SosOffering;

/**
 * SOS internal representation of a sensor description
 * 
 * @since 4.0.0
 */
public class SensorML extends AbstractSensorML {

    private static final long serialVersionUID = -6665511248268256489L;

    private String version;

    private final List<AbstractProcess> members = new LinkedList<AbstractProcess>();

    /**
     * default constructor
     */
    public SensorML() {
    }

    public String getVersion() {
        return version;
    }

    public SensorML setVersion(final String version) {
        this.version = version;
        return this;
    }

    public List<AbstractProcess> getMembers() {
        return members;
    }

    public SensorML setMembers(final List<AbstractProcess> members) {
        for (final AbstractProcess member : members) {
            addMember(member);
        }
        return this;
    }

    public SensorML addMember(final AbstractProcess member) {
        if (isEmpty() && !isSetIdentifier() && member.isSetIdentifier()) {
            setIdentifier(member.getIdentifierCodeWithAuthority());
        }
        if (isEmpty() && !isSetOfferings() && member.isSetOfferings()) {
            for (SosOffering offering : member.getOfferings()) {
                addOffering(offering);
            }
        }
        members.add(member);
        return this;
    }

    /**
     * @return <tt>true</tt>, if everything from the super class is not set
     */
    private boolean isEmpty() {
        //don't check validTime
        return !isSetKeywords() && !isSetIdentifications() && !isSetClassifications() && !isSetCapabilities()
                && !isSetCharacteristics() && !isSetContact() && !isSetDocumentation() && !isSetHistory();
    }

    /**
     * @return <tt>true</tt>, if this instance contains only members and
     *         everything else is not set
     */
    public boolean isWrapper() {
        return isEmpty() && isSetMembers();
    }

    public boolean isSetMembers() {
        return members != null && !members.isEmpty();
    }

    /**
     * @return If member's parent procedures are set if this is a wrapper, if
     *         normal parent procedures are set otherwise
     */
    @Override
    public boolean isSetParentProcedures() {
        if (isWrapper() && !super.isSetParentProcedures()) {
            return members.get(0).isSetParentProcedures();
        }
        return super.isSetParentProcedures();
    }

    /**
     * @return Member's parent procedures if this is a wrapper, normal parent
     *         procedures otherwise
     */
    @Override
    public Set<String> getParentProcedures() {
        if (isWrapper() && !super.isSetParentProcedures()) {
            return members.get(0).getParentProcedures();
        }
        return super.getParentProcedures();
    }

    /**
     * @return If member's offerings are set if this is a wrapper, if normal
     *         offerings are set otherwise
     */
    @Override
    public boolean isSetOfferings() {
        if (isWrapper() && !super.isSetOfferings()) {
            return members.get(0).isSetOfferings();
        }
        return super.isSetOfferings();
    }

    /**
     * @return Member's offerings if this is a wrapper, normal offerings
     *         otherwise
     */
    @Override
    public Set<SosOffering> getOfferings() {
        if (isWrapper() && !isSetOfferings()) {
            return members.get(0).getOfferings();
        }
        return super.getOfferings();
    }
}
