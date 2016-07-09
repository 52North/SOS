/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.service.it.functional;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.InsertObservationDocument;
import net.opengis.sos.x20.InsertObservationResponseDocument;
import net.opengis.sos.x20.InsertObservationType;
import net.opengis.sos.x20.SosInsertionMetadataType;
import net.opengis.swes.x20.InsertSensorDocument;
import net.opengis.swes.x20.InsertSensorResponseDocument;
import net.opengis.swes.x20.InsertSensorType;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.w3c.dom.Node;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingValue;
import org.n52.sos.config.SettingValueFactory;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.OwsExceptionCode;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.OmCompositePhenomenon;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweSimpleDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceSettings;
import org.n52.sos.service.it.AbstractComplianceSuiteTest;
import org.n52.sos.service.it.Client;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Iterators;

public class ComplexObservationTest extends AbstractComplianceSuiteTest {
    private static final NamespaceContextImpl NS_CTX = new NamespaceContextImpl();
    private static final String COMPLEX_OBSERVATION_PROCEDURE = "procedure";
    private static final String COMPLEX_OBSERVATION_OFFERING = "offering";
    private static final String NUMERIC_OBSERVATION_PROCEDURE = "numericProcedure";
    private static final String NUMERIC_OBSERVATION_OFFERING = "numericOffering";
    private static final String PARENT_OBSERVABLE_PROPERTY = "http://example.tld/phenomenon/parent";
    private static final String CHILD_OBSERVABLE_PROPERTY_5 = "http://example.tld/phenomenon/child/5";
    private static final String CHILD_OBSERVABLE_PROPERTY_4 = "http://example.tld/phenomenon/child/4";
    private static final String CHILD_OBSERVABLE_PROPERTY_3 = "http://example.tld/phenomenon/child/3";
    private static final String CHILD_OBSERVABLE_PROPERTY_2 = "http://example.tld/phenomenon/child/2";
    private static final String CHILD_OBSERVABLE_PROPERTY_1 = "http://example.tld/phenomenon/child/1";
    private static final String APPLICATION_XML = MediaTypes.APPLICATION_XML.toString();
    private static final String FEATURE_OF_INTEREST = "featureOfInterest";
    private static final String OFFERING_CAPABILITIES_NAME = "offerings";
    private static final String UNIQUE_ID_NAME = "uniqueID";
    private static final String[] CHILD_OBSERVABLE_PROPERTIES = {
        CHILD_OBSERVABLE_PROPERTY_1,
        CHILD_OBSERVABLE_PROPERTY_2,
        CHILD_OBSERVABLE_PROPERTY_3,
        CHILD_OBSERVABLE_PROPERTY_4,
        CHILD_OBSERVABLE_PROPERTY_5
    };
    private static final String[] ALL_OBSERVABLE_PROPERTIES = {
        PARENT_OBSERVABLE_PROPERTY,
        CHILD_OBSERVABLE_PROPERTY_1,
        CHILD_OBSERVABLE_PROPERTY_2,
        CHILD_OBSERVABLE_PROPERTY_3,
        CHILD_OBSERVABLE_PROPERTY_4,
        CHILD_OBSERVABLE_PROPERTY_5
    };
    private static final String UNIT = "unit";
    private static final String CODESPACE = "codespace";

    @Rule
    public final ErrorCollector errors = new ErrorCollector();


    @Before
    public void before() throws OwsExceptionReport {
        activate();
        assertThat(pox().entity(createComplexInsertSensorRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertSensorResponseDocument.class)));
        assertThat(pox().entity(createComplexInsertObservationRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
    }

    private void activate() {
        ServiceOperatorKey sok = new ServiceOperatorKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION);
        RequestOperatorRepository.getInstance().setActive(new RequestOperatorKey(sok, Sos2Constants.Operations.InsertSensor.name()), true);
        RequestOperatorRepository.getInstance().setActive(new RequestOperatorKey(sok, SosConstants.Operations.InsertObservation.name()), true);
    }

    @After
    public void after() throws OwsExceptionReport {
        H2Configuration.truncate();
        Configurator.getInstance().getCacheController().update();
    }

    @Test
    public void testHiddenParentChildQuery() {
        showChildren(true);
        checkSingleChildObservations(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, Joiner.on(",").join(CHILD_OBSERVABLE_PROPERTIES)).response().asXmlObject());
    }

    @Test
    public void testHiddenParentWithQuery() {
        showChildren(true);
        checkSingleChildObservations(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.procedure, COMPLEX_OBSERVATION_PROCEDURE).query(SosConstants.GetObservationParams.offering, COMPLEX_OBSERVATION_OFFERING).query(SosConstants.GetObservationParams.featureOfInterest, FEATURE_OF_INTEREST).response().asXmlObject());
    }

    @Test
    public void testHiddenParentNoQuery() {
        showChildren(true);
        checkSingleChildObservations(kvp(SosConstants.Operations.GetObservation).response().asXmlObject());
    }

    @Test
    public void testHiddenParentParentQuery() {
        showChildren(true);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, PARENT_OBSERVABLE_PROPERTY).response().asXmlObject());
    }

    @Test
    public  void testHiddenChildrenChild5Query() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, CHILD_OBSERVABLE_PROPERTY_5).response().asXmlObject());
    }

    @Test
    public  void testHiddenChildrenChild4Query() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, CHILD_OBSERVABLE_PROPERTY_4).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenChild3Query() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, CHILD_OBSERVABLE_PROPERTY_3).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenChild2Query() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, CHILD_OBSERVABLE_PROPERTY_2).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenChild1Query() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, CHILD_OBSERVABLE_PROPERTY_1).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenParentQuery() {
        showChildren(false);
        checkSingleParentObservation(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, PARENT_OBSERVABLE_PROPERTY).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenNoQuery() throws OwsExceptionReport  {
        showChildren(false);
        checkSingleParentObservation(kvp(SosConstants.Operations.GetObservation).response().asXmlObject());
    }

    @Test
    public void testInsertComplexThenNumericObservation() throws OwsExceptionReport {
        assertThat(pox().entity(createNumericInsertSensorRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertSensorResponseDocument.class)));
        assertThat(pox().entity(createNumericInsertObservationRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
    }
    
    @Test
    public void testInsertComplexThenNumericObservationSameOffering() throws OwsExceptionReport {
        assertThat(pox().entity(createNumericInsertSensorRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertSensorResponseDocument.class)));
        assertThat(pox().entity(createNumericInsertObservationRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
    }

    @Test
    public void testInsertNumericThenComplexObservation() throws OwsExceptionReport {
        after();
        assertThat(pox().entity(createNumericInsertSensorRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertSensorResponseDocument.class)));
        assertThat(pox().entity(createNumericInsertObservationRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
        assertThat(pox().entity(createComplexInsertSensorRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertSensorResponseDocument.class)));
        assertThat(pox().entity(createComplexInsertObservationRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
    }

    private void checkObservationCount(int count, XmlObject response) {
        assertThat(response, is(instanceOf(GetObservationResponseDocument.class)));
        GetObservationResponseDocument document = (GetObservationResponseDocument) response;
        errors.checkThat(document.getGetObservationResponse().getObservationDataArray(), arrayWithSize(count));
    }

    @Test
    public void testInsertMultipleComplexObservations() throws OwsExceptionReport {
        DateTime now = DateTime.now();
        for (int i = 0; i < 5; ++i) {
            assertThat(pox().entity(createComplexInsertObservationRequest(now.plusHours(i)).xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
        }
        showChildren(false);
        checkObservationCount(6, kvp(SosConstants.Operations.GetObservation).response().asXmlObject());
        showChildren(true);
        checkObservationCount(30, kvp(SosConstants.Operations.GetObservation).response().asXmlObject());
    }

    @Test
    public void testInsertMultipleNumericObservations() throws OwsExceptionReport {
        after();
        assertThat(pox().entity(createNumericInsertSensorRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertSensorResponseDocument.class)));
        DateTime now = DateTime.now();
        for (int i = 0; i < 5; ++i) {
            assertThat(pox().entity(createNumericInsertObservationRequest(now.plusHours(i)).xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
        }
        showChildren(false);
        checkObservationCount(5, kvp(SosConstants.Operations.GetObservation).response().asXmlObject());
        showChildren(true);
        checkObservationCount(5, kvp(SosConstants.Operations.GetObservation).response().asXmlObject());
    }

    @Test
    public void testDatabaseCacheUpdate() throws OwsExceptionReport {
        ContentCache imcache = Configurator.getInstance().getCacheController().getCache();
        Configurator.getInstance().getCacheController().update();
        ContentCache dbcache = Configurator.getInstance().getCacheController().getCache();

        assertThat(dbcache, is(not(sameInstance(imcache))));

        errors.checkThat(dbcache.getObservableProperties(), is(equalTo(imcache.getObservableProperties())));
        errors.checkThat(dbcache.getCompositePhenomenons(), is(equalTo(imcache.getCompositePhenomenons())));
        errors.checkThat(dbcache.getObservablePropertiesForOffering(COMPLEX_OBSERVATION_OFFERING), is(equalTo(imcache.getObservablePropertiesForOffering(COMPLEX_OBSERVATION_OFFERING))));
        errors.checkThat(dbcache.getObservablePropertiesForProcedure(COMPLEX_OBSERVATION_PROCEDURE), is(equalTo(imcache.getObservablePropertiesForProcedure(COMPLEX_OBSERVATION_PROCEDURE))));

        for (String observableProperty : ALL_OBSERVABLE_PROPERTIES) {
            errors.checkThat(dbcache.getProceduresForObservableProperty(observableProperty), is(equalTo(imcache.getProceduresForObservableProperty(observableProperty))));
            errors.checkThat(dbcache.getOfferingsForObservableProperty(observableProperty), is(equalTo(imcache.getOfferingsForObservableProperty(observableProperty))));
            errors.checkThat(dbcache.getCompositePhenomenonForObservableProperty(observableProperty), is(equalTo(imcache.getCompositePhenomenonForObservableProperty(observableProperty))));
            errors.checkThat(dbcache.getObservablePropertiesForCompositePhenomenon(observableProperty), is(equalTo(imcache.getObservablePropertiesForCompositePhenomenon(observableProperty))));
        }
    }

    private void checkSingleParentObservation(XmlObject getObservationResponse) {
//        System.out.println(getObservationResponse.xmlText(getXmlOptions()));
        assertThat(getObservationResponse, is(instanceOf(GetObservationResponseDocument.class)));
        GetObservationResponseDocument document = (GetObservationResponseDocument) getObservationResponse;
        Node node = getObservationResponse.getDomNode();
        errors.checkThat(document.getGetObservationResponse().getObservationDataArray(), arrayWithSize(1));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:observedProperty/@xlink:href", NS_CTX, is(PARENT_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:featureOfInterest/@xlink:href", NS_CTX, is(FEATURE_OF_INTEREST)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:procedure/@xlink:href", NS_CTX, is(COMPLEX_OBSERVATION_PROCEDURE)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Quantity/@definition", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_1)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Quantity/swe:value", NS_CTX, is("42.0")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Quantity/swe:uom/@code", NS_CTX, is(UNIT)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Boolean/@definition", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_2)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Boolean/swe:value", NS_CTX, is("true")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Count/@definition", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_3)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Count/swe:value", NS_CTX, is("42")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Text/@definition", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_4)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Text/swe:value", NS_CTX, is("42")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Category/@definition", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_5)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Category/swe:value", NS_CTX, is("52")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Category/swe:codeSpace/@xlink:href", NS_CTX, is(CODESPACE)));
    }

    private void checkSingleChildObservations(XmlObject getObservationResponse) {
//        System.out.println(getObservationResponse.xmlText(getXmlOptions()));
        assertThat(getObservationResponse, is(instanceOf(GetObservationResponseDocument.class)));
        GetObservationResponseDocument document = (GetObservationResponseDocument) getObservationResponse;
        Node node = getObservationResponse.getDomNode();
        errors.checkThat(document.getGetObservationResponse().getObservationDataArray(), arrayWithSize(5));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"ns:MeasureType\"]", NS_CTX, is("42.0")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"ns:MeasureType\"]/@uom", NS_CTX, is(UNIT)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"ns:MeasureType\"]/../om:observedProperty/@xlink:href", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_1)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:boolean\"]", NS_CTX, is("true")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:boolean\"]/../om:observedProperty/@xlink:href", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_2)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:integer\"]", NS_CTX, is("42")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:integer\"]/../om:observedProperty/@xlink:href", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_3)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:string\"]", NS_CTX, is("42")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:string\"]/../om:observedProperty/@xlink:href", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_4)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"ns:ReferenceType\"]/@xlink:title", NS_CTX, is("52")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"ns:ReferenceType\"]/../om:observedProperty/@xlink:href", NS_CTX, is(CHILD_OBSERVABLE_PROPERTY_5)));
    }

    private void checkInvalidObservedProperty(XmlObject response) {
        Node node = response.getDomNode();
        assertThat(response, is(instanceOf(ExceptionReportDocument.class)));
//        System.out.println(response.xmlText(getXmlOptions()));
        errors.checkThat(node, hasXPath("/ows:ExceptionReport/ows:Exception/@exceptionCode", NS_CTX, is(OwsExceptionCode.InvalidParameterValue.toString())));
        errors.checkThat(node, hasXPath("/ows:ExceptionReport/ows:Exception/@locator", NS_CTX, is(SosConstants.GetObservationParams.observedProperty.toString())));
    }

    protected Client pox() {
        return getExecutor().pox()
                .contentType(APPLICATION_XML)
                .accept(APPLICATION_XML);
    }

    protected Client kvp(Enum<?> operation) {
        return getExecutor().kvp()
                .accept(APPLICATION_XML)
                .query(OWSConstants.RequestParams.service, SosConstants.SOS)
                .query(OWSConstants.RequestParams.version, Sos2Constants.SERVICEVERSION)
                .query(OWSConstants.RequestParams.request, operation);
    }


    private InsertSensorDocument createComplexInsertSensorRequest() throws OwsExceptionReport {
        return createInsertSensorRequest(createComplexObservationProcedure());
    }

    private InsertSensorDocument createNumericInsertSensorRequest() throws OwsExceptionReport {
        return createInsertSensorRequest(createNumericObservationProcedure());
    }

    private SensorML createComplexObservationProcedure() {
        return createProcedure(COMPLEX_OBSERVATION_PROCEDURE, COMPLEX_OBSERVATION_OFFERING);
    }

    private SensorML createNumericObservationProcedure() {
        return createProcedure(NUMERIC_OBSERVATION_PROCEDURE, NUMERIC_OBSERVATION_OFFERING);
    }

    protected InsertSensorDocument createInsertSensorRequest(SensorML procedure) throws OwsExceptionReport {
        InsertSensorDocument document = InsertSensorDocument.Factory.newInstance();
        InsertSensorType insertSensor = document.addNewInsertSensor();
        insertSensor.setService(SosConstants.SOS);
        insertSensor.setVersion(Sos2Constants.SERVICEVERSION);
        insertSensor.addObservableProperty(PARENT_OBSERVABLE_PROPERTY);
        insertSensor.setProcedureDescriptionFormat(SensorMLConstants.NS_SML);
        insertSensor.addNewMetadata().addNewInsertionMetadata().set(createSensorInsertionMetadata());
        insertSensor.addNewProcedureDescription().set(CodingHelper.encodeObjectToXml(SensorMLConstants.NS_SML, procedure));
        return document;
    }

    private SosInsertionMetadataType createSensorInsertionMetadata() {
        SosInsertionMetadataType sosInsertionMetadata = SosInsertionMetadataType.Factory.newInstance();
        sosInsertionMetadata.addFeatureOfInterestType(OGCConstants.UNKNOWN);
        sosInsertionMetadata.addFeatureOfInterestType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT);
        for (String observationType : OmConstants.OBSERVATION_TYPES) {
            sosInsertionMetadata.addObservationType(observationType);
        }
        return sosInsertionMetadata;
    }

    protected SensorML createProcedure(String procedure, String offering) {
        SensorML wrapper = new SensorML();
        org.n52.sos.ogc.sensorML.System sensorML = new org.n52.sos.ogc.sensorML.System();
        wrapper.addMember(sensorML);
        sensorML.addIdentifier(new SmlIdentifier(UNIQUE_ID_NAME, OGCConstants.URN_UNIQUE_IDENTIFIER, procedure));
        sensorML.addCapabilities(createOfferingCapabilities(offering));
        sensorML.addPhenomenon(createCompositePhenomenon());
        wrapper.setIdentifier(new CodeWithAuthority(procedure, CODESPACE));
        return wrapper;
    }

    private SmlCapabilities createOfferingCapabilities(String offering) {
        return new SmlCapabilities(OFFERING_CAPABILITIES_NAME, new SweSimpleDataRecord().addField(createOfferingField(offering)));
    }

    private SweField createOfferingField(String offering) {
        return new SweField(offering, new SweText().setValue(offering).setDefinition(OGCConstants.URN_OFFERING_ID));
    }

    protected InsertObservationDocument createComplexInsertObservationRequest() throws OwsExceptionReport {
        return createComplexInsertObservationRequest(DateTime.now());
    }

    protected InsertObservationDocument createComplexInsertObservationRequest(DateTime time) throws OwsExceptionReport {
        return createInsertObservationRequest(createComplexObservation(time), COMPLEX_OBSERVATION_OFFERING);
    }

    protected InsertObservationDocument createNumericInsertObservationRequest() throws OwsExceptionReport {
        return createNumericInsertObservationRequest(DateTime.now());
    }

    protected InsertObservationDocument createNumericInsertObservationRequest(DateTime time) throws OwsExceptionReport {
        return createInsertObservationRequest(createNumericObservation(time), NUMERIC_OBSERVATION_OFFERING);
    }

    protected InsertObservationDocument createInsertObservationRequest(OmObservation observation, String offering) throws OwsExceptionReport {
        InsertObservationDocument document = InsertObservationDocument.Factory.newInstance();
        InsertObservationType insertObservation = document.addNewInsertObservation();
        insertObservation.setService(SosConstants.SOS);
        insertObservation.setVersion(Sos2Constants.SERVICEVERSION);
        insertObservation.addNewOffering().setStringValue(offering);
        insertObservation.addNewObservation().addNewOMObservation().set(CodingHelper
                .encodeObjectToXml(OmConstants.NS_OM_2, observation));
        return document;
    }

    public OmObservation createComplexObservation(DateTime time) {

        TimeInstant resultTime = new TimeInstant(time);
        TimeInstant phenomenonTime = new TimeInstant(time);
        TimePeriod validTime = new TimePeriod(time.minusMinutes(5), time.plusMinutes(5));

        OmObservation observation = new OmObservation();
        observation.setObservationConstellation(createObservationConstellation(COMPLEX_OBSERVATION_PROCEDURE, COMPLEX_OBSERVATION_OFFERING, OmConstants.OBS_TYPE_COMPLEX_OBSERVATION, createCompositePhenomenon()));
        observation.setResultTime(resultTime);
        observation.setValidTime(validTime);
        observation.setValue(new SingleObservationValue<>(phenomenonTime, new ComplexValue(createSweDataRecord())));

        return observation;
    }

    private OmObservation createNumericObservation(DateTime time) {
        TimeInstant resultTime = new TimeInstant(time);
        TimeInstant phenomenonTime = new TimeInstant(time);
        TimePeriod validTime = new TimePeriod(time.minusMinutes(5), time.plusMinutes(5));

        OmObservation observation = new OmObservation();
        observation.setObservationConstellation(createObservationConstellation(NUMERIC_OBSERVATION_PROCEDURE, NUMERIC_OBSERVATION_OFFERING, OmConstants.OBS_TYPE_MEASUREMENT, createObservableProperty()));
        observation.setResultTime(resultTime);
        observation.setValidTime(validTime);
        observation.setValue(new SingleObservationValue<>(phenomenonTime, new QuantityValue(Double.valueOf(42), UNIT)));

        return observation;
    }

    private OmObservationConstellation createObservationConstellation(String procedure, String offering, String observationType, AbstractPhenomenon observableProperty) {
        OmObservationConstellation observationConstellation = new OmObservationConstellation();
        observationConstellation.setFeatureOfInterest(createFeature());
        observationConstellation.setObservableProperty(observableProperty);
        observationConstellation.setObservationType(observationType);
        observationConstellation.setProcedure(createProcedure(procedure, offering));
        return observationConstellation;
    }

    private SamplingFeature createFeature() {
        return new SamplingFeature(new CodeWithAuthority(FEATURE_OF_INTEREST));
    }

    private SweDataRecord createSweDataRecord() {
        SweDataRecord sweDataRecord = new SweDataRecord();
        sweDataRecord.addField(createSweQuantityField());
        sweDataRecord.addField(createSweBooleanField());
        sweDataRecord.addField(createSweCountField());
        sweDataRecord.addField(createSweTextField());
        sweDataRecord.addField(createSweCategoryField());
        return sweDataRecord;
    }

    private SweField createSweCategoryField() {
        SweCategory sweCategory = new SweCategory();
        sweCategory.setDefinition(CHILD_OBSERVABLE_PROPERTY_5);
        sweCategory.setCodeSpace(CODESPACE);
        sweCategory.setValue("52");
        return new SweField("child5", sweCategory);
    }

    private SweField createSweTextField() {
        SweText sweText = new SweText();
        sweText.setDefinition(CHILD_OBSERVABLE_PROPERTY_4);
        sweText.setValue("42");
        return new SweField("child4", sweText);
    }

    private SweField createSweCountField() {
        SweCount sweCount = new SweCount();
        sweCount.setDefinition(CHILD_OBSERVABLE_PROPERTY_3);
        sweCount.setValue(42);
        return new SweField("child3", sweCount);
    }

    private SweField createSweBooleanField() {
        SweBoolean sweBoolean = new SweBoolean();
        sweBoolean.setValue(Boolean.TRUE);
        sweBoolean.setDefinition(CHILD_OBSERVABLE_PROPERTY_2);
        return new SweField("child2", sweBoolean);
    }

    private SweField createSweQuantityField() {
        SweQuantity sweQuantity = new SweQuantity();
        sweQuantity.setDefinition(CHILD_OBSERVABLE_PROPERTY_1);
        sweQuantity.setUom(UNIT);
        sweQuantity.setValue(42.0);
        return new SweField("child1", sweQuantity);
    }

    protected OmCompositePhenomenon createCompositePhenomenon() {
        OmCompositePhenomenon observableProperty = new OmCompositePhenomenon(PARENT_OBSERVABLE_PROPERTY);
        observableProperty.addPhenomenonComponent(new OmObservableProperty(CHILD_OBSERVABLE_PROPERTY_1));
        observableProperty.addPhenomenonComponent(new OmObservableProperty(CHILD_OBSERVABLE_PROPERTY_2));
        observableProperty.addPhenomenonComponent(new OmObservableProperty(CHILD_OBSERVABLE_PROPERTY_3));
        observableProperty.addPhenomenonComponent(new OmObservableProperty(CHILD_OBSERVABLE_PROPERTY_4));
        return observableProperty;
    }
    protected OmObservableProperty createObservableProperty() {
        return new OmObservableProperty(PARENT_OBSERVABLE_PROPERTY);
    }

    private static void showChildren(boolean show) {
        changeSetting(ServiceSettings.EXPOSE_CHILD_OBSERVABLE_PROPERTIES, Boolean.toString(show));
    }

    private static void changeSetting(String setting, String value) {
        SettingsManager sm = SettingsManager.getInstance();
        SettingValueFactory sf = sm.getSettingFactory();
        SettingDefinition<?, ?> sd = sm.getDefinitionByKey(setting);
        SettingValue<?> sv = sf.newSettingValue(sd, value);
        try {
            sm.changeSetting(sv);
        } catch (ConfigurationException |
                 ConnectionProviderException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static XmlOptions getXmlOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }

    private static class NamespaceContextImpl implements NamespaceContext {
        private final ImmutableBiMap<String, String> map = ImmutableBiMap
                .<String, String>builder()
                .put(Sos2Constants.NS_SOS_PREFIX, Sos2Constants.NS_SOS_20)
                .put(OWSConstants.NS_OWS_PREFIX, OWSConstants.NS_OWS)
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
