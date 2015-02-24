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
package org.n52.sos.ds.hibernate.values;

import java.util.Set;

import org.hibernate.HibernateException;
import org.n52.sos.ds.hibernate.dao.ObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.http.HTTPStatus;

import com.google.common.collect.Sets;

/**
 * Streaming observation class for old concept
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class HibernateStreamingObservation extends AbstractHibernateStreamingObservation {

    private static final long serialVersionUID = 3162933935914818428L;

    private final ObservationDAO observationDAO = new ObservationDAO();

    private Set<Long> procedureIds = Sets.newHashSet();

    private Set<Long> observablePropertyIds = Sets.newHashSet();

    private Set<Long> featureIds = Sets.newHashSet();

    /**
     * constructor
     * 
     * @param request
     *            {@link GetObservationRequest}
     */
    public HibernateStreamingObservation(GetObservationRequest request) {
        super(request);
    }

    @Override
    protected AbstractObservation checkShowMetadtaOfEmptyObservations(AbstractObservation abstractObservation) {
        if (showMetadataOfEmptyObservation) {
            procedureIds.add(abstractObservation.getProcedure().getProcedureId());
            observablePropertyIds.add(abstractObservation.getObservableProperty().getObservablePropertyId());
            featureIds.add(abstractObservation.getFeatureOfInterest().getFeatureOfInterestId());
        }
        return abstractObservation;
    }

    @Override
    protected void getNextScrollableResults() throws OwsExceptionReport {
        if (session == null) {
            session = sessionHolder.getSession();
        }
        try {
            if (observationNotQueried) {
                // query with temporal filter
                if (temporalFilterCriterion != null) {
                    setResult(observationDAO.getStreamingObservationsFor(request, features, temporalFilterCriterion,
                            session));
                }
                // query without temporal or indeterminate filters
                else {
                    setResult(observationDAO.getStreamingObservationsFor(request, features, session));
                }
                observationNotQueried = false;
            }
            if (!observationNotQueried && showMetadataOfEmptyObservation) {
                if (temporalFilterCriterion != null) {
                    setResult(observationDAO.getNotMatchingSeries(procedureIds, observablePropertyIds, featureIds,
                            request, features, temporalFilterCriterion, session));
                }
                // query without temporal or indeterminate filters
                else {
                    setResult(observationDAO.getNotMatchingSeries(procedureIds, observablePropertyIds, featureIds,
                            request, features, session));
                }

            }
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
