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
package org.n52.svalbard.gwml.v21.encode;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.xmlbeans.XmlObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gwml.GWMLConstants;
import org.n52.sos.ogc.om.values.GWGeologyLogCoverage;
import org.n52.sos.ogc.om.values.LogValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;

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
        GWGeologyLogCoverage coverage = createGWGeologyLogCoverage(false, false);
        XmlObject encodedObject = CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, coverage);
        assertThat(XmlHelper.validateDocument(encodedObject), is(TRUE));
        assertThat(encodedObject, instanceOf(GWGeologyLogCoveragePropertyType.class));
    }
    
    @Test
    public void should_encode_GWGeologyLogCoverage_full() throws UnsupportedEncoderInputException, OwsExceptionReport {
        GWGeologyLogCoverage coverage = createGWGeologyLogCoverage(true, true);
        XmlObject encodedObject = CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, coverage);
        assertThat(XmlHelper.validateDocument(encodedObject), is(TRUE));
        assertThat(encodedObject, instanceOf(GWGeologyLogCoveragePropertyType.class));
    }
    
    @Test
    public void should_encode_GWGeologyLogCoverage_fromDepth() throws UnsupportedEncoderInputException, OwsExceptionReport {
        GWGeologyLogCoverage coverage = createGWGeologyLogCoverage(true, false);
        XmlObject encodedObject = CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, coverage);
        assertThat(XmlHelper.validateDocument(encodedObject), is(TRUE));
        assertThat(encodedObject, instanceOf(GWGeologyLogCoveragePropertyType.class));
    }
    
    @Test
    public void should_encode_GWGeologyLogCoverage_toDepth() throws UnsupportedEncoderInputException, OwsExceptionReport {
        GWGeologyLogCoverage coverage = createGWGeologyLogCoverage(false, true);
        XmlObject encodedObject = CodingHelper.encodeObjectToXmlPropertyType(GWMLConstants.NS_GWML_22, coverage);
        assertThat(XmlHelper.validateDocument(encodedObject), is(TRUE));
        assertThat(encodedObject, instanceOf(GWGeologyLogCoveragePropertyType.class));
    }

    private GWGeologyLogCoverage createGWGeologyLogCoverage(boolean fromDepth, boolean toDepth) {
        GWGeologyLogCoverage coverage = new GWGeologyLogCoverage();
        coverage.addValue(createLogValue(fromDepth, toDepth));
        return coverage;
    }

    private LogValue createLogValue(boolean fromDepth, boolean toDepth) {
        LogValue logValue = new LogValue();
        if (fromDepth) {
            logValue.setFromDepth(createQuantity("fromDepth", 10.0, "m"));
        }
        if (toDepth) {
            logValue.setToDepth(createQuantity("fromDepth", 20.0, "m"));
        }
        logValue.setValue(createDataRecord());
        return logValue;
    }

    private SweQuantity createQuantity(String definition, double value, String unit) {
        SweQuantity quantity = new SweQuantity();
        quantity.setValue(value).setUom(unit).setDefinition(definition);
        return quantity;
    }

    private DataRecord createDataRecord() {
        SweDataRecord dataRecord = new SweDataRecord();
        dataRecord.setDefinition("http://www.opengis.net/def/gwml/2.0/observedProperty/earthMaterial");
        dataRecord.addField(new SweField(new CodeType("lithology"), new SweText().setValue("weathered grey brown basalt")));
        return dataRecord;
    }
    
}
