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
package org.n52.sos.ds.hibernate.values.series;

import org.hibernate.Session;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesValueDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesValueTimeDAO;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.n52.sos.ds.hibernate.values.AbstractHibernateStreamingValue;
import org.n52.svalbard.util.GmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Hibernate series streaming value class for the series concept
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.2
 *
 */
public abstract class HibernateSeriesStreamingValue extends AbstractHibernateStreamingValue {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSeriesStreamingValue.class);

    protected final AbstractSeriesValueDAO seriesValueDAO;

    protected final AbstractSeriesValueTimeDAO seriesValueTimeDAO;

    protected final AbstractSeriesDAO seriesDAO;

    protected long series;

    /**
     * constructor
     *
     * @param connectionProvider
     *            the connection provider
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @throws OwsExceptionReport If an error occurs
     */
    public HibernateSeriesStreamingValue(ConnectionProvider connectionProvider, DaoFactory daoFactory,
            AbstractObservationRequest request, long series, BindingRepository bindingRepository)
            throws OwsExceptionReport {
        super(connectionProvider, daoFactory, request, bindingRepository);
        this.series = series;
        this.seriesValueDAO = daoFactory.getValueDAO();
        this.seriesValueTimeDAO = daoFactory.getValueTimeDAO();
        this.seriesDAO = daoFactory.getSeriesDAO();
    }

    @Override
    protected void queryTimes() {
        Session session = null;
        try {
            session = getSession();
            ObservationTimeExtrema timeExtrema = seriesValueTimeDAO.getTimeExtremaForSeries(
                    (GetObservationRequest) request, series, temporalFilterCriterion, session);
            if (timeExtrema.isSetPhenomenonTimes()) {
                setPhenomenonTime(
                        GmlHelper.createTime(timeExtrema.getMinPhenomenonTime(), timeExtrema.getMaxPhenomenonTime()));
            }
            if (timeExtrema.isSetResultTimes()) {
                setResultTime(new TimeInstant(timeExtrema.getMaxResultTime()));
            }
            if (timeExtrema.isSetValidTime()) {
                setValidTime(GmlHelper.createTime(timeExtrema.getMinValidTime(), timeExtrema.getMaxValidTime()));
            }
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while querying times", owse);
        } finally {
            returnSession(session);
        }
    }

    @Override
    protected void queryUnit() {
        Session session = null;
        try {
            session = getSession();
            setUnit(seriesDAO.getUnit(series, session));
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while querying unit", owse);
        } finally {
            returnSession(session);
        }
    }

}
