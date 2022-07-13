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

    @JsonProperty("Grades")
    @Valid
    private List<Grade> grades = new ArrayList<Grade>();
    @JsonProperty("ResponseVersion")
    private Integer responseVersion;
    @JsonProperty("ResponseTime")
    private String responseTime;
    @JsonProperty("Summary")
    private String summary;
    private final static long serialVersionUID = 2991222437815253569L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Grades() {
    }

    /**
     *
     * @param summary
     * @param responseVersion
     * @param responseTime
     * @param grades
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
        if ((other instanceof Grades) == false) {
            return false;
        }
        Grades rhs = ((Grades) other);
        return new EqualsBuilder().append(this.summary, rhs.summary).append(this.grades, rhs.grades)
                .append(responseVersion, rhs.responseVersion).append(this.responseTime, rhs.responseTime).isEquals();
    }

}