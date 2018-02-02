/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.MultiObservationValues;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationMergeIndicator;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.ParameterHolder;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.ProfileLevel;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweCoordinate;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.SweVector;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swe.simpleType.SweTime;
import org.n52.shetland.ogc.swe.simpleType.SweTimeRange;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.JTSHelper;
import org.n52.sos.ds.hibernate.dao.observation.ValueCreatingSweDataComponentVisitor;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.IncDecInteger;
import org.n52.svalbard.util.SweHelper;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public class ObservationUnfolder {
    private final OmObservation multiObservation;

    private final SweHelper helper;

    public ObservationUnfolder(OmObservation multiObservation, SweHelper sweHelper) {
        this.multiObservation = multiObservation;
        this.helper = sweHelper;
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
            Map<String, SosProcedureDescription<?>> procedures = new HashMap<>();
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
                    ParameterHolder parameterHolder = new ParameterHolder();
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
                                if (dataComponent.getDefinition().contains(OmConstants.OM_PARAMETER)) {
                                    parseDataRecordAsParameter((SweDataRecord) dataComponent, block, tokenIndex,
                                            parameterHolder);
                                } else {
                                    observedValue =
                                            parseSweDataRecord(((SweDataRecord) dataComponent).copy(), block, tokenIndex, parameterHolder);
                                }
                        } else if (dataComponent instanceof SweVector) {
                                parseSweVectorAsGeometry(((SweVector) dataComponent).copy(), block, tokenIndex,
                                        samplingGeometry);
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
                                try {
                                    newObservation.addSpatialFilteringProfileParameter(samplingGeometry.getGeometry());
                                } catch (ParseException e) {
                                   throw new NoApplicableCodeException().causedBy(e);
                                }
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
                                    procedures.put(procedure, new SosProcedureDescription<AbstractFeature>(new SensorML().setIdentifier(procedure)));
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
                if (complex) {
                    List<OmObservation> observations = new ArrayList<>();
                    for (ObservationStream stream : getProfileLists(observationCollection)) {
                        observations.addAll(toList(stream.merge(ObservationMergeIndicator.sameObservationConstellation())));
                    }
                    return observations;
                } else {
                    return toList(ObservationStream.of(observationCollection).merge(ObservationMergeIndicator.sameObservationConstellation().setPhenomenonTime(true)));
                }
            }
            return observationCollection;
        }
    }

    private List<OmObservation> toList(ObservationStream stream) throws NoSuchElementException, OwsExceptionReport {
        List<OmObservation> observations = new ArrayList<>();
        while (stream.hasNext()) {
            observations.add(stream.next());
        }
        return observations;
    }

    private List<ObservationStream> getProfileLists(List<OmObservation> observationCollection) {
        List<ObservationStream> list = new ArrayList<>();
        Map<DateTime, OmObservation> map = getMap(observationCollection);
        DateTime currentTime = null;
        List<OmObservation> currentObservations = new ArrayList<>();
        for (Entry<DateTime, OmObservation> entry : map.entrySet()) {
            if (currentTime != null && Minutes.minutesBetween(currentTime, entry.getKey()).getMinutes() > 5) {
                list.add(ObservationStream.of(currentObservations));
                currentObservations.clear();
            }
            currentObservations.add(entry.getValue());
            currentTime = entry.getKey();
        }
        list.add(ObservationStream.of(currentObservations));
        return list;
    }

    private Map<DateTime, OmObservation> getMap(List<OmObservation> observationCollection) {
        Map<DateTime, OmObservation> map = new TreeMap<>();
        for (OmObservation omObservation : observationCollection) {
            if (omObservation.getPhenomenonTime() instanceof TimeInstant) {
                map.put(((TimeInstant) omObservation.getPhenomenonTime()).getValue(), omObservation);
            } else if (omObservation.getPhenomenonTime() instanceof TimePeriod) {
                map.put(((TimePeriod) omObservation.getPhenomenonTime()).getStart(), omObservation);
            }
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
                if (checkDefinitionForDephtHeight(field)) {
                    parseFieldAsParameter(field, token, parameterHolder);
                } else {
                    ((SweQuantity) field.getElement()).setValue(new BigDecimal(token));
                }
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

    private OmObservationConstellation getObservationConstellation(OmObservation multiObservation) {
            return multiObservation.getObservationConstellation().copy();
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
                holder.setSrid(getCrsFromString(sweVector.getReferenceFrame()));
            }
            boolean tokenIndexIncreased = false;
            for (SweCoordinate<?> coordinate : sweVector.getCoordinates()) {
                String token = block.get(tokenIndex.get());
                if (coordinate.getValue() instanceof SweQuantity) {
                    double value = Double.parseDouble(token);
                    if (helper.hasAltitudeName(coordinate.getName(), coordinate.getValue().getDefinition())) {
                        holder.setAltitude(value);
                    } else if (helper.hasNorthingName(coordinate.getName(), coordinate.getValue().getDefinition())) {
                        holder.setLatitude(value);
                    } else if (helper.hasEastingName(coordinate.getName(), coordinate.getValue().getDefinition())) {
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

    private boolean parseFieldAsParameter(SweField field, String token, ParameterHolder parameterHolder) throws CodedException {
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
        try {
            profileLevel.setLocation(samplingGeometry.getGeometry());
        } catch (ParseException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
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
        return new ProfileValue("").addValue(profileLevel);
    }

    private QuantityValue toQuantityValue(NamedValue<BigDecimal> parameter) {
        QuantityValue value = (QuantityValue) parameter.getValue();
        value.setDefinition(parameter.getName().getHref());
        return value;
    }

    private boolean isProfileObservations() {
        return multiObservation.getObservationConstellation().isSetObservationType()
                && multiObservation.getObservationConstellation().getObservationType().equals(OmConstants.OBS_TYPE_PROFILE_OBSERVATION);
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

    protected int getCrsFromString(String crs) throws OwsExceptionReport {
        if (!Strings.isNullOrEmpty(crs) && !"NOT_SET".equalsIgnoreCase(crs)) {
            int lastIndex = 0;
            if (crs.startsWith("http")) {
                lastIndex = crs.lastIndexOf('/');
            } else if (crs.indexOf(':') != -1) {
                lastIndex = crs.lastIndexOf(':');
            }
            try {
                return lastIndex == 0 ? Integer.valueOf(crs) : Integer.valueOf(crs.substring(lastIndex + 1));
            } catch (final NumberFormatException nfe) {
                String parameter = new StringBuilder().append(SosConstants.GetObservationParams.srsName.name())
                        .append('/').append(OWSConstants.AdditionalRequestParams.crs.name()).toString();
                throw new NoApplicableCodeException().causedBy(nfe).at(parameter).withMessage(
                        "Error while parsing '%s' parameter! Parameter has to contain EPSG code number", parameter);
            }
        }
        throw new MissingParameterValueException(OWSConstants.AdditionalRequestParams.crs);
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

        public Geometry getGeometry() throws ParseException {
            List<Double> coordinates = Lists.newArrayListWithExpectedSize(2);
            if (getGeomtryHandler().isNorthingFirstEpsgCode(getSrid())) {
                coordinates.add(getLatitude());
                coordinates.add(getLongitude());
            } else {
                coordinates.add(getLongitude());
                coordinates.add(getLatitude());
            }
            Geometry geometry = JTSHelper.createGeometryFromWKT(
                    JTSHelper.createWKTPointFromCoordinateString(Joiner.on(" ").join(coordinates)),
                    getSrid());
            geometry.setSRID(getSrid());
            if (isSetAltitude() && geometry instanceof Point) {
                ((Point) geometry).getCoordinate().y = getAltitude();
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
