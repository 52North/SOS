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

import java.util.Comparator;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 *            the type
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class SimilarityComparator<T extends Similar<T>> implements Comparator<T> {
    private final T ref;

    public SimilarityComparator(T ref) {
        this.ref = ref;
    }

    @Override
    public int compare(T o1, T o2) {
        if (o1 == o2) {
            return 0;
        }
        // FIXME this conflicts with the contract of compare
        if (o1 == null || o2 == null) {
            return -1;
        }
        // check for equals after strict == and null checks
        if (o1.equals(o2)) {
            return 0;
        }        
        int s1 = o1.getSimilarity(ref);
        int s2 = o2.getSimilarity(ref);
        //check for inheritance
        if (s1 == s2 && !o1.getClass().equals(o2.getClass())) {
            if (o1.getClass().isAssignableFrom(o2.getClass())) {
                return 1;
            } else if (o2.getClass().isAssignableFrom(o1.getClass())) {
                return -1;
            }
        }
        if (s1 == 0) {
            return -1;
        }
        if (s2 == 0) {
            return 1;
        }
        if (s1 < 0) {
            return s2 < 0 ? 0 : 1;
        } else if (s2 < 0 || s1 < s2) {
            return -1;
        } else {
            return 1;
        }
    }
}
