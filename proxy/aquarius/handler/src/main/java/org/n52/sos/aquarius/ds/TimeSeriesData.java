/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.ds;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Approval;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GapTolerance;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Grade;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InterpolationType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Method;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalTimeRange;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Iterables;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class TimeSeriesData {

    private final TimeSeriesDataServiceResponse original;
    private CheckerHandler checker = new CheckerHandler();

    public TimeSeriesData(TimeSeriesDataServiceResponse original) {
        this.original = original;
    }

    public String getUniqueId() {
        return original.getUniqueId();
    }

    public String getParameter() {
        return original.getParameter();
    }

    public String getLabel() {
        return original.getLabel();
    }

    public String getLocationIdentifier() {
        return original.getLocationIdentifier();
    }

    public Long getNumPoints() {
        return original.getNumPoints();
    }

    public String getUnit() {
        return original.getUnit();
    }

    public List<Approval> getApprovals() {
        return Collections.unmodifiableList(checkList(original.getApprovals()));
    }

    public List<Qualifier> getQualifiers() {
        return Collections.unmodifiableList(checkList(original.getQualifiers()));
    }

    public boolean hasQualifiers() {
        return getQualifiers() != null && !getQualifiers().isEmpty();
    }

    public List<Method> getMethods() {
        return Collections.unmodifiableList(checkList(original.getMethods()));
    }

    public List<Grade> getGrades() {
        return Collections.unmodifiableList(checkList(original.getGrades()));
    }

    public List<GapTolerance> getGapTolerances() {
        return Collections.unmodifiableList(checkList(original.getGapTolerances()));
    }

    public List<InterpolationType> getInterpolationTypes() {
        return Collections.unmodifiableList(checkList(original.getInterpolationTypes()));
    }

    public boolean hasInterpolationTypes() {
        return getInterpolationTypes() != null && !getInterpolationTypes().isEmpty();
    }

    public List<com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Note> getNotes() {
        return Collections.unmodifiableList(checkList(original.getNotes()));
    }

    public StatisticalTimeRange getTimeRange() {
        return original.getTimeRange();
    }

    public List<Point> getPoints() {
        return Collections.unmodifiableList(getChecker().check(original.getPoints()));
    }

    @JsonIgnore
    public boolean hasPoints() {
        return getPoints() != null && !getPoints().isEmpty();
    }

    public Instant getResponseTime() {
        return original.getResponseTime();
    }

    public String getSummary() {
        return original.getSummary();
    }

    @JsonIgnore
    public Point getFirstPoint() {
        return hasPoints() ? getChecker().check(Iterables.getFirst(original.getPoints(), null)) : null;
    }

    @JsonIgnore
    public Point getLastPoint() {
        return hasPoints() ? getChecker().check(Iterables.getLast(original.getPoints())) : null;
    }

    private <T> List<T> checkList(ArrayList<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    @JsonIgnore
    public TimeSeriesData addChecker(IntervalCheckerAndApplyer checker) {
        this.checker.addChecker(checker);
        return this;
    }

    @JsonIgnore
    private CheckerHandler getChecker() {
        return checker;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("uniqueId", getUniqueId()).append("parameter", getParameter())
                .append("label", getLabel()).append("locationIdentifier", getLocationIdentifier())
                .append("numPoints", getNumPoints()).append("unit", getUnit()).append("approvals", getApprovals())
                .append("qualifiers", getQualifiers()).append("methods", getMethods()).append("grades", getGrades())
                .append("gapTolerances", getGapTolerances()).append("interpolationTypes", getInterpolationTypes())
                .append("notes", getNotes()).append("timeRange", getTimeRange()).append("points", getPoints())
                .append("responseTime", getResponseTime()).append("summary", getSummary()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getNumPoints()).append(getSummary()).append(getNotes())
                .append(getMethods()).append(getResponseTime()).append(getQualifiers()).append(getLabel())
                .append(getGrades()).append(getPoints()).append(getUnit()).append(getParameter())
                .append(getApprovals()).append(getGapTolerances()).append(getInterpolationTypes())
                .append(getUniqueId()).append(getLocationIdentifier()).append(getTimeRange()).toHashCode();
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
        return new EqualsBuilder().append(getNumPoints(), rhs.getNumPoints()).append(getSummary(), rhs.getSummary())
                .append(getNotes(), rhs.getNotes()).append(getMethods(), rhs.getMethods())
                .append(getResponseTime(), rhs.getResponseTime()).append(getQualifiers(), rhs.getQualifiers())
                .append(getLabel(), rhs.getLabel()).append(getGrades(), rhs.getGrades())
                .append(getPoints(), rhs.getPoints()).append(getUnit(), rhs.getUnit())
                .append(getParameter(), rhs.getParameter()).append(getApprovals(), rhs.getApprovals())
                .append(getGapTolerances(), rhs.getGapTolerances())
                .append(getInterpolationTypes(), rhs.getInterpolationTypes()).append(getUniqueId(), rhs.getUniqueId())
                .append(getLocationIdentifier(), rhs.getTimeRange()).append(getTimeRange(), rhs.getTimeRange())
                .isEquals();
    }

}
