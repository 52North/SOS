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

import java.util.Scanner;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

public class Comparables {
    public static final int LESS = -1, EQUAL = 0, GREATER = 1;

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
        return (a == b) ? EQUAL : a == null ? LESS : b == null ? GREATER : a
                .compareTo(b);
    }

    public static ComparisonChain chain(Object o) {
        Preconditions.checkNotNull(o);
        return ComparisonChain.start();
    }

    public static <T> Ordering<T> inheritance() {
        return InheritanceComparator.instance();
    }

    public static Ordering<String> version() {
        return VersionComparator.instance();
    }

    private static class VersionComparator extends Ordering<String> {
        private static final VersionComparator INSTANCE = new VersionComparator();
        private static final Pattern DELIMITER = Pattern.compile("[._-]");
        private static final Pattern EOF = Pattern.compile("\\z");

        private VersionComparator() {
        }

        @Override
        public int compare(String a, String b) {
            Scanner as = new Scanner(a).useDelimiter(DELIMITER);
            Scanner bs = new Scanner(b).useDelimiter(DELIMITER);
            while (as.hasNextInt() && bs.hasNextInt()) {
                int c = Comparables.compare(as.nextInt(), bs.nextInt());
                if (c != 0) {
                    return c;
                }
            }
            if (as.hasNextInt()) {
                return 1;
            } else if (bs.hasNextInt()) {
                return -1;
            } else {
                boolean na = as.useDelimiter(EOF).hasNext();
                boolean nb = bs.useDelimiter(EOF).hasNext();
                if (na && nb) {
                    return as.next().compareTo(bs.next());
                } else if (na) {
                    return -1;
                } else if (nb) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        public static VersionComparator instance() {
            return INSTANCE;
        }
    }

    private static class InheritanceComparator<T> extends Ordering<T> {
        private static final InheritanceComparator<Object> INSTANCE
                = new InheritanceComparator<Object>();

        private InheritanceComparator() {
        }

        @Override
        public int compare(T o1, T o2) {
            if (o1 == null) {
                return LESS;
            } else if (o2 == null) {
                return GREATER;
            } else {
                Class<?> c1 = o1.getClass();
                Class<?> c2 = o2.getClass();
                if (c1.isAssignableFrom(c2)) {
                    return GREATER;
                } else if (c2.isAssignableFrom(c1)) {
                    return LESS;
                } else {
                    return EQUAL;
                }
            }
        }

        @SuppressWarnings("unchecked")
        public static <T> InheritanceComparator<T> instance() {
            return (InheritanceComparator<T>) INSTANCE;
        }
    }
}
