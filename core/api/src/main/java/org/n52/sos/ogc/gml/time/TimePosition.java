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
package org.n52.sos.ogc.gml.time;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.time.Time.TimeFormat;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.util.Constants;

/**
 * Representation class for GML TimePosition. Used by TimeInstant and TimePeriod
 * during encoding to reduce duplicate code.
 * 
 * @since 4.0.0
 * 
 */
public class TimePosition {

    /**
     * Date time of time position
     */
    private DateTime time;

    /**
     * Indeterminate value of time position
     */
    private TimeIndeterminateValue indeterminateValue;

    /**
     * Time format
     */
    private TimeFormat timeFormat;

    /**
     * constructor
     * 
     * @param time
     *            Time postion time
     */
    public TimePosition(DateTime time) {
        super();
    }

    /**
     * constructor
     * 
     * @param indeterminateValue
     *            Indeterminate value of time position
     */
    public TimePosition(TimeIndeterminateValue indeterminateValue) {
        super();
        this.indeterminateValue = indeterminateValue;
    }

    /**
     * constructor
     * 
     * @param time
     *            Time position time
     * @param timeFormat
     *            Time format
     */
    public TimePosition(DateTime time, TimeFormat timeFormat) {
        super();
        this.time = time;
        this.setTimeFormat(timeFormat);
    }

    /**
     * Get time position time
     * 
     * @return the time Time position time
     */
    public DateTime getTime() {
        return time;
    }

    /**
     * Set time position time
     * 
     * @param time
     *            the time to set
     */
    public void setTime(DateTime time) {
        this.time = time;
    }

    /**
     * Get time position indeterminate value
     * 
     * @return the indeterminateValue time position indeterminate value
     */
    public TimeIndeterminateValue getIndeterminateValue() {
        return indeterminateValue;
    }

    /**
     * Get time position time format
     * 
     * @return the timeFormat Time position time format
     */
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    /**
     * Set time position time format
     * 
     * @param timeFormat
     *            the timeFormat to set
     */
    public void setTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
     * Set time position indeterminat value
     * 
     * @param indeterminateValue
     *            the indeterminateValue to set
     */
    public void setIndeterminateValue(TimeIndeterminateValue indeterminateValue) {
        this.indeterminateValue = indeterminateValue;
    }

    /**
     * Check if time value is set
     * 
     * @return <tt>true</tt>, if time is set
     */
    public boolean isSetTime() {
        return getTime() != null;
    }

    /**
     * Check if indeterminateValue is set
     * 
     * @return <tt>true</tt>, if indeterminateValue is set
     */
    public boolean isSetIndeterminateValue() {
        return getIndeterminateValue() != null;
    }

    /**
     * Check if time format is set
     * 
     * @return <tt>true</tt>, if time format is set
     */
    public boolean isSetTimeFormat() {
        return getTimeFormat() != null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Time position: ");
        if (isSetTime()) {
            result.append(getTime().toString()).append(Constants.COMMA_STRING);
        }
        result.append(getIndeterminateValue());
        return result.toString();
    }
}
