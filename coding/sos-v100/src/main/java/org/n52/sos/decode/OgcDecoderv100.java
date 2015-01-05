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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.opengis.ogc.BBOXType;
import net.opengis.ogc.BinarySpatialOpType;
import net.opengis.ogc.BinaryTemporalOpType;
import net.opengis.ogc.PropertyNameDocument;
import net.opengis.ogc.PropertyNameType;
import net.opengis.ogc.SpatialOperatorType;
import net.opengis.ogc.TemporalOperatorType;
import net.opengis.ogc.impl.BBOXTypeImpl;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.google.common.base.Joiner;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @since 4.0.0
 * 
 */
public class OgcDecoderv100 implements Decoder<Object, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OgcDecoderv100.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(OGCConstants.NS_OGC,
            SpatialOperatorType.class, TemporalOperatorType.class, BinarySpatialOpType.class,
            BinaryTemporalOpType.class, BBOXType.class, PropertyNameDocument.class);

    public OgcDecoderv100() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return new HashSet<String>(0);
    }

    @Override
    public Object decode(XmlObject xmlObject) throws OwsExceptionReport {
        // validate document

        // FIXME Validation currently fails against abstract types
        // XmlHelper.validateDocument(xmlObject);

        if (xmlObject instanceof BinaryTemporalOpType) {
            return parseTemporalOperatorType((BinaryTemporalOpType) xmlObject);
        }
        if (xmlObject instanceof TemporalOperatorType) {
            throw new InvalidParameterValueException().at(Sos1Constants.GetObservationParams.eventTime).withMessage(
                    "The requested temporal filter operand is not supported by this SOS!");
        }
        // add propertyNameDoc here
        if (xmlObject instanceof PropertyNameDocument) {
            PropertyNameDocument xbPropertyNameDoc = ((PropertyNameDocument) xmlObject);
            return xbPropertyNameDoc.getPropertyName();
        }
        // add BBOXType here
        if (xmlObject instanceof BinarySpatialOpType) {
            return parseSpatialOperatorType((BinarySpatialOpType) xmlObject);
        }
        if (xmlObject instanceof BBOXType) {
            return parseBBOXFilterType((BBOXTypeImpl) xmlObject);
        }
        if (xmlObject instanceof BBOXTypeImpl) {
            return parseBBOXFilterType((BBOXTypeImpl) xmlObject);
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
        // TODO more spatial filters (contains, intersects, overlaps Point
        // Linestring Polygon, not supported by this SOS yet
        // return error message
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    /**
     * parses a single temporal filter of the requests and returns SOS temporal
     * filter
     * 
     * @param xbBinaryTemporalOp
     *            XmlObject representing the temporal filter
     * @return Returns SOS representation of temporal filter
     * 
     * 
     * @throws OwsExceptionReport
     *             if parsing of the element failed
     */
    private Object parseTemporalOperatorType(BinaryTemporalOpType xbBinaryTemporalOp) throws OwsExceptionReport {

        TemporalFilter temporalFilter = new TemporalFilter();
        // FIXME local workaround against SOSHelper check value reference
        String valueRef = "phenomenonTime";
        try {

            NodeList nodes = xbBinaryTemporalOp.getDomNode().getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {

                if (nodes.item(i).getNamespaceURI() != null
                        && !nodes.item(i).getLocalName().equals(FilterConstants.EN_VALUE_REFERENCE)) {
                    // GML decoder will return TimeInstant or TimePriod
                    Object timeObject = CodingHelper.decodeXmlElement(XmlObject.Factory.parse(nodes.item(i)));

                    if (timeObject instanceof PropertyNameType) {
                        PropertyNameType propType = (PropertyNameType) timeObject;

                        // TODO here apply logic for ogc property
                        // om:samplingTime etc
                        // valueRef = propType.getDomNode().getNodeValue();

                    }

                    if (timeObject instanceof Time) {
                        TimeOperator operator;
                        Time time = (Time) timeObject;
                        String localName = XmlHelper.getLocalName(xbBinaryTemporalOp);
                        // change to SOS 1.0. TMDuring kind of
                        if (localName.equals(TimeOperator.TM_During.name()) && time instanceof TimePeriod) {
                            operator = TimeOperator.TM_During;
                        } else if (localName.equals(TimeOperator.TM_Equals.name()) && time instanceof TimeInstant) {
                            operator = TimeOperator.TM_Equals;
                        } else if (localName.equals(TimeOperator.TM_After.name()) && time instanceof TimeInstant) {
                            operator = TimeOperator.TM_After;
                        } else if (localName.equals(TimeOperator.TM_Before.name()) && time instanceof TimeInstant) {
                            operator = TimeOperator.TM_Before;
                        } else {
                            throw new InvalidParameterValueException()
                                    .at(Sos1Constants.GetObservationParams.eventTime).withMessage(
                                            "The requested temporal filter operand is not supported by this SOS!");
                        }
                        temporalFilter.setOperator(operator);
                        temporalFilter.setTime(time);
                        // actually it should be eg om:samplingTime
                        temporalFilter.setValueReference(valueRef);
                        break;
                    }
                }
            }

        } catch (XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage("Error while parsing temporal filter!");
        }
        return temporalFilter;

    }

    /**
     * Parses the spatial filter of a request.
     * 
     * @param xbBBOX
     *            XmlBean representing the feature of interest parameter of the
     *            request
     * @return Returns SpatialFilter created from the passed foi request
     *         parameter
     * 
     * 
     * @throws OwsExceptionReport
     *             * if creation of the SpatialFilter failed
     */
    private SpatialFilter parseBBOXFilterType(BBOXTypeImpl xbBBOX) throws OwsExceptionReport {

        SpatialFilter spatialFilter = new SpatialFilter();
        // FIXME local workaround for SOSHelper check value reference
        String valueRef = "om:featureOfInterest/sams:SF_SpatialSamplingFeature/sams:shape";
        try {

            spatialFilter.setOperator(FilterConstants.SpatialOperator.BBOX);
            XmlCursor geometryCursor = xbBBOX.newCursor();
            if (geometryCursor.toChild(GmlConstants.QN_ENVELOPE)) {
                Object sosGeometry =
                        CodingHelper.decodeXmlElement(XmlObject.Factory.parse(geometryCursor.getDomNode()));

                if (sosGeometry instanceof PropertyNameType) {
                    PropertyNameType propType = (PropertyNameType) sosGeometry;

                    // TODO here apply logic for ogc property
                    // urn:ogc:data:location etc
                    // valueRef = propType.getDomNode().getNodeValue();

                }

                if (sosGeometry instanceof Geometry) {
                    spatialFilter.setGeometry((Geometry) sosGeometry);
                    spatialFilter.setValueReference(valueRef);
                }

            } else {
                throw new InvalidParameterValueException().at("FeatureOfInterest Filter").withMessage(
                        "The requested spatial filter operand is not supported by this SOS!");
            }
            geometryCursor.dispose();

        } catch (XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage("Error while parsing spatial filter!");
        }
        return spatialFilter;
    }

    private Object parseSpatialOperatorType(BinarySpatialOpType xbSpatialOpsType) throws OwsExceptionReport {
        SpatialFilter spatialFilter = new SpatialFilter();
        try {
            if (xbSpatialOpsType instanceof BBOXTypeImpl) {
                spatialFilter.setOperator(FilterConstants.SpatialOperator.BBOX);
                BBOXTypeImpl xbBBOX = (BBOXTypeImpl) xbSpatialOpsType;
                spatialFilter.setOperator(FilterConstants.SpatialOperator.BBOX);
                XmlCursor geometryCursor = xbBBOX.newCursor();
                if (geometryCursor.toChild(GmlConstants.QN_ENVELOPE)) {
                    Object sosGeometry =
                            CodingHelper.decodeXmlElement(XmlObject.Factory.parse(geometryCursor.getDomNode()));
                    if (sosGeometry instanceof Geometry) {
                        spatialFilter.setGeometry((Geometry) sosGeometry);
                    }

                } else {
                    throw new InvalidParameterValueException().at(Sos2Constants.GetObservationParams.spatialFilter)
                            .withMessage("The requested spatial filter operand is not supported by this SOS!");
                }
                geometryCursor.dispose();
            } else {
                throw new InvalidParameterValueException().at("GetFeatureOfInterest Filter").withMessage(
                        "The requested spatial filter is not supported by this SOS!");
            }
        } catch (XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage("Error while parsing spatial filter!");
        }
        return spatialFilter;
    }
}
