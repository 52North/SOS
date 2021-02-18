/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

/**
 * Interface for netCDF encoding settings
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public interface NetcdfSettingsProvider {

    String NETCDF_VERSION = "netcdf.version";

    String NETCDF_CHUNK_SIZE_TIME = "netcdf.chunk.size";

    String NETCDF_FILL_VALUE = "netcdf.fillValue";

    String NETCDF_HEIGHT_DEPTH = "netcdf.heightDepth";

    String NETCDF_VARIABLE_TYPE = "netcdf.varibale.type";

    String NETCDF_VARIABLE_UPPER_CASE = "netcdf.varibale.upperCase";

    String NETCDF_PUBLISHER = "netcdf.publisher";

    String NETCDF_CONTRIBUTOR = "netcdf.contributor";

    String NETCDF_PHEN_LATITUDE = "netcdf.phenomenon.latitude";

    String NETCDF_PHEN_LONGITUDE = "netcdf.phenomenon.longitude";

    String NETCDF_PHEN_Z = "netcdf.phenomenon.z";

}
