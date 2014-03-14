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
package org.n52.sos.ds.hibernate.entities.series;

import java.io.Serializable;

import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservableProperty;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedure;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDeletedFlag;

/**
 * Hibernate entity for series
 * 
 * @since 4.0.0
 * 
 */
public class Series implements Serializable, HasProcedure, HasObservableProperty, HasFeatureOfInterest, HasDeletedFlag {

    private static final long serialVersionUID = 7838379468605356753L;

    public static String ID = "seriesId";

    private long seriesId;

    private FeatureOfInterest featureOfInterest;

    private ObservableProperty observableProperty;

    private Procedure procedure;

    private Boolean deleted = false;

    /**
     * Get series id
     * 
     * @return Series id
     */
    public long getSeriesId() {
        return seriesId;
    }

    /**
     * Set series id
     * 
     * @param seriesId
     *            Series id
     */
    public void setSeriesId(final long seriesId) {
        this.seriesId = seriesId;
    }

    @Override
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    @Override
    public void setFeatureOfInterest(final FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    @Override
    public ObservableProperty getObservableProperty() {
        return observableProperty;
    }

    @Override
    public void setObservableProperty(final ObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
    }

    @Override
    public Procedure getProcedure() {
        return procedure;
    }

    @Override
    public void setProcedure(final Procedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public Series setDeleted(final boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }
}
