/**
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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservablePropertyGetter;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.observation.ProfileGeneratorSplitter;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.BlobValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.BooleanValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.CategoryValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.GeometryValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.NumericValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.ReferenceValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.TextValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.BlobValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.BooleanValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CategoryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ComplexValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CountValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.GeometryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.IdentifierNamDescription;
import org.n52.sos.ds.hibernate.entities.observation.valued.NumericValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ProfileValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ReferenceValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.SweDataArrayValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.TextValuedObservation;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ObservationValueCreator
        implements ValuedObservationVisitor<Value<?>> {

    @Override
    public QuantityValue visit(NumericValuedObservation o) {
        QuantityValue v = new QuantityValue(o.getValue());
        if (!addUnit(o, v) && o instanceof NumericValuedSeriesObservation && ((NumericValuedSeriesObservation) o).getSeries().isSetUnit()) {
            v.setUnit(getUnit(((NumericValuedSeriesObservation) o).getSeries().getUnit()));
        }
        return v;
    }


    @Override
    public UnknownValue visit(BlobValuedObservation o) {
        UnknownValue v = new UnknownValue(o.getValue());
        if (!addUnit(o, v) && o instanceof BlobValuedSeriesObservation && ((BlobValuedSeriesObservation) o).getSeries().isSetUnit()) {
            v.setUnit(getUnit(((BlobValuedSeriesObservation) o).getSeries().getUnit()));
        }
        return v;
    }

    @Override
    public BooleanValue visit(BooleanValuedObservation o) {
        BooleanValue v = new BooleanValue(o.getValue());
        if (!addUnit(o, v) && o instanceof BooleanValuedSeriesObservation && ((BooleanValuedSeriesObservation) o).getSeries().isSetUnit()) {
            v.setUnit(getUnit(((BooleanValuedSeriesObservation) o).getSeries().getUnit()));
        }
        return v;
    }

    @Override
    public CategoryValue visit(CategoryValuedObservation o) {
        CategoryValue v = new CategoryValue(o.getValue());
        addAdditonalData(o, v);
        addDefinitionFromObservableProperty(o, v);
        if (!addUnit(o, v) && o instanceof CategoryValuedSeriesObservation && ((CategoryValuedSeriesObservation) o).getSeries().isSetUnit()) {
            v.setUnit(getUnit(((CategoryValuedSeriesObservation) o).getSeries().getUnit()));
        }
        return v;
    }

    @Override
    public ComplexValue visit(ComplexValuedObservation o)
            throws OwsExceptionReport {
        SweAbstractDataComponentCreator visitor
                = new SweAbstractDataComponentCreator();
        SweDataRecord record = visitor.visit(o);
        return new ComplexValue(record);
    }

    @Override
    public CountValue visit(CountValuedObservation o) {
        return new CountValue(o.getValue());
    }

    @Override
    public GeometryValue visit(GeometryValuedObservation o) {
        GeometryValue v = new GeometryValue(o.getValue());
        if (!addUnit(o, v) && o instanceof GeometryValuedSeriesObservation && ((GeometryValuedSeriesObservation) o).getSeries().isSetUnit()) {
            v.setUnit(getUnit(((GeometryValuedSeriesObservation) o).getSeries().getUnit()));
        }
        return v;
    }

    @Override
    public TextValue visit(TextValuedObservation o) {
        TextValue v = new TextValue(o.getValue());
        addAdditonalData(o, v);
        addDefinitionFromObservableProperty(o, v);
        if (!addUnit(o, v) && o instanceof TextValuedSeriesObservation && ((TextValuedSeriesObservation) o).getSeries().isSetUnit()) {
            v.setUnit(getUnit(((TextValuedSeriesObservation) o).getSeries().getUnit()));
        }
        return v;
    }

    @Override
    public SweDataArrayValue visit(SweDataArrayValuedObservation o)
            throws OwsExceptionReport {
        XmlObject xml = XmlHelper.parseXmlString(o.getStructure());
        SweDataArray array = (SweDataArray) CodingHelper.decodeXmlElement(xml);
        array.setXml(null);
        List<List<String>> values = new LinkedList<>();
        for (String block : o.getValue().split(((SweTextEncoding)array.getEncoding()).getBlockSeparator())) {
            List<String> v = new LinkedList<>();
            for (String value : block.split(((SweTextEncoding)array.getEncoding()).getTokenSeparator())) {
                v.add(value);
            }
            values.add(v);
        }
        array.setValues(values);
        return new SweDataArrayValue(array);
    }

    @Override
    public ProfileValue visit(ProfileValuedObservation o) throws OwsExceptionReport {
        return ProfileGeneratorSplitter.create(o);
    }

    @Override
    public ReferenceValue visit(ReferenceValuedObservation o) {
        ReferenceValue v = new ReferenceValue(o.getValue());
        if (!addUnit(o, v) && o instanceof ReferenceValuedSeriesObservation && ((ReferenceValuedSeriesObservation) o).getSeries().isSetUnit()) {
            v.setUnit(getUnit(((ReferenceValuedSeriesObservation) o).getSeries().getUnit()));
        }
        return v;
    }

    @SuppressWarnings("rawtypes")
    protected void addAdditonalData(IdentifierNamDescription o, SweAbstractSimpleType v) {
        if (o.isSetValueIdentifier()) {
            v.setIdentifier(o.getValueIdentifier());
        }
        if (o.isSetValueName()) {
            v.setName(o.getValueName());
        }
        if (o.isSetValueDescription()) {
            v.setDescription(o.getValueDescription());
        }
    }
    
    @SuppressWarnings("rawtypes")
    protected void addDefinitionFromObservableProperty(ValuedObservation o, SweAbstractSimpleType v) {
        if (o instanceof HasObservablePropertyGetter) {
            if (((HasObservablePropertyGetter)o).getObservableProperty() != null) {
                v.setDefinition(((HasObservablePropertyGetter)o).getObservableProperty().getIdentifier());
            }
        }
    }

    protected boolean addUnit(ValuedObservation<?> o, Value<?> v) {
        if (!v.isSetUnit() && o.isSetUnit()) {
            v.setUnit(getUnit(o.getUnit()));
            return true;
        }
        return false;
    }


    protected UoM getUnit(Unit unit) {
        UoM uom = new UoM(unit.getUnit());
        return uom;
    }

}
