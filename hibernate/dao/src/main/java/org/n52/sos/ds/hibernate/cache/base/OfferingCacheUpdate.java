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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.n52.sos.ds.hibernate.cache.AbstractQueueingDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.dao.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO.OfferingTimeExtrema;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.TOffering;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationConstellationInfo;
import org.n52.sos.exception.CodedException;
import org.n52.sos.util.CacheHelper;
import org.n52.sos.util.StringHelper;
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

    private final OfferingDAO offeringDAO = new OfferingDAO();

    private Collection<String> offeringsIdToUpdate = Lists.newArrayList();

    private Collection<Offering> offeringsToUpdate;
    
    private Map<String,Collection<ObservationConstellationInfo>> offObsConstInfoMap;
    
    /**
     * constructor
     * @param threads Thread count
     */
    public OfferingCacheUpdate(int threads) {
        super(threads, THREAD_GROUP_NAME);
    }

    public OfferingCacheUpdate(int threads, Collection<String> offeringIdsToUpdate) {
        super(threads, THREAD_GROUP_NAME);
        this.offeringsIdToUpdate = offeringIdsToUpdate;
    }

    private Collection<Offering> getOfferingsToUpdate() {
        if (offeringsToUpdate == null) {            
            offeringsToUpdate = offeringDAO.getOfferingObjectsForCacheUpdate(offeringsIdToUpdate, getSession());
        }
        return offeringsToUpdate;
    }

    private Map<String,Collection<ObservationConstellationInfo>> getOfferingObservationConstellationInfo() {
        if (offObsConstInfoMap == null) {
            offObsConstInfoMap = ObservationConstellationInfo.mapByOffering(
                new ObservationConstellationDAO().getObservationConstellationInfo(getSession()));
        }
        return offObsConstInfoMap;
    }

    @Override
    public void execute() {        
        LOGGER.debug("Executing OfferingCacheUpdate (Single Threaded Tasks)");
        startStopwatch();
        //perform single threaded updates here
        for (Offering offering : getOfferingsToUpdate()){
            String offeringId = offering.getIdentifier();
            if (shouldOfferingBeProcessed(offeringId)) {
                String offeringName = offering.getName();
    
                String prefixedOfferingId = CacheHelper.addPrefixOrGetOfferingIdentifier(offeringId);
                
                getCache().addOffering(prefixedOfferingId);
                getCache().setNameForOffering(prefixedOfferingId, getOfferingName(prefixedOfferingId, offeringName));
                
                if (offering instanceof TOffering) {
                    TOffering tOffering = (TOffering) offering;
                    // Related features
                    getCache().setRelatedFeaturesForOffering(prefixedOfferingId,
                            getRelatedFeatureIdentifiersFrom(tOffering));
                    getCache().setAllowedObservationTypeForOffering(prefixedOfferingId,
                            getObservationTypesFromObservationType(tOffering.getObservationTypes()));
                    // featureOfInterestTypes
                    getCache().setAllowedFeatureOfInterestTypeForOffering(prefixedOfferingId,
                            getFeatureOfInterestTypesFromFeatureOfInterestType(tOffering.getFeatureOfInterestTypes()));
                }   
            }
        }

        //time ranges
        //TODO querying offering time extrema in a single query is definitely faster for a properly
        //     indexed Postgres db, but may not be true for all platforms. move back to multithreaded execution
        //     in OfferingCacheUpdateTask if needed
        Map<String, OfferingTimeExtrema> offeringTimeExtrema = null;
        try {
            offeringTimeExtrema = offeringDAO.getOfferingTimeExtrema(offeringsIdToUpdate, getSession());
        } catch (CodedException ce) {
            LOGGER.error("Error while querying offering time ranges!", ce);
            getErrors().add(ce);
        }
        if (!CollectionHelper.isEmpty(offeringTimeExtrema)) {
            for (Entry<String,OfferingTimeExtrema> entry : offeringTimeExtrema.entrySet()) {
                String offeringId = entry.getKey();
                OfferingTimeExtrema ote = entry.getValue();
                getCache().setMinPhenomenonTimeForOffering(offeringId, ote.getMinPhenomenonTime());
                getCache().setMaxPhenomenonTimeForOffering(offeringId, ote.getMaxPhenomenonTime());
                getCache().setMinResultTimeForOffering(offeringId, ote.getMinResultTime());
                getCache().setMaxResultTimeForOffering(offeringId, ote.getMaxResultTime());
            }
        }
        LOGGER.debug("Finished executing OfferingCacheUpdate (Single Threaded Tasks) ({})", getStopwatchResult());
        
        //execute multi-threaded updates
        LOGGER.debug("Executing OfferingCacheUpdate (Multi-Threaded Tasks)");
        startStopwatch();
        super.execute();
        LOGGER.debug("Finished executing OfferingCacheUpdate (Multi-Threaded Tasks) ({})", getStopwatchResult());
    }

    @Override
    protected OfferingCacheUpdateTask[] getUpdatesToExecute() {
        Collection<OfferingCacheUpdateTask> offeringUpdateTasks = Lists.newArrayList();
        for (Offering offering : getOfferingsToUpdate()){
            if (shouldOfferingBeProcessed(offering.getIdentifier())) {
                offeringUpdateTasks.add(new OfferingCacheUpdateTask(offering.getIdentifier(),
                        getOfferingObservationConstellationInfo().get(offering.getIdentifier())));
            }
        }
        return offeringUpdateTasks.toArray(new OfferingCacheUpdateTask[offeringUpdateTasks.size()]);
    }    
    
    protected boolean shouldOfferingBeProcessed(String offeringIdentifier) {
        try {        
            if (HibernateHelper.isEntitySupported(ObservationConstellation.class, getSession())) {
                return getOfferingObservationConstellationInfo().containsKey(offeringIdentifier);
            } else {
                AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO(getSession());
                Criteria criteria = observationDAO.getDefaultObservationInfoCriteria(getSession());
                criteria.createCriteria(AbstractObservation.OFFERINGS).add(
                        Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
                criteria.setProjection(Projections.rowCount());
                LOGGER.debug("QUERY shouldOfferingBeProcessed(offering): {}", HibernateHelper.getSqlString(criteria));
                return (Long) criteria.uniqueResult() > 0;
            }
        } catch (CodedException ce) {
            LOGGER.error("Error while getting observation DAO class from factory!", ce);
            getErrors().add(ce);
        }
        return false;
    }
    
    private String getOfferingName(String offeringIdentifier, String offeringName){
        if (!StringHelper.isNotEmpty(offeringName)) {
            offeringName = offeringIdentifier;
            if (offeringName.startsWith("http")) {
                offeringName = offeringName.substring(offeringName.lastIndexOf('/') + 1, offeringName.length());
            } else if (offeringName.startsWith("urn")) {
                offeringName = offeringName.substring(offeringName.lastIndexOf(':') + 1, offeringName.length());
            }
            if (offeringName.contains("#")) {
                offeringName = offeringName.substring(offeringName.lastIndexOf('#') + 1, offeringName.length());
            }
        }
        return offeringName;
    }

    protected Set<String> getObservationTypesFromObservationType(Set<ObservationType> observationTypes) {
        Set<String> obsTypes = new HashSet<String>(observationTypes.size());
        for (ObservationType obsType : observationTypes) {
            obsTypes.add(obsType.getObservationType());
        }
        return obsTypes;
    }

    protected Collection<String> getFeatureOfInterestTypesFromFeatureOfInterestType(
            Set<FeatureOfInterestType> featureOfInterestTypes) {
        Set<String> featTypes = new HashSet<String>(featureOfInterestTypes.size());
        for (FeatureOfInterestType featType : featureOfInterestTypes) {
            featTypes.add(featType.getFeatureOfInterestType());
        }
        return featTypes;
    }

    protected Set<String> getRelatedFeatureIdentifiersFrom(TOffering hOffering) {
        Set<String> relatedFeatureList = new HashSet<String>(hOffering.getRelatedFeatures().size());
        for (RelatedFeature hRelatedFeature : hOffering.getRelatedFeatures()) {
            if (hRelatedFeature.getFeatureOfInterest() != null
                    && hRelatedFeature.getFeatureOfInterest().getIdentifier() != null) {
                relatedFeatureList.add(hRelatedFeature.getFeatureOfInterest().getIdentifier());
            }
        }
        return relatedFeatureList;
    }
}