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

import javax.annotation.Generated;
import javax.validation.Valid;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "TokenExpired", "NextToken", "TimeSeriesUniqueIds", "ResponseVersion", "ResponseTime",
        "Summary" })
@Generated("jsonschema2pojo")
public class TimeSeriesUniqueIds implements Serializable {

    private static final long serialVersionUID = -645683936806589033L;

    @JsonProperty("TokenExpired")
    private Boolean tokenExpired;

    @JsonProperty("NextToken")
    private String nextToken;

    @JsonProperty("TimeSeriesUniqueIds")
    @Valid
    private List<TimeSeriesUniqueId> timeSeriesUniqueIds = new ArrayList<TimeSeriesUniqueId>();

    @JsonProperty("ResponseVersion")
    private Integer responseVersion;

    @JsonProperty("ResponseTime")
    private String responseTime;

    @JsonProperty("Summary")
    private String summary;

    /**
     * No args constructor for use in serialization
     *
     */
    public TimeSeriesUniqueIds() {
    }

    public TimeSeriesUniqueIds(Boolean tokenExpired, String nextToken,
            Collection<TimeSeriesUniqueId> timeSeriesUniqueIds, Integer responseVersion, String responseTime,
            String summary) {
        super();
        this.tokenExpired = tokenExpired;
        this.nextToken = nextToken;
        setTimeSeriesUniqueIds(timeSeriesUniqueIds);
        this.responseVersion = responseVersion;
        this.responseTime = responseTime;
        this.summary = summary;
    }

    @JsonProperty("TokenExpired")
    public Boolean getTokenExpired() {
        return tokenExpired;
    }

    @JsonProperty("TokenExpired")
    public void setTokenExpired(Boolean tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    public TimeSeriesUniqueIds withTokenExpired(Boolean tokenExpired) {
        this.tokenExpired = tokenExpired;
        return this;
    }

    @JsonProperty("NextToken")
    public String getNextToken() {
        return nextToken;
    }

    @JsonProperty("NextToken")
    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    public TimeSeriesUniqueIds withNextToken(String nextToken) {
        this.nextToken = nextToken;
        return this;
    }

    @JsonProperty("TimeSeriesUniqueIds")
    public List<TimeSeriesUniqueId> getTimeSeriesUniqueIds() {
        return Collections.unmodifiableList(timeSeriesUniqueIds);
    }

    @JsonProperty("TimeSeriesUniqueIds")
    public void setTimeSeriesUniqueIds(Collection<TimeSeriesUniqueId> timeSeriesUniqueIds) {
        this.timeSeriesUniqueIds.clear();
        if (timeSeriesUniqueIds != null) {
            this.timeSeriesUniqueIds.addAll(timeSeriesUniqueIds);
        }
    }

    public TimeSeriesUniqueIds withTimeSeriesUniqueIds(Collection<TimeSeriesUniqueId> timeSeriesUniqueIds) {
        this.timeSeriesUniqueIds.clear();
        if (timeSeriesUniqueIds != null) {
            this.timeSeriesUniqueIds.addAll(timeSeriesUniqueIds);
        }
        return this;
    }

    public boolean hasTimeSeriesUniqueIds() {
        return getTimeSeriesUniqueIds() != null && !getTimeSeriesUniqueIds().isEmpty();
    }

    @JsonProperty("ResponseVersion")
    public Integer getResponseVersion() {
        return responseVersion;
    }

    @JsonProperty("ResponseVersion")
    public void setResponseVersion(Integer responseVersion) {
        this.responseVersion = responseVersion;
    }

    public TimeSeriesUniqueIds withResponseVersion(Integer responseVersion) {
        this.responseVersion = responseVersion;
        return this;
    }

    @JsonProperty("ResponseTime")
    public String getResponseTime() {
        return responseTime;
    }

    @JsonProperty("ResponseTime")
    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public TimeSeriesUniqueIds withResponseTime(String responseTime) {
        this.responseTime = responseTime;
        return this;
    }

    @JsonProperty("Summary")
    public String getSummary() {
        return summary;
    }

    @JsonProperty("Summary")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public TimeSeriesUniqueIds withSummary(String summary) {
        this.summary = summary;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("tokenExpired", tokenExpired).append("nextToken", nextToken)
                .append("timeSeriesUniqueIds", timeSeriesUniqueIds).append("responseVersion", responseVersion)
                .append("responseTime", responseTime).append("summary", summary).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(summary).append(responseVersion).append(timeSeriesUniqueIds)
                .append(responseTime).append(nextToken).append(tokenExpired).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TimeSeriesUniqueIds)) {
            return false;
        }
        TimeSeriesUniqueIds rhs = (TimeSeriesUniqueIds) other;
        return new EqualsBuilder().append(summary, rhs.summary).append(responseVersion, rhs.responseVersion)
                .append(nextToken, rhs.nextToken).append(tokenExpired, rhs.tokenExpired)
                .append(timeSeriesUniqueIds, rhs.timeSeriesUniqueIds).append(responseTime, rhs.responseTime)
                .isEquals();
    }

}
