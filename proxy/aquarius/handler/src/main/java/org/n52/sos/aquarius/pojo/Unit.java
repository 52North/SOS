/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
@JsonPropertyOrder({ "UniqueId", "Identifier", "GroupIdentifier", "Symbol", "DisplayName", "BaseMultiplier",
        "BaseOffset" })
public class Unit implements Serializable {

    private static final long serialVersionUID = -5014534214360856507L;

    @JsonProperty("UniqueId")
    private String uniqueId;

    @JsonProperty("Identifier")
    private String identifier;

    @JsonProperty("GroupIdentifier")
    private String groupIdentifier;

    @JsonProperty("Symbol")
    private String symbol;

    @JsonProperty("DisplayName")
    private String displayName;

    @JsonProperty("BaseMultiplier")
    private String baseMultiplier;

    @JsonProperty("BaseOffset")
    private String baseOffset;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Unit() {
    }

    public Unit(String uniqueId, String identifier, String groupIdentifier, String symbol, String displayName,
            String baseMultiplier, String baseOffset) {
        super();
        this.uniqueId = uniqueId;
        this.identifier = identifier;
        this.groupIdentifier = groupIdentifier;
        this.symbol = symbol;
        this.displayName = displayName;
        this.baseMultiplier = baseMultiplier;
        this.baseOffset = baseOffset;
    }

    @JsonProperty("UniqueId")
    public String getUniqueId() {
        return uniqueId;
    }

    @JsonProperty("UniqueId")
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @JsonProperty("Identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("Identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty("GroupIdentifier")
    public String getGroupIdentifier() {
        return groupIdentifier;
    }

    @JsonProperty("GroupIdentifier")
    public void setGroupIdentifier(String groupIdentifier) {
        this.groupIdentifier = groupIdentifier;
    }

    @JsonProperty("Symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("Symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("DisplayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("DisplayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("BaseMultiplier")
    public String getBaseMultiplier() {
        return baseMultiplier;
    }

    @JsonProperty("BaseMultiplier")
    public void setBaseMultiplier(String baseMultiplier) {
        this.baseMultiplier = baseMultiplier;
    }

    @JsonProperty("BaseOffset")
    public String getBaseOffset() {
        return baseOffset;
    }

    @JsonProperty("BaseOffset")
    public void setBaseOffset(String baseOffset) {
        this.baseOffset = baseOffset;
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
        return new ToStringBuilder(this).append("uniqueId", uniqueId)
                .append("identifier", identifier)
                .append("groupIdentifier", groupIdentifier)
                .append("symbol", symbol)
                .append("displayName", displayName)
                .append("baseMultiplier", baseMultiplier)
                .append("baseOffset", baseOffset)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(baseOffset)
                .append(identifier)
                .append(symbol)
                .append(displayName)
                .append(additionalProperties)
                .append(groupIdentifier)
                .append(uniqueId)
                .append(baseMultiplier)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Unit)) {
            return false;
        }
        Unit rhs = (Unit) other;
        return new EqualsBuilder().append(baseOffset, rhs.baseOffset)
                .append(identifier, rhs.identifier)
                .append(symbol, rhs.symbol)
                .append(displayName, rhs.displayName)
                .append(additionalProperties, rhs.additionalProperties)
                .append(groupIdentifier, rhs.groupIdentifier)
                .append(uniqueId, rhs.uniqueId)
                .append(baseMultiplier, rhs.baseMultiplier)
                .isEquals();
    }

}
