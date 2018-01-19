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
package org.n52.sos.ds.hibernate.util.observation;

import javax.inject.Inject;

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
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.ReferenceValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.util.JTSConverter;
import org.n52.svalbard.decode.DecoderRepository;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ObservationValueCreator implements ValuedObservationVisitor<Value<?>> {

    private DecoderRepository decoderRepository;

    @Inject
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    @Override
    public Value<?> visit(DataEntity o)
            throws OwsExceptionReport {
       if (o instanceof QuantityDataEntity) {
           return visit((QuantityDataEntity)o);
       } else if (o instanceof BlobDataEntity) {
           return visit((BlobDataEntity)o);
       } else if (o instanceof BooleanDataEntity) {
           return visit((BooleanDataEntity)o);
       } else if (o instanceof CategoryDataEntity) {
           return visit((CategoryDataEntity)o);
       } else if (o instanceof ComplexDataEntity) {
           return visit((ComplexDataEntity)o);
       } else if (o instanceof CountDataEntity) {
           return visit((CountDataEntity)o);
       } else if (o instanceof GeometryDataEntity) {
           return visit((GeometryDataEntity)o);
       } else if (o instanceof TextDataEntity) {
           return visit((TextDataEntity)o);
       } else if (o instanceof DataArrayDataEntity) {
           return visit((DataArrayDataEntity)o);
       } else if (o instanceof ProfileDataEntity) {
           return visit((ProfileDataEntity)o);
       } else if (o instanceof ReferencedDataEntity) {
           return visit((ReferencedDataEntity)o);
       }
        return null;
    }

    @Override
    public QuantityValue visit(QuantityDataEntity o) {
        QuantityValue v = new QuantityValue(o.getValue().doubleValue());
        if (o.getDataset().hasUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }


    @Override
    public UnknownValue visit(BlobDataEntity o) {
        UnknownValue v = new UnknownValue(o.getValue());
        if (o.getDataset().hasUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    @Override
    public BooleanValue visit(BooleanDataEntity o) {
        BooleanValue v = new BooleanValue(o.getValue());
        if (o.getDataset().hasUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    @Override
    public CategoryValue visit(CategoryDataEntity o) {
        CategoryValue v = new CategoryValue(o.getValue());
        addAdditonalData(o, v);
        addDefinitionFromObservableProperty(o, v);
        if (o.getDataset().hasUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    @Override
    public ComplexValue visit(ComplexDataEntity o) throws OwsExceptionReport {
        SweAbstractDataComponentCreator visitor
                = new SweAbstractDataComponentCreator();
        SweDataRecord record = visitor.visit(o);
        return new ComplexValue(record);
    }

    @Override
    public CountValue visit(CountDataEntity o) {
        return new CountValue(o.getValue());
    }

    @Override
    public GeometryValue visit(GeometryDataEntity o) throws OwsExceptionReport {
        GeometryValue v = new GeometryValue(JTSConverter.convert(o.getValue().getGeometry()));
        if (o.getDataset().hasUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    @Override
    public TextValue visit(TextDataEntity o) {
        TextValue v = new TextValue(o.getValue());
        addAdditonalData(o, v);
        addDefinitionFromObservableProperty(o, v);
        if (o.getDataset().hasUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    @Override
    public SweDataArrayValue visit(DataArrayDataEntity o)
            throws OwsExceptionReport {
        SweDataArray array = new SweDataArray();
        // TODO
        return new SweDataArrayValue(array);
    }

    @Override
    public ProfileValue visit(ProfileDataEntity o) throws OwsExceptionReport {
        return ProfileGeneratorSplitter.create(o);
    }

    @Override
    public ReferenceValue visit(ReferencedDataEntity o) {
        ReferenceValue v = new ReferenceValue(new ReferenceType(o.getValue()));
        if (o.hasValueName()) {
            v.getValue().setTitle(o.getValueName());
        }
        if (o.getDataset().hasUnit()) {
            v.setUnit(getUnit(o.getDataset().getUnit()));
        }
        return v;
    }

    @SuppressWarnings("rawtypes")
    protected void addAdditonalData(DataEntity o, SweAbstractSimpleType v) {
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
            if (((HasObservablePropertyGetter)o).getObservableProperty() != null) {
                v.setDefinition(((HasObservablePropertyGetter)o).getObservableProperty().getIdentifier());
            }
        }
    }

    protected UoM getUnit(UnitEntity unit) {
        UoM uom = new UoM(unit.getUnit());
        return uom;
    }

}
