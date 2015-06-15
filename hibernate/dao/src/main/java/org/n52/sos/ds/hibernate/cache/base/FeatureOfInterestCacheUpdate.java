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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class FeatureOfInterestCacheUpdate extends AbstractThreadableDatasourceCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureOfInterestCacheUpdate.class);

    @Override
    public void execute() {
        LOGGER.debug("Executing FeatureOfInterestCacheUpdate");
        startStopwatch();
        // FIXME shouldn't the identifiers be translated using
        // CacheHelper.addPrefixAndGetFeatureIdentifier()?        
        try {
        	FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO();
            Map<String,Collection<String>> foisWithParents = new FeatureOfInterestDAO()
                .getFeatureOfInterestIdentifiersWithParents(getSession());
            List<FeatureOfInterest> featureOfInterestObjects = featureOfInterestDAO.getFeatureOfInterestObjects(getSession());
            
            Map<String, Collection<String>> procsForAllFois = new ProcedureDAO()
                    .getProceduresForAllFeaturesOfInterest(getSession());

            for (final FeatureOfInterest featureOfInterest : featureOfInterestObjects) {
            	String featureOfInterestIdentifier = featureOfInterest.getIdentifier();
                getCache().addFeatureOfInterest(featureOfInterestIdentifier);
                if (featureOfInterest.isSetName()) {
                	getCache().addFeatureOfInterestIdentifierHumanReadableName(featureOfInterestIdentifier, featureOfInterest.getName());
                }
                getCache().setProceduresForFeatureOfInterest(featureOfInterestIdentifier,
                        procsForAllFois.get(featureOfInterestIdentifier));
                Collection<String> parentFois = foisWithParents.get(featureOfInterestIdentifier);
                if (!CollectionHelper.isEmpty(parentFois)) {
                    getCache().addParentFeatures(featureOfInterestIdentifier, parentFois);
                }
            }
            
            FeatureQueryHandlerQueryObject queryHandler =
                    new FeatureQueryHandlerQueryObject().setFeatureIdentifiers(getCache().getFeaturesOfInterest())
                            .setConnection(getSession());
            getCache().setGlobalEnvelope(getFeatureQueryHandler().getEnvelopeForFeatureIDs(queryHandler));
        } catch (final OwsExceptionReport ex) {
            getErrors().add(ex);
        }
        LOGGER.debug("Finished executing FeatureOfInterestCacheUpdate ({})", getStopwatchResult());
    }

    /**
     * Get identifiers from featureOfInterest entities
     * 
     * @param featuresOfInterest
     *            FeatureOfInterest entities
     * @return Identifiers from featureOfInterest entities
     */
    protected Set<String> getFeatureIdentifiers(final Collection<FeatureOfInterest> featuresOfInterest) {
        final Set<String> featureList = new HashSet<String>(featuresOfInterest.size());
        for (final FeatureOfInterest featureOfInterest : featuresOfInterest) {
            featureList.add(featureOfInterest.getIdentifier());
        }
        return featureList;
    }
}
