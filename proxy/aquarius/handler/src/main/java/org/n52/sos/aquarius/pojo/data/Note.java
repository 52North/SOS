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
@JsonPropertyOrder({ "NoteText", "StartTime", "EndTime" })
public class Note implements Serializable {

    private static final long serialVersionUID = -1336215510099873186L;

    @JsonProperty("NoteText")
    private String noteText;

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
    public Note() {
    }

    public Note(String noteText, String startTime, String endTime) {
        super();
        this.noteText = noteText;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @JsonProperty("NoteText")
    public String getNoteText() {
        return noteText;
    }

    @JsonProperty("NoteText")
    public void setNoteText(String noteText) {
        this.noteText = noteText;
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
        return new ToStringBuilder(this).append("noteText", noteText)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(noteText)
                .append(startTime)
                .append(endTime)
                .append(additionalProperties)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Note)) {
            return false;
        }
        Note rhs = (Note) other;
        return new EqualsBuilder().append(noteText, rhs.noteText)
                .append(startTime, rhs.startTime)
                .append(endTime, rhs.endTime)
                .append(additionalProperties, rhs.additionalProperties)
                .isEquals();
    }

}
