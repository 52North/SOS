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
import java.util.HashMap;
import java.util.List;
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
@JsonPropertyOrder({ "TimeSeriesDescriptions", "ResponseVersion", "ResponseTime", "Summary" })
public class TimeSeriesDescriptions implements Serializable {

    private static final long serialVersionUID = 227126252437919710L;

    @JsonProperty("TimeSeriesDescriptions")
    private List<TimeSeriesDescription> timeSeriesDescriptions = new ArrayList<TimeSeriesDescription>();

    @JsonProperty("ResponseVersion")
    private Integer responseVersion;

    @JsonProperty("ResponseTime")
    private String responseTime;

    @JsonProperty("Summary")
    private String summary;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public TimeSeriesDescriptions() {
    }

    public TimeSeriesDescriptions(List<TimeSeriesDescription> timeSeriesDescriptions, Integer responseVersion,
            String responseTime, String summary) {
        super();
        this.timeSeriesDescriptions = timeSeriesDescriptions;
        this.responseVersion = responseVersion;
        this.responseTime = responseTime;
        this.summary = summary;
    }

    @JsonProperty("TimeSeriesDescriptions")
    public List<TimeSeriesDescription> getTimeSeriesDescriptions() {
        return timeSeriesDescriptions;
    }

    @JsonProperty("TimeSeriesDescriptions")
    public void setTimeSeriesDescriptions(List<TimeSeriesDescription> timeSeriesDescriptions) {
        this.timeSeriesDescriptions = timeSeriesDescriptions;
    }

    public boolean hasTimeSeriesDescriptions() {
        return getTimeSeriesDescriptions() != null && !getTimeSeriesDescriptions().isEmpty();
    }

    @JsonProperty("ResponseVersion")
    public Integer getResponseVersion() {
        return responseVersion;
    }

    @JsonProperty("ResponseVersion")
    public void setResponseVersion(Integer responseVersion) {
        this.responseVersion = responseVersion;
    }

    @JsonProperty("ResponseTime")
    public String getResponseTime() {
        return responseTime;
    }

    @JsonProperty("ResponseTime")
    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    @JsonProperty("Summary")
    public String getSummary() {
        return summary;
    }

    @JsonProperty("Summary")
    public void setSummary(String summary) {
        this.summary = summary;
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
        return new ToStringBuilder(this).append("timeSeriesDescriptions", timeSeriesDescriptions)
                .append("responseVersion", responseVersion)
                .append("responseTime", responseTime)
                .append("summary", summary)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(summary)
                .append(responseVersion)
                .append(additionalProperties)
                .append(timeSeriesDescriptions)
                .append(responseTime)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TimeSeriesDescriptions)) {
            return false;
        }
        TimeSeriesDescriptions rhs = (TimeSeriesDescriptions) other;
        return new EqualsBuilder().append(summary, rhs.summary)
                .append(responseVersion, rhs.responseVersion)
                .append(additionalProperties, rhs.additionalProperties)
                .append(timeSeriesDescriptions, rhs.timeSeriesDescriptions)
                .append(responseTime, rhs.responseTime)
                .isEquals();
    }

}
