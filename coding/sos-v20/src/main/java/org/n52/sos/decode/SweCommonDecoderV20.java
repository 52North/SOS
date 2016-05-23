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
package org.n52.sos.decode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.swe.x20.AbstractDataComponentDocument;
import net.opengis.swe.x20.AbstractDataComponentType;
import net.opengis.swe.x20.AbstractEncodingType;
import net.opengis.swe.x20.AnyScalarPropertyType;
import net.opengis.swe.x20.BooleanPropertyType;
import net.opengis.swe.x20.BooleanType;
import net.opengis.swe.x20.CategoryType;
import net.opengis.swe.x20.CountPropertyType;
import net.opengis.swe.x20.CountRangeType;
import net.opengis.swe.x20.CountType;
import net.opengis.swe.x20.DataArrayDocument;
import net.opengis.swe.x20.DataArrayPropertyType;
import net.opengis.swe.x20.DataArrayType;
import net.opengis.swe.x20.DataRecordDocument;
import net.opengis.swe.x20.DataRecordType;
import net.opengis.swe.x20.DataRecordType.Field;
import net.opengis.swe.x20.EncodedValuesPropertyType;
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
import org.joda.time.DateTime;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.RangeValue;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweCoordinate;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweVector;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweCountRange;
import org.n52.sos.ogc.swe.simpleType.SweQuality;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @since 4.0.0
 * 
 */
public class SweCommonDecoderV20 implements Decoder<Object, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SweCommonDecoderV20.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(SweConstants.NS_SWE_20,
            DataArrayPropertyType.class, DataArrayDocument.class, DataArrayType.class, DataRecordDocument.class,
            DataRecordType.class, CountType.class, QuantityType.class, TextType.class, Coordinate[].class,
            AnyScalarPropertyType[].class, TextEncodingDocument.class, TextEncodingType.class,
            AbstractDataComponentDocument.class, AbstractDataComponentType.class, TextPropertyType.class, CountPropertyType.class, BooleanPropertyType.class);

    public SweCommonDecoderV20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Object decode(final Object element) throws OwsExceptionReport {
        if (element instanceof DataArrayPropertyType) {
            final DataArrayPropertyType dataArrayPropertyType = (DataArrayPropertyType) element;
            return parseAbstractDataComponent(dataArrayPropertyType.getDataArray1());
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
            sosTextEncoding.setXml(textEncodingDoc.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
            return sosTextEncoding;
        } else if (element instanceof TextEncodingType) {
            final TextEncodingDocument textEncodingDoc =
                    TextEncodingDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            final TextEncodingType textEncoding = (TextEncodingType) element;
            textEncodingDoc.setTextEncoding(textEncoding);
            final SweTextEncoding sosTextEncoding = parseTextEncoding(textEncoding);
            sosTextEncoding.setXml(textEncodingDoc.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
            return sosTextEncoding;
        } else if (element instanceof TextPropertyType) {
            return parseAbstractDataComponent(((TextPropertyType)element).getText());
        } else if (element instanceof CountPropertyType) {
            return parseAbstractDataComponent(((CountPropertyType)element).getCount());
        } else if (element instanceof BooleanPropertyType) {
            return parseAbstractDataComponent(((BooleanPropertyType)element).getBoolean());
        } else {
            throw new UnsupportedDecoderInputException(this, element);
        }
    }

    private SweAbstractDataComponent parseAbstractDataComponent(final AbstractDataComponentType abstractDataComponent)
            throws OwsExceptionReport {
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
        } else if (abstractDataComponent instanceof DataArrayDocument) {
            sosAbstractDataComponent = parseDataArray(((DataArrayDocument) abstractDataComponent).getDataArray1());
        } else if (abstractDataComponent instanceof DataRecordType) {
            final SweDataRecord sosDataRecord = parseDataRecord((DataRecordType) abstractDataComponent);
            final DataRecordDocument dataRecordDoc =
                    DataRecordDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            dataRecordDoc.setDataRecord((DataRecordType) abstractDataComponent);
            sosDataRecord.setXml(dataRecordDoc.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
            sosAbstractDataComponent = sosDataRecord;
        } else if (abstractDataComponent instanceof DataArrayType) {
            final SweDataArray sosDataArray = parseDataArray((DataArrayType) abstractDataComponent);
            final DataArrayDocument dataArrayDoc =
                    DataArrayDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            dataArrayDoc.setDataArray1((DataArrayType) abstractDataComponent);
            sosDataArray.setXml(dataArrayDoc.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
            sosAbstractDataComponent = sosDataArray;
        } else {
            throw new UnsupportedDecoderInputException(this, abstractDataComponent);
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
            throws OwsExceptionReport {
        final SweAbstractDataComponent sosAbstractDataComponent =
                parseAbstractDataComponent(abstractDataComponentDoc.getAbstractDataComponent());
        sosAbstractDataComponent.setXml(abstractDataComponentDoc.xmlText(XmlOptionsHelper.getInstance()
                .getXmlOptions()));
        return sosAbstractDataComponent;
    }

    private SweDataArray parseDataArray(final DataArrayType xbDataArray) throws OwsExceptionReport {
        final SweDataArray sosSweDataArray = new SweDataArray();

        final CountPropertyType elementCount = xbDataArray.getElementCount();
        if (elementCount != null) {
            sosSweDataArray.setElementCount(parseElementCount(elementCount));
        }

        // parse data record to elementType
        final DataArrayType.ElementType xbElementType = xbDataArray.getElementType();
        if (xbElementType != null && xbElementType.getAbstractDataComponent() != null) {
            sosSweDataArray.setElementType(parseAbstractDataComponent(xbElementType.getAbstractDataComponent()));
        }
        if (xbDataArray.isSetEncoding()) {
            sosSweDataArray.setEncoding(parseEncoding(xbDataArray.getEncoding().getAbstractEncoding()));
        }

        // parse values
        if (xbDataArray.isSetValues()) {
            sosSweDataArray.setValues(parseValues(sosSweDataArray.getElementCount(), sosSweDataArray.getElementType(),
                    sosSweDataArray.getEncoding(), xbDataArray.getValues()));
        }
        // set XML
        final DataArrayDocument dataArrayDoc =
                DataArrayDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        dataArrayDoc.setDataArray1(xbDataArray);
        sosSweDataArray.setXml(dataArrayDoc.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
        return sosSweDataArray;
    }

    private List<List<String>> parseValues(final SweCount elementCount, final SweAbstractDataComponent elementType,
            final SweAbstractEncoding encoding, final EncodedValuesPropertyType encodedValuesPropertyType)
            throws OwsExceptionReport {
        assert elementCount != null;
        assert elementType != null;
        assert encoding != null;
        if (checkParameterTypes(elementType, encoding)) {
            // Get swe values String via cursor as String
            String values;
            // TODO replace XmlCursor
            /*
             * if (encodedValuesPropertyType.schemaType() == XmlString.type) {
             * XmlString xbString
             */
            // @see SosDecoderv20#parseResultValues
            final XmlCursor xbCursor = encodedValuesPropertyType.newCursor();
            xbCursor.toFirstContentToken();
            if (xbCursor.isText()) {
                values = xbCursor.getTextValue().trim();
                xbCursor.dispose();
                if (values != null && !values.isEmpty()) {
                    final SweTextEncoding textEncoding = (SweTextEncoding) encoding;

                    final String[] blocks = values.split(textEncoding.getBlockSeparator());
                    final List<List<String>> resultValues = new ArrayList<List<String>>(blocks.length);
                    for (final String block : blocks) {
                        final String[] tokens = block.split(textEncoding.getTokenSeparator());
                        final List<String> tokenList = Arrays.asList(tokens);
                        resultValues.add(tokenList);
                    }
                    return resultValues;
                }
            }
        }
        assert false;
        return null;
    }

    private boolean checkParameterTypes(final SweAbstractDataComponent elementType, final SweAbstractEncoding encoding)
            throws OwsExceptionReport {
        if (!(encoding instanceof SweTextEncoding)) {
            throw new NotYetSupportedException(SweConstants.EN_ENCODING_TYPE, encoding);
        }
        if (!(elementType instanceof SweDataRecord)) {
            throw new NotYetSupportedException(SweConstants.EN_ENCODING_TYPE, elementType);
        }
        return true;
    }

    private SweAbstractEncoding parseEncoding(final AbstractEncodingType abstractEncodingType) throws OwsExceptionReport {
        assert abstractEncodingType != null;
        if (abstractEncodingType instanceof TextEncodingType) {
            return parseTextEncoding((TextEncodingType) abstractEncodingType);
        }
        throw new NotYetSupportedException(SweConstants.EN_ENCODING_TYPE, abstractEncodingType,
                TextEncodingType.type.getName());
    }

    private SweDataRecord parseDataRecord(final DataRecordType dataRecord) throws OwsExceptionReport {
        final SweDataRecord sosSweDataRecord = new SweDataRecord();
        for (final Field field : dataRecord.getFieldArray()) {
            sosSweDataRecord.addField(new SweField(field.getName(), parseAbstractDataComponent(field
                    .getAbstractDataComponent())));
        }
        return sosSweDataRecord;
    }

    private SweBoolean parseBoolean(final BooleanType xbBoolean) throws OwsExceptionReport {
        final SweBoolean sosBoolean = new SweBoolean();
        if (xbBoolean.isSetValue()) {
            sosBoolean.setValue(xbBoolean.getValue());
        }
        return sosBoolean;
    }

    private SweCategory parseCategory(final CategoryType xbCategory) throws OwsExceptionReport {
        final SweCategory sosSweCategory = new SweCategory();
        if (xbCategory.isSetCodeSpace() && xbCategory.getCodeSpace().isSetHref()) {
            sosSweCategory.setCodeSpace(xbCategory.getCodeSpace().getHref());
        }
        if (xbCategory.isSetValue()) {
            sosSweCategory.setValue(xbCategory.getValue());
        }
        return sosSweCategory;
    }

    private SweCount parseCount(final CountType count) throws OwsExceptionReport {
        final SweCount sosCount = new SweCount();
        if (count.getQualityArray() != null) {
            sosCount.setQuality(parseQuality(count.getQualityArray()));
        }
        if (count.isSetValue()) {
            sosCount.setValue(count.getValue().intValue());
        }
        return sosCount;
    }
    
    private SweCountRange parseCountRange(final CountRangeType countRange) throws OwsExceptionReport {
        throw new NotYetSupportedException(SweConstants.EN_COUNT_RANGE);
    }

    private SweQuantity parseQuantity(final QuantityType xbQuantity) throws OwsExceptionReport {
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
            sosQuantity.setValue(Double.valueOf(xbQuantity.getValue()));
        }
        return sosQuantity;
    }

    private SweQuantityRange parseQuantityRange(final QuantityRangeType quantityRange) throws OwsExceptionReport {
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

    private RangeValue<Double> parseRangeValue(List<?> value) throws CodedException {
    	if (value == null || value.isEmpty() || value.size() != 2) {
    		throw new NoApplicableCodeException()
    			.at("?:QuantityRange/?:value")
    			.withMessage("The 'swe:value' element of an 'swe:QuantityRange' is not set correctly", "");
    	}
		return new RangeValue<Double>(Double.parseDouble(value.get(0).toString()), Double.parseDouble(value.get(1).toString()));
	}

	private SweText parseText(final TextType xbText) {
        final SweText sosText = new SweText();
        if (xbText.isSetValue()) {
            sosText.setValue(xbText.getValue());
        }
        return sosText;
    }

    private SweTime parseTime(final TimeType xbTime) throws OwsExceptionReport {
        final SweTime sosTime = new SweTime();
        if (xbTime.isSetValue()) {
            sosTime.setValue(DateTimeHelper.parseIsoString2DateTime(xbTime.getValue().toString()));
        }
        if (xbTime.getUom() != null) {
            sosTime.setUom(xbTime.getUom().getHref());
        }
        return sosTime;
    }

    private SweTimeRange parseTimeRange(final TimeRangeType xbTime) throws OwsExceptionReport {
        final SweTimeRange sosTimeRange = new SweTimeRange();
        if (xbTime.isSetValue()) {
            final List<?> value = xbTime.getValue();
            if (value != null && !value.isEmpty()) {
                final RangeValue<DateTime> range = new RangeValue<DateTime>();
                boolean first = true;
                for (final Object object : value) {
                    if (first) {
                        range.setRangeStart(DateTimeHelper.parseIsoString2DateTime(object.toString()));
                        first = false;
                    }
                    range.setRangeEnd(DateTimeHelper.parseIsoString2DateTime(object.toString()));
                }
                sosTimeRange.setValue(range);
            }
        }
        if (xbTime.getUom() != null) {
            sosTimeRange.setUom(xbTime.getUom().getHref());
        }
        return sosTimeRange;
    }

    private Collection<SweQuality> parseQuality(final XmlObject[] qualityArray) throws OwsExceptionReport {
        if (qualityArray == null || qualityArray.length == 0) {
            return null;
        }
        throw new NotYetSupportedException(SweConstants.EN_QUALITY);
    }

    private SweAbstractDataComponent parseVector(final VectorType vector) throws OwsExceptionReport {
        final SweVector sweVector = new SweVector();
        if (vector.isSetLocalFrame()) {
            sweVector.setLocalFrame(vector.getLocalFrame());
        }
        sweVector.setReferenceFrame(vector.getReferenceFrame());
        sweVector.setCoordinates(parseCoordinates(vector.getCoordinateArray()));
        return sweVector;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<SweCoordinate<?>> parseCoordinates(final Coordinate[] coordinateArray) throws OwsExceptionReport {
        final List<SweCoordinate<?>> sosCoordinates = new ArrayList<SweCoordinate<?>>(coordinateArray.length);
        for (final Coordinate xbCoordinate : coordinateArray) {
            // validate document
            XmlHelper.validateDocument(xbCoordinate);
            if (xbCoordinate.isSetQuantity()) {
                sosCoordinates.add(new SweCoordinate(xbCoordinate.getName(),
                        (SweAbstractSimpleType) parseAbstractDataComponent(xbCoordinate.getQuantity())));
            } else {
                throw new InvalidParameterValueException().at(SweConstants.EN_POSITION).withMessage(
                        "Error when parsing the Coordinates of Position: It must be of type Quantity!");
            }
        }
        return sosCoordinates;
    }

    private List<SweField> parseAnyScalarPropertyTypeArray(final AnyScalarPropertyType[] fieldArray)
            throws OwsExceptionReport {
        final List<SweField> sosFields = new ArrayList<SweField>(fieldArray.length);
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

    private SweCount parseElementCount(final CountPropertyType elementCount) throws OwsExceptionReport {
        if (elementCount.isSetCount()) {
            return (SweCount) parseAbstractDataComponent(elementCount.getCount());
        }
        return null;
    }
}
