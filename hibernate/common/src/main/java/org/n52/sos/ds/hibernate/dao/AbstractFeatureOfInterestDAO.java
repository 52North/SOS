/**
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
package org.n52.sos.ds.hibernate.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractFeatureOfInterestDAO extends AbstractIdentifierNameDescriptionDAO implements HibernateSqlQueryConstants {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFeatureOfInterestDAO.class);

    public abstract AbstractFeatureOfInterest insertFeature(AbstractFeature samplingFeature, Session session) throws OwsExceptionReport;
    
    public AbstractFeatureOfInterest getFeature(String identifier, Session session) {
        Criteria criteria = getDefaultCriteria(session)
                .add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getFeature(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (AbstractFeatureOfInterest) criteria.uniqueResult();
    }
    

    @SuppressWarnings("unchecked")
    public List<String> getFeatureIdentifiers(SpatialFilter filter, Session session) throws OwsExceptionReport {
        final Criteria c = getDefaultCriteria(session)
                .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
        if (filter != null) {
            c.add(SpatialRestrictions.filter(FeatureOfInterest.GEOMETRY, filter.getOperator(), filter.getGeometry()));
        }
        return c.list();
    }

    public Geometry getFeatureExtent(Set<String> identifiers, Session session) {
        return (Geometry) getDefaultCriteria(session)
                .add(QueryHelper.getCriterionForIdentifiers(FeatureOfInterest.IDENTIFIER, identifiers))
                .setProjection(SpatialProjections.extent(FeatureOfInterest.GEOMETRY))
                .uniqueResult();
    }

    /**
     * Get featureOfInterest objects for featureOfInterest identifiers
     *
     * @param identifiers
     *            FeatureOfInterest identifiers
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest objects
     */
    @SuppressWarnings("unchecked")
    public List<AbstractFeatureOfInterest> getFeatureOfInterestObjects(final Collection<String> identifiers,
            final Session session) {
        if (identifiers != null && !identifiers.isEmpty()) {
            Criteria criteria = getDefaultCriteria(session)
                    .add(QueryHelper.getCriterionForIdentifiers(FeatureOfInterest.IDENTIFIER, identifiers));
            LOGGER.debug("QUERY getFeatureOfInterestObjects(identifiers): {}", HibernateHelper.getSqlString(criteria));
            return criteria.list();
        }
        return Collections.emptyList();
    }
    
    protected AbstractFeatureOfInterest getFeatureOfInterest(final String identifier, final Geometry geometry,
            final Session session) throws OwsExceptionReport {
        if (!identifier.startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX)) {
            return (FeatureOfInterest) getDefaultCriteria(session)
                    .add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, identifier)).uniqueResult();
        } else {
            return (FeatureOfInterest) getDefaultCriteria(session)
                    .add(SpatialRestrictions.eq(FeatureOfInterest.GEOMETRY, GeometryHandler.getInstance()
                            .switchCoordinateAxisFromToDatasourceIfNeeded(geometry))).uniqueResult();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<AbstractFeatureOfInterest> getFeatures(Session session) {
        return getDefaultCriteria(session).list();
    }

    @SuppressWarnings("unchecked")
    public List<AbstractFeatureOfInterest> getFeatures(Set<String> identifiers, List<SpatialFilter> filters,
            Session session) throws OwsExceptionReport {
        final Criteria c = getDefaultCriteria(session);
        boolean filtered = false;
        if (CollectionHelper.isNotEmpty(identifiers)) {
            c.add(QueryHelper.getCriterionForIdentifiers(FeatureOfInterest.IDENTIFIER, identifiers));
            filtered = true;
        }
        if (CollectionHelper.isNotEmpty(filters)) {
            final Disjunction disjunction = Restrictions.disjunction();
            for (final SpatialFilter filter : filters) {
                disjunction.add(SpatialRestrictions.filter(FeatureOfInterest.GEOMETRY, filter.getOperator(),
                        filter.getGeometry()));
            }
            c.add(disjunction);
            filtered = true;
        }
        if (filtered) {
            return c.list();
        } else {
            return null;
        }
    }

    protected Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(AbstractFeatureOfInterest.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }
    
}
