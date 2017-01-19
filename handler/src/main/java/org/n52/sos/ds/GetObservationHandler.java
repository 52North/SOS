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
package org.n52.sos.ds;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.io.request.IoParameters;
import org.n52.io.request.RequestSimpleParameterSet;
import org.n52.proxy.db.dao.ProxyFeatureDao;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.HibernateSessionStore;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.sos.ds.dao.GetObservationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetObservationHandler extends AbstractGetObservationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationHandler.class);

    private HibernateSessionStore sessionStore;
    private GetObservationDao dao;

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setGetObservationDao(GetObservationDao getObservationDao) {
        this.dao = getObservationDao;
    }

    public GetObservationHandler() {
        super(SosConstants.SOS);
    }

    @Override
    public GetObservationResponse getObservation(GetObservationRequest request) throws OwsExceptionReport {
        Session session = sessionStore.getSession();
        try {
            GetObservationResponse response = new GetObservationResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            response.setResponseFormat(request.getResponseFormat());
            response.setResultModel(request.getResultModel());
            List<FeatureEntity> features = new ProxyFeatureDao(session).getAllInstances(createDbQuery(request));
            if (features == null || (features != null && features.isEmpty())) {
                return response;
            }
            request.setFeatureIdentifiers(features.stream().map(f -> f.getDomainId()).collect(Collectors.toList()));
            dao.queryObservationData(request, response);
            return response;
        } catch (DataAccessException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying data for GetObservation!");
        } finally {
            sessionStore.returnSession(session);
        }
    }

    private DbQuery createDbQuery(GetObservationRequest request) {
        RequestSimpleParameterSet rsps = new RequestSimpleParameterSet();
        if (request.isSetFeatureOfInterest()) {
            rsps.addParameter(IoParameters.FEATURES, IoParameters.getJsonNodeFrom(request.getFeatureIdentifiers()));
        }
        if (request.isSetSpatialFilter() && !request.hasSpatialFilteringProfileSpatialFilter()) {
            // TODO
        }
        rsps.addParameter(IoParameters.MATCH_DOMAIN_IDS, IoParameters.getJsonNodeFrom(true));
        return new DbQuery(IoParameters.createFromQuery(rsps));
    }

}
