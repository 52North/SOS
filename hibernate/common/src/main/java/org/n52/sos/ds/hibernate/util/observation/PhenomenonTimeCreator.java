/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.util.observation;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;

public class PhenomenonTimeCreator {

    private DataEntity hObservation;
    private DatasetEntity hSeries;

    public PhenomenonTimeCreator(DataEntity hObservation) {
        this.hObservation = hObservation;
    }

    public PhenomenonTimeCreator(DatasetEntity hSeries) {
        this.hSeries = hSeries;
    }

    public Time create() {
        if (hObservation != null) {
            return createFromObservation();
        } else if (hSeries != null && hSeries.isSetFirstValueAt() && hSeries.isSetLastValueAt()) {
            return createFromSeries();
        }
        return null;
    }

    private Time createFromObservation() {
        // create time element
        final DateTime phenStartTime = new DateTime(hObservation.getSamplingTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (hObservation.getSamplingTimeEnd() != null) {
            phenEndTime = new DateTime(hObservation.getSamplingTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        return createTime(phenStartTime, phenEndTime);
    }

    private Time createFromSeries() {
        // create time element
        final DateTime phenStartTime = new DateTime(hSeries.getFirstValueAt(), DateTimeZone.UTC);
        final DateTime phenEndTime = new DateTime(hSeries.getLastValueAt(), DateTimeZone.UTC);
        return createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create {@link Time} from {@link DateTime}s
     *
     * @param start
     *            Start {@link DateTime}
     * @param end
     *            End {@link DateTime}
     * @return Resulting {@link Time}
     */
    protected Time createTime(DateTime start, DateTime end) {
        if (start.equals(end)) {
            return new TimeInstant(start);
        } else {
            return new TimePeriod(start, end);
        }
    }

}
