/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Geometry;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.dataset.DatasetType;
import org.n52.series.db.beans.dataset.ObservationType;
import org.n52.series.db.beans.dataset.ValueType;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.features.SfConstants;

public interface DatabaseQueryHelper {

    default String getObservationType(DatasetEntity dataset) {
        if (dataset != null) {
            if (dataset.isSetOmObservationType()) {
                return dataset.getOmObservationType().getFormat();
            } else if (dataset.getDatasetType().equals(DatasetType.profile)
                    || dataset.getObservationType().equals(ObservationType.profile)) {
                return OmConstants.OBS_TYPE_PROFILE_OBSERVATION;
            } else if (!dataset.getValueType().equals(ValueType.not_initialized)) {
                return getObservationTypeForValueType(dataset.getValueType());
            }
        }
        return "";
    }

    default Set<String> getObservationTypes(Collection<DatasetEntity> datasets) {
        if (datasets != null) {
            return datasets.stream()
                    .filter(d -> !d.isHidden() && !d.isDeleted() && d.isPublished()
                            && !d.getDatasetType().equals(DatasetType.not_initialized))
                    .map(d -> getObservationType(d)).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    default String getObservationTypeForValueType(ValueType valueType) {
        switch (valueType) {
            case bool:
                return  OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
            case category:
                return  OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
            case count:
                return  OmConstants.OBS_TYPE_COUNT_OBSERVATION;
            case geometry:
                return  OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION;
            case quantity:
                return  OmConstants.OBS_TYPE_MEASUREMENT;
            case referenced:
                return  OmConstants.OBS_TYPE_REFERENCE_OBSERVATION;
            case text:
                return  OmConstants.OBS_TYPE_TEXT_OBSERVATION;
            default:
                return OmConstants.OBS_TYPE_OBSERVATION;
        }
    }

    default String getFeatureType(DatasetEntity dataset) {
        if (dataset != null) {
            if (dataset.isSetFeature()) {
                if (dataset.getFeature().isSetFeatureType()) {
                    return dataset.getFeature().getFeatureType().getFormat();
                } else if (dataset.getFeature().isSetGeometry()) {
                    return getFeatureTypeForGeoemtry(dataset.getFeature().getGeometry());
                }
            }
        }
        return "";
    }

    default Set<String> getFeatureTypes(Collection<DatasetEntity> datasets) {
        if (datasets != null) {
            return datasets.stream()
                    .filter(d -> !d.isHidden() && !d.isDeleted() && d.isPublished()
                            && !d.getDatasetType().equals(DatasetType.not_initialized))
                    .map(d -> getFeatureType(d)).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    default String getFeatureTypes(FeatureEntity feature) {
        if (feature != null) {
            if (feature.isSetFeatureType()) {
                return feature.getFeatureType().getFormat();
            } else if (feature.isSetGeometry()) {
                return getFeatureTypeForGeoemtry(feature.getGeometry());
            }
        }
        return "";
    }

    default String getFeatureTypeForGeoemtry(Geometry geometry) {
        switch (geometry.getGeometryType()) {
            case "Point":
                return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT;
            case "LineString":
                return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE;
            case "Polygon":
                return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE;
            default:
                return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_FEATURE;
        }
    }
}
