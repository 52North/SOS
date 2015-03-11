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
package org.n52.sos.ds.hibernate.entities.series;

import java.io.Serializable;
import java.util.Date;

import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.*;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.Unit;
/**
 * Hibernate entity for series
 * 
 * @since 4.0.0
 * 
 */
public class Series implements Serializable, HasProcedure, HasObservableProperty, HasFeatureOfInterest, HasDeletedFlag, HasPublishedFlag, HasUnit {

    private static final long serialVersionUID = 7838379468605356753L;
    
    public static String ID = "seriesId";
    
    public static String FIRST_TIME_STAMP = "firstTimeStamp";
    
    public static String LAST_TIME_STAMP = "lastTimeStamp";

    private long seriesId;

    private FeatureOfInterest featureOfInterest;

    private ObservableProperty observableProperty;

    private Procedure procedure;

    private Boolean deleted = false;
    
    private Boolean published = true;

    // the following values are used by the timeseries api
    private Date firstTimeStamp;
    
    private Date lastTimeStamp;
    
    private Double firstNumericValue;
    
    private Double lastNumericValue;
    
    private Unit unit;
    
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
    
    @Override
    public Series setPublished(final boolean published) {
        this.published = published;
        return this;
    }

    @Override
    public boolean getDeleted() {
        return deleted;
    }

    @Override
    public boolean isPublished() {
        return published;
    }

    /**
     * @return the firstTimeStamp
     */
    public Date getFirstTimeStamp() {
        return firstTimeStamp;
    }

    /**
     * @param firstTimeStamp the firstTimeStamp to set
     */
    public void setFirstTimeStamp(Date firstTimeStamp) {
        this.firstTimeStamp = firstTimeStamp;
    }
    
    public boolean isSetFirstTimeStamp() {
        return getFirstTimeStamp() != null;
    }

    /**
     * @return the lastTimeStamp
     */
    public Date getLastTimeStamp() {
        return lastTimeStamp;
    }

    /**
     * @param lastTimeStamp the lastTimeStamp to set
     */
    public void setLastTimeStamp(Date lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }
    
    public boolean isSetLastTimeStamp() {
        return getLastTimeStamp() != null;
    }

    /**
     * @return the firstNumericValue
     */
    public Double getFirstNumericValue() {
        return firstNumericValue;
    }

    /**
     * @param firstNumericValue the firstNumericValue to set
     */
    public void setFirstNumericValue(Double firstNumericValue) {
        this.firstNumericValue = firstNumericValue;
    }
    
    public boolean isSeFirstNumericValue() {
        return getFirstNumericValue() != null;
    }

    /**
     * @return the lastNumericValue
     */
    public Double getLastNumericValue() {
        return lastNumericValue;
    }

    /**
     * @param lastNumericValue the lastNumericValue to set
     */
    public void setLastNumericValue(Double lastNumericValue) {
        this.lastNumericValue = lastNumericValue;
    }
    
    public boolean isSeLastNumericValue() {
        return getLastNumericValue() != null;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

    @Override
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public boolean isSetUnit() {
        return getUnit() != null && getUnit().isSetUnit();
    }

    public boolean isSetFirstLastTime() {
        return isSetFirstTimeStamp() && isSetLastTimeStamp();
    }
}
