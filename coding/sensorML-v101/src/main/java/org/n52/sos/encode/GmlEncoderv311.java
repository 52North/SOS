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

import javax.xml.namespace.QName;

import net.opengis.gml.AbstractFeatureCollectionType;
import net.opengis.gml.AbstractRingPropertyType;
import net.opengis.gml.AbstractRingType;
import net.opengis.gml.CodeType;
import net.opengis.gml.DirectPositionListType;
import net.opengis.gml.DirectPositionType;
import net.opengis.gml.EnvelopeType;
import net.opengis.gml.FeatureCollectionDocument2;
import net.opengis.gml.FeaturePropertyType;
import net.opengis.gml.LineStringType;
import net.opengis.gml.LinearRingType;
import net.opengis.gml.MeasureType;
import net.opengis.gml.PointType;
import net.opengis.gml.PolygonType;
import net.opengis.gml.ReferenceType;
import net.opengis.gml.TimeIndeterminateValueType;
import net.opengis.gml.TimeInstantDocument;
import net.opengis.gml.TimeInstantType;
import net.opengis.gml.TimePeriodDocument;
import net.opengis.gml.TimePeriodType;
import net.opengis.gml.TimePositionType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlRuntimeException;
import org.apache.xmlbeans.impl.values.XmlValueDisconnectedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.gml.time.TimePosition;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.CategoryValue;
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
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.PolygonExtracter;

/**
 * @since 4.0.0
 * 
 */
public class GmlEncoderv311 extends AbstractXmlEncoder<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GmlEncoderv311.class);

    private static final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(GmlConstants.NS_GML,
            org.n52.sos.ogc.gml.time.Time.class, com.vividsolutions.jts.geom.Geometry.class,
            org.n52.sos.ogc.om.values.CategoryValue.class, org.n52.sos.ogc.gml.ReferenceType.class,
            org.n52.sos.ogc.om.values.QuantityValue.class, org.n52.sos.ogc.gml.CodeWithAuthority.class,
            org.n52.sos.ogc.gml.CodeType.class, AbstractFeature.class, SosEnvelope.class);

    public GmlEncoderv311() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(GmlConstants.NS_GML, GmlConstants.NS_GML_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(GmlConstants.GML_311_SCHEMAL_LOCATION);
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        XmlObject encodedObject = null;
        if (element instanceof Time) {
            encodedObject = createTime((Time) element, additionalValues);
        } else if (element instanceof Geometry) {
            encodedObject = createPosition((Geometry) element, additionalValues.get(HelperValues.GMLID));
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
            encodedObject = createFeature((AbstractFeature) element);
        } else if (element instanceof SosEnvelope) {
            encodedObject = createEnvelope((SosEnvelope) element);
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
        LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject createTime(Time time, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (time != null) {
            if (time instanceof TimeInstant) {
                if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                    return createTimeInstantDocument((TimeInstant) time);
                } else {
                    return createTimeInstantType((TimeInstant) time, null);
                }
            } else if (time instanceof TimePeriod) {
                if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                    return createTimePeriodDocument((TimePeriod) time);
                } else {
                    return createTimePeriodType((TimePeriod) time, null);
                }
            } else {
                throw new UnsupportedEncoderInputException(this, time);
            }
        }
        return null;
    }

    private XmlObject createTimePeriodDocument(TimePeriod time) throws OwsExceptionReport {
        TimePeriodDocument timePeriodDoc =
                TimePeriodDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        createTimePeriodType(time, timePeriodDoc.addNewTimePeriod());
        return timePeriodDoc;
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
    private TimePeriodType createTimePeriodType(TimePeriod timePeriod, TimePeriodType timePeriodType)
            throws OwsExceptionReport {
        try {
            if (timePeriodType == null) {
                timePeriodType = TimePeriodType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            }
            if (timePeriod.getGmlId() != null && !timePeriod.getGmlId().isEmpty()) {
                timePeriodType.setId(timePeriod.getGmlId());
            }
            timePeriodType.setBeginPosition(createTimePositionType(timePeriod.getStartTimePosition()));
            timePeriodType.setEndPosition(createTimePositionType(timePeriod.getEndTimePosition()));

            return timePeriodType;
        } catch (XmlRuntimeException x) {
            throw new NoApplicableCodeException().causedBy(x).withMessage("Error while creating TimePeriod!");
        } catch (XmlValueDisconnectedException x) {
            throw new NoApplicableCodeException().causedBy(x).withMessage("Error while creating TimePeriod!");
        }
    }

    private XmlObject createTimeInstantDocument(TimeInstant time) throws OwsExceptionReport {
        TimeInstantDocument timeInstantDoc =
                TimeInstantDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        createTimeInstantType(time, timeInstantDoc.addNewTimeInstant());
        return timeInstantDoc;
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
    private TimeInstantType createTimeInstantType(TimeInstant timeInstant, TimeInstantType timeInstantType)
            throws OwsExceptionReport {
        // create time instant
        if (timeInstantType == null) {
            timeInstantType = TimeInstantType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        }
        if (timeInstant.isSetGmlId()) {
            timeInstantType.setId(timeInstant.getGmlId());
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
            final String endString =
                    DateTimeHelper.formatDateTime2String(timePosition.getTime(), timePosition.getTimeFormat());

            // concat minutes for timeZone offset, because gml requires
            // xs:dateTime, which needs minutes in
            // timezone offset
            // TODO enable really
            xbTimePosition.setStringValue(endString);
        }
        return xbTimePosition;
    }

    private XmlObject createPosition(Geometry geom, String foiId) throws OwsExceptionReport {
        if (geom instanceof Point) {
            PointType xbPoint = PointType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (foiId != null) {
                xbPoint.setId("point_" + foiId);
            }
            createPointFromJtsGeometry((Point) geom, xbPoint);
            return xbPoint;
        } else if (geom instanceof LineString) {
            LineStringType xbLineString =
                    LineStringType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (foiId != null) {
                xbLineString.setId("lineString_" + foiId);
            }
            createLineStringFromJtsGeometry((LineString) geom, xbLineString);
            return xbLineString;
        } else if (geom instanceof Polygon) {
            PolygonType xbPolygon = PolygonType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (foiId != null) {
                xbPolygon.setId("polygon_" + foiId);
            }
            createPolygonFromJtsGeometry((Polygon) geom, xbPolygon);
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
    private void createPointFromJtsGeometry(Point jtsPoint, PointType xbPoint) throws OwsExceptionReport {
        DirectPositionType xbPos = xbPoint.addNewPos();
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
    private void createLineStringFromJtsGeometry(LineString jtsLineString, LineStringType xbLst)
            throws OwsExceptionReport {
        DirectPositionListType xbPosList = xbLst.addNewPosList();
        xbPosList.setSrsName(getSrsName(jtsLineString));
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
    private void createPolygonFromJtsGeometry(Polygon jtsPolygon, PolygonType xbPolType) throws OwsExceptionReport {
        List<?> jtsPolygons = PolygonExtracter.getPolygons(jtsPolygon);
        for (int i = 0; i < jtsPolygons.size(); i++) {

            Polygon pol = (Polygon) jtsPolygons.get(i);

            AbstractRingPropertyType xbArpt = xbPolType.addNewExterior();
            AbstractRingType xbArt = xbArpt.addNewRing();

            LinearRingType xbLrt = LinearRingType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());

            // Exterior ring
            LineString ring = pol.getExteriorRing();
            String coords = JTSHelper.getCoordinatesString(ring);
            DirectPositionListType xbPosList = xbLrt.addNewPosList();
            xbPosList.setSrsName(getSrsName(jtsPolygon));
            // switch coordinates
            xbPosList.setStringValue(coords);
            xbArt.set(xbLrt);

            // Rename element name for output
            XmlCursor cursor = xbArpt.newCursor();
            if (cursor.toChild(GmlConstants.QN_ABSTRACT_RING)) {
                cursor.setName(GmlConstants.QN_LINEAR_RING);
            }

            // Interior ring
            int numberOfInteriorRings = pol.getNumInteriorRing();
            for (int ringNumber = 0; ringNumber < numberOfInteriorRings; ringNumber++) {
                xbArpt = xbPolType.addNewInterior();
                xbArt = xbArpt.addNewRing();

                xbLrt = LinearRingType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());

                ring = pol.getInteriorRingN(ringNumber);

                xbPosList = xbLrt.addNewPosList();
                xbPosList.setSrsName(getSrsName(jtsPolygon));
                xbPosList.setStringValue(JTSHelper.getCoordinatesString(ring));
                xbArt.set(xbLrt);

                // Rename element name for output
                cursor = xbArpt.newCursor();
                if (cursor.toChild(GmlConstants.QN_ABSTRACT_RING)) {
                    cursor.setName(GmlConstants.QN_LINEAR_RING);
                }
            }
        }
    }

    private XmlObject createReferenceTypeForCategroyValue(CategoryValue categoryValue) {
        ReferenceType xbRef = ReferenceType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (categoryValue.getValue() != null && !categoryValue.getValue().isEmpty()) {
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

    private XmlObject createReferencType(org.n52.sos.ogc.gml.ReferenceType sosReferenceType) {
        if (sosReferenceType.isSetHref()) {
            ReferenceType referenceType =
                    ReferenceType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            referenceType.setHref(sosReferenceType.getHref());
            if (sosReferenceType.isSetTitle()) {
                referenceType.setTitle(sosReferenceType.getTitle());
            }
            if (sosReferenceType.isSetRole()) {
                referenceType.setRole(sosReferenceType.getRole());
            }
            return referenceType;
        }
        return null;

    }

    private XmlObject createCodeWithAuthorityType(CodeWithAuthority sosCodeWithAuthority) {
        if (sosCodeWithAuthority.isSetValue()) {
            CodeType codeType = CodeType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            String value = sosCodeWithAuthority.getValue();
            codeType.setStringValue(value);
            codeType.setCodeSpace(sosCodeWithAuthority.getCodeSpace());
            return codeType;
        }
        return null;
    }

    private XmlObject createCodeType(org.n52.sos.ogc.gml.CodeType sosCodeType) {
        CodeType codeType = CodeType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        codeType.setCodeSpace(sosCodeType.getCodeSpace());
        codeType.setStringValue(sosCodeType.getValue());
        return codeType;
    }

    protected XmlObject createMeasureType(QuantityValue quantityValue) {
        MeasureType measureType = MeasureType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (quantityValue.getUnit() != null) {
            measureType.setUom(quantityValue.getUnit());
        } else {
            measureType.setUom("");
        }
        if (quantityValue.getValue() != null) {
            measureType.setDoubleValue(quantityValue.getValue().doubleValue());
        } else {
            measureType.setNil();
        }
        return measureType;
    }

    private XmlObject createFeature(AbstractFeature sosAbstractFeature) throws OwsExceptionReport {
        if (sosAbstractFeature instanceof SamplingFeature) {
            SamplingFeature sampFeat = (SamplingFeature) sosAbstractFeature;
            if (sosAbstractFeature.isSetGmlID()) {
                FeaturePropertyType featureProperty =
                        FeaturePropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                featureProperty.setHref("#" + sosAbstractFeature.getGmlId());
                return featureProperty;
            } else {
                if (!sampFeat.isSetGeometry()) {
                    FeaturePropertyType featureProperty =
                            FeaturePropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                    featureProperty.setHref(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue());
                    if (sampFeat.isSetName()) {
                        featureProperty.setTitle(sampFeat.getFirstName().getValue());
                    }
                    return featureProperty;
                }
                StringBuilder builder = new StringBuilder();
                builder.append("sf_");
                builder.append(JavaHelper.generateID(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue()));
                sosAbstractFeature.setGmlId(builder.toString());
                Encoder<XmlObject, SamplingFeature> encoder = CodingHelper.getEncoder(SfConstants.NS_SA, sampFeat);
                if (encoder != null) {
                    return encoder.encode(sampFeat);
                } else {
                    FeaturePropertyType featureProperty =
                            FeaturePropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                    featureProperty.setHref(sampFeat.getIdentifierCodeWithAuthority().getValue());
                    if (sampFeat.isSetName()) {
                        featureProperty.setTitle(sampFeat.getFirstName().getValue());
                    }
                    return featureProperty;
                }
            }
        } else if (sosAbstractFeature instanceof FeatureCollection) {
            return createFeatureCollection((FeatureCollection) sosAbstractFeature);
        }
        throw new UnsupportedEncoderInputException(this, sosAbstractFeature);
    }

    private XmlObject createFeatureCollection(FeatureCollection sosFeatureCollection) throws OwsExceptionReport {
        Map<String, AbstractFeature> members = sosFeatureCollection.getMembers();
        XmlObject xmlObject = null;
        if (sosFeatureCollection.isSetMembers()) {
            if (members.size() == 1) {
                for (String member : members.keySet()) {
                    if (members.get(member) instanceof SamplingFeature) {
                        return createFeature((SamplingFeature) members.get(member));
                    } else {
                        throw new NoApplicableCodeException().withMessage("No encoder found for featuretype");
                    }
                }
            } else {
                FeatureCollectionDocument2 xbFeatureColllectionDoc =
                        FeatureCollectionDocument2.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                AbstractFeatureCollectionType xbFeatCol = xbFeatureColllectionDoc.addNewFeatureCollection();
                StringBuilder builder = new StringBuilder();
                builder.append("sfc_");
                builder.append(JavaHelper.generateID(Long.toString(System.currentTimeMillis())));
                xbFeatCol.setId(builder.toString());
                for (String member : members.keySet()) {
                    if (members.get(member) instanceof SamplingFeature) {
                        XmlObject xmlFeature = createFeature((SamplingFeature) members.get(member));
                        xbFeatCol.addNewFeatureMember().set(xmlFeature);
                    } else {
                        throw new NoApplicableCodeException().withMessage("No encoder found for featuretype");
                    }
                }
                xmlObject = xbFeatureColllectionDoc;
            }
        } else {
            FeatureCollectionDocument2 xbFeatColDoc =
                    FeatureCollectionDocument2.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            xbFeatColDoc.addNewFeatureCollection();
            xmlObject = xbFeatColDoc;
        }
        XmlCursor cursor = xmlObject.newCursor();
        boolean isAFC = cursor.toChild(new QName(GmlConstants.NS_GML, GmlConstants.EN_ABSTRACT_FEATURE_COLLECTION));
        if (isAFC) {
            cursor.setName(new QName(GmlConstants.NS_GML, GmlConstants.EN_FEATURE_COLLECTION));
        }
        cursor.dispose();
        return xmlObject;
    }

    private XmlObject createEnvelope(SosEnvelope sosEnvelope) {
        EnvelopeType envelopeType = EnvelopeType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        MinMax<String> minmax = SosHelper.getMinMaxFromEnvelope(sosEnvelope.getEnvelope());
        envelopeType.addNewLowerCorner().setStringValue(minmax.getMinimum());
        envelopeType.addNewUpperCorner().setStringValue(minmax.getMaximum());
        envelopeType.setSrsName(ServiceConfiguration.getInstance().getSrsNamePrefix() + sosEnvelope.getSrid());
        return envelopeType;
    }

    protected String getSrsName(Geometry geom) {
        return ServiceConfiguration.getInstance().getSrsNamePrefix() + geom.getSRID();
    }
}
