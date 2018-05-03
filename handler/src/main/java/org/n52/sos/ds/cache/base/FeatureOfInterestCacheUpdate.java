/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.n52.io.request.IoParameters;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.dataset.NotInitializedDataset;
import org.n52.series.db.dao.DatasetDao;
import org.n52.series.db.dao.DbQuery;
import org.n52.series.db.dao.FeatureDao;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.sos.ds.cache.AbstractThreadableDatasourceCacheUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
        try {
            Collection<FeatureEntity> features = new FeatureDao(getSession()).get(new DbQuery(IoParameters.createDefaults()));
            for (FeatureEntity featureEntity : features) {
                String identifier = featureEntity.getIdentifier();
                getCache().addFeatureOfInterest(identifier);
                Collection<DatasetEntity> datasets = new DatasetDao<>(getSession()).get(createDatasetDbQuery(featureEntity));
                if (datasets != null && !datasets.isEmpty()) {
                    if (datasets.stream().anyMatch(d -> d.isPublished() || d instanceof NotInitializedDataset)) {
                        getCache().addPublishedFeatureOfInterest(identifier);
                    }
                    getCache().setProceduresForFeatureOfInterest(identifier, getProcedures(datasets));
                }
                if (featureEntity.isSetName()) {
                        getCache().addFeatureOfInterestIdentifierHumanReadableName(identifier, featureEntity.getName());
                }
                if (featureEntity.hasParents()) {
                    getCache().addParentFeatures(identifier, getParents(featureEntity));
                }
            }
        } catch (HibernateException he) {
            getErrors().add(new NoApplicableCodeException().causedBy(he).withMessage("Error while updating featureOfInterest cache!"));
        }
        LOGGER.debug("Finished executing FeatureOfInterestCacheUpdate ({})", getStopwatchResult());
    }

    private Collection<String> getProcedures(Collection<DatasetEntity> datasets) {
        return datasets.stream().filter(d -> d.getProcedure() != null).map(d -> d.getProcedure().getIdentifier())
                .collect(Collectors.toSet());
    }

    private DbQuery createProcedureDbQuery(FeatureEntity featureEntity) {
        Map<String, String> map = Maps.newHashMap();
        map.put(IoParameters.FEATURES, Long.toString(featureEntity.getId()));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    private Collection<String> getParents(FeatureEntity featureEntity) {
        Set<String> parentFeatures = Sets.newTreeSet();
        if (featureEntity.hasParents()) {
            for (FeatureEntity parentEntity : featureEntity.getParents()) {
                parentFeatures.add(parentEntity.getIdentifier());
                parentFeatures.addAll(getParents(parentEntity));
            }
        }
        return parentFeatures;
    }

    /**
     * Get identifiers from featureOfInterest entities
     *
     * @param featuresOfInterest
     *            FeatureOfInterest entities
     * @return Identifiers from featureOfInterest entities
     */
    protected Set<String> getFeatureIdentifiers(Collection<FeatureEntity> features) {
        return features.stream()
                .map(FeatureEntity::getIdentifier)
                .collect(Collectors.toSet());
    }

    private DbQuery createDatasetDbQuery(FeatureEntity feature) {
        IoParameters parameters = IoParameters.createDefaults();
        parameters.extendWith(IoParameters.FEATURES, Long.toString(feature.getId()));
        return new DbQuery(parameters);
    }
}
