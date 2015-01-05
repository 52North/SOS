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
package org.n52.sos.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

public class DateTimeHelperTest {
    
    private final String testTimePositiveTimeZone = "2014-01-28T10:16:35.945+02:00";
    
    private final String testTimeNegativeTimeZone = "2014-01-28T10:16:35.945-02:00";
    
    private final String testTimeZTimeZone = "2014-01-28T10:16:35.945Z";
    
    private final int TIME_LENGTH = 23;
    
    @Test
    public void testGetTimeLengthBeforeTimeZone() {
        assertThat(DateTimeHelper.getTimeLengthBeforeTimeZone(testTimePositiveTimeZone), is(TIME_LENGTH));
        assertThat(DateTimeHelper.getTimeLengthBeforeTimeZone(testTimeNegativeTimeZone), is(TIME_LENGTH));
        assertThat(DateTimeHelper.getTimeLengthBeforeTimeZone(testTimeZTimeZone), is(TIME_LENGTH));
    }
    
    @Test
    public void testMakeDateTime() {
        long current = System.currentTimeMillis();
        DateTime currentDateTime = new DateTime(current, DateTimeZone.UTC);
        assertThat(currentDateTime.equals(DateTimeHelper.makeDateTime(currentDateTime)), is(true));
        assertThat(currentDateTime.equals(DateTimeHelper.makeDateTime(new java.util.Date(current))), is(true));
        assertThat(currentDateTime.equals(DateTimeHelper.makeDateTime(new java.sql.Date(current))), is(true));
        assertThat(currentDateTime.equals(DateTimeHelper.makeDateTime(new java.sql.Timestamp(current))), is(true));
        assertThat(currentDateTime.equals(DateTimeHelper.makeDateTime(new java.sql.Time(current))), is(true));
    }

}
