/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.gml;

/**
 * Class represents a GML conform MetaDataProperty element
 * @since 4.0.0
 * 
 */
public class GmlMetaDataProperty {

    /**
     * Title
     */
    private String title;

    /**
     * Role
     */
    private String role;

    /**
     * Href
     */
    private String href;

    /**
     * Set title
     * @param title Title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set role
     * @param role Role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Set href
     * @param href Href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Get title
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get role
     * @return Role
     */
    public String getRole() {
        return role;
    }

    /**
     * Get href
     * @return Href
     */
    public String getHref() {
        return href;
    }
    
    @Override
    public String toString() {
        return String.format("GmlMetaDataProperty [title=%s, role=%s, href=%s]", getTitle(), getRole(), getHref());
    }
}
