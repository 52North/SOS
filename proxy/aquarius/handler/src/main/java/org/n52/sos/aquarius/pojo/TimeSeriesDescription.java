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
@JsonPropertyOrder({ "Identifier", "UniqueId", "LocationIdentifier", "Parameter", "Unit", "UtcOffset",
        "UtcOffsetIsoDuration", "LastModified", "RawStartTime", "RawEndTime", "CorrectedStartTime", "CorrectedEndTime",
        "TimeSeriesType", "Label", "Comment", "Description", "Publish", "ComputationIdentifier",
        "ComputationPeriodIdentifier", "SubLocationIdentifier", "ExtendedAttributes", "Thresholds" })
public class TimeSeriesDescription implements Serializable {

    private static final long serialVersionUID = 7181648809033131665L;

    @JsonProperty("Identifier")
    private String identifier;

    @JsonProperty("UniqueId")
    private String uniqueId;

    @JsonProperty("LocationIdentifier")
    private String locationIdentifier;

    @JsonProperty("Parameter")
    private String parameter;

    @JsonProperty("Unit")
    private String unit;

    @JsonProperty("UtcOffset")
    private Integer utcOffset;

    @JsonProperty("UtcOffsetIsoDuration")
    private String utcOffsetIsoDuration;

    @JsonProperty("LastModified")
    private String lastModified;

    @JsonProperty("RawStartTime")
    private String rawStartTime;

    @JsonProperty("RawEndTime")
    private String rawEndTime;

    @JsonProperty("CorrectedStartTime")
    private String correctedStartTime;

    @JsonProperty("CorrectedEndTime")
    private String correctedEndTime;

    @JsonProperty("TimeSeriesType")
    private String timeSeriesType;

    @JsonProperty("Label")
    private String label;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Publish")
    private Boolean publish;

    @JsonProperty("ComputationIdentifier")
    private String computationIdentifier;

    @JsonProperty("ComputationPeriodIdentifier")
    private String computationPeriodIdentifier;

    @JsonProperty("SubLocationIdentifier")
    private String subLocationIdentifier;

    @JsonProperty("ExtendedAttributes")
    private List<ExtendedAttribute> extendedAttributes = new ArrayList<ExtendedAttribute>();

    @JsonProperty("Thresholds")
    private List<Threshold> thresholds = new ArrayList<Threshold>();

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public TimeSeriesDescription() {
    }

    public TimeSeriesDescription(String identifier, String uniqueId, String locationIdentifier, String parameter,
            String unit, Integer utcOffset, String utcOffsetIsoDuration, String lastModified, String rawStartTime,
            String rawEndTime, String correctedStartTime, String correctedEndTime, String timeSeriesType, String label,
            String comment, String description, Boolean publish, String computationIdentifier,
            String computationPeriodIdentifier, String subLocationIdentifier,
            List<ExtendedAttribute> extendedAttributes, List<Threshold> thresholds) {
        super();
        this.identifier = identifier;
        this.uniqueId = uniqueId;
        this.locationIdentifier = locationIdentifier;
        this.parameter = parameter;
        this.unit = unit;
        this.utcOffset = utcOffset;
        this.utcOffsetIsoDuration = utcOffsetIsoDuration;
        this.lastModified = lastModified;
        this.rawStartTime = rawStartTime;
        this.rawEndTime = rawEndTime;
        this.correctedStartTime = correctedStartTime;
        this.correctedEndTime = correctedEndTime;
        this.timeSeriesType = timeSeriesType;
        this.label = label;
        this.comment = comment;
        this.description = description;
        this.publish = publish;
        this.computationIdentifier = computationIdentifier;
        this.computationPeriodIdentifier = computationPeriodIdentifier;
        this.subLocationIdentifier = subLocationIdentifier;
        this.extendedAttributes = extendedAttributes;
        this.thresholds = thresholds;
    }

    @JsonProperty("Identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("Identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty("UniqueId")
    public String getUniqueId() {
        return uniqueId;
    }

    @JsonProperty("UniqueId")
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @JsonProperty("LocationIdentifier")
    public String getLocationIdentifier() {
        return locationIdentifier;
    }

    @JsonProperty("LocationIdentifier")
    public void setLocationIdentifier(String locationIdentifier) {
        this.locationIdentifier = locationIdentifier;
    }

    @JsonProperty("Parameter")
    public String getParameter() {
        return parameter;
    }

    @JsonProperty("Parameter")
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @JsonProperty("Unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("Unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty("UtcOffset")
    public Integer getUtcOffset() {
        return utcOffset;
    }

    @JsonProperty("UtcOffset")
    public void setUtcOffset(Integer utcOffset) {
        this.utcOffset = utcOffset;
    }

    @JsonProperty("UtcOffsetIsoDuration")
    public String getUtcOffsetIsoDuration() {
        return utcOffsetIsoDuration;
    }

    @JsonProperty("UtcOffsetIsoDuration")
    public void setUtcOffsetIsoDuration(String utcOffsetIsoDuration) {
        this.utcOffsetIsoDuration = utcOffsetIsoDuration;
    }

    @JsonProperty("LastModified")
    public String getLastModified() {
        return lastModified;
    }

    @JsonProperty("LastModified")
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    @JsonProperty("RawStartTime")
    public String getRawStartTime() {
        return rawStartTime;
    }

    @JsonProperty("RawStartTime")
    public void setRawStartTime(String rawStartTime) {
        this.rawStartTime = rawStartTime;
    }

    @JsonProperty("RawEndTime")
    public String getRawEndTime() {
        return rawEndTime;
    }

    @JsonProperty("RawEndTime")
    public void setRawEndTime(String rawEndTime) {
        this.rawEndTime = rawEndTime;
    }

    @JsonProperty("CorrectedStartTime")
    public String getCorrectedStartTime() {
        return correctedStartTime;
    }

    @JsonProperty("CorrectedStartTime")
    public void setCorrectedStartTime(String correctedStartTime) {
        this.correctedStartTime = correctedStartTime;
    }

    @JsonProperty("CorrectedEndTime")
    public String getCorrectedEndTime() {
        return correctedEndTime;
    }

    @JsonProperty("CorrectedEndTime")
    public void setCorrectedEndTime(String correctedEndTime) {
        this.correctedEndTime = correctedEndTime;
    }

    @JsonProperty("TimeSeriesType")
    public String getTimeSeriesType() {
        return timeSeriesType;
    }

    @JsonProperty("TimeSeriesType")
    public void setTimeSeriesType(String timeSeriesType) {
        this.timeSeriesType = timeSeriesType;
    }

    @JsonProperty("Label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("Label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("Comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("Comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("Publish")
    public Boolean getPublish() {
        return publish;
    }

    @JsonProperty("Publish")
    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    @JsonProperty("ComputationIdentifier")
    public String getComputationIdentifier() {
        return computationIdentifier;
    }

    @JsonProperty("ComputationIdentifier")
    public void setComputationIdentifier(String computationIdentifier) {
        this.computationIdentifier = computationIdentifier;
    }

    public boolean hasComputationIdentifier() {
        return getComputationIdentifier() != null && !getComputationIdentifier().isEmpty();
    }

    @JsonProperty("ComputationPeriodIdentifier")
    public String getComputationPeriodIdentifier() {
        return computationPeriodIdentifier;
    }

    @JsonProperty("ComputationPeriodIdentifier")
    public void setComputationPeriodIdentifier(String computationPeriodIdentifier) {
        this.computationPeriodIdentifier = computationPeriodIdentifier;
    }

    public boolean hasComputationPeriodIdentifier() {
        return getComputationPeriodIdentifier() != null && !getComputationPeriodIdentifier().isEmpty();
    }

    @JsonProperty("SubLocationIdentifier")
    public String getSubLocationIdentifier() {
        return subLocationIdentifier;
    }

    @JsonProperty("SubLocationIdentifier")
    public void setSubLocationIdentifier(String subLocationIdentifier) {
        this.subLocationIdentifier = subLocationIdentifier;
    }

    @JsonProperty("ExtendedAttributes")
    public List<ExtendedAttribute> getExtendedAttributes() {
        return extendedAttributes;
    }

    @JsonProperty("ExtendedAttributes")
    public void setExtendedAttributes(List<ExtendedAttribute> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }

    @JsonProperty("Thresholds")
    public List<Threshold> getThresholds() {
        return thresholds;
    }

    @JsonProperty("Thresholds")
    public void setThresholds(List<Threshold> thresholds) {
        this.thresholds = thresholds;
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
        return new ToStringBuilder(this).append("identifier", identifier)
                .append("uniqueId", uniqueId)
                .append("locationIdentifier", locationIdentifier)
                .append("parameter", parameter)
                .append("unit", unit)
                .append("utcOffset", utcOffset)
                .append("utcOffsetIsoDuration", utcOffsetIsoDuration)
                .append("lastModified", lastModified)
                .append("rawStartTime", rawStartTime)
                .append("rawEndTime", rawEndTime)
                .append("correctedStartTime", correctedStartTime)
                .append("correctedEndTime", correctedEndTime)
                .append("timeSeriesType", timeSeriesType)
                .append("label", label)
                .append("comment", comment)
                .append("description", description)
                .append("publish", publish)
                .append("computationIdentifier", computationIdentifier)
                .append("computationPeriodIdentifier", computationPeriodIdentifier)
                .append("subLocationIdentifier", subLocationIdentifier)
                .append("extendedAttributes", extendedAttributes)
                .append("thresholds", thresholds)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(identifier)
                .append(computationIdentifier)
                .append(utcOffset)
                .append(correctedEndTime)
                .append(rawStartTime)
                .append(description)
                .append(timeSeriesType)
                .append(label)
                .append(extendedAttributes)
                .append(computationPeriodIdentifier)
                .append(unit)
                .append(subLocationIdentifier)
                .append(thresholds)
                .append(parameter)
                .append(publish)
                .append(rawEndTime)
                .append(comment)
                .append(lastModified)
                .append(additionalProperties)
                .append(uniqueId)
                .append(locationIdentifier)
                .append(utcOffsetIsoDuration)
                .append(correctedStartTime)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TimeSeriesDescription)) {
            return false;
        }
        TimeSeriesDescription rhs = (TimeSeriesDescription) other;
        return new EqualsBuilder().append(identifier, rhs.identifier)
                .append(computationIdentifier, rhs.computationIdentifier)
                .append(utcOffset, rhs.utcOffset)
                .append(correctedEndTime, rhs.correctedEndTime)
                .append(rawStartTime, rhs.rawStartTime)
                .append(description, rhs.description)
                .append(timeSeriesType, rhs.timeSeriesType)
                .append(label, rhs.label)
                .append(extendedAttributes, rhs.extendedAttributes)
                .append(computationPeriodIdentifier, rhs.computationPeriodIdentifier)
                .append(unit, rhs.unit)
                .append(subLocationIdentifier, rhs.subLocationIdentifier)
                .append(thresholds, rhs.thresholds)
                .append(parameter, rhs.parameter)
                .append(publish, rhs.publish)
                .append(rawEndTime, rhs.rawEndTime)
                .append(comment, rhs.comment)
                .append(lastModified, rhs.lastModified)
                .append(additionalProperties, rhs.additionalProperties)
                .append(uniqueId, rhs.uniqueId)
                .append(locationIdentifier, rhs.locationIdentifier)
                .append(utcOffsetIsoDuration, rhs.utcOffsetIsoDuration)
                .append(correctedStartTime, rhs.correctedStartTime)
                .isEquals();
    }
}
