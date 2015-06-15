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
package org.n52.sos.ds.hibernate.dao;

import java.sql.Timestamp;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ogc.gml.time.TimePeriod;

/**
 * Abstract class to create a time period object
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public abstract class TimeCreator {

    protected enum MinMax {
        MIN, MAX
    }

    /**
     * Creates a time period object from sources
     * 
     * @param minStart
     *            Min start timestamp
     * @param maxStart
     *            Max start timestamp
     * @param maxEnd
     *            Max end timestamp
     * @return Time period object
     */
    public TimePeriod createTimePeriod(Timestamp minStart, Timestamp maxStart, Timestamp maxEnd) {
        DateTime start = new DateTime(minStart, DateTimeZone.UTC);
        DateTime end = new DateTime(maxStart, DateTimeZone.UTC);
        if (maxEnd != null) {
            DateTime endTmp = new DateTime(maxEnd, DateTimeZone.UTC);
            if (endTmp.isAfter(end)) {
                end = endTmp;
            }
        }
        return new TimePeriod(start, end);
    }

    /**
     * Add min/max projection to criteria
     * 
     * @param criteria
     *            Hibernate Criteria to add projection
     * @param minMax
     *            Min/Max identifier
     * @param property
     *            Property to apply projection to
     */
    public void addMinMaxProjection(Criteria criteria, MinMax minMax, String property) {
        // TODO move this to a better location, maybe with Java 8 in an own Interface with Multiple Inheritance
        switch (minMax) {
        case MIN:
            criteria.setProjection(Projections.min(property));
            break;
        case MAX:
            criteria.setProjection(Projections.max(property));
            break;
        }
    }
}
