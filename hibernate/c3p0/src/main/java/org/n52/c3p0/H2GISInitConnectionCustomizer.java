/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.c3p0;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2gis.functions.factory.H2GISFunctions;
import org.n52.faroe.ConfigurationError;

import com.mchange.v2.c3p0.AbstractConnectionCustomizer;

public class H2GISInitConnectionCustomizer extends AbstractConnectionCustomizer {

    public static final String H2GIS = "h2gis";

    private static final String H2_PRODUCT_NAME = "H2";

    /**
     * Cheap probe for an alias that only exists once H2GIS has been loaded.
     */
    private static final String SPATIAL_PROBE = "SELECT H2GIS_VERSION()";

    @Override
    public void onAcquire(Connection c, String parentDataSourceIdentityToken) throws Exception {
        if (!isH2(c) || isSpatialEnabled(c)) {
            return;
        }
        try {
            H2GISFunctions.load(c);
        } catch (SQLException ex) {
            throw new ConfigurationError("Could not initialize H2GIS", ex);
        }
    }

    private boolean isH2(Connection c) {
        if (c == null) {
            return false;
        }
        try {
            return H2_PRODUCT_NAME.equalsIgnoreCase(c.getMetaData().getDatabaseProductName());
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * H2GIS registers its functions as database aliases, i.e. they survive the
     * connection and are persisted with the database. Loading them is therefore
     * only necessary once, and since {@code load(Connection)} issues several
     * hundred DDL statements it should not run on every pool acquisition.
     *
     * A failed probe does not poison the connection on H2, so it is safe to
     * simply try the call and fall back to loading.
     */
    private boolean isSpatialEnabled(Connection c) {
        try (Statement st = c.createStatement()) {
            st.executeQuery(SPATIAL_PROBE).close();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

}