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
package org.n52.sos.aquarius.pojo.location;

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
@JsonPropertyOrder({ "StandardIdentifier", "IsMeasuredAgainstLocalAssumedDatum", "ValidFrom", "Unit", "Elevation",
        "MeasurementDirection", "Comment", "AppliedTime", "AppliedByUser" })
public class ReferencePointPeriod implements Serializable {

    private static final long serialVersionUID = 6033745997190394932L;

    @JsonProperty("StandardIdentifier")
    private String standardIdentifier;

    @JsonProperty("IsMeasuredAgainstLocalAssumedDatum")
    private Boolean isMeasuredAgainstLocalAssumedDatum;

    @JsonProperty("ValidFrom")
    private String validFrom;

    @JsonProperty("Unit")
    private String unit;

    @JsonProperty("Elevation")
    private Integer elevation;

    @JsonProperty("MeasurementDirection")
    private String measurementDirection;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("AppliedTime")
    private String appliedTime;

    @JsonProperty("AppliedByUser")
    private String appliedByUser;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public ReferencePointPeriod() {
    }

    public ReferencePointPeriod(String standardIdentifier, Boolean isMeasuredAgainstLocalAssumedDatum,
            String validFrom, String unit, Integer elevation, String measurementDirection, String comment,
            String appliedTime, String appliedByUser) {
        super();
        this.standardIdentifier = standardIdentifier;
        this.isMeasuredAgainstLocalAssumedDatum = isMeasuredAgainstLocalAssumedDatum;
        this.validFrom = validFrom;
        this.unit = unit;
        this.elevation = elevation;
        this.measurementDirection = measurementDirection;
        this.comment = comment;
        this.appliedTime = appliedTime;
        this.appliedByUser = appliedByUser;
    }

    @JsonProperty("StandardIdentifier")
    public String getStandardIdentifier() {
        return standardIdentifier;
    }

    @JsonProperty("StandardIdentifier")
    public void setStandardIdentifier(String standardIdentifier) {
        this.standardIdentifier = standardIdentifier;
    }

    @JsonProperty("IsMeasuredAgainstLocalAssumedDatum")
    public Boolean getIsMeasuredAgainstLocalAssumedDatum() {
        return isMeasuredAgainstLocalAssumedDatum;
    }

    @JsonProperty("IsMeasuredAgainstLocalAssumedDatum")
    public void setIsMeasuredAgainstLocalAssumedDatum(Boolean isMeasuredAgainstLocalAssumedDatum) {
        this.isMeasuredAgainstLocalAssumedDatum = isMeasuredAgainstLocalAssumedDatum;
    }

    @JsonProperty("ValidFrom")
    public String getValidFrom() {
        return validFrom;
    }

    @JsonProperty("ValidFrom")
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    @JsonProperty("Unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("Unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty("Elevation")
    public Integer getElevation() {
        return elevation;
    }

    @JsonProperty("Elevation")
    public void setElevation(Integer elevation) {
        this.elevation = elevation;
    }

    @JsonProperty("MeasurementDirection")
    public String getMeasurementDirection() {
        return measurementDirection;
    }

    @JsonProperty("MeasurementDirection")
    public void setMeasurementDirection(String measurementDirection) {
        this.measurementDirection = measurementDirection;
    }

    @JsonProperty("Comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("Comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("AppliedTime")
    public String getAppliedTime() {
        return appliedTime;
    }

    @JsonProperty("AppliedTime")
    public void setAppliedTime(String appliedTime) {
        this.appliedTime = appliedTime;
    }

    @JsonProperty("AppliedByUser")
    public String getAppliedByUser() {
        return appliedByUser;
    }

    @JsonProperty("AppliedByUser")
    public void setAppliedByUser(String appliedByUser) {
        this.appliedByUser = appliedByUser;
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
        return new ToStringBuilder(this).append("standardIdentifier", standardIdentifier)
                .append("isMeasuredAgainstLocalAssumedDatum", isMeasuredAgainstLocalAssumedDatum)
                .append("validFrom", validFrom)
                .append("unit", unit)
                .append("elevation", elevation)
                .append("measurementDirection", measurementDirection)
                .append("comment", comment)
                .append("appliedTime", appliedTime)
                .append("appliedByUser", appliedByUser)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(elevation)
                .append(isMeasuredAgainstLocalAssumedDatum)
                .append(unit)
                .append(appliedTime)
                .append(standardIdentifier)
                .append(measurementDirection)
                .append(comment)
                .append(validFrom)
                .append(additionalProperties)
                .append(appliedByUser)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ReferencePointPeriod)) {
            return false;
        }
        ReferencePointPeriod rhs = (ReferencePointPeriod) other;
        return new EqualsBuilder().append(elevation, rhs.elevation)
                .append(isMeasuredAgainstLocalAssumedDatum, rhs.isMeasuredAgainstLocalAssumedDatum)
                .append(unit, rhs.unit)
                .append(appliedTime, rhs.appliedTime)
                .append(standardIdentifier, rhs.standardIdentifier)
                .append(measurementDirection, rhs.measurementDirection)
                .append(comment, rhs.comment)
                .append(validFrom, rhs.validFrom)
                .append(additionalProperties, rhs.additionalProperties)
                .append(appliedByUser, rhs.appliedByUser)
                .isEquals();
    }

}
