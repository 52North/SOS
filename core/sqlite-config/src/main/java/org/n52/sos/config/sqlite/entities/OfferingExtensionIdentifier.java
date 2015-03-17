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
package org.n52.sos.config.sqlite.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 */
@Embeddable
public class OfferingExtensionIdentifier implements Serializable {
    private static final long serialVersionUID = 8330805590108988308L;
    public static final String OFFERING = "offering";
    public static final String IDENTIFIER = "identifier";
    @Column(name = OFFERING)
    private String offering;
    @Column(name = IDENTIFIER)
    private String identifier;

    public OfferingExtensionIdentifier(String offering, String identifier) {
        this.offering = offering;
        this.identifier = identifier;
    }

    public OfferingExtensionIdentifier() {
    }

    public String getOffering() {
        return offering;
    }

    public void setOffering(String offering) {
        this.offering = offering;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.offering != null ? this.offering.hashCode() : 0);
        hash = 89 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OfferingExtensionIdentifier other = (OfferingExtensionIdentifier) obj;
        if ((this.offering == null) ? (other.offering != null) : !this.offering.equals(other.offering)) {
            return false;
        }
        if ((this.identifier == null) ? (other.identifier != null) : !this.identifier.equals(other.identifier)) {
            return false;
        }
        return true;
    }
}
