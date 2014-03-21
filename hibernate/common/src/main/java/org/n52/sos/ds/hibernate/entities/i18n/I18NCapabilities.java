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

import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCodespace;

public class I18NCapabilities implements Serializable, HasCodespace {

    private static final long serialVersionUID = -7622695606037331060L;
    
    private long id;
    
    private Codespace codespace;
    
    private String title;
    
    private String abstrakt;
    
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    @Override
    public Codespace getCodespace() {
        return codespace;
    }

    @Override
    public I18NCapabilities setCodespace(Codespace codespace) {
        this.codespace = codespace;
        return this;
    }

    @Override
    public boolean isSetCodespace() {
        return getCodespace() != null;
    }
    
    /**
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     * @return
     */
    public I18NCapabilities setTitle(String title) {
        this.title = title;
        return this;
    }
    
    /**
     * @return
     */
    public boolean isSetTitle() {
        return getTitle() != null;
    }
    
    /**
     * @return
     */
    public String getAbstract() {
        return abstrakt;
    }

    /**
     * @param abstrakt
     * @return
     */
    public I18NCapabilities setAbstract(String abstrakt) {
        this.abstrakt = abstrakt;
        return this;
    }

    /**
     * @return
     */
    public boolean isSetAbstract() {
        return getAbstract() != null;
    }

}
