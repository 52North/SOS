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
package org.n52.sos.util.http;

import static org.hamcrest.Matchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class MediaTypeTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Test
    public void applicationXml() {
        MediaType mt = MediaType.parse("application/xml");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.isWildcard(), is(false));
        errors.checkThat(mt.isWildcardType(), is(false));
        errors.checkThat(mt.isWildcardSubtype(), is(false));
        errors.checkThat(mt.getParameters().size(), is(0));
    }

    @Test
    public void applicationXmlWithQ() {
        MediaType mt = MediaType.parse("application/xml;q=1");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("q").get(0), is("1"));
    }

    @Test
    public void applicationXmlWithQAndSpace() {
        MediaType mt = MediaType.parse("application/xml; q=1");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("q").get(0), is("1"));
    }

    @Test
    public void applicationXmlWithQAndTwoSpace() {
        MediaType mt = MediaType.parse("application/xml; q=1");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("q").get(0), is("1"));
    }

    @Test
    public void applicationXmlWithQAndThreeSpace() {
        MediaType mt = MediaType.parse("application/xml; q=1");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("q").get(0), is("1"));
    }

    @Test
    public void applicationXmlWithQAndFourSpace() {
        MediaType mt = MediaType.parse("application/xml; q=1");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("q").get(0), is("1"));
    }

    @Test
    public void applicationXmlWithQAndFiveSpace() {
        MediaType mt = MediaType.parse("application/xml; q=1 ");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("q").get(0), is("1"));
    }

    @Test
    public void applicationXmlWithQAndSixSpace() {
        MediaType mt = MediaType.parse(" application/xml; q=1 ");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("q").get(0), is("1"));
    }

    @Test
    public void applicationXmlWithQAndV() {
        MediaType mt = MediaType.parse("application/xml;q=1;v=2");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(2));
        errors.checkThat(mt.getParameter("q").get(0), is("1"));
        errors.checkThat(mt.getParameter("v").get(0), is("2"));
    }

    @Test
    public void applicationXmlWithQAndVAndSpaces() {
        MediaType mt = MediaType.parse(" application/xml; q=1; v=2 ");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(2));
        errors.checkThat(mt.getParameter("q").get(0), is("1"));
        errors.checkThat(mt.getParameter("v").get(0), is("2"));
    }

    @Test
    public void applicationXmlWithQuotedParameter() {
        MediaType mt = MediaType.parse(" application/xml; a=\"asdf\"");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("a").get(0), is("asdf"));
    }

    @Test
    public void applicationXmlWithQuotedParameterAndSpaces() {
        MediaType mt = MediaType.parse(" application/xml; a=\"asdf\" ");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("a").get(0), is("asdf"));
    }

    @Test
    public void applicationXmlWithQuotedParameterAndSpacesWithin() {
        MediaType mt = MediaType.parse(" application/xml; a=\"as df\" ");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("a").get(0), is("as df"));
    }

    @Test
    public void applicationXmlWithQuotedParameterAndEqualSignWithin() {
        MediaType mt = MediaType.parse(" application/xml; a=\"as = df\" ");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("a").get(0), is("as = df"));
    }

    @Test
    public void applicationXmlWithQuotedParameterAndQuotesWithin() {
        MediaType mt = MediaType.parse("application/xml;a=\"as\\\" = df\"");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("a").get(0), is("as\" = df"));
    }

    @Test
    public void applicationXmlWithQuotedParameterFollowingChars() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application/xml;a=\"as\\\" = df\"aa");
    }

    @Test
    public void applicationXmlWithUnquotedParameterWithQuoteWithin() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application/xml;a=a\"b");
    }

    @Test
    public void applicationXmlWithUnquotedParameterWithQuotedQuoteWithin() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application/xml;a=a\\\"b");
    }

    @Test
    public void applicationXmlWithQuotedParameterWithUnescapedSlash() {
        MediaType mt = MediaType.parse("application/xml;a=\"a\\\\b\"");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("xml"));
        errors.checkThat(mt.getParameters().size(), is(1));
        errors.checkThat(mt.getParameter("a").size(), is(1));
        errors.checkThat(mt.getParameter("a").get(0), is("a\\b"));
    }

    @Test
    public void applicationXmlWithUnquotedParameterWithSpaceWithin() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application/xml;a=a b");
    }

    @Test
    public void missingSubtype() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application/;a=b");
    }

    @Test
    public void singleType() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application;a=b");
    }

    @Test
    public void missingType() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("/xml;a=b");
    }

    @Test
    public void missingParameterName() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application/xml;=b");
    }

    @Test
    public void missingParameterNameWithSpace() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application/xml; =b");
    }

    @Test
    public void missingParameterValue() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application/xml;a=");
    }

    @Test
    public void missingParameterValueWithSpace() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("application/xml;a= ");
    }

    @Test
    public void emptyInput() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("");
    }

    @Test
    public void nullInput() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse(null);
    }

    @Test
    public void wildCard() {
        MediaType mt = MediaType.parse("*/*");
        errors.checkThat(mt.getType(), is("*"));
        errors.checkThat(mt.getSubtype(), is("*"));
        errors.checkThat(mt.isWildcard(), is(true));
        errors.checkThat(mt.isWildcardType(), is(true));
        errors.checkThat(mt.isWildcardSubtype(), is(true));
        errors.checkThat(mt.getParameters().size(), is(0));
    }

    @Test
    public void wildCardSubtype() {
        MediaType mt = MediaType.parse("application/*");
        errors.checkThat(mt.getType(), is("application"));
        errors.checkThat(mt.getSubtype(), is("*"));
        errors.checkThat(mt.isWildcard(), is(false));
        errors.checkThat(mt.isWildcardType(), is(false));
        errors.checkThat(mt.isWildcardSubtype(), is(true));
        errors.checkThat(mt.getParameters().size(), is(0));
    }

    @Test
    public void testSlashEscape() {
        MediaType mt = MediaType.parse("a/b; x=\"a/1\"");
        errors.checkThat(mt.toString(), is("a/b; x=\"a/1\""));
    }

    @Test
    public void testSlashEscape2() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse("a/b; x=a/1");
    }

    @Test
    public void testSlashEscape3() {
        MediaType mt = new MediaType("a", "b", "x", "a/1");
        errors.checkThat(mt.toString(), is("a/b; x=\"a/1\""));
    }

    @Test
    public void testUrn() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse(OGCConstants.URN_IDENTIFIER_IDENTIFICATION);
    }

    @Test
    public void testOgcUrlUnknown() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse(OGCConstants.UNKNOWN);
    }

    @Test
    public void testSensorMLUrl() {
        thrown.expect(IllegalArgumentException.class);
        MediaType.parse(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
    }

    @Test
    public void testCompatibleXmlTypes() {
        MediaType applicationXml = MediaType.parse("application/xml");
        MediaType textXml = MediaType.parse("text/xml");
        MediaType textPlain = MediaType.parse("text/plain");
        errors.checkThat(applicationXml.isCompatible(textXml), is(true));
        errors.checkThat(textXml.isCompatible(applicationXml), is(true));
        errors.checkThat(applicationXml.isCompatible(textPlain), is(false));
        errors.checkThat(textXml.isCompatible(textPlain), is(false));
    }
}
