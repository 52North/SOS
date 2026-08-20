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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
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

/**
 * Factory methods to create {@link Predicate Predicates} for
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
public final class SosTemporalRestrictions {

    /**
     * Fields describing the phenomenon time of a {@code Observation}.
     *
     * @see DataEntity#PROPERTY_SAMPLING_TIME_START
     * @see DataEntity#PROPERTY_SAMPLING_TIME_END
     */
    public static final AbstractTimePrimitiveFieldDescriptor PHENOMENON_TIME_FIELDS = new TimePrimitiveFieldDescriptor(
            DataEntity.PROPERTY_SAMPLING_TIME_START, DataEntity.PROPERTY_SAMPLING_TIME_END);

    /**
     * Fields describing the result time of a {@code Observation}.
     *
     * @see DataEntity#PROPERTY_RESULT_TIME
     */
    public static final AbstractTimePrimitiveFieldDescriptor RESULT_TIME_FIELDS =
            new TimePrimitiveNullableFieldDescriptor(DataEntity.PROPERTY_RESULT_TIME,
                    new TimePrimitiveFieldDescriptor(DataEntity.PROPERTY_SAMPLING_TIME_END));

    /**
     * Fields describing the valid time of a {@code Observation}.
     *
     * @see DataEntity#PROPERTY_VALID_TIME_START
     * @see DataEntity#PROPERTY_VALID_TIME_END
     */
    public static final AbstractTimePrimitiveFieldDescriptor VALID_TIME_FIELDS =
            new TimePrimitiveFieldDescriptor(DataEntity.PROPERTY_VALID_TIME_START, DataEntity.PROPERTY_VALID_TIME_END);

    /**
     * Fields describing the valid time of a {@code ValidProcedureTime}.
     *
     * @see ProcedureHistoryEntity#START_TIME
     * @see ProcedureHistoryEntity#END_TIME
     */
    public static final AbstractTimePrimitiveFieldDescriptor VALID_TIME_DESCRIBE_SENSOR_FIELDS =
            new TimePrimitiveFieldDescriptor(ProcedureHistoryEntity.START_TIME, ProcedureHistoryEntity.END_TIME);

    /**
     * Private constructor due to static access.
     */
    private SosTemporalRestrictions() {
        // noop
    }

    /**
     * Create a new {@code Predicate} using the specified filter.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param filter
     *            the filter
     *
     * @return the {@code Predicate}
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
    public static Predicate filter(CriteriaBuilder cb, Path<?> root, TemporalFilter filter)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        return TemporalRestrictions.filter(cb, root, filter.getOperator(), getFields(filter.getValueReference()),
                filter.getTime());
    }

    /**
     * Creates a conjunction for the specified temporal filters.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param temporalFilters
     *            the filters
     *
     * @return Predicate for the temporal filters
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
    public static Predicate filter(CriteriaBuilder cb, Path<?> root, Iterable<TemporalFilter> temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Collection<Predicate> disjunctions = getDisjunction(cb, root, temporalFilters);
        if (disjunctions.size() == 1) {
            return disjunctions.iterator()
                    .next();
        }
        return cb.and(disjunctions.toArray(new Predicate[0]));
    }

    /**
     * Creates a disjunction for each set of filters with the same
     * valueReference.
     *
     * @param cb
     *            the criteria builder
     * @param root
     *            the path holding the property/properties
     * @param temporalFilters
     *            the filters
     *
     * @return {@link Collection} of {@link Predicate}, one per valueReference
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
    private static Collection<Predicate> getDisjunction(CriteriaBuilder cb, Path<?> root,
            Iterable<TemporalFilter> temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Map<String, List<Predicate>> byValueReference = new HashMap<>();
        for (TemporalFilter temporalFilter : temporalFilters) {
            byValueReference.computeIfAbsent(temporalFilter.getValueReference(), k -> new ArrayList<>())
                    .add(filter(cb, root, temporalFilter));
        }
        List<Predicate> disjunctions = new ArrayList<>(byValueReference.size());
        for (List<Predicate> predicates : byValueReference.values()) {
            disjunctions.add(predicates.size() == 1 ? predicates.get(0) : cb.or(predicates.toArray(new Predicate[0])));
        }
        return disjunctions;
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