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
package org.n52.sos.decode.json.impl;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONUtils;
import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweCountRange;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.util.DateTimeHelper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class FieldDecoderTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    private static final String DEFINITION = "definition";

    private static final String NAME = "name";

    private static final String DESCRIPTION = "description";

    private static final String IDENTIFIER = "identifier";

    private static final String LABEL = "label";

    private static final String UOM = "uom";

    private static final String TIME_START = "2013-08-02T14:43:05+0200";

    private static final String TIME_END = "2013-08-02T14:48:05+0200";

    private static final int COUNT_VALUE_START = 12;

    private static final int COUNT_VALUE_END = 13;

    private static final String OBSERVED_PROPERTY_VALUE = "obsProp";

    private static final String TEXT_VALUE = "text";

    private static final double QUANTITY_VALUE_START = 52.2;

    private static final double QUANTITY_VALUE_END = 52.3;

    private static final String CODESPACE = "codespace";

    private static final String CATEGORY_VALUE = "category";

    private final ErrorCollector errors = new ErrorCollector();

    private DateTime timeStart;

    private DateTime timeEnd;

    private JSONValidator validator;

    private FieldDecoder decoder;

    @Before
    public void before() throws OwsExceptionReport {
        this.decoder = new FieldDecoder();
        this.timeStart = DateTimeHelper.parseIsoString2DateTime(TIME_START);
        this.timeEnd = DateTimeHelper.parseIsoString2DateTime(TIME_END);
        this.validator = JSONValidator.getInstance();
    }

    @Test
    public void testCountWithValue() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.COUNT_TYPE).put(JSONConstants.VALUE,
                        COUNT_VALUE_START);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweCount.class)));
        SweCount swe = (SweCount) field.getElement();
        errors.checkThat(swe.getValue(), is(COUNT_VALUE_START));
    }

    @Test
    public void testCount() throws OwsExceptionReport {
        ObjectNode json = createField().put(JSONConstants.TYPE, JSONConstants.COUNT_TYPE);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweCount.class)));
    }

    @Test
    public void testBooleanWithValueTrue() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.BOOLEAN_TYPE).put(JSONConstants.VALUE, true);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweBoolean.class)));
        SweBoolean swe = (SweBoolean) field.getElement();
        errors.checkThat(swe.getValue(), is(true));
    }

    @Test
    public void testBooleanWithValueFalse() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.BOOLEAN_TYPE).put(JSONConstants.VALUE, false);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweBoolean.class)));
        SweBoolean swe = (SweBoolean) field.getElement();
        errors.checkThat(swe.getValue(), is(false));
    }

    @Test
    public void testBoolean() throws OwsExceptionReport {
        ObjectNode json = createField().put(JSONConstants.TYPE, JSONConstants.BOOLEAN_TYPE);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweBoolean.class)));
    }

    @Test
    public void testCountRangeWithValue() throws OwsExceptionReport {
        ObjectNode json = createField().put(JSONConstants.TYPE, JSONConstants.COUNT_RANGE_TYPE);
        json.putArray(JSONConstants.VALUE).add(COUNT_VALUE_START).add(COUNT_VALUE_END);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweCountRange.class)));
        SweCountRange swe = (SweCountRange) field.getElement();
        assertThat(swe.getValue(), is(notNullValue()));
        errors.checkThat(swe.getValue().getRangeStart(), is(COUNT_VALUE_START));
        errors.checkThat(swe.getValue().getRangeEnd(), is(COUNT_VALUE_END));
    }

    @Test
    public void testCountRange() throws OwsExceptionReport {
        ObjectNode json = createField().put(JSONConstants.TYPE, JSONConstants.COUNT_RANGE_TYPE);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweCountRange.class)));
        SweCountRange swe = (SweCountRange) field.getElement();
        assertThat(swe.getValue(), is(nullValue()));
    }

    @Test
    public void testObservablePropertyWithValue() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.OBSERVABLE_PROPERTY_TYPE).put(JSONConstants.VALUE,
                        OBSERVED_PROPERTY_VALUE);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweObservableProperty.class)));
        SweObservableProperty swe = (SweObservableProperty) field.getElement();
        errors.checkThat(swe.getValue(), is(OBSERVED_PROPERTY_VALUE));
    }

    @Test
    public void testObservableProperty() throws OwsExceptionReport {
        ObjectNode json = createField().put(JSONConstants.TYPE, JSONConstants.OBSERVABLE_PROPERTY_TYPE);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweObservableProperty.class)));
        SweObservableProperty swe = (SweObservableProperty) field.getElement();
        errors.checkThat(swe.getValue(), is(nullValue()));
    }

    @Test
    @Ignore("not yet supported")
    public void testQualityWithValue() {
    }

    @Test
    @Ignore("not yet supported")
    public void testQuality() {
    }

    @Test
    public void testTextWithValue() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.TEXT_TYPE).put(JSONConstants.VALUE, TEXT_VALUE);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweText.class)));
        SweText swe = (SweText) field.getElement();
        errors.checkThat(swe.getValue(), is(TEXT_VALUE));
    }

    @Test
    public void testText() throws OwsExceptionReport {
        ObjectNode json = createField().put(JSONConstants.TYPE, JSONConstants.TEXT_TYPE);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweText.class)));
        SweText swe = (SweText) field.getElement();
        errors.checkThat(swe.getValue(), is(nullValue()));
    }

    @Test
    public void testQuantityWithValue() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.QUANTITY_TYPE).put(JSONConstants.UOM, UOM)
                        .put(JSONConstants.VALUE, QUANTITY_VALUE_START);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweQuantity.class)));
        SweQuantity swe = (SweQuantity) field.getElement();
        errors.checkThat(swe.getValue(), is(QUANTITY_VALUE_START));
        errors.checkThat(swe.getUom(), is(UOM));
    }

    @Test
    public void testQuantity() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.QUANTITY_TYPE).put(JSONConstants.UOM, UOM);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweQuantity.class)));
        SweQuantity swe = (SweQuantity) field.getElement();
        errors.checkThat(swe.getUom(), is(UOM));
    }

    @Test
    public void testQuantityRangeWithValue() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.QUANTITY_RANGE_TYPE).put(JSONConstants.UOM, UOM);
        json.putArray(JSONConstants.VALUE).add(QUANTITY_VALUE_START).add(QUANTITY_VALUE_END);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweQuantityRange.class)));
        SweQuantityRange swe = (SweQuantityRange) field.getElement();
        errors.checkThat(swe.getUom(), is(UOM));
        errors.checkThat(swe.getValue(), is(notNullValue()));
        errors.checkThat(swe.getValue().getRangeStart(), is(QUANTITY_VALUE_START));
        errors.checkThat(swe.getValue().getRangeEnd(), is(QUANTITY_VALUE_END));
    }

    @Test
    public void testQuantityRange() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.QUANTITY_RANGE_TYPE).put(JSONConstants.UOM, UOM);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweQuantityRange.class)));
        SweQuantityRange swe = (SweQuantityRange) field.getElement();
        errors.checkThat(swe.getUom(), is(UOM));
        errors.checkThat(swe.getValue(), is(nullValue()));
    }

    @Test
    public void timeWithValue() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.TIME_TYPE).put(JSONConstants.UOM, UOM)
                        .put(JSONConstants.VALUE, TIME_START);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweTime.class)));
        SweTime swe = (SweTime) field.getElement();
        errors.checkThat(swe.getValue(), is(timeStart));
        errors.checkThat(swe.getUom(), is(UOM));
    }

    @Test
    public void time() throws OwsExceptionReport {
        ObjectNode json = createField().put(JSONConstants.TYPE, JSONConstants.TIME_TYPE).put(JSONConstants.UOM, UOM);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweTime.class)));
        SweTime swe = (SweTime) field.getElement();
        errors.checkThat(swe.getValue(), is(nullValue()));
        errors.checkThat(swe.getUom(), is(UOM));
    }

    @Test
    public void timeRangeWithValue() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.TIME_RANGE_TYPE).put(JSONConstants.UOM, UOM);
        json.putArray(JSONConstants.VALUE).add(TIME_START).add(TIME_END);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweTimeRange.class)));
        SweTimeRange swe = (SweTimeRange) field.getElement();
        errors.checkThat(swe.getUom(), is(UOM));
        errors.checkThat(swe.getValue(), is(notNullValue()));
        errors.checkThat(swe.getValue().getRangeStart(), is(timeStart));
        errors.checkThat(swe.getValue().getRangeEnd(), is(timeEnd));

    }

    @Test
    public void timeRange() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.TIME_RANGE_TYPE).put(JSONConstants.UOM, UOM);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweTimeRange.class)));
        SweTimeRange swe = (SweTimeRange) field.getElement();
        errors.checkThat(swe.getUom(), is(UOM));
        errors.checkThat(swe.getValue(), is(nullValue()));

    }

    @Test
    public void testCategoryWithValue() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.CATEGORY_TYPE)
                        .put(JSONConstants.CODESPACE, CODESPACE).put(JSONConstants.VALUE, CATEGORY_VALUE);
        SweField field = checkCommon(json, true);
        assertThat(field.getElement(), is(instanceOf(SweCategory.class)));
        SweCategory swe = (SweCategory) field.getElement();
        errors.checkThat(swe.getValue(), is(CATEGORY_VALUE));
        errors.checkThat(swe.getCodeSpace(), is(CODESPACE));
    }

    @Test
    public void testCategory() throws OwsExceptionReport {
        ObjectNode json =
                createField().put(JSONConstants.TYPE, JSONConstants.CATEGORY_TYPE).put(JSONConstants.CODESPACE,
                        CODESPACE);
        SweField field = checkCommon(json, false);
        assertThat(field.getElement(), is(instanceOf(SweCategory.class)));
        SweCategory swe = (SweCategory) field.getElement();
        errors.checkThat(swe.getValue(), is(nullValue()));
        errors.checkThat(swe.getCodeSpace(), is(CODESPACE));
    }

    protected SweField validateWithValueAndDecode(ObjectNode json, boolean withValue) throws OwsExceptionReport {
        ProcessingReport report =
                validator.validate(json, withValue ? SchemaConstants.Common.FIELD_WITH_VALUE
                        : SchemaConstants.Common.FIELD);
        if (!report.isSuccess()) {
            System.err.println(validator.encode(report, json));
            fail("Invalid generated field!");
        }
        return decoder.decode(json);
    }

    protected ObjectNode createField() {
        return JSONUtils.nodeFactory().objectNode().put(JSONConstants.NAME, NAME).put(JSONConstants.LABEL, LABEL)
                .put(JSONConstants.DEFINITION, DEFINITION).put(JSONConstants.DESCRIPTION, DESCRIPTION)
                .put(JSONConstants.IDENTIFIER, IDENTIFIER);
    }

    protected SweField checkCommon(ObjectNode json, boolean withValue) throws OwsExceptionReport {
        SweField field = validateWithValueAndDecode(json, withValue);
        assertThat(field, is(notNullValue()));
        errors.checkThat(field.getName().getValue(), is(NAME));
        assertThat(field.getElement(), is(notNullValue()));
        errors.checkThat(field.getElement().getDefinition(), is(DEFINITION));
        errors.checkThat(field.getElement().getDescription(), is(DESCRIPTION));
        errors.checkThat(field.getElement().getIdentifier(), is(IDENTIFIER));
        errors.checkThat(field.getElement().getLabel(), is(LABEL));
        return field;
    }

    @Rule
    public ErrorCollector getErrorCollectorRule() {
        return errors;
    }
}
