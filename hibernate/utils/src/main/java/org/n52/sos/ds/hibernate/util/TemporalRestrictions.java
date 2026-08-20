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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

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
 * Factory that dispatches to the 13 {@link TemporalRestriction} implementations
 * identified by Allen, building a {@link Predicate} for the given
 * {@link TimeOperator}, property descriptor and time.
 *
 * @see AfterRestriction
 * @see BeforeRestriction
 * @see BeginsRestriction
 * @see BegunByRestriction
 * @see ContainsRestriction
 * @see DuringRestriction
 * @see EndedByRestriction
 * @see EndsRestriction
 * @see EqualsRestriction
 * @see MeetsRestriction
 * @see MetByRestriction
 * @see OverlappedByRestriction
 * @see OverlapsRestriction
 * @author Christian Autermann
 */
public final class TemporalRestrictions {

    /**
     * Marker for a value reference referencing the phenomenon time ({@value} ).
     */
    public static final String PHENOMENON_TIME_VALUE_REFERENCE = "phenomenonTime";

    /**
     * Marker for a value reference referencing the result time ({@value} ).
     */
    public static final String RESULT_TIME_VALUE_REFERENCE = "resultTime";

    /**
     * Marker for a value reference referencing the valid time ({@value} ).
     */
    public static final String VALID_TIME_VALUE_REFERENCE = "validTime";

    /**
     * Marker for a value reference referencing the valid time ({@value} ).
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
     * Create a new {@code Predicate} using the specified operator, fields and
     * value.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param operator
     *            the operator
     * @param property
     *            the property field name(s)
     * @param value
     *            the value
     *
     * @return the {@code Predicate}
     *
     * @throws UnsupportedOperatorException
     *             if no restriction definition for the {@link TimeOperator} is
     *             found
     * @throws UnsupportedTimeException
     *             if the value and property combination is not applicable for
     *             this restriction
     */
    public static Predicate filter(CriteriaBuilder cb, Path<?> root, TimeOperator operator,
            AbstractTimePrimitiveFieldDescriptor property, Time value)
            throws UnsupportedOperatorException, UnsupportedTimeException {
        Predicate p = forOperator(operator).getPredicate(cb, root, property, value);
        if (p != null) {
            return p;
        }
        throw new UnsupportedTimeException(value);
    }

    private static TemporalRestriction forOperator(TimeOperator operator) throws UnsupportedOperatorException {
        switch (operator) {
            case TM_Before:
                return BEFORE;
            case TM_After:
                return AFTER;
            case TM_Begins:
                return BEGINS;
            case TM_Ends:
                return ENDS;
            case TM_EndedBy:
                return ENDED_BY;
            case TM_BegunBy:
                return BEGUN_BY;
            case TM_During:
                return DURING;
            case TM_Equals:
                return EQUALS;
            case TM_Contains:
                return CONTAINS;
            case TM_Overlaps:
                return OVERLAPS;
            case TM_Meets:
                return MEETS;
            case TM_MetBy:
                return MET_BY;
            case TM_OverlappedBy:
                return OVERLAPPED_BY;
            default:
                throw new UnsupportedOperatorException(operator);
        }
    }
}