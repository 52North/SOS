/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.gml.time;

import org.joda.time.DateTime;
import org.n52.iceland.ogc.gml.time.Time.TimeFormat;
import org.n52.iceland.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.iceland.util.Constants;

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
