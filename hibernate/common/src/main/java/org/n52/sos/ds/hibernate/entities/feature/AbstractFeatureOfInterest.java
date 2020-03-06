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
package org.n52.sos.ds.hibernate.entities.feature;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCoordinate;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDescriptionXml;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasFeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasGeometry;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasParameters;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasParentChilds;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUrl;
import org.n52.sos.ds.hibernate.entities.SpatialEntity;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractFeatureOfInterest extends SpatialEntity  implements Serializable, HasFeatureOfInterestType, HasGeometry,
HasDescriptionXml, HasUrl, HasCoordinate, HasParentChilds<AbstractFeatureOfInterest>, HasParameters {
    
    private static final long serialVersionUID = -5435922563580498368L;

    public abstract AbstractFeature accept(FeatureVisitor visitor) throws OwsExceptionReport;

    public abstract Geometry accept(GeometryVisitor visitor) throws OwsExceptionReport;
    
    public static final String ID = "featureOfInterestId";
    private long featureOfInterestId;
    private FeatureOfInterestType featureOfInterestType;
    private String url;
    private Set<AbstractFeatureOfInterest> childs = Sets.newHashSet();
    private Set<AbstractFeatureOfInterest> parents = Sets.newHashSet();
    private Set<Parameter> parameters = new HashSet<>(0);

    public long getFeatureOfInterestId() {
        return this.featureOfInterestId;
    }

    public void setFeatureOfInterestId(long featureOfInterestId) {
        this.featureOfInterestId = featureOfInterestId;
    }

    @Override
    public FeatureOfInterestType getFeatureOfInterestType() {
        return this.featureOfInterestType;
    }

    @Override
    public void setFeatureOfInterestType(FeatureOfInterestType featureOfInterestType) {
        this.featureOfInterestType = featureOfInterestType;
    }

    @Override
    public boolean isSetCodespace() {
        return getCodespace() != null && getCodespace().isSetCodespace();
    }

    @Override
    public boolean isSetCodespaceName() {
        return getCodespaceName() != null && getCodespaceName().isSetCodespace();
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public boolean isSetUrl() {
        return !Strings.isNullOrEmpty(getUrl());
    }

    @Override
    public boolean isSetDescription() {
        return StringHelper.isNotEmpty(getDescription());
    }
    
    @Override
    public Set<AbstractFeatureOfInterest> getParents() {
        return parents;
    }

    @Override
    public void setParents(final Set<AbstractFeatureOfInterest> parents) {
        this.parents = parents;
    }

    @Override
    public Set<AbstractFeatureOfInterest> getChilds() {
        return childs;
    }

    @Override
    public void setChilds(final Set<AbstractFeatureOfInterest> childs) {
        this.childs = childs;
    }

    @Override
    public void addParent(AbstractFeatureOfInterest parent) {
        if (parent == null) {
            return;
        }
        if (this.parents == null) {
            this.parents = new HashSet<>();
        }
        this.parents.add(parent);
    }

    @Override
    public void addChild(AbstractFeatureOfInterest child) {
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
    public Set<Parameter> getParameters() {
        return parameters;
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public void setParameters(Object parameters) {
        if (parameters instanceof Set<?>) {
            this.parameters = (Set<Parameter>) parameters;
        } else {
            getParameters().add((Parameter) parameters);
        }
    }
    
    @Override
    public boolean hasParameters() {
        return CollectionHelper.isNotEmpty(getParameters());
    }

}
