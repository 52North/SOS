package org.n52.sos.ds.hibernate.util.observation;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.TrajectoryDataEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.om.values.TrajectoryElement;
import org.n52.shetland.ogc.om.values.TrajectoryValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TrajectoryGeneratorSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(TrajectoryGeneratorSplitter.class);

    private AbstractObservationValueCreator creator;

    public TrajectoryGeneratorSplitter(AbstractObservationValueCreator creator) {
        this.creator = creator;
    }

    public TrajectoryValue create(TrajectoryDataEntity entity) throws OwsExceptionReport {
        TrajectoryValue trajectoryValue = new TrajectoryValue("");
        trajectoryValue.setGmlId("pv" + entity.getId());
        UoM uom = null;
        trajectoryValue.setValue(createTrajectoryElement(entity));
        return trajectoryValue;
    }

    public SweAbstractDataComponent createValue(TrajectoryDataEntity entity) throws OwsExceptionReport {
        return create(entity).asDataRecord();
    }

    private List<TrajectoryElement> createTrajectoryElement(TrajectoryDataEntity entity) throws OwsExceptionReport {
        Map<Date, TrajectoryElement> map = Maps.newTreeMap();
        if (entity.hasValue()) {
            for (DataEntity<?> observation : entity.getValue()) {
                Date key = observation.getSamplingTimeStart();
                Value<?> value = creator.visit(observation);
                if (map.containsKey(key)) {
                    map.get(key)
                            .addValue(value);
                } else {
                    TrajectoryElement profileLevel = new TrajectoryElement();
                    if (observation.isSetGeometryEntity()) {
                        profileLevel.setLocation(observation.getGeometryEntity()
                                .getGeometry());
                    }
                    profileLevel.setPhenomenonTime(new PhenomenonTimeCreator(observation).create());
                    profileLevel.addValue(value);
                    map.put(key, profileLevel);
                }
            }
        }
        return (List<TrajectoryElement>) Lists.newArrayList(map.values());
    }

    public void split(TrajectoryValue coverage, TrajectoryDataEntity entity) {
        LOG.warn("Inserting of GW_GeologyLogCoverages is not yet supported!");
    }
}
