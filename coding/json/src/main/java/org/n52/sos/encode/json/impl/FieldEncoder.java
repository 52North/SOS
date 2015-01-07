/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode.json.impl;

import static org.n52.sos.util.DateTimeHelper.formatDateTime2IsoString;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweCountRange;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.util.DateTimeHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class FieldEncoder extends JSONEncoder<SweField> {
    public FieldEncoder() {
        super(SweField.class);
    }

    @Override
    public JsonNode encodeJSON(SweField field) throws OwsExceptionReport {
        switch (field.getElement().getDataComponentType()) {
        case Count:
            return encodeSweCountField(field);
        case Boolean:
            return encodeSweBooleanField(field);
        case CountRange:
            return encodeSweCountRangeField(field);
        case ObservableProperty:
            return encodeSweObservableProperyField(field);
        case Text:
            return encodeSweTextField(field);
        case Quantity:
            return encodeSweQuantityField(field);
        case QuantityRange:
            return encodeSweQuantityRangeField(field);
        case Time:
            return encodeSweTimeField(field);
        case TimeRange:
            return encodeSweTimeRangeField(field);
        case Category:
            return encodeSweCategoryField(field);
        default:
            throw new UnsupportedEncoderInputException(this, field);
        }
    }

    private ObjectNode createField(SweField field) {
        ObjectNode jfield = nodeFactory().objectNode();
        jfield.put(JSONConstants.NAME, field.getName().getValue());
        SweAbstractDataComponent element = field.getElement();
        if (element.isSetDefinition()) {
            jfield.put(JSONConstants.DEFINITION, element.getDefinition());
        }
        if (element.isSetDescription()) {
            jfield.put(JSONConstants.DESCRIPTION, element.getDescription());
        }
        if (element.isSetIdentifier()) {
            jfield.put(JSONConstants.IDENTIFIER, element.getIdentifier());
        }
        if (element.isSetLabel()) {
            jfield.put(JSONConstants.LABEL, element.getLabel());
        }
        return jfield;
    }

    private ObjectNode encodeSweCountField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.COUNT_TYPE);
        SweCount sweCount = (SweCount) field.getElement();
        if (sweCount.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweCount.getValue());
        }
        return jfield;
    }

    private ObjectNode encodeSweBooleanField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.BOOLEAN_TYPE);
        SweBoolean sweBoolean = (SweBoolean) field.getElement();
        if (sweBoolean.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweBoolean.getValue());
        }
        return jfield;
    }

    private ObjectNode encodeSweCountRangeField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.COUNT_RANGE_TYPE);
        SweCountRange sweCountRange = (SweCountRange) field.getElement();
        if (sweCountRange.isSetValue()) {
            ArrayNode av = jfield.putArray(JSONConstants.VALUE);
            av.add(sweCountRange.getValue().getRangeStart());
            av.add(sweCountRange.getValue().getRangeEnd());
        }
        return jfield;
    }

    private ObjectNode encodeSweObservableProperyField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.OBSERVABLE_PROPERTY_TYPE);
        SweObservableProperty sweObservableProperty = (SweObservableProperty) field.getElement();
        if (sweObservableProperty.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweObservableProperty.getValue());
        }
        return jfield;
    }

    private ObjectNode encodeSweTextField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.TEXT_TYPE);
        SweText sweText = (SweText) field.getElement();
        if (sweText.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweText.getValue());
        }
        return jfield;
    }

    private ObjectNode encodeSweQuantityField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.QUANTITY_TYPE);
        SweQuantity sweQuantity = (SweQuantity) field.getElement();
        if (sweQuantity.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweQuantity.getValue());
        }
        jfield.put(JSONConstants.UOM, sweQuantity.getUom());
        return jfield;
    }

    private ObjectNode encodeSweQuantityRangeField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.QUANTITY_RANGE_TYPE);
        SweQuantityRange sweQuantityRange = (SweQuantityRange) field.getElement();
        jfield.put(JSONConstants.UOM, sweQuantityRange.getUom());
        if (sweQuantityRange.isSetValue()) {
            ArrayNode av = jfield.putArray(JSONConstants.VALUE);
            av.add(sweQuantityRange.getValue().getRangeStart());
            av.add(sweQuantityRange.getValue().getRangeEnd());
        }
        return jfield;
    }

    private ObjectNode encodeSweTimeField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.TIME_TYPE);
        SweTime sweTime = (SweTime) field.getElement();
        jfield.put(JSONConstants.UOM, sweTime.getUom());
        if (sweTime.isSetValue()) {
            jfield.put(JSONConstants.VALUE, formatDateTime2IsoString(sweTime.getValue()));
        }
        return jfield;
    }

    private ObjectNode encodeSweTimeRangeField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.TIME_RANGE_TYPE);
        SweTimeRange sweTimeRange = (SweTimeRange) field.getElement();
        jfield.put(JSONConstants.UOM, sweTimeRange.getUom());
        if (sweTimeRange.isSetValue()) {
            ArrayNode av = jfield.putArray(JSONConstants.VALUE);
            av.add(DateTimeHelper.formatDateTime2IsoString(sweTimeRange.getValue().getRangeStart()));
            av.add(DateTimeHelper.formatDateTime2IsoString(sweTimeRange.getValue().getRangeEnd()));
        }
        return jfield;
    }

    private ObjectNode encodeSweCategoryField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.CATEGORY_TYPE);
        SweCategory sweCategory = (SweCategory) field.getElement();
        jfield.put(JSONConstants.CODESPACE, sweCategory.getCodeSpace());
        if (sweCategory.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweCategory.getValue());
        }
        return jfield;
    }
}
