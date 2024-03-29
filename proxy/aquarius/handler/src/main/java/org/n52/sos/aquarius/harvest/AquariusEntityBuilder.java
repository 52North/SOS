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
package org.n52.sos.aquarius.harvest;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.util.Range;
import org.joda.time.DateTime;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.n52.sensorweb.server.helgoland.adapters.utils.EntityBuilder;
import org.n52.sensorweb.server.helgoland.adapters.web.Counter;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.DetectionLimitEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.GeometryEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.parameter.ParameterFactory;
import org.n52.series.db.beans.parameter.ParameterFactory.EntityType;
import org.n52.series.db.beans.parameter.TextParameterEntity;
import org.n52.series.db.beans.parameter.dataset.DatasetParameterEntity;
import org.n52.series.db.beans.quality.TextQualityEntity;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.ds.AquariusTimeHelper;
import org.n52.sos.aquarius.ds.Grade;
import org.n52.sos.aquarius.ds.Point;
import org.n52.sos.aquarius.ds.Qualifier;
import org.n52.sos.aquarius.ds.QualifierKey;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;

public interface AquariusEntityBuilder extends EntityBuilder, AquariusTimeHelper {

    String COMMA = ",";

    String UNDERSCORE = "_";

    String DOT = ".";

    Pattern PATTERN = Pattern.compile("[0-9],[0-9]");

    default OfferingEntity createOffering(Map<String, OfferingEntity> offerings, TimeSeriesDescription timeSeries,
            ProcedureEntity procedure, ServiceEntity service, AquariusHelper aquariusHelper) {
        String offeringId = getValidatedIdentifier(timeSeries.getIdentifier());
        OfferingEntity parentOffering = getParentOffering(procedure, offerings, service);
        if (!offerings.containsKey(offeringId)) {
            OfferingEntity entity = createOffering(offeringId, getValidatedIdentifier(timeSeries.getIdentifier()),
                    offeringId, service);
            if (parentOffering != null) {
                Set<OfferingEntity> parents = new LinkedHashSet<>();
                parents.add(parentOffering);
                entity.setParents(parents);
            }
            offerings.put(offeringId, entity);
        }
        Range<DateTime> timeRange = aquariusHelper.getTimeRange(timeSeries);
        updateTime(parentOffering, timeRange);
        return updateTime(offerings.get(offeringId), timeRange);
    }

    default OfferingEntity updateTime(OfferingEntity entity, Range<DateTime> timeRange) {
        if (timeRange != null) {
            if (entity.getSamplingTimeStart() == null
                    || timeRange.getMinValue().toDate().before(entity.getSamplingTimeStart())) {
                entity.setSamplingTimeStart(timeRange.getMinValue().toDate());
            }
            if (entity.getSamplingTimeEnd() == null
                    || timeRange.getMaxValue().toDate().before(entity.getSamplingTimeEnd())) {
                entity.setSamplingTimeEnd(timeRange.getMaxValue().toDate());
            }
            // resultTime
            if (entity.getResultTimeStart() == null
                    || timeRange.getMinValue().toDate().before(entity.getResultTimeStart())) {
                entity.setResultTimeStart(timeRange.getMinValue().toDate());
            }
            if (entity.getResultTimeEnd() == null
                    || timeRange.getMaxValue().toDate().before(entity.getResultTimeEnd())) {
                entity.setResultTimeStart(timeRange.getMaxValue().toDate());
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

    default ProcedureEntity createProcedure(LocationDataServiceResponse location,
            Map<String, ProcedureEntity> procedures, ServiceEntity service) {
        String procedureId = location.getLocationType().replace(" ", "_");
        if (!procedures.containsKey(procedureId)) {
            ProcedureEntity entity = createProcedure(procedureId, location.getLocationType(), null, service);
            procedures.put(procedureId, entity);
        }
        return procedures.get(procedureId);
    }

    default FeatureEntity createFeature(LocationDataServiceResponse location, Map<String, FeatureEntity> features,
            ServiceEntity service) throws CodedException {
        if (!features.containsKey(location.getIdentifier())) {
            FeatureEntity entity =
                    createFeature(location.getIdentifier(), createNameFromLocation(location), null, service);
            entity.setGeometry(createGeometry(location));
            features.put(location.getIdentifier(), entity);
        }
        return features.get(location.getIdentifier());
    }

    default PlatformEntity createPlatform(LocationDataServiceResponse location, Map<String, PlatformEntity> platforms,
            ServiceEntity service) {
        if (!platforms.containsKey(location.getIdentifier())) {
            PlatformEntity entity =
                    createPlatform(location.getIdentifier(), createNameFromLocation(location), null, service);
            platforms.put(location.getIdentifier(), entity);
        }
        return platforms.get(location.getIdentifier());
    }

    default String createNameFromLocation(LocationDataServiceResponse location) {
        return location.getLocationName() + " (" + location.getIdentifier() + ")";
    }

    default Geometry createGeometry(LocationDataServiceResponse location) {
        if (location.getLongitude() != null && location.getLatitude() != null) {
            GeometryEntity geometryEntity = new GeometryEntity();
            geometryEntity.setGeometryFactory(new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326));
            geometryEntity.setLat(location.getLatitude());
            geometryEntity.setLon(location.getLongitude());
            Geometry geometry = geometryEntity.getGeometry();
            return geometry;
        }
        return null;
    }

    default PhenomenonEntity createPhenomenon(ParameterMetadata parameter, Map<String, PhenomenonEntity> phenomenon,
            ServiceEntity service) {
        String identifier = getValidatedIdentifier(parameter.getIdentifier());
        if (!phenomenon.containsKey(identifier)) {
            PhenomenonEntity entity =
                    createPhenomenon(identifier, parameter.getDisplayName(), parameter.getDisplayName(), service);
            phenomenon.put(entity.getIdentifier(), entity);
        }
        return phenomenon.get(identifier);
    }

    default String getValidatedIdentifier(String identifier) {
        if (identifier.contains(COMMA)) {
            Matcher matcher = PATTERN.matcher(identifier);
            String checked = identifier;
            while (matcher.find()) {
                checked = checked.replace(matcher.group(0), matcher.group(0).replace(COMMA, DOT));
            }
            return checked.replaceAll(COMMA, UNDERSCORE);
        }
        return identifier;
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
            PlatformEntity platform, TimeSeriesDescription timeSeries, ParameterMetadata parameter, UnitMetadata unit,
            ServiceEntity service) {
        DatasetEntity entity = createDataset(timeSeries.getUniqueId(), timeSeries.getIdentifier(),
                getDatasetDescription(timeSeries, parameter), service);
        entity.setProcedure(procedure);
        entity.setOffering(offering);
        entity.setFeature(feature);
        entity.setPlatform(platform);
        // tags???
        entity.setInsitu(Boolean.TRUE);
        entity.setMobile(Boolean.FALSE);
        entity.setUnit(unit != null ? createUnit(unit, service) : createUnit(timeSeries.getUnit(), service));
        return entity;
    }

    default String getDatasetDescription(TimeSeriesDescription timeSeries, ParameterMetadata parameter) {
        if (parameter != null && parameter.getDisplayName() != null) {
            return timeSeries.getIdentifier().replaceAll(parameter.getIdentifier(), parameter.getDisplayName());
        }
        return timeSeries.getIdentifier();
    }

    default QuantityDataEntity createDataEntity(DateTime time, BigDecimal value, Long id, Qualifier qualifier) {
        QuantityDataEntity entity = createDataEntity(time, value, id);
        entity.setValue(value);
        DetectionLimitEntity detectionLimitEntity = new DetectionLimitEntity();
        detectionLimitEntity.setDetectionLimit(value);
        detectionLimitEntity.setFlag(null);
        entity.setDetectionLimit(detectionLimitEntity);

        return entity;
    }

    default DataEntity<?> createDataEntity(DatasetEntity dataset, Point point, Counter counter,
            boolean applyRounding) {
        return createDataEntity(dataset, point, counter.next(), applyRounding);
    }

    default DataEntity<?> createDataEntity(DatasetEntity dataset, Point point, Long id, boolean applyRounding) {
        DateTime time = point.getDateTime();
        if (point.isNumeric()) {
            QuantityDataEntity dataEntity = createQuantityDataEntity(dataset, time, id);
            checkAndAddQualifiersAndValue(dataEntity, point, applyRounding);
            checkAndAddGrades(dataEntity, point);
            return dataEntity;
        } else if (point.isDisplay()) {
            TextDataEntity dataEntity = createTextDataEntity(dataset, time, id);
            dataEntity.setValue(point.getValue().getDisplay());
            return dataEntity;
        }
        return null;
    }

    default QuantityDataEntity createQuantityDataEntity(Long datasetId) {
        QuantityDataEntity mde = new QuantityDataEntity();
        mde.setDatasetId(datasetId);
        return mde;
    }

    default QuantityDataEntity createQuantityDataEntity(DatasetEntity dataset, DateTime time, Long id) {
        return (QuantityDataEntity) addDefault(new QuantityDataEntity(), dataset, time, id);
    }

    default TextDataEntity createTextDataEntity(DatasetEntity dataset, DateTime time, Long id) {
        return (TextDataEntity) addDefault(new TextDataEntity(), dataset, time, id);
    }

    default Point checkAndAddGrades(QuantityDataEntity dataEntity, Point point) {
        if (point.hasGrades()) {
            for (Grade grade : point.getGrades()) {
                addGrade(dataEntity, grade);
            }
        }
        return point;
    }

    default DataEntity<?> addGrade(QuantityDataEntity dataEntity, Grade grade) {
        TextQualityEntity textQualityEntity = new TextQualityEntity();
        // textQualityEntity.setIdentifier(grade.getGradeCode());
        textQualityEntity.setName(grade.getDisplayName());
        textQualityEntity.setDescription(grade.getDescription());
        textQualityEntity.setValue(grade.getGradeCode());
        dataEntity.addQuality(textQualityEntity);
        return dataEntity;
    }

    default Point checkAndAddQualifiersAndValue(QuantityDataEntity dataEntity, Point point, boolean applyRounding) {
        boolean detectionLimitAdded = false;
        if (point.hasQualifiers()) {
            for (Qualifier qualifier : point.getQualifiers()) {
                if (qualifier.getKey().isEquals(QualifierKey.ABOVE)
                        || qualifier.getKey().isEquals(QualifierKey.BELOW)) {
                    addDetectionLimit(dataEntity, point, qualifier, applyRounding);
                    detectionLimitAdded = true;
                } else {
                    addQualifier(dataEntity, qualifier);
                }
            }
        }
        if (!detectionLimitAdded) {
            if (applyRounding && point.isDisplay()) {
                dataEntity.setValue(point.getDisplayAsBigDecimal());
            } else {
                dataEntity.setValue(point.getNumericAsBigDecimal());
            }
        }
        return point;
    }

    default DataEntity<?> addQualifier(QuantityDataEntity dataEntity, Qualifier qualifier) {
        TextQualityEntity textQualityEntity = new TextQualityEntity();
        // textQualityEntity.setIdentifier(qualifier.getIdentifier());
        textQualityEntity.setName(qualifier.getIdentifier());
        textQualityEntity.setDescription(qualifier.getIdentifier());
        textQualityEntity.setValue(qualifier.getIdentifier());
        dataEntity.addQuality(textQualityEntity);
        return dataEntity;
    }

    default DataEntity<?> addDetectionLimit(QuantityDataEntity dataEntity, Point point, Qualifier qualifier,
            boolean applyRounding) {
        DetectionLimitEntity detectionLimitEntity = new DetectionLimitEntity();
        if (applyRounding && point.isDisplay()) {
            detectionLimitEntity.setDetectionLimit(point.getDisplayAsBigDecimal());
        } else {
            detectionLimitEntity.setDetectionLimit(point.getNumericAsBigDecimal());
        }
        if (qualifier.getKey().isEquals(QualifierKey.ABOVE)) {
            detectionLimitEntity.setFlag(Short.valueOf("1"));
        } else if (qualifier.getKey().isEquals(QualifierKey.BELOW)) {
            detectionLimitEntity.setFlag(Short.valueOf("-1"));
        }
        dataEntity.setDetectionLimit(detectionLimitEntity);
        return dataEntity;
    }

    default DataEntity<?> addDefault(DataEntity<?> data, DatasetEntity dataset, DateTime time, Long id) {
        data.setId(id);
        data.setDataset(dataset);
        data.setDatasetId(dataset.getId());
        data.setSamplingTimeStart(time.toDate());
        data.setSamplingTimeEnd(time.toDate());
        addDescribeableData(data, null, null, null, null);
        return data;
    }

    default UnitEntity createUnit(UnitMetadata unit, ServiceEntity service) {
        return createUnit(unit.getIdentifier(), unit.getSymbol(), unit.getDisplayName(), service);
    }

    default UnitEntity createUnit(String unit, ServiceEntity service) {
        return createUnit(unit, unit, null, service);
    }

    default ParameterEntity<?> createParameter(DatasetEntity dataset, String domain, String name, String value) {
        ParameterEntity<?> param = createDatasetParameterEntity(dataset,
                org.n52.series.db.beans.parameter.ParameterFactory.ValueType.TEXT);
        ((TextParameterEntity) param).setValue(value);
        param.setName(name);
        param.setDomain(domain);
        return param;
    }

    default ParameterEntity<?> createDatasetParameterEntity(DatasetEntity dataset,
            org.n52.series.db.beans.parameter.ParameterFactory.ValueType valueType) {
        DatasetParameterEntity<?> param =
                (DatasetParameterEntity<?>) ParameterFactory.from(EntityType.DATASET, valueType);
        param.setDataset(dataset);
        return param;
    }
}
