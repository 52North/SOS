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

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDeletedFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDisabledFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasHiddenChildFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservableProperty;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservationType;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasOffering;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedure;

/**
 * @since 4.0.0
 * 
 */
public class ObservationConstellation implements Serializable, HasProcedure, HasObservableProperty, HasOffering,
        HasObservationType, HasHiddenChildFlag, HasDeletedFlag, HasDisabledFlag {

    public static final String ID = "observationConstellationId";

    private static final long serialVersionUID = -3890149740562709928L;

    private long observationConstellationId;

    private ObservableProperty observableProperty;

    private Procedure procedure;

    private ObservationType observationType;

    private Offering offering;

    private Boolean deleted = false;
    
    private Boolean disabled = false;

    private Boolean hiddenChild = false;

    public ObservationConstellation() {
    }

    public long getObservationConstellationId() {
        return observationConstellationId;
    }

    public void setObservationConstellationId(final long observationConstellationId) {
        this.observationConstellationId = observationConstellationId;
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
    public ObservationType getObservationType() {
        return observationType;
    }

    @Override
    public void setObservationType(final ObservationType observationType) {
        this.observationType = observationType;
    }

    @Override
    public Offering getOffering() {
        return offering;
    }

    @Override
    public void setOffering(final Offering offering) {
        this.offering = offering;
    }

    public boolean getDeleted() {
        return deleted;
    }

    @Override
    public ObservationConstellation setDeleted(final boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }
    
    @Override
    public HasDisabledFlag setDisabled(final boolean  disabled) {
        this.disabled = disabled;
        return this;
    }

    @Override
    public boolean getDisabled() {
        return disabled;
    }

    @Override
    public boolean isDisabled() {
        return getDisabled();
    }

    @Override
    public ObservationConstellation setHiddenChild(final boolean hiddenChild) {
        this.hiddenChild = hiddenChild;
        return this;
    }

    public boolean getHiddenChild() {
        return hiddenChild;
    }

    @Override
    public boolean isHiddenChild() {
        return hiddenChild;
    }

    @Override
    public String toString() {
        return String
                .format("ObservationConstellation [observationConstellationId=%s, observableProperty=%s, procedure=%s, observationType=%s, offering=%s, deleted=%s, hiddenChild=%s]",
                        observationConstellationId, observableProperty, procedure, observationType, offering, deleted,
                        hiddenChild);
    }

    public boolean isSetObservationType() {
        return getObservationType() != null && getObservationType().isSetObservationType();
    }
}
