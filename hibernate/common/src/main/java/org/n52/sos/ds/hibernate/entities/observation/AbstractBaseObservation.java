/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.observation;

import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;
import org.n52.sos.util.CollectionHelper;

import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractBaseObservation
        extends AbstractIdentifierNameDescriptionEntity
        implements BaseObservation {
    private static final long serialVersionUID = 5618279055717823761L;

    private long observationId;
    private Set<Offering> offerings = new HashSet<>(0);
    private Geometry samplingGeometry;
    private Object latitude;
    private Object longitude;
    private Object altitude;
    private int srid;
    private boolean deleted;
    private boolean child;
    private boolean parent;
    private Set<Parameter> parameters = new HashSet<>(0);

    @Override
    public boolean getDeleted() {
        return deleted;
    }

    @Override
    public long getObservationId() {
        return observationId;
    }

    @Override
    public void setObservationId(long observationId) {
        this.observationId = observationId;
    }

    @Override
    public Set<Offering> getOfferings() {
        return offerings;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void setOfferings(Object offerings) {
        if (offerings instanceof Set<?>) {
            this.offerings = (Set<Offering>) offerings;
        } else {
            getOfferings().add((Offering) offerings);
        }
    }

    @Override
    public Geometry getSamplingGeometry() {
        return samplingGeometry;
    }

    @Override
    public void setSamplingGeometry(Geometry samplingGeometry) {
        this.samplingGeometry = samplingGeometry;
    }

    @Override
    public boolean hasSamplingGeometry() {
        return getSamplingGeometry() != null && !getSamplingGeometry().isEmpty();
    }
    
    @Override
    public Object getLongitude() {
        return longitude;
    }

    @Override
    public AbstractBaseObservation setLongitude(final Object longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public Object getLatitude() {
        return latitude;
    }

    @Override
    public AbstractBaseObservation setLatitude(final Object latitude) {
        this.latitude = latitude;
        return this;
    }

    @Override
    public Object getAltitude() {
        return altitude;
    }

    @Override
    public AbstractBaseObservation setAltitude(final Object altitude) {
        this.altitude = altitude;
        return this;
    }

    public boolean isSetLongLat() {
        return getLongitude() != null && getLatitude() != null;
    }

    public boolean isSetAltitude() {
        return getAltitude() != null;
    }

    public boolean isSpatial() {
        return hasSamplingGeometry() || isSetLongLat();
    }
    
    @Override
    public int getSrid() {
        return srid;
    }

    @Override
    public AbstractBaseObservation setSrid(final int srid) {
        this.srid = srid;
        return this;
    }
    
    public boolean isSetSrid() {
        return getSrid() > 0;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean isChild() {
        return this.child;
    }

    @Override
    public boolean isParent() {
        return this.parent;
    }

    @Override
    public void setParent(boolean parent) {
        this.parent = parent;
    }

    @Override
    public void setChild(boolean child) {
        this.child = child;
    }
    
    @Override
    public Set<Parameter> getParameters() {
        return parameters;
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public void setParameters(Object parameters) {
        if (parameters instanceof Set<?>) {
            this.parameters = (Set<Parameter>) parameters;
        } else {
            getParameters().add((Parameter) parameters);
        }
    }
    
    @Override
    public boolean hasParameters() {
        return CollectionHelper.isNotEmpty(getParameters());
    }

}
