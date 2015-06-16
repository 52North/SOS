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
package org.n52.sos.decode.xml.stream.inspire.gn;


import static org.hamcrest.core.Is.is;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.xml.stream.XMLStreamException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.inspire.aqd.GeographicalName;
import org.n52.sos.inspire.aqd.Spelling;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.Nillable;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class GeographicalNameReaderTest {
    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Test
    public void test() throws UnsupportedEncodingException, XMLStreamException, OwsExceptionReport {

        String xml = "<gn:GeographicalName xmlns:gn=\"urn:x-inspire:specification:gmlas:GeographicalNames:3.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                     "  <gn:language>eng</gn:language>\n" +
                     "  <gn:nativeness>&lt;asdfasdf</gn:nativeness>\n" +
                     "  <gn:nameStatus xsi:nil=\"true\" nilReason=\"unknown\"/>\n" +
                     "  <gn:sourceOfName xsi:nil=\"true\" nilReason=\"missing\"/>\n" +
                     "  <gn:pronunciation>\n" +
                     "    <gn:PronunciationOfName>\n" +
                     "      <gn:pronunciationSoundLink>http://asdfasdf</gn:pronunciationSoundLink>\n" +
                     "      <gn:pronunciationIPA>asdfasdf</gn:pronunciationIPA>\n" +
                     "    </gn:PronunciationOfName>\n" +
                     "  </gn:pronunciation>\n" +
                     "  <gn:spelling>\n" +
                     "    <gn:SpellingOfName>\n" +
                     "      <gn:text>asdfasdf</gn:text>\n" +
                     "      <gn:script>asdfasdf</gn:script>\n" +
                     "      <gn:transliterationScheme>asdfasdfasdf</gn:transliterationScheme>\n" +
                     "    </gn:SpellingOfName>\n" +
                     "  </gn:spelling>\n" +
                     "  <gn:grammaticalGender codeSpace=\"b\">a</gn:grammaticalGender>\n" +
                     "  <gn:grammaticalNumber codeSpace=\"d\">c</gn:grammaticalNumber>\n" +
                     "</gn:GeographicalName>";

        GeographicalName gn = new GeographicalNameReader()
                .read(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        errors.checkThat(gn.getGrammaticalGender(), is(Nillable.of(new CodeType("a", "b"))));
        errors.checkThat(gn.getGrammaticalNumber(), is(Nillable.of(new CodeType("c", "d"))));
        errors.checkThat(gn.getLanguage(), is(Nillable.of("eng")));
        errors.checkThat(gn.getNativeness(), is(Nillable.of(new CodeType("<asdfasdf"))));
        errors.checkThat(gn.getNameStatus(), is(Nillable.<CodeType>unknown()));

        for (Spelling sp : gn.getSpelling()) {
            errors.checkThat(sp.getText(), is("asdfasdf"));
            errors.checkThat(sp.getScript(), is(Nillable.of("asdfasdf")));
            errors.checkThat(sp.getTransliterationScheme(), is(Nillable.of("asdfasdfasdf")));
        }

        errors.checkThat(gn.getPronunciation().get().getIPA(), is(Nillable.of("asdfasdf")));
        errors.checkThat(gn.getPronunciation().get().getSoundLink(), is(Nillable.of(URI.create("http://asdfasdf"))));

    }
}
