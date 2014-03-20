/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class Comparables {

    private Comparables() {
    }

    public static int compare(int x, int y) {
        return Ints.compare(x, y);
    }

    public static int compare(byte x, byte y) {
        return x - y;
    }

    public static int compare(short x, short y) {
        return Shorts.compare(x, y);
    }

    public static int compare(char x, char y) {
        return Chars.compare(x, y);
    }

    public static int compare(long x, long y) {
        return Longs.compare(x, y);
    }

    public static int compare(boolean x, boolean y) {
        return Booleans.compare(x, y);
    }

    public static int compare(float a, float b) {
        return Floats.compare(a, b);
    }

    public static int compare(double a, double b) {
        return Doubles.compare(a, b);
    }

    public static <T extends Comparable<T>> int compare(T a, T b) {
        return (a == b) ? 0 : ((a == null) ? -1 : ((b == null) ? 1 : a.compareTo(b)));
    }

    public static ComparisonChain chain(Object o) {
        Preconditions.checkNotNull(o);
        return ComparisonChain.start();
    }
}
