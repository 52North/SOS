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
import java.util.Collections;
import java.util.List;

import javax.annotation.Generated;
import javax.validation.Valid;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Grades", "ResponseVersion", "ResponseTime", "Summary" })
@Generated("jsonschema2pojo")
public class Grades implements Serializable {

    private static final long serialVersionUID = 358173135697378454L;
    @JsonProperty("Grades")
    @Valid
    private List<Grade> grades = new ArrayList<Grade>();
    @JsonProperty("ResponseVersion")
    private Integer responseVersion;
    @JsonProperty("ResponseTime")
    private String responseTime;
    @JsonProperty("Summary")
    private String summary;

    /**
     * No args constructor for use in serialization
     *
     */
    public Grades() {
    }

    /**
     *
     * @param summary
     *            The summary
     * @param responseVersion
     *            The response version
     * @param responseTime
     *            The response time
     * @param grades
     *            The grade list
     */
    public Grades(List<Grade> grades, Integer responseVersion, String responseTime, String summary) {
        super();
        setGrades(grades);
        this.responseVersion = responseVersion;
        this.responseTime = responseTime;
        this.summary = summary;
    }

    @JsonProperty("Grades")
    public List<Grade> getGrades() {
        return Collections.unmodifiableList(grades);
    }

    @JsonProperty("Grades")
    public void setGrades(List<Grade> grades) {
        this.grades.clear();
        if (grades != null) {
            this.grades.addAll(grades);
        }
    }

    @JsonProperty("ResponseVersion")
    public Integer getResponseVersion() {
        return responseVersion;
    }

    @JsonProperty("ResponseVersion")
    public void setResponseVersion(Integer responseVersion) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("grades", this.grades).append("responseVersion", this.responseVersion)
                .append("responseTime", this.responseTime).append("summary", this.summary).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(summary).append(grades).append(responseVersion).append(responseTime)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Grades)) {
            return false;
        }
        Grades rhs = (Grades) other;
        return new EqualsBuilder().append(this.summary, rhs.summary).append(this.grades, rhs.grades)
                .append(responseVersion, rhs.responseVersion).append(this.responseTime, rhs.responseTime).isEquals();
    }

}