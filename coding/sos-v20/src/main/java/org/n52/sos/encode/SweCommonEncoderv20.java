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
package org.n52.sos.encode;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.joda.time.DateTime;
import org.n52.oxf.xml.NcNameResolver;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.RangeValue;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweAbstractDataRecord;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweCoordinate;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweVector;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ogc.swe.simpleType.SweAllowedTimes;
import org.n52.sos.ogc.swe.simpleType.SweAllowedTokens;
import org.n52.sos.ogc.swe.simpleType.SweAllowedValues;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweQuality;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.xlink.Referenceable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x1999.xlink.ActuateType;
import org.w3.x1999.xlink.ShowType;
import org.w3.x1999.xlink.TypeType;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.opengis.swe.x20.AbstractDataComponentType;
import net.opengis.swe.x20.AbstractEncodingDocument;
import net.opengis.swe.x20.AbstractEncodingType;
import net.opengis.swe.x20.AllowedTimesPropertyType;
import net.opengis.swe.x20.AllowedTimesType;
import net.opengis.swe.x20.AllowedTokensPropertyType;
import net.opengis.swe.x20.AllowedTokensType;
import net.opengis.swe.x20.AllowedValuesPropertyType;
import net.opengis.swe.x20.AllowedValuesType;
import net.opengis.swe.x20.BooleanType;
import net.opengis.swe.x20.CategoryType;
import net.opengis.swe.x20.CountType;
import net.opengis.swe.x20.DataArrayDocument;
import net.opengis.swe.x20.DataArrayPropertyType;
import net.opengis.swe.x20.DataArrayType;
import net.opengis.swe.x20.DataArrayType.ElementType;
import net.opengis.swe.x20.DataArrayType.Encoding;
import net.opengis.swe.x20.DataRecordDocument;
import net.opengis.swe.x20.DataRecordPropertyType;
import net.opengis.swe.x20.DataRecordType;
import net.opengis.swe.x20.DataRecordType.Field;
import net.opengis.swe.x20.QualityPropertyType;
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

    @SuppressWarnings("rawtypes")
    @Override
    public XmlObject encode(final Object sosSweType, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject encodedObject = null;
        if (sosSweType instanceof SweCoordinate) {
            encodedObject = createCoordinate((SweCoordinate) sosSweType);
        } else if (sosSweType instanceof SweAbstractEncoding) {
            encodedObject = createAbstractEncoding((SweAbstractEncoding) sosSweType);
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                if (encodedObject instanceof TextEncodingType) {
                    final TextEncodingDocument textEncodingDoc = TextEncodingDocument.Factory.newInstance(getXmlOptions());
                    textEncodingDoc.setTextEncoding((TextEncodingType) encodedObject);
                    encodedObject = textEncodingDoc;
                } else {
                    final AbstractEncodingDocument abstractEncodingDoc =
                            AbstractEncodingDocument.Factory.newInstance(getXmlOptions());
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
                        DataArrayPropertyType.Factory.newInstance(getXmlOptions());
                dataArrayProperty.setDataArray1(dataArrayType);
                encodedObject = dataArrayProperty;
            }
            encodedObject = dataArrayType;
        } else {
            throw new UnsupportedEncoderInputException(this, sosSweType);
        }
        if (LOGGER.isTraceEnabled()) {
        	LOGGER.trace("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                    XmlHelper.validateDocument(encodedObject));
        }
        return encodedObject;
    }

    @SuppressWarnings("rawtypes")
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
        } else if (sosSweAbstractDataComponent instanceof SweAbstractDataRecord) {
            abstractDataComponentType = createDataRecord((SweAbstractDataRecord) sosSweAbstractDataComponent);
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
        if (abstractDataComponentType instanceof DataArrayType) {
            if (additionalValues.containsKey(HelperValues.FOR_OBSERVATION)
                    || additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
                final DataArrayPropertyType dataArrayProperty =
                        DataArrayPropertyType.Factory.newInstance(getXmlOptions());
                dataArrayProperty.setDataArray1((DataArrayType) abstractDataComponentType);
                return dataArrayProperty;
            }
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                final DataArrayDocument dataArrayDoc = DataArrayDocument.Factory.newInstance(getXmlOptions());
                dataArrayDoc.setDataArray1((DataArrayType) abstractDataComponentType);
                return dataArrayDoc;
            }
        }
        if ((abstractDataComponentType instanceof DataRecordType)) {
            if (additionalValues.containsKey(HelperValues.FOR_OBSERVATION)
                    || additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
                final DataRecordPropertyType dataRecordProperty =
                        DataRecordPropertyType.Factory.newInstance(getXmlOptions());
                dataRecordProperty.setDataRecord((DataRecordType) abstractDataComponentType);
                return dataRecordProperty;
            }
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                final DataRecordDocument dataRecordDoc = DataRecordDocument.Factory.newInstance(getXmlOptions());
                dataRecordDoc.setDataRecord((DataRecordType) abstractDataComponentType);
                return dataRecordDoc;
            }

        }
        return abstractDataComponentType;
    }

    private DataRecordType createDataRecord(final SweAbstractDataRecord sosDataRecord) throws OwsExceptionReport {
        final List<SweField> sosFields = sosDataRecord.getFields();
        final DataRecordType xbDataRecord = DataRecordType.Factory.newInstance(getXmlOptions());
        if (sosFields != null) {
            final ArrayList<Field> xbFields = new ArrayList<>(sosFields.size());
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
                    DataArrayType.Factory.newInstance(getXmlOptions());
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

                elementType.addNewAbstractDataComponent().set(createDataRecord((SweAbstractDataRecord) sosDataArray.getElementType()));
                elementType.getAbstractDataComponent().substitute(SweConstants.QN_DATA_RECORD_SWE_200, DataRecordType.type);
            }
            if (sosDataArray.isSetEncoding()) {
                Encoding xbEncoding = xbDataArray.addNewEncoding();
                xbEncoding.setAbstractEncoding(createAbstractEncoding(sosDataArray.getEncoding()));
                xbEncoding.getAbstractEncoding().substitute(SweConstants.QN_TEXT_ENCODING_SWE_200, TextEncodingType.type);
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
        final XmlString xbValueString = XmlString.Factory.newInstance(getXmlOptions());
        xbValueString.setStringValue(valueString);
        return xbValueString;
    }

    private DataRecordType.Field createField(final SweField sweField) throws OwsExceptionReport {
        final SweAbstractDataComponent sosElement = sweField.getElement();
        LOGGER.trace("sweField: {}, sosElement: {}", sweField, sosElement);
        final DataRecordType.Field xbField =
                DataRecordType.Field.Factory.newInstance(getXmlOptions());
        if (sweField.isSetName()) {
            xbField.setName(NcNameResolver.fixNcName(sweField.getName().getValue()));
        }
        if (sosElement != null) {
            final XmlObject encodeObjectToXml = createAbstractDataComponent(sosElement, new EnumMap<SosConstants.HelperValues, String>(
                    HelperValues.class));
            XmlObject substituteElement =
                    XmlHelper.substituteElement(xbField.addNewAbstractDataComponent(), encodeObjectToXml);
            substituteElement.set(encodeObjectToXml);
        }
        return xbField;
    }

    /*
     *
     * SIMPLE TYPES
     */
    private AbstractDataComponentType createSimpleType(final SweAbstractSimpleType<?> sosSimpleType) throws OwsExceptionReport {
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

    private BooleanType createBoolean(final SweBoolean sosElement) throws OwsExceptionReport {
        final BooleanType xbBoolean = BooleanType.Factory.newInstance(getXmlOptions());
        if (sosElement.isSetValue()) {
            xbBoolean.setValue(sosElement.getValue());
        }
        if (sosElement.isSetQuality()) {
            xbBoolean.setQualityArray(createQuality(sosElement.getQuality()));
        }
        return xbBoolean;
    }

    private CategoryType createCategory(final SweCategory sosCategory) throws OwsExceptionReport {
        final CategoryType xbCategory =
                CategoryType.Factory.newInstance(getXmlOptions());
        if (sosCategory.getCodeSpace() != null) {
            final Reference xbCodespace = xbCategory.addNewCodeSpace();
            xbCodespace.setHref(sosCategory.getCodeSpace());
        }
        if (sosCategory.isSetValue()) {
            xbCategory.setValue(sosCategory.getValue());
        }
        if (sosCategory.isSetContstraint()) {
            createConstraint(xbCategory.addNewConstraint(), sosCategory.getConstraint());
        }
        if (sosCategory.isSetQuality()) {
            xbCategory.setQualityArray(createQuality(sosCategory.getQuality()));
        }
        return xbCategory;
    }

    private CountType createCount(final SweCount sosCount) throws OwsExceptionReport {
        final CountType xbCount = CountType.Factory.newInstance(getXmlOptions());
        if (sosCount.isSetValue()) {
            final BigInteger bigInt = new BigInteger(Integer.toString(sosCount.getValue().intValue()));
            xbCount.setValue(bigInt);
        }
        if (sosCount.isSetContstraint()) {
            createConstraint(xbCount.addNewConstraint(), sosCount.getConstraint());
        }
        if (sosCount.isSetQuality()) {
            xbCount.setQualityArray(createQuality(sosCount.getQuality()));
        }
        return xbCount;
    }

    private AbstractDataComponentType createObservableProperty(final SweObservableProperty sosSweAbstractDataComponent) {
        throw new RuntimeException("NOT YET IMPLEMENTED: encoding of swe:ObservableProperty");
    }

    protected QuantityType createQuantity(final SweQuantity quantity) throws OwsExceptionReport {
        final QuantityType xbQuantity =
                QuantityType.Factory.newInstance(getXmlOptions());
        if (quantity.isSetAxisID()) {
            xbQuantity.setAxisID(quantity.getAxisID());
        }
        if (quantity.isSetValue()) {
            xbQuantity.setValue(quantity.getValue());
        }
        if (quantity.isSetUom()) {
            xbQuantity.setUom(createUnitReference(quantity.getUomObject()));
        } else {
            xbQuantity.setUom(createUnknownUnitReference());
        }
        if (quantity.isSetQuality()) {
            xbQuantity.setQualityArray(createQuality(quantity.getQuality()));
        }
        if (quantity.isSetContstraint()) {
            createConstraint(xbQuantity.addNewConstraint(), quantity.getConstraint());
        }
        return xbQuantity;
    }

    protected QuantityRangeType createQuantityRange(final SweQuantityRange quantityRange) throws OwsExceptionReport {
        final QuantityRangeType xbQuantityRange =
                QuantityRangeType.Factory.newInstance(getXmlOptions());
        if (quantityRange.isSetAxisID()) {
            xbQuantityRange.setAxisID(quantityRange.getAxisID());
        }
        if (quantityRange.isSetValue()) {
            xbQuantityRange.setValue(quantityRange.getValue().getRangeAsList());
        }
        if (quantityRange.isSetUom()) {
            xbQuantityRange.setUom(createUnitReference(quantityRange.getUomObject()));
        } else {
            xbQuantityRange.setUom(createUnknownUnitReference());
        }
        if (quantityRange.isSetQuality()) {
            xbQuantityRange.setQualityArray(createQuality(quantityRange.getQuality()));
        }
        if (quantityRange.isSetContstraint()) {
            createConstraint(xbQuantityRange.addNewConstraint(), quantityRange.getConstraint());
        }
        return xbQuantityRange;
    }

    private TextType createText(final SweText text) {
        final TextType xbText = TextType.Factory.newInstance(getXmlOptions());
        if (text.isSetValue()) {
            xbText.setValue(text.getValue());
        }
        if (text.isSetContstraint()) {
            createConstraint(xbText.addNewConstraint(), text.getConstraint());
        }
        return xbText;
    }

    private TimeType createTime(final SweTime sosTime) throws OwsExceptionReport {
        final TimeType xbTime = TimeType.Factory.newInstance(getXmlOptions());
        if (sosTime.isSetValue()) {
            xbTime.setValue(DateTimeHelper.formatDateTime2IsoString(sosTime.getValue()));
        }
        if (sosTime.isSetUom()) {
            xbTime.setUom(createUnitReference(sosTime.getUomObject()));
        }
        if (sosTime.isSetQuality()) {
            xbTime.setQualityArray(createQuality(sosTime.getQuality()));
        }
        if (sosTime.isSetContstraint()) {
            createConstraint(xbTime.addNewConstraint(), sosTime.getConstraint());
        }
        return xbTime;
    }

    private TimeRangeType createTimeRange(final SweTimeRange sosTimeRange) throws OwsExceptionReport {
        final TimeRangeType xbTimeRange = TimeRangeType.Factory.newInstance(getXmlOptions());
        if (sosTimeRange.isSetUom()) {
            xbTimeRange.addNewUom().setHref(sosTimeRange.getUom());
        }
        if (sosTimeRange.isSetValue()) {
            xbTimeRange.setValue(sosTimeRange.getValue().getRangeAsStringList());
        }
        if (sosTimeRange.isSetQuality()) {
            xbTimeRange.setQualityArray(createQuality(sosTimeRange.getQuality()));
        }
        if (sosTimeRange.isSetContstraint()) {
            createConstraint(xbTimeRange.addNewConstraint(), sosTimeRange.getConstraint());
        }
        return xbTimeRange;
    }

    private AllowedValuesPropertyType createConstraint(AllowedValuesPropertyType avpt,
            Referenceable<SweAllowedValues> constraint) {
        if (constraint.isInstance()) {
            createAllowedValues(avpt.addNewAllowedValues(), constraint.getInstance());
        } else if (constraint.isReference()) {
            org.n52.sos.w3c.xlink.Reference ref = constraint.getReference();
            if (ref.getHref().isPresent()) {
                avpt.setHref(ref.getHref().get().toString());
            }
            if (ref.getTitle().isPresent()) {
                avpt.setTitle(ref.getTitle().get());
            }
            if (ref.getActuate().isPresent()) {
                avpt.setActuate(ActuateType.Enum.forString(ref.getActuate().get()));
            }
            if (ref.getArcrole().isPresent()) {
                avpt.setArcrole(ref.getArcrole().get());
            }
            if (ref.getRole().isPresent()) {
                avpt.setRole(ref.getRole().get());
            }
            if (ref.getShow().isPresent()) {
                avpt.setShow(ShowType.Enum.forString(ref.getShow().get()));
            }
            if (ref.getType().isPresent()) {
                avpt.setType(TypeType.Enum.forString(ref.getType().get()));
            }
        }
        return avpt;
    }

    private AllowedValuesType createAllowedValues(AllowedValuesType avt, Nillable<SweAllowedValues> instance) {
        if (instance.isPresent()) {
            if (instance.get().isSetGmlID()) {
                avt.setId(instance.get().getGmlId());
            }
            if (instance.get().isSetValue()) {
                for (Double value : instance.get().getValue()) {
                    avt.addNewValue().setDoubleValue(value);
                }          
            }
            if (instance.get().isSetInterval()) {
                for (RangeValue<Double> interval : instance.get().getInterval()) {
                    avt.addInterval(interval.getRangeAsList());
                }
            }
            if (instance.get().isSetSignificantFigures()) {
                avt.setSignificantFigures(instance.get().getSignificantFigures());
            }
        }
        return avt;
    }

    private AllowedTokensPropertyType createConstraint(AllowedTokensPropertyType atpt,
            Referenceable<SweAllowedTokens> constraint) {
        if (constraint.isInstance()) {
            createAllowedTokens(atpt.addNewAllowedTokens(), constraint.getInstance());
        } else if (constraint.isReference()) {
            org.n52.sos.w3c.xlink.Reference ref = constraint.getReference();
            if (ref.getHref().isPresent()) {
                atpt.setHref(ref.getHref().get().toString());
            }
            if (ref.getTitle().isPresent()) {
                atpt.setTitle(ref.getTitle().get());
            }
            if (ref.getActuate().isPresent()) {
                atpt.setActuate(ActuateType.Enum.forString(ref.getActuate().get()));
            }
            if (ref.getArcrole().isPresent()) {
                atpt.setArcrole(ref.getArcrole().get());
            }
            if (ref.getRole().isPresent()) {
                atpt.setRole(ref.getRole().get());
            }
            if (ref.getShow().isPresent()) {
                atpt.setShow(ShowType.Enum.forString(ref.getShow().get()));
            }
            if (ref.getType().isPresent()) {
                atpt.setType(TypeType.Enum.forString(ref.getType().get()));
            }
        }
        return atpt;
    }

    private AllowedTokensType createAllowedTokens(AllowedTokensType att, Nillable<SweAllowedTokens> instance) {
        if (instance.isPresent()) {
            if (instance.get().isSetGmlID()) {
                att.setId(instance.get().getGmlId());
            }
            if (instance.get().isSetValue()) {
                for (String value : instance.get().getValue()) {
                    att.addNewValue().setStringValue(value);
                }          
            }
            if (instance.get().isSetPattern()) {
                att.setPattern(instance.get().getPattern());
            }
        }
        return att;
    }

    private AllowedTimesPropertyType createConstraint(AllowedTimesPropertyType atpt,
            Referenceable<SweAllowedTimes> constraint) {
        if (constraint.isInstance()) {
            createAllowedTimes(atpt.addNewAllowedTimes(), constraint.getInstance());
        } else if (constraint.isReference()) {
            org.n52.sos.w3c.xlink.Reference ref = constraint.getReference();
            if (ref.getHref().isPresent()) {
                atpt.setHref(ref.getHref().get().toString());
            }
            if (ref.getTitle().isPresent()) {
                atpt.setTitle(ref.getTitle().get());
            }
            if (ref.getActuate().isPresent()) {
                atpt.setActuate(ActuateType.Enum.forString(ref.getActuate().get()));
            }
            if (ref.getArcrole().isPresent()) {
                atpt.setArcrole(ref.getArcrole().get());
            }
            if (ref.getRole().isPresent()) {
                atpt.setRole(ref.getRole().get());
            }
            if (ref.getShow().isPresent()) {
                atpt.setShow(ShowType.Enum.forString(ref.getShow().get()));
            }
            if (ref.getType().isPresent()) {
                atpt.setType(TypeType.Enum.forString(ref.getType().get()));
            }
        }
        return atpt;
    }

    private AllowedTimesType createAllowedTimes(AllowedTimesType att, Nillable<SweAllowedTimes> instance) {
        if (instance.isPresent()) {
            if (instance.get().isSetGmlID()) {
                att.setId(instance.get().getGmlId());
            }
            if (instance.get().isSetValue()) {
                for (DateTime value : instance.get().getValue()) {
                    att.addNewValue().setStringValue(DateTimeHelper.formatDateTime2IsoString(value));
                }          
            }
            if (instance.get().isSetInterval()) {
                for (RangeValue<DateTime> interval : instance.get().getInterval()) {
                    List<String> list = Lists.newArrayListWithCapacity(2);
                    list.add(DateTimeHelper.formatDateTime2IsoString(interval.getRangeStart()));
                    if (interval.isSetEndValue()) {
                        list.add(DateTimeHelper.formatDateTime2IsoString(interval.getRangeEnd()));
                    }
                    att.addInterval(list);
                }
            }
            if (instance.get().isSetSignificantFigures()) {
                att.setSignificantFigures(instance.get().getSignificantFigures());
            }
        }
        return att;
    }
    
    private QualityPropertyType[] createQuality(final Collection<SweQuality> quality) throws OwsExceptionReport {
        if (!quality.isEmpty()) {
            final ArrayList<QualityPropertyType> xbQualities = Lists.newArrayListWithCapacity(quality.size());
            for (final SweQuality sweQuality : quality) {
                final QualityPropertyType xbQuality = QualityPropertyType.Factory.newInstance();
                if (sweQuality instanceof SweText) {
                    xbQuality.addNewText().set(createText((SweText) sweQuality));
                } else if (sweQuality instanceof SweCategory) {
                    xbQuality.addNewCategory().set(createCategory((SweCategory) sweQuality));
                } else if (sweQuality instanceof SweQuantity) {
                    xbQuality.addNewQuantity().set(createQuantity((SweQuantity) sweQuality));
                } else if (sweQuality instanceof SweQuantityRange) {
                    xbQuality.addNewQuantityRange().set(createQuantityRange((SweQuantityRange) sweQuality));
                }
                xbQualities.add(xbQuality);
            }
            return xbQualities.toArray(new QualityPropertyType[xbQualities.size()]);
        }
        final QualityPropertyType[] result = { QualityPropertyType.Factory.newInstance() };
        return result;
    }

    private VectorType createVector(SweVector sweVector) throws OwsExceptionReport {
        final VectorType xbVector = VectorType.Factory.newInstance(getXmlOptions());
        if (sweVector.isSetReferenceFrame()) {
            xbVector.setReferenceFrame(sweVector.getReferenceFrame());
        }
        if (sweVector.isSetLocalFrame()) {
            xbVector.setLocalFrame(sweVector.getLocalFrame());
        }
        if (sweVector.isSetCoordinates()) {
            for (SweCoordinate<?> coordinate : sweVector.getCoordinates()) {
                if (coordinate != null && coordinate.getValue() != null) {
                    xbVector.addNewCoordinate().set(createCoordinate(coordinate));
                }
            }
        }
        return xbVector;
    }

    private Coordinate createCoordinate(final SweCoordinate<?> coordinate) throws OwsExceptionReport {
        final Coordinate xbCoordinate = Coordinate.Factory.newInstance(getXmlOptions());
        xbCoordinate.setName(coordinate.getName());
        xbCoordinate.setQuantity((QuantityType)createAbstractDataComponent((SweQuantity) coordinate.getValue(), null));
        return xbCoordinate;
    }

    private AbstractEncodingType createAbstractEncoding(final SweAbstractEncoding sosSweAbstractEncoding) throws OwsExceptionReport {
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
                TextEncodingType.Factory.newInstance(getXmlOptions());
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

    private UnitReference createUnitReference(final UoM uom) {
        final UnitReference unitReference =
                UnitReference.Factory.newInstance(getXmlOptions());
        if (!uom.isSetLink() && (uom.getUom().startsWith("urn:") || uom.getUom().startsWith("http://"))) {
            unitReference.setHref(uom.getUom());
        } else {
            unitReference.setCode(uom.getUom());
        }
        if (uom.isSetName()) {
            unitReference.setTitle(uom.getName());
        }
        if (uom.isSetLink()) {
            unitReference.setHref(uom.getLink());
        }
        return unitReference;
    }
    
    private UnitReference createUnitReference(final String uom) {
        final UnitReference unitReference =
                UnitReference.Factory.newInstance(getXmlOptions());
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

    protected static XmlOptions getXmlOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }
}
