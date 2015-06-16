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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.swe.x101.AbstractDataArrayType.ElementCount;
import net.opengis.swe.x101.AbstractDataComponentType;
import net.opengis.swe.x101.AbstractDataRecordDocument;
import net.opengis.swe.x101.AbstractDataRecordType;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.BooleanDocument;
import net.opengis.swe.x101.CategoryDocument;
import net.opengis.swe.x101.CategoryDocument.Category;
import net.opengis.swe.x101.CountDocument;
import net.opengis.swe.x101.CountDocument.Count;
import net.opengis.swe.x101.CountRangeDocument;
import net.opengis.swe.x101.CountRangeDocument.CountRange;
import net.opengis.swe.x101.BlockEncodingPropertyType;
import net.opengis.swe.x101.DataArrayDocument;
import net.opengis.swe.x101.DataArrayType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.EnvelopeType;
import net.opengis.swe.x101.ObservablePropertyDocument;
import net.opengis.swe.x101.ObservablePropertyDocument.ObservableProperty;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.QualityPropertyType;
import net.opengis.swe.x101.QuantityDocument;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.QuantityRangeDocument;
import net.opengis.swe.x101.QuantityRangeDocument.QuantityRange;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.TextDocument;
import net.opengis.swe.x101.TextBlockDocument.TextBlock;
import net.opengis.swe.x101.TextDocument.Text;
import net.opengis.swe.x101.TimeDocument;
import net.opengis.swe.x101.TimeDocument.Time;
import net.opengis.swe.x101.TimeRangeDocument;
import net.opengis.swe.x101.TimeRangeDocument.TimeRange;
import net.opengis.swe.x101.VectorPropertyType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.joda.time.DateTime;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;
import org.n52.sos.ogc.swe.RangeValue;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweConstants.SweCoordinateName;
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
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 *
 */
public class SweCommonDecoderV101 implements Decoder<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SweCommonDecoderV101.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(SweConstants.NS_SWE_101,
            DataArrayDocument.class, DataArrayType.class, AbstractDataComponentType.class, BooleanDocument.class,
            net.opengis.swe.x101.BooleanDocument.Boolean.class, CategoryDocument.class, Category.class,
            CountDocument.class, Count.class, CountRangeDocument.class, CountRange.class,
            ObservablePropertyDocument.class, ObservableProperty.class, QuantityDocument.class, Quantity.class,
            QuantityRangeDocument.class, QuantityRange.class, TextDocument.class, Text.class, TimeDocument.class,
            Time.class, TimeRangeDocument.class, TimeRange.class, DataComponentPropertyType[].class,
            PositionType.class, Coordinate[].class, AnyScalarPropertyType[].class, AbstractDataRecordDocument.class,
            AbstractDataRecordType.class);

    public SweCommonDecoderV101() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Object decode(final Object element) throws OwsExceptionReport {
        if (element instanceof DataArrayDocument) {
            return parseAbstractDataComponentType(((DataArrayDocument) element).getDataArray1());
        } else if (element instanceof AbstractDataComponentType) {
            return parseAbstractDataComponentType((AbstractDataComponentType) element);
        } else if (element instanceof BooleanDocument) {
            return parseAbstractDataComponentType(((BooleanDocument) element).getBoolean());
        } else if (element instanceof CategoryDocument) {
            return parseAbstractDataComponentType(((CategoryDocument) element).getCategory());
        } else if (element instanceof CountDocument) {
            return parseAbstractDataComponentType(((CountDocument) element).getCount());
        } else if (element instanceof CountRangeDocument) {
            return parseAbstractDataComponentType(((CountRangeDocument) element).getCountRange());
        } else if (element instanceof ObservablePropertyDocument) {
            return parseAbstractDataComponentType(((ObservablePropertyDocument) element).getObservableProperty());
        } else if (element instanceof QuantityDocument) {
            return parseAbstractDataComponentType(((QuantityDocument) element).getQuantity());
        } else if (element instanceof QuantityRangeDocument) {
            return parseAbstractDataComponentType(((QuantityRangeDocument) element).getQuantityRange());
        } else if (element instanceof TextDocument) {
            return parseAbstractDataComponentType(((TextDocument) element).getText());
        } else if (element instanceof TimeDocument) {
            return parseAbstractDataComponentType(((TimeDocument) element).getTime());
        } else if (element instanceof TimeRangeDocument) {
            return parseAbstractDataComponentType(((TimeRangeDocument) element).getTimeRange());
        } else if (element instanceof DataComponentPropertyType[]) {
            return parseDataComponentPropertyArray((DataComponentPropertyType[]) element);
        } else if (element instanceof Coordinate[]) {
            return parseCoordinates((Coordinate[]) element);
        } else if (element instanceof AnyScalarPropertyType[]) {
            return parseAnyScalarPropertyArray((AnyScalarPropertyType[]) element);
        } else if (element instanceof AbstractDataRecordDocument) {
            return parseAbstractDataComponentType(((AbstractDataRecordDocument) element).getAbstractDataRecord());
        } else {
            throw new UnsupportedDecoderInputException(this, element);
        }
    }

    private SweAbstractDataComponent parseAbstractDataComponentType(
            final AbstractDataComponentType abstractDataComponent) throws OwsExceptionReport {
        SweAbstractDataComponent sosAbstractDataComponent = null;
        if (abstractDataComponent instanceof net.opengis.swe.x101.BooleanDocument.Boolean) {
            sosAbstractDataComponent =
                    parseBoolean((net.opengis.swe.x101.BooleanDocument.Boolean) abstractDataComponent);
        } else if (abstractDataComponent instanceof Category) {
            sosAbstractDataComponent = parseCategory((Category) abstractDataComponent);
        } else if (abstractDataComponent instanceof Count) {
            sosAbstractDataComponent = parseCount((Count) abstractDataComponent);
        } else if (abstractDataComponent instanceof CountRange) {
            sosAbstractDataComponent = parseCountRange((CountRange) abstractDataComponent);
        } else if (abstractDataComponent instanceof ObservableProperty) {
            sosAbstractDataComponent = parseObservableProperty((ObservableProperty) abstractDataComponent);
        } else if (abstractDataComponent instanceof Quantity) {
            sosAbstractDataComponent = parseQuantity((Quantity) abstractDataComponent);
        } else if (abstractDataComponent instanceof QuantityRange) {
            sosAbstractDataComponent = parseQuantityRange((QuantityRange) abstractDataComponent);
        } else if (abstractDataComponent instanceof Text) {
            sosAbstractDataComponent = parseText((Text) abstractDataComponent);
        } else if (abstractDataComponent instanceof Time) {
            sosAbstractDataComponent = parseTime((Time) abstractDataComponent);
        } else if (abstractDataComponent instanceof TimeRange) {
            sosAbstractDataComponent = parseTimeRange((TimeRange) abstractDataComponent);
        } else if (abstractDataComponent instanceof PositionType) {
            sosAbstractDataComponent = parsePosition((PositionType) abstractDataComponent);
        } else if (abstractDataComponent instanceof DataRecordPropertyType) {
            sosAbstractDataComponent = parseDataRecordProperty((DataRecordPropertyType) abstractDataComponent);
        } else if (abstractDataComponent instanceof SimpleDataRecordType) {
            sosAbstractDataComponent = parseSimpleDataRecord((SimpleDataRecordType) abstractDataComponent);
        } else if (abstractDataComponent instanceof DataArrayType) {
            sosAbstractDataComponent = parseSweDataArrayType((DataArrayType) abstractDataComponent);
        } else if (abstractDataComponent instanceof DataRecordType) {
            sosAbstractDataComponent = parseDataRecord((DataRecordType) abstractDataComponent);
        } else if (abstractDataComponent instanceof EnvelopeType) {
            sosAbstractDataComponent = parseEnvelope((EnvelopeType) abstractDataComponent);
        }
        if (sosAbstractDataComponent != null) {
            if (abstractDataComponent.isSetDefinition()) {
                sosAbstractDataComponent.setDefinition(abstractDataComponent.getDefinition());
            }
            if (abstractDataComponent.isSetDescription()) {
                sosAbstractDataComponent.setDescription(abstractDataComponent.getDescription().getStringValue());
            }
        }
        return sosAbstractDataComponent;
    }

    // private SosSweAbstractDataComponent
    // parseAbstractDataRecord(AbstractDataRecordType abstractDataRecord) throws
    // OwsExceptionReport {
    // if (abstractDataRecord instanceof DataRecordPropertyType) {
    // return parseDataRecordProperty((DataRecordPropertyType)
    // abstractDataRecord);
    // } else if (abstractDataRecord instanceof SimpleDataRecordType) {
    // return parseSimpleDataRecord((SimpleDataRecordType) abstractDataRecord);
    // }
    // return null;
    // }

    private SweDataRecord parseDataRecordProperty(final DataRecordPropertyType dataRecordProperty)
            throws OwsExceptionReport {
        final DataRecordType dataRecord = dataRecordProperty.getDataRecord();
        return parseDataRecord(dataRecord);
    }

    private SweDataRecord parseDataRecord(final DataRecordType dataRecord) throws OwsExceptionReport {
        final SweDataRecord sosDataRecord = new SweDataRecord();
        if (dataRecord.getFieldArray() != null) {
            sosDataRecord.setFields(parseDataComponentPropertyArray(dataRecord.getFieldArray()));
        }
        return sosDataRecord;
    }

    private SweAbstractDataComponent parseEnvelope(final EnvelopeType envelopeType) throws OwsExceptionReport {
        final SweEnvelope envelope = new SweEnvelope();
        if (envelopeType.isSetReferenceFrame()) {
            envelope.setReferenceFrame(envelopeType.getReferenceFrame());
        }
        if (envelopeType.getLowerCorner() != null) {
            envelope.setLowerCorner(parseVectorProperty(envelopeType.getLowerCorner()));
        }
        if (envelopeType.getUpperCorner() != null) {
            envelope.setUpperCorner(parseVectorProperty(envelopeType.getUpperCorner()));
        }
        if (envelopeType.isSetTime()) {
            envelope.setTime((SweTimeRange) parseTimeRange(envelopeType.getTime().getTimeRange()));
        }
        return envelope;
    }

    private SweVector parseVectorProperty(final VectorPropertyType vectorPropertyType) throws OwsExceptionReport {
        return parseVector(vectorPropertyType.getVector());
    }

    private SweVector parseVector(final VectorType vectorType) throws OwsExceptionReport {
        return new SweVector(parseCoordinates(vectorType.getCoordinateArray()));
    }

    private SweSimpleDataRecord parseSimpleDataRecord(final SimpleDataRecordType simpleDataRecord)
            throws OwsExceptionReport {
        final SweSimpleDataRecord sosSimpleDataRecord = new SweSimpleDataRecord();
        if (simpleDataRecord.getFieldArray() != null) {
            sosSimpleDataRecord.setFields(parseAnyScalarPropertyArray(simpleDataRecord.getFieldArray()));
        }
        return sosSimpleDataRecord;
    }

    private SweDataArray parseSweDataArrayType(final DataArrayType xbDataArray) throws OwsExceptionReport {
        if (!xbDataArray.getElementType().isSetAbstractDataRecord()) {
            throw new InvalidParameterValueException()
                    .at(XmlHelper.getLocalName(xbDataArray.getElementType()))
                    .withMessage(
                            "The swe:DataArray contains a not yet supported elementType element. Currently only 'swe:DataRecord' is supported as elementType element.");
        }
        final SweDataArray dataArray = new SweDataArray();
        if (xbDataArray.getElementCount() != null) {
            dataArray.setElementCount(parseElementCount(xbDataArray.getElementCount()));
        }
        // parse data record to elementType
        DataComponentPropertyType elementType = xbDataArray.getElementType();

        if (elementType != null) {
            SweAbstractDataComponent sosAbstractDataComponentType = null;
            if (elementType.isSetBoolean()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getBoolean());
            } else if (elementType.isSetCategory()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getCategory());
            } else if (elementType.isSetCount()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getCount());
            } else if (elementType.isSetCountRange()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getCountRange());
            } else if (elementType.isSetQuantity()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getQuantity());
            } else if (elementType.isSetQuantityRange()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getQuantityRange());
            } else if (elementType.isSetText()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getText());
            } else if (elementType.isSetTime()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getTime());
            } else if (elementType.isSetTimeRange()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getTimeRange());
            } else if (elementType.isSetAbstractDataRecord()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(elementType.getAbstractDataRecord());
            }
            if (sosAbstractDataComponentType != null) {
                dataArray.setElementType(sosAbstractDataComponentType);
            }
        }
        if (xbDataArray.isSetEncoding()) {
            dataArray.setEncoding(parseEncoding(xbDataArray.getEncoding()));
        }

        // parse values
        if (xbDataArray.isSetValues()) {
            // TODO implement full support
            // dataArray.setValues(parseValues(dataArray.getElementCount(),
            // dataArray.getElementType(),
            // dataArray.getEncoding(), xbDataArray.getValues()));
        }

        DataArrayDocument xbDataArrayDoc =
                DataArrayDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        xbDataArrayDoc.setDataArray1(xbDataArray);
        dataArray.setXml(xbDataArrayDoc.xmlText());
        return dataArray;
    }

    private List<SweField> parseDataComponentPropertyArray(final DataComponentPropertyType[] fieldArray)
            throws OwsExceptionReport {
        final List<SweField> sosFields = new ArrayList<SweField>(fieldArray.length);
        for (final DataComponentPropertyType xbField : fieldArray) {
            SweAbstractDataComponent sosAbstractDataComponentType = null;
            if (xbField.isSetBoolean()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getBoolean());
            } else if (xbField.isSetCategory()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getCategory());
            } else if (xbField.isSetCount()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getCount());
            } else if (xbField.isSetCountRange()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getCountRange());
            } else if (xbField.isSetQuantity()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getQuantity());
            } else if (xbField.isSetQuantityRange()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getQuantityRange());
            } else if (xbField.isSetText()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getText());
            } else if (xbField.isSetTime()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getTime());
            } else if (xbField.isSetTimeRange()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getTimeRange());
            } else if (xbField.isSetAbstractDataRecord()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getAbstractDataRecord());
            }
            if (sosAbstractDataComponentType != null) {
                sosFields.add(new SweField(xbField.getName(), sosAbstractDataComponentType));
            }
        }
        return sosFields;
    }

    private SweAbstractSimpleType<Boolean> parseBoolean(final net.opengis.swe.x101.BooleanDocument.Boolean xbBoolean)
            throws OwsExceptionReport {
        final SweBoolean sosBoolean = new SweBoolean();
        if (xbBoolean.isSetDefinition()) {
        	sosBoolean.setDefinition(xbBoolean.getDefinition());
        }
        if (xbBoolean.isSetDescription()) {
        	sosBoolean.setDescription(xbBoolean.getDescription().getStringValue());
        }
        if (xbBoolean.isSetValue()) {
            sosBoolean.setValue(xbBoolean.getValue());
        }
        if (xbBoolean.isSetQuality()) {
        	sosBoolean.setQuality(parseQuality(xbBoolean.getQuality()));
        }
        return sosBoolean;
    }

    private SweAbstractSimpleType<String> parseCategory(final Category category) throws OwsExceptionReport {
        final SweCategory sosCategory = new SweCategory();
        if (category.isSetValue()) {
            sosCategory.setValue(category.getValue());
        }
        if (category.isSetCodeSpace()) {
            sosCategory.setCodeSpace(category.getCodeSpace().getHref());
        }
        if (category.isSetQuality()) {
        	sosCategory.setQuality(parseQuality(category.getQuality()));
        }
        return sosCategory;
    }

    private SweAbstractSimpleType<Integer> parseCount(final Count xbCount) throws OwsExceptionReport {
        final SweCount sosCount = new SweCount();
        if (xbCount.getQualityArray() != null) {
            sosCount.setQuality(parseQuality(xbCount.getQualityArray()));
        }
        if (xbCount.isSetValue()) {
            sosCount.setValue(xbCount.getValue().intValue());
        }
        return sosCount;
    }

    private SweAbstractSimpleType<RangeValue<Integer>> parseCountRange(final CountRange countRange)
            throws OwsExceptionReport {
        // FIXME count range
        throw new NotYetSupportedException("CountRange");
    }

    private SweAbstractSimpleType<String> parseObservableProperty(final ObservableProperty observableProperty) {
        return new SweObservableProperty();
    }

    private SweAbstractSimpleType<Double> parseQuantity(final Quantity xbQuantity) throws OwsExceptionReport {
        final SweQuantity sosQuantity = new SweQuantity();
        if (xbQuantity.isSetAxisID()) {
            sosQuantity.setAxisID(xbQuantity.getAxisID());
        }
        if (xbQuantity.getQualityArray() != null) {
            sosQuantity.setQuality(parseQuality(xbQuantity.getQualityArray()));
        }
        if (xbQuantity.isSetUom() && xbQuantity.getUom().isSetCode()) {
            sosQuantity.setUom(xbQuantity.getUom().getCode());
        }
        if (xbQuantity.isSetValue()) {
            sosQuantity.setValue(Double.valueOf(xbQuantity.getValue()));
        }
        return sosQuantity;
    }

    private SweAbstractSimpleType<RangeValue<Double>> parseQuantityRange(final QuantityRange xbQuantityRange)
            throws OwsExceptionReport {
    	final SweQuantityRange sosQuantityRange = new SweQuantityRange();
    	if (xbQuantityRange.isSetAxisID()) {
    		sosQuantityRange.setAxisID(xbQuantityRange.getAxisID());
    	}
    	if (xbQuantityRange.isSetDefinition()) {
    		sosQuantityRange.setDefinition(xbQuantityRange.getDefinition());
    	}
    	if (xbQuantityRange.isSetDescription()) {
    		sosQuantityRange.setDescription(xbQuantityRange.getDescription().getStringValue());
    	}
    	if (xbQuantityRange.isSetUom() && xbQuantityRange.getUom().isSetCode()) {
    		sosQuantityRange.setUom(xbQuantityRange.getUom().getCode());
    	}
    	if (xbQuantityRange.isSetValue()) {
    		try {
    			final List<?> value = xbQuantityRange.getValue();
    			final Double rangeStart = Double.parseDouble(value.get(0).toString());
    			final Double rangeEnd = Double.parseDouble(value.get(1).toString());
    			sosQuantityRange.setValue(new RangeValue<Double>(rangeStart, rangeEnd));
    		}
    		catch (final NumberFormatException nfe) { throw createParsingException(nfe); }
    		catch (final NullPointerException e) { throw createParsingException(e); }
    		catch (final IndexOutOfBoundsException e) { throw createParsingException(e); }
    	}
    	if (xbQuantityRange.isSetConstraint()) {
    		LOGGER.error("Decoding of swe:QuantityRange/swe:constraint is not implemented");
    	}
    	if (xbQuantityRange.getQualityArray() != null && xbQuantityRange.getQualityArray().length > 0) {
    		LOGGER.error("Decoding of swe:QuantityRange/swe:quality is not implemented");
    	}
    	return sosQuantityRange;
    }

	private CodedException createParsingException(final Exception e) {
		return new InvalidParameterValueException()
			.at("QuantityRange")
			.withMessage("Error when parsing 'swe:QuantityRange/swe:value': It must be of type 'double double!")
		    .causedBy(e);
	}

    private SweAbstractSimpleType<?> parseText(final Text xbText) {
        final SweText sosText = new SweText();
        if (xbText.isSetValue()) {
            sosText.setValue(xbText.getValue());
        }
        return sosText;
    }

    private SweAbstractSimpleType<DateTime> parseTime(final Time time) throws OwsExceptionReport {
        final SweTime sosTime = new SweTime();
        if (time.isSetValue()) {
            sosTime.setValue(DateTimeHelper.parseIsoString2DateTime(time.getValue().toString()));
        }
        if (time.getUom() != null) {
            sosTime.setUom(time.getUom().getHref());
        }
        return sosTime;
    }

    private SweAbstractSimpleType<RangeValue<DateTime>> parseTimeRange(final TimeRange timeRange)
            throws OwsExceptionReport {
        final SweTimeRange sosTimeRange = new SweTimeRange();
        if (timeRange.isSetValue()) {
            final List<?> value = timeRange.getValue();
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
        if (timeRange.getUom() != null) {
            sosTimeRange.setUom(timeRange.getUom().getHref());
        }
        return sosTimeRange;
    }

    private Collection<SweQuality> parseQuality(final QualityPropertyType... qualityArray) throws OwsExceptionReport {
    	if (qualityArray != null && qualityArray.length > 0) {
    		final ArrayList<SweQuality> sosQualities = Lists.newArrayListWithCapacity(qualityArray.length);
    		for (final QualityPropertyType quality : qualityArray) {
				if (quality.isSetQuantity()) {
					sosQualities.add((SweQuality) parseQuantity(quality.getQuantity()));
				}
				else if (quality.isSetQuantityRange()) {
					sosQualities.add((SweQuality) parseQuantityRange(quality.getQuantityRange()));
				}
				else if (quality.isSetCategory()) {
					sosQualities.add((SweQuality) parseCategory(quality.getCategory()));
				}
				else if (quality.isSetText()) {
					sosQualities.add((SweQuality) parseText(quality.getText()));
				}
			}
    		return sosQualities;
    	}
        return Collections.emptyList();
    }

    private SmlPosition parsePosition(final PositionType position) throws OwsExceptionReport {
        final SmlPosition sosSMLPosition = new SmlPosition();
        if (position.isSetReferenceFrame()) {
            sosSMLPosition.setReferenceFrame(position.getReferenceFrame());
        }
        if (position.isSetLocation() && position.getLocation().isSetVector()) {
            if (position.getLocation().getVector().isSetReferenceFrame()) {
                sosSMLPosition.setReferenceFrame(position.getLocation().getVector().getReferenceFrame());
            }
            sosSMLPosition.setPosition(parseCoordinates(position.getLocation().getVector().getCoordinateArray()));
        }
        return sosSMLPosition;
    }

    @SuppressWarnings("unchecked")
    private List<SweCoordinate<?>> parseCoordinates(final Coordinate[] coordinateArray) throws OwsExceptionReport {
        final List<SweCoordinate<?>> sosCoordinates = new ArrayList<SweCoordinate<?>>(coordinateArray.length);
        for (final Coordinate xbCoordinate : coordinateArray) {
            if (xbCoordinate.isSetQuantity()) {
                sosCoordinates.add(new SweCoordinate<Double>(xbCoordinate.getName(),
                        (SweAbstractSimpleType<Double>) parseAbstractDataComponentType(xbCoordinate.getQuantity())));
            } else {
                throw new InvalidParameterValueException().at("Position").withMessage(
                        "Error when parsing the Coordinates of Position: It must be of type Quantity!");
            }
        }
        return sosCoordinates;
    }

    @Deprecated
    private SweCoordinateName checkCoordinateName(final String name) throws OwsExceptionReport {
        if (name.equals(SweCoordinateName.easting.name())) {
            return SweCoordinateName.easting;
        } else if (name.equals(SweCoordinateName.northing.name())) {
            return SweCoordinateName.northing;
        } else if (name.equals(SweCoordinateName.altitude.name())) {
            return SweCoordinateName.altitude;
        } else {
            throw new InvalidParameterValueException().at("Position").withMessage(
                    "The coordinate name is neighter 'easting' nor 'northing' nor 'altitude'!");
        }
    }

    private List<SweField> parseAnyScalarPropertyArray(final AnyScalarPropertyType[] fieldArray)
            throws OwsExceptionReport {
        final List<SweField> sosFields = new ArrayList<SweField>(fieldArray.length);
        for (final AnyScalarPropertyType xbField : fieldArray) {
            SweAbstractDataComponent sosAbstractDataComponentType = null;
            if (xbField.isSetBoolean()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getBoolean());
            } else if (xbField.isSetCategory()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getCategory());
            } else if (xbField.isSetCount()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getCount());
            } else if (xbField.isSetQuantity()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getQuantity());
            } else if (xbField.isSetText()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getText());
            } else if (xbField.isSetTime()) {
                sosAbstractDataComponentType = parseAbstractDataComponentType(xbField.getTime());
            }
            if (sosAbstractDataComponentType != null) {
                sosFields.add(new SweField(xbField.getName(), sosAbstractDataComponentType));
            }
        }
        return sosFields;
    }
    
    private SweCount parseElementCount(final ElementCount elementCount) throws OwsExceptionReport {
        if (elementCount.isSetCount()) {
            return (SweCount) parseCount(elementCount.getCount());
        }
        return null;
    }
    
    private SweAbstractEncoding parseEncoding(final BlockEncodingPropertyType abstractEncodingType) throws OwsExceptionReport {
        assert abstractEncodingType != null;
        if (abstractEncodingType.isSetTextBlock()) {
            return parseTextEncoding(abstractEncodingType.getTextBlock());
        }
        throw new NotYetSupportedException(SweConstants.EN_ENCODING_TYPE, abstractEncodingType,
                TextBlock.type.getName());
    }
    
    private SweTextEncoding parseTextEncoding(final TextBlock textEncoding) {
        final SweTextEncoding sosTextEncoding = new SweTextEncoding();
        sosTextEncoding.setBlockSeparator(textEncoding.getBlockSeparator());
        sosTextEncoding.setTokenSeparator(textEncoding.getTokenSeparator());
        sosTextEncoding.setDecimalSeparator(textEncoding.getDecimalSeparator());
        return sosTextEncoding;
    }
}
