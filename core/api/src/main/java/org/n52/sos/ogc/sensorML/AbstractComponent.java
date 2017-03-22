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
package org.n52.sos.ogc.sensorML;

import org.n52.sos.ogc.sensorML.elements.SmlLocation;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;

/**
 * @since 4.0.0
 * 
 */
public class AbstractComponent extends AbstractProcess implements HasPosition<AbstractComponent> {

    private static final long serialVersionUID = -7668360974212650356L;
    private SmlPosition position;

    private SmlLocation location;

    public SmlPosition getPosition() {
        return position;
    }

    public AbstractComponent setPosition(final SmlPosition position) {
        this.position = position;
        return this;
    }

    public boolean isSetPosition() {
        return position != null;
    }

    public SmlLocation getLocation() {
        return location;
    }

    public AbstractComponent setLocation(final SmlLocation location) {
        this.location = location;
        return this;
    }

    public boolean isSetLocation() {
        return location != null;
    }
    
    @Override
    public String getDescriptionFormat() {
        return SensorMLConstants.NS_SML;
    }
    
    @Override
    public String getDefaultElementEncoding() {
        return SensorMLConstants.NS_SML;
    }
}
