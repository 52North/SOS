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
package org.n52.sos.ds.hibernate.util;

import static org.n52.sos.util.DateTimeHelper.formatDateTime2IsoString;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.BlobObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.CountObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.NumericObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.TextObservation;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;

import com.vividsolutions.jts.io.WKTWriter;

/**
 * @since 4.0.0
 * 
 */
public class ResultHandlingHelper {

    private static final String RESULT_TIME = OmConstants.RESULT_TIME;

    private static final String PHENOMENON_TIME = OmConstants.PHENOMENON_TIME;

    /**
     * Create internal ResultEncoding from String representation
     * 
     * @param resultEncoding
     *            String representation of ResultEncoding
     * @return Internal ResultEncoding
     */
    public static SosResultEncoding createSosResultEncoding(final String resultEncoding) {
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
    public static SosResultStructure createSosResultStructure(final String resultStructure) {
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
    public static String createResultValuesFromObservations(final List<AbstractObservation> observations,
            final SosResultEncoding sosResultEncoding, final SosResultStructure sosResultStructure)
            throws OwsExceptionReport {
        final StringBuilder builder = new StringBuilder();
        if (CollectionHelper.isNotEmpty(observations)) {
            final String tokenSeparator = getTokenSeparator(sosResultEncoding.getEncoding());
            final String blockSeparator = getBlockSeparator(sosResultEncoding.getEncoding());
            final Map<Integer, String> valueOrder = getValueOrderMap(sosResultStructure.getResultStructure());
            addElementCount(builder, observations.size(), blockSeparator);
            for (final AbstractObservation observation : observations) {
                for (final Integer intger : valueOrder.keySet()) {
                    final String definition = valueOrder.get(intger);
                    if (definition.equals(PHENOMENON_TIME)) {
                        builder.append(getTimeStringForPhenomenonTime(observation.getPhenomenonTimeStart(),
                                observation.getPhenomenonTimeEnd()));
                    } else if (definition.equals(RESULT_TIME)) {
                        builder.append(getTimeStringForResultTime(observation.getResultTime()));
                    } else {
                        builder.append(getValueAsStringForObservedProperty(observation, definition));
                    }
                    builder.append(tokenSeparator);
                }
                builder.delete(builder.lastIndexOf(tokenSeparator), builder.length());
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
    public static String getTokenSeparator(final SweAbstractEncoding encoding) {
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
    public static String getBlockSeparator(final SweAbstractEncoding encoding) {
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
    public static int hasResultTime(final SweAbstractDataComponent sweDataElement) {
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
    public static int hasPhenomenonTime(final SweAbstractDataComponent sweDataElement) {
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
    public static int checkFields(final List<SweField> fields, final String definition) {
        int i = 0;
        for (final SweField f : fields) {
            final SweAbstractDataComponent element = f.getElement();
            if (element instanceof SweAbstractSimpleType) {
                final SweAbstractSimpleType<?> simpleType = (SweAbstractSimpleType<?>) element;
                if (simpleType.isSetDefinition() && simpleType.getDefinition().equals(definition)) {
                    return i;
                }
            }
            ++i;
        }
        return -1;
    }

    private static void addElementCount(final StringBuilder builder, final int size, final String blockSeparator) {
        builder.append(String.valueOf(size));
        builder.append(blockSeparator);
    }

    private static Object getTimeStringForResultTime(final Date resultTime) {
        if (resultTime != null) {
            return DateTimeHelper.formatDateTime2IsoString(new DateTime(resultTime, DateTimeZone.UTC));
        }
        return Configurator.getInstance().getProfileHandler().getActiveProfile().getResponseNoDataPlaceholder();
    }

    private static Object getTimeStringForPhenomenonTime(final Date phenomenonTimeStart, final Date phenomenonTimeEnd) {
        if (phenomenonTimeStart == null && phenomenonTimeEnd == null) {
            return Configurator.getInstance().getProfileHandler().getActiveProfile().getResponseNoDataPlaceholder();
        }

        final StringBuilder builder = new StringBuilder();
        if (phenomenonTimeStart.equals(phenomenonTimeEnd)) {
            builder.append(formatDateTime2IsoString(new DateTime(phenomenonTimeStart, DateTimeZone.UTC)));
        } else {
            builder.append(formatDateTime2IsoString(new DateTime(phenomenonTimeStart, DateTimeZone.UTC)));
            builder.append('/');
            builder.append(formatDateTime2IsoString(new DateTime(phenomenonTimeEnd, DateTimeZone.UTC)));
        }
        return builder.toString();
    }

    private static Map<Integer, String> getValueOrderMap(final SweAbstractDataComponent sweDataElement) {
        final Map<Integer, String> valueOrder = new HashMap<Integer, String>(0);
        if (sweDataElement instanceof SweDataArray
                && ((SweDataArray) sweDataElement).getElementType() instanceof SweDataRecord) {
            final SweDataArray dataArray = (SweDataArray) sweDataElement;
            addOrderAndDefinitionToMap(((SweDataRecord) dataArray.getElementType()).getFields(), valueOrder);
        } else if (sweDataElement instanceof SweDataRecord) {
            final SweDataRecord dataRecord = (SweDataRecord) sweDataElement;
            addOrderAndDefinitionToMap(dataRecord.getFields(), valueOrder);
        }
        return new TreeMap<Integer, String>(valueOrder);
    }

    private static void addOrderAndDefinitionToMap(final List<SweField> fields, final Map<Integer, String> valueOrder) {
        for (int i = 0; i < fields.size(); i++) {
            final SweAbstractDataComponent element = fields.get(i).getElement();
            if (element instanceof SweAbstractSimpleType) {
                final SweAbstractSimpleType<?> simpleType = (SweAbstractSimpleType<?>) element;
                if (simpleType.isSetDefinition()) {
                    addValueToValueOrderMap(valueOrder, i, simpleType.getDefinition());
                }
            }
        }
    }

    private static void addValueToValueOrderMap(final Map<Integer, String> valueOrder, final int index,
            final String value) {
        if (index >= 0) {
            valueOrder.put(index, value);
        }
    }

    private static String getValueAsStringForObservedProperty(final AbstractObservation observation,
            final String definition) {
        final String observedProperty = observation.getObservableProperty().getIdentifier();

        if (observedProperty.equals(definition)) {
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
            // // TODO multiple values?
            // Set<BooleanValue> booleanValues = observation.getBooleanValue();
            // if (booleanValues != null && !booleanValues.isEmpty()) {
            // return
            // String.valueOf(booleanValues.iterator().next().getValue());
            // }
            //
            // Set<CategoryValue> categoryValues =
            // observation.getCategoryValue();
            // if (categoryValues != null && !categoryValues.isEmpty()) {
            // return categoryValues.iterator().next().getValue();
            // }
            //
            // Set<CountValue> countValues = observation.getCountValue();
            // if (countValues != null && !countValues.isEmpty()) {
            // return String.valueOf(countValues.iterator().next().getValue());
            // }
            //
            // Set<NumericValue> numericValues = observation.getNumericValues();
            // if (numericValues != null && !numericValues.isEmpty()) {
            // return
            // String.valueOf(numericValues.iterator().next().getValue());
            // }
            //
            // //TODO geometry values;
            //
            // Set<TextValue> textValues = observation.getTextValues();
            // if (textValues != null && !textValues.isEmpty()) {
            // StringBuilder builder = new StringBuilder();
            // for (TextValue textValue : textValues) {
            // builder.append(textValue.getValue());
            // }
            // return builder.toString();
            // }
        }
        return Configurator.getInstance().getProfileHandler().getActiveProfile().getResponseNoDataPlaceholder();
    }

    private ResultHandlingHelper() {
    }

}
