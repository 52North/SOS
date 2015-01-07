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

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.n52.sos.ogc.swes.SwesExtensionImpl;

/**
 * @param <K> the key type
 * @param <T> the type of the extending class
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
@MappedSuperclass
public class Activatable<K extends Serializable, T extends Activatable<K, T>> extends SwesExtensionImpl<String> implements Serializable{
    private static final long serialVersionUID = -1470828735015412115L;
	
	public static final String ACTIVE = "active";
	public static final String COMPOSITE_KEY = "id";
	
    @Id
    private K key;
	
	@Column(name = ACTIVE)
    private boolean active;

    public Activatable(final K key) {
        this.key = key;
    }

    public boolean isActive() {
        return active;
    }

    @SuppressWarnings("unchecked")
    public T setActive(final boolean active) {
        this.active = active;
        return (T) this;
    }

    public K getKey() {
        return key;
    }

    @SuppressWarnings("unchecked")
    public T setKey(final K encodingKey) {
        this.key = encodingKey;
        return (T) this;
    }

    @Override
    public int hashCode() {
        final int prime = 97;
        int hash = 3;
        hash = prime * hash + (getKey() != null ? getKey().hashCode() : 0);
        hash = prime * hash + (isActive() ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Activatable) {
            final Activatable<?, ?> o = (Activatable<?, ?>) obj;
            return (getKey() == null ? o.getKey() == null : getKey().equals(o.getKey())) && isActive() == o.isActive();
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[key=%s, active=%b]", getClass().getSimpleName(), getKey(), isActive());
    }
}
