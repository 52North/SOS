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
package org.n52.shetland.ogc.sos.ifoi;

import org.n52.sos.util.XmlHelper;

public interface InsertFeatureOfInterestConstants {
    
    public interface InsertFeatureOfInterestParams {

    }

    /**
     * The operation name.
     */
    String OPERATION_NAME = "InsertFeatureOfInterest";
    
    /*
     * GDA v10
     */
    String NS_IFOI = "http://www.opengis.net/ifoi/1.0";
    
    String NS_IFOI_PREFIX = "ifoi";
    
    String XPATH_PREFIXES_IFOI = XmlHelper.getXPathPrefix(NS_IFOI_PREFIX, NS_IFOI);
    
    String SCHEMA_LOCATION_URL_INSERT_FEATURE_OF_INTEREST =  "http://52north.org/schema/ifoi/1.0/InsertFeatureOfInterest.xsd";

    String CONFORMANCE_CLASS = "http://www.opengis.net/spec/SOS/2.0/conf/foi";

}
