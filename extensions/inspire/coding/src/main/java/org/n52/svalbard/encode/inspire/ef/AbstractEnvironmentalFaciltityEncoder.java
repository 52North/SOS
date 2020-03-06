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
package org.n52.svalbard.encode.inspire.ef;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractGmlEncoderv321;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.svalbard.inspire.base.InspireBaseConstants;
import org.n52.svalbard.inspire.base2.InspireBase2Constants;
import org.n52.svalbard.inspire.ef.InspireEfConstants;

import com.google.common.collect.Sets;

public abstract class AbstractEnvironmentalFaciltityEncoder<T> extends AbstractGmlEncoderv321<T> {
    
    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(InspireEfConstants.NS_EF, InspireEfConstants.NS_EF_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(InspireEfConstants.EF_40_SCHEMA_LOCATION);
    }

    protected static XmlObject encodeEF(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireEfConstants.NS_EF, o);
    }
    
    protected static XmlObject encodeEFPropertyType(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlPropertyType(InspireEfConstants.NS_EF, o);
    }
    
    protected static XmlObject encodeEFDocument(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlDocument(InspireEfConstants.NS_EF, o);
    }

    protected static XmlObject encodeEF(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireEfConstants.NS_EF, o, helperValues);
    }
    
    protected static XmlObject encodeBASE2(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireBase2Constants.NS_BASE2, o);
    }
    
    protected static XmlObject encodeBASE2PropertyType(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlPropertyType(InspireBase2Constants.NS_BASE2, o);
    }
    
    protected static XmlObject encodeBASE2Document(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlDocument(InspireBase2Constants.NS_BASE2, o);
    }

    protected static XmlObject encodeBASE2(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireBase2Constants.NS_BASE2, o, helperValues);
    }
    
    protected static XmlObject encodeBASE(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireBaseConstants.NS_BASE, o);
    }
    
    protected static XmlObject encodeBASEPropertyType(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlPropertyType(InspireBaseConstants.NS_BASE, o);
    }
    
    protected static XmlObject encodeBASEDocument(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXmlDocument(InspireBaseConstants.NS_BASE, o);
    }

    protected static XmlObject encodeBASE(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireBaseConstants.NS_BASE, o, helperValues);
    }
    
}
