/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.w3c;

import org.n52.iceland.util.Comparables;

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
