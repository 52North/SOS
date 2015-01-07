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
package org.n52.sos.config;

import org.n52.sos.exception.NoSuchOfferingException;
import org.n52.sos.exception.NoSuchExtensionException;
import java.util.List;
import java.util.Map;

import org.n52.sos.ogc.ows.OfferingExtension;
import org.n52.sos.ogc.ows.StaticCapabilities;
import org.n52.sos.ogc.ows.StringBasedCapabilitiesExtension;

public interface CapabilitiesExtensionManager {

    Map<String, List<OfferingExtension>> getOfferingExtensions();
    Map<String, List<OfferingExtension>> getActiveOfferingExtensions();
    void saveOfferingExtension(String offering, String identifier, String value) throws NoSuchOfferingException;
    void disableOfferingExtension(String offering, String identifier, boolean disabled) throws NoSuchExtensionException,
                                                                                               NoSuchOfferingException;

    void deleteOfferingExtension(String offering, String identifier) throws NoSuchOfferingException,
                                                                            NoSuchExtensionException;
    Map<String, StringBasedCapabilitiesExtension> getActiveCapabilitiesExtensions();
    Map<String, StringBasedCapabilitiesExtension> getAllCapabilitiesExtensions();
    void saveCapabilitiesExtension(String identifier, String value);
    void disableCapabilitiesExtension(String identifier, boolean disabled) throws NoSuchExtensionException;
    void deleteCapabiltiesExtension(String identfier) throws NoSuchExtensionException;
    void setActiveStaticCapabilities(String identifier) throws NoSuchExtensionException;

    /**
     * @return the identifier
     */
    String getActiveStaticCapabilities();

    /**
     * @return the document
     */
    String getActiveStaticCapabilitiesDocument();
    boolean isStaticCapabilitiesActive();
    Map<String, StaticCapabilities> getStaticCapabilities();
    StaticCapabilities getStaticCapabilities(String id);
    void saveStaticCapabilities(String identifier, String document);
    void deleteStaticCapabilities(String identifier) throws NoSuchExtensionException;
}
