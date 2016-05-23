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

import java.util.Collections;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.series.SeriesDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.GetObservationRequest;

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
        DeleteObservationResponse response = new DeleteObservationResponse();
        response.setVersion(request.getVersion());
        response.setService(request.getService());
        Session session = null;
        Transaction transaction = null;
        try {
            session = hibernateSessionHolder.getSession();
            transaction = session.beginTransaction();
            String id = request.getObservationIdentifier();
            AbstractObservation observation = null;
            try {
                observation =
                        DaoFactory.getInstance().getObservationDAO().getObservationByIdentifier(id, session);
            } catch (HibernateException he) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new InvalidParameterValueException(DeleteObservationConstants.PARAMETER_NAME, request.getObservationIdentifier());
            }
            OmObservation so = null;
            if (observation != null) {
                so =
                        HibernateObservationUtilities
                                .createSosObservationsFromObservations(Collections.singleton(observation), getRequest(request), null, session).iterator().next();
                observation.setDeleted(true);
                session.saveOrUpdate(observation);
                checkSeriesForFirstLatest(observation, session);
                session.flush();
            } else {
                throw new InvalidParameterValueException(DeleteObservationConstants.PARAMETER_NAME, request.getObservationIdentifier());
            }
            transaction.commit();
            response.setObservationId(request.getObservationIdentifier());
            response.setDeletedObservation(so);
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while updating deleted observation flag data!");
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage(
                    "Error while updating deleted observation flag data!");
        } finally {
            hibernateSessionHolder.returnSession(session);
        }
        return response;
    }

    private AbstractObservationRequest getRequest(
			DeleteObservationRequest request) {
		// TODO Auto-generated method stub
		return (AbstractObservationRequest) new GetObservationRequest().setService(request.getService()).setVersion(request.getVersion());
	}

	@Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

	/**
	 * Check if {@link Series} should be updated
	 * 
	 * @param observation
	 *            Deleted observation
	 * @param session
	 *            Hibernate session
	 */
	private void checkSeriesForFirstLatest(AbstractObservation observation, Session session) {
		if (observation instanceof SeriesObservation) {
			Series series = ((SeriesObservation)observation).getSeries();
			if (series.getFirstTimeStamp().equals(observation.getPhenomenonTimeStart()) || series.getLastTimeStamp().equals(observation.getPhenomenonTimeEnd())) {
				new SeriesDAO().updateSeriesAfterObservationDeletion(series, (SeriesObservation)observation, session);
			}
		}
	}
}
