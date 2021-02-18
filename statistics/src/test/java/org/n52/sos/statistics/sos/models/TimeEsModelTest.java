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
package org.n52.sos.statistics.sos.models;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import org.n52.iceland.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;

public class TimeEsModelTest {

    private static final String VAL_REF = "val-ref";

    @Test
    public void timeInstant() {
        TimeInstant instant = new TimeInstant(DateTime.now());
        Map<String, Object> map = TimeEsModel.convert(instant);

        Assert.assertEquals(instant.getValue(), map.get(ObjectEsParameterFactory.TIME_TIMEINSTANT.getName()));
    }

    @Test
    public void timePeriodNoDuration() {
        TimePeriod period = new TimePeriod(DateTime.now(), DateTime.now().plusHours(3));
        Map<String, Object> map = TimeEsModel.convert(period);

        Assert.assertEquals(period.getStart(), map.get(ObjectEsParameterFactory.TIME_START.getName()));
        Assert.assertEquals(period.getEnd(), map.get(ObjectEsParameterFactory.TIME_END.getName()));
        Assert.assertEquals(Long.valueOf(3 * 60 * 60 * 1000),
                map.get(ObjectEsParameterFactory.TIME_DURARTION.getName()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidStartEndTimePeriod() {
        TimePeriod period = new TimePeriod(DateTime.now().plusHours(3), DateTime.now());
        Map<String, Object> map = TimeEsModel.convert(period);

        Assert.assertEquals(period.getStart(), map.get(ObjectEsParameterFactory.TIME_START.getName()));
        Assert.assertEquals(period.getEnd(), map.get(ObjectEsParameterFactory.TIME_END.getName()));
        Assert.assertNull(period.getDuration());
    }

    @Test
    public void temporalFilterConversion() {
        TemporalFilter filter = new TemporalFilter(TimeOperator.TM_After, new TimeInstant(DateTime.now()), VAL_REF);
        Map<String, Object> map = TimeEsModel.convert(filter);

        Assert.assertEquals(TimeOperator.TM_After.toString(),
                map.get(ObjectEsParameterFactory.TEMPORAL_FILTER_OPERATOR.getName()));
        Assert.assertEquals(VAL_REF, map.get(ObjectEsParameterFactory.TEMPORAL_FILTER_VALUE_REF.getName()));
    }

    @Test
    public void temporalFilterListConversion() {
        TemporalFilter filter = new TemporalFilter(TimeOperator.TM_After, new TimeInstant(DateTime.now()), VAL_REF);
        List<Map<String, Object>> list = TimeEsModel.convert(Arrays.asList(filter));

        Map<String, Object> map = list.get(0);

        Assert.assertEquals(TimeOperator.TM_After.toString(),
                map.get(ObjectEsParameterFactory.TEMPORAL_FILTER_OPERATOR.getName()));
        Assert.assertEquals(VAL_REF, map.get(ObjectEsParameterFactory.TEMPORAL_FILTER_VALUE_REF.getName()));
    }

    @Test
    public void spansJanuaryFromMarchConversion() {
        TimePeriod period = new TimePeriod(new DateTime(2015, 1, 1, 0, 0), new DateTime(2015, 3, 10, 0, 0));
        Map<String, Object> map = TimeEsModel.convert(period);

        Assert.assertEquals(69, ((List<?>) map.get(ObjectEsParameterFactory.TIME_SPAN_AS_DAYS.getName())).size());

    }

    @Test
    public void betweenYearSpanConversion() {
        TimePeriod period = new TimePeriod(new DateTime(2014, 12, 1, 0, 0), new DateTime(2015, 2, 5, 0, 0));
        Map<String, Object> map = TimeEsModel.convert(period);

        Assert.assertEquals(67, ((List<?>) map.get(ObjectEsParameterFactory.TIME_SPAN_AS_DAYS.getName())).size());

    }
}
