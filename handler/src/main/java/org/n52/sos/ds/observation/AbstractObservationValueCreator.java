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
package org.n52.sos.ds.observation;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import org.n52.series.db.beans.BlobDataEntity;
import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DetectionLimitEntity;
import org.n52.series.db.beans.GeometryDataEntity;
import org.n52.series.db.beans.HibernateRelations.HasObservablePropertyGetter;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.ReferencedDataEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.series.db.beans.TrajectoryDataEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.quality.QualityEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.series.tsml.TimeseriesMLConstants;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.ReferenceValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.shetland.ogc.swe.simpleType.SweQuality;
import org.n52.shetland.ogc.swe.simpleType.SweQualityHolder;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;

public abstract class AbstractObservationValueCreator extends AbstractValuedObservationCreator<Value<?>> {

    private static final ObservationQualityVisitorCreator QUALITY_CREATOR = new ObservationQualityVisitorCreator();

    public AbstractObservationValueCreator(ObservationHelper observationHelper) {
        super(observationHelper);
    }

    public AbstractObservationValueCreator(ObservationHelper observationHelper, boolean noValues) {
        super(observationHelper, noValues);
    }

    @SuppressWarnings("rawtypes")
    protected void addAdditonalDataEntity(DataEntity d, SweAbstractSimpleType v) {
        if (d.hasValueIdentifier()) {
            v.setIdentifier(d.getValueIdentifier());
        }
        if (d.hasValueName()) {
            v.setName(d.getValueName());
        }
        if (d.hasValueDescription()) {
            v.setDescription(d.getValueDescription());
        }
        if (d.hasQuality()) {
            SweQualityHolder holder = v.isSetQuality() ? v.getQuality() : new SweQualityHolder();
            for (QualityEntity quality : (Set<QualityEntity<?>>) d.getQuality()) {
                holder.addQuality(QUALITY_CREATOR.visit(quality));
            }
            v.setQuality(holder);
        }
    }

    @SuppressWarnings("rawtypes")
    protected void addDefinitionFromObservableProperty(DataEntity o, SweAbstractSimpleType v) {
        if (o instanceof HasObservablePropertyGetter getter) {
            if (getter.getObservableProperty() != null) {
                v.setDefinition(getter.getObservableProperty().getIdentifier());
            }
        }
    }

    protected UoM getUnit(UnitEntity unit) {
        UoM uom = new UoM(unit.getUnit());
        return uom;
    }

    @Override
    public Value<?> visit(DataEntity o) throws OwsExceptionReport {
        if (o instanceof QuantityDataEntity entity11) {
            return visit(entity11);
        } else if (o instanceof BlobDataEntity entity10) {
            return visit(entity10);
        } else if (o instanceof BooleanDataEntity entity9) {
            return visit(entity9);
        } else if (o instanceof CategoryDataEntity entity8) {
            return visit(entity8);
        } else if (o instanceof ComplexDataEntity entity7) {
            return visit(entity7);
        } else if (o instanceof CountDataEntity entity6) {
            return visit(entity6);
        } else if (o instanceof GeometryDataEntity entity5) {
            return visit(entity5);
        } else if (o instanceof TextDataEntity entity4) {
            return visit(entity4);
        } else if (o instanceof DataArrayDataEntity entity3) {
            return visit(entity3);
        } else if (o instanceof ProfileDataEntity entity2) {
            return visit(entity2);
        } else if (o instanceof TrajectoryDataEntity entity1) {
            return visit(entity1);
        } else if (o instanceof ReferencedDataEntity entity) {
            return visit(entity);
        }
        return null;
    }

    public QuantityValue visit(QuantityDataEntity o, QuantityValue v) {
        addAdditonalDataEntity(o, v);
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        checkDetectionLimit(v, o);
        return v;
    }

    public UnknownValue visit(BlobDataEntity o, UnknownValue v) {
        addAdditonalDataEntity(o, null);
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        checkDetectionLimit(v, o);
        return v;
    }

    public BooleanValue visit(BooleanDataEntity o, BooleanValue v) {
        addAdditonalDataEntity(o, v);
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        checkDetectionLimit(v, o);
        return v;
    }

    public CategoryValue visit(CategoryDataEntity o, CategoryValue v) {
        addAdditonalDataEntity(o, v);
        addDefinitionFromObservableProperty(o, v);
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        checkDetectionLimit(v, o);
        return v;
    }

    public GeometryValue visit(GeometryDataEntity o, GeometryValue v) throws OwsExceptionReport {
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        checkDetectionLimit(v, o);
        return v;
    }

    public TextValue visit(TextDataEntity o, TextValue v) {
        addAdditonalDataEntity(o, v);
        addDefinitionFromObservableProperty(o, v);
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        checkDetectionLimit(v, o);
        return v;
    }

    public ReferenceValue visit(ReferencedDataEntity o, ReferenceValue v) {
        if (o.hasValueName()) {
            v.getValue().setTitle(o.getValueName());
        }
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        checkDetectionLimit(v, o);
        return v;
    }

    public void checkDetectionLimit(Value<?> value, OmObservation observation, String responseFormat) {
        if (!checkResponseFormat(responseFormat) && value != null && value instanceof SweAbstractSimpleType<?> type
                && type.isSetQuality()) {
            observation.addParameter(createDetectionLimitNamedValue(type.getQuality()));
        }
    }

    private void checkDetectionLimit(Value<?> value, DataEntity<?> o) {
        if (value instanceof SweAbstractSimpleType<?> type && o.hasDetectionLimit()) {
            type.setQuality(createDetectionLimitQuality(
                    type.getQuality(), o.getDetectionLimit(), value));
        }
    }

    private NamedValue<?> createDetectionLimitNamedValue(SweQualityHolder quality) {
        final NamedValue<BigDecimal> namedValue = new NamedValue<>();
        if (quality.getReferences().containsKey(WaterMLConstants.EN_CENSORED_REASON)) {
            namedValue.setName(quality.getReferences().get(WaterMLConstants.EN_CENSORED_REASON));
        } else {
            return null;
        }
        Optional<SweQuality> qual = quality.getQuality().stream().filter(q -> q instanceof SweQuantity).findFirst();
        if (qual.isPresent()) {
            SweQuality sweQuality = qual.get();
            if (sweQuality instanceof SweQuantity quantity) {
                namedValue.setValue(new QuantityValue(quantity.getValue(),
                        quantity.getUomObject()));
            }
        } else {
            return null;
        }
        return namedValue;
    }

    private SweQualityHolder createDetectionLimitQuality(SweQualityHolder sweQualityHolder,
            DetectionLimitEntity detectionLimit, Value<?> value) {
        SweQualityHolder holder = sweQualityHolder != null ? sweQualityHolder : new SweQualityHolder();
        if (value instanceof SweQuantity) {
            SweQuantity quantity = new SweQuantity(detectionLimit.getDetectionLimit(), value.getUnitObject());
            ReferenceType reference = new ReferenceType();
            checkDetectionLimitDefinitions(detectionLimit, quantity, reference);
            holder.addQuality(quantity);
            holder.addReference(WaterMLConstants.EN_CENSORED_REASON, reference);
        } else {
            holder.addQuality(new TextValue((String) null));
        }
        return holder;
    }

    private void checkDetectionLimitDefinitions(DetectionLimitEntity detectionLimit, SweQuantity quantity,
            ReferenceType reference) {
        if (detectionLimit.getFlag() > 0) {
            quantity.setDefinition(getObservationHelper().getQualifierDefinitionAbove());
            quantity.setDescription(getObservationHelper().getQualifierDescriptionAbove());
            reference.setHref(getObservationHelper().getCensoredReasonHrefAbove());
            reference.setTitle(getObservationHelper().getCensoredReasonTitleAbove());
        } else {
            quantity.setDefinition(getObservationHelper().getQualifierDefinitionBelow());
            quantity.setDescription(getObservationHelper().getQualifierDescriptionBelow());
            reference.setHref(getObservationHelper().getCensoredReasonHrefBelow());
            reference.setTitle(getObservationHelper().getCensoredReasonTitleBelow());
        }
    }

    private boolean checkResponseFormat(String responseFormat) {
        return responseFormat != null && WaterMLConstants.NS_WML_20.endsWith(responseFormat)
                || TimeseriesMLConstants.NS_TSML_10.equals(responseFormat);
    }
}
