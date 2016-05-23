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
package org.hibernate.spatial.dialect.mysql;

import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;

/**
 * Hibernate Spatial {@link Dialect} for MySQL that registers Types.TIMESTAMP to
 * timestampt instead of datetime.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.3.0
 *
 */
public class MySQLSpatial5InnoDBTimestampDialect extends MySQLSpatial5InnoDBDialect {

    private static final long serialVersionUID = 4518550802945449263L;

    public MySQLSpatial5InnoDBTimestampDialect() {
        super();
        registerColumnType( Types.TIMESTAMP, "timestamp" );
    }
    
    @Override
    public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
        if (Types.TIMESTAMP == code ) {
            return "timestamp";
        }
        return super.getTypeName(code, length, precision, scale);
    }
    
    @Override
    public String getTypeName(int code) throws HibernateException {
        if (Types.TIMESTAMP == code ) {
            return "timestamp";
        }
        return super.getTypeName(code);
    }

}
