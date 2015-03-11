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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import net.opengis.swe.x20.BooleanType;
import net.opengis.swe.x20.CategoryType;
import net.opengis.swe.x20.TimeRangeDocument;
import net.opengis.swe.x20.TimeRangeType;

import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SweCommonDecoderV20Test {

    private SweCommonDecoderV20 decoder;

    private String definition = "test-definition";

    @Before
    public void initDecoder() {
        decoder = new SweCommonDecoderV20();
    }

    @After
    public void nullDecoder() {
        decoder = null;
    }

    @Test
    public void should_encode_xbBoolean_into_SosSweBoolean_with_correct_value_and_definition()
            throws OwsExceptionReport {
        BooleanType xbBoolean = BooleanType.Factory.newInstance();
        final boolean value = true;
        xbBoolean.setValue(value);
        xbBoolean.setDefinition(definition);

        Object decodedObject = decoder.decode(xbBoolean);

        assertThat(decodedObject.getClass().getName(), is(SweBoolean.class.getName()));

        SweBoolean sosBoolean = (SweBoolean) decodedObject;

        assertThat(sosBoolean.getValue(), is(value));
        assertThat(sosBoolean.getDefinition(), is(definition));
    }

    @Test
    public void should_encode_xbCategory_into_SosSweCategory_with_correct_value_definition_and_codespace()
            throws OwsExceptionReport, XmlException {
        final String codeSpace = "test-codespace";
        final String value = "test-category-value";

        CategoryType xbCategory = CategoryType.Factory.newInstance();
        xbCategory.addNewCodeSpace().setHref(codeSpace);
        xbCategory.setValue(value);
        xbCategory.setDefinition(definition);

        Object decodedObject = decoder.decode(xbCategory);

        assertThat(decodedObject.getClass().getName(), is(SweCategory.class.getName()));

        SweCategory sosCategory = (SweCategory) decodedObject;

        assertThat(sosCategory.getValue(), is(value));
        assertThat(sosCategory.getDefinition(), is(definition));
        assertThat(sosCategory.getCodeSpace(), is(codeSpace));
    }

    @Test
    public void should_decode_TimeRange() throws OwsExceptionReport {
         final TimeRangeDocument xbTimeRangeDoc = TimeRangeDocument.Factory.newInstance();
         TimeRangeType xbTimeRange = xbTimeRangeDoc.addNewTimeRange();
         final DateTime startDate = new DateTime(1970, 1, 1, 0, 0, DateTimeZone.UTC);
         final DateTime endDate = new DateTime(2013, 12, 31, 23, 59, DateTimeZone.UTC);
         final List<String> values = Lists.newArrayList(startDate.toString(), endDate.toString());
         xbTimeRange.setValue(values);        
         final String iso8601Uom = "urn:ogc:def:unit:ISO:8601";
         xbTimeRange.addNewUom().setHref(iso8601Uom);
         final Object decodedObject = new SweCommonDecoderV20().decode(xbTimeRange);
         assertThat(decodedObject, is(instanceOf(SweTimeRange.class)));
         final SweTimeRange sweTimeRange = (SweTimeRange) decodedObject;
         assertThat(sweTimeRange.isSetUom(), is(true));
         assertThat(sweTimeRange.getUom(), is(iso8601Uom));
         assertThat(sweTimeRange.isSetValue(), is(true));
         assertThat(sweTimeRange.getValue().getRangeStart(), is(startDate));
         assertThat(sweTimeRange.getValue().getRangeEnd(), is(endDate));
     }    
}
