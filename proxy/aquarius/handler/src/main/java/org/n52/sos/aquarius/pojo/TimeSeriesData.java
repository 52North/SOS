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
import org.n52.sos.aquarius.ds.QualifierChecker;
import org.n52.sos.aquarius.pojo.data.Approval;
import org.n52.sos.aquarius.pojo.data.GapTolerance;
import org.n52.sos.aquarius.pojo.data.Grade;
import org.n52.sos.aquarius.pojo.data.InterpolationType;
import org.n52.sos.aquarius.pojo.data.Method;
import org.n52.sos.aquarius.pojo.data.Note;
import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.aquarius.pojo.data.Qualifier;
import org.n52.sos.aquarius.pojo.data.TimeRange;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Iterables;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "UniqueId", "Parameter", "Label", "LocationIdentifier", "NumPoints", "Unit", "Approvals",
        "Qualifiers", "Methods", "Grades", "GapTolerances", "InterpolationTypes", "Notes", "TimeRange", "Points",
        "ResponseVersion", "ResponseTime", "Summary" })
public class TimeSeriesData implements Serializable {

    private static final long serialVersionUID = -1300064642777536708L;

    @JsonProperty("UniqueId")
    private String uniqueId;

    @JsonProperty("Parameter")
    private String parameter;

    @JsonProperty("Label")
    private String label;

    @JsonProperty("LocationIdentifier")
    private String locationIdentifier;

    @JsonProperty("NumPoints")
    private String numPoints;

    @JsonProperty("Unit")
    private String unit;

    @JsonProperty("Approvals")
    private List<Approval> approvals = new ArrayList<Approval>();

    @JsonProperty("Qualifiers")
    private List<Qualifier> qualifiers = new ArrayList<Qualifier>();

    @JsonProperty("Methods")
    private List<Method> methods = new ArrayList<Method>();

    @JsonProperty("Grades")
    private List<Grade> grades = new ArrayList<Grade>();

    @JsonProperty("GapTolerances")
    private List<GapTolerance> gapTolerances = new ArrayList<GapTolerance>();

    @JsonProperty("InterpolationTypes")
    private List<InterpolationType> interpolationTypes = new ArrayList<InterpolationType>();

    @JsonProperty("Notes")
    private List<Note> notes = new ArrayList<Note>();

    @JsonProperty("TimeRange")
    private TimeRange timeRange;

    @JsonProperty("Points")
    private List<Point> points = new ArrayList<Point>();

    @JsonProperty("ResponseVersion")
    private Integer responseVersion;

    @JsonProperty("ResponseTime")
    private String responseTime;

    @JsonProperty("Summary")
    private String summary;

    @JsonIgnore
    private QualifierChecker checker = new QualifierChecker();

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public TimeSeriesData() {
    }

    public TimeSeriesData(String uniqueId, String parameter, String label, String locationIdentifier, String numPoints,
            String unit, List<Approval> approvals, List<Qualifier> qualifiers, List<Method> methods,
            List<Grade> grades, List<GapTolerance> gapTolerances, List<InterpolationType> interpolationTypes,
            List<Note> notes, TimeRange timeRange, List<Point> points, Integer responseVersion, String responseTime,
            String summary) {
        super();
        this.uniqueId = uniqueId;
        this.parameter = parameter;
        this.label = label;
        this.locationIdentifier = locationIdentifier;
        this.numPoints = numPoints;
        this.unit = unit;
        this.approvals = approvals;
        this.qualifiers = qualifiers;
        this.methods = methods;
        this.grades = grades;
        this.gapTolerances = gapTolerances;
        this.interpolationTypes = interpolationTypes;
        this.notes = notes;
        this.timeRange = timeRange;
        this.points = points;
        this.responseVersion = responseVersion;
        this.responseTime = responseTime;
        this.summary = summary;
    }

    @JsonProperty("UniqueId")
    public String getUniqueId() {
        return uniqueId;
    }

    @JsonProperty("UniqueId")
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @JsonProperty("Parameter")
    public String getParameter() {
        return parameter;
    }

    @JsonProperty("Parameter")
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @JsonProperty("Label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("Label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("LocationIdentifier")
    public String getLocationIdentifier() {
        return locationIdentifier;
    }

    @JsonProperty("LocationIdentifier")
    public void setLocationIdentifier(String locationIdentifier) {
        this.locationIdentifier = locationIdentifier;
    }

    @JsonProperty("NumPoints")
    public String getNumPoints() {
        return numPoints;
    }

    @JsonProperty("NumPoints")
    public void setNumPoints(String numPoints) {
        this.numPoints = numPoints;
    }

    @JsonProperty("Unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("Unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty("Approvals")
    public List<Approval> getApprovals() {
        return approvals;
    }

    @JsonProperty("Approvals")
    public void setApprovals(List<Approval> approvals) {
        this.approvals = approvals;
    }

    @JsonProperty("Qualifiers")
    public List<Qualifier> getQualifiers() {
        return qualifiers;
    }

    @JsonProperty("Qualifiers")
    public void setQualifiers(List<Qualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }

    @JsonIgnore
    public boolean hasQualifiers() {
        return getQualifiers() != null && !getQualifiers().isEmpty();
    }

    @JsonProperty("Methods")
    public List<Method> getMethods() {
        return methods;
    }

    @JsonProperty("Methods")
    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    @JsonProperty("Grades")
    public List<Grade> getGrades() {
        return grades;
    }

    @JsonProperty("Grades")
    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    @JsonProperty("GapTolerances")
    public List<GapTolerance> getGapTolerances() {
        return gapTolerances;
    }

    @JsonProperty("GapTolerances")
    public void setGapTolerances(List<GapTolerance> gapTolerances) {
        this.gapTolerances = gapTolerances;
    }

    @JsonProperty("InterpolationTypes")
    public List<InterpolationType> getInterpolationTypes() {
        return interpolationTypes;
    }

    @JsonProperty("InterpolationTypes")
    public void setInterpolationTypes(List<InterpolationType> interpolationTypes) {
        this.interpolationTypes = interpolationTypes;
    }

    public boolean hasInterpolationTypes() {
        return getInterpolationTypes() != null && !getInterpolationTypes().isEmpty();
    }

    @JsonProperty("Notes")
    public List<Note> getNotes() {
        return notes;
    }

    @JsonProperty("Notes")
    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @JsonProperty("TimeRange")
    public TimeRange getTimeRange() {
        return timeRange;
    }

    @JsonProperty("TimeRange")
    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    @JsonProperty("Points")
    public List<Point> getPoints() {
        return checker.check(points);
    }

    @JsonProperty("Points")
    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @JsonIgnore
    public boolean hasPoints() {
        return getPoints() != null && !getPoints().isEmpty();
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonIgnore
    public Point getFirstPoint() {
        return hasPoints() ? checker.check(Iterables.getFirst(getPoints(), null)) : null;
    }

    @JsonIgnore
    public Point getLastPoint() {
        return hasPoints() ? checker.check(Iterables.getLast(getPoints())) : null;
    }

    @JsonIgnore
    public TimeSeriesData setQualifierChecker(QualifierChecker checker) {
        this.checker = checker;
        return this;
    }

    @JsonIgnore
    private QualifierChecker getChecker() {
        return checker;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("uniqueId", uniqueId)
                .append("parameter", parameter)
                .append("label", label)
                .append("locationIdentifier", locationIdentifier)
                .append("numPoints", numPoints)
                .append("unit", unit)
                .append("approvals", approvals)
                .append("qualifiers", qualifiers)
                .append("methods", methods)
                .append("grades", grades)
                .append("gapTolerances", gapTolerances)
                .append("interpolationTypes", interpolationTypes)
                .append("notes", notes)
                .append("timeRange", timeRange)
                .append("points", points)
                .append("responseVersion", responseVersion)
                .append("responseTime", responseTime)
                .append("summary", summary)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(numPoints)
                .append(summary)
                .append(notes)
                .append(methods)
                .append(responseTime)
                .append(qualifiers)
                .append(label)
                .append(grades)
                .append(points)
                .append(unit)
                .append(responseVersion)
                .append(parameter)
                .append(approvals)
                .append(additionalProperties)
                .append(gapTolerances)
                .append(interpolationTypes)
                .append(uniqueId)
                .append(locationIdentifier)
                .append(timeRange)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TimeSeriesData)) {
            return false;
        }
        TimeSeriesData rhs = (TimeSeriesData) other;
        return new EqualsBuilder().append(numPoints, rhs.numPoints)
                .append(summary, rhs.summary)
                .append(notes, rhs.notes)
                .append(methods, rhs.methods)
                .append(responseTime, rhs.responseTime)
                .append(qualifiers, rhs.qualifiers)
                .append(label, rhs.label)
                .append(grades, rhs.grades)
                .append(points, rhs.points)
                .append(unit, rhs.unit)
                .append(responseVersion, rhs.responseVersion)
                .append(parameter, rhs.parameter)
                .append(approvals, rhs.approvals)
                .append(additionalProperties, rhs.additionalProperties)
                .append(gapTolerances, rhs.gapTolerances)
                .append(interpolationTypes, rhs.interpolationTypes)
                .append(uniqueId, rhs.uniqueId)
                .append(locationIdentifier, rhs.locationIdentifier)
                .append(timeRange, rhs.timeRange)
                .isEquals();
    }

}
