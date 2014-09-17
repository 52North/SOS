/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.List;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdUomRepository;
import org.n52.sos.aqd.AqdConstants.ElementType;
import org.n52.sos.aqd.AqdUomRepository.Uom;
import org.n52.sos.ds.hibernate.entities.AbstractObservationTime;
import org.n52.sos.ds.hibernate.entities.ereporting.HiberanteEReportingRelations.EReportingValues;
import org.n52.sos.ds.hibernate.entities.ereporting.values.EReportingValue;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.util.Constants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.SweHelper;

import com.google.common.collect.Lists;

/**
 * Helper class for eReporting.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.3.0
 *
 */
public class EReportingHelper {

    /**
     * private constructor
     */
    private EReportingHelper() {

    }

    /**
     * Creates an {@link ObservationValue} from the {@link EReportingValue}
     * 
     * @param omObservation
     *            Corresponding {@link OmObservation}
     * @param observation
     *            {@link EReportingValue} to create {@link ObservationValue}
     *            from
     * @return Created {@link ObservationValue}.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static SingleObservationValue<?> createSweDataArrayValue(OmObservation omObservation,
            EReportingValues observation) {
        SweDataArrayValue sweDataArrayValue = new SweDataArrayValue();
        sweDataArrayValue.setValue(createSweDataArray(omObservation, observation));
        SingleObservationValue observationValue = new SingleObservationValue(sweDataArrayValue);
        observationValue.setPhenomenonTime(getPhenomenonTime(omObservation,
                ((AbstractObservationTime) observation).createPhenomenonTime()));
        return observationValue;
    }

    /**
     * Creates an {@link SweDataArray} object from the {@link EReportingValue}
     * 
     * @param omObservation
     *            Corresponding {@link OmObservation}
     * @param observation
     *            {@link EReportingValue} to create {@link SweDataArray} from
     * @return Created {@link SweDataArray}
     */
    public static SweDataArray createSweDataArray(OmObservation omObservation, EReportingValues observation) {
        SweDataArray sweDataArray = new SweDataArray();
        sweDataArray.setElementCount(createElementCount(omObservation));
        sweDataArray.setElementType(createElementType(getUnit(omObservation, observation)));
        sweDataArray.setEncoding(createEncoding(omObservation));
        sweDataArray.setValues(createValue(omObservation, observation));
        return sweDataArray;
    }

    /**
     * Merge {@link SweDataArray}s to a single {@link SweDataArray}
     * 
     * @param combinedValue
     *            {@link SweDataArray} which the data is to be added
     * @param value
     *            {@link SweDataArray} to be added to the other
     * @return Merged {@link SweDataArray}
     */
    public static SweDataArray mergeValues(SweDataArray combinedValue, SweDataArray value) {
        if (value.isSetValues()) {
            combinedValue.addAll(value.getValues());
        }
        return combinedValue;
    }

    private static String getUnit(OmObservation omObservation, EReportingValues observation) {
        if (omObservation.isSetValue() && omObservation.getValue().getValue().isSetUnit()) {
            return omObservation.getValue().getValue().getUnit();
        } else if (observation.isSetUnit()) {
            return observation.getUnit().getUnit();
        }
        return null;
    }

    private static SweCount createElementCount(OmObservation omObservation) {
        if (omObservation.isSetValue() && omObservation.getValue().getValue() instanceof SweDataArrayValue) {
            SweDataArray value = (SweDataArray) omObservation.getValue().getValue().getValue();
            SweCount elementCount = value.getElementCount();
            elementCount.increaseCount();
        }
        return new SweCount().setValue(1);
    }

    private static SweAbstractDataComponent createElementType(String unit) {
        SweDataRecord dataRecord = new SweDataRecord();
        dataRecord.setDefinition(AqdConstants.NAME_FIXED_OBSERVATIONS);
        dataRecord.addField(createField(ElementType.StartTime, createSweTimeSamplingTime(ElementType.StartTime)));
        dataRecord.addField(createField(ElementType.EndTime, createSweTimeSamplingTime(ElementType.EndTime)));
        dataRecord.addField(createField(ElementType.Verification, createSweCatagory(ElementType.Verification)));
        dataRecord.addField(createField(ElementType.Validation, createSweCatagory(ElementType.Validation)));
        dataRecord.addField(createField(ElementType.Pollutant, createSweQuantity(ElementType.Pollutant, unit)));
        return dataRecord;
    }

    private static SweField createField(ElementType elementType, SweAbstractDataComponent content) {
        return new SweField(elementType.name(), content);
    }

    private static SweAbstractDataComponent createSweTimeSamplingTime(ElementType elementType) {
        SweTime time = new SweTime();
        time.setDefinition(elementType.getDefinition());
        if (elementType.isSetUOM()) {
            time.setUom(elementType.getUOM());
        }
        return time;
    }

    private static SweAbstractDataComponent createSweCatagory(ElementType elementType) {
        return new SweCategory().setDefinition(elementType.getDefinition());
    }

    private static SweAbstractDataComponent createSweQuantity(ElementType elementType, String unit) {
        SweQuantity quantity = new SweQuantity();
        quantity.setDefinition(elementType.getDefinition());
        Uom aqdUom = AqdUomRepository.getAqdUom(unit);
        if (aqdUom != null) {
            quantity.setUom(aqdUom.getConceptURI());
        } else {
            quantity.setUom(OGCConstants.UNKNOWN);
        }
        return quantity;
    }

    private static SweAbstractEncoding createEncoding(OmObservation omObservation) {
        return SweHelper.createTextEncoding(omObservation);
    }

    private static List<List<String>> createValue(OmObservation omObservation, EReportingValues observation) {
        List<String> value = Lists.newArrayListWithCapacity(5);
        addTimes(value, ((AbstractObservationTime) observation).createPhenomenonTime());
        addIntegerValue(value, observation.getVerification());
        addIntegerValue(value, observation.getValidation());
        addPollutant(value, observation);
        List<List<String>> list = Lists.newArrayList();
        list.add(value);
        return list;
    }

    private static void addIntegerValue(List<String> list, Integer value) {
        if (value != null) {
            list.add(Integer.toString(value));
        } else {
            list.add(Constants.EMPTY_STRING);
        }
    }

    private static void addPollutant(List<String> value, OmObservation omObservation) {
        if (omObservation.getValue() instanceof SingleObservationValue<?>) {
            value.add(JavaHelper.asString(omObservation.getValue().getValue().getValue()));
        } else {
            value.add(Constants.EMPTY_STRING);
        }
    }

    private static void addPollutant(List<String> value, EReportingValues observation) {
        if (observation.isSetValue()) {
            value.add(observation.getValueAsString());
        } else {
            value.add(Constants.EMPTY_STRING);
        }
    }

    private static void addTimes(List<String> value, Time time) {
        if (time instanceof TimeInstant) {
            value.add(DateTimeHelper.formatDateTime2IsoString(((TimeInstant) time).getValue()));
            value.add(DateTimeHelper.formatDateTime2IsoString(((TimeInstant) time).getValue()));
        } else if (time instanceof TimePeriod) {
            value.add(DateTimeHelper.formatDateTime2IsoString(((TimePeriod) time).getStart()));
            value.add(DateTimeHelper.formatDateTime2IsoString(((TimePeriod) time).getEnd()));
        } else {
            value.add(Constants.EMPTY_STRING);
            value.add(Constants.EMPTY_STRING);
        }
    }

    private static Time getPhenomenonTime(OmObservation omObservation, Time time) {
        if (omObservation.isSetValue() && omObservation.getPhenomenonTime() != null) {
            if (omObservation.getPhenomenonTime() instanceof TimePeriod) {
                TimePeriod timePeriod = (TimePeriod) omObservation.getPhenomenonTime();
                timePeriod.extendToContain(time);
                return timePeriod;
            } else {
                TimePeriod timePeriod = new TimePeriod();
                timePeriod.extendToContain(omObservation.getPhenomenonTime());
                timePeriod.extendToContain(time);
                return timePeriod;
            }
        }
        return time;
    }
}
