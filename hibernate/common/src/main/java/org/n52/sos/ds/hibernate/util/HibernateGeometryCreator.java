/*
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
package org.n52.sos.ds.hibernate.util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.n52.series.db.beans.HibernateRelations.HasCoordinate;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.JTSHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.sos.util.GeometryHandler;

public class HibernateGeometryCreator {

    public HibernateGeometryCreator() {
    }

    public Geometry createGeometry(final HasCoordinate coodinates, GeometryHandler geometryHandler)
            throws OwsExceptionReport {
        if (coodinates.isSetLongLat()) {
            int epsg = geometryHandler.getStorageEPSG();
            if (coodinates.isSetSrid()) {
                epsg = coodinates.getSrid();
            }
            final String wktString = geometryHandler.getWktString(coodinates.getLon(), coodinates.getLat(), epsg);
            Geometry geom;
            try {
                geom = JTSHelper.createGeometryFromWKT(wktString, epsg);
            } catch (ParseException e) {
                throw new NoApplicableCodeException().causedBy(e);
            }
            if (coodinates.isSetAlt()) {
                geom.getCoordinate().z = JavaHelper.asDouble(coodinates.getAlt());
                if (geom.getSRID() == geometryHandler.getStorage3DEPSG()) {
                    geom.setSRID(geometryHandler.getStorage3DEPSG());
                }
            }
            return geom;
        }
        return null;
    }

}
