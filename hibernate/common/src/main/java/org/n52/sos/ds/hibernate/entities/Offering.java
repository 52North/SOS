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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDisabledFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservationTypes;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasParentChilds;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class Offering extends AbstractIdentifierNameDescriptionEntity
        implements Serializable, HasDisabledFlag, HasParentChilds<Offering>, HasObservationTypes {

    private static final long serialVersionUID = 6512574941388917166L;
    public static final String ID = "offeringId";
    private long offeringId;
    private Boolean disabled = false;
    private Set<Offering> childs = Sets.newHashSet();
    private Set<Offering> parents = Sets.newHashSet();
    private Set<ObservationType> observationTypes = new HashSet<ObservationType>(0);

    public long getOfferingId() {
        return this.offeringId;
    }

    public void setOfferingId(long offeringId) {
        this.offeringId = offeringId;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean isDisabled() {
        return getDisabled();
    }

    @Override
    public boolean getDisabled() {
        return disabled;
    }
    
    @Override
    public Set<Offering> getParents() {
        return parents;
    }

    @Override
    public void setParents(Set<Offering> parents) {
        this.parents = parents;
    }

    @Override
    public Set<Offering> getChilds() {
        return childs;
    }

    @Override
    public void setChilds(Set<Offering> childs) {
        this.childs = childs;
    }

    @Override
    public void addParent(Offering parent) {
        if (parent == null) {
            return;
        }
        if (this.parents == null) {
            this.parents = new HashSet<>();
        }
        this.parents.add(parent);
    }

    @Override
    public void addChild(Offering child) {
        if (child == null) {
            return;
        }
        if (this.childs == null) {
            this.childs = new HashSet<>();
        }
        this.childs.add(child);
    }

    @Override
    public boolean hasParents() {
        return CollectionHelper.isNotEmpty(getParents());
    }

    @Override
    public boolean hasChilds() {
        return CollectionHelper.isNotEmpty(getChilds());
    }
    
    @Override
    public Set<ObservationType> getObservationTypes() {
        return observationTypes;
    }

    @Override
    public void setObservationTypes(final Set<ObservationType> observationTypes) {
        this.observationTypes = observationTypes;
    }
}
