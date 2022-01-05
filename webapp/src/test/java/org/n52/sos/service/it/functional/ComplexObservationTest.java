/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.shetland.ogc.om.OmCompositePhenomenon;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionCode;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.service.SosSettings;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.exception.EncodingException;
import org.w3c.dom.Node;
import com.google.common.base.Joiner;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.InsertObservationDocument;
import net.opengis.sos.x20.InsertObservationResponseDocument;
import net.opengis.swes.x20.InsertSensorDocument;
import net.opengis.swes.x20.InsertSensorResponseDocument;


public class ComplexObservationTest extends AbstractObservationTest {
    private static final NamespaceContextImpl NS_CTX = new NamespaceContextImpl();
    private static final String FEATURE_OF_INTEREST = "featureOfInterest";
    private static final String COMPLEX_OBSERVATION_PROCEDURE = "procedure";
    private static final String COMPLEX_OBSERVATION_OFFERING = "offering";
    private static final String NUMERIC_OBSERVATION_PROCEDURE = "numericProcedure";
    private static final String NUMERIC_OBSERVATION_OFFERING = "numericOffering";
    private static final String COMPOSITE_OBSERVABLE_PROPERTY = "http://example.tld/phenomenon/composite";
    private static final String BOOLEAN_OBSERVABLE_PROPERTY = "http://example.tld/phenomenon/boolean";
    private static final String CATEGORY_OBSERVABLE_PROPERTY = "http://example.tld/phenomenon/category";
    private static final String COUNT_OBSERVABLE_PROPERTY = "http://example.tld/phenomenon/count";
    private static final String QUANTITY_OBSERVABLE_PROPERTY = "http://example.tld/phenomenon/quantity";
    private static final String TEXT_OBSERVABLE_PROPERTY = "http://example.tld/phenomenon/text";
    private static final String UNIT = "unit";

    protected static final String[] CHILD_OBSERVABLE_PROPERTIES = {
        BOOLEAN_OBSERVABLE_PROPERTY,
        CATEGORY_OBSERVABLE_PROPERTY,
        COUNT_OBSERVABLE_PROPERTY,
        QUANTITY_OBSERVABLE_PROPERTY,
        TEXT_OBSERVABLE_PROPERTY
    };

    protected static final String[] ALL_OBSERVABLE_PROPERTIES = {
        COMPOSITE_OBSERVABLE_PROPERTY,
        BOOLEAN_OBSERVABLE_PROPERTY,
        CATEGORY_OBSERVABLE_PROPERTY,
        COUNT_OBSERVABLE_PROPERTY,
        QUANTITY_OBSERVABLE_PROPERTY,
        TEXT_OBSERVABLE_PROPERTY
    };
    private EncoderRepository encoderRepository = new EncoderRepository();
    private RequestOperatorRepository operatorRepository = new RequestOperatorRepository();

    @Rule
    public final ErrorCollector errors = new ErrorCollector();


    @Before
    public void before() throws OwsExceptionReport, EncodingException {
        encoderRepository.init();
        operatorRepository.init();
        activate();
        initCache();

        assertThat(pox().entity(createComplexInsertSensorRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertSensorResponseDocument.class)));
        assertThat(pox().entity(createComplexInsertObservationRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
    }

    @After
    public void after() throws OwsExceptionReport {
        H2Configuration.truncate();
        updateCache();
    }

    private InsertSensorDocument createComplexInsertSensorRequest() throws OwsExceptionReport {
        return createInsertSensorRequest(COMPLEX_OBSERVATION_PROCEDURE, COMPLEX_OBSERVATION_OFFERING, COMPOSITE_OBSERVABLE_PROPERTY);
    }

    private InsertSensorDocument createNumericInsertSensorRequest() throws OwsExceptionReport {
        return createInsertSensorRequest(NUMERIC_OBSERVATION_PROCEDURE, NUMERIC_OBSERVATION_OFFERING, QUANTITY_OBSERVABLE_PROPERTY);
    }

    private InsertObservationDocument createComplexInsertObservationRequest() throws OwsExceptionReport {
        return createComplexInsertObservationRequest(new DateTime());
    }

    private InsertObservationDocument createComplexInsertObservationRequest(DateTime date) throws OwsExceptionReport {
        OmObservation observation = createObservation(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION, COMPLEX_OBSERVATION_PROCEDURE, COMPLEX_OBSERVATION_OFFERING,
                createCompositePhenomenon(), createFeature(FEATURE_OF_INTEREST, createRandomPoint4326()), date, new ComplexValue(createSweDataRecord()));
        return createInsertObservationRequest(observation, COMPLEX_OBSERVATION_OFFERING);
    }

    private InsertObservationDocument createNumericInsertObservationRequest() throws OwsExceptionReport {
        return createNumericInsertObservationRequest(new DateTime());
    }

    private InsertObservationDocument createNumericInsertObservationRequest(DateTime date) throws OwsExceptionReport {
        OmObservation observation = createObservation(OmConstants.OBS_TYPE_MEASUREMENT, NUMERIC_OBSERVATION_PROCEDURE, NUMERIC_OBSERVATION_OFFERING,
                createObservableProperty(QUANTITY_OBSERVABLE_PROPERTY), createFeature(FEATURE_OF_INTEREST, createRandomPoint4326()), date,
                new QuantityValue(Double.valueOf(42), UNIT));
        return createInsertObservationRequest(observation, NUMERIC_OBSERVATION_OFFERING);
    }

    private void activate() {
        OwsServiceKey sok = new OwsServiceKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION);
        operatorRepository.setActive(new RequestOperatorKey(sok, Sos2Constants.Operations.InsertSensor.name()), true);
        operatorRepository.setActive(new RequestOperatorKey(sok, SosConstants.Operations.InsertObservation.name()), true);
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
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, COMPOSITE_OBSERVABLE_PROPERTY).response().asXmlObject());
    }

    @Test
    public  void testHiddenChildrenChildBooleanQuery() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, BOOLEAN_OBSERVABLE_PROPERTY).response().asXmlObject());
    }

    @Test
    public  void testHiddenChildrenChildCategoryQuery() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, CATEGORY_OBSERVABLE_PROPERTY).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenChildCountQuery() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, COUNT_OBSERVABLE_PROPERTY).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenChildQuantityQuery() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, QUANTITY_OBSERVABLE_PROPERTY).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenChildTextQuery() {
        showChildren(false);
        checkInvalidObservedProperty(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, TEXT_OBSERVABLE_PROPERTY).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenParentQuery() {
        showChildren(false);
        checkSingleParentObservation(kvp(SosConstants.Operations.GetObservation).query(SosConstants.GetObservationParams.observedProperty, COMPOSITE_OBSERVABLE_PROPERTY).response().asXmlObject());
    }

    @Test
    public void testHiddenChildrenNoQuery() throws OwsExceptionReport  {
        showChildren(false);
        checkSingleParentObservation(kvp(SosConstants.Operations.GetObservation).response().asXmlObject());
    }

    @Test
    public void testInsertComplexThenNumericObservation() throws OwsExceptionReport, EncodingException {
        assertThat(pox().entity(createNumericInsertSensorRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertSensorResponseDocument.class)));
        assertThat(pox().entity(createNumericInsertObservationRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
    }

    @Test
    public void testInsertComplexThenNumericObservationSameOffering() throws OwsExceptionReport, EncodingException {
        assertThat(pox().entity(createNumericInsertSensorRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertSensorResponseDocument.class)));
        assertThat(pox().entity(createNumericInsertObservationRequest().xmlText(getXmlOptions())).response().asXmlObject(), is(instanceOf(InsertObservationResponseDocument.class)));
    }

    @Test
    public void testInsertNumericThenComplexObservation() throws OwsExceptionReport, EncodingException {
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
    public void testInsertMultipleComplexObservations() throws OwsExceptionReport, EncodingException {
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
    public void testInsertMultipleNumericObservations() throws OwsExceptionReport, EncodingException {
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
//        ContentCache imcache = Configurator.getInstance().getCacheController().getCache();
//        Configurator.getInstance().getCacheController().update();
//        ContentCache dbcache = Configurator.getInstance().getCacheController().getCache();
//
//        assertThat(dbcache, is(not(sameInstance(imcache))));
//
//        errors.checkThat(dbcache.getObservableProperties(), is(equalTo(imcache.getObservableProperties())));
//        errors.checkThat(dbcache.getCompositePhenomenons(), is(equalTo(imcache.getCompositePhenomenons())));
//        errors.checkThat(dbcache.getObservablePropertiesForOffering(COMPLEX_OBSERVATION_OFFERING), is(equalTo(imcache.getObservablePropertiesForOffering(COMPLEX_OBSERVATION_OFFERING))));
//        errors.checkThat(dbcache.getObservablePropertiesForProcedure(COMPLEX_OBSERVATION_PROCEDURE), is(equalTo(imcache.getObservablePropertiesForProcedure(COMPLEX_OBSERVATION_PROCEDURE))));
//
//        for (String observableProperty : ALL_OBSERVABLE_PROPERTIES) {
//            errors.checkThat(dbcache.getProceduresForObservableProperty(observableProperty), is(equalTo(imcache.getProceduresForObservableProperty(observableProperty))));
//            errors.checkThat(dbcache.getOfferingsForObservableProperty(observableProperty), is(equalTo(imcache.getOfferingsForObservableProperty(observableProperty))));
//            errors.checkThat(dbcache.getCompositePhenomenonForObservableProperty(observableProperty), is(equalTo(imcache.getCompositePhenomenonForObservableProperty(observableProperty))));
//            errors.checkThat(dbcache.getObservablePropertiesForCompositePhenomenon(observableProperty), is(equalTo(imcache.getObservablePropertiesForCompositePhenomenon(observableProperty))));
//        }
    }

    private void checkSingleParentObservation(XmlObject getObservationResponse) {
//        System.out.println(getObservationResponse.xmlText(getXmlOptions()));
        assertThat(getObservationResponse, is(instanceOf(GetObservationResponseDocument.class)));
        GetObservationResponseDocument document = (GetObservationResponseDocument) getObservationResponse;
        Node node = getObservationResponse.getDomNode();
        errors.checkThat(document.getGetObservationResponse().getObservationDataArray(), arrayWithSize(1));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:observedProperty/@xlink:href", NS_CTX, is(COMPOSITE_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:featureOfInterest/sams:SF_SpatialSamplingFeature/gml:identifier", NS_CTX, is(FEATURE_OF_INTEREST)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:procedure/@xlink:href", NS_CTX, is(COMPLEX_OBSERVATION_PROCEDURE)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Quantity/@definition", NS_CTX, is(QUANTITY_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Quantity/swe:value", NS_CTX, is("42.0")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Quantity/swe:uom/@code", NS_CTX, is(UNIT)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Boolean/@definition", NS_CTX, is(BOOLEAN_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Boolean/swe:value", NS_CTX, is("true")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Count/@definition", NS_CTX, is(COUNT_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Count/swe:value", NS_CTX, is("42")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Text/@definition", NS_CTX, is(TEXT_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Text/swe:value", NS_CTX, is("42")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result/swe:DataRecord/swe:field/swe:Category/@definition", NS_CTX, is(CATEGORY_OBSERVABLE_PROPERTY)));
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
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"ns:MeasureType\"]/../om:observedProperty/@xlink:href", NS_CTX, is(QUANTITY_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:boolean\"]", NS_CTX, is("true")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:boolean\"]/../om:observedProperty/@xlink:href", NS_CTX, is(BOOLEAN_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:integer\"]", NS_CTX, is("42")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:integer\"]/../om:observedProperty/@xlink:href", NS_CTX, is(COUNT_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:string\"]", NS_CTX, is("42")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"xs:string\"]/../om:observedProperty/@xlink:href", NS_CTX, is(TEXT_OBSERVABLE_PROPERTY)));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"ns:ReferenceType\"]/@xlink:title", NS_CTX, is("52")));
        errors.checkThat(node, hasXPath("/sos:GetObservationResponse/sos:observationData/om:OM_Observation/om:result[@xsi:type=\"ns:ReferenceType\"]/../om:observedProperty/@xlink:href", NS_CTX, is(CATEGORY_OBSERVABLE_PROPERTY)));
    }

    private void checkInvalidObservedProperty(XmlObject response) {
        Node node = response.getDomNode();
        assertThat(response, is(instanceOf(ExceptionReportDocument.class)));
//        System.out.println(response.xmlText(getXmlOptions()));
        errors.checkThat(node, hasXPath("/ows:ExceptionReport/ows:Exception/@exceptionCode", NS_CTX, is(OwsExceptionCode.InvalidParameterValue.toString())));
        errors.checkThat(node, hasXPath("/ows:ExceptionReport/ows:Exception/@locator", NS_CTX, is(SosConstants.GetObservationParams.observedProperty.toString())));
    }

    private static void showChildren(boolean show) {
        changeSetting(SosSettings.EXPOSE_CHILD_OBSERVABLE_PROPERTIES, Boolean.toString(show));
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
        sweCategory.setDefinition(CATEGORY_OBSERVABLE_PROPERTY);
        sweCategory.setCodeSpace(CODESPACE);
        sweCategory.setValue("52");
        return new SweField("category", sweCategory);
    }

    private SweField createSweTextField() {
        SweText sweText = new SweText();
        sweText.setDefinition(TEXT_OBSERVABLE_PROPERTY);
        sweText.setValue("42");
        return new SweField("text", sweText);
    }

    private SweField createSweCountField() {
        SweCount sweCount = new SweCount();
        sweCount.setDefinition(COUNT_OBSERVABLE_PROPERTY);
        sweCount.setValue(42);
        return new SweField("count", sweCount);
    }

    private SweField createSweBooleanField() {
        SweBoolean sweBoolean = new SweBoolean();
        sweBoolean.setValue(Boolean.TRUE);
        sweBoolean.setDefinition(BOOLEAN_OBSERVABLE_PROPERTY);
        return new SweField("boolean", sweBoolean);
    }

    private SweField createSweQuantityField() {
        SweQuantity sweQuantity = new SweQuantity();
        sweQuantity.setDefinition(QUANTITY_OBSERVABLE_PROPERTY);
        sweQuantity.setUom(UNIT);
        sweQuantity.setValue(42.0);
        return new SweField("quantity", sweQuantity);
    }

    protected OmCompositePhenomenon createCompositePhenomenon() {
        OmCompositePhenomenon compositePhenomenon = new OmCompositePhenomenon(COMPOSITE_OBSERVABLE_PROPERTY);
        compositePhenomenon.addPhenomenonComponent(new OmObservableProperty(BOOLEAN_OBSERVABLE_PROPERTY));
        compositePhenomenon.addPhenomenonComponent(new OmObservableProperty(CATEGORY_OBSERVABLE_PROPERTY));
        compositePhenomenon.addPhenomenonComponent(new OmObservableProperty(COUNT_OBSERVABLE_PROPERTY));
        compositePhenomenon.addPhenomenonComponent(new OmObservableProperty(QUANTITY_OBSERVABLE_PROPERTY));
        compositePhenomenon.addPhenomenonComponent(new OmObservableProperty(TEXT_OBSERVABLE_PROPERTY));
        return compositePhenomenon;
    }
}
