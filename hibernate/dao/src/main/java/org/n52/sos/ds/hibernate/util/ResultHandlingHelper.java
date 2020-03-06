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
package org.n52.sos.ds.hibernate.util;

import static org.n52.sos.util.DateTimeHelper.formatDateTime2IsoString;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ProfileObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.ParameterHolder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweCoordinate;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweVector;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.IncDecInteger;
import org.n52.sos.util.SweHelper;

import com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * @since 4.0.0
 *
 */
public class ResultHandlingHelper {

    private final String RESULT_TIME = OmConstants.RESULT_TIME;
    private final String PHENOMENON_TIME = OmConstants.PHENOMENON_TIME;
    public final String OM_PROCEDURE = "om:procedure";
    public final String OM_FEATURE_OF_INTEREST = "om:featureOfInterest";
    private final SweHelper helper = new SweHelper();
    
    public ResultHandlingHelper() {
        
    }

    /**
     * Create internal ResultEncoding from String representation
     *
     * @param resultEncoding
     *            String representation of ResultEncoding
     * @return Internal ResultEncoding
     */
    public SosResultEncoding createSosResultEncoding(final String resultEncoding) {
        final SosResultEncoding sosResultEncoding = new SosResultEncoding();
        sosResultEncoding.setXml(resultEncoding);
        return sosResultEncoding;
    }

    /**
     * Create internal ResultStructure from String representation
     *
     * @param resultStructure
     *            String representation of ResultStructure
     * @return Internal ResultStructure
     */
    public SosResultStructure createSosResultStructure(final String resultStructure) {
        final SosResultStructure sosResultStructure = new SosResultStructure();
        sosResultStructure.setXml(resultStructure);
        return sosResultStructure;
    }

    /**
     * Create result values from observation according to ResultEncoding and
     * ResultStructure
     *
     * @param observations
     *            Observation to create result values from
     * @param sosResultEncoding
     *            The ResultEncoding
     * @param sosResultStructure
     *            The ResultStructure
     * @return Result values String from observation according to ResultEncoding
     *         and ResultStructure
     * @throws OwsExceptionReport
     *             If creation fails
     */
    public String createResultValuesFromObservations(final Collection<Observation<?>> observations,
            final SosResultEncoding sosResultEncoding, final SosResultStructure sosResultStructure)
            throws OwsExceptionReport {
        final Map<Integer, String> valueOrder = getValueOrderMap(sosResultStructure.getResultStructure());
        return createResultValuesFromObservations(observations, sosResultEncoding, sosResultStructure, valueOrder, true);
        
    }
    
    private String createResultValuesFromObservations(final Collection<Observation<?>> observations,
            final SosResultEncoding sosResultEncoding, final SosResultStructure sosResultStructure, Map<Integer, String> valueOrder, boolean addCount)
            throws OwsExceptionReport {
        final StringBuilder builder = new StringBuilder();
        if (CollectionHelper.isNotEmpty(observations)) {
            final String tokenSeparator = getTokenSeparator(sosResultEncoding.getEncoding());
            final String blockSeparator = getBlockSeparator(sosResultEncoding.getEncoding());
            if (addCount) {
                addElementCount(builder, observations.size(), blockSeparator);
            }
            for (final Observation<?> observation : observations) {
                if (observation instanceof ProfileObservation) {
                    builder.append(createResultValuesFromObservations(((ProfileObservation) observation).getValue(), sosResultEncoding, sosResultStructure, valueOrder, false));
                } else {
                    for (final Integer intger : valueOrder.keySet()) {
                        final String definition = valueOrder.get(intger);
                        switch (definition) {
                            case PHENOMENON_TIME:
                                builder.append(getTimeStringForPhenomenonTime(observation.getPhenomenonTimeStart(),
                                                                              observation.getPhenomenonTimeEnd()));
                                break;
                            case RESULT_TIME:
                                builder.append(getTimeStringForResultTime(observation.getResultTime()));
                                break;
                            case OmConstants.PARAM_NAME_SAMPLING_GEOMETRY:
                                builder.append(getSamplingGeometry(observation, tokenSeparator, sosResultStructure.getResultStructure()));
                                break;
                            case OmConstants.OM_PARAMETER:
                            case OmConstants.PARAMETER:
                                builder.append(getParameters(observation, tokenSeparator, sosResultStructure.getResultStructure()));
                                break;
                            case OM_PROCEDURE:
                                if (observation.getProcedure() != null && observation.getProcedure().isSetIdentifier()) {
                                    builder.append(observation.getProcedure().getIdentifier());
                                } else {
                                    builder.append("");
                                }
                                break;
                            case OM_FEATURE_OF_INTEREST:
                                if (observation.getFeatureOfInterest() != null && observation.getFeatureOfInterest().isSetIdentifier()) {
                                    builder.append(observation.getFeatureOfInterest().getIdentifier());
                                } else {
                                    builder.append("");
                                }
                                break;
                            default:
                                builder.append(getValueAsStringForObservedProperty(observation, definition));
                                break;
                        }
                        builder.append(tokenSeparator);
                    }
                    builder.delete(builder.lastIndexOf(tokenSeparator), builder.length());
                }
                builder.append(blockSeparator);
            }
            if (builder.length() > 0) {
                builder.delete(builder.lastIndexOf(blockSeparator), builder.length());
            }
        }
        return builder.toString();
    }

    /**
     * Get token separator from encoding
     *
     * @param encoding
     *            Abstract encoding
     * @return Token separator
     */
    public String getTokenSeparator(final SweAbstractEncoding encoding) {
        if (encoding instanceof SweTextEncoding) {
            return ((SweTextEncoding) encoding).getTokenSeparator();
        }
        return null;
    }

    /**
     * Get block separator from encoding
     *
     * @param encoding
     *            Abstract encoding
     * @return Block separator
     */
    public String getBlockSeparator(final SweAbstractEncoding encoding) {
        if (encoding instanceof SweTextEncoding) {
            return ((SweTextEncoding) encoding).getBlockSeparator();
        }
        return null;
    }

    /**
     * Check if data component has a result time element and return the position
     *
     * @param sweDataElement
     *            Data component
     * @return Position of the result time element or -1 if it is not contained
     */
    public int hasResultTime(final SweAbstractDataComponent sweDataElement) {
        if (sweDataElement instanceof SweDataArray
                && ((SweDataArray) sweDataElement).getElementType() instanceof SweDataRecord) {
            final SweDataArray dataArray = (SweDataArray) sweDataElement;
            return checkFields(((SweDataRecord) dataArray.getElementType()).getFields(), RESULT_TIME);
        } else if (sweDataElement instanceof SweDataRecord) {
            final SweDataRecord dataRecord = (SweDataRecord) sweDataElement;
            return checkFields(dataRecord.getFields(), RESULT_TIME);
        }
        return -1;
    }

    /**
     * Check if data component has a phenomenon time element and return the
     * position
     *
     * @param sweDataElement
     *            Data component
     * @return Position of the phenomenon time element or -1 if it is not
     *         contained
     */
    public int hasPhenomenonTime(final SweAbstractDataComponent sweDataElement) {
        if (sweDataElement instanceof SweDataArray
                && ((SweDataArray) sweDataElement).getElementType() instanceof SweDataRecord) {
            final SweDataArray dataArray = (SweDataArray) sweDataElement;
            return checkFields(((SweDataRecord) dataArray.getElementType()).getFields(), PHENOMENON_TIME);
        } else if (sweDataElement instanceof SweDataRecord) {
            final SweDataRecord dataRecord = (SweDataRecord) sweDataElement;
            return checkFields(dataRecord.getFields(), PHENOMENON_TIME);
        }
        return -1;
    }

    /**
     * Check fields for definition and return the position
     *
     * @param fields
     *            Fields list to check
     * @param definition
     *            Definition to check
     * @return Position of the element with the definition or -1 if it is not
     *         contained
     */
    public int checkFields(final List<SweField> fields, final String definition) {
        int i = 0;
        for (final SweField f : fields) {
            final SweAbstractDataComponent element = f.getElement();
            if (element.isSetDefinition() && element.getDefinition().equals(definition)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    private void addElementCount(final StringBuilder builder, final int size, final String blockSeparator) {
        builder.append(String.valueOf(size));
        builder.append(blockSeparator);
    }

    private Object getTimeStringForResultTime(final Date resultTime) {
        if (resultTime != null) {
            return DateTimeHelper.formatDateTime2IsoString(new DateTime(resultTime, DateTimeZone.UTC));
        }
        return Configurator.getInstance().getProfileHandler().getActiveProfile().getResponseNoDataPlaceholder();
    }

    private Object getTimeStringForPhenomenonTime(final Date phenomenonTimeStart, final Date phenomenonTimeEnd) {
        if (phenomenonTimeStart == null) {
            return Configurator.getInstance().getProfileHandler().getActiveProfile().getResponseNoDataPlaceholder();
        }

        final StringBuilder builder = new StringBuilder();
        if (phenomenonTimeEnd == null || phenomenonTimeStart.equals(phenomenonTimeEnd)) {
            builder.append(formatDateTime2IsoString(new DateTime(phenomenonTimeStart, DateTimeZone.UTC)));
        } else {
            builder.append(formatDateTime2IsoString(new DateTime(phenomenonTimeStart, DateTimeZone.UTC)));
            builder.append('/');
            builder.append(formatDateTime2IsoString(new DateTime(phenomenonTimeEnd, DateTimeZone.UTC)));
        }
        return builder.toString();
    }

    private Map<Integer, String> getValueOrderMap(final SweAbstractDataComponent sweDataElement) {
        final Map<Integer, String> valueOrder = new HashMap<>(0);
        if (sweDataElement instanceof SweDataArray
                && ((SweDataArray) sweDataElement).getElementType() instanceof SweDataRecord) {
            final SweDataArray dataArray = (SweDataArray) sweDataElement;
            addOrderAndDefinitionToMap(((SweDataRecord) dataArray.getElementType()).getFields(), valueOrder, new IncDecInteger());
        } else if (sweDataElement instanceof SweDataRecord) {
            final SweDataRecord dataRecord = (SweDataRecord) sweDataElement;
            addOrderAndDefinitionToMap(dataRecord.getFields(), valueOrder, new IncDecInteger());
        }
        return new TreeMap<>(valueOrder);
    }

    private void addOrderAndDefinitionToMap(final List<SweField> fields, final Map<Integer, String> valueOrder, IncDecInteger tokenIndex) {
        for (SweField sweField : fields) {
            final SweAbstractDataComponent element = sweField.getElement();
            if (element instanceof SweAbstractSimpleType) {
                final SweAbstractSimpleType<?> simpleType = (SweAbstractSimpleType<?>) element;
                if (simpleType.isSetDefinition()) {
                    addValueToValueOrderMap(valueOrder, tokenIndex, simpleType.getDefinition());
                }
                tokenIndex.increment();
            } else if (element instanceof SweDataRecord) {
                if (element.isSetDefinition() && element.getDefinition().contains(OmConstants.PARAMETER)) {
                    addValueToValueOrderMap(valueOrder, tokenIndex, element.getDefinition());
                    tokenIndex.increment();
                } else {
                    addOrderAndDefinitionToMap(((SweDataRecord) element).getFields(), valueOrder, tokenIndex);
                }
            } else if (element instanceof SweVector) {
                if (element.isSetDefinition()) {
                    addValueToValueOrderMap(valueOrder, tokenIndex, element.getDefinition());
                    tokenIndex.increment();
                }
//                addOrderAndVectorDefinitionToMap(((SweVector) element).getCoordinates(), valueOrder, tokenIndex);
            }
        }
    }

    private void addOrderAndVectorDefinitionToMap(List<SweCoordinate<?>> coordinates, Map<Integer, String> valueOrder, IncDecInteger tokenIndex) {
        for (SweCoordinate<?> sweCoordinate : coordinates) {
            final SweAbstractDataComponent element = sweCoordinate.getValue();
            if (element instanceof SweAbstractSimpleType) {
                final SweAbstractSimpleType<?> simpleType = (SweAbstractSimpleType<?>) element;
                if (simpleType.isSetDefinition()) {
                    addValueToValueOrderMap(valueOrder, tokenIndex, simpleType.getDefinition());
                }
                tokenIndex.increment();
            } else if (element instanceof SweDataRecord) {
                addOrderAndDefinitionToMap(((SweDataRecord) element).getFields(), valueOrder, tokenIndex);
            } else if (element instanceof SweVector) {
                addOrderAndVectorDefinitionToMap(((SweVector) element).getCoordinates(), valueOrder, tokenIndex);
            }
        }
    }

    private void addValueToValueOrderMap(final Map<Integer, String> valueOrder, final IncDecInteger index,
            final String value) {
        if (index.get() >= 0) {
            valueOrder.put(index.get(), value);
        }
    }

    private String getValueAsStringForObservedProperty(final Observation<?> observation,
            final String definition) {
        final String observedProperty = observation.getObservableProperty().getIdentifier();
        if (observation instanceof ComplexObservation) {
            for (Observation<?> contentObservation : ((ComplexObservation)observation).getValue()) {
                String value = getValueAsStringForObservedProperty(contentObservation, definition);
                if (!Strings.isNullOrEmpty(value)) {
                    return value;
                }
            }
        } else if (observedProperty.equals(definition)) {
            if (observation instanceof NumericObservation) {
                return String.valueOf(((NumericObservation) observation).getValue());
            } else if (observation instanceof BooleanObservation) {
                return String.valueOf(((BooleanObservation) observation).getValue());
            } else if (observation instanceof CategoryObservation) {
                return String.valueOf(((CategoryObservation) observation).getValue());
            } else if (observation instanceof CountObservation) {
                return String.valueOf(((CountObservation) observation).getValue());
            } else if (observation instanceof TextObservation) {
                return String.valueOf(((TextObservation) observation).getValue());
            } else if (observation instanceof GeometryObservation) {
                final WKTWriter writer = new WKTWriter();
                return writer.write(((GeometryObservation) observation).getValue());
            } else if (observation instanceof BlobObservation) {
                return String.valueOf(((BlobObservation) observation).getValue());
            } 
        }
        return "";
    }
    
    private String getSamplingGeometry(Observation<?> observation, String tokenSeparator, SweAbstractDataComponent sweAbstractDataComponent) throws OwsExceptionReport {
        SweVector vector = getVector(sweAbstractDataComponent);
        if (vector != null && vector.isSetCoordinates()) {
            final Map<Integer, String> valueOrder = new HashMap<>(0);
            addOrderAndVectorDefinitionToMap(vector.getCoordinates(), valueOrder, new IncDecInteger());
            final StringBuilder builder = new StringBuilder();
            Geometry samplingGeometry = null;
            if (observation.hasSamplingGeometry()) {
                samplingGeometry = GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(observation.getSamplingGeometry());
            }
            for (final Integer intger : valueOrder.keySet()) {
                final String definition = valueOrder.get(intger);
                if (samplingGeometry != null && samplingGeometry instanceof Point) {
                    Coordinate coordinate = samplingGeometry.getCoordinate();
                    if (helper.checkAltitudeNameDefinition(definition) && checkCoordinate(coordinate.z)) {
                        builder.append(coordinate.z);
                    } else if (helper.checkNorthingNameDefinition(definition)) {
                        if (getGeomtryHandler().isNorthingFirstEpsgCode(samplingGeometry.getSRID())) {
                            builder.append(coordinate.x);
                        } else {
                            builder.append(coordinate.y);
                        }
                    } else if (helper.checkEastingNameDefinition(definition)) { 
                        if (getGeomtryHandler().isNorthingFirstEpsgCode(samplingGeometry.getSRID())) {
                            builder.append(coordinate.y);
                        } else {
                            builder.append(coordinate.x);
                        }
                    } else {
                        builder.append(Configurator.getInstance().getProfileHandler().getActiveProfile().getResponseNoDataPlaceholder());
                    }
                } else {
                    builder.append(Configurator.getInstance().getProfileHandler().getActiveProfile().getResponseNoDataPlaceholder());
                }
                builder.append(tokenSeparator);
            }
            return builder.delete(builder.lastIndexOf(tokenSeparator), builder.length()).toString();
        }
        return Configurator.getInstance().getProfileHandler().getActiveProfile().getResponseNoDataPlaceholder();
    }
    
    private boolean checkCoordinate(Double value) {
        return value != null;
    }

    private SweVector getVector(SweAbstractDataComponent sweAbstractDataComponent) throws CodedException {
        if (sweAbstractDataComponent instanceof SweDataArray
                && ((SweDataArray) sweAbstractDataComponent).getElementType() instanceof SweDataRecord) {
            final SweDataArray dataArray = (SweDataArray) sweAbstractDataComponent;
            return getVector(((SweDataRecord) dataArray.getElementType()).getFields());
        } else if (sweAbstractDataComponent instanceof SweDataRecord) {
            final SweDataRecord dataRecord = (SweDataRecord) sweAbstractDataComponent;
            return getVector(dataRecord.getFields());
        } else if (sweAbstractDataComponent instanceof SweVector) {
            return (SweVector)sweAbstractDataComponent;
        }
        return null;
    }

    private SweVector getVector(List<SweField> fields) throws CodedException {
        for (SweField sweField : fields) {
            if (isVector(sweField) && checkVectorForSamplingGeometry(sweField)) {
                return (SweVector)sweField.getElement();
            }
        }
        return null;
    }

    private String getParameters(Observation<?> observation, String tokenSeparator,
            SweAbstractDataComponent resultStructure) {
        SweDataRecord record = null;
        if (resultStructure instanceof SweDataArray
                && ((SweDataArray) resultStructure).getElementType() instanceof SweDataRecord) {
            final SweDataArray dataArray = (SweDataArray) resultStructure;
            record = (SweDataRecord) dataArray.getElementType();
        } else if (resultStructure instanceof SweDataRecord) {
            record = (SweDataRecord) resultStructure;
        }
        Map<Integer, String> valueOrder = getParameterDataRecord(record.getFields());
        StringBuilder builder = new StringBuilder();
        if (valueOrder != null) {
            for (Entry<Integer, String> order : valueOrder.entrySet()) {
                if (observation.hasParameters() && hasParameter(observation.getParameters(), order.getValue())) {
                    builder.append(getParameterValue(observation.getParameters(), order.getValue())).append(tokenSeparator);
                } else {
                    builder.append("").append(tokenSeparator);
                }
            }
            return builder.delete(builder.lastIndexOf(tokenSeparator), builder.length()).toString();
        }
        return "";
    }
    
    private Object getParameterValue(Set<Parameter> parameters, String value) {
        for (Parameter parameter : parameters) {
            if (parameter.getName().equals(value)) {
                return parameter.getValueAsString();
            }
        }
        return "";
    }

    private boolean hasParameter(Set<Parameter> parameters, String value) {
        for (Parameter parameter : parameters) {
            if (parameter.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private Map<Integer, String> getParameterDataRecord(List<SweField> list) {
        for (SweField sweField : list) {
            if (isDataRecord(sweField) && checkDefinition(sweField, OmConstants.PARAMETER)) {
                return getValueOrderMap(sweField.getElement());
            } else  if (isDataRecord(sweField) && checkDefinition(sweField, OmConstants.OM_PARAMETER)) {
                return getValueOrderMap(sweField.getElement());
            }
        }
        return null;
    }

    public boolean checkDataRecordForObservedProperty(SweField swefield, String observedProperty) throws CodedException {
        if (isDataRecord(swefield) && !checkDefinition(swefield, observedProperty) && !checkDataRecordForParameter(swefield)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage(
                            "The swe:DataRecord element is currently only supported for the definition of the observedProperty and om:parameter. "
                            + "The definition should be '%s' or '%s'!",
                            observedProperty, OmConstants.PARAMETER);
        }
        return true;
    }
    
    public boolean checkDataArrayForObservedProperty(SweField swefield, String observedProperty) throws CodedException {
        if (isDataArray(swefield) && !checkDefinition(swefield, observedProperty)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage(
                            "The swe:DataArray element is currently only supported for the definition of the phenomenonTime and observedProperty. "
                            + "The definition should be '%s' or '%s'!",
                            PHENOMENON_TIME, observedProperty);
        }
        return true;
    }
    
    public boolean checkForFeatureOfInterest(SweField swefield) throws CodedException {
        if (isText(swefield) && !checkDefinition(swefield, OM_FEATURE_OF_INTEREST)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
            .withMessage(
                    "The featureOfInterest is not defined in the observationTemplate and the swe:DataRecord does not contain a featureOfInterest definition with '%s'!",
                    OM_FEATURE_OF_INTEREST);
        }
        return true;
    }
    
    public boolean checkForProcedure(SweField swefield) throws CodedException {
        if (isText(swefield) && !checkDefinition(swefield, OM_PROCEDURE)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
            .withMessage(
                    "The procedure is not defined in the observationTemplate and the swe:DataRecord does not contain a procedure definition with '%s'!",
                    OM_PROCEDURE);
        }
        return true;
    }

    public boolean isText(SweField swefield) {
        return swefield != null && swefield.getElement() != null && swefield.getElement() instanceof SweText;
    }

    public boolean checkVectorForSamplingGeometry(SweField swefield) throws CodedException {
        if (isVector(swefield) && !checkDefinition(swefield, OmConstants.PARAM_NAME_SAMPLING_GEOMETRY)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage(
                            "The swe:Vector element is currently only supported for the definition of the samplingGeometry with definition '%s'!",
                            OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        }
        return true;
    }
    
    public boolean checkDefinition(SweField sweField, String definition) {
        if (sweField != null && sweField.getElement().isSetDefinition()) {
            return definition.equals(sweField.getElement().getDefinition());
        }
        return false;
    }
    
    public boolean isDataRecord(SweField sweField) {
        return sweField.getElement() instanceof SweDataRecord;
    }

    public boolean isDataArray(SweField sweField) {
        return sweField.getElement() instanceof SweDataArray;
    }

    public boolean isVector(SweField sweField) {
        return sweField.getElement() instanceof SweVector;
    }

    public boolean checkDataRecordForParameter(SweField swefield) throws CodedException {
        if (isDataRecord(swefield) && !checkDefinition(swefield, OmConstants.OM_PARAMETER)) {
            return false;
        }
        return true;
        
    }

    private GeometryHandler getGeomtryHandler() {
        return GeometryHandler.getInstance();
    }
}
