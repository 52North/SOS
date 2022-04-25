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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Name", "Description", "ReferenceCode", "Severity", "Type", "DisplayColor", "Periods" })
public class Threshold implements Serializable {

    private static final long serialVersionUID = 5838874075847471175L;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("ReferenceCode")
    private String referenceCode;

    @JsonProperty("Severity")
    private Integer severity;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("DisplayColor")
    private String displayColor;

    @JsonProperty("Periods")
    private List<Period> periods = new ArrayList<Period>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Threshold() {
    }

    public Threshold(String name, String description, String referenceCode, Integer severity, String type,
            String displayColor, Collection<Period> periods) {
        super();
        this.name = name;
        this.description = description;
        this.referenceCode = referenceCode;
        this.severity = severity;
        this.type = type;
        this.displayColor = displayColor;
        setPeriods(periods);
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("ReferenceCode")
    public String getReferenceCode() {
        return referenceCode;
    }

    @JsonProperty("ReferenceCode")
    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    @JsonProperty("Severity")
    public Integer getSeverity() {
        return severity;
    }

    @JsonProperty("Severity")
    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    @JsonProperty("Type")
    public String getType() {
        return type;
    }

    @JsonProperty("Type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("DisplayColor")
    public String getDisplayColor() {
        return displayColor;
    }

    @JsonProperty("DisplayColor")
    public void setDisplayColor(String displayColor) {
        this.displayColor = displayColor;
    }

    @JsonProperty("Periods")
    public List<Period> getPeriods() {
        return Collections.unmodifiableList(periods);
    }

    @JsonProperty("Periods")
    public void setPeriods(Collection<Period> periods) {
        this.periods.clear();
        if (periods != null) {
            this.periods.addAll(periods);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name)
                .append("description", description)
                .append("referenceCode", referenceCode)
                .append("severity", severity)
                .append("type", type)
                .append("displayColor", displayColor)
                .append("periods", periods)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(severity)
                .append(name)
                .append(description)
                .append(periods)
                .append(displayColor)
                .append(referenceCode)
                .append(type)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Threshold)) {
            return false;
        }
        Threshold rhs = (Threshold) other;
        return new EqualsBuilder().append(severity, rhs.severity)
                .append(name, rhs.name)
                .append(description, rhs.description)
                .append(periods, rhs.periods)
                .append(displayColor, rhs.displayColor)
                .append(referenceCode, rhs.referenceCode)
                .append(type, rhs.type)
                .isEquals();
    }

}
