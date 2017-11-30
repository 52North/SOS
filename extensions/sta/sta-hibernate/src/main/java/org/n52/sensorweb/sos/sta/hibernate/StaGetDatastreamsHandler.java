/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sensorweb.sos.sta.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.sensorweb.sos.sta.operation.StaAbstractGetDatastreamsHandler;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sta.StaDatastream;
import org.n52.shetland.ogc.sta.request.StaGetDatastreamsRequest;
import org.n52.shetland.ogc.sta.response.StaGetDatastreamsResponse;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of {@link StaAbstractGetDatastreamsHandler}
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetDatastreamsHandler extends StaAbstractGetDatastreamsHandler {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetDatastreamsHandler.class);

    private HibernateSessionHolder sessionHolder;

    private DaoFactory daoFactory;
    private Locale defaultLanguage;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public StaGetDatastreamsResponse getDatastreams(StaGetDatastreamsRequest request) throws OwsExceptionReport {

        Session session = null;
        try {
            final StaGetDatastreamsResponse response = new StaGetDatastreamsResponse(request.getService(), request.getVersion());

            session = sessionHolder.getSession();
            response.setDatastreams(queryDatastreams(request, session));

            return response;

        } catch (final HibernateException | CodedException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying data for STA GET Datastreams request.");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    private Set<StaDatastream> queryDatastreams(StaGetDatastreamsRequest request, Session session) throws CodedException {

        Set<StaDatastream> datastreams = new HashSet<>();

        AbstractSeriesDAO seriesDao;

        try {
            seriesDao = daoFactory.getSeriesDAO();

            if (request.getId() != null) {
                // request a single datastream

                Series s = seriesDao.getSeriesForId(request.getId(), session);
                datastreams.add(convertSeries(s));

            } else {
                // TODO add filters

                List<Series> list = seriesDao.getSeries(session);
                list.forEach((Series s) -> {
                    datastreams.add(convertSeries(s));
                });
            }
        } catch (OwsExceptionReport ex) {
            // TODO throw exception
            LOG.error("Error while querying series to STA GET Datastream request.");
        }
        return datastreams;
    }


    private StaDatastream convertSeries(Series series) {

        StaDatastream datastream = new StaDatastream(series.getSeriesId());

//        datastream.setName(series.getName());
//        datastream.setDescription(series.getDescription());

        // TODO derive properties from series observation: observation type, unit of measurement, observed area, result time
        datastream.setPhenomenonTime(new TimePeriod(series.getFirstTimeStamp(), series.getLastTimeStamp()));

        // TODO add entities: thing, sensor, observedProperty, observations

        return datastream;
    }

//    private void convertObservations(List<Observation<?>> observations, StaDatastream datastream) {
//
//        // TODO convert observations and derive datastream parameters from observations
//
//        // TODO add observations to datastream
//    }
//
//    @SuppressWarnings("unchecked")
//    private List<Observation<?>> queryObservation(Long seriesId, Session session)
//            throws OwsExceptionReport {
//
//        Criteria c = daoFactory.getObservationDAO().getDefaultObservationCriteria(session);
//        c.add(Restrictions.eq(Series.ID, seriesId));
//
//        LOG.debug("QUERY queryObservation(request): {}", HibernateHelper.getSqlString(c));
//        return c.list();
//    }

    @Override
    public void init() {
        // no initialization needed
    }
}
