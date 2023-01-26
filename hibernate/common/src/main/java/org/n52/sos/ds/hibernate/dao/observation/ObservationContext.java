/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.VerticalMetadataEntity;
import org.n52.series.db.beans.dataset.DatasetType;
import org.n52.series.db.beans.dataset.ValueType;
import org.n52.series.db.beans.sampling.SamplingEntity;
import org.n52.series.db.beans.sampling.SamplingProfileDatasetEntity;
import org.n52.sos.ds.hibernate.util.HibernateHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Class to carry observation identifiers (featureOfInterest,
 * observableProperty, procedure).
 *
 * @author Carsten Hollmann
 * @author <a href="mailto:e.h.juerrens@52north.org">J&uuml;rrens, Eike Hinderk</a>
 * @since 4.0.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ObservationContext {
    private AbstractFeatureEntity<?> featureOfInterest;
    private PhenomenonEntity observableProperty;
    private ProcedureEntity procedure;
    private OfferingEntity offering;
    private CategoryEntity category;
    private PlatformEntity platform;
    private boolean hiddenChild;
    private boolean publish = true;
    private FormatEntity observationType;
    private UnitEntity unit;
    private VerticalMetadataEntity vertical;
    private boolean mobile;
    private boolean insitu = true;
    private ValueType valueType;
    private boolean includeCategory;

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
     * @param category
     *            the category to set
     */
    public ObservationContext setCategory(CategoryEntity category) {
        return setCategory(category, false);
    }

    /**
     * @param category
     *            the category to set
     */
    public ObservationContext setCategory(CategoryEntity category, boolean include) {
        this.category = category;
        this.includeCategory = include;
        return this;
    }

    public PlatformEntity getPlatform() {
        return platform;
    }

    public ObservationContext setPlatform(PlatformEntity platform) {
        this.platform = platform;
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
     *            the procedure to set
     * @return this {@link ObservationContext}
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

    public UnitEntity getUnit() {
        return unit;
    }

    public ObservationContext setUnit(UnitEntity unit) {
        this.unit = unit;
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

    public boolean isSetPlatform() {
        return getPlatform() != null;
    }

    public boolean isSetObservationType() {
        return getObservationType() != null;
    }

    public boolean isSetUnit() {
        return getUnit() != null;
    }

    public void addIdentifierRestrictionsToCritera(Criteria c) {
        addIdentifierRestrictionsToCritera(c, true, true);
    }

    public void addIdentifierRestrictionsToCritera(Criteria c, boolean includeFeatureAndPlatform,
            boolean includeCategory) {
        if (includeFeatureAndPlatform) {
            if (isSetFeatureOfInterest()) {
                c.add(Restrictions.eqOrIsNull(DatasetEntity.PROPERTY_FEATURE, getFeatureOfInterest()));

            }
            if (isSetPlatform()) {
                c.add(Restrictions.eq(DatasetEntity.PROPERTY_PLATFORM, getPlatform()));
            }
        } else {
            c.add(Restrictions.isNull(DatasetEntity.PROPERTY_FEATURE));
            c.add(Restrictions.isNull(DatasetEntity.PROPERTY_PLATFORM));
        }
        if (isSetPhenomenon()) {
            c.add(Restrictions.eq(DatasetEntity.PROPERTY_PHENOMENON, getPhenomenon()));
        }
        if (isSetProcedure()) {
            c.add(Restrictions.eq(DatasetEntity.PROPERTY_PROCEDURE, getProcedure()));
        }
        if (isSetOffering()) {
            c.add(Restrictions.eq(DatasetEntity.PROPERTY_OFFERING, getOffering()));
        }

        if (includeCategory && isSetCategory()) {
            c.add(Restrictions.eq(DatasetEntity.PROPERTY_CATEGORY, getCategory()));
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
        if (contextual.getPlatform() == null && isSetPlatform()) {
            contextual.setPlatform(getPlatform());
        }
        if (!contextual.isSetOMObservationType() && isSetObservationType()) {
            contextual.setOmObservationType(getObservationType());
        }
        if (!contextual.isSetUnit() && isSetUnit()) {
            contextual.setUnit(getUnit());
        }
        if (!contextual.hasVerticalMetadata() && isSetVerticalMetadata()) {
            contextual.setVerticalMetadata(getVertical());
        }
        contextual.setHidden(isHiddenChild());
        contextual.setMobile(isMobile());
        contextual.setInsitu(isInsitu());
        if (DatasetType.trajectory.equals(contextual.getDatasetType())
                && ValueType.not_initialized.equals(contextual.getValueType())) {
            contextual.setValueType(getValueType());
        }
        if (HibernateHelper.isEntitySupported(SamplingEntity.class)) {
            contextual.setSamplingProfile(new SamplingProfileDatasetEntity());
        }
    }

    private boolean isSetVerticalMetadata() {
        return getVertical() != null;
    }

    public ObservationContext setHiddenChild(boolean hiddenChild) {
        this.hiddenChild = hiddenChild;
        return this;
    }

    public boolean isHiddenChild() {
        return hiddenChild;
    }

    public boolean isMobile() {
        return mobile;
    }

    public ObservationContext setMobile(boolean mobile) {
        this.mobile = mobile;
        return this;
    }

    public boolean isInsitu() {
        return insitu;
    }

    public ObservationContext setInsitu(boolean insitu) {
        this.insitu = insitu;
        return this;
    }

    public VerticalMetadataEntity getVertical() {
        return vertical;
    }

    public ObservationContext setVertical(VerticalMetadataEntity vertical) {
        this.vertical = vertical;
        return this;
    }

    public boolean isSetVertical() {
        return getVertical() != null;
    }

    public ObservationContext setValueType(ValueType valueType) {
        this.valueType = valueType;
        return this;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public boolean isSetValueType() {
        return getValueType() != null;
    }

    /**
     * @return the includeCategory
     */
    public boolean isIncludeCategory() {
        return includeCategory;
    }

    /**
     * @param includeCategory the includeCategory to set
     */
    public void setIncludeCategory(boolean includeCategory) {
        this.includeCategory = includeCategory;
    }

    public ObservationContext copy(ObservationContext context) {
        setCategory(context.getCategory());
        setFeatureOfInterest(context.getFeatureOfInterest());
        setHiddenChild(context.isHiddenChild());
        setIncludeCategory(context.isIncludeCategory());
        setInsitu(context.isInsitu());
        setMobile(context.isMobile());
        setObservationType(context.getObservationType());
        setOffering(context.getOffering());
        setPhenomenon(context.getPhenomenon());
        setPlatform(context.getPlatform());
        setProcedure(context.getProcedure());
        setPublish(context.isPublish());
        setUnit(context.getUnit());
        setValueType(context.getValueType());
        setVertical(context.getVertical());
        return this;
    }
}
