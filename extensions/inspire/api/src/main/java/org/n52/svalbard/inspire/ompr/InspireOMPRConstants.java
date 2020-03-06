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
package org.n52.svalbard.inspire.ompr;

import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;

/**
 * INSPIRE OM Process constants
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public interface InspireOMPRConstants {

    String NS_OMPR_30 = "http://inspire.ec.europa.eu/schemas/ompr/3.0";

    String NS_OMPR_PREFIX = "ompr";

    String SCHEMA_LOCATION_URL_OMPR = "http://inspire.ec.europa.eu/schemas/ompr/3.0/Process.xsd";

    SchemaLocation OMPR_SCHEMA_LOCATION = new SchemaLocation(NS_OMPR_30, SCHEMA_LOCATION_URL_OMPR);

    String OMPR_30_OUTPUT_FORMAT_URL = NS_OMPR_30;

    MediaType OMPR_30_CONTENT_TYPE = new MediaType("text", "xml", "subtype", "ompr/3.0");

    String OMPR_30_OUTPUT_FORMAT_MIME_TYPE = OMPR_30_CONTENT_TYPE.toString();
}
