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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.n52.sos.config.SettingValue;

/**
 * @param <T> settings type
 * <p/>
 * @author Christian Autermann <c.autermann@52north.org>
 */
@Entity(name = "settings")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractSettingValue<T> implements SettingValue<T>, Serializable {
    
    @Id
    private String identifier;

    @Override
    public String getKey() {
        return this.identifier;
    }

    @Override
    public SettingValue<T> setKey(String key) {
        this.identifier = key;
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s[type=%s, key=%s, value=%s]", getClass().getSimpleName(), getType(),
        		getKey(), getValue());
    }

    @Override
    public int hashCode() {
        final int prime = 79;
        int hash = 7;
        hash = prime * hash + (this.getKey() != null ? this.getKey().hashCode() : 0);
        hash = prime * hash + (this.getValue() != null ? this.getValue().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SettingValue) {
            final SettingValue<?> other = (SettingValue<?>) obj;
            return (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey()))
                   && (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
        }
        return false;
    }
}
