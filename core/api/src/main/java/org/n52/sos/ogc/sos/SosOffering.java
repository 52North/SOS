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
package org.n52.sos.ogc.sos;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * class represents an offering in the SOS database
 * 
 * @since 4.0.0
 */
public class SosOffering implements Comparable<SosOffering> {

    /** identifier of this offering */
    private String identifier;

    /** name of this offering */
    private String name;

    /**
     * flag to identify offering as offering from a parent procedure, default =
     * false.
     */
    private boolean parentOffering = false;

    /**
     * constructor
     * 
     * @param offeringIdentifier
     *            offering identifier
     * @param offeringName
     *            offering name
     */
    public SosOffering(String offeringIdentifier, String offeringName) {
        this.identifier = offeringIdentifier;
        this.name = offeringName;
    }

    public SosOffering(String procedureIdentifier) {
        this.identifier = procedureIdentifier + "/observations";
        this.name = "Offering for sensor " + procedureIdentifier;
    }

    /**
     * Get offering identifier
     * 
     * @return Returns the identifier.
     */
    public String getOfferingIdentifier() {
        return identifier;
    }

    /**
     * Set offering identifier
     * 
     * @param offeringIdentifier
     *            The identifier to set.
     */
    public void setOfferingIdentifier(String offeringIdentifier) {
        this.identifier = offeringIdentifier;
    }

    /**
     * Get offering name
     * 
     * @return Returns the name.
     */
    public String getOfferingName() {
        return name;
    }

    /**
     * Set offering name
     * 
     * @param offeringName
     *            The name to set.
     */
    public void setOfferingName(String offeringName) {
        this.name = offeringName;
    }

    public boolean isSetOfferingIdentifier() {
        return identifier != null && !identifier.isEmpty();
    }

    public boolean isSetOfferingName() {
        return name != null && !name.isEmpty();
    }

    /**
     * Set if offering is from parent procedure or not
     * 
     * @param parentOfferingFlag
     *            Offering is from parent procedure or not
     */
    public void setParentOfferingFlag(boolean parentOfferingFlag) {
        this.parentOffering = parentOfferingFlag;
    }

    /**
     * 
     * @return offering is from parent procedure or not
     */
    public boolean isParentOffering() {
        return parentOffering;
    }

    @Override
    public int compareTo(SosOffering o) {
        return checkNotNull(o) == this ? 0
                : getOfferingIdentifier() == o.getOfferingIdentifier() ? 0
                    : getOfferingIdentifier() == null ? -1
                       : o.getOfferingIdentifier() == null ? 1
                          : getOfferingIdentifier().compareTo(o.getOfferingIdentifier());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("identifier", getOfferingIdentifier())
                .add("name", getOfferingName())
                .add("parentOfferingFlag", isParentOffering())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SosOffering) {
            SosOffering other = (SosOffering) o;
            return Objects.equal(getOfferingIdentifier(),
                                 other.getOfferingIdentifier()) &&
                   Objects.equal(getOfferingName(),
                                 other.getOfferingName()) &&
                   Objects.equal(isParentOffering(),
                                 other.isParentOffering());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getOfferingIdentifier(),
                                getOfferingName(),
                                isParentOffering());
    }

    /**
     * Creates a set of {@literal SosOffering}s from a map containing identifiers
     * as keys and names as values.
     *
     * @param map the map (may be {@literal null})
     *
     * @return the set (never {@literal null})
     */
    public static Set<SosOffering> fromMap(Map<String, String> map) {
        if (map == null) {
            return Collections.emptySet();
        }
        final Set<SosOffering> set = Sets.newHashSetWithExpectedSize(map.size());
        for (Entry<String, String> e : map.entrySet()) {
            set.add(new SosOffering(e.getKey(), e.getValue()));
        }
        return set;
    }
}
