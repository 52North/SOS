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
package org.n52.sos.aquarius.harvest;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.geotools.util.Range;
import org.joda.time.DateTime;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.GeometryEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.dataset.DatasetType;
import org.n52.series.db.beans.dataset.ObservationType;
import org.n52.series.db.beans.dataset.ValueType;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.Parameter;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.proxy.harvest.EntityBuilder;

public interface AquariusEntityBuilder extends EntityBuilder {

    String TWENTY_FOUR = "T24:";

    String TWENTY_THREE = "T23:";

    default OfferingEntity createOffering(Map<String, OfferingEntity> offerings, TimeSeriesDescription dataset,
            Parameter parameter, ProcedureEntity procedure, ServiceEntity service, AquariusHelper aquariusHelper) {
        String offeringId = dataset.getIdentifier();
        OfferingEntity parentOffering = getParentOffering(procedure, offerings, service);
        if (!offerings.containsKey(offeringId)) {
            OfferingEntity entity = createOffering(offeringId, dataset.getIdentifier()
                    .replaceAll(parameter.getIdentifier(), parameter.getDisplayName()), offeringId, service);
            if (parentOffering != null) {
                Set<OfferingEntity> parents = new LinkedHashSet<>();
                parents.add(parentOffering);
                entity.setParents(parents);
            }
            offerings.put(offeringId, entity);
        }
        Range<DateTime> timeRange = aquariusHelper.getTimeRange(dataset);
        updateTime(parentOffering, timeRange);
        return updateTime(offerings.get(offeringId), timeRange);
    }

    default OfferingEntity updateTime(OfferingEntity entity, Range<DateTime> timeRange) {
        if (timeRange != null) {
            if (entity.getSamplingTimeStart() == null || timeRange.getMinValue()
                    .toDate()
                    .before(entity.getSamplingTimeStart())) {
                entity.setSamplingTimeStart(timeRange.getMinValue()
                        .toDate());
            }
            if (entity.getSamplingTimeEnd() == null || timeRange.getMaxValue()
                    .toDate()
                    .before(entity.getSamplingTimeEnd())) {
                entity.setSamplingTimeEnd(timeRange.getMaxValue()
                        .toDate());
            }
            // resultTime
            if (entity.getResultTimeStart() == null || timeRange.getMinValue()
                    .toDate()
                    .before(entity.getResultTimeStart())) {
                entity.setResultTimeStart(timeRange.getMinValue()
                        .toDate());
            }
            if (entity.getResultTimeEnd() == null || timeRange.getMaxValue()
                    .toDate()
                    .before(entity.getResultTimeEnd())) {
                entity.setResultTimeStart(timeRange.getMaxValue()
                        .toDate());
            }
        }
        return entity;
    }

    default OfferingEntity getParentOffering(ProcedureEntity procedure, Map<String, OfferingEntity> offerings,
            ServiceEntity service) {
        if (!offerings.containsKey(procedure.getIdentifier())) {
            OfferingEntity entity = createOffering(procedure.getIdentifier(), procedure.getName(),
                    procedure.getDescription(), service);
            entity.setDescription(procedure.getDescription());
            offerings.put(entity.getIdentifier(), entity);
        }
        return offerings.get(procedure.getIdentifier());
    }

    default ProcedureEntity createProcedure(Location location, Map<String, ProcedureEntity> procedures,
            ServiceEntity service) {
        String procedureId = location.getLocationType()
                .replace(" ", "_");
        if (!procedures.containsKey(procedureId)) {
            ProcedureEntity entity = createProcedure(procedureId, location.getLocationType(), null, service);
            procedures.put(procedureId, entity);
        }
        return procedures.get(procedureId);
    }

    default FeatureEntity createFeature(Location location, Map<String, FeatureEntity> features, ServiceEntity service)
            throws CodedException {
        if (!features.containsKey(location.getIdentifier())) {
            FeatureEntity entity = createFeature(location.getIdentifier(),
                    createNameFromLocation(location), null, service);
            entity.setGeometry(createGeometry(location));
            features.put(location.getIdentifier(), entity);
        }
        return features.get(location.getIdentifier());
    }

    default PlatformEntity createPlatform(Location location, Map<String, PlatformEntity> platforms,
            ServiceEntity service) {
        if (!platforms.containsKey(location.getIdentifier())) {
            PlatformEntity entity = createPlatform(location.getIdentifier(),
                    createNameFromLocation(location), null, service);
            platforms.put(location.getIdentifier(), entity);
        }
        return platforms.get(location.getIdentifier());
    }

    default String createNameFromLocation(Location location) {
        return location.getLocationName() + " (" + location.getIdentifier() + ")";
    }

    default Geometry createGeometry(Location location) {
        if (location.getLongitude() != null && location.getLatitude() != null) {
            GeometryEntity geometryEntity = new GeometryEntity();
            geometryEntity.setGeometryFactory(new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326));
            geometryEntity.setLat(location.getLatitude());
            geometryEntity.setLon(location.getLongitude());
            Geometry geometry = geometryEntity.getGeometry();
            if (location.getElevation() != null) {
                geometry.getCoordinate().z = location.getElevation();
            }
            return geometry;
        }
        return null;
    }

    default PhenomenonEntity createPhenomenon(Parameter parameter, Map<String, PhenomenonEntity> phenomenon,
            ServiceEntity service) {
        if (!phenomenon.containsKey(parameter.getIdentifier())) {
            PhenomenonEntity entity = createPhenomenon(parameter.getIdentifier(), parameter.getDisplayName(),
                    parameter.getDisplayName(), service);
            phenomenon.put(entity.getIdentifier(), entity);
        }
        return phenomenon.get(parameter.getIdentifier());
    }

    default CategoryEntity createCategory(PhenomenonEntity phen, Map<String, CategoryEntity> categories,
            ServiceEntity service) {
        if (!categories.containsKey(phen.getIdentifier())) {
            CategoryEntity entity =
                    createCategory(phen.getIdentifier(), phen.getName(), phen.getDescription(), service);
            categories.put(phen.getIdentifier(), entity);
        }
        return categories.get(phen.getIdentifier());
    }

    default DatasetEntity createDataset(ProcedureEntity procedure, OfferingEntity offering, FeatureEntity feature,
            PlatformEntity platform, TimeSeriesDescription timeSeries, Parameter parameter, ServiceEntity service) {
        DatasetEntity entity = new DatasetEntity(DatasetType.timeseries, ObservationType.simple, ValueType.quantity);
        addDescribeableData(entity, timeSeries.getUniqueId(), timeSeries.getIdentifier()
                .replaceAll(parameter.getIdentifier(), parameter.getDisplayName()), timeSeries.getIdentifier(),
                service);
        entity.setProcedure(procedure);
        entity.setOffering(offering);
        entity.setFeature(feature);
        entity.setPlatform(platform);
        // tags???
        entity.setDeleted(Boolean.FALSE);
        entity.setPublished(Boolean.TRUE);
        entity.setInsitu(Boolean.TRUE);
        entity.setMobile(Boolean.FALSE);
        entity.setUnit(createUnit(timeSeries.getUnit(), service));
        return entity;
    }

    default DatasetEntity updateDataset(DatasetEntity entity, Point timeSeriesDataFirstPoint,
            Point timeSeriesDataLastPoint) {
        if (timeSeriesDataFirstPoint != null) {
            DateTime dateTime = checkDateTimeStringFor24(timeSeriesDataFirstPoint.getTimestamp());
            entity.setFirstValueAt(dateTime.toDate());
            entity.setFirstObservation(createDataEntitiy(dateTime, timeSeriesDataFirstPoint.getValue()
                    .getNumericAsBigDecimal(), null));
            entity.setFirstQuantityValue(entity.getFirstObservation()
                    .getValueQuantity());
        }
        if (timeSeriesDataLastPoint != null) {
            DateTime dateTime = checkDateTimeStringFor24(timeSeriesDataLastPoint.getTimestamp());
            entity.setLastValueAt(dateTime.toDate());
            entity.setLastObservation(createDataEntitiy(dateTime, timeSeriesDataLastPoint.getValue()
                    .getNumericAsBigDecimal(), null));
            entity.setLastQuantityValue(entity.getLastObservation()
                    .getValueQuantity());
        }
        return entity;
    }

    default DateTime checkDateTimeStringFor24(String time) {
        return time.contains(TWENTY_FOUR) ? new DateTime(time.replace(TWENTY_FOUR, TWENTY_THREE)).plusDays(1)
                : new DateTime(time);
    }

    default UnitEntity createUnit(String unit, ServiceEntity service) {
        UnitEntity entity = createUnit(unit, unit, null, service);
        addDescribeableData(entity, unit, unit, unit, service);
        return entity;
    }

}
