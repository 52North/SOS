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
package org.n52.sos.decode.xml.stream.inspire.aqd;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import javax.xml.stream.XMLStreamException;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.encode.xml.stream.inspire.aqd.EReportingHeaderEncoder;
import org.n52.sos.inspire.aqd.Address;
import org.n52.sos.inspire.aqd.Contact;
import org.n52.sos.inspire.aqd.EReportingChange;
import org.n52.sos.inspire.aqd.EReportingHeader;
import org.n52.sos.inspire.aqd.GeographicalName;
import org.n52.sos.inspire.aqd.InspireID;
import org.n52.sos.inspire.aqd.Pronunciation;
import org.n52.sos.inspire.aqd.RelatedParty;
import org.n52.sos.inspire.aqd.Spelling;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.Nillable;
import org.n52.sos.util.Reference;
import org.n52.sos.util.Referenceable;
import org.xml.sax.SAXException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ReportingHeaderReaderTest {

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Test
    public void testValidity()
            throws XMLStreamException, OwsExceptionReport,
                                       SAXException, MalformedURLException, IOException {
        EReportingHeader header
                = new EReportingHeader()
                        .setInspireID(new InspireID()
                                .setLocalId("id")
                                .setNamespace("namespace")
                                .setVersionId(Nillable.<String>missing()))
                        .setChange(new EReportingChange("Changed because... you know"))
                        .setReportingPeriod(Referenceable.of(Nillable
                                .<Time>present(new TimeInstant(DateTime.now()))))
                        .setReportingAuthority(new RelatedParty()
                                .setIndividualName(Nillable.<String>missing())
                                .setOrganisationName("Organisation")
                                .setPositionName("Postionti")
                                .addRole(new Reference().setHref(URI
                                        .create("http://hallo")))
                                .addRole(Nillable.<Reference>withheld())
                                .setContact(new Contact()
                                        .addTelephoneFacsimile("1234")
                                        .addTelephoneFacsimile(Nillable
                                                .<String>missing())
                                        .addTelephoneVoice("asdfasdf")
                                        .setHoursOfService("asdfasdf")
                                        .setWebsite(Nillable.<String>unknown())
                                        .setElectronicMailAddress(Nillable
                                                .<String>unknown())
                                        .setAddress(new Address()
                                                .setPostCode("12341234")
                                                .setAddressFeature(new Reference()
                                                        .setHref(URI
                                                                .create("http://asdfasdf")))
                                                .addLocatorDesignator("localtor")
                                                .addAddressArea(Nillable
                                                        .<GeographicalName>withheld())
                                                .addAddressArea(new GeographicalName()
                                                        .setGrammaticalGender(new CodeType("a", "b"))
                                                        .setGrammaticalNumber(new CodeType("c", "d"))
                                                        .setLanguage("eng")
                                                        .setNativeness(new CodeType("<asdfasdf"))
                                                        .setNameStatus(Nillable
                                                                .<CodeType>unknown())
                                                        .addSpelling(new Spelling()
                                                                .setScript("asdfasdf")
                                                                .setText("asdfasdf")
                                                                .setTransliterationScheme("asdfasdfasdf")
                                                        )
                                                        .setPronunciation(new Pronunciation()
                                                                .setIPA("asdfasdf")
                                                                .setSoundLink(URI
                                                                        .create("http://asdfasdf"))
                                                        )
                                                )
                                                .addAdminUnit(new GeographicalName()
                                                        .setGrammaticalGender(new CodeType("a", "b"))
                                                        .setGrammaticalNumber(new CodeType("c", "d"))
                                                        .setLanguage("eng")
                                                        .setNativeness(new CodeType("<asdfasdf"))
                                                        .setNameStatus(Nillable
                                                                .<CodeType>unknown())
                                                        .addSpelling(new Spelling()
                                                                .setScript("asdfasdf")
                                                                .setText("asdfasdf")
                                                                .setTransliterationScheme("asdfasdfasdf")
                                                        )
                                                        .setPronunciation(new Pronunciation()
                                                                .setIPA("asdfasdf")
                                                                .setSoundLink(URI
                                                                        .create("http://asdfasdf"))
                                                        ))
                                                .addPostName(Nillable
                                                        .<GeographicalName>withheld())
                                                .addPostName(new GeographicalName()
                                                        .setGrammaticalGender(new CodeType("a", "b"))
                                                        .setGrammaticalNumber(new CodeType("c", "d"))
                                                        .setLanguage("eng")
                                                        .setNativeness(new CodeType("<asdfasdf"))
                                                        .setNameStatus(Nillable
                                                                .<CodeType>unknown())
                                                        .addSpelling(new Spelling()
                                                                .setScript("asdfasdf")
                                                                .setText("asdfasdf")
                                                                .setTransliterationScheme("asdfasdfasdf")
                                                        )
                                                        .setPronunciation(new Pronunciation()
                                                                .setIPA("asdfasdf")
                                                                .setSoundLink(URI
                                                                        .create("http://asdfasdf"))
                                                        ))
                                                .addThoroughfare(Nillable
                                                        .<GeographicalName>withheld())
                                                .addThoroughfare(new GeographicalName()
                                                        .setGrammaticalGender(new CodeType("a", "b"))
                                                        .setGrammaticalNumber(new CodeType("c", "d"))
                                                        .setLanguage("eng")
                                                        .setNativeness(new CodeType("<asdfasdf"))
                                                        .setNameStatus(Nillable
                                                                .<CodeType>unknown())
                                                        .addSpelling(new Spelling()
                                                                .setScript("asdfasdf")
                                                                .setText("asdfasdf")
                                                                .setTransliterationScheme("asdfasdfasdf")
                                                        )
                                                        .setPronunciation(new Pronunciation()
                                                                .setIPA("asdfasdf")
                                                                .setSoundLink(URI
                                                                        .create("http://asdfasdf"))
                                                        )
                                                )
                                        )
                                )
                        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new EReportingHeaderEncoder(header).write(baos);
        ByteArrayInputStream in = new ByteArrayInputStream(baos.toByteArray());
        EReportingHeader read = new ReportingHeaderReader().read(in);

        errors.checkThat(read.getChange(), is(equalTo(header.getChange())));
        errors.checkThat(read.getContent(), is(equalTo(header.getContent())));
        errors.checkThat(read.getDelete(), is(equalTo(header.getDelete())));
        errors.checkThat(read.getInspireID(), is(equalTo(header.getInspireID())));
        errors.checkThat(read.getReportingPeriod(), is(equalTo(header.getReportingPeriod())));
        errors.checkThat(read.getReportingAuthority(), is(equalTo(header.getReportingAuthority())));
        errors.checkThat(read.getReportingAuthority().getIndividualName(),
                         is(header.getReportingAuthority().getIndividualName()));
        errors.checkThat(read.getReportingAuthority().getOrganisationName(),
                         is(header.getReportingAuthority().getOrganisationName()));
        errors.checkThat(read.getReportingAuthority().getPositionName(),
                         is(header.getReportingAuthority().getPositionName()));
        Contact c1 = read.getReportingAuthority().getContact().get();
        Contact c2 = header.getReportingAuthority().getContact().get();
        errors.checkThat(c1, is(c2));
        errors.checkThat(c1.getContactInstructions(), is(c2.getContactInstructions()));
        errors.checkThat(c1.getElectronicMailAddress(), is(c2.getElectronicMailAddress()));
        errors.checkThat(c1.getHoursOfService(), is(c2.getHoursOfService()));
        errors.checkThat(c1.getTelephoneFacsimile(), is(c2.getTelephoneFacsimile()));
        errors.checkThat(c1.getTelephoneVoice(), is(c2.getTelephoneVoice()));
        errors.checkThat(c1.getWebsite(), is(c2.getWebsite()));
        Address a1 = c1.getAddress().get();
        Address a2 = c2.getAddress().get();

        errors.checkThat(a1.getAddressAreas(), is(a2.getAddressAreas()));
        errors.checkThat(a1.getAddressFeature(), is(a2.getAddressFeature()));
        errors.checkThat(a1.getAdminUnits(), is(a2.getAdminUnits()));
        errors.checkThat(a1.getLocatorDesignators(), is(a2.getLocatorDesignators()));
        errors.checkThat(a1.getLocatorNames(), is(a2.getLocatorNames()));
        errors.checkThat(a1.getPostCode(), is(a2.getPostCode()));
        errors.checkThat(a1.getPostNames(), is(a2.getPostNames()));
        errors.checkThat(a1.getThoroughfares(), is(a2.getThoroughfares()));

    }

}
