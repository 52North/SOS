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
@JsonPropertyOrder({ "UniqueId", "Name", "Description", "DecommissionedDate", "DecommissionedReason",
        "PrimarySinceDate", "Latitude", "Longitude", "ReferencePointPeriods" })
public class ReferencePoint implements Serializable {

    private static final long serialVersionUID = -3039622222736417619L;

    @JsonProperty("UniqueId")
    private String uniqueId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("DecommissionedDate")
    private String decommissionedDate;

    @JsonProperty("DecommissionedReason")
    private String decommissionedReason;

    @JsonProperty("PrimarySinceDate")
    private String primarySinceDate;

    @JsonProperty("Latitude")
    private Integer latitude;

    @JsonProperty("Longitude")
    private Integer longitude;

    @JsonProperty("ReferencePointPeriods")
    private List<ReferencePointPeriod> referencePointPeriods = new ArrayList<ReferencePointPeriod>();

    /**
     * No args constructor for use in serialization
     *
     */
    public ReferencePoint() {
    }

    public ReferencePoint(String uniqueId, String name, String description, String decommissionedDate,
            String decommissionedReason, String primarySinceDate, Integer latitude, Integer longitude,
            Collection<ReferencePointPeriod> referencePointPeriods) {
        super();
        this.uniqueId = uniqueId;
        this.name = name;
        this.description = description;
        this.decommissionedDate = decommissionedDate;
        this.decommissionedReason = decommissionedReason;
        this.primarySinceDate = primarySinceDate;
        this.latitude = latitude;
        this.longitude = longitude;
        setReferencePointPeriods(referencePointPeriods);
    }

    @JsonProperty("UniqueId")
    public String getUniqueId() {
        return uniqueId;
    }

    @JsonProperty("UniqueId")
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("DecommissionedDate")
    public String getDecommissionedDate() {
        return decommissionedDate;
    }

    @JsonProperty("DecommissionedDate")
    public void setDecommissionedDate(String decommissionedDate) {
        this.decommissionedDate = decommissionedDate;
    }

    @JsonProperty("DecommissionedReason")
    public String getDecommissionedReason() {
        return decommissionedReason;
    }

    @JsonProperty("DecommissionedReason")
    public void setDecommissionedReason(String decommissionedReason) {
        this.decommissionedReason = decommissionedReason;
    }

    @JsonProperty("PrimarySinceDate")
    public String getPrimarySinceDate() {
        return primarySinceDate;
    }

    @JsonProperty("PrimarySinceDate")
    public void setPrimarySinceDate(String primarySinceDate) {
        this.primarySinceDate = primarySinceDate;
    }

    @JsonProperty("Latitude")
    public Integer getLatitude() {
        return latitude;
    }

    @JsonProperty("Latitude")
    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("Longitude")
    public Integer getLongitude() {
        return longitude;
    }

    @JsonProperty("Longitude")
    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("ReferencePointPeriods")
    public List<ReferencePointPeriod> getReferencePointPeriods() {
        return Collections.unmodifiableList(referencePointPeriods);
    }

    @JsonProperty("ReferencePointPeriods")
    public void setReferencePointPeriods(Collection<ReferencePointPeriod> referencePointPeriods) {
        this.referencePointPeriods.clear();
        if (referencePointPeriods != null) {
            this.referencePointPeriods.addAll(referencePointPeriods);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("uniqueId", uniqueId)
                .append("name", name)
                .append("description", description)
                .append("decommissionedDate", decommissionedDate)
                .append("decommissionedReason", decommissionedReason)
                .append("primarySinceDate", primarySinceDate)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .append("referencePointPeriods", referencePointPeriods)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(decommissionedDate)
                .append(referencePointPeriods)
                .append(decommissionedReason)
                .append(latitude)
                .append(name)
                .append(description)
                .append(primarySinceDate)
                .append(uniqueId)
                .append(longitude)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ReferencePoint)) {
            return false;
        }
        ReferencePoint rhs = (ReferencePoint) other;
        return new EqualsBuilder().append(decommissionedDate, rhs.decommissionedDate)
                .append(referencePointPeriods, rhs.referencePointPeriods)
                .append(decommissionedReason, rhs.decommissionedReason)
                .append(latitude, rhs.latitude)
                .append(name, rhs.name)
                .append(description, rhs.description)
                .append(primarySinceDate, rhs.primarySinceDate)
                .append(uniqueId, rhs.uniqueId)
                .append(longitude, rhs.longitude)
                .isEquals();
    }

}
