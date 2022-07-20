/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.ereporting.EReportingQualityEntity;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.aqd.ReportObligations;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.StreamingValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingQualityDAO;
import org.n52.sos.ds.observation.EReportingHelper;
import org.n52.sos.ds.observation.ObservationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Abstract class for Hibernate streaming values
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public abstract class AbstractHibernateStreamingValue extends StreamingValue<DataEntity<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHibernateStreamingValue.class);

    protected final AbstractObservationRequest request;

    protected Criterion temporalFilterCriterion;

    private final HibernateSessionHolder sessionHolder;

    private final DaoFactory daoFactory;

    private final EReportingHelper helper;

    private Session session;

    /**
     * constructor
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param daoFactory
     *            the DAO factory
     * @param connectionProvider
     *            the connection provider
     */
    public AbstractHibernateStreamingValue(ConnectionProvider connectionProvider, DaoFactory daoFactory,
            AbstractObservationRequest request) {
        this.request = request;
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
        this.daoFactory = daoFactory;
        this.helper = new EReportingHelper(daoFactory.getSweHelper());
    }

    protected Session getSession() throws OwsExceptionReport {
        if (session == null) {
            session = sessionHolder.getSession();
        }

        return session;
    }

    protected void returnSession(Session session) {
        this.session = null;
        sessionHolder.returnSession(session);
    }

    @Override
    public ObservationStream merge() throws OwsExceptionReport {
        Map<String, OmObservation> observations = Maps.newHashMap();
        while (hasNext()) {
            DataEntity<?> nextEntity = nextEntity();
            boolean mergableObservationValue = checkForMergability(nextEntity);
            OmObservation observation = null;
            if (observations.containsKey(getDiscriminator(nextEntity)) && mergableObservationValue) {
                observation = observations.get(getDiscriminator(nextEntity));
            } else {
                observation = getObservationTemplate().cloneTemplate();
                addSpecificValuesToObservation(observation, nextEntity, request.getExtensions());
                if (!mergableObservationValue && getDiscriminator(nextEntity) == null) {
                    observations.put(Long.toString(nextEntity.getId()), observation);
                } else {
                    observations.put(getDiscriminator(nextEntity), observation);
                }
            }
            daoFactory.getObservationHelper().mergeValueToObservation(nextEntity, observation, getResponseFormat());
            sessionHolder.getSession().evict(nextEntity);
        }
        return ObservationStream.of(observations.values());
    }

    private String getDiscriminator(DataEntity<?> o) {
        return daoFactory.getObservationHelper().getDiscriminator(o);
    }

    private boolean checkForMergability(DataEntity<?> nextEntity) {
        return !(nextEntity instanceof DataArrayDataEntity);
    }

    private void addSpecificValuesToObservation(OmObservation observation, DataEntity<?> value,
            Extensions extensions) {
        boolean newSession = false;
        try {
            if (session == null) {
                session = sessionHolder.getSession();
                newSession = true;
            }
            addValueSpecificDataToObservation(value, observation, session, extensions);
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

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    public ObservationHelper getObservationHelper() {
        return getDaoFactory().getObservationHelper();
    }

    public void addValueSpecificDataToObservation(DataEntity<?> o, OmObservation observation, Session session,
            Extensions extensions) throws OwsExceptionReport {
        if (o.hasEreportingProfile()) {
            if (ReportObligations.hasFlow(extensions)) {
                ReportObligationType flow = ReportObligations.getFlow(extensions);
                if (ReportObligationType.E1A.equals(flow) || ReportObligationType.E1B.equals(flow)) {
                    int year = DateTimeHelper.makeDateTime(o.getSamplingTimeStart()).getYear();
                    EReportingQualityEntity eReportingQuality = new EReportingQualityDAO().getEReportingQuality(
                            o.getDataset().getId(), year, o.getEreportingProfile().getPrimaryObservation(), session);
                    if (eReportingQuality != null) {
                        observation.setResultQuality(helper.getGmdDomainConsistency(eReportingQuality, true));
                    } else {
                        observation
                                .setResultQuality(helper.getGmdDomainConsistency(new EReportingQualityEntity(), true));
                    }
                }
            }
        }
    }

}
