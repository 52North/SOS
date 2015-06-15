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
package org.n52.sos.w3c.xlink;

import org.n52.sos.util.StringHelper;
import org.n52.sos.w3c.W3CConstants;

public abstract class SimpleAttrs {
    
    private String href;
    
    private String role;
    
    private String arcrole;
    
    private String title;
    
    private W3CConstants.ShowType show;
    
    private W3CConstants.ActuateType actuate;

    /**
     * @return the type
     */
    public W3CConstants.TypeType getType() {
        return W3CConstants.TypeType.simple;
    }

    /**
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public SimpleAttrs setHref(String href) {
        this.href = href;
        return this;
    }
    
    public boolean isSetHref() {
        return StringHelper.isNotEmpty(getHref());
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public SimpleAttrs setRole(String role) {
        this.role = role;
        return this;
    }
    
    public boolean isSetRole() {
        return StringHelper.isNotEmpty(getRole());
    }

    /**
     * @return the arcrole
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * @param arcrole the arcrole to set
     */
    public SimpleAttrs setArcrole(String arcrole) {
        this.arcrole = arcrole;
        return this;
    }
    
    public boolean isSetArcrole() {
        return StringHelper.isNotEmpty(getArcrole());
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public SimpleAttrs setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public boolean isSetTitle() {
        return StringHelper.isNotEmpty(getTitle());
    }

    /**
     * @return the show
     */
    public W3CConstants.ShowType getShow() {
        return show;
    }

    /**
     * @param show the show to set
     */
    public SimpleAttrs setShow(W3CConstants.ShowType show) {
        this.show = show;
        return this;
    }
    
    public boolean isSetShow() {
        return getShow() != null;
    }

    /**
     * @return the actuate
     */
    public W3CConstants.ActuateType getActuate() {
        return actuate;
    }

    /**
     * @param actuate the actuate to set
     */
    public SimpleAttrs setActuate(W3CConstants.ActuateType actuate) {
        this.actuate = actuate;
        return this;
    }
    
    public boolean isSetActuate() {
        return getActuate() != null;
    }

}
