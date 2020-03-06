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
package org.n52.svalbard.gwml.v21.encode;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gwml.GWMLConstants;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ProfileLevel;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;

import com.google.common.collect.Lists;

import net.opengis.gwmlWell.x22.GWGeologyLogCoveragePropertyType;

public class GWGeologyLogCoveragePropertyEncoderTest {
    
    @BeforeClass
    public static void initSettingsManager() {
        SettingsManager.getInstance();
    }

    @AfterClass
    public static void cleanupSettingManager() {
        SettingsManager.getInstance().cleanup();
    }

    @Test
    public void should_encode_GWGeologyLogCoverage_only_value() throws UnsupportedEncoderInputException, OwsExceptionReport {
        ProfileValue coverage = createGWGeologyLogCoverage(false, false);
        XmlObject encodedObject = CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, coverage);
        assertThat(XmlHelper.validateDocument(encodedObject), is(TRUE));
        assertThat(encodedObject, instanceOf(GWGeologyLogCoveragePropertyType.class));
    }
    
    @Test
    public void should_encode_GWGeologyLogCoverage_full() throws UnsupportedEncoderInputException, OwsExceptionReport {
        ProfileValue coverage = createGWGeologyLogCoverage(true, true);
        XmlObject encodedObject = CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, coverage);
        assertThat(XmlHelper.validateDocument(encodedObject), is(TRUE));
        assertThat(encodedObject, instanceOf(GWGeologyLogCoveragePropertyType.class));
    }
    
    @Test
    public void should_encode_GWGeologyLogCoverage_fromDepth() throws UnsupportedEncoderInputException, OwsExceptionReport {
        ProfileValue coverage = createGWGeologyLogCoverage(true, false);
        XmlObject encodedObject = CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, coverage);
        assertThat(XmlHelper.validateDocument(encodedObject), is(TRUE));
        assertThat(encodedObject, instanceOf(GWGeologyLogCoveragePropertyType.class));
    }
    
    @Test
    public void should_encode_GWGeologyLogCoverage_toDepth() throws UnsupportedEncoderInputException, OwsExceptionReport {
        ProfileValue coverage = createGWGeologyLogCoverage(false, true);
        XmlObject encodedObject = CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, coverage);
        assertThat(XmlHelper.validateDocument(encodedObject), is(TRUE));
        assertThat(encodedObject, instanceOf(GWGeologyLogCoveragePropertyType.class));
    }

    private ProfileValue createGWGeologyLogCoverage(boolean fromDepth, boolean toDepth) {
        ProfileValue coverage = new ProfileValue();
        coverage.addValue(createLogValue(fromDepth, toDepth, 0.0));
        coverage.addValue(createLogValue(fromDepth, toDepth, 10.0));
        return coverage;
    }

    private ProfileLevel createLogValue(boolean fromDepth, boolean toDepth, double start) {
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
        TextValue text = new TextValue("weathered grey brown basalt");
        text.setDefinition("http://www.opengis.net/def/gwml/2.0/observedProperty/earthMaterial");
        text.addName(new CodeType("text"));
        list.add(text);
        return list;
    }
    
}
