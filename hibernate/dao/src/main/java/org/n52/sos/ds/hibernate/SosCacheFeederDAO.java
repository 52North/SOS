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
package org.n52.sos.ds.hibernate;

import static org.n52.sos.ds.hibernate.CacheFeederSettingDefinitionProvider.CACHE_THREAD_COUNT;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.CacheFeederDAO;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.cache.InitialCacheUpdate;
import org.n52.sos.ds.hibernate.cache.base.OfferingCacheUpdate;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the interface CacheFeederDAO
 * 
 * @since 4.0.0
 */
@Configurable
public class SosCacheFeederDAO extends HibernateSessionHolder implements CacheFeederDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosCacheFeederDAO.class);

    /**
     * Defines the number of threads available in the thread pool of the cache
     * update executor service.
     */
    private int cacheThreadCount = 5;

    /**
     * Get the defined cache thread count or the maximum connection count minus
     * one to avoid hanging threads during the cache update.
     * 
     * @return Defined cache thread count or the maximum connection count minus
     *         one
     */
    public int getCacheThreadCount() {
        int maxConnections = getConnectionProvider().getMaxConnections();
        return maxConnections > 0 && cacheThreadCount >= maxConnections ? maxConnections - 1 : cacheThreadCount;
    }

    @Setting(CACHE_THREAD_COUNT)
    public void setCacheThreadCount(int threads) throws ConfigurationException {
        Validation.greaterZero("Cache Thread Count", threads);
        this.cacheThreadCount = threads;
    }

    @Override
    public void updateCache(WritableContentCache cache) throws OwsExceptionReport {
        checkCacheNotNull(cache);
        List<OwsExceptionReport> errors = CollectionHelper.synchronizedList();
        Session session = null;
        try {
            InitialCacheUpdate update = new InitialCacheUpdate(getCacheThreadCount());
            session = getSession();
            update.setCache(cache);
            update.setErrors(errors);
            update.setSession(session);

            LOGGER.info("Starting cache update");
            long cacheUpdateStartTime = System.currentTimeMillis();

            update.execute();

            logCacheLoadTime(cacheUpdateStartTime);
        } catch (Exception e) {
            LOGGER.error("Error while updating ContentCache!", e);
            errors.add(new NoApplicableCodeException().causedBy(e).withMessage("Error while updating ContentCache!"));
        } finally {
            returnSession(session);
        }
        if (!errors.isEmpty()) {
            throw new CompositeOwsException(errors);
        }
        
    }

    @Override
    public void updateCacheOfferings(WritableContentCache cache, Collection<String> offeringsNeedingUpdate)
            throws OwsExceptionReport {
        checkCacheNotNull(cache);
        if (CollectionHelper.isEmpty(offeringsNeedingUpdate)) {
            return;
        }
        List<OwsExceptionReport> errors = CollectionHelper.synchronizedList();
        Session session = getSession();
        OfferingCacheUpdate update = new OfferingCacheUpdate(getCacheThreadCount(), offeringsNeedingUpdate);
        update.setCache(cache);
        update.setErrors(errors);
        update.setSession(session);
        
        LOGGER.info("Starting offering cache update for {} offering(s)", offeringsNeedingUpdate.size());
        long cacheUpdateStartTime = System.currentTimeMillis();

        try {
            update.execute();
        } catch (Exception e) {
            LOGGER.error("Error while updating ContentCache!", e);
            errors.add(new NoApplicableCodeException().causedBy(e).withMessage("Error while updating ContentCache!"));
        } finally {
            returnSession(session);
        }

        logCacheLoadTime(cacheUpdateStartTime);

        if (!errors.isEmpty()) {
            throw new CompositeOwsException(errors);
        }
    }

    private void checkCacheNotNull(WritableContentCache cache) {
        if (cache == null) {
            throw new NullPointerException("cache is null");
        }        
    }

    private void logCacheLoadTime(long startTime) {
        Period cacheLoadPeriod = new Period(startTime, System.currentTimeMillis());
        LOGGER.info("Cache load finished in {} ({} seconds)",
                PeriodFormat.getDefault().print(cacheLoadPeriod.normalizedStandard()),
                cacheLoadPeriod.toStandardSeconds());         
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }
}
