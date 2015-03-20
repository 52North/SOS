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
package org.n52.sos.ds.hibernate.util;

import org.junit.Test;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class HibernateProcedureConverterTest {
//    private static final ProcedureDescriptionFormat PROCEDURE_DESCRIPTION_FORMAT =
//            new ProcedureDescriptionFormat()
//            .setProcedureDescriptionFormat(SensorMLConstants.NS_SML);
//    private static final String PROCEDURE_IDENTIFIER =
//            "test-procedure-identifier";
//    private static String PROCEDURE_DESCRIPTION_NON_SPATIAL;
//    private static final String[] OBSERVABLE_PROPERTIES = {
//        "test-obserable-property-1", "test-obserable-property-2" };
//    private static final String SERVICE_VERSION = Sos2Constants.SERVICEVERSION;
//    private static ContentCache CONTENT_CACHE = null;
//    private static Session SESSION = null;
//    private static HibernateProcedureConverter converter;
//    private static Procedure spatialProcedure;
//    private static Procedure nonSpatialProc;
//    private static String METHOD_DESCRIPTION;
//
//    @BeforeClass
//    public static void initFixtures() {
//        // init settings
//        ProcedureDescriptionSettings.getInstance();
//
//        spatialProcedure =
//                (Procedure) (new Procedure().setProcedureDescriptionFormat(PROCEDURE_DESCRIPTION_FORMAT)
//                        .setIdentifier(PROCEDURE_IDENTIFIER).setSrid(Constants.EPSG_WGS84).setAltitude(42.0).setLongitude(7.2)
//                        .setLatitude(52.0));
//
//        nonSpatialProc =
//                (Procedure) (new Procedure().setProcedureDescriptionFormat(PROCEDURE_DESCRIPTION_FORMAT)
//                        .setIdentifier(PROCEDURE_IDENTIFIER));
//
//        PROCEDURE_DESCRIPTION_NON_SPATIAL =
//                format(ProcedureDescriptionSettings.getInstance().getDescriptionTemplate(), "procedure",
//                        PROCEDURE_IDENTIFIER, Joiner.on(",").join(OBSERVABLE_PROPERTIES));
//
//        METHOD_DESCRIPTION =
//                format(ProcedureDescriptionSettings.getInstance().getProcessMethodRulesDefinitionDescriptionTemplate(),
//                        PROCEDURE_IDENTIFIER, Joiner.on(",").join(OBSERVABLE_PROPERTIES));
//
//        CONTENT_CACHE = new WritableCache();
//    }
//
//    @BeforeClass
//    public static void initConverterMockup() throws OwsExceptionReport, ConverterException {
//        SESSION = mock(Session.class);
//
//        converter = mock(HibernateProcedureConverter.class);
//        when(converter.getObservablePropertiesForProcedure(anyString())).thenReturn(OBSERVABLE_PROPERTIES);
//
//        when(converter.getServiceProvider()).thenReturn(mock(SosServiceProvider.class));
//
//        when(converter.getExampleObservation(anyString(), anyString(), any(Session.class))).thenReturn(
//                new NumericObservation());
//
//        when(converter.getServiceConfig()).thenReturn(mock(ServiceConfiguration.class));
//        when(converter.getEnvelope((Collection<SosOffering>) any())).thenReturn(new SosEnvelope());
//        when(converter.createSosProcedureDescription(any(Procedure.class), anyString(), anyString(),
//                        any(Session.class))).thenCallRealMethod();
//        when(converter.createSosProcedureDescription(any(Procedure.class), anyString(), anyString(),
//                        anyMapOf(String.class, Procedure.class), any(Session.class))).thenCallRealMethod();
//
//        doNothing().when(converter).checkOutputFormatWithDescriptionFormat(any(Procedure.class), anyString(), anyString());
//
//        when(converter.getCache()).thenReturn(CONTENT_CACHE);
//
//    }
//
//    @Test(expected = NoApplicableCodeException.class)
//    public void should_throw_exception_with_null_parameters() throws OwsExceptionReport, ConverterException {
//        converter.createSosProcedureDescription(null, null, null, null);
//    }
//
//    @Test
//    public void should_return_sml_system_for_spatial_procedure() throws OwsExceptionReport, ConverterException {
//        final SosProcedureDescription description =
//                converter.createSosProcedureDescription(spatialProcedure, NS_SML, SERVICE_VERSION, SESSION);
//        assertThat(description, is(instanceOf(SensorML.class)));
//        final SensorML smlDesc = (SensorML) description;
//        assertThat(smlDesc.getMembers().get(0), instanceOf(System.class));
//    }
//
//    @Test
//    public void should_return_sml_process_model_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final SosProcedureDescription description =
//                converter.createSosProcedureDescription(nonSpatialProc, NS_SML, SERVICE_VERSION, SESSION);
//        assertThat(description, is(instanceOf(SensorML.class)));
//        final SensorML smlDesc = (SensorML) description;
//        assertThat(smlDesc.getMembers().get(0), instanceOf(ProcessModel.class));
//    }
//
//    @Test
//    public void should_set_description_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final AbstractProcess process = setupAbstractProcess();
//        assertThat(process.getDescriptions().size(), is(1));
//        assertThat(process.getDescriptions().get(0), is(PROCEDURE_DESCRIPTION_NON_SPATIAL));
//    }
//
//    @Test
//    public void should_set_name_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final AbstractProcess process = setupAbstractProcess();
//        assertThat(process.getNames().size(), is(1));
//        assertThat(process.getNames().get(0).getValue(), is(PROCEDURE_IDENTIFIER));
//    }
//
//    @Test
//    public void should_set_method_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getMethod(), instanceOf(ProcessMethod.class));
//    }
//
//    @Test
//    public void should_set_identifier_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getIdentifier(), is(PROCEDURE_IDENTIFIER));
//        assertThat(uniqueIdIdentifierOf(pModel), is(PROCEDURE_IDENTIFIER));
//    }
//
//    @Test
//    public void should_set_longname_and_shortname_identifier_for_smlProcessModel() throws OwsExceptionReport,
//            ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getIdentifications().size(), is(greaterThanOrEqualTo(2)));
//        assertThat(longNameIdentifierOf(pModel), is(PROCEDURE_IDENTIFIER));
//        assertThat(shortNameIdentifierOf(pModel), is(PROCEDURE_IDENTIFIER));
//    }
//
//    @Test
//    public void should_set_outputs_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getOutputs().size(), is(greaterThanOrEqualTo(2)));
//        assertThat(outputDefinition(0, pModel), is(OBSERVABLE_PROPERTIES[0]));
//        assertThat(outputDefinition(1, pModel), is(OBSERVABLE_PROPERTIES[1]));
//    }
//
//    @Test
//    public void should_set_processMethod_description_for_smlProcessModel() throws OwsExceptionReport,
//            ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getMethod().getRulesDefinition(), instanceOf(RulesDefinition.class));
//        assertThat(pModel.getMethod().getRulesDefinition().isSetDescription(), is(Boolean.TRUE));
//        assertThat(pModel.getMethod().getRulesDefinition().getDescription().isEmpty(), is(Boolean.FALSE));
//        assertThat(pModel.getMethod().getRulesDefinition().getDescription(), is(METHOD_DESCRIPTION));
//    }
//
//    @Test
//    public void should_set_classifiers_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        // local fixtures
//        final String intendedApplicationValue = "test-intended-application-value";
//        final String intendedApplicationDefinition = "test-intended-application-definition";
//        final String procedureTypeValue = "test-sensor-type-value";
//        final String procedureTypeDefinition = "test-sensor-type-definition";
//        final ProcedureDescriptionSettings sdgs = ProcedureDescriptionSettings.getInstance();
//
//        sdgs.setClassifierIntendedApplicationValue(intendedApplicationValue);
//        sdgs.setClassifierIntendedApplicationDefinition(intendedApplicationDefinition);
//        sdgs.setClassifierProcedureTypeValue(procedureTypeValue);
//        sdgs.setClassifierProcedureTypeDefinition(procedureTypeDefinition);
//
//        final ProcessModel pModel = setupProcessModel();
//
//        assertThat(pModel.getClassifications().size(), is(2));
//
//        for (final SmlClassifier classifier : pModel.getClassifications()) {
//            if (classifier.getName().equalsIgnoreCase(INTENDED_APPLICATION)) {
//                assertThat(classifier.getDefinition(), is(intendedApplicationDefinition));
//                assertThat(classifier.getValue(), is(intendedApplicationValue));
//            } else if (classifier.getName().equalsIgnoreCase(PROCEDURE_TYPE)) {
//                assertThat(classifier.getDefinition(), is(procedureTypeDefinition));
//                assertThat(classifier.getValue(), is(procedureTypeValue));
//            }
//        }
//    }
//
//    private String outputDefinition(final int i, final ProcessModel pModel) {
//        return pModel.getOutputs().get(i).getIoValue().getDefinition();
//    }
//
//    private String shortNameIdentifierOf(final ProcessModel pModel) {
//        return getIdentifier(pModel, ELEMENT_NAME_SHORT_NAME, "urn:ogc:def:identifier:OGC:1.0:shortname");
//    }
//
//    private String longNameIdentifierOf(final ProcessModel pModel) {
//        return getIdentifier(pModel, ELEMENT_NAME_LONG_NAME, "urn:ogc:def:identifier:OGC:1.0:longname");
//    }
//
//    private String uniqueIdIdentifierOf(final ProcessModel pModel) {
//        return getIdentifier(pModel, URN_UNIQUE_IDENTIFIER_END, URN_UNIQUE_IDENTIFIER);
//    }
//
//    private String getIdentifier(final ProcessModel pModel, final String idName, final String idDefinition) {
//        for (final SmlIdentifier identifier : pModel.getIdentifications()) {
//            if (identifier.getName().equalsIgnoreCase(idName)
//                    && identifier.getDefinition().equalsIgnoreCase(idDefinition)) {
//                return identifier.getValue();
//            }
//        }
//        return null;
//    }
//
//    protected ProcessModel setupProcessModel() throws OwsExceptionReport, ConverterException {
//        final AbstractProcess process = setupAbstractProcess();
//        assertThat(process, instanceOf(ProcessModel.class));
//        final ProcessModel pModel = (ProcessModel) process;
//        return pModel;
//    }
//
//    protected AbstractProcess setupAbstractProcess() throws OwsExceptionReport, ConverterException {
//        final SensorML description =
//                (SensorML) converter.createSosProcedureDescription(nonSpatialProc, NS_SML, SERVICE_VERSION, SESSION);
//        assertThat(description.getMembers().size(), is(1));
//        final AbstractProcess process = description.getMembers().get(0);
//        return process;
//    }
//
//    private static final ProcedureDescriptionFormat PROCEDURE_DESCRIPTION_FORMAT = new ProcedureDescriptionFormat()
//            .setProcedureDescriptionFormat(SensorMLConstants.NS_SML);
//
//    private static final String PROCEDURE_IDENTIFIER = "test-procedure-identifier";
//
//    private static String PROCEDURE_DESCRIPTION_NON_SPATIAL;
//
//    private static final String[] OBSERVABLE_PROPERTIES = { "test-obserable-property-1", "test-obserable-property-2" };
//
//    private static final String SERVICE_VERSION = Sos2Constants.SERVICEVERSION;
//
//    private static ContentCache CONTENT_CACHE = null;
//
//    private static Session SESSION = null;
//
//    private static HibernateProcedureConverter converter;
//
//    private static Procedure spatialProcedure;
//
//    private static Procedure nonSpatialProc;
//
//    private static String METHOD_DESCRIPTION;
//
//    @BeforeClass
//    public static void initFixtures() {
//        // init settings
//        ProcedureDescriptionSettings.getInstance();
//
//        spatialProcedure =
//                (Procedure) (new Procedure().setProcedureDescriptionFormat(PROCEDURE_DESCRIPTION_FORMAT)
//                        .setSrid(Constants.EPSG_WGS84).setAltitude(42.0).setLongitude(7.2)
//                        .setLatitude(52.0).setIdentifier(PROCEDURE_IDENTIFIER));
//
//        nonSpatialProc =
//                (Procedure) (new Procedure().setProcedureDescriptionFormat(PROCEDURE_DESCRIPTION_FORMAT)
//                        .setIdentifier(PROCEDURE_IDENTIFIER));
//
//        PROCEDURE_DESCRIPTION_NON_SPATIAL =
//                format(ProcedureDescriptionSettings.getInstance().getDescriptionTemplate(), "procedure",
//                        PROCEDURE_IDENTIFIER, Joiner.on(",").join(OBSERVABLE_PROPERTIES));
//
//        METHOD_DESCRIPTION =
//                format(ProcedureDescriptionSettings.getInstance().getProcessMethodRulesDefinitionDescriptionTemplate(),
//                        PROCEDURE_IDENTIFIER, Joiner.on(",").join(OBSERVABLE_PROPERTIES));
//
//        CONTENT_CACHE = new WritableCache();
//    }
//
//    @BeforeClass
//    public static void initConverterMockup() throws OwsExceptionReport, ConverterException {
//        SESSION = mock(Session.class);
//
//        converter = mock(HibernateProcedureConverter.class);
//        when(converter.getObservablePropertiesForProcedure(anyString())).thenReturn(OBSERVABLE_PROPERTIES);
//
//        when(converter.getServiceProvider()).thenReturn(mock(SosServiceProvider.class));
//
//        when(converter.getExampleObservation(anyString(), anyString(), any(Session.class))).thenReturn(
//                new NumericObservation());
//
//        when(converter.getServiceConfig()).thenReturn(mock(ServiceConfiguration.class));
//
//        when(
//                converter.createSosProcedureDescription(any(Procedure.class), anyString(), anyString(),
//                        any(Session.class))).thenCallRealMethod();
//        when(
//                converter.createSosProcedureDescription(any(Procedure.class), anyString(), anyString(),
//                        anyMapOf(String.class, Procedure.class), any(Session.class))).thenCallRealMethod();
//
//        when(converter.checkOutputFormatWithDescriptionFormat(anyString(), anyString())).thenReturn(true);
//
//        when(converter.getCache()).thenReturn(CONTENT_CACHE);
//
//    }
//
//    @Test(expected = NoApplicableCodeException.class)
//    public void should_throw_exception_with_null_parameters() throws OwsExceptionReport, ConverterException {
//        converter.createSosProcedureDescription(null, null, null, null);
//    }
//
//    @Test
//    public void should_return_sml_system_for_spatial_procedure() throws OwsExceptionReport, ConverterException {
//        final SosProcedureDescription description =
//                converter.createSosProcedureDescription(spatialProcedure, NS_SML, SERVICE_VERSION, SESSION);
//        assertThat(description, is(instanceOf(SensorML.class)));
//        final SensorML smlDesc = (SensorML) description;
//        assertThat(smlDesc.getMembers().get(0), instanceOf(System.class));
//    }
//
//    @Test
//    public void should_return_sml_process_model_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final SosProcedureDescription description =
//                converter.createSosProcedureDescription(nonSpatialProc, NS_SML, SERVICE_VERSION, SESSION);
//        assertThat(description, is(instanceOf(SensorML.class)));
//        final SensorML smlDesc = (SensorML) description;
//        assertThat(smlDesc.getMembers().get(0), instanceOf(ProcessModel.class));
//    }
//
//    @Test
//    public void should_set_description_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final AbstractProcess process = setupAbstractProcess();
//        assertThat(process.getDescriptions().size(), is(1));
//        assertThat(process.getDescriptions().get(0), is(PROCEDURE_DESCRIPTION_NON_SPATIAL));
//    }
//
//    @Test
//    public void should_set_name_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final AbstractProcess process = setupAbstractProcess();
//        assertThat(process.getNames().size(), is(1));
//        assertThat(process.getNames().get(0).getValue(), is(PROCEDURE_IDENTIFIER));
//    }
//
//    @Test
//    public void should_set_method_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getMethod(), instanceOf(ProcessMethod.class));
//    }
//
//    @Test
//    public void should_set_identifier_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getIdentifierString(), is(PROCEDURE_IDENTIFIER));
//        assertThat(uniqueIdIdentifierOf(pModel), is(PROCEDURE_IDENTIFIER));
//    }
//
//    @Test
//    public void should_set_longname_and_shortname_identifier_for_smlProcessModel() throws OwsExceptionReport,
//            ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getIdentifications().size(), is(greaterThanOrEqualTo(2)));
//        assertThat(longNameIdentifierOf(pModel), is(PROCEDURE_IDENTIFIER));
//        assertThat(shortNameIdentifierOf(pModel), is(PROCEDURE_IDENTIFIER));
//    }
//
//    @Test
//    public void should_set_outputs_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getOutputs().size(), is(greaterThanOrEqualTo(2)));
//        assertThat(outputDefinition(0, pModel), is(OBSERVABLE_PROPERTIES[0]));
//        assertThat(outputDefinition(1, pModel), is(OBSERVABLE_PROPERTIES[1]));
//    }
//
//    @Test
//    public void should_set_processMethod_description_for_smlProcessModel() throws OwsExceptionReport,
//            ConverterException {
//        final ProcessModel pModel = setupProcessModel();
//        assertThat(pModel.getMethod().getRulesDefinition(), instanceOf(RulesDefinition.class));
//        assertThat(pModel.getMethod().getRulesDefinition().isSetDescription(), is(Boolean.TRUE));
//        assertThat(pModel.getMethod().getRulesDefinition().getDescription().isEmpty(), is(Boolean.FALSE));
//        assertThat(pModel.getMethod().getRulesDefinition().getDescription(), is(METHOD_DESCRIPTION));
//    }
//
//    @Test
//    public void should_set_classifiers_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        // local fixtures
//        final String intendedApplicationValue = "test-intended-application-value";
//        final String intendedApplicationDefinition = "test-intended-application-definition";
//        final String procedureTypeValue = "test-sensor-type-value";
//        final String procedureTypeDefinition = "test-sensor-type-definition";
//        final ProcedureDescriptionSettings sdgs = ProcedureDescriptionSettings.getInstance();
//
//        sdgs.setClassifierIntendedApplicationValue(intendedApplicationValue);
//        sdgs.setClassifierIntendedApplicationDefinition(intendedApplicationDefinition);
//        sdgs.setClassifierProcedureTypeValue(procedureTypeValue);
//        sdgs.setClassifierProcedureTypeDefinition(procedureTypeDefinition);
//
//        final ProcessModel pModel = setupProcessModel();
//
//        assertThat(pModel.getClassifications().size(), is(2));
//
//        for (final SmlClassifier classifier : pModel.getClassifications()) {
//            if (classifier.getName().equalsIgnoreCase(INTENDED_APPLICATION)) {
//                assertThat(classifier.getDefinition(), is(intendedApplicationDefinition));
//                assertThat(classifier.getValue(), is(intendedApplicationValue));
//            } else if (classifier.getName().equalsIgnoreCase(PROCEDURE_TYPE)) {
//                assertThat(classifier.getDefinition(), is(procedureTypeDefinition));
//                assertThat(classifier.getValue(), is(procedureTypeValue));
//            }
//        }
//    }
//
//    private String outputDefinition(final int i, final ProcessModel pModel) {
//        return pModel.getOutputs().get(i).getIoValue().getDefinition();
//    }
//
//    private String shortNameIdentifierOf(final ProcessModel pModel) {
//        return getIdentifier(pModel, ELEMENT_NAME_SHORT_NAME, "urn:ogc:def:identifier:OGC:1.0:shortname");
//    }
//
//    private String longNameIdentifierOf(final ProcessModel pModel) {
//        return getIdentifier(pModel, ELEMENT_NAME_LONG_NAME, "urn:ogc:def:identifier:OGC:1.0:longname");
//    }
//
//    private String uniqueIdIdentifierOf(final ProcessModel pModel) {
//        return getIdentifier(pModel, URN_UNIQUE_IDENTIFIER_END, URN_UNIQUE_IDENTIFIER);
//    }
//
//    private String getIdentifier(final ProcessModel pModel, final String idName, final String idDefinition) {
//        for (final SmlIdentifier identifier : pModel.getIdentifications()) {
//            if (identifier.getName().equalsIgnoreCase(idName)
//                    && identifier.getDefinition().equalsIgnoreCase(idDefinition)) {
//                return identifier.getValue();
//            }
//        }
//        return null;
//    }
//
//    protected ProcessModel setupProcessModel() throws OwsExceptionReport, ConverterException {
//        final AbstractProcess process = setupAbstractProcess();
//        assertThat(process, instanceOf(ProcessModel.class));
//        final ProcessModel pModel = (ProcessModel) process;
//        return pModel;
//    }
//
//    protected AbstractProcess setupAbstractProcess() throws OwsExceptionReport, ConverterException {
//        final SensorML description =
//                (SensorML) converter.createSosProcedureDescription(nonSpatialProc, NS_SML, SERVICE_VERSION, SESSION);
//        assertThat(description.getMembers().size(), is(1));
//        final AbstractProcess process = description.getMembers().get(0);
//        return process;
//    }

    @Test
    public void testCreateIdentifications() throws Exception {

    }

}
