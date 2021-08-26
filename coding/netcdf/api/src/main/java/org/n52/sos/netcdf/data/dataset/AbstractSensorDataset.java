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
package org.n52.sos.netcdf.data.dataset;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.netcdf.data.subsensor.SubSensor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import ucar.nc2.constants.CF;

/**
 * Abstract sensor dataset class u com.google.common.collect.Lists;sed by netCDF
 * encoding
 *
 * @author <a href="mailto:shane@axiomdatascience.com">Shane StClair</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractSensorDataset implements Comparable<AbstractSensorDataset> {
    private CF.FeatureType featureType;

    private DatasetSensor sensor;

    private AbstractFeature procedure;

    private List<OmObservableProperty> obsProps;

    private List<Time> times;

    private List<SubSensor> subSensors;

    private Map<Time, Map<OmObservableProperty, Map<SubSensor, Value<?>>>> dataValues;

    public AbstractSensorDataset(CF.FeatureType featureType, DatasetSensor sensor,
            Map<Time, Map<OmObservableProperty, Map<SubSensor, Value<?>>>> dataValues, AbstractFeature procedure) {
        this.featureType = featureType;
        this.sensor = sensor;
        this.procedure = procedure;
        // make the sensorDataValues unmodifiable, since some data summaries
        // will be made below and we don't want the data changing
        this.dataValues = Collections.unmodifiableMap(dataValues);

        // set times, phenomena, and subsensors
        Set<Time> timeSet = Sets.newHashSet();
        Set<OmObservableProperty> obsPropSet = Sets.newHashSet();
        Set<SubSensor> subSensorSet = Sets.newHashSet();
        for (Entry<Time, Map<OmObservableProperty, Map<SubSensor, Value<?>>>> dataValuesEntry : dataValues
                .entrySet()) {
            Time time = dataValuesEntry.getKey();
            timeSet.add(time);
            for (Map.Entry<OmObservableProperty, Map<SubSensor, Value<?>>> phenObsEntry : dataValuesEntry.getValue()
                    .entrySet()) {
                OmObservableProperty phen = phenObsEntry.getKey();
                Set<SubSensor> phenSubSensors = phenObsEntry.getValue().keySet();
                obsPropSet.add(phen);
                for (SubSensor subSensor : phenSubSensors) {
                    if (subSensor != null) {
                        subSensorSet.add(subSensor);
                    }
                }
            }
        }

        List<Time> timeList = Lists.newArrayList(timeSet);
        Collections.sort(timeList);
        times = Collections.unmodifiableList(timeList);

        List<OmObservableProperty> obsPropList = Lists.newArrayList(obsPropSet);
        Collections.sort(obsPropList);
        obsProps = Collections.unmodifiableList(obsPropList);

        List<SubSensor> subSensorList = Lists.newArrayList(subSensorSet);
        Collections.sort(subSensorList);
        subSensors = Collections.unmodifiableList(subSensorList);
    }

    public DatasetSensor getSensor() {
        return sensor;
    }

    public String getSensorIdentifier() {
        return getSensor().getSensorIdentifier();
    }

    public CF.FeatureType getFeatureType() {
        return featureType;
    }

    public List<OmObservableProperty> getPhenomena() {
        return Collections.unmodifiableList(obsProps);
    }

    public List<SubSensor> getSubSensors() {
        return Collections.unmodifiableList(subSensors);
    }

    public AbstractFeature getProcedureDescription() {
        return procedure;
    }

    public List<Time> getTimes() {
        return Collections.unmodifiableList(times);
    }

    public Map<Time, Map<OmObservableProperty, Map<SubSensor, Value<?>>>> getDataValues() {
        return Collections.unmodifiableMap(dataValues);
    }

    public static Set<AbstractSensorDataset> getAbstractAssetDatasets(
            Set<? extends AbstractSensorDataset> stationDatasets) {
        Set<AbstractSensorDataset> abstractStationDatasets = new HashSet<AbstractSensorDataset>();
        abstractStationDatasets.addAll(stationDatasets);
        return abstractStationDatasets;
    }

    @Override
    public int compareTo(AbstractSensorDataset o) {
        if (sensor == null && o.getSensor() == null) {
            return 0;
        }
        if (sensor == null) {
            return -1;
        }
        if (o.getSensor() == null) {
            return 1;
        }
        if (sensor.equals(o.getSensor())) {
            return 0;
        }
        return -1;
    }

    public boolean isSetSubSensors() {
        return CollectionHelper.isNotEmpty(getSubSensors());
    }
}
