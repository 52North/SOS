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
package org.n52.svalbard.cv.v02.encode;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.encode.Encoder;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.cv.CvConstants;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.PointValuePair;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityRangeValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.JavaHelper;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Point;

import net.opengis.cv.x02.gml32.CVDiscretePointCoverageType;
import net.opengis.cv.x02.gml32.CVPointValuePairPropertyType;
import net.opengis.cv.x02.gml32.CVPointValuePairType;
import net.opengis.gml.x32.PointPropertyType;
import net.opengis.gml.x32.PointType;

/**
 * Abstract {@link Encoder} class for CV_DiscretePointCoverage
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 * @param <T>
 */
public abstract class AbstractCVDiscretePointCoverageTypeEncoder<T>
        extends AbstractSpecificXmlEncoder<T, CvDiscretePointCoverage> {

    /**
     * Encode {@link CvDiscretePointCoverage} to
     * {@link CVDiscretePointCoverageType}
     * 
     * @param cvDiscretePointCoverage
     *            The {@link CvDiscretePointCoverage} to encode
     * @return The encoded {@link CvDiscretePointCoverage}
     * @throws UnsupportedEncoderInputException
     *             If an element can not be encoded
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected CVDiscretePointCoverageType encodeCVDiscretePointCoverage(
            CvDiscretePointCoverage cvDiscretePointCoverage)
                    throws UnsupportedEncoderInputException, OwsExceptionReport {
        CVDiscretePointCoverageType cvdpct = CVDiscretePointCoverageType.Factory.newInstance(getXmlOptions());
        cvdpct.setId(cvDiscretePointCoverage.getGmlId());
        cvdpct.addNewDomainExtent().setHref(cvDiscretePointCoverage.getDomainExtent());
        cvdpct.addNewRangeType().setHref(cvDiscretePointCoverage.getRangeType().getHref());
        CVPointValuePairPropertyType cvpvppt = cvdpct.addNewElement();
        cvpvppt.setCVPointValuePair(encodePointValuePair(cvDiscretePointCoverage.getValue()));
        return cvdpct;
    }

    /**
     * Encode {@link PointValuePair} to {@link CVPointValuePairType}
     * 
     * @param value
     *            The {@link PointValuePair} to encode
     * @return The encoded {@link PointValuePair}
     * @throws UnsupportedEncoderInputException
     *             If an element can not be encoded
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private CVPointValuePairType encodePointValuePair(PointValuePair value)
            throws UnsupportedEncoderInputException, OwsExceptionReport {
        CVPointValuePairType cvpvpt = CVPointValuePairType.Factory.newInstance(getXmlOptions());
        cvpvpt.setGeometry(encodeGeometry(value.getPoint(), JavaHelper.generateID(value.toString())));
        if (value.isSetValue()) {
            cvpvpt.setValue(encodeValue(value.getValue()));
        } else {
            cvpvpt.addNewValue();
        }
        return cvpvpt;
    }

    /**
     * Encode {@link Point} to {@link PointPropertyType}
     * 
     * @param point
     *            The {@link Point} to encode
     * @param gmlId
     *            The gml id for the point
     * @return The encoded {@link Point}
     * @throws UnsupportedEncoderInputException
     *             If an element can not be encoded
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private PointPropertyType encodeGeometry(Point point, String gmlId)
            throws UnsupportedEncoderInputException, OwsExceptionReport {
        Map<HelperValues, String> additionalValues = Maps.newHashMap();
        additionalValues.put(HelperValues.GMLID, gmlId);
        PointPropertyType ppt = PointPropertyType.Factory.newInstance(getXmlOptions());
        ppt.setPoint((PointType) encodeGML(point, additionalValues));
        return ppt;
    }

    /**
     * Encode {@link Value} to an {@link XmlObject}
     * 
     * @param value
     *            The {@link Value} to encode
     * @return The encoded {@link Value}
     * @throws UnsupportedEncoderInputException
     *             If an element can not be encoded
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private XmlObject encodeValue(Value<?> value) throws UnsupportedEncoderInputException, OwsExceptionReport {
        return value.accept(new ResultValueVisitor());
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(CvConstants.NS_CV, CvConstants.NS_CV_PREFIX);
    }

    protected static XmlObject encodeGML(Object o, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o, additionalValues);
    }

    protected static XmlObject encodeGML(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o);
    }

    protected static XmlObject encodeSWE(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o);
    }

    protected static XmlObject encodeSWE(Object o, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o, additionalValues);
    }

    protected static XmlString createXmlString() {
        return XmlString.Factory.newInstance(getXmlOptions());
    }

    protected static XmlInteger createXmlInteger() {
        return XmlInteger.Factory.newInstance(getXmlOptions());
    }

    protected static XmlBoolean createXmlBoolean() {
        return XmlBoolean.Factory.newInstance(getXmlOptions());
    }

    /**
     * {@link ValueVisitor} implementation for the result
     * 
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    private static class ResultValueVisitor implements ValueVisitor<XmlObject> {

        ResultValueVisitor() {
        }

        @Override
        public XmlObject visit(BooleanValue value) throws OwsExceptionReport {
            XmlBoolean xbBoolean = createXmlBoolean();
            if (value.isSetValue()) {
                xbBoolean.setBooleanValue(value.getValue());
            } else {
                xbBoolean.setNil();
            }
            return xbBoolean;
        }

        @Override
        public XmlObject visit(CategoryValue value) throws OwsExceptionReport {
            if (value.isSetValue() && !value.getValue().isEmpty()) {
                return encodeGML(value);
            }
            return null;
        }

        @Override
        public XmlObject visit(ComplexValue value) throws OwsExceptionReport {

            if (value.isSetValue()) {
                Map<HelperValues, String> additionalValue = new EnumMap<>(HelperValues.class);
                additionalValue.put(HelperValues.FOR_OBSERVATION, null);
                return encodeSWE(value.getValue(), additionalValue);
            }
            return null;
        }

        @Override
        public XmlObject visit(CountValue value) throws OwsExceptionReport {
            XmlInteger xbInteger = createXmlInteger();
            if (value.isSetValue() && value.getValue() != Integer.MIN_VALUE) {
                xbInteger.setBigIntegerValue(new BigInteger(value.getValue().toString()));
            } else {
                xbInteger.setNil();
            }
            return xbInteger;
        }

        @Override
        public XmlObject visit(GeometryValue value) throws OwsExceptionReport {
            if (value.isSetValue()) {
                Map<HelperValues, String> additionalValue = new EnumMap<>(HelperValues.class);
                additionalValue.put(HelperValues.GMLID,
                        SosConstants.OBS_ID_PREFIX + JavaHelper.generateID(value.toString()));
                additionalValue.put(HelperValues.PROPERTY_TYPE, null);
                return encodeGML(value.getValue(), additionalValue);
            } else {
                return null;
            }
        }

        @Override
        public XmlObject visit(HrefAttributeValue value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(NilTemplateValue value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(QuantityValue value) throws OwsExceptionReport {
            return encodeGML(value);
        }

        @Override
        public XmlObject visit(ReferenceValue value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(SweDataArrayValue value) throws OwsExceptionReport {
            Map<HelperValues, String> additionalValues = new EnumMap<>(HelperValues.class);
            additionalValues.put(HelperValues.FOR_OBSERVATION, null);
            return encodeSWE(value.getValue(), additionalValues);
        }

        @Override
        public XmlObject visit(TVPValue value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(TLVTValue value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(TextValue value) throws OwsExceptionReport {
            XmlString xbString = createXmlString();
            if (value.isSetValue()) {
                xbString.setStringValue(value.getValue());
            } else {
                xbString.setNil();
            }
            return xbString;
        }

        @Override
        public XmlObject visit(XmlValue value) throws OwsExceptionReport {
            return value.getValue();
        }

        @Override
        public XmlObject visit(UnknownValue value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(MultiPointCoverage value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(RectifiedGridCoverage value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(ProfileValue value) throws OwsExceptionReport {
            return CodingHelper.encodeObjectToXml(value.getDefaultElementEncoding(), value);
        }

        @Override
        public XmlObject visit(QuantityRangeValue value) throws OwsExceptionReport {
            return null;
        }
    }
}
