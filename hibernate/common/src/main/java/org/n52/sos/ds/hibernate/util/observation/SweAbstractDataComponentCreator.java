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

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.janmayen.NcNameResolver;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.valued.BlobValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.BooleanValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CategoryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ComplexValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CountValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.GeometryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.NumericValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.SweDataArrayValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.TextValuedObservation;
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

    @Inject
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    @Override
    public SweAbstractDataComponent visit(GeometryValuedObservation o)
            throws OwsExceptionReport {
        // TODO implement SweEnvelope/SweCoordinte etc.
        throw notSupported(o);
    }

    @Override
    public SweAbstractDataComponent visit(BlobValuedObservation o)
            throws OwsExceptionReport {
        throw notSupported(o);
    }

    @Override
    public SweQuantity visit(NumericValuedObservation o) throws CodedException {
        SweQuantity component = new SweQuantity();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweBoolean visit(BooleanValuedObservation o) throws CodedException {
        SweBoolean component = new SweBoolean();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweCategory visit(CategoryValuedObservation o) throws CodedException {
        SweCategory component = new SweCategory();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweDataRecord visit(ComplexValuedObservation o)
            throws OwsExceptionReport {
        SweDataRecord record = new SweDataRecord();
        for (Observation<?> sub : o.getValue()) {
            String fieldName = getFieldName(sub);
            record.addField(new SweField(fieldName, sub.accept(this)));
        }
        return setCommonValues(record, o);
    }

    protected String getFieldName(Observation<?> sub) {
        String name = sub.getObservableProperty().getName();
        if (name != null && !name.isEmpty()) { return name; }
        return NcNameResolver.fixNcName(sub.getObservableProperty().getIdentifier());
    }

    @Override
    public SweCount visit(CountValuedObservation o) throws CodedException {
        SweCount component = new SweCount();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweText visit(TextValuedObservation o)
            throws OwsExceptionReport {
        SweText component = new SweText();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweDataArray visit(SweDataArrayValuedObservation o)
            throws OwsExceptionReport {
        try {
            XmlObject xml = XmlHelper.parseXmlString(o.getValue());
            return (SweDataArray) decoderRepository.getDecoder(CodingHelper.getDecoderKey(xml)).decode(xml);
        } catch (DecodingException ex) {
            throw new NoApplicableCodeException().causedBy(ex);
        }
    }

    protected <T extends SweAbstractDataComponent> T setCommonValues(
            T component, ValuedObservation<?> valuedObservation) throws CodedException {

        if (valuedObservation instanceof ContextualReferencedObservation) {
            ContextualReferencedObservation observation
                    = (ContextualReferencedObservation) valuedObservation;
            ObservableProperty op = observation.getObservableProperty();
            component.setIdentifier(op.getIdentifier());
            component.setDefinition(op.getIdentifier());
            component.setDescription(op.getDescription());
            if (op.getCodespace() != null) {
                String codespace = op.getCodespaceName().getCodespace();
                try {
                    component.setName(new CodeType(op.getName(), new URI(codespace)));
                } catch (URISyntaxException e) {
                    throw new NoApplicableCodeException().causedBy(e).withMessage(
                            "Error while creating URI from '{}'", codespace);
                }
            } else {
                component.setName(op.getName());
            }
        }

        if (valuedObservation.getUnit() != null &&
            component instanceof SweAbstractUomType) {
            SweAbstractUomType<?> uomType = (SweAbstractUomType) component;
            uomType.setUom(valuedObservation.getUnit().getUnit());
        }
        return component;
    }

    protected OwsExceptionReport notSupported(ValuedObservation<?> o) {
        return new NoApplicableCodeException()
                .withMessage("Complex observation fields of type %s" +
                             " are currently not supported", o.getValue());
    }

}
