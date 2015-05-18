/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc;

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
