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

import static org.n52.sos.util.http.HTTPStatus.BAD_REQUEST;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import net.opengis.gml.StringOrRefType;
import net.opengis.swe.x101.AbstractDataComponentType;
import net.opengis.swe.x101.AbstractEncodingType;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.BlockEncodingPropertyType;
import net.opengis.swe.x101.CategoryDocument.Category;
import net.opengis.swe.x101.CountDocument.Count;
import net.opengis.swe.x101.DataArrayDocument;
import net.opengis.swe.x101.DataArrayType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.EnvelopeType;
import net.opengis.swe.x101.ObservablePropertyDocument.ObservableProperty;
import net.opengis.swe.x101.QualityPropertyType;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.QuantityRangeDocument.QuantityRange;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.TextBlockDocument.TextBlock;
import net.opengis.swe.x101.TextDocument.Text;
import net.opengis.swe.x101.TimeDocument.Time;
import net.opengis.swe.x101.TimeGeometricPrimitivePropertyType;
import net.opengis.swe.x101.TimeRangeDocument.TimeRange;
import net.opengis.swe.x101.UomPropertyType;
import net.opengis.swe.x101.VectorPropertyType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.xmlbeans.GDateBuilder;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.XmlValueDisconnectedException;
import org.joda.time.DateTime;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweCoordinate;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweEnvelope;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweSimpleDataRecord;
import org.n52.sos.ogc.swe.SweVector;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
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
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Encoder class for SWE Common 1.0.1
 *
 * @since 4.0.0
 */
public class SweCommonEncoderv101 extends AbstractXmlEncoder<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SweCommonEncoderv101.class);

    private static final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(SweConstants.NS_SWE_101,
            SweBoolean.class, SweCategory.class, SweCount.class, SweObservableProperty.class, SweQuantity.class,
            SweQuantityRange.class, SweText.class, SweTime.class, SweTimeRange.class, SweEnvelope.class,
            SweCoordinate.class, SweDataArray.class, SweDataRecord.class, SweSimpleDataRecord.class, TimePeriod.class);

    public SweCommonEncoderv101() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(SweConstants.NS_SWE_101, SweConstants.NS_SWE_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SweConstants.SWE_101_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(final Object element, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject encodedObject = null;
        if (element instanceof SweAbstractSimpleType) {
            encodedObject = createSimpleType((SweAbstractSimpleType<?>)element, additionalValues);
//        }
//        if (element instanceof SweBoolean) {
//            encodedObject = createBoolean((SweBoolean) element);
//        } else if (element instanceof SweCategory) {
//            encodedObject = createCategory((SweCategory) element);
//        } else if (element instanceof SweCount) {
//            encodedObject = createCount((SweCount) element);
//        } else if (element instanceof SweObservableProperty) {
//            encodedObject = createObservableProperty((SweObservableProperty) element);
//        } else if (element instanceof SweQuantity) {
//            encodedObject = createQuantity((SweQuantity) element);
//        } else if (element instanceof SweQuantityRange) {
//            encodedObject = createQuantityRange((SweQuantityRange) element);
//        } else if (element instanceof SweText) {
//            encodedObject = createText((SweText) element);
//        } else if (element instanceof SweTime) {
//            encodedObject = createTime((SweTime) element);
//        } else if (element instanceof SweTimeRange) {
//            encodedObject = createTimeRange((SweTimeRange) element);
        } else if (element instanceof SweCoordinate) {
            encodedObject = createCoordinate((SweCoordinate<?>) element);
        } else if (element instanceof SweDataArray) {
            encodedObject = createDataArray((SweDataArray) element);
        } else if (element instanceof SweDataRecord) {
            encodedObject = createDataRecord((SweDataRecord) element);
        } else if (element instanceof SweEnvelope) {
            encodedObject = createEnvelope((SweEnvelope) element);
        } else if (element instanceof SweSimpleDataRecord) {
            encodedObject = createSimpleDataRecord((SweSimpleDataRecord) element);
        } else if (element instanceof TimePeriod) {
            encodedObject = createTimeGeometricPrimitivePropertyType((TimePeriod) element);
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
        LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }
    
    private AbstractDataComponentType createSimpleType(final SweAbstractSimpleType<?> sosSimpleType)
            throws OwsExceptionReport {
        return createSimpleType(sosSimpleType, null);
    }
    
    private AbstractDataComponentType createSimpleType(final SweAbstractSimpleType<?> sosSimpleType, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        AbstractDataComponentType abstractDataComponentType = null;
        if (sosSimpleType instanceof SweBoolean) {
            abstractDataComponentType = createBoolean((SweBoolean) sosSimpleType);
        } else if (sosSimpleType instanceof SweCategory) {
            abstractDataComponentType = createCategory((SweCategory) sosSimpleType);
        } else if (sosSimpleType instanceof SweCount) {
            abstractDataComponentType = createCount((SweCount) sosSimpleType);
        } else if (sosSimpleType instanceof SweObservableProperty) {
            abstractDataComponentType = createObservableProperty((SweObservableProperty) sosSimpleType);
        } else if (sosSimpleType instanceof SweQuantity) {
            abstractDataComponentType = createQuantity((SweQuantity) sosSimpleType);
        } else if (sosSimpleType instanceof SweQuantityRange) {
            abstractDataComponentType = createQuantityRange((SweQuantityRange) sosSimpleType);
        } else if (sosSimpleType instanceof SweText) {
            abstractDataComponentType = createText((SweText) sosSimpleType);
        } else if (sosSimpleType instanceof SweTimeRange) {
            abstractDataComponentType = createTimeRange((SweTimeRange) sosSimpleType);
        } else if (sosSimpleType instanceof SweTime) {
            abstractDataComponentType = createTime((SweTime) sosSimpleType);
        } else {
            throw new NotYetSupportedException(SweAbstractSimpleType.class.getSimpleName(), sosSimpleType);
        }
        addAbstractDataComponentValues(abstractDataComponentType, sosSimpleType);
        return abstractDataComponentType;
       
    }

    private SimpleDataRecordType createSimpleDataRecord(final SweSimpleDataRecord simpleDataRecord)
            throws OwsExceptionReport {
        final SimpleDataRecordType xbSimpleDataRecord =
                SimpleDataRecordType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (simpleDataRecord.isSetDefinition()) {
            xbSimpleDataRecord.setDefinition(simpleDataRecord.getDefinition());
        }
        if (simpleDataRecord.isSetDescription()) {
            final StringOrRefType xbSoR = StringOrRefType.Factory.newInstance();
            xbSoR.setStringValue(simpleDataRecord.getDefinition());
            xbSimpleDataRecord.setDescription(xbSoR);
        }
        if (simpleDataRecord.isSetFields()) {
            final AnyScalarPropertyType[] xbFields = new AnyScalarPropertyType[simpleDataRecord.getFields().size()];
            int xbFieldIndex = 0;
            for (final SweField sweField : simpleDataRecord.getFields()) {
                final AnyScalarPropertyType xbField = createFieldForSimpleDataRecord(sweField);
                xbFields[xbFieldIndex] = xbField;
                xbFieldIndex++;
            }
            xbSimpleDataRecord.setFieldArray(xbFields);
        }
        return xbSimpleDataRecord;
    }

    private AnyScalarPropertyType createFieldForSimpleDataRecord(final SweField sweField) throws OwsExceptionReport {
        final SweAbstractDataComponent sosElement = sweField.getElement();
        final AnyScalarPropertyType xbField =
                AnyScalarPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (sweField.isSetName()) {
            xbField.setName(sweField.getName().getValue());
        }
        final AbstractDataComponentType xbDCD;
        if (sosElement instanceof SweBoolean) {
            xbDCD = xbField.addNewBoolean();
            xbDCD.set(createSimpleType((SweBoolean) sosElement));
        } else if (sosElement instanceof SweCategory) {
            xbDCD = xbField.addNewCategory();
            xbDCD.set(createSimpleType((SweCategory) sosElement));
        } else if (sosElement instanceof SweCount) {
            xbDCD = xbField.addNewCount();
            xbDCD.set(createSimpleType((SweCount) sosElement));
        } else if (sosElement instanceof SweQuantity) {
            xbDCD = xbField.addNewQuantity();
            xbDCD.set(createSimpleType((SweQuantity) sosElement));
        } else if (sosElement instanceof SweText) {
            xbDCD = xbField.addNewText();
            xbDCD.set(createSimpleType((SweText) sosElement));
        } else if (sosElement instanceof SweTime) {
            xbDCD = xbField.addNewTime();
            xbDCD.set(createSimpleType((SweTime) sosElement));
        } else {
            throw new NoApplicableCodeException().withMessage(
                    "The element type '%s' of the received %s is not supported by this encoder '%s'.",
                    sosElement != null ? sosElement.getClass().getName() : null, sweField.getClass().getName(),
                    getClass().getName()).setStatus(BAD_REQUEST);
        }
        return xbField;
    }

    private DataComponentPropertyType createField(final SweField sweField) throws OwsExceptionReport {
        final SweAbstractDataComponent sosElement = sweField.getElement();
        final DataComponentPropertyType xbField =
                DataComponentPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (sweField.isSetName()) {
            xbField.setName(sweField.getName().getValue());
        }
        if (sosElement instanceof SweBoolean) {
            xbField.addNewBoolean().set(createSimpleType((SweBoolean) sosElement));
        } else if (sosElement instanceof SweCategory) {
            xbField.addNewCategory().set(createSimpleType((SweCategory) sosElement));
        } else if (sosElement instanceof SweCount) {
            xbField.addNewCount().set(createSimpleType((SweCount) sosElement));
        } else if (sosElement instanceof SweQuantity) {
            xbField.addNewQuantity().set(createSimpleType((SweQuantity) sosElement));
        } else if (sosElement instanceof SweText) {
            xbField.addNewText().set(createSimpleType((SweText) sosElement));
        } else if (sosElement instanceof SweTimeRange) {
            xbField.addNewTimeRange().set(createSimpleType((SweTimeRange) sosElement));
        } else if (sosElement instanceof SweTime) {
            xbField.addNewTime().set(createSimpleType((SweTime) sosElement));
        } else if (sosElement instanceof SweEnvelope) {
            final EnvelopeType xbEnvelope = (EnvelopeType) xbField.addNewAbstractDataRecord()
                    .substitute(SweConstants.QN_ENVELOPE_SWE_101, EnvelopeType.type);
            xbEnvelope.set(createEnvelope((SweEnvelope) sosElement));
        } else if (sosElement instanceof SweDataRecord) {
            final DataRecordType xbEnvelope = (DataRecordType) xbField.addNewAbstractDataRecord()
                    .substitute(SweConstants.QN_DATA_RECORD_SWE_101, DataRecordType.type);
            xbEnvelope.set(createDataRecord((SweDataRecord) sosElement));
        } else if (sosElement instanceof SweDataArray) {
            final DataArrayType xbEnvelope = (DataArrayType) xbField.addNewAbstractDataRecord()
                    .substitute(SweConstants.QN_DATA_RECORD_SWE_101, DataArrayType.type);
            xbEnvelope.set(createDataArray((SweDataArray) sosElement).getDataArray1());
        } else {
            throw new NoApplicableCodeException().withMessage(
                    "The element type '%s' of the received '%s' is not supported by this encoder '%s'.",
                    sosElement != null ? sosElement.getClass().getName() : null, sweField.getClass().getName(),
                    getClass().getName()).setStatus(BAD_REQUEST);
        }
        return xbField;
    }

    private net.opengis.swe.x101.BooleanDocument.Boolean createBoolean(final SweBoolean bool) throws OwsExceptionReport {
        final net.opengis.swe.x101.BooleanDocument.Boolean xbBoolean =
                net.opengis.swe.x101.BooleanDocument.Boolean.Factory.newInstance(XmlOptionsHelper.getInstance()
                        .getXmlOptions());
        if (bool.isSetValue()) {
            xbBoolean.setValue(bool.getValue().booleanValue());
        }
        if (bool.isSetQuality()) {
        	xbBoolean.setQuality(createQuality(bool.getQuality())[0]);
        }
        return xbBoolean;
    }
    
    private QualityPropertyType[] createQuality(final Collection<SweQuality> quality) throws OwsExceptionReport {
        if (!quality.isEmpty()) {
                final ArrayList<QualityPropertyType> xbQualities = Lists.newArrayListWithCapacity(quality.size());
                for (final SweQuality sweQuality : quality) {
                        final QualityPropertyType xbQuality = QualityPropertyType.Factory.newInstance();
                        if (sweQuality instanceof SweText) {
                                xbQuality.addNewText().set(createText((SweText) sweQuality));
                        }
                        else if (sweQuality instanceof SweCategory) {
                                xbQuality.addNewCategory().set(createCategory((SweCategory) sweQuality));
                        }
                        else if (sweQuality instanceof SweQuantity) {
                                xbQuality.addNewQuantity().set(createQuantity((SweQuantity) sweQuality));
                        }
                        else if (sweQuality instanceof SweQuantityRange) {
                                xbQuality.addNewQuantityRange().set(createQuantityRange((SweQuantityRange) sweQuality));
                        }
                        xbQualities.add(xbQuality);
                }
                return xbQualities.toArray(new QualityPropertyType[xbQualities.size()]);
        }
                final QualityPropertyType[] result = {QualityPropertyType.Factory.newInstance()};
                return result;
        }

    private Category createCategory(final SweCategory category) throws OwsExceptionReport {
        final Category xbCategory = Category.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (category.isSetValue()) {
            xbCategory.setValue(category.getValue());
        }
        if (category.isSetCodeSpace()) {
            xbCategory.addNewCodeSpace().setHref(category.getCodeSpace());
        }
        if (category.isSetQuality()) {
        	xbCategory.setQuality(createQuality(category.getQuality())[0]);
        }
        return xbCategory;
    }

    private Count createCount(final SweCount count) throws OwsExceptionReport {
        final Count xbCount = Count.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (count.isSetValue()) {
            xbCount.setValue(new BigInteger(Integer.toString(count.getValue().intValue())));
        }
        if (count.isSetQuality()) {
            xbCount.setQualityArray(createQuality(count.getQuality()));
        }
        return xbCount;
    }

    private ObservableProperty createObservableProperty(final SweObservableProperty observableProperty) throws OwsExceptionReport {
        final ObservableProperty xbObservableProperty =
                ObservableProperty.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        return xbObservableProperty;
    }

    /**
     * Adds values to SWE quantity
     *
     * @param quantity
     *            SOS internal representation
     * @throws OwsExceptionReport 
     */
    protected Quantity createQuantity(final SweQuantity quantity) throws OwsExceptionReport {
        final Quantity xbQuantity = Quantity.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (quantity.isSetAxisID()) {
            xbQuantity.setAxisID(quantity.getAxisID());
        }
        if (quantity.isSetValue()) {
            xbQuantity.setValue(Double.valueOf(quantity.getValue()));
        }
        if (quantity.isSetUom()) {
            xbQuantity.addNewUom().set(createUom(quantity.getUom()));
        }
        if (quantity.isSetQuality()) {
        	xbQuantity.setQualityArray(createQuality(quantity.getQuality()));
        }
        return xbQuantity;
    }

    protected QuantityRange createQuantityRange(final SweQuantityRange quantityRange) throws OwsExceptionReport {
        final QuantityRange xbQuantityRange =
                QuantityRange.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (quantityRange.isSetAxisID()) {
            xbQuantityRange.setAxisID(quantityRange.getDescription());
        }
        if (quantityRange.isSetValue()) {
            xbQuantityRange.setValue(quantityRange.getValue().getRangeAsList());
        }
        if (quantityRange.isSetUom()) {
            xbQuantityRange.addNewUom().set(createUom(quantityRange.getUom()));
        }
        if (quantityRange.isSetQuality()) {
        	xbQuantityRange.setQualityArray(createQuality(quantityRange.getQuality()));
        }
        return xbQuantityRange;
    }

    /**
     * Adds values to SWE text
     *
     * @param text
     *            SOS internal representation
     * @throws OwsExceptionReport 
     */
    private Text createText(final SweText text) throws OwsExceptionReport {
        final Text xbText = Text.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (text.isSetValue()) {
            xbText.setValue(text.getValue());
        }
        return xbText;
    }

    private Time createTime(final SweTime time) throws OwsExceptionReport {
        final Time xbTime = Time.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (time.isSetValue()) {
            final XmlDateTime xbDateTime = createDateTime(time.getValue());
            xbTime.setValue(xbDateTime);
        }
        if (time.isSetUom()) {
            if (time.getUom().startsWith("urn:") || time.getUom().startsWith("http://")) {
                xbTime.addNewUom().setHref(time.getUom());
            } else {
                xbTime.addNewUom().setCode(time.getUom());
            }
        }
        if (time.isSetQuality()) {
        	xbTime.setQuality(createQuality(time.getQuality())[0]);
        }
        return xbTime;
    }

    private XmlDateTime createDateTime(final DateTime sosDateTime) {
        final XmlDateTime xbDateTime = XmlDateTime.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());

        //encode the DateTime in UTC
        final GDateBuilder gdb = new GDateBuilder(sosDateTime.toDate());
        gdb.normalize();
        xbDateTime.setGDateValue(gdb.toGDate());

        return xbDateTime;
    }

    private EnvelopeType createEnvelope(final SweEnvelope sosSweEnvelope) throws OwsExceptionReport {
        final EnvelopeType envelopeType =
                EnvelopeType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        addAbstractDataComponentValues(envelopeType, sosSweEnvelope);
        if (sosSweEnvelope.isReferenceFrameSet()) {
            envelopeType.setReferenceFrame(sosSweEnvelope.getReferenceFrame());
        }
        if (sosSweEnvelope.isLowerCornerSet()) {
            envelopeType.setLowerCorner(createVectorProperty(sosSweEnvelope.getLowerCorner()));
        }
        if (sosSweEnvelope.isUpperCornerSet()) {
            envelopeType.setUpperCorner(createVectorProperty(sosSweEnvelope.getUpperCorner()));
        }
        if (sosSweEnvelope.isTimeSet()) {
            envelopeType.addNewTime().setTimeRange(createTimeRange(sosSweEnvelope.getTime()));
        }
        return envelopeType;
    }

    private VectorPropertyType createVectorProperty(final SweVector sosSweVector) throws OwsExceptionReport {
        final VectorPropertyType vectorPropertyType =
                VectorPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        vectorPropertyType.setVector(createVector(sosSweVector.getCoordinates()));
        return vectorPropertyType;
    }

    private VectorType createVector(final List<SweCoordinate<?>> coordinates) throws OwsExceptionReport {
        final VectorType vectorType = VectorType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        vectorType.setCoordinateArray(createCoordinates(coordinates));
        return vectorType;
    }

    private TimeRange createTimeRange(final SweTimeRange timeRange) throws OwsExceptionReport {
        final TimeRange xbTimeRange = TimeRange.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        addAbstractDataComponentValues(xbTimeRange, timeRange);
        if (timeRange.isSetValue()) {
            xbTimeRange.setValue(timeRange.getValue().getRangeAsStringList());
        }
        if (timeRange.isSetUom()) {
            xbTimeRange.addNewUom().setCode(timeRange.getUom());
        }
        if (timeRange.isSetQuality()) {
        	xbTimeRange.setQuality(createQuality(timeRange.getQuality())[0]);
        }
        return xbTimeRange;
    }

    private void addAbstractDataComponentValues(final AbstractDataComponentType xbComponent,
            final SweAbstractDataComponent component) throws OwsExceptionReport {
        if (component.isSetDefinition()) {
            xbComponent.setDefinition(component.getDefinition());
        }
        if (component.isSetDescription()) {
            xbComponent.addNewDescription().setStringValue(component.getDescription());
        }
        if (component.isSetName()) {
            xbComponent.addNewName().set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, component.getName()));
        }
    }

    /**
     * Adds values to SWE coordinates
     *
     * @param coordinate
     *            SOS internal representation
     * @throws OwsExceptionReport 
     */
    private Coordinate createCoordinate(final SweCoordinate<?> coordinate) throws OwsExceptionReport {
        final Coordinate xbCoordinate = Coordinate.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        xbCoordinate.setName(coordinate.getName());
        xbCoordinate.setQuantity(createQuantity((SweQuantity) coordinate.getValue()));
        return xbCoordinate;
    }

    /**
     * Adds values to SWE coordinates
     *
     * @param coordinates
     *            SOS internal representation
     * @throws OwsExceptionReport 
     */
    private Coordinate[] createCoordinates(final List<SweCoordinate<?>> coordinates) throws OwsExceptionReport {
        if (coordinates != null) {
            final ArrayList<Coordinate> xbCoordinates = new ArrayList<Coordinate>(coordinates.size());
            for (final SweCoordinate<?> coordinate : coordinates) {
                xbCoordinates.add(createCoordinate(coordinate));
            }
            return xbCoordinates.toArray(new Coordinate[xbCoordinates.size()]);
        }
        return null;
    }

    // TODO check types for SWE101
    private DataRecordType createDataRecord(final SweDataRecord sosDataRecord) throws OwsExceptionReport {

        final List<SweField> sosFields = sosDataRecord.getFields();

        final DataRecordType xbDataRecord =
                DataRecordType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        
        if (sosDataRecord.isSetDefinition()) {
        	xbDataRecord.setDefinition(sosDataRecord.getDefinition());
        }
        
        if (sosDataRecord.isSetFields()) {
            final DataComponentPropertyType[] xbFields = new DataComponentPropertyType[sosFields.size()];
            int xbFieldIndex = 0;
            for (final SweField sosSweField : sosFields) {
                final DataComponentPropertyType xbField = createField(sosSweField);
                xbFields[xbFieldIndex] = xbField;
                xbFieldIndex++;
            }
            xbDataRecord.setFieldArray(xbFields);
        }
        return xbDataRecord;
    }

    private DataArrayDocument createDataArray(final SweDataArray sosDataArray) throws OwsExceptionReport {
        if (sosDataArray != null) {
        	if (sosDataArray.isSetElementTyp()) {
	            final DataArrayDocument xbDataArrayDoc =
	                    DataArrayDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
	            final DataArrayType xbDataArray = xbDataArrayDoc.addNewDataArray1();
	
	            // set element count
	            if (sosDataArray.isSetElementCount()) {
	                xbDataArray.addNewElementCount().addNewCount().set(createCount(sosDataArray.getElementCount()));
	            }
	
	            if (sosDataArray.isSetElementTyp()) {
	                final DataComponentPropertyType xbElementType = xbDataArray.addNewElementType();
	                xbElementType.setName("Components");
	                if (sosDataArray.getElementType() instanceof SweBoolean) {
	                    xbElementType.addNewBoolean().set(createSimpleType((SweBoolean)sosDataArray.getElementType()));
	                } else if (sosDataArray.getElementType() instanceof Category) {
	                    xbElementType.addNewCategory().set(createSimpleType((SweCategory) sosDataArray.getElementType()));
	                } else if (sosDataArray.getElementType() instanceof SweCount) {
	                    xbElementType.addNewCount().set(createSimpleType((SweCount) sosDataArray.getElementType()));
	                } else if (sosDataArray.getElementType() instanceof SweQuantity) {
	                    xbElementType.addNewQuantity().set(createSimpleType((SweQuantity) sosDataArray.getElementType()));
	                } else if (sosDataArray.getElementType() instanceof SweText) {
	                    xbElementType.addNewText().set(createSimpleType((SweText) sosDataArray.getElementType()));
	                } else if (sosDataArray.getElementType() instanceof SweTimeRange) {
	                    xbElementType.addNewTimeRange().set(createSimpleType((SweTimeRange) sosDataArray.getElementType()));
	                } else if (sosDataArray.getElementType() instanceof SweTime) {
	                    xbElementType.addNewTime().set(createSimpleType((SweTime) sosDataArray.getElementType()));
	                } else if (sosDataArray.getElementType() instanceof SweEnvelope) {
	                    xbElementType.addNewAbstractDataRecord().set(createEnvelope((SweEnvelope) sosDataArray.getElementType()));
	                    xbElementType.getAbstractDataRecord().substitute(SweConstants.QN_ENVELOPE_SWE_101, EnvelopeType.type);
	                } else if (sosDataArray.getElementType() instanceof SweDataRecord) {
	                    xbElementType.addNewAbstractDataRecord().set(createDataRecord((SweDataRecord) sosDataArray.getElementType()));
	                    xbElementType.getAbstractDataRecord().substitute(SweConstants.QN_DATA_RECORD_SWE_101, DataRecordType.type);
	                } else if (sosDataArray.getElementType() instanceof SweDataArray) {
	                    xbElementType.addNewAbstractDataArray1().set(createDataArray((SweDataArray) sosDataArray.getElementType()).getDataArray1());
	                    xbElementType.getAbstractDataArray1().substitute(SweConstants.QN_DATA_RECORD_SWE_101, DataArrayType.type);
	                } else {
	                    throw new NoApplicableCodeException().withMessage(
	                            "The element type '%s' of the received '%s' is not supported by this encoder '%s'.",
	                            sosDataArray.getElementType() != null ? sosDataArray.getElementType().getClass().getName() : null, xbElementType.getClass().getName(),
	                            getClass().getName()).setStatus(BAD_REQUEST);
	                }
	            }
	
	            if (sosDataArray.isSetEncoding()) {
	
	                final BlockEncodingPropertyType xbEncoding = xbDataArray.addNewEncoding();
	                xbEncoding.set(createBlockEncoding(sosDataArray.getEncoding()));
	                // xbDataArray.getEncoding().substitute(
	                // new QName(SWEConstants.NS_SWE_101,
	                // SWEConstants.EN_TEXT_ENCODING,
	                // SWEConstants.NS_SWE_PREFIX), TextBlock.type);
	            }
	            // if (absObs.getObservationTemplateIDs() == null
	            // || (absObs.getObservationTemplateIDs() != null &&
	            // absObs.getObservationTemplateIDs().isEmpty())) {
	            // xbValues.newCursor().setTextValue(createResultString(phenComponents,
	            // absObs));
	            // }
	            if (sosDataArray.isSetValues()) {
	                xbDataArray.addNewValues().set(createValues(sosDataArray.getValues(), sosDataArray.getEncoding()));
	            }
	            return xbDataArrayDoc;
        	} else if (sosDataArray.isSetXml()) {
        		try {
					XmlObject xmlObject = XmlObject.Factory.parse(sosDataArray.getXml().trim());
					if (xmlObject instanceof DataArrayDocument) {
						return (DataArrayDocument)xmlObject;
					} else {
						DataArrayDocument xbDataArrayDoc =
			                    DataArrayDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
						xbDataArrayDoc.setDataArray1(DataArrayType.Factory.parse(sosDataArray.getXml().trim()));
						return xbDataArrayDoc;
					}
				} catch (XmlException e) {
					throw new NoApplicableCodeException().causedBy(e).withMessage("Error while encoding SweDataArray!");
				}
        	}
        }
        return null;
    }

    private XmlString createValues(final List<List<String>> values, final SweAbstractEncoding encoding) {
        // TODO How to deal with the decimal separator - is it an issue here?
        final StringBuilder valueStringBuilder = new StringBuilder(256);
        final SweTextEncoding textEncoding = (SweTextEncoding) encoding;
        // would also work, hmm
        // Configurator.getInstance().getDecimalSeparator();
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

    private BlockEncodingPropertyType createBlockEncoding(final SweAbstractEncoding sosSweAbstractEncoding)
            throws OwsExceptionReport {

        try {
            if (sosSweAbstractEncoding instanceof SweTextEncoding) {
                return createTextEncoding((SweTextEncoding) sosSweAbstractEncoding);
            }
            if (sosSweAbstractEncoding.getXml() != null && !sosSweAbstractEncoding.getXml().isEmpty()) {
                final XmlObject xmlObject = XmlObject.Factory.parse(sosSweAbstractEncoding.getXml());
                if (xmlObject instanceof AbstractEncodingType) {
                    return (BlockEncodingPropertyType) xmlObject;
                }
                throw new NoApplicableCodeException().withMessage("AbstractEncoding can not be encoded!");
            }

        } catch (final XmlException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("Error while encoding AbstractEncoding!");
        } catch (final XmlValueDisconnectedException xvde) {
            throw new NoApplicableCodeException().causedBy(xvde).withMessage("Error while encoding AbstractEncoding!");
        } catch (final Exception ge) {
            throw new NoApplicableCodeException().causedBy(ge).withMessage("Error while encoding AbstractEncoding!");
        }
        return null;
    }

    private BlockEncodingPropertyType createTextEncoding(final SweTextEncoding sosTextEncoding) {
        final BlockEncodingPropertyType xbTextEncodingType =
                BlockEncodingPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final TextBlock xbTextEncoding = xbTextEncodingType.addNewTextBlock();

        if (sosTextEncoding.getBlockSeparator() != null) {
            xbTextEncoding.setBlockSeparator(sosTextEncoding.getBlockSeparator());
        }
        // TODO check not used in SWE101
        // if (sosTextEncoding.isSetCollapseWhiteSpaces()) {
        // xbTextEncoding.setCollapseWhiteSpaces(sosTextEncoding.isCollapseWhiteSpaces());
        // }
        if (sosTextEncoding.getDecimalSeparator() != null) {
            xbTextEncoding.setDecimalSeparator(sosTextEncoding.getDecimalSeparator());
        }
        if (sosTextEncoding.getTokenSeparator() != null) {
            xbTextEncoding.setTokenSeparator(sosTextEncoding.getTokenSeparator());
        }
        // wont cast !!! net.opengis.swe.x101.impl.BlockEncodingPropertyTypeImpl
        // cannot be cast to net.opengis.swe.x101.AbstractEncodingType
        return xbTextEncodingType;
    }

    private XmlObject createTimeGeometricPrimitivePropertyType(final TimePeriod timePeriod) throws OwsExceptionReport {
        final TimeGeometricPrimitivePropertyType xbTimeGeometricPrimitiveProperty =
                TimeGeometricPrimitivePropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (timePeriod.isSetStart() && timePeriod.isSetEnd()) {
            xbTimeGeometricPrimitiveProperty.addNewTimeGeometricPrimitive().set(
                    CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, timePeriod));
        }
        // TODO check GML 311 rename nodename of geometric primitive to
        // gml:timePeriod
        final XmlCursor timeCursor = xbTimeGeometricPrimitiveProperty.newCursor();
        final boolean hasTimePrimitive =
                timeCursor.toChild(new QName(GmlConstants.NS_GML, GmlConstants.EN_ABSTRACT_TIME_GEOM_PRIM));
        if (hasTimePrimitive) {
            timeCursor.setName(new QName(GmlConstants.NS_GML, GmlConstants.EN_TIME_PERIOD));
        }
        timeCursor.dispose();
        return xbTimeGeometricPrimitiveProperty;
    }

    private UomPropertyType createUom(final String uom) {
        final UomPropertyType xbUom =
                UomPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (uom.startsWith("urn:") || uom.startsWith("http://")) {
            xbUom.setHref(uom);
        } else {
            xbUom.setCode(uom);
        }
        return xbUom;
    }
}
