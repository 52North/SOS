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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDisabledFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasHiddenChildFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasParentChilds;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class ObservableProperty extends AbstractIdentifierNameDescriptionEntity implements Serializable, HasDisabledFlag, HasHiddenChildFlag,HasParentChilds<ObservableProperty> {

    private static final long serialVersionUID = -4804791207463850138L;
    public static final String ID = "observablePropertyId";
    private long observablePropertyId;
    private Boolean disabled = false;
    private Boolean hiddenChild = false;
    private Set<ObservableProperty> childs = Sets.newHashSet();
    private Set<ObservableProperty> parents = Sets.newHashSet();

    public long getObservablePropertyId() {
        return this.observablePropertyId;
    }

    public void setObservablePropertyId(long observablePropertyId) {
        this.observablePropertyId = observablePropertyId;
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
    public void setHiddenChild(boolean hiddenChild) {
        this.hiddenChild = hiddenChild;
    }

    @Override
    public boolean isHiddenChild() {
        return this.hiddenChild;
    }
    
    @Override
    public Set<ObservableProperty> getParents() {
        return parents;
    }

    @Override
    public void setParents(final Set<ObservableProperty> parents) {
        this.parents = parents;
    }

    @Override
    public Set<ObservableProperty> getChilds() {
        return childs;
    }

    @Override
    public void setChilds(final Set<ObservableProperty> childs) {
        this.childs = childs;
    }

    @Override
    public void addParent(ObservableProperty parent) {
        if (parent == null) {
            return;
        }
        if (parents == null) {
            parents = new HashSet<>();
        }
        parents.add(parent);
    }

    @Override
    public void addChild(ObservableProperty child) {
        if (child == null) {
            return;
        }
        if (childs == null) {
            childs = new HashSet<>();
        }
        childs.add(child);
    }
    
    @Override
    public boolean hasParents() {
        return CollectionHelper.isNotEmpty(getParents());
    }

    @Override
    public boolean hasChilds() {
        return CollectionHelper.isNotEmpty(getChilds());
    }

}
