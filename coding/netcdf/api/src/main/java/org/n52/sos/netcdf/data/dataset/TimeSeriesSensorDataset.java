/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.Map;

import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.sos.netcdf.data.subsensor.SubSensor;

import ucar.nc2.constants.CF;

/**
 * Implementation of {@link AbstractSensorDataset} for time series sensor datasets
 *
 * @author <a href="mailto:shane@axiomdatascience.com">Shane StClair</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class TimeSeriesSensorDataset extends AbstractSensorDataset
        implements StaticLocationDataset, StaticAltitudeDataset {
    private final Double lng;

    private final Double lat;

    private final Double alt;

    public TimeSeriesSensorDataset(DatasetSensor sensor, Double lng, Double lat, Double alt,
            Map<Time, Map<OmObservableProperty, Map<SubSensor, Value<?>>>> dataValues, AbstractFeature procedure) {
        super(CF.FeatureType.timeSeries, sensor, dataValues, procedure);
        this.lng = lng;
        this.lat = lat;
        this.alt = alt;
    }

    @Override
    public Double getLng() {
        return lng;
    }

    @Override
    public Double getLat() {
        return lat;
    }

    @Override
    public Double getAlt() {
        return alt;
    }
}
