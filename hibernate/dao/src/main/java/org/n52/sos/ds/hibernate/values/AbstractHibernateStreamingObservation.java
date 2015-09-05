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

import java.util.Locale;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.ows.OwsServiceProvider;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.iceland.util.http.HTTPStatus;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateGetObservationHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.StreamingObservation;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.service.profile.ProfileHandler;

/**
 * Abstract class for streaming observations
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public abstract class AbstractHibernateStreamingObservation extends StreamingObservation {

    private static final long serialVersionUID = 7836070766447328741L;
    protected final HibernateSessionHolder sessionHolder;
    protected Session session;
    protected ScrollableResults result;
    protected GetObservationRequest request;
    protected Set<String> features;
    protected Criterion temporalFilterCriterion;
    protected boolean showMetadataOfEmptyObservation = false;
    protected boolean observationNotQueried = true;
    private LocalizedProducer<OwsServiceProvider> serviceProvider;
    private final Locale locale;

    /**
     * constructor
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param connectionProvider the connection provider
     */
    public AbstractHibernateStreamingObservation(ConnectionProvider connectionProvider, GetObservationRequest request, LocalizedProducer<OwsServiceProvider> serviceProvider) {
        this.request = request;
        showMetadataOfEmptyObservation =
                ProfileHandler.getInstance().getActiveProfile().isShowMetadataOfEmptyObservations();
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
        this.serviceProvider = serviceProvider;
        this.locale =  request.getRequestedLocale();
    }

    @Override
    public boolean hasNextValue() throws OwsExceptionReport {
        boolean next = false;
        if (result == null) {
            getNextScrollableResults();
            if (result != null) {
                next = result.next();
            }
        } else {
            next = result.next();
            if (!next) {
                getNextScrollableResults();
                if (result != null) {
                    next = result.next();
                }
            }
        }
        if (!next) {
            sessionHolder.returnSession(session);
        }
        return next;
    }

    @Override
    public OmObservation nextSingleObservation() throws OwsExceptionReport {
        try {
            OmObservation observation;
            Object resultObject = result.get()[0];
            if (resultObject instanceof SeriesObservation) {
                observation =
                        HibernateGetObservationHelper.toSosObservation(
                                checkShowMetadataOfEmptyObservations((SeriesObservation) result.get()[0]),
                                this.request, this.serviceProvider, this.locale, this.session);
            } else if (resultObject instanceof Series) {
                observation = HibernateObservationUtilities.createSosObservationFromSeries((Series) resultObject, this.request, this.serviceProvider, this.locale, this.session).iterator().next();
            } else {
                throw new NoApplicableCodeException().withMessage("The object {} is not supported", resultObject
                        .getClass().getName());
            }
            checkForModifications(observation);
            session.evict(resultObject);
            checkMaxNumberOfReturnedValues(1);
            return observation;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } catch (ConverterException ce) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Set the scrollable result
     *
     * @param result
     *            {@link ScrollableResults} to set
     */
    protected void setResult(ScrollableResults result) {
        this.result = result;
    }

    /**
     * Set the valid featureOfInterest identifiers
     *
     * @param features
     *            featureOfInterest identifiers to set
     */
    public void setValidFeatures(Set<String> features) {
        this.features = features;
    }

    /**
     * Set the temporal filter {@link Criterion}
     *
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     */
    public void setTemporalFilterCriterion(Criterion temporalFilterCriterion) {
        this.temporalFilterCriterion = temporalFilterCriterion;
    }

    /**
     * Check if metadata fo emtpy observations should be show in the response
     * and store required information
     *
     * @param abstractObservation
     *            Observation to check
     * @return Checked observation
     */
    protected abstract Observation<?> checkShowMetadataOfEmptyObservations(Observation<?> abstractObservation);

    /**
     * Get the next {@link ScrollableResults} from database
     *
     * @throws OwsExceptionReport
     *             If an error occurs when querying the database
     */
    protected abstract void getNextScrollableResults() throws OwsExceptionReport;
}
