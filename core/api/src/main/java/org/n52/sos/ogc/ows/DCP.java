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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.base.Objects;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class DCP implements Comparable<DCP> {
    private final String url;

    private final SortedSet<Constraint> constraints;

    public DCP(String url) {
        this(url, (Set<Constraint>) null);
    }

    public DCP(String url, Set<Constraint> constraints) {
        this.url = url;
        if (constraints == null) {
            this.constraints = new TreeSet<Constraint>();
        } else {
            this.constraints = new TreeSet<Constraint>(constraints);
        }
    }

    public DCP(String url, Constraint constraint) {
        this(url, Collections.singleton(constraint));
    }

    public String getUrl() {
        return url;
    }

    public Set<Constraint> getConstraints() {
        return Collections.unmodifiableSet(constraints);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUrl(), getConstraints());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DCP) {
            DCP o = (DCP) obj;
            return Objects.equal(getUrl(), o.getUrl()) && Objects.equal(getConstraints(), o.getConstraints());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("DCP[]", getUrl(), getConstraints());
    }

    @Override
    public int compareTo(DCP o) {
        if (o == null) {
            throw new NullPointerException();
        }

        if (url == null ^ o.getUrl() == null) {
            return (url == null) ? -1 : 1;
        }

        if (url == null && o.getUrl() == null) {
            return 0;
        }
        
        return url.compareTo(o.getUrl());
    }
}
