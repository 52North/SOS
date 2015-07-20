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
package org.n52.sos.statistics.api.parameters;

import java.util.List;
import java.util.Map;

import org.n52.sos.statistics.api.parameters.ElasticsearchTypeRegistry.ElasticsearchType;

/**
 * Abstract Elasticsearch variables which the user sees on the interface by the
 * name
 */
public abstract class AbstractEsParameter {
    private final String name;
    private String description;
    private ElasticsearchType type = null;

    public AbstractEsParameter(String name) {
        this.name = name;
    }

    public AbstractEsParameter(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final String getName() {
        return name;
    }

    public List<AbstractEsParameter> getAllChildren() {
        return null;
    }

    public ElasticsearchType getType() {
        return type;
    }

    public Map<String, Object> getTypeAsMap() {
        if (type != null) {
            return type.getType();
        } else {
            return null;
        }
    }

    public void setType(ElasticsearchType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AbstractEsParameter [name=" + name + ", description=" + description + ", type=" + type + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEsParameter other = (AbstractEsParameter) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
