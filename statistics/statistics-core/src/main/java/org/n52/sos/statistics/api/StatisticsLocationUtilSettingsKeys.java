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
package org.n52.sos.statistics.api;

public class StatisticsLocationUtilSettingsKeys {

    public static final String ENABLED = "statistics.geoloc.enabled";
    public static final String DOWNLOAD_FOLDERPATH = "statistics.geoloc.download_folder";
    // CHOICE
    public static final String DATABASE_DOWNLOADER = "statistics.geoloc.db_downloader";
    public static final String DATABASE_DOWNLOADER_AUTO = "statistics.geoloc.db_downloader.auto";
    public static final String DATABASE_DOWNLOADER_MANUAL = "statistics.geoloc.db_downloader.manual";

    public static final String MANUAL_CITY_LOC = "statistics.geoloc.city_location";
    public static final String MANUAL_COUNTRY_LOC = "statistics.geoloc.country_location";

    // CHOICE
    public static final String DATABASE_TYPE = "statistics.geoloc.db_type";
    public static final String DATABASE_TYPE_CITY = "statistics.geoloc.db_type.city";
    public static final String DATABASE_TYPE_COUNTRY = "statistics.geoloc.db_type.country";

}
