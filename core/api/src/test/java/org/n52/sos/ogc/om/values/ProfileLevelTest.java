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
package org.n52.sos.ogc.om.values;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.n52.sos.ogc.swe.SweDataRecord;

public class ProfileLevelTest {
    
    private ProfileLevel level;

    @Before
    public void setUp() {
        level = new ProfileLevel();
        TextValue textValue1 = new TextValue("text 1");
        textValue1.setDefinition("definition");
        textValue1.setLabel("label");
        textValue1.setDescription("description");
        TextValue textValue2 = new TextValue("text 2");
        textValue2.setDefinition("definition");
        textValue2.setLabel("label");
        textValue2.setDescription("description");
        level.addValue(textValue1);
        level.addValue(textValue2);
    }
    
    @Test
    public void test_relabel_field_names() {
        SweDataRecord dataRecord = level.valueAsDataRecord();
        assertThat(dataRecord.getFields().size(), is(2));
        assertThat(dataRecord.getFields().get(0).getName().getValue(), equalTo("label_1"));
        assertThat(dataRecord.getFields().get(1).getName().getValue(), equalTo("label_2"));
    }
    
    @Test
    public void test_field_name() {
        level.getValue().remove(1);
        SweDataRecord dataRecord = level.valueAsDataRecord();
        assertThat(dataRecord.getFields().size(), is(1));
        assertThat(dataRecord.getFields().get(0).getName().getValue(), equalTo("label"));
    }

    @Test
    public void test_not_relabel_field_names() {
        ((TextValue) level.getValue().get(1)).setLabel("label_test");
        SweDataRecord dataRecord = level.valueAsDataRecord();
        assertThat(dataRecord.getFields().size(), is(2));
        assertThat(dataRecord.getFields().get(0).getName().getValue(), equalTo("label"));
        assertThat(dataRecord.getFields().get(1).getName().getValue(), equalTo("label_test"));
    }
}
