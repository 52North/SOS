/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.wml;

import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;

/**
 * @since 4.0.0
 * 
 */
public interface WaterMLConstants {
    String NS_WML_20 = "http://www.opengis.net/waterml/2.0";

    String NS_WML2_DR = "http://www.opengis.net/waterml-dr/2.0";

    String NS_WML_20_PREFIX = "wml2";

    String NS_WML_20_DR_PREFIX = "wml2dr";
    
    String NS_WML_20_PROCEDURE_ENCODING = "http://www.opengis.net/waterml/2.0/observationProcess";

    String PROCESS_TYPE_SIMULATION = "http://www.opengis.net/def/waterml/2.0/processType/Simulation";

    String PROCESS_TYPE_MANUAL_METHOD = "http://www.opengis.net/def/waterml/2.0/processType/ManualMethod";

    String PROCESS_TYPE_SENSOR = "http://www.opengis.net/def/waterml/2.0/processType/Sensor";

    String PROCESS_TYPE_ALGORITHM = "http://www.opengis.net/def/waterml/2.0/processType/Algorithm";

    String PROCESS_TYPE_UNKNOWN = OGCConstants.UNKNOWN;

    String OBSERVATION_TYPE_MEASURMENT_TVP =
            "http://www.opengis.net/def/observationType/waterml/2.0/MeasurementTimeseriesTVPObservation";

    String OBSERVATION_TYPE_CATEGORICAL_TVP =
            "http://www.opengis.net/def/observationType/waterml/2.0/CategoricalTVPTimeseriesObservation";

    String OBSERVATION_TYPE_MEASURMENT_TDR =
            "http://www.opengis.net/def/observationType/waterml/2.0/measurementDRTimeseriesObservation";

    String OBSERVATION_TYPE_CATEGORICAL_TDR =
            "http://www.opengis.net/def/observationType/waterml/2.0/categoricalDRTimeseriesObservation";

    MediaType WML_CONTENT_TYPE = new MediaType("text", "xml", "subtype", "waterml/2.0");

    MediaType WML_DR_CONTENT_TYPE = new MediaType("text", "xml", "subtype", "waterml-dr/2.0");

    String SCHEMA_LOCATION_URL_WML_20 = "http://schemas.opengis.net/waterml/2.0/waterml2.xsd";

    String SCHEMA_LOCATION_URL_WML_20_DR =
            "http://schemas.opengis.net/waterml/2.0/domain-range-informative/timeseries-domain-range.xsd";

    SchemaLocation WML_20_SCHEMA_LOCATION = new SchemaLocation(NS_WML_20, SCHEMA_LOCATION_URL_WML_20);

    SchemaLocation WML_20_DR_SCHEMA_LOCATION = new SchemaLocation(NS_WML_20_DR_PREFIX, SCHEMA_LOCATION_URL_WML_20_DR);

}
