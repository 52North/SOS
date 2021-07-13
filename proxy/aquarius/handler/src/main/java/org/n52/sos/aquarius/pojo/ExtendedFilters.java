/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Joiner;

public class ExtendedFilters {

    private Map<String, String> filter = new LinkedHashMap<>();

    public ExtendedFilters() {
    }

    public ExtendedFilters addFilter(String filterName, String FilterValue) {
        this.filter.put(filterName, FilterValue);
        return this;
    }

    public ExtendedFilters addFilter(Map<String, String> filter) {
        if (filter != null) {
            this.filter.putAll(filter);
        }
        return this;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public boolean hasFilters() {
        return !getFilter().isEmpty();
    }

    public String encodeFilters() {
        if (hasFilters()) {
            String join = Joiner.on(",")
                    .join(encodeFilter());
            if (filter.size() > 1) {
                // [{FilterName:ContactName,FilterValue:Joe},{FilterName:Region,FilterValue:South
                // Basin}]
                return new StringBuffer().append("[")
                        .append(join)
                        .append("]")
                        .toString();
            } else {
                // {FilterName:LoggerPort,FilterValue:D2}
                // {FilterName:SOS_SYNC,FilterValue:true}
                return join;
            }
        }
        return "";
    }

    private Iterable<?> encodeFilter() {
        Set<String> encodedFilters = new LinkedHashSet<>();
        for (Entry<String, String> entry : getFilter().entrySet()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("{");
            buffer.append("FilterName")
                    .append(":")
                    .append(entry.getKey())
                    .append(",");
            buffer.append("FilterValue")
                    .append(":")
                    .append(entry.getValue());
            buffer.append("}");
            encodedFilters.add(buffer.toString());
        }
        return encodedFilters;
    }

}
