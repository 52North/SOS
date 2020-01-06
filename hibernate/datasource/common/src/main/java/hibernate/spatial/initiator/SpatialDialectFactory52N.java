/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package hibernate.spatial.initiator;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.internal.DialectFactoryImpl;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.spatial.HibernateSpatialConfiguration;
import org.hibernate.spatial.dialect.oracle.OracleSpatial10gDialect;

import hibernate.spatial.dialect.oracle.OracleSpatial10gDoubleFloatDialect;

public class SpatialDialectFactory52N extends DialectFactoryImpl {

    final private HibernateSpatialConfiguration configuration;

    public SpatialDialectFactory52N(HibernateSpatialConfiguration configuration) {
        super();
        this.configuration = configuration;
    }

    @Override
    public Dialect buildDialect(Map configValues, DialectResolutionInfoSource resolutionInfoSource)
            throws HibernateException {
        Dialect dialect = super.buildDialect(configValues, resolutionInfoSource);
        if (dialect instanceof OracleSpatial10gDialect) {
            return new OracleSpatial10gDoubleFloatDialect(configuration);
        } else {
            return dialect;
        }
    }
}
