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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.opengis.gml.x32.AbstractGeometryType;
import net.opengis.gml.x32.AbstractRingPropertyType;
import net.opengis.gml.x32.AbstractRingType;
import net.opengis.gml.x32.AbstractSurfaceType;
import net.opengis.gml.x32.CodeType;
import net.opengis.gml.x32.CodeWithAuthorityType;
import net.opengis.gml.x32.CompositeSurfaceType;
import net.opengis.gml.x32.CoordinatesType;
import net.opengis.gml.x32.DirectPositionListType;
import net.opengis.gml.x32.DirectPositionType;
import net.opengis.gml.x32.EnvelopeDocument;
import net.opengis.gml.x32.EnvelopeType;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.GeometryPropertyType;
import net.opengis.gml.x32.LineStringType;
import net.opengis.gml.x32.LinearRingType;
import net.opengis.gml.x32.MeasureType;
import net.opengis.gml.x32.PointDocument;
import net.opengis.gml.x32.PointType;
import net.opengis.gml.x32.PolygonType;
import net.opengis.gml.x32.ReferenceType;
import net.opengis.gml.x32.SurfacePropertyType;
import net.opengis.gml.x32.TimeInstantDocument;
import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePeriodDocument;
import net.opengis.gml.x32.TimePeriodType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.gml.x32.VerticalDatumPropertyType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.gml.AbstractGeometry;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.GmlMeasureType;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.util.CRSHelper;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.DateTimeParseException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderXmlInputException;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.XmlHelper;

import com.google.common.base.Joiner;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @since 4.0.0
 *
 */
public class GmlDecoderv321 extends AbstractGmlDecoderv321<XmlObject, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GmlDecoderv321.class);

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
            GmlConstants.NS_GML_32, EnvelopeDocument.class, TimeInstantType.class, TimePeriodType.class,
            TimeInstantDocument.class, TimePeriodDocument.class, ReferenceType.class, MeasureType.class,
            PointType.class, PointDocument.class, LineStringType.class, PolygonType.class, CompositeSurfaceType.class,
            CodeWithAuthorityType.class, CodeType.class, FeaturePropertyType.class, GeometryPropertyType.class,
            VerticalDatumPropertyType.class

    ), CodingHelper.decoderKeysForElements(MeasureType.type.toString(), MeasureType.class));

    private static final String CS = ",";

    private static final String DECIMAL = ".";

    private static final String TS = " ";

    public GmlDecoderv321() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Object decode(XmlObject xmlObject) throws DecodingException {
        if (xmlObject instanceof FeaturePropertyType) {
            return parseFeaturePropertyType((FeaturePropertyType) xmlObject);
        } else if (xmlObject instanceof EnvelopeDocument) {
            return parseEnvelope((EnvelopeDocument) xmlObject);
        } else if (xmlObject instanceof TimeInstantType) {
            return parseTimeInstant((TimeInstantType) xmlObject);
        } else if (xmlObject instanceof TimePeriodType) {
            return parseTimePeriod((TimePeriodType) xmlObject);
        } else if (xmlObject instanceof TimeInstantDocument) {
            return parseTimeInstant(((TimeInstantDocument) xmlObject).getTimeInstant());
        } else if (xmlObject instanceof TimePeriodDocument) {
            return parseTimePeriod(((TimePeriodDocument) xmlObject).getTimePeriod());
        } else if (xmlObject instanceof ReferenceType) {
            return parseReferenceType((ReferenceType) xmlObject);
        } else if (xmlObject instanceof MeasureType) {
            return parseMeasureType((MeasureType) xmlObject);
        } else if (xmlObject instanceof PointType) {
            return parsePointType((PointType) xmlObject);
        } else if (xmlObject instanceof PointDocument) {
            return parsePointType(((PointDocument) xmlObject).getPoint());
        } else if (xmlObject instanceof LineStringType) {
            return parseLineStringType((LineStringType) xmlObject);
        } else if (xmlObject instanceof PolygonType) {
            return parsePolygonType((PolygonType) xmlObject);
        } else if (xmlObject instanceof CompositeSurfaceType) {
            return parseCompositeSurfaceType((CompositeSurfaceType) xmlObject);
        } else if (xmlObject instanceof CodeWithAuthorityType) {
            return parseCodeWithAuthorityTye((CodeWithAuthorityType) xmlObject);
        } else if (xmlObject instanceof CodeType) {
            return parseCodeType((CodeType) xmlObject);
        } else if (xmlObject instanceof GeometryPropertyType) {
            return parseGeometryPropertyType((GeometryPropertyType) xmlObject);
        } else if (xmlObject instanceof VerticalDatumPropertyType) {
            return parseVerticalDatumPropertyType((VerticalDatumPropertyType) xmlObject);
        } else {
            throw new UnsupportedDecoderXmlInputException(this, xmlObject);
        }
    }

    private Object parseFeaturePropertyType(FeaturePropertyType featurePropertyType) throws DecodingException {
        SamplingFeature feature = null;
        // if xlink:href is set
        if (featurePropertyType.getHref() != null) {
            if (featurePropertyType.getHref().startsWith("#")) {
                feature = new SamplingFeature(null, featurePropertyType.getHref().replace("#", ""));
            } else {
                feature = new SamplingFeature(new CodeWithAuthority(featurePropertyType.getHref()));
                if (featurePropertyType.getTitle() != null && !featurePropertyType.getTitle().isEmpty()) {
                    feature.addName(new org.n52.shetland.ogc.gml.CodeType(featurePropertyType.getTitle()));
                }
            }
            feature.setGmlId(featurePropertyType.getHref());
        }
        // if feature is encoded
        else {
            XmlObject abstractFeature = null;
            if (featurePropertyType.getAbstractFeature() != null) {
                abstractFeature = featurePropertyType.getAbstractFeature();
            } else if (featurePropertyType.getDomNode().hasChildNodes()) {
                try {
                    abstractFeature =
                            XmlObject.Factory.parse(XmlHelper.getNodeFromNodeList(featurePropertyType.getDomNode()
                                    .getChildNodes()));
                } catch (XmlException xmle) {
                    throw new DecodingException("Error while parsing feature request!", xmle);
                }
            }
            if (abstractFeature != null) {
                Object decodedObject = decodeXmlObject(abstractFeature);
                if (decodedObject instanceof SamplingFeature) {
                    feature = (SamplingFeature) decodedObject;
                } else {
                    throw new DecodingException(Sos2Constants.InsertObservationParams.observation,
                            "The requested featurePropertyType type is not supported by this service!");
                }
            }
        }
        if (feature == null) {
            throw new DecodingException(Sos2Constants.InsertObservationParams.observation,
                    "The requested featurePropertyType type is not supported by this service!");
        }
        return feature;
    }

    /**
     * parses the BBOX element of the featureOfInterest element contained in the
     * GetObservation request and returns a String representing the BOX in
     * Well-Known-Text format
     *
     * @param envelopeDocument
     *            XmlBean representing the BBOX-element in the request
     * @return Returns WKT-String representing the BBOX as Multipoint with two
     *         elements
     *
     *
     * @throws DecodingException
     *             * if parsing the BBOX element failed
     */
    private Geometry parseEnvelope(EnvelopeDocument envelopeDocument) throws DecodingException {
        EnvelopeType envelopeType = envelopeDocument.getEnvelope();
        int srid = CRSHelper.parseSrsName(envelopeType.getSrsName());
        if (srid <= 0) {
            srid = GeometryHandler.getInstance().getStorageEPSG();
        }
        String lowerCorner = envelopeType.getLowerCorner().getStringValue();
        String upperCorner = envelopeType.getUpperCorner().getStringValue();
        return JTSHelper.createGeometryFromWKT(JTSHelper.createWKTPolygonFromEnvelope(lowerCorner, upperCorner), srid);
    }

    /**
     * parses TimeInstant
     *
     * @param xbTimeIntant
     *            XmlBean representation of TimeInstant
     * @return Returns a TimeInstant created from the TimeInstantType
     * @throws DecodingException
     */
    private Object parseTimeInstant(TimeInstantType xbTimeIntant) throws DecodingException {
        TimeInstant ti = parseTimePosition(xbTimeIntant.getTimePosition());
        ti.setGmlId(xbTimeIntant.getId());
        return ti;
    }

    /**
     * creates SOS representation of time period from XMLBeans representation of
     * time period
     *
     * @param xbTimePeriod
     *            XMLBeans representation of time period
     * @return Returns SOS representation of time period
     *
     *
     * @throws DecodingException
     */
    private Object parseTimePeriod(TimePeriodType xbTimePeriod) throws DecodingException {
        // begin position
        TimePositionType xbBeginTPT = xbTimePeriod.getBeginPosition();
        TimeInstant begin = null;
        if (xbBeginTPT != null) {
            begin = parseTimePosition(xbBeginTPT);
        } else {
            throw new DecodingException("gml:TimePeriod must contain gml:beginPosition Element with valid ISO:8601 String!");
        }

        // end position
        TimePositionType xbEndTPT = xbTimePeriod.getEndPosition();
        TimeInstant end = null;
        if (xbEndTPT != null) {
            end = parseTimePosition(xbEndTPT);
        } else {
            throw new DecodingException("gml:TimePeriod must contain gml:endPosition Element with valid ISO:8601 String!");
        }
        TimePeriod timePeriod = new TimePeriod(begin, end);
        timePeriod.setGmlId(xbTimePeriod.getId());
        return timePeriod;
    }

    private TimeInstant parseTimePosition(TimePositionType xbTimePosition) throws DecodingException {
        TimeInstant ti = new TimeInstant();
        String timeString = xbTimePosition.getStringValue();
        if (timeString != null && !timeString.isEmpty()) {
            try {
                // TODO better differentiate between ISO8601 and indeterminate value
                ti.setValue(DateTimeHelper.parseIsoString2DateTime(timeString));
                ti.setRequestedTimeLength(DateTimeHelper.getTimeLengthBeforeTimeZone(timeString));
            } catch (DateTimeParseException ex) {
                ti.setIndeterminateValue(new IndeterminateValue(timeString));
            }
        }

        if (xbTimePosition.isSetIndeterminatePosition()) {
            ti.setIndeterminateValue(new IndeterminateValue(xbTimePosition.getIndeterminatePosition().toString()));
        }

        return ti;
    }

    private GmlMeasureType parseMeasureType(MeasureType measureType) {
        GmlMeasureType sosMeasureType = new GmlMeasureType(measureType.getDoubleValue());
        sosMeasureType.setUnit(measureType.getUom());
        return sosMeasureType;
    }

    private Object parseGeometryPropertyType(GeometryPropertyType geometryPropertyType) throws DecodingException {
        return parseAbstractGeometryType(geometryPropertyType.getAbstractGeometry());
    }

    private AbstractGeometry parseAbstractGeometryType(AbstractGeometryType agt) throws DecodingException {
        AbstractGeometry abstractGeometry = new AbstractGeometry(agt.getId());
        parseAbstractGMLType(agt, abstractGeometry);
        abstractGeometry.setGeometry((Geometry)decode(agt));
        return abstractGeometry;
    }

    private Object parsePointType(PointType xbPointType) throws DecodingException {

        String geomWKT = null;
        int srid = -1;
        if (xbPointType.getSrsName() != null) {
            srid = CRSHelper.parseSrsName(xbPointType.getSrsName());
        }

        if (xbPointType.getPos() != null) {
            DirectPositionType xbPos = xbPointType.getPos();
            if (srid == -1 && xbPos.getSrsName() != null) {
                srid = CRSHelper.parseSrsName(xbPos.getSrsName());
            }
            String directPosition = getString4Pos(xbPos);
            geomWKT = "POINT(" + directPosition + ")";
        } else if (xbPointType.getCoordinates() != null) {
            CoordinatesType xbCoords = xbPointType.getCoordinates();
            String directPosition = getString4Coordinates(xbCoords);
            geomWKT = "POINT" + directPosition;
        } else {
            throw new DecodingException("For geometry type 'gml:Point' only element "
                    + "'gml:pos' and 'gml:coordinates' are allowed " + "in the feature of interest parameter!");
        }

        checkSrid(srid);
        if (srid == -1) {
            throw new DecodingException("No SrsName ist specified for geometry!");
        }

        return JTSHelper.createGeometryFromWKT(geomWKT, srid);
    }

    private Object parseLineStringType(LineStringType xbLineStringType) throws DecodingException {
        int srid = -1;
        if (xbLineStringType.getSrsName() != null) {
            srid = CRSHelper.parseSrsName(xbLineStringType.getSrsName());
        }

        DirectPositionType[] xbPositions = xbLineStringType.getPosArray();

        StringBuilder positions = new StringBuilder();
        if (xbPositions != null && xbPositions.length > 0) {
            if (srid == -1 && xbPositions[0].getSrsName() != null && !(xbPositions[0].getSrsName().isEmpty())) {
                srid = CRSHelper.parseSrsName(xbPositions[0].getSrsName());
            }
            positions.append(getString4PosArray(xbLineStringType.getPosArray()));
        }
        String geomWKT = "LINESTRING" + positions.toString() + "";

        checkSrid(srid);

        return JTSHelper.createGeometryFromWKT(geomWKT, srid);
    }

    private Object parsePolygonType(PolygonType xbPolygonType) throws DecodingException {
        int srid = -1;
        if (xbPolygonType.getSrsName() != null) {
            srid = CRSHelper.parseSrsName(xbPolygonType.getSrsName());
        }
        String exteriorCoordString = null;
        StringBuilder geomWKT = new StringBuilder();
        StringBuilder interiorCoordString = new StringBuilder();

        AbstractRingPropertyType xbExterior = xbPolygonType.getExterior();

        if (xbExterior != null) {
            AbstractRingType xbExteriorRing = xbExterior.getAbstractRing();
            if (xbExteriorRing instanceof LinearRingType) {
                LinearRingType xbLinearRing = (LinearRingType) xbExteriorRing;
                exteriorCoordString = getCoordString4LinearRing(xbLinearRing);
            } else {
                throw new DecodingException("The Polygon must contain the following elements "
                        + "<gml:exterior><gml:LinearRing><gml:posList>!");
            }
        }

        AbstractRingPropertyType[] xbInterior = xbPolygonType.getInteriorArray();
        AbstractRingPropertyType xbInteriorRing;
        if (xbInterior != null && xbInterior.length != 0) {
            for (int i = 0; i < xbInterior.length; i++) {
                xbInteriorRing = xbInterior[i];
                if (xbInteriorRing instanceof LinearRingType) {
                    interiorCoordString.append(", ")
                            .append(getCoordString4LinearRing((LinearRingType) xbInteriorRing));
                }
            }
        }

        geomWKT.append("POLYGON(");
        geomWKT.append(exteriorCoordString);
        geomWKT.append(interiorCoordString);
        geomWKT.append(")");

        checkSrid(srid);
        return JTSHelper.createGeometryFromWKT(geomWKT.toString(), srid);
    }

    private Geometry parseCompositeSurfaceType(CompositeSurfaceType xbCompositeSurface) throws DecodingException {
        SurfacePropertyType[] xbCurfaceProperties = xbCompositeSurface.getSurfaceMemberArray();
        int srid = -1;
        ArrayList<Polygon> polygons = new ArrayList<>(xbCurfaceProperties.length);
        if (xbCompositeSurface.getSrsName() != null) {
            srid = CRSHelper.parseSrsName(xbCompositeSurface.getSrsName());
        }
        for (SurfacePropertyType xbSurfaceProperty : xbCurfaceProperties) {
            AbstractSurfaceType xbAbstractSurface = xbSurfaceProperty.getAbstractSurface();
            if (srid == -1 && xbAbstractSurface.getSrsName() != null) {
                srid = CRSHelper.parseSrsName(xbAbstractSurface.getSrsName());
            }
            if (xbAbstractSurface instanceof PolygonType) {
                polygons.add((Polygon) parsePolygonType((PolygonType) xbAbstractSurface));
            } else {
                throw new DecodingException("The FeatureType %s is not supportted! Only PolygonType", xbAbstractSurface);
            }
        }
        if (polygons.isEmpty()) {
            throw new DecodingException("The FeatureType: %s does not contain any member!", xbCompositeSurface);
        }
        checkSrid(srid);
        GeometryFactory factory = new GeometryFactory();
        Geometry geom = factory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
        geom.setSRID(srid);
        return geom;
    }

    private org.n52.shetland.ogc.gml.ReferenceType parseVerticalDatumPropertyType(VerticalDatumPropertyType vdpt) {
        // TODO parse VerticalDatumType
        if (vdpt.isSetHref() && !vdpt.getHref().isEmpty()) {
            org.n52.shetland.ogc.gml.ReferenceType referenceType =
                    new org.n52.shetland.ogc.gml.ReferenceType(vdpt.getHref());
            if (vdpt.isSetTitle() && !vdpt.getTitle().isEmpty()) {
                referenceType.setTitle(vdpt.getTitle());
            }
            return referenceType;
        }
        return new org.n52.shetland.ogc.gml.ReferenceType("UNKNOWN");
    }

    /**
     * method parses the passed linearRing(generated thru XmlBEans) and returns
     * a string containing the coordinate values of the passed ring
     *
     * @param xbLinearRing
     *            linearRing(generated thru XmlBEans)
     * @return Returns a string containing the coordinate values of the passed
     *         ring
     *
     *
     * @throws DecodingException
     *             * if parsing the linear Ring failed
     */
    private String getCoordString4LinearRing(LinearRingType xbLinearRing) throws DecodingException {

        String result = "";
        DirectPositionListType xbPosList = xbLinearRing.getPosList();
        CoordinatesType xbCoordinates = xbLinearRing.getCoordinates();
        DirectPositionType[] xbPosArray = xbLinearRing.getPosArray();
        if (xbPosList != null && !(xbPosList.getStringValue().isEmpty())) {
            result = getString4PosList(xbPosList);
        } else if (xbCoordinates != null && !(xbCoordinates.getStringValue().isEmpty())) {
            result = getString4Coordinates(xbCoordinates);
        } else if (xbPosArray != null && xbPosArray.length > 0) {
            result = getString4PosArray(xbPosArray);
        } else {
            throw new DecodingException("The Polygon must contain the following elements "
                    + "<gml:exterior><gml:LinearRing><gml:posList>, "
                    + "<gml:exterior><gml:LinearRing><gml:coordinates> "
                    + "or <gml:exterior><gml:LinearRing><gml:pos>{<gml:pos>}!");
        }

        return result;
    }// end getCoordStrig4LinearRing

    /**
     * parses XmlBeans DirectPosition to a String with coordinates for WKT.
     *
     * @param xbPos
     *            XmlBeans generated DirectPosition.
     * @return Returns String with coordinates for WKT.
     */
    private String getString4Pos(DirectPositionType xbPos) {
        return xbPos.getStringValue();
    }

    /**
     * parses XmlBeans DirectPosition[] to a String with coordinates for WKT.
     *
     * @param xbPosArray
     *            XmlBeans generated DirectPosition[].
     * @return Returns String with coordinates for WKT.
     */
    private String getString4PosArray(DirectPositionType[] xbPosArray) {
        StringBuilder coordinateString = new StringBuilder();
        coordinateString.append("(");
        for (DirectPositionType directPositionType : xbPosArray) {
            coordinateString.append(directPositionType.getStringValue());
            coordinateString.append(", ");
        }
        coordinateString.append(xbPosArray[0].getStringValue());
        coordinateString.append(")");

        return coordinateString.toString();
    }

    /**
     * parses XmlBeans DirectPositionList to a String with coordinates for WKT.
     *
     * @param xbPosList
     *            XmlBeans generated DirectPositionList.
     * @return Returns String with coordinates for WKT.
     *
     *
     * @throws DecodingException
     */
    private String getString4PosList(DirectPositionListType xbPosList) throws DecodingException {
        StringBuilder coordinateString = new StringBuilder("(");
        List<?> values = xbPosList.getListValue();
        if ((values.size() % 2) != 0) {
            throw new DecodingException("The Polygons posList must contain pairs of coordinates!");
        } else {
            for (int i = 0; i < values.size(); i++) {
                coordinateString.append(values.get(i));
                if ((i % 2) != 0) {
                    coordinateString.append(", ");
                } else {
                    coordinateString.append(" ");
                }
            }
        }
        int length = coordinateString.length();
        coordinateString.delete(length - 2, length);
        coordinateString.append(")");

        return coordinateString.toString();
    }

    /**
     * parses XmlBeans Coordinates to a String with coordinates for WKT.
     * Replaces cs, decimal and ts if different from default.
     *
     * @param xbCoordinates
     *            XmlBeans generated Coordinates.
     * @return Returns String with coordinates for WKT.
     */
    private String getString4Coordinates(CoordinatesType xbCoordinates) {
        String coordinateString = "(" + xbCoordinates.getStringValue() + ")";

        // replace cs, decimal and ts if different from default.
        if (!xbCoordinates.getCs().equals(CS)) {
            coordinateString = coordinateString.replace(xbCoordinates.getCs(), CS);
        }
        if (!xbCoordinates.getDecimal().equals(DECIMAL)) {
            coordinateString = coordinateString.replace(xbCoordinates.getDecimal(), DECIMAL);
        }
        if (!xbCoordinates.getTs().equals(TS)) {
            coordinateString = coordinateString.replace(xbCoordinates.getTs(), TS);
        }

        return coordinateString;
    }

    private void checkSrid(int srid) throws DecodingException {
        if (srid == 0 || srid == -1) {
            throw new DecodingException("No SrsName is specified for geometry!");
        }
    }
}
