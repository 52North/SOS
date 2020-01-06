/*
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
package org.n52.sos.ds.hibernate.util;

import org.hibernate.criterion.Criterion;

import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.sos.ds.hibernate.util.restriction.AfterRestriction;
import org.n52.sos.ds.hibernate.util.restriction.BeforeRestriction;
import org.n52.sos.ds.hibernate.util.restriction.BeginsRestriction;
import org.n52.sos.ds.hibernate.util.restriction.BegunByRestriction;
import org.n52.sos.ds.hibernate.util.restriction.ContainsRestriction;
import org.n52.sos.ds.hibernate.util.restriction.DuringRestriction;
import org.n52.sos.ds.hibernate.util.restriction.EndedByRestriction;
import org.n52.sos.ds.hibernate.util.restriction.EndsRestriction;
import org.n52.sos.ds.hibernate.util.restriction.EqualsRestriction;
import org.n52.sos.ds.hibernate.util.restriction.MeetsRestriction;
import org.n52.sos.ds.hibernate.util.restriction.MetByRestriction;
import org.n52.sos.ds.hibernate.util.restriction.OverlappedByRestriction;
import org.n52.sos.ds.hibernate.util.restriction.OverlapsRestriction;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public final class TemporalRestrictions {

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

    private static final TemporalRestriction AFTER = new AfterRestriction();

    private static final TemporalRestriction BEFORE = new BeforeRestriction();

    private static final TemporalRestriction BEGINS = new BeginsRestriction();

    private static final TemporalRestriction BEGUN_BY = new BegunByRestriction();

    private static final TemporalRestriction CONTAINS = new ContainsRestriction();

    private static final TemporalRestriction DURING = new DuringRestriction();

    private static final TemporalRestriction ENDED_BY = new EndedByRestriction();

    private static final TemporalRestriction ENDS = new EndsRestriction();

    private static final TemporalRestriction MEETS = new MeetsRestriction();

    private static final TemporalRestriction MET_BY = new MetByRestriction();

    private static final TemporalRestriction OVERLAPPED_BY = new OverlappedByRestriction();

    private static final TemporalRestriction OVERLAPS = new OverlapsRestriction();

    private static final TemporalRestriction EQUALS = new EqualsRestriction();

    private TemporalRestrictions() {
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.end &lt; other.begin}</td>
     * <td>{@code self.end &lt; other.position}</td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td>{@code self.position &lt; other.begin}</td>
     * <td>{@code self.position &lt; other.position}</td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction before() {
        return BEFORE;
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
     * @return the {@code Criterion}
     *
     * @see BeforeRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion before(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.before(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see BeforeRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion before(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.before(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see BeforeRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion before(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.before(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.begin &gt; other.end}</td>
     * <td>{@code self.begin &gt; other.position}</td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td>{@code self.position &gt; other.end}</td>
     * <td>{@code self.position &gt; other.position}</td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction after() {
        return AFTER;
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
     * @return the {@code Criterion}
     *
     * @see AfterRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion after(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.after(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see AfterRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion after(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.after(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}-
     *
     * @see AfterRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion after(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.after(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.begin = other.begin AND self.end &lt; other.end}</td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td>{@code self.position = other.begin}</td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction begins() {
        return BEGINS;
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
     * @return the {@code Criterion}
     *
     * @see BeginsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begins(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.begins(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see BeginsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begins(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.begins(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see BeginsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begins(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.begins(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.begin &gt; other.begin AND self.end = other.end}</td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td>{@code self.position = other.end}</td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction ends() {
        return ENDS;
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
     * @return the {@code Criterion}
     *
     * @see EndsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion ends(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.ends(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see EndsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion ends(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.ends(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see EndsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion ends(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.ends(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.begin &lt; other.begin AND self.end = other.end}</td>
     * <td>{@code self.end = other.position}</td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction endedBy() {
        return ENDED_BY;
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
     * @return the {@code Criterion}
     *
     * @see EndedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion endedBy(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.endedBy(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see EndedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion endedBy(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.endedBy(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see EndedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion endedBy(TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return filter(TemporalRestrictions.endedBy(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.begin = other.begin AND self.end &gt; other.end}</td>
     * <td>{@code self.begin = other.position}</td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction begunBy() {
        return BEGUN_BY;
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
     * @return the {@code Criterion}
     *
     * @see BegunByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begunBy(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.begunBy(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see BegunByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begunBy(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.begunBy(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see BegunByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion begunBy(TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return filter(TemporalRestrictions.begunBy(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.begin &gt; other.begin AND self.end &lt; other.end}</td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td>{@code self.position &gt; other.begin AND self.position &lt; other.end}</td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction during() {
        return DURING;
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
     * @return the {@code Criterion}
     *
     * @see DuringRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion during(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.during(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see DuringRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion during(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.during(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see DuringRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion during(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.during(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.begin = other.begin AND self.end = other.end}</td>
     * <td><i>if period is from a reduced precision instant, self.begin &ge;
     * other.begin and self.end &le; other.end, otherwise not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td>{@code self.position = other.position}</td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction equals() {
        return EQUALS;
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
     * @return the {@code Criterion}
     *
     * @see EqualsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion equals(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.equals(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see EqualsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion equals(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.equals(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see EqualsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion equals(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.equals(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.begin &lt; other.begin AND self.end &gt; other.end}</td>
     * <td>
     * {@code self.begin &lt; other.position AND self.end &gt; other.position}</td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction contains() {
        return CONTAINS;
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
     * @return the {@code Criterion}
     *
     * @see ContainsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion contains(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.contains(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see ContainsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion contains(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.contains(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see ContainsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion contains(TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return filter(TemporalRestrictions.contains(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>
     * {@code self.begin &lt; other.begin AND self.end &gt; other.begin AND self.end &lt; other.end}
     * </td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction overlaps() {
        return OVERLAPS;
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
     * @return the {@code Criterion}
     *
     * @see OverlapsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlaps(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.overlaps(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see OverlapsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlaps(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.overlaps(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see OverlapsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlaps(TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return filter(TemporalRestrictions.overlaps(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.end = other.begin}</td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction meets() {
        return MEETS;
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
     * @return the {@code Criterion}
     *
     * @see MeetsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion meets(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.meets(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see MeetsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion meets(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.meets(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see MeetsRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion meets(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.meets(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>{@code self.begin = other.end}</td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction metBy() {
        return MET_BY;
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
     * @return the {@code Criterion}
     *
     * @see MetByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion metBy(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.metBy(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see MetByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion metBy(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.metBy(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see MetByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion metBy(TimePrimitiveFieldDescriptor property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.metBy(), property, value);
    }

    /**
     * Creates filters according to the following table.
     * <table>
     * <tr>
     * <td><i>Self/Other</i></td>
     * <td><b>Period</b></td>
     * <td><b>Instant</b></td>
     * </tr>
     * <tr>
     * <td><b>Period</b></td>
     * <td>
     * {@code self.begin &gt; other.begin AND self.begin &lt; other.end AND self.end &gt; other.end}
     * </td>
     * <td><i>not defined</i></td>
     * </tr>
     * <tr>
     * <td><b>Instant</b></td>
     * <td><i>not defined</i></td>
     * <td><i>not defined</i></td>
     * </tr>
     * </table>
     *
     * @return the filter
     */
    static TemporalRestriction overlappedBy() {
        return OVERLAPPED_BY;
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
     * @return the {@code Criterion}
     *
     * @see OverlappedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlappedBy(String begin, String end, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.overlappedBy(), begin, end, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see OverlappedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlappedBy(String property, Time value) throws UnsupportedTimeException {
        return filter(TemporalRestrictions.overlappedBy(), property, value);
    }

    /**
     * Creates a temporal restriction for the specified time and property.
     *
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @see OverlappedByRestriction
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion overlappedBy(TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return filter(TemporalRestrictions.overlappedBy(), property, value);
    }

    /**
     * Create a new {@code Criterion} using the specified property, restricion
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
     * @return the {@code Criterion}
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
     * Create a new {@code Criterion} using the specified property, restricion
     * and value.
     *
     * @param restriction
     *            the restriction
     * @param property
     *            the property field name
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
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
     * Create a new {@code Criterion} using the specified property, restricion
     * and value.
     *
     * @param restriction
     *            the restriction
     * @param property
     *            the property field name(s)
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    private static Criterion filter(TemporalRestriction restriction, TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedTimeException {
        return filter(restriction, property, value, null);
    }

    private static Criterion filter(TemporalRestriction restriction, TimePrimitiveFieldDescriptor property, Time value,
            Integer count) throws UnsupportedTimeException {
        Criterion c = restriction.getCriterion(property, value, count);
        if (c != null) {
            return c;
        }
        throw new UnsupportedTimeException(value);
    }

    /**
     * Create a new {@code Criterion} using the specified operator, fields and
     * value
     *
     * @param operator
     *            the operator
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @throws UnsupportedOperatorException
     *             if no restriction definition for the {@link TimeOperator} is
     *             found
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion filter(TimeOperator operator, String property, Time value)
            throws UnsupportedOperatorException, UnsupportedTimeException {
        return filter(operator, new TimePrimitiveFieldDescriptor(property), value);
    }

    public static Criterion filter(TimeOperator operator, String property, Time value, Integer count)
            throws UnsupportedOperatorException, UnsupportedTimeException {
        return filter(operator, new TimePrimitiveFieldDescriptor(property), value, count);
    }

    /**
     * Create a new {@code Criterion} using the specified operator, fields and
     * value
     *
     * @param operator
     *            the operator
     * @param property
     *            the property
     * @param value
     *            the value
     *
     * @return the {@code Criterion}
     *
     * @throws UnsupportedOperatorException
     *             if no restriction definition for the {@link TimeOperator} is
     *             found
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Criterion filter(TimeOperator operator, TimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedOperatorException, UnsupportedTimeException {
        return filter(forOperator(operator), property, value, null);
    }

    public static Criterion filter(TimeOperator operator, TimePrimitiveFieldDescriptor property, Time value,
            Integer count) throws UnsupportedOperatorException, UnsupportedTimeException {
        return filter(forOperator(operator), property, value, count);
    }

    private static TemporalRestriction forOperator(TimeOperator operator) throws UnsupportedOperatorException {
        switch (operator) {
            case TM_Before:
                return before();
            case TM_After:
                return after();
            case TM_Begins:
                return begins();
            case TM_Ends:
                return ends();
            case TM_EndedBy:
                return endedBy();
            case TM_BegunBy:
                return begunBy();
            case TM_During:
                return during();
            case TM_Equals:
                return equals();
            case TM_Contains:
                return contains();
            case TM_Overlaps:
                return overlaps();
            case TM_Meets:
                return meets();
            case TM_MetBy:
                return metBy();
            case TM_OverlappedBy:
                return overlappedBy();
            default:
                throw new UnsupportedOperatorException(operator);
        }
    }
}
