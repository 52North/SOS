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
package org.n52.sos.w3c;

import org.n52.sos.util.Comparables;

import com.google.common.base.Objects;

/**
 * Class represents a XML schema location with namespace and schema fileURL.
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class SchemaLocation implements Comparable<SchemaLocation> {
    private final String namespace;

    private final String schemaFileUrl;

    private final String schemaLocationString;

    /**
     * Constructor
     * 
     * @param namespace
     *            Namespace
     * @param schemaFileUrl
     *            Schema file URL
     */
    public SchemaLocation(String namespace, String schemaFileUrl) {
        this.namespace = namespace;
        this.schemaFileUrl = schemaFileUrl;
        this.schemaLocationString = new StringBuilder().append(namespace).append(' ').append(schemaFileUrl).toString();
    }

    /**
     * Get namespace of schema location
     * 
     * @return namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Get schema file URL
     * 
     * @return schema file URL
     */
    public String getSchemaFileUrl() {
        return schemaFileUrl;
    }

    /**
     * @return Schema location string
     */
    public String getSchemaLocationString() {
        return schemaLocationString;
    }

    @Override
    public int compareTo(SchemaLocation o) {
        return Comparables.chain(o).compare(getNamespace(), o.getNamespace())
                .compare(getSchemaFileUrl(), o.getSchemaFileUrl()).result();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getNamespace(), getSchemaFileUrl());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            SchemaLocation other = (SchemaLocation) obj;
            return Objects.equal(getNamespace(), other.getNamespace())
                    && Objects.equal(getSchemaFileUrl(), other.getSchemaFileUrl());
        }
        return false;
    }
}
