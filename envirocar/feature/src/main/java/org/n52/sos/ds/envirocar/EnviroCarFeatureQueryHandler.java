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
package org.n52.sos.ds.envirocar;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.envirocar.server.core.entities.Track;
import org.envirocar.server.core.entities.Tracks;
import org.envirocar.server.core.filter.TrackFilter;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.SosHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.CoordinateSequences;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class EnviroCarFeatureQueryHandler extends EnviroCarDaoFactoryHolder implements FeatureQueryHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnviroCarFeatureQueryHandler.class);

    @Override
    public AbstractFeature getFeatureByID(String featureID, Object connection, String version, int responseSrid)
            throws OwsExceptionReport {
        EnviroCarDaoFactory daoFac = EnviroCarDaoFactoryHolder.getEnviroCarDaoFactory(connection);
        Track track = daoFac.getTrackDAO().getById(featureID);
        return createAbstractFeature(track, version, daoFac);
    }

    @Override
    public Collection<String> getFeatureIDs(SpatialFilter filter, Object connection) throws OwsExceptionReport {
        EnviroCarDaoFactory daoFac = EnviroCarDaoFactoryHolder.getEnviroCarDaoFactory(connection);
        Tracks tracks = daoFac.getTrackDAO().get(new TrackFilter(filter.getGeometry(), null));
        return getIds(tracks);
    }

    @Override
    public Map<String, AbstractFeature> getFeatures(Collection<String> foiIDs, List<SpatialFilter> list,
            Object connection, String version, int responseSrid) throws OwsExceptionReport {
        EnviroCarDaoFactory daoFac = EnviroCarDaoFactoryHolder.getEnviroCarDaoFactory(connection);
        
        return null;
    }

    @Override
    public SosEnvelope getEnvelopeForFeatureIDs(Collection<String> featureIDs, Object connection)
            throws OwsExceptionReport {
        EnviroCarDaoFactory daoFac = EnviroCarDaoFactoryHolder.getEnviroCarDaoFactory(connection);
        return null;
    }

    @Override
    public String insertFeature(SamplingFeature samplingFeature, Object connection) throws OwsExceptionReport {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getConnectionProviderIdentifier() {
        return HibernateDatasourceConstants.OGM_CONNECTION_PROVIDER_IDENTIFIER;
    }

    @Override
    public int getDefaultEPSG() {
        return GeometryHandler.getInstance().getDefaultEPSG();
    }

    @Override
    public int getDefault3DEPSG() {
        return GeometryHandler.getInstance().getDefault3DEPSG();
    }

    private AbstractFeature createAbstractFeature(Track track, String version, EnviroCarDaoFactory daoFac) {
        if (track == null) {
            return null;
        }
        String checkedFoiID = null;
        if (SosHelper.checkFeatureOfInterestIdentifierForSosV2(track.getIdentifier(), version)) {
            checkedFoiID = track.getIdentifier();
        }
        final CodeWithAuthority identifier = new CodeWithAuthority(checkedFoiID);
        final SamplingFeature sampFeat = new SamplingFeature(identifier);
        if (!Strings.isNullOrEmpty(track.getName())) {
            sampFeat.setName(SosHelper.createCodeTypeListFromCSV(track.getName()));
        }
        sampFeat.setDescription(null);
        // get single locations from Measurement
        sampFeat.setGeometry(getGeomtery(track, daoFac));
//        sampFeat.setFeatureType(feature.getFeatureOfInterestType().getFeatureOfInterestType());
        return sampFeat;
    }
    
    /**
     * Get the geometry from featureOfInterest object.
     * @param daoFac 
     * 
     * @param feature
     * @return geometry
     * @throws OwsExceptionReport
     */
    protected Geometry getGeomtery(final Track track, EnviroCarDaoFactory daoFac) throws OwsExceptionReport {
        if (track != null) {
            // get geometries from track;
            List<Coordinate> coordinates = Lists.newArrayList();
            for (Geometry geometry : daoFac.getMeasurementDAO().getGeometries(track)) {
//              GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(feature.getGeom());
                coordinates.add(geometry.getCoordinate());
            }
            GeometryFactory gf = new GeometryFactory();
            return new GeometryFactory().createLineString(coordinates.toArray(new Coordinate[coordinates.size()]));

        }
        return null;
    }

    private Collection<String> getIds(Tracks tracks) {
        Set<String> ids = Sets.newHashSet();
        for (Track track : tracks) {
            ids.add(track.getIdentifier());
        }
        return ids;
    }

}
