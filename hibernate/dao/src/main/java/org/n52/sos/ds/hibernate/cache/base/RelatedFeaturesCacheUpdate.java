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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.dao.RelatedFeatureDAO;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.RelatedFeatureRole;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class RelatedFeaturesCacheUpdate extends AbstractThreadableDatasourceCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedFeaturesCacheUpdate.class);

    @Override
    public void execute() {
        LOGGER.debug("Executing RelatedFeaturesCacheUpdate");
        startStopwatch();
        // TODO Carsten: use RelatedFeatures and query...
        if (HibernateHelper.isEntitySupported(RelatedFeature.class)) {
            List<RelatedFeature> relatedFeatures = new RelatedFeatureDAO().getRelatedFeatureObjects(getSession());
            for (RelatedFeature relatedFeature : relatedFeatures) {
                Set<String> roles = new HashSet<String>(relatedFeature.getRelatedFeatureRoles().size());
                for (RelatedFeatureRole relatedFeatureRole : relatedFeature.getRelatedFeatureRoles()) {
                    roles.add(relatedFeatureRole.getRelatedFeatureRole());
                }
                getCache().setRolesForRelatedFeature(relatedFeature.getFeatureOfInterest().getIdentifier(), roles);
            }
        }
        LOGGER.debug("Finished executing RelatedFeaturesCacheUpdate ({})", getStopwatchResult());        
    }
}
