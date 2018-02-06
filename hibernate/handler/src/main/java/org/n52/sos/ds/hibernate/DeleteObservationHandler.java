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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.series.db.beans.CompositeDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationResponse;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.AbstractDeleteObservationHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesDAO;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.util.observation.OmObservationCreatorContext;

import com.google.common.base.Joiner;

public class DeleteObservationHandler
        extends AbstractDeleteObservationHandler {

    private HibernateSessionHolder sessionHolder;

    private DaoFactory daoFactory;

    private OmObservationCreatorContext observationCreatorContext;

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setOmObservationCreatorContext(OmObservationCreatorContext observationCreatorContext) {
        this.observationCreatorContext = observationCreatorContext;
    }

    @Override
    public synchronized DeleteObservationResponse deleteObservation(DeleteObservationRequest request)
            throws OwsExceptionReport {
        DeleteObservationResponse response = new DeleteObservationResponse(request.getResponseFormat());
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionHolder.getSession();
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
            sessionHolder.returnSession(session);
        }
        return response;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    private AbstractObservationRequest getRequest(DeleteObservationRequest request) {
        return (AbstractObservationRequest) new GetObservationRequest().setService(request.getService())
                .setVersion(request.getVersion());
    }

    private void deleteObservationsByIdentifier(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport, ConverterException {
        Set<String> ids = request.getObservationIdentifiers();
        List<DataEntity<?>> observations = daoFactory.getObservationDAO().getObservationByIdentifiers(ids, session);
        if (CollectionHelper.isNotEmpty(observations)) {
            for (DataEntity<?> observation : observations) {
                delete(observation, session);
            }
            if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                DataEntity<?> observation = observations.iterator().next();
                Set<DataEntity<?>> oberservations = Collections.singleton(observation);
                OmObservation so = HibernateObservationUtilities.createSosObservationsFromObservations(oberservations,
                        getRequest(request), getRequestedLocale(request), null, observationCreatorContext, session).next();
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
            filter = SosTemporalRestrictions.filter(request.getTemporalFilters());
        }
        ScrollableResults result = daoFactory.getObservationDAO().getObservations(request.getProcedures(),
                request.getObservedProperties(), request.getFeatureIdentifiers(), request.getOfferings(), filter,
                session);
        while (result.next()) {
            delete((DataEntity<?>) result.get()[0], session);
        }
    }

    private void delete(DataEntity<?> observation, Session session) {
        if (observation != null) {
            if (observation instanceof CompositeDataEntity) {
                for (DataEntity<?> o : ((CompositeDataEntity) observation).getValue()) {
                    delete(o, session);
                }
            }
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
    private void checkSeriesForFirstLatest(DataEntity<?> observation, Session session) {
            DatasetEntity series = observation.getDataset();
            if ((series.isSetFirstValueAt()
                    && series.getFirstValueAt().equals(observation.getSamplingTimeStart()))
                    || (series.isSetLastValueAt()
                            && series.getLastValueAt().equals(observation.getSamplingTimeEnd()))) {
                new SeriesDAO(daoFactory).updateSeriesAfterObservationDeletion(series, observation,
                        session);
                new OfferingDAO(daoFactory).updateAfterObservationDeletion(series.getOffering(), observation,
                        session);
            }
    }

}
