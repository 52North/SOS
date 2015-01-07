/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCoordinate;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasGeometry;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSrid;
import org.n52.sos.util.StringHelper;

import com.vividsolutions.jts.geom.Geometry;
@Deprecated
public abstract class AbstractSpatialFilteringProfile extends AbstractIdentifierNameDescriptionEntity implements Serializable, HasGeometry, HasCoordinate, HasSrid {

    public static final String OBSERVATION = "observation";

    private static final long serialVersionUID = 8483088637171898375L;

    private AbstractObservation observation;

    private long spatialFilteringProfileId;

    private String definition;

    private String title;

    private Geometry geom;

    private Object longitude;

    private Object latitude;

    private Object altitude;

    private int srid;

    /**
     * Get SpatialFilteringProfile id
     * 
     * @return SpatialFilteringProfile id
     */
    public long getSpatialFilteringProfileId() {
        return this.spatialFilteringProfileId;
    }

    /**
     * Set SpatialFilteringProfile id
     * 
     * @param spatialFilteringProfileId
     *            SpatialFilteringProfile id to set
     */
    public void setSpatialFilteringProfileId(long spatialFilteringProfileId) {
        this.spatialFilteringProfileId = spatialFilteringProfileId;
    }

    /**
     * Get related observation
     * 
     * @return Related observation
     */
    public AbstractObservation getObservation() {
        return observation;
    }

    /**
     * Set related observation
     * 
     * @param observation
     *            Related observation
     * @return AbstractSpatialFilteringProfile
     */
    public AbstractSpatialFilteringProfile setObservation(AbstractObservation observation) {
        this.observation = observation;
        return this;
    }

    /**
     * Get definition
     * 
     * @return Definition
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Set definition
     * 
     * @param definition
     *            Definition to set
     * @return AbstractSpatialFilteringProfile
     */
    public AbstractSpatialFilteringProfile setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    /**
     * Get title
     * 
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title
     * 
     * @param title
     *            Title to set
     * @return AbstractSpatialFilteringProfile
     */
    public AbstractSpatialFilteringProfile setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Is definition set
     * 
     * @return <code>true</code>, if definition is set
     */
    public boolean isSetDefinition() {
        return StringHelper.isNotEmpty(getDefinition());
    }

    /**
     * Is title set
     * 
     * @return <code>true</code>, if title is set
     */
    public boolean isSetTitle() {
        return StringHelper.isNotEmpty(getTitle());
    }

    @Override
    public Geometry getGeom() {
        return geom;
    }

    @Override
    public AbstractSpatialFilteringProfile setGeom(Geometry geom) {
        this.geom = geom;
        return this;
    }

    @Override
    public int getSrid() {
        return srid;
    }

    @Override
    public AbstractSpatialFilteringProfile setSrid(int srid) {
        this.srid = srid;
        return this;
    }

    @Override
    public Object getLongitude() {
        return longitude;
    }

    @Override
    public AbstractSpatialFilteringProfile setLongitude(Object longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public Object getLatitude() {
        return latitude;
    }

    @Override
    public AbstractSpatialFilteringProfile setLatitude(Object latitude) {
        this.latitude = latitude;
        return this;
    }

    @Override
    public Object getAltitude() {
        return altitude;
    }

    @Override
    public AbstractSpatialFilteringProfile setAltitude(Object altitude) {
        this.altitude = altitude;
        return this;
    }

    @Override
    public boolean isSetGeometry() {
        return getGeom() != null;
    }
    
    @Override
    public boolean isSetLongLat() {
        return getLongitude() != null && getLatitude() != null;
    }

    @Override
    public boolean isSetAltitude() {
        return getAltitude() != null;
    }

    @Override
    public boolean isSetSrid() {
        return getSrid() > 0;
    }
    
    @Override
    public boolean isSpatial() {
        return isSetGeometry() || isSetLongLat();
    }

}