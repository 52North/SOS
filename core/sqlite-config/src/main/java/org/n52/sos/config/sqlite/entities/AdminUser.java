/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
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
package org.n52.sos.config.sqlite.entities;

import java.io.Serializable;

import javax.persistence.*;

import org.n52.sos.config.AdministratorUser;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 */
@Entity(name="administrator_user")
public class AdminUser implements Serializable, AdministratorUser {
    private static final long serialVersionUID = -6073682567042001348L;
    public static final String USERNAME_PROPERTY = "username";
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = AdminUser.USERNAME_PROPERTY, unique = true)
    private String username;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public AdminUser setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public AdminUser setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 67;
        int hash = 5;
        hash = prime * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = prime * hash + (this.username != null ? this.username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AdminUser) {
            final AdminUser other = (AdminUser) obj;
            return (getId() == null ? other.getId() == null : getId().equals(other.getId()))
                   && (getUsername() == null ? other.getUsername() == null : getUsername().equals(other.getUsername()));
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[username=%s, hash=%s]", getClass().getSimpleName(), getUsername(), getPassword());
    }
}
