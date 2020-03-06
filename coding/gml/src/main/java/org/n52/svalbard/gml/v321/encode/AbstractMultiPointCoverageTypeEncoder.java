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

import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.Encoder;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.MultiPointCoverage.PointValueLists;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.XmlHelper;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

import net.opengis.gml.x32.DiscreteCoverageType;
import net.opengis.gml.x32.DomainSetType;
import net.opengis.gml.x32.MultiPointDomainDocument;

/**
 * Abstract {@link Encoder} implementation to encode {@link MultiPointCoverage}
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 * @param <T>
 */
public abstract class AbstractMultiPointCoverageTypeEncoder<T> extends AbstractCoverageEncoder<T, MultiPointCoverage> {

    /**
     * Encode {@link MultiPointCoverage} to {@link DiscreteCoverageType}
     * 
     * @param dct
     *            {@link DiscreteCoverageType} to add values to
     * @param multiPointCoverage
     *            {@link MultiPointCoverage} to encode
     * @return
     * @throws OwsExceptionReport
     */
    protected DiscreteCoverageType encodeMultiPointCoverageType(DiscreteCoverageType dct,
            MultiPointCoverage multiPointCoverage) throws OwsExceptionReport {
        dct.setId(multiPointCoverage.getGmlId());
        PointValueLists pointValues = multiPointCoverage.getPointValue();
        encodeMultiPointDomain(dct, pointValues);
        encodeRangeSet(dct, multiPointCoverage);
        return dct;
    }

    private void encodeMultiPointDomain(DiscreteCoverageType dct, PointValueLists pointValues)
            throws OwsExceptionReport {
        MultiPointDomainDocument mpdd = MultiPointDomainDocument.Factory.newInstance();
        DomainSetType mpdst = mpdd.addNewMultiPointDomain();
        GeometryFactory factory = pointValues.getPoints().get(0).getFactory();
        MultiPoint multiPoint = factory.createMultiPoint(pointValues.getPoints().toArray(new Point[0]));
        Map<SosConstants.HelperValues, String> helperValues = Maps.newHashMap();
        helperValues.put(HelperValues.GMLID, JavaHelper.generateID(multiPoint.toString()));
        helperValues.put(HelperValues.PROPERTY_TYPE, "true");
        XmlObject encodedGeometry = encodeGML(multiPoint);
        mpdst.addNewAbstractGeometry().set(encodedGeometry);
        XmlHelper.substituteElement(mpdst.getAbstractGeometry(), encodedGeometry);
        dct.setDomainSet(mpdst);
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(GmlConstants.NS_GML_32, GmlConstants.NS_GML_PREFIX);
    }

    protected static XmlObject encodeGML(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o);
    }
}
