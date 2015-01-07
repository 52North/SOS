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

import java.io.Serializable;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Sets;

/**
 * Abstract class for time objects
 * 
 * @since 4.0.0
 * 
 */
public abstract class Time implements Comparable<Time>, Serializable {

    /**
     * serial numbeer
     */
    private static final long serialVersionUID = 1366100818431254519L;

    /**
     * GML id
     */
    private String gmlId;

    /**
     * Time format
     */
    private TimeFormat timeFormat = TimeFormat.NOT_SET;

    /**
     * Reference
     */
    private String reference;

    /**
     * nil reason
     */
    private NilReason nilReason;

    /**
     * default constructor
     */
    public Time() {
        this(null);
    }

    /**
     * constructor
     * 
     * @param gmlId
     *            GML id
     */
    public Time(String gmlId) {
        this.gmlId = gmlId;
    }

    /**
     * Set GML id
     * 
     * @param gmlId
     *            Id to set
     */
    public void setGmlId(String gmlId) {
        this.gmlId = gmlId;
    }

    /**
     * Get GML id. If not null, first {@link Constants#NUMBER_SIGN_STRING}
     * (document reference indicator) is removed
     * 
     * @return GML id
     */
    public String getGmlId() {
        if (this.gmlId != null) {
            return this.gmlId.replaceFirst(Constants.NUMBER_SIGN_STRING, Constants.EMPTY_STRING);
        }
        return this.gmlId;
    }

    /**
     * Check whether GML id is set
     * 
     * @return <code>true</code>, if GML id is set
     */
    public boolean isSetGmlId() {
        return getGmlId() != null && !getGmlId().isEmpty();
    }

    /**
     * Check whether GML id contains document reference indicator
     * 
     * @return <code>true</code>, if GML id contains document reference
     *         indicator
     */
    public boolean isReferenced() {
        return isSetGmlId() && this.gmlId.startsWith(Constants.NUMBER_SIGN_STRING);
    }

    /**
     * Get time format
     * 
     * @return Time format
     */
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    /**
     * Set time format
     * 
     * @param timeFormat
     *            Time format to set
     */
    public void setTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
     * Check whether time format is set
     * 
     * @return <code>true</code>, if time format is set
     */
    public boolean isSetTimeFormat() {
        return getTimeFormat() != null;
    }

    /**
     * Get reference
     * 
     * @return Reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Set reference
     * 
     * @param reference
     *            Reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Check reference is set
     * 
     * @return <code>true</code>, if reference is set
     */
    public boolean isSetReference() {
        return StringHelper.isNotEmpty(getReference());
    }

    /**
     * Get the nil reason
     * 
     * @return Nil reason
     */
    public NilReason getNilReason() {
        return nilReason;
    }

    /**
     * Set nil reason
     * 
     * @param nilReason
     *            Nil reason to set
     */
    public void setNilReason(NilReason nilReason) {
        this.nilReason = nilReason;
    }

    /**
     * Check if nil reason is set
     * 
     * @return <code>true</code>, if nil reason is set
     */
    public boolean isSetNilReason() {
        return getNilReason() != null;
    }

    /**
     * Check if set nil reason equals to
     * 
     * @param value
     *            whose it shall comply with
     * @return <code>true</code>, if nil reason equals queried
     */
    @SuppressWarnings("rawtypes")
    public boolean isNilReasonEqualTo(Enum value) {
        return isSetNilReason() && getNilReason().equals(value);
    }

    /**
     * Resolve date time from dateTime or from indertminateValue
     * 
     * @param dateTime
     *            DateTime to check
     * @param indeterminateValue
     *            IndeterminateValue to check
     * @return Passed DateTime or current time
     *         {@link TimeIndeterminateValue#now}
     */
    protected DateTime resolveDateTime(DateTime dateTime, TimeIndeterminateValue indeterminateValue) {
        if (dateTime != null) {
            return dateTime;
        }
        if (indeterminateValue != null & TimeIndeterminateValue.now.equals(indeterminateValue)) {
            return new DateTime(DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Check if time is empty
     * 
     * @return <code>true</code> if not set nil reason and not set reference
     */
    public boolean isEmpty() {
        return !isSetNilReason() && !isSetReference();
    }

    /**
     * Enum for time formats <br>
     * 
     * {@link TimeFormat#ISO8601} - full ISO 8601 format <br>
     * {@link TimeFormat#Y} - only year <br>
     * {@link TimeFormat#YM} - year, month <br>
     * {@link TimeFormat#YMD} - year, month, day <br>
     * {v #NOT_SET} - not defined
     * 
     * @since 4.0.0
     * 
     */
    public enum TimeFormat {
        ISO8601, YMD, YM, Y, NOT_SET;
        
        public static final Collection<TimeFormat> SUPPORTED_FORAMTS = Sets.newHashSet(ISO8601, YMD, YM, Y);
    }

    /**
     * Enum for intederminate time values <br>
     * 
     * {@link TimeIndeterminateValue#after} - after the set time position <br>
     * {@link TimeIndeterminateValue#before} - before the set time position <br>
     * {@link TimeIndeterminateValue#now} - current time <br>
     * {@link TimeIndeterminateValue#unknown} - unknown time <br>
     * {@link TimeIndeterminateValue#template} - template, e.g. result handling
     * 
     * @since 4.0.0
     * 
     */
    public enum TimeIndeterminateValue {
        after, before, now, unknown, template;

        public static boolean contains(final String timeString) {
            return getEnumForString(timeString) != null;
        }

        public static TimeIndeterminateValue getEnumForString(final String value) {
            for (TimeIndeterminateValue tiv : values()) {
                if (tiv.name().equalsIgnoreCase(value)) {
                    return tiv;
                }
            }
            return null;
        }

    }

    /**
     * Enum for relative positions <br>
     * 
     * {@link RelativePosition#Before} - Before <br>
     * {@link RelativePosition#After} - After <br>
     * {@link RelativePosition#Begins} - Begins <br>
     * {@link RelativePosition#Ends} - Ends <br>
     * {@link RelativePosition#During} - During <br>
     * {@link RelativePosition#Equals} - Equals <br>
     * {@link RelativePosition#Contains} - Contains <br>
     * {@link RelativePosition#Overlaps} - Overlaps <br>
     * {@link RelativePosition#Meets} - Meets <br>
     * {@link RelativePosition#OverlappedBy} - OverlappedBy <br>
     * {@link RelativePosition#MetBy} - MetBy <br>
     * {@link RelativePosition#BegunBy} - BegunBy <br>
     * {@link RelativePosition#EndedB} - EndedB <br>
     * 
     * @since 4.0.0
     * 
     */
    public enum RelativePosition {
        Before, After, Begins, Ends, During, Equals, Contains, Overlaps, Meets, OverlappedBy, MetBy, BegunBy, EndedB
    }

    /**
     * Enum for nil reasons <br>
     * 
     * {@link NilReason#template} - template, e.g. result handling
     * 
     * @since 4.0.0
     * 
     */
    public enum NilReason {
        template;

        public static boolean contains(final String nilReason) {
            return nilReason.equalsIgnoreCase(template.name());
        }

        public static NilReason getEnumForString(final String value) {
            if (value.equalsIgnoreCase(template.name())) {
                return template;
            }
            return null;
        }
    }
}
