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
package org.n52.sos.ds.hibernate.dao;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.RelatedFeatureRole;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for related features
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class RelatedFeatureDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedFeatureDAO.class);

    /**
     * Get related feature objects for offering identifier
     * 
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Related feature objects
     */
    @SuppressWarnings("unchecked")
    public List<RelatedFeature> getRelatedFeatureForOffering(final String offering, final Session session) {
        final Criteria criteria = session.createCriteria(RelatedFeature.class);
        criteria.createCriteria(RelatedFeature.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offering));
        LOGGER.debug("QUERY getRelatedFeatureForOffering(offering): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get all related feature objects
     * 
     * @param session
     *            Hibernate session
     * @return Related feature objects
     */
    @SuppressWarnings("unchecked")
    public List<RelatedFeature> getRelatedFeatureObjects(final Session session) {
        final Criteria criteria = session.createCriteria(RelatedFeature.class);
        LOGGER.debug("QUERY getRelatedFeatureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
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
    @SuppressWarnings("unchecked")
    public List<RelatedFeature> getRelatedFeatures(final String targetIdentifier, final Session session) {
        final Criteria criteria = session.createCriteria(RelatedFeature.class);
        criteria.createCriteria(RelatedFeature.FEATURE_OF_INTEREST).add(
                Restrictions.eq(FeatureOfInterest.IDENTIFIER, targetIdentifier));
        LOGGER.debug("QUERY getRelatedFeatures(targetIdentifier): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Insert and get related feature objects.
     * 
     * @param feature
     *            Related feature
     * @param roles
     *            Related feature role objects
     * @param session
     *            Hibernate session
     * @return Related feature objects
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public List<RelatedFeature> getOrInsertRelatedFeature(final AbstractFeature feature, final List<RelatedFeatureRole> roles,
            final Session session) throws OwsExceptionReport {
        // TODO: create featureOfInterest and link to relatedFeature
        List<RelatedFeature> relFeats = getRelatedFeatures(feature.getIdentifierCodeWithAuthority().getValue(), session);
        if (relFeats == null) {
            relFeats = new LinkedList<RelatedFeature>();
        }
        if (relFeats.isEmpty()) {
            final RelatedFeature relFeat = new RelatedFeature();
            String identifier = feature.getIdentifierCodeWithAuthority().getValue();
            String url = null;
            if (feature instanceof SamplingFeature) {
                identifier =
                        Configurator.getInstance().getFeatureQueryHandler()
                                .insertFeature((SamplingFeature) feature, session);
                url = ((SamplingFeature) feature).getUrl();
            }
            relFeat.setFeatureOfInterest(new FeatureOfInterestDAO().getOrInsertFeatureOfInterest(identifier, url,
                    session));
            relFeat.setRelatedFeatureRoles(new HashSet<RelatedFeatureRole>(roles));
            session.save(relFeat);
            session.flush();
            relFeats.add(relFeat);
        }
        return relFeats;
    }
}
