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

import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.RelatedFeatureRole;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for featureofInterest types
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class RelatedFeatureRoleDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedFeatureRoleDAO.class);

    /**
     * Get related feature role objects for role
     * 
     * @param role
     *            Related feature role
     * @param session
     *            Hibernate session
     * @return Related feature role objects
     */
    @SuppressWarnings("unchecked")
    public List<RelatedFeatureRole> getRelatedFeatureRole(String role, Session session) {
        Criteria criteria =
                session.createCriteria(RelatedFeatureRole.class).add(
                        Restrictions.eq(RelatedFeatureRole.RELATED_FEATURE_ROLE, role));
        LOGGER.debug("QUERY getRelatedFeatureRole(role): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Insert and get related feature role objects
     * 
     * @param role
     *            Related feature role
     * @param session
     *            Hibernate session
     * @return Related feature objects
     */
    public List<RelatedFeatureRole> getOrInsertRelatedFeatureRole(String role, Session session) {
        List<RelatedFeatureRole> relFeatRoles = new RelatedFeatureRoleDAO().getRelatedFeatureRole(role, session);
        if (relFeatRoles == null) {
            relFeatRoles = new LinkedList<RelatedFeatureRole>();
        }
        if (relFeatRoles.isEmpty()) {
            RelatedFeatureRole relFeatRole = new RelatedFeatureRole();
            relFeatRole.setRelatedFeatureRole(role);
            session.save(relFeatRole);
            session.flush();
            relFeatRoles.add(relFeatRole);
        }
        return relFeatRoles;
    }
}
