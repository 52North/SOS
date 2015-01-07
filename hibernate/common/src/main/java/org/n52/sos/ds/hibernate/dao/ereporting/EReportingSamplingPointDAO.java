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
package org.n52.sos.ds.hibernate.dao.ereporting;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.aqd.AqdSamplingPoint;
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for entity {@link EReportingSamplingPoint}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.3.0
 *
 */
public class EReportingSamplingPointDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(EReportingSamplingPointDAO.class);

    /**
     * Get default Hibernate Criteria for querying sampling point
     * 
     * @param session
     *            Hibernate Session
     * @return Default criteria
     */
    public Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(EReportingSamplingPoint.class).setResultTransformer(
                Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Get the {@link EReportingSamplingPoint} for the id
     * 
     * @param samplingPointId
     *            Id to get {@link EReportingSamplingPoint} for
     * @param session
     *            Hibernate session
     * @return The resulting {@link EReportingSamplingPoint}
     */
    public EReportingSamplingPoint getEReportingSamplingPoint(long samplingPointId, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingSamplingPoint.ID, samplingPointId));
        LOGGER.debug("QUERY getEReportingSamplingPoint(samplingPointId): {}", HibernateHelper.getSqlString(c));
        return (EReportingSamplingPoint) c.uniqueResult();
    }

    /**
     * Get the {@link EReportingSamplingPoint} for the identifier
     * 
     * @param identifier
     *            Identifier to get {@link EReportingSamplingPoint} for
     * @param session
     *            Hibernate session
     * @return The resulting {@link EReportingSamplingPoint}
     */
    public EReportingSamplingPoint getEReportingSamplingPoint(String identifier, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingSamplingPoint.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getEReportingSamplingPoint(identifier): {}", HibernateHelper.getSqlString(c));
        return (EReportingSamplingPoint) c.uniqueResult();
    }

    /**
     * Get or insert {@link AqdSamplingPoint}
     * 
     * @param samplingPoint
     *            {@link AqdSamplingPoint} to insert
     * @param session
     *            Hibernate session
     * @return The resulting {@link EReportingSamplingPoint}
     */
    public EReportingSamplingPoint getOrInsert(AqdSamplingPoint samplingPoint, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingSamplingPoint.IDENTIFIER, samplingPoint.getIdentifier()));
        LOGGER.debug("QUERY getOrIntert(samplingPoint): {}", HibernateHelper.getSqlString(c));
        EReportingSamplingPoint eReportingSamplingPoint = (EReportingSamplingPoint) c.uniqueResult();
        if (eReportingSamplingPoint == null) {
            eReportingSamplingPoint = new EReportingSamplingPoint();
            addIdentifierNameDescription(samplingPoint, eReportingSamplingPoint, session);
            eReportingSamplingPoint.setAssessmentType(new EReportingAssessmentTypeDAO().getOrInsert(
                    samplingPoint.getAssessmentType(), session));
            session.save(eReportingSamplingPoint);
            session.flush();
            session.refresh(eReportingSamplingPoint);
        }
        return eReportingSamplingPoint;
    }

}
