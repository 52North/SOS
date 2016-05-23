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
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCodespaceName;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDescription;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasIdentifier;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasName;
import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 *
 */
public abstract class AbstractIdentifierNameDescriptionEntity
        implements Serializable,
                   HasIdentifier,
                   HasCodespace,
                   HasName,
                   HasCodespaceName,
                   HasDescription {

    private static final long serialVersionUID = -5784528065957127968L;
    private String identifier;
    private Codespace codespace;
    private String name;
    private Codespace codespaceName;
    private String decription;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public AbstractIdentifierNameDescriptionEntity setIdentifier(
            final String identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public boolean isSetIdentifier() {
        return StringHelper.isNotEmpty(getIdentifier());
    }

    @Override
    public Codespace getCodespace() {
        return this.codespace;
    }

    @Override
    public AbstractIdentifierNameDescriptionEntity setCodespace(
            Codespace codespace) {
        this.codespace = codespace;
        return this;
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
    public AbstractIdentifierNameDescriptionEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean isSetName() {
        return StringHelper.isNotEmpty(name);
    }

    @Override
    public Codespace getCodespaceName() {
        return codespaceName;
    }

    @Override
    public AbstractIdentifierNameDescriptionEntity setCodespaceName(
            Codespace codespaceName) {
        this.codespaceName = codespaceName;
        return this;
    }

    @Override
    public boolean isSetCodespaceName() {
        return getCodespaceName() != null && getCodespaceName().isSetCodespace();
    }

    @Override
    public String getDescription() {
        return decription;
    }

    @Override
    public AbstractIdentifierNameDescriptionEntity setDescription(
            String description) {
        this.decription = description;
        return this;
    }

    @Override
    public boolean isSetDescription() {
        return StringHelper.isNotEmpty(getDescription());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [identifier=" +
               getIdentifier() + "]";
    }

}
