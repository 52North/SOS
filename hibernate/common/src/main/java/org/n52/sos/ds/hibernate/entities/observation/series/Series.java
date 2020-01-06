/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.observation.series;

import java.util.Date;
import java.util.List;
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.Category;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDeletedFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasHiddenChildFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasOffering;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasPublishedFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSeriesType;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUnit;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasWriteableObservationContext;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.util.Constants;

import com.google.common.base.Strings;

/**
 * Hibernate entity for series
 *
 * @since 4.0.0
 *
 */
public class Series extends AbstractIdentifierNameDescriptionEntity
        implements HasWriteableObservationContext,
                   HasDeletedFlag,
                   HasHiddenChildFlag,
                   HasUnit,
                   HasPublishedFlag,
                   HasSeriesType,
                   HasOffering {

    private static final long serialVersionUID = 7838379468605356753L;

    public static String ID = "seriesId";
    public static String FIRST_TIME_STAMP = "firstTimeStamp";
    public static String LAST_TIME_STAMP = "lastTimeStamp";
    public static String CATEGORY = "category";

    public static final String ALIAS = "s";

    public static final String ALIAS_DOT = ALIAS + Constants.DOT_STRING;

    private long seriesId;
    private AbstractFeatureOfInterest featureOfInterest;
    private ObservableProperty observableProperty;
    private Procedure procedure;
    private Offering offering;
    private Category category;

    private Boolean deleted = false;
    private Boolean published = true;
    // the following values are used by the rest api
    private Date firstTimeStamp;
    private Date lastTimeStamp;
    private Double firstNumericValue;
    private Double lastNumericValue;
    private Unit unit;
    private boolean hiddenChild;
    private String seriesType;
    private List<Series> referenceValues;
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
     *                 Series id
     */
    public void setSeriesId(final long seriesId) {
        this.seriesId = seriesId;
    }

    @Override
    public AbstractFeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    @Override
    public void setFeatureOfInterest(final AbstractFeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    @Override
    public ObservableProperty getObservableProperty() {
        return observableProperty;
    }

    @Override
    public void setObservableProperty(
            final ObservableProperty observableProperty) {
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
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Category getCategory() {
        return category;
    }

    @Override
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
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

    /**
     * @deprecated see {@link #isSetFirstNumericValue()}
     */
    @Deprecated
    public boolean isSeFirstNumericValue() {
        return getFirstNumericValue() != null;
    }

    public boolean isSetFirstNumericValue() {
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

    /**
     * @deprecated see {@link #isSetLastNumericValue()}
     */
    @Deprecated
    public boolean isSeLastNumericValue() {
        return getLastNumericValue() != null;
    }

    public boolean isSetLastNumericValue() {
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

    @Override
    public boolean isSetUnit() {
        return getUnit() != null && getUnit().isSetUnit();
    }

    @Override
    public void setHiddenChild(boolean hiddenChild) {
        this.hiddenChild = hiddenChild;
    }

    @Override
    public boolean isHiddenChild() {
        return hiddenChild;
    }

    public boolean isSetFirstLastTime() {
        return isSetFirstTimeStamp() && isSetLastTimeStamp();
    }

    @Override
    public Offering getOffering() {
        return offering;
    }

    @Override
    public void setOffering(final Offering offering) {
        this.offering = offering;
    }

    @Override
    public boolean isSetOffering() {
        return getOffering() != null;
    }

    public boolean hasSameObservationIdentifier(Series s) {
        return getFeatureOfInterest().equals(s.getFeatureOfInterest()) && getProcedure().equals(s.getProcedure())
                && getObservableProperty().equals(s.getObservableProperty());
    }

    @Override
    public String getSeriesType() {
        return seriesType;
    }

    @Override
    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
    }

    @Override
    public boolean isSetSeriesType() {
        return !Strings.isNullOrEmpty(getSeriesType());
    }

    public List<Series> getReferenceValues() {
        return referenceValues;
    }

    public void setReferenceValues(List<Series> referenceValues) {
        this.referenceValues = referenceValues;
    }
}
