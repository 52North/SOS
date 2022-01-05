/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.TimeZone;

import org.hibernate.HibernateException;
import org.hibernate.TypeMismatchException;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.n52.shetland.util.DateTimeFormatException;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.DateTimeParseException;

import com.google.common.base.Strings;

public class IsoTimeStringTypeDescriptor implements SqlTypeDescriptor {

    private static final long serialVersionUID = -6841667236080304205L;

    private DateTimeZone timeZone = DateTimeZone.UTC;

    private String dateFormat;

    private boolean useZAsOffset;

    public IsoTimeStringTypeDescriptor(String timeZone, String dateFormat) {
        setTimeZone(timeZone);
        setDateFormat(dateFormat);
    }

    public IsoTimeStringTypeDescriptor(String timeZone, String dateFormat, boolean useZAsOffset) {
        setTimeZone(timeZone);
        setDateFormat(dateFormat);
        this.useZAsOffset = useZAsOffset;
    }

    public IsoTimeStringTypeDescriptor() {
    }

    public IsoTimeStringTypeDescriptor setTimeZone(String timeZone) {
        if (!Strings.isNullOrEmpty(timeZone)) {
            this.timeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone.trim()));
        }
        return this;
    }

    public IsoTimeStringTypeDescriptor setDateFormat(String dateFormat) {
        if (!Strings.isNullOrEmpty(dateFormat)) {
            this.dateFormat = dateFormat.trim();
        }
        return this;
    }

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public boolean canBeRemapped() {
        return true;
    }

    @Override
    public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicBinder<X>(javaTypeDescriptor, this) {
            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
                    throws SQLException {
                Date d = javaTypeDescriptor.unwrap(value, Timestamp.class, options);
                st.setString(index, encode(d));
            }

            @Override
            protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
                    throws SQLException {
                Date d = javaTypeDescriptor.unwrap(value, Timestamp.class, options);
                st.setString(name, encode(d));
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X>(javaTypeDescriptor, this) {
            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                if (rs.getObject(name) != null) {
                    return javaTypeDescriptor.wrap(decode(rs.getString(name)), options);
                }
                return null;
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(decode(statement.getString(index)), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options)
                    throws SQLException {
                return javaTypeDescriptor.wrap(decode(statement.getString(name)), options);
            }
        };
    }

    protected Date decode(String s) throws HibernateException {
        try {
            if (!Strings.isNullOrEmpty(s)) {
                if (dateFormat != null && !dateFormat.isEmpty()) {
                    return DateTimeHelper.parseString2DateTime(s, dateFormat).toDate();
                }
                return DateTimeHelper.parseIsoString2DateTime(s).toDate();
            }
        } catch (DateTimeParseException e) {
            throw new TypeMismatchException(String.format("Error while creating Time from %s", s));
        }
        return null;
    }

    protected String encode(Date d) throws HibernateException {
        if (d != null) {
            if (dateFormat != null && !dateFormat.isEmpty()) {
                try {
                    if (useZAsOffset) {
                        String format = dateFormat.contains("Z") ? dateFormat.replace("Z", "") : dateFormat;
                        DateTimeFormatter formatter =
                                new DateTimeFormatterBuilder().append(DateTimeFormat.forPattern(format))
                                        .appendTimeZoneOffset("Z", true, 2, 4).toFormatter();
                        return DateTimeHelper.formatDateTime2FormattedString(new DateTime(d, timeZone), formatter);
                    }
                    return DateTimeHelper.formatDateTime2FormattedString(new DateTime(d, timeZone), dateFormat);

                } catch (DateTimeFormatException e) {
                    throw new TypeMismatchException(
                            String.format("Error while creating time string for format %s", d));
                }
            }
            return DateTimeHelper.formatDateTime2IsoString(new DateTime(d, timeZone));
        }
        return "";
    }
}
