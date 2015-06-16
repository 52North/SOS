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
package org.n52.sos.encode;

import static org.junit.Assert.assertTrue;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @since 4.0.0
 * 
 */
public class GmlEncoderTest {

    private GmlEncoderv321 encoder = new GmlEncoderv321();

    @Test(expected = OwsExceptionReport.class)
    public void throwIAEForEncodeNullTest() throws OwsExceptionReport {
        encoder.encode(null);
    }

    @Test(expected = UnsupportedEncoderInputException.class)
    public void isNullForNotSupportedObjectTest() throws OwsExceptionReport {
        encoder.encode(5);
    }

    @Test(expected = OwsExceptionReport.class)
    public void throwsIllegalArgumentExceptionWhenConstructorValueNullTest() throws OwsExceptionReport {
        QuantityValue quantity = new QuantityValue(null);
        encoder.encode(quantity);
    }

    @Test
    public void isMeasureTypeValidWithoutUnitTest() throws OwsExceptionReport {
        QuantityValue quantity = new QuantityValue(2.2);
        XmlObject encode = encoder.encode(quantity);
        assertTrue("Encoded Object is NOT valid", encode.validate());
    }

    @Test
    public void isMeasureTypeValidAllSetTest() throws OwsExceptionReport {
        QuantityValue quantity = new QuantityValue(2.2);
        quantity.setUnit("cm");
        XmlObject encode = encoder.encode(quantity);
        assertTrue("Encoded Object is NOT valid", encode.validate());
    }

}
