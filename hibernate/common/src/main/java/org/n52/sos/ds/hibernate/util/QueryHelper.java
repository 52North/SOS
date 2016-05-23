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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.joda.time.DateTime;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.SpatialFeatureQueryRequest;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 * 
 */
public class QueryHelper {

    private QueryHelper() {
    }

    public static Set<String> getFeatures(final SpatialFeatureQueryRequest request, final Session session)
            throws OwsExceptionReport {
        if (request.hasSpatialFilteringProfileSpatialFilter()) {
            return QueryHelper.getFeatureIdentifier(null, request.getFeatureIdentifiers(), session);
        } else {
            return QueryHelper.getFeatureIdentifier(request.getSpatialFilter(), request.getFeatureIdentifiers(),
                    session);
        }
    }

    public static Set<String> getFeatureIdentifier(SpatialFilter spatialFilter, List<String> featureIdentifier,
            Session session) throws OwsExceptionReport {
        Set<String> foiIDs = null;
        // spatial filter
        if (spatialFilter != null) {
            if (spatialFilter.getValueReference().contains("om:featureOfInterest")
                    && spatialFilter.getValueReference().contains("sams:shape")) {
                foiIDs =
                        new HashSet<String>(Configurator
                                .getInstance()
                                .getFeatureQueryHandler()
                                .getFeatureIDs(
                                        new FeatureQueryHandlerQueryObject().addSpatialFilter(spatialFilter)
                                                .setConnection(session)));
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("The requested valueReference for spatial filters is not supported by this server!");
            }
        }
        // feature of interest
        if (CollectionHelper.isNotEmpty(featureIdentifier)) {
            if (foiIDs == null) {
                foiIDs = new HashSet<String>(featureIdentifier);
            } else {
                Set<String> tempFoiIDs = new HashSet<String>();
                for (String foiID : featureIdentifier) {
                    if (foiIDs.contains(foiID)) {
                        tempFoiIDs.add(foiID);
                    }
                }
                foiIDs = tempFoiIDs;
            }
        }
        return foiIDs;
    }

    /**
     * Get Criterion for DescribeSensor validTime parameter.
     * 
     * @param validTime
     *            ValidTime parameter value
     * @return Criterion with temporal filters
     * @throws UnsupportedTimeException
     *             If the time value is invalid
     * @throws UnsupportedValueReferenceException
     *             If the valueReference is not supported
     * @throws UnsupportedOperatorException
     *             If the temporal operator is not supported
     */
    public static Criterion getValidTimeCriterion(Time validTime) throws UnsupportedTimeException,
            UnsupportedValueReferenceException, UnsupportedOperatorException {
        if (validTime instanceof TimeInstant) {
            return TemporalRestrictions.filter(getFiltersForTimeInstant((TimeInstant) validTime));
        } else if (validTime instanceof TimePeriod) {
            return TemporalRestrictions.filter(getFiltersForTimePeriod(validTime));
        }
        return null;
    }

    /**
     * Get temporal filters for validTime TimeInstant
     * 
     * @param validTime
     *            TimeInstant
     * @return Collection with temporal filters
     */
    private static Collection<TemporalFilter> getFiltersForTimeInstant(TimeInstant validTime) {
        if (validTime.isSetIndeterminateValue()) {
            final List<TemporalFilter> filters = Lists.newLinkedList();
            switch (validTime.getIndeterminateValue()) {
            case after:
                filters.add(new TemporalFilter(TimeOperator.TM_After, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE));
                break;
            case before:
                filters.add(new TemporalFilter(TimeOperator.TM_Before, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE));
                break;
            case now:
                validTime.setValue(new DateTime());
                return getDefautlTimeInstantFilters(validTime);
            default:
                return getDefautlTimeInstantFilters(validTime);
            }
            return filters;
        } else {
            return getDefautlTimeInstantFilters(validTime);
        }
    }

    /**
     * Get default filters for valid TimeInstant
     * 
     * @param validTime
     *            TimeInstant
     * @return default filters
     */
    private static Collection<TemporalFilter> getDefautlTimeInstantFilters(TimeInstant validTime) {
        return Lists.newArrayList(new TemporalFilter(TimeOperator.TM_EndedBy, validTime,
                TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                TimeOperator.TM_Contains, validTime, TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE),
                new TemporalFilter(TimeOperator.TM_Equals, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                        TimeOperator.TM_BegunBy, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE));
    }

    /**
     * Get temporal filters for validTime TimePeriod
     * 
     * @param validTime
     *            TimePeriod
     * 
     * @return Collection with temporal filters
     */
    private static Collection<TemporalFilter> getFiltersForTimePeriod(Time validTime) {
        return Lists.newArrayList(new TemporalFilter(TimeOperator.TM_Meets, validTime,
                TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                TimeOperator.TM_Overlaps, validTime, TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE),
                new TemporalFilter(TimeOperator.TM_Begins, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                        TimeOperator.TM_BegunBy, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                        TimeOperator.TM_During, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                        TimeOperator.TM_Contains, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                        TimeOperator.TM_Equals, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                        TimeOperator.TM_OverlappedBy, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                        TimeOperator.TM_Ends, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                        TimeOperator.TM_EndedBy, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE), new TemporalFilter(
                        TimeOperator.TM_MetBy, validTime,
                        TemporalRestrictions.VALID_DESCRIBE_SENSOR_TIME_VALUE_REFERENCE));
    }
}
