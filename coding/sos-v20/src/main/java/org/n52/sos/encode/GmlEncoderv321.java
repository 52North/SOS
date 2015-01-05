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
package org.n52.sos.encode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.AbstractGeometry;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.gml.time.TimePosition;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.MinMax;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class GmlEncoderv321 extends AbstractXmlEncoder<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GmlEncoderv321.class);

    private static final Set<EncoderKey> ENCODER_KEY_TYPES = CodingHelper.encoderKeysForElements(
            GmlConstants.NS_GML_32, org.n52.sos.ogc.gml.time.Time.class, com.vividsolutions.jts.geom.Geometry.class,
            org.n52.sos.ogc.om.values.CategoryValue.class, org.n52.sos.ogc.gml.ReferenceType.class,
            org.n52.sos.ogc.om.values.QuantityValue.class, org.n52.sos.ogc.gml.CodeWithAuthority.class,
            org.n52.sos.ogc.gml.CodeType.class, SamplingFeature.class, SosEnvelope.class, FeatureCollection.class,
            AbstractGeometry.class);

    public GmlEncoderv321() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEY_TYPES));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
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
    public XmlObject encode(final Object element, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject encodedObject = null;
        if (element instanceof Time) {
            encodedObject = createTime((Time) element, additionalValues);
        } else if (element instanceof Geometry) {
            encodedObject = createPosition((Geometry) element, additionalValues);
        } else if (element instanceof CategoryValue) {
            encodedObject = createReferenceTypeForCategroyValue((CategoryValue) element);
        } else if (element instanceof org.n52.sos.ogc.gml.ReferenceType) {
            encodedObject = createReferencType((org.n52.sos.ogc.gml.ReferenceType) element);
        } else if (element instanceof CodeWithAuthority) {
            encodedObject = createCodeWithAuthorityType((CodeWithAuthority) element);
        } else if (element instanceof QuantityValue) {
            encodedObject = createMeasureType((QuantityValue) element);
        } else if (element instanceof org.n52.sos.ogc.gml.CodeType) {
            encodedObject = createCodeType((org.n52.sos.ogc.gml.CodeType) element);
        } else if (element instanceof AbstractFeature) {
            encodedObject = createFeaturePropertyType((AbstractFeature) element, additionalValues);
        } else if (element instanceof GeometryValue) {
            encodedObject = createGeomteryPropertyType((AbstractGeometry) element, additionalValues);
        } else if (element instanceof SosEnvelope) {
            encodedObject = createEnvelope((SosEnvelope) element);
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
        // LOGGER.debug("Encoded object {} is valid: {}",
        // encodedObject.schemaType().toString(),
        // XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject createFeaturePropertyType(final AbstractFeature feature,
            final Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (feature instanceof FeatureCollection) {
            return createFeatureCollection((FeatureCollection) feature, additionalValues);
        } else if (feature instanceof SamplingFeature) {
            return createFeature(feature, additionalValues);
        } else if (feature instanceof AbstractFeature && feature.isSetDefaultElementEncoding()) {
            return CodingHelper.encodeObjectToXml(feature.getDefaultElementEncoding(), feature);
        } else if (feature instanceof AbstractFeature && additionalValues.containsKey(HelperValues.ENCODE_NAMESPACE)) {
            return CodingHelper.encodeObjectToXml(additionalValues.get(HelperValues.ENCODE_NAMESPACE), feature,
                    additionalValues);
        } else {
            throw new UnsupportedEncoderInputException(this, feature);
        }
    }

    private XmlObject createFeatureCollection(final FeatureCollection element,
            final Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        final FeatureCollectionDocument featureCollectionDoc =
                FeatureCollectionDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final FeatureCollectionType featureCollection = featureCollectionDoc.addNewFeatureCollection();
        featureCollection.setId(element.getGmlId());
        boolean document = checkAndPrepareHelperValues(additionalValues);
        if (element.isSetMembers()) {
            for (final AbstractFeature abstractFeature : element.getMembers().values()) {
                featureCollection.addNewFeatureMember().set(
                        createFeaturePropertyType(abstractFeature, additionalValues));
            }
        }
        if (document) {
            return featureCollectionDoc;
        }
        final FeaturePropertyType featurePropertyType =
                FeaturePropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        featurePropertyType.addNewAbstractFeature().set(featureCollection);
        return XmlHelper.substituteElement(featurePropertyType.getAbstractFeature(), featurePropertyType);
        // return featureCollection;
    }

    private boolean checkAndPrepareHelperValues(Map<HelperValues, String> additionalValues) {
        // add propertyType flag
        additionalValues.put(HelperValues.PROPERTY_TYPE, null);
        // check for document flag and remove flag from values if contained
        if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            additionalValues.remove(HelperValues.DOCUMENT);
            return true;
        }
        return false;
    }

    private XmlObject createFeature(final AbstractFeature feature, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        final FeaturePropertyType featurePropertyType =
                FeaturePropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (isNotSamplingFeature(feature) || additionalValues.containsKey(HelperValues.REFERENCED)) {
            featurePropertyType.setHref(feature.getIdentifierCodeWithAuthority().getValue());
            return featurePropertyType;
        } else {
            final SamplingFeature samplingFeature = (SamplingFeature) feature;
            if (samplingFeature.isSetGmlID()) {
                featurePropertyType.setHref("#" + samplingFeature.getGmlId());
                return featurePropertyType;
            } else {
                if (additionalValues.containsKey(HelperValues.ENCODE)
                        && additionalValues.get(HelperValues.ENCODE).equals("false") || !samplingFeature.isEncode()) {
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
                    if (additionalValues.containsKey(HelperValues.ENCODE_NAMESPACE)) {
                        namespace = additionalValues.get(HelperValues.ENCODE_NAMESPACE);
                    } else {
                        namespace = OMHelper.getNamespaceForFeatureType(samplingFeature.getFeatureType());
                    }
                    final XmlObject encodedXmlObject = CodingHelper.encodeObjectToXml(namespace, samplingFeature);

                    if (encodedXmlObject != null) {
                        return encodedXmlObject;
                    } else {
                        if (samplingFeature.getXmlDescription() != null) {
                            try {
                                // TODO how set gml:id in already existing
                                // XmlDescription? <-- XmlCursor
                                return XmlObject.Factory.parse(samplingFeature.getXmlDescription());
                            } catch (final XmlException xmle) {
                                throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                                        "Error while encoding featurePropertyType!");
                            }
                        } else {
                            featurePropertyType.setHref(samplingFeature.getIdentifierCodeWithAuthority().getValue());
                            if (samplingFeature.isSetName()) {
                                featurePropertyType.setTitle(samplingFeature.getFirstName().getValue());
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

    private XmlObject createEnvelope(final SosEnvelope sosEnvelope) {
        final Envelope envelope = sosEnvelope.getEnvelope();
        final int srid = sosEnvelope.getSrid();
        final EnvelopeType envelopeType = EnvelopeType.Factory.newInstance();
        final MinMax<String> minmax = SosHelper.getMinMaxFromEnvelope(envelope);
        envelopeType.addNewLowerCorner().setStringValue(minmax.getMinimum());
        envelopeType.addNewUpperCorner().setStringValue(minmax.getMaximum());
        envelopeType.setSrsName(getSrsName(srid));
        return envelopeType;
    }

    private XmlObject createTime(final Time time, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        if (time != null) {
            if (time instanceof TimeInstant) {
                if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                    return createTimeInstantDocument((TimeInstant) time);
                } else if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
                    return createTimeInstantPropertyType((TimeInstant) time);
                } else {
                    return createTimeInstantType((TimeInstant) time, null);
                }
            } else if (time instanceof TimePeriod) {
                if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                    return createTimePeriodDocument((TimePeriod) time);
                } else if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
                    return createTimePeriodPropertyType((TimePeriod) time);
                } else {
                    return createTimePeriodType((TimePeriod) time, null);
                }
            } else {
                throw new UnsupportedEncoderInputException(this, time);
            }
        }
        return null;
    }

    private XmlObject createTimePeriodDocument(final TimePeriod time) throws OwsExceptionReport {
        final TimePeriodDocument timePeriodDoc =
                TimePeriodDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        createTimePeriodType(time, timePeriodDoc.addNewTimePeriod());
        return timePeriodDoc;
    }

    private XmlObject createTimePeriodPropertyType(final TimePeriod time) throws OwsExceptionReport {
        final TimePeriodPropertyType timePeriodPropertyType =
                TimePeriodPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        createTimePeriodType(time, timePeriodPropertyType.addNewTimePeriod());
        return timePeriodPropertyType;
    }

    /**
     * Creates a XML TimePeriod from the SOS time object.
     * 
     * @param timePeriod
     *            SOS time object
     * @param timePeriodType
     * @return XML TimePeriod
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs.
     */
    private TimePeriodType createTimePeriodType(final TimePeriod timePeriod, TimePeriodType timePeriodType)
            throws OwsExceptionReport {
        if (timePeriodType == null) {
            timePeriodType = TimePeriodType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        }
        if (timePeriod.getGmlId() != null && !timePeriod.getGmlId().isEmpty()) {
            timePeriodType.setId(timePeriod.getGmlId());
        } else {
            timePeriodType.setId("tp_" + JavaHelper.generateID(timePeriod.toString() + System.currentTimeMillis()));
        }
        timePeriodType.setBeginPosition(createTimePositionType(timePeriod.getStartTimePosition()));
        timePeriodType.setEndPosition(createTimePositionType(timePeriod.getEndTimePosition()));

        return timePeriodType;
    }

    private XmlObject createTimeInstantDocument(final TimeInstant time) throws OwsExceptionReport {
        final TimeInstantDocument timeInstantDoc =
                TimeInstantDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        createTimeInstantType(time, timeInstantDoc.addNewTimeInstant());
        return timeInstantDoc;
    }

    private XmlObject createTimeInstantPropertyType(TimeInstant time) throws OwsExceptionReport {
        final TimeInstantPropertyType timeInstantPropertyType =
                TimeInstantPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        createTimeInstantType(time, timeInstantPropertyType.addNewTimeInstant());
        return timeInstantPropertyType;
    }

    /**
     * Creates a XML TimeInstant from the SOS time object.
     * 
     * @param timeInstant
     *            SOS time object
     * @param timeInstantType
     * @return XML TimeInstant
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs.
     */
    private TimeInstantType createTimeInstantType(final TimeInstant timeInstant, TimeInstantType timeInstantType)
            throws OwsExceptionReport {
        // create time instant
        if (timeInstantType == null) {
            timeInstantType = TimeInstantType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        }
        if (timeInstant.isSetGmlId()) {
            timeInstantType.setId(timeInstant.getGmlId());
        } else {
            timeInstantType.setId("ti_"
                    + JavaHelper.generateID(timeInstantType.toString() + System.currentTimeMillis()));
        }
        timeInstantType.setTimePosition(createTimePositionType(timeInstant.getTimePosition()));
        return timeInstantType;
    }

    private TimePositionType createTimePositionType(final TimePosition timePosition) throws DateTimeFormatException {
        final TimePositionType xbTimePosition = TimePositionType.Factory.newInstance();
        if (!timePosition.isSetTime()) {
            if (timePosition.isSetIndeterminateValue()) {
                xbTimePosition.setIndeterminatePosition(TimeIndeterminateValueType.Enum.forString(timePosition
                        .getIndeterminateValue().name()));
            } else {
                xbTimePosition.setIndeterminatePosition(TimeIndeterminateValueType.Enum
                        .forString(TimeIndeterminateValue.unknown.name()));
            }
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

    private XmlObject createGeomteryPropertyType(AbstractGeometry element, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
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
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
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
            for (org.n52.sos.ogc.gml.CodeType codeType : element.getName()) {
                abstractGeometryType.addNewName().set(createCodeType(codeType));
            }
        }
        if (element.isSetDescription()) {
            abstractGeometryType.addNewDescription().setStringValue(element.getDescription());
        }
        return abstractGeometryType;
    }

    private XmlObject createPosition(Geometry geom, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        String foiId = additionalValues.get(HelperValues.GMLID);
        if (geom instanceof Point) {
            final PointType xbPoint = PointType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            xbPoint.setId("point_" + foiId);
            createPointFromJtsGeometry((Point) geom, xbPoint);
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                PointDocument xbPointDoc =
                        PointDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                xbPointDoc.setPoint(xbPoint);
                return xbPointDoc;
            } else if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
                GeometryPropertyType geometryPropertyType =
                        GeometryPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                geometryPropertyType.setAbstractGeometry(xbPoint);
                geometryPropertyType.getAbstractGeometry().substitute(GmlConstants.QN_POINT_32, PointType.type);
                return geometryPropertyType;
            }
            return xbPoint;
        }

        else if (geom instanceof LineString) {
            final LineStringType xbLineString =
                    LineStringType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            xbLineString.setId("lineString_" + foiId);
            createLineStringFromJtsGeometry((LineString) geom, xbLineString);
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                LineStringDocument xbLineStringDoc =
                        LineStringDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                xbLineStringDoc.setLineString(xbLineString);
                return xbLineStringDoc;
            } else if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
                GeometryPropertyType geometryPropertyType =
                        GeometryPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                geometryPropertyType.setAbstractGeometry(xbLineString);
                geometryPropertyType.getAbstractGeometry().substitute(GmlConstants.QN_LINESTRING_32,
                        LineStringType.type);
                return geometryPropertyType;
            }
            return xbLineString;
        }

        else if (geom instanceof Polygon) {
            final PolygonType xbPolygon =
                    PolygonType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            xbPolygon.setId("polygon_" + foiId);
            createPolygonFromJtsGeometry((Polygon) geom, xbPolygon);
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                PolygonDocument xbPolygonDoc =
                        PolygonDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                xbPolygonDoc.setPolygon(xbPolygon);
                return xbPolygonDoc;
            } else if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
                GeometryPropertyType geometryPropertyType =
                        GeometryPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
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
    private void createPointFromJtsGeometry(final Point jtsPoint, final PointType xbPoint) throws OwsExceptionReport {
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
    private void createLineStringFromJtsGeometry(final LineString jtsLineString, final LineStringType xbLst)
            throws OwsExceptionReport {
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
    private void createPolygonFromJtsGeometry(final Polygon jtsPolygon, final PolygonType xbPolType)
            throws OwsExceptionReport {
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
        final ReferenceType xbRef = ReferenceType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (categoryValue.isSetValue()) {
            if (categoryValue.getValue().startsWith("http://")) {
                xbRef.setHref(categoryValue.getValue());
            } else {
                xbRef.setTitle(categoryValue.getValue());
            }
        } else {
            xbRef.setNil();
        }
        return xbRef;
    }

    private ReferenceType createReferencType(final org.n52.sos.ogc.gml.ReferenceType sosReferenceType) {
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
                CodeWithAuthorityType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        codeWithAuthority.setStringValue(sosCodeWithAuthority.getValue());
        if (sosCodeWithAuthority.isSetCodeSpace()) {
            codeWithAuthority.setCodeSpace(sosCodeWithAuthority.getCodeSpace());
        } else {
            codeWithAuthority.setCodeSpace(OGCConstants.UNKNOWN);
        }
        return codeWithAuthority;
    }

    private CodeType createCodeType(final org.n52.sos.ogc.gml.CodeType sosCodeType) {
        if (!sosCodeType.isSetValue()) {
            final String exceptionText =
                    String.format("The required 'value' parameter is empty for encoding %s!", CodeType.class.getName());
            LOGGER.error(exceptionText);
            throw new IllegalArgumentException(exceptionText);
        }
        final CodeType codeType = CodeType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        codeType.setStringValue(sosCodeType.getValue());
        if (sosCodeType.isSetCodeSpace()) {
            codeType.setCodeSpace(sosCodeType.getCodeSpace());
        } else {
            codeType.setCodeSpace(OGCConstants.UNKNOWN);
        }
        return codeType;
    }

    protected MeasureType createMeasureType(final QuantityValue quantityValue) throws OwsExceptionReport {
        if (!quantityValue.isSetValue()) {
            throw new NoApplicableCodeException().withMessage(
                    "The required 'value' parameter is empty for encoding %s!", MeasureType.class.getName());
        }
        final MeasureType measureType =
                MeasureType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        measureType.setDoubleValue(quantityValue.getValue().doubleValue());
        if (quantityValue.isSetUnit()) {
            measureType.setUom(quantityValue.getUnit());
        } else {
            measureType.setUom(OGCConstants.UNKNOWN);
        }

        return measureType;
    }

    protected String getSrsName(final Geometry geom) {
        return getSrsName(geom.getSRID());
    }

    protected String getSrsName(final int srid) {
        return ServiceConfiguration.getInstance().getSrsNamePrefixSosV2() + srid;
    }
}
