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
package org.n52.sos.ogc.ows;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class Constraint implements Comparable<Constraint> {
    private final String name;

    private final List<OwsParameterValue> values;

    public Constraint(String name, List<OwsParameterValue> values) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (values == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.values = values;
    }

    public Constraint(String name, OwsParameterValue value) {
        this(name, Collections.singletonList(value));
    }

    public String getName() {
        return name;
    }

    public List<OwsParameterValue> getValues() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getValues());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Constraint) {
            Constraint c = (Constraint) obj;
            return Objects.equal(getName(), c.getName()) && Objects.equal(getValues(), c.getValues());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Constraint[name=%s, values=%s]", getName(), getValues());
    }

    @Override
    public int compareTo(Constraint o) {
        return getName().compareTo(o.getName());
    }
}
