/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException.TimeType;

/**
 * Implements the 13 temporal relationships identified by Allen as
 * {@link Criterion Criterions}. Specification can be found in:
 * <ul>
 * <li>ISO 19143:2009, Geographic information — Filter encoding</li>
 * <li>ISO 19108:2002, Geographic Information — Temporal schema, Section
 * 5.2.3.5</li>
 * </ul>
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @since 4.0.0
 */
public interface TemporalRestriction {

    String START = "start";

    String END = "end";

    String INSTANT = "instant";

    /**
     * Creates a criterion from this restriction for the specified fields and
     * time.
     *
     * @param ref
     *            the descriptor holding the property name(s)
     * @param time
     *            the compared time
     * @param count
     *            the count for placeholder, can be null
     * @return a {@code Criterion} that describes this restriction
     *
     * @throws UnsupportedTimeException
     *             if the supplied time can not be used with this restriction
     */
    default Criterion getCriterion(AbstractTimePrimitiveFieldDescriptor ref, Time time, Integer count)
            throws UnsupportedTimeException {
        if (time instanceof TimePeriod) {
            return filterWithPeriod((TimePeriod) time, ref, false, count);
        } else if (time instanceof TimeInstant) {
            return filterWithInstant((TimeInstant) time, ref, count);
        } else {
            throw new UnsupportedTimeException(time);
        }
    }

    default String getPlaceHolder(String placeHolder, Integer count) {
        return count != null ? ":" + placeHolder + count : ":" + placeHolder;
    }

    default String getStartPlaceHolder(Integer count) {
        return getPlaceHolder(START, count);
    }

    default String getEndPlaceHolder(Integer count) {
        return getPlaceHolder(END, count);
    }

    default String getInstantPlaceHolder(Integer count) {
        return getPlaceHolder(INSTANT, count);
    }

    /**
     * Applies this restriction to the specified time periods.
     *
     * @param selfBegin
     *            the property name of the begin time stamp
     * @param selfEnd
     *            the property name of the end time stamp
     * @param otherBegin
     *            the begin instance of the compared time period
     * @param otherEnd
     *            the end instance of the compared time period
     *
     * @return the criterion for the temporal relation (or {@code null} if not
     *         applicable)
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd)
            throws UnsupportedTimeException {
        throw new UnsupportedTimeException(new TimePeriod(otherBegin, otherEnd));
    }

    default Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Integer count)
            throws UnsupportedTimeException {
        throw new UnsupportedTimeException(TimeType.TimePeriod, TimeType.TimePeriod);
    }

    /**
     * Applies this restriction to the specified time instance and time period.
     *
     * @param selfPosition
     *            the property name of the instance
     * @param otherBegin
     *            the begin instance of the compared time period
     * @param otherEnd
     *            the end instance of the compared time period
     * @param isOtherPeriodFromReducedPrecisionInstant
     *            was the period interpreted from a reduced precision time? see
     *            DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition
     *
     * @return the criterion for the temporal relation (or {@code null} if not
     *         applicable)
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterInstantWithPeriod(String selfPosition, Date otherBegin, Date otherEnd,
            boolean isOtherPeriodFromReducedPrecisionInstant) throws UnsupportedTimeException {
        throw new UnsupportedTimeException(new TimePeriod(otherBegin, otherEnd));
    }

    default Criterion filterInstantWithPeriod(String selfPosition, String otherPosition, Integer count)
            throws UnsupportedTimeException {
        throw new UnsupportedTimeException(TimeType.TimeInstant, TimeType.TimePeriod);
    }

    default Criterion filterInstantWithPeriod(String position, String endPosition, Date begin, Date end,
            boolean periodFromReducedPrecisionInstant) throws UnsupportedTimeException {
        throw new UnsupportedTimeException(new TimePeriod(begin, end));
    }

    /**
     * Applies this restriction to the specified time period and time instance.
     *
     * @param selfBegin
     *            the property name of the begin time stamp
     * @param selfEnd
     *            the property name of the end time stamp
     * @param otherPosition
     *            the position of the compared time instance
     *
     * @return the criterion for the temporal relation (or {@code null} if not
     *         applicable)
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Date otherPosition)
            throws UnsupportedTimeException {
        throw new UnsupportedTimeException(new TimeInstant(otherPosition));
    }

    default Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Integer count)
            throws UnsupportedTimeException {
        throw new UnsupportedTimeException(TimeType.TimePeriod, TimeType.TimeInstant);
    }

    /**
     * Applies this restriction to the specified time instantes.
     *
     * @param selfPosition
     *            the property name of the time instance
     * @param otherPosition
     *            the position of the compared time instance
     *
     * @return the criterion for the temporal relation (or {@code null} if not
     *         applicable)
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterInstantWithInstant(String selfPosition, Date otherPosition)
            throws UnsupportedTimeException {
        throw new UnsupportedTimeException(new TimeInstant(otherPosition));
    }

    default Criterion filterInstantWithInstant(String selfPosition, String otherPosition, Integer count)
            throws UnsupportedTimeException {
        throw new UnsupportedTimeException(TimeType.TimeInstant, TimeType.TimeInstant);
    }

    /**
     * Create a filter for the specified period and fields that are nullable,
     * e.g. resultTime. If the period is no real period but a instance, the
     * method will call
     * {@link #filterWithInstant(TimeInstant, TimePrimitiveNullableFieldDescriptor)}.
     *
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Criterion} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterWithPeriod(TimePeriod time, TimePrimitiveNullableFieldDescriptor r,
            boolean periodFromReducedPrecisionInstant) throws UnsupportedTimeException {
        return Restrictions.disjunction(
                Restrictions.conjunction(Restrictions.isNotNull(r.getPosition()),
                        createFilterWithPeriod(time, (AbstractTimePrimitiveFieldDescriptor) r,
                                periodFromReducedPrecisionInstant)),
                Restrictions.conjunction(Restrictions.isNull(r.getPosition()),
                        createFilterWithPeriod(time, r.getAlternative(), periodFromReducedPrecisionInstant)));
    }

    /**
     * Create a filter for the specified period and fields. If the period is no
     * real period but a instance, the method will call
     * {@link #filterWithInstant(TimeInstant, TimePrimitiveFieldDescriptor)}.
     *
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Criterion} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterWithPeriod(TimePeriod time, TimePrimitiveFieldDescriptor r,
            boolean periodFromReducedPrecisionInstant) throws UnsupportedTimeException {
        return createFilterWithPeriod(time, r, periodFromReducedPrecisionInstant);
    }

    /**
     * Create a filter for the specified period and fields. If the period is no
     * real period but a instance, the method will call
     * {@link #filterWithInstant(TimeInstant, AbstractTimePrimitiveFieldDescriptor)}.
     *
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Criterion} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterWithPeriod(TimePeriod time, AbstractTimePrimitiveFieldDescriptor r,
            boolean periodFromReducedPrecisionInstant) throws UnsupportedTimeException {
        return r instanceof TimePrimitiveFieldDescriptor
                ? createFilterWithPeriod(time, r, periodFromReducedPrecisionInstant)
                : filterWithPeriod(time, (TimePrimitiveNullableFieldDescriptor) r, periodFromReducedPrecisionInstant);
    }

    default Criterion filterWithPeriod(TimePeriod time, TimePrimitiveNullableFieldDescriptor r,
            boolean periodFromReducedPrecisionInstant, Integer count) throws UnsupportedTimeException {
        return Restrictions.disjunction(
                Restrictions.conjunction(Restrictions.isNotNull(r.getPosition()),
                        createFilterWithPeriod(time, r, periodFromReducedPrecisionInstant, count)),
                Restrictions.conjunction(Restrictions.isNull(r.getPosition()),
                        createFilterWithPeriod(time, r.getAlternative(), periodFromReducedPrecisionInstant, count)));
    }

    default Criterion filterWithPeriod(TimePeriod time, TimePrimitiveFieldDescriptor r,
            boolean periodFromReducedPrecisionInstant, Integer count) throws UnsupportedTimeException {
        return createFilterWithPeriod(time, r, periodFromReducedPrecisionInstant, count);
    }

    default Criterion filterWithPeriod(TimePeriod time, AbstractTimePrimitiveFieldDescriptor r,
            boolean periodFromReducedPrecisionInstant, Integer count) throws UnsupportedTimeException {
        return r instanceof TimePrimitiveFieldDescriptor
                ? createFilterWithPeriod(time, r, periodFromReducedPrecisionInstant, count)
                : filterWithPeriod(time, (TimePrimitiveNullableFieldDescriptor) r, periodFromReducedPrecisionInstant,
                        count);
    }

    default Criterion createFilterWithPeriod(TimePeriod time, AbstractTimePrimitiveFieldDescriptor r,
            boolean periodFromReducedPrecisionInstant) throws UnsupportedTimeException {
        Date begin = time.resolveStart()
                .toDate();
        // FIXME should also incorporate reduced precision like
        // getRequestedTimeLength()
        // (Partially?) fixed with use of periodFromReducedPrecisionInstant?
        Date end = time.resolveEnd()
                .toDate();
        if (begin.equals(end)) {
            return filterWithInstant(new TimeInstant(time.resolveStart()), r);
        }
        if (r.isPeriod()) {
            return getPropertyCheckingCriterion(
                    filterPeriodWithPeriod(r.getBeginPosition(), r.getEndPosition(), begin, end), null, r);
        } else {
            return filterInstantWithPeriod(r.getPosition(), begin, end, periodFromReducedPrecisionInstant);
        }
    }

    default Criterion createFilterWithPeriod(TimePeriod time, AbstractTimePrimitiveFieldDescriptor r,
            boolean periodFromReducedPrecisionInstant, Integer count) throws UnsupportedTimeException {
        Date begin = time.resolveStart()
                .toDate();
        // FIXME should also incorporate reduced precision like
        // getRequestedTimeLength()
        // (Partially?) fixed with use of periodFromReducedPrecisionInstant?
        Date end = time.resolveEnd()
                .toDate();
        if (begin.equals(end)) {
            return filterWithInstant(new TimeInstant(time.resolveStart()), r);
        }
        if (r.isPeriod()) {
            return count != null
                    ? getPropertyCheckingCriterion(
                            filterPeriodWithPeriod(r.getBeginPosition(), r.getEndPosition(), count), null, r)
                    : getPropertyCheckingCriterion(
                            filterPeriodWithPeriod(r.getBeginPosition(), r.getEndPosition(), begin, end), null, r);
        } else {
            return count != null ? filterInstantWithPeriod(r.getBeginPosition(), r.getEndPosition(), count)
                    : filterInstantWithPeriod(r.getPosition(), begin, end, periodFromReducedPrecisionInstant);
        }

    }

    /**
     * Creates a filter for the specfied instant and fields that are nullable.
     * In case of a instance with reduced precision a the method will call
     * {@link #filterWithPeriod(TimePeriod, AbstractTimePrimitiveFieldDescriptor, boolean)}.
     *
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Criterion} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterWithInstant(TimeInstant time, TimePrimitiveNullableFieldDescriptor r)
            throws UnsupportedTimeException {
        /*
         * Saved primitives can be periods, but can also be instants. As begin
         * &lt; end has to be true for all periods those are instants and have
         * to be treated as such. Also instants with reduced precision are
         * semantically periods and have to be handled like periods.
         */
        return Restrictions.disjunction(
                Restrictions.conjunction(Restrictions.isNotNull(r.getPosition()),
                        createFilterWithInstant(time, (AbstractTimePrimitiveFieldDescriptor) r)),
                Restrictions.conjunction(Restrictions.isNull(r.getPosition()),
                        createFilterWithInstant(time, r.getAlternative())));
    }

    /**
     * Creates a filter for the specfied instant and fields. In case of a
     * instance with reduced precision a the method will call
     * {@link #filterWithPeriod(TimePeriod, TimePrimitiveFieldDescriptor, boolean)}.
     *
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Criterion} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterWithInstant(TimeInstant time, TimePrimitiveFieldDescriptor r)
            throws UnsupportedTimeException {
        return createFilterWithInstant(time, r);
    }

    /**
     * Creates a filter for the specfied instant and fields. In case of a
     * instance with reduced precision a the method will call
     * {@link #filterWithPeriod(TimePeriod, AbstractTimePrimitiveFieldDescriptor, boolean)}.
     *
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Criterion} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Criterion filterWithInstant(TimeInstant time, AbstractTimePrimitiveFieldDescriptor r)
            throws UnsupportedTimeException {
        return r instanceof TimePrimitiveFieldDescriptor ? createFilterWithInstant(time, r)
                : filterWithInstant(time, (TimePrimitiveNullableFieldDescriptor) r);
    }

    default Criterion filterWithInstant(TimeInstant time, TimePrimitiveNullableFieldDescriptor r, Integer count)
            throws UnsupportedTimeException {
        /*
         * Saved primitives can be periods, but can also be instants. As begin
         * &lt; end has to be true for all periods those are instants and have
         * to be treated as such. Also instants with reduced precision are
         * semantically periods and have to be handled like periods.
         */
        return Restrictions.disjunction(
                Restrictions.conjunction(Restrictions.isNotNull(r.getPosition()),
                        createFilterWithInstant(time, (AbstractTimePrimitiveFieldDescriptor) r, count)),
                Restrictions.conjunction(Restrictions.isNull(r.getPosition()),
                        createFilterWithInstant(time, r.getAlternative(), count)));
    }

    default Criterion filterWithInstant(TimeInstant time, TimePrimitiveFieldDescriptor r, Integer count)
            throws UnsupportedTimeException {
        return createFilterWithInstant(time, r, count);
    }

    default Criterion filterWithInstant(TimeInstant time, AbstractTimePrimitiveFieldDescriptor r, Integer count)
            throws UnsupportedTimeException {
        return r instanceof TimePrimitiveFieldDescriptor ? createFilterWithInstant(time, r, count)
                : filterWithInstant(time, (TimePrimitiveNullableFieldDescriptor) r, count);
    }

    default Criterion createFilterWithInstant(TimeInstant time, AbstractTimePrimitiveFieldDescriptor r)
            throws UnsupportedTimeException {
        /*
         * Saved primitives can be periods, but can also be instants. As begin
         * &lt; end has to be true for all periods those are instants and have
         * to be treated as such. Also instants with reduced precision are
         * semantically periods and have to be handled like periods.
         */
        Date begin = time.resolveValue()
                .toDate();
        Date end = checkInstantWithReducedPrecision(time);
        if (end != null) {
            return filterWithPeriod(new TimePeriod(new DateTime(begin), new DateTime(end)), r, true);
        }
        if (r.isPeriod()) {
            return getPropertyCheckingCriterion(
                    filterPeriodWithInstant(r.getBeginPosition(), r.getEndPosition(), begin), null, r);

        } else {
            return filterInstantWithInstant(r.getPosition(), begin);
        }
    }

    default Criterion createFilterWithInstant(TimeInstant time, AbstractTimePrimitiveFieldDescriptor r, Integer count)
            throws UnsupportedTimeException {
        /*
         * Saved primitives can be periods, but can also be instants. As begin
         * &lt; end has to be true for all periods those are instants and have
         * to be treated as such. Also instants with reduced precision are
         * semantically periods and have to be handled like periods.
         */
        Date begin = time.resolveValue()
                .toDate();
        Date end = checkInstantWithReducedPrecision(time);
        if (end != null) {
            return filterWithPeriod(new TimePeriod(new DateTime(begin), new DateTime(end)), r, true, count);
        }
        if (r.isPeriod()) {
            return count != null
                    ? getPropertyCheckingCriterion(
                            filterPeriodWithInstant(r.getBeginPosition(), r.getEndPosition(), count), null, r)
                    : getPropertyCheckingCriterion(
                            filterPeriodWithInstant(r.getBeginPosition(), r.getEndPosition(), time.getValue()
                                    .toDate()),
                            null, r);

        } else {
            return count != null ? filterInstantWithInstant(r.getPosition(), r.getBeginPosition(), count)
                    : filterInstantWithInstant(r.getPosition(), begin);
        }
    }

    /**
     * Check if {@code time} is a instance with reduces precision that describes
     * a period (a day, a hour, etc.).
     *
     * @param time
     *            the instant to check
     *
     * @return the end date of the period the instance with reduced precision
     *         started or {@code null} if there is no reduced precision
     */
    static Date checkInstantWithReducedPrecision(TimeInstant time) {
        DateTime end = DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition(time.getValue(),
                time.getRequestedTimeLength());
        return time.getValue()
                .equals(end) ? null : end.toDate();
    }

    /**
     * Creates a {@code Criterion} that takes care of instants that are saved as
     * periods ({@code begin == end}). The method builds a composite that
     * applies {@code periods} to "real" periods and {@code instants} to periods
     * that are instants by definition.
     *
     * @param periods
     *            the {@code Criterion} for "real" periods (may be {@code null})
     * @param instants
     *            the {@code Criterion} for periods with equal begin and end
     *            (may be {@code null})
     * @param r
     *            the {@code TimePrimitiveFieldDescriptor} that holds the
     *            property names
     *
     * @return the composite criterion or {@code null} if no {@code Criterion}
     *         could be applied
     */
    static Criterion getPropertyCheckingCriterion(Criterion periods, Criterion instants,
            AbstractTimePrimitiveFieldDescriptor r) {
        if (periods != null && instants != null) {
            return Restrictions.or(periods, instants);
        } else if (periods != null && instants == null) {
            return periods;
        } else if (periods == null && instants != null) {
            return periods;
        }
        return null;
    }

}
