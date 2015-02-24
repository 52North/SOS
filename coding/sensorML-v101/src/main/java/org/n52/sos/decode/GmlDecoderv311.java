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
package org.n52.sos.decode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.opengis.gml.CodeType;
import net.opengis.gml.CoordinatesType;
import net.opengis.gml.DirectPositionType;
import net.opengis.gml.EnvelopeDocument;
import net.opengis.gml.EnvelopeType;
import net.opengis.gml.PointType;
import net.opengis.gml.TimeInstantDocument;
import net.opengis.gml.TimeInstantType;
import net.opengis.gml.TimePeriodDocument;
import net.opengis.gml.TimePeriodType;
import net.opengis.gml.TimePositionType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.SosHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @since 4.0.0
 * 
 */
public class GmlDecoderv311 implements Decoder<Object, XmlObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GmlDecoderv311.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(GmlConstants.NS_GML,
            EnvelopeDocument.class, TimeInstantType.class, TimePeriodType.class, TimeInstantDocument.class,
            TimePeriodDocument.class, CodeType.class, PointType.class);

    private static final String CS = ",";

    private static final String DECIMAL = ".";

    private static final String TS = " ";

    public GmlDecoderv311() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Object decode(XmlObject xmlObject) throws OwsExceptionReport {
        if (xmlObject instanceof EnvelopeDocument) {
            return getGeometry4BBOX((EnvelopeDocument) xmlObject);
        } else if (xmlObject instanceof TimeInstantType) {
            return parseTimeInstant((TimeInstantType) xmlObject);
        } else if (xmlObject instanceof TimePeriodType) {
            return parseTimePeriod((TimePeriodType) xmlObject);
        } else if (xmlObject instanceof TimeInstantDocument) {
            return parseTimeInstant(((TimeInstantDocument) xmlObject).getTimeInstant());
        } else if (xmlObject instanceof TimePeriodDocument) {
            return parseTimePeriod(((TimePeriodDocument) xmlObject).getTimePeriod());
        } else if (xmlObject instanceof CodeType) {
            return parseCodeType((CodeType) xmlObject);
        } else if (xmlObject instanceof PointType) {
            return parsePointType((PointType) xmlObject);
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }

    private Geometry getGeometry4BBOX(EnvelopeDocument xbBbox) throws OwsExceptionReport {
        EnvelopeType xbEnvelope = xbBbox.getEnvelope();
        // parse srid; if not set, throw exception!
        int srid = SosHelper.parseSrsName(xbEnvelope.getSrsName());
        String lower = xbEnvelope.getLowerCorner().getStringValue();
        String upper = xbEnvelope.getUpperCorner().getStringValue();
        String geomWKT = String.format("MULTIPOINT(%s, %s)", lower, upper);
        return JTSHelper.createGeometryFromWKT(geomWKT, srid).getEnvelope();
    }

    private Object parseTimePeriod(TimePeriodType xbTimePeriod) throws OwsExceptionReport {
        // begin position
        TimePositionType xbBeginTPT = xbTimePeriod.getBeginPosition();
        TimeInstant begin = null;
        if (xbBeginTPT != null) {
            begin = parseTimePosition(xbBeginTPT);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("gml:TimePeriod must contain gml:beginPosition Element with valid ISO:8601 String!");
        }

        // end position
        TimePositionType xbEndTPT = xbTimePeriod.getEndPosition();
        TimeInstant end = null;
        if (xbEndTPT != null) {
            end = parseTimePosition(xbEndTPT);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("gml:TimePeriod must contain gml:endPosition Element with valid ISO:8601 String!");
        }
        TimePeriod timePeriod = new TimePeriod(begin, end);
        timePeriod.setGmlId(xbTimePeriod.getId());
        return timePeriod;
    }

    private Object parseTimeInstant(TimeInstantType xbTimeIntant) throws OwsExceptionReport {
        TimeInstant ti = parseTimePosition(xbTimeIntant.getTimePosition());
        ti.setGmlId(xbTimeIntant.getId());
        return ti;
    }

    private TimeInstant parseTimePosition(TimePositionType xbTimePosition) throws OwsExceptionReport {
        TimeInstant ti = new TimeInstant();
        String timeString = xbTimePosition.getStringValue();
        if (timeString != null && !timeString.isEmpty()) {
            if ((SosIndeterminateTime.contains(timeString))) {
                ti.setSosIndeterminateTime(SosIndeterminateTime.getEnumForString(timeString));
            } else {
                ti.setValue(DateTimeHelper.parseIsoString2DateTime(timeString));
                ti.setRequestedTimeLength(DateTimeHelper.getTimeLengthBeforeTimeZone(timeString));
            }
        }
        if (xbTimePosition.getIndeterminatePosition() != null) {
            ti.setIndeterminateValue(TimeIndeterminateValue.getEnumForString(xbTimePosition.getIndeterminatePosition()
                    .toString()));
        }

        return ti;
    }

    private org.n52.sos.ogc.gml.CodeType parseCodeType(CodeType element) {
        org.n52.sos.ogc.gml.CodeType codeType = new org.n52.sos.ogc.gml.CodeType(element.getStringValue());
        if (element.isSetCodeSpace()) {
            codeType.setCodeSpace(element.getCodeSpace());
        }
        return codeType;
    }

    private Object parsePointType(PointType xbPointType) throws OwsExceptionReport {
        String geomWKT = null;
        int srid = -1;
        if (xbPointType.getSrsName() != null) {
            srid = SosHelper.parseSrsName(xbPointType.getSrsName());
        }

        if (xbPointType.getPos() != null) {
            DirectPositionType xbPos = xbPointType.getPos();
            if (srid == -1 && xbPos.getSrsName() != null) {
                srid = SosHelper.parseSrsName(xbPos.getSrsName());
            }
            String directPosition = getString4Pos(xbPos);
            geomWKT = JTSHelper.createWKTPointFromCoordinateString(directPosition);
        } else if (xbPointType.getCoordinates() != null) {
            CoordinatesType xbCoords = xbPointType.getCoordinates();
            String directPosition = getString4Coordinates(xbCoords);
            geomWKT = JTSHelper.createWKTPointFromCoordinateString(directPosition);
        } else {
            throw new NoApplicableCodeException().withMessage("For geometry type 'gml:Point' only elements "
                    + "'gml:pos' and 'gml:coordinates' are allowed");
        }

        checkSrid(srid);
        if (srid == -1) {
            throw new NoApplicableCodeException().withMessage("No SrsName ist specified for geometry!");
        }

        return JTSHelper.createGeometryFromWKT(geomWKT, srid);
    }

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
     * parses XmlBeans Coordinates to a String with coordinates for WKT.
     * Replaces cs, decimal and ts if different from default.
     * 
     * @param xbCoordinates
     *            XmlBeans generated Coordinates.
     * @return Returns String with coordinates for WKT.
     */
    private String getString4Coordinates(CoordinatesType xbCoordinates) {
        String coordinateString = xbCoordinates.getStringValue();

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

    private void checkSrid(int srid) throws OwsExceptionReport {
        if (srid == 0 || srid == -1) {
            throw new NoApplicableCodeException().withMessage("No SrsName is specified for geometry!");
        }
    }
}
