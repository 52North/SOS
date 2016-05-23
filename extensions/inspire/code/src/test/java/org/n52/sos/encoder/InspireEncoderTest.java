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
package org.n52.sos.encoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.encode.InspireXmlEncoder;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.inspire.InspireConformity;
import org.n52.sos.inspire.InspireConformity.InspireDegreeOfConformity;
import org.n52.sos.inspire.InspireConformityCitation;
import org.n52.sos.inspire.InspireConstants;
import org.n52.sos.inspire.InspireDateOfCreation;
import org.n52.sos.inspire.InspireDateOfLastRevision;
import org.n52.sos.inspire.InspireDateOfPublication;
import org.n52.sos.inspire.InspireKeyword;
import org.n52.sos.inspire.InspireLanguageISO6392B;
import org.n52.sos.inspire.InspireMandatoryKeyword;
import org.n52.sos.inspire.InspireMandatoryKeywordValue;
import org.n52.sos.inspire.InspireMetadataPointOfContact;
import org.n52.sos.inspire.InspireOriginatingControlledVocabulary;
import org.n52.sos.inspire.InspireResourceLocator;
import org.n52.sos.inspire.InspireSupportedCRS;
import org.n52.sos.inspire.InspireSupportedLanguages;
import org.n52.sos.inspire.InspireTemporalReference;
import org.n52.sos.inspire.InspireUniqueResourceIdentifier;
import org.n52.sos.inspire.capabilities.FullInspireExtendedCapabilities;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireServiceSpatialDataResourceType;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireSpatialDataServiceType;
import org.n52.sos.inspire.capabilities.MinimalInspireExtendedCapabilities;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.http.MediaTypes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

public class InspireEncoderTest {

    private static XmlOptions xmlOptions = new XmlOptions();

    /*
     * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
     * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation=
     * "http://inspire.ec.europa.eu/schemas/inspire_dls/1.0 http://inspire.ec.europa.eu/schemas/inspire_dls/1.0/inspire_dls.xsd"
     */
//
//    @BeforeClass
//    public static void init() {
//        Map<String, String> prefixes = new HashMap<String, String>();
//        prefixes.put(InspireConstants.NS_INSPIRE_COMMON, InspireConstants.NS_INSPIRE_COMMON_PREFIX);
//        prefixes.put(InspireConstants.NS_INSPIRE_DLS, InspireConstants.NS_INSPIRE_DLS_PREFIX);
//        xmlOptions.setSaveSuggestedPrefixes(prefixes);
//        xmlOptions.setSaveImplicitNamespaces(prefixes);
//        xmlOptions.setSaveAggressiveNamespaces();
//        xmlOptions.setSavePrettyPrint();
//        xmlOptions.setSaveNamespacesFirst();
//        xmlOptions.setCharacterEncoding("UTF-8");
//    }
//
//    @Test
//    public void enocodeMinimalInspireExtendedCapabilities() throws UnsupportedEncoderInputException,
//            OwsExceptionReport, SAXException, IOException {
//        InspireXmlEncoder inspireEncoder = new InspireXmlEncoder();
//        validate(inspireEncoder.encode(getMinimalInspireExtendedCapabilities()));
//    }
//
//    @Test
//    public void enocodeFullIsnpireExtendedCapabilities() throws UnsupportedEncoderInputException, OwsExceptionReport,
//            SAXException, IOException {
//        InspireXmlEncoder inspireEncoder = new InspireXmlEncoder();
//        validate(inspireEncoder.encode(getFullInspireExtendedCapabilities()));
//    }
//
//    @Test
//    public void valid_iso8601() {
//        // date
//        String datePattern = "\\d{4}-(1[0-2]|0[1-9])-(3[0-1]|0[1-9]|[1-2][0-9])";
//        String date = "2013-09-26";
//        Assert.assertThat(Pattern.matches(datePattern, date), Matchers.is(true));
//        // time
//        String timePattern = "(T(2[0-3]|[0-1][0-9]):([0-5][0-9]):([0-5][0-9])(\\.[0-9]+)?)?";
//        String time_HH_MM_SS_S = "T12:49:41.740";
//        Assert.assertThat(Pattern.matches(timePattern, time_HH_MM_SS_S), Matchers.is(true));
//        String time_HH_MM_SS = "T12:49:41";
//        Assert.assertThat(Pattern.matches(timePattern, time_HH_MM_SS), Matchers.is(true));
//        // offset
//        String offsetPattern = "(Z|[+|-](2[0-3]|[0-1][0-9]):([0-5][0-9]))?";
//        String offset_PLUS_HH_MM = "+02:00";
//        Assert.assertThat(Pattern.matches(offsetPattern, offset_PLUS_HH_MM), Matchers.is(true));
//        String offset_MINUS_HH_MM = "-02:00";
//        Assert.assertThat(Pattern.matches(offsetPattern, offset_MINUS_HH_MM), Matchers.is(true));
//        String offset_Z = "Z";
//        Assert.assertThat(Pattern.matches(offsetPattern, offset_Z), Matchers.is(true));
//        // date time
//        String dtPattern = datePattern + timePattern;
//        Assert.assertThat(Pattern.matches(dtPattern, date + time_HH_MM_SS_S), Matchers.is(true));
//        Assert.assertThat(Pattern.matches(dtPattern, date + time_HH_MM_SS), Matchers.is(true));
//        // date time offset
//        String dtoPattern = dtPattern + offsetPattern; 
//        Assert.assertThat(Pattern.matches(dtoPattern, date + time_HH_MM_SS_S + offset_PLUS_HH_MM), Matchers.is(true));
//        Assert.assertThat(Pattern.matches(dtoPattern, date + time_HH_MM_SS_S + offset_MINUS_HH_MM), Matchers.is(true));
//        Assert.assertThat(Pattern.matches(dtoPattern, date + time_HH_MM_SS_S + offset_Z), Matchers.is(true));
//        Assert.assertThat(Pattern.matches(dtoPattern, date + time_HH_MM_SS + offset_PLUS_HH_MM), Matchers.is(true));
//        Assert.assertThat(Pattern.matches(dtoPattern, date + time_HH_MM_SS + offset_MINUS_HH_MM), Matchers.is(true));
//        Assert.assertThat(Pattern.matches(dtoPattern, date + time_HH_MM_SS + offset_Z), Matchers.is(true));
//        // valid patter for schema: \d{4}-(1[0-2]|0[1-9])-(3[0-1]|0[1-9]|[1-2][0-9])(T(2[0-3]|[0-1][0-9]):([0-5][0-9]):([0-5][0-9])(\.[0-9]+)?)?(Z|[+|-](2[0-3]|[0-1][0-9]):([0-5][0-9]))?
//        
////        String pattern =
////                "\\d{4}-(1[0-2]|0[1-9])-(3[0-1]|0[1-9]|[1-2][0-9])(T(2[0-3]|[0-1][0-9]):([0-5][0-9]):([0-5][0-9])(\\.[0-9]+)?)?(Z|([+|-](2[0-3]|[0-1][0-9]):([0-5][0-9]):([0-5][0-9])(\\.[0-9])?)?)?";
////        Assert.assertThat(Pattern.matches(pattern, "2013-09-26T12:49:41.740+02:00"), Matchers.is(true));
//    }

    private MinimalInspireExtendedCapabilities getMinimalInspireExtendedCapabilities() {
        // --------------------
        InspireResourceLocator resourceLocator = new InspireResourceLocator(ServiceConfiguration.getInstance().getServiceURL());
        resourceLocator.addMediaType(MediaTypes.APPLICATION_SOAP_XML);
        // --------------------
        InspireSupportedLanguages inspireSupportedLanguages =
                new InspireSupportedLanguages(InspireLanguageISO6392B.ENG);
        // --------------------
        InspireLanguageISO6392B responseLanguage = InspireLanguageISO6392B.ENG;
        // --------------------
        Set<InspireUniqueResourceIdentifier> spatialDataSetIdentifier = Sets.newHashSet();
        InspireUniqueResourceIdentifier iuri = new InspireUniqueResourceIdentifier("test");
        iuri.setNamespace("http://test.org");
        spatialDataSetIdentifier.add(iuri);
        // --------------------
        return new MinimalInspireExtendedCapabilities(resourceLocator, inspireSupportedLanguages, responseLanguage,
                spatialDataSetIdentifier,new InspireSupportedCRS("4326"));
    }
    
    private void validate(XmlObject xmlObject) throws SAXException, IOException {
        SchemaFactory sf = SchemaFactory.newInstance(
        XMLConstants.XML_NS_URI );
        Schema schema = sf.newSchema(InspireEncoderTest.class.getResource("/inspire_dls/1.0/inspire_dls.xsd"));
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(xmlObject.getDomNode()));
    }

    private FullInspireExtendedCapabilities getFullInspireExtendedCapabilities() {

        InspireResourceLocator resourceLocator = new InspireResourceLocator(ServiceConfiguration.getInstance().getServiceURL());
        resourceLocator.addMediaType(MediaTypes.APPLICATION_SOAP_XML);
        // -------------------
        InspireTemporalReference temporalReference = new InspireTemporalReference();
        temporalReference.setDateOfCreation(new InspireDateOfCreation(new DateTime()));
        temporalReference.setDateOfLastRevision(new InspireDateOfLastRevision(new DateTime()));
        temporalReference.addDateOfPublication(new InspireDateOfPublication(new DateTime()));
        temporalReference.addTemporalExtent(new TimeInstant(new DateTime()));
        temporalReference.addTemporalExtent(new TimePeriod(new DateTime(), new DateTime().plus(3456)));
        // -------------------
        InspireConformityCitation inspireConformityCitation =
                new InspireConformityCitation("Test", new InspireDateOfCreation(new DateTime()));
        InspireConformity conformity =
                new InspireConformity(inspireConformityCitation, InspireDegreeOfConformity.notEvaluated);
        // -------------------
        InspireMetadataPointOfContact inspireMetadataPointOfContact =
                new InspireMetadataPointOfContact("test", "test@test.te");
        // -------------------
        InspireOriginatingControlledVocabulary inspireOriginatingControlledVocabulary =
                new InspireOriginatingControlledVocabulary("Test", new InspireDateOfCreation(new DateTime()));
        InspireMandatoryKeyword inspireMandatoryKeyword =
                new InspireMandatoryKeyword(InspireMandatoryKeywordValue.humanServiceEditor,
                        inspireOriginatingControlledVocabulary);
        // --------------------
        InspireSupportedLanguages inspireSupportedLanguages =
                new InspireSupportedLanguages(InspireLanguageISO6392B.ENG);
        // --------------------
        InspireLanguageISO6392B responseLanguage = InspireLanguageISO6392B.ENG;
        // --------------------
        InspireUniqueResourceIdentifier iuri = new InspireUniqueResourceIdentifier("test");
        iuri.setNamespace("http://test.org");
        // --------------------
        FullInspireExtendedCapabilities inspireExtendedCapabilities =
                new FullInspireExtendedCapabilities(resourceLocator, inspireSupportedLanguages, responseLanguage, iuri, new InspireSupportedCRS("4326"));
        inspireExtendedCapabilities.setResourceType(InspireServiceSpatialDataResourceType.service);
        inspireExtendedCapabilities.addKeyword(new InspireKeyword("test"));
        inspireExtendedCapabilities.addMandatoryKeyword(inspireMandatoryKeyword);
        // -------------------
        inspireExtendedCapabilities.setMetadataDate(new TimeInstant(new DateTime()));
        // -------------------
        inspireExtendedCapabilities.addMetadataPointOfContact(inspireMetadataPointOfContact);
        inspireExtendedCapabilities.addConformity(conformity);
        inspireExtendedCapabilities.addTemporalReference(temporalReference);
        return inspireExtendedCapabilities;
    }
}
