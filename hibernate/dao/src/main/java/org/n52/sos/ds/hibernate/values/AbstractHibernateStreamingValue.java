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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.joda.time.DateTime;

import org.n52.iceland.ogc.ows.Extensions;
import org.n52.iceland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.entities.AbstractObservationTime;
import org.n52.sos.ds.hibernate.entities.interfaces.SweDataArrayValue;
import org.n52.sos.ds.hibernate.entities.values.AbstractValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.GmlHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.gml.time.Time;
import org.n52.iceland.ogc.gml.time.TimeInstant;

import com.google.common.collect.Maps;

/**
 * Abstract class for Hibernate streaming values
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public abstract class AbstractHibernateStreamingValue extends StreamingValue<AbstractValue> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHibernateStreamingValue.class);

    private static final long serialVersionUID = -8355955808723620476L;

    protected final HibernateSessionHolder sessionHolder;

    protected Session session;

    protected GetObservationRequest request;

    protected Criterion temporalFilterCriterion;

    /**
     * constructor
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param connectionProvider the connection provider
     */
    public AbstractHibernateStreamingValue(ConnectionProvider connectionProvider, GetObservationRequest request) {
        this.request = request;
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public Collection<OmObservation> mergeObservation() throws OwsExceptionReport {

        Map<String, OmObservation> observations = Maps.newHashMap();
        while (hasNextValue()) {
            AbstractValue nextEntity = nextEntity();
            boolean mergableObservationValue = checkForMergability(nextEntity);
            OmObservation observation = null;
            if (observations.containsKey(nextEntity.getDiscriminator()) && mergableObservationValue) {
                observation = observations.get(nextEntity.getDiscriminator());
            } else {
                observation = observationTemplate.cloneTemplate();
                addSpecificValuesToObservation(observation, nextEntity, request.getExtensions());
                if (!mergableObservationValue && nextEntity.getDiscriminator() == null) {
                    observations.put(Long.toString(nextEntity.getObservationId()), observation);
                } else {
                    observations.put(nextEntity.getDiscriminator(), observation);
                }
            }
            nextEntity.mergeValueToObservation(observation, getResponseFormat());
            sessionHolder.getSession().evict(nextEntity);
        }
        return observations.values();
    }



    private boolean checkForMergability(AbstractValue nextEntity) {
        return !(nextEntity instanceof SweDataArrayValue);
    }

    private void addSpecificValuesToObservation(OmObservation observation, AbstractValue value, Extensions extensions) {
        boolean newSession = false;
        try {
            if (session == null) {
                session = sessionHolder.getSession();
                newSession = true;
            }
            value.addValueSpecificDataToObservation(observation, session, extensions);
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while querying times", owse);
        } finally {
            if (newSession) {
                sessionHolder.returnSession(session);
            }
        }
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
     * Get the observation ids from {@link AbstractValue}s
     *
     * @param abstractValuesResult
     *            {@link AbstractValue}s to get ids from
     * @return Set with ids
     */
    protected Set<Long> getObservationIds(Collection<AbstractValue> abstractValuesResult) {
        Set<Long> ids = new HashSet<>();
        for (AbstractValue abstractValue : abstractValuesResult) {
            ids.add(abstractValue.getObservationId());
        }
        return ids;
    }

    /**
     * Create phenomenon time from min and max {@link AbstractObservationTime}s
     *
     * @param minTime
     *            minimum {@link AbstractObservationTime}
     * @param maxTime
     *            maximum {@link AbstractObservationTime}
     * @return phenomenon time
     */
    protected Time createPhenomenonTime(AbstractObservationTime minTime, AbstractObservationTime maxTime) {
        // create time element

        final DateTime phenStartTime = DateTimeHelper.makeDateTime(minTime.getPhenomenonTimeStart());
        DateTime phenEndTime;
        if (maxTime.getPhenomenonTimeEnd() != null) {
            phenEndTime = DateTimeHelper.makeDateTime(minTime.getPhenomenonTimeEnd());
        } else {
            phenEndTime = phenStartTime;
        }
        return GmlHelper.createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create result time from {@link AbstractObservationTime}
     *
     * @param maxTime
     *            {@link AbstractObservationTime} to create result time from
     * @return result time
     */
    protected TimeInstant createResutlTime(AbstractObservationTime maxTime) {
        DateTime dateTime = DateTimeHelper.makeDateTime(maxTime.getResultTime());
        return new TimeInstant(dateTime);
    }

    /**
     * Create valid time from min and max {@link AbstractObservationTime}s
     *
     * @param minTime
     *            minimum {@link AbstractObservationTime}
     * @param maxTime
     *            maximum {@link AbstractObservationTime}
     * @return valid time or null if valid time is not set in datasource
     */
    protected Time createValidTime(AbstractObservationTime minTime, AbstractObservationTime maxTime) {
        // create time element
        if (minTime.getValidTimeStart() != null && maxTime.getValidTimeEnd() != null) {
            final DateTime startTime = DateTimeHelper.makeDateTime(minTime.getValidTimeStart());
            DateTime endTime = DateTimeHelper.makeDateTime(minTime.getValidTimeEnd());
            return GmlHelper.createTime(startTime, endTime);
        }
        return null;
    }

}
