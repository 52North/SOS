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
package org.n52.sos.ds.hibernate.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.AfterRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.BeforeRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.BeginsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.BegunByRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.ContainsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.DuringRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.EndedByRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.EndsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.MeetsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.MetByRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.OverlappedByRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.OverlapsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.TEqualsRestriction;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.Time;

import com.google.common.collect.Maps;

/**
 * Factory methods to create {@link Criterion Criterions} for
 * {@link TemporalFilter TemporalFilters}.
 * 
 * @see AfterRestriction
 * @see BeforeRestriction
 * @see BeginsRestriction
 * @see BegunByRestriction
 * @see ContainsRestriction
 * @see DuringRestriction
 * @see EndedByRestriction
 * @see EndsRestriction
 * @see TEqualsRestriction
 * @see MeetsRestriction
 * @see MetByRestriction
 * @see OverlappedByRestriction
 * @see OverlapsRestriction
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class TemporalRestrictions {
    /**
     * Marker for a value reference referencing the phenomenon time ({@value} ).
     * 
     * @see #PHENOMENON_TIME_FIELDS
     */
    public static final String PHENOMENON_TIME_VALUE_REFERENCE = "phenomenonTime";

    /**
     * Marker for a value reference referencing the result time ({@value} ).
     * 
     * @see #RESULT_TIME_FIELDS
     */
    public static final String RESULT_TIME_VALUE_REFERENCE = "resultTime";

    /**
     * Marker for a value reference referencing the valid time ({@value} ).
     * 
     * @see #VALID_TIME_FIELDS
     */
    public static final String VALID_TIME_VALUE_REFERENCE = "validTime";

    /**
     * Marker for a value reference referencing the valid time ({@value} ).
     * 
     * @see #VALID_TIME_FIELDS
     */
    public static final String VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE = "validDescribeSensorTime";

    /**
     * Fields describing the phenomenon time of a <tt>Observation</tt>.
     * 
     * @see Observation#PHENOMENON_TIME_START
     * @see Observation#PHENOMENON_TIME_END
     */
    public static final TimePrimitiveFieldDescriptor PHENOMENON_TIME_FIELDS = new TimePrimitiveFieldDescriptor(
            Observation.PHENOMENON_TIME_START, Observation.PHENOMENON_TIME_END);

    /**
     * Fields describing the result time of a <tt>Observation</tt>.
     * 
     * @see Observation#RESULT_TIME
     */
    public static final TimePrimitiveFieldDescriptor RESULT_TIME_FIELDS = new TimePrimitiveFieldDescriptor(
            Observation.RESULT_TIME);

    /**
     * Fields describing the valid time of a <tt>Observation</tt>.
     * 
     * @see Observation#VALID_TIME_START
     * @see Observation#VALID_TIME_END
     */
    public static final TimePrimitiveFieldDescriptor VALID_TIME_FIELDS = new TimePrimitiveFieldDescriptor(
            Observation.VALID_TIME_START, Observation.VALID_TIME_END);

    /**
     * Fields describing the valid time of a <tt>ValidProcedureTime</tt>.
     * 
     * @see ValidProcedureTime#START_TIME
     * @see ValidProcedureTime#END_TIME
     */
    public static final TimePrimitiveFieldDescriptor VALID_TIME_DESCRIBE_SENSOR_FIELDS =
            new TimePrimitiveFieldDescriptor(ValidProcedureTime.START_TIME, ValidProcedureTime.END_TIME);

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see BeforeRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion before(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new BeforeRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see BeforeRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion before(String property, Time value) throws UnsupportedTimeException {
        return filter(new BeforeRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see BeforeRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion before(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new BeforeRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see AfterRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion after(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new AfterRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see AfterRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion after(String property, Time value) throws UnsupportedTimeException {
        return filter(new AfterRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>-
     * 
     * @see AfterRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion after(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new AfterRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see BeginsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begins(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new BeginsRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see BeginsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begins(String property, Time value) throws UnsupportedTimeException {
        return filter(new BeginsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see BeginsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begins(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new BeginsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see EndsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion ends(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new EndsRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see EndsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion ends(String property, Time value) throws UnsupportedTimeException {
        return filter(new EndsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see EndsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion ends(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new EndsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see EndedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion endedBy(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new EndedByRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see EndedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion endedBy(String property, Time value) throws UnsupportedTimeException {
        return filter(new EndedByRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see EndedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion endedBy(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new EndedByRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see BegunByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begunBy(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new BegunByRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see BegunByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begunBy(String property, Time value) throws UnsupportedTimeException {
        return filter(new BegunByRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see BegunByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begunBy(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new BegunByRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see DuringRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion during(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new DuringRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see DuringRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion during(String property, Time value) throws UnsupportedTimeException {
        return filter(new DuringRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see DuringRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion during(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new DuringRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see TEqualsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion tEquals(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new TEqualsRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see TEqualsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion tEquals(String property, Time value) throws UnsupportedTimeException {
        return filter(new TEqualsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see TEqualsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion tEquals(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new TEqualsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see ContainsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion contains(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new ContainsRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see ContainsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion contains(String property, Time value) throws UnsupportedTimeException {
        return filter(new ContainsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see ContainsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion contains(TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return filter(new ContainsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see OverlapsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlaps(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new OverlapsRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see OverlapsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlaps(String property, Time value) throws UnsupportedTimeException {
        return filter(new OverlapsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see OverlapsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlaps(TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return filter(new OverlapsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see MeetsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion meets(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new MeetsRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see MeetsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion meets(String property, Time value) throws UnsupportedTimeException {
        return filter(new MeetsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see MeetsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion meets(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new MeetsRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see MetByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion metBy(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new MetByRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see MetByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion metBy(String property, Time value) throws UnsupportedTimeException {
        return filter(new MetByRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see MetByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion metBy(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(new MetByRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param begin
     *            the begin property name
     * @param end
     *            the end property name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see OverlappedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlappedBy(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(new OverlappedByRestriction(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see OverlappedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlappedBy(String property, Time value) throws UnsupportedTimeException {
        return filter(new OverlappedByRestriction(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     * 
     * @param property
     *            the property
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @see OverlappedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlappedBy(TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return filter(new OverlappedByRestriction(), property, value);
    }

    /**
     * Create a new <tt>Criterion</tt> using the specified property, restricion
     * and value.
     * 
     * @param restriction
     *            the restriction
     * @param begin
     *            the begin property field name
     * @param end
     *            the end property field name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    private static Criterion filter(TemporalRestriction restriction, String begin, String end, Time value)
            throws UnsupportedTimeException {
        return filter(restriction, new TimePrimitiveFieldDescriptor(begin, end), value);
    }

    /**
     * Create a new <tt>Criterion</tt> using the specified property, restricion
     * and value.
     * 
     * @param restriction
     *            the restriction
     * @param property
     *            the property field name
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    private static Criterion filter(TemporalRestriction restriction, String property, Time value)
            throws UnsupportedTimeException {
        return filter(restriction, new TimePrimitiveFieldDescriptor(property), value);
    }

    /**
     * Create a new <tt>Criterion</tt> using the specified property, restricion
     * and value.
     * 
     * @param restriction
     *            the restriction
     * @param property
     *            the property field name(s)
     * @param value
     *            the value
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    private static Criterion filter(TemporalRestriction restriction, TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return restriction.get(property, value);
    }

    /**
     * Create a new <tt>Criterion</tt> using the specified filter.
     * 
     * @param filter
     *            the filter
     * 
     * @return the <tt>Criterion</tt>
     * 
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     * @throws UnsupportedValueReferenceException
     *             if the {@link TemporalFilter#getValueReference() value
     *             reference} can not be decoded
     * @throws UnsupportedOperatorException
     *             if no restriction definition for the {@link TimeOperator} is
     *             found
     */
    public static Criterion filter(TemporalFilter filter) throws UnsupportedTimeException,
            UnsupportedValueReferenceException, UnsupportedOperatorException {
        TimePrimitiveFieldDescriptor property = getFields(filter.getValueReference());
        Time value = filter.getTime();
        switch (filter.getOperator()) {
        case TM_Before:
            return before(property, value);
        case TM_After:
            return after(property, value);
        case TM_Begins:
            return begins(property, value);
        case TM_Ends:
            return ends(property, value);
        case TM_EndedBy:
            return endedBy(property, value);
        case TM_BegunBy:
            return begunBy(property, value);
        case TM_During:
            return during(property, value);
        case TM_Equals:
            return tEquals(property, value);
        case TM_Contains:
            return contains(property, value);
        case TM_Overlaps:
            return overlaps(property, value);
        case TM_Meets:
            return meets(property, value);
        case TM_MetBy:
            return metBy(property, value);
        case TM_OverlappedBy:
            return overlappedBy(property, value);
        default:
            throw new UnsupportedOperatorException(filter.getOperator());
        }
    }

    /**
     * Creates a {@link Conjunction} for the specified temporal filters.
     * 
     * @param temporalFilters
     *            the filters
     * 
     * @return Hibernate temporal filter criterion
     * 
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     * @throws UnsupportedValueReferenceException
     *             if the {@link TemporalFilter#getValueReference() value
     *             reference} can not be decoded
     * @throws UnsupportedOperatorException
     *             if no restriction definition for the {@link TimeOperator} is
     *             found
     */
    public static Criterion filter(Iterable<TemporalFilter> temporalFilters) throws UnsupportedTimeException,
            UnsupportedValueReferenceException, UnsupportedOperatorException {
        Conjunction conjunction = Restrictions.conjunction();
        Collection<Disjunction> disjunctions = getDisjunction(temporalFilters);
        if (disjunctions.size() == 1) {
            return disjunctions.iterator().next();
        }
        for (Disjunction disjunction : disjunctions) {
            conjunction.add(disjunction);
        }
        return conjunction;
    }

    /**
     * Creates {@link Disjunction}s for the specified temporal filters with the
     * same valueReference.
     * 
     * @param temporalFilters
     *            the filters
     * @return {@link Collection} of {@link Disjunction}
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     * @throws UnsupportedValueReferenceException
     *             if the {@link TemporalFilter#getValueReference() value
     *             reference} can not be decoded
     * @throws UnsupportedOperatorException
     *             if no restriction definition for the {@link TimeOperator} is
     *             found
     */
    private static Collection<Disjunction> getDisjunction(Iterable<TemporalFilter> temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Map<String, Disjunction> map = Maps.newHashMap();
        for (TemporalFilter temporalFilter : temporalFilters) {
            if (map.containsKey(temporalFilter.getValueReference())) {
                map.get(temporalFilter.getValueReference()).add(filter(temporalFilter));
            } else {
                Disjunction disjunction = Restrictions.disjunction();
                disjunction.add(filter(temporalFilter));
                map.put(temporalFilter.getValueReference(), disjunction);
            }
        }
        return (Collection<Disjunction>) map.values();
    }

    /**
     * Gets the field descriptor for the specified value reference.
     * 
     * @param valueReference
     *            the value reference
     * 
     * @return the property descriptor
     * 
     * @see #PHENOMENON_TIME_VALUE_REFERENCE
     * @see #RESULT_TIME_VALUE_REFERENCE
     * @see #VALID_TIME_VALUE_REFERENCE
     * @see #PHENOMENON_TIME_FIELDS
     * @see #RESULT_TIME_FIELDS
     * @see #VALID_TIME_FIELDS
     * 
     * @throws UnsupportedValueReferenceException
     *             if the <tt>valueReference</tt> can not be decoded
     */
    private static TimePrimitiveFieldDescriptor getFields(String valueReference)
            throws UnsupportedValueReferenceException {
        if (valueReference.contains(PHENOMENON_TIME_VALUE_REFERENCE)) {
            return PHENOMENON_TIME_FIELDS;
        } else if (valueReference.contains(RESULT_TIME_VALUE_REFERENCE)) {
            return RESULT_TIME_FIELDS;
        } else if (valueReference.contains(VALID_TIME_VALUE_REFERENCE)) {
            return VALID_TIME_FIELDS;
        } else if (valueReference.contains(VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE)) {
            return VALID_TIME_DESCRIBE_SENSOR_FIELDS;
        } else {
            throw new UnsupportedValueReferenceException(valueReference);
        }
    }

    /**
     * Private constructor due to static access.
     */
    private TemporalRestrictions() {
        // noop
    }
}
