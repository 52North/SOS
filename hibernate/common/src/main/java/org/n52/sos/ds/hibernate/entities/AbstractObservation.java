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

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCodespace;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDescription;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasFeatureOfInterestGetter;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasIdentifier;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservablePropertyGetter;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedureGetter;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUnit;

/**
 * Abstract Hibernate Observation entity class. Contains the default
 * getter/setter methods and constants for Criteria creation.
 * 
 * @since 4.0.0
 * 
 */
public abstract class AbstractObservation extends AbstractObservationTime implements HasIdentifier, HasFeatureOfInterestGetter,
        HasObservablePropertyGetter, HasProcedureGetter, HasCodespace, HasUnit, HasDescription {

    private static final long serialVersionUID = -5638600640028433573L;

    private String identifier;

    private Codespace codespace;

    private String description;

    private Unit unit;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public AbstractObservation setIdentifier(final String identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public Codespace getCodespace() {
        return codespace;
    }

    @Override
    public void setCodespace(final Codespace codespace) {
        this.codespace = codespace;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public AbstractObservation setDescription(final String description) {
        this.description = description;
        return this;
    }

    
    @Override
    public Unit getUnit() {
        return unit;
    }

    @Override
    public void setUnit(final Unit unit) {
        this.unit = unit;
    }

    @Override
    public boolean isSetIdentifier() {
        return getIdentifier() != null && !getIdentifier().isEmpty();
    }

    @Override
    public boolean isSetCodespace() {
        return getCodespace() != null && getCodespace().isSetCodespace();
    }

    @Override
    public boolean isSetDescription() {
        return getDescription() != null && !getDescription().isEmpty();
    }

    @Override
    public boolean isSetUnit() {
        return getUnit() != null && getUnit().isSetUnit();
    }
}
