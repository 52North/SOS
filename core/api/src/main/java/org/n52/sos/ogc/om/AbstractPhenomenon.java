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
package org.n52.sos.ogc.om;

import java.io.Serializable;

import org.n52.sos.util.StringHelper;

import com.google.common.base.Objects;

/**
 * Abstract class for phenomena
 * 
 * @since 4.0.0
 */
public class AbstractPhenomenon implements Comparable<AbstractPhenomenon>, Serializable {
    /**
     * serial number
     */
    private static final long serialVersionUID = 8730485367220080360L;

    /** phenomenon identifier */
    private String identifier;

    /** phenomenon description */
    private String description;

    /**
     * constructor
     * 
     * @param identifier
     *            Phenomenon identifier
     */
    public AbstractPhenomenon(final String identifier) {
        super();
        this.identifier = identifier;
    }

    /**
     * constructor
     * 
     * @param identifier
     *            Phenomenon identifier
     * @param description
     *            Phenomenon description
     */
    public AbstractPhenomenon(final String identifier, final String description) {
        super();
        this.identifier = identifier;
        this.description = description;
    }

    /**
     * Get phenomenon identifier
     * 
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set phenomenon identifier
     * 
     * @param identifier
     *            the identifier to set
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * Check whether identifier is set
     * 
     * @return <code>true</code>, if identifier is set
     */
    public boolean isSetIdentifier() {
        return StringHelper.isNotEmpty(getIdentifier());
    }

    /**
     * Get phenomenon description
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set phenomenon description
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Check whether description is set
     * 
     * @return <code>true</code>, if description is set
     */
    public boolean isSetDescription() {
        return StringHelper.isNotEmpty(getDescription());
    }

    @Override
    public boolean equals(final Object paramObject) {
        if (paramObject instanceof AbstractPhenomenon) {
            final AbstractPhenomenon phen = (AbstractPhenomenon) paramObject;
            return getIdentifier().equals(phen.getIdentifier());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifier());
    }

    @Override
    public int compareTo(final AbstractPhenomenon o) {
        return getIdentifier().compareTo(o.getIdentifier());
    }

    @Override
    public String toString() {
        return String.format("AbstractPhenomenon [identifier=%s, description=%s]", getIdentifier(), getDescription());
    }
}
