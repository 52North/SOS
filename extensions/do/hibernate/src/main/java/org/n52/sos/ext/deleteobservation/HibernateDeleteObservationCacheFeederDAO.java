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
package org.n52.sos.ext.deleteobservation;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Updates the cache after a Observation was deleted. Uses the deleted
 * observation to determine which cache relations have to be updated.
 * <p/>
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 1.0.0
 */
public class HibernateDeleteObservationCacheFeederDAO extends DeleteObservationCacheFeederDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateDeleteObservationCacheFeederDAO.class);

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    private Session session;

    private AbstractObservationDAO observationDAO = null;

    private final OfferingDAO offeringDAO = new OfferingDAO();

    private final ProcedureDAO procedureDAO = new ProcedureDAO();

    @Override
    protected boolean isLastForProcedure(String feature, String procedure) throws OwsExceptionReport {
        Criteria criteria = DaoFactory.getInstance().getObservationDAO().getObservationInfoCriteriaForFeatureOfInterestAndProcedure(feature, procedure, getConnection());
        return isEmpty(criteria);
    }

    @Override
    protected boolean isLastForOffering(String feature, String offering) throws OwsExceptionReport {
        Criteria criteria = DaoFactory.getInstance().getObservationDAO().getObservationInfoCriteriaForFeatureOfInterestAndOffering(feature, offering, getConnection());
        return isEmpty(criteria);
    }

    /**
     * Checks if the specified query has no results.
     *
     * @param q
     *            the query
     *
     * @return if it has no results
     */
    protected boolean isEmpty(Criteria q) {
        Criteria criteria = q.setProjection(Projections.rowCount());
        LOGGER.debug("QUERY isEmpty(criteria): {}", HibernateHelper.getSqlString(criteria));
        return ((Number) criteria.uniqueResult()).longValue() == 0L;
    }

    @Override
    protected DateTime getMaxResultTime() {
        return observationDAO.getMaxResultTime(getConnection());
    }

    @Override
    protected DateTime getMinResultTime() {
        return observationDAO.getMinResultTime(getConnection());
    }

    @Override
    protected DateTime getMaxPhenomenonTime() {
        return observationDAO.getMaxPhenomenonTime(getConnection());
    }

    @Override
    protected DateTime getMinPhenomenonTime() {
        return observationDAO.getMinPhenomenonTime(getConnection());
    }

    @Override
    protected DateTime getMaxDateForOffering(final String offering) throws OwsExceptionReport {
        return offeringDAO.getMaxDate4Offering(offering, getConnection());
    }

    @Override
    protected DateTime getMaxDateForProcedure(final String procedure) throws OwsExceptionReport {
        return procedureDAO.getMaxDate4Procedure(procedure, getConnection());
    }

    @Override
    protected DateTime getMinResultTimeForOffering(final String offering) throws OwsExceptionReport {
        return offeringDAO.getMinResultTime4Offering(offering, getConnection());
    }

    @Override
    protected DateTime getMaxResultTimeForOffering(final String offering) throws OwsExceptionReport {
        return offeringDAO.getMaxResultTime4Offering(offering, getConnection());
    }

    @Override
    protected DateTime getMinDateForOffering(final String offering) throws OwsExceptionReport {
        return offeringDAO.getMinDate4Offering(offering, getConnection());
    }

    @Override
    protected DateTime getMinDateForProcedure(final String procedure) throws OwsExceptionReport {
        return procedureDAO.getMinDate4Procedure(procedure, getConnection());
    }

    @Override
    protected Session getConnection() {
        return session;
    }

    @Override
    protected void prepare() throws OwsExceptionReport {
        this.session = this.sessionHolder.getSession();
        this.observationDAO = DaoFactory.getInstance().getObservationDAO();
    }

    @Override
    protected void cleanup() {
        this.sessionHolder.returnSession(session);
    }
}
