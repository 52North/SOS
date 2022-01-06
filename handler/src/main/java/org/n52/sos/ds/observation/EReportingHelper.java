/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.observation;

import java.util.List;
import java.util.Set;

import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.ereporting.EReportingProfileDataEntity;
import org.n52.series.db.beans.ereporting.HiberanteEReportingRelations.EReportingQualityData;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.AqdConstants.PrimaryObservation;
import org.n52.shetland.aqd.AqdUomRepository;
import org.n52.shetland.aqd.AqdUomRepository.Uom;
import org.n52.shetland.aqd.ElementType;
import org.n52.shetland.iso.gmd.GmdDomainConsistency;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.quality.OmResultQuality;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweTime;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.svalbard.util.SweHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Helper class for eReporting.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public class EReportingHelper {

    private final SweHelper helper;

    /**
     * private constructor
     */
    public EReportingHelper(SweHelper sweHelper) {
        this.helper = sweHelper;
    }

    /**
     * Creates an {@link ObservationValue} from the {@link DataEntity}
     *
     * @param omObservation
     *            Corresponding {@link OmObservation}
     * @param observation
     *            {@link DataEntity} to create {@link ObservationValue} from
     * @return Created {@link ObservationValue}.
     * @throws CodedException
     *             If an error occurs
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public SingleObservationValue<?> createSweDataArrayValue(OmObservation omObservation, DataEntity observation)
            throws CodedException {
        SweDataArrayValue sweDataArrayValue = new SweDataArrayValue();
        sweDataArrayValue.setValue(createSweDataArray(omObservation, observation));
        SingleObservationValue observationValue = new SingleObservationValue(sweDataArrayValue);
        observationValue.setPhenomenonTime(
                getPhenomenonTime(omObservation, DataTimeCreator.createPhenomenonTime(observation)));
        addQuality(observation.getEreportingProfile(), observationValue);
        return observationValue;
    }

    /**
     * Creates an {@link SweDataArray} object from the {@link DataEntity}
     *
     * @param omObservation
     *            Corresponding {@link OmObservation}
     * @param observation
     *            {@link DataEntity} to create {@link SweDataArray} from
     * @return Created {@link SweDataArray}
     */
    public SweDataArray createSweDataArray(OmObservation omObservation, DataEntity observation) {
        SweDataArray sweDataArray = new SweDataArray();
        sweDataArray.setElementCount(createElementCount(omObservation));
        PrimaryObservation primaryObservation =
                PrimaryObservation.from(observation.getEreportingProfile().getPrimaryObservation());
        sweDataArray.setElementType(createElementType(primaryObservation, getUnit(omObservation, observation)));
        sweDataArray.setEncoding(createEncoding(omObservation));
        sweDataArray.setValues(createValue(omObservation, observation, primaryObservation));
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
    public SweDataArray mergeValues(SweDataArray combinedValue, SweDataArray value) {
        if (value.isSetValues()) {
            combinedValue.addAll(value.getValues());
        }
        return combinedValue;
    }

    private String getUnit(OmObservation omObservation, DataEntity observation) {
        if (omObservation.isSetValue() && omObservation.getValue().getValue().isSetUnit()) {
            return omObservation.getValue().getValue().getUnit();
        } else if (observation.getDataset().isSetUnit()) {
            return observation.getDataset().getUnit().getUnit();
        }
        return null;
    }

    private SweCount createElementCount(OmObservation omObservation) {
        if (omObservation.isSetValue() && omObservation.getValue().getValue() instanceof SweDataArrayValue) {
            SweDataArray value = (SweDataArray) omObservation.getValue().getValue().getValue();
            SweCount elementCount = value.getElementCount();
            elementCount.increaseCount();
        }
        return new SweCount().setValue(1);
    }

    private SweAbstractDataComponent createElementType(PrimaryObservation primaryObservation, String unit) {
        SweDataRecord dataRecord = new SweDataRecord();
        dataRecord.setDefinition(AqdConstants.NAME_FIXED_OBSERVATIONS);
        dataRecord.addField(createField(ElementType.START_TIME, createSweTimeSamplingTime(ElementType.START_TIME)));
        dataRecord.addField(createField(ElementType.END_TIME, createSweTimeSamplingTime(ElementType.END_TIME)));
        dataRecord.addField(createField(ElementType.VERIFICATION, createSweCatagory(ElementType.VERIFICATION)));
        dataRecord.addField(createField(ElementType.VALIDITY, createSweCatagory(ElementType.VALIDITY)));
        ElementType value = ElementType.getValueElementType(primaryObservation, unit);
        dataRecord.addField(createField(value, createSweQuantity(value, unit)));
        if (primaryObservation.isMultyDayPrimaryObservation()) {
            dataRecord.addField(createField(ElementType.DATA_CAPTURE, createSweQuantity(ElementType.DATA_CAPTURE)));
        }
        return dataRecord;
    }

    private SweField createField(ElementType elementType, SweAbstractDataComponent content) {
        return new SweField(elementType.getName(), content);
    }

    private SweAbstractDataComponent createSweTimeSamplingTime(ElementType elementType) {
        SweTime time = new SweTime();
        time.setDefinition(elementType.getDefinition());
        if (elementType.isSetUOM()) {
            time.setUom(elementType.getUOM());
        }
        return time;
    }

    private SweAbstractDataComponent createSweCatagory(ElementType elementType) {
        return new SweCategory().setDefinition(elementType.getDefinition());
    }

    private SweAbstractDataComponent createSweQuantity(ElementType elementType) {
        return createSweQuantity(elementType, elementType.getUOM());
    }

    private SweAbstractDataComponent createSweQuantity(ElementType elementType, String unit) {
        SweQuantity quantity = new SweQuantity();
        quantity.setDefinition(elementType.getDefinition());
        Uom aqdUom = AqdUomRepository.getAqdUom(unit);
        if (aqdUom != null) {
            quantity.setUom(aqdUom.getConceptURI());
        } else {
            quantity.setUom(unit);
        }
        return quantity;
    }

    private SweAbstractEncoding createEncoding(OmObservation omObservation) {
        return helper.createTextEncoding(omObservation);
    }

    private void addDoubleValue(List<String> list, Double value) {
        if (value != null) {
            list.add(Double.toString(value));
        } else {
            list.add("");
        }

    }

    private void addIntegerValue(List<String> list, Integer value) {
        if (value != null) {
            list.add(Integer.toString(value));
        } else {
            list.add("");
        }
    }

    private void addValue(List<String> value, DataEntity observation, OmObservation omObservation) {
        if (observation.getValue() != null) {
            // TODO check if this is the best solution
            if (omObservation.isSetDecimalSeparator() && !omObservation.getDecimalSeparator().equals(".")) {
                value.add(observation.getValue().toString().replace(".", omObservation.getDecimalSeparator()));
            } else {
                value.add(observation.getValue().toString());
            }
        } else {
            value.add("");
        }
    }

    private List<List<String>> createValue(OmObservation omObservation, DataEntity observation,
            PrimaryObservation primaryObservation) {
        List<String> value = Lists.newArrayListWithCapacity(5);
        addTimes(value, DataTimeCreator.createPhenomenonTime(observation));
        addIntegerValue(value, observation.getEreportingProfile().getVerification());
        addIntegerValue(value, observation.getEreportingProfile().getValidation());
        addValue(value, observation, omObservation);
        if (primaryObservation.isMultyDayPrimaryObservation()) {
            addDoubleValue(value, observation.getEreportingProfile().getDataCapture());
        }
        List<List<String>> list = Lists.newArrayList();
        list.add(value);
        return list;
    }

    private void addTimes(List<String> value, Time time) {
        if (time instanceof TimeInstant) {
            value.add(DateTimeHelper.formatDateTime2IsoString(((TimeInstant) time).getValue()));
            value.add(DateTimeHelper.formatDateTime2IsoString(((TimeInstant) time).getValue()));
        } else if (time instanceof TimePeriod) {
            value.add(DateTimeHelper.formatDateTime2IsoString(((TimePeriod) time).getStart()));
            value.add(DateTimeHelper.formatDateTime2IsoString(((TimePeriod) time).getEnd()));
        } else {
            value.add("");
            value.add("");
        }
    }

    private Time getPhenomenonTime(OmObservation omObservation, Time time) {
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

    private void addQuality(EReportingProfileDataEntity eReportingProfileDataEntity, SingleObservationValue<?> value)
            throws CodedException {
        value.addQualityList(getGmdDomainConsistency(eReportingProfileDataEntity, false));
    }

    public Set<OmResultQuality> getGmdDomainConsistency(EReportingQualityData eReportingQualityData, boolean force)
            throws CodedException {
        Set<OmResultQuality> set = Sets.newHashSet();
        if (eReportingQualityData.isSetDataCaptureFlag()) {
            set.add(GmdDomainConsistency.dataCapture(eReportingQualityData.getDataCaptureFlag()));
        } else if (force) {
            set.add(GmdDomainConsistency.dataCapture(GmlConstants.NilReason.unknown));
        }
        if (eReportingQualityData.isSetTimeCoverageFlag()) {
            set.add(GmdDomainConsistency.timeCoverage(eReportingQualityData.getTimeCoverageFlag()));
        } else if (force) {
            set.add(GmdDomainConsistency.timeCoverage(GmlConstants.NilReason.unknown));
        }
        if (eReportingQualityData.isSetUncertaintyEstimation()) {
            set.add(GmdDomainConsistency.uncertaintyEstimation(eReportingQualityData.getUncertaintyEstimation()));
        } else if (force) {
            set.add(GmdDomainConsistency.uncertaintyEstimation(GmlConstants.NilReason.unknown));
        }
        return set;
    }

}
