/**
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
package org.n52.svalbard.gwml.v22.encode;

import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gwml.GWMLConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

public abstract class AbstractGroundWaterMLEncoder<T, S> extends AbstractSpecificXmlEncoder<T, S> {
    
    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(GWMLConstants.NS_GWML_22, GWMLConstants.NS_GWML_2_PREFIX);
        nameSpacePrefixMap.put(GWMLConstants.NS_GWML_WELL_22, GWMLConstants.NS_GWML_WELL_2_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(GWMLConstants.GWML_22_SCHEMA_LOCATION, GWMLConstants.GWML_WELL_22_SCHEMA_LOCATION);
    }
    
    protected static XmlObject encodeGML(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o);
    }

    protected static XmlObject encodeGML(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o, helperValues);
    }
    
    protected static XmlObject encodeSweCommon(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o);
    }

    protected static XmlObject encodeSweCommon(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o, helperValues);
    }

    protected static XmlObject encodeGWML(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GWMLConstants.NS_GWML_22, o);
    }

    protected static XmlObject encodeGWML(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GWMLConstants.NS_GWML_22, o, helperValues);
    }
    
    protected static XmlObject encodeGWMLProperty(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, o);
    }

    protected static XmlObject encodeGWMLProperty(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, o, helperValues);
    }
}
