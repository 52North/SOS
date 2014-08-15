/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.hibernate.usertype;

import org.hibernate.HibernateException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @author Simona Badoiu <s.a.badoiu@52north.org>
 */
public class HibernateGeometryType extends AbstractStringBasedHibernateUserType<Geometry> {
	private static final int SRID_LENGTH = 4;

    public HibernateGeometryType() {
        super(Geometry.class);
    }


	@Override
	protected String encode(Geometry jtsGeom) throws HibernateException {
		int srid = jtsGeom.getSRID();
		WKTWriter writer = new WKTWriter();
		String wkt = writer.write(jtsGeom);
		
		return srid + "|" + wkt;
	}


	@Override
	protected Geometry decode(String data) throws HibernateException {
		if (data == null) {
			return null;
		}
		
		int srid = Integer.parseInt(data.substring(0, SRID_LENGTH - 1));
		Geometry geom;
		
		try {
			WKTReader reader = new WKTReader();
			geom = reader.read(data.substring(SRID_LENGTH + 1));
		} catch (Exception e) {
			throw new RuntimeException("Couldn't parse incoming wkt geometry.", e);
		}
		geom.setSRID(srid);
		
		return geom;
	}
}
