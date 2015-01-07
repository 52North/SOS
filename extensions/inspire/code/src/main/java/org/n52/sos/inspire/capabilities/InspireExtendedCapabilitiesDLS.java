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
import java.util.Set;

import org.n52.sos.inspire.InspireConstants;
import org.n52.sos.inspire.InspireLanguageISO6392B;
import org.n52.sos.inspire.InspireSupportedCRS;
import org.n52.sos.inspire.InspireSupportedLanguages;
import org.n52.sos.inspire.InspireUniqueResourceIdentifier;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesSpatialDataSetIdentifier;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesSupportedCRS;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * Abstract service internal representation of INSPIRE DLS ExtendedCapabilities. Extends the
 * {@link InspireExtendedCapabilities} with the DLS specific data
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public abstract class InspireExtendedCapabilitiesDLS extends InspireExtendedCapabilities implements
        InspireExtendedCapabilitiesSpatialDataSetIdentifier, InspireExtendedCapabilitiesSupportedCRS {

    /* SpatialDataSetIdentifier 1..* */
    private Set<InspireUniqueResourceIdentifier> spatialDataSetIdentifier = Sets.newHashSet();
    
    private InspireSupportedCRS supportedCRS;

    /**
     * constructor
     * @param supportedLanguages
     *            Supported languages to set
     * @param responseLanguage
     *            Response language to set
     * @param spatialDataSetIdentifier
     *            Spatial dataset identifier to set
     *  @param supportedCRS
     *            Supported CRSes to set
     */
    public InspireExtendedCapabilitiesDLS(InspireSupportedLanguages supportedLanguages,
            InspireLanguageISO6392B responseLanguage, InspireUniqueResourceIdentifier spatialDataSetIdentifier, InspireSupportedCRS supportedCRS) {
        super(supportedLanguages, responseLanguage);
        addSpatialDataSetIdentifier(spatialDataSetIdentifier);
        setSupportedCRS(supportedCRS);
    }

    /**
     * constructor
     * @param supportedLanguages
     *            Supported languages to set
     * @param responseLanguage
     *            Response language to set
     * @param spatialDataSetIdentifiers
     *            Spatial dataset identifiers to set
     *  @param supportedCRS
     *            Supported CRSes to set
     */
    public InspireExtendedCapabilitiesDLS(InspireSupportedLanguages supportedLanguages,
            InspireLanguageISO6392B responseLanguage,
            Collection<InspireUniqueResourceIdentifier> spatialDataSetIdentifiers, InspireSupportedCRS supportedCRS) {
        super(supportedLanguages, responseLanguage);
        setSpatialDataSetIdentifier(spatialDataSetIdentifiers);
        setSupportedCRS(supportedCRS);
    }

    @Override
    public String getNamespace() {
        return InspireConstants.NS_INSPIRE_DLS;
    }

    @Override
    public Set<InspireUniqueResourceIdentifier> getSpatialDataSetIdentifier() {
        return spatialDataSetIdentifier;
    }

    @Override
    public InspireExtendedCapabilitiesDLS setSpatialDataSetIdentifier(
            Collection<InspireUniqueResourceIdentifier> spatialDataSetIdentifier) {
        if (CollectionHelper.isNotEmpty(spatialDataSetIdentifier)) {
            getSpatialDataSetIdentifier().clear();
            getSpatialDataSetIdentifier().addAll(spatialDataSetIdentifier);
        }
        return this;
    }

    @Override
    public InspireExtendedCapabilitiesDLS addSpatialDataSetIdentifier(
            InspireUniqueResourceIdentifier spatialDataSetIdentifier) {
        getSpatialDataSetIdentifier().add(spatialDataSetIdentifier);
        return this;

    }

    @Override
    public boolean isSetSpatialDataSetIdentifier() {
        return CollectionHelper.isNotEmpty(getSpatialDataSetIdentifier());
    }

    @Override
    public InspireExtendedCapabilitiesSupportedCRS setSupportedCRS(InspireSupportedCRS supportedCRS) {
        this.supportedCRS = supportedCRS;
        return this;
    }
    
    @Override
    public InspireSupportedCRS getSupportedCRS() {
        return supportedCRS;
    }
    
    @Override
    public boolean isSetSupportedCRS() {
        return getSupportedCRS() != null;
    }

}
