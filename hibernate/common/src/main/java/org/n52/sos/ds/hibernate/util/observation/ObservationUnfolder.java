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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.n52.sos.ds.hibernate.dao.observation.ValueCreatingSweDataComponentVisitor;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.ObservationMergeIndicator;
import org.n52.sos.ogc.om.ObservationMerger;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.ParameterHolder;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.ProfileLevel;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweCoordinate;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweVector;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.util.Constants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.IncDecInteger;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.SweHelper;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class ObservationUnfolder {
    private final OmObservation multiObservation;

    private final SweHelper helper = new SweHelper();

    public ObservationUnfolder(OmObservation multiObservation) {
        this.multiObservation = multiObservation;
    }

    public List<OmObservation> unfold() throws OwsExceptionReport {
        return unfold(false);
    }

    public List<OmObservation> unfold(boolean complexToSingleProfiles) throws OwsExceptionReport {
        if (multiObservation.getValue() instanceof SingleObservationValue) {
            return Collections.singletonList(multiObservation);
        } else {
            final List<OmObservation> observationCollection = new ArrayList<OmObservation>();
            Map<String, AbstractFeature> features = new HashMap<>();
            Map<String, SosProcedureDescription> procedures = new HashMap<>();
            boolean complex = false;
            if (((MultiObservationValues<?>) multiObservation.getValue()).getValue() instanceof SweDataArrayValue) {
                final SweDataArrayValue arrayValue =
                        (SweDataArrayValue) ((MultiObservationValues<?>) multiObservation.getValue()).getValue();
                final List<List<String>> values = arrayValue.getValue().getValues();
                SweDataRecord elementType = null;
                if (arrayValue.getValue().getElementType() != null
                        && arrayValue.getValue().getElementType() instanceof SweDataRecord) {
                    elementType = (SweDataRecord) arrayValue.getValue().getElementType();
                } else {
                    throw new NoApplicableCodeException().withMessage("sweElementType type \"%s\" not supported",
                            elementType != null ? elementType.getClass().getName() : "null");
                }

                for (final List<String> block : values) {
                    IncDecInteger tokenIndex = new IncDecInteger();
                    Time phenomenonTime = null;
                    TimeInstant resultTime = null;
                    final List<Value<?>> observedValues = new LinkedList<Value<?>>();
                    // map to store the observed properties
                    final Map<Value<?>, String> definitionsForObservedValues = Maps.newHashMap();
                    Value<?> observedValue = null;
                    GeometryHolder samplingGeometry = new GeometryHolder();
                    ParameterHolder parameterHolder = getParameterHolder(multiObservation.getParameterHolder());
                    String featureOfInterest = null;
                    String procedure = null;
                    for (SweField field : elementType.getFields()) {
                        final SweAbstractDataComponent dataComponent = field.getElement();
                        String token = block.get(tokenIndex.get());
                        /*
                         * get phenomenon time
                         */
                        if (dataComponent instanceof SweTime) {
                            try {
                                if (dataComponent.isSetDefinition()
                                        && OmConstants.RESULT_TIME.equals(dataComponent.getDefinition())) {
                                    resultTime = new TimeInstant(DateTimeHelper.parseIsoString2DateTime(token));
                                } else {
                                    if (phenomenonTime == null) {
                                        phenomenonTime =
                                                new TimeInstant(DateTimeHelper.parseIsoString2DateTime(token));
                                    }
                                }
                            } catch (final OwsExceptionReport e) {
                                throw e;
                            } catch (final Exception e) {
                                /*
                                 * FIXME what is the valid exception code if the
                                 * result is not correct?
                                 */
                                throw new NoApplicableCodeException().causedBy(e)
                                        .withMessage("Error while parse time String to DateTime!");
                            }
                        } else if (dataComponent instanceof SweTimeRange) {
                            try {
                                final String[] subTokens = token.split("/");
                                phenomenonTime = new TimePeriod(DateTimeHelper.parseIsoString2DateTime(subTokens[0]),
                                        DateTimeHelper.parseIsoString2DateTime(subTokens[1]));
                            } catch (final OwsExceptionReport e) {
                                throw e;
                            } catch (final Exception e) {
                                /*
                                 * FIXME what is the valid exception code if the
                                 * result is not correct?
                                 */
                                throw new NoApplicableCodeException().causedBy(e)
                                        .withMessage("Error while parse time String to DateTime!");
                            }
                        }
                        /*
                         * observation values
                         */
                        else if (dataComponent instanceof SweAbstractSimpleType) {
                            if (dataComponent instanceof SweText
                                    && dataComponent.getDefinition().contains("om:featureOfInterest")) {
                                featureOfInterest = token;
                            } else if (dataComponent instanceof SweText
                                    && dataComponent.getDefinition().contains("om:procedure")) {
                                procedure = token;
                            } else if (dataComponent instanceof SweQuantity && checkDefinitionForDephtHeight(field)) {
                                parseFieldAsParameter(field, token, parameterHolder);
                            } else {
                                observedValue = parseSweAbstractSimpleType(dataComponent, token);
                            }
                        } else if (dataComponent instanceof SweDataRecord) {
                            try {
                                if (dataComponent.getDefinition().contains(OmConstants.OM_PARAMETER)) {
                                    parseDataRecordAsParameter((SweDataRecord) dataComponent, block, tokenIndex,
                                            parameterHolder);
                                } else {
                                    observedValue = parseSweDataRecord(((SweDataRecord) dataComponent).clone(), block,
                                            tokenIndex, parameterHolder);
                                }
                            } catch (CloneNotSupportedException e) {
                                throw new NoApplicableCodeException().causedBy(e).withMessage(
                                        "Unable to copy element '%s'.", dataComponent.getClass().getName());
                            }
                        } else if (dataComponent instanceof SweDataArray) {
                            try {
                                observedValue = parseSweDataArray(((SweDataArray) dataComponent).clone(), block,
                                        tokenIndex, parameterHolder, multiObservation.getObservationConstellation()
                                                .getObservablePropertyIdentifier());
                            } catch (CloneNotSupportedException e) {
                                throw new NoApplicableCodeException().causedBy(e).withMessage(
                                        "Unable to copy element '%s'.", dataComponent.getClass().getName());
                            }
                        } else if (dataComponent instanceof SweVector) {
                            try {
                                parseSweVectorAsGeometry(((SweVector) dataComponent).clone(), block, tokenIndex,
                                        samplingGeometry);
                            } catch (CloneNotSupportedException e) {
                                throw new NoApplicableCodeException().causedBy(e).withMessage(
                                        "Unable to copy element '%s'.", dataComponent.getClass().getName());
                            }
                        } else {
                            throw new NoApplicableCodeException().withMessage("sweField type '%s' not supported",
                                    dataComponent != null ? dataComponent.getClass().getName() : "null");
                        }
                        if (observedValue != null) {

                            definitionsForObservedValues.put(observedValue, dataComponent.getDefinition());
                            observedValues.add(observedValue);
                            observedValue = null;
                        }
                        tokenIndex.incrementAndGet();
                    }
                    for (final Value<?> iValue : observedValues) {
                        List<OmObservation> newObservations = new ArrayList<>();
                        if (isProfileObservations(parameterHolder)) {
                            if (iValue instanceof ComplexValue && complexToSingleProfiles) {
                                complex = true;
                                for (SweField field : ((ComplexValue) iValue).getValue().getFields()) {
                                    if (!checkDefinitionForDephtHeight(field)) {
                                        String definition = field.getElement().getDefinition();
                                        newObservations
                                                .add(createSingleValueObservation(multiObservation, phenomenonTime,
                                                        resultTime, definition,
                                                        convertToProfileValue(
                                                                field.accept(ValueCreatingSweDataComponentVisitor
                                                                        .getInstance()),
                                                                samplingGeometry, phenomenonTime, parameterHolder)));
                                    }
                                }

                            } else {
                                newObservations.add(createSingleValueObservation(multiObservation, phenomenonTime,
                                        resultTime, convertToProfileValue(iValue, samplingGeometry, phenomenonTime,
                                                parameterHolder)));
                            }
                            if (parameterHolder.isSetHeightDepthParameter()) {
                                parameterHolder.removeParameter(parameterHolder.getHeightDepthParameter());
                            }
                            if (parameterHolder.isSetFromToParameter()) {
                                parameterHolder.removeParameter(parameterHolder.getFromParameter());
                                parameterHolder.removeParameter(parameterHolder.getToParameter());
                            }

                        } else {
                            newObservations.add(createSingleValueObservation(multiObservation, phenomenonTime,
                                    resultTime, iValue));
                        }
                        for (OmObservation newObservation : newObservations) {
                            if (samplingGeometry != null && samplingGeometry.hasGeometry()) {
                                newObservation.addSpatialFilteringProfileParameter(samplingGeometry.getGeometry());
                            }
                            if (!Strings.isNullOrEmpty(featureOfInterest)) {
                                if (!features.containsKey(featureOfInterest)) {
                                    features.put(featureOfInterest,
                                            new SamplingFeature(new CodeWithAuthority(featureOfInterest)));
                                }
                                newObservation.getObservationConstellation()
                                        .setFeatureOfInterest(features.get(featureOfInterest));
                            }
                            if (!Strings.isNullOrEmpty(procedure)) {
                                if (!procedures.containsKey(procedure)) {
                                    procedures.put(procedure, new SensorML().setIdentifier(procedure));
                                }
                                newObservation.getObservationConstellation().setProcedure(procedures.get(procedure));
                            }
                            if (parameterHolder.isSetParameter()) {
                                newObservation.setParameter(parameterHolder.getParameter());
                            }
                            observationCollection.add(newObservation);
                        }
                    }
                    featureOfInterest = null;
                    procedure = null;
                }
            }
            if (isProfileObservations()) {
                if (complex
                        || observationCollection.get(0).getObservationConstellation().getProcedure().isSetMobile()) {
                    List<OmObservation> observations = new ArrayList<>();
                    for (List<OmObservation> list : getProfileLists(observationCollection)) {
                        observations.addAll(new ObservationMerger().mergeObservations(list,
                                ObservationMergeIndicator.defaultObservationMergerIndicator()));
                    }
                    return observations;
                } else {
                    List<OmObservation> observations = new ArrayList<>();
                    for (List<OmObservation> list : getProfileLists(observationCollection, 1)) {
                        observations.addAll(new ObservationMerger().mergeObservations(list,
                                ObservationMergeIndicator.defaultObservationMergerIndicator()));
                    }
                    return observations;
                }
            }
            return observationCollection;
        }
    }

    private List<List<OmObservation>> getProfileLists(List<OmObservation> observationCollection) {
        return getProfileLists(observationCollection, 5);
    }
    
    private List<List<OmObservation>> getProfileLists(List<OmObservation> observationCollection, int time) {
        List<List<OmObservation>> list = new ArrayList<>();
        Map<DateTime, List<OmObservation>> map = getMap(observationCollection);
        DateTime currentTime = null;
        List<OmObservation> currentObservations = new ArrayList<>();
        for (Entry<DateTime, List<OmObservation>> entry : map.entrySet()) {
            if (currentTime != null && Minutes.minutesBetween(currentTime, entry.getKey()).getMinutes() > time) {
                list.add(Lists.newArrayList(currentObservations));
                currentObservations.clear();
            }
            currentObservations.addAll(entry.getValue());
            currentTime = entry.getKey();
        }
        list.add(Lists.newArrayList(currentObservations));
        return list;
    }

    private Map<DateTime, List<OmObservation>> getMap(List<OmObservation> observationCollection) {
        Map<DateTime, List<OmObservation>> map = new TreeMap<>();
        for (OmObservation omObservation : observationCollection) {
            DateTime time = null;
            if (omObservation.getPhenomenonTime() instanceof TimeInstant) {
                time = ((TimeInstant) omObservation.getPhenomenonTime()).getValue();
            } else if (omObservation.getPhenomenonTime() instanceof TimePeriod) {
                time = ((TimePeriod) omObservation.getPhenomenonTime()).getStart();
            }
            List list = map.containsKey(time) ? map.get(time) : new LinkedList<>();
            list.add(omObservation);
            map.put(time, list);
        }
        return map;
    }

    private Value<?> parseSweAbstractSimpleType(SweAbstractDataComponent dataComponent, String token)
            throws CodedException {
        Value<?> observedValue = null;
        if (dataComponent instanceof SweQuantity) {
            observedValue = new QuantityValue(Double.parseDouble(token));
            observedValue.setUnit(((SweQuantity) dataComponent).getUom());
        } else if (dataComponent instanceof SweBoolean) {
            observedValue = new BooleanValue(Boolean.parseBoolean(token));
        } else if (dataComponent instanceof SweText) {
            observedValue = new TextValue(token);
        } else if (dataComponent instanceof SweCategory) {
            observedValue = new CategoryValue(token);
            observedValue.setUnit(((SweCategory) dataComponent).getCodeSpace());
        } else if (dataComponent instanceof SweCount) {
            observedValue = new CountValue(Integer.parseInt(token));
        } else {
            throw new NoApplicableCodeException().withMessage("sweField type '%s' not supported",
                    dataComponent != null ? dataComponent.getClass().getName() : "null");
        }
        return observedValue;
    }

    private Value<?> parseSweDataRecord(SweDataRecord record, List<String> block, IncDecInteger tokenIndex,
            ParameterHolder parameterHolder) throws CodedException {
        boolean tokenIndexIncreased = false;
        for (SweField field : record.getFields()) {
            String token = block.get(tokenIndex.get());
            if (field.getElement() instanceof SweQuantity) {
                ((SweQuantity) field.getElement()).setValue(Double.parseDouble(token));
            } else if (field.getElement() instanceof SweBoolean) {
                ((SweBoolean) field.getElement()).setValue(Boolean.parseBoolean(token));
            } else if (field.getElement() instanceof SweText) {
                ((SweText) field.getElement()).setValue(token);
            } else if (field.getElement() instanceof SweCategory) {
                ((SweCategory) field.getElement()).setValue(token);
            } else if (field.getElement() instanceof SweCount) {
                ((SweCount) field.getElement()).setValue(Integer.parseInt(token));
            } else {
                throw new NoApplicableCodeException().withMessage("sweField type '%s' not yet supported",
                        field != null ? field.getClass().getName() : "null");
            }
            tokenIndex.incrementAndGet();
            tokenIndexIncreased = true;
        }
        // decrease token index because it is increased in the calling method.
        if (tokenIndexIncreased) {
            tokenIndex.decrementAndGet();
        }
        return new ComplexValue(record);
    }
    
    private Value<?> parseSweDataArray(SweDataArray dataArray, List<String> block, IncDecInteger tokenIndex,
            ParameterHolder parameterHolder, String observedProperty) throws CodedException {
        List<List<String>> values = new LinkedList<>();
        values.add(block.subList(4, block.size()));
        dataArray.setValues(values);
        SweDataArrayValue sweDataArrayValue = new SweDataArrayValue(dataArray);
        SweField field = ((SweDataRecord) dataArray.getElementType()).getFieldByIdentifier(observedProperty);
        if (field != null && field.getElement() instanceof SweAbstractUomType) {
            sweDataArrayValue.setUnit(((SweAbstractUomType) field.getElement()).getUomObject());
        }
        return sweDataArrayValue;
    }

    private OmObservation createSingleValueObservation(final OmObservation multiObservation, final Time phenomenonTime,
            TimeInstant resultTime, final Value<?> iValue) throws CodedException {
        return createSingleValueObservation(multiObservation, phenomenonTime, resultTime,
                getObservationConstellation(multiObservation), iValue);
    }

    private OmObservation createSingleValueObservation(final OmObservation multiObservation, final Time phenomenonTime,
            TimeInstant resultTime, String observedProperty, final Value<?> iValue) throws CodedException {
        /*
         * TODO create new ObservationConstellation only with the specified
         * observed property and observation type
         */
        OmObservationConstellation obsConst = getObservationConstellation(multiObservation);
        // change observedProperty
        obsConst.setObservableProperty(new OmObservableProperty(observedProperty));

        return createSingleValueObservation(multiObservation, phenomenonTime, resultTime, obsConst, iValue);
    }

    private OmObservationConstellation getObservationConstellation(OmObservation multiObservation2)
            throws CodedException {
        try {
            return multiObservation.getObservationConstellation().clone();
        } catch (CloneNotSupportedException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("Error while cloning %s!",
                    OmObservationConstellation.class.getName());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private OmObservation createSingleValueObservation(final OmObservation multiObservation, final Time phenomenonTime,
            TimeInstant resultTime, OmObservationConstellation obsConst, final Value<?> iValue) throws CodedException {
        final ObservationValue<?> value = new SingleObservationValue(phenomenonTime, iValue);
        final OmObservation newObservation = new OmObservation();
        newObservation.setNoDataValue(multiObservation.getNoDataValue());
        newObservation.setObservationConstellation(obsConst);
        newObservation.setValidTime(multiObservation.getValidTime());
        if (resultTime != null && !resultTime.isEmpty()) {
            newObservation.setResultTime(resultTime);
        } else if (multiObservation.isSetResultTime() && !multiObservation.getResultTime().isEmpty()) {
            newObservation.setResultTime(multiObservation.getResultTime());
        } else {
            if (phenomenonTime instanceof TimeInstant) {
                newObservation.setResultTime((TimeInstant) phenomenonTime);
            } else if (phenomenonTime instanceof TimePeriod) {
                newObservation.setResultTime(new TimeInstant(((TimePeriod) phenomenonTime).getEnd()));
            }
        }
        newObservation.setSeriesType(multiObservation.getSeriesType());
        newObservation.setTokenSeparator(multiObservation.getTokenSeparator());
        newObservation.setTupleSeparator(multiObservation.getTupleSeparator());
        newObservation.setDecimalSeparator(multiObservation.getDecimalSeparator());
        newObservation.setResultType(multiObservation.getResultType());
        newObservation.setValue(value);
        return newObservation;
    }

    private void parseSweVectorAsGeometry(SweVector sweVector, List<String> block, IncDecInteger tokenIndex,
            GeometryHolder holder) throws OwsExceptionReport {
        if (OmConstants.PARAM_NAME_SAMPLING_GEOMETRY.equals(sweVector.getDefinition())) {
            if (sweVector.isSetReferenceFrame()) {
                holder.setSrid(SosHelper.parseSrsName(sweVector.getReferenceFrame()));
            }
            boolean tokenIndexIncreased = false;
            for (SweCoordinate<?> coordinate : sweVector.getCoordinates()) {
                String token = block.get(tokenIndex.get());
                if (coordinate.getValue() instanceof SweQuantity) {
                    double value = Double.parseDouble(token);
                    if (helper.checkAltitudeNameDefinition(coordinate)) {
                        holder.setAltitude(value);
                    } else if (helper.checkNorthingNameDefinition(coordinate)) {
                        holder.setLatitude(value);
                    } else if (helper.checkEastingNameDefinition(coordinate)) {
                        holder.setLongitude(value);
                    }
                } else {
                    throw new NoApplicableCodeException().withMessage("sweField type '%s' not yet supported",
                            coordinate != null ? coordinate.getClass().getName() : "null");
                }
                tokenIndex.incrementAndGet();
                tokenIndexIncreased = true;
            }
            // decrease token index because it is increased in the calling
            // method.
            if (tokenIndexIncreased) {
                tokenIndex.decrementAndGet();
            }
        }
    }

    private void parseDataRecordAsParameter(SweDataRecord record, List<String> block, IncDecInteger tokenIndex,
            ParameterHolder parameterHolder) throws CodedException {
        boolean tokenIndexIncreased = false;
        for (SweField field : record.getFields()) {
            String token = block.get(tokenIndex.get());
            parseFieldAsParameter(field, token, parameterHolder);
            tokenIndex.incrementAndGet();
            tokenIndexIncreased = true;
        }
        // decrease token index because it is increased in the calling method.
        if (tokenIndexIncreased) {
            tokenIndex.decrementAndGet();
        }
    }

    private boolean parseFieldAsParameter(SweField field, String token, ParameterHolder parameterHolder)
            throws CodedException {
        Value<?> value = null;
        ReferenceType name = new ReferenceType(field.getElement().getDefinition());
        if (field.getElement() instanceof SweQuantity) {
            value = new QuantityValue(Double.parseDouble(token), ((SweQuantity) field.getElement()).getUomObject());
        } else if (field.getElement() instanceof SweBoolean) {
            value = new BooleanValue(Boolean.parseBoolean(token));
        } else if (field.getElement() instanceof SweText) {
            value = new TextValue(token);
        } else if (field.getElement() instanceof SweCategory) {
            value = new CategoryValue(token);
        } else if (field.getElement() instanceof SweCount) {
            value = new CountValue(Integer.parseInt(token));
        } else {
            throw new NoApplicableCodeException().withMessage("sweField type '%s' not yet supported",
                    field != null ? field.getClass().getName() : "null");
        }
        parameterHolder.addParameter(new NamedValue<>(getParameterName(name), value));
        return true;
    }

    private Value<?> convertToProfileValue(Value<?> value, GeometryHolder samplingGeometry, Time phenomenonTime,
            ParameterHolder parameterHolder) throws OwsExceptionReport {
        ProfileLevel profileLevel = new ProfileLevel();
        profileLevel.setLocation(samplingGeometry.getGeometry());
        profileLevel.setPhenomenonTime(phenomenonTime);
        if (value instanceof ComplexValue) {
            for (SweField field : ((ComplexValue) value).getValue().getFields()) {
                if (!checkDefinitionForDephtHeight(field)) {
                    Value<?> levelValue = field.accept(ValueCreatingSweDataComponentVisitor.getInstance());
                    if (levelValue instanceof SweAbstractDataComponent) {
                        String definition = field.getElement().getDefinition();
                        ((SweAbstractDataComponent) levelValue).setIdentifier(definition);
                        ((SweAbstractDataComponent) levelValue).setDefinition(definition);
                    }
                }
            }
        } else {
            profileLevel.addValue(value);
        }
        if (parameterHolder.isSetHeightDepthParameter()) {
            if (parameterHolder.isSetHeightParameter()) {
                profileLevel.setLevelStart(toQuantityValue(parameterHolder.getHeightParameter()));
            }
            if (parameterHolder.isSetDepthParameter()) {
                profileLevel.setLevelStart(toQuantityValue(parameterHolder.getDepthParameter()));
            }
        } else if (parameterHolder.isSetFromToParameter()) {
            profileLevel.setLevelStart(toQuantityValue(parameterHolder.getFromParameter()));
            profileLevel.setLevelEnd(toQuantityValue(parameterHolder.getToParameter()));
        }
        return new ProfileValue().addValue(profileLevel);
    }

    private QuantityValue toQuantityValue(NamedValue<Double> parameter) {
        QuantityValue value = (QuantityValue) parameter.getValue();
        value.setDefinition(parameter.getName().getHref());
        return value;
    }

    private boolean isProfileObservations() {
        return multiObservation.getObservationConstellation().isSetObservationType() && multiObservation
                .getObservationConstellation().getObservationType().equals(OmConstants.OBS_TYPE_PROFILE_OBSERVATION);
    }

    private boolean isProfileObservations(ParameterHolder parameterHolder) {
        if (isProfileObservations()
                || (multiObservation.getObservationConstellation().isSetObservationType()
                        && multiObservation.getObservationConstellation().getObservationType()
                                .equals(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION)
                        && parameterHolder.isSetHeightDepthParameter())) {
            multiObservation.getObservationConstellation()
                    .setObservationType(OmConstants.OBS_TYPE_PROFILE_OBSERVATION);
            return true;
        }
        return false;
    }

    private boolean checkDefinitionForDephtHeight(SweField field) {
        return field != null && field.getElement().isSetDefinition()
                && (field.getElement().getDefinition().contains("depth")
                        || field.getElement().getDefinition().contains("height")
                        || field.getElement().getDefinition().equalsIgnoreCase("from")
                        || field.getElement().getDefinition().equalsIgnoreCase("to"));
    }

    private ReferenceType getParameterName(ReferenceType name) {
        if (name.getHref().contains("depth")) {
            return (ReferenceType) new ReferenceType().setHref("depth");
        } else if (name.getHref().contains("height")) {
            return (ReferenceType) new ReferenceType().setHref("height");
        } else if (name.getHref().equalsIgnoreCase("from")) {
            return (ReferenceType) new ReferenceType().setHref("from");
        } else if (name.getHref().equalsIgnoreCase("to")) {
            return (ReferenceType) new ReferenceType().setHref("to");
        }
        return name;
    }

    private ParameterHolder getParameterHolder(ParameterHolder parameterHolder) {
        ParameterHolder parameter = new ParameterHolder();
        if (parameterHolder.isSetParameter()) {
            parameter.addParameter(parameterHolder.getParameter());
        }
        return parameter;
    }

    public class GeometryHolder {
        private Double latitude;

        private Double longitude;

        private Double altitude;

        private Integer srid = getGeomtryHandler().getStorage3DEPSG();

        /**
         * @return the latitude
         */
        public Double getLatitude() {
            return latitude;
        }

        /**
         * @param latitude
         *            the latitude to set
         */
        public GeometryHolder setLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        private boolean isSetLatitude() {
            return getLatitude() != null && !getLatitude().isNaN();
        }

        /**
         * @return the longitude
         */
        public Double getLongitude() {
            return longitude;
        }

        /**
         * @param longitude
         *            the longitude to set
         */
        public GeometryHolder setLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        private boolean isSetLongitude() {
            return getLongitude() != null && !getLongitude().isNaN();
        }

        /**
         * @return the altitude
         */
        public Double getAltitude() {
            return altitude;
        }

        /**
         * @param altitude
         *            the altitude to set
         */
        public GeometryHolder setAltitude(Double altitude) {
            this.altitude = altitude;
            return this;
        }

        private boolean isSetAltitude() {
            return getAltitude() != null && !getAltitude().isNaN();
        }

        /**
         * @return the srid
         */
        public Integer getSrid() {
            return srid;
        }

        /**
         * @param srid
         *            the srid to set
         */
        public GeometryHolder setSrid(int srid) {
            this.srid = srid;
            return this;
        }

        public Geometry getGeometry() throws OwsExceptionReport {
            List<Double> coordinates = Lists.newArrayListWithExpectedSize(Constants.INT_2);
            if (getGeomtryHandler().isNorthingFirstEpsgCode(getSrid())) {
                coordinates.add(getLatitude());
                coordinates.add(getLongitude());
            } else {
                coordinates.add(getLongitude());
                coordinates.add(getLatitude());
            }
            Geometry geometry = JTSHelper.createGeometryFromWKT(
                    JTSHelper.createWKTPointFromCoordinateString(Joiner.on(Constants.SPACE_STRING).join(coordinates)),
                    getSrid());
            geometry.setSRID(getSrid());
            if (isSetAltitude() && geometry instanceof Point) {
                ((Point) geometry).getCoordinate().z = getAltitude();
            }
            return geometry;
        }

        private GeometryHandler getGeomtryHandler() {
            return GeometryHandler.getInstance();
        }

        public boolean hasGeometry() {
            return isSetLatitude() && isSetLongitude();
        }

    }

}
