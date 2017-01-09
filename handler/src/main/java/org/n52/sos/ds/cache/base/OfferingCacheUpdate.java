/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.cache.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.concrete.GenericThrowableWrapperException;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.io.request.IoParameters;
import org.n52.proxy.db.dao.ProxyDatasetDao;
import org.n52.proxy.db.dao.ProxyOfferingDao;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.ds.hibernate.cache.AbstractQueueingDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class OfferingCacheUpdate extends AbstractQueueingDatasourceCacheUpdate<OfferingCacheUpdateTask> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferingCacheUpdate.class);
    private static final String THREAD_GROUP_NAME = "offering-cache-update";
    private ProxyOfferingDao offeringDAO;
    private Collection<String> offeringsIdToUpdate = Lists.newArrayList();
    private Collection<OfferingEntity> offeringsToUpdate;
    private Map<String,Collection<DatasetEntity>> offDatasetMap;
    private final Locale defaultLanguage;
    private final I18NDAORepository i18NDAORepository;

    public OfferingCacheUpdate(int threads, Locale defaultLanguage, I18NDAORepository i18NDAORepository, ConnectionProvider connectionProvider) {
        this(threads, defaultLanguage, i18NDAORepository, connectionProvider, null);
    }

    public OfferingCacheUpdate(int threads, Locale defaultLanguage, I18NDAORepository i18NDAORepository, ConnectionProvider connectionProvider, Collection<String> offeringIdsToUpdate) {
        super(threads, THREAD_GROUP_NAME, connectionProvider);
        if (offeringIdsToUpdate != null) {
            this.offeringsIdToUpdate.addAll(offeringIdsToUpdate);
        }
        this.defaultLanguage = defaultLanguage;
        this.i18NDAORepository = i18NDAORepository;
    }

    private Collection<OfferingEntity> getOfferingsToUpdate() {
        try {
            if (offeringDAO == null) {
                offeringDAO = new ProxyOfferingDao(getSession());
            }
            if (offeringsToUpdate == null) {
                offeringsToUpdate = offeringDAO.getInstancesFor(offeringsIdToUpdate);
            }
        } catch (Exception e) {
            getErrors().add(new GenericThrowableWrapperException(e)
                    .withMessage("Error while processing procedure cache update task!"));
        }
        return offeringsToUpdate;
    }

    @SuppressWarnings("unchecked")
    private Map<String,Collection<DatasetEntity>> getOfferingDatasets() throws OwsExceptionReport {
        if (offDatasetMap == null) {
            try {
                offDatasetMap = DatasourceCacheUpdateHelper.mapByOffering(
                    new ProxyDatasetDao(getSession()).getAllInstances(new DbQuery(IoParameters.createDefaults())));
            } catch (HibernateException | DataAccessException dae) {
                throw new NoApplicableCodeException().causedBy(dae).withMessage("Error while querying datasets for offerings");
            }
        }
        return offDatasetMap;
    }

    @Override
    public void execute() {
        LOGGER.debug("Executing OfferingCacheUpdate (Single Threaded Tasks)");
        startStopwatch();
        //perform single threaded updates here
//        for (OfferingEntity offering : getOfferingsToUpdate()) {
//            String identifier = offering.getDomainId();
//            if (shouldOfferingBeProcessed(offering)) {
//                getCache().addOffering(identifier);
//
//                if (offering instanceof TOffering) {
//                    TOffering tOffering = (TOffering) offering;
//                    // Related features
//                    Set<String> relatedFeatures = getRelatedFeatureIdentifiersFrom(tOffering);
//                    if (!relatedFeatures.isEmpty()) {
//                        getCache().setRelatedFeaturesForOffering(identifier, relatedFeatures);
//                    }
//                    getCache().setAllowedObservationTypeForOffering(identifier,
//                            getObservationTypesFromObservationType(tOffering.getObservationTypes()));
//                    // featureOfInterestTypes
//                    getCache().setAllowedFeatureOfInterestTypeForOffering(identifier,
//                            getFeatureOfInterestTypesFromFeatureOfInterestType(tOffering.getFeatureOfInterestTypes()));
//                }
//            }
//        }
        LOGGER.debug("Finished executing OfferingCacheUpdate (Single Threaded Tasks) ({})", getStopwatchResult());

        //execute multi-threaded updates
        LOGGER.debug("Executing OfferingCacheUpdate (Multi-Threaded Tasks)");
        startStopwatch();
        super.execute();
        LOGGER.debug("Finished executing OfferingCacheUpdate (Multi-Threaded Tasks) ({})", getStopwatchResult());
    }

    @Override
    protected OfferingCacheUpdateTask[] getUpdatesToExecute() throws OwsExceptionReport {
        Collection<OfferingCacheUpdateTask> offeringUpdateTasks = Lists.newArrayList();
        boolean hasSamplingGeometry = false;
        for (OfferingEntity offering : getOfferingsToUpdate()){
            if (shouldOfferingBeProcessed(offering)) {
                Collection<DatasetEntity> datasets
                        = getOfferingDatasets().get(offering.getDomainId());
                offeringUpdateTasks.add(new OfferingCacheUpdateTask(
                        offering,
                        datasets,
                        this.defaultLanguage,
                        this.i18NDAORepository));
            }
        }
        return offeringUpdateTasks.toArray(new OfferingCacheUpdateTask[offeringUpdateTasks.size()]);
    }

//    /**
//     * Check if the observation table contains samplingGeometries with values.
//     *
//     * @return <code>true</code>, if the observation table contains samplingGeometries with values
//     */
//    private boolean checkForSamplingGeometry() {
//        try {
//            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
//            return observationDAO.containsSamplingGeometries(getSession());
//        } catch (OwsExceptionReport e) {
//            LOGGER.error("Error while getting observation DAO class from factory!", e);
//            getErrors().add(e);
//        }
//        return false;
//    }

    protected boolean shouldOfferingBeProcessed(OfferingEntity offering) {
        try {
            if (HibernateHelper.isEntitySupported(ObservationConstellation.class)) {
                return getOfferingDatasets().containsKey(offering.getDomainId());
            }
        } catch (OwsExceptionReport e) {
            LOGGER.error("Error while getting observation DAO class from factory!", e);
            getErrors().add(e);
        }
        return false;
    }

    protected Set<String> getObservationTypesFromObservationType(Set<ObservationType> observationTypes) {
        Set<String> obsTypes = new HashSet<>(observationTypes.size());
        for (ObservationType obsType : observationTypes) {
            obsTypes.add(obsType.getObservationType());
        }
        return obsTypes;
    }

    protected Collection<String> getFeatureOfInterestTypesFromFeatureOfInterestType(
            Set<FeatureOfInterestType> featureOfInterestTypes) {
        Set<String> featTypes = new HashSet<>(featureOfInterestTypes.size());
        for (FeatureOfInterestType featType : featureOfInterestTypes) {
            featTypes.add(featType.getFeatureOfInterestType());
        }
        return featTypes;
    }
}
