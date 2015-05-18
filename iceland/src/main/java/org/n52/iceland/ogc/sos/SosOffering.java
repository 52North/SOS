/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.sos;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.iceland.ogc.gml.AbstractFeature;
import org.n52.iceland.ogc.gml.CodeType;
import org.n52.iceland.ogc.swe.simpleType.SweAbstractSimpleType;

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
     * Get offering name
     * 
     * @return Returns the name.
     */
    public String getOfferingName() {
        return getFirstName().getValue();
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
