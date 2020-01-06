/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.ogc.ows.extension.OwsCapabilitiesExtensionKey;
import org.n52.iceland.ogc.ows.extension.OwsCapabilitiesExtensionProvider;
import org.n52.shetland.ogc.ows.OwsCapabilitiesExtension;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosInsertionCapabilities;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class InsertResultCapabiltiesExtensionProvider
        implements OwsCapabilitiesExtensionProvider {

    private static final OwsCapabilitiesExtensionKey KEY
            = new OwsCapabilitiesExtensionKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION);
    @Inject
    private ContentCacheController contentCacheController;

    @Inject
    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    @Override
    public OwsCapabilitiesExtension getExtension() {
        SosInsertionCapabilities insertionCapabilities = new SosInsertionCapabilities();
        SosContentCache cache = getCache();
        insertionCapabilities.addFeatureOfInterestTypes(cache.getFeatureOfInterestTypes());
        insertionCapabilities.addObservationTypes(cache.getObservationTypes());
        insertionCapabilities.addProcedureDescriptionFormats(procedureDescriptionFormatRepository
                        .getSupportedProcedureDescriptionFormats(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
        // TODO dynamic
        insertionCapabilities.addSupportedEncoding(SweConstants.ENCODING_TEXT);
        return insertionCapabilities;
    }

    @Override
    public boolean hasRelatedOperation() {
        return true;
    }

    @Override
    public String getRelatedOperation() {
        return Sos2Constants.Operations.InsertResult.name();
    }

    @Override
    public Set<OwsCapabilitiesExtensionKey> getKeys() {
        return Collections.singleton(KEY);
    }

    private SosContentCache getCache() {
        return (SosContentCache) this.contentCacheController.getCache();
    }

}
