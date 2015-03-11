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

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasIdentifier;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservableProperty;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasOffering;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedure;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasResultEncoding;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasResultStructure;
import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 * 
 */
public class ResultTemplate implements Serializable, HasIdentifier, HasProcedure, HasObservableProperty, HasOffering,
        HasFeatureOfInterest, HasResultStructure, HasResultEncoding {

    private static final long serialVersionUID = -8847952458819368733L;

    public static final String ID = "resultTemplateId";

    private long resultTemplateId;

    private Offering offering;

    private ObservableProperty observableProperty;

    private Procedure procedure;

    private FeatureOfInterest featureOfInterest;

    private String identifier;

    private String resultStructure;

    private String resultEncoding;

    public ResultTemplate() {
    }

    public long getResultTemplateId() {
        return this.resultTemplateId;
    }

    public void setResultTemplateId(long resultTemplateId) {
        this.resultTemplateId = resultTemplateId;
    }

    @Override
    public Offering getOffering() {
        return this.offering;
    }

    @Override
    public void setOffering(Offering offering) {
        this.offering = offering;
    }

    @Override
    public ObservableProperty getObservableProperty() {
        return this.observableProperty;
    }

    @Override
    public void setObservableProperty(ObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
    }

    @Override
    public Procedure getProcedure() {
        return this.procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public FeatureOfInterest getFeatureOfInterest() {
        return this.featureOfInterest;
    }

    @Override
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    public ResultTemplate setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }
    
    @Override
    public boolean isSetIdentifier() {
        return StringHelper.isNotEmpty(getIdentifier());
    }

    @Override
    public String getResultStructure() {
        return resultStructure;
    }

    public boolean isSetResultStructure() {
        return StringHelper.isNotEmpty(resultStructure);
    }

    @Override
    public void setResultStructure(String resultStructure) {
        this.resultStructure = resultStructure;
    }

    @Override
    public String getResultEncoding() {
        return this.resultEncoding;
    }

    @Override
    public boolean isSetResultEncoding() {
        return StringHelper.isNotEmpty(resultEncoding);
    }

    @Override
    public void setResultEncoding(String resultEncoding) {
        this.resultEncoding = resultEncoding;
    }

}
