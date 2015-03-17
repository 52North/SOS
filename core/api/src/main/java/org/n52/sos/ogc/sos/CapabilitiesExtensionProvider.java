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
package org.n52.sos.ogc.sos;

/**
 * Interface for CapabilitiesExtensionProvider. Implementations of this
 * interface are loaded by the {@link CapabilitiesExtensionRepository}.
 * 
 * @since 4.0.0
 * 
 */
public interface CapabilitiesExtensionProvider {

    /**
     * Get the {@link CapabilitiesExtensionKey} for this provider
     * 
     * @return CapabilitiesExtensionKey
     */
    CapabilitiesExtensionKey getCapabilitiesExtensionKey();

    /**
     * Get the {@link CapabilitiesExtension} the provider provides.
     * 
     * @return provided CapabilitiesExtension
     */
    CapabilitiesExtension getExtension();

    /**
     * Does this {@link CapabilitiesExtension} related to a specific service
     * operation
     * 
     * @return <code>true</code>, if service relates to a specific service
     *         operation
     */
    boolean hasRelatedOperation();

    /**
     * Get the specific service operation name this
     * {@link CapabilitiesExtension} relates to.
     * 
     * @return Related service operation name
     */
    String getRelatedOperation();
}
