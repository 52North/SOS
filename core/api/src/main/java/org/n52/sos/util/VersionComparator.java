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
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class VersionComparator implements Comparator<String> {
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
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final VersionComparator INSTANCE = new VersionComparator();

        private InstanceHolder() {
        }
    }
}
