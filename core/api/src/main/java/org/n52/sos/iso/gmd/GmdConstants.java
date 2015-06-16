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
package org.n52.sos.iso.gmd;

import javax.xml.namespace.QName;

import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.w3c.SchemaLocation;

public interface GmdConstants {
    
    String NS_GMD = "http://www.isotc211.org/2005/gmd";

    String NS_GMD_PREFIX = "gmd";

    String SCHEMA_LOCATION_URL_GMD = "http://schemas.opengis.net/iso/19139/20070417/gmd/gmd.xsd";

    SchemaLocation GMD_SCHEMA_LOCATION = new SchemaLocation(NS_GMD, SCHEMA_LOCATION_URL_GMD);

    QName QN_GMD_CONFORMANCE_RESULT = new QName(GmdConstants.NS_GMD, "DQ_ConformanceResult",
            GmdConstants.NS_GMD_PREFIX);

    QName QN_GMD_QUANTITATIVE_RESULT = new QName(GmdConstants.NS_GMD, "DQ_QuantitativeResult",
            GmdConstants.NS_GMD_PREFIX);

    QName QN_GML_BASE_UNIT = new QName(GmlConstants.NS_GML_32, "BaseUnit", GmlConstants.NS_GML_PREFIX);
}
