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

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.ogc.gml.time.TimePeriod;

public class SosTimeJsonHolderTest {

    @Test
    public void timeInstant() {
        TimeInstant instant = new TimeInstant(DateTime.now());
        SosTimeJsonHolder ret = SosTimeJsonHolder.convert(instant);

        Assert.assertEquals(instant.getValue(), ret.getTimeInstant());
    }

    @Test
    public void timePeriodNoDuration() {
        TimePeriod period = new TimePeriod(DateTime.now(), DateTime.now().plusHours(3));
        SosTimeJsonHolder ret = SosTimeJsonHolder.convert(period);

        Assert.assertEquals(period.getStart(), ret.getStart());
        Assert.assertEquals(period.getEnd(), ret.getEnd());
        Assert.assertEquals(Long.valueOf(3 * 60 * 60 * 1000), ret.getDuration());
    }

    @Test
    public void invalidStartEndTimePeriod() {
        TimePeriod period = new TimePeriod(DateTime.now().plusHours(3), DateTime.now());
        SosTimeJsonHolder ret = SosTimeJsonHolder.convert(period);

        Assert.assertEquals(period.getStart(), ret.getStart());
        Assert.assertEquals(period.getEnd(), ret.getEnd());
        Assert.assertNull(period.getDuration());
    }
}
