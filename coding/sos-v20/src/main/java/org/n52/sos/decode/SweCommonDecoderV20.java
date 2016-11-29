/*
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
package org.n52.sos.decode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.opengis.swe.x20.AbstractDataComponentDocument;
import net.opengis.swe.x20.AbstractDataComponentType;
import net.opengis.swe.x20.AbstractEncodingType;
import net.opengis.swe.x20.AnyScalarPropertyType;
import net.opengis.swe.x20.BooleanPropertyType;
import net.opengis.swe.x20.BooleanType;
import net.opengis.swe.x20.CategoryPropertyType;
import net.opengis.swe.x20.CategoryType;
import net.opengis.swe.x20.CountPropertyType;
import net.opengis.swe.x20.CountRangeType;
import net.opengis.swe.x20.CountType;
import net.opengis.swe.x20.DataArrayDocument;
import net.opengis.swe.x20.DataArrayPropertyType;
import net.opengis.swe.x20.DataArrayType;
import net.opengis.swe.x20.DataRecordDocument;
import net.opengis.swe.x20.DataRecordPropertyType;
import net.opengis.swe.x20.DataRecordType;
import net.opengis.swe.x20.DataRecordType.Field;
import net.opengis.swe.x20.EncodedValuesPropertyType;
import net.opengis.swe.x20.QuantityPropertyType;
import net.opengis.swe.x20.QuantityRangeType;
import net.opengis.swe.x20.QuantityType;
import net.opengis.swe.x20.TextEncodingDocument;
import net.opengis.swe.x20.TextEncodingType;
import net.opengis.swe.x20.TextPropertyType;
import net.opengis.swe.x20.TextType;
import net.opengis.swe.x20.TimeRangeType;
import net.opengis.swe.x20.TimeType;
import net.opengis.swe.x20.UnitReference;
import net.opengis.swe.x20.VectorType;
import net.opengis.swe.x20.VectorType.Coordinate;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.shetland.ogc.swe.RangeValue;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.ogc.swe.SweCoordinate;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.SweVector;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweCountRange;
import org.n52.shetland.ogc.swe.simpleType.SweQuality;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweQuantityRange;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swe.simpleType.SweTime;
import org.n52.shetland.ogc.swe.simpleType.SweTimeRange;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderXmlInputException;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.decode.exception.NotYetSupportedDecodingException;
import org.n52.svalbard.decode.exception.UnsupportedDecoderInputException;

import com.google.common.base.Joiner;

/**
 * @since 4.0.0
 *
 */
public class SweCommonDecoderV20 implements Decoder<Object, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SweCommonDecoderV20.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper
            .decoderKeysForElements(SweConstants.NS_SWE_20,
                                    AbstractDataComponentDocument.class,
                                    AbstractDataComponentType.class,
                                    AnyScalarPropertyType[].class,
                                    BooleanPropertyType.class,
                                    BooleanType.class,
                                    Coordinate[].class,
                                    CategoryPropertyType.class,
                                    CategoryType.class,
                                    CountPropertyType.class,
                                    CountType.class,
                                    DataArrayDocument.class,
                                    DataArrayPropertyType.class,
                                    DataArrayType.class,
                                    DataRecordDocument.class,
                                    DataRecordPropertyType.class,
                                    DataRecordType.class,
                                    QuantityPropertyType.class,
                                    QuantityType.class,
                                    TextEncodingDocument.class,
                                    TextEncodingType.class,
                                    TextPropertyType.class,
                                    TextType.class);

    public SweCommonDecoderV20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Object decode(Object element) throws DecodingException {
        if (element instanceof DataArrayPropertyType) {
            final DataArrayPropertyType dataArrayPropertyType = (DataArrayPropertyType) element;
            return parseAbstractDataComponent(dataArrayPropertyType.getDataArray1());
        } else if (element instanceof DataRecordPropertyType) {
            final DataRecordPropertyType dataRecordPropertyType = (DataRecordPropertyType) element;
            return parseAbstractDataComponent(dataRecordPropertyType.getDataRecord());
        } else if (element instanceof AbstractDataComponentDocument) {
            return parseAbstractDataComponentDocument((AbstractDataComponentDocument) element);
        } else if (element instanceof AbstractDataComponentType) {
            return parseAbstractDataComponent((AbstractDataComponentType) element);
        } else if (element instanceof Coordinate[]) {
            return parseCoordinates((Coordinate[]) element);
        } else if (element instanceof AnyScalarPropertyType[]) {
            return parseAnyScalarPropertyTypeArray((AnyScalarPropertyType[]) element);
        } else if (element instanceof TextEncodingDocument) {
            final TextEncodingDocument textEncodingDoc = (TextEncodingDocument) element;
            final SweTextEncoding sosTextEncoding = parseTextEncoding(textEncodingDoc.getTextEncoding());
            sosTextEncoding.setXml(textEncodingDoc.xmlText(getXmlOptions()));
            return sosTextEncoding;
        } else if (element instanceof TextEncodingType) {
            TextEncodingDocument textEncodingDoc = TextEncodingDocument.Factory.newInstance(getXmlOptions());
            TextEncodingType textEncoding = (TextEncodingType) element;
            textEncodingDoc.setTextEncoding(textEncoding);
            SweTextEncoding sosTextEncoding = parseTextEncoding(textEncoding);
            sosTextEncoding.setXml(textEncodingDoc.xmlText(getXmlOptions()));
            return sosTextEncoding;
        } else if (element instanceof TextPropertyType) {
            return parseAbstractDataComponent(((TextPropertyType)element).getText());
        } else if (element instanceof CountPropertyType) {
            return parseAbstractDataComponent(((CountPropertyType)element).getCount());
        } else if (element instanceof BooleanPropertyType) {
            return parseAbstractDataComponent(((BooleanPropertyType)element).getBoolean());
        } else if (element instanceof CategoryPropertyType) {
            return parseAbstractDataComponent(((CategoryPropertyType)element).getCategory());
        } else if (element instanceof QuantityPropertyType) {
            return parseAbstractDataComponent(((QuantityPropertyType)element).getQuantity());
        } else if (element instanceof XmlObject) {
            throw new UnsupportedDecoderXmlInputException(this, (XmlObject)element);
        } else {
            throw new UnsupportedDecoderInputException(this, element);
        }
    }

    private SweAbstractDataComponent parseAbstractDataComponent(AbstractDataComponentType abstractDataComponent)
            throws DecodingException {
        SweAbstractDataComponent sosAbstractDataComponent = null;
        if (abstractDataComponent instanceof BooleanType) {
            sosAbstractDataComponent = parseBoolean((BooleanType) abstractDataComponent);
        } else if (abstractDataComponent instanceof CategoryType) {
            sosAbstractDataComponent = parseCategory((CategoryType) abstractDataComponent);
        } else if (abstractDataComponent instanceof CountRangeType) {
            sosAbstractDataComponent = parseCountRange((CountRangeType) abstractDataComponent);
        } else if (abstractDataComponent instanceof CountType) {
            sosAbstractDataComponent = parseCount((CountType) abstractDataComponent);
        } else if (abstractDataComponent instanceof QuantityType) {
            sosAbstractDataComponent = parseQuantity((QuantityType) abstractDataComponent);
        } else if (abstractDataComponent instanceof QuantityRangeType) {
            sosAbstractDataComponent = parseQuantityRange((QuantityRangeType) abstractDataComponent);
        } else if (abstractDataComponent instanceof TextType) {
            sosAbstractDataComponent = parseText((TextType) abstractDataComponent);
        } else if (abstractDataComponent instanceof TimeType) {
            sosAbstractDataComponent = parseTime((TimeType) abstractDataComponent);
        } else if (abstractDataComponent instanceof TimeRangeType) {
            sosAbstractDataComponent = parseTimeRange((TimeRangeType) abstractDataComponent);
        } else if (abstractDataComponent instanceof VectorType) {
            sosAbstractDataComponent = parseVector((VectorType) abstractDataComponent);
        } else if (abstractDataComponent instanceof DataRecordType) {
            SweDataRecord sosDataRecord = parseDataRecord((DataRecordType) abstractDataComponent);
            DataRecordDocument dataRecordDoc = DataRecordDocument.Factory.newInstance(getXmlOptions());
            dataRecordDoc.setDataRecord((DataRecordType) abstractDataComponent);
            sosDataRecord.setXml(dataRecordDoc.xmlText(getXmlOptions()));
            sosAbstractDataComponent = sosDataRecord;
        } else if (abstractDataComponent instanceof DataArrayType) {
            SweDataArray sosDataArray = parseDataArray((DataArrayType) abstractDataComponent);
            DataArrayDocument dataArrayDoc = DataArrayDocument.Factory.newInstance(getXmlOptions());
            dataArrayDoc.setDataArray1((DataArrayType) abstractDataComponent);
            sosDataArray.setXml(dataArrayDoc.xmlText(getXmlOptions()));
            sosAbstractDataComponent = sosDataArray;
        } else {
            throw new UnsupportedDecoderXmlInputException(this, abstractDataComponent);
        }
        if (sosAbstractDataComponent != null) {
            if (abstractDataComponent.isSetDefinition()) {
                sosAbstractDataComponent.setDefinition(abstractDataComponent.getDefinition());
            }
            if (abstractDataComponent.isSetDescription()) {
                sosAbstractDataComponent.setDescription(abstractDataComponent.getDescription());
            }
            if (abstractDataComponent.isSetIdentifier()) {
                sosAbstractDataComponent.setIdentifier(abstractDataComponent.getIdentifier());
            }
            if (abstractDataComponent.isSetLabel()) {
                sosAbstractDataComponent.setLabel(abstractDataComponent.getLabel());
            }
        }
        return sosAbstractDataComponent;
    }

    private Object parseAbstractDataComponentDocument(final AbstractDataComponentDocument abstractDataComponentDoc)
            throws DecodingException {
        SweAbstractDataComponent sosAbstractDataComponent = parseAbstractDataComponent(abstractDataComponentDoc.getAbstractDataComponent());
        sosAbstractDataComponent.setXml(abstractDataComponentDoc.xmlText(getXmlOptions()));
        return sosAbstractDataComponent;
    }

    private SweDataArray parseDataArray(DataArrayType xbDataArray) throws DecodingException {
        SweDataArray sosSweDataArray = new SweDataArray();

        CountPropertyType elementCount = xbDataArray.getElementCount();
        if (elementCount != null) {
            sosSweDataArray.setElementCount(parseElementCount(elementCount));
        }

        // parse data record to elementType
        DataArrayType.ElementType xbElementType = xbDataArray.getElementType();
        if (xbElementType != null && xbElementType.getAbstractDataComponent() != null) {
            sosSweDataArray.setElementType(parseAbstractDataComponent(xbElementType.getAbstractDataComponent()));
        }
        if (xbDataArray.isSetEncoding()) {
            sosSweDataArray.setEncoding(parseEncoding(xbDataArray.getEncoding().getAbstractEncoding()));
        }

        // parse values
        if (xbDataArray.isSetValues()) {
            sosSweDataArray.setValues(parseValues(
                    sosSweDataArray.getElementCount(),
                    sosSweDataArray.getElementType(),
                    sosSweDataArray.getEncoding(),
                    xbDataArray.getValues()));
        }
        // set XML
        DataArrayDocument dataArrayDoc = DataArrayDocument.Factory.newInstance(getXmlOptions());
        dataArrayDoc.setDataArray1(xbDataArray);
        sosSweDataArray.setXml(dataArrayDoc.xmlText(getXmlOptions()));
        return sosSweDataArray;
    }

    private List<List<String>> parseValues(final SweCount elementCount, final SweAbstractDataComponent elementType,
            final SweAbstractEncoding encoding, final EncodedValuesPropertyType encodedValuesPropertyType)
            throws DecodingException {
        if (checkParameterTypes(elementType, encoding)) {
            // Get swe values String via cursor as String
            String values;
            // TODO replace XmlCursor
            /*
             * if (encodedValuesPropertyType.schemaType() == XmlString.type) {
             * XmlString xbString
             */
            // @see SosDecoderv20#parseResultValues
            XmlCursor xbCursor = encodedValuesPropertyType.newCursor();
            xbCursor.toFirstContentToken();
            if (xbCursor.isText()) {
                values = xbCursor.getTextValue().trim();
                xbCursor.dispose();
                if (values != null && !values.isEmpty()) {
                    SweTextEncoding textEncoding = (SweTextEncoding) encoding;

                    String[] blocks = values.split(textEncoding.getBlockSeparator());
                    List<List<String>> resultValues = new ArrayList<>(blocks.length);
                    for (String block : blocks) {
                        String[] tokens = block.split(textEncoding.getTokenSeparator());
                        List<String> tokenList = Arrays.asList(tokens);
                        resultValues.add(tokenList);
                    }
                    return resultValues;
                }
            }
        }
        assert false;
        return null;
    }

    private boolean checkParameterTypes(SweAbstractDataComponent elementType, SweAbstractEncoding encoding)
            throws DecodingException {
        if (!(encoding instanceof SweTextEncoding)) {
            throw new NotYetSupportedDecodingException(SweConstants.EN_ENCODING_TYPE, encoding);
        }
        if (!(elementType instanceof SweDataRecord)) {
            throw new NotYetSupportedDecodingException(SweConstants.EN_ENCODING_TYPE, elementType);
        }
        return true;
    }

    private SweAbstractEncoding parseEncoding(AbstractEncodingType abstractEncodingType) throws DecodingException {
        if (abstractEncodingType instanceof TextEncodingType) {
            return parseTextEncoding((TextEncodingType) abstractEncodingType);
        }
        throw new NotYetSupportedDecodingException(SweConstants.EN_ENCODING_TYPE, abstractEncodingType, TextEncodingType.type.getName());
    }

    private SweDataRecord parseDataRecord(DataRecordType dataRecord) throws DecodingException {
        SweDataRecord sosSweDataRecord = new SweDataRecord();
        for (final Field field : dataRecord.getFieldArray()) {
            sosSweDataRecord.addField(new SweField(field.getName(), parseAbstractDataComponent(field.getAbstractDataComponent())));
        }
        return sosSweDataRecord;
    }

    private SweBoolean parseBoolean(BooleanType xbBoolean) throws DecodingException {
        SweBoolean sosBoolean = new SweBoolean();
        if (xbBoolean.isSetValue()) {
            sosBoolean.setValue(xbBoolean.getValue());
        }
        return sosBoolean;
    }

    private SweCategory parseCategory(CategoryType xbCategory) throws DecodingException {
        SweCategory sosSweCategory = new SweCategory();
        if (xbCategory.isSetCodeSpace() && xbCategory.getCodeSpace().isSetHref()) {
            sosSweCategory.setCodeSpace(xbCategory.getCodeSpace().getHref());
        }
        if (xbCategory.isSetValue()) {
            sosSweCategory.setValue(xbCategory.getValue());
        }
        return sosSweCategory;
    }

    private SweCount parseCount(CountType count) throws DecodingException {
        final SweCount sosCount = new SweCount();
        if (count.getQualityArray() != null) {
            sosCount.setQuality(parseQuality(count.getQualityArray()));
        }
        if (count.isSetValue()) {
            sosCount.setValue(count.getValue().intValue());
        }
        return sosCount;
    }

    private SweCountRange parseCountRange(CountRangeType countRange) throws DecodingException {
        throw new UnsupportedDecoderInputException(this, countRange);
    }

    private SweQuantity parseQuantity(final QuantityType xbQuantity) throws DecodingException {
        final SweQuantity sosQuantity = new SweQuantity();
        if (xbQuantity.isSetAxisID()) {
            sosQuantity.setAxisID(xbQuantity.getAxisID());
        }
        if (xbQuantity.getQualityArray() != null) {
            sosQuantity.setQuality(parseQuality(xbQuantity.getQualityArray()));
        }

        if (xbQuantity.getUom() != null) {
            final UnitReference uom = xbQuantity.getUom();
            if (uom.isSetCode()) {
                sosQuantity.setUom(uom.getCode());
            } else if (uom.isSetHref()) {
                sosQuantity.setUom(uom.getHref());
            }
        }

        if (xbQuantity.isSetValue()) {
            sosQuantity.setValue(xbQuantity.getValue());
        }
        return sosQuantity;
    }

    private SweQuantityRange parseQuantityRange(QuantityRangeType quantityRange) throws DecodingException {
        SweQuantityRange sweQuantityRange = new SweQuantityRange();
        if (quantityRange.isSetDefinition()) {
            sweQuantityRange.setDefinition(quantityRange.getDefinition());
        }
        if (quantityRange.isSetLabel()) {
            sweQuantityRange.setLabel(quantityRange.getLabel());
        }
        if (!quantityRange.getUom().isNil() && quantityRange.getUom().isSetCode()) {
            sweQuantityRange.setUom(quantityRange.getUom().getCode());
        }
        if (quantityRange.getValue() != null) {
            sweQuantityRange.setValue(parseRangeValue(quantityRange.getValue()));
        }
        return sweQuantityRange;
    }

    private RangeValue<Double> parseRangeValue(List<?> value) throws DecodingException {
        if (value == null || value.isEmpty() || value.size() != 2) {
            throw new DecodingException("?:QuantityRange/?:value", "The 'swe:value' element of an 'swe:QuantityRange' is not set correctly");
        }
        return new RangeValue<>(Double.parseDouble(value.get(0).toString()),
                                Double.parseDouble(value.get(1).toString()));
    }

    private SweText parseText(final TextType xbText) {
        final SweText sosText = new SweText();
        if (xbText.isSetValue()) {
            sosText.setValue(xbText.getValue());
        }
        return sosText;
    }

    private SweTime parseTime(TimeType xbTime) throws DecodingException {
        final SweTime sosTime = new SweTime();
        if (xbTime.isSetValue()) {
            sosTime.setValue(DateTimeHelper.parseIsoString2DateTime(xbTime.getValue().toString()));
        }
        if (xbTime.getUom() != null) {
            sosTime.setUom(xbTime.getUom().getHref());
        }
        return sosTime;
    }

    private SweTimeRange parseTimeRange(TimeRangeType xbTime) throws DecodingException {
        SweTimeRange sosTimeRange = new SweTimeRange();
        if (xbTime.isSetValue()) {
            List<?> value = xbTime.getValue();
            if (value != null && !value.isEmpty()) {
                RangeValue<DateTime> range = new RangeValue<>();
                Iterator<?> iter = value.iterator();
                if (iter.hasNext()) {
                    range.setRangeStart(DateTimeHelper.parseIsoString2DateTime(iter.next().toString()));
                }
                if (iter.hasNext()) {
                    range.setRangeEnd(DateTimeHelper.parseIsoString2DateTime(iter.next().toString()));
                }
                sosTimeRange.setValue(range);
            }
        }
        if (xbTime.getUom() != null) {
            sosTimeRange.setUom(xbTime.getUom().getHref());
        }
        return sosTimeRange;
    }

    private Collection<SweQuality> parseQuality(XmlObject[] qualityArray) throws DecodingException {
        if (qualityArray == null || qualityArray.length == 0) {
            return null;
        }
        throw new UnsupportedDecoderInputException(this, SweConstants.NS_SWE_PREFIX + ":" + SweConstants.EN_QUALITY);
    }

    private SweAbstractDataComponent parseVector(VectorType vector) throws DecodingException {
        final SweVector sweVector = new SweVector();
        if (vector.isSetLocalFrame()) {
            sweVector.setLocalFrame(vector.getLocalFrame());
        }
        sweVector.setReferenceFrame(vector.getReferenceFrame());
        sweVector.setCoordinates(parseCoordinates(vector.getCoordinateArray()));
        return sweVector;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<SweCoordinate<?>> parseCoordinates(Coordinate[] coordinateArray) throws DecodingException {
        List<SweCoordinate<?>> sosCoordinates = new ArrayList<>(coordinateArray.length);
        for (final Coordinate xbCoordinate : coordinateArray) {
            // validate document
            XmlHelper.validateDocument(xbCoordinate);
            if (xbCoordinate.isSetQuantity()) {
                sosCoordinates.add(new SweCoordinate(xbCoordinate.getName(),
                        (SweAbstractSimpleType) parseAbstractDataComponent(xbCoordinate.getQuantity())));
            } else {
                throw new DecodingException(SweConstants.EN_POSITION,
                        "Error when parsing the Coordinates of Position: It must be of type Quantity!");
            }
        }
        return sosCoordinates;
    }

    private List<SweField> parseAnyScalarPropertyTypeArray(final AnyScalarPropertyType[] fieldArray)
            throws DecodingException {
        final List<SweField> sosFields = new ArrayList<>(fieldArray.length);
        for (final AnyScalarPropertyType xbField : fieldArray) {
            // validate document
            XmlHelper.validateDocument(xbField);
            /*
             * if (xbField.isSetBoolean()) { sosFields.add(new
             * SosSweField(xbField.getName(),
             * parseAbstractDataComponent(xbField.getBoolean()))); } else if
             * (xbField.isSetCategory()) { sosFields.add(new
             * SosSweField(xbField.getName(),
             * parseAbstractDataComponent(xbField.getCategory()))); } else if
             * (xbField.isSetCount()) { sosFields.add(new
             * SosSweField(xbField.getName(),
             * parseAbstractDataComponent(xbField.getCount()))); } else if
             * (xbField.isSetQuantity()) { sosFields.add(new
             * SosSweField(xbField.getName(),
             * parseAbstractDataComponent(xbField.getQuantity()))); } else if
             * (xbField.isSetText()) { sosFields.add(new
             * SosSweField(xbField.getName(),
             * parseAbstractDataComponent(xbField.getText()))); } else if
             * (xbField.isSetTime()) { sosFields.add(new
             * SosSweField(xbField.getName(),
             * parseAbstractDataComponent(xbField.getTime()))); }
             */

        }
        return sosFields;
    }

    private SweTextEncoding parseTextEncoding(final TextEncodingType textEncoding) {
        final SweTextEncoding sosTextEncoding = new SweTextEncoding();
        sosTextEncoding.setBlockSeparator(textEncoding.getBlockSeparator());
        sosTextEncoding.setTokenSeparator(textEncoding.getTokenSeparator());
        if (textEncoding.isSetDecimalSeparator()) {
            sosTextEncoding.setDecimalSeparator(textEncoding.getDecimalSeparator());
        }
        if (textEncoding.isSetCollapseWhiteSpaces()) {
            sosTextEncoding.setCollapseWhiteSpaces(textEncoding.getCollapseWhiteSpaces());
        }
        return sosTextEncoding;
    }

    private SweCount parseElementCount(final CountPropertyType elementCount) throws DecodingException {
        if (elementCount.isSetCount()) {
            return (SweCount) parseAbstractDataComponent(elementCount.getCount());
        }
        return null;
    }

    protected static XmlOptions getXmlOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }
}
