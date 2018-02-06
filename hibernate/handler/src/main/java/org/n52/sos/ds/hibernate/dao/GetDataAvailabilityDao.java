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
package org.n52.sos.ds.hibernate.dao;

import static org.n52.janmayen.http.HTTPStatus.INTERNAL_SERVER_ERROR;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;


public class GetDataAvailabilityDao implements org.n52.sos.ds.dao.GetDataAvailabilityDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetDataAvailabilityDao.class);
    private HibernateSessionHolder sessionHolder;
    private Locale defaultLanguage;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = new Locale(defaultLanguage);
    }

    @Override
    public Map<String, NamedValue<?>> getMetadata(DataAvailability dataAvailability) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            Map<String, NamedValue<?>> map = new HashMap<>();
//            if (HibernateHelper.isEntitySupported(SeriesMetadata.class)) {
//                List<SeriesMetadata> metadataList = new SeriesMetadataDAO().getMetadata(series.getSeriesId(), session);
//                if (CollectionHelper.isNotEmpty(metadataList)) {
//                    for (SeriesMetadata seriesMetadata : metadataList) {
//                        map.put(seriesMetadata.getDomain(), new NamedValue<>(new ReferenceType(seriesMetadata.getIdentifier()),
//                                new ReferenceValue(new ReferenceType(seriesMetadata.getValue()))));
//                    }
//                }
//            }
            return map;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying metadata for GetDataAvailability!")
            .setStatus(INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public List<TimeInstant> getResultTimes(DataAvailability dataAvailability, GetDataAvailabilityRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            Criteria c = getDefaultObservationInfoCriteria(session);
            Criteria datasetCriteria = c.createCriteria(DataEntity.PROPERTY_DATASET);
            datasetCriteria.createCriteria(DatasetEntity.PROPERTY_FEATURE).add(
                    Restrictions.eq(DatasetEntity.IDENTIFIER, dataAvailability.getFeatureOfInterest().getHref()));
            datasetCriteria.createCriteria( DatasetEntity.PROPERTY_PROCEDURE).add(
                    Restrictions.eq(ProcedureEntity.IDENTIFIER, dataAvailability.getProcedure().getHref()));
            datasetCriteria.createCriteria(DatasetEntity.PROPERTY_PHENOMENON).add(
                    Restrictions.eq(PhenomenonEntity.IDENTIFIER, dataAvailability.getObservedProperty().getHref()));
            if (request.isSetOfferings()) {
                c.createCriteria( DatasetEntity.PROPERTY_OFFERING).add(
                        Restrictions.in(OfferingEntity.IDENTIFIER, request.getOfferings()));
            }
            if (hasPhenomenonTimeFilter(request.getExtensions())) {
                c.add(SosTemporalRestrictions.filter(getPhenomenonTimeFilter(request.getExtensions())));
            }
            c.setProjection(Projections.distinct(Projections.property(DataEntity.PROPERTY_RESULT_TIME)));
            c.addOrder(Order.asc(DataEntity.PROPERTY_RESULT_TIME));
            LOGGER.debug("QUERY getResultTimesFromObservation({}): {}", HibernateHelper.getSqlString(c));
            List<TimeInstant> resultTimes = Lists.newArrayList();
            for (Date date : (List<Date>) c.list()) {
                resultTimes.add(new TimeInstant(date));
            }
            return resultTimes;
        } catch (final HibernateException | OwsExceptionReport he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying result time for GetDataAvailability!")
                    .setStatus(INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    private Criteria getDefaultObservationInfoCriteria(Session session) {
        return session.createCriteria(DataEntity.class)
                .add(Restrictions.eq(DataEntity.PROPERTY_DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Check if extensions contains a temporal filter with valueReference
     * phenomenonTime
     *
     * @param extensions
     *            Extensions to check
     * @return <code>true</code>, if extensions contains a temporal filter with
     *         valueReference phenomenonTime
     */
    private boolean hasPhenomenonTimeFilter(Extensions extensions) {
        boolean hasFilter = false;
        for (Extension<?> extension : extensions.getExtensions()) {
            if (extension.getValue() instanceof TemporalFilter) {
                TemporalFilter filter = (TemporalFilter) extension.getValue();
                if (TemporalRestrictions.PHENOMENON_TIME_VALUE_REFERENCE.equals(filter.getValueReference())) {
                    hasFilter = true;
                }
            }
        }
        return hasFilter;
    }

    /**
     * Get the temporal filter with valueReference phenomenonTime from
     * extensions
     *
     * @param extensions
     *            To get filter from
     * @return Temporal filter with valueReference phenomenonTime
     */
    private TemporalFilter getPhenomenonTimeFilter(Extensions extensions) {
        for (Extension<?> extension : extensions.getExtensions()) {
            if (extension.getValue() instanceof TemporalFilter) {
                TemporalFilter filter = (TemporalFilter) extension.getValue();
                if (TemporalRestrictions.PHENOMENON_TIME_VALUE_REFERENCE.equals(filter.getValueReference())) {
                    return filter;
                }
            }
        }
        return null;
    }

    @Override
    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }
}
