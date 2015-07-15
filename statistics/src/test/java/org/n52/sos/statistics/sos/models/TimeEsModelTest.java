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
package org.n52.sos.statistics.sos.models;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.statistics.sos.SosDataMapping;

public class TimeEsModelTest {

    @Test
    public void timeInstant() {
        TimeInstant instant = new TimeInstant(DateTime.now());
        Map<String, Object> map = TimeEsModel.convert(instant);

        Assert.assertEquals(instant.getValue(), map.get(SosDataMapping.TIME_TIMEINSTANT));
    }

    @Test
    public void timePeriodNoDuration() {
        TimePeriod period = new TimePeriod(DateTime.now(), DateTime.now().plusHours(3));
        Map<String, Object> map = TimeEsModel.convert(period);

        Assert.assertEquals(period.getStart(), map.get(SosDataMapping.TIME_START));
        Assert.assertEquals(period.getEnd(), map.get(SosDataMapping.TIME_END));
        Assert.assertEquals(Long.valueOf(3 * 60 * 60 * 1000), map.get(SosDataMapping.TIME_DURARTION));
    }

    @Test
    public void invalidStartEndTimePeriod() {
        TimePeriod period = new TimePeriod(DateTime.now().plusHours(3), DateTime.now());
        Map<String, Object> map = TimeEsModel.convert(period);

        Assert.assertEquals(period.getStart(), map.get(SosDataMapping.TIME_START));
        Assert.assertEquals(period.getEnd(), map.get(SosDataMapping.TIME_END));
        Assert.assertNull(period.getDuration());
    }

    @Test
    public void temporalFilterConversion() {
        TemporalFilter filter = new TemporalFilter(TimeOperator.TM_After, new TimeInstant(DateTime.now()), "val-ref");
        Map<String, Object> map = TimeEsModel.convert(filter);

        Assert.assertEquals(TimeOperator.TM_After.toString(), map.get(SosDataMapping.TEMPORAL_FILTER_OPERATOR));
        Assert.assertEquals("val-ref", map.get(SosDataMapping.TEMPORAL_FILTER_VALUE_REF));
    }

    @Test
    public void temporalFilterListConversion() {
        TemporalFilter filter = new TemporalFilter(TimeOperator.TM_After, new TimeInstant(DateTime.now()), "val-ref");
        List<Map<String, Object>> list = TimeEsModel.convert(Arrays.asList(filter));

        Map<String, Object> map = list.get(0);

        Assert.assertEquals(TimeOperator.TM_After.toString(), map.get(SosDataMapping.TEMPORAL_FILTER_OPERATOR));
        Assert.assertEquals("val-ref", map.get(SosDataMapping.TEMPORAL_FILTER_VALUE_REF));
    }
}
