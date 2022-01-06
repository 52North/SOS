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
@JsonPropertyOrder({ "UniqueId", "LastModifiedUtc", "FromTimeUtc", "ToTimeUtc", "Details", "Tags",
        "LastModifiedByUser" })
public class LocationNote implements Serializable {

    private static final long serialVersionUID = -6202138769977653681L;

    @JsonProperty("UniqueId")
    private String uniqueId;

    @JsonProperty("LastModifiedUtc")
    private String lastModifiedUtc;

    @JsonProperty("FromTimeUtc")
    private String fromTimeUtc;

    @JsonProperty("ToTimeUtc")
    private String toTimeUtc;

    @JsonProperty("Details")
    private String details;

    @JsonProperty("Tags")
    private List<Tag> tags = new ArrayList<Tag>();

    @JsonProperty("LastModifiedByUser")
    private String lastModifiedByUser;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public LocationNote() {
    }

    public LocationNote(String uniqueId, String lastModifiedUtc, String fromTimeUtc, String toTimeUtc, String details,
            List<Tag> tags, String lastModifiedByUser) {
        super();
        this.uniqueId = uniqueId;
        this.lastModifiedUtc = lastModifiedUtc;
        this.fromTimeUtc = fromTimeUtc;
        this.toTimeUtc = toTimeUtc;
        this.details = details;
        this.tags = tags;
        this.lastModifiedByUser = lastModifiedByUser;
    }

    @JsonProperty("UniqueId")
    public String getUniqueId() {
        return uniqueId;
    }

    @JsonProperty("UniqueId")
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @JsonProperty("LastModifiedUtc")
    public String getLastModifiedUtc() {
        return lastModifiedUtc;
    }

    @JsonProperty("LastModifiedUtc")
    public void setLastModifiedUtc(String lastModifiedUtc) {
        this.lastModifiedUtc = lastModifiedUtc;
    }

    @JsonProperty("FromTimeUtc")
    public String getFromTimeUtc() {
        return fromTimeUtc;
    }

    @JsonProperty("FromTimeUtc")
    public void setFromTimeUtc(String fromTimeUtc) {
        this.fromTimeUtc = fromTimeUtc;
    }

    @JsonProperty("ToTimeUtc")
    public String getToTimeUtc() {
        return toTimeUtc;
    }

    @JsonProperty("ToTimeUtc")
    public void setToTimeUtc(String toTimeUtc) {
        this.toTimeUtc = toTimeUtc;
    }

    @JsonProperty("Details")
    public String getDetails() {
        return details;
    }

    @JsonProperty("Details")
    public void setDetails(String details) {
        this.details = details;
    }

    @JsonProperty("Tags")
    public List<Tag> getTags() {
        return tags;
    }

    @JsonProperty("Tags")
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @JsonProperty("LastModifiedByUser")
    public String getLastModifiedByUser() {
        return lastModifiedByUser;
    }

    @JsonProperty("LastModifiedByUser")
    public void setLastModifiedByUser(String lastModifiedByUser) {
        this.lastModifiedByUser = lastModifiedByUser;
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
        return new ToStringBuilder(this).append("uniqueId", uniqueId)
                .append("lastModifiedUtc", lastModifiedUtc)
                .append("fromTimeUtc", fromTimeUtc)
                .append("toTimeUtc", toTimeUtc)
                .append("details", details)
                .append("tags", tags)
                .append("lastModifiedByUser", lastModifiedByUser)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(toTimeUtc)
                .append(lastModifiedUtc)
                .append(fromTimeUtc)
                .append(details)
                .append(additionalProperties)
                .append(uniqueId)
                .append(tags)
                .append(lastModifiedByUser)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LocationNote)) {
            return false;
        }
        LocationNote rhs = (LocationNote) other;
        return new EqualsBuilder().append(toTimeUtc, rhs.toTimeUtc)
                .append(lastModifiedUtc, rhs.lastModifiedUtc)
                .append(fromTimeUtc, rhs.fromTimeUtc)
                .append(details, rhs.details)
                .append(additionalProperties, rhs.additionalProperties)
                .append(uniqueId, rhs.uniqueId)
                .append(tags, rhs.tags)
                .append(lastModifiedByUser, rhs.lastModifiedByUser)
                .isEquals();
    }

}
