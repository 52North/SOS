/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.ds.hibernate.dao.ValueDAO;
import org.n52.sos.ds.hibernate.dao.ValueTimeDAO;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractValuedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.TemporalReferencedLegacyObservation;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Abstract Hibernate streaming value class for old observation concept
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class HibernateStreamingValue extends AbstractHibernateStreamingValue {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateStreamingValue.class);
    private static final long serialVersionUID = -7451818170087729427L;
    protected final ValueDAO valueDAO = new ValueDAO();
    protected final ValueTimeDAO valueTimeDAO = new ValueTimeDAO();
    protected final Set<Long> procedure = Sets.newHashSet();
    protected final Set<Long> featureOfInterest = Sets.newHashSet();
    protected final Set<Long> observableProperty = Sets.newHashSet();

    /**
     * constructor
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            observableProperty procedure id
     * @param featureOfInterest
     *            featureOfInterest procedure id
     */
    public HibernateStreamingValue(GetObservationRequest request,
                                   long procedure,
                                   long observableProperty,
                                   long featureOfInterest) {
        super(request);
        this.procedure.add(procedure);
        this.observableProperty.add(observableProperty);
        this.featureOfInterest.add(featureOfInterest);
    }

    @Override
    protected void queryTimes() {
        try {
            if (request instanceof GetObservationRequest) {
                GetObservationRequest getObsReq = (GetObservationRequest)request;
                TemporalReferencedLegacyObservation minTime;
                TemporalReferencedLegacyObservation maxTime;
                // query with temporal filter
                if (temporalFilterCriterion != null) {
                    minTime = valueTimeDAO.getMinValueFor(getObsReq, procedure, observableProperty, featureOfInterest, temporalFilterCriterion, getSession());
                    maxTime = valueTimeDAO.getMaxValueFor(getObsReq, procedure, observableProperty, featureOfInterest, temporalFilterCriterion, getSession());
                }
                // query without temporal or indeterminate filters
                else {
                    minTime = valueTimeDAO.getMinValueFor(getObsReq, procedure, observableProperty, featureOfInterest, getSession());
                    maxTime = valueTimeDAO.getMaxValueFor(getObsReq, procedure, observableProperty, featureOfInterest, getSession());
                }
                setPhenomenonTime(createPhenomenonTime(minTime, maxTime));
                setResultTime(createResutlTime(maxTime));
                setValidTime(createValidTime(minTime, maxTime));
            }
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while querying times", owse);
        }
    }

    @Override
    protected void queryUnit() {
        try {
            if (request instanceof GetObservationRequest) {
                setUnit(valueDAO.getUnit((GetObservationRequest)request, procedure, observableProperty, featureOfInterest, getSession()));
            }
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while querying unit", owse);
        }
    }
    
    @Override
    public void mergeValue(StreamingValue<AbstractValuedLegacyObservation<?>> streamingValue) {
        // TODO Auto-generated method stub
        
    }

}
