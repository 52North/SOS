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
package org.n52.sos.ds.hibernate.util.observation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.n52.janmayen.NcName;
import org.n52.series.db.beans.BlobDataEntity;
import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.GeometryDataEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.ReferencedDataEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweAbstractDataRecord;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.util.JavaHelper;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.util.CodingHelper;
import org.n52.svalbard.util.XmlHelper;

/**
 * {@code ValuedObservationVisitor} to create {@link SweAbstractDataComponent}
 * from observations.
 *
 * @author Christian Autermann
 */
public class SweAbstractDataComponentCreator
        implements ValuedObservationVisitor<SweAbstractDataComponent> {

    private DecoderRepository decoderRepository;

    public SweAbstractDataComponentCreator(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    public SweAbstractDataComponent visit(DataEntity o) throws OwsExceptionReport {
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

    @Override
    public SweAbstractDataComponent visit(GeometryDataEntity o)
            throws OwsExceptionReport {
        // TODO implement SweEnvelope/SweCoordinte etc.
        throw notSupported(o);
    }

    @Override
    public SweAbstractDataComponent visit(BlobDataEntity o)
            throws OwsExceptionReport {
        throw notSupported(o);
    }

    @Override
    public SweQuantity visit(QuantityDataEntity o) throws CodedException {
        SweQuantity component = new SweQuantity();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweBoolean visit(BooleanDataEntity o) throws CodedException {
        SweBoolean component = new SweBoolean();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweCategory visit(CategoryDataEntity o) throws CodedException {
        SweCategory component = new SweCategory();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweDataRecord visit(ComplexDataEntity o)
            throws OwsExceptionReport {
        SweDataRecord record = new SweDataRecord();
        for (DataEntity<?> sub : o.getValue()) {
            String fieldName = getFieldName(sub);
            record.addField(new SweField(fieldName, this.visit(sub)));
        }
        return setCommonValues(record, o);
    }

    @Override
    public SweCount visit(CountDataEntity o) throws CodedException {
        SweCount component = new SweCount();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweText visit(TextDataEntity o)
            throws OwsExceptionReport {
        SweText component = new SweText();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweDataArray visit(DataArrayDataEntity o) throws OwsExceptionReport {
        try {
            SweDataArray array = new SweDataArray();
            decode(XmlHelper.parseXmlString(o.getResultTemplate().getEncoding()));
            array.setEncoding(
                    (SweAbstractEncoding) decode(XmlHelper.parseXmlString(o.getResultTemplate().getEncoding())));
            array.setElementType(
                    (SweAbstractDataComponent) decode(XmlHelper.parseXmlString(o.getResultTemplate().getStructure())));
            if (o.isSetStringValue()) {
                array.setXml(null);
                List<List<String>> values = new LinkedList<>();
                for (String block : o.getStringValue()
                        .split(((SweTextEncoding) array.getEncoding()).getBlockSeparator())) {
                    List<String> v = new LinkedList<>();
                    for (String value : block.split(((SweTextEncoding) array.getEncoding()).getTokenSeparator())) {
                        v.add(value);
                    }
                    values.add(v);
                }
                array.setValues(values);
            } else if (o.getValue() != null && !o.getValue().isEmpty()) {
                int i = ((SweAbstractDataRecord) array.getElementType())
                        .getFieldIndexByIdentifier(o.getDataset().getPhenomenon().getIdentifier()) == 0 ? 1 : 0;
                List<List<String>> values = new LinkedList<>();
                for (DataEntity<?> v : o.getValue()) {
                    List<String> value = new LinkedList<>();
                    if (i == 0) {
                        value.add(v.getDataset().getPhenomenon().getName());
                        value.add(JavaHelper.asString(v.getValue()));
                    } else {
                        value.add(JavaHelper.asString(v.getValue()));
                        value.add(v.getDataset().getPhenomenon().getName());
                    }
                    values.add(value);
                }
                array.setValues(values);
            }
            return array;
        } catch (DecodingException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while creating SweDataArray from database entity!");
        }
    }

    @Override
    public SweAbstractDataComponent visit(ProfileDataEntity o) throws OwsExceptionReport {
        return new ProfileGeneratorSplitter(new ObservationValueCreator(decoderRepository)).createValue(o);
    }

    @Override
    public SweCategory visit(ReferencedDataEntity o) throws OwsExceptionReport {
        SweCategory component = new SweCategory();
        if (o.hasValue()) {
            component.setValue(o.getValue());
        } else if (o.hasValueName()) {
            component.setValue(o.getValueName());
        }
        return setCommonValues(component, o);
    }

    protected Object decode(XmlObject xml) throws DecodingException {
        return decoderRepository.getDecoder(CodingHelper.getDecoderKey(xml)).decode(xml);
    }

    protected String getFieldName(DataEntity<?> sub) {
        String name = sub.getDataset().getObservableProperty().getName();
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return NcName.makeValid(sub.getDataset().getObservableProperty().getIdentifier());
    }

    protected <T extends SweAbstractDataComponent> T setCommonValues(
            T component, DataEntity<?> valuedObservation) throws CodedException {
        if (valuedObservation != null) {
            PhenomenonEntity op = valuedObservation.getDataset().getPhenomenon();
            component.setIdentifier(op.getIdentifier());
            component.setDefinition(op.getIdentifier());
            component.setDescription(op.getDescription());
            if (op.isSetNameCodespace()) {
                String codespace = op.getNameCodespace().getName();
                try {
                    component.setName(new CodeType(op.getName(), new URI(codespace)));
                } catch (URISyntaxException e) {
                    throw new NoApplicableCodeException().causedBy(e).withMessage(
                            "Error while creating URI from '{}'", codespace);
                }
            } else {
                component.setName(op.getName());
            }

            if (valuedObservation.getDataset().hasUnit() &&
                component instanceof SweAbstractUomType) {
                SweAbstractUomType<?> uomType = (SweAbstractUomType<?>) component;
                uomType.setUom(valuedObservation.getDataset().getUnit().getUnit());
            }
        }
        return component;
    }

    protected OwsExceptionReport notSupported(DataEntity<?> o) {
        return new NoApplicableCodeException()
                .withMessage("Complex observation fields of type %s" +
                             " are currently not supported", o.getValue());
    }

}
