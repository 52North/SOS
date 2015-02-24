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

import java.util.Set;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasParentChilds;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * 
 * @since 4.0.0
 */
public class TFeatureOfInterest extends FeatureOfInterest implements
        HasParentChilds<FeatureOfInterest, TFeatureOfInterest> {
    private static final long serialVersionUID = -880472749711995015L;

    private Set<FeatureOfInterest> childs = Sets.newHashSet();

    private Set<FeatureOfInterest> parents = Sets.newHashSet();

    public TFeatureOfInterest() {
        super();
    }

    @Override
    public Set<FeatureOfInterest> getParents() {
        return parents;
    }

    @Override
    public TFeatureOfInterest setParents(final Set<FeatureOfInterest> parents) {
        this.parents = parents;
        return this;
    }

    @Override
    public Set<FeatureOfInterest> getChilds() {
        return childs;
    }

    @Override
    public TFeatureOfInterest setChilds(final Set<FeatureOfInterest> childs) {
        this.childs = childs;
        return this;
    }
}