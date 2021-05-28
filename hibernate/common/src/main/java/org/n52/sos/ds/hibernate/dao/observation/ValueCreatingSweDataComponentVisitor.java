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
package org.n52.sos.ds.hibernate.dao.observation;

import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.elements.SmlPosition;
import org.n52.shetland.ogc.sensorML.v20.SmlDataInterface;
import org.n52.shetland.ogc.sensorML.v20.SmlFeatureOfInterest;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataComponentVisitor;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweEnvelope;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.SweSimpleDataRecord;
import org.n52.shetland.ogc.swe.SweVector;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCategoryRange;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweCountRange;
import org.n52.shetland.ogc.swe.simpleType.SweObservableProperty;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweQuantityRange;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swe.simpleType.SweTime;
import org.n52.shetland.ogc.swe.simpleType.SweTimeRange;
import org.n52.shetland.ogc.swe.stream.StreamingSweDataArray;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public final class ValueCreatingSweDataComponentVisitor
        implements SweDataComponentVisitor<Value<?>, OwsExceptionReport> {

    private ValueCreatingSweDataComponentVisitor() {
    }

    private OwsExceptionReport notSupported(SweAbstractDataComponent component) throws OwsExceptionReport {
        throw new NoApplicableCodeException().withMessage(
                "SweDataComponent {} is not supported as an observation value", component.getDataComponentType());
    }

    @Override
    public Value<?> visit(SweField component) throws OwsExceptionReport {
        return component.getElement().accept(this);
    }

    @Override
    public Value<?> visit(SweDataRecord component) {
        return new ComplexValue(component);
    }

    @Override
    public Value<?> visit(SweSimpleDataRecord component) {
        return new ComplexValue(component);
    }

    @Override
    public Value<?> visit(SweDataArray component) {
        return new SweDataArrayValue(component);
    }

    @Override
    public Value<?> visit(SweCount component) {
        return new CountValue(component.getValue());
    }

    @Override
    public Value<?> visit(SweBoolean component) {
        return new BooleanValue(component.getValue());
    }

    @Override
    public Value<?> visit(SweCategory component) {
        return new CategoryValue(component.getValue(), component.getUom());
    }

    @Override
    public Value<?> visit(SweQuantity component) {
        return new QuantityValue(component.getValue(), component.getUom());
    }

    @Override
    public Value<?> visit(SweText component) {
        return new TextValue(component.getValue());
    }

    @Override
    public Value<?> visit(SweObservableProperty component)
            throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SweQuantityRange component)
            throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SweCountRange component)
            throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SweCategoryRange component)
            throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SweTime component)
            throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SweTimeRange component)
            throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SweEnvelope component)
            throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SweVector component)
            throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SmlPosition component)
            throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SmlDataInterface component) throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(SmlFeatureOfInterest component) throws OwsExceptionReport {
        throw notSupported(component);
    }

    @Override
    public Value<?> visit(StreamingSweDataArray component) throws OwsExceptionReport {
        throw notSupported(component);
    }

    public static ValueCreatingSweDataComponentVisitor getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ValueCreatingSweDataComponentVisitor INSTANCE
                = new ValueCreatingSweDataComponentVisitor();

        private Holder() {
        }
    }

}
