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
package org.n52.svalbard.gml.v321.encode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.encode.ClassToClassEncoderKey;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.XmlPropertyTypeEncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Point;

import net.opengis.gml.x32.PointPropertyType;
import net.opengis.gml.x32.PointType;

/**
 * {@link Encoder} implementation for {@link Point} to {@link PointPropertyType}
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class PointPropertyTypeEncoder extends AbstractSpecificXmlEncoder<PointPropertyType, Point> {

    protected static final Set<EncoderKey> ENCODER_KEYS =
            Sets.newHashSet(new ClassToClassEncoderKey(PointPropertyType.class, Point.class),
                    new XmlPropertyTypeEncoderKey(GmlConstants.NS_GML_32, Point.class));

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public PointPropertyType encode(Point point) throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(point, new EnumMap<HelperValues, String>(HelperValues.class));
    }

    @Override
    public PointPropertyType encode(Point point, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        PointPropertyType ppt = PointPropertyType.Factory.newInstance();
        ppt.setPoint(encodePointType(point, additionalValues));
        return ppt;
    }

    private PointType encodePointType(Point point, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        return (PointType) encodeGML(point, additionalValues);
    }

    protected static XmlObject encodeGML(Object o, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o, additionalValues);
    }

}
