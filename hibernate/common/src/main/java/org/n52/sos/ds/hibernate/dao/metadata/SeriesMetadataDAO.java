/**
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
package org.n52.sos.ds.hibernate.dao.metadata;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.entities.metadata.SeriesMetadata;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class SeriesMetadataDAO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesMetadataDAO.class);

    @SuppressWarnings("unchecked")
    public List<SeriesMetadata> getMetadata(long series, Session session) {
        Criteria c = getDefaultSeriesCriteria(session);
        addSeriesRestriction(series, c);
        LOGGER.debug("QUERY getMetadata(series): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }

    private Criteria getDefaultSeriesCriteria(Session session) {
        return session.createCriteria(SeriesMetadata.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    @SuppressWarnings("unchecked")
    public List<SeriesMetadata> getDomainMetadata(long seriesId, String domainIdentifier, Session session) {
        Criteria c = getDefaultSeriesCriteria(session);
        addSeriesRestriction(seriesId, c);
        addDomainRestriction(domainIdentifier, c);
        LOGGER.debug("QUERY getDomainMetadata(seriesId, domainIdentifier): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }

    public Optional<String> getMetadataElement(List<SeriesMetadata> seriesMetadata, String domainIdentifier,
            String identifier) {
        for (SeriesMetadata seriesMetadataElement : seriesMetadata) {
            if (seriesMetadataElement.getDomain().equals(domainIdentifier) &&
                    seriesMetadataElement.getIdentifier().equals(identifier)) {
                return Optional.fromNullable(seriesMetadataElement.getValue());
            }
        }
        return Optional.absent();
    }

    private void addSeriesRestriction(long series, Criteria c) {
        c.add(Restrictions.eq(SeriesMetadata.SERIES_ID, series));
    }

    private void addDomainRestriction(String domainIdentifier, Criteria c) {
        c.add(Restrictions.eq(SeriesMetadata.DOMAIN, domainIdentifier));
    }

}
