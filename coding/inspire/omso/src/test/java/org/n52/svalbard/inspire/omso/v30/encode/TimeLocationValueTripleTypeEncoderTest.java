/**
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
package org.n52.svalbard.inspire.omso.v30.encode;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.XmlHelper;

import com.vividsolutions.jts.geom.Geometry;

import eu.europa.ec.inspire.schemas.omso.x30.CategoricalTimeLocationValueTripleType;
import eu.europa.ec.inspire.schemas.omso.x30.MeasurementTimeLocationValueTripleType;
import net.opengis.waterml.x20.TimeValuePairType;

public class TimeLocationValueTripleTypeEncoderTest {

    private TimeLocationValueTripleTypeEncoder encoder = new TimeLocationValueTripleTypeEncoder();
    
    
    @Test
    public void test_Quantity() throws UnsupportedEncoderInputException, OwsExceptionReport {
        TimeValuePairType encoded = encoder.encode(getQuantityTimeLocationValueTriple());
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
        assertThat(encoded, instanceOf(MeasurementTimeLocationValueTripleType.class));
    }
    
    @Test
    public void test_Count() throws UnsupportedEncoderInputException, OwsExceptionReport {
        TimeValuePairType encoded = encoder.encode(getCountTimeLocationValueTriple());
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
        assertThat(encoded, instanceOf(MeasurementTimeLocationValueTripleType.class));
    }
    
    @Test
    public void test_Categorical() throws UnsupportedEncoderInputException, OwsExceptionReport {
        TimeValuePairType encoded = encoder.encode(getCategoricalTimeLocationValueTriple());
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
        assertThat(encoded, instanceOf(CategoricalTimeLocationValueTripleType.class));
    }

    private TimeLocationValueTriple getQuantityTimeLocationValueTriple() throws OwsExceptionReport {
        return getTimeLocationValueTriple(new QuantityValue(15.6, "C"));
    }

    private TimeLocationValueTriple getCountTimeLocationValueTriple() throws OwsExceptionReport {
        return getTimeLocationValueTriple(new CountValue(15));
    }

    private TimeLocationValueTriple getCategoricalTimeLocationValueTriple() throws OwsExceptionReport {
        return getTimeLocationValueTriple(new CategoryValue("test", "test_voc"));
    }
    
    private TimeLocationValueTriple getTimeLocationValueTriple(Value<?> value) throws OwsExceptionReport {
        return new TimeLocationValueTriple(new TimeInstant(new DateTime()), value, getGeometry() );
    }
    
    private Geometry getGeometry() throws OwsExceptionReport {
        final String wktString =
                GeometryHandler.getInstance().getWktString("7.52", "52.7", 4326);
        return JTSHelper.createGeometryFromWKT(wktString, 4326);
    }
}
