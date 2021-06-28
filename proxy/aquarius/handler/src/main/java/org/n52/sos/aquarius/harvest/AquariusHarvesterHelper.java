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
