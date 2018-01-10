/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
import org.n52.shetland.ogc.om.OmObservationConstellation;

public class OmObservationConstellationEsModel extends AbstractElasticsearchModel {

    private final OmObservationConstellation constellation;

    private OmObservationConstellationEsModel(OmObservationConstellation constellation) {
        this.constellation = constellation;
    }

    public static Map<String, Object> convert(OmObservationConstellation observationConstellation) {
        if (observationConstellation == null) {
            return null;
        }
        return new OmObservationConstellationEsModel(observationConstellation).getAsMap();
    }

    public static List<Map<String, Object>> convert(Collection<OmObservationConstellation> observationConstellation) {
        if (observationConstellation == null || observationConstellation.isEmpty()) {
            return null;
        }
        return observationConstellation.stream().map(OmObservationConstellationEsModel::convert).collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> getAsMap() {
        if (constellation.getProcedure() != null) {
            put(ObjectEsParameterFactory.OMOCONSTELL_PROCEDURE, constellation.getProcedure().getIdentifier());
        }
        if (constellation.getObservableProperty() != null) {
            put(ObjectEsParameterFactory.OMOCONSTELL_OBSERVABLE_PROPERTY, constellation.getObservableProperty().getIdentifier());
        }
        if (constellation.getFeatureOfInterest() != null) {
            put(ObjectEsParameterFactory.OMOCONSTELL_FEATURE_OF_INTEREST, constellation.getFeatureOfInterest().getIdentifier());
        }

        put(ObjectEsParameterFactory.OMOCONSTELL_OBSERVATION_TYPE, constellation.getObservationType());

        return dataMap;
    }
}
