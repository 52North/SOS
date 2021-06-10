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
import org.n52.sos.aquarius.pojo.location.Tag;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Name", "Identifier", "UniqueId", "IsExternalLocation", "PrimaryFolder", "SecondaryFolders",
        "LastModified", "Tags" })
public class LocationDescription implements Serializable {

    private static final long serialVersionUID = 7574631245937011029L;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Identifier")
    private String identifier;

    @JsonProperty("UniqueId")
    private String uniqueId;

    @JsonProperty("IsExternalLocation")
    private Boolean isExternalLocation;

    @JsonProperty("PrimaryFolder")
    private String primaryFolder;

    @JsonProperty("SecondaryFolders")
    private List<String> secondaryFolders = new ArrayList<String>();

    @JsonProperty("LastModified")
    private String lastModified;

    @JsonProperty("Tags")
    private List<Tag> tags = new ArrayList<Tag>();

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public LocationDescription() {
    }

    public LocationDescription(String name, String identifier, String uniqueId, Boolean isExternalLocation,
            String primaryFolder, List<String> secondaryFolders, String lastModified, List<Tag> tags) {
        super();
        this.name = name;
        this.identifier = identifier;
        this.uniqueId = uniqueId;
        this.isExternalLocation = isExternalLocation;
        this.primaryFolder = primaryFolder;
        this.secondaryFolders = secondaryFolders;
        this.lastModified = lastModified;
        this.tags = tags;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
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

    @JsonProperty("IsExternalLocation")
    public Boolean getIsExternalLocation() {
        return isExternalLocation;
    }

    @JsonProperty("IsExternalLocation")
    public void setIsExternalLocation(Boolean isExternalLocation) {
        this.isExternalLocation = isExternalLocation;
    }

    @JsonProperty("PrimaryFolder")
    public String getPrimaryFolder() {
        return primaryFolder;
    }

    @JsonProperty("PrimaryFolder")
    public void setPrimaryFolder(String primaryFolder) {
        this.primaryFolder = primaryFolder;
    }

    @JsonProperty("SecondaryFolders")
    public List<String> getSecondaryFolders() {
        return secondaryFolders;
    }

    @JsonProperty("SecondaryFolders")
    public void setSecondaryFolders(List<String> secondaryFolders) {
        this.secondaryFolders = secondaryFolders;
    }

    @JsonProperty("LastModified")
    public String getLastModified() {
        return lastModified;
    }

    @JsonProperty("LastModified")
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    @JsonProperty("Tags")
    public List<Tag> getTags() {
        return tags;
    }

    @JsonProperty("Tags")
    public void setTags(List<Tag> tags) {
        this.tags = tags;
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
        return new ToStringBuilder(this).append("name", name)
                .append("identifier", identifier)
                .append("uniqueId", uniqueId)
                .append("isExternalLocation", isExternalLocation)
                .append("primaryFolder", primaryFolder)
                .append("secondaryFolders", secondaryFolders)
                .append("lastModified", lastModified)
                .append("tags", tags)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(secondaryFolders)
                .append(identifier)
                .append(isExternalLocation)
                .append(name)
                .append(primaryFolder)
                .append(lastModified)
                .append(additionalProperties)
                .append(uniqueId)
                .append(tags)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LocationDescription)) {
            return false;
        }
        LocationDescription rhs = (LocationDescription) other;
        return new EqualsBuilder().append(secondaryFolders, rhs.secondaryFolders)
                .append(identifier, rhs.identifier)
                .append(isExternalLocation, rhs.isExternalLocation)
                .append(name, rhs.name)
                .append(primaryFolder, rhs.primaryFolder)
                .append(lastModified, rhs.lastModified)
                .append(additionalProperties, rhs.additionalProperties)
                .append(uniqueId, rhs.uniqueId)
                .append(tags, rhs.tags)
                .isEquals();
    }

}
