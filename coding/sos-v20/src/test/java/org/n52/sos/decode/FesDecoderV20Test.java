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
package org.n52.sos.decode;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import net.opengis.fes.x20.BinaryComparisonOpType;
import net.opengis.fes.x20.FilterDocument;
import net.opengis.fes.x20.FilterType;
import net.opengis.fes.x20.LiteralType;
import net.opengis.fes.x20.PropertyIsEqualToDocument;

import org.apache.xmlbeans.XmlString;
import org.junit.Test;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * FES 2.0 decoder test class
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 * 
 */
public class FesDecoderV20Test {

    private static final String TEST_VALUE_REFERENCE = "testValueReference";

    private static final String TEST_LITERAL = "testLiteral";

    private static final FesDecoderv20 decoder = new FesDecoderv20();

    /**
     * Test PropertyIsEqualTo filter decoding
     * 
     * @throws OwsExceptionReport
     */
    @Test
    public void should_parse_PropertyIsEqualTo_Filter() throws OwsExceptionReport {
        PropertyIsEqualToDocument propertyIsEqualToDoc = PropertyIsEqualToDocument.Factory.newInstance();
        BinaryComparisonOpType propertyIsEqualToType = propertyIsEqualToDoc.addNewPropertyIsEqualTo();
        // valueReference
        XmlString valueReference =
                (XmlString) propertyIsEqualToType.addNewExpression().substitute(FilterConstants.QN_VALUE_REFERENCE,
                        XmlString.type);
        valueReference.setStringValue(TEST_VALUE_REFERENCE);
        // literal
        LiteralType literalType =
                (LiteralType) propertyIsEqualToType.addNewExpression().substitute(FilterConstants.QN_LITERAL,
                        LiteralType.type);
        XmlString newInstance = XmlString.Factory.newInstance();
        newInstance.setStringValue(TEST_LITERAL);
        literalType.set(newInstance);
        // create document
        FilterDocument filterDoc = FilterDocument.Factory.newInstance();
        FilterType filterType = filterDoc.addNewFilter();
        filterType.setComparisonOps(propertyIsEqualToType);
        filterType.getComparisonOps().substitute(FilterConstants.QN_PROPERTY_IS_EQUAL_TO, BinaryComparisonOpType.type);
        ComparisonFilter comparisonFilter = (ComparisonFilter) decoder.decode(filterDoc);
        // test
        assertThat(comparisonFilter.getOperator(), is(FilterConstants.ComparisonOperator.PropertyIsEqualTo));
        assertThat(comparisonFilter.getValueReference(), is(TEST_VALUE_REFERENCE));
        assertThat(comparisonFilter.getValue(), is(TEST_LITERAL));
    }

}
