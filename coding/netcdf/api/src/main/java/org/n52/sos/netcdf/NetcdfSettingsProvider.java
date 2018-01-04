/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
 * Implementation of {@link SettingDefinitionProvider} for netCDF encoding
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public interface NetcdfSettingsProvider {

    public static final String NETCDF_VERSION = "netcdf.version";

    public static final String NETCDF_CHUNK_SIZE_TIME = "netcdf.chunk.size";

    public static final String NETCDF_FILL_VALUE = "netcdf.fillValue";

    public static final String NETCDF_HEIGHT_DEPTH = "netcdf.heightDepth";

    public static final String NETCDF_VARIABLE_TYPE = "netcdf.varibale.type";

    public static final String NETCDF_VARIABLE_UPPER_CASE = "netcdf.varibale.upperCase";

    public static final String NETCDF_PUBLISHER = "netcdf.publisher";

    public static final String NETCDF_CONTRIBUTOR = "netcdf.contributor";

    public static final String NETCDF_PHEN_LATITUDE = "netcdf.phenomenon.latitude";

    public static final String NETCDF_PHEN_LONGITUDE = "netcdf.phenomenon.longitude";

    public static final String NETCDF_PHEN_Z = "netcdf.phenomenon.z";

}
