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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.n52.sos.aquarius.ds.Checker;
import org.n52.sos.aquarius.ds.CheckerHandler;
import org.n52.sos.aquarius.pojo.data.Approval;
import org.n52.sos.aquarius.pojo.data.GapTolerance;
import org.n52.sos.aquarius.pojo.data.Grade;
import org.n52.sos.aquarius.pojo.data.InterpolationType;
import org.n52.sos.aquarius.pojo.data.Method;
import org.n52.sos.aquarius.pojo.data.Note;
import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.aquarius.pojo.data.Qualifier;
import org.n52.sos.aquarius.pojo.data.TimeRange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Iterables;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "UniqueId", "Parameter", "Label", "LocationIdentifier", "NumPoints", "Unit", "Approvals",
        "Qualifiers", "Methods", "Grades", "GapTolerances", "InterpolationTypes", "Notes", "TimeRange", "Points",
        "ResponseVersion", "ResponseTime", "Summary" })
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
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
    private CheckerHandler checker = new CheckerHandler();

    /**
     * No args constructor for use in serialization
     *
     */
    public TimeSeriesData() {
    }

    public TimeSeriesData(String uniqueId, String parameter, String label, String locationIdentifier, String numPoints,
            String unit, Collection<Approval> approvals, Collection<Qualifier> qualifiers, Collection<Method> methods,
            Collection<Grade> grades, Collection<GapTolerance> gapTolerances,
            Collection<InterpolationType> interpolationTypes, Collection<Note> notes, TimeRange timeRange,
            Collection<Point> points, Integer responseVersion, String responseTime, String summary) {
        super();
        this.uniqueId = uniqueId;
        this.parameter = parameter;
        this.label = label;
        this.locationIdentifier = locationIdentifier;
        this.numPoints = numPoints;
        this.unit = unit;
        setApprovals(approvals);
        setQualifiers(qualifiers);
        setMethods(methods);
        setGrades(grades);
        setGapTolerances(gapTolerances);
        setInterpolationTypes(interpolationTypes);
        setNotes(notes);
        this.timeRange = timeRange;
        setPoints(points);
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
        return Collections.unmodifiableList(approvals);
    }

    @JsonProperty("Approvals")
    public void setApprovals(Collection<Approval> approvals) {
        this.approvals.clear();
        if (approvals != null) {
            this.approvals.addAll(approvals);
        }
    }

    @JsonProperty("Qualifiers")
    public List<Qualifier> getQualifiers() {
        return Collections.unmodifiableList(qualifiers);
    }

    @JsonProperty("Qualifiers")
    public void setQualifiers(Collection<Qualifier> qualifiers) {
        this.qualifiers.clear();
        if (qualifiers != null) {
            this.qualifiers.addAll(qualifiers);
        }
    }

    @JsonIgnore
    public boolean hasQualifiers() {
        return getQualifiers() != null && !getQualifiers().isEmpty();
    }

    @JsonProperty("Methods")
    public List<Method> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    @JsonProperty("Methods")
    public void setMethods(Collection<Method> methods) {
        this.methods.clear();
        if (methods != null) {
            this.methods.addAll(methods);
        }
    }

    @JsonProperty("Grades")
    public List<Grade> getGrades() {
        return Collections.unmodifiableList(grades);
    }

    @JsonProperty("Grades")
    public void setGrades(Collection<Grade> grades) {
        this.grades.clear();
        if (grades != null) {
            this.grades.addAll(grades);
        }
    }

    @JsonProperty("GapTolerances")
    public List<GapTolerance> getGapTolerances() {
        return Collections.unmodifiableList(gapTolerances);
    }

    @JsonProperty("GapTolerances")
    public void setGapTolerances(Collection<GapTolerance> gapTolerances) {
        this.gapTolerances.clear();
        if (gapTolerances != null) {
            this.gapTolerances.addAll(gapTolerances);
        }
    }

    @JsonProperty("InterpolationTypes")
    public List<InterpolationType> getInterpolationTypes() {
        return Collections.unmodifiableList(interpolationTypes);
    }

    @JsonProperty("InterpolationTypes")
    public void setInterpolationTypes(Collection<InterpolationType> interpolationTypes) {
        this.interpolationTypes.clear();
        if (interpolationTypes != null) {
            this.interpolationTypes.addAll(interpolationTypes);
        }
    }

    public boolean hasInterpolationTypes() {
        return getInterpolationTypes() != null && !getInterpolationTypes().isEmpty();
    }

    @JsonProperty("Notes")
    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }

    @JsonProperty("Notes")
    public void setNotes(Collection<Note> notes) {
        this.notes.clear();
        if (notes != null) {
            this.notes.addAll(notes);
        }
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
        return Collections.unmodifiableList(getChecker().check(points));
    }

    @JsonProperty("Points")
    public void setPoints(Collection<Point> points) {
        this.points.clear();
        if (points != null) {
            this.points.addAll(points);
        }
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

    @JsonIgnore
    public Point getFirstPoint() {
        return hasPoints() ? getChecker().check(Iterables.getFirst(getPoints(), null)) : null;
    }

    @JsonIgnore
    public Point getLastPoint() {
        return hasPoints() ? getChecker().check(Iterables.getLast(getPoints())) : null;
    }

    @JsonIgnore
    public TimeSeriesData addChecker(Checker checker) {
        this.checker.addChecker(checker);
        return this;
    }

    @JsonIgnore
    private CheckerHandler getChecker() {
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
                .append(gapTolerances, rhs.gapTolerances)
                .append(interpolationTypes, rhs.interpolationTypes)
                .append(uniqueId, rhs.uniqueId)
                .append(locationIdentifier, rhs.locationIdentifier)
                .append(timeRange, rhs.timeRange)
                .isEquals();
    }

}
