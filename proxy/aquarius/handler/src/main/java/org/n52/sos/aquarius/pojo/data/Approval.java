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
@JsonPropertyOrder({ "ApprovalLevel", "DateAppliedUtc", "User", "LevelDescription", "Comment", "StartTime",
        "EndTime" })
public class Approval implements Serializable {

    private static final long serialVersionUID = -1993954583489699037L;

    @JsonProperty("ApprovalLevel")
    private Integer approvalLevel;

    @JsonProperty("DateAppliedUtc")
    private String dateAppliedUtc;

    @JsonProperty("User")
    private String user;

    @JsonProperty("LevelDescription")
    private String levelDescription;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("StartTime")
    private String startTime;

    @JsonProperty("EndTime")
    private String endTime;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Approval() {
    }

    public Approval(Integer approvalLevel, String dateAppliedUtc, String user, String levelDescription, String comment,
            String startTime, String endTime) {
        super();
        this.approvalLevel = approvalLevel;
        this.dateAppliedUtc = dateAppliedUtc;
        this.user = user;
        this.levelDescription = levelDescription;
        this.comment = comment;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @JsonProperty("ApprovalLevel")
    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    @JsonProperty("ApprovalLevel")
    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    @JsonProperty("DateAppliedUtc")
    public String getDateAppliedUtc() {
        return dateAppliedUtc;
    }

    @JsonProperty("DateAppliedUtc")
    public void setDateAppliedUtc(String dateAppliedUtc) {
        this.dateAppliedUtc = dateAppliedUtc;
    }

    @JsonProperty("User")
    public String getUser() {
        return user;
    }

    @JsonProperty("User")
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty("LevelDescription")
    public String getLevelDescription() {
        return levelDescription;
    }

    @JsonProperty("LevelDescription")
    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }

    @JsonProperty("Comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("Comment")
    public void setComment(String comment) {
        this.comment = comment;
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
        return new ToStringBuilder(this).append("approvalLevel", approvalLevel)
                .append("dateAppliedUtc", dateAppliedUtc)
                .append("user", user)
                .append("levelDescription", levelDescription)
                .append("comment", comment)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(dateAppliedUtc)
                .append(comment)
                .append(startTime)
                .append(endTime)
                .append(additionalProperties)
                .append(approvalLevel)
                .append(user)
                .append(levelDescription)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Approval)) {
            return false;
        }
        Approval rhs = (Approval) other;
        return new EqualsBuilder().append(dateAppliedUtc, rhs.dateAppliedUtc)
                .append(comment, rhs.comment)
                .append(startTime, rhs.startTime)
                .append(endTime, rhs.endTime)
                .append(additionalProperties, rhs.additionalProperties)
                .append(approvalLevel, rhs.approvalLevel)
                .append(user, rhs.user)
                .append(levelDescription, rhs.levelDescription)
                .isEquals();
    }

}
