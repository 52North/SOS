/*
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
package org.n52.sos.ds.hibernate.dao;

import static org.n52.iceland.util.http.HTTPStatus.INTERNAL_SERVER_ERROR;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.InsertResultDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class GetDataAvailabilityDao implements org.n52.sos.ds.dao.GetDataAvailabilityDao {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GetDataAvailabilityDao.class);
    private HibernateSessionHolder sessionHolder;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public Map<String, NamedValue> getMetadata(DataAvailability dataAvailability) {
        Session session;
        try {
            session = sessionHolder.getSession();
            Map<String, NamedValue> map = new HashMap<>();
            if (HibernateHelper.isEntitySupported(SeriesMetadata.class)) {
//                List<SeriesMetadata> metadataList = new SeriesMetadataDAO().getMetadata(series.getSeriesId(), session);
//                if (CollectionHelper.isNotEmpty(metadataList)) {
//                    for (SeriesMetadata seriesMetadata : metadataList) {
//                        map.put(seriesMetadata.getDomain(), new NamedValue<>(new ReferenceType(seriesMetadata.getIdentifier()),
//                                new ReferenceValue(new ReferenceType(seriesMetadata.getValue()))));
//                    }
//                }
            }
            return map;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying metadata for GetDataAvailability!")
            .setStatus(INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public List<TimeInstant> getResultTimes(DataAvailability dataAvailability, GetDataAvailabilityRequest request) {
        Session session;
        try {
            session = sessionHolder.getSession();
            Criteria c = getDefaultObservationInfoCriteria(session);
            c.createCriteria(ContextualReferencedObservation.FEATURE_OF_INTEREST).add(
                    Restrictions.eq(FeatureOfInterest.IDENTIFIER, dataAvailability.getFeatureOfInterest().getHref()));
            c.createCriteria(ContextualReferencedObservation.PROCEDURE).add(
                    Restrictions.eq(Procedure.IDENTIFIER, dataAvailability.getProcedure().getHref()));
            c.createCriteria(ContextualReferencedObservation.OBSERVABLE_PROPERTY).add(
                    Restrictions.eq(ObservableProperty.IDENTIFIER, dataAvailability.getObservedProperty().getHref()));
            if (request.isSetOfferings()) {
                c.createCriteria(ContextualReferencedObservation.OFFERINGS).add(
                        Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
            }
            if (hasPhenomenonTimeFilter(request.getExtensions())) {
                c.add(TemporalRestrictions.filter(getPhenomenonTimeFilter(request.getExtensions())));
            }
            c.setProjection(Projections.distinct(Projections.property(ContextualReferencedObservation.RESULT_TIME)));
            c.addOrder(Order.asc(ContextualReferencedObservation.RESULT_TIME));
            LOGGER.debug("QUERY getResultTimesFromObservation({}): {}", HibernateHelper.getSqlString(c));
            List<TimeInstant> resultTimes = Lists.newArrayList();
            for (Date date : (List<Date>) c.list()) {
                resultTimes.add(new TimeInstant(date));
            }
            return resultTimes;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying result time for GetDataAvailability!")
                    .setStatus(INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }
    
    private Criteria getDefaultObservationInfoCriteria(Session session) {
        return session.createCriteria(ContextualReferencedObservation.class)
                .add(Restrictions.eq(ContextualReferencedObservation.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

}
