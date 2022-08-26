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

import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.harvest.AbstractAquariusHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

public class AquariustDatasetHarvester extends AbstractAquariusHarvester {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariustDatasetHarvester.class);


    @Transactional(rollbackFor = Exception.class)
    public void deleteObsoleteData(Map<String, DatasetEntity> datasets) {
        super.deleteObsoleteData(datasets);
    }

    @Transactional(rollbackFor = Exception.class)
    public void harvestDatasets(LocationDataServiceResponse location, Map<String, DatasetEntity> datasets,
            AquariusConnector connector) throws OwsExceptionReport {
        LOGGER.debug("Start harvesting datasets/timeSeries!");
        ServiceEntity service = getOrInsertServiceEntity();
        if (checkLocation(location)) {
            LOGGER.debug("Harvesting timeseries for location '{}'", location.getLocationName());
            for (TimeSeriesDescription ts : getTimeSeries(location.getIdentifier(), connector)) {
                if (ts != null) {
                    LOGGER.debug("Harvesting timeseries '{}'", ts.getIdentifier());
                    try {
                        ProcedureEntity procedure = createProcedure(location, getProcedures(), service);
                        FeatureEntity feature = createFeature(location, getFeatures(), service);
                        PlatformEntity platform = createPlatform(location, getPlatforms(), service);
                        harvestDatasets(location, ts, feature, procedure, platform, service, connector);
                        datasets.remove(ts.getUniqueId());
                    } catch (Exception e) {
                        LOGGER.error(String.format("Error while harvesting dataset '%s'!", ts.getIdentifier()), e);
                    }
                }
            }
        } else {
            LOGGER.debug("Location '{}' does not have coordinates!", location.getLocationName());
        }
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

}
