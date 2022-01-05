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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.SpatialFeatureQueryRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;

import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 *
 */
public final class QueryHelper {

    private static final int LIMIT_EXPRESSION_DEPTH = 1000;

    private static final String SAMS_SHAPE = "sams:shape";

    private static final String OM_FEATURE_OF_INTEREST = "om:featureOfInterest";

    private static final TimeOperator[] DEFAULT_INSTANT_TIME_OPERATORS = new TimeOperator[] { TimeOperator.TM_EndedBy,
        TimeOperator.TM_Contains, TimeOperator.TM_Equals, TimeOperator.TM_BegunBy };

    private static final TimeOperator[] DEFAULT_PERIOD_TIME_OPERATORS = new TimeOperator[] { TimeOperator.TM_Meets,
        TimeOperator.TM_Overlaps, TimeOperator.TM_Begins, TimeOperator.TM_BegunBy, TimeOperator.TM_During,
        TimeOperator.TM_Contains, TimeOperator.TM_Equals, TimeOperator.TM_OverlappedBy, TimeOperator.TM_Ends,
        TimeOperator.TM_EndedBy, TimeOperator.TM_MetBy };

    private QueryHelper() {
    }

    public static Set<String> getFeatures(FeatureQueryHandler featureQueryHandler, SpatialFeatureQueryRequest request,
            Session session) throws OwsExceptionReport {
        SpatialFilter spatialFilter = null;
        if (!request.hasSpatialFilteringProfileSpatialFilter()) {
            spatialFilter = request.getSpatialFilter();
        }
        return QueryHelper.getFeatureIdentifier(featureQueryHandler, spatialFilter, request.getFeatureIdentifiers(),
                session);
    }

    public static Set<String> getFeatureIdentifier(FeatureQueryHandler featureQueryHandler,
            SpatialFilter spatialFilter, List<String> featureIdentifier, Session session) throws OwsExceptionReport {
        Set<String> foiIDs = null;
        // spatial filter
        if (spatialFilter != null) {
            String valueReference = spatialFilter.getValueReference();
            if (!valueReference.contains(OM_FEATURE_OF_INTEREST) || !valueReference.contains(SAMS_SHAPE)) {
                throw new NoApplicableCodeException().withMessage(
                        "The requested valueReference for spatial filters is not supported by this server!");
            }
            FeatureQueryHandlerQueryObject query =
                    new FeatureQueryHandlerQueryObject(session).addSpatialFilter(spatialFilter);
            foiIDs = new HashSet<>(featureQueryHandler.getFeatureIDs(query));
        }

        // feature of interest
        if (CollectionHelper.isNotEmpty(featureIdentifier)) {
            return (foiIDs == null) ? new HashSet<>(featureIdentifier)
                    : featureIdentifier.stream().filter(foiIDs::contains).collect(Collectors.toSet());
        }
        return foiIDs;
    }

    /**
     * Get Criterion for DescribeSensor validTime parameter.
     *
     * @param validTime
     *            ValidTime parameter value
     *
     * @return Criterion with temporal filters
     *
     * @throws UnsupportedTimeException
     *             If the time value is invalid
     * @throws UnsupportedValueReferenceException
     *             If the valueReference is not supported
     * @throws UnsupportedOperatorException
     *             If the temporal operator is not supported
     */
    public static Criterion getValidTimeCriterion(Time validTime)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        if (validTime instanceof TimeInstant) {
            return SosTemporalRestrictions.filter(getFiltersForTimeInstant((TimeInstant) validTime));
        } else if (validTime instanceof TimePeriod) {
            return SosTemporalRestrictions.filter(getFiltersForTimePeriod((TimePeriod) validTime));
        } else {
            return null;
        }
    }

    /**
     * Get temporal filters for validTime TimeInstant
     *
     * @param validTime
     *            TimeInstant
     *
     * @return Collection with temporal filters
     */
    private static Collection<TemporalFilter> getFiltersForTimeInstant(TimeInstant validTime) {
        IndeterminateValue indeterminateValue =
                Optional.ofNullable(validTime.getIndeterminateValue()).orElse(IndeterminateValue.UNKNOWN);
        if (indeterminateValue.isAfter()) {
            return Collections.singleton(createBeforeFilter(validTime));
        } else if (indeterminateValue.isBefore()) {
            return Collections.singleton(createAfterFilter(validTime));
        } else if (indeterminateValue.isNow()) {
            validTime.setValue(DateTime.now());
            return getDefaultTimeInstantFilters(validTime);
        } else {
            return getDefaultTimeInstantFilters(validTime);
        }
    }

    private static TemporalFilter createAfterFilter(TimeInstant validTime) {
        return new TemporalFilter(TimeOperator.TM_Before, validTime,
                TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE);
    }

    private static TemporalFilter createBeforeFilter(TimeInstant validTime) {
        return new TemporalFilter(TimeOperator.TM_After, validTime,
                TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE);
    }

    /**
     * Get default filters for valid TimeInstant
     *
     * @param validTime
     *            TimeInstant
     *
     * @return default filters
     */
    private static Collection<TemporalFilter> getDefaultTimeInstantFilters(TimeInstant validTime) {
        return getFiltersForOperators(validTime, DEFAULT_INSTANT_TIME_OPERATORS);
    }

    /**
     * Get temporal filters for validTime TimePeriod
     *
     * @param validTime
     *            TimePeriod
     *
     * @return Collection with temporal filters
     */
    private static Collection<TemporalFilter> getFiltersForTimePeriod(TimePeriod validTime) {
        return getFiltersForOperators(validTime, DEFAULT_PERIOD_TIME_OPERATORS);
    }

    private static Collection<TemporalFilter> getFiltersForOperators(Time validTime, TimeOperator... operators) {
        String reference = TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE;
        return Arrays.asList(operators).stream().map(op -> new TemporalFilter(op, validTime, reference))
                .collect(Collectors.toList());
    }

    /**
     * Creates a criterion for objects, considers if size is > 1000 (Oracle
     * expression limit).
     *
     * @param propertyName
     *            Column name.
     * @param identifiers
     *            Objects list
     * @return Criterion.
     */
    public static Criterion getCriterionForObjects(String propertyName, Collection<?> identifiers) {
        if (identifiers.size() >= LIMIT_EXPRESSION_DEPTH) {
            List<?> identifiersList = Lists.newArrayList(identifiers);
            Criterion criterion = null;
            List<Object> ids = null;
            for (int i = 0; i < identifiersList.size(); i++) {
                if (i == 0 || i % (LIMIT_EXPRESSION_DEPTH - 1) == 0) {
                    if (criterion == null && i != 0) {
                        criterion = Restrictions.in(propertyName, ids);
                    } else if (criterion != null) {
                        criterion = Restrictions.or(criterion, Restrictions.in(propertyName, ids));
                    }
                    ids = Lists.newArrayList();
                    ids.add(identifiersList.get(i));
                } else {
                    ids.add(identifiersList.get(i));
                }
            }
            return criterion;
        } else {
            return Restrictions.in(propertyName, identifiers);
        }
    }

    /**
     * Creates a list of lists from identifiers, considers if size is > 1000
     * (Oracle expression limit).
     *
     * @param identifiers
     *            Identifiers list
     * @return The splitted identifiers
     */
    public static List<List<String>> getListsForIdentifiers(Collection<String> identifiers) {
        List<List<String>> list = new ArrayList<>();
        List<String> identifiersList = Lists.newArrayList(identifiers);
        if (identifiers.size() >= LIMIT_EXPRESSION_DEPTH) {
            List<String> ids = null;
            for (int i = 0; i < identifiersList.size(); i++) {
                if (i == 0 || i % (LIMIT_EXPRESSION_DEPTH - 1) == 0) {
                    if (i != 0) {
                        list.add(ids);
                    }
                    ids = Lists.newArrayList();
                    ids.add(identifiersList.get(i));
                } else {
                    ids.add(identifiersList.get(i));
                }
            }
        } else {
            list.add(identifiersList);
        }
        return list;
    }
}
