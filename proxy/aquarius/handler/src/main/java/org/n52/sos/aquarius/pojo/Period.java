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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "StartTime", "EndTime", "AppliedTime", "Comments", "ReferenceValue", "SecondaryReferenceValue",
        "SuppressData" })
public class Period implements Serializable {

    private static final long serialVersionUID = 882257534342345048L;

    @JsonProperty("StartTime")
    private String startTime;

    @JsonProperty("EndTime")
    private String endTime;

    @JsonProperty("AppliedTime")
    private String appliedTime;

    @JsonProperty("Comments")
    private String comments;

    @JsonProperty("ReferenceValue")
    private Integer referenceValue;

    @JsonProperty("SecondaryReferenceValue")
    private Integer secondaryReferenceValue;

    @JsonProperty("SuppressData")
    private Boolean suppressData;

    /**
     * No args constructor for use in serialization
     *
     */
    public Period() {
    }

    public Period(String startTime, String endTime, String appliedTime, String comments, Integer referenceValue,
            Integer secondaryReferenceValue, Boolean suppressData) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.appliedTime = appliedTime;
        this.comments = comments;
        this.referenceValue = referenceValue;
        this.secondaryReferenceValue = secondaryReferenceValue;
        this.suppressData = suppressData;
    }

    @JsonProperty("StartTime")
    public String getStartTime() {
        return startTime;
    }

    @JsonProperty("StartTime")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("EndTime")
    public String getEndTime() {
        return endTime;
    }

    @JsonProperty("EndTime")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("AppliedTime")
    public String getAppliedTime() {
        return appliedTime;
    }

    @JsonProperty("AppliedTime")
    public void setAppliedTime(String appliedTime) {
        this.appliedTime = appliedTime;
    }

    @JsonProperty("Comments")
    public String getComments() {
        return comments;
    }

    @JsonProperty("Comments")
    public void setComments(String comments) {
        this.comments = comments;
    }

    @JsonProperty("ReferenceValue")
    public Integer getReferenceValue() {
        return referenceValue;
    }

    @JsonProperty("ReferenceValue")
    public void setReferenceValue(Integer referenceValue) {
        this.referenceValue = referenceValue;
    }

    @JsonProperty("SecondaryReferenceValue")
    public Integer getSecondaryReferenceValue() {
        return secondaryReferenceValue;
    }

    @JsonProperty("SecondaryReferenceValue")
    public void setSecondaryReferenceValue(Integer secondaryReferenceValue) {
        this.secondaryReferenceValue = secondaryReferenceValue;
    }

    @JsonProperty("SuppressData")
    public Boolean getSuppressData() {
        return suppressData;
    }

    @JsonProperty("SuppressData")
    public void setSuppressData(Boolean suppressData) {
        this.suppressData = suppressData;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("startTime", startTime)
                .append("endTime", endTime)
                .append("appliedTime", appliedTime)
                .append("comments", comments)
                .append("referenceValue", referenceValue)
                .append("secondaryReferenceValue", secondaryReferenceValue)
                .append("suppressData", suppressData)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(appliedTime)
                .append(comments)
                .append(startTime)
                .append(endTime)
                .append(secondaryReferenceValue)
                .append(suppressData)
                .append(referenceValue)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Period)) {
            return false;
        }
        Period rhs = (Period) other;
        return new EqualsBuilder().append(appliedTime, rhs.appliedTime)
                .append(comments, rhs.comments)
                .append(startTime, rhs.startTime)
                .append(endTime, rhs.endTime)
                .append(secondaryReferenceValue, rhs.secondaryReferenceValue)
                .append(suppressData, rhs.suppressData)
                .append(referenceValue, rhs.referenceValue)
                .isEquals();
    }

}
