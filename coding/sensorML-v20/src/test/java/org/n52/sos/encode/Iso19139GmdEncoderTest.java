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

import static org.hamcrest.Matchers.*;

import javax.xml.namespace.NamespaceContext;

import org.isotc211.x2005.gmd.DQDomainConsistencyDocument;
import org.isotc211.x2005.gmd.DQDomainConsistencyPropertyType;
import org.isotc211.x2005.gmd.DQDomainConsistencyType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.iso.GcoConstants;
import org.n52.sos.iso.gmd.GmdConformanceResult;
import org.n52.sos.iso.gmd.GmdConstants;
import org.n52.sos.iso.gmd.GmdDomainConsistency;
import org.n52.sos.iso.gmd.GmdQuantitativeResult;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.NamespaceContextBuilder;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.w3c.W3CConstants;
import org.w3c.dom.Node;

import com.google.common.collect.ImmutableMap;

public class Iso19139GmdEncoderTest {
    private static final ImmutableMap<HelperValues, String> TYPE
            = ImmutableMap.of(HelperValues.TYPE, "true");
    private static final ImmutableMap<HelperValues, String> PROPERTY_TYPE
            = ImmutableMap.of(HelperValues.PROPERTY_TYPE, "true");
    private static final ImmutableMap<HelperValues, String> DOCUMENT_TYPE
            = ImmutableMap.of(HelperValues.DOCUMENT, "true");
    private static final NamespaceContext NS_CTX = new NamespaceContextBuilder()
            .add(GmlConstants.NS_GML_32, GmlConstants.NS_GML_PREFIX)
            .add(GcoConstants.NS_GCO, GcoConstants.NS_GCO_PREFIX)
            .add(GmdConstants.NS_GMD, GmdConstants.NS_GMD_PREFIX)
            .add(W3CConstants.NS_XLINK, W3CConstants.NS_XLINK_PREFIX)
            .build();
    
    
    @BeforeClass
    public static void initSettingsManager() {
        SettingsManager.getInstance();
    }

    @AfterClass
    public static void cleanupSettingManager() {
        SettingsManager.getInstance().cleanup();
    }

    @Rule
    public final ErrorCollector errors = new ErrorCollector();
    private final Iso19139GmdEncoder encoder = new Iso19139GmdEncoder();

    @Test
    public void checkReturnType() throws Exception {
        GmdConformanceResult cr = GmdDomainConsistency.dataCapture(true);
        GmdQuantitativeResult qr = GmdDomainConsistency.uncertaintyEstimation(20);
        errors.checkThat(encoder.encode(cr), is(instanceOf(DQDomainConsistencyType.class)));
        errors.checkThat(encoder.encode(cr, DOCUMENT_TYPE), is(instanceOf(DQDomainConsistencyDocument.class)));
        errors.checkThat(encoder.encode(cr, PROPERTY_TYPE), is(instanceOf(DQDomainConsistencyPropertyType.class)));
        errors.checkThat(encoder.encode(cr, TYPE), is(instanceOf(DQDomainConsistencyType.class)));
        errors.checkThat(encoder.encode(qr), is(instanceOf(DQDomainConsistencyType.class)));
        errors.checkThat(encoder.encode(qr, DOCUMENT_TYPE), is(instanceOf(DQDomainConsistencyDocument.class)));
        errors.checkThat(encoder.encode(qr, PROPERTY_TYPE), is(instanceOf(DQDomainConsistencyPropertyType.class)));
        errors.checkThat(encoder.encode(qr, TYPE), is(instanceOf(DQDomainConsistencyType.class)));
    }
    
    @Test
    public void checkValidity() throws OwsExceptionReport {
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.dataCapture(GmlConstants.NilReason.unknown), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.dataCapture(true), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.timeCoverage(GmlConstants.NilReason.unknown), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.timeCoverage(true), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.uncertaintyEstimation(5), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.uncertaintyEstimation(GmlConstants.NilReason.unknown), DOCUMENT_TYPE)), is(true));
    }

    @Test
    public void checkConformanceResult() throws OwsExceptionReport {
        Node node = encoder.encode(GmdDomainConsistency.dataCapture(true), DOCUMENT_TYPE).getDomNode();
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency", NS_CTX));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:title/gco:CharacterString", NS_CTX, is("EC/50/2008")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:Date", NS_CTX, is("2008")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode", NS_CTX, is("publication")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeList", NS_CTX, is("eng")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue", NS_CTX, is("publication")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:explanation/gco:CharacterString", NS_CTX, is("Data Capture")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:pass/gco:Boolean", NS_CTX, is("true")));
    }

    @Test
    public void checkQuantitativeResult() throws OwsExceptionReport {
        Node node = encoder.encode(GmdDomainConsistency.uncertaintyEstimation(5), DOCUMENT_TYPE).getDomNode();
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/@gml:id", NS_CTX, startsWith("PercentageUnit")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/gml:identifier/@codeSpace", NS_CTX, is("http://dd.eionet.europa.eu/vocabularies/aq/resultquality/uncertaintyestimation/")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/gml:catalogSymbol/@codeSpace", NS_CTX, is("http://www.opengis.net/def/uom/UCUM/")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/gml:catalogSymbol", NS_CTX, is("%")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/gml:unitsSystem/@xlink:href", NS_CTX, is("http://www.opengis.net/def/uom/UCUM/")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:value/gco:Record", NS_CTX, is("5.0")));
    }
}
