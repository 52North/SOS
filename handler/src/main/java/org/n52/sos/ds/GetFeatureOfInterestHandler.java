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

import java.util.ArrayList;
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
import org.n52.io.geojson.old.GeojsonPoint;
import org.n52.io.request.IoParameters;
import org.n52.io.request.RequestSimpleParameterSet;
import org.n52.io.response.BBox;
import org.n52.proxy.db.dao.ProxyFeatureDao;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.HibernateSessionStore;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.om.features.samplingFeatures.InvalidSridException;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.ds.dao.GetFeatureOfInterestDao;
import org.n52.sos.util.GeometryHandler;

import com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Envelope;

public class GetFeatureOfInterestHandler extends AbstractGetFeatureOfInterestHandler implements ProxyQueryHelper {

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

            response.setAbstractFeature(getFeatures(request, session));
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying data for GetFeatureOfInterest!");
        } finally {
            sessionStore.returnSession(session);
        }
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
        Set<FeatureEntity> featureEntities = new HashSet<>(queryFeaturesForParameter(request, session));
        if (featureEntities == null || featureEntities.isEmpty()) {
            return new FeatureCollection();
        }
        if (dao != null) {
            request.setFeatureIdentifiers(featureEntities.stream().map(f -> f.getDomainId()).collect(Collectors.toSet()));
            return new FeatureCollection(dao.getFeatureOfInterest(request));
        }
        return new FeatureCollection(createFeatures(featureEntities));
    }

    private Map<String, AbstractFeature> createFeatures(Set<FeatureEntity> featureEntities) throws InvalidSridException, OwsExceptionReport {
        final Map<String, AbstractFeature> map = new HashMap<>(featureEntities.size());
        for (final FeatureEntity feature : featureEntities) {
            final AbstractFeature abstractFeature = createFeature(feature);
            map.put(abstractFeature.getIdentifier(), abstractFeature);
        }
        return map;
    }

    private AbstractFeature createFeature(FeatureEntity feature) throws InvalidSridException, OwsExceptionReport {
        final SamplingFeature sampFeat = new SamplingFeature(new CodeWithAuthority(feature.getDomainId()));
        if (feature.isSetName()) {
            sampFeat.addName(feature.getName());
        }
        if (!Strings.isNullOrEmpty(feature.getDescription())) {
            sampFeat.setDescription(feature.getDescription());
        }
        if (feature.isSetGeometry()) {
            sampFeat.setGeometry(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(feature.getGeometry()));
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
    private List<FeatureEntity> queryFeaturesForParameter(GetFeatureOfInterestRequest req, Session session)
            throws OwsExceptionReport {
        try {
            return new ProxyFeatureDao(session).getAllInstances(createDbQuery(req));
        } catch (DataAccessException dae) {
            throw new NoApplicableCodeException().causedBy(dae)
                    .withMessage("Error while querying data for GetFeatureOfInterest!");
        }
    }

    private DbQuery createDbQuery(GetFeatureOfInterestRequest req) {
        RequestSimpleParameterSet rsps = new RequestSimpleParameterSet();
        if (req.isSetFeatureOfInterestIdentifiers()) {
            rsps.setParameter(IoParameters.FEATURES, IoParameters.getJsonNodeFrom(listToString(req.getFeatureIdentifiers())));
        }
        if (req.isSetProcedures()) {
            rsps.setParameter(IoParameters.PROCEDURES, IoParameters.getJsonNodeFrom(listToString(req.getProcedures())));
        }
        if (req.isSetObservableProperties()) {
            rsps.setParameter(IoParameters.PHENOMENA, IoParameters.getJsonNodeFrom(listToString(req.getObservedProperties())));
        }
        if (req.isSetSpatialFilters()) {
            Envelope envelope = null;
            for (SpatialFilter spatialFilter : req.getSpatialFilters()) {
                if (SpatialOperator.BBOX.equals(spatialFilter.getOperator())) {
                    if (envelope == null) {
                        envelope = spatialFilter.getGeometry().getEnvelopeInternal();
                    } else {
                        envelope.expandToInclude(spatialFilter.getGeometry().getEnvelopeInternal());
                    }
                }
            }
            if (envelope != null) {
                BBox bbox = new BBox();
                GeojsonPoint ll = new GeojsonPoint();
                ll.setCoordinates(toArray(envelope.getMinX(), envelope.getMinY()));
                bbox.setLl(ll);
                GeojsonPoint ur = new GeojsonPoint();
                ur.setCoordinates(toArray(envelope.getMaxX(), envelope.getMaxY()));
                bbox.setUr(ur);
                rsps.setParameter(IoParameters.BBOX, IoParameters.getJsonNodeFrom(bbox));
            }
        }
        rsps.setParameter(IoParameters.MATCH_DOMAIN_IDS, IoParameters.getJsonNodeFrom(true));
        return new DbQuery(IoParameters.createFromQuery(rsps));
    }

    private Double[] toArray(double x, double y) {
       Double[] array = new Double[2];
       array[0] = x;
       array[1] = y;
       return array;
    }

    protected GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

}
