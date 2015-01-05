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
package org.n52.sos.util;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class CompositeSimilar<T extends Similar<T>> implements Similar<T> {
    private final Set<T> similars;

    public CompositeSimilar(Iterable<T> similars) {
        this.similars = Sets.newHashSet(similars);
    }

    protected Set<T> getSimilars() {
        return Collections.unmodifiableSet(this.similars);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            @SuppressWarnings(value = "unchecked")
            CompositeSimilar<T> key = (CompositeSimilar) obj;
            return key.matches(getSimilars()) && matches(key.getSimilars());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), Joiner.on(", ").join(similars));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(similars.toArray());
    }

    public boolean matches(Set<T> toTest) {
        return toTest == null ? similars.isEmpty() : toTest.containsAll(similars);
    }

    @Override
    public int getSimilarity(T key) {
        if (key != null && key.getClass() == getClass()) {
            CompositeSimilar<T> cek = (CompositeSimilar<T>) key;
            if (cek.getSimilars().size() != similars.size()) {
                return -1;
            }
            int similarity = 0;
            for (T k1 : similars) {
                int s = -1;
                for (T k2 : cek.getSimilars()) {
                    int ks = k1.getSimilarity(k2);
                    s = (s < 0) ? ks : Math.min(s, ks);
                    if (s == 0) {
                        break;
                    }
                }
                if (s < 0) {
                    return -1;
                } else {
                    similarity += s;
                }
            }
            return similarity;
        }
        return -1;
    }
}
