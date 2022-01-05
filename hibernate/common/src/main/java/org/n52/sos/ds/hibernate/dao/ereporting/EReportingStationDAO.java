/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
import org.n52.series.db.beans.ereporting.EReportingSamplingPointEntity;
import org.n52.series.db.beans.ereporting.EReportingStationEntity;
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for entity {@link EReportingSamplingPointEntity}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public class EReportingStationDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(EReportingStationDAO.class);

    public EReportingStationDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Get default Hibernate Criteria for querying station
     *
     * @param session
     *            Hibernate Session
     * @return Default criteria
     */
    public Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(EReportingSamplingPointEntity.class).setResultTransformer(
                Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Get the {@link EReportingSamplingPointEntity} for the id
     *
     * @param stationId
     *            Id to get {@link EReportingSamplingPointEntity} for
     * @param session
     *            Hibernate Session
     * @return The resulting {@link EReportingSamplingPointEntity}
     */
    public EReportingSamplingPointEntity getEReportingStationt(long stationId, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingSamplingPointEntity.PROPERTY_ID, stationId));
        LOGGER.trace("QUERY getEReportingStationt(stationId): {}", HibernateHelper.getSqlString(c));
        return (EReportingSamplingPointEntity) c.uniqueResult();
    }

    /**
     * Get the {@link EReportingStationEntity} for the identifier
     *
     * @param identifier
     *            Identifier to get {@link EReportingSamplingPointEntity} for
     * @param session
     *            Hibernate Session
     * @return The resulting {@link EReportingSamplingPointEntity}
     */
    public EReportingSamplingPointEntity getEReportingStationt(String identifier, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingSamplingPointEntity.PROPERTY_IDENTIFIER, identifier));
        LOGGER.trace("QUERY getEReportingStationt(identifier): {}", HibernateHelper.getSqlString(c));
        return (EReportingSamplingPointEntity) c.uniqueResult();
    }
}
