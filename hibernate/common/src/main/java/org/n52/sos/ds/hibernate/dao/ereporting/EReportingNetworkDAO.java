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
package org.n52.sos.ds.hibernate.dao.ereporting;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.ereporting.EReportingNetworkEntity;
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for entity {@link EReportingNetworkEntity}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public class EReportingNetworkDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(EReportingNetworkDAO.class);

    public EReportingNetworkDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Get default Hibernate Criteria for querying network
     *
     * @param session
     *            Hibernate Session
     * @return Default criteria
     */
    public Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(EReportingNetworkEntity.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Get the {@link EReportingNetworkEntity} for the id
     *
     * @param networkId
     *            Id to get {@link EReportingNetworkEntity} for
     * @param session
     *            Hibernate Session
     * @return The resulting {@link EReportingNetworkEntity}
     */
    public EReportingNetworkEntity getEReportingNetwork(long networkId, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingNetworkEntity.ID, networkId));
        LOGGER.trace("QUERY getEReportingNetwork(networkId): {}", HibernateHelper.getSqlString(c));
        return (EReportingNetworkEntity) c.uniqueResult();
    }

    /**
     * Get the {@link EReportingNetworkEntity} for the identifier
     *
     * @param identifier
     *            Identifier to get {@link EReportingNetworkEntity} for
     * @param session
     *            Hibernate Session
     * @return The resulting {@link EReportingNetworkEntity}
     */
    public EReportingNetworkEntity getEReportingNetwork(String identifier, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(EReportingNetworkEntity.PROPERTY_IDENTIFIER, identifier));
        LOGGER.trace("QUERY getEReportingNetwork(identifier): {}", HibernateHelper.getSqlString(c));
        return (EReportingNetworkEntity) c.uniqueResult();
    }
}
