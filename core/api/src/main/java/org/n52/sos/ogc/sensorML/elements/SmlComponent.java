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
package org.n52.sos.ogc.sensorML.elements;

import org.n52.sos.ogc.sensorML.AbstractSensorML;

/**
 * SOS internal representation of SensorML component
 * 
 * @since 4.0.0
 */
public class SmlComponent {

    private String name;

    private String title;

    private String href;

    private String role;

    private AbstractSensorML process;

    public SmlComponent() {

    }

    /**
     * constructor
     * 
     * @param name
     *            Component identifier
     */
    public SmlComponent(String name) {
        super();
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setProcess(AbstractSensorML process) {
        this.process = process;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }

    public AbstractSensorML getProcess() {
        return process;
    }

    public boolean isSetProcess() {
        return process != null;
    }

    public boolean isSetName() {
        return name != null && !name.isEmpty();
    }

}
