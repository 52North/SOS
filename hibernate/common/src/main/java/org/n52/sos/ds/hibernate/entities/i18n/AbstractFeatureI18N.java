/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.i18n;

import java.io.Serializable;

import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.*;
import org.n52.sos.util.StringHelper;

/**
 * Abstract feature I18N entity
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public abstract class AbstractFeatureI18N implements Serializable, HasCodespace, HasName, HasDescription {

    private static final long serialVersionUID = 6284817322541256323L;

    public static final String OBJECT_ID = "objectId";

    private long id;

    private AbstractIdentifierNameDescriptionEntity objectId;

    private Codespace codespace;

    private String name;

    private String description;

    /**
     * Get the object id
     * 
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the object id
     * 
     * @param id
     *            the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the related object
     * 
     * @return Related object
     */
    public AbstractIdentifierNameDescriptionEntity getObjectId() {
        return this.objectId;
    }

    /**
     * Set the related object
     * 
     * @param objectId
     *            Related object
     */
    public void setObjectId(AbstractIdentifierNameDescriptionEntity objectId) {
        this.objectId = objectId;
    }

    @Override
    public Codespace getCodespace() {
        return codespace;
    }

    @Override
    public AbstractFeatureI18N setCodespace(Codespace codespace) {
        this.codespace = codespace;
        return this;
    }

    @Override
    public boolean isSetCodespace() {
        return getCodespace() != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AbstractFeatureI18N setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean isSetName() {
        return StringHelper.isNotEmpty(getName());
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public AbstractFeatureI18N setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean isSetDescription() {
        return StringHelper.isNotEmpty(getDescription());
    }

}
