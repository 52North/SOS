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
package org.n52.sos.aquarius.pojo.data;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Strings;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Identifier", "DateApplied", "User", "StartTime", "EndTime" })
public class Qualifier extends IntervalCheckerAndApplyer implements Serializable {

    private static final long serialVersionUID = -8429449365231482643L;

    @JsonProperty("Identifier")
    private String identifier;

    @JsonProperty("DateApplied")
    private String dateApplied;

    @JsonProperty("User")
    private String user;

    @JsonProperty("StartTime")
    private String startTime;

    @JsonProperty("EndTime")
    private String endTime;

    @JsonIgnore
    private QualifierKey key;

    @JsonIgnore
    private String code;

    @JsonIgnore
    private String displayName;


    /**
     * No args constructor for use in serialization
     *
     */
    public Qualifier() {
    }

    public Qualifier(String identifier, String dateApplied, String user, String startTime, String endTime) {
        super();
        this.identifier = identifier;
        this.dateApplied = dateApplied;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @JsonProperty("Identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("Identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty("DateApplied")
    public String getDateApplied() {
        return dateApplied;
    }

    @JsonProperty("DateApplied")
    public void setDateApplied(String dateApplied) {
        this.dateApplied = dateApplied;
    }

    @JsonProperty("User")
    public String getUser() {
        return user;
    }

    @JsonProperty("User")
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty("StartTime")
    @Override
    public String getStartTime() {
        return startTime;
    }

    @JsonProperty("StartTime")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("EndTime")
    @Override
    public String getEndTime() {
        return endTime;
    }

    @JsonProperty("EndTime")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @JsonIgnore
    public QualifierKey getKey() {
        return key;
    }

    @JsonIgnore
    public Qualifier setKey(QualifierKey key) {
        this.key = key;
        return this;
    }

    @JsonIgnore
    public String getCode() {
        return Strings.isNullOrEmpty(code) ? getIdentifier() : code;
    }

    @JsonIgnore
    public void setCode(String code) {
        this.code = code;
    }

    @JsonIgnore
    public String getDisplayName() {
        return Strings.isNullOrEmpty(displayName) ? getIdentifier() : displayName;
    }

    @JsonIgnore
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    protected void applyToPoint(Point point) {
        point.addQualifier(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("identifier", identifier).append("dateApplied", dateApplied)
                .append("user", user).append("startTime", startTime).append("endTime", endTime).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(identifier).append(startTime).append(endTime).append(dateApplied)
                .append(user).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Qualifier)) {
            return false;
        }
        Qualifier rhs = (Qualifier) other;
        return new EqualsBuilder().append(identifier, rhs.identifier).append(startTime, rhs.startTime)
                .append(endTime, rhs.endTime).append(dateApplied, rhs.dateApplied).append(user, rhs.user).isEquals();
    }

}
