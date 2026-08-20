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

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.RelatedFeatureEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Hibernate data access class for related features
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class RelatedFeatureDAO {

    private final DaoFactory daoFactory;

    public RelatedFeatureDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Get related feature objects for offering identifier
     *
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Related feature objects
     */
    public List<RelatedFeatureEntity> getRelatedFeatureForOffering(final String offering, final Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<RelatedFeatureEntity> query = cb.createQuery(RelatedFeatureEntity.class);
        Root<RelatedFeatureEntity> root = query.from(RelatedFeatureEntity.class);
        Join<RelatedFeatureEntity, OfferingEntity> offerings = root.join(RelatedFeatureEntity.OFFERINGS);
        query.where(cb.equal(offerings.get(OfferingEntity.PROPERTY_IDENTIFIER), offering));
        return session.createQuery(query)
                .list();
    }


    /**
     * Get related feature objects for target identifier
     *
     * @param targetIdentifier
     *            Target identifier
     * @param session
     *            Hibernate session
     * @return Related feature objects
     */
    public List<RelatedFeatureEntity> getRelatedFeatures(final String targetIdentifier, final Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<RelatedFeatureEntity> query = cb.createQuery(RelatedFeatureEntity.class);
        Root<RelatedFeatureEntity> root = query.from(RelatedFeatureEntity.class);
        Join<RelatedFeatureEntity, AbstractFeatureEntity> feature =
                root.join(RelatedFeatureEntity.FEATURE_OF_INTEREST);
        query.where(cb.equal(feature.get(AbstractFeatureEntity.PROPERTY_IDENTIFIER), targetIdentifier));
        return session.createQuery(query)
                .list();
    }

    /**
     * Insert and get related feature objects.
     *
     * @param feature
     *            Related feature
     * @param role
     *            Related feature role objects
     * @param session
     *            Hibernate session
     * @return Related feature objects
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public List<RelatedFeatureEntity> getOrInsertRelatedFeature(final AbstractFeature feature, final String role,
            final Session session) throws OwsExceptionReport {
        // TODO: create featureOfInterest and link to relatedFeature
        List<RelatedFeatureEntity> relFeats =
                getRelatedFeatures(feature.getIdentifierCodeWithAuthority().getValue(), session);
        if (relFeats == null) {
            relFeats = new LinkedList<>();
        }
        if (relFeats.isEmpty() && role != null && !role.isEmpty()) {
            final RelatedFeatureEntity relFeat = new RelatedFeatureEntity();
            String identifier = feature.getIdentifierCodeWithAuthority().getValue();
            String url = null;
            if (feature instanceof AbstractSamplingFeature samplingFeature) {
                identifier =
                        daoFactory.getFeatureQueryHandler().insertFeature(samplingFeature, session);
                url = samplingFeature.getUrl();
            }
            relFeat.setFeature(daoFactory.getFeatureOfInterestDAO().getOrInsert(identifier, url, session));
            relFeat.setRole(role);
            session.persist(relFeat);
            session.flush();
            relFeats.add(relFeat);
        }
        return relFeats;
    }
}
