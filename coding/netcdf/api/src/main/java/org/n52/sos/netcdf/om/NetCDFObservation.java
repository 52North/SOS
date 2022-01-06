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
package org.n52.sos.netcdf.om;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.sos.netcdf.data.dataset.AbstractSensorDataset;

import com.google.common.collect.Lists;
import org.locationtech.jts.geom.Envelope;

import ucar.nc2.constants.CF;

/**
 * An netCDF compatible observation block containing all observations for a
 * feature type.
 *
 * @author <a href="mailto:shane@axiomdatascience.com">Shane StClair</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class NetCDFObservation {
    // for metadata block
    private CF.FeatureType featureType;

    private TimePeriod samplingTime = new TimePeriod();

    private Set<OmObservableProperty> phenomena = new HashSet<OmObservableProperty>();

    private Envelope envelope = new Envelope();

    // for data block
    private Map<String, ? extends AbstractSensorDataset> sensorDatasetMap;

    // constructor
    public NetCDFObservation(CF.FeatureType featureType, TimePeriod samplingTime,
            Map<String, ? extends AbstractSensorDataset> sensorDatasetMap, Set<OmObservableProperty> phenomena,
            Envelope envelope) {
        super();
        this.featureType = featureType;
        this.samplingTime = samplingTime;
        this.sensorDatasetMap = sensorDatasetMap;
        this.phenomena = phenomena;
        this.envelope = envelope;
    }

    public CF.FeatureType getFeatureType() {
        return featureType;
    }

    public TimePeriod getSamplingTime() {
        return samplingTime;
    }

    public Map<String, ? extends AbstractSensorDataset> getSensorDatasetMap() {
        return sensorDatasetMap;
    }

    public Set<OmObservableProperty> getPhenomena() {
        return phenomena;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public List<? extends AbstractSensorDataset> getSensorDatasets() {
        return Collections.unmodifiableList(Lists.newArrayList(sensorDatasetMap.values()));
    }

}
