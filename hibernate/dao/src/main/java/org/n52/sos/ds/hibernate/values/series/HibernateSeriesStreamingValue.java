/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.values.series;

import org.n52.sos.ds.hibernate.dao.series.SeriesValueDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesValueTimeDAO;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValueTime;
import org.n52.sos.ds.hibernate.values.AbstractHibernateStreamingValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Hibernate series streaming value class for the series concept
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.2
 *
 */
public abstract class HibernateSeriesStreamingValue extends AbstractHibernateStreamingValue {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSeriesStreamingValue.class);

    private static final long serialVersionUID = 201732114914686926L;

    protected final SeriesValueDAO seriesValueDAO = new SeriesValueDAO();

    protected final SeriesValueTimeDAO seriesValueTimeDAO = new SeriesValueTimeDAO();

    protected long series;

    /**
     * constructor
     * 
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     */
    public HibernateSeriesStreamingValue(GetObservationRequest request, long series) {
        super(request);
        this.series = series;
    }

    @Override
    protected void queryTimes() {
        try {
            SeriesValueTime minTime;
            SeriesValueTime maxTime;
            // query with temporal filter
            if (temporalFilterCriterion != null) {
                minTime = seriesValueTimeDAO.getMinSeriesValueFor(request, series, temporalFilterCriterion, session);
                maxTime = seriesValueTimeDAO.getMaxSeriesValueFor(request, series, temporalFilterCriterion, session);
            }
            // query without temporal or indeterminate filters
            else {
                minTime = seriesValueTimeDAO.getMinSeriesValueFor(request, series, session);
                maxTime = seriesValueTimeDAO.getMaxSeriesValueFor(request, series, session);
            }
            setPhenomenonTime(createPhenomenonTime(minTime, maxTime));
            setResultTime(createResutlTime(maxTime));
            setValidTime(createValidTime(minTime, maxTime));
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while querying times", owse);
        }
    }

    @Override
    protected void queryUnit() {
        try {
            setUnit(seriesValueDAO.getUnit(request, series, session));
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while querying unit", owse);
        }

    }
}
