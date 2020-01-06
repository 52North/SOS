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
package org.n52.sos.decode;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataRecord;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.util.CodingHelper;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class OmDecoderV20Test {

    String getComplexObservationXml() {
        return "<om:OM_Observation \n" +
               "    xmlns:om=\"http://www.opengis.net/om/2.0\"\n" +
               "    xmlns:gml=\"http://www.opengis.net/gml/3.2\"\n" +
               "    xmlns:swe=\"http://www.opengis.net/swe/2.0\"\n" +
               "    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
               "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
               "  <om:type xlink:href=\"http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation\"/>\n" +
               "  <om:phenomenonTime>\n" +
               "    <gml:TimeInstant gml:id=\"phenomenonTime\">\n" +
               "      <gml:timePosition>2014-08-13T07:26:56.196Z</gml:timePosition>\n" +
               "    </gml:TimeInstant>\n" +
               "  </om:phenomenonTime>\n" +
               "  <om:resultTime xlink:href=\"#phenomenonTime\"/>\n" +
               "  <om:validTime>\n" +
               "    <gml:TimePeriod gml:id=\"validTime\">\n" +
               "      <gml:beginPosition>2014-08-13T07:21:56.196Z</gml:beginPosition>\n" +
               "      <gml:endPosition>2014-08-13T07:31:56.196Z</gml:endPosition>\n" +
               "    </gml:TimePeriod>\n" +
               "  </om:validTime>\n" +
               "  <om:procedure xlink:href=\"procedure\"/>\n" +
               "  <om:observedProperty xlink:href=\"http://example.tld/phenomenon/parent\"/>\n" +
               "  <om:featureOfInterest xlink:href=\"http://example.tld/feature/1\"/>\n" +
               "  <om:result xsi:type=\"swe:DataRecordPropertyType\">\n" +
               "    <swe:DataRecord>\n" +
               "      <swe:field name=\"child1\">\n" +
               "        <swe:Quantity definition=\"http://example.tld/phenomenon/child/1\">\n" +
               "          <swe:uom code=\"unit\"/>\n" +
               "          <swe:value>42.0</swe:value>\n" +
               "        </swe:Quantity>\n" +
               "      </swe:field>\n" +
               "      <swe:field name=\"child2\">\n" +
               "        <swe:Boolean definition=\"http://example.tld/phenomenon/child/2\">\n" +
               "          <swe:value>true</swe:value>\n" +
               "        </swe:Boolean>\n" +
               "      </swe:field>\n" +
               "      <swe:field name=\"child3\">\n" +
               "        <swe:Count definition=\"http://example.tld/phenomenon/child/3\">\n" +
               "          <swe:value>42</swe:value>\n" +
               "        </swe:Count>\n" +
               "      </swe:field>\n" +
               "      <swe:field name=\"child4\">\n" +
               "        <swe:Text definition=\"http://example.tld/phenomenon/child/4\">\n" +
               "          <swe:value>42</swe:value>\n" +
               "        </swe:Text>\n" +
               "      </swe:field>\n" +
               "      <swe:field name=\"child5\">\n" +
               "        <swe:Category definition=\"http://example.tld/phenomenon/child/5\">\n" +
               "          <swe:codeSpace xlink:href=\"codespace\"/>\n" +
               "          <swe:value>52</swe:value>\n" +
               "        </swe:Category>\n" +
               "      </swe:field>\n" +
               "    </swe:DataRecord>\n" +
               "  </om:result>\n" +
               "</om:OM_Observation>";
    }

    @Rule
    public final ErrorCollector errors = new ErrorCollector();


    @Test
    public void testComplexObservation()
            throws XmlException, OwsExceptionReport {
        CodingRepository
                .getInstance();
        XmlObject xml = XmlObject.Factory.parse(getComplexObservationXml());

        Object decoded = CodingHelper.decodeXmlObject(xml);

        assertThat(decoded, is(instanceOf(OmObservation.class)));

        OmObservation observation = (OmObservation) decoded;

        assertThat(observation.getValue(), is(instanceOf(SingleObservationValue.class)));
        assertThat(observation.getValue().getValue(), is(instanceOf(ComplexValue.class)));

        ComplexValue value = (ComplexValue) observation.getValue().getValue();

        assertThat(value.getValue(), is(notNullValue()));

        SweAbstractDataRecord dataRecord = value.getValue();

        assertThat(dataRecord.getFields(), hasSize(5));

        SweField field1 = dataRecord.getFields().get(0);
        SweField field2 = dataRecord.getFields().get(1);
        SweField field3 = dataRecord.getFields().get(2);
        SweField field4 = dataRecord.getFields().get(3);
        SweField field5 = dataRecord.getFields().get(4);

        errors.checkThat(field1.getElement().getDefinition(), is("http://example.tld/phenomenon/child/1"));
        errors.checkThat(field2.getElement().getDefinition(), is("http://example.tld/phenomenon/child/2"));
        errors.checkThat(field3.getElement().getDefinition(), is("http://example.tld/phenomenon/child/3"));
        errors.checkThat(field4.getElement().getDefinition(), is("http://example.tld/phenomenon/child/4"));
        errors.checkThat(field5.getElement().getDefinition(), is("http://example.tld/phenomenon/child/5"));

        errors.checkThat(field1.getElement().getDataComponentType(), is(SweDataComponentType.Quantity));
        errors.checkThat(field2.getElement().getDataComponentType(), is(SweDataComponentType.Boolean));
        errors.checkThat(field3.getElement().getDataComponentType(), is(SweDataComponentType.Count));
        errors.checkThat(field4.getElement().getDataComponentType(), is(SweDataComponentType.Text));
        errors.checkThat(field5.getElement().getDataComponentType(), is(SweDataComponentType.Category));

    }
}
