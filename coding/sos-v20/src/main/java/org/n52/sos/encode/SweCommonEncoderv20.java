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
package org.n52.sos.encode;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import net.opengis.swe.x20.AbstractDataComponentType;
import net.opengis.swe.x20.AbstractEncodingDocument;
import net.opengis.swe.x20.AbstractEncodingType;
import net.opengis.swe.x20.BooleanType;
import net.opengis.swe.x20.CategoryType;
import net.opengis.swe.x20.CountType;
import net.opengis.swe.x20.DataArrayDocument;
import net.opengis.swe.x20.DataArrayPropertyType;
import net.opengis.swe.x20.DataArrayType;
import net.opengis.swe.x20.DataArrayType.ElementType;
import net.opengis.swe.x20.DataRecordDocument;
import net.opengis.swe.x20.DataRecordPropertyType;
import net.opengis.swe.x20.DataRecordType;
import net.opengis.swe.x20.DataRecordType.Field;
import net.opengis.swe.x20.QuantityRangeType;
import net.opengis.swe.x20.QuantityType;
import net.opengis.swe.x20.Reference;
import net.opengis.swe.x20.TextEncodingDocument;
import net.opengis.swe.x20.TextEncodingType;
import net.opengis.swe.x20.TextType;
import net.opengis.swe.x20.TimeRangeType;
import net.opengis.swe.x20.TimeType;
import net.opengis.swe.x20.UnitReference;
import net.opengis.swe.x20.VectorType;
import net.opengis.swe.x20.VectorType.Coordinate;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.oxf.xml.NcNameResolver;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
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
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class SweCommonEncoderv20 extends AbstractXmlEncoder<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SweCommonEncoderv20.class);

    private static final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(SweConstants.NS_SWE_20,
            SweCoordinate.class, SweAbstractSimpleType.class, SweAbstractEncoding.class,
            SweAbstractDataComponent.class, SweDataArray.class);

    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet(ConformanceClasses.SWE_V2_CORE,
            ConformanceClasses.SWE_V2_UML_SIMPLE_COMPONENTS, ConformanceClasses.SWE_V2_UML_RECORD_COMPONENTS,
            ConformanceClasses.SWE_V2_UML_BLOCK_ENCODINGS, ConformanceClasses.SWE_V2_UML_SIMPLE_ENCODINGS,
            ConformanceClasses.SWE_V2_XSD_SIMPLE_COMPONENTS, ConformanceClasses.SWE_V2_XSD_RECORD_COMPONENTS,
            ConformanceClasses.SWE_V2_XSD_BLOCK_COMPONENTS, ConformanceClasses.SWE_V2_XSD_SIMPLE_ENCODINGS,
            ConformanceClasses.SWE_V2_GENERAL_ENCODING_RULES, ConformanceClasses.SWE_V2_TEXT_ENCODING_RULES);

    public SweCommonEncoderv20() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(SweConstants.NS_SWE_20, SweConstants.NS_SWE_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SwesConstants.SWES_20_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(final Object sosSweType, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject encodedObject = null;
        if (sosSweType instanceof SweCoordinate) {
            encodedObject = createCoordinate((SweCoordinate) sosSweType);
        } else if (sosSweType instanceof SweAbstractEncoding) {
            encodedObject = createAbstractEncoding((SweAbstractEncoding) sosSweType, additionalValues);
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                if (encodedObject instanceof TextEncodingType) {
                    final TextEncodingDocument textEncodingDoc = TextEncodingDocument.Factory.newInstance();
                    textEncodingDoc.setTextEncoding((TextEncodingType) encodedObject);
                    encodedObject = textEncodingDoc;
                } else {
                    final AbstractEncodingDocument abstractEncodingDoc =
                            AbstractEncodingDocument.Factory.newInstance();
                    abstractEncodingDoc.setAbstractEncoding((AbstractEncodingType) encodedObject);
                    return abstractEncodingDoc;
                }
            }
        } else if (sosSweType instanceof SweAbstractDataComponent) {
            encodedObject = createAbstractDataComponent((SweAbstractDataComponent) sosSweType, additionalValues);
        } else if (sosSweType instanceof SweDataArray) {
            final DataArrayType dataArrayType = createDataArray((SweDataArray) sosSweType);
            if (additionalValues.containsKey(HelperValues.FOR_OBSERVATION)) {
                final DataArrayPropertyType dataArrayProperty =
                        DataArrayPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                dataArrayProperty.setDataArray1(dataArrayType);
                encodedObject = dataArrayProperty;
            }
            encodedObject = dataArrayType;
        } else {
            throw new UnsupportedEncoderInputException(this, sosSweType);
        }
        LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject createAbstractDataComponent(final SweAbstractDataComponent sosSweAbstractDataComponent,
            final Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (sosSweAbstractDataComponent == null) {
            throw new UnsupportedEncoderInputException(this, sosSweAbstractDataComponent);
        }
        AbstractDataComponentType abstractDataComponentType = null;
        if (sosSweAbstractDataComponent instanceof SweAbstractSimpleType) {
            abstractDataComponentType = createSimpleType((SweAbstractSimpleType) sosSweAbstractDataComponent);
        } else if (sosSweAbstractDataComponent instanceof SweVector) {
            abstractDataComponentType = createVector((SweVector) sosSweAbstractDataComponent);
        } else if (sosSweAbstractDataComponent instanceof SweDataRecord) {
            abstractDataComponentType = createDataRecord((SweDataRecord) sosSweAbstractDataComponent);
        } else if (sosSweAbstractDataComponent instanceof SweDataArray) {
            abstractDataComponentType = createDataArray((SweDataArray) sosSweAbstractDataComponent);
        } else if ((sosSweAbstractDataComponent.getXml() != null) && !sosSweAbstractDataComponent.getXml().isEmpty()) {
            try {
                return XmlObject.Factory.parse(sosSweAbstractDataComponent.getXml());
            } catch (final XmlException ex) {
                throw new XmlDecodingException(SweAbstractDataComponent.class.getName(),
                        sosSweAbstractDataComponent.getXml(), ex);
            }
        } else {
            throw new NotYetSupportedException(SweAbstractDataComponent.class.getName(), sosSweAbstractDataComponent);
        }
        // add AbstractDataComponentType information
        if (abstractDataComponentType != null) {
            if (sosSweAbstractDataComponent.isSetDefinition()) {
                abstractDataComponentType.setDefinition(sosSweAbstractDataComponent.getDefinition());
            }
            if (sosSweAbstractDataComponent.isSetDescription()) {
                abstractDataComponentType.setDescription(sosSweAbstractDataComponent.getDescription());
            }
            if (sosSweAbstractDataComponent.isSetIdentifier()) {
                abstractDataComponentType.setIdentifier(sosSweAbstractDataComponent.getIdentifier());
            }
            if (sosSweAbstractDataComponent.isSetLabel()) {
                abstractDataComponentType.setLabel(sosSweAbstractDataComponent.getLabel());
            }
        }
        if ((abstractDataComponentType instanceof DataArrayType)
                && additionalValues.containsKey(HelperValues.FOR_OBSERVATION)) {
            final DataArrayPropertyType dataArrayProperty = DataArrayPropertyType.Factory.newInstance();
            dataArrayProperty.setDataArray1((DataArrayType) abstractDataComponentType);
            return dataArrayProperty;
        }
        if ((abstractDataComponentType instanceof DataRecordType)) {
            if (additionalValues.containsKey(HelperValues.FOR_OBSERVATION)) {
                final DataRecordPropertyType dataRecordProperty = DataRecordPropertyType.Factory.newInstance();
                dataRecordProperty.setDataRecord((DataRecordType) abstractDataComponentType);
                return dataRecordProperty;
            }
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                final DataRecordDocument dataRecordDoc = DataRecordDocument.Factory.newInstance();
                dataRecordDoc.setDataRecord((DataRecordType) abstractDataComponentType);
                return dataRecordDoc;
            }

        }
        return abstractDataComponentType;
    }

    private DataRecordType createDataRecord(final SweDataRecord sosDataRecord) throws OwsExceptionReport {
        final List<SweField> sosFields = sosDataRecord.getFields();
        final DataRecordType xbDataRecord = DataRecordType.Factory.newInstance();
        if (sosFields != null) {
            final ArrayList<Field> xbFields = new ArrayList<DataRecordType.Field>(sosFields.size());
            for (final SweField sosSweField : sosFields) {
                if (sosSweField != null) {
                    final Field xbField = createField(sosSweField);
                    xbFields.add(xbField);
                } else {
                    LOGGER.error("sosSweField is null is sosDataRecord");
                }
            }
            xbDataRecord.setFieldArray(xbFields.toArray(new Field[xbFields.size()]));
        } else {
            LOGGER.error("sosDataRecord contained no fields");
        }
        return xbDataRecord;
    }

    private DataArrayType createDataArray(final SweDataArray sosDataArray) throws OwsExceptionReport {
        if (sosDataArray != null) {
            if (sosDataArray.isSetXml()) {
                try {
                    XmlObject parse = XmlObject.Factory.parse(sosDataArray.getXml());
                    if (parse instanceof DataArrayType) {
                        return (DataArrayType) parse;
                    } else if (parse instanceof DataArrayDocument) {
                        return ((DataArrayDocument) parse).getDataArray1();
                    }
                } catch (XmlException e) {
                    LOGGER.warn("Error while parsing XML representation of DataArray^", e);
                }
            }

            final DataArrayType xbDataArray =
                    DataArrayType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (sosDataArray.isSetElementCount()) {
                xbDataArray.addNewElementCount().setCount(createCount(sosDataArray.getElementCount()));
            } else {
                xbDataArray.addNewElementCount().addNewCount();
            }
            if (sosDataArray.isSetElementTyp()) {
                final ElementType elementType = xbDataArray.addNewElementType();
                if (sosDataArray.getElementType().isSetDefinition()) {
                    elementType.setName(sosDataArray.getElementType().getDefinition());
                } else {
                    elementType.setName("Components");
                }

                elementType.addNewAbstractDataComponent().set(
                        createDataRecord((SweDataRecord) sosDataArray.getElementType()));
                // TODO allow other dataComponents.
                elementType.getAbstractDataComponent().substitute(
                        new QName(SweConstants.NS_SWE_20, SweConstants.EN_DATA_RECORD, SweConstants.NS_SWE_PREFIX),
                        DataRecordType.type);
            }
            if (sosDataArray.isSetEncoding()) {
                xbDataArray.addNewEncoding().addNewAbstractEncoding();
                xbDataArray
                        .getEncoding()
                        .getAbstractEncoding()
                        .set(createAbstractEncoding(sosDataArray.getEncoding(),
                                Maps.<HelperValues, String> newEnumMap(HelperValues.class)));
                xbDataArray
                        .getEncoding()
                        .getAbstractEncoding()
                        .substitute(
                                new QName(SweConstants.NS_SWE_20, SweConstants.EN_TEXT_ENCODING,
                                        SweConstants.NS_SWE_PREFIX), TextEncodingType.type);
            }
            if (sosDataArray.isSetValues()) {
                xbDataArray.addNewValues().set(createValues(sosDataArray.getValues(), sosDataArray.getEncoding()));
            }
            return xbDataArray;
        }
        return null;
    }

    private XmlString createValues(final List<List<String>> values, final SweAbstractEncoding encoding) {
        // TODO How to deal with the decimal separator - is it an issue here?
        final StringBuilder valueStringBuilder = new StringBuilder(256);
        final SweTextEncoding textEncoding = (SweTextEncoding) encoding;
        final String tokenSeparator = textEncoding.getTokenSeparator();
        final String blockSeparator = textEncoding.getBlockSeparator();
        for (final List<String> block : values) {
            final StringBuilder blockStringBuilder = new StringBuilder();
            for (final String token : block) {
                blockStringBuilder.append(token);
                blockStringBuilder.append(tokenSeparator);
            }
            String blockString = blockStringBuilder.toString();
            // remove last token sep
            blockString = blockString.substring(0, blockString.lastIndexOf(tokenSeparator));
            valueStringBuilder.append(blockString);
            valueStringBuilder.append(blockSeparator);
        }
        String valueString = valueStringBuilder.toString();
        // remove last block sep
        valueString = valueString.substring(0, valueString.lastIndexOf(blockSeparator));
        // create XB result object
        final XmlString xbValueString = XmlString.Factory.newInstance();
        xbValueString.setStringValue(valueString);
        return xbValueString;
    }

    private DataRecordType.Field createField(final SweField sweField) throws OwsExceptionReport {
        final SweAbstractDataComponent sosElement = sweField.getElement();
        LOGGER.trace("sweField: {}, sosElement: {}", sweField, sosElement);
        final DataRecordType.Field xbField =
                DataRecordType.Field.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (sweField.isSetName()) {
            xbField.setName(NcNameResolver.fixNcName(sweField.getName().getValue()));
        }
        final AbstractDataComponentType xbDCD = xbField.addNewAbstractDataComponent();
        xbDCD.set(createAbstractDataComponent(sosElement, new EnumMap<SosConstants.HelperValues, String>(
                HelperValues.class)));
        if (sosElement instanceof SweBoolean) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_BOOLEAN_SWE_200, BooleanType.type);
        } else if (sosElement instanceof SweCategory) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_CATEGORY_SWE_200, CategoryType.type);
        } else if (sosElement instanceof SweCount) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_COUNT_SWE_200, CountType.type);
        } else if (sosElement instanceof SweQuantity) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_QUANTITY_SWE_200, QuantityType.type);
        } else if (sosElement instanceof SweText) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_TEXT_SWE_200, TextType.type);
        } else if (sosElement instanceof SweTimeRange) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_TIME_RANGE_SWE_200, TimeRangeType.type);
        } else if (sosElement instanceof SweTime) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_TIME_SWE_200, TimeType.type);
        } else if (sosElement instanceof SweDataArray) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_DATA_ARRAY_SWE_200, DataArrayType.type);
        } else if (sosElement instanceof SweDataRecord) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_DATA_RECORD_SWE_200, DataRecordType.type);
        } else if (sosElement instanceof SweVector) {
            xbField.getAbstractDataComponent().substitute(SweConstants.QN_VECTOR_SWE_200, VectorType.type);
        } else {
            throw new NotYetSupportedException(SweAbstractDataComponent.class.getName(), sosElement);
        }
        return xbField;
    }

    /*
     * 
     * SIMPLE TYPES
     */
    private AbstractDataComponentType createSimpleType(final SweAbstractSimpleType<?> sosSimpleType)
            throws OwsExceptionReport {
        if (sosSimpleType instanceof SweBoolean) {
            return createBoolean((SweBoolean) sosSimpleType);
        } else if (sosSimpleType instanceof SweCategory) {
            return createCategory((SweCategory) sosSimpleType);
        } else if (sosSimpleType instanceof SweCount) {
            return createCount((SweCount) sosSimpleType);
        } else if (sosSimpleType instanceof SweObservableProperty) {
            return createObservableProperty((SweObservableProperty) sosSimpleType);
        } else if (sosSimpleType instanceof SweQuantity) {
            return createQuantity((SweQuantity) sosSimpleType);
        } else if (sosSimpleType instanceof SweQuantityRange) {
            return createQuantityRange((SweQuantityRange) sosSimpleType);
        } else if (sosSimpleType instanceof SweText) {
            return createText((SweText) sosSimpleType);
        } else if (sosSimpleType instanceof SweTimeRange) {
            return createTimeRange((SweTimeRange) sosSimpleType);
        } else if (sosSimpleType instanceof SweTime) {
            return createTime((SweTime) sosSimpleType);
        }
        throw new NotYetSupportedException(SweAbstractSimpleType.class.getSimpleName(), sosSimpleType);
    }

    private BooleanType createBoolean(final SweBoolean sosElement) {
        final BooleanType xbBoolean = BooleanType.Factory.newInstance();
        if (sosElement.isSetValue()) {
            xbBoolean.setValue(sosElement.getValue());
        }
        return xbBoolean;
    }

    private CategoryType createCategory(final SweCategory sosCategory) {
        final CategoryType xbCategory =
                CategoryType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (sosCategory.getCodeSpace() != null) {
            final Reference xbCodespace = xbCategory.addNewCodeSpace();
            xbCodespace.setHref(sosCategory.getCodeSpace());
        }
        return xbCategory;
    }

    private CountType createCount(final SweCount sosCount) {
        final CountType xbCount = CountType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (sosCount.isSetValue()) {
            final BigInteger bigInt = new BigInteger(Integer.toString(sosCount.getValue().intValue()));
            xbCount.setValue(bigInt);
        }
        return xbCount;
    }

    private AbstractDataComponentType createObservableProperty(final SweObservableProperty sosSweAbstractDataComponent) {
        throw new RuntimeException("NOT YET IMPLEMENTED: encoding of swe:ObservableProperty");
    }

    protected QuantityType createQuantity(final SweQuantity quantity) {
        final QuantityType xbQuantity =
                QuantityType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (quantity.isSetAxisID()) {
            xbQuantity.setAxisID(quantity.getAxisID());
        }
        if (quantity.isSetValue()) {
            xbQuantity.setValue(Double.valueOf(quantity.getValue()));
        }
        if (quantity.isSetUom()) {
            xbQuantity.setUom(createUnitReference(quantity.getUom()));
        } else {
            xbQuantity.setUom(createUnknownUnitReference());
        }
        if (quantity.getQuality() != null) {
            // TODO implement
            logWarnQualityNotSupported(xbQuantity.schemaType());
        }
        return xbQuantity;
    }

    protected QuantityRangeType createQuantityRange(final SweQuantityRange quantityRange) {
        final QuantityRangeType xbQuantityRange =
                QuantityRangeType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (quantityRange.isSetAxisID()) {
            xbQuantityRange.setAxisID(quantityRange.getAxisID());
        }
        if (quantityRange.isSetValue()) {
            xbQuantityRange.setValue(quantityRange.getValue().getRangeAsList());
        }
        if (quantityRange.isSetUom()) {
            xbQuantityRange.setUom(createUnitReference(quantityRange.getUom()));
        } else {
            xbQuantityRange.setUom(createUnknownUnitReference());
        }
        if (quantityRange.isSetQuality()) {
            // TODO implement
            logWarnQualityNotSupported(xbQuantityRange.schemaType());
        }
        return xbQuantityRange;
    }

    private TextType createText(final SweText text) {
        final TextType xbText = TextType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (text.isSetValue()) {
            xbText.setValue(text.getValue());
        }
        return xbText;
    }

    private TimeType createTime(final SweTime sosTime) {
        final TimeType xbTime = TimeType.Factory.newInstance();
        if (sosTime.isSetValue()) {
            xbTime.setValue(sosTime.getValue());
        }
        if (sosTime.isSetUom()) {
            xbTime.setUom(createUnitReference(sosTime.getUom()));
        }
        if (sosTime.getQuality() != null) {
            // TODO implement
            logWarnQualityNotSupported(xbTime.schemaType());
        }
        return xbTime;
    }

    private TimeRangeType createTimeRange(final SweTimeRange sosTimeRange) {
        final TimeRangeType xbTimeRange = TimeRangeType.Factory.newInstance();
        if (sosTimeRange.isSetUom()) {
            xbTimeRange.addNewUom().setHref(sosTimeRange.getUom());
        }
        if (sosTimeRange.isSetValue()) {
            xbTimeRange.setValue(sosTimeRange.getValue().getRangeAsStringList());
        }
        if (sosTimeRange.isSetQuality()) {
            // TODO implement
            logWarnQualityNotSupported(xbTimeRange.schemaType());
        }
        return xbTimeRange;
    }

    private VectorType createVector(SweVector sweVector) throws OwsExceptionReport {
        final VectorType xbVector = VectorType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (sweVector.isSetReferenceFrame()) {
            xbVector.setReferenceFrame(sweVector.getReferenceFrame());
        }
        if (sweVector.isSetLocalFrame()) {
            xbVector.setLocalFrame(sweVector.getLocalFrame());
        }
        if (sweVector.isSetCoordinates()) {
            for (SweCoordinate<?> coordinate : sweVector.getCoordinates()) {
                xbVector.addNewCoordinate().set(createCoordinate(coordinate));
            }
        }
        return xbVector;
    }

    private Coordinate createCoordinate(final SweCoordinate<?> coordinate) throws OwsExceptionReport {
        final Coordinate xbCoordinate = Coordinate.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        xbCoordinate.setName(coordinate.getName());
        xbCoordinate.setQuantity((QuantityType)createAbstractDataComponent((SweQuantity) coordinate.getValue(), null));
        return xbCoordinate;
    }

    private AbstractEncodingType createAbstractEncoding(final SweAbstractEncoding sosSweAbstractEncoding,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {

        if (sosSweAbstractEncoding instanceof SweTextEncoding) {
            return createTextEncoding((SweTextEncoding) sosSweAbstractEncoding);
        }

        try {
            if ((sosSweAbstractEncoding.getXml() != null) && !sosSweAbstractEncoding.getXml().isEmpty()) {
                final XmlObject xmlObject = XmlObject.Factory.parse(sosSweAbstractEncoding.getXml());
                if (xmlObject instanceof AbstractEncodingType) {
                    return (AbstractEncodingType) xmlObject;
                }
            }
            throw new NoApplicableCodeException().withMessage("AbstractEncoding can not be encoded!");
        } catch (final XmlException e) {
            throw new NoApplicableCodeException().withMessage("Error while encoding AbstractEncoding!");
        }
    }

    private TextEncodingType createTextEncoding(final SweTextEncoding sosTextEncoding) {
        final TextEncodingType xbTextEncoding =
                TextEncodingType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (sosTextEncoding.getBlockSeparator() != null) {
            xbTextEncoding.setBlockSeparator(sosTextEncoding.getBlockSeparator());
        }
        if (sosTextEncoding.isSetCollapseWhiteSpaces()) {
            xbTextEncoding.setCollapseWhiteSpaces(sosTextEncoding.isCollapseWhiteSpaces());
        }
        if (sosTextEncoding.getDecimalSeparator() != null) {
            xbTextEncoding.setDecimalSeparator(sosTextEncoding.getDecimalSeparator());
        }
        if (sosTextEncoding.getTokenSeparator() != null) {
            xbTextEncoding.setTokenSeparator(sosTextEncoding.getTokenSeparator());
        }
        return xbTextEncoding;
    }

    private UnitReference createUnitReference(final String uom) {
        final UnitReference unitReference =
                UnitReference.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (uom.startsWith("urn:") || uom.startsWith("http://")) {
            unitReference.setHref(uom);
        } else {
            unitReference.setCode(uom);
        }
        return unitReference;
    }
    
    private UnitReference createUnknownUnitReference() {
        final UnitReference unitReference =
                UnitReference.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        unitReference.setHref(OGCConstants.UNKNOWN);
        return unitReference;
    }

    private void logWarnQualityNotSupported(SchemaType schemaType) {
        LOGGER.warn("Quality encoding is not supported for {}", schemaType);
    }
}
