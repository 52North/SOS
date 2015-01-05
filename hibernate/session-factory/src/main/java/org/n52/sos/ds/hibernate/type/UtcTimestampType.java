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
package org.n52.sos.ds.hibernate.type;

import java.util.Comparator;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;

/**
 * A type that maps between {@link java.sql.Types#TIMESTAMP TIMESTAMP} and
 * {@link java.sql.Timestamp}. Delegates to Hibernate's TimestampType, but
 * specifies UtcTimestampTypeDescriptor as the SqlTypeDescriptor so that times
 * from the database are always retrieved in UTC.
 * 
 * @see <a
 *      href="http://stackoverflow.com/questions/508019/jpa-hibernate-store-date-in-utc-time-zone/3430957#3430957">http://stackoverflow.com/questions/508019/jpa-hibernate-store-date-in-utc-time-zone/3430957#3430957</a>
 * 
 * @author Shane StClair <shane@axiomalaska.com>
 * 
 * @since 4.0.0
 */
public class UtcTimestampType extends AbstractSingleColumnStandardBasicType<Date> implements VersionType<Date>,
        LiteralType<Date> {
    private static final long serialVersionUID = 1425445989380765998L;

    public static final UtcTimestampType INSTANCE = new UtcTimestampType();

    public UtcTimestampType() {
        super(UtcTimestampTypeDescriptor.INSTANCE, JdbcTimestampTypeDescriptor.INSTANCE);
    }

    public String getName() {
        return TimestampType.INSTANCE.getName();
    }

    @Override
    public String[] getRegistrationKeys() {
        return TimestampType.INSTANCE.getRegistrationKeys();
    }

    public Date next(Date current, SessionImplementor session) {
        return TimestampType.INSTANCE.next(current, session);
    }

    public Date seed(SessionImplementor session) {
        return TimestampType.INSTANCE.seed(session);
    }

    public Comparator<Date> getComparator() {
        return TimestampType.INSTANCE.getComparator();
    }

    public String objectToSQLString(Date value, Dialect dialect) throws Exception {
        return TimestampType.INSTANCE.objectToSQLString(value, dialect);
    }

    public Date fromStringValue(String xml) throws HibernateException {
        return TimestampType.INSTANCE.fromStringValue(xml);
    }
}
