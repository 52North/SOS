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

import java.util.Map;

import javax.inject.Inject;

import org.n52.sensorweb.server.helgoland.adapters.harvest.FullHarvester;
import org.n52.sensorweb.server.helgoland.adapters.harvest.FullHarvesterResponse;
import org.n52.sensorweb.server.helgoland.adapters.harvest.HarvestContext;
import org.n52.sensorweb.server.helgoland.adapters.harvest.HarvesterResponse;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.harvest.AbstractAquariusHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AquariusFullHarvester extends AbstractAquariusHarvester implements FullHarvester {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusFullHarvester.class);

    @Inject
    private AquariustDatasetHarvester harvester;

    @Override
    public HarvesterResponse process(HarvestContext context) {
        if (context instanceof AquariusHarvesterContext) {
            try {
                AquariusConnector connector = ((AquariusHarvesterContext) context).getConnector();
                clearMaps();
                checkGradesAndQualifier(connector);
                getParameterList(connector);
                getUnitList(connector);
                Map<String, DatasetEntity> datasets = getIdentifierDatasetMap(getServiceEntity());
                int counter = 0;
                for (String identifier : getLocationIds(connector)) {
                    if (identifier != null && !identifier.isEmpty()) {
                        try {
                            harvester.harvestDatasets(getLocation(identifier, connector), datasets, connector);
                            if (getAquariusHelper().getUpdateCount() > 0
                                    && ++counter % getAquariusHelper().getUpdateCount() == 0) {
                                updateCache();
                            }
                        } catch (Exception e) {
                            LOGGER.error(String.format("Error while harvesting data for location '%s'!", identifier),
                                    e);
                        }
                    }
                }
                if (!datasets.isEmpty()) {
                    LOGGER.debug("Start removing datasets/timeSeries!");
                    harvester.deleteObsoleteData(datasets);
                }
                return new FullHarvesterResponse();
            } catch (Exception e) {
                LOGGER.error("Error while harvesting data!", e);
            }
        }
        return new FullHarvesterResponse(false);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

}
