/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.n52.io.request.IoParameters;
import org.n52.io.request.RequestSimpleParameterSet;
import org.n52.proxy.db.dao.ProxyFeatureDao;
import org.n52.proxy.db.dao.ProxyProcedureDao;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            List<FeatureEntity> features = new ProxyFeatureDao(getSession()).getAllInstances(new DbQuery(IoParameters.createDefaults()));
            ProxyProcedureDao procedureDao = new ProxyProcedureDao(getSession());
            for (FeatureEntity featureEntity : features) {
                String identifier = featureEntity.getDomainId();
                getCache().addFeatureOfInterest(identifier);
                if (featureEntity.isSetName()) {
                        getCache().addFeatureOfInterestIdentifierHumanReadableName(identifier, featureEntity.getName());
                }
                if (featureEntity.hasParents()) {
                    getCache().addParentFeatures(identifier, getParents(featureEntity));
                }
                getCache().setProceduresForFeatureOfInterest(identifier, getProcedures(procedureDao.getAllInstances(createProcedureDbQuery(featureEntity))));
            }

        } catch (HibernateException | DataAccessException dae) {
            getErrors().add(new NoApplicableCodeException().causedBy(dae).withMessage("Error while updating featureOfInterest cache!"));
        }
        LOGGER.debug("Finished executing FeatureOfInterestCacheUpdate ({})", getStopwatchResult());
    }

    private Collection<String> getProcedures(List<ProcedureEntity> procedures) {
        Set<String> set = Sets.newTreeSet();
        for (ProcedureEntity procedure : procedures) {
            set.add(procedure.getDomainId());
        }
        return set;
    }

    private DbQuery createProcedureDbQuery(FeatureEntity featureEntity) {
        RequestSimpleParameterSet rsps = new RequestSimpleParameterSet();
        rsps.addParameter(IoParameters.FEATURES, IoParameters.getJsonNodeFrom(featureEntity.getPkid()));
        return new DbQuery(IoParameters.createFromQuery(rsps));
    }

    private Collection<String> getParents(FeatureEntity featureEntity) {
        Set<String> parentFeatures = Sets.newTreeSet();
        if (featureEntity.hasParents()) {
            for (FeatureEntity parentEntity : featureEntity.getParents()) {
                parentFeatures.add(parentEntity.getDomainId());
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
                .map(FeatureEntity::getDomainId)
                .collect(Collectors.toSet());
    }
}
