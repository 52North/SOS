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

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.CountDocument.Count;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.EnvelopeType;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.sos.AbstractBeforeAfterClassSettingsManagerTest;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.swe.RangeValue;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweEnvelope;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweSimpleDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuality;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.util.DateTimeHelper;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 * @since 4.0.0
 */
public class SweCommonEncoderv101Test extends AbstractBeforeAfterClassSettingsManagerTest {

    @Test
    public final void should_encode_simpleDataRecord() throws OwsExceptionReport {
        final XmlObject encode = new SweCommonEncoderv101().encode(new SweSimpleDataRecord());

        assertThat(encode, instanceOf(SimpleDataRecordType.class));
    }

    @Test
    public void should_encode_simpleDataRecordWithFields() throws OwsExceptionReport {
        final String field0Value = "field-0-value";
        final String field0Name = "field-0";
        final String field1Name = "field-1";
        final Boolean field1Value = Boolean.TRUE;

        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweSimpleDataRecord().addField(
                        new SweField(field0Name, new SweText().setValue(field0Value))).addField(
                        new SweField(field1Name, new SweBoolean().setValue(field1Value))));

        assertThat(encode, instanceOf(SimpleDataRecordType.class));

        final SimpleDataRecordType xbSimpleDataRecord = (SimpleDataRecordType) encode;
        final AnyScalarPropertyType field0 = xbSimpleDataRecord.getFieldArray(0);
        final AnyScalarPropertyType field1 = xbSimpleDataRecord.getFieldArray(1);

        assertThat(xbSimpleDataRecord.getFieldArray().length, is(2));
        assertThat(field0.isSetText(), is(TRUE));
        assertThat(field0.getName(), is(field0Name));
        assertThat(field0.getText().getValue(), is(field0Value));
        assertThat(field1.isSetBoolean(), is(TRUE));
        assertThat(field1.getName(), is(field1Name));
        assertThat(field1.getBoolean().getValue(), is(field1Value));
    }

    @Test
    public void should_encode_simpleDatarecord_with_fieldBoolean() throws OwsExceptionReport {
        final String field1Name = "field-1";
        final Boolean field1Value = Boolean.TRUE;

        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweSimpleDataRecord().addField(new SweField(field1Name,
                        new SweBoolean().setValue(field1Value))));

        assertThat(encode, instanceOf(SimpleDataRecordType.class));

        final SimpleDataRecordType xbSimpleDataRecord = (SimpleDataRecordType) encode;
        final AnyScalarPropertyType field1 = xbSimpleDataRecord.getFieldArray(0);

        assertThat(xbSimpleDataRecord.getFieldArray().length, is(1));
        assertThat(field1.isSetBoolean(), is(TRUE));
        assertThat(field1.getName(), is(field1Name));
        assertThat(field1.getBoolean().getValue(), is(field1Value));
    }

    @Test
    public void should_encode_Datarecord_with_fieldText() throws OwsExceptionReport {

        final String field1Name = "test-name";
        final String field1Value = "test-value";
        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweDataRecord().addField(new SweField(field1Name, new SweText()
                        .setValue(field1Value))));
        assertThat(encode, is(instanceOf(DataRecordType.class)));

        final DataRecordType xbDataRecord = (DataRecordType) encode;
        final DataComponentPropertyType field1 = xbDataRecord.getFieldArray(0);

        assertThat(xbDataRecord.getFieldArray().length, is(1));
        assertThat(field1.isSetText(), is(TRUE));
        assertThat(field1.getName(), is(field1Name));
        assertThat(field1.getText().getValue(), is(field1Value));
    }

    @Test
    public void should_encode_Datarecord_with_fieldBoolean() throws OwsExceptionReport {

        final String field1Name = "test-name";
        final boolean field1Value = true;
        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweDataRecord().addField(new SweField(field1Name,
                        new SweBoolean().setValue(field1Value))));
        assertThat(encode, is(instanceOf(DataRecordType.class)));

        final DataRecordType xbDataRecord = (DataRecordType) encode;
        final DataComponentPropertyType field1 = xbDataRecord.getFieldArray(0);

        assertThat(xbDataRecord.getFieldArray().length, is(1));
        assertThat(field1.isSetBoolean(), is(TRUE));
        assertThat(field1.getName(), is(field1Name));
        assertThat(field1.getBoolean().getValue(), is(field1Value));
    }

    @Test
    public void should_encode_Datarecord_with_fieldCategory() throws OwsExceptionReport {

        final String field1Name = "test-name";
        final String field1Value = "test-value";
        final String codeSpace = "test-codespace";
        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweDataRecord().addField(new SweField(field1Name,
                        new SweCategory().setCodeSpace(codeSpace).setValue(field1Value))));
        assertThat(encode, is(instanceOf(DataRecordType.class)));

        final DataRecordType xbDataRecord = (DataRecordType) encode;
        final DataComponentPropertyType field1 = xbDataRecord.getFieldArray(0);

        assertThat(xbDataRecord.getFieldArray().length, is(1));
        assertThat(field1.isSetCategory(), is(TRUE));
        assertThat(field1.getName(), is(field1Name));
        assertThat(field1.getCategory().getValue(), is(field1Value));
        assertThat(field1.getCategory().getCodeSpace().getHref(), is(codeSpace));
    }

    @Test
    public void should_encode_Datarecord_with_fieldCount() throws OwsExceptionReport {

        final String field1Name = "test-name";
        final int field1Value = 52;
        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweDataRecord().addField(new SweField(field1Name, new SweCount()
                        .setValue(field1Value))));
        assertThat(encode, is(instanceOf(DataRecordType.class)));

        final DataRecordType xbDataRecord = (DataRecordType) encode;
        final DataComponentPropertyType field1 = xbDataRecord.getFieldArray(0);

        assertThat(xbDataRecord.getFieldArray().length, is(1));
        assertThat(field1.isSetCount(), is(TRUE));
        assertThat(field1.getName(), is(field1Name));
        assertThat(field1.getCount().getValue(), is(BigInteger.valueOf(field1Value)));
    }

    @Test
    public void should_encode_Datarecord_with_fieldQuantity() throws OwsExceptionReport {

        final String field1Name = "test-name";
        final double field1Value = 52.0;
        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweDataRecord().addField(new SweField(field1Name,
                        new SweQuantity().setValue(field1Value))));
        assertThat(encode, is(instanceOf(DataRecordType.class)));

        final DataRecordType xbDataRecord = (DataRecordType) encode;
        final DataComponentPropertyType field1 = xbDataRecord.getFieldArray(0);

        assertThat(xbDataRecord.getFieldArray().length, is(1));
        assertThat(field1.isSetQuantity(), is(TRUE));
        assertThat(field1.getName(), is(field1Name));
        assertThat(field1.getQuantity().getValue(), is(field1Value));
    }

    @Test
    public void should_encode_Datarecord_with_fieldTimeRange() throws OwsExceptionReport {

        final String field1Name = "test-name";
        final RangeValue<DateTime> field1Value = new RangeValue<DateTime>();
        final long now = System.currentTimeMillis();
        final DateTime rangeStart = new DateTime(now - 1000);
        final DateTime rangeEnd = new DateTime(now + 1000);
        field1Value.setRangeStart(rangeStart);
        field1Value.setRangeEnd(rangeEnd);
        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweDataRecord().addField(new SweField(field1Name,
                        new SweTimeRange().setValue(field1Value))));
        assertThat(encode, is(instanceOf(DataRecordType.class)));

        final DataRecordType xbDataRecord = (DataRecordType) encode;

        assertThat(xbDataRecord.getFieldArray().length, is(1));

        final DataComponentPropertyType field1 = xbDataRecord.getFieldArray(0);

        assertThat(field1.isSetTimeRange(), is(TRUE));

        final DateTime xbTimeRangeStart =
                new DateTime(((XmlCalendar) field1.getTimeRange().getValue().get(0)).getTimeInMillis());
        final DateTime xbTimeRangeEnd =
                new DateTime(((XmlCalendar) field1.getTimeRange().getValue().get(1)).getTimeInMillis());

        assertThat(field1.getName(), is(field1Name));
        assertThat(xbTimeRangeStart, is(field1Value.getRangeStart()));
        assertThat(xbTimeRangeEnd, is(field1Value.getRangeEnd()));
    }

    @Test
    public void should_encode_Datarecord_with_fieldTime() throws OwsExceptionReport {

        final String field1Name = "test-name";
        final DateTime field1Value = new DateTime(System.currentTimeMillis());
        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweDataRecord().addField(new SweField(field1Name, new SweTime()
                        .setValue(field1Value))));
        assertThat(encode, is(instanceOf(DataRecordType.class)));

        final DataRecordType xbDataRecord = (DataRecordType) encode;
        final DataComponentPropertyType field1 = xbDataRecord.getFieldArray(0);

        assertThat(xbDataRecord.getFieldArray().length, is(1));
        assertThat(field1.isSetTime(), is(TRUE));
        assertThat(field1.getName(), is(field1Name));
        final DateTime xbTime = new DateTime(((XmlCalendar) field1.getTime().getValue()).getTimeInMillis(), DateTimeZone.UTC);

        assertThat(xbTime.toDateTime(field1Value.getZone()), is(field1Value));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void should_throw_NoApplicableCodeException_with_DataRecord_and_field_with_not_supported_element()
            throws OwsExceptionReport {
        thrown.expect(NoApplicableCodeException.class);
        thrown.expectMessage("The element type 'org.n52.sos.encode.SweCommonEncoderv101Test$1' "
                + "of the received 'org.n52.sos.ogc.swe.SweField' is not supported"
                + " by this encoder 'org.n52.sos.encode.SweCommonEncoderv101'.");
        new SweCommonEncoderv101().encode(new SweDataRecord().addField(new SweField("test",
                new SweAbstractDataComponent() {

                    @Override
                    public SweDataComponentType getDataComponentType() {
                        return null;
                    }
                })));
    }

    @Test
    public void should_encode_simpleDatarecord_with_fieldText() throws OwsExceptionReport {
        final String field1Name = "field-1";
        final String field1Value = "field-1-value";

        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweSimpleDataRecord().addField(new SweField(field1Name,
                        new SweText().setValue(field1Value))));

        assertThat(encode, instanceOf(SimpleDataRecordType.class));

        final SimpleDataRecordType xbSimpleDataRecord = (SimpleDataRecordType) encode;
        final AnyScalarPropertyType field1 = xbSimpleDataRecord.getFieldArray(0);

        assertThat(xbSimpleDataRecord.getFieldArray().length, is(1));
        assertThat(field1.isSetText(), is(TRUE));
        assertThat(field1.getName(), is(field1Name));
        assertThat(field1.getText().getValue(), is(field1Value));
    }

    @Test
    public void should_encode_simpleDatarecord_with_fieldCategory() throws OwsExceptionReport {
        final String name = "field-1";
        final String value = "field-1-value";

        final String codeSpace = "field-1-codespace";
        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweSimpleDataRecord().addField(new SweField(name,
                        new SweCategory().setValue(value).setCodeSpace(codeSpace))));

        assertThat(encode, instanceOf(SimpleDataRecordType.class));

        final SimpleDataRecordType xbSimpleDataRecord = (SimpleDataRecordType) encode;
        final AnyScalarPropertyType field1 = xbSimpleDataRecord.getFieldArray(0);

        assertThat(xbSimpleDataRecord.getFieldArray().length, is(1));
        assertThat(field1.isSetCategory(), is(TRUE));
        assertThat(field1.getName(), is(name));
        assertThat(field1.getCategory().getValue(), is(value));
        assertThat(field1.getCategory().isSetCodeSpace(), is(TRUE));
        assertThat(field1.getCategory().getCodeSpace().isSetHref(), is(TRUE));
    }

    @Test
    public void should_encode_simpleDatarecord_with_fieldCount() throws OwsExceptionReport {
        final String name = "field-1";
        final int value = 42;

        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweSimpleDataRecord().addField(new SweField(name, new SweCount()
                        .setValue(value))));

        assertThat(encode, instanceOf(SimpleDataRecordType.class));

        final SimpleDataRecordType xbSimpleDataRecord = (SimpleDataRecordType) encode;
        final AnyScalarPropertyType field1 = xbSimpleDataRecord.getFieldArray(0);

        assertThat(xbSimpleDataRecord.getFieldArray().length, is(1));
        assertThat(field1.getName(), is(name));
        assertThat(field1.isSetCount(), is(TRUE));
        assertThat(field1.getCount().getValue().intValue(), is(value));
    }

    @Test
    public void should_encode_simpleDatarecord_with_fieldQuantity() throws OwsExceptionReport {
        final String name = "field-1";
        final double value = 42.5;

        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweSimpleDataRecord().addField(new SweField(name,
                        new SweQuantity().setValue(value))));

        assertThat(encode, instanceOf(SimpleDataRecordType.class));

        final SimpleDataRecordType xbSimpleDataRecord = (SimpleDataRecordType) encode;
        final AnyScalarPropertyType field1 = xbSimpleDataRecord.getFieldArray(0);

        assertThat(xbSimpleDataRecord.getFieldArray().length, is(1));
        assertThat(field1.getName(), is(name));
        assertThat(field1.isSetQuantity(), is(TRUE));
        assertThat(field1.getQuantity().getValue(), is(value));
    }

    @Test
    public void should_encode_simpleDatarecord_with_fieldTime() throws OwsExceptionReport {
        final String name = "field-1";
        final DateTime value = new DateTime(DateTimeZone.UTC);

        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweSimpleDataRecord().addField(new SweField(name, new SweTime()
                        .setValue(value))));

        assertThat(encode, instanceOf(SimpleDataRecordType.class));

        final SimpleDataRecordType xbSimpleDataRecord = (SimpleDataRecordType) encode;
        final AnyScalarPropertyType field1 = xbSimpleDataRecord.getFieldArray(0);

        assertThat(xbSimpleDataRecord.getFieldArray().length, is(1));
        assertThat(field1.getName(), is(name));
        assertThat(field1.isSetTime(), is(TRUE));
        assertThat(DateTimeHelper.parseIsoString2DateTime(field1.getTime().getValue().toString()).toString(), is(value.toString()));
    }

    @Test
    public void should_encode_simpleDatarecord_with_quantities() throws OwsExceptionReport {
        final String name = "field-1";
        final String unit = "m";
        final Double value = 1.1;
        final String name2 = "field-2";
        final String unit2 = "urn:ogc:def:uom:UCUM::m";
        final Double value2 = 1.2;

        final XmlObject encode =
                new SweCommonEncoderv101().encode(new SweSimpleDataRecord().addField(
                        new SweField(name, new SweQuantity().setUom(unit).setValue(value))).addField(
                        new SweField(name2, new SweQuantity().setUom(unit2).setValue(value2))));

        assertThat(encode, instanceOf(SimpleDataRecordType.class));
        final SimpleDataRecordType xbSimpleDataRecord = (SimpleDataRecordType) encode;
        assertThat(xbSimpleDataRecord.getFieldArray().length, is(2));

        final AnyScalarPropertyType field1 = xbSimpleDataRecord.getFieldArray(0);
        final AnyScalarPropertyType field2 = xbSimpleDataRecord.getFieldArray(1);

        // unit in code
        assertThat(field1.getName(), is(name));
        assertThat(field1.isSetQuantity(), is(TRUE));
        assertThat(field1.getQuantity().getValue(), is(value));
        assertThat(field1.getQuantity().getUom().getCode(), is(unit));

        // unit in href
        assertThat(field2.getName(), is(name2));
        assertThat(field2.isSetQuantity(), is(TRUE));
        assertThat(field2.getQuantity().getValue(), is(value2));
        assertThat(field2.getQuantity().getUom().getHref(), is(unit2));
    }

    @Test(expected = NoApplicableCodeException.class)
    public void should_throw_exception_if_received_simpleDataRecord_with_field_with_null_element()
            throws OwsExceptionReport {
        new SweCommonEncoderv101().encode(new SweSimpleDataRecord().addField(new SweField("field-name", null)));
    }

    @Test public void
    should_encode_count_with_quality_text()
    		throws OwsExceptionReport {
    	final String qualityTextValue = "quality-text-value";
		final SweCount sosCount = (SweCount) new SweCount().setQuality(Lists.newArrayList((SweQuality)new SweText().setValue(qualityTextValue)));


		final XmlObject encode = new SweCommonEncoderv101().encode(sosCount);

		assertThat(encode, instanceOf(Count.class));

		final Count xbCount = (Count) encode;
		assertThat(xbCount.getQualityArray(), is(not(nullValue())));
		assertThat(xbCount.getQualityArray().length, is(1));
		assertThat(xbCount.getQualityArray(0).isSetText(), is(true));
		assertThat(xbCount.getQualityArray(0).getText().getValue(),is(qualityTextValue));
    }
    
    @Test public void
    should_encode_count_with_quality_Category()
    		throws OwsExceptionReport {
    	final String qualityCategoryValue = "quality-category-value";
		final SweCount sosCount = (SweCount) new SweCount().setQuality(Lists.newArrayList((SweQuality)new SweCategory().setValue(qualityCategoryValue)));


		final XmlObject encode = new SweCommonEncoderv101().encode(sosCount);

		assertThat(encode, instanceOf(Count.class));

		final Count xbCount = (Count) encode;
		assertThat(xbCount.getQualityArray(), is(not(nullValue())));
		assertThat(xbCount.getQualityArray().length, is(1));
		assertThat(xbCount.getQualityArray(0).isSetCategory(), is(true));
		assertThat(xbCount.getQualityArray(0).getCategory().getValue(),is(qualityCategoryValue));
    }
    
    @Test public void
    should_encode_count_with_quality_Quantity()
    		throws OwsExceptionReport {
    	final double qualityQuantityValue = 42.0;
		final SweCount sosCount = (SweCount) new SweCount().setQuality(Lists.newArrayList((SweQuality)new SweQuantity().setValue(qualityQuantityValue)));


		final XmlObject encode = new SweCommonEncoderv101().encode(sosCount);

		assertThat(encode, instanceOf(Count.class));

		final Count xbCount = (Count) encode;
		assertThat(xbCount.getQualityArray(), is(not(nullValue())));
		assertThat(xbCount.getQualityArray().length, is(1));
		assertThat(xbCount.getQualityArray(0).isSetQuantity(), is(true));
		assertThat(xbCount.getQualityArray(0).getQuantity().getValue(),is(qualityQuantityValue));
    }
    
    @Test public void
    should_encode_count_with_quality_QuantityRange()
    		throws OwsExceptionReport {
    	final RangeValue<Double> qualityQuantityRangeValue = new RangeValue<Double>(1.0, 2.0);
		final SweCount sosCount = (SweCount) new SweCount().setQuality(Lists.newArrayList((SweQuality)new SweQuantityRange().setValue(qualityQuantityRangeValue)));


		final XmlObject encode = new SweCommonEncoderv101().encode(sosCount);

		assertThat(encode, instanceOf(Count.class));

		final Count xbCount = (Count) encode;
		assertThat(xbCount.getQualityArray(), is(not(nullValue())));
		assertThat(xbCount.getQualityArray().length, is(1));
		assertThat(xbCount.getQualityArray(0).isSetQuantityRange(), is(true));
		assertThat((Double)xbCount.getQualityArray(0).getQuantityRange().getValue().get(0),is(qualityQuantityRangeValue.getRangeStart()));
		assertThat((Double)xbCount.getQualityArray(0).getQuantityRange().getValue().get(1),is(qualityQuantityRangeValue.getRangeEnd()));
    }

    @Test
    public void should_encode_SosEnvelope() throws OwsExceptionReport {
        final int srid = 4326;
        final double y1 = 7.0;
        final double x1 = 51.0;
        final double y2 = 8.0;
        final double x2 = 52.0;
        final String uom = "test-uom";
        final String definition = "test-definition";
        final SweEnvelope sweEnvelope = new SweEnvelope(new SosEnvelope(new Envelope(x1, x2, y1, y2), srid), uom);
        final String xAxisId = "x";
        final String yAxisId = "y";
        final String northing = "northing";
        final String easting = "easting";
        sweEnvelope.setDefinition(definition);

        final XmlObject encode = new SweCommonEncoderv101().encode(sweEnvelope);

        assertThat(encode, instanceOf(EnvelopeType.class));

        final EnvelopeType xbEnvelope = (EnvelopeType) encode;
        assertThat(xbEnvelope.isSetDefinition(), is(true));
        assertThat(xbEnvelope.getDefinition(), is(definition));

        final Coordinate lcX = xbEnvelope.getLowerCorner().getVector().getCoordinateArray(0);
        assertThat(lcX.getName(), is(easting));
        assertThat(lcX.getQuantity().getAxisID(), is(xAxisId));
        assertThat(lcX.getQuantity().getUom().getCode(), is(uom));
        assertThat(lcX.getQuantity().getValue(), is(y1));

        final Coordinate lcY = xbEnvelope.getLowerCorner().getVector().getCoordinateArray(1);
        assertThat(lcY.getName(), is(northing));
        assertThat(lcY.getQuantity().getAxisID(), is(yAxisId));
        assertThat(lcY.getQuantity().getUom().getCode(), is(uom));
        assertThat(lcY.getQuantity().getValue(), is(x1));

        final Coordinate ucX = xbEnvelope.getUpperCorner().getVector().getCoordinateArray(0);
        assertThat(ucX.getName(), is(easting));
        assertThat(ucX.getQuantity().getAxisID(), is(xAxisId));
        assertThat(ucX.getQuantity().getUom().getCode(), is(uom));
        assertThat(ucX.getQuantity().getValue(), is(y2));

        final Coordinate ucY = xbEnvelope.getUpperCorner().getVector().getCoordinateArray(1);
        assertThat(ucY.getName(), is(northing));
        assertThat(ucY.getQuantity().getAxisID(), is(yAxisId));
        assertThat(ucY.getQuantity().getUom().getCode(), is(uom));
        assertThat(ucY.getQuantity().getValue(), is(x2));
		
		assertThat(xbEnvelope.isSetReferenceFrame(), is(true));
		assertThat(xbEnvelope.getReferenceFrame(), is(""+srid));
    }
}
