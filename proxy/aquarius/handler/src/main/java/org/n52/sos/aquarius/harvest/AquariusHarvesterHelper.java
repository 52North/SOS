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
package org.n52.sos.aquarius.harvest;

import java.util.Map;

import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.TimeSeriesData;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.proxy.harvest.HarvesterHelper;

public interface AquariusHarvesterHelper extends HarvesterHelper, AquariusEntityBuilder {

    AquariusHelper getAquariusHelper();

    default boolean checkLocation(String identifier, Map<String, Location> locations) {
        if (locations.containsKey(identifier)) {
            Location location = locations.get(identifier);
            return location.getLatitude() != null && location.getLongitude() != null;
        }
        return false;
    }

    default void updateFirstLastObservation(DatasetEntity dataset, TimeSeriesDescription timeSeries,
            AquariusConnector connector) throws OwsExceptionReport {
        TimeSeriesData firstTimeSeriesData = connector.getTimeSeriesDataFirstPoint(timeSeries.getUniqueId());
        TimeSeriesData lastTimeSeriesData = connector.getTimeSeriesDataLastPoint(timeSeries.getUniqueId());
        updateDataset(dataset, firstTimeSeriesData, lastTimeSeriesData);
    }

    default DatasetEntity updateDataset(DatasetEntity entity, TimeSeriesData firstTimeSeriesData,
            TimeSeriesData lastTimeSeriesDataLast) {
        Point timeSeriesDataFirstPoint = null;
        Point timeSeriesDataLastPoint = null;
        if (firstTimeSeriesData != null) {
            timeSeriesDataFirstPoint = getAquariusHelper().applyQualifierChecker(firstTimeSeriesData)
                    .getFirstPoint();
        }
        if (lastTimeSeriesDataLast != null) {
            timeSeriesDataLastPoint = getAquariusHelper().applyQualifierChecker(firstTimeSeriesData)
                    .getLastPoint();
        }
        return updateDataset(entity, timeSeriesDataFirstPoint, timeSeriesDataLastPoint);
    }

}
