/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.JTSHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCoordinate;
import org.n52.sos.util.GeometryHandler;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

public class HibernateGeometryCreator {

    private int storageEPSG = GeometryHandler.getInstance().getStorageEPSG();
    private int storage3depsg =  GeometryHandler.getInstance().getStorage3DEPSG();

    public HibernateGeometryCreator() {

    }

    public HibernateGeometryCreator(int storageEPSG, int storage3DEPSG) {
        this.storageEPSG = storageEPSG;
        this.storage3depsg = storage3DEPSG;
    }

    private int getStorageEPSG() {
        return storageEPSG;
    }

    private int getStorage3DEPSG() {
        return storage3depsg;
    }

    /**
     * Get the geometry from featureOfInterest object.
     *
     * @param feature
     * @return geometry
     * @throws OwsExceptionReport
     */
    public Geometry createGeometry(final HasCoordinate coodinates) throws OwsExceptionReport {
      if (coodinates.isSetLongLat()) {
            int epsg = getStorageEPSG();
            if (coodinates.isSetSrid()) {
                epsg = coodinates.getSrid();
            }
            final String wktString =
                    GeometryHandler.getInstance().getWktString(coodinates.getLongitude(), coodinates.getLatitude(), epsg);
            Geometry geom;
            try {
                geom = JTSHelper.createGeometryFromWKT(wktString, epsg);
            } catch (ParseException e) {
                throw new NoApplicableCodeException().causedBy(e);
            }
            if (coodinates.isSetAltitude()) {
                geom.getCoordinate().z = JavaHelper.asDouble(coodinates.getAltitude());
                if (geom.getSRID() == getStorage3DEPSG()) {
                    geom.setSRID(getStorage3DEPSG());
                }
            }
            return geom;
        }
        return null;
    }

}
