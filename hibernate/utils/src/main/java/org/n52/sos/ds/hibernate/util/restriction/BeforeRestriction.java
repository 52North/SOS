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
package org.n52.sos.ds.hibernate.util.restriction;

import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import org.n52.sos.ds.hibernate.util.TemporalRestriction;

/**
 * Creates filters according to the following table.
 * <table>
 * <tr>
 * <td><i>Self/Other</i></td>
 * <td><b>Period</b></td>
 * <td><b>Instant</b></td>
 * </tr>
 * <tr>
 * <td><b>Period</b></td>
 * <td>{@code self.end &lt; other.begin}</td>
 * <td>{@code self.end &lt; other.position}</td>
 * </tr>
 * <tr>
 * <td><b>Instant</b></td>
 * <td>{@code self.position &lt; other.begin}</td>
 * <td>{@code self.position &lt; other.position}</td>
 * </tr>
 * </table>
 */
public class BeforeRestriction implements TemporalRestriction {
    @Override
    public Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Date otherBegin, Date otherEnd) {
        return Restrictions.lt(selfEnd, otherBegin);
    }

    @Override
    public Criterion filterPeriodWithPeriod(String selfBegin, String selfEnd, Integer count) {
        return Restrictions.lt(selfEnd, getStartPlaceHolder(count));
    }

    @Override
    public Criterion filterInstantWithPeriod(String selfPosition, Date otherBegin, Date otherEnd,
                                                boolean isOtherPeriodFromReducedPrecisionInstant) {
        return Restrictions.lt(selfPosition, otherBegin);
    }

    @Override
    public Criterion filterInstantWithPeriod(String selfPosition, String otherPosition, Integer count) {
        return Restrictions.lt(selfPosition, getStartPlaceHolder(count));
    }

    @Override
    public Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Date otherPosition) {
        return Restrictions.lt(selfEnd, otherPosition);
    }

    @Override
    public Criterion filterPeriodWithInstant(String selfBegin, String selfEnd, Integer count) {
        return Restrictions.lt(selfEnd, getInstantPlaceHolder(count));
    }

    @Override
    public Criterion filterInstantWithInstant(String selfPosition, Date otherPosition) {
        return Restrictions.lt(selfPosition, otherPosition);
    }

    @Override
    public Criterion filterInstantWithInstant(String selfPosition, String otherPosition, Integer count) {
        return Restrictions.lt(selfPosition, getInstantPlaceHolder(count));
    }
}
