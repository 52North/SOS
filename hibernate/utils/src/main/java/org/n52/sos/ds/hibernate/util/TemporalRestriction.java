/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import org.joda.time.DateTime;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;

/**
 * Implements the 13 temporal relationships identified by Allen as
 * {@link Predicate Predicates}. Specification can be found in:
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

    /**
     * Creates a predicate from this restriction for the specified fields and
     * time.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param ref
     *            the descriptor holding the property name(s)
     * @param time
     *            the compared time
     * @return a {@code Predicate} that describes this restriction
     *
     * @throws UnsupportedTimeException
     *             if the supplied time can not be used with this restriction
     */
    default Predicate getPredicate(CriteriaBuilder cb, Path<?> root, AbstractTimePrimitiveFieldDescriptor ref,
            Time time) throws UnsupportedTimeException {
        if (time instanceof TimePeriod period) {
            return filterWithPeriod(cb, root, period, ref, false);
        } else if (time instanceof TimeInstant instant) {
            return filterWithInstant(cb, root, instant, ref);
        } else {
            throw new UnsupportedTimeException(time);
        }
    }

    /**
     * Applies this restriction to the specified time periods.
     *
     * @param cb
     *            the criteria builder
     * @param selfBegin
     *            the path of the begin time stamp
     * @param selfEnd
     *            the path of the end time stamp
     * @param otherBegin
     *            the begin instance of the compared time period
     * @param otherEnd
     *            the end instance of the compared time period
     *
     * @return the predicate for the temporal relation (or {@code null} if not
     *         applicable)
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterPeriodWithPeriod(CriteriaBuilder cb, Path<Date> selfBegin, Path<Date> selfEnd,
            Date otherBegin, Date otherEnd) throws UnsupportedTimeException {
        throw new UnsupportedTimeException(new TimePeriod(otherBegin, otherEnd));
    }

    /**
     * Applies this restriction to the specified time instance and time period.
     *
     * @param cb
     *            the criteria builder
     * @param selfPosition
     *            the path of the instance
     * @param otherBegin
     *            the begin instance of the compared time period
     * @param otherEnd
     *            the end instance of the compared time period
     * @param isOtherPeriodFromReducedPrecisionInstant
     *            was the period interpreted from a reduced precision time? see
     *            DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition
     *
     * @return the predicate for the temporal relation (or {@code null} if not
     *         applicable)
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterInstantWithPeriod(CriteriaBuilder cb, Path<Date> selfPosition, Date otherBegin,
            Date otherEnd, boolean isOtherPeriodFromReducedPrecisionInstant) throws UnsupportedTimeException {
        throw new UnsupportedTimeException(new TimePeriod(otherBegin, otherEnd));
    }

    /**
     * Applies this restriction to the specified time period and time instance.
     *
     * @param cb
     *            the criteria builder
     * @param selfBegin
     *            the path of the begin time stamp
     * @param selfEnd
     *            the path of the end time stamp
     * @param otherPosition
     *            the position of the compared time instance
     *
     * @return the predicate for the temporal relation (or {@code null} if not
     *         applicable)
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterPeriodWithInstant(CriteriaBuilder cb, Path<Date> selfBegin, Path<Date> selfEnd,
            Date otherPosition) throws UnsupportedTimeException {
        throw new UnsupportedTimeException(new TimeInstant(otherPosition));
    }

    /**
     * Applies this restriction to the specified time instantes.
     *
     * @param cb
     *            the criteria builder
     * @param selfPosition
     *            the path of the time instance
     * @param otherPosition
     *            the position of the compared time instance
     *
     * @return the predicate for the temporal relation (or {@code null} if not
     *         applicable)
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterInstantWithInstant(CriteriaBuilder cb, Path<Date> selfPosition, Date otherPosition)
            throws UnsupportedTimeException {
        throw new UnsupportedTimeException(new TimeInstant(otherPosition));
    }

    /**
     * Create a filter for the specified period and fields that are nullable,
     * e.g. resultTime. If the period is no real period but a instance, the
     * method will call
     * {@link #filterWithInstant(CriteriaBuilder, Path, TimeInstant, TimePrimitiveNullableFieldDescriptor)}.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Predicate} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterWithPeriod(CriteriaBuilder cb, Path<?> root, TimePeriod time,
            TimePrimitiveNullableFieldDescriptor r, boolean periodFromReducedPrecisionInstant)
            throws UnsupportedTimeException {
        Path<Date> position = root.get(r.getPosition());
        return cb.or(
                cb.and(cb.isNotNull(position), createFilterWithPeriod(cb, root, time,
                        (AbstractTimePrimitiveFieldDescriptor) r, periodFromReducedPrecisionInstant)),
                cb.and(cb.isNull(position), createFilterWithPeriod(cb, root, time, r.getAlternative(),
                        periodFromReducedPrecisionInstant)));
    }

    /**
     * Create a filter for the specified period and fields. If the period is no
     * real period but a instance, the method will call
     * {@link #filterWithInstant(CriteriaBuilder, Path, TimeInstant, TimePrimitiveFieldDescriptor)}.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Predicate} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterWithPeriod(CriteriaBuilder cb, Path<?> root, TimePeriod time,
            TimePrimitiveFieldDescriptor r, boolean periodFromReducedPrecisionInstant)
            throws UnsupportedTimeException {
        return createFilterWithPeriod(cb, root, time, r, periodFromReducedPrecisionInstant);
    }

    /**
     * Create a filter for the specified period and fields. If the period is no
     * real period but a instance, the method will call
     * {@link #filterWithInstant(CriteriaBuilder, Path, TimeInstant, AbstractTimePrimitiveFieldDescriptor)}.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Predicate} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterWithPeriod(CriteriaBuilder cb, Path<?> root, TimePeriod time,
            AbstractTimePrimitiveFieldDescriptor r, boolean periodFromReducedPrecisionInstant)
            throws UnsupportedTimeException {
        return r instanceof TimePrimitiveFieldDescriptor
                ? createFilterWithPeriod(cb, root, time, r, periodFromReducedPrecisionInstant)
                : filterWithPeriod(cb, root, time, (TimePrimitiveNullableFieldDescriptor) r,
                        periodFromReducedPrecisionInstant);
    }

    default Predicate createFilterWithPeriod(CriteriaBuilder cb, Path<?> root, TimePeriod time,
            AbstractTimePrimitiveFieldDescriptor r, boolean periodFromReducedPrecisionInstant)
            throws UnsupportedTimeException {
        Date begin = time.resolveStart()
                .toDate();
        // FIXME should also incorporate reduced precision like
        // getRequestedTimeLength()
        // (Partially?) fixed with use of periodFromReducedPrecisionInstant?
        Date end = time.resolveEnd()
                .toDate();
        if (begin.equals(end)) {
            return filterWithInstant(cb, root, new TimeInstant(time.resolveStart()), r);
        }
        if (r.isPeriod()) {
            return filterPeriodWithPeriod(cb, root.get(r.getBeginPosition()), root.get(r.getEndPosition()), begin,
                    end);
        } else {
            return filterInstantWithPeriod(cb, root.get(r.getPosition()), begin, end,
                    periodFromReducedPrecisionInstant);
        }
    }

    /**
     * Creates a filter for the specfied instant and fields that are nullable.
     * In case of a instance with reduced precision a the method will call
     * {@link #filterWithPeriod(CriteriaBuilder, Path, TimePeriod, AbstractTimePrimitiveFieldDescriptor, boolean)}.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Predicate} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterWithInstant(CriteriaBuilder cb, Path<?> root, TimeInstant time,
            TimePrimitiveNullableFieldDescriptor r) throws UnsupportedTimeException {
        /*
         * Saved primitives can be periods, but can also be instants. As begin
         * &lt; end has to be true for all periods those are instants and have
         * to be treated as such. Also instants with reduced precision are
         * semantically periods and have to be handled like periods.
         */
        Path<Date> position = root.get(r.getPosition());
        return cb.or(
                cb.and(cb.isNotNull(position),
                        createFilterWithInstant(cb, root, time, (AbstractTimePrimitiveFieldDescriptor) r)),
                cb.and(cb.isNull(position), createFilterWithInstant(cb, root, time, r.getAlternative())));
    }

    /**
     * Creates a filter for the specfied instant and fields. In case of a
     * instance with reduced precision a the method will call
     * {@link #filterWithPeriod(CriteriaBuilder, Path, TimePeriod, TimePrimitiveFieldDescriptor, boolean)}.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Predicate} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterWithInstant(CriteriaBuilder cb, Path<?> root, TimeInstant time,
            TimePrimitiveFieldDescriptor r) throws UnsupportedTimeException {
        return createFilterWithInstant(cb, root, time, r);
    }

    /**
     * Creates a filter for the specfied instant and fields. In case of a
     * instance with reduced precision a the method will call
     * {@link #filterWithPeriod(CriteriaBuilder, Path, TimePeriod, AbstractTimePrimitiveFieldDescriptor, boolean)}.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param time
     *            the time
     * @param r
     *            the property name(s)
     *
     * @return the {@code Predicate} that describes this restriction
     * @throws UnsupportedTimeException If the filter is not supported!
     */
    default Predicate filterWithInstant(CriteriaBuilder cb, Path<?> root, TimeInstant time,
            AbstractTimePrimitiveFieldDescriptor r) throws UnsupportedTimeException {
        return r instanceof TimePrimitiveFieldDescriptor ? createFilterWithInstant(cb, root, time, r)
                : filterWithInstant(cb, root, time, (TimePrimitiveNullableFieldDescriptor) r);
    }

    default Predicate createFilterWithInstant(CriteriaBuilder cb, Path<?> root, TimeInstant time,
            AbstractTimePrimitiveFieldDescriptor r) throws UnsupportedTimeException {
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
            return filterWithPeriod(cb, root, new TimePeriod(new DateTime(begin), new DateTime(end)), r, true);
        }
        if (r.isPeriod()) {
            return filterPeriodWithInstant(cb, root.get(r.getBeginPosition()), root.get(r.getEndPosition()), begin);
        } else {
            return filterInstantWithInstant(cb, root.get(r.getPosition()), begin);
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

}