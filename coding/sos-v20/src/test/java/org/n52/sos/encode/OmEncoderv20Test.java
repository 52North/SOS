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
package org.n52.sos.encode;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import org.apache.xmlbeans.XmlObject;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmCompositePhenomenon;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.OmObservationContext;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.DefaultProfileHandler;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.W3CConstants;
import org.w3c.dom.Node;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Iterators;

public class OmEncoderv20Test {

    private static final String PROCEDURE = "procedure";
    private static final String OFFERING = "offering";
    private static final String PARENT_OBSERVABLE_PROPERTY
            = "http://example.tld/phenomenon/parent";
    private static final String CHILD_OBSERVABLE_PROPERTY_5
            = "http://example.tld/phenomenon/child/5";
    private static final String CHILD_OBSERVABLE_PROPERTY_4
            = "http://example.tld/phenomenon/child/4";
    private static final String CHILD_OBSERVABLE_PROPERTY_3
            = "http://example.tld/phenomenon/child/3";
    private static final String CHILD_OBSERVABLE_PROPERTY_2
            = "http://example.tld/phenomenon/child/2";
    private static final String CHILD_OBSERVABLE_PROPERTY_1
            = "http://example.tld/phenomenon/child/1";
    private static final String CODE_SPACE = "codespace";

    private static final String TOKEN_SEPERATOR = "##";
    private static final String TUPLE_SEPERATOR = "@@";

    protected static final String CHILD_OBSERVABLE_PROPERTY_1_NAME = "child1";
    protected static final String CHILD_OBSERVABLE_PROPERTY_2_NAME = "child2";
    protected static final String CHILD_OBSERVABLE_PROPERTY_3_NAME = "child3";
    protected static final String CHILD_OBSERVABLE_PROPERTY_4_NAME = "child4";
    protected static final String CHILD_OBSERVABLE_PROPERTY_5_NAME = "child5";

    @Rule
    public final ErrorCollector errors = new ErrorCollector();
    
    @Test
    public void testComplexObservation()
            throws OwsExceptionReport {
        OmObservation observation = createComplexObservation();
        Map<HelperValues, String> helperValues
                = new EnumMap<>(HelperValues.class);
        helperValues.put(HelperValues.DOCUMENT, "true");
        XmlObject xb = CodingHelper.encodeObjectToXml(OmConstants.NS_OM_2, observation, helperValues);
        Node node = xb.getDomNode();
        Checker checker = new Checker(new NamespaceContextImpl());
//        System.out.println(xb.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
        errors.checkThat(node, checker.hasXPath("/om:OM_Observation/om:observedProperty[@xlink:href='http://example.tld/phenomenon/parent']"));
        errors.checkThat(node, checker.hasXPath("/om:OM_Observation/om:result/@xsi:type", containsString("DataRecordPropertyType")));
        errors.checkThat(node, checker.hasXPath("/om:OM_Observation/om:result/swe:DataRecord/swe:field[@name='child1']/swe:Quantity[@definition='http://example.tld/phenomenon/child/1']"));
        errors.checkThat(node, checker.hasXPath("/om:OM_Observation/om:result/swe:DataRecord/swe:field[@name='child2']/swe:Boolean[@definition='http://example.tld/phenomenon/child/2']"));
        errors.checkThat(node, checker.hasXPath("/om:OM_Observation/om:result/swe:DataRecord/swe:field[@name='child3']/swe:Count[@definition='http://example.tld/phenomenon/child/3']"));
        errors.checkThat(node, checker.hasXPath("/om:OM_Observation/om:result/swe:DataRecord/swe:field[@name='child4']/swe:Text[@definition='http://example.tld/phenomenon/child/4']"));
        errors.checkThat(node, checker.hasXPath("/om:OM_Observation/om:result/swe:DataRecord/swe:field[@name='child5']/swe:Category[@definition='http://example.tld/phenomenon/child/5']"));
    }


    protected OmObservation createComplexObservation() {
        DateTime now = new DateTime(DateTimeZone.UTC);
        TimeInstant resultTime = new TimeInstant(now);
        TimeInstant phenomenonTime = new TimeInstant(now);
        TimePeriod validTime = new TimePeriod(now.minusMinutes(5), now.plusMinutes(5));
        OmObservation observation = new OmObservation();
        OmObservationConstellation observationConstellation = new OmObservationConstellation();
        observationConstellation.setFeatureOfInterest(new SamplingFeature(new CodeWithAuthority("feature", CODE_SPACE)));
        OmCompositePhenomenon observableProperty = new OmCompositePhenomenon(PARENT_OBSERVABLE_PROPERTY);
        observableProperty.addPhenomenonComponent(new OmObservableProperty(CHILD_OBSERVABLE_PROPERTY_1));
        observableProperty.addPhenomenonComponent(new OmObservableProperty(CHILD_OBSERVABLE_PROPERTY_2));
        observableProperty.addPhenomenonComponent(new OmObservableProperty(CHILD_OBSERVABLE_PROPERTY_3));
        observableProperty.addPhenomenonComponent(new OmObservableProperty(CHILD_OBSERVABLE_PROPERTY_4));
        observationConstellation.setObservableProperty(observableProperty);
        observationConstellation.setObservationType(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION);
        observationConstellation.addOffering(OFFERING);
        SensorML procedure = new SensorML();
        procedure.setIdentifier(new CodeWithAuthority(PROCEDURE, CODE_SPACE));
        observationConstellation.setProcedure(procedure);
        observation.setObservationConstellation(observationConstellation);
        observation.addRelatedObservation(new OmObservationContext(new ReferenceType("role"), new ReferenceType("observation")));
        observation.setParameter(null);
        observation.setResultTime(resultTime);
        observation.setTokenSeparator(TOKEN_SEPERATOR);
        observation.setTupleSeparator(TUPLE_SEPERATOR);
        observation.setValidTime(validTime);
        ComplexValue complexValue = new ComplexValue();
        SweDataRecord sweDataRecord = new SweDataRecord();
        SweQuantity sweQuantity = new SweQuantity();
        sweQuantity.setDefinition(CHILD_OBSERVABLE_PROPERTY_1);
        sweQuantity.setUom("unit");
        sweQuantity.setValue(42.0);
        sweDataRecord.addField(new SweField(CHILD_OBSERVABLE_PROPERTY_1_NAME, sweQuantity));
        SweBoolean sweBoolean = new SweBoolean();
        sweBoolean.setValue(Boolean.TRUE);
        sweBoolean.setDefinition(CHILD_OBSERVABLE_PROPERTY_2);
        sweDataRecord.addField(new SweField(CHILD_OBSERVABLE_PROPERTY_2_NAME, sweBoolean));
        SweCount sweCount = new SweCount();
        sweCount.setDefinition(CHILD_OBSERVABLE_PROPERTY_3);
        sweCount.setValue(42);
        sweDataRecord.addField(new SweField(CHILD_OBSERVABLE_PROPERTY_3_NAME, sweCount));
        SweText sweText = new SweText();
        sweText.setDefinition(CHILD_OBSERVABLE_PROPERTY_4);
        sweText.setValue("42");
        sweDataRecord.addField(new SweField(CHILD_OBSERVABLE_PROPERTY_4_NAME, sweText));
        SweCategory sweCategory = new SweCategory();
        sweCategory.setDefinition(CHILD_OBSERVABLE_PROPERTY_5);
        sweCategory.setCodeSpace(CODE_SPACE);
        sweCategory.setValue("52");
        sweDataRecord.addField(new SweField(CHILD_OBSERVABLE_PROPERTY_5_NAME, sweCategory));
        complexValue.setValue(sweDataRecord);
        observation.setValue(new SingleObservationValue<>(phenomenonTime, complexValue));
        return observation;
    }
    
    @Before
    public void init() {
        Configurator configurator = mock(Configurator.class);
        when(configurator.getProfileHandler()).thenReturn(new DefaultProfileHandler());
        Configurator.setInstance(configurator);
    }

    private class Checker {
        private final NamespaceContext ctx;

        Checker(NamespaceContext ctx) {
            this.ctx = ctx;
        }
        public Matcher<Node> hasXPath(String path) {
            return Matchers.hasXPath(path, ctx);
        }
        public Matcher<Node> hasXPath(String path, Matcher<String> value) {
            return Matchers.hasXPath(path, ctx, value);
        }
    }

    private class NamespaceContextImpl implements NamespaceContext {
        private ImmutableBiMap<String, String> map = ImmutableBiMap
                .<String, String>builder()
                .put(SweConstants.NS_SWE_PREFIX, SweConstants.NS_SWE_20)
                .put(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM_2)
                .put(W3CConstants.NS_XSI_PREFIX, W3CConstants.NS_XSI)
                .put(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK)
                .put(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32)
                .build();

        @Override
        public String getNamespaceURI(String prefix) {
            return map.get(prefix);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return map.inverse().get(namespaceURI);
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            return Iterators.singletonIterator(getPrefix(namespaceURI));
        }
    }
}
