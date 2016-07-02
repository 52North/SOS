/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;

/**
 * Holder for observation time extrema. Contains phenomenon, result and valid
 * time.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.3.0
 *
 */
public class ObservationTimeExtrema {

    private DateTime minPhenTime;

    private DateTime maxPhenTime;

    private DateTime maxResultTime;

    private DateTime minValidTime;

    private DateTime maxValidTime;

    public DateTime getMinPhenTime() {
        return minPhenTime;
    }

    public void setMinPhenTime(DateTime minPhenTime) {
        this.minPhenTime = minPhenTime;
    }

    public DateTime getMaxPhenTime() {
        return maxPhenTime;
    }

    public void setMaxPhenTime(DateTime maxPhenTime) {
        this.maxPhenTime = maxPhenTime;
    }

    public DateTime getMaxResultTime() {
        return maxResultTime;
    }

    public void setMaxResultTime(DateTime maxResultTime) {
        this.maxResultTime = maxResultTime;
    }

    public DateTime getMinValidTime() {
        return minValidTime;
    }

    public void setMinValidTime(DateTime minValidTime) {
        this.minValidTime = minValidTime;
    }

    public DateTime getMaxValidTime() {
        return maxValidTime;
    }

    public void setMaxValidTime(DateTime maxValidTime) {
        this.maxValidTime = maxValidTime;
    }
    
    public Time getPhenomenonTime() {
        if (isSetPhenomenonTime()) {
            if (getMinPhenTime().equals(getMaxPhenTime())) {
                return new TimeInstant(getMaxPhenTime());
            }
            return new TimePeriod(getMinPhenTime(), getMaxPhenTime());
            }
        return null;
    }
    
    public Time getResultTime() {
        if (isSetResultTime()) {
            return new TimeInstant(getMaxResultTime());
        }
        return null;
    }
    
    public Time getValidTime() {
        if (isSetValidTime()) {
            if (getMinValidTime().equals(getMaxValidTime())) {
                return new TimeInstant(getMaxValidTime());
            }
            return new TimePeriod(getMinValidTime(), getMaxValidTime());
            }
        return null;
    }

    public boolean isSetPhenomenonTime() {
        return getMinPhenTime() != null && getMaxPhenTime() != null;
    }

    public boolean isSetResultTime() {
        return getMaxResultTime() != null;
    }

    public boolean isSetValidTime() {
        return getMinValidTime() != null && getMaxValidTime() != null;
    }
    
    public boolean isEmpty() {
        return !isSetPhenomenonTime() && !isSetResultTime() && !isSetValidTime();
    }

}
