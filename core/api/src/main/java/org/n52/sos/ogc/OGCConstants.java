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
package org.n52.sos.ogc;

/**
 * Interface for OGC constants
 * 
 * @since 4.0.0
 */
public interface OGCConstants {

    String NS_OGC = "http://www.opengis.net/ogc";

    String NS_OGC_PREFIX = "ogc";

    String SCHEMA_LOCATION_OGC = "http://schemas.opengis.net/sos/1.0.0/ogc4sos.xsd";

    String UNKNOWN = "http://www.opengis.net/def/nil/OGC/0/unknown";

    /** Constant for prefixes of FOIs */
    String URN_FOI_PREFIX = "urn:ogc:def:object:feature:";

    String URN_IDENTIFIER_IDENTIFICATION = "urn:ogc:def:identifier:OGC::identification";

    String URN_OFFERING_ID = "urn:ogc:def:identifier:OGC:offeringID";

    /** Constant for prefixes of procedures */
    String URN_PHENOMENON_PREFIX = "urn:ogc:def:phenomenon:OGC:1.0.30:";

    /** Constant for prefixes of procedures */
    String URN_PROCEDURE_PREFIX = "urn:ogc:object:feature:Sensor:IFGI:";

    String URN_PROPERTY_NAME_LOCATION = "urn:ogc:data:location";

    String URN_PROPERTY_NAME_SAMPLING_GEOMETRY = "urn:ogc:data:samplingGeometry";

    String URN_PROPERTY_NAME_SPATIAL_VALUE = "urn:ogc:data:spatialValue";

    String URN_UNIQUE_IDENTIFIER = "urn:ogc:def:identifier:OGC:uniqueID";

    String UNIQUE_ID = "uniqueID";
    
    String URN_UNIQUE_IDENTIFIER_END = UNIQUE_ID;

    String URN_UNIQUE_IDENTIFIER_START = "urn:ogc:def:identifier:OGC:";

    String URN_OBSERVED_BBOX = "urn:ogc:def:property:OGC:1.0:observedBBOX";
    
    String QUERY_LANGUAGE_PREFIX = "urn:ogc:def:queryLanguage:";
    
    String URN_DEF_CRS_EPSG = "urn:ogc:def:crs:EPSG::";
    
    String URL_DEF_CRS_EPSG = "http://www.opengis.net/def/crs/EPSG/0/";
}
