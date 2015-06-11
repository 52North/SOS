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
package org.n52.sos.util;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.service.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SWE helper class.
 * 
 * @since 4.0.0
 * 
 */
public final class SweHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SweHelper.class);

    /*
     * public static SweDataArray
     * createSosSweDataArrayFromObservationValue(OmObservation sosObservation)
     * throws OwsExceptionReport { if (sosObservation.getv) { return
     * createSosSweDataArrayWithResultTemplate(sosObservation); } else { return
     * createSosSweDataArrayWithoutResultTemplate(sosObservation); } }
     * 
     * private static SweDataArray
     * createSosSweDataArrayWithResultTemplate(OmObservation sosObservation)
     * throws OwsExceptionReport { SosResultTemplate sosResultTemplate =
     * sosObservation.getObservationConstellation().getResultTemplate(); String
     * observablePropertyIdentifier =
     * sosObservation.getObservationConstellation(
     * ).getObservableProperty().getIdentifier(); SweDataArrayValue
     * dataArrayValue = new SweDataArrayValue(); SweDataArray dataArray = new
     * SweDataArray();
     * dataArray.setElementType(sosResultTemplate.getResultStructure());
     * dataArray.setEncoding(sosResultTemplate.getResultEncoding());
     * dataArrayValue.setValue(dataArray); if (sosObservation.getValue()
     * instanceof SingleObservationValue) { SingleObservationValue<?>
     * singleValue = (SingleObservationValue) sosObservation.getValue();
     * dataArrayValue.addBlock(createBlock(dataArray.getElementType(),
     * sosObservation.getPhenomenonTime(), observablePropertyIdentifier,
     * singleValue.getValue())); } else if (sosObservation.getValue() instanceof
     * MultiObservationValues) { MultiObservationValues<?> multiValue =
     * (MultiObservationValues) sosObservation.getValue(); if
     * (multiValue.getValue() instanceof SweDataArrayValue) { return
     * ((SweDataArrayValue) multiValue.getValue()).getValue(); } else if
     * (multiValue.getValue() instanceof TVPValue) { TVPValue tvpValues =
     * (TVPValue) multiValue.getValue(); for (TimeValuePair timeValuePair :
     * tvpValues.getValue()) { List<String> newBlock =
     * createBlock(dataArray.getElementType(), timeValuePair.getTime(),
     * observablePropertyIdentifier, timeValuePair.getValue());
     * dataArrayValue.addBlock(newBlock); } } } return
     * dataArrayValue.getValue(); }
     */

    public static SweDataArray createSosSweDataArray(OmObservation sosObservation) {
        String observablePropertyIdentifier =
                sosObservation.getObservationConstellation().getObservableProperty().getIdentifier();
        SweDataArrayValue dataArrayValue = new SweDataArrayValue();
        SweDataArray dataArray = new SweDataArray();
        dataArray.setEncoding(createTextEncoding(sosObservation));
        dataArrayValue.setValue(dataArray);
        if (sosObservation.getValue() instanceof SingleObservationValue) {
            SingleObservationValue<?> singleValue = (SingleObservationValue<?>) sosObservation.getValue();
            if (singleValue.getValue() instanceof SweDataArrayValue) {
                return (SweDataArray) singleValue.getValue().getValue();
            } else {
                dataArray.setElementType(createElementType(singleValue, observablePropertyIdentifier));
                dataArrayValue.addBlock(createBlock(dataArray.getElementType(), sosObservation.getPhenomenonTime(),
                        observablePropertyIdentifier, singleValue.getValue()));
            }
        } else if (sosObservation.getValue() instanceof MultiObservationValues) {
            MultiObservationValues<?> multiValue = (MultiObservationValues<?>) sosObservation.getValue();
            if (multiValue.getValue() instanceof SweDataArrayValue) {
                return ((SweDataArrayValue) multiValue.getValue()).getValue();
            } else if (multiValue.getValue() instanceof TVPValue) {
                TVPValue tvpValues = (TVPValue) multiValue.getValue();
                for (TimeValuePair timeValuePair : tvpValues.getValue()) {
                    if (!dataArray.isSetElementTyp()) {
                        dataArray.setElementType(createElementType(timeValuePair,
                                observablePropertyIdentifier));
                    }
                    List<String> newBlock =
                            createBlock(dataArray.getElementType(), timeValuePair.getTime(),
                                    observablePropertyIdentifier, timeValuePair.getValue());
                    dataArrayValue.addBlock(newBlock);
                }
            }
        }
        return dataArray;
    }

    public static SweDataArray createSosSweDataArray(AbstractObservationValue<?> observationValue) {
        String observablePropertyIdentifier = observationValue.getObservableProperty();
        SweDataArrayValue dataArrayValue = new SweDataArrayValue();
        SweDataArray dataArray = new SweDataArray();
        dataArray.setEncoding(createTextEncoding(observationValue));
        dataArrayValue.setValue(dataArray);
        if (observationValue instanceof SingleObservationValue) {
            SingleObservationValue<?> singleValue = (SingleObservationValue<?>) observationValue;
            if (singleValue.getValue() instanceof SweDataArrayValue) {
                return (SweDataArray) singleValue.getValue().getValue();
            } else {
                dataArray.setElementType(createElementType(singleValue, observablePropertyIdentifier));
                dataArrayValue.addBlock(createBlock(dataArray.getElementType(), observationValue.getPhenomenonTime(),
                        observablePropertyIdentifier, singleValue.getValue()));
            }
        } else if (observationValue instanceof MultiObservationValues) {
            MultiObservationValues<?> multiValue = (MultiObservationValues<?>) observationValue;
            if (multiValue.getValue() instanceof SweDataArrayValue) {
                return ((SweDataArrayValue) multiValue.getValue()).getValue();
            } else if (multiValue.getValue() instanceof TVPValue) {
                TVPValue tvpValues = (TVPValue) multiValue.getValue();
                for (TimeValuePair timeValuePair : tvpValues.getValue()) {
                    if (!dataArray.isSetElementTyp()) {
                        dataArray.setElementType(createElementType(timeValuePair,
                                observablePropertyIdentifier));
                    }
                    List<String> newBlock =
                            createBlock(dataArray.getElementType(), timeValuePair.getTime(),
                                    observablePropertyIdentifier, timeValuePair.getValue());
                    dataArrayValue.addBlock(newBlock);
                }
            }
        }
        return dataArray;
    }

    private static SweAbstractDataComponent createElementType(TimeValuePair tvp, String name) {
        SweDataRecord dataRecord = new SweDataRecord();
        dataRecord.addField(getPhenomenonTimeField(tvp.getTime()));
        dataRecord.addField(getFieldForValue(tvp.getValue(), name));
        return dataRecord;
    }

    private static SweAbstractDataComponent createElementType(SingleObservationValue<?> sov, String name) {
        SweDataRecord dataRecord = new SweDataRecord();
        dataRecord.addField(getPhenomenonTimeField(sov.getPhenomenonTime()));
        dataRecord.addField(getFieldForValue(sov.getValue(), name));
        return dataRecord;
    }

    private static SweField getPhenomenonTimeField(Time sosTime) {
        SweAbstractUomType<?> time = null;
        if (sosTime instanceof TimePeriod) {
            time = new SweTimeRange();
        } else {
            time = new SweTime();
        }
        time.setDefinition(OmConstants.PHENOMENON_TIME);
        time.setUom(OmConstants.PHEN_UOM_ISO8601);
        return new SweField(OmConstants.PHENOMENON_TIME_NAME, time);
    }

    private static SweField getFieldForValue(Value<?> iValue, String name) {
        SweAbstractDataComponent value = getValue(iValue);
        value.setDefinition(name);
        return new SweField(name, value);
    }

    private static SweAbstractDataComponent getValue(Value<?> iValue) {
        if (iValue instanceof BooleanValue) {
            return new SweBoolean();
        } else if (iValue instanceof CategoryValue) {
            SweCategory sosSweCategory = new SweCategory();
            sosSweCategory.setCodeSpace(((CategoryValue) iValue).getUnit());
            return sosSweCategory;
        } else if (iValue instanceof CountValue) {
            return new SweCount();
        } else if (iValue instanceof QuantityValue) {
            SweQuantity sosSweQuantity = new SweQuantity();
            sosSweQuantity.setUom(((QuantityValue) iValue).getUnit());
            return sosSweQuantity;
        } else if (iValue instanceof TextValue) {
            return new SweText();
        } else if (iValue instanceof NilTemplateValue) {
            return new SweText();
        }
        return null;
    }

    /**
     * Create a TextEncoding object for token and tuple separators from
     * SosObservation. If separators not set, definitions from Configurator are
     * used.
     * 
     * @param sosObservation
     *            SosObservation with token and tuple separator
     * @return TextEncoding
     */
    public static SweAbstractEncoding createTextEncoding(OmObservation sosObservation) {
        String tupleSeparator = ServiceConfiguration.getInstance().getTupleSeparator();
        String tokenSeparator = ServiceConfiguration.getInstance().getTokenSeparator();
        String decimalSeparator = null;
        if (sosObservation.isSetTupleSeparator()) {
            tupleSeparator = sosObservation.getTupleSeparator();
        }
        if (sosObservation.isSetTokenSeparator()) {
            tokenSeparator = sosObservation.getTokenSeparator();
        }
        if (sosObservation.isSetDecimalSeparator()) {
            decimalSeparator = sosObservation.getDecimalSeparator();
        }
        return createTextEncoding(tupleSeparator, tokenSeparator, decimalSeparator);
    }

    /**
     * Create a TextEncoding object for token and tuple separators from
     * SosObservation. If separators not set, definitions from Configurator are
     * used.
     * 
     * @param observationValue
     *            AbstractObservationValue with token and tuple separator
     * @return TextEncoding
     */
    private static SweAbstractEncoding createTextEncoding(AbstractObservationValue<?> observationValue) {
        String tupleSeparator = ServiceConfiguration.getInstance().getTupleSeparator();
        String tokenSeparator = ServiceConfiguration.getInstance().getTokenSeparator();
        String decimalSeparator = null;
        if (observationValue.isSetTupleSeparator()) {
            tupleSeparator = observationValue.getTupleSeparator();
        }
        if (observationValue.isSetTokenSeparator()) {
            tokenSeparator = observationValue.getTokenSeparator();
        }
        if (observationValue.isSetDecimalSeparator()) {
            decimalSeparator = observationValue.getDecimalSeparator();
        }
        return createTextEncoding(tupleSeparator, tokenSeparator, decimalSeparator);
    }

    /**
     * Create a TextEncoding object for token and tuple separators.
     * 
     * @param tupleSeparator
     *            Token separator
     * @param tokenSeparator
     *            Tuple separator
     * @param decimalSeparator
     *            Decimal separator
     * @return TextEncoding
     */
    private static SweAbstractEncoding createTextEncoding(String tupleSeparator, String tokenSeparator, String decimalSeparator) {
        SweTextEncoding sosTextEncoding = new SweTextEncoding();
        sosTextEncoding.setBlockSeparator(tupleSeparator);
        sosTextEncoding.setTokenSeparator(tokenSeparator);
        if (StringHelper.isNotEmpty(decimalSeparator)) {
            sosTextEncoding.setDecimalSeparator(decimalSeparator);
        }
        return sosTextEncoding;
    }

    private static List<String> createBlock(SweAbstractDataComponent elementType, Time phenomenonTime, String phenID,
            Value<?> value) {
        if (elementType instanceof SweDataRecord) {
            SweDataRecord elementTypeRecord = (SweDataRecord) elementType;
            List<String> block = new ArrayList<String>(elementTypeRecord.getFields().size());
            for (SweField sweField : elementTypeRecord.getFields()) {
                if (!(value instanceof NilTemplateValue)) {
                    if (sweField.getElement() instanceof SweTime || sweField.getElement() instanceof SweTimeRange) {
                        block.add(DateTimeHelper.format(phenomenonTime));
                    } else if (sweField.getElement() instanceof SweAbstractDataComponent
                            && sweField.getElement().getDefinition().equals(phenID)) {
                        block.add(value.getValue().toString());
                    } else if (sweField.getElement() instanceof SweObservableProperty) {
                        block.add(phenID);
                    }
                }
            }
            return block;
        }
        String exceptionMsg =
                String.format("Type of ElementType is not supported: %s", elementType != null ? elementType.getClass()
                        .getName() : "null");
        LOGGER.debug(exceptionMsg);
        throw new IllegalArgumentException(exceptionMsg);
    }

    /**
     * Create a {@link SweQuantity} from parameter
     * 
     * @param value
     *            the {@link SweQuantity} value
     * @param axis
     *            the {@link SweQuantity} axis id
     * @param uom
     *            the {@link SweQuantity} unit of measure
     * @return the {@link SweQuantity} from parameter
     */
    public static SweQuantity createSweQuantity(Object value, String axis, String uom) {
        return new SweQuantity().setAxisID(axis).setUom(uom).setValue(JavaHelper.asDouble(value));
    }

    private SweHelper() {
    }

}
