/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.feature;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.features.SfConstants;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.JTSHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.aquarius.ds.AccessorConnector;
import org.n52.sos.aquarius.ds.AquariusConnectionFactory;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configurable
public class AquariusFeatureQueryHandler implements FeatureQueryHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusFeatureQueryHandler.class);

    private GeometryHandler geometryHandler;

    private AquariusHelper aquariusHelper;

    private AquariusConnectionFactory connectorFactory;

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    @Inject
    public void setAquariusHelper(AquariusHelper aquariusHelper) {
        this.aquariusHelper = aquariusHelper;
    }

    @Inject
    public void setPegelOnlineConnectionFactory(AquariusConnectionFactory connectorFactory) {
        this.connectorFactory = connectorFactory;
    }

    @Override
    public AbstractFeature getFeatureByID(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        if (queryObject.isSetFeatureObject() && queryObject.getFeatureObject() instanceof Location) {
            return createSosAbstractFeature((Location) queryObject.getFeatureObject(), queryObject);
        }
        try {
            AccessorConnector accessorConnector = getConnector(queryObject.getConnection());
            return createSosAbstractFeature(queryLocation(queryObject.getFeatureIdentifier(), accessorConnector),
                    queryObject);
        } catch (ConnectionProviderException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    @Override
    public Collection<String> getFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        try {
            AccessorConnector accessorConnector = getConnector(queryObject.getConnection());
            List<String> identifiers = new LinkedList<>();
            for (Location location : getLocations(queryObject, accessorConnector)) {
                identifiers.add(location.getIdentifier());
            }
            return identifiers;
        } catch (ConnectionProviderException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    @Override
    public Map<String, AbstractFeature> getFeatures(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport {
        try {
            AccessorConnector accessorConnector = getConnector(queryObject.getConnection());
            List<Location> location = getLocations(queryObject, accessorConnector);
            if (location != null && !location.isEmpty()) {
                return createSosFeatures(location, queryObject, accessorConnector);
            }
            return Collections.emptyMap();
        } catch (ConnectionProviderException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }

    }

    @Override
    public ReferencedEnvelope getEnvelopeForFeatureIDs(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport {
        try {
            AccessorConnector accessorConnector = getConnector(queryObject.getConnection());
            if (queryObject.isSetFeatures()) {
                final Envelope envelope = new Envelope();
                for (String identifier : queryObject.getFeatures()) {
                    Location location = queryLocation(identifier, accessorConnector);
                    try {

                        final Geometry geom = getGeomtery(location);
                        if (geom != null) {
                            envelope.expandToInclude(geom.getEnvelopeInternal());
                        }
                    } catch (final OwsExceptionReport owse) {
                        LOGGER.warn(String.format("Error while adding '%s' to envelope!", location.getIdentifier()),
                                owse);
                    }
                }
                if (!envelope.isNull()) {
                    return new ReferencedEnvelope(envelope, getGeometryHandler().getStorageEPSG());
                }
            }
            return null;
        } catch (ConnectionProviderException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    @Override
    public String insertFeature(AbstractSamplingFeature samplingFeature, Object connection) throws OwsExceptionReport {
        throw new NoApplicableCodeException().withMessage("The transactional operations are not supported!");
    }

    @Override
    public int getStorageEPSG() {
        return getGeometryHandler().getStorageEPSG();
    }

    @Override
    public int getStorage3DEPSG() {
        return getGeometryHandler().getStorage3DEPSG();
    }

    protected GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    /**
     * Creates a map with FOI identifier and SOS feature
     *
     * @param features
     *            FeatureOfInterest objects
     * @param queryObject
     *            query object holder
     * @param accessorConnector
     *            Aquarius connector
     * @return Map with FOI identifier and SOS feature
     * @throws OwsExceptionReport
     *             If feature type is not supported
     */
    protected Map<String, AbstractFeature> createSosFeatures(final Collection<Location> features,
            final FeatureQueryHandlerQueryObject queryObject, AccessorConnector accessorConnector)
            throws OwsExceptionReport {
        final Map<String, AbstractFeature> sosAbstractFois = new HashMap<>(features.size());
        for (final Location feature : features) {
            final AbstractFeature sosFeature = createSosAbstractFeature(feature, queryObject);
            sosAbstractFois.put(feature.getIdentifier(), sosFeature);
        }
        return sosAbstractFois;
    }

    /**
     * Creates a SOS feature from the FeatureOfInterest object
     *
     * @param feature
     *            FeatureOfInterest object
     * @param queryObject
     *            query object holder
     * @param accessorConnector
     *            Aquarius connector
     * @return SOS feature
     * @throws OwsExceptionReport
     *             If feature type is not supported
     */
    protected AbstractFeature createSosAbstractFeature(final Location feature,
            final FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        if (feature == null) {
            return null;
        }
        final CodeWithAuthority identifier = new CodeWithAuthority(feature.getIdentifier());
        final SamplingFeature sampFeat = new SamplingFeature(identifier);
        sampFeat.addName(feature.getLocationName());
        sampFeat.setGeometry(getGeomtery(feature));
        sampFeat.setFeatureType(SfConstants.FT_SAMPLINGPOINT);
        return sampFeat;
    }

    /**
     * Get the geometry from featureOfInterest object.
     *
     * @param feature
     *            the {@link Location}
     * @return geometry the {@link Geometry}
     * @throws OwsExceptionReport
     *             If feature type is not supported
     */
    protected Geometry getGeomtery(final Location feature) throws OwsExceptionReport {
        if (feature.getLongitude() != null && feature.getLatitude() != null) {
            try {
                int epsg = getStorageEPSG();
                final String wktString =
                        getGeometryHandler().getWktString(feature.getLongitude(), feature.getLatitude(), epsg);
                Geometry geom = JTSHelper.createGeometryFromWKT(wktString, epsg);
                if (feature.getElevation() != null) {
                    geom.getCoordinate().z = JavaHelper.asDouble(feature.getElevation());
                }
                return geom;
            } catch (ParseException de) {
                throw new NoApplicableCodeException().causedBy(de);
            }
        }
        return null;
    }

    private List<Location> getLocations(FeatureQueryHandlerQueryObject queryObject,
            AccessorConnector accessorConnector) throws OwsExceptionReport {

        if (queryObject.isSetSpatialFilters() && SpatialOperator.BBOX.equals(queryObject.getSpatialFitler()
                .getOperator())) {
            List<Location> locations = new LinkedList<>();
            List<Location> spatialLocations =
                    accessorConnector.getLocations(Collections.emptySet(), queryObject.getSpatialFitler());
            if (queryObject.isSetFeatures()) {
                for (Location locationDO : spatialLocations) {
                    if (queryObject.getFeatures()
                            .contains(locationDO.getIdentifier())) {
                        locations.add(locationDO);
                    }
                }
            } else {
                locations.addAll(spatialLocations);
            }
            return locations;
        } else if (queryObject.isSetFeatures()) {
            List<Location> locations = new LinkedList<>();
            for (String identifier : queryObject.getFeatures()) {
                locations.add(queryLocation(identifier, accessorConnector));
            }
            return locations;
        }
        return accessorConnector.getLocations(aquariusHelper.getLocationDescriptionListRequest());
    }

    private Location queryLocation(String identifier, AccessorConnector accessorConnector) throws OwsExceptionReport {
        Location location = aquariusHelper.getLocation(identifier);
        return location != null ? location : accessorConnector.getLocation(identifier);
    }

    private AquariusConnector getConnector(Object connection) throws ConnectionProviderException {
        return connection != null && connection instanceof AquariusConnector ? (AquariusConnector) connection
                : connectorFactory.getConnection();
    }
}
