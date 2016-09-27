/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.math.BigDecimal;
import java.util.List;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweAbstractDataRecord;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.util.OMHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class ObservationEncoder extends JSONEncoder<OmObservation> {
    public ObservationEncoder() {
        super(OmObservation.class);
    }

    @Override
    public JsonNode encodeJSON(OmObservation o) throws OwsExceptionReport {
        ObjectNode json = nodeFactory().objectNode();
        encodeObservationType(o, json);
        encodeIdentifier(o, json);
        encodeProcedure(o, json);
        encodeParameter(o, json);
        encodeObservableProperty(o, json);
        encodeFeatureOfInterest(o, json);
        encodePhenomenonTime(o, json);
        encodeResultTime(o, json);
        encodeValidTime(o, json);
        encodeResult(o, json);
        return json;
    }

    private void encodeIdentifier(OmObservation o, ObjectNode json) {
        if (o.isSetIdentifier()) {
            json.put(JSONConstants.IDENTIFIER, encodeCodeWithAuthority(o.getIdentifierCodeWithAuthority()));
        }
    }

    private void encodeProcedure(OmObservation o, ObjectNode json) {
        json.put(JSONConstants.PROCEDURE, o.getObservationConstellation().getProcedure().getIdentifier());
    }

    private void encodeParameter(OmObservation o, ObjectNode json) throws OwsExceptionReport {
        if (o.isSetParameter()) {
            if (o.getParameter().size() == 1) {
                json.put(JSONConstants.PARAMETER, encodeNamedValue(o.getParameter().iterator().next()));
            } else {
                ArrayNode parameters = json.putArray(JSONConstants.PARAMETER);
                for (NamedValue<?> namedValue : o.getParameter()) {
                    parameters.add(encodeNamedValue(namedValue));
                }
            }
        }
    }

    private JsonNode encodeNamedValue(NamedValue<?> namedValue) throws OwsExceptionReport {
        ObjectNode namedValueObject = nodeFactory().objectNode();
        namedValueObject.put(JSONConstants.NAME, namedValue.getName().getHref());
        namedValueObject.put(JSONConstants.VALUE, encodeValue(namedValue.getValue()));
        ObjectNode parameterObject = nodeFactory().objectNode();
        parameterObject.put(JSONConstants.NAMED_VALUE, namedValueObject);
        return parameterObject;
    }

    private void encodeObservableProperty(OmObservation o, ObjectNode json) {
        json.put(JSONConstants.OBSERVABLE_PROPERTY,
                o.getObservationConstellation().getObservableProperty().getIdentifier());
    }

    private void encodeObservationType(OmObservation o, ObjectNode json) throws OwsExceptionReport {
        json.put(JSONConstants.TYPE, getObservationType(o));
    }

    private void encodeFeatureOfInterest(OmObservation o, ObjectNode json) throws OwsExceptionReport {
        OmObservationConstellation oc = o.getObservationConstellation();
        json.put(JSONConstants.FEATURE_OF_INTEREST, encodeObjectToJson(oc.getFeatureOfInterest()));
    }

    private void encodeResultTime(OmObservation o, ObjectNode json) throws OwsExceptionReport {
        if (o.isSetResultTime()) {
            json.put(JSONConstants.RESULT_TIME, encodeObjectToJson(o.getResultTime()));
        }
    }

    private void encodeValidTime(OmObservation o, ObjectNode json) throws OwsExceptionReport {
        if (o.isSetValidTime()) {
            json.put(JSONConstants.VALID_TIME, encodeObjectToJson(o.getValidTime()));
        }
    }

    private void encodePhenomenonTime(OmObservation o, ObjectNode json) throws OwsExceptionReport {
        json.put(JSONConstants.PHENOMENON_TIME, encodeObjectToJson(o.getPhenomenonTime()));
    }

    private void encodeResult(OmObservation o, ObjectNode json) throws OwsExceptionReport {
        json.put(JSONConstants.RESULT, encodeResult(o));
    }

    private JsonNode encodeResult(OmObservation o) throws OwsExceptionReport {
        Value<?> value = o.getValue().getValue();
        String type = getObservationType(o);
        if (value instanceof TVPValue) {
            if (type.equals(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION)) {
                return encodeTVPValue(o);
            }
        } else {
            return encodeValue(value);
        }
        throw new UnsupportedEncoderInputException(this, value);
    }

    private JsonNode encodeValue(Value<?> value)
            throws OwsExceptionReport {
        return value.accept(new ValueVisitor<JsonNode>() {
            @Override
            public JsonNode visit(BooleanValue value) {
                return encodeBooleanValue(value);
            }

            @Override
            public JsonNode visit(CategoryValue value) {
                return encodeCategoryValue(value);
            }

            @Override
            public JsonNode visit(ComplexValue value)
                    throws OwsExceptionReport {
                return encodeComplexValue(value);
            }

            @Override
            public JsonNode visit(CountValue value) {
                return encodeCountValue(value);
            }

            @Override
            public JsonNode visit(GeometryValue value)
                    throws OwsExceptionReport {
                return encodeGeometryValue(value);
            }

            @Override
            public JsonNode visit(HrefAttributeValue value) {
                return encodeHrefAttributeValue(value);
            }

            @Override
            public JsonNode visit(NilTemplateValue value)
                    throws OwsExceptionReport {
                throw new UnsupportedEncoderInputException(ObservationEncoder.this, value);

            }

            @Override
            public JsonNode visit(QuantityValue value) {
                return encodeQualityValue(value);
            }

            @Override
            public JsonNode visit(ReferenceValue value) {
                return encodeReferenceValue(value);
            }

            @Override
            public JsonNode visit(SweDataArrayValue value)
                    throws OwsExceptionReport {
                return encodeSweDataArrayValue(value);
            }

            @Override
            public JsonNode visit(TVPValue value) throws OwsExceptionReport {
                return encodeTVPValue(value);
            }

            @Override
            public JsonNode visit(TextValue value) {
                return encodeTextValue(value);
            }

            @Override
            public JsonNode visit(UnknownValue value)
                    throws OwsExceptionReport {
                throw new UnsupportedEncoderInputException(ObservationEncoder.this, value);
            }

            @Override
            public JsonNode visit(XmlValue value) {
                return encodeXmlValue(value);
            }

            @Override
            public JsonNode visit(TLVTValue value) throws OwsExceptionReport {
                throw new UnsupportedEncoderInputException(ObservationEncoder.this, value);
            }

            @Override
            public JsonNode visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
                throw new UnsupportedEncoderInputException(ObservationEncoder.this, value);
            }

            @Override
            public JsonNode visit(MultiPointCoverage value) throws OwsExceptionReport {
                throw new UnsupportedEncoderInputException(ObservationEncoder.this, value);
            }

            @Override
            public JsonNode visit(RectifiedGridCoverage value) throws OwsExceptionReport {
                throw new UnsupportedEncoderInputException(ObservationEncoder.this, value);
            }

            @Override
            public JsonNode visit(ProfileValue value) throws OwsExceptionReport {
                throw new UnsupportedEncoderInputException(ObservationEncoder.this, value);
            }
        });
    }

    private JsonNode encodeReferenceValue(ReferenceValue value) {
        ReferenceType ref = value.getValue();
        ObjectNode node = nodeFactory().objectNode();
        node.put(JSONConstants.HREF, ref.getHref());
        node.put(JSONConstants.ROLE, ref.getRole());
        node.put(JSONConstants.TITLE, ref.getTitle());
        return node;
    }

    private JsonNode encodeHrefAttributeValue(HrefAttributeValue value) {
        ObjectNode node = nodeFactory().objectNode();
        node.put(JSONConstants.HREF, value.getValue().getHref());
        return node;
    }

    private JsonNode encodeTVPValue(TVPValue value) throws OwsExceptionReport {
        ArrayNode arrayNode = nodeFactory().arrayNode();
        for (TimeValuePair tvp : value.getValue()) {
            ObjectNode node = nodeFactory().objectNode();
            node.put(JSONConstants.TIME, encodeObjectToJson(tvp.getTime()));
            node.put(JSONConstants.VALUE, encodeValue(value));
            arrayNode.add(node);
        }
        return arrayNode;
    }

    private JsonNode encodeQualityValue(Value<?> value) {
        QuantityValue quantityValue = (QuantityValue) value;
        ObjectNode node = nodeFactory().objectNode();
        node.put(JSONConstants.UOM, quantityValue.getUnit());
        node.put(JSONConstants.VALUE, quantityValue.getValue());
        return node;
    }

    private JsonNode encodeCountValue(Value<?> value) {
        CountValue countValue = (CountValue) value;
        return nodeFactory().numberNode(countValue.getValue());
    }

    private JsonNode encodeTextValue(Value<?> value) {
        TextValue textValue = (TextValue) value;
        return nodeFactory().textNode(textValue.getValue());
    }

    private JsonNode encodeBooleanValue(Value<?> value) {
        BooleanValue booleanValue = (BooleanValue) value;
        return nodeFactory().booleanNode(booleanValue.getValue());
    }

    private JsonNode encodeCategoryValue(Value<?> value) {
        CategoryValue categoryValue = (CategoryValue) value;
        ObjectNode node = nodeFactory().objectNode();
        node.put(JSONConstants.CODESPACE, categoryValue.getUnit());
        node.put(JSONConstants.VALUE, categoryValue.getValue());
        return node;
    }

    private JsonNode encodeGeometryValue(Value<?> value) throws OwsExceptionReport {
        GeometryValue geometryValue = (GeometryValue) value;
        return encodeObjectToJson(geometryValue.getValue());
    }

    private JsonNode encodeComplexValue(Value<?> value) throws OwsExceptionReport {
        ArrayNode result = nodeFactory().arrayNode();
        ComplexValue complexValue = (ComplexValue) value;
        SweAbstractDataRecord sweDataRecord = complexValue.getValue();
        for (SweField field : sweDataRecord.getFields()) {
            result.add(encodeObjectToJson(field));
        }
        return result;
    }

    private JsonNode encodeSweDataArrayValue(Value<?> value) throws OwsExceptionReport {
        SweDataArrayValue sweDataArrayValue = (SweDataArrayValue) value;
        ObjectNode result = nodeFactory().objectNode();
        ArrayNode jfields = result.putArray(JSONConstants.FIELDS);
        ArrayNode jvalues = result.putArray(JSONConstants.VALUES);
        List<SweField> fields = ((SweDataRecord) sweDataArrayValue.getValue().getElementType()).getFields();
        List<List<String>> values = sweDataArrayValue.getValue().getValues();
        TokenConverter[] conv = new TokenConverter[fields.size()];
        int i = 0;
        for (SweField field : fields) {
            try {
                conv[i++] = TokenConverter.forField(field);
            } catch (IllegalArgumentException e) {
                throw new UnsupportedEncoderInputException(this, field);
            }
            jfields.add(encodeObjectToJson(field));
        }

        for (List<String> block : values) {
            ArrayNode jblock = jvalues.addArray();
            i = 0;
            for (String token : block) {
                jblock.add(conv[i++].convert(token));
            }
        }
        return result;
    }

    private String getObservationType(OmObservation o) throws OwsExceptionReport {
        if (o.getObservationConstellation().isSetObservationType()) {
            return o.getObservationConstellation().getObservationType();
        } else {
            return OMHelper.getObservationTypeFor(o.getValue().getValue());
        }
    }

    private JsonNode encodeTVPValue(OmObservation o) throws OwsExceptionReport {
        TVPValue tvpValue = (TVPValue) o.getValue().getValue();
        ObjectNode result = nodeFactory().objectNode();
        List<TimeValuePair> values = tvpValue.getValue();
        if (values != null && !values.isEmpty()) {
            String obsProp = o.getObservationConstellation().getObservableProperty().getIdentifier();
            SweTime timeDef = new SweTime();
            timeDef.setDefinition(OmConstants.PHENOMENON_TIME);
            timeDef.setUom(OmConstants.PHEN_UOM_ISO8601);
            SweField timeField = new SweField(OmConstants.PHENOMENON_TIME_NAME, timeDef);
            SweField valueField = getFieldForValue(obsProp, values.get(0).getValue());

            result.putArray(JSONConstants.FIELDS).add(encodeObjectToJson(timeField))
                    .add(encodeObjectToJson(valueField));
            ArrayNode jvalues = result.putArray(JSONConstants.VALUES);

            for (TimeValuePair tvp : values) {
                jvalues.addArray().add(encodeObjectToJson(tvp.getTime())).add(getTokenForValue(tvp.getValue()));
            }
        }
        return result;
    }

    private SweField getFieldForValue(String phenomenon, Value<?> value) throws UnsupportedEncoderInputException {
        final SweAbstractDataComponent def;
        if (value instanceof BooleanValue) {
            def = new SweBoolean();
        } else if (value instanceof CategoryValue) {
            SweCategory sweCategory = new SweCategory();
            CategoryValue categoryValue = (CategoryValue) value;
            sweCategory.setCodeSpace(categoryValue.getUnit());
            def = sweCategory;
        } else if (value instanceof CountValue) {
            def = new SweCount();
        } else if (value instanceof QuantityValue) {
            SweQuantity sweQuantity = new SweQuantity();
            QuantityValue quantityValue = (QuantityValue) value;
            sweQuantity.setUom(quantityValue.getUnit());
            def = sweQuantity;
        } else if (value instanceof TextValue) {
            def = new SweText();
        } else if (value instanceof NilTemplateValue) {
            def = new SweText();
        } else if (value instanceof BooleanValue) {
            def = new SweBoolean();
        } else if (value instanceof GeometryValue) {
            def = new SweText();
        } else {
            throw new UnsupportedEncoderInputException(this, value);
        }
        def.setDefinition(phenomenon);
        return new SweField(phenomenon, def);
    }

    private JsonNode getTokenForValue(Value<?> value) throws OwsExceptionReport {
        if (value instanceof QuantityValue) {
            QuantityValue quantityValue = (QuantityValue) value;
            return nodeFactory().numberNode(quantityValue.getValue());
        } else if (value instanceof CountValue) {
            CountValue countValue = (CountValue) value;
            return nodeFactory().numberNode(countValue.getValue());
        } else if (value instanceof TextValue) {
            TextValue textValue = (TextValue) value;
            return nodeFactory().textNode(textValue.getValue());
        } else if (value instanceof BooleanValue) {
            BooleanValue booleanValue = (BooleanValue) value;
            return nodeFactory().booleanNode(booleanValue.getValue());
        } else if (value instanceof CategoryValue) {
            CategoryValue categoryValue = (CategoryValue) value;
            return nodeFactory().textNode(categoryValue.getValue());
        } else if (value instanceof GeometryValue) {
            GeometryValue geometryValue = (GeometryValue) value;
            // TODO WKT?
            return encodeObjectToJson(geometryValue);
        } else if (value instanceof NilTemplateValue) {
            return nodeFactory().nullNode();
        } else {
            throw new UnsupportedEncoderInputException(this, value);
        }
    }

    private TextNode encodeXmlValue(XmlValue value) {
        return nodeFactory().textNode(value.getValue().xmlText());
    }

    /**
     * Class used to convert string values of a SweDataArray back to a more
     * native representation.
     */
    private static abstract class TokenConverter {
        private static final TokenConverter TEXT_CONVERTER = new TextConverter();

        private static final TokenConverter COUNT_CONVERTER = new CountConverter();

        private static final TokenConverter QUANTITY_CONVERTER = new QuantityConverter();

        private static final TokenConverter BOOLEAN_CONVERTER = new BooleanConverter();

        private static final TokenConverter OBSERVABLE_PROPERTY_CONVERTER = TEXT_CONVERTER;

        private static final TokenConverter CATEGORY_CONVERTER = TEXT_CONVERTER;

        private static final TokenConverter TIME_CONVERTER = TEXT_CONVERTER;

        private static final TokenConverter TIME_RANGE_CONVERTER = new RangeTokenConverter(TIME_CONVERTER);

        private static final TokenConverter QUANTITY_RANGE_CONVERTER = new RangeTokenConverter(QUANTITY_CONVERTER);

        private static final TokenConverter COUNT_RANGE_CONVERTER = new RangeTokenConverter(COUNT_CONVERTER);

        JsonNodeFactory nodeFactory() {
            return JSONUtils.nodeFactory();
        }

        abstract JsonNode convert(String s);

        static TokenConverter forField(SweField field) {
            switch (field.getElement().getDataComponentType()) {
            case Count:
                return COUNT_CONVERTER;
            case Boolean:
                return BOOLEAN_CONVERTER;
            case CountRange:
                return COUNT_RANGE_CONVERTER;
            case ObservableProperty:
                return OBSERVABLE_PROPERTY_CONVERTER;
            case Text:
                return TEXT_CONVERTER;
            case Quantity:
                return QUANTITY_CONVERTER;
            case QuantityRange:
                return QUANTITY_RANGE_CONVERTER;
            case Time:
                return TIME_CONVERTER;
            case TimeRange:
                return TIME_RANGE_CONVERTER;
            case Category:
                return CATEGORY_CONVERTER;
            default:
                throw new IllegalArgumentException("Unknown field type");
            }
        }

        private static class RangeTokenConverter extends TokenConverter {
            private final TokenConverter conv;

            RangeTokenConverter(TokenConverter conv) {
                this.conv = conv;
            }

            @Override
            JsonNode convert(String s) {
                String[] split = s.split("/");
                return nodeFactory().arrayNode().add(conv.convert(split[0])).add(conv.convert(split[1]));
            }
        }

        private static class TextConverter extends TokenConverter {
            @Override
            JsonNode convert(String s) {
                return nodeFactory().textNode(s);
            }
        }

        private static class QuantityConverter extends TokenConverter {
            @Override
            JsonNode convert(String s) {
                return nodeFactory().numberNode(new BigDecimal(s));
            }
        }

        private static class BooleanConverter extends TokenConverter {
            @Override
            JsonNode convert(String s) {
                return nodeFactory().booleanNode(Boolean.parseBoolean(s));
            }
        }

        private static class CountConverter extends TokenConverter {
            @Override
            JsonNode convert(String s) {
                return nodeFactory().numberNode(Integer.parseInt(s));
            }
        }
    }
}
