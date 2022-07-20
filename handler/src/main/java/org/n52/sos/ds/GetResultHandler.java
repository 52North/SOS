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
package org.n52.sos.ds;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Session;
import org.locationtech.jts.geom.Envelope;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.io.request.IoParameters;
import org.n52.sensorweb.server.db.old.dao.DbQuery;
import org.n52.sensorweb.server.db.old.dao.DbQueryFactory;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.old.HibernateSessionStore;
import org.n52.series.db.old.dao.FeatureDao;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.sos.ds.dao.GetResultDao;
import org.n52.sos.service.SosSettings;
import org.n52.svalbard.ConformanceClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Implementation of the abstract class AbstractGetResultHandler
 *
 * @since 4.0.0
 *
 */
@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class GetResultHandler extends AbstractGetResultHandler implements ApiQueryHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetResultHandler.class);

    private HibernateSessionStore sessionStore;

    private Optional<GetResultDao> dao;

    private boolean strictSpatialFilteringProfile;

    private DbQueryFactory dbQueryFactory;

    public GetResultHandler() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setGetResultDao(Optional<GetResultDao> getResultDao) {
        this.dao = getResultDao;
    }

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setDbQueryFactory(DbQueryFactory dbQueryFactory) {
        this.dbQueryFactory = dbQueryFactory;
    }

    @Setting(SosSettings.STRICT_SPATIAL_FILTERING_PROFILE)
    public void setStrictSpatialFilteringProfile(final boolean strictSpatialFilteringProfile) {
        this.strictSpatialFilteringProfile = strictSpatialFilteringProfile;
    }

    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            if (strictSpatialFilteringProfile) {
                return Sets.newHashSet(ConformanceClasses.SOS_V2_SPATIAL_FILTERING_PROFILE);
            }
        }
        return super.getConformanceClasses(service, version);
    }

    @Override
    public boolean isSupported() {
        return dao.isPresent();
    }

    @Override
    @Transactional()
    public GetResultResponse getResult(final GetResultRequest request) throws OwsExceptionReport {
        final GetResultResponse response = new GetResultResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        if (isSupported()) {
            Session session = sessionStore.getSession();
            try {
                List<FeatureEntity> features = new FeatureDao(session).getAllInstances(createDbQuery(request));
                if (features == null || features.isEmpty()) {
                    return response;
                }
                request.setFeatureIdentifiers(features.stream()
                        .map(f -> f.getIdentifier())
                        .collect(Collectors.toList()));
                dao.get().queryResultData(request, response, session);
            } finally {
                sessionStore.returnSession(session);
            }
        }
        return response;
    }

    private DbQuery createDbQuery(GetResultRequest request) {
        Map<String, String> map = Maps.newHashMap();
        if (request.isSetFeatureOfInterest()) {
            map.put(IoParameters.FEATURES, listToString(request.getFeatureIdentifiers()));
        }
        if (request.isSetSpatialFilter() && !request.hasSpatialFilteringProfileSpatialFilter()) {
            if (SpatialOperator.BBOX.equals(request.getSpatialFilter().getOperator())) {
                if (request.getSpatialFilter().getGeometry().isGeometry()
                        && request.getSpatialFilter().getGeometry().getGeometry().isPresent()) {
                    Envelope envelope =
                            request.getSpatialFilter().getGeometry().getGeometry().get().getEnvelopeInternal();
                    if (envelope != null) {
                        List<Double> bbox = Lists.newArrayList();
                        bbox.add(envelope.getMinX());
                        bbox.add(envelope.getMinY());
                        bbox.add(envelope.getMaxX());
                        bbox.add(envelope.getMaxY());
                        map.put(IoParameters.BBOX, Joiner.on(",").join(bbox));
                    }
                } else if (request.getSpatialFilter().getGeometry().isEnvelope()
                        && request.getSpatialFilter().getGeometry().getEnvelope().isPresent()) {
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
        return createDbQuery(IoParameters.createFromSingleValueMap(map));
    }

    @Override
    public DbQuery createDbQuery(IoParameters parameters) {
        return dbQueryFactory.createFrom(parameters);
    }

}
