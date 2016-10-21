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
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSeriesType;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasWriteableObservationContext;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;

import com.google.common.base.Strings;

/**
 * Class to carry observation identifiers (featureOfInterest,
 * observableProperty, procedure).
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 *
 */
public class ObservationContext {
    private AbstractFeatureOfInterest featureOfInterest;
    private ObservableProperty observableProperty;
    private Procedure procedure;
    private Offering offering;
    private String seriesType;
    private boolean hiddenChild = false; 

    /**
     * @return the featureOfInterest
     */
    public AbstractFeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * @param featureOfInterest
     *                          the featureOfInterest to set
     */
    public void setFeatureOfInterest(AbstractFeatureOfInterest featureOfInterest) {
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
    
    /**
     * @return the offering
     */
    public Offering getOffering() {
        return offering;
    }

    /**
     * @param offering the offering to set
     */
    public void setOffering(Offering offering) {
        this.offering = offering;
    }
    
    public boolean isSetOffering() {
        return getOffering() != null;
    }

    public void addIdentifierRestrictionsToCritera(Criteria c) {
        if (isSetFeatureOfInterest()) {
            c.add(Restrictions
                    .eq(HasWriteableObservationContext.FEATURE_OF_INTEREST,
                        getFeatureOfInterest()));
        }
        if (isSetObservableProperty()) {
            c.add(Restrictions
                    .eq(HasWriteableObservationContext.OBSERVABLE_PROPERTY,
                        getObservableProperty()));
        }
        if (isSetProcedure()) {
            c.add(Restrictions
                    .eq(HasWriteableObservationContext.PROCEDURE,
                        getProcedure()));
        }
        if (isSetOffering()) {
            c.add(Restrictions.eq(HasWriteableObservationContext.OFFERING, offering));
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
        if (isSetOffering()) {
            contextual.setOffering(getOffering());
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
    
    public void setHiddenChild(boolean hiddenChild) {
        this.hiddenChild = hiddenChild;
    }

    public boolean isHiddenChild() {
        return this.hiddenChild;
    }

}
