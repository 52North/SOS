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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ReverseOf extends BaseMatcher<Geometry> {

    private Geometry original;

    public ReverseOf(Geometry original) {
        this.original = original;
    }

    @Override
    public boolean matches(Object item) {
        if (item == null || item.getClass() != original.getClass()) {
            return false;
        }
        Geometry geom = (Geometry) item;
        Coordinate[] orig = original.getCoordinates();
        Coordinate[] switched = geom.getCoordinates();
        if (orig.length != switched.length) {
            return false;
        }
        for (int i = 0; i < orig.length; ++i) {
            if (!isSwitched(orig[i], switched[i])) {
                return false;
            }
        }
        return true;
    }

    protected boolean equal(double a, double b) {
        return Double.isNaN(a) ? Double.isNaN(b) : Double.compare(a, b) == 0;
    }

    protected boolean isSwitched(Coordinate a, Coordinate b) {
        return equal(a.x, b.y) && equal(a.y, b.x) && equal(a.z, b.z);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("reverse of ").appendValue(original);
    }

    @Factory
    public static Matcher<Geometry> reverseOf(Geometry geom) {
        return new ReverseOf(geom);
    }
}
