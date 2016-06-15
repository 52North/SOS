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
package org.n52.sos.ds.hibernate.dao.observation;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasWriteableObservationContext;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSeriesType;

import com.google.common.base.Strings;

import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Procedure;

/**
 * Class to carry observation identifiers (featureOfInterest,
 * observableProperty, procedure).
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 *
 */
public class ObservationContext {
    private FeatureOfInterest featureOfInterest;
    private ObservableProperty observableProperty;
    private Procedure procedure;
    private String seriesType;

    /**
     * @return the featureOfInterest
     */
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * @param featureOfInterest
     *                          the featureOfInterest to set
     */
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * @return the observableProperty
     */
    public ObservableProperty getObservableProperty() {
        return observableProperty;
    }

    /**
     * @param observableProperty
     *                           the observableProperty to set
     */
    public void setObservableProperty(ObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
    }

    /**
     * @return the procedure
     */
    public Procedure getProcedure() {
        return procedure;
    }

    /**
     * @param procedure
     *                  the procedure to set
     */
    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public boolean isSetFeatureOfInterest() {
        return getFeatureOfInterest() != null;
    }

    public boolean isSetObservableProperty() {
        return getObservableProperty() != null;
    }

    public boolean isSetProcedure() {
        return getProcedure() != null;
    }

    public void addIdentifierRestrictionsToCritera(Criteria criteria) {
        if (isSetFeatureOfInterest()) {
            criteria.add(Restrictions
                    .eq(HasWriteableObservationContext.FEATURE_OF_INTEREST,
                        getFeatureOfInterest()));
        }
        if (isSetObservableProperty()) {
            criteria.add(Restrictions
                    .eq(HasWriteableObservationContext.OBSERVABLE_PROPERTY,
                        getObservableProperty()));
        }
        if (isSetProcedure()) {
            criteria.add(Restrictions
                    .eq(HasWriteableObservationContext.PROCEDURE,
                        getProcedure()));
        }
    }

    public void addValuesToSeries(HasWriteableObservationContext contextual) {
        if (isSetFeatureOfInterest()) {
            contextual.setFeatureOfInterest(getFeatureOfInterest());
        }
        if (isSetObservableProperty()) {
            contextual.setObservableProperty(getObservableProperty());
        }
        if (isSetProcedure()) {
            contextual.setProcedure(getProcedure());
        }
        if (contextual instanceof HasSeriesType && isSetSeriesType()) {
            ((HasSeriesType)contextual).setSeriesType(getSeriesType());
        }
    }

    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
    }
    
    public String getSeriesType() {
        return this.seriesType;
    }
    
    public boolean isSetSeriesType() {
        return !Strings.isNullOrEmpty(getSeriesType());
    }

}
