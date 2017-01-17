/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import javax.xml.namespace.NamespaceContext;

import org.isotc211.x2005.gmd.DQDomainConsistencyDocument;
import org.isotc211.x2005.gmd.DQDomainConsistencyPropertyType;
import org.isotc211.x2005.gmd.DQDomainConsistencyType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.w3c.dom.Node;

import org.n52.shetland.iso.GcoConstants;
import org.n52.shetland.iso.gmd.GmdConformanceResult;
import org.n52.shetland.iso.gmd.GmdConstants;
import org.n52.shetland.iso.gmd.GmdDomainConsistency;
import org.n52.shetland.iso.gmd.GmdQuantitativeResult;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.w3c.W3CConstants;
import org.n52.sos.util.NamespaceContextBuilder;
import org.n52.svalbard.XmlBeansEncodingFlags;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.encode.EncodingContext;
import org.n52.svalbard.encode.Iso19139GmdEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.util.XmlHelper;

public class Iso19139GmdEncoderTest {
    private static final EncodingContext TYPE = EncodingContext.of(XmlBeansEncodingFlags.TYPE);
    private static final EncodingContext PROPERTY_TYPE = EncodingContext.of(XmlBeansEncodingFlags.PROPERTY_TYPE);
    private static final EncodingContext DOCUMENT_TYPE = EncodingContext.of(XmlBeansEncodingFlags.DOCUMENT);
    private static final NamespaceContext NS_CTX = new NamespaceContextBuilder()
            .add(GmlConstants.NS_GML_32, GmlConstants.NS_GML_PREFIX)
            .add(GcoConstants.NS_GCO, GcoConstants.NS_GCO_PREFIX)
            .add(GmdConstants.NS_GMD, GmdConstants.NS_GMD_PREFIX)
            .add(W3CConstants.NS_XLINK, W3CConstants.NS_XLINK_PREFIX)
            .build();


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
    public void checkValidity() throws EncodingException , DecodingException {
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.dataCapture(GmlConstants.NilReason.unknown), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.dataCapture(true), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.timeCoverage(GmlConstants.NilReason.unknown), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.timeCoverage(true), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.uncertaintyEstimation(5), DOCUMENT_TYPE)), is(true));
        errors.checkThat(XmlHelper.validateDocument(encoder.encode(GmdDomainConsistency.uncertaintyEstimation(GmlConstants.NilReason.unknown), DOCUMENT_TYPE)), is(true));
    }

    @Test
    public void checkConformanceResult() throws EncodingException {
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
    public void checkQuantitativeResult() throws EncodingException {
        Node node = encoder.encode(GmdDomainConsistency.uncertaintyEstimation(5), DOCUMENT_TYPE).getDomNode();
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/@gml:id", NS_CTX, startsWith("PercentageUnit")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/gml:identifier/@codeSpace", NS_CTX, is("http://dd.eionet.europa.eu/vocabularies/aq/resultquality/uncertaintyestimation/")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/gml:catalogSymbol/@codeSpace", NS_CTX, is("http://www.opengis.net/def/uom/UCUM/")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/gml:catalogSymbol", NS_CTX, is("%")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:BaseUnit/gml:unitsSystem/@xlink:href", NS_CTX, is("http://www.opengis.net/def/uom/UCUM/")));
        errors.checkThat(node, hasXPath("/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_QuantitativeResult/gmd:value/gco:Record", NS_CTX, is("5.0")));
    }
}
