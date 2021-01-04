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
package org.n52.sos.ds.hibernate.type;

import com.google.common.base.Strings;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;
import org.joda.time.DateTimeZone;

/**
 * Hibernate TypeDescriptor which forces all Timestamps queried from/inserted to
 * the database to use UTC instead of the JVM's timezone.
 *
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 *
 * @since 4.3.12
 */
public class ConfigurableTimestampTypeDescriptor extends TimestampTypeDescriptor {

    private static final long serialVersionUID = 1037943750961368390L;

    private TimeZone timeZone = TimeZone.getTimeZone("UTC");

    /**
     * Get instance of {@link ConfigurableTimestampTypeDescriptor} constructor
     */
    public ConfigurableTimestampTypeDescriptor() {
    }

    /**
     * Get instance of {@link ConfigurableTimestampTypeDescriptor}
     *
     * @param timeZone
     *            The {@link TimeZone} string. If null or empty, UTC is used.
     */
    public ConfigurableTimestampTypeDescriptor(String timeZone) {
        if (!Strings.isNullOrEmpty(timeZone)) {
            this.timeZone = DateTimeZone.forID(timeZone.trim()).toTimeZone();
        }
    }

    @Override
    public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicBinder<X>(javaTypeDescriptor, this) {
            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
                    throws SQLException {
                st.setTimestamp(index, javaTypeDescriptor.unwrap(value, Timestamp.class, options),
                        Calendar.getInstance(timeZone));
            }

            @Override
            protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
                    throws SQLException {
                st.setTimestamp(name, javaTypeDescriptor.unwrap(value, Timestamp.class, options),
                        Calendar.getInstance(timeZone));
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X>(javaTypeDescriptor, this) {
            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                if (rs.getObject(name) != null) {
                    return javaTypeDescriptor.wrap(rs.getTimestamp(name, Calendar.getInstance(timeZone)), options);
                }
                return null;
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getTimestamp(index, Calendar.getInstance(timeZone)), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options)
                    throws SQLException {
                return javaTypeDescriptor.wrap(statement.getTimestamp(name, Calendar.getInstance(timeZone)), options);
            }
        };
    }
}
