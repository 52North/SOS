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
package org.n52.sos.iso;

import javax.xml.namespace.QName;

import org.n52.sos.w3c.SchemaLocation;

public interface GcoConstants {
    String NS_GCO = "http://www.isotc211.org/2005/gco";

    String NS_GCO_PREFIX = "gco";

    String SCHEMA_LOCATION_URL_GCO = "http://schemas.opengis.net/iso/19139/20070417/gco/gco.xsd";

    SchemaLocation GCO_SCHEMA_LOCATION = new SchemaLocation(NS_GCO, SCHEMA_LOCATION_URL_GCO);

    String EN_CHARACTER_STRING = "CharacterString";
    
    String AN_NIL_REASON = "nilReason";

    QName QN_GCO_DATE = new QName(GcoConstants.NS_GCO, "Date", GcoConstants.NS_GCO_PREFIX);
    
    QName QN_GCO_CHARACTER_STRING = new QName(NS_GCO, EN_CHARACTER_STRING, NS_GCO_PREFIX);

    QName QN_GCO_NIL_REASON = new QName(NS_GCO, AN_NIL_REASON, NS_GCO_PREFIX);



}
