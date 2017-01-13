/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.svalbard.gml.v321.encode;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.SortedMap;

import org.junit.Test;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.XmlHelper;

import com.google.common.collect.Maps;

import net.opengis.gml.x32.RectifiedGridCoverageDocument;

public class RectifiedGridCoverageDocumentEncoderTest {
    
    private RectifiedGridCoverageDocumentEncoder encoder = new RectifiedGridCoverageDocumentEncoder();

    @Test
    public void test_encoding() throws UnsupportedEncoderInputException, OwsExceptionReport {
        RectifiedGridCoverageDocument encoded = encoder.encode(getRectifiedGridCoverage());
        
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
    }
    
    private RectifiedGridCoverage getRectifiedGridCoverage() {
        RectifiedGridCoverage gridCoverage = new RectifiedGridCoverage("test");
        SortedMap<Double, Value<?>> values = Maps.newTreeMap();
        values.put(2.5, new QuantityValue(10.0));
        values.put(5.0, new QuantityValue(8.0));
        values.put(10.0, new QuantityValue(3.0));
        gridCoverage.setValue(values);
        gridCoverage.setUnit("C");
        return gridCoverage;
    }
}
