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
package org.n52.sos.ds.hibernate.type;

import java.util.Date;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;

public class IsoTimeStringType extends AbstractSingleColumnStandardBasicType<Date> implements LiteralType<Date> {

    public static final IsoTimeStringType INSTANCE = new IsoTimeStringType();

    private static final long serialVersionUID = 1578546594599136420L;

    private String name = "iso_string";

    public IsoTimeStringType() {
        super(new IsoTimeStringTypeDescriptor(), JdbcTimestampTypeDescriptor.INSTANCE);
    }

    public IsoTimeStringType(String timeZone, String dateFormat) {
        super(new IsoTimeStringTypeDescriptor(timeZone, dateFormat), JdbcTimestampTypeDescriptor.INSTANCE);
    }

    public IsoTimeStringType(String timeZone, String dateFormat, boolean useZAsOffset) {
        super(new IsoTimeStringTypeDescriptor(timeZone, dateFormat, useZAsOffset),
                JdbcTimestampTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String objectToSQLString(Date value, Dialect dialect) throws Exception {
        return StringType.INSTANCE
                .objectToSQLString(((IsoTimeStringTypeDescriptor) getSqlTypeDescriptor()).encode(value), dialect);
    }

}
