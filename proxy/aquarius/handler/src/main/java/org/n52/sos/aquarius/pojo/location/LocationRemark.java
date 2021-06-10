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
@JsonPropertyOrder({ "CreateTime", "FromTime", "ToTime", "TypeName", "Description", "Remark" })
public class LocationRemark implements Serializable {

    private static final long serialVersionUID = 7214455102030236769L;

    @JsonProperty("CreateTime")
    private String createTime;

    @JsonProperty("FromTime")
    private String fromTime;

    @JsonProperty("ToTime")
    private String toTime;

    @JsonProperty("TypeName")
    private String typeName;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Remark")
    private String remark;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public LocationRemark() {
    }

    public LocationRemark(String createTime, String fromTime, String toTime, String typeName, String description,
            String remark) {
        super();
        this.createTime = createTime;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.typeName = typeName;
        this.description = description;
        this.remark = remark;
    }

    @JsonProperty("CreateTime")
    public String getCreateTime() {
        return createTime;
    }

    @JsonProperty("CreateTime")
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @JsonProperty("FromTime")
    public String getFromTime() {
        return fromTime;
    }

    @JsonProperty("FromTime")
    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    @JsonProperty("ToTime")
    public String getToTime() {
        return toTime;
    }

    @JsonProperty("ToTime")
    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    @JsonProperty("TypeName")
    public String getTypeName() {
        return typeName;
    }

    @JsonProperty("TypeName")
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("Remark")
    public String getRemark() {
        return remark;
    }

    @JsonProperty("Remark")
    public void setRemark(String remark) {
        this.remark = remark;
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
        return new ToStringBuilder(this).append("createTime", createTime)
                .append("fromTime", fromTime)
                .append("toTime", toTime)
                .append("typeName", typeName)
                .append("description", description)
                .append("remark", remark)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(createTime)
                .append(fromTime)
                .append(typeName)
                .append(description)
                .append(remark)
                .append(additionalProperties)
                .append(toTime)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LocationRemark)) {
            return false;
        }
        LocationRemark rhs = (LocationRemark) other;
        return new EqualsBuilder().append(createTime, rhs.createTime)
                .append(fromTime, rhs.fromTime)
                .append(typeName, rhs.typeName)
                .append(description, rhs.description)
                .append(remark, rhs.remark)
                .append(additionalProperties, rhs.additionalProperties)
                .append(toTime, rhs.toTime)
                .isEquals();
    }

}
