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
package org.n52.sos.inspire.capabilities;

import java.util.Collection;

import org.n52.sos.inspire.InspireLanguageISO6392B;
import org.n52.sos.inspire.InspireResourceLocator;
import org.n52.sos.inspire.InspireSupportedCRS;
import org.n52.sos.inspire.InspireSupportedLanguages;
import org.n52.sos.inspire.InspireUniqueResourceIdentifier;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesMetadataURL;

/**
 * Service internal object to represent the minimal INSPIRE DLS ExtendedCapabilities
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class MinimalInspireExtendedCapabilities extends InspireExtendedCapabilitiesDLS implements
        InspireExtendedCapabilitiesMetadataURL {

    /* MetadataUrl 1..1 */
    private InspireResourceLocator metadataUrl;

    /**
     * constructor
     * 
     * @param metadataUrl
     *            Metadata URL to set
     * @param supportedLanguages
     *            Supported languages to set
     * @param responseLanguage
     *            Response language to set
     * @param spatialDataSetIdentifier
     *            Spatial dataset identifier to set
     *  @param supportedCRS
     *            Supported CRSes to set
     */
    public MinimalInspireExtendedCapabilities(InspireResourceLocator metadataUrl,
            InspireSupportedLanguages supportedLanguages, InspireLanguageISO6392B responseLanguage,
            Collection<InspireUniqueResourceIdentifier> spatialDataSetIdentifier, InspireSupportedCRS supportedCRS) {
        super(supportedLanguages, responseLanguage, spatialDataSetIdentifier, supportedCRS);
        setMetadataUrl(metadataUrl);
    }

    @Override
    public InspireResourceLocator getMetadataUrl() {
        return metadataUrl;
    }

    @Override
    public MinimalInspireExtendedCapabilities setMetadataUrl(InspireResourceLocator metadataUrl) {
        this.metadataUrl = metadataUrl;
        return this;
    }

    @Override
    public boolean isSetMetadataUrl() {
        return getMetadataUrl() != null;
    }

    @Override
    public String toString() {
        return String.format("%s %n[%n \tn supportedLanguages=%s," + "%n responseLanguage=%s,"
                + "%n metadataUrl=%s%n]", this.getClass().getSimpleName(), getSupportedLanguages(),
                getResponseLanguage(), getMetadataUrl());
    }
}
