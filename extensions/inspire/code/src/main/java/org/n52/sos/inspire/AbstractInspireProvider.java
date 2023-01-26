/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.inspire;

import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.shetland.inspire.InspireSupportedCRS;
import org.n52.shetland.inspire.InspireSupportedLanguages;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.inspire.capabilities.InspireExtendedCapabilitiesProvider;
import org.n52.sos.inspire.offering.InspireOfferingExtensionProvider;
import org.n52.sos.util.GeometryHandler;

import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Abstract INSPIRE provider class provides methods used by
 * {@link InspireExtendedCapabilitiesProvider} and
 * {@link InspireOfferingExtensionProvider}.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class AbstractInspireProvider {

    private InspireHelper inspireHelper;

    private ContentCacheController contentCacheController;

    private GeometryHandler geometryHandler;

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    @Inject
    public void setContentCacheController(ContentCacheController ctrl) {
        this.contentCacheController = ctrl;
    }

    @Inject
    public void setInspireHelper(InspireHelper inspireHelper) {
        this.inspireHelper = inspireHelper;
    }

    /**
     * Get the supported languages
     *
     * @return the supported languages
     */
    protected InspireSupportedLanguages getSupportedLanguages() {
        InspireSupportedLanguages inspireSupportedLanguages =
                new InspireSupportedLanguages(getInspireHelper().getDefaultLanguage());
        inspireSupportedLanguages.setSupportedLanguages(getInspireHelper().getSupportedLanguages());
        return inspireSupportedLanguages;
    }

    /**
     * Get the supported languages
     *
     * @return the supported languages
     */
    protected InspireSupportedCRS getSupportedCRS() {
        InspireSupportedCRS inspireSupportedCRS = null;
        if (getInspireHelper().isUseAuthority()) {
            inspireSupportedCRS = new InspireSupportedCRS(
                    getGeometryHandler().addAuthorityCrsPrefix(getGeometryHandler().getDefaultResponseEPSG()));
            inspireSupportedCRS.setOtherCRS(getGeometryHandler()
                    .addAuthorityCrsPrefix(removeDefaultCRS(getGeometryHandler().getDefaultResponseEPSG(),
                            getGeometryHandler().getSupportedCRS())));
        } else {
            inspireSupportedCRS = new InspireSupportedCRS(
                    getGeometryHandler().addOgcCrsPrefix(getGeometryHandler().getDefaultResponseEPSG()));
            inspireSupportedCRS.setOtherCRS(getGeometryHandler()
                    .addOgcCrsPrefix(removeDefaultCRS(getGeometryHandler().getDefaultResponseEPSG(),
                            getGeometryHandler().getSupportedCRS())));
        }
        return inspireSupportedCRS;
    }

    /**
     * Remove the default CRS from other CRS set
     *
     * @param defaultCRS
     *            Default CRS to remove
     * @param otherCRS
     *            Other CRSes
     * @return Set without default CRS
     */
    protected Set<String> removeDefaultCRS(int defaultCRS, Set<String> otherCRS) {
        Set<String> checkSet = Sets.newHashSetWithExpectedSize(otherCRS.size());
        for (String string : otherCRS) {
            if (!string.equals(Integer.toString(defaultCRS))) {
                checkSet.add(string);
            }
        }
        return checkSet;
    }

    /**
     * Get the {@link InspireHelper} instance
     *
     * @return the {@link InspireHelper} instance
     */
    protected InspireHelper getInspireHelper() {
        return this.inspireHelper;
    }

    /**
     * Get the {@link GeometryHandler} instance
     *
     * @return the {@link GeometryHandler} instance
     */
    protected GeometryHandler getGeometryHandler() {
        return this.geometryHandler;
    }

    protected SosContentCache getCache() {
        return (SosContentCache) this.contentCacheController.getCache();
    }

}
