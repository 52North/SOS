/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.opengis.gml.x32.AbstractGeometryType;
import net.opengis.gml.x32.AbstractRingPropertyType;
import net.opengis.gml.x32.AbstractRingType;
import net.opengis.gml.x32.CodeType;
import net.opengis.gml.x32.CodeWithAuthorityType;
import net.opengis.gml.x32.DirectPositionListType;
import net.opengis.gml.x32.DirectPositionType;
import net.opengis.gml.x32.EnvelopeType;
import net.opengis.gml.x32.FeatureCollectionDocument;
import net.opengis.gml.x32.FeatureCollectionType;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.GeometryPropertyType;
import net.opengis.gml.x32.LineStringDocument;
import net.opengis.gml.x32.LineStringType;
import net.opengis.gml.x32.LinearRingType;
import net.opengis.gml.x32.MeasureType;
import net.opengis.gml.x32.PointDocument;
import net.opengis.gml.x32.PointType;
import net.opengis.gml.x32.PolygonDocument;
import net.opengis.gml.x32.PolygonType;
import net.opengis.gml.x32.ReferenceType;
import net.opengis.gml.x32.TimeIndeterminateValueType;
import net.opengis.gml.x32.TimeInstantDocument;
import net.opengis.gml.x32.TimeInstantPropertyType;
import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePeriodDocument;
import net.opengis.gml.x32.TimePeriodPropertyType;
import net.opengis.gml.x32.TimePeriodType;
import net.opengis.gml.x32.TimePositionType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.service.ServiceConfiguration;
import org.n52.iceland.util.MinMax;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.AbstractGeometry;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.gml.time.TimePosition;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.util.DateTimeFormatException;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.EncodingContext;
import org.n52.svalbard.SosHelperValues;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;
import org.n52.svalbard.xml.AbstractXmlEncoder;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.PolygonExtracter;

/**
 * @since 4.0.0
 *
 */
public class GmlEncoderv321 extends AbstractXmlEncoder<XmlObject, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GmlEncoderv321.class);

    private static final Set<EncoderKey> ENCODER_KEY_TYPES = CodingHelper
            .encoderKeysForElements(GmlConstants.NS_GML_32,
                                    org.n52.shetland.ogc.gml.time.Time.class,
                                    com.vividsolutions.jts.geom.Geometry.class,
                                    org.n52.shetland.ogc.om.values.CategoryValue.class,
                                    org.n52.shetland.ogc.gml.ReferenceType.class,
                                    org.n52.shetland.ogc.om.values.QuantityValue.class,
                                    org.n52.shetland.ogc.gml.CodeWithAuthority.class,
                                    org.n52.shetland.ogc.gml.CodeType.class,
                                    SamplingFeature.class,
                                    ReferencedEnvelope.class,
                                    FeatureCollection.class,
                                    AbstractGeometry.class);

    public GmlEncoderv321() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEY_TYPES));
    }

    @Override
    public Set<EncoderKey> getKeys() {
        return Collections.unmodifiableSet(ENCODER_KEY_TYPES);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(GmlConstants.NS_GML_32, GmlConstants.NS_GML_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(GmlConstants.GML_32_SCHEMAL_LOCATION);
    }

    @Override
    public XmlObject encode(final Object element, final EncodingContext additionalValues)
            throws EncodingException {
        XmlObject encodedObject = null;
        if (element instanceof Time) {
            encodedObject = createTime((Time) element, additionalValues);
        } else if (element instanceof Geometry) {
            encodedObject = createPosition((Geometry) element, additionalValues);
        } else if (element instanceof CategoryValue) {
            encodedObject = createReferenceTypeForCategroyValue((CategoryValue) element);
        } else if (element instanceof org.n52.shetland.ogc.gml.ReferenceType) {
            encodedObject = createReferencType((org.n52.shetland.ogc.gml.ReferenceType) element);
        } else if (element instanceof CodeWithAuthority) {
            encodedObject = createCodeWithAuthorityType((CodeWithAuthority) element);
        } else if (element instanceof QuantityValue) {
            encodedObject = createMeasureType((QuantityValue) element);
        } else if (element instanceof org.n52.shetland.ogc.gml.CodeType) {
            encodedObject = createCodeType((org.n52.shetland.ogc.gml.CodeType) element);
        } else if (element instanceof AbstractFeature) {
            encodedObject = createFeaturePropertyType((AbstractFeature) element, additionalValues);
        } else if (element instanceof GeometryValue) {
            encodedObject = createGeomteryPropertyType((AbstractGeometry) element, additionalValues);
        } else if (element instanceof ReferencedEnvelope) {
            encodedObject = createEnvelope((ReferencedEnvelope) element);
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
        // LOGGER.debug("Encoded object {} is valid: {}",
        // encodedObject.schemaType().toString(),
        // XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject createFeaturePropertyType(final AbstractFeature feature,
                                                EncodingContext additionalValues) throws EncodingException {
        if (feature instanceof FeatureCollection) {
            return createFeatureCollection((FeatureCollection) feature, additionalValues);
        } else if (feature instanceof SamplingFeature) {
            return createFeature(feature, additionalValues);
        } else if (feature instanceof AbstractFeature && feature.isSetDefaultElementEncoding()) {
            return encodeObjectToXml(feature.getDefaultElementEncoding(), feature);
        } else if (feature instanceof AbstractFeature && additionalValues.has(SosHelperValues.ENCODE_NAMESPACE)) {

            return encodeObjectToXml(additionalValues.get(SosHelperValues.ENCODE_NAMESPACE), feature, additionalValues);
        } else {
            throw new UnsupportedEncoderInputException(this, feature);
        }
    }

    private XmlObject createFeatureCollection(FeatureCollection element, EncodingContext additionalValues) throws EncodingException {
        final FeatureCollectionDocument featureCollectionDoc =
                FeatureCollectionDocument.Factory.newInstance(getXmlOptions());
        final FeatureCollectionType featureCollection = featureCollectionDoc.addNewFeatureCollection();
        featureCollection.setId(element.getGmlId());
        boolean document = additionalValues.has(SosHelperValues.DOCUMENT);
        EncodingContext ctx = additionalValues.with(SosHelperValues.PROPERTY_TYPE).without(SosHelperValues.DOCUMENT);

        if (element.isSetMembers()) {
            for (final AbstractFeature abstractFeature : element.getMembers().values()) {
                featureCollection.addNewFeatureMember().set(
                        createFeaturePropertyType(abstractFeature, ctx));
            }
        }
        if (document) {
            return featureCollectionDoc;
        }
        final FeaturePropertyType featurePropertyType =
                FeaturePropertyType.Factory.newInstance(getXmlOptions());
        featurePropertyType.addNewAbstractFeature().set(featureCollection);
        return XmlHelper.substituteElement(featurePropertyType.getAbstractFeature(), featurePropertyType);
        // return featureCollection;
    }

    private XmlObject createFeature(final AbstractFeature feature, final EncodingContext additionalValues)
            throws EncodingException {
        final FeaturePropertyType featurePropertyType =
                FeaturePropertyType.Factory.newInstance(getXmlOptions());
        if (isNotSamplingFeature(feature) || additionalValues.has(SosHelperValues.REFERENCED)) {
            featurePropertyType.setHref(feature.getIdentifierCodeWithAuthority().getValue());
            return featurePropertyType;
        } else {
            final SamplingFeature samplingFeature = (SamplingFeature) feature;
            if (samplingFeature.isSetGmlID()) {
                featurePropertyType.setHref("#" + samplingFeature.getGmlId());
                return featurePropertyType;
            } else {
                if (additionalValues.has(SosHelperValues.ENCODE) && !additionalValues.getBoolean(SosHelperValues.ENCODE) || !samplingFeature.isEncode()) {
                    featurePropertyType.setHref(feature.getIdentifierCodeWithAuthority().getValue());
                    if (feature instanceof SamplingFeature && samplingFeature.isSetName()) {
                        featurePropertyType.setTitle(samplingFeature.getFirstName().getValue());
                    }
                    return featurePropertyType;
                }
                if (!samplingFeature.isSetGeometry()) {
                    featurePropertyType.setHref(samplingFeature.getIdentifierCodeWithAuthority().getValue());
                    if (samplingFeature.isSetName()) {
                        featurePropertyType.setTitle(samplingFeature.getFirstName().getValue());
                    }
                    return featurePropertyType;
                }
                if (samplingFeature.isSetUrl()) {
                    featurePropertyType.setHref(samplingFeature.getUrl());
                    if (samplingFeature.isSetName()) {
                        featurePropertyType.setTitle(samplingFeature.getFirstName().getValue());
                    }
                    return featurePropertyType;
                } else {
                    String namespace;
                    if (additionalValues.has(SosHelperValues.ENCODE_NAMESPACE)) {
                        namespace = additionalValues.get(SosHelperValues.ENCODE_NAMESPACE);
                    } else {
                        namespace = OMHelper.getNamespaceForFeatureType(samplingFeature.getFeatureType());
                    }
                    final XmlObject encodedXmlObject = encodeObjectToXml(namespace, samplingFeature);

                    if (encodedXmlObject != null) {
                        return encodedXmlObject;
                    } else {
                        if (feature.isSetXml()) {
                            try {
                                // TODO how set gml:id in already existing
                                // XmlDescription? <-- XmlCursor
                                return XmlObject.Factory.parse(feature.getXml());
                            } catch (final XmlException xmle) {
                                throw new EncodingException("Error while encoding featurePropertyType!", xmle);
                            }
                        } else {
                            featurePropertyType.setHref(feature.getIdentifierCodeWithAuthority().getValue());
                            if (samplingFeature.isSetName()) {
                                featurePropertyType.setTitle(feature.getFirstName().getValue());
                            }
                            return featurePropertyType;
                        }
                    }
                }
            }
        }
    }

    private boolean isNotSamplingFeature(final AbstractFeature feature) {
        return !(feature instanceof SamplingFeature);
    }

    private XmlObject createEnvelope(final ReferencedEnvelope sosEnvelope) {
        final Envelope envelope = sosEnvelope.getEnvelope();
        final int srid = sosEnvelope.getSrid();
        final EnvelopeType envelopeType = EnvelopeType.Factory.newInstance();
        final MinMax<String> minmax = SosHelper.getMinMaxFromEnvelope(envelope);
        envelopeType.addNewLowerCorner().setStringValue(minmax.getMinimum());
        envelopeType.addNewUpperCorner().setStringValue(minmax.getMaximum());
        envelopeType.setSrsName(getSrsName(srid));
        return envelopeType;
    }

    private XmlObject createTime(Time time, EncodingContext additionalValues)
            throws EncodingException {
        if (time == null) {
            return null;
        }

        if (time instanceof TimeInstant) {
            TimeInstant instant = (TimeInstant) time;

            if (additionalValues.has(SosHelperValues.DOCUMENT)) {
                return createTimeInstantDocument(instant);
            }

            if (additionalValues.has(SosHelperValues.PROPERTY_TYPE)) {
                return createTimeInstantPropertyType(instant);
            }

            return createTimeInstantType(instant);
        }

        if (time instanceof TimePeriod) {
            TimePeriod period = (TimePeriod) time;

            if (additionalValues.has(SosHelperValues.DOCUMENT)) {
                return createTimePeriodDocument(period);
            }

            if (additionalValues.has(SosHelperValues.PROPERTY_TYPE)) {
                return createTimePeriodPropertyType(period);
            }

            return createTimePeriodType(period);
        }

        throw new UnsupportedEncoderInputException(this, time);
    }

    private XmlObject createTimePeriodDocument(TimePeriod time) throws EncodingException {
        TimePeriodDocument timePeriodDoc = TimePeriodDocument.Factory.newInstance(getXmlOptions());
        createTimePeriodType(time, timePeriodDoc.addNewTimePeriod());
        return timePeriodDoc;
    }

    private XmlObject createTimePeriodPropertyType(TimePeriod time) throws EncodingException {
        TimePeriodPropertyType timePeriodPropertyType = TimePeriodPropertyType.Factory.newInstance(getXmlOptions());
        createTimePeriodType(time, timePeriodPropertyType.addNewTimePeriod());
        return timePeriodPropertyType;
    }

    /**
     * Creates a XML TimePeriod from the SOS time object.
     *
     * @param timePeriod
     *            SOS time object
     * @param timePeriodType
     *
     * @throws EncodingException
     *             * if an error occurs.
     */
    private void createTimePeriodType(TimePeriod timePeriod, TimePeriodType timePeriodType)
            throws EncodingException {
        if (timePeriod.getGmlId() != null && !timePeriod.getGmlId().isEmpty()) {
            timePeriodType.setId(timePeriod.getGmlId());
        } else {
            timePeriodType.setId("tp_" + JavaHelper.generateID(timePeriod.toString() + System.currentTimeMillis()));
        }
        timePeriodType.setBeginPosition(createTimePositionType(timePeriod.getStartTimePosition()));
        timePeriodType.setEndPosition(createTimePositionType(timePeriod.getEndTimePosition()));
    }

    private TimePeriodType createTimePeriodType(TimePeriod timePeriod) throws EncodingException {
        TimePeriodType timePeriodType = TimePeriodType.Factory.newInstance(getXmlOptions());
        createTimePeriodType(timePeriod, timePeriodType);
        return timePeriodType;
    }

    private XmlObject createTimeInstantDocument(TimeInstant time) throws EncodingException {
        TimeInstantDocument timeInstantDoc = TimeInstantDocument.Factory.newInstance(getXmlOptions());
        createTimeInstantType(time, timeInstantDoc.addNewTimeInstant());
        return timeInstantDoc;
    }

    private XmlObject createTimeInstantPropertyType(TimeInstant time) throws EncodingException {
        TimeInstantPropertyType timeInstantPropertyType = TimeInstantPropertyType.Factory.newInstance(getXmlOptions());
        createTimeInstantType(time, timeInstantPropertyType.addNewTimeInstant());
        return timeInstantPropertyType;
    }

    /**
     * Creates a XML TimeInstant from the SOS time object.
     *
     * @param timeInstant
     *            SOS time object
     * @param timeInstantType
     *
     *
     * @throws EncodingException
     *             if an error occurs.
     */
    private void createTimeInstantType(final TimeInstant timeInstant, TimeInstantType timeInstantType)
            throws EncodingException {
        // create time instant
        if (timeInstant.isSetGmlId()) {
            timeInstantType.setId(timeInstant.getGmlId());
        } else {
            timeInstantType.setId("ti_"
                    + JavaHelper.generateID(timeInstantType.toString() + System.currentTimeMillis()));
        }
        timeInstantType.setTimePosition(createTimePositionType(timeInstant.getTimePosition()));
    }

    private TimeInstantType createTimeInstantType(TimeInstant timeInstant) throws EncodingException {
        TimeInstantType timeInstantType = TimeInstantType.Factory.newInstance(getXmlOptions());
        createTimeInstantType(timeInstant, timeInstantType);
        return timeInstantType;
    }

    private TimePositionType createTimePositionType(final TimePosition timePosition) throws DateTimeFormatException {
        TimePositionType xbTimePosition = TimePositionType.Factory.newInstance();
        if (!timePosition.isSetTime()) {
            xbTimePosition.setIndeterminatePosition(TimeIndeterminateValueType.Enum.forString(Optional
                    .ofNullable(timePosition.getIndeterminateValue()).orElse(IndeterminateValue.UNKNOWN).getValue()));
        } else {
            final String endString = DateTimeHelper.formatDateTime2String(timePosition);

            // concat minutes for timeZone offset, because gml requires
            // xs:dateTime, which needs minutes in
            // timezone offset
            // TODO enable really
            xbTimePosition.setStringValue(endString);
        }
        return xbTimePosition;
    }

    private XmlObject createGeomteryPropertyType(AbstractGeometry element, EncodingContext additionalValues)
            throws EncodingException {
        GeometryPropertyType geometryPropertyType = GeometryPropertyType.Factory.newInstance();
        if (element.isReferenced()) {
            geometryPropertyType.setHref(element.getGmlId());
        } else {
            AbstractGeometryType xmlObject = createAbstractGeometry(element, additionalValues);
            geometryPropertyType.setAbstractGeometry(xmlObject);
            XmlHelper.substituteElement(geometryPropertyType.getAbstractGeometry(), xmlObject);
        }

        return geometryPropertyType;
    }

    private AbstractGeometryType createAbstractGeometry(AbstractGeometry element,
            EncodingContext additionalValues) throws EncodingException {
        XmlObject xbGeometry = createPosition(element.getGeometry(), additionalValues);
        AbstractGeometryType abstractGeometryType = null;
        if (xbGeometry instanceof AbstractGeometryType) {
            abstractGeometryType = (AbstractGeometryType) xbGeometry;
        } else if (xbGeometry instanceof GeometryPropertyType) {
            abstractGeometryType = ((GeometryPropertyType) xbGeometry).getAbstractGeometry();
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }

        if (element.isSetIdentifier()) {
            abstractGeometryType.setIdentifier(createCodeWithAuthorityType(element.getIdentifierCodeWithAuthority()));
        }
        if (element.isSetName()) {
            for (org.n52.shetland.ogc.gml.CodeType codeType : element.getName()) {
                abstractGeometryType.addNewName().set(createCodeType(codeType));
            }
        }
        if (element.isSetDescription()) {
            abstractGeometryType.addNewDescription().setStringValue(element.getDescription());
        }
        return abstractGeometryType;
    }

    private XmlObject createPosition(Geometry geom, EncodingContext additionalValues)
            throws EncodingException {
        String foiId = additionalValues.get(SosHelperValues.GMLID);
        if (geom instanceof Point) {
            final PointType xbPoint = PointType.Factory.newInstance(getXmlOptions());
            xbPoint.setId("point_" + foiId);
            createPointFromJtsGeometry((Point) geom, xbPoint);
            if (additionalValues.has(SosHelperValues.DOCUMENT)) {
                PointDocument xbPointDoc =
                        PointDocument.Factory.newInstance(getXmlOptions());
                xbPointDoc.setPoint(xbPoint);
                return xbPointDoc;
            } else if (additionalValues.has(SosHelperValues.PROPERTY_TYPE)) {
                GeometryPropertyType geometryPropertyType =
                        GeometryPropertyType.Factory.newInstance(getXmlOptions());
                geometryPropertyType.setAbstractGeometry(xbPoint);
                geometryPropertyType.getAbstractGeometry().substitute(GmlConstants.QN_POINT_32, PointType.type);
                return geometryPropertyType;
            }
            return xbPoint;
        }

        else if (geom instanceof LineString) {
            final LineStringType xbLineString =
                    LineStringType.Factory.newInstance(getXmlOptions());
            xbLineString.setId("lineString_" + foiId);
            createLineStringFromJtsGeometry((LineString) geom, xbLineString);
            if (additionalValues.has(SosHelperValues.DOCUMENT)) {
                LineStringDocument xbLineStringDoc =
                        LineStringDocument.Factory.newInstance(getXmlOptions());
                xbLineStringDoc.setLineString(xbLineString);
                return xbLineStringDoc;
            } else if (additionalValues.has(SosHelperValues.PROPERTY_TYPE)) {
                GeometryPropertyType geometryPropertyType =
                        GeometryPropertyType.Factory.newInstance(getXmlOptions());
                geometryPropertyType.setAbstractGeometry(xbLineString);
                geometryPropertyType.getAbstractGeometry().substitute(GmlConstants.QN_LINESTRING_32,
                        LineStringType.type);
                return geometryPropertyType;
            }
            return xbLineString;
        }

        else if (geom instanceof Polygon) {
            final PolygonType xbPolygon =
                    PolygonType.Factory.newInstance(getXmlOptions());
            xbPolygon.setId("polygon_" + foiId);
            createPolygonFromJtsGeometry((Polygon) geom, xbPolygon);
            if (additionalValues.has(SosHelperValues.DOCUMENT)) {
                PolygonDocument xbPolygonDoc =
                        PolygonDocument.Factory.newInstance(getXmlOptions());
                xbPolygonDoc.setPolygon(xbPolygon);
                return xbPolygonDoc;
            } else if (additionalValues.has(SosHelperValues.PROPERTY_TYPE)) {
                GeometryPropertyType geometryPropertyType =
                        GeometryPropertyType.Factory.newInstance(getXmlOptions());
                geometryPropertyType.setAbstractGeometry(xbPolygon);
                geometryPropertyType.getAbstractGeometry().substitute(GmlConstants.QN_POLYGON_32, PolygonType.type);
                return geometryPropertyType;
            }
            return xbPolygon;
        } else {
            throw new UnsupportedEncoderInputException(this, geom);
        }
    }

    /**
     * Creates a XML Point from a SOS Point.
     *
     * @param jtsPoint
     *            SOS Point
     * @param xbPoint
     *            XML Point
     */
    private void createPointFromJtsGeometry(final Point jtsPoint, final PointType xbPoint) {
        final DirectPositionType xbPos = xbPoint.addNewPos();
        xbPos.setSrsName(getSrsName(jtsPoint));
        xbPos.setStringValue(JTSHelper.getCoordinatesString(jtsPoint));
    }

    /**
     * Creates a XML LineString from a SOS LineString.
     *
     * @param jtsLineString
     *            SOS LineString
     * @param xbLst
     *            XML LinetSring
     */
    private void createLineStringFromJtsGeometry(final LineString jtsLineString, final LineStringType xbLst) {
        final String srsName = getSrsName(jtsLineString);
        xbLst.setSrsName(srsName);
        final DirectPositionListType xbPosList = xbLst.addNewPosList();
        xbPosList.setSrsName(srsName);
        xbPosList.setStringValue(JTSHelper.getCoordinatesString(jtsLineString));

    }

    /**
     * Creates a XML Polygon from a SOS Polygon.
     *
     * @param jtsPolygon
     *            SOS Polygon
     * @param xbPolType
     *            XML Polygon
     */
    private void createPolygonFromJtsGeometry(final Polygon jtsPolygon, final PolygonType xbPolType) {
        final List<?> jtsPolygons = PolygonExtracter.getPolygons(jtsPolygon);
        final String srsName = getSrsName(jtsPolygon);

        for (int i = 0; i < jtsPolygons.size(); i++) {

            final Polygon pol = (Polygon) jtsPolygons.get(i);

            AbstractRingPropertyType xbArpt = xbPolType.addNewExterior();
            AbstractRingType xbArt = xbArpt.addNewAbstractRing();

            LinearRingType xbLrt = LinearRingType.Factory.newInstance();

            // Exterior ring
            LineString ring = pol.getExteriorRing();
            DirectPositionListType xbPosList = xbLrt.addNewPosList();

            xbPosList.setSrsName(srsName);
            xbPosList.setStringValue(JTSHelper.getCoordinatesString(ring));
            xbArt.set(xbLrt);

            // Rename element name for output
            XmlCursor cursor = xbArpt.newCursor();
            if (cursor.toChild(GmlConstants.QN_ABSTRACT_RING_32)) {
                cursor.setName(GmlConstants.QN_LINEAR_RING_32);
            }

            // Interior ring
            final int numberOfInteriorRings = pol.getNumInteriorRing();
            for (int ringNumber = 0; ringNumber < numberOfInteriorRings; ringNumber++) {
                xbArpt = xbPolType.addNewInterior();
                xbArt = xbArpt.addNewAbstractRing();

                xbLrt = LinearRingType.Factory.newInstance();

                ring = pol.getInteriorRingN(ringNumber);

                xbPosList = xbLrt.addNewPosList();
                xbPosList.setSrsName(srsName);
                xbPosList.setStringValue(JTSHelper.getCoordinatesString(ring));
                xbArt.set(xbLrt);

                // Rename element name for output
                cursor = xbArpt.newCursor();
                if (cursor.toChild(GmlConstants.QN_ABSTRACT_RING_32)) {
                    cursor.setName(GmlConstants.QN_LINEAR_RING_32);
                }
            }
        }
    }

    private XmlObject createReferenceTypeForCategroyValue(final CategoryValue categoryValue) {
        final ReferenceType xbRef = ReferenceType.Factory.newInstance(getXmlOptions());
        if (categoryValue.isSetValue()) {
            if (categoryValue.getValue().startsWith("http://")) {
                xbRef.setHref(categoryValue.getValue());
            } else {
                xbRef.setTitle(categoryValue.getValue());
            }
            if (categoryValue.isSetUnit()) {
                xbRef.setRole(categoryValue.getUnit());
            }
        } else {
            xbRef.setNil();
        }
        return xbRef;
    }

    private ReferenceType createReferencType(final org.n52.shetland.ogc.gml.ReferenceType sosReferenceType) {
        if (!sosReferenceType.isSetHref()) {
            final String exceptionText =
                    String.format("The required 'href' parameter is empty for encoding %s!",
                            ReferenceType.class.getName());
            LOGGER.error(exceptionText);
            throw new IllegalArgumentException(exceptionText);
        }
        final ReferenceType referenceType = ReferenceType.Factory.newInstance();
        referenceType.setHref(sosReferenceType.getHref());
        if (sosReferenceType.isSetTitle()) {
            referenceType.setTitle(sosReferenceType.getTitle());
        }
        if (sosReferenceType.isSetRole()) {
            referenceType.setRole(sosReferenceType.getRole());
        }
        return referenceType;
    }

    private CodeWithAuthorityType createCodeWithAuthorityType(final CodeWithAuthority sosCodeWithAuthority) {
        if (!sosCodeWithAuthority.isSetValue()) {
            final String exceptionText =
                    String.format("The required 'value' parameter is empty for encoding %s!",
                            CodeWithAuthorityType.class.getName());
            LOGGER.error(exceptionText);
            throw new IllegalArgumentException(exceptionText);
        }
        final CodeWithAuthorityType codeWithAuthority =
                CodeWithAuthorityType.Factory.newInstance(getXmlOptions());
        codeWithAuthority.setStringValue(sosCodeWithAuthority.getValue());
        if (sosCodeWithAuthority.isSetCodeSpace()) {
            codeWithAuthority.setCodeSpace(sosCodeWithAuthority.getCodeSpace());
        } else {
            codeWithAuthority.setCodeSpace(OGCConstants.UNKNOWN);
        }
        return codeWithAuthority;
    }

    private CodeType createCodeType(final org.n52.shetland.ogc.gml.CodeType sosCodeType) {
        if (!sosCodeType.isSetValue()) {
            final String exceptionText =
                    String.format("The required 'value' parameter is empty for encoding %s!", CodeType.class.getName());
            LOGGER.error(exceptionText);
            throw new IllegalArgumentException(exceptionText);
        }
        final CodeType codeType = CodeType.Factory.newInstance(getXmlOptions());
        codeType.setStringValue(sosCodeType.getValue());
        if (sosCodeType.isSetCodeSpace()) {
            codeType.setCodeSpace(sosCodeType.getCodeSpace().toString());
        } else {
            codeType.setCodeSpace(OGCConstants.UNKNOWN);
        }
        return codeType;
    }

    protected MeasureType createMeasureType(final QuantityValue quantityValue) throws EncodingException {
        if (!quantityValue.isSetValue()) {
            throw new EncodingException(
                    "The required 'value' parameter is empty for encoding %s!", MeasureType.class.getName());
        }
        final MeasureType measureType =
                MeasureType.Factory.newInstance(getXmlOptions());
        measureType.setDoubleValue(quantityValue.getValue());
        if (quantityValue.isSetUnit()) {
            measureType.setUom(quantityValue.getUnit());
        } else {
            measureType.setUom(OGCConstants.UNKNOWN);
        }

        return measureType;
    }

    protected String getSrsName(Geometry geom) {
        return getSrsName(geom.getSRID());
    }

    protected String getSrsName(int srid) {
        return ServiceConfiguration.getInstance().getSrsNamePrefixSosV2().concat(String.valueOf(srid));
    }
}
