/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.locationtech.jts.geom.Geometry;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;

public abstract class AbstractFeatureOfInterestDAO extends AbstractIdentifierNameDescriptionDAO
        implements HibernateSqlQueryConstants {

    public AbstractFeatureOfInterestDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    public abstract AbstractFeatureEntity insertFeature(AbstractFeature samplingFeature, Session session)
            throws OwsExceptionReport;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AbstractFeatureEntity getFeature(String identifier, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getFeatureEntityClass());
        Root root = query.from(getFeatureEntityClass());
        query.where(cb.equal(root.get(AbstractFeatureEntity.IDENTIFIER), identifier));
        return (AbstractFeatureEntity) session.createQuery(query).uniqueResult();
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<AbstractFeatureEntity> getFeatureOfInterestObjects(final Collection<String> identifiers,
            final Session session) {
        if (identifiers != null && !identifiers.isEmpty()) {
            List<AbstractFeatureEntity> features = new ArrayList<>();
            for (List<String> ids : QueryHelper.getListsForIdentifiers(identifiers)) {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery query = cb.createQuery(getFeatureEntityClass());
                Root root = query.from(getFeatureEntityClass());
                query.where(root.get(AbstractFeatureEntity.IDENTIFIER).in(ids));
                features.addAll(session.createQuery(query).list());
            }
            return features;
        } else {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery(getFeatureEntityClass());
            query.from(getFeatureEntityClass());
            return session.createQuery(query).list();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected AbstractFeatureEntity getFeatureOfInterest(final String identifier, final Geometry geometry,
            final Session session) throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getFeatureEntityClass());
        Root root = query.from(getFeatureEntityClass());
        if (!identifier.startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX)) {
            query.where(cb.equal(root.get(AbstractFeatureEntity.IDENTIFIER), identifier));
        } else {
            query.where(SpatialRestrictions.eq(cb, root.get(AbstractFeatureEntity.PROPERTY_GEOMETRY_ENTITY),
                    getDaoFactory().getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry)));
        }
        return (AbstractFeatureEntity) session.createQuery(query).uniqueResult();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<AbstractFeatureEntity> getFeatures(Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getFeatureEntityClass());
        query.from(getFeatureEntityClass());
        return session.createQuery(query).list();
    }

    public List<AbstractFeatureEntity> getFeatures(Collection<String> identifiers, Collection<SpatialFilter> filters,
            Session session) throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(identifiers)) {
            return getFeaturesChunks(identifiers, filters, session);
        } else {
            return getFeaturesForFilters(filters, session);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<AbstractFeatureEntity> getFeaturesForFilters(Collection<SpatialFilter> filters, Session session)
            throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getFeatureEntityClass());
        Root root = query.from(getFeatureEntityClass());
        Predicate spatial = spatialFilterPredicate(cb, root, filters);
        if (spatial != null) {
            query.where(spatial);
        }
        return session.createQuery(query).list();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<AbstractFeatureEntity> getFeaturesChunks(Collection<String> identifiers,
            Collection<SpatialFilter> filters, Session session) throws OwsExceptionReport {
        List<AbstractFeatureEntity> features = new ArrayList<>();
        for (List<String> ids : QueryHelper.getListsForIdentifiers(identifiers)) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery(getFeatureEntityClass());
            Root root = query.from(getFeatureEntityClass());
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get(AbstractFeatureEntity.IDENTIFIER).in(ids));
            Predicate spatial = spatialFilterPredicate(cb, root, filters);
            if (spatial != null) {
                predicates.add(spatial);
            }
            query.where(predicates.toArray(new Predicate[0]));
            features.addAll(session.createQuery(query).list());
        }
        return features;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Predicate spatialFilterPredicate(CriteriaBuilder cb, Root root, Collection<SpatialFilter> filters)
            throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(filters)) {
            List<Predicate> disjuncts = new ArrayList<>();
            for (final SpatialFilter filter : filters) {
                if (filter != null && (filter.getGeometry().getGeometry().isPresent()
                        || filter.getGeometry().getEnvelope().isPresent())) {
                    disjuncts.add(SpatialRestrictions.filter(cb, root.get(AbstractFeatureEntity.PROPERTY_GEOMETRY_ENTITY),
                            filter.getOperator(), filter.getGeometry().toGeometry()));
                }
            }
            if (!disjuncts.isEmpty()) {
                return cb.or(disjuncts.toArray(new Predicate[0]));
            }
        }
        return null;
    }

    public void updateFeatureOfInterest(AbstractFeatureEntity featureOfInterest, AbstractFeature abstractFeature,
            Session session) {
        addName(abstractFeature, featureOfInterest, session);
        session.saveOrUpdate(featureOfInterest);
    }

    protected Class<? extends AbstractFeatureEntity> getFeatureEntityClass() {
        return AbstractFeatureEntity.class;
    }

}