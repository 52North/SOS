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
                LOGGER.debug("Harvesting timeseries '{}'", ts.getIdentifier());
                ProcedureEntity procedure = createProcedure(location, getProcedures(), service);
                FeatureEntity feature = createFeature(location, getFeatures(), service);
                PlatformEntity platform = createPlatform(location, getPlatforms(), service);
                harvestDatasets(location, ts, feature, procedure, platform, service, connector);
                datasets.remove(ts.getUniqueId());
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
