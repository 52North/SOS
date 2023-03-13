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
package org.n52.sos.ds.hibernate.util;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.ByteType;
import org.hibernate.type.CharacterType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.StringType;
import org.hibernate.type.TrueFalseType;
import org.hibernate.type.Type;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MoreProjections {

    public <T, X extends Type & LiteralType<T> & SingleColumnType<T>> Projection constant(T value, X type) {
        return new ConstantProjection<>(value, type);
    }

    public Projection constant(String value) {
        return constant(value, StringType.INSTANCE);
    }

    public Projection constant(boolean value) {
        return constant(value, TrueFalseType.INSTANCE);
    }

    public Projection constant(double value) {
        return constant(value, DoubleType.INSTANCE);
    }

    public Projection constant(float value) {
        return constant(value, FloatType.INSTANCE);
    }

    public Projection constant(long value) {
        return constant(value, LongType.INSTANCE);
    }

    public Projection constant(int value) {
        return constant(value, IntegerType.INSTANCE);
    }

    public Projection constant(short value) {
        return constant(value, ShortType.INSTANCE);
    }

    public Projection constant(byte value) {
        return constant(value, ByteType.INSTANCE);
    }

    public Projection constant(char value) {
        return constant(value, CharacterType.INSTANCE);
    }

    public Projection constant(Date value) {
        return constant(value, DateType.INSTANCE);
    }

    public Projection constant(BigInteger value) {
        return constant(value, BigIntegerType.INSTANCE);
    }

    private static class ConstantProjection<T, X extends Type & LiteralType<T> & SingleColumnType<T>>
            extends SimpleProjection {
        private static final long serialVersionUID = -754276448432073689L;
        private final X type;
        private final T value;

        ConstantProjection(T value, X type) {
            this.type = Objects.requireNonNull(type);
            this.value = value;
        }

        @Override
        public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) {
            try {
                Dialect dialect = criteriaQuery.getFactory().getDialect();
                StringBuilder buf = new StringBuilder();
                if (this.value == null) {
                    buf.append("null");
                } else {
                    buf.append(this.type.objectToSQLString(this.value, dialect));
                }
                return buf.append(" as y").append(position).append("_").toString();
            } catch (Exception ex) {
                throw new HibernateException(ex);
            }
        }

        @Override
        public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) {
            return new Type[] { this.type };
        }
    }
}
