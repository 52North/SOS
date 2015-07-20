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

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ElasticsearchTypeRegistry {
    public final static ElasticsearchType stringField = new ElasticsearchType(ImmutableMap.<String, Object> of("type", "string", "index",
            "not_analyzed"));
    public final static ElasticsearchType stringAnalyzedField = new ElasticsearchType(ImmutableMap.<String, Object> of("type", "string", "index",
            "analyzed"));
    public final static ElasticsearchType dateField = new ElasticsearchType(ImmutableMap.<String, Object> of("type", "date"));
    public final static ElasticsearchType integerField = new ElasticsearchType(ImmutableMap.<String, Object> of("type", "integer"));
    public final static ElasticsearchType longField = new ElasticsearchType(ImmutableMap.<String, Object> of("type", "long"));
    public final static ElasticsearchType doubleField = new ElasticsearchType(ImmutableMap.<String, Object> of("type", "double"));
    public final static ElasticsearchType booleanField = new ElasticsearchType(ImmutableMap.<String, Object> of("type", "boolean"));
    public final static ElasticsearchType geoPointField = new ElasticsearchType(ImmutableMap.<String, Object> of("type", "geo_point"));
    public final static ElasticsearchType geoShapeField = new ElasticsearchType(ImmutableMap.<String, Object> of("type", "geo_shape", "precision",
            "1km"));

    public static class ElasticsearchType {
        private final Map<String, Object> type;

        public ElasticsearchType(Map<String, Object> type) {
            this.type = type;
        }

        public Map<String, Object> getType() {
            return type;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
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
            ElasticsearchType other = (ElasticsearchType) obj;
            if (type == null) {
                if (other.type != null)
                    return false;
            } else if (!type.equals(other.type))
                return false;
            return true;
        }
    }

}
