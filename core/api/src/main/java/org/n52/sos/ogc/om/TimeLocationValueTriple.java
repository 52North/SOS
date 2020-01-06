/**
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
package org.n52.sos.ogc.om;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.values.Value;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Class that hold the time, the location and the value.
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class TimeLocationValueTriple extends TimeValuePair {

    /**
     * Time location value triple value
     */
    private Geometry location;

    public TimeLocationValueTriple(Time time, Value<?> value, Geometry location) {
        super(time, value);
        setLocation(location);
    }

    /**
     * @return the location
     */
    public Geometry getLocation() {
        return location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(Geometry location) {
        this.location = location;
    }

    public boolean isSetLocation() {
        return getLocation() != null && !getLocation().isEmpty();
    }

    @Override
    public int compareTo(TimeValuePair o) {
        CompareToBuilder compareToBuilder = new CompareToBuilder().appendSuper(super.compareTo(o));
        if (o instanceof TimeLocationValueTriple) {
            compareToBuilder.append(this.getLocation(), ((TimeLocationValueTriple) o).getLocation());
        }
        return compareToBuilder.toComparison();
    }
}
