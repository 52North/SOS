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
import java.util.Set;
import javax.inject.Inject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.sensorweb.sos.sta.operation.StaAbstractGetObservedPropertiesHandler;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sta.StaObservedProperty;
import org.n52.shetland.ogc.sta.request.StaGetObservedPropertiesRequest;
import org.n52.shetland.ogc.sta.response.StaGetObservedPropertiesResponse;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of {@link StaAbstractGetObservedPropertiesHandler}
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetObservedPropertiesHandler extends StaAbstractGetObservedPropertiesHandler {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetObservedPropertiesHandler.class);

    private HibernateSessionHolder sessionHolder;
    private DaoFactory daoFactory;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public StaGetObservedPropertiesResponse getObservedProperties(StaGetObservedPropertiesRequest request) throws OwsExceptionReport {

        Session session = null;
        try {
            final StaGetObservedPropertiesResponse response = new StaGetObservedPropertiesResponse(request.getService(), request.getVersion());

            session = sessionHolder.getSession();
            response.setObservedProperties(queryObservedProperties(request, session));

            return response;

        } catch (final HibernateException | CodedException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying data for STA GET ObservedProperties request.");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    private Set<StaObservedProperty> queryObservedProperties(StaGetObservedPropertiesRequest request, Session session) throws CodedException {

        Set<StaObservedProperty> properties = new HashSet<>();

        ObservablePropertyDAO observablePropertyDao = daoFactory.getObservablePropertyDAO();

        if (request.getId() != null) {
            // request a single procedure

            ObservableProperty observableProperty = observablePropertyDao.getObservablePropertyForId(request.getId(), session);
            properties.add(convertObservableProperty(observableProperty));

        } else {
            // TODO add filters

            List<ObservableProperty> list = observablePropertyDao.getObservablePropertyObjects(session);
            list.forEach((ObservableProperty p) -> {
                try {
                        properties.add(convertObservableProperty(p));
                } catch (CodedException ex) {
                    // TODO throw exception
                    LOG.error("Error while querying observableProperty to STA GET ObservedProperties request.");
                }
            });
        }
        return properties;
    }

    private StaObservedProperty convertObservableProperty(ObservableProperty observableProperty) throws CodedException {

        StaObservedProperty property = new StaObservedProperty(observableProperty.getObservablePropertyId());

        property.setName(observableProperty.getName());
        property.setDescription(observableProperty.getDescription());
        // TODO add to DB
        //property.setDefinition(procedure.getDefinition());

        return property;
    }

    @Override
    public void init() {
        // no initialization needed
    }
}
