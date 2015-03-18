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
package org.n52.sos.util.http;

import com.google.common.collect.ImmutableSetMultimap;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public interface MediaTypes {
    
    String TEXT = "text";
    
    String XML = "xml";
    
    String APPLICATION = "application";
    
    MediaType WILD_CARD = new MediaType();

    MediaType TEXT_PLAIN = new MediaType(TEXT, "plain");

    MediaType TEXT_XML = new MediaType(TEXT, XML);

    MediaType APPLICATION_XML = new MediaType(APPLICATION, XML);

    MediaType APPLICATION_ZIP = new MediaType(APPLICATION, "zip");

    MediaType APPLICATION_JSON = new MediaType(APPLICATION, "json");
    
    MediaType APPLICATION_EXI = new MediaType(APPLICATION, "exi");

    MediaType APPLICATION_KVP = new MediaType(APPLICATION, "x-kvp");

    MediaType APPLICATION_SOAP_XML = new MediaType(APPLICATION, "soap+xml");

    MediaType APPLICATION_NETCDF = new MediaType(APPLICATION, "netcdf");
    
    MediaType APPLICTION_GML_32 = new MediaType(APPLICATION, "gml+xml", "version", "3.2");
    
    MediaType APPLICTION_OM_20 = new MediaType(APPLICATION, "om+xml", "version", "2.0");
    
    ImmutableSetMultimap<MediaType, MediaType> COMPATIBLE_TYPES = new ImmutableSetMultimap
            .Builder<MediaType, MediaType>()
            .put(TEXT_XML, APPLICATION_XML)
            .put(APPLICATION_XML, TEXT_XML)
            .build();
}
