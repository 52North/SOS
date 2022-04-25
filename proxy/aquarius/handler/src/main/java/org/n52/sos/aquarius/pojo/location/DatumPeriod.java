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
package org.n52.sos.aquarius.pojo.location;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Standard", "TimeRange", "UnitIdentifier", "OffsetToStandard", "Uncertainty",
        "MeasurementDirection", "Comments", "AppliedTimeUtc", "User" })
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class DatumPeriod implements Serializable {

    private static final long serialVersionUID = 3821922609155300279L;

    @JsonProperty("Standard")
    private String standard;

    @JsonProperty("TimeRange")
    private TimeRange timeRange;

    @JsonProperty("UnitIdentifier")
    private String unitIdentifier;

    @JsonProperty("OffsetToStandard")
    private Integer offsetToStandard;

    @JsonProperty("Uncertainty")
    private Integer uncertainty;

    @JsonProperty("MeasurementDirection")
    private String measurementDirection;

    @JsonProperty("Comments")
    private String comments;

    @JsonProperty("AppliedTimeUtc")
    private String appliedTimeUtc;

    @JsonProperty("User")
    private String user;

    /**
     * No args constructor for use in serialization
     *
     */
    public DatumPeriod() {
    }

    public DatumPeriod(String standard, TimeRange timeRange, String unitIdentifier, Integer offsetToStandard,
            Integer uncertainty, String measurementDirection, String comments, String appliedTimeUtc, String user) {
        super();
        this.standard = standard;
        this.timeRange = timeRange;
        this.unitIdentifier = unitIdentifier;
        this.offsetToStandard = offsetToStandard;
        this.uncertainty = uncertainty;
        this.measurementDirection = measurementDirection;
        this.comments = comments;
        this.appliedTimeUtc = appliedTimeUtc;
        this.user = user;
    }

    @JsonProperty("Standard")
    public String getStandard() {
        return standard;
    }

    @JsonProperty("Standard")
    public void setStandard(String standard) {
        this.standard = standard;
    }

    @JsonProperty("TimeRange")
    public TimeRange getTimeRange() {
        return timeRange;
    }

    @JsonProperty("TimeRange")
    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    @JsonProperty("UnitIdentifier")
    public String getUnitIdentifier() {
        return unitIdentifier;
    }

    @JsonProperty("UnitIdentifier")
    public void setUnitIdentifier(String unitIdentifier) {
        this.unitIdentifier = unitIdentifier;
    }

    @JsonProperty("OffsetToStandard")
    public Integer getOffsetToStandard() {
        return offsetToStandard;
    }

    @JsonProperty("OffsetToStandard")
    public void setOffsetToStandard(Integer offsetToStandard) {
        this.offsetToStandard = offsetToStandard;
    }

    @JsonProperty("Uncertainty")
    public Integer getUncertainty() {
        return uncertainty;
    }

    @JsonProperty("Uncertainty")
    public void setUncertainty(Integer uncertainty) {
        this.uncertainty = uncertainty;
    }

    @JsonProperty("MeasurementDirection")
    public String getMeasurementDirection() {
        return measurementDirection;
    }

    @JsonProperty("MeasurementDirection")
    public void setMeasurementDirection(String measurementDirection) {
        this.measurementDirection = measurementDirection;
    }

    @JsonProperty("Comments")
    public String getComments() {
        return comments;
    }

    @JsonProperty("Comments")
    public void setComments(String comments) {
        this.comments = comments;
    }

    @JsonProperty("AppliedTimeUtc")
    public String getAppliedTimeUtc() {
        return appliedTimeUtc;
    }

    @JsonProperty("AppliedTimeUtc")
    public void setAppliedTimeUtc(String appliedTimeUtc) {
        this.appliedTimeUtc = appliedTimeUtc;
    }

    @JsonProperty("User")
    public String getUser() {
        return user;
    }

    @JsonProperty("User")
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("standard", standard)
                .append("timeRange", timeRange)
                .append("unitIdentifier", unitIdentifier)
                .append("offsetToStandard", offsetToStandard)
                .append("uncertainty", uncertainty)
                .append("measurementDirection", measurementDirection)
                .append("comments", comments)
                .append("appliedTimeUtc", appliedTimeUtc)
                .append("user", user)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(standard)
                .append(offsetToStandard)
                .append(comments)
                .append(appliedTimeUtc)
                .append(measurementDirection)
                .append(uncertainty)
                .append(user)
                .append(timeRange)
                .append(unitIdentifier)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DatumPeriod)) {
            return false;
        }
        DatumPeriod rhs = (DatumPeriod) other;
        return new EqualsBuilder().append(standard, rhs.standard)
                .append(offsetToStandard, rhs.offsetToStandard)
                .append(comments, rhs.comments)
                .append(appliedTimeUtc, rhs.appliedTimeUtc)
                .append(measurementDirection, rhs.measurementDirection)
                .append(uncertainty, rhs.uncertainty)
                .append(user, rhs.user)
                .append(timeRange, rhs.timeRange)
                .append(unitIdentifier, rhs.unitIdentifier)
                .isEquals();
    }

}
