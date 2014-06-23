/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.envirocar;

import java.util.Collection;
import java.util.Set;

import org.envirocar.server.core.dao.SensorDao;
import org.envirocar.server.core.filter.TrackFilter;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.ds.CacheFeederDAO;
import org.n52.sos.ds.EnviroCarConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class EnviroCarCacheFeederDAO extends EnviroCarDaoFactoryHolder implements CacheFeederDAO {


    private static final Logger LOGGER = LoggerFactory.getLogger(EnviroCarCacheFeederDAO.class);

    /**
     * Defines the number of threads available in the thread pool of the cache
     * update executor service.
     */
    private int cacheThreadCount = 5;

    public int getCacheThreadCount() {
        return cacheThreadCount;
    }


    @Override
    public void updateCache(WritableContentCache cache) throws OwsExceptionReport {
        checkCacheNotNull(cache);
        getOfferings(cache);
        getObservableProperties(cache);
        
//        List<OwsExceptionReport> errors = CollectionHelper.synchronizedList();
//        Session session = null;
//        try {
//            EnviroCarInitialCacheUptate update = new EnviroCarInitialCacheUptate(getCacheThreadCount());
//            session = getSession();
//            update.setCache(cache);
//            update.setErrors(errors);
//            update.setSession(session);
//
//            LOGGER.info("Starting cache update");
//            long cacheUpdateStartTime = System.currentTimeMillis();
//
//            update.execute();
//
//            logCacheLoadTime(cacheUpdateStartTime);
//        } catch (HibernateException he) {
//            LOGGER.error("Error while updating ContentCache!", he);
//        } finally {
//            returnSession(session);
//        }
//        if (!errors.isEmpty()) {
//            throw new CompositeOwsException(errors);
//        }
    }

    @Override
    public void updateCacheOfferings(WritableContentCache cache, Collection<String> offeringsNeedingUpdate)
            throws OwsExceptionReport {
        checkCacheNotNull(cache);
//        if (CollectionHelper.isEmpty(offeringsNeedingUpdate)) {
//            return;
//        }
//        List<OwsExceptionReport> errors = CollectionHelper.synchronizedList();
//        Session session = getSession();
//        EnviroCarOfferingCacheUpdate update = new EnviroCarOfferingCacheUpdate(getCacheThreadCount(), offeringsNeedingUpdate);
//        update.setCache(cache);
//        update.setErrors(errors);
//        update.setSession(session);
//        
//        LOGGER.info("Starting offering cache update for {} offering(s)", offeringsNeedingUpdate.size());
//        long cacheUpdateStartTime = System.currentTimeMillis();
//
//        try {
//            update.execute();
//        } catch (HibernateException he) {
//            LOGGER.error("Error while updating ContentCache!", he);
//        } finally {
//            returnSession(session);
//        }
//
//        logCacheLoadTime(cacheUpdateStartTime);
//
//        if (!errors.isEmpty()) {
//            throw new CompositeOwsException(errors);
//        }
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
        return EnviroCarConstants.ENVIROCAR_DATASOURCE_DAO_IDENTIFIER;
    }


    private void getOfferings(WritableContentCache cache) throws OwsExceptionReport {
        SensorDao sensorDAO = getEnviroCarDaoFactory().getSensorDAO();
        Set<String> s = Sets.newHashSet(sensorDAO.getTypes());
        cache.setOfferings(s);
        for (String offering : s) {
            cache.setNameForOffering(offering, offering);
            // procedures
            cache.setProceduresForOffering(offering, Sets.newHashSet(offering));
            // Observable properties
            cache.setObservablePropertiesForOffering(offering, getObservablePropertyIdentifier(offering, getEnviroCarDaoFactory()));

            // Observation types
//            cache.setObservationTypesForOffering(offering, );

            // Features of Interest
//            List<String> featureOfInterestIdentifiers =
//                    featureDAO.getFeatureOfInterestIdentifiersForOffering(offering, session);
//            cache.setFeaturesOfInterestForOffering(offering,
//                    getValidFeaturesOfInterestFrom(featureOfInterestIdentifiers));
//            cache.setFeatureOfInterestTypesForOffering(offering,
//                    getFeatureOfInterestTypes(featureOfInterestIdentifiers, session));
//
//            // Spatial Envelope
//            cache.setEnvelopeForOffering(offering, getEnvelopeForOffering(featureOfInterestIdentifiers, session));
//            // Spatial Filtering Profile Spatial Envelope
//            addSpatialFilteringProfileEnvelopeForOffering(offering, offeringId, session);
            
//            getCache().setNameForOffering(prefixedOfferingId, getOfferingName(prefixedOfferingId, offeringName));
//            getCache().setMinPhenomenonTimeForOffering(offeringId, ote.getMinPhenomenonTime());
//            getCache().setMaxPhenomenonTimeForOffering(offeringId, ote.getMaxPhenomenonTime());
//            getCache().setMinResultTimeForOffering(offeringId, ote.getMinResultTime());
//            getCache().setMaxResultTimeForOffering(offeringId, ote.getMaxResultTime());
        }
    }


    private Collection<String> getObservablePropertyIdentifier(String offering, EnviroCarDaoFactory enviroCarDaoFactory) {
//        enviroCarDaoFactory.getMeasurementDAO().get(new MeasurementFilter())
        return null;
    }


    private void getObservableProperties(WritableContentCache cache) {
//        List<ObservableProperty> ops = new ObservablePropertyDAO().getObservablePropertyObjects(getSession());
//        //if ObservationConstellation is supported load them all at once, otherwise query obs directly
//        if (HibernateHelper.isEntitySupported(ObservationConstellation.class, getSession())) {
//            Map<String, Collection<ObservationConstellationInfo>> ociMap = ObservationConstellationInfo.mapByObservableProperty(
//                    new ObservationConstellationDAO().getObservationConstellationInfo(getSession()));
//            for (ObservableProperty op : ops) {
//                final String obsPropIdentifier = op.getIdentifier();
//                Collection<ObservationConstellationInfo> ocis = ociMap.get(obsPropIdentifier);
//                if (CollectionHelper.isNotEmpty(ocis)) {
//                    getCache().setOfferingsForObservableProperty(obsPropIdentifier,
//                            DatasourceCacheUpdateHelper.getAllOfferingIdentifiersFromObservationConstellationInfos(ocis));
//                    getCache().setProceduresForObservableProperty(obsPropIdentifier,
//                            DatasourceCacheUpdateHelper.getAllProcedureIdentifiersFromObservationConstellationInfos(ocis));
//                }
//            }
//        } else {
//            for (ObservableProperty op : ops) {
//                final String obsPropIdentifier = op.getIdentifier();
//                try {
//                    getCache().setOfferingsForObservableProperty(obsPropIdentifier,
//                            new OfferingDAO().getOfferingIdentifiersForObservableProperty(obsPropIdentifier, getSession()));
//                } catch (CodedException e) {
//                    getErrors().add(e);
//                }
//                getCache().setProceduresForObservableProperty(obsPropIdentifier,
//                        new ProcedureDAO().getProcedureIdentifiersForObservableProperty(obsPropIdentifier, getSession()));                
//            }
//        }
    }

}
