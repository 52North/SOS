/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Session;
import org.locationtech.jts.geom.Envelope;
import org.n52.io.request.IoParameters;
import org.n52.sensorweb.server.db.old.dao.DbQuery;
import org.n52.sensorweb.server.db.old.dao.DbQueryFactory;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.old.HibernateSessionStore;
import org.n52.series.db.old.dao.FeatureDao;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.sos.ds.dao.GetObservationDao;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class GetObservationHandler extends AbstractGetObservationHandler implements ApiQueryHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationHandler.class);

    private HibernateSessionStore sessionStore;

    private GetObservationDao dao;

    private DbQueryFactory dbQueryFactory;

    private GeometryHandler geometryHandler;

    public GetObservationHandler() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setGetObservationDao(GetObservationDao getObservationDao) {
        this.dao = getObservationDao;
    }

    @Inject
    public void setDbQueryFactory(DbQueryFactory dbQueryFactory) {
        this.dbQueryFactory = dbQueryFactory;
    }

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    @Override
    @Transactional
    public GetObservationResponse getObservation(GetObservationRequest request) throws OwsExceptionReport {
        if (request.getVersion().equals(Sos1Constants.SERVICEVERSION) && request.getObservedProperties().isEmpty()) {
            throw new MissingObservedPropertyParameterException();
        }
        GetObservationResponse response = new GetObservationResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        response.setResponseFormat(request.getResponseFormat());
        response.setResultModel(request.getResultModel());
        Session session = sessionStore.getSession();
        try {
            List<FeatureEntity> features = new FeatureDao(session).getAllInstances(createDbQuery(request));
            if (features == null || features.isEmpty()) {
                return response;
            }
            request.setFeatureIdentifiers(features.stream().map(f -> f.getIdentifier()).collect(Collectors.toList()));
            dao.queryObservationData(request, response, session);
        } finally {
            sessionStore.returnSession(session);
        }
        return response;
    }

    @Override
    public boolean isSupported() {
        return dao != null;
    }

    private GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    private DbQuery createDbQuery(GetObservationRequest request) throws OwsExceptionReport {
        Map<String, String> map = Maps.newHashMap();
        if (request.isSetFeatureOfInterest()) {
            map.put(IoParameters.FEATURES, listToString(request.getFeatureIdentifiers()));
        }
        if (request.isSetSpatialFilter() && !request.hasSpatialFilteringProfileSpatialFilter()) {
            Envelope envelope = null;
            if (SpatialOperator.BBOX.equals(request.getSpatialFilter().getOperator())) {
                envelope = getGeometryHandler().getEnvelope(request.getSpatialFilter().getGeometry());
                if (envelope != null) {
                    List<Double> bbox = Lists.newArrayList();
                    bbox.add(envelope.getMinX());
                    bbox.add(envelope.getMinY());
                    bbox.add(envelope.getMaxX());
                    bbox.add(envelope.getMaxY());
                    map.put(IoParameters.BBOX, Joiner.on(",").join(bbox));
                }
            }
        }
        map.put(IoParameters.MATCH_DOMAIN_IDS, Boolean.toString(true));
        return createDbQuery(IoParameters.createFromSingleValueMap(map));
    }

    @Override
    public DbQuery createDbQuery(IoParameters parameters) {
        return dbQueryFactory.createFrom(parameters);
    }

}
