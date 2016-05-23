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

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.util.Constants;

/**
 * Class represents a GML conform timeInstant element
 * 
 * @since 4.0.0
 */
public class TimeInstant extends Time {
    /**
     * serial number
     */
    private static final long serialVersionUID = -1426561343329882331L;

    /** date for this timeInstant */
    private DateTime value;

    /** length of timeInstant date */
    private int requestedTimeLength;

    /**
     * SOS indeterminate time, e.g. {@link SosIndeterminateTime#first},
     * {@link SosIndeterminateTime#latest}
     */
    private SosIndeterminateTime sosIndeterminateTime;

    /**
     * Indeterminate value
     */
    private TimeIndeterminateValue indeterminateValue;

    /**
     * default constructor
     */
    public TimeInstant() {
    }

    /**
     * constructor
     * 
     * @param dateTime
     *            Time position of this time instant
     */
    public TimeInstant(final DateTime dateTime) {
        this.value = dateTime;
    }

    /**
     * constructor
     * 
     * @param indeterminateValue
     *            Indeterminate value
     */
    public TimeInstant(TimeIndeterminateValue indeterminateValue) {
        this(null, indeterminateValue);
    }

    /**
     * constructor
     * 
     * @param sosIndeterminateTime
     *            SOS indeterminate time
     */
    public TimeInstant(SosIndeterminateTime sosIndeterminateTime) {
        this.sosIndeterminateTime = sosIndeterminateTime;
    }

    /**
     * constructor with date and indeterminateValue.
     * 
     * @param dateValue
     *            date of the timeInstante
     * @param indeterminateValue
     *            Indeterminate value
     */
    public TimeInstant(final DateTime dateValue, final TimeIndeterminateValue indeterminateValue) {
        super(null);
        this.value = dateValue;
        this.indeterminateValue = indeterminateValue;
    }

    /**
     * Constructor using Java Dates, setting unknown indeterminate values if
     * null
     * 
     * @param date Value date
     */
    public TimeInstant(Date date) {
        if (date != null) {
            this.value = new DateTime(date, DateTimeZone.UTC);
        } else {
            this.indeterminateValue = TimeIndeterminateValue.unknown;
        }
    }

    /**
     * Get time value
     * 
     * @return Returns the value.
     */
    public DateTime getValue() {
        return value;
    }

    /**
     * Get time value, resolving indeterminate value if value is null
     * 
     * @return Returns the resolved value.
     */
    public DateTime resolveValue() {
        return resolveDateTime(value, getIndeterminateValue());
    }

    /**
     * Set time value
     * 
     * @param value
     *            The value to set.
     */
    public void setValue(final DateTime value) {
        this.value = value;
    }

    /**
     * Set requested time length
     * 
     * @param requestedTimeLength
     *            the requestedTimeLength to set
     */
    public void setRequestedTimeLength(final int requestedTimeLength) {
        this.requestedTimeLength = requestedTimeLength;
    }

    /**
     * Get requested time length
     * 
     * @return the requestedTimeLength
     */
    public int getRequestedTimeLength() {
        return requestedTimeLength;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Time instant: ");
        if (isSetValue()) {
            result.append(getValue().toString()).append(Constants.COMMA_STRING);
        }
        result.append(getIndeterminateValue());
        return result.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Time o) {
        if (o instanceof TimeInstant) {
            final TimeInstant ti = (TimeInstant) o;
            if (isSetValue() && ti.isSetValue()) {
                if (getValue().isBefore(ti.getValue())) {
                    return -1;
                } else if (getValue().isAfter(ti.getValue())) {
                    return 1;
                }
            }
        } else if (o instanceof TimePeriod) {
            final TimePeriod tp = (TimePeriod) o;
            if (isSetValue() && tp.isSetStart() && tp.isSetEnd()) {
                if (getValue().isBefore(tp.getStart())) {
                    return -1;
                } else if (getValue().isAfter(tp.getEnd())) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object paramObject) {
        if (paramObject instanceof TimeInstant) {
            TimeInstant toCheck = (TimeInstant)paramObject;
            if (isSetValue() && toCheck.isSetValue()) {
                return getValue().isEqual(toCheck.getValue());
            } else if (isSetIndeterminateValue() && toCheck.isSetIndeterminateValue()) {
                return getIndeterminateValue().equals(toCheck.getIndeterminateValue());
            } else if (isSetSosIndeterminateTime() && toCheck.isSetSosIndeterminateTime()) {
                return getSosIndeterminateTime().equals(toCheck.getSosIndeterminateTime());
            } else if (isSetValue() && toCheck.isSetValue() && isSetIndeterminateValue() && toCheck.isSetIndeterminateValue()) {
                return getValue().isEqual(toCheck.getValue()) && getIndeterminateValue().equals(toCheck.getIndeterminateValue());
            } 
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + ((isSetValue()) ? getValue().hashCode() : 0);
        return hash;
    }

    /**
     * Check if time value is set
     * 
     * @return <tt>true</tt> if set
     */
    public boolean isSetValue() {
        return getValue() != null;
    }

    /**
     * Get indeterminate value
     * 
     * @return Returns the indeterminateValue.
     */
    public TimeIndeterminateValue getIndeterminateValue() {
        return indeterminateValue;
    }

    /**
     * Get the time position of this time instant
     * 
     * @return Time position
     */
    public TimePosition getTimePosition() {
        if (isSetIndeterminateValue()) {
            return new TimePosition(getIndeterminateValue());
        } else {
            return new TimePosition(getValue(), getTimeFormat());
        }
    }

    /**
     * Set indeterminate value
     * 
     * @param indeterminateValue
     *            The indeterminateValue to set.
     * @return this
     */
    public Time setIndeterminateValue(final TimeIndeterminateValue indeterminateValue) {
        this.indeterminateValue = indeterminateValue;
        return this;
    }

    /**
     * Check if indeterminate value is set
     * 
     * @return <tt>true</tt> if set
     */
    public boolean isSetIndeterminateValue() {
        return getIndeterminateValue() != null;
    }

    /**
     * Check whether this indeterminate value equals expected value
     * 
     * @param value
     *            Expected value
     * @return <code>true</code>, if this indeterminate value equals expected
     *         value
     */
    @SuppressWarnings("rawtypes")
    public boolean isIndeterminateValueEqualTo(Enum value) {
        return isSetIndeterminateValue() && getIndeterminateValue().equals(value);
    }

    /**
     * Get SOS indeterminate value
     * 
     * @return SOS indeterminate value
     */
    public SosConstants.SosIndeterminateTime getSosIndeterminateTime() {
        return sosIndeterminateTime;
    }

    /**
     * Set SOS indeterminate value
     * 
     * @param sosIndeterminateTime
     * @return This TimeInstant
     */
    public TimeInstant setSosIndeterminateTime(final SosConstants.SosIndeterminateTime sosIndeterminateTime) {
        this.sosIndeterminateTime = sosIndeterminateTime;
        return this;
    }

    /**
     * Check if SOS indeterminate value is set
     * 
     * @return <tt>true</tt> if set
     */
    public boolean isSetSosIndeterminateTime() {
        return getSosIndeterminateTime() != null;
    }

    /**
     * @return <tt>true</tt>, if value and indeterminateValue are NOT set
     * @see #isSetValue()
     * @see #isSetIndeterminateValue()
     */
    @Override
    public boolean isEmpty() {
        return !isSetValue() && !isSetIndeterminateValue() && !isSetSosIndeterminateTime() && super.isEmpty();
    }
}
