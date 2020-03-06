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
package org.n52.sos.decode;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import net.opengis.sos.x10.GetObservationDocument;

public class OgcDecoderv100Test {
    
    OgcDecoderv100 decoder = new OgcDecoderv100();
    
    @Test
    public void parsePropertyIsLike() throws XmlException, IOException, OwsExceptionReport {
        XmlObject parse = XmlObject.Factory.parse(getClass().getResourceAsStream("/GetObsPropertyIsLike.xml"));
        Object decode = decoder.decode(((GetObservationDocument)parse).getGetObservation().getResult().getComparisonOps());
        assertThat(decode, instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) decode;
        assertThat(filter.getOperator(), is(ComparisonOperator.PropertyIsLike));
        assertThat(filter.getWildCard(), is("*"));
        assertThat(filter.getSingleChar(), is("?"));
        assertThat(filter.getEscapeString(), is("\\"));
        assertThat(filter.getValueReference(), is("QualityFlag"));
        assertThat(filter.getValue(), is("4/_(2)%"));
    }
    
    @Test
    public void parsePropertyIsNull() throws XmlException, IOException, OwsExceptionReport {
        XmlObject parse = XmlObject.Factory.parse(getClass().getResourceAsStream("/GetObsPropertyIsNull.xml"));
        Object decode = decoder.decode(((GetObservationDocument)parse).getGetObservation().getResult().getComparisonOps());
        assertThat(decode, instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) decode;
        assertThat(filter.getOperator(), is(ComparisonOperator.PropertyIsNull));
        assertThat(filter.getValueReference(), is("IsNull"));
    }
    
    @Test
    public void parsePropertyIsBetween() throws XmlException, IOException, OwsExceptionReport {
        XmlObject parse = XmlObject.Factory.parse(getClass().getResourceAsStream("/GetObsPropertyIsBetween.xml"));
        Object decode = decoder.decode(((GetObservationDocument)parse).getGetObservation().getResult().getComparisonOps());
        assertThat(decode, instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) decode;
        assertThat(filter.getOperator(), is(ComparisonOperator.PropertyIsBetween));
        assertThat(filter.getValueReference(), is("IsBetween"));
        assertThat(filter.getValue(), is("1"));
        assertThat(filter.getValueUpper(), is("10"));
    }
    
    @Test
    public void parsePropertyIsEqualTo() throws XmlException, IOException, OwsExceptionReport {
        XmlObject parse = XmlObject.Factory.parse(getClass().getResourceAsStream("/GetObsPropertyIsEqualTo.xml"));
        Object decode = decoder.decode(((GetObservationDocument)parse).getGetObservation().getResult().getComparisonOps());
        assertThat(decode, instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) decode;
        assertThat(filter.getOperator(), is(ComparisonOperator.PropertyIsEqualTo));
        assertThat(filter.getValueReference(), is("IsEqualTo"));
        assertThat(filter.getValue(), is("52"));
        assertThat(filter.isMatchCase(), is(false));
    }
    
    @Test
    public void parsePropertyIsNotEqualTo() throws XmlException, IOException, OwsExceptionReport {
        XmlObject parse = XmlObject.Factory.parse(getClass().getResourceAsStream("/GetObsPropertyIsNotEqualTo.xml"));
        Object decode = decoder.decode(((GetObservationDocument)parse).getGetObservation().getResult().getComparisonOps());
        assertThat(decode, instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) decode;
        assertThat(filter.getOperator(), is(ComparisonOperator.PropertyIsNotEqualTo));
        assertThat(filter.getValueReference(), is("IsNotEqualTo"));
        assertThat(filter.getValue(), is("52"));
        assertThat(filter.isMatchCase(), is(true));
    }
    
    @Test
    public void parsePropertyIsLessThan() throws XmlException, IOException, OwsExceptionReport {
        XmlObject parse = XmlObject.Factory.parse(getClass().getResourceAsStream("/GetObsPropertyIsLessThan.xml"));
        Object decode = decoder.decode(((GetObservationDocument)parse).getGetObservation().getResult().getComparisonOps());
        assertThat(decode, instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) decode;
        assertThat(filter.getOperator(), is(ComparisonOperator.PropertyIsLessThan));
        assertThat(filter.getValueReference(), is("IsLessThan"));
        assertThat(filter.getValue(), is("7.52"));
        assertThat(filter.isMatchCase(), is(false));
    }
    
    @Test
    public void parsePropertyIsGreaterThan() throws XmlException, IOException, OwsExceptionReport {
        XmlObject parse = XmlObject.Factory.parse(getClass().getResourceAsStream("/GetObsPropertyIsGreaterThan.xml"));
        Object decode = decoder.decode(((GetObservationDocument)parse).getGetObservation().getResult().getComparisonOps());
        assertThat(decode, instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) decode;
        assertThat(filter.getOperator(), is(ComparisonOperator.PropertyIsGreaterThan));
        assertThat(filter.getValueReference(), is("IsGreaterThan"));
        assertThat(filter.getValue(), is("52.7"));
        assertThat(filter.isMatchCase(), is(false));
    }
    
    @Test
    public void parsePropertyIsLessThanOrEqualTo() throws XmlException, IOException, OwsExceptionReport {
        XmlObject parse = XmlObject.Factory.parse(getClass().getResourceAsStream("/GetObsPropertyIsLessThanOrEqualTo.xml"));
        Object decode = decoder.decode(((GetObservationDocument)parse).getGetObservation().getResult().getComparisonOps());
        assertThat(decode, instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) decode;
        assertThat(filter.getOperator(), is(ComparisonOperator.PropertyIsLessThanOrEqualTo));
        assertThat(filter.getValueReference(), is("IsLessThanOrEqualTo"));
        assertThat(filter.getValue(), is("7.52"));
        assertThat(filter.isMatchCase(), is(true));
    }
    
    @Test
    public void parsePropertyIsGreaterThanOrEqualTo() throws XmlException, IOException, OwsExceptionReport {
        XmlObject parse = XmlObject.Factory.parse(getClass().getResourceAsStream("/GetObsPropertyIsGreaterThanOrEqualTo.xml"));
        Object decode = decoder.decode(((GetObservationDocument)parse).getGetObservation().getResult().getComparisonOps());
        assertThat(decode, instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) decode;
        assertThat(filter.getOperator(), is(ComparisonOperator.PropertyIsGreaterThanOrEqualTo));
        assertThat(filter.getValueReference(), is("IsGreaterThanOrEqualTo"));
        assertThat(filter.getValue(), is("52.7"));
        assertThat(filter.isMatchCase(), is(true));
    }
    
}
