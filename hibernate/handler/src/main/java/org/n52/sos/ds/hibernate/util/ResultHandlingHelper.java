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
package org.n52.sos.ds.hibernate.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;
import org.n52.janmayen.NcName;
import org.n52.series.db.beans.BlobDataEntity;
import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.Describable;
import org.n52.series.db.beans.GeometryDataEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.series.db.beans.VerticalMetadataEntity;
import org.n52.series.db.beans.dataset.DatasetType;
import org.n52.series.db.beans.dataset.ObservationType;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.values.ProfileLevel;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweConstants.SweCoordinateNames;
import org.n52.shetland.ogc.swe.SweCoordinate;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.SweVector;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swe.simpleType.SweTime;
import org.n52.shetland.ogc.swe.simpleType.SweTimeRange;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.util.observation.ObservationValueCreator;
import org.n52.sos.ds.hibernate.util.observation.SweAbstractDataComponentCreator;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.IncDecInteger;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.util.SweHelper;

import com.google.common.base.Strings;

/**
 * @since 4.0.0
 *
 */
public class ResultHandlingHelper implements HibernateUnproxy {

    public static final String OM_PROCEDURE = "om:procedure";

    public static final String OM_FEATURE_OF_INTEREST = "om:featureOfInterest";

    public static final String PHENOMENON_TIME = "phenomenonTime";

    private final SweHelper helper;

    private GeometryHandler geometryHandler;

    private DecoderRepository decoderRepository;

    public ResultHandlingHelper(GeometryHandler geometryHandler, SweHelper sweHelper,
            DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
        this.geometryHandler = geometryHandler;
        this.helper = sweHelper;
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
     * @param session
     *            The Hibernate session
     * @return Result values String from observation according to ResultEncoding
     *         and ResultStructure
     * @throws OwsExceptionReport
     *             If creation fails
     */
    public String createResultValuesFromObservations(final Collection<DataEntity<?>> observations,
            final SosResultEncoding sosResultEncoding, final SosResultStructure sosResultStructure,
            String noDataPlaceholder, Session session) throws OwsExceptionReport {
        final Map<Integer, String> valueOrder = getValueOrderMap(sosResultStructure.get()
                .get());
        return createResultValuesFromObservations(observations, sosResultEncoding, sosResultStructure,
                noDataPlaceholder, valueOrder, true, null, session);
    }

    private String createResultValuesFromObservations(final Collection<DataEntity<?>> observations,
            final SosResultEncoding sosResultEncoding, final SosResultStructure sosResultStructure,
            String noDataPlaceholder, Map<Integer, String> valueOrder, boolean addCount,
            VerticalMetadataEntity vertical, Session session) throws OwsExceptionReport {
        final StringBuilder builder = new StringBuilder();
        if (CollectionHelper.isNotEmpty(observations)) {
            final String tokenSeparator = getTokenSeparator(sosResultEncoding.get()
                    .get());
            final String blockSeparator = getBlockSeparator(sosResultEncoding.get()
                    .get());
            if (addCount) {
                addElementCount(builder, observations.size(), blockSeparator);
            }
            for (final DataEntity<?> obs : observations) {
                DataEntity<?> observation = unproxy(obs, session);
                if (observation instanceof ProfileDataEntity) {
                    builder.append(createResultValuesFromObservations(((ProfileDataEntity) observation).getValue(),
                            sosResultEncoding, sosResultStructure, noDataPlaceholder, valueOrder, false,
                            ((ProfileDataEntity) observation).getDataset()
                                    .getVerticalMetadata(),
                            session));
                    builder.append(blockSeparator);
                } else {
                    for (final Entry<Integer, String> entry : valueOrder.entrySet()) {
                        final String definition = entry.getValue();
                        switch (definition) {
                            case OmConstants.PHENOMENON_TIME:
                                builder.append(getTimeStringForPhenomenonTime(observation.getSamplingTimeStart(),
                                        observation.getSamplingTimeEnd(), noDataPlaceholder));
                                break;
                            case OmConstants.RESULT_TIME:
                                builder.append(
                                        getTimeStringForResultTime(observation.getResultTime(), noDataPlaceholder));
                                break;
                            case OmConstants.PARAM_NAME_SAMPLING_GEOMETRY:
                                builder.append(
                                        getSamplingGeometry(observation, tokenSeparator, sosResultStructure.get()
                                                .get(), noDataPlaceholder));
                                break;
                            case OmConstants.OM_PARAMETER:
                            case OmConstants.PARAMETER:
                                builder.append(getParameters(observation, tokenSeparator, sosResultStructure.get()
                                        .get(), vertical));
                                break;
                            case OM_PROCEDURE:
                                if (observation.getDataset()
                                        .getProcedure() != null && observation.getDataset()
                                                .getProcedure()
                                                .isSetIdentifier()) {
                                    builder.append(observation.getDataset()
                                            .getProcedure()
                                            .getIdentifier());
                                } else {
                                    builder.append("");
                                }
                                break;
                            case OM_FEATURE_OF_INTEREST:
                                if (observation.getDataset()
                                        .getFeature() != null && observation.getDataset()
                                                .getFeature()
                                                .isSetIdentifier()) {
                                    builder.append(observation.getDataset()
                                            .getFeature()
                                            .getIdentifier());
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
                    builder.append(blockSeparator);
                }
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
            return checkFields(((SweDataRecord) dataArray.getElementType()).getFields(), OmConstants.RESULT_TIME);
        } else if (sweDataElement instanceof SweDataRecord) {
            final SweDataRecord dataRecord = (SweDataRecord) sweDataElement;
            return checkFields(dataRecord.getFields(), OmConstants.RESULT_TIME);
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
            return checkFields(((SweDataRecord) dataArray.getElementType()).getFields(), OmConstants.PHENOMENON_TIME);
        } else if (sweDataElement instanceof SweDataRecord) {
            final SweDataRecord dataRecord = (SweDataRecord) sweDataElement;
            return checkFields(dataRecord.getFields(), OmConstants.PHENOMENON_TIME);
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
            if (element.isSetDefinition() && element.getDefinition()
                    .equals(definition)) {
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

    private Object getTimeStringForResultTime(final Date resultTime, String noDataPlaceholder) {
        if (resultTime != null) {
            return DateTimeHelper.formatDateTime2IsoString(new DateTime(resultTime, DateTimeZone.UTC));
        }
        return noDataPlaceholder;
    }

    private Object getTimeStringForPhenomenonTime(final Date phenomenonTimeStart, final Date phenomenonTimeEnd,
            String noDataPlaceholder) {
        if (phenomenonTimeStart == null) {
            return noDataPlaceholder;
        }

        final StringBuilder builder = new StringBuilder();
        if (phenomenonTimeEnd == null || phenomenonTimeStart.equals(phenomenonTimeEnd)) {
            builder.append(
                    DateTimeHelper.formatDateTime2IsoString(new DateTime(phenomenonTimeStart, DateTimeZone.UTC)));
        } else {
            builder.append(
                    DateTimeHelper.formatDateTime2IsoString(new DateTime(phenomenonTimeStart, DateTimeZone.UTC)));
            builder.append('/');
            builder.append(DateTimeHelper.formatDateTime2IsoString(new DateTime(phenomenonTimeEnd, DateTimeZone.UTC)));
        }
        return builder.toString();
    }

    private Map<Integer, String> getValueOrderMap(final SweAbstractDataComponent sweDataElement) {
        final Map<Integer, String> valueOrder = new HashMap<>(0);
        if (sweDataElement instanceof SweDataArray
                && ((SweDataArray) sweDataElement).getElementType() instanceof SweDataRecord) {
            final SweDataArray dataArray = (SweDataArray) sweDataElement;
            addOrderAndDefinitionToMap(((SweDataRecord) dataArray.getElementType()).getFields(), valueOrder,
                    new IncDecInteger());
        } else if (sweDataElement instanceof SweDataRecord) {
            final SweDataRecord dataRecord = (SweDataRecord) sweDataElement;
            addOrderAndDefinitionToMap(dataRecord.getFields(), valueOrder, new IncDecInteger());
        }
        return new TreeMap<>(valueOrder);
    }

    private void addOrderAndDefinitionToMap(final List<SweField> fields, final Map<Integer, String> valueOrder,
            IncDecInteger tokenIndex) {
        for (SweField sweField : fields) {
            final SweAbstractDataComponent element = sweField.getElement();
            if (element instanceof SweAbstractSimpleType) {
                final SweAbstractSimpleType<?> simpleType = (SweAbstractSimpleType<?>) element;
                if (simpleType.isSetDefinition()) {
                    addValueToValueOrderMap(valueOrder, tokenIndex, simpleType.getDefinition());
                }
                tokenIndex.increment();
            } else if (element instanceof SweDataRecord) {
                if (element.isSetDefinition() && element.getDefinition()
                        .contains(OmConstants.PARAMETER)) {
                    addValueToValueOrderMap(valueOrder, tokenIndex, element.getDefinition());
                    tokenIndex.increment();
                } else {
                    addOrderAndDefinitionToMap(((SweDataRecord) element).getFields(), valueOrder, tokenIndex);
                }
            } else if (element instanceof SweVector || element instanceof SweDataArray) {
                if (element.isSetDefinition()) {
                    addValueToValueOrderMap(valueOrder, tokenIndex, element.getDefinition());
                    tokenIndex.increment();
                }
                // addOrderAndVectorDefinitionToMap(((SweVector)
                // element).getCoordinates(), valueOrder, tokenIndex);
            }
        }
    }

    private void addOrderAndVectorDefinitionToMap(Collection<? extends SweCoordinate<? extends Number>> list,
            Map<Integer, String> valueOrder, IncDecInteger tokenIndex) {
        for (SweCoordinate<?> sweCoordinate : list) {
            final SweAbstractSimpleType<?> element = sweCoordinate.getValue();
            if (element.isSetDefinition()) {
                addValueToValueOrderMap(valueOrder, tokenIndex, element.getDefinition());
            }
            tokenIndex.increment();
        }
    }

    private void addValueToValueOrderMap(final Map<Integer, String> valueOrder, final IncDecInteger index,
            final String value) {
        if (index.get() >= 0) {
            valueOrder.put(index.get(), value);
        }
    }

    private String getValueAsStringForObservedProperty(final DataEntity<?> observation, final String definition) {
        final String observedProperty = observation.getDataset()
                .getObservableProperty()
                .getIdentifier();
        if (observation instanceof ComplexDataEntity) {
            for (DataEntity<?> contentObservation : ((ComplexDataEntity) observation).getValue()) {
                String value = getValueAsStringForObservedProperty(contentObservation, definition);
                if (!Strings.isNullOrEmpty(value)) {
                    return value;
                }
            }
        } else if (observedProperty.equals(definition) && observation.hasValue()) {
            if (observation instanceof QuantityDataEntity) {
                return String.valueOf(((QuantityDataEntity) observation).getValue());
            } else if (observation instanceof BooleanDataEntity) {
                return String.valueOf(((BooleanDataEntity) observation).getValue());
            } else if (observation instanceof CategoryDataEntity) {
                return String.valueOf(((CategoryDataEntity) observation).getValue());
            } else if (observation instanceof CountDataEntity) {
                return String.valueOf(((CountDataEntity) observation).getValue());
            } else if (observation instanceof TextDataEntity) {
                return String.valueOf(((TextDataEntity) observation).getValue());
            } else if (observation instanceof GeometryDataEntity) {
                final WKTWriter writer = new WKTWriter();
                return writer.write(((GeometryDataEntity) observation).getValue()
                        .getGeometry());
            } else if (observation instanceof BlobDataEntity) {
                return String.valueOf(((BlobDataEntity) observation).getValue());
            } else if (observation instanceof DataArrayDataEntity) {
                DataArrayDataEntity o = (DataArrayDataEntity) observation;
                return o.isSetStringValue() ? o.getStringValue() : "";
            }
        }
        return "";
    }

    private String getSamplingGeometry(DataEntity<?> observation, String tokenSeparator,
            SweAbstractDataComponent sweAbstractDataComponent, String noDataPlaceholder) throws OwsExceptionReport {
        SweVector vector = getVector(sweAbstractDataComponent);
        if (vector != null && vector.isSetCoordinates()) {
            final Map<Integer, String> valueOrder = new HashMap<>(0);
            addOrderAndVectorDefinitionToMap(vector.getCoordinates(), valueOrder, new IncDecInteger());
            final StringBuilder builder = new StringBuilder();
            Geometry samplingGeometry = null;
            if (observation.isSetGeometryEntity()) {
                samplingGeometry = getGeomtryHandler()
                        .switchCoordinateAxisFromToDatasourceIfNeeded(observation.getGeometryEntity()
                                .getGeometry());
            }
            for (final Entry<Integer, String> entry : valueOrder.entrySet()) {
                final String definition = entry.getValue();
                if (samplingGeometry != null && samplingGeometry instanceof Point) {
                    Coordinate coordinate = samplingGeometry.getCoordinate();
                    if (helper.hasAltitudeName(definition) && checkCoordinate(coordinate.getZ())) {
                        builder.append(coordinate.getZ());
                    } else if (helper.hasNorthingName(definition)) {
                        if (getGeomtryHandler().isNorthingFirstEpsgCode(samplingGeometry.getSRID())) {
                            builder.append(coordinate.x);
                        } else {
                            builder.append(coordinate.y);
                        }
                    } else if (helper.hasEastingName(definition)) {
                        if (getGeomtryHandler().isNorthingFirstEpsgCode(samplingGeometry.getSRID())) {
                            builder.append(coordinate.y);
                        } else {
                            builder.append(coordinate.x);
                        }
                    } else {
                        builder.append(noDataPlaceholder);
                    }
                } else {
                    builder.append(noDataPlaceholder);
                }
                builder.append(tokenSeparator);
            }
            return builder.delete(builder.lastIndexOf(tokenSeparator), builder.length())
                    .toString();
        }
        return noDataPlaceholder;
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
            return (SweVector) sweAbstractDataComponent;
        }
        return null;
    }

    private SweVector getVector(List<SweField> fields) throws CodedException {
        for (SweField sweField : fields) {
            if (isVector(sweField) && checkVectorForSamplingGeometry(sweField)) {
                return (SweVector) sweField.getElement();
            }
        }
        return null;
    }

    private String getParameters(DataEntity<?> observation, String tokenSeparator,
            SweAbstractDataComponent resultStructure, VerticalMetadataEntity vertical) {
        SweDataRecord record = null;
        if (resultStructure instanceof SweDataArray
                && ((SweDataArray) resultStructure).getElementType() instanceof SweDataRecord) {
            final SweDataArray dataArray = (SweDataArray) resultStructure;
            record = (SweDataRecord) dataArray.getElementType();
        } else if (resultStructure instanceof SweDataRecord) {
            record = (SweDataRecord) resultStructure;
        } else {
            return "";
        }
        Map<Integer, String> valueOrder = getParameterDataRecord(record.getFields());
        StringBuilder builder = new StringBuilder();
        if (valueOrder != null) {
            for (Entry<Integer, String> order : valueOrder.entrySet()) {
                if (observation.hasParameters() && hasParameter(observation.getParameters(), order.getValue())) {
                    builder.append(getParameterValue(observation.getParameters(), order.getValue()))
                            .append(tokenSeparator);
                } else if (vertical != null && (vertical.getVerticalFromName()
                        .equals(order.getValue())
                        || vertical.getVerticalToName()
                                .equals(order.getValue()))) {
                    if (vertical.areVerticalNamesEqual()) {
                        builder.append(observation.getVerticalTo()
                                .toPlainString())
                                .append(tokenSeparator);
                    } else {
                        if (vertical.getVerticalFromName()
                                .equals(order.getValue())) {
                            builder.append(observation.getVerticalFrom()
                                    .toPlainString())
                                    .append(tokenSeparator);
                        } else if (vertical.getVerticalToName()
                                .equals(order.getValue())) {
                            builder.append(observation.getVerticalTo()
                                    .toPlainString())
                                    .append(tokenSeparator);
                        }
                    }
                } else {
                    builder.append("")
                            .append(tokenSeparator);
                }
            }
            return builder.delete(builder.lastIndexOf(tokenSeparator), builder.length())
                    .toString();
        }
        return "";
    }

    private String getParameterValue(Set<ParameterEntity<?>> set, String value) {
        for (ParameterEntity<?> parameter : set) {
            if (parameter.getName()
                    .equals(value)) {
                return parameter.getValueAsString();
            }
        }
        return "";
    }

    private boolean hasParameter(Set<ParameterEntity<?>> set, String value) {
        for (ParameterEntity<?> parameter : set) {
            if (parameter.getName()
                    .equals(value)) {
                return true;
            }
        }
        return false;
    }

    private Map<Integer, String> getParameterDataRecord(List<SweField> list) {
        for (SweField sweField : list) {
            if (isDataRecord(sweField) && checkDefinition(sweField, OmConstants.PARAMETER)) {
                return getValueOrderMap(sweField.getElement());
            } else if (isDataRecord(sweField) && checkDefinition(sweField, OmConstants.OM_PARAMETER)) {
                return getValueOrderMap(sweField.getElement());
            }
        }
        return null;
    }

    public boolean checkDataRecordForObservedProperty(SweField swefield, String observedProperty)
            throws CodedException {
        if (isDataRecord(swefield) && !checkDefinition(swefield, observedProperty)
                && !checkDataRecordForParameter(swefield)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage(
                            "The swe:DataRecord element is currently only supported for "
                                    + "the definition of the observedProperty and om:parameter. "
                                    + "The definition should be '%s' or '%s'!",
                            observedProperty, OmConstants.PARAMETER);
        }
        return true;
    }

    public boolean checkDataArrayForObservedProperty(SweField swefield, String observedProperty)
            throws CodedException {
        if (isDataArray(swefield) && !checkDefinition(swefield, observedProperty)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage(
                            "The swe:DataArray element is currently only supported for the definition of the "
                                    + "phenomenonTime and observedProperty. The definition should be '%s' or '%s'!",
                            OmConstants.PHENOMENON_TIME, observedProperty);
        }
        return true;
    }

    public boolean checkForFeatureOfInterest(SweField swefield) throws CodedException {
        if (isText(swefield) && !checkDefinition(swefield, OM_FEATURE_OF_INTEREST)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage(
                            "The featureOfInterest is not defined in the observationTemplate and the swe:DataRecord "
                                    + "does not contain a featureOfInterest definition with '%s'!",
                            OM_FEATURE_OF_INTEREST);
        }
        return true;
    }

    public boolean checkForProcedure(SweField swefield) throws CodedException {
        if (isText(swefield) && !checkDefinition(swefield, OM_PROCEDURE)) {
            throw new NoApplicableCodeException().at(Sos2Constants.InsertResultTemplateParams.resultStructure)
                    .withMessage("The procedure is not defined in the observationTemplate and the swe:DataRecord"
                            + " does not contain a procedure definition with '%s'!", OM_PROCEDURE);
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
                            "The swe:Vector element is currently only supported for the definition "
                                    + "of the samplingGeometry with definition '%s'!",
                            OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        }
        return true;
    }

    public boolean checkDefinition(SweField sweField, String definition) {
        if (sweField != null && sweField.getElement()
                .isSetDefinition()) {
            return definition.equals(sweField.getElement()
                    .getDefinition());
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
        return geometryHandler;
    }

    public SweDataRecord createDataRecordForResultTemplate(DataEntity<?> observation, boolean procedure,
            boolean feature) throws OwsExceptionReport {
        SweDataRecord record = new SweDataRecord();
        if (observation.isSamplingTimePeriod()) {
            record.addField(createPhenomenonTimeRange());
        } else {
            record.addField(createPhenomenonTimeField());
        }
        record.addField(createResultTimeField());
        createObservedPropertyField(observation).stream()
                .forEach(f -> record.addField(f));
        if (procedure) {
            record.addField(createProcedureField());
        }
        if (feature) {
            record.addField(createFeatureOfInterestField());
        }
        if (observation.isSetGeometryEntity()) {
            record.addField(createSamplingGeometryField());
        }
        return record;
    }

    public static SweField createPhenomenonTimeRange() {
        return new SweField(PHENOMENON_TIME, new SweTimeRange().setUom(OmConstants.PHEN_UOM_ISO8601)
                .setDefinition(OmConstants.PHENOMENON_TIME));
    }

    public static SweField createPhenomenonTimeField() {
        return new SweField(PHENOMENON_TIME, new SweTime().setUom(OmConstants.PHEN_UOM_ISO8601)
                .setDefinition(OmConstants.PHENOMENON_TIME));
    }

    public static SweField createResultTimeField() {
        return new SweField("resultTime", new SweTime().setUom(OmConstants.PHEN_UOM_ISO8601)
                .setDefinition(OmConstants.RESULT_TIME));
    }

    public static SweField createProcedureField() {
        return new SweField("procedure", new SweText().setDefinition(OM_PROCEDURE));
    }

    public static SweField createFeatureOfInterestField() {
        return new SweField("featureOfInterest", new SweText().setDefinition(OM_FEATURE_OF_INTEREST));
    }

    public static SweField createSamplingGeometryField() {
        return new SweField("samplingGeometry", createSamplingGeometryVector());
    }

    private List<SweField> createObservedPropertyField(DataEntity<?> observation) throws OwsExceptionReport {
        List<SweField> fields = new LinkedList<>();
        PhenomenonEntity phenomenon = observation.getDataset()
                .getPhenomenon();
        if (observation.getDataset()
                .getDatasetType()
                .equals(DatasetType.profile)
                || observation.getDataset()
                        .getObservationType()
                        .equals(ObservationType.profile)) {
            ProfileValue profile = (ProfileValue) new ObservationValueCreator(decoderRepository).visit(observation);
            ProfileLevel level = profile.getValue()
                    .get(0);
            fields.add(createVerticalParameter(level));
            if (level.getValue()
                    .get(0) instanceof SweAbstractDataComponent) {
                if (level.getValue()
                        .size() > 1) {
                    SweDataRecord record = new SweDataRecord();
                    for (Value<?> v : level.getValue()) {
                        SweAbstractDataComponent dc = (SweAbstractDataComponent) v;
                        record.addField(new SweField(NcName.makeValid(dc.getDefinition()), dc));
                    }
                    fields.add(new SweField(getNcNameName(phenomenon), record));
                    return fields;
                } else {
                    SweAbstractDataComponent swe = (SweAbstractDataComponent) level.getValue()
                            .get(0)
                            .setValue(null);
                    swe.setDefinition(phenomenon.getIdentifier());
                    fields.add(new SweField(getNcNameName(phenomenon), swe));
                    return fields;
                }
            }
        } else {
            SweAbstractDataComponent value =
                    new SweAbstractDataComponentCreator(decoderRepository, true).visit(observation);
            fields.add(new SweField(getNcNameName(phenomenon), (SweAbstractDataComponent) value));
            return fields;
        }
        throw new NoApplicableCodeException();
    }

    private SweField createVerticalParameter(ProfileLevel level) {
        SweDataRecord record = new SweDataRecord();
        record.setDefinition(OmConstants.OM_PARAMETER);
        if (level.isSetLevelStart()) {
            record.addField(new SweField(level.getLevelStart()
                    .getName(),
                    level.getLevelStart()
                            .setValue((BigDecimal) null)));
        }
        if (level.isSetLevelEnd()) {
            record.addField(new SweField(level.getLevelEnd()
                    .getName(),
                    level.getLevelEnd()
                            .setValue((BigDecimal) null)));
        }
        return new SweField(OmConstants.OM_PARAMETER, record);
    }

    public static SweVector createSamplingGeometryVector(String referenceFrame) {
        SweVector vector = new SweVector();
        vector.setDefinition(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        vector.setReferenceFrame(referenceFrame);
        List<SweCoordinate<BigDecimal>> coordinates = new LinkedList<>();
        coordinates.add(new SweCoordinate<>(SweCoordinateNames.LATITUDE,
                createSweQuantityLatLon(SweCoordinateNames.LATITUDE, "lat")));
        coordinates.add(new SweCoordinate<>(SweCoordinateNames.LONGITUDE,
                createSweQuantityLatLon(SweCoordinateNames.LONGITUDE, "lon")));
        vector.setCoordinates(coordinates);
        return vector;
    }

    public static SweVector createSamplingGeometryVector() {
        return createSamplingGeometryVector(OGCConstants.URL_DEF_CRS_EPSG + 4326);
    }

    private static SweQuantity createSweQuantityLatLon(String definition, String axis) {
        return createSweQuantity(definition, axis, "deg");
    }

    private static SweQuantity createSweQuantity(String definition, String axis, String unit) {
        return (SweQuantity) new SweQuantity().setAxisID(axis)
                .setUom(unit)
                .setDefinition(definition);
    }

    private String getNcNameName(Describable entity) {
        return NcName.makeValid(entity.isSetName() ? entity.getName() : entity.getIdentifier());
    }

}
