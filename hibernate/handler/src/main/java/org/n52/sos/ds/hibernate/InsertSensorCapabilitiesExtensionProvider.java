/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import javax.inject.Inject;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.shetland.ogc.ows.OwsCapabilitiesExtension;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosInsertionCapabilities;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class InsertSensorCapabilitiesExtensionProvider extends AbstractCapabilitiesExtensionProvider {

    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    private ContentCacheController contentCacheController;

    public InsertSensorCapabilitiesExtensionProvider() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, Sos2Constants.Operations.InsertSensor.name());
    }

    @Inject
    public void setContentCacheController(ContentCacheController contentCacheController) {
        this.contentCacheController = contentCacheController;
    }

    @Inject
    public void setProcedureDescriptionFormatRepository(
            ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository) {
        this.procedureDescriptionFormatRepository = procedureDescriptionFormatRepository;
    }

    @Override
    public OwsCapabilitiesExtension getExtension() {
        SosContentCache cache = getCache();
        SosInsertionCapabilities insertionCapabilities = new SosInsertionCapabilities();
        insertionCapabilities.addFeatureOfInterestTypes(cache.getFeatureOfInterestTypes());
        insertionCapabilities.addObservationTypes(cache.getObservationTypes());
        insertionCapabilities.addProcedureDescriptionFormats(this.procedureDescriptionFormatRepository
                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
        return insertionCapabilities;
    }

    private SosContentCache getCache() {
        return (SosContentCache) this.contentCacheController.getCache();
    }

}
