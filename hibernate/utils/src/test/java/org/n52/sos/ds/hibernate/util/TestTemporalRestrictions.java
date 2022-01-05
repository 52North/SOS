/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
import java.util.Map;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.filter.TemporalFilter;
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
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;

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
 * @see EqualsRestriction
 * @see MeetsRestriction
 * @see MetByRestriction
 * @see OverlappedByRestriction
 * @see OverlapsRestriction
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @since 4.0.0
 */
public final class TestTemporalRestrictions implements TemporalRestrictionTestConstants {

    /**
     * Fields describing the phenomenon time of a {@code Observation}.
     *
     * @see DataEntity#PROPERTY_SAMPLING_TIME_START
     * @see DataEntity#PROPERTY_SAMPLING_TIME_END
     */
    public static final AbstractTimePrimitiveFieldDescriptor PHENOMENON_TIME_FIELDS = new TimePrimitiveFieldDescriptor(
            PROPERTY_SAMPLING_TIME_START, PROPERTY_SAMPLING_TIME_END);

    /**
     * Fields describing the result time of a {@code Observation}.
     *
     * @see DataEntity#PROPERTY_RESULT_TIME
     */
    public static final AbstractTimePrimitiveFieldDescriptor RESULT_TIME_FIELDS =
            new TimePrimitiveNullableFieldDescriptor(PROPERTY_RESULT_TIME,
                    new TimePrimitiveFieldDescriptor(PROPERTY_SAMPLING_TIME_END));

    /**
     * Fields describing the valid time of a {@code Observation}.
     *
     * @see DataEntity#PROPERTY_VALID_TIME_START
     * @see DataEntity#PROPERTY_VALID_TIME_END
     */
    public static final AbstractTimePrimitiveFieldDescriptor VALID_TIME_FIELDS =
            new TimePrimitiveFieldDescriptor(PROPERTY_VALID_TIME_START, PROPERTY_VALID_TIME_END);

    /**
     * Fields describing the valid time of a {@code ValidProcedureTime}.
     *
     * @see ProcedureHistoryEntity#START_TIME
     * @see ProcedureHistoryEntity#END_TIME
     */
    public static final AbstractTimePrimitiveFieldDescriptor VALID_TIME_DESCRIBE_SENSOR_FIELDS =
            new TimePrimitiveFieldDescriptor(START_TIME, END_TIME);

    /**
     * Private constructor due to static access.
     */
    private TestTemporalRestrictions() {
        // noop
    }

    /**
     * Create a new {@code Criterion} using the specified filter.
     *
     * @param filter
     *            the filter
     *
     * @return the {@code Criterion}
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
    public static Criterion filter(TemporalFilter filter)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        return TemporalRestrictions.filter(filter.getOperator(), getFields(filter.getValueReference()),
                filter.getTime());
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
    public static Criterion filter(Iterable<TemporalFilter> temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Conjunction conjunction = Restrictions.conjunction();
        Collection<Disjunction> disjunctions = getDisjunction(temporalFilters);
        if (disjunctions.size() == 1) {
            return disjunctions.iterator().next();
        }
        disjunctions.forEach(conjunction::add);
        return conjunction;
    }

    public static Criterion filterHql(TemporalFilter filter, Integer count)
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        return TemporalRestrictions.filter(filter.getOperator(), getFields(filter.getValueReference()),
                filter.getTime(), count);
    }

    public static Criterion filterHql(Iterable<TemporalFilter> temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Conjunction conjunction = Restrictions.conjunction();
        Collection<Disjunction> disjunctions = getDisjunctionHql(temporalFilters);
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
     *
     * @return {@link Collection} of {@link Disjunction}
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
        return map.values();
    }

    private static Collection<Disjunction> getDisjunctionHql(Iterable<TemporalFilter> temporalFilters)
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        Map<String, Disjunction> map = Maps.newHashMap();
        Integer count = Integer.valueOf(1);
        for (TemporalFilter temporalFilter : temporalFilters) {
            if (map.containsKey(temporalFilter.getValueReference())) {
                map.get(temporalFilter.getValueReference()).add(filterHql(temporalFilter, count));
            } else {
                Disjunction disjunction = Restrictions.disjunction();
                disjunction.add(filterHql(temporalFilter, count));
                map.put(temporalFilter.getValueReference(), disjunction);
            }
            count++;
        }
        return map.values();
    }

    /**
     * Gets the field descriptor for the specified value reference.
     *
     * @param valueReference
     *            the value reference
     *
     * @return the property descriptor
     *
     * @see TemporalRestrictions#PHENOMENON_TIME_VALUE_REFERENCE
     * @see TemporalRestrictions #RESULT_TIME_VALUE_REFERENCE
     * @see TemporalRestrictions#VALID_TIME_VALUE_REFERENCE
     * @see #PHENOMENON_TIME_FIELDS
     * @see #RESULT_TIME_FIELDS
     * @see #VALID_TIME_FIELDS
     *
     * @throws UnsupportedValueReferenceException
     *             if the {@code valueReference} can not be decoded
     */
    public static AbstractTimePrimitiveFieldDescriptor getFields(String valueReference)
            throws UnsupportedValueReferenceException {
        if (valueReference.contains(TemporalRestrictions.PHENOMENON_TIME_VALUE_REFERENCE)) {
            return PHENOMENON_TIME_FIELDS;
        } else if (valueReference.contains(TemporalRestrictions.RESULT_TIME_VALUE_REFERENCE)) {
            return RESULT_TIME_FIELDS;
        } else if (valueReference.contains(TemporalRestrictions.VALID_TIME_VALUE_REFERENCE)) {
            return VALID_TIME_FIELDS;
        } else if (valueReference.contains(TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE)) {
            return VALID_TIME_DESCRIBE_SENSOR_FIELDS;
        } else {
            throw new UnsupportedValueReferenceException(valueReference);
        }
    }

}
