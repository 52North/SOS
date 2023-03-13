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
package org.n52.sos.statistics.sos.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.n52.iceland.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP"})
public final class TimeEsModel extends AbstractElasticsearchModel {
    private DateTime timeInstant;

    private DateTime start;

    private DateTime end;

    private Long duration;

    private String timeOperator;

    private String valueReference;

    private Time time;

    private TimeEsModel(Time time) {
        this.time = time;
    }

    public static Map<String, Object> convert(Time time) {
        return new TimeEsModel(time).getAsMap();
    }

    public static Map<String, Object> convert(TemporalFilter temporalFilter) {
        TimeEsModel timeEsModel = new TimeEsModel(temporalFilter.getTime());
        timeEsModel.timeOperator = temporalFilter.getOperator().toString();
        timeEsModel.valueReference = temporalFilter.getValueReference();
        return timeEsModel.getAsMap();
    }

    public static List<Map<String, Object>> convert(Collection<TemporalFilter> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        return filters.stream().map(TimeEsModel::convert).collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> getAsMap() {
        if (time instanceof TimeInstant) {
            this.timeInstant = ((TimeInstant) time).getValue();

        } else if (time instanceof TimePeriod) {
            TimePeriod p = (TimePeriod) time;

            if (p.getStart() != null && p.getEnd() != null) {
                if (p.getEnd().compareTo(p.getStart()) >= 0) {
                    this.duration = p.getEnd().getMillis() - p.getStart().getMillis();
                }
            }

            if (p.getStart() != null) {
                this.start = p.getStart();
            }
            if (p.getEnd() != null) {
                this.end = p.getEnd();
            }
        }

        put(ObjectEsParameterFactory.TIME_DURARTION, duration);
        put(ObjectEsParameterFactory.TIME_START, start);
        put(ObjectEsParameterFactory.TIME_END, end);
        put(ObjectEsParameterFactory.TIME_TIMEINSTANT, timeInstant);
        put(ObjectEsParameterFactory.TIME_SPAN_AS_DAYS, calculateSpanDays(start, end));
        // only by TemporalFilter
        put(ObjectEsParameterFactory.TEMPORAL_FILTER_OPERATOR, timeOperator);
        put(ObjectEsParameterFactory.TEMPORAL_FILTER_VALUE_REF, valueReference);
        return dataMap;
    }

    private List<DateTime> calculateSpanDays(final DateTime start, DateTime end) {
        if (!checkDates(start, end)) {
            return null;
        }
        List<DateTime> result = new ArrayList<>();
        DateTime temp =
                new DateTime(start.getYear(), start.getMonthOfYear(), start.getDayOfMonth(), 0, 0, DateTimeZone.UTC);
        while (temp.getYear() != end.getYear() || temp.getMonthOfYear() != end.getMonthOfYear()
                || temp.getDayOfMonth() != end.getDayOfMonth()) {
            result.add(temp);
            temp = temp.plusDays(1);
        }
        result.add(temp);
        return result;
    }

    private boolean checkDates(DateTime start, DateTime end) {
        if (start == null || end == null) {
            return false;
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException(
                    String.format("Start date is not before the end date. Start date %s end date %s", start.toString(),
                            end.toString()));
        }
        return true;
    }

    public DateTime getTimeInstant() {
        return timeInstant;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }

    public Long getDuration() {
        return duration;
    }

    public String getTimeOperator() {
        return timeOperator;
    }

}
