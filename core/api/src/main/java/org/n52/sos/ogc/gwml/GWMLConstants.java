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
package org.n52.sos.ogc.gwml;

import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;

public interface GWMLConstants {

    String NS_GWML_22 = "http://www.opengis.net/gwml/2.2";
    
    String NS_GWML_WELL_22 = "http://www.opengis.net/gwml-well/2.2";
    
    String NS_GWML_2_PREFIX = "gwml2";
    
    String NS_GWML_WELL_2_PREFIX = "gwml2w";
    
    String SCHEMA_LOCATION_URL_GWML_22 = "http://schemas.opengis.net/gwml/2.2/gwml2.xsd";
    
    String SCHEMA_LOCATION_URL_GWML_WELL_22 = "http://schemas.opengis.net/gwml/2.2/gwml2-well.xsd";

    SchemaLocation GWML_22_SCHEMA_LOCATION = new SchemaLocation(NS_GWML_22, SCHEMA_LOCATION_URL_GWML_22);
    
    SchemaLocation GWML_WELL_22_SCHEMA_LOCATION = new SchemaLocation(NS_GWML_WELL_22, SCHEMA_LOCATION_URL_GWML_WELL_22);

    String OBS_TYPE_GEOLOGY_LOG = "http://www.opengis.net/def/observationType/OGC-GWML/2.2/GW_GeologyLog";
    
    String OBS_TYPE_GEOLOGY_LOG_COVERAGE = "http://www.opengis.net/def/observationType/OGC-GWML/2.2/GW_GeologyLogCoverage";
    
    MediaType CONTENT_TYPE_GWML_22 = new MediaType("text", "xml", "subtype", "gwml/2.2");
    
    String PARAM_FROM_DEPTH = "fromDepth";
    
    String PARAM_TO_DEPTH = "toDepth";
}
