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
/**

 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.

 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH

 *

 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:

 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.

 *

 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0

 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:

 *

 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.

 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0

 *

 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.

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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCoordinate;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasGeometry;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservation;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSrid;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @since 4.0.0
 * 
 */
public class SpatialFilteringProfile extends AbstractSpatialFilteringProfile implements Serializable, HasGeometry, HasCoordinate, HasSrid, HasObservation {

    private static final long serialVersionUID = 7200974625085342134L;

    private long spatialFilteringProfileId;
    
    private Observation observation;

    private Geometry geom;

    private Object longitude;

    private Object latitude;

    private Object altitude;

    private int srid;

    public SpatialFilteringProfile() {
        super();
    }

    public long getSpatialFilteringProfileId() {
        return this.spatialFilteringProfileId;
    }

    public void setSpatialFilteringProfileId(long spatialFilteringProfileId) {
        this.spatialFilteringProfileId = spatialFilteringProfileId;
    }

    @Override
    public Geometry getGeom() {
        return geom;
    }

    @Override
    public SpatialFilteringProfile setGeom(Geometry geom) {
        this.geom = geom;
        return this;
    }

    @Override
    public int getSrid() {
        return srid;
    }

    @Override
    public SpatialFilteringProfile setSrid(int srid) {
        this.srid = srid;
        return this;
    }

    @Override
    public Object getLongitude() {
        return longitude;
    }

    @Override
    public SpatialFilteringProfile setLongitude(Object longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public Object getLatitude() {
        return latitude;
    }

    @Override
    public SpatialFilteringProfile setLatitude(Object latitude) {
        this.latitude = latitude;
        return this;
    }

    @Override
    public Object getAltitude() {
        return altitude;
    }

    @Override
    public SpatialFilteringProfile setAltitude(Object altitude) {
        this.altitude = altitude;
        return this;
    }

    public boolean isSetGeometry() {
        return getGeom() != null;
    }

    public boolean isSetLongLat() {
        return getLongitude() != null && getLatitude() != null;
    }

    public boolean isSetAltitude() {
        return getAltitude() != null;
    }

    public boolean isSetSrid() {
        return getSrid() > 0;
    }

    @Override
    public Observation getObservation() {
        return observation;
    }

    @Override
    public SpatialFilteringProfile setObservation(Observation observation) {
        this.observation = observation;
        return this;
    }

//    public class SpatialFilteringProfile extends AbstractSpatialFilteringProfile implements Serializable {
//
//        private static final long serialVersionUID = 7200974625085342134L;
//
//        /**
//         * constructor
//         */
//        public SpatialFilteringProfile() {
//            super();
//        }
//
//    }
}
