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
package org.n52.sos.encode.json.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.joda.time.DateTime;
import org.junit.ClassRule;
import org.junit.Test;
import org.n52.janmayen.Json;
import org.n52.shetland.aqd.EReportingChange;
import org.n52.shetland.aqd.EReportingHeader;
import org.n52.shetland.inspire.GeographicalName;
import org.n52.shetland.inspire.Pronunciation;
import org.n52.shetland.inspire.Spelling;
import org.n52.shetland.inspire.ad.AddressRepresentation;
import org.n52.shetland.inspire.base.Identifier;
import org.n52.shetland.inspire.base2.Contact;
import org.n52.shetland.inspire.base2.RelatedParty;
import org.n52.shetland.iso.gmd.LocalisedCharacterString;
import org.n52.shetland.iso.gmd.PT_FreeText;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.w3c.Nillable;
import org.n52.shetland.w3c.xlink.Reference;
import org.n52.shetland.w3c.xlink.Referenceable;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.svalbard.encode.exception.EncodingException;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class EReportingHeaderJSONEncoderTest {

    @ClassRule
    public static final ConfiguredSettingsManager CSM = new ConfiguredSettingsManager();

    @Test
    public void test()
            throws OwsExceptionReport, URISyntaxException, EncodingException {
        EReportingHeader header
        = new EReportingHeader()
                .setInspireID(new Identifier("id", "namespace")
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
                                .setHoursOfService(new PT_FreeText()
                                        .addTextGroup(new LocalisedCharacterString("asdfasdf")))
                                .setWebsite(Nillable.unknown())
                                .setElectronicMailAddress(Nillable.unknown())
                                .setAddress(new AddressRepresentation()
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

        JsonNode o = new EReportingHeaderJSONEncoder().encode(header);

        System.out.println(Json.print(o));
    }
}
