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
package org.n52.sos.statistics.api.interfaces.geolocation;

public interface IAdminStatisticsLocation {
    /**
     * The country or the city database indicator.
     */
    public enum LocationDatabaseType {
        // Maybe these string values will change in the future.
        // If there is any version change at the Maxmind GeoLite's side.
        CITY("GeoLite2-City"), COUNTRY("GeoLite2-Country");

        private final String geoLite2Name;

        private LocationDatabaseType(String geoLite2Name) {
            this.geoLite2Name = geoLite2Name;
        }

        public String getGeoLite2Name() {
            return geoLite2Name;
        }

    }

    /**
     * Initialize the memory database. The path and the
     * {@code LocationDatabaseType} MUST match.
     * 
     * @param type
     *            type of the loaded database from file.
     * @param pathToDatabase
     *            can be <code>classpath:[path]</code> or
     *            <code>[absolute path]</code> string to the appropriate file.
     */
    public void initDatabase(LocationDatabaseType type,
            String pathToDatabase);
}
