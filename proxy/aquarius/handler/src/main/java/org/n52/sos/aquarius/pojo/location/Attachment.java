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
@JsonPropertyOrder({ "AttachmentType", "AttachmentCategory", "FileName", "DateCreated", "DateUploaded",
        "DateLastAccessed", "UploadedByUser", "Comment", "GpsLatitude", "GpsLongitude", "Url" })
public class Attachment implements Serializable {

    private static final long serialVersionUID = 2712156592686748288L;

    @JsonProperty("AttachmentType")
    private String attachmentType;

    @JsonProperty("AttachmentCategory")
    private String attachmentCategory;

    @JsonProperty("FileName")
    private String fileName;

    @JsonProperty("DateCreated")
    private String dateCreated;

    @JsonProperty("DateUploaded")
    private String dateUploaded;

    @JsonProperty("DateLastAccessed")
    private String dateLastAccessed;

    @JsonProperty("UploadedByUser")
    private String uploadedByUser;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("GpsLatitude")
    private Integer gpsLatitude;

    @JsonProperty("GpsLongitude")
    private Integer gpsLongitude;

    @JsonProperty("Url")
    private String url;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Attachment() {
    }

    public Attachment(String attachmentType, String attachmentCategory, String fileName, String dateCreated,
            String dateUploaded, String dateLastAccessed, String uploadedByUser, String comment, Integer gpsLatitude,
            Integer gpsLongitude, String url) {
        super();
        this.attachmentType = attachmentType;
        this.attachmentCategory = attachmentCategory;
        this.fileName = fileName;
        this.dateCreated = dateCreated;
        this.dateUploaded = dateUploaded;
        this.dateLastAccessed = dateLastAccessed;
        this.uploadedByUser = uploadedByUser;
        this.comment = comment;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.url = url;
    }

    @JsonProperty("AttachmentType")
    public String getAttachmentType() {
        return attachmentType;
    }

    @JsonProperty("AttachmentType")
    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    @JsonProperty("AttachmentCategory")
    public String getAttachmentCategory() {
        return attachmentCategory;
    }

    @JsonProperty("AttachmentCategory")
    public void setAttachmentCategory(String attachmentCategory) {
        this.attachmentCategory = attachmentCategory;
    }

    @JsonProperty("FileName")
    public String getFileName() {
        return fileName;
    }

    @JsonProperty("FileName")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("DateCreated")
    public String getDateCreated() {
        return dateCreated;
    }

    @JsonProperty("DateCreated")
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @JsonProperty("DateUploaded")
    public String getDateUploaded() {
        return dateUploaded;
    }

    @JsonProperty("DateUploaded")
    public void setDateUploaded(String dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    @JsonProperty("DateLastAccessed")
    public String getDateLastAccessed() {
        return dateLastAccessed;
    }

    @JsonProperty("DateLastAccessed")
    public void setDateLastAccessed(String dateLastAccessed) {
        this.dateLastAccessed = dateLastAccessed;
    }

    @JsonProperty("UploadedByUser")
    public String getUploadedByUser() {
        return uploadedByUser;
    }

    @JsonProperty("UploadedByUser")
    public void setUploadedByUser(String uploadedByUser) {
        this.uploadedByUser = uploadedByUser;
    }

    @JsonProperty("Comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("Comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("GpsLatitude")
    public Integer getGpsLatitude() {
        return gpsLatitude;
    }

    @JsonProperty("GpsLatitude")
    public void setGpsLatitude(Integer gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    @JsonProperty("GpsLongitude")
    public Integer getGpsLongitude() {
        return gpsLongitude;
    }

    @JsonProperty("GpsLongitude")
    public void setGpsLongitude(Integer gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    @JsonProperty("Url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("Url")
    public void setUrl(String url) {
        this.url = url;
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
        return new ToStringBuilder(this).append("attachmentType", attachmentType)
                .append("attachmentCategory", attachmentCategory)
                .append("fileName", fileName)
                .append("dateCreated", dateCreated)
                .append("dateUploaded", dateUploaded)
                .append("dateLastAccessed", dateLastAccessed)
                .append("uploadedByUser", uploadedByUser)
                .append("comment", comment)
                .append("gpsLatitude", gpsLatitude)
                .append("gpsLongitude", gpsLongitude)
                .append("url", url)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(fileName)
                .append(gpsLongitude)
                .append(attachmentType)
                .append(attachmentCategory)
                .append(dateUploaded)
                .append(url)
                .append(dateCreated)
                .append(uploadedByUser)
                .append(dateLastAccessed)
                .append(comment)
                .append(additionalProperties)
                .append(gpsLatitude)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Attachment)) {
            return false;
        }
        Attachment rhs = (Attachment) other;
        return new EqualsBuilder().append(fileName, rhs.fileName)
                .append(gpsLongitude, rhs.gpsLongitude)
                .append(attachmentType, rhs.attachmentType)
                .append(attachmentCategory, rhs.attachmentCategory)
                .append(dateUploaded, rhs.dateUploaded)
                .append(url, rhs.url)
                .append(dateCreated, rhs.dateCreated)
                .append(uploadedByUser, rhs.uploadedByUser)
                .append(dateLastAccessed, rhs.dateLastAccessed)
                .append(comment, rhs.comment)
                .append(additionalProperties, rhs.additionalProperties)
                .append(gpsLatitude, rhs.gpsLatitude)
                .isEquals();
    }

}
