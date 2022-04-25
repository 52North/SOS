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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Username", "EncryptedPassword", "Locale" })
public class SessionRequestEntity implements Serializable {

    private static final long serialVersionUID = 2666053679064946462L;

    @JsonProperty("Username")
    private String username;

    @JsonProperty("EncryptedPassword")
    private String encryptedPassword;

    @JsonProperty("Locale")
    private String locale;

    /**
     * No args constructor for use in serialization
     *
     */
    public SessionRequestEntity() {
    }

    public SessionRequestEntity(String username, String encryptedPassword) {
        super();
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    public SessionRequestEntity(String username, String encryptedPassword, String locale) {
        super();
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.locale = locale;
    }

    @JsonProperty("Username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("Username")
    public SessionRequestEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    @JsonProperty("EncryptedPassword")
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    @JsonProperty("EncryptedPassword")
    public SessionRequestEntity setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
        return this;
    }

    @JsonProperty("Locale")
    public String getLocale() {
        return locale;
    }

    @JsonProperty("Locale")
    public SessionRequestEntity setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("username", username)
                .append("encryptedPassword", encryptedPassword)
                .append("locale", locale)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(locale)
                .append(username)
                .append(encryptedPassword)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SessionRequestEntity)) {
            return false;
        }
        SessionRequestEntity rhs = (SessionRequestEntity) other;
        return new EqualsBuilder()
                .append(locale, rhs.locale)
                .append(username, rhs.username)
                .append(encryptedPassword, rhs.encryptedPassword)
                .isEquals();
    }

}
