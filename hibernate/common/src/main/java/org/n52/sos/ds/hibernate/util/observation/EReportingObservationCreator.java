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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingBlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingBooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingCategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingCountObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingGeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingNumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingTextObservation;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;

public class EReportingObservationCreator implements AdditionalObservationCreator<EReportingSeries> {

    private static final Set<AdditionalObservationCreatorKey> KEYS
            = AdditionalObservationCreatorRepository
            .encoderKeysForElements(AqdConstants.NS_AQD,
                                    AbstractEReportingObservation.class,
                                    EReportingBlobObservation.class,
                                    EReportingBooleanObservation.class,
                                    EReportingCategoryObservation.class,
                                    EReportingCountObservation.class,
                                    EReportingGeometryObservation.class,
                                    EReportingNumericObservation.class,
                                    EReportingSweDataArrayObservation.class,
                                    EReportingTextObservation.class);

    private final EReportingObservationHelper helper
            = new EReportingObservationHelper();

    @Override
    public Set<AdditionalObservationCreatorKey> getKeys() {
        return Collections.unmodifiableSet(KEYS);
    }

    @Override
    public OmObservation create(OmObservation omObservation, Observation<?> observation) {
        if (observation instanceof EReportingObservation) {
            EReportingObservation<?> eReportingObservation = (EReportingObservation<?>) observation;
            create(omObservation, eReportingObservation.getEReportingSeries());
            add(omObservation, observation);
            omObservation.setValue(EReportingHelper.createSweDataArrayValue(omObservation, eReportingObservation));
            omObservation.getObservationConstellation().setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        }
        return omObservation;
    }
    
    @Override
    public OmObservation create(OmObservation omObservation, EReportingSeries series) {
        for (NamedValue<?> namedValue : helper.createOmParameterForEReporting(series)) {
            omObservation.addParameter(namedValue);
        }
        return omObservation;
    }

    @Override
    public OmObservation add(OmObservation omObservation, Observation<?> observation) {
        if (observation instanceof EReportingObservation) {
            EReportingObservation<?> eReportingObservation = (EReportingObservation<?>) observation;
            omObservation.setAdditionalMergeIndicator(eReportingObservation.getPrimaryObservation());
        }
        return omObservation;
    }

//    @Override
//    public OmObservation add(OmObservation omObservation, AbstractObservation observation) {
//        if (observation instanceof EReportingObservation) {
//            EReportingObservation eReportingObservation = (EReportingObservation) observation;
//            omObservation.setAdditionalMergeIndicator(eReportingObservation.getPrimaryObservation());
//        }
//        return omObservation;
//    }
//
//    private void addQualityFlags(SingleObservationValue<?> value, AbstractEReportingObservation<?> observation) {
//        value.addQuality(new SosQuality(ElementType.Validation.name(), null, Integer.toString(observation
//                .getValidation()), ElementType.Validation.getDefinition(), QualityType.category));
//        value.addQuality(new SosQuality(ElementType.Verification.name(), null, Integer.toString(observation
//                .getVerification()), ElementType.Verification.getDefinition(), QualityType.category));
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    private SingleObservationValue<?> createSweDataArrayValue(OmObservation omObservation,
//            AbstractEReportingObservation observation) {
//        SweDataArray sweDataArray = new SweDataArray();
//        sweDataArray.setElementCount(createElementCount(omObservation));
//        sweDataArray.setElementType(createElementType(omObservation.getValue().getValue().getUnit()));
//        sweDataArray.setEncoding(createEncoding(omObservation));
//        sweDataArray.setValues(createValue(omObservation, observation));
//        SweDataArrayValue sweDataArrayValue = new SweDataArrayValue();
//        sweDataArrayValue.setValue(sweDataArray);
//        SingleObservationValue observationValue = new SingleObservationValue(sweDataArrayValue);
//        observationValue.setPhenomenonTime(omObservation.getPhenomenonTime());
//        return observationValue;
//    }
//
//    private SweCount createElementCount(OmObservation omObservation) {
//        return new SweCount().setValue(1);
//    }
//
//    private SweAbstractDataComponent createElementType(String unit) {
//        SweDataRecord dataRecord = new SweDataRecord();
//        dataRecord.setDefinition(AqdConstants.NAME_FIXED_OBSERVATIONS);
//        dataRecord.addField(createField(ElementType.StartTime, createSweTimeSamplingTime(ElementType.StartTime)));
//        dataRecord.addField(createField(ElementType.EndTime, createSweTimeSamplingTime(ElementType.EndTime)));
//        dataRecord.addField(createField(ElementType.Verification, createSweCatagory(ElementType.Verification)));
//        dataRecord.addField(createField(ElementType.Validation, createSweCatagory(ElementType.Validation)));
//        dataRecord.addField(createField(ElementType.Pollutant, createSweQuantity(ElementType.Pollutant, unit)));
//        return dataRecord;
//    }
//
//    private SweField createField(ElementType elementType, SweAbstractDataComponent content) {
//        return new SweField(elementType.name(), content);
//    }
//
//    private SweAbstractDataComponent createSweTimeSamplingTime(ElementType elementType) {
//        SweTime time = new SweTime();
//        time.setDefinition(elementType.getDefinition());
//        if (elementType.isSetUOM()) {
//            time.setUom(elementType.getUOM());
//        }
//        return omObservation;
//    }
//
//    private SweAbstractEncoding createEncoding(OmObservation omObservation) {
//        return SweHelper.createTextEncoding(omObservation);
//    }
//
//    private List<List<String>> createValue(OmObservation omObservation, AbstractEReportingObservation<?> observation) {
//        List<String> value = Lists.newArrayListWithCapacity(5);
//        addTimes(value, omObservation.getPhenomenonTime());
//        addIntegerValue(value, observation.getVerification());
//        addIntegerValue(value, observation.getValidation());
//        addPollutant(value, omObservation);
//        List<List<String>> list = Lists.newArrayList();
//        list.add(value);
//        return list;
//    }
//
//    private void addIntegerValue(List<String> list, Integer value) {
//        if (value != null) {
//            list.add(Integer.toString(value));
//        } else {
//            list.add(Constants.EMPTY_STRING);
//        }
//    }
//
//    private void addPollutant(List<String> value, OmObservation omObservation) {
//        if (omObservation.getValue() instanceof SingleObservationValue<?>) {
//            value.add(JavaHelper.asString(omObservation.getValue().getValue().getValue()));
//        } else {
//            value.add(Constants.EMPTY_STRING);
//        }
//    }
//
//    private void addTimes(List<String> value, Time time) {
//        if (time instanceof TimeInstant) {
//            value.add(DateTimeHelper.formatDateTime2IsoString(((TimeInstant) time).getValue()));
//            value.add(DateTimeHelper.formatDateTime2IsoString(((TimeInstant) time).getValue()));
//        } else if (time instanceof TimePeriod) {
//            value.add(DateTimeHelper.formatDateTime2IsoString(((TimePeriod) time).getStart()));
//            value.add(DateTimeHelper.formatDateTime2IsoString(((TimePeriod) time).getEnd()));
//        } else {
//            value.add(Constants.EMPTY_STRING);
//            value.add(Constants.EMPTY_STRING);
//        }
//    }

}
