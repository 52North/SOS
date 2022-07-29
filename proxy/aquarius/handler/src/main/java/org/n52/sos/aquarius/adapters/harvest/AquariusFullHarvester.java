/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.adapters.harvest;

import org.n52.sensorweb.server.helgoland.adapters.harvest.FullHarvester;
import org.n52.sensorweb.server.helgoland.adapters.harvest.FullHarvesterResponse;
import org.n52.sensorweb.server.helgoland.adapters.harvest.HarvestContext;
import org.n52.sensorweb.server.helgoland.adapters.harvest.HarvesterResponse;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.harvest.AbstractAquariusHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class AquariusFullHarvester extends AbstractAquariusHarvester implements FullHarvester {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusFullHarvester.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HarvesterResponse process(HarvestContext context) {
        if (context instanceof AquariusHarvesterContext) {
            AquariusConnector connector = ((AquariusHarvesterContext) context).getConnector();
            procedures.clear();
            phenomenon.clear();
            categories.clear();
            offerings.clear();
            parameters.clear();
            features.clear();
            platforms.clear();
            locations.clear();
            units.clear();
            checkGradesAndQualifier(connector);
            try {
                ServiceEntity service = getOrInsertServiceEntity();
                parameters = getParameterList(connector);
                units = getUnitList(connector);
                harvestDatasets(service, connector);
            } catch (OwsExceptionReport e) {
                LOGGER.error("Error while harvesting data!", e);
            }
            return new FullHarvesterResponse();
        }
        return new FullHarvesterResponse(false);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

}
