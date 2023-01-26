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
package org.n52.sos.aquarius.ds;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.threeten.extra.Interval;

public class AquariusHelperTest {

    DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_DATE_TIME)
    .toFormatter();

    @Test
    public void initial_grades_loaeding() {
        AquariusHelper aquariusHelper = new AquariusHelper();
        aquariusHelper.init();
        Assertions.assertTrue(aquariusHelper.getGrades().size() > 0);
    }

    @Test
    public void test_interval() {
        String time = "2021-11-03T12:13:00.0000000+12:00";
        String startTime1 = "2021-11-01T00:00:00.0000000+12:00";
        String endTime1 = "2021-11-03T12:13:00.0000001+12:00";
        String startTime2 = "2021-11-03T12:13:00.0000001+12:00";
        String endTime2 = "2021-11-03T13:25:00.0000000+12:00";

        LocalDateTime startLocalDateTime1 = parse(startTime1);
        LocalDateTime endLocalDateTime1 = parse(endTime1);
        LocalDateTime startLocalDateTime2 = parse(startTime2);
        LocalDateTime endLocalDateTime2 = parse(endTime2);
        Interval first = Interval.of(startLocalDateTime1.toInstant(ZoneOffset.UTC),
                endLocalDateTime1.toInstant(ZoneOffset.UTC));
        Interval second = Interval.of(startLocalDateTime2.toInstant(ZoneOffset.UTC),
                endLocalDateTime2.toInstant(ZoneOffset.UTC));
        LocalDateTime timeLocalDateTime = parse(time);
        Assertions.assertTrue(first.contains(timeLocalDateTime.toInstant(ZoneOffset.UTC)));
        Assertions.assertFalse(second.contains(timeLocalDateTime.toInstant(ZoneOffset.UTC)));
    }

    private LocalDateTime parse(String time) {
        return LocalDateTime.parse(time, formatter);
    }

}
