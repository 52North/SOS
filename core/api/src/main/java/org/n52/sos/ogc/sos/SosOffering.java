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

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * class represents an offering in the SOS database
 * 
 * @since 4.0.0
 */
public class SosOffering extends AbstractFeature implements Comparable<SosOffering> {

    private static final long serialVersionUID = -7800205161914910464L;

    /**
     * flag to identify offering as offering from a parent procedure, default =
     * false.
     */
    private boolean parentOffering = false;

    private static final String OFFERING_NAME_PREFIX = "Offering for sensor ";

    /**
     * constructor
     * 
     * @param identifier
     *            offering identifier
     * @param name
     *            offering name
     */
    public SosOffering(final String identifier, final String name) {
        this.setIdentifier(identifier);
        if (Strings.isNullOrEmpty(name)) {
            setName(new CodeType(OFFERING_NAME_PREFIX + identifier));
        } else {
            this.setName(new CodeType(name));
        }
    }
    
    /**
     * constructor
     * 
     * @param identifier
     *            offering identifier
     * @param name
     *            offering name
     */
    public SosOffering(final String identifier, boolean generateName) {
        this.setIdentifier(identifier);
        if (generateName) {
            setName(new CodeType(OFFERING_NAME_PREFIX + identifier));
        }
    }

    public SosOffering(final String identifier, final CodeType name) {
        this.setIdentifier(identifier);
        if (!name.isSetValue()) {
            name.setValue(OFFERING_NAME_PREFIX + identifier);
        }
        this.setName(name);
    }

    /**
     * constructor with procedure identifier
     * 
     * @param procedureIdentifier
     *            Procedure identifier
     */
    public SosOffering(String procedureIdentifier) {
        setIdentifier(procedureIdentifier + "/observations");
        setName(new CodeType(OFFERING_NAME_PREFIX + procedureIdentifier));
    }

    /**
     * Get offering identifier
     * 
     * @return Returns the identifier.
     */
    @Deprecated
    public String getOfferingIdentifier() {
        return getIdentifier();
    }

    /**
     * Set offering identifier
     * 
     * @param offeringIdentifier
     *            The identifier to set.
     */
    @Deprecated
    public void setOfferingIdentifier(String offeringIdentifier) {
        setIdentifier(offeringIdentifier);
    }

    /**
     * Get offering name
     * 
     * @return Returns the name.
     */
    public String getOfferingName() {
        return getFirstName().getValue();
    }

    /**
     * Set offering name
     * 
     * @param offeringName
     *            The name to set.
     */
    @Deprecated
    public void setOfferingName(String offeringName) {
        setName(new CodeType(offeringName));
    }

    @Deprecated
    public boolean isSetOfferingIdentifier() {
        return isSetIdentifier();
    }

    @Deprecated
    public boolean isSetOfferingName() {
        return isSetName();
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
                : getIdentifier() == o.getIdentifier() ? 0 
                        : getIdentifier() == null ? -1
                                : o.getIdentifier() == null ? 1 
                                        : getIdentifier().compareTo(o.getIdentifier());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("identifier", getIdentifier())
                .add("name", getName())
                .add("description", getDescription())
                .add("parentOfferingFlag", isParentOffering()).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SosOffering) {
            SosOffering other = (SosOffering) o;
            return Objects.equal(getIdentifier(), other.getIdentifier()) 
                    && Objects.equal(getName(), other.getName())
                    && Objects.equal(isParentOffering(), other.isParentOffering());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifier(), getName(), isParentOffering());
    }

    /**
     * Creates a set of {@literal SosOffering}s from a map containing
     * identifiers as keys and names as values.
     * 
     * @param map
     *            the map (may be {@literal null})
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

    /**
     * Creates a set of {@literal SosOffering}s from a map containing
     * identifiers as keys and names as values.
     * 
     * @param map
     *            the map (may be {@literal null})
     * 
     * @return the set (never {@literal null})
     */
    public static Set<SosOffering> fromSet(Set<SweAbstractSimpleType<?>> set) {
        if (set == null) {
            return Collections.emptySet();
        }
        final Set<SosOffering> offeringSet = Sets.newHashSetWithExpectedSize(set.size());
        for (SweAbstractSimpleType<?> type : set) {
            SosOffering sosOffering = new SosOffering(type.getValue().toString(), type.getName());
            if (type.isSetDescription()) {
                sosOffering.setDescription(type.getDescription());
            }
            offeringSet.add(sosOffering);
        }
        return offeringSet;
    }
    
    /**
     * Creates a set of {@literal SosOffering}s from a map containing
     * identifiers as keys and names as values.
     * 
     * @param map
     *            the map (may be {@literal null})
     * 
     * @return the set (never {@literal null})
     */
    public static SosOffering from(SweAbstractSimpleType<?> type) {
        if (type == null) {
            return null;
        }
        SosOffering sosOffering = new SosOffering(type.getValue().toString(), type.getName());
        if (type.isSetDescription()) {
            sosOffering.setDescription(type.getDescription());
        }
        return sosOffering;
    }

}
