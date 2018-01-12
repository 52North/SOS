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
package org.n52.sos.ds;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Session;
import org.locationtech.jts.geom.Envelope;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.io.request.IoParameters;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.HibernateSessionStore;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.series.db.dao.FeatureDao;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.sos.ds.dao.GetObservationDao;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GetObservationHandler extends AbstractGetObservationHandler implements ApiQueryHelper {

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
        if (request.getVersion().equals(Sos1Constants.SERVICEVERSION) &&
                request.getObservedProperties().isEmpty()) {
           throw new MissingObservedPropertyParameterException();
       }
       if (request.isSetResultFilter()) {
           throw new NotYetSupportedException("result filtering");
       }
        Session session = sessionStore.getSession();
        try {
            GetObservationResponse response = new GetObservationResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            response.setResponseFormat(request.getResponseFormat());
            response.setResultModel(request.getResultModel());
            List<FeatureEntity> features = new FeatureDao(session).getAllInstances(createDbQuery(request));
            if (features == null || (features != null && features.isEmpty())) {
                return response;
            }
            request.setFeatureIdentifiers(features.stream().map(f -> f.getIdentifier()).collect(Collectors.toList()));
            dao.queryObservationData(request, response);
            return response;
        } catch (DataAccessException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying data for GetObservation!");
        } finally {
            sessionStore.returnSession(session);
        }
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    private DbQuery createDbQuery(GetObservationRequest request) {
        Map<String, String> map = Maps.newHashMap();
        if (request.isSetFeatureOfInterest()) {
            map.put(IoParameters.FEATURES, listToString(request.getFeatureIdentifiers()));
        }
        if (request.isSetSpatialFilter() && !request.hasSpatialFilteringProfileSpatialFilter()) {
            if (SpatialOperator.BBOX.equals(request.getSpatialFilter().getOperator())) {
                if (request.getSpatialFilter().getGeometry().isGeometry() && request.getSpatialFilter().getGeometry().getGeometry().isPresent()) {
                    Envelope envelope = request.getSpatialFilter().getGeometry().getGeometry().get().getEnvelopeInternal();
                    if (envelope != null) {
                        List<Double> bbox = Lists.newArrayList();
                        bbox.add(envelope.getMinX());
                        bbox.add(envelope.getMinY());
                        bbox.add(envelope.getMaxX());
                        bbox.add(envelope.getMaxY());
                        map.put(IoParameters.BBOX, Joiner.on(",").join(bbox));
                    }
                } else if (request.getSpatialFilter().getGeometry().isEnvelope() && request.getSpatialFilter().getGeometry().getEnvelope().isPresent()){
                    Envelope envelope = request.getSpatialFilter().getGeometry().getEnvelope().get().getEnvelope();
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
        }
        map.put(IoParameters.MATCH_DOMAIN_IDS, Boolean.toString(true));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    private Double[] toArray(double x, double y) {
        Double[] array = new Double[2];
        array[0] = x;
        array[1] = y;
        return array;
     }

}
