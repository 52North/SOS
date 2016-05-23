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
package org.n52.sos.ds.hibernate.cache.base;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;
import org.hibernate.spatial.dialect.postgis.PostgisDialect;
import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class SridCacheUpdate extends AbstractThreadableDatasourceCacheUpdate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SridCacheUpdate.class);
    
    private static final String SQL_QUERY_GET_DEFAULT_FEATURE_SRID_POSTGIS = "getDefaultFeatureGeomSridPostgis";

    private static final String SQL_QUERY_GET_DEFAULT_FEATURE_SRID_ORACLE = "getDefaultFeatureGeomSridOracle";

    private static final String SQL_QUERY_GET_DEFAULT_FEATURE_SRID_H2 = "getDefaultFeatureGeomSridGeoDB";
    
    @Override
    public void execute() {
        LOGGER.debug("Executing SridCacheUpdate");
        startStopwatch();
        checkEpsgCode(getSession());
        for (String epsg : GeometryHandler.getInstance().getSupportedCRS()) {
            getCache().addEpsgCode(Integer.valueOf(epsg));
        }
        LOGGER.debug("Finished executing SridCacheUpdate ({})", getStopwatchResult());
    }

    @SuppressWarnings({ "unchecked", "unused" })
    private boolean checkAndGetEpsgCodes(Session session) {
        Dialect dialect = HibernateHelper.getDialect(session);
        String namedQueryName = null;
        if (dialect instanceof PostgisDialect || dialect instanceof PostgreSQL81Dialect) {
            namedQueryName = SQL_QUERY_GET_DEFAULT_FEATURE_SRID_POSTGIS;
        } else if (dialect instanceof Oracle8iDialect) {
            namedQueryName = SQL_QUERY_GET_DEFAULT_FEATURE_SRID_ORACLE;
        } else if (dialect instanceof GeoDBDialect) {
            namedQueryName = SQL_QUERY_GET_DEFAULT_FEATURE_SRID_H2;
        }
        if (StringHelper.isNotEmpty(namedQueryName) && HibernateHelper.isNamedQuerySupported(namedQueryName, session)) {
            Query namedQuery = session.getNamedQuery(namedQueryName);
            LOGGER.debug("QUERY checkAndGetEpsgCodes() with NamedQuery: {}", namedQuery);
            getCache().addEpsgCodes(namedQuery.list());
            return true;
        }
        return false;
    }
    
    private void checkEpsgCode(Session session) {
        Dialect dialect = HibernateHelper.getDialect(session);
        String namedQueryName = null;
        if (dialect instanceof PostgisDialect || dialect instanceof PostgreSQL81Dialect) {
            namedQueryName = SQL_QUERY_GET_DEFAULT_FEATURE_SRID_POSTGIS;
        } else if (dialect instanceof Oracle8iDialect) {
            namedQueryName = SQL_QUERY_GET_DEFAULT_FEATURE_SRID_ORACLE;
        } else if (dialect instanceof GeoDBDialect) {
            namedQueryName = SQL_QUERY_GET_DEFAULT_FEATURE_SRID_H2;
        }
        if (StringHelper.isNotEmpty(namedQueryName) && HibernateHelper.isNamedQuerySupported(namedQueryName, session)) {
            Query namedQuery = session.getNamedQuery(namedQueryName);
            LOGGER.debug("QUERY checkEpsgCode() with NamedQuery: {}", namedQuery);
            Integer uniqueResult = (Integer) namedQuery.uniqueResult();
            if (GeometryHandler.getInstance().getStorageEPSG() != uniqueResult) {
                GeometryHandler.getInstance().setStorageEpsg(uniqueResult);
            }
        }
    }
}
