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
@JsonPropertyOrder({ "Identifier", "DisplayName", "Description", "Color" })
@Generated("jsonschema2pojo")
public class Grade implements Serializable {

    @JsonProperty("Identifier")
    private String identifier;
    @JsonProperty("DisplayName")
    private String displayName;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Color")
    private String color;
    private final static long serialVersionUID = 358173135697378454L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Grade() {
    }

    /**
     *
     * @param identifier
     * @param color
     * @param displayName
     * @param description
     */
    public Grade(String identifier, String displayName, String description, String color) {
        super();
        this.identifier = identifier;
        this.displayName = displayName;
        this.description = description;
        this.color = color;
    }

    @JsonProperty("Identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("Identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Grade withIdentifier(String identifier) {
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

    public Grade withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    public Grade withDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("Color")
    public String getColor() {
        return color;
    }

    @JsonProperty("Color")
    public void setColor(String color) {
        this.color = color;
    }

    public Grade withColor(String color) {
        this.color = color;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("identifier", this.identifier).append("displayName", this.displayName)
                .append("description", this.description).append("color", this.color).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.identifier).append(this.description).append(this.color)
                .append(this.displayName).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Grade) == false) {
            return false;
        }
        Grade rhs = ((Grade) other);
        return new EqualsBuilder().append(this.identifier, rhs.identifier).append(this.description, rhs.description)
                .append(this.color, rhs.color).append(this.displayName, rhs.displayName).isEquals();
    }

}
