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
package org.n52.sos.ogc.gml;

import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;
import org.n52.sos.w3c.xlink.W3CHrefAttribute;

public class AbstractReferenceType {

    /**
     * Href
     */
    private W3CHrefAttribute href;

    /**
     * Title
     */
    private String title;

    /**
     * Role
     */
    private String role;

    /**
     * Get href
     * 
     * @return Href
     */
    public String getHref() {
    	if (href != null) {
    		return href.getHref();
    	}
        return null;
    }

    /**
     * Get title
     * 
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get role
     * 
     * @return Role
     */
    public String getRole() {
        return role;
    }

    /**
     * Set href
     * 
     * @param href
     *            Href to set
     */
    public void setHref(String href) {
        this.href = new W3CHrefAttribute(href);
    }

    /**
     * Set title
     * 
     * @param title
     *            Title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set role
     * 
     * @param role
     *            Role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Check whether href is set
     * 
     * @return <code>true</code>, if href is set
     */
    public boolean isSetHref() {
        return StringHelper.isNotEmpty(getHref());
    }

    /**
     * Check whether title is set
     * 
     * @return <code>true</code>, if title is set
     */
    public boolean isSetTitle() {
        return StringHelper.isNotEmpty(title);
    }

    /**
     * Check whether role is set
     * 
     * @return <code>true</code>, if role is set
     */
    public boolean isSetRole() {
        return StringHelper.isNotEmpty(role);
    }

    /**
     * Check whether href, title, and role are set
     * 
     * @return <code>true</code>, if href, title, and role are set
     */
    public boolean hasValues() {
        return isSetHref() && isSetRole() && isSetTitle();
    }

    /**
     * Get title from href.<br>
     * Cuts href: <br>
     * - starts with 'http': cuts string at last {@link Constants#SLASH_CHAR}<br>
     * - starts with 'urn': cuts string at last {@link Constants#COLON_CHAR}<br>
     * - contains {@link Constants#NUMBER_SIGN_STRING}: cuts string at last
     * {@link Constants#NUMBER_SIGN_CHAR}<br>
     * 
     * @return Title from href
     */
    public String getTitleFromHref() {
        String title = getHref();
        if (title.startsWith("http")) {
            title = title.substring(title.lastIndexOf(Constants.SLASH_CHAR) + 1, title.length());
        } else if (title.startsWith("urn")) {
            title = title.substring(title.lastIndexOf(Constants.COLON_CHAR) + 1, title.length());
        }
        if (title.contains(Constants.NUMBER_SIGN_STRING)) {
            title = title.substring(title.lastIndexOf(Constants.NUMBER_SIGN_CHAR) + 1, title.length());
        }
        return title;
    }

    @Override
    public String toString() {
        return String.format("AbstractReferenceType [title=%s, role=%s, href=%s]", getTitle(), getRole(), getHref());
    }
}
