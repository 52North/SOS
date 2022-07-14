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

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Identifier", "DisplayName", "Description", "Color" })
@Generated("jsonschema2pojo")
public class Grade implements Serializable {

    private static final long serialVersionUID = 358173135697378454L;
    @JsonProperty("Identifier")
    private String identifier;
    @JsonProperty("DisplayName")
    private String displayName;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Color")
    private String color;

    /**
     * No args constructor for use in serialization
     *
     */
    public Grade() {
    }

    /**
     *
     * @param identifier
     *            The identifier
     * @param color
     *            The color
     * @param displayName
     *            The display name
     * @param description
     *            The description
     */
    public Grade(String identifier, String displayName, String description, String color) {
        super();
        this.identifier = identifier;
        this.displayName = displayName;
        this.description = description;
        this.color = color;
    }

    @JsonProperty("Identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("Identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Grade withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @JsonProperty("DisplayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("DisplayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Grade withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    public Grade withDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("Color")
    public String getColor() {
        return color;
    }

    @JsonProperty("Color")
    public void setColor(String color) {
        this.color = color;
    }

    public Grade withColor(String color) {
        this.color = color;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("identifier", this.identifier).append("displayName", this.displayName)
                .append("description", this.description).append("color", this.color).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.identifier).append(this.description).append(this.color)
                .append(this.displayName).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Grade)) {
            return false;
        }
        Grade rhs = (Grade) other;
        return new EqualsBuilder().append(this.identifier, rhs.identifier).append(this.description, rhs.description)
                .append(this.color, rhs.color).append(this.displayName, rhs.displayName).isEquals();
    }

}
