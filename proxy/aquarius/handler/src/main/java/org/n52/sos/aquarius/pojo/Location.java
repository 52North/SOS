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
import org.n52.sos.aquarius.pojo.location.Attachment;
import org.n52.sos.aquarius.pojo.location.ExtendedAttribute;
import org.n52.sos.aquarius.pojo.location.LocationDatum;
import org.n52.sos.aquarius.pojo.location.LocationNote;
import org.n52.sos.aquarius.pojo.location.LocationRemark;
import org.n52.sos.aquarius.pojo.location.ReferencePoint;
import org.n52.sos.aquarius.pojo.location.Tag;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "LocationName", "Description", "Identifier", "UniqueId", "LocationType", "IsExternalLocation",
        "Latitude", "Longitude", "Srid", "ElevationUnits", "Elevation", "UtcOffset", "Tags", "ExtendedAttributes",
        "LocationRemarks", "LocationNotes", "Attachments", "LocationDatum", "ReferencePoints", "ResponseVersion",
        "ResponseTime", "Summary" })
public class Location implements Serializable {

    private static final long serialVersionUID = -8891399059182138893L;

    @JsonProperty("LocationName")
    private String locationName;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Identifier")
    private String identifier;

    @JsonProperty("UniqueId")
    private String uniqueId;

    @JsonProperty("LocationType")
    private String locationType;

    @JsonProperty("IsExternalLocation")
    private Boolean isExternalLocation;

    @JsonProperty("Latitude")
    private Double latitude;

    @JsonProperty("Longitude")
    private Double longitude;

    @JsonProperty("Srid")
    private Integer srid;

    @JsonProperty("ElevationUnits")
    private String elevationUnits;

    @JsonProperty("Elevation")
    private Double elevation;

    @JsonProperty("UtcOffset")
    private Double utcOffset;

    @JsonProperty("Tags")
    private List<Tag> tags = new ArrayList<Tag>();

    @JsonProperty("ExtendedAttributes")
    private List<ExtendedAttribute> extendedAttributes = new ArrayList<ExtendedAttribute>();

    @JsonProperty("LocationRemarks")
    private List<LocationRemark> locationRemarks = new ArrayList<LocationRemark>();

    @JsonProperty("LocationNotes")
    private List<LocationNote> locationNotes = new ArrayList<LocationNote>();

    @JsonProperty("Attachments")
    private List<Attachment> attachments = new ArrayList<Attachment>();

    @JsonProperty("LocationDatum")
    private LocationDatum locationDatum;

    @JsonProperty("ReferencePoints")
    private List<ReferencePoint> referencePoints = new ArrayList<ReferencePoint>();

    @JsonProperty("ResponseVersion")
    private Double responseVersion;

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
    public Location() {
    }

    public Location(String locationName, String description, String identifier, String uniqueId, String locationType,
            Boolean isExternalLocation, Double latitude, Double longitude, Integer srid, String elevationUnits,
            Double elevation, Double utcOffset, List<Tag> tags, List<ExtendedAttribute> extendedAttributes,
            List<LocationRemark> locationRemarks, List<LocationNote> locationNotes, List<Attachment> attachments,
            LocationDatum locationDatum, List<ReferencePoint> referencePoints, Double responseVersion,
            String responseTime, String summary) {
        super();
        this.locationName = locationName;
        this.description = description;
        this.identifier = identifier;
        this.uniqueId = uniqueId;
        this.locationType = locationType;
        this.isExternalLocation = isExternalLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.srid = srid;
        this.elevationUnits = elevationUnits;
        this.elevation = elevation;
        this.utcOffset = utcOffset;
        this.tags = tags;
        this.extendedAttributes = extendedAttributes;
        this.locationRemarks = locationRemarks;
        this.locationNotes = locationNotes;
        this.attachments = attachments;
        this.locationDatum = locationDatum;
        this.referencePoints = referencePoints;
        this.responseVersion = responseVersion;
        this.responseTime = responseTime;
        this.summary = summary;
    }

    @JsonProperty("LocationName")
    public String getLocationName() {
        return locationName;
    }

    @JsonProperty("LocationName")
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
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

    @JsonProperty("LocationType")
    public String getLocationType() {
        return locationType;
    }

    @JsonProperty("LocationType")
    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    @JsonProperty("IsExternalLocation")
    public Boolean getIsExternalLocation() {
        return isExternalLocation;
    }

    @JsonProperty("IsExternalLocation")
    public void setIsExternalLocation(Boolean isExternalLocation) {
        this.isExternalLocation = isExternalLocation;
    }

    @JsonProperty("Latitude")
    public Double getLatitude() {
        return latitude;
    }

    @JsonProperty("Latitude")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("Longitude")
    public Double getLongitude() {
        return longitude;
    }

    @JsonProperty("Longitude")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("Srid")
    public Integer getSrid() {
        return srid;
    }

    @JsonProperty("Srid")
    public void setSrid(Integer srid) {
        this.srid = srid;
    }

    @JsonProperty("ElevationUnits")
    public String getElevationUnits() {
        return elevationUnits;
    }

    @JsonProperty("ElevationUnits")
    public void setElevationUnits(String elevationUnits) {
        this.elevationUnits = elevationUnits;
    }

    @JsonProperty("Elevation")
    public Double getElevation() {
        return elevation;
    }

    @JsonProperty("Elevation")
    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    @JsonProperty("UtcOffset")
    public Double getUtcOffset() {
        return utcOffset;
    }

    @JsonProperty("UtcOffset")
    public void setUtcOffset(Double utcOffset) {
        this.utcOffset = utcOffset;
    }

    @JsonProperty("Tags")
    public List<Tag> getTags() {
        return tags;
    }

    @JsonProperty("Tags")
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @JsonProperty("ExtendedAttributes")
    public List<ExtendedAttribute> getExtendedAttributes() {
        return extendedAttributes;
    }

    @JsonProperty("ExtendedAttributes")
    public void setExtendedAttributes(List<ExtendedAttribute> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }

    @JsonProperty("LocationRemarks")
    public List<LocationRemark> getLocationRemarks() {
        return locationRemarks;
    }

    @JsonProperty("LocationRemarks")
    public void setLocationRemarks(List<LocationRemark> locationRemarks) {
        this.locationRemarks = locationRemarks;
    }

    @JsonProperty("LocationNotes")
    public List<LocationNote> getLocationNotes() {
        return locationNotes;
    }

    @JsonProperty("LocationNotes")
    public void setLocationNotes(List<LocationNote> locationNotes) {
        this.locationNotes = locationNotes;
    }

    @JsonProperty("Attachments")
    public List<Attachment> getAttachments() {
        return attachments;
    }

    @JsonProperty("Attachments")
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @JsonProperty("LocationDatum")
    public LocationDatum getLocationDatum() {
        return locationDatum;
    }

    @JsonProperty("LocationDatum")
    public void setLocationDatum(LocationDatum locationDatum) {
        this.locationDatum = locationDatum;
    }

    @JsonProperty("ReferencePoints")
    public List<ReferencePoint> getReferencePoints() {
        return referencePoints;
    }

    @JsonProperty("ReferencePoints")
    public void setReferencePoints(List<ReferencePoint> referencePoints) {
        this.referencePoints = referencePoints;
    }

    @JsonProperty("ResponseVersion")
    public Double getResponseVersion() {
        return responseVersion;
    }

    @JsonProperty("ResponseVersion")
    public void setResponseVersion(Double responseVersion) {
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
        return new ToStringBuilder(this).append("locationName", locationName)
                .append("description", description)
                .append("identifier", identifier)
                .append("uniqueId", uniqueId)
                .append("locationType", locationType)
                .append("isExternalLocation", isExternalLocation)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .append("srid", srid)
                .append("elevationUnits", elevationUnits)
                .append("elevation", elevation)
                .append("utcOffset", utcOffset)
                .append("tags", tags)
                .append("extendedAttributes", extendedAttributes)
                .append("locationRemarks", locationRemarks)
                .append("locationNotes", locationNotes)
                .append("attachments", attachments)
                .append("locationDatum", locationDatum)
                .append("referencePoints", referencePoints)
                .append("responseVersion", responseVersion)
                .append("responseTime", responseTime)
                .append("summary", summary)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(elevation)
                .append(summary)
                .append(identifier)
                .append(locationName)
                .append(utcOffset)
                .append(attachments)
                .append(responseTime)
                .append(latitude)
                .append(description)
                .append(locationType)
                .append(extendedAttributes)
                .append(srid)
                .append(tags)
                .append(elevationUnits)
                .append(responseVersion)
                .append(locationRemarks)
                .append(locationNotes)
                .append(isExternalLocation)
                .append(referencePoints)
                .append(additionalProperties)
                .append(uniqueId)
                .append(longitude)
                .append(locationDatum)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Location)) {
            return false;
        }
        Location rhs = (Location) other;
        return new EqualsBuilder().append(elevation, rhs.elevation)
                .append(summary, rhs.summary)
                .append(identifier, rhs.identifier)
                .append(locationName, rhs.locationName)
                .append(utcOffset, rhs.utcOffset)
                .append(attachments, rhs.attachments)
                .append(responseTime, rhs.responseTime)
                .append(latitude, rhs.latitude)
                .append(description, rhs.description)
                .append(locationType, rhs.locationType)
                .append(extendedAttributes, rhs.extendedAttributes)
                .append(srid, rhs.srid)
                .append(tags, rhs.tags)
                .append(elevationUnits, rhs.elevationUnits)
                .append(responseVersion, rhs.responseVersion)
                .append(locationRemarks, rhs.locationRemarks)
                .append(locationNotes, rhs.locationNotes)
                .append(isExternalLocation, rhs.isExternalLocation)
                .append(referencePoints, rhs.referencePoints)
                .append(additionalProperties, rhs.additionalProperties)
                .append(uniqueId, rhs.uniqueId)
                .append(longitude, rhs.longitude)
                .append(locationDatum, rhs.locationDatum)
                .isEquals();
    }
}
