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
package org.n52.sos.ogc.om.values;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.n52.sos.ogc.gml.CodeType;
import com.google.common.collect.Lists;

public class ProfileValueTest {
    
    private ProfileValue profileValue = createProfileValue(true, true);
    
    @Test
    public void testFromLevel() {
        assertThat(profileValue.getFromLevel().getValue(), is(0.0));
    }
    
    @Test
    public void testToLevel() {
        assertThat(profileValue.getToLevel().getValue(), is(30.0));
    }
    
    private ProfileValue createProfileValue(boolean fromDepth, boolean toDepth) {
        ProfileValue coverage = new ProfileValue();
        coverage.addValue(createProfileLevel(fromDepth, toDepth, 0.0));
        coverage.addValue(createProfileLevel(fromDepth, toDepth, 10.0));
        coverage.addValue(createProfileLevel(fromDepth, toDepth, 20.0));
        return coverage;
    }

    private ProfileLevel createProfileLevel(boolean fromDepth, boolean toDepth, double start) {
        ProfileLevel profileLevel = new ProfileLevel();
        if (fromDepth) {
            profileLevel.setLevelStart(createQuantity("fromDepth", start, "m"));
        }
        if (toDepth) {
            profileLevel.setLevelEnd(createQuantity("toDepth", start + 10.0, "m"));
        }
        profileLevel.setValue(createProfileLevel());
        return profileLevel;
    }

    private QuantityValue createQuantity(String definition, double value, String unit) {
        QuantityValue quantity = new QuantityValue(value, unit);
        quantity.setValue(value).setUom(unit).setDefinition(definition);
        return quantity;
    }

    private List<Value<?>> createProfileLevel() {
        List<Value<?>> list = Lists.newArrayList();
        CategoryValue category = new CategoryValue("weathered grey brown basalt", "unknown");
        category.setDefinition("http://www.opengis.net/def/gwml/2.0/observedProperty/earthMaterial");
        category.addName(new CodeType("lithology"));
        list.add(category);
        return list;
    }

}
