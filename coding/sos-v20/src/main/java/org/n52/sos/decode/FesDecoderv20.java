/*
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
package org.n52.sos.decode;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.opengis.fes.x20.BBOXType;
import net.opengis.fes.x20.BinaryComparisonOpType;
import net.opengis.fes.x20.BinaryLogicOpType;
import net.opengis.fes.x20.BinaryTemporalOpType;
import net.opengis.fes.x20.ComparisonOpsType;
import net.opengis.fes.x20.FilterDocument;
import net.opengis.fes.x20.FilterType;
import net.opengis.fes.x20.LiteralType;
import net.opengis.fes.x20.LogicOpsType;
import net.opengis.fes.x20.PropertyIsBetweenType;
import net.opengis.fes.x20.PropertyIsLikeType;
import net.opengis.fes.x20.PropertyIsNilType;
import net.opengis.fes.x20.PropertyIsNullType;
import net.opengis.fes.x20.SpatialOpsType;
import net.opengis.fes.x20.TemporalOpsDocument;
import net.opengis.fes.x20.TemporalOpsType;
import net.opengis.fes.x20.UnaryLogicOpType;
import net.opengis.fes.x20.ValueReferenceDocument;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlObject.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.filter.BinaryLogicFilter;
import org.n52.shetland.ogc.filter.ComparisonFilter;
import org.n52.shetland.ogc.filter.Filter;
import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.filter.FilterConstants.BinaryLogicOperator;
import org.n52.shetland.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator2;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.filter.UnaryLogicFilter;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderXmlInputException;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.xml.AbstractXmlDecoder;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @since 4.0.0
 *
 */
public class FesDecoderv20 extends AbstractXmlDecoder<XmlObject, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FesDecoderv20.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(FilterConstants.NS_FES_2,
            SpatialOpsType.class, TemporalOpsType.class, ComparisonOpsType.class, LogicOpsType.class,
            FilterType.class, FilterDocument.class, TemporalOpsDocument.class);

    public FesDecoderv20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Object decode(XmlObject xmlObject) throws DecodingException {
        if (xmlObject instanceof SpatialOpsType) {
            return parseSpatialFilterType((SpatialOpsType) xmlObject);
        } else if (xmlObject instanceof TemporalOpsType) {
            return parseTemporalFilterType((TemporalOpsType) xmlObject);
        } else if (xmlObject instanceof TemporalOpsDocument) {
            return parseTemporalFilterType(((TemporalOpsDocument) xmlObject).getTemporalOps());
        } else if (xmlObject instanceof ComparisonOpsType) {
            return parseComparisonFilterType((ComparisonOpsType) xmlObject);
        } else if (xmlObject instanceof LogicOpsType) {
            return parseLogicFilterType((LogicOpsType) xmlObject);
        } else if (xmlObject instanceof FilterType) {
            return parseFilterType((FilterType) xmlObject);
        } else if (xmlObject instanceof FilterDocument) {
            return parseFilterType(((FilterDocument) xmlObject).getFilter());
        } else {
            throw new UnsupportedDecoderXmlInputException(this, xmlObject);
        }
    }

    /**
     * Parse XML FilterType element
     *
     * @param filterType
     *            XML element to parse
     * @return SOS Filter object
     * @throws DecodingException
     *             If an error occurs or the filter type is not supported!
     */
    private Filter<?> parseFilterType(FilterType filterType) throws DecodingException {
        if (filterType.isSetComparisonOps()) {
            return parseComparisonFilterType(filterType.getComparisonOps());
        } else if (filterType.isSetSpatialOps()) {
            return parseSpatialFilterType(filterType.getSpatialOps());
        } else if (filterType.isSetTemporalOps()) {
            return parseTemporalFilterType(filterType.getTemporalOps());
        } else if (filterType.isSetLogicOps()) {
            return parseLogicFilterType(filterType.getLogicOps());
        } else if (filterType.isSetFunction()) {
            throw new UnsupportedDecoderXmlInputException(this, filterType);
        } else if (filterType.getIdArray() != null) {
            throw new UnsupportedDecoderXmlInputException(this, filterType);
        } else {
            throw new UnsupportedDecoderXmlInputException(this, filterType);
        }
    }

    /**
     * Parses the spatial filter of a request.
     *
     * @param xbSpatialOpsType
     *            XmlBean representing the feature of interest parameter of the
     *            request
     * @return Returns SpatialFilter created from the passed foi request
     *         parameter
     *
     *
     * @throws DecodingException
     *             * if creation of the SpatialFilter failed
     */
    private SpatialFilter parseSpatialFilterType(SpatialOpsType xbSpatialOpsType) throws DecodingException {
        SpatialFilter spatialFilter = new SpatialFilter();
        try {
            if (xbSpatialOpsType instanceof BBOXType) {
                spatialFilter.setOperator(FilterConstants.SpatialOperator.BBOX);
                BBOXType xbBBOX = (BBOXType) xbSpatialOpsType;
                if (isValueReferenceExpression(xbBBOX.getExpression())) {
                    spatialFilter.setValueReference(parseValueReference(xbBBOX.getExpression()));
                }
                XmlCursor geometryCursor = xbSpatialOpsType.newCursor();
                if (geometryCursor.toChild(GmlConstants.QN_ENVELOPE_32)) {
                    Object sosGeometry = decodeXmlObject(Factory.parse(geometryCursor.getDomNode()));
                    if (sosGeometry instanceof Geometry) {
                        spatialFilter.setGeometry((Geometry) sosGeometry);
                    } else {
                        throw new UnsupportedDecoderXmlInputException(this, xbSpatialOpsType);
                    }

                } else {
                    throw new DecodingException(Sos2Constants.GetObservationParams.spatialFilter,
                            "The requested spatial filter operand is not supported by this SOS!");
                }
                geometryCursor.dispose();
            } else {
                throw new DecodingException(Sos2Constants.GetObservationParams.spatialFilter,
                        "The requested spatial filter is not supported by this SOS!");
            }
        } catch (XmlException xmle) {
            throw new DecodingException("Error while parsing spatial filter!", xmle);
        }
        return spatialFilter;
    }

    /**
     * parses a single temporal filter of the requests and returns SOS temporal
     * filter
     *
     * @param xbTemporalOpsType
     *            XmlObject representing the temporal filter
     * @return Returns SOS representation of temporal filter
     *
     *
     * @throws DecodingException
     *             * if parsing of the element failed
     */
    private TemporalFilter parseTemporalFilterType(TemporalOpsType xbTemporalOpsType) throws DecodingException {
        TemporalFilter temporalFilter = new TemporalFilter();
        try {
            if (xbTemporalOpsType instanceof BinaryTemporalOpType) {
                BinaryTemporalOpType btot = (BinaryTemporalOpType) xbTemporalOpsType;
                if (btot.getValueReference() != null && !btot.getValueReference().isEmpty()) {
                    temporalFilter.setValueReference(btot.getValueReference().trim());
                }
                NodeList nodes = btot.getDomNode().getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    if (nodes.item(i).getNamespaceURI() != null
                            && !nodes.item(i).getLocalName().equals(FilterConstants.EN_VALUE_REFERENCE)) {
                        Object timeObject = decodeXmlObject(Factory.parse(nodes.item(i)));
                        if (timeObject instanceof Time) {
                            TimeOperator operator;
                            Time time = (Time) timeObject;
                            String localName = XmlHelper.getLocalName(xbTemporalOpsType);
                            if (localName.equals(TimeOperator2.After.name())) {
                                operator = TimeOperator.TM_After;
                            } else if (localName.equals(TimeOperator2.Before.name())) {
                                operator = TimeOperator.TM_Before;
                            } else if (localName.equals(TimeOperator2.Begins.name())) {
                                operator = TimeOperator.TM_Begins;
                            } else if (localName.equals(TimeOperator2.BegunBy.name())) {
                                operator = TimeOperator.TM_BegunBy;
                            } else if (localName.equals(TimeOperator2.TContains.name())) {
                                operator = TimeOperator.TM_Contains;
                            } else if (localName.equals(TimeOperator2.During.name())) {
                                operator = TimeOperator.TM_During;
                            } else if (localName.equals(TimeOperator2.EndedBy.name())) {
                                operator = TimeOperator.TM_EndedBy;
                            } else if (localName.equals(TimeOperator2.Ends.name())) {
                                operator = TimeOperator.TM_Ends;
                            } else if (localName.equals(TimeOperator2.TEquals.name())) {
                                operator = TimeOperator.TM_Equals;
                            } else if (localName.equals(TimeOperator2.Meets.name())) {
                                operator = TimeOperator.TM_Meets;
                            } else if (localName.equals(TimeOperator2.MetBy.name())) {
                                operator = TimeOperator.TM_MetBy;
                            } else if (localName.equals(TimeOperator2.TOverlaps.name())) {
                                operator = TimeOperator.TM_Overlaps;
                            } else if (localName.equals(TimeOperator2.OverlappedBy.name())) {
                                operator = TimeOperator.TM_OverlappedBy;
                            } else {
                                throw new DecodingException(Sos2Constants.GetObservationParams.temporalFilter,
                                        "The requested temporal filter operand is not supported by this SOS!");
                            }
                            temporalFilter.setOperator(operator);
                            temporalFilter.setTime(time);
                            break;
                        } else {
                            throw new DecodingException(Sos2Constants.GetObservationParams.temporalFilter,
                                    "The requested temporal filter value is not supported by this SOS!");
                        }
                    }
                }
            } else {
                throw new DecodingException(Sos2Constants.GetObservationParams.temporalFilter,
                        "The requested temporal filter operand is not supported by this SOS!");
            }
        } catch (XmlException xmle) {
            throw new DecodingException("Error while parsing temporal filter!", xmle);
        }
        return temporalFilter;
    }

    /**
     * parses a single comparison filter of the requests and returns service
     * comparison filter
     *
     * @param comparisonOpsType
     *            XmlObject representing the comparison filter
     * @return Service representation of comparison filter
     * @throws DecodingException
     *             if creation of the ComparisonFilter failed
     */
    private ComparisonFilter parseComparisonFilterType(ComparisonOpsType comparisonOpsType) throws DecodingException {
        if (comparisonOpsType instanceof BinaryComparisonOpType) {
            return parseBinaryComparisonFilter((BinaryComparisonOpType) comparisonOpsType);
        } else if (comparisonOpsType instanceof PropertyIsLikeType) {
            return parsePropertyIsLikeFilter((PropertyIsLikeType) comparisonOpsType);
        } else if (comparisonOpsType instanceof PropertyIsNullType) {
            return parsePropertyIsNullFilter((PropertyIsNullType) comparisonOpsType);
        } else if (comparisonOpsType instanceof PropertyIsNilType) {
            return parsePropertyIsNilFilter((PropertyIsNilType) comparisonOpsType);
        } else if (comparisonOpsType instanceof PropertyIsBetweenType) {
            return parsePropertyIsBetweenFilter((PropertyIsBetweenType) comparisonOpsType);
        } else {
            throw new UnsupportedDecoderXmlInputException(this, comparisonOpsType);
        }
    }

    private ComparisonFilter parseBinaryComparisonFilter(BinaryComparisonOpType comparisonOpsType)
            throws DecodingException {
        ComparisonFilter comparisonFilter = new ComparisonFilter();
        String localName = XmlHelper.getLocalName(comparisonOpsType);
        if (ComparisonOperator.PropertyIsEqualTo.name().equals(localName)) {
            comparisonFilter.setOperator(ComparisonOperator.PropertyIsEqualTo);
        } else if (ComparisonOperator.PropertyIsNotEqualTo.name().equals(localName)) {
            comparisonFilter.setOperator(ComparisonOperator.PropertyIsNotEqualTo);
        } else if (ComparisonOperator.PropertyIsLessThan.name().equals(localName)) {
            comparisonFilter.setOperator(ComparisonOperator.PropertyIsLessThan);
        } else if (ComparisonOperator.PropertyIsGreaterThan.name().equals(localName)) {
            comparisonFilter.setOperator(ComparisonOperator.PropertyIsGreaterThan);
        } else if (ComparisonOperator.PropertyIsLessThanOrEqualTo.name().equals(localName)) {
            comparisonFilter.setOperator(ComparisonOperator.PropertyIsLessThanOrEqualTo);
        } else if (ComparisonOperator.PropertyIsGreaterThanOrEqualTo.name().equals(localName)) {
            comparisonFilter.setOperator(ComparisonOperator.PropertyIsGreaterThanOrEqualTo);
        } else {
            throw new UnsupportedDecoderXmlInputException(this, comparisonOpsType);
        }
        parseExpressions(comparisonOpsType.getExpressionArray(), comparisonFilter);
        return comparisonFilter;
    }

    /**
     * Parse XML expression array
     *
     * @param expressionArray
     *            XML expression array
     * @param comparisonFilter
     *            SOS comparison filter
     * @throws DecodingException
     *             if an error occurs
     */
    private void parseExpressions(XmlObject[] expressionArray, ComparisonFilter comparisonFilter)
            throws DecodingException {
        for (XmlObject xmlObject : expressionArray) {
            if (isValueReferenceExpression(xmlObject)) {
                try {
                    comparisonFilter.setValueReference(parseValueReference(xmlObject));
                } catch (XmlException xmle) {
                    throw new DecodingException("Error while parsing valueReference element!", xmle);
                }
            } else if (xmlObject instanceof LiteralType) {
                // TODO is this the best way?
                LiteralType literalType = (LiteralType) xmlObject;
                comparisonFilter.setValue(literalType.getDomNode().getFirstChild().getNodeValue());
            }
        }
    }

    /**
     * Check if the XmlObject is a valueReference element
     *
     * @param xmlObject
     *            Element to check
     * @return <code>true</code>, if XmlObject is a valueReference element
     */
    private boolean isValueReferenceExpression(XmlObject xmlObject) {
        return FilterConstants.EN_VALUE_REFERENCE.equals(XmlHelper.getLocalName(xmlObject));
    }

    /**
     * Parse XML valueReference element
     *
     * @param xmlObject
     *            XML valueReference
     * @return ValueReference string
     * @throws XmlException
     *             If an error occurs
     */
    private String parseValueReference(XmlObject xmlObject) throws XmlException {
        ValueReferenceDocument valueRefernece = ValueReferenceDocument.Factory.parse(xmlObject.getDomNode());
        return valueRefernece.getValueReference().trim();
    }

    /**
     * Parse XML propertyIsLike element
     *
     * @param comparisonOpsType
     *            XML propertyIsLike element
     * @return SOS comparison filter
     * @throws DecodingException
     *             If an error occurs of the filter is not supported!
     */
    private ComparisonFilter parsePropertyIsLikeFilter(PropertyIsLikeType comparisonOpsType) throws DecodingException {
        ComparisonFilter comparisonFilter = new ComparisonFilter();
        comparisonFilter.setOperator(ComparisonOperator.PropertyIsLike);
        comparisonFilter.setEscapeString(comparisonOpsType.getEscapeChar());
        comparisonFilter.setSingleChar(comparisonOpsType.getSingleChar());
        comparisonFilter.setWildCard(comparisonOpsType.getWildCard());
        parseExpressions(comparisonOpsType.getExpressionArray(), comparisonFilter);
        return comparisonFilter;
    }

    /**
     * Parse XML propertyIsNull element
     *
     * @param comparisonOpsType
     *            XML propertyIsNull element
     * @return SOS comparison filter
     * @throws DecodingException
     *             If an error occurs of the filter is not supported
     */
    private ComparisonFilter parsePropertyIsNullFilter(PropertyIsNullType comparisonOpsType) throws DecodingException {
        ComparisonFilter comparisonFilter = new ComparisonFilter();
        comparisonFilter.setOperator(ComparisonOperator.PropertyIsNull);
        throw new UnsupportedDecoderXmlInputException(this, comparisonOpsType);
        // TODO get values
        // return comparisonFilter;
    }

    /**
     * Parse XML propertyIsNil element
     *
     * @param comparisonOpsType
     *            XML propertyIsNil element
     * @return SOS comparison filter
     * @throws DecodingException
     *             If an error occurs of the filter is not supported
     */
    private ComparisonFilter parsePropertyIsNilFilter(PropertyIsNilType comparisonOpsType) throws DecodingException {
        ComparisonFilter comparisonFilter = new ComparisonFilter();
        comparisonFilter.setOperator(ComparisonOperator.PropertyIsNil);
        throw new UnsupportedDecoderXmlInputException(this, comparisonOpsType);
        // TODO get values
        // return comparisonFilter;
    }

    /**
     * Parse XML propertyIsBetween element
     *
     * @param comparisonOpsType
     *            XML propertyIsBetween element
     * @return SOS comparison filter
     * @throws DecodingException
     *             If an error occurs of the filter is not supported
     */
    private ComparisonFilter parsePropertyIsBetweenFilter(PropertyIsBetweenType comparisonOpsType)
            throws DecodingException {
        ComparisonFilter comparisonFilter = new ComparisonFilter();
        comparisonFilter.setOperator(ComparisonOperator.PropertyIsBetween);
        throw new UnsupportedDecoderXmlInputException(this, comparisonOpsType);
        // TODO get values
        // return comparisonFilter;
    }

    /**
     * parses a single logic filter of the requests and returns service logic
     * filter
     *
     * @param logicOpsType
     *            XmlObject representing the logic filter
     * @return Service representation of logic filter
     * @throws DecodingException
     *             if creation of the logic filter failed
     */
    private Filter<?> parseLogicFilterType(LogicOpsType logicOpsType) throws DecodingException {
        if (logicOpsType instanceof UnaryLogicOpType) {
            return parseUnaryLogicalFilter((UnaryLogicOpType) logicOpsType);
        } else if (logicOpsType instanceof BinaryLogicOpType) {
            return parseBinaryLogicalFilter((BinaryLogicOpType) logicOpsType);
        }
        throw new UnsupportedDecoderXmlInputException(this, logicOpsType);
    }

    /**
     * parses a single unary logic filter of the requests and returns service
     * unary logic filter
     *
     * @param unaryLogicOpType
     *            XmlObject representing the unary logic filter
     * @return Service representation of unary logic filter
     * @throws DecodingException
     *             if creation of the UnaryLogicFilter failed
     */
    private UnaryLogicFilter parseUnaryLogicalFilter(UnaryLogicOpType unaryLogicOpType) throws DecodingException {
        if (unaryLogicOpType.isSetComparisonOps()) {
            return new UnaryLogicFilter(parseComparisonFilterType(unaryLogicOpType.getComparisonOps()));
        } else if (unaryLogicOpType.isSetSpatialOps()) {
            return new UnaryLogicFilter(parseSpatialFilterType(unaryLogicOpType.getSpatialOps()));
        } else if (unaryLogicOpType.isSetTemporalOps()) {
            return new UnaryLogicFilter(parseTemporalFilterType(unaryLogicOpType.getTemporalOps()));
        } else if (unaryLogicOpType.isSetLogicOps()) {
            return new UnaryLogicFilter(parseLogicFilterType(unaryLogicOpType.getLogicOps()));
        } else if (unaryLogicOpType.isSetFunction()) {
            throw new UnsupportedDecoderXmlInputException(this, unaryLogicOpType);
        } else if (unaryLogicOpType.getIdArray() != null) {
            throw new UnsupportedDecoderXmlInputException(this, unaryLogicOpType);
        } else {
            throw new UnsupportedDecoderXmlInputException(this, unaryLogicOpType);
        }
    }

    /**
     * parses a single binary logic filter of the requests and returns service
     * binary logic filter
     *
     * @param binaryLogicOpType
     *            XmlObject representing the binary logic filter
     * @return Service representation of binary logic filter
     * @throws DecodingException
     *             if creation of the BinaryLogicFilter failed or filter size is
     *             less than two
     */
    private BinaryLogicFilter parseBinaryLogicalFilter(BinaryLogicOpType binaryLogicOpType) throws DecodingException {
        BinaryLogicFilter binaryLogicFilter = null;
        String localName = XmlHelper.getLocalName(binaryLogicOpType);
        if (localName.equals(BinaryLogicOperator.And.name())) {
            binaryLogicFilter = new BinaryLogicFilter(BinaryLogicOperator.And);
        } else if (localName.equals(BinaryLogicOperator.Or.name())) {
            binaryLogicFilter = new BinaryLogicFilter(BinaryLogicOperator.Or);
        } else {
            throw new UnsupportedDecoderXmlInputException(this, binaryLogicOpType);
        }
        Set<Filter<?>> filters = getFilterPredicates(binaryLogicOpType);

        if (filters.size() < 2) {
            throw new DecodingException("The binary logic filter requires minimla two filter predicates!");
        }
        binaryLogicFilter.addFilterPredicates(filters);
        return binaryLogicFilter;
    }

    /**
     * Get the predicate filters from binary logic filter
     *
     * @param binaryLogicOpType
     *            XmlObject representing the binary logic filter
     * @return Predicate filters
     * @throws DecodingException
     *             if creation of the predicate filters failed
     */
    private Set<Filter<?>> getFilterPredicates(BinaryLogicOpType binaryLogicOpType) throws DecodingException {
        Set<Filter<?>> filters = Sets.newHashSet();
        if (CollectionHelper.isNotNullOrEmpty(binaryLogicOpType.getComparisonOpsArray())) {
            filters.addAll(parseComparisonFilterArray(binaryLogicOpType.getComparisonOpsArray()));
        }
        if (CollectionHelper.isNotNullOrEmpty(binaryLogicOpType.getLogicOpsArray())) {
            filters.addAll(parseLogicFilterArray(binaryLogicOpType.getLogicOpsArray()));
        }
        if (CollectionHelper.isNotNullOrEmpty(binaryLogicOpType.getSpatialOpsArray())) {
            filters.addAll(parseSpatialFilterArray(binaryLogicOpType.getSpatialOpsArray()));
        }
        if (CollectionHelper.isNotNullOrEmpty(binaryLogicOpType.getTemporalOpsArray())) {
            filters.addAll(parseTemporalFilterArray(binaryLogicOpType.getTemporalOpsArray()));
        }
        if (CollectionHelper.isNotNullOrEmpty(binaryLogicOpType.getExtensionOpsArray())) {
            throw new UnsupportedDecoderXmlInputException(this, binaryLogicOpType);
        }
        if (CollectionHelper.isNotNullOrEmpty(binaryLogicOpType.getFunctionArray())) {
            throw new UnsupportedDecoderXmlInputException(this, binaryLogicOpType);
        }
        if (CollectionHelper.isNotNullOrEmpty(binaryLogicOpType.getIdArray())) {
            throw new UnsupportedDecoderXmlInputException(this, binaryLogicOpType);
        }
        return filters;
    }

    /**
     * Parse single comparison filter from comparison filter array
     *
     * @param comparisonOpsArray
     *            XmlBeans comparison filter array
     * @return Service comparison filters
     * @throws DecodingException
     *             if creation of the comparison filters failed
     */
    private Collection<? extends Filter<?>> parseComparisonFilterArray(ComparisonOpsType[] comparisonOpsArray)
            throws DecodingException {
        Set<Filter<?>> filters = Sets.newHashSet();
        for (ComparisonOpsType comparisonOpsType : comparisonOpsArray) {
            filters.add(parseComparisonFilterType(comparisonOpsType));
        }
        return filters;
    }

    /**
     * Parse single logic filter from logic filter array
     *
     * @param logicOpsArray
     *            XmlBeans logic filter array
     * @return Service logic filters
     * @throws DecodingException
     *             if creation of the logic filters failed
     */
    private Collection<? extends Filter<?>> parseLogicFilterArray(LogicOpsType[] logicOpsArray)
            throws DecodingException {
        Set<Filter<?>> filters = Sets.newHashSet();
        for (LogicOpsType logicOpsType : logicOpsArray) {
            filters.add(parseLogicFilterType(logicOpsType));
        }
        return filters;
    }

    /**
     * Parse single spatial filter from spatial filter array
     *
     * @param spatialOpsArray
     *            XmlBeans spatial filter array
     * @return Service spatial filters
     * @throws DecodingException
     *             if creation of the spatial filters failed
     */
    private Collection<? extends Filter<?>> parseSpatialFilterArray(SpatialOpsType[] spatialOpsArray)
            throws DecodingException {
        Set<Filter<?>> filters = Sets.newHashSet();
        for (SpatialOpsType spatialOpsType : spatialOpsArray) {
            filters.add(parseSpatialFilterType(spatialOpsType));
        }
        return filters;
    }

    /**
     * Parse single temporal filter from temporal filter array
     *
     * @param temporalOpsArray
     *            XmlBeans temporal filter array
     * @return Service temporal filters
     * @throws DecodingException
     *             if creation of the temporal filters failed
     */
    private Collection<? extends Filter<?>> parseTemporalFilterArray(TemporalOpsType[] temporalOpsArray)
            throws DecodingException {
        Set<Filter<?>> filters = Sets.newHashSet();
        for (TemporalOpsType temporalOpsType : temporalOpsArray) {
            filters.add(parseTemporalFilterType(temporalOpsType));
        }
        return filters;
    }

}
