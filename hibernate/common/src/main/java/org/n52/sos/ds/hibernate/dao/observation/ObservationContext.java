/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
//import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSeriesType;
//import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasWriteableObservationContext;
//import org.n52.sos.ds.hibernate.entities.PhenomenonEntity;
//import org.n52.sos.ds.hibernate.entities.Offering;
//import org.n52.sos.ds.hibernate.entities.ProcedureEntity;
//import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureEntity;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;

/**
 * Class to carry observation identifiers (featureOfInterest,
 * observableProperty, procedure).
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">J&uuml;rrens, Eike Hinderk</a>
 * @since 4.0.0
 *
 */
public class ObservationContext {
    private AbstractFeatureEntity<?> featureOfInterest;
    private PhenomenonEntity observableProperty;
    private ProcedureEntity procedure;
    private OfferingEntity offering;
    private CategoryEntity category;
    private boolean hiddenChild = false;
    private boolean publish = true;
    private FormatEntity observationType;

    /**
     * Indicates that the series of the observation should be published
     */
    public boolean isPublish() {
        return publish;
    }

    public ObservationContext setPublish(boolean publish) {
        this.publish = publish;
        return this;
    }

    public AbstractFeatureEntity<?> getFeatureOfInterest() {
        return featureOfInterest;
    }

    public ObservationContext setFeatureOfInterest(AbstractFeatureEntity<?> featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        return this;
    }

    public PhenomenonEntity getPhenomenon() {
        return observableProperty;
    }

    public ObservationContext setPhenomenon(PhenomenonEntity observableProperty) {
        this.observableProperty = observableProperty;
        return this;
    }

    /**
     * @return the category
     */
    public CategoryEntity getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public ObservationContext setCategory(CategoryEntity category) {
        this.category = category;
        return this;
    }

    /**
     * @return the offering
     */
    public OfferingEntity getOffering() {
        return offering;
    }

    public ObservationContext setOffering(OfferingEntity offering) {
        this.offering = offering;
        return this;
    }

    public ProcedureEntity getProcedure() {
        return procedure;
    }


    /**
     * @param procedure
     *                  the procedure to set
     * @return
     */
    public ObservationContext setProcedure(ProcedureEntity procedure) {
        this.procedure = procedure;
        return this;
    }

    public FormatEntity getObservationType() {
        return observationType;
    }

    public ObservationContext setObservationType(FormatEntity observationType) {
        this.observationType = observationType;
        return this;
    }

    public boolean isSetFeatureOfInterest() {
        return getFeatureOfInterest() != null;
    }

    public boolean isSetPhenomenon() {
        return getPhenomenon() != null;
    }

    public boolean isSetProcedure() {
        return getProcedure() != null;
    }

    public boolean isSetOffering() {
        return getOffering() != null;
    }

    public boolean isSetCategory() {
        return getCategory() != null;
    }

    public boolean isSetObservationType() {
        return getObservationType() != null;
    }

    public void addIdentifierRestrictionsToCritera(Criteria c) {
        addIdentifierRestrictionsToCritera(c, true);
    }

    public void addIdentifierRestrictionsToCritera(Criteria c, boolean includeFeature) {
        if (includeFeature) {
            if (isSetFeatureOfInterest()) {
                c.add(Restrictions
                        .eq(DatasetEntity.PROPERTY_FEATURE,
                            getFeatureOfInterest()));

            }
        } else {
            c.add(Restrictions.isNull(DatasetEntity.PROPERTY_FEATURE));
        }
        if (isSetPhenomenon()) {
            c.add(Restrictions
                    .eq(DatasetEntity.PROPERTY_PHENOMENON,
                        getPhenomenon()));
        }
        if (isSetProcedure()) {
            c.add(Restrictions
                    .eq(DatasetEntity.PROPERTY_PROCEDURE,
                        getProcedure()));
        }
        if (isSetOffering()) {
            c.add(Restrictions
                    .eq(DatasetEntity.PROPERTY_OFFERING,
                        getOffering()));
        }

        if (isSetCategory()) {
            c.add(Restrictions
                    .eq(DatasetEntity.PROPERTY_CATEGORY,
                        getCategory()));
        }
    }

    public void addValuesToSeries(DatasetEntity contextual) {
        if (!contextual.isSetFeature() && isSetFeatureOfInterest()) {
            contextual.setFeature(getFeatureOfInterest());
        }
        if (contextual.getPhenomenon() == null && isSetPhenomenon()) {
            contextual.setPhenomenon(getPhenomenon());
        }
        if (contextual.getProcedure() == null && isSetProcedure()) {
            contextual.setProcedure(getProcedure());
        }
        if (!contextual.isSetOffering() && isSetOffering()) {
            contextual.setOffering(getOffering());
        }
        if (contextual.getCategory() == null && isSetCategory()) {
            contextual.setCategory(getCategory());
        }
        if (!contextual.isSetObservationType() && isSetObservationType()) {
            contextual.setObservationType(getObservationType());
        }
       contextual.setHiddenChild(isHiddenChild());
    }

    public ObservationContext setHiddenChild(boolean hiddenChild) {
        this.hiddenChild = hiddenChild;
        return this;
    }

    public boolean isHiddenChild() {
        return hiddenChild;
    }

}
