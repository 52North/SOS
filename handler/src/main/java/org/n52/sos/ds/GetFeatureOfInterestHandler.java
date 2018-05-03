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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.locationtech.jts.geom.Envelope;
import org.n52.io.request.IoParameters;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.HibernateSessionStore;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.dataset.NotInitializedDataset;
import org.n52.series.db.dao.DatasetDao;
import org.n52.series.db.dao.DbQuery;
import org.n52.series.db.dao.FeatureDao;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.om.features.samplingFeatures.InvalidSridException;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;
import org.n52.shetland.util.EnvelopeOrGeometry;
import org.n52.sos.ds.dao.GetFeatureOfInterestDao;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSConverter;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GetFeatureOfInterestHandler extends AbstractGetFeatureOfInterestHandler implements ApiQueryHelper {

    private HibernateSessionStore sessionStore;
    private GetFeatureOfInterestDao dao;
    private GeometryHandler geometryHandler;

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setGetFeatureOfInterestDao(Optional<GetFeatureOfInterestDao> getFeatureOfInterestDao) {
        if (getFeatureOfInterestDao.isPresent()) {
            this.dao = getFeatureOfInterestDao.get();
        }
    }

    public GetFeatureOfInterestHandler() {
        super(SosConstants.SOS);
    }

    @Override
    public GetFeatureOfInterestResponse getFeatureOfInterest(GetFeatureOfInterestRequest request)
            throws OwsExceptionReport {
        Session session = sessionStore.getSession();
        try {
            GetFeatureOfInterestResponse response = new GetFeatureOfInterestResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());

            if (isSos100(request)) {
                // sos 1.0.0 either or
                if (isMixedFeatureIdentifierAndSpatialFilters(request)) {
                    throw new NoApplicableCodeException()
                            .withMessage("Only one out of featureofinterestid or location possible.");
                } else if (isFeatureIdentifierRequest(request) || isSpatialFilterRequest(request)) {
                    response.setAbstractFeature(getFeatures(request, session));
                } else {
                    throw new CompositeOwsException(new MissingParameterValueException(
                            Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID),
                            new MissingParameterValueException(Sos1Constants.GetFeatureOfInterestParams.location));
                }
            } else // SOS 2.0
            {
            response.setAbstractFeature(getFeatures(request, session));
            }
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying data for GetFeatureOfInterest!");
        } finally {
            sessionStore.returnSession(session);
        }
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    /**
     * Get featureOfInterest as a feature collection
     *
     * @param request
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate session
     * @return Feature collection with requested featuresOfInterest
     * @throws OwsExceptionReport
     *             If an error occurs during processing
     */
    private FeatureCollection getFeatures(GetFeatureOfInterestRequest request, Session session)
            throws OwsExceptionReport {
        Set<AbstractFeatureEntity> featureEntities = new HashSet<>(queryFeaturesForParameter(request, session));
        if (featureEntities == null || featureEntities.isEmpty()) {
            return new FeatureCollection();
        }
        if (dao != null) {
            request.setFeatureIdentifiers(featureEntities.stream().map(f -> f.getIdentifier()).collect(Collectors.toSet()));
            return new FeatureCollection(dao.getFeatureOfInterest(request));
        }
        return new FeatureCollection(createFeatures(featureEntities));
    }

    private Map<String, AbstractFeature> createFeatures(Set<AbstractFeatureEntity> featureEntities) throws InvalidSridException, OwsExceptionReport {
        final Map<String, AbstractFeature> map = new HashMap<>(featureEntities.size());
        for (final AbstractFeatureEntity feature : featureEntities) {
            final AbstractFeature abstractFeature = createFeature(feature);
            map.put(abstractFeature.getIdentifier(), abstractFeature);
        }
        return map;
    }

    private AbstractFeature createFeature(AbstractFeatureEntity feature) throws InvalidSridException, OwsExceptionReport {
        final SamplingFeature sampFeat = new SamplingFeature(new CodeWithAuthority(feature.getIdentifier()));
        if (feature.isSetName()) {
            sampFeat.addName(feature.getName());
        }
        if (!Strings.isNullOrEmpty(feature.getDescription())) {
            sampFeat.setDescription(feature.getDescription());
        }
        if (feature.isSetGeometry() && !feature.getGeometryEntity().isEmpty()) {
            sampFeat.setGeometry(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(
                    JTSConverter.convert(feature.getGeometryEntity().getGeometry())));
        }
        final Set<FeatureEntity> parentFeatures = feature.getParents();
        if (parentFeatures != null && !parentFeatures.isEmpty()) {
            final List<AbstractFeature> sampledFeatures = new ArrayList<>(parentFeatures.size());
            for (final FeatureEntity parentFeature : parentFeatures) {
                sampledFeatures.add(createFeature(parentFeature));
            }
            sampFeat.setSampledFeatures(sampledFeatures);
        }
        return sampFeat;
    }
    /**
     * Get featureOfInterest identifiers for requested parameters
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate session
     * @return Resulting FeatureOfInterest identifiers list
     * @throws OwsExceptionReport
     *             If an error occurs during processing
     */
    private Collection<AbstractFeatureEntity> queryFeaturesForParameter(GetFeatureOfInterestRequest req, Session session)
            throws OwsExceptionReport {
//        try {
             Collection<DatasetEntity> datasets = new DatasetDao(session).get(createDbQuery(req));
        if (datasets != null) {
            return datasets.stream()
                    .filter(d -> d.isSetFeature() && (d.isPublished() || !d.isPublished()
                            && d.getValueType().equalsIgnoreCase(NotInitializedDataset.DATASET_TYPE)))
                    .map(d -> d.getFeature()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
//            return new FeatureDao(session).getAllInstances(createDbQuery(req));
//        } catch (DataAccessException dae) {
//            throw new NoApplicableCodeException().causedBy(dae)
//                    .withMessage("Error while querying data for GetFeatureOfInterest!");
//        }
    }

    private DbQuery createDbQuery(GetFeatureOfInterestRequest req) {
        Map<String, String> map = Maps.newHashMap();
        if (req.isSetFeatureOfInterestIdentifiers()) {
            map.put(IoParameters.FEATURES, listToString(req.getFeatureIdentifiers()));
        }
        if (req.isSetProcedures()) {
            map.put(IoParameters.PROCEDURES, listToString(req.getProcedures()));
        }
        if (req.isSetObservableProperties()) {
            map.put(IoParameters.PHENOMENA, listToString(req.getObservedProperties()));
        }
        if (req.isSetSpatialFilters()) {
            Envelope envelope = null;
            for (SpatialFilter spatialFilter : req.getSpatialFilters()) {
                if (SpatialOperator.BBOX.equals(spatialFilter.getOperator())) {
                    Envelope toAdd = getEnvelope(spatialFilter.getGeometry());
                    if (toAdd != null) {
                        if (envelope == null) {
                            envelope = toAdd;
                        } else {
                            envelope.expandToInclude(toAdd);
                        }
                    }
                }
            }
            if (envelope != null) {
                List<Double> bbox = Lists.newArrayList();
                bbox.add(envelope.getMinX());
                bbox.add(envelope.getMinY());
                bbox.add(envelope.getMaxX());
                bbox.add(envelope.getMaxY());
                map.put(IoParameters.BBOX, Joiner.on(",").join(bbox));
            }
        }
        map.put(IoParameters.MATCH_DOMAIN_IDS, Boolean.toString(true));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    private Envelope getEnvelope(EnvelopeOrGeometry geometry) {
        if (geometry.isGeometry() && geometry.getGeometry().isPresent()) {
            return geometry.getGeometry().get().getEnvelopeInternal();
        } else if (geometry.isEnvelope() && geometry.getEnvelope().isPresent()) {
            return geometry.getEnvelope().get().getEnvelope();
        }
        return null;
    }

    private Double[] toArray(double x, double y) {
       Double[] array = new Double[2];
       array[0] = x;
       array[1] = y;
       return array;
    }

    /**
     * Check if the request contains spatial filters
     *
     * @param request
     *            GetFeatureOfInterest request to check
     * @return <code>true</code>, if the request contains spatial filters
     */
    private boolean isSpatialFilterRequest(final GetFeatureOfInterestRequest request) {
        return request.getSpatialFilters() != null && !request.getSpatialFilters().isEmpty();
    }

    /**
     * Check if the request contains feature identifiers
     *
     * @param request
     *            GetFeatureOfInterest request to check
     * @return <code>true</code>, if the request contains feature identifiers
     */
    private boolean isFeatureIdentifierRequest(final GetFeatureOfInterestRequest request) {
        return request.getFeatureIdentifiers() != null && !request.getFeatureIdentifiers().isEmpty();
    }

    /**
     * Check if the request contains spatial filters and feature identifiers
     *
     * @param request
     *            GetFeatureOfInterest request to check
     * @return <code>true</code>, if the request contains spatial filters and
     *         feature identifiers
     */
    private boolean isMixedFeatureIdentifierAndSpatialFilters(final GetFeatureOfInterestRequest request) {
        return isFeatureIdentifierRequest(request) && isSpatialFilterRequest(request);
    }

    /**
     * Check if the requested version is SOS 1.0.0
     *
     * @param request
     *            GetFeatureOfInterest request to check
     * @return <code>true</code>, if the requested version is SOS 1.0.0
     */
    private boolean isSos100(final GetFeatureOfInterestRequest request) {
        return request.getVersion().equals(Sos1Constants.SERVICEVERSION);
    }

    protected GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

}
