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
package org.n52.sos.ogc.om;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.Constants;
import org.n52.sos.util.JTSHelper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @since 4.0.0
 * 
 */
public class OmObservationTest {

    @Test
    public final void should_have_SpatialFilteringProfileParameter() throws OwsExceptionReport {
        OmObservation omObservation = new OmObservation();
        NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
        namedValue.setName(new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY));
        namedValue.setValue(new GeometryValue(JTSHelper.createGeometryFromWKT("POINT (34.5 76.4)", Constants.EPSG_WGS84)));
        // test no parameter is set
        assertFalse(omObservation.isSetParameter());
        assertFalse(omObservation.isSetSpatialFilteringProfileParameter());
        omObservation.addParameter(namedValue);
        // test with set SpatialFilteringProfile parameter
        assertTrue(omObservation.isSetParameter());
        assertTrue(omObservation.isSetSpatialFilteringProfileParameter());
        assertThat(omObservation.getSpatialFilteringProfileParameter(), is(equalTo(namedValue)));
    }

}
