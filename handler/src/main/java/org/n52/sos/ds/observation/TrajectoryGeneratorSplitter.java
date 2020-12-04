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
package org.n52.sos.ds.observation;

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
