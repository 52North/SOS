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
package org.n52.sos.ogc.sensorML;

/**
 * Implementation for sml:RulesDefinition
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class RulesDefinition {

    private String description;

    public RulesDefinition() {
    };

    public RulesDefinition(final String description) {
        this.description = description;
    }

    /**
     * @return <tt>true</tt>, if description is set and not empty.
     */
    public boolean isSetDescription() {
        return description != null && !description.isEmpty();
    }

    /**
     * 
     * @param description
     *            a {@link String} holding the
     *            "[t]extual description of the i/o structure" (Source: SensorML
     *            1.0.1).
     * @return the {@link RulesDefinition} object instance
     */
    public RulesDefinition setDescription(final String description) {
        this.description = description;
        return this;
    }

    /**
     * 
     * @return Textual description of the i/o structure (Source: SensorML 1.0.1)
     */
    public String getDescription() {
        return description;
    }

}
