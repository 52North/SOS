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
package org.n52.svalbard.gml.v321.encode;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.xmlbeans.XmlOptions;
import org.junit.Test;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.QuantityRangeValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.XmlHelper;

import net.opengis.gml.x32.RectifiedGridCoverageDocument;

public class RectifiedGridCoverageDocumentEncoderTest {
    
    private RectifiedGridCoverageDocumentEncoder encoder = new RectifiedGridCoverageDocumentEncoder();

    @Test
    public void test_quantity_encoding() throws UnsupportedEncoderInputException, OwsExceptionReport {
        RectifiedGridCoverageDocument encoded = encoder.encode(getRectifiedGridCoverage());
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
        System.out.println(encoded.xmlText(new XmlOptions().setSavePrettyPrint()));
    }
    
    @Test
    public void test_category_encoding() throws UnsupportedEncoderInputException, OwsExceptionReport {
        RectifiedGridCoverageDocument encoded = encoder.encode(getCategoryRectifiedGridCoverage());
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
        System.out.println(encoded.xmlText(new XmlOptions().setSavePrettyPrint()));
    }
    
    @Test
    public void test_text_encoding() throws UnsupportedEncoderInputException, OwsExceptionReport {
        RectifiedGridCoverageDocument encoded = encoder.encode(getTextRectifiedGridCoverage());
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
        System.out.println(encoded.xmlText(new XmlOptions().setSavePrettyPrint()));
    }
    
    private RectifiedGridCoverage getRectifiedGridCoverage() {
        RectifiedGridCoverage rgc = new RectifiedGridCoverage("quantity");
        rgc.addValue(new QuantityValue(2.5, "m"), new QuantityValue(10.0));
        rgc.addValue(new QuantityValue(5.0, "m"), new QuantityValue(8.0));
        rgc.addValue(new QuantityValue(10.0, "m"), new QuantityValue(3.0));
        rgc.setUnit("C");
        return rgc;
    }
      
    private RectifiedGridCoverage getCategoryRectifiedGridCoverage() {
        RectifiedGridCoverage rgc = new RectifiedGridCoverage("category");
        rgc.setUnit("d");
        rgc.setRangeParameters("category_param");
        rgc.addValue(new QuantityRangeValue(0.0, 5.0, "m"), new CategoryValue("test category"));
        rgc.addValue(new QuantityRangeValue(5.0, 10.0, "m"), new CategoryValue("test category 2"));
        rgc.addValue(new QuantityRangeValue(10.0, 15.0, "m"), new CategoryValue("test category 2 test"));
        return rgc;
    }
    
    private RectifiedGridCoverage getTextRectifiedGridCoverage() {
        RectifiedGridCoverage rgc = new RectifiedGridCoverage("text");
        rgc.setUnit("d");
        rgc.setRangeParameters("text_param");
        rgc.addValue(new QuantityRangeValue(0.0, 5.0, "m"), new TextValue("test text"));
        rgc.addValue(new QuantityRangeValue(5.0, 10.0, "m"), new TextValue("test text 2"));
        rgc.addValue(new QuantityRangeValue(10.0, 15.0, "m"), new TextValue("test text 2 test"));
        return rgc;
    }
}
