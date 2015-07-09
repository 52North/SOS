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
package org.n52.sos.statistics.api.utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.n52.iceland.config.annotation.Configurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configurable
public class GeoLiteFileDownloader {
    private static final Logger logger = LoggerFactory.getLogger(GeoLiteFileDownloader.class);
    public static final String CITY_GZ_FILE_NAME = "city.mmdb.gz";
    public static final String COUNTRY_GZ_FILE_NAME = "country.mmdb.gz";
    public static final String CITY_FILE_NAME = "city.mmdb";
    public static final String COUNTRY_FILE_NAME = "country.mmdb";

    public static void downloadDefaultDatabases(String folderPath) {
        try {

            // create folder
            File folder = new File(folderPath);
            if (!folder.exists()) {
                try {
                    FileUtils.forceMkdir(folder);
                } catch (IOException e) {
                    logger.error("Can not create folder", e);
                    return;
                }
            }

            Properties prop = new Properties();
            prop.load(GeoLiteFileDownloader.class.getResourceAsStream("/geolitepaths.properties"));

            String cityUrl = prop.getProperty("url.city");
            String countryUrl = prop.getProperty("url.country");

            if (cityUrl == null || countryUrl == null) {
                logger.error("Urls not found in geolitepaths.properties file");
                return;
            }

            String cityOutPath = folder.getPath().concat("/").concat(CITY_GZ_FILE_NAME);
            String countryOutPath = folder.getPath().concat("/").concat(COUNTRY_GZ_FILE_NAME);

            logger.info("Downloading {} to {}", cityUrl, cityOutPath);
            FileDownloader.downloadFile(cityUrl, cityOutPath);
            logger.info("Downloading {} to {}", countryUrl, countryOutPath);
            FileDownloader.downloadFile(countryUrl, countryOutPath);
            logger.info("Gunzip {}", cityOutPath);
            FileDownloader.gunzipFile(cityOutPath);
            logger.info("Gunzip {}", countryOutPath);
            FileDownloader.gunzipFile(countryOutPath);

        } catch (IOException e) {
            logger.error("Error during default download", e);
        } catch (Throwable e) {
            logger.error(null, e);
        }
    }
}
