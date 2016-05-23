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

import java.util.ArrayList;
import java.util.List;

import net.opengis.swe.x101.BooleanDocument;
import net.opengis.swe.x101.CategoryDocument;
import net.opengis.swe.x101.CountDocument;
import net.opengis.swe.x101.QuantityDocument;
import net.opengis.swe.x101.QuantityRangeDocument;
import net.opengis.swe.x101.QuantityRangeDocument.QuantityRange;
import net.opengis.swe.x101.TimeRangeDocument;
import net.opengis.swe.x101.TimeRangeDocument.TimeRange;
import net.opengis.swe.x101.UomPropertyType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.RangeValue;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;

import com.google.common.collect.Lists;

/**
 * @author Carsten Hollmann
 * 
 * @since 4.0.0
 */
public class SweCommonDecoderV101Test {


	@Test public void
	should_decode_Count_with_Quality_Text()
			throws OwsExceptionReport {
		final CountDocument xbCount = CountDocument.Factory.newInstance();

		final String textValue = "quality-text";
		xbCount.addNewCount().addNewQuality().addNewText().setValue(textValue);

		final Object decodedObject = new SweCommonDecoderV101().decode(xbCount);

		assertThat(decodedObject, is(instanceOf(SweCount.class)));
		final SweCount sweCount = (SweCount) decodedObject;
		assertThat(sweCount.isSetQuality(), is(true));
		assertThat(sweCount.getQuality().size(), is(1));
		assertThat(sweCount.getQuality().iterator().next(), is(instanceOf(SweText.class)));
		assertThat(((SweText)sweCount.getQuality().iterator().next()).getValue(),is(textValue));
	}

	@Test public void
	should_decode_Quantity_with_Quality_Category()
			throws OwsExceptionReport {
		final QuantityDocument xbQuantity = QuantityDocument.Factory.newInstance();

		final String categoryValue = "quality-category";
		xbQuantity.addNewQuantity().addNewQuality().addNewCategory().setValue(categoryValue);

		final Object decodedObject = new SweCommonDecoderV101().decode(xbQuantity);

		assertThat(decodedObject, is(instanceOf(SweQuantity.class)));
		final SweQuantity sweQuantity = (SweQuantity) decodedObject;
		assertThat(sweQuantity.isSetQuality(), is(true));
		assertThat(sweQuantity.getQuality().size(), is(1));
		assertThat(sweQuantity.getQuality().iterator().next(), is(instanceOf(SweCategory.class)));
		assertThat(((SweCategory)sweQuantity.getQuality().iterator().next()).getValue(),is(categoryValue));
	}

	@Test public void
	should_decode_Category_with_Quality_QuantityRange()
			throws OwsExceptionReport {
		final CategoryDocument xbQuantity = CategoryDocument.Factory.newInstance();

		final Double rangeStart = 1.0;
		final Double rangeEnd = 2.0;
		final ArrayList<Double> categoryValue = Lists.newArrayList(rangeStart,rangeEnd);
		xbQuantity.addNewCategory().addNewQuality().addNewQuantityRange().setValue(categoryValue);

		final Object decodedObject = new SweCommonDecoderV101().decode(xbQuantity);

		assertThat(decodedObject, is(instanceOf(SweCategory.class)));
		final SweCategory sweCategory = (SweCategory) decodedObject;
		assertThat(sweCategory.isSetQuality(), is(true));
		assertThat(sweCategory.getQuality().size(), is(1));
		assertThat(sweCategory.getQuality().iterator().next(), is(instanceOf(SweQuantityRange.class)));
		assertThat(((SweQuantityRange)sweCategory.getQuality().iterator().next()).getValue(),is(new RangeValue<Double>(rangeStart, rangeEnd)));
	}

	@Test public void
	should_decode_Boolean_with_Quality_Quantity()
			throws OwsExceptionReport {
		final BooleanDocument xbBoolean = BooleanDocument.Factory.newInstance();

		final double quantityValue = 42.0;
		xbBoolean.addNewBoolean().addNewQuality().addNewQuantity().setValue(quantityValue);

		final Object decodedObject = new SweCommonDecoderV101().decode(xbBoolean);

		assertThat(decodedObject, is(instanceOf(SweBoolean.class)));
		final SweBoolean sweBoolean = (SweBoolean) decodedObject;
		assertThat(sweBoolean.isSetQuality(), is(true));
		assertThat(sweBoolean.getQuality().size(), is(1));
		assertThat(sweBoolean.getQuality().iterator().next(), is(instanceOf(SweQuantity.class)));
		assertThat(((SweQuantity)sweBoolean.getQuality().iterator().next()).getValue(),is(quantityValue));
	}

	@Test public void
	should_decode_QuantityRange()
			throws OwsExceptionReport {
		final QuantityRangeDocument xbQuantityRange = QuantityRangeDocument.Factory.newInstance();

		final ArrayList<Double> values = Lists.newArrayList(1.0,2.0);
		final QuantityRange xbQuantityRangeType = xbQuantityRange.addNewQuantityRange();
		xbQuantityRangeType.setValue(values);
		final String definition = "definition";
		xbQuantityRangeType.setDefinition(definition);
		final String axisId = "axis-id";
		xbQuantityRangeType.setAxisID(axisId);
		final String description = "description";
		xbQuantityRangeType.addNewDescription().setStringValue(description);
		final UomPropertyType xbUom = xbQuantityRangeType.addNewUom();
		final String uomCode = "uom-code";
		xbUom.setCode(uomCode);
		final Object decodedObject = new SweCommonDecoderV101().decode(xbQuantityRange);

		assertThat(decodedObject, is(instanceOf(SweQuantityRange.class)));
		final SweQuantityRange sweQuantityRange = (SweQuantityRange) decodedObject;
		assertThat(sweQuantityRange.isSetDefinition(), is(true));
		assertThat(sweQuantityRange.getDefinition(),is(definition));
		assertThat(sweQuantityRange.isSetUom(), is(true));
		assertThat(sweQuantityRange.getUom(), is(uomCode));
		assertThat(sweQuantityRange.isSetAxisID(), is(true));
		assertThat(sweQuantityRange.getAxisID(), is(axisId));
		assertThat(sweQuantityRange.isSetDescription(), is(true));
		assertThat(sweQuantityRange.getDescription(), is(description));
		assertThat(sweQuantityRange.isSetValue(), is(true));
		assertThat(sweQuantityRange.getValue().getRangeStart(), is(values.get(0)));
		assertThat(sweQuantityRange.getValue().getRangeEnd(), is(values.get(1)));
	}

   @Test
   public void should_decode_TimeRange() throws OwsExceptionReport {
        final TimeRangeDocument xbTimeRangeDoc = TimeRangeDocument.Factory.newInstance();
        TimeRange xbTimeRange = xbTimeRangeDoc.addNewTimeRange();
        final DateTime startDate = new DateTime(1970, 1, 1, 0, 0, DateTimeZone.UTC);
        final DateTime endDate = new DateTime(2013, 12, 31, 23, 59, DateTimeZone.UTC);
        final List<String> values = Lists.newArrayList(startDate.toString(), endDate.toString());
        xbTimeRange.setValue(values);        
        final String iso8601Uom = "urn:ogc:def:unit:ISO:8601";
        xbTimeRange.addNewUom().setHref(iso8601Uom);
        final Object decodedObject = new SweCommonDecoderV101().decode(xbTimeRange);
        assertThat(decodedObject, is(instanceOf(SweTimeRange.class)));
        final SweTimeRange sweTimeRange = (SweTimeRange) decodedObject;
        assertThat(sweTimeRange.isSetUom(), is(true));
        assertThat(sweTimeRange.getUom(), is(iso8601Uom));
        assertThat(sweTimeRange.isSetValue(), is(true));
        assertThat(sweTimeRange.getValue().getRangeStart(), is(startDate));
        assertThat(sweTimeRange.getValue().getRangeEnd(), is(endDate));
    }
}
