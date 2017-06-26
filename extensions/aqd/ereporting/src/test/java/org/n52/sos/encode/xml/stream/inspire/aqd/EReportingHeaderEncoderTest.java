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
package org.n52.sos.encode.xml.stream.inspire.aqd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.EReportingChange;
import org.n52.shetland.aqd.EReportingHeader;
import org.n52.shetland.inspire.Contact;
import org.n52.shetland.inspire.GeographicalName;
import org.n52.shetland.inspire.InspireID;
import org.n52.shetland.inspire.Pronunciation;
import org.n52.shetland.inspire.RelatedParty;
import org.n52.shetland.inspire.Spelling;
import org.n52.shetland.inspire.ad.AddressRepresentation;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.w3c.Nillable;
import org.n52.shetland.w3c.xlink.Reference;
import org.n52.shetland.w3c.xlink.Referenceable;
import org.n52.svalbard.encode.EReportingHeaderEncoder;
import org.n52.svalbard.encode.exception.EncodingException;

public class EReportingHeaderEncoderTest {
    @Test
    public void testValidity()
            throws XMLStreamException, OwsExceptionReport, SAXException,
                   MalformedURLException, IOException, URISyntaxException, EncodingException {
        EReportingHeader header
                = new EReportingHeader()
                        .setInspireID(new InspireID()
                                .setLocalId("id")
                                .setNamespace("namespace")
                                .setVersionId(Nillable.missing()))
                        .setChange(new EReportingChange("Changed because... you know"))
                        .setReportingPeriod(Referenceable.of(Nillable
                                .present(new TimeInstant(DateTime.now()))))
                        .setReportingAuthority(new RelatedParty()
                                .setIndividualName(Nillable.missing())
                                .setOrganisationName("Organisation")
                                .setPositionName("Postionti")
                                .addRole(new Reference().setHref(URI.create("http://hallo")))
                                .addRole(Nillable.withheld())
                                .setContact(new Contact()
                                        .addTelephoneFacsimile("1234")
                                        .addTelephoneFacsimile(Nillable.missing())
                                        .addTelephoneVoice("asdfasdf")
                                        .setHoursOfService("asdfasdf")
                                        .setWebsite(Nillable.unknown())
                                        .setElectronicMailAddressRepresentation(Nillable.unknown())
                                        .setAddressRepresentation(new AddressRepresentation()
                                                .setPostCode("12341234")
                                                .setAddressFeature(new Reference()
                                                        .setHref(URI.create("http://asdfasdf")))
                                                .addLocatorDesignator("localtor")
                                                .addAddressArea(Nillable.withheld())
                                                .addAddressArea(new GeographicalName()
                                                        .setGrammaticalGender(new CodeType("a", new URI("b")))
                                                        .setGrammaticalNumber(new CodeType("c", new URI("d")))
                                                        .setLanguage("eng")
                                                        .setNativeness(new CodeType("<asdfasdf"))
                                                        .setNameStatus(Nillable.unknown())
                                                        .addSpelling(new Spelling()
                                                                .setScript("asdfasdf")
                                                                .setText("asdfasdf")
                                                                .setTransliterationScheme("asdfasdfasdf")
                                                        )
                                                        .setPronunciation(new Pronunciation()
                                                                .setIPA("asdfasdf")
                                                                .setSoundLink(URI.create("http://asdfasdf"))
                                                        )
                                                )
                                                .addAdminUnit(new GeographicalName()
                                                        .setGrammaticalGender(new CodeType("a", new URI("b")))
                                                        .setGrammaticalNumber(new CodeType("c", new URI("d")))
                                                        .setLanguage("eng")
                                                        .setNativeness(new CodeType("<asdfasdf"))
                                                        .setNameStatus(Nillable.unknown())
                                                        .addSpelling(new Spelling()
                                                                .setScript("asdfasdf")
                                                                .setText("asdfasdf")
                                                                .setTransliterationScheme("asdfasdfasdf")
                                                        )
                                                        .setPronunciation(new Pronunciation()
                                                                .setIPA("asdfasdf")
                                                                .setSoundLink(URI.create("http://asdfasdf"))
                                                        ))
                                                .addPostName(Nillable
                                                        .withheld())
                                                .addPostName(new GeographicalName()
                                                        .setGrammaticalGender(new CodeType("a", new URI("b")))
                                                        .setGrammaticalNumber(new CodeType("c", new URI("d")))
                                                        .setLanguage("eng")
                                                        .setNativeness(new CodeType("<asdfasdf"))
                                                        .setNameStatus(Nillable.unknown())
                                                        .addSpelling(new Spelling()
                                                                .setScript("asdfasdf")
                                                                .setText("asdfasdf")
                                                                .setTransliterationScheme("asdfasdfasdf")
                                                        )
                                                        .setPronunciation(new Pronunciation()
                                                                .setIPA("asdfasdf")
                                                                .setSoundLink(URI.create("http://asdfasdf"))
                                                        ))
                                                .addThoroughfare(Nillable.withheld())
                                                .addThoroughfare(new GeographicalName()
                                                        .setGrammaticalGender(new CodeType("a", new URI("b")))
                                                        .setGrammaticalNumber(new CodeType("c", new URI("d")))
                                                        .setLanguage("eng")
                                                        .setNativeness(new CodeType("<asdfasdf"))
                                                        .setNameStatus(Nillable.unknown())
                                                        .addSpelling(new Spelling()
                                                                .setScript("asdfasdf")
                                                                .setText("asdfasdf")
                                                                .setTransliterationScheme("asdfasdfasdf")
                                                        )
                                                        .setPronunciation(new Pronunciation()
                                                                .setIPA("asdfasdf")
                                                                .setSoundLink(URI.create("http://asdfasdf"))
                                                        )
                                                )
                                        )
                                )
                        );

        validate(header);
    }

    protected void validate(EReportingHeader header) throws XMLStreamException,
                                                            OwsExceptionReport,
                                                            IOException,
                                                            SAXException,
                                                            MalformedURLException,
                                                            EncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new EReportingHeaderEncoder(header).write(baos);
        System.out.println(baos.toString("UTF-8"));
//        xmlValidation(baos);

    }

    private void xmlValidation(ByteArrayOutputStream baos)
            throws XMLStreamException, OwsExceptionReport, IOException,
                   SAXException, MalformedURLException {
        ByteArrayInputStream in = new ByteArrayInputStream(baos.toByteArray());
        URL schemaFile = new URL(AqdConstants.NS_AQD_SCHEMA);
        Source xmlFile = new StreamSource(in);
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaFile);
        Validator validator = schema.newValidator();

        try {
            validator.validate(xmlFile);
        } catch (SAXException e) {
            Assert.fail(e.getLocalizedMessage());
        }
    }

}
