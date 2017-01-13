/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesDAO;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 1.0.0
 */
public class DeleteObservationDAO extends DeleteObservationAbstractDAO {

    private HibernateSessionHolder hibernateSessionHolder = new HibernateSessionHolder();

    @Override
    public synchronized DeleteObservationResponse deleteObservation(DeleteObservationRequest request)
            throws OwsExceptionReport {
        DeleteObservationResponse response = request.getResponse();
        Session session = null;
        Transaction transaction = null;
        try {
            session = hibernateSessionHolder.getSession();
            transaction = session.beginTransaction();
            if (request.isSetObservationIdentifiers()) {
                deleteObservationsByIdentifier(request, response, session);
            } else {
                deleteObservationByParameter(request, response, session);
            }
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while updating deleted observation flag data!");
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce)
                    .withMessage("Error while updating deleted observation flag data!");
        } finally {
            hibernateSessionHolder.returnSession(session);
        }
        return response;
    }

    private AbstractObservationRequest getRequest(DeleteObservationRequest request) {
        return (AbstractObservationRequest) new GetObservationRequest().setService(request.getService())
                .setVersion(request.getVersion());
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    private void deleteObservationsByIdentifier(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport, ConverterException {
        Set<String> ids = request.getObservationIdentifiers();
        List<Observation<?>> observations = DaoFactory.getInstance().getObservationDAO().getObservationByIdentifiers(ids, session);
        if (CollectionHelper.isNotEmpty(observations)) {
            for (Observation<?> observation : observations) {
                delete(observation, session);
            }
            if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                Observation<?> observation = observations.iterator().next();
                OmObservation so = HibernateObservationUtilities
                        .createSosObservationsFromObservations(Sets.<Observation<?>>newHashSet(observation), getRequest(request),
                                null, session)
                        .iterator().next();
                response.setObservationId(request.getObservationIdentifiers().iterator().next());
                response.setDeletedObservation(so);
            }
        } else {
            if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                throw new InvalidParameterValueException(DeleteObservationConstants.PARAM_OBSERVATION,
                        Joiner.on(", ").join(request.getObservationIdentifiers()));
            }
        }
    }

    private void deleteObservationByParameter(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport {
        Criterion filter = null;
        if (CollectionHelper.isNotEmpty(request.getTemporalFilters())) {
            filter = TemporalRestrictions.filter(request.getTemporalFilters());
        }
        ScrollableResults result = DaoFactory.getInstance().getObservationDAO().getObservations(request.getProcedures(),
                request.getObservedProperties(), request.getFeatureIdentifiers(), request.getOfferings(),
                filter, session);
        while (result.next()) {
            delete((Observation<?>) result.get()[0], session);
        }
    }
    
    private void delete(Observation<?> observation, Session session) {
        if (observation != null) {
            observation.setDeleted(true);
            session.saveOrUpdate(observation);
            checkSeriesForFirstLatest(observation, session);
            session.flush();
        }
    }

    /**
     * Check if {@link Series} should be updated
     * 
     * @param observation
     *            Deleted observation
     * @param session
     *            Hibernate session
     */
    private void checkSeriesForFirstLatest(Observation<?> observation, Session session) {
        if (observation instanceof SeriesObservation) {
            Series series = ((SeriesObservation) observation).getSeries();
            if (series.getFirstTimeStamp().equals(observation.getPhenomenonTimeStart())
                    || series.getLastTimeStamp().equals(observation.getPhenomenonTimeEnd())) {
                new SeriesDAO().updateSeriesAfterObservationDeletion(series, (SeriesObservation) observation, session);
            }
        }
    }
}
