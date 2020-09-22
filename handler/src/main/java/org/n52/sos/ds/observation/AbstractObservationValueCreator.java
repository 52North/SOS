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

import org.n52.series.db.beans.BlobDataEntity;
import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.GeometryDataEntity;
import org.n52.series.db.beans.HibernateRelations.HasObservablePropertyGetter;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.ReferencedDataEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.shetland.ogc.UoM;
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
import org.n52.svalbard.decode.DecoderRepository;

public abstract class AbstractObservationValueCreator extends AbstractValuedObservationCreator<Value<?>> {

    public AbstractObservationValueCreator(DecoderRepository decoderRepository) {
        super(decoderRepository);
    }

    public AbstractObservationValueCreator(DecoderRepository decoderRepository, boolean noValues) {
        super(decoderRepository, noValues);
    }

    @SuppressWarnings("rawtypes")
    protected void addAdditonalDataEntity(DataEntity o, SweAbstractSimpleType v) {
        if (o.hasValueIdentifier()) {
            v.setIdentifier(o.getValueIdentifier());
        }
        if (o.hasValueName()) {
            v.setName(o.getValueName());
        }
        if (o.hasValueDescription()) {
            v.setDescription(o.getValueDescription());
        }
    }

    @SuppressWarnings("rawtypes")
    protected void addDefinitionFromObservableProperty(DataEntity o, SweAbstractSimpleType v) {
        if (o instanceof HasObservablePropertyGetter) {
            if (((HasObservablePropertyGetter) o).getObservableProperty() != null) {
                v.setDefinition(((HasObservablePropertyGetter) o).getObservableProperty().getIdentifier());
            }
        }
    }

    protected UoM getUnit(UnitEntity unit) {
        UoM uom = new UoM(unit.getUnit());
        return uom;
    }

    @Override
    public Value<?> visit(DataEntity o) throws OwsExceptionReport {
        if (o instanceof QuantityDataEntity) {
            return visit((QuantityDataEntity) o);
        } else if (o instanceof BlobDataEntity) {
            return visit((BlobDataEntity) o);
        } else if (o instanceof BooleanDataEntity) {
            return visit((BooleanDataEntity) o);
        } else if (o instanceof CategoryDataEntity) {
            return visit((CategoryDataEntity) o);
        } else if (o instanceof ComplexDataEntity) {
            return visit((ComplexDataEntity) o);
        } else if (o instanceof CountDataEntity) {
            return visit((CountDataEntity) o);
        } else if (o instanceof GeometryDataEntity) {
            return visit((GeometryDataEntity) o);
        } else if (o instanceof TextDataEntity) {
            return visit((TextDataEntity) o);
        } else if (o instanceof DataArrayDataEntity) {
            return visit((DataArrayDataEntity) o);
        } else if (o instanceof ProfileDataEntity) {
            return visit((ProfileDataEntity) o);
        } else if (o instanceof ReferencedDataEntity) {
            return visit((ReferencedDataEntity) o);
        }
        return null;
    }

    public QuantityValue visit(QuantityDataEntity o, QuantityValue v) {
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    public UnknownValue visit(BlobDataEntity o, UnknownValue v) {
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    public BooleanValue visit(BooleanDataEntity o, BooleanValue v) {
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    public CategoryValue visit(CategoryDataEntity o, CategoryValue v) {
        addAdditonalDataEntity(o, v);
        addDefinitionFromObservableProperty(o, v);
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    public GeometryValue visit(GeometryDataEntity o, GeometryValue v) throws OwsExceptionReport {
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    public TextValue visit(TextDataEntity o, TextValue v) {
        addAdditonalDataEntity(o, v);
        addDefinitionFromObservableProperty(o, v);
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    public ReferenceValue visit(ReferencedDataEntity o, ReferenceValue v) {
        if (o.hasValueName()) {
            v.getValue().setTitle(o.getValueName());
        }
        if (o.getDataset().isSetUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }
}
