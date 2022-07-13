package org.n52.sos.aquarius.pojo;

import java.io.Serializable;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Identifier", "Code", "DisplayName" })
@Generated("jsonschema2pojo")
public class Qualifier implements Serializable {

    @JsonProperty("Identifier")
    private String identifier;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("DisplayName")
    private String displayName;

    private final static long serialVersionUID = 358173135697378454L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Qualifier() {
    }

    /**
     *
     * @param identifier
     * @param code
     * @param displayName
     */
    public Qualifier(String identifier, String code, String displayName) {
        super();
        this.identifier = identifier;
        this.displayName = displayName;
        this.code = code;
    }

    @JsonProperty("Identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("Identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Qualifier withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @JsonProperty("DisplayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("DisplayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Qualifier withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    @JsonProperty("Code")
    public void setCode(String code) {
        this.code = code;
    }

    public Qualifier withCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("identifier", this.identifier).append("displayName", this.displayName)
                .append("code", this.code).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.identifier).append(this.code)
                .append(this.displayName).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Qualifier) == false) {
            return false;
        }
        Qualifier rhs = ((Qualifier) other);
        return new EqualsBuilder().append(this.identifier, rhs.identifier)
                .append(this.code, rhs.code).append(this.displayName, rhs.displayName).isEquals();
    }

}
