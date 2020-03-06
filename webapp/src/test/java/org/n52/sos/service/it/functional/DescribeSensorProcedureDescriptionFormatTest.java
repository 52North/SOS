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
package org.n52.sos.service.it.functional;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OwsExceptionCode;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sensorML.v20.SimpleProcess;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.it.AbstractComplianceSuiteTest;
import org.n52.sos.service.it.Client;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaTypes;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorml.x20.PhysicalSystemDocument;
import net.opengis.sos.x10.DescribeSensorDocument.DescribeSensor;
import net.opengis.sos.x20.SosInsertionMetadataType;
import net.opengis.swes.x20.DescribeSensorDocument;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorType;
import net.opengis.swes.x20.InsertSensorDocument;
import net.opengis.swes.x20.InsertSensorResponseDocument;
import net.opengis.swes.x20.InsertSensorType;

/**
 * @author Shane St Clair <shane@axiomdatascience.com>
 */

public class DescribeSensorProcedureDescriptionFormatTest extends AbstractComplianceSuiteTest {
    private static final XmlOptions XML_OPTIONS = XmlOptionsHelper.getInstance().getXmlOptions();

    private static final String PROCEDURE1 = "procedure1";
    private static final String PROCEDURE2 = "procedure2";

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Before
    public void before() throws OwsExceptionReport {
        activate();

        InsertSensorDocument insertSensorSml1Doc = createInsertSensorRequest(PROCEDURE1, PROCEDURE1, "offering1", "obs_prop",
                SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
        assertThat(pox().entity(insertSensorSml1Doc.xmlText(XML_OPTIONS)).response().asXmlObject(),
                is(instanceOf(InsertSensorResponseDocument.class)));

        InsertSensorDocument insertSensorSml2Doc = createInsertSensorRequest(PROCEDURE2, PROCEDURE2, "offering2", "obs_prop",
                SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL);
        assertThat(pox().entity(insertSensorSml2Doc.xmlText(XML_OPTIONS)).response().asXmlObject(),
                is(instanceOf(InsertSensorResponseDocument.class)));
    }

    private void activate() {
        ServiceOperatorKey sok = new ServiceOperatorKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION);
        RequestOperatorRepository.getInstance().setActive(new RequestOperatorKey(sok, Sos2Constants.Operations.InsertSensor.name()), true);
    }

    @After
    public void after() throws OwsExceptionReport {
        H2Configuration.truncate();
        Configurator.getInstance().getCacheController().update();
    }

    // Non-mimetype formats cannot be used with SOS 1.0.0 because the OGC spec/schema
    // dictates that outputFormats must be mimetypes
    @Test
    public void testSos1DescribeSensorSensor1MLUrlPox() throws OwsExceptionReport {
        XmlObject responseXml  = sendDescribeSensor1RequestViaPox(PROCEDURE1, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
        assertThat(responseXml, is(instanceOf(ExceptionReportDocument.class)));
    }

    @Test
    public void testSos1DescribeSensorSensor1MLUrlKvp() throws OwsExceptionReport {
        XmlObject responseXml  = sendDescribeSensor1RequestViaKvp(PROCEDURE1, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
        assertThat(responseXml, is(instanceOf(ExceptionReportDocument.class)));
    }

    // Test procedure inserted with SensorML 1.0.1 URL format

    // Procedure inserted with SensorML 1.0.1 URL format can be requested with SOS 2.0
    // using SensorML 1.0.1 URL format (http://www.opengis.net/sensorML/1.0.1)
    @Test
    public void testSos2DescribeSensorSensorML1UrlPox() {
        verifyDescribeSensorResponseDocument(sendDescribeSensor2RequestViaPox(PROCEDURE1, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL),
                SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
    }

    @Test
    public void testSos2DescribeSensorSensorML1UrlKvp() {
        verifyDescribeSensorResponseDocument(sendDescribeSensor2RequestViaKvp(PROCEDURE1, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL),
                SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
    }

    // Procedure inserted with SensorML 1.0.1 URL format can be requested with SOS 2.0
    // using SensorML 2.0 URL format (http://www.opengis.net/sensorml/2.0)
    @Test
    public void testSos2DescribeSensorSensorML2UrlPox() {
        verifyDescribeSensorResponseDocument(sendDescribeSensor2RequestViaPox(PROCEDURE1, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL),
                SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL);
    }

    @Test
    public void testSos2DescribeSensorSensorML2UrlKvp() {
        verifyDescribeSensorResponseDocument(sendDescribeSensor2RequestViaKvp(PROCEDURE1, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL),
                SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL);
    }

    // Procedure inserted with SensorML 1.0.1 URL format can NOT be requested with SOS 2.0
    // using SensorML 1.0.1 mime type format (text/xml; subtype="sensorML/1.0.1").
    // SosHelper.checkFormat rejects the mime type format because it's not in any
    // ProcedureEncoder's getSupportedProcedureDescriptionFormats for SOS 2.0 (ConverterKeys are not checked)
    @Test
    public void testSos2DescribeSensorSensorML1MimeTypePox() throws OwsExceptionReport {
        verifyInvalidParameterValue(sendDescribeSensor2RequestViaPox(PROCEDURE1, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE));
    }

    @Test
    public void testSos2DescribeSensorSensorML1MimeTypeKvp() throws OwsExceptionReport {
        verifyInvalidParameterValue(sendDescribeSensor2RequestViaKvp(PROCEDURE1, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE));
    }

    // Procedure inserted with SensorML 1.0.1 URL format can NOT be requested with SOS 2.0
    // using SensorML 2.0 mime type format (text/xml; subtype="sensorml/2.0")
    // Mime type isn't registered for SOS 2.0
    @Test
    public void testSos2DescribeSensorSensorML2MimeTypePox() throws OwsExceptionReport {
        verifyInvalidParameterValue(sendDescribeSensor2RequestViaPox(PROCEDURE1, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE));
    }

    @Test
    public void testSos2DescribeSensorSensorML2MimeTypeKvp() throws OwsExceptionReport {
        verifyInvalidParameterValue(sendDescribeSensor2RequestViaKvp(PROCEDURE1, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE));
    }

    // Procedure inserted with SensorML 1.0.1 URL format can be requested with SOS 1.0
    // using SensorML 1.0.1 mime type (text/xml; subtype="sensorML/1.0.1")
    @Test
    public void testSos1DescribeSensorSensorML1MimeTypePox() throws OwsExceptionReport {
        verifySensorMLDocument(sendDescribeSensor1RequestViaPox(PROCEDURE1, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE), PROCEDURE1);
    }

    @Test
    public void testSos1DescribeSensorSensorML1MimeTypeKvp() throws OwsExceptionReport {
        verifySensorMLDocument(sendDescribeSensor1RequestViaKvp(PROCEDURE1, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE), PROCEDURE1);
    }

    // Procedure inserted with SensorML 1.0.1 URL format can be requested with SOS 1.0
    // using SensorML 2.0 mime type format (text/xml; subtype="sensorml/2.0")
    @Test
    public void testSos1DescribeSensorSensorML2MimeTypePox() throws OwsExceptionReport {
        verifyPhysicalSystemDocument(sendDescribeSensor1RequestViaPox(PROCEDURE1, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE),
                PROCEDURE1);
    }

    @Test
    public void testSos1DescribeSensorSensorML2MimeTypeKvp() throws OwsExceptionReport {
        verifyPhysicalSystemDocument(sendDescribeSensor1RequestViaKvp(PROCEDURE1, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE),
               PROCEDURE1);
    }

    // Test procedure inserted with SensorML 2.0 URL format

    // Procedure inserted with SensorML 2.0 URL format can be requested with SOS 2.0
    // using SensorML 1.0.1 URL format (http://www.opengis.net/sensorML/1.0.1)
    @Test
    public void testSml2Sos2DescribeSensorSensorML1UrlPox() {
        verifyDescribeSensorResponseDocument(sendDescribeSensor2RequestViaPox(PROCEDURE2, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL),
                SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
    }

    @Test
    public void testSml2Sos2DescribeSensorSensorML1UrlKvp() {
        verifyDescribeSensorResponseDocument(sendDescribeSensor2RequestViaKvp(PROCEDURE2, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL),
                SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
    }

    // Procedure inserted with SensorML 2.0 Sml2 URL format can be requested with SOS 2.0
    // using SensorML 2.0 URL format (http://www.opengis.net/sensorml/2.0)
    @Test
    public void testSml2Sos2DescribeSensorSensorML2UrlPox() {
        verifyDescribeSensorResponseDocument(sendDescribeSensor2RequestViaPox(PROCEDURE2, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL),
                SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL);
    }

    @Test
    public void testSml2Sos2DescribeSensorSensorML2UrlKvp() {
        verifyDescribeSensorResponseDocument(sendDescribeSensor2RequestViaKvp(PROCEDURE2, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL),
                SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL);
    }

    // Procedure inserted with SensorML 2.0 URL format can NOT be requested with SOS 2.0
    // using SensorML 1.0.1 mime type format (text/xml; subtype="sensorML/1.0.1").
    // SosHelper.checkFormat rejects the mime type format because it's not in any
    // ProcedureEncoder's getSupportedProcedureDescriptionFormats for SOS 2.0 (ConverterKeys are not checked)
    @Test
    public void testSml2Sos2DescribeSensorSensorML1MimeTypePox() throws OwsExceptionReport {
        verifyInvalidParameterValue(sendDescribeSensor2RequestViaPox(PROCEDURE2, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE));
    }

    @Test
    public void testSml2Sos2DescribeSensorSensorML1MimeTypeKvp() throws OwsExceptionReport {
        verifyInvalidParameterValue(sendDescribeSensor2RequestViaKvp(PROCEDURE2, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE));
    }

    // Procedure inserted with SensorML 2.0 URL format can NOT be requested with SOS 2.0
    // using SensorML 2.0 mime type format (text/xml; subtype="sensorml/2.0").
    // SosHelper.checkFormat rejects the mime type format because it's not in any
    // ProcedureEncoder's getSupportedProcedureDescriptionFormats for SOS 2.0 (ConverterKeys are not checked)
    @Test
    public void testSml2Sos2DescribeSensorSensorML2MimeTypePox() throws OwsExceptionReport {
        verifyInvalidParameterValue(sendDescribeSensor2RequestViaPox(PROCEDURE2, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE));
    }

    @Test
    public void testSml2Sos2DescribeSensorSensorML2MimeTypeKvp() throws OwsExceptionReport {
        verifyInvalidParameterValue(sendDescribeSensor2RequestViaKvp(PROCEDURE2, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE));
    }

    // Procedure inserted with SensorML 2.0 URL format can be requested with SOS 1.0
    // using SensorML 1.0.1 mime type (text/xml; subtype="sensorML/1.0.1")
    @Test
    public void testSml2Sos1DescribeSensorSensorML1MimeTypePox() throws OwsExceptionReport {
        verifySensorMLDocument(sendDescribeSensor1RequestViaPox(PROCEDURE2, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE), PROCEDURE2);
    }

    @Test
    public void testSml2Sos1DescribeSensorSensorML1MimeTypeKvp() throws OwsExceptionReport {
        verifySensorMLDocument(sendDescribeSensor1RequestViaKvp(PROCEDURE2, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE), PROCEDURE2);
    }

    // Procedure inserted with SensorML 2.0 URL format can be requested with SOS 1.0
    // using SensorML 2.0 mime type format (text/xml; subtype="sensorml/2.0")
    @Test
    public void testSml2Sos1DescribeSensorSensorML2MimeTypePox() throws OwsExceptionReport {
        verifyPhysicalSystemDocument(sendDescribeSensor1RequestViaPox(PROCEDURE2, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE), PROCEDURE2);
    }

    @Test
    public void testSml2Sos1DescribeSensorSensorML2MimeTypeKvp() throws OwsExceptionReport {
        verifyPhysicalSystemDocument(sendDescribeSensor1RequestViaKvp(PROCEDURE2, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE), PROCEDURE2);
    }

    private void verifyInvalidParameterValue(XmlObject xmlObject) throws OwsExceptionReport {
        assertThat(xmlObject, is(instanceOf(ExceptionReportDocument.class)));
        ExceptionReportDocument exceptionReportDoc = (ExceptionReportDocument) xmlObject;
        assertEquals(OwsExceptionCode.InvalidParameterValue.toString(),
                exceptionReportDoc.getExceptionReport().getExceptionArray(0).getExceptionCode());
    }

    private void verifyDescribeSensorResponseDocument(XmlObject xmlObject, String expectedProcedureDescriptionFormat) {
        assertThat(xmlObject, is(instanceOf(DescribeSensorResponseDocument.class)));
        verifyDescribeSensorResponseDocument((DescribeSensorResponseDocument) xmlObject, expectedProcedureDescriptionFormat);
    }

    private void verifyDescribeSensorResponseDocument(DescribeSensorResponseDocument describeSensorResponseDoc,
            String expectedProcedureDescriptionFormat) {
        String procedureDescriptionFormat = describeSensorResponseDoc.getDescribeSensorResponse().getProcedureDescriptionFormat();

        // should be equal to what was requested in DescribeSensor request
        assertEquals(expectedProcedureDescriptionFormat, procedureDescriptionFormat);
    }

    private void verifySensorMLDocument(XmlObject xmlObject, String identifier) throws OwsExceptionReport {
        assertThat(xmlObject, is(instanceOf(SensorMLDocument.class)));
        verifySensorMLDocument((SensorMLDocument) xmlObject, identifier);
    }

    private void verifySensorMLDocument(SensorMLDocument sensorMLDoc, String identifier)
            throws OwsExceptionReport {
        Object decodedXmlObject = CodingHelper.decodeXmlObject(sensorMLDoc);
        assertThat(decodedXmlObject, is(instanceOf(SensorML.class)));
        SensorML sensorML = (SensorML) decodedXmlObject;

        //should be equal to what was requested in DescribeSensor request
        assertEquals(identifier, sensorML.getIdentifier());
    }

    private void verifyPhysicalSystemDocument(XmlObject xmlObject, String identifier) throws OwsExceptionReport {
        assertThat(xmlObject, is(instanceOf(PhysicalSystemDocument.class)));
        verifyPhysicalSystemDocument((PhysicalSystemDocument) xmlObject, identifier);
    }

    private void verifyPhysicalSystemDocument(PhysicalSystemDocument physicalSystemDoc, String identifier)
            throws OwsExceptionReport {
        Object decodedXmlObject = CodingHelper.decodeXmlObject(physicalSystemDoc);
        assertThat(decodedXmlObject, is(instanceOf(PhysicalSystem.class)));
        PhysicalSystem physicalSystem = (PhysicalSystem) decodedXmlObject;

        //should be equal to what was requested in DescribeSensor request
        assertEquals(identifier, physicalSystem.getIdentifier());
    }

    private XmlObject sendDescribeSensor2RequestViaPox(String procedure, String procedureDescriptionFormat) {
        DescribeSensorDocument document = DescribeSensorDocument.Factory.newInstance();
        DescribeSensorType describeSensorRequest = document.addNewDescribeSensor();
        describeSensorRequest.setService(SosConstants.SOS);
        describeSensorRequest.setVersion(Sos2Constants.SERVICEVERSION);
        describeSensorRequest.setProcedure(procedure);
        describeSensorRequest.setProcedureDescriptionFormat(procedureDescriptionFormat);
        XmlObject responseXml = pox().entity(document.xmlText(XML_OPTIONS)).response().asXmlObject();
        return responseXml;
    }

    private XmlObject sendDescribeSensor1RequestViaPox(String procedure, String outputFormat) {
        net.opengis.sos.x10.DescribeSensorDocument document = net.opengis.sos.x10.DescribeSensorDocument.Factory.newInstance();
        DescribeSensor describeSensorRequest = document.addNewDescribeSensor();
        describeSensorRequest.setService(SosConstants.SOS);
        describeSensorRequest.setVersion(Sos1Constants.SERVICEVERSION);
        describeSensorRequest.setProcedure(procedure);
        describeSensorRequest.setOutputFormat(outputFormat);
        XmlObject responseXml = pox().entity(document.xmlText(XML_OPTIONS)).response().asXmlObject();
        return responseXml;
    }

    private XmlObject sendDescribeSensor2RequestViaKvp(String procedure, String procedureDescriptionFormat) {
        return getExecutor().kvp()
                .query(OWSConstants.RequestParams.service, SosConstants.SOS)
                .query(OWSConstants.RequestParams.version, Sos2Constants.SERVICEVERSION)
                .query(OWSConstants.RequestParams.request, SosConstants.Operations.DescribeSensor.name())
                .query(SosConstants.DescribeSensorParams.procedure, procedure)
                .query(Sos2Constants.DescribeSensorParams.procedureDescriptionFormat, procedureDescriptionFormat)
                .response().asXmlObject();
    }

    private XmlObject sendDescribeSensor1RequestViaKvp(String procedure, String outputFormat) {
        return getExecutor().kvp()
                .query(OWSConstants.RequestParams.service, SosConstants.SOS)
                .query(OWSConstants.RequestParams.version, Sos1Constants.SERVICEVERSION)
                .query(OWSConstants.RequestParams.request, SosConstants.Operations.DescribeSensor.name())
                .query(SosConstants.DescribeSensorParams.procedure, procedure)
                .query(Sos1Constants.DescribeSensorParams.outputFormat, outputFormat)
                .response().asXmlObject();
    }

    protected Client pox() {
        return getExecutor().pox()
                .contentType(MediaTypes.APPLICATION_XML.toString())
                .accept(MediaTypes.APPLICATION_XML.toString());
    }

    protected void addValueToProcedure(AbstractProcess process, String identifier, String procedure, String offering, String obsProp) {
        process.addIdentifier(new SmlIdentifier(identifier, OGCConstants.URN_UNIQUE_IDENTIFIER, procedure));
        process.addPhenomenon(new OmObservableProperty(obsProp));
        process.setIdentifier(new CodeWithAuthority(procedure, "identifier_codespace"));
    }

    protected InsertSensorDocument createInsertSensorRequest(String identifier, String procedure, String offering,
            String obsProp, String procedureDescriptionFormat) throws OwsExceptionReport {
        String namespace = null;
        AbstractSensorML sml = null;
        if (SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL.equals(procedureDescriptionFormat)) {
            org.n52.sos.ogc.sensorML.System system = new org.n52.sos.ogc.sensorML.System();
            addValueToProcedure(system, identifier, procedure, offering, obsProp);
            sml = new SensorML().addMember(system);
            namespace = SensorMLConstants.NS_SML;
        } else if (SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL.equals(procedureDescriptionFormat)) {
            PhysicalSystem physicalSystem = new PhysicalSystem();
            addValueToProcedure(physicalSystem, identifier, procedure, offering, obsProp);
            sml = physicalSystem;
            namespace = SensorML20Constants.NS_SML_20;
        }
        if (namespace == null && sml == null) {
            throw new NoApplicableCodeException();
        }
        InsertSensorDocument document = InsertSensorDocument.Factory.newInstance();
        InsertSensorType insertSensor = document.addNewInsertSensor();
        insertSensor.setService(SosConstants.SOS);
        insertSensor.setVersion(Sos2Constants.SERVICEVERSION);
        insertSensor.addObservableProperty(obsProp);

        // Only URL formats are supported here because valid procedureDescriptionFormats
        // (checked by SosHelper.checkFormat) are determined by scanning all
        // ProcedureEncoder getSupportedProcedureDescriptionFormats(),
        // which specifies different supported formats by service and version.
        // Since InsertSensor is an SOS 2.0 operation, only formats listed by
        // ProcedureEncoders as 2.0 formats are supported.
        // Conversions to other requested formats are enabled by converters (e.g. SensorMLUrlMimeTypeConverter).
        insertSensor.setProcedureDescriptionFormat(procedureDescriptionFormat);

        insertSensor.addNewMetadata().addNewInsertionMetadata().set(createSensorInsertionMetadata());
        insertSensor.addNewProcedureDescription().set(CodingHelper.encodeObjectToXml(namespace, sml));
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
}
