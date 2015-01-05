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
package org.n52.sos.config.sqlite;

import org.hibernate.HibernateException;
import org.hibernate.TypeMismatchException;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.util.Constants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.StringHelper;

public class HibernateTimeInstantType extends AbstractStringBasedHibernateUserType<Time> {
    
    private static final String VALUE_SEPARATOR = "@@";


    public HibernateTimeInstantType() {
        super(Time.class);
    }

    @Override
    protected Time decode(String s) throws HibernateException {
        try {
            return decodeTimeInstant(s);
        } catch (DateTimeParseException e) {
            throw new TypeMismatchException(String.format("Error while creating Time from %s", s));
        }
    }

    @Override
    protected String encode(Time t) throws HibernateException {
        if (t instanceof TimeInstant) {
            return encodeTimeInstant((TimeInstant)t);
        } 
        return Constants.EMPTY_STRING;
    }
    
    private Time decodeTimeInstant(String s) throws DateTimeParseException {
        if (StringHelper.isNotEmpty(s)) {
        String[] split = s.split(VALUE_SEPARATOR);
            TimeInstant time = (TimeInstant)DateTimeHelper.parseIsoString2DateTime2Time(split[0]);
            if (split.length == 2) {
                time.setRequestedTimeLength(Integer.parseInt(split[1]));
            }
            return time;
        }
        return null;
    }

    private String encodeTimeInstant(TimeInstant t) {
        StringBuilder builder = new StringBuilder();
        builder.append(t.getValue().toString());
        builder.append(VALUE_SEPARATOR);
        builder.append(t.getRequestedTimeLength());
        return builder.toString();
    }


}
