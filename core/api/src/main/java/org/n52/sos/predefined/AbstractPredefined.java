/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.predefined;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "type",
    "name",
    "description",
    "values"
})
public abstract class AbstractPredefined<T extends Translate> {

    private String name;

    private String description;

    private List<T> values = new LinkedList<>();

    public abstract PredefinedType getType();

    public void setType(PredefinedType type) {

    }

    public String getName() {
        return name;
    }

    public AbstractPredefined<T> setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AbstractPredefined<T> setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<T> getValues() {
        return values;
    }

    public AbstractPredefined<T> setValues(Collection<T> values) {
        this.values.addAll(values);
        return this;
    }

    public AbstractPredefined<T> addValue(T value) {
        this.values.add(value);
        return this;
    }
}
