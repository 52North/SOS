/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.netcdf;

import org.n52.janmayen.http.MediaType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Constants interface for netCDF
 *
 * @author <a href="mailto:shane@axiomdatascience.com">Shane StClair</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public interface NetcdfConstants {

    String APPLICATION = "application";

    String NETCDF = "netcdf";

    String PROFILE = "profile";

    String PARAM_VERSION = "version";

    String SUBTYPE = "subtype";

    String ZIP = "zip";

    MediaType CONTENT_TYPE_NETCDF = new MediaType(APPLICATION, NETCDF);

    MediaType CONTENT_TYPE_NETCDF_3 = new MediaType(APPLICATION, NETCDF, PARAM_VERSION, "3");

    MediaType CONTENT_TYPE_NETCDF_4 = new MediaType(APPLICATION, NETCDF, PARAM_VERSION, "4");

    MediaType CONTENT_TYPE_NETCDF_ZIP = new MediaType(APPLICATION, ZIP, SUBTYPE, NETCDF);

    MediaType CONTENT_TYPE_NETCDF_3_ZIP = new MediaType(APPLICATION, ZIP,
            ImmutableMap.of(SUBTYPE, ImmutableList.of(NETCDF), PARAM_VERSION, ImmutableList.of("3")));

    MediaType CONTENT_TYPE_NETCDF_4_ZIP = new MediaType(APPLICATION, ZIP,
            ImmutableMap.of(SUBTYPE, ImmutableList.of(NETCDF), PARAM_VERSION, ImmutableList.of("4")));

    // MediaType CONTENT_TYPE_NETCDF_3_ZIP = new MediaType(APPLICATION, ZIP,
    // ImmutableListMultimap.of(SUBTYPE, NETCDF, PARAM_VERSION, "3"));
    //
    // MediaType CONTENT_TYPE_NETCDF_4_ZIP = new MediaType(APPLICATION, ZIP,
    // ImmutableListMultimap.of(SUBTYPE, NETCDF, PARAM_VERSION, "4"));

    String CONTRIBUTOR_EMAIL = "contributor_email";

}
