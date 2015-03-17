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
package org.n52.sos.ogc.swe;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.swe.SweConstants.SweCoordinateName;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SweEnvelopeTest {

    @Test public void
    should_create_valid_sosSweEnvelope_from_sosEnvelope()
    {
        final int srid = 52;
        final double x1 = 1;
        final double y1 = 2;
        final double y2 = 3;
        final double x2 = 4;
        final String uom = "deg";
        final SosEnvelope sosEnvelope = new SosEnvelope(new Envelope(x1, x2, y1, y2), srid);
        final SweEnvelope sweEnvelope = new SweEnvelope(sosEnvelope, uom);

        // srid
        assertThat(sweEnvelope.getReferenceFrame(), is(Integer.toString(srid)));
        // x1
        final List<SweCoordinate<?>> lcCoordinates = sweEnvelope.getLowerCorner().getCoordinates();
		assertThat(((Double) lcCoordinates.get(0).getValue().getValue()).doubleValue(), is(x1));
        // y1
        assertThat(((Double) lcCoordinates.get(1).getValue().getValue()).doubleValue(), is(y1));
        // x2
        final List<SweCoordinate<?>> ucCoordinates = sweEnvelope.getUpperCorner().getCoordinates();
		assertThat(((Double) ucCoordinates.get(0).getValue().getValue()).doubleValue(), is(x2));
        // y2
        assertThat(((Double) ucCoordinates.get(1).getValue().getValue()).doubleValue(), is(y2));
        // uom
        assertThat(((SweQuantity) lcCoordinates.get(0).getValue()).getUom(), is(uom));
        assertThat(((SweQuantity) lcCoordinates.get(1).getValue()).getUom(), is(uom));
        assertThat(((SweQuantity) ucCoordinates.get(0).getValue()).getUom(), is(uom));
        assertThat(((SweQuantity) ucCoordinates.get(1).getValue()).getUom(), is(uom));
        // northing
        assertThat(lcCoordinates.get(0).getName(),
                is(SweCoordinateName.easting.name()));
        assertThat(ucCoordinates.get(0).getName(),
                is(SweCoordinateName.easting.name()));
        // easting
        assertThat(lcCoordinates.get(1).getName(),
                is(SweCoordinateName.northing.name()));
        assertThat(ucCoordinates.get(1).getName(),
                is(SweCoordinateName.northing.name()));
    }

}
