/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.DeleteSensorRequest;
import org.n52.shetland.ogc.sos.response.DeleteSensorResponse;
import org.n52.sos.ds.AbstractDeleteSensorHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.ValidProcedureTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.legacy.LegacyObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;

/**
 * Implementation of the abstract class AbstractDeleteSensorHandler
 * @since 4.0.0
 *
 */
public class DeleteSensorDAO extends AbstractDeleteSensorHandler {
    private HibernateSessionHolder sessionHolder;
    private DaoFactory daoFactory;

    public DeleteSensorDAO() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public synchronized DeleteSensorResponse deleteSensor(DeleteSensorRequest request) throws OwsExceptionReport {
        DeleteSensorResponse response = new DeleteSensorResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            setDeleteSensorFlag(request.getProcedureIdentifier(), true, session);
            new ValidProcedureTimeDAO(daoFactory).setValidProcedureDescriptionEndTime(request.getProcedureIdentifier(), session);
            transaction.commit();
            response.setDeletedProcedure(request.getProcedureIdentifier());
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while updateing deleted sensor flag data!");
        } finally {
            sessionHolder.returnSession(session);
        }
        return response;
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ValidProcedureTime.class);
    }

    /**
     * Set the deleted flag of the procedure and corresponding entities
     * (observations, series, obervationConstellation) to <code>true</code>
     *
     * @param identifier
     *            Procedure identifier
     * @param deleteFlag
     *            Deleted flag to set
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If the procedure is not contained in the database
     */
    private void setDeleteSensorFlag(String identifier, boolean deleteFlag, Session session) throws OwsExceptionReport {
        Procedure procedure = new ProcedureDAO(daoFactory).getProcedureForIdentifier(identifier, session);
        if (procedure != null) {
            procedure.setDeleted(deleteFlag);
            session.saveOrUpdate(procedure);
            session.flush();
            // set deleted flag in ObservationConstellation table to true
            if (HibernateHelper.isEntitySupported(ObservationConstellation.class)) {
                daoFactory.getObservationConstellationDAO().updateObservatioConstellationSetAsDeletedForProcedure(identifier,
                        deleteFlag, session);
            }
            // set deleted flag in Series and Observation table for series concept to true
            if (EntitiyHelper.getInstance().isSeriesSupported()) {
                List<Series> series =
                        daoFactory.getSeriesDAO().updateSeriesSetAsDeletedForProcedureAndGetSeries(identifier, deleteFlag,
                                session);
                getSeriesObservationDAO().updateObservationSetAsDeletedForSeries(series, deleteFlag, session);
            }
            // set deleted flag in Observation table for old concept to true
            else {
                new LegacyObservationDAO(daoFactory).updateObservationSetAsDeletedForProcedure(identifier, deleteFlag, session);
            }
        } else {
            throw new NoApplicableCodeException().withMessage("The requested identifier is not contained in database");
        }
    }

    protected AbstractSeriesObservationDAO getSeriesObservationDAO() throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = daoFactory.getObservationDAO();
        if (observationDAO instanceof AbstractSeriesObservationDAO) {
            return (AbstractSeriesObservationDAO) observationDAO;
        } else {
            throw new NoApplicableCodeException().withMessage("The required '%s' implementation is no supported!",
                    AbstractObservationDAO.class.getName());
        }
    }

}
