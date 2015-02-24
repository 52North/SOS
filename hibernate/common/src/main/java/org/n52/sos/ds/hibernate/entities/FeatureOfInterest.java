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

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCodespace;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCoordinate;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDescriptionXml;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasFeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasGeometry;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasStringOrClobNames;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUrl;
import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 * 
 */
public class FeatureOfInterest extends SpatialEntity implements Serializable, HasFeatureOfInterestType, HasGeometry,
        HasDescriptionXml, HasStringOrClobNames, HasUrl, HasCodespace, HasCoordinate {

    private static final long serialVersionUID = 4142090100433622512L;

    public static final String ID = "featureOfInterestId";

    private long featureOfInterestId;

    private FeatureOfInterestType featureOfInterestType;

    private Codespace codespace;

    private String name;

    private String url;

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
    public Codespace getCodespace() {
        return this.codespace;
    }

    @Override
    public void setCodespace(Codespace codespace) {
        this.codespace = codespace;
    }

    @Override
    public boolean isSetCodespace() {
        return getCodespace() != null && getCodespace().isSetCodespace();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isSetName() {
        return StringHelper.isNotEmpty(name);
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

}
