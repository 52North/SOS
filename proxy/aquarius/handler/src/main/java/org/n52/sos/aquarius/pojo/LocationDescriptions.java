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
@JsonPropertyOrder({ "LocationDescriptions", "NextToken", "ResponseVersion", "ResponseTime", "Summary" })
public class LocationDescriptions implements Serializable {

    private static final long serialVersionUID = 5954261901691478255L;

    @JsonProperty("LocationDescriptions")
    private List<LocationDescription> locationDescriptions = new ArrayList<LocationDescription>();

    @JsonProperty("NextToken")
    private String nextToken;

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
    public LocationDescriptions() {
    }

    public LocationDescriptions(Collection<LocationDescription> locationDescriptions, String nextToken,
            Integer responseVersion, String responseTime, String summary) {
        super();
        setLocationDescriptions(locationDescriptions);
        this.nextToken = nextToken;
        this.responseVersion = responseVersion;
        this.responseTime = responseTime;
        this.summary = summary;
    }

    @JsonProperty("LocationDescriptions")
    public List<LocationDescription> getLocationDescriptions() {
        return Collections.unmodifiableList(locationDescriptions);
    }

    @JsonProperty("LocationDescriptions")
    public void setLocationDescriptions(Collection<LocationDescription> locationDescriptions) {
        this.locationDescriptions.clear();
        if (locationDescriptions != null) {
            this.locationDescriptions.addAll(locationDescriptions);
        }
    }

    public boolean hasLocationDesctiptions() {
        return getLocationDescriptions() != null && !getLocationDescriptions().isEmpty();
    }

    @JsonProperty("NextToken")
    public String getNextToken() {
        return nextToken;
    }

    @JsonProperty("NextToken")
    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("locationDescriptions", locationDescriptions)
                .append("nextToken", nextToken)
                .append("responseVersion", responseVersion)
                .append("responseTime", responseTime)
                .append("summary", summary)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(summary)
                .append(responseVersion)
                .append(nextToken)
                .append(responseTime)
                .append(locationDescriptions)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LocationDescriptions)) {
            return false;
        }
        LocationDescriptions rhs = (LocationDescriptions) other;
        return new EqualsBuilder().append(summary, rhs.summary)
                .append(responseVersion, rhs.responseVersion)
                .append(nextToken, rhs.nextToken)
                .append(responseTime, rhs.responseTime)
                .append(locationDescriptions, rhs.locationDescriptions)
                .isEquals();
    }

}
