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
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for observation types
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ObservationTypeDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationTypeDAO.class);

    /**
     * Get observation type objects for observation types
     * 
     * @param observationTypes
     *            Observation types
     * @param session
     *            Hibernate session
     * @return Observation type objects
     */
    @SuppressWarnings("unchecked")
    public List<ObservationType> getObservationTypeObjects(List<String> observationTypes, Session session) {
        Criteria criteria =
                session.createCriteria(ObservationType.class).add(
                        Restrictions.in(ObservationType.OBSERVATION_TYPE, observationTypes));
        LOGGER.debug("QUERY getObservationTypeObjects(observationTypes): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get observation type object for observation type
     * 
     * @param observationType
     * @param session
     *            Hibernate session
     * @return Observation type object
     */
    public ObservationType getObservationTypeObject(String observationType, Session session) {
        Criteria criteria =
                session.createCriteria(ObservationType.class).add(
                        Restrictions.eq(ObservationType.OBSERVATION_TYPE, observationType));
        LOGGER.debug("QUERY getObservationTypeObject(observationType): {}", HibernateHelper.getSqlString(criteria));
        return (ObservationType) criteria.uniqueResult();
    }

    /**
     * Insert or/and get observation type object for observation type
     * 
     * @param observationType
     *            Observation type
     * @param session
     *            Hibernate session
     * @return Observation type object
     */
    public ObservationType getOrInsertObservationType(String observationType, Session session) {
        ObservationType hObservationType = getObservationTypeObject(observationType, session);
        if (hObservationType == null) {
            hObservationType = new ObservationType();
            hObservationType.setObservationType(observationType);
            session.save(hObservationType);
            session.flush();
        }
        return hObservationType;
    }

    /**
     * Insert or/and get observation type objects for observation types
     * 
     * @param observationTypes
     *            Observation types
     * @param session
     *            Hibernate session
     * @return Observation type objects
     */
    public List<ObservationType> getOrInsertObservationTypes(Set<String> observationTypes, Session session) {
        List<ObservationType> obsTypes = new LinkedList<ObservationType>();
        for (String observationType : observationTypes) {
            obsTypes.add(getOrInsertObservationType(observationType, session));
        }
        return obsTypes;
    }
}
