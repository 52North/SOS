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
package org.n52.sos.statistics.sos.models;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.n52.iceland.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.om.OmObservation;

public final class OmObservationEsModel extends AbstractElasticsearchModel {

    private final OmObservation observation;

    private OmObservationEsModel(OmObservation observation) {
        this.observation = observation;
    }

    public static Map<String, Object> convert(OmObservation observation) {
        if (observation == null) {
            return null;
        }
        return new OmObservationEsModel(observation).getAsMap();
    }

    public static List<Map<String, Object>> convert(Collection<OmObservation> observation) {
        if (observation == null || observation.isEmpty()) {
            return null;
        }
        return observation.stream().map(OmObservationEsModel::convert).collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> getAsMap() {
        Map<String, Object> constellation =
                OmObservationConstellationEsModel.convert(observation.getObservationConstellation());
        put(ObjectEsParameterFactory.OMOBS_CONSTELLATION, constellation);

        if (observation.getSpatialFilteringProfileParameter() != null) {
            SpatialFilter dummy = new SpatialFilter(SpatialOperator.BBOX,
                    observation.getSpatialFilteringProfileParameter().getValue().getValue(), null);
            put(ObjectEsParameterFactory.OMOBS_SAMPLING_GEOMETRY, SpatialFilterEsModel.convert(dummy));
        }

        put(ObjectEsParameterFactory.OMOBS_PHENOMENON_TIME, TimeEsModel.convert(observation.getPhenomenonTime()));
        put(ObjectEsParameterFactory.OMOBS_RESULT_TIME, TimeEsModel.convert(observation.getResultTime()));
        put(ObjectEsParameterFactory.OMOBS_VALID_TIME, TimeEsModel.convert(observation.getValidTime()));

        return dataMap;
    }

}
