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
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Timestamp", "Value" })
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Point implements Serializable {

    private static final long serialVersionUID = -7566570989032058426L;

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("Value")
    private Value value;

    @JsonIgnore
    private Set<Qualifier> qualifiers;
    
    @JsonIgnore
    private Set<Grade> grades;

    /**
     * No args constructor for use in serialization
     *
     */
    public Point() {
    }

    public Point(String timestamp, Value value) {
        super();
        this.timestamp = timestamp;
        this.value = value;
    }

    @JsonProperty("Timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("Timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("Value")
    public Value getValue() {
        return value;
    }

    @JsonProperty("Value")
    public void setValue(Value value) {
        this.value = value;
    }

    @JsonIgnore
    public Point setQualifier(Set<Qualifier> qualifiers) {
        this.qualifiers.clear();
        addQualifiers(qualifiers);
        return this;
    }

    @JsonIgnore
    public Point addQualifiers(Set<Qualifier> qualifiers) {
        if (qualifiers != null) {
            this.qualifiers.addAll(qualifiers);
        }
        return this;
    }

    @JsonIgnore
    public Point addQualifier(Qualifier qualifier) {
        if (qualifier != null) {
            this.qualifiers.add(qualifier);
        }
        return this;
    }

    @JsonIgnore
    public Set<Qualifier> getQualifiers() {
        return qualifiers;
    }

    @JsonIgnore
    public boolean hasQualifiers() {
        return getQualifiers() != null && !getQualifiers().isEmpty();
    }

    @JsonIgnore
    public Point setGrade(Set<Grade> grades) {
        this.grades.clear();
        addGrades(grades);
        return this;
    }

    @JsonIgnore
    public Point addGrades(Set<Grade> grades) {
        if (grades != null) {
            this.grades.addAll(grades);
        }
        return this;
    }

    @JsonIgnore
    public Point addGrade(Grade grade) {
        if (grade != null) {
            this.grades.add(grade);
        }
        return this;
    }

    @JsonIgnore
    public Set<Grade> getGrades() {
        return grades;
    }

    @JsonIgnore
    public boolean hasGrades() {
        return getGrades() != null && !getGrades().isEmpty();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("timestamp", timestamp).append("value", value).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(value).append(timestamp).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Point)) {
            return false;
        }
        Point rhs = (Point) other;
        return new EqualsBuilder().append(value, rhs.value).append(timestamp, rhs.timestamp).isEquals();
    }

}
