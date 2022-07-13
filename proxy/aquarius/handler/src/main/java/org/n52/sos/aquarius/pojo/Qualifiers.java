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
@JsonPropertyOrder({ "Qualifiers", "ResponseVersion", "ResponseTime", "Summary" })
@Generated("jsonschema2pojo")
public class Qualifiers implements Serializable {

    @JsonProperty("Qualifiers")
    @Valid
    private List<Qualifier> qualifiers = new ArrayList<Qualifier>();
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
    public Qualifiers() {
    }

    /**
     *
     * @param summary
     * @param responseVersion
     * @param responseTime
     * @param qualifiers
     */
    public Qualifiers(List<Qualifier> qualifiers, Integer responseVersion, String responseTime, String summary) {
        super();
        setQualifiers(qualifiers);
        this.responseVersion = responseVersion;
        this.responseTime = responseTime;
        this.summary = summary;
    }

    @JsonProperty("Qualifiers")
    public List<Qualifier> getQualifiers() {
        return Collections.unmodifiableList(qualifiers);
    }

    @JsonProperty("Qualifiers")
    public void setQualifiers(List<Qualifier> qualifiers) {
        this.qualifiers.clear();
        if (qualifiers != null) {
            this.qualifiers.addAll(qualifiers);
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
        return new ToStringBuilder(this).append("qualifiers", this.qualifiers).append("responseVersion", this.responseVersion)
                .append("responseTime", this.responseTime).append("summary", this.summary).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(summary).append(qualifiers).append(responseVersion).append(responseTime)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Qualifiers) == false) {
            return false;
        }
        Qualifiers rhs = ((Qualifiers) other);
        return new EqualsBuilder().append(this.summary, rhs.summary).append(this.qualifiers, rhs.qualifiers)
                .append(responseVersion, rhs.responseVersion).append(this.responseTime, rhs.responseTime).isEquals();
    }

}