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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 *            the type to compare
 * @param <K>
 *            the similarity type of {@code T}
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class ProxySimilarityComparator<T, K extends Similar<K>> implements Comparator<T> {
    private final SimilarityComparator<K> comp;

    public ProxySimilarityComparator(K ref) {
        this.comp = new SimilarityComparator<K>(ref);
    }

    @Override
    public int compare(T o1, T o2) {
        int compResult = comp.compare(Collections.min(getSimilars(o1), comp), Collections.min(getSimilars(o2), comp));
        // check inheritance hierarchy if key matches are equal and classes are not
        if (compResult == 0 && !o1.getClass().equals(o2.getClass())) {
            if (o1.getClass().isAssignableFrom(o2.getClass())) {
                return 1;
            } else if (o2.getClass().isAssignableFrom(o1.getClass())) {
                return -1;
            }
        }
        return compResult;
    }

    protected abstract Collection<K> getSimilars(T t);
}
