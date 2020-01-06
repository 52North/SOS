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
package org.n52.schetland.uvf;

import java.util.Collections;
import java.util.List;

import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.MediaType;


public interface UVFConstants {

    MediaType CONTENT_TYPE_UVF = new MediaType("application", "uvf");
    
    MediaType CONTENT_TYPE_UVF_WINDOWS = new MediaType("application", "uvf", "lineending", "Windows");
    
    MediaType CONTENT_TYPE_UVF_UNIX = new MediaType("application", "uvf", "lineending", "Unix");
    
    MediaType CONTENT_TYPE_UVF_MAC = new MediaType("application", "uvf", "lineending", "Mac");

    /**
     * Time format to be used in UVF encoded data: <code>yyMMddHHmm</code>,
     * e.g. <code>7001011230</code> is 01.01.1970 12:30 UTC
     */
    String TIME_FORMAT = "yyMMddHHmm";
    
    /**
     * The identifiers length is limited to 15 characters following UVF spec for lines 2, 3
     */
    int MAX_IDENTIFIER_LENGTH = 15;

    /**
     * The maximum length of a value string is limited to 10 characters. Hence, the values are shortened,
     * e.g. <code>52.1234567890</code> will be cut to <code>52.1234567</code>
     */
    int MAX_VALUE_LENGTH = 10;

    /**
     * No data values MUST be encoded with <code>-777</code> in the UVF format.
     */
    String NO_DATA_STRING = "-777";
    
    /**
     * The list of allowed CRS EPSG codes. Here, the German GK bands:
     * <ul>
     * <li>31466</li>
     * <li>31467</li>
     * <li>31468</li>
     * <li>31469</li>
     * </ul>
     */
    List<String> ALLOWED_CRS = Collections.unmodifiableList(CollectionHelper.list("31466", "31467", "31468",
            "31469"));

    int MINIMUM_EPSG_CODE = 31466;

    int MAXIMUM_EPSG_CODE = 31469;
    
    String LINE_ENDING_UNIX = "\n";
    
    String LINE_ENDING_WINDOWS = "\r\n";
    
    String LINE_ENDING_MAC = "\r";
    
    enum LineEnding{
        Windows, Unix, Mac;
    }
    
    enum FunktionInterpretation {
        Linie, Blockanfang, Blockende, Summenlinie;
    }
}
 
