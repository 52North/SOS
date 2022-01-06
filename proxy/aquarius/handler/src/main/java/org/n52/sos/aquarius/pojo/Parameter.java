/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Identifier", "UnitGroupIdentifier", "UnitIdentifier", "DisplayName", "InterpolationType",
        "RoundingSpec" })
public class Parameter implements Serializable {

    private static final long serialVersionUID = -3969478786406455540L;

    @JsonProperty("Identifier")
    private String identifier;

    @JsonProperty("UnitGroupIdentifier")
    private String unitGroupIdentifier;

    @JsonProperty("UnitIdentifier")
    private String unitIdentifier;

    @JsonProperty("DisplayName")
    private String displayName;

    @JsonProperty("InterpolationType")
    private String interpolationType;

    @JsonProperty("RoundingSpec")
    private String roundingSpec;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Parameter() {
    }

    public Parameter(String identifier, String unitGroupIdentifier, String unitIdentifier, String displayName,
            String interpolationType, String roundingSpec) {
        super();
        this.identifier = identifier;
        this.unitGroupIdentifier = unitGroupIdentifier;
        this.unitIdentifier = unitIdentifier;
        this.displayName = displayName;
        this.interpolationType = interpolationType;
        this.roundingSpec = roundingSpec;
    }

    @JsonProperty("Identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("Identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty("UnitGroupIdentifier")
    public String getUnitGroupIdentifier() {
        return unitGroupIdentifier;
    }

    @JsonProperty("UnitGroupIdentifier")
    public void setUnitGroupIdentifier(String unitGroupIdentifier) {
        this.unitGroupIdentifier = unitGroupIdentifier;
    }

    @JsonProperty("UnitIdentifier")
    public String getUnitIdentifier() {
        return unitIdentifier;
    }

    @JsonProperty("UnitIdentifier")
    public void setUnitIdentifier(String unitIdentifier) {
        this.unitIdentifier = unitIdentifier;
    }

    @JsonProperty("DisplayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("DisplayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public boolean hasDisplayName() {
        return getDisplayName() != null && !getDisplayName().isEmpty();
    }

    @JsonProperty("InterpolationType")
    public String getInterpolationType() {
        return interpolationType;
    }

    @JsonProperty("InterpolationType")
    public void setInterpolationType(String interpolationType) {
        this.interpolationType = interpolationType;
    }

    @JsonProperty("RoundingSpec")
    public String getRoundingSpec() {
        return roundingSpec;
    }

    @JsonProperty("RoundingSpec")
    public void setRoundingSpec(String roundingSpec) {
        this.roundingSpec = roundingSpec;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("identifier", identifier)
                .append("unitGroupIdentifier", unitGroupIdentifier)
                .append("unitIdentifier", unitIdentifier)
                .append("displayName", displayName)
                .append("interpolationType", interpolationType)
                .append("roundingSpec", roundingSpec)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(identifier)
                .append(displayName)
                .append(roundingSpec)
                .append(additionalProperties)
                .append(unitGroupIdentifier)
                .append(unitIdentifier)
                .append(interpolationType)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Parameter)) {
            return false;
        }
        Parameter rhs = (Parameter) other;
        return new EqualsBuilder().append(identifier, rhs.identifier)
                .append(displayName, rhs.displayName)
                .append(roundingSpec, rhs.roundingSpec)
                .append(additionalProperties, rhs.additionalProperties)
                .append(unitGroupIdentifier, rhs.unitGroupIdentifier)
                .append(unitIdentifier, rhs.unitIdentifier)
                .append(interpolationType, rhs.interpolationType)
                .isEquals();
    }

}
