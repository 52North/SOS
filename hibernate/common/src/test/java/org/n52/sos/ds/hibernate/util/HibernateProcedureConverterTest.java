/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

//import static java.lang.String.format;
//import static org.hamcrest.Matchers.greaterThanOrEqualTo;
//import static org.hamcrest.Matchers.instanceOf;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.junit.Assert.assertThat;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.util.Optional;
//
//import org.hibernate.Session;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.n52.faroe.SettingsServiceImpl;
//import org.n52.iceland.binding.BindingRepository;
//import org.n52.iceland.cache.ContentCacheController;
//import org.n52.iceland.cache.ctrl.ContentCacheControllerImpl;
//import org.n52.iceland.convert.ConverterException;
//import org.n52.iceland.convert.ConverterRepository;
//import org.n52.iceland.i18n.I18NDAORepository;
//import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
//import org.n52.iceland.ogc.ows.OwsServiceMetadataRepositoryImpl;
//import org.n52.iceland.service.operator.ServiceOperatorRepository;
//import org.n52.iceland.util.LocalizedProducer;
//import org.n52.series.db.beans.FormatEntity;
//import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.OGCConstants;
//import org.n52.shetland.ogc.ows.OwsServiceProvider;
//import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
//import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
//import org.n52.shetland.ogc.sensorML.AbstractProcess;
//import org.n52.shetland.ogc.sensorML.ProcessMethod;
//import org.n52.shetland.ogc.sensorML.ProcessModel;
//import org.n52.shetland.ogc.sensorML.RulesDefinition;
//import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
//import org.n52.shetland.ogc.sensorML.elements.SmlClassifier;
//import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
//import org.n52.shetland.ogc.sos.Sos2Constants;
//import org.n52.shetland.ogc.sos.SosProcedureDescription;
//import org.n52.sos.cache.InMemoryCacheImpl;
//import org.n52.sos.cache.SosContentCache;
//import org.n52.sos.convert.SensorML20SensorML101Converter;
//import org.n52.sos.convert.SensorML20UrlMimeTypeConverter;
//import org.n52.sos.ds.hibernate.dao.DaoFactory;
//import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
//import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureCreationContext;
//import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorFactoryRepository;
//import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorFactorySml101;
//import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorSml101;
//import org.n52.sos.service.ProcedureDescriptionSettings;
//import org.n52.sos.service.profile.DefaultProfileHandler;
//import org.n52.sos.service.profile.ProfileHandler;
//import org.n52.sos.util.GeometryHandler;
//import org.n52.svalbard.decode.DecoderRepository;
//
//import com.google.common.base.Joiner;
//import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 4.0.0
 */
public class HibernateProcedureConverterTest implements SensorMLConstants, OGCConstants {
//    private static final FormatEntity PROCEDURE_DESCRIPTION_FORMAT =
//            new FormatEntity().setFormat(SensorMLConstants.NS_SML);
//
//    private static final String PROCEDURE_IDENTIFIER = "test-procedure-identifier";
//
//    private static String PROCEDURE_DESCRIPTION_NON_SPATIAL;
//
//    private static final String[] OBSERVABLE_PROPERTIES = { "test-obserable-property-1", "test-obserable-property-2" };
//
//    private static final String SERVICE_VERSION = Sos2Constants.SERVICEVERSION;
//
//    private static SosContentCache CONTENT_CACHE = null;
//
//    private static Session SESSION = null;
//
//    private static HibernateProcedureConverter converter;
//
//    private static ProcedureEntity spatialProcedure;
//
//    private static ProcedureEntity nonSpatialProc;
//
//    private static String METHOD_DESCRIPTION;
//
//    private static HibernateProcedureDescriptionGeneratorFactoryRepository FACTORY_REPOSITORY;
//
//    private static ConverterRepository CONVERTER_REPOSITORY;
//
//    private static OwsServiceMetadataRepository SERVICE_METADATA_REPO;
//
//    @BeforeClass
//    public static void initFixtures() {
//        // init settings
//
//        spatialProcedure = (ProcedureEntity) new ProcedureEntity();
//        spatialProcedure.setFormat(PROCEDURE_DESCRIPTION_FORMAT);
//        spatialProcedure.setIdentifier(PROCEDURE_IDENTIFIER);
//        // spatialProcedure.setSrid(4326);
//        // spatialProcedure.setAltitude(42.0);
//        // spatialProcedure.setLongitude(7.2);
//        // spatialProcedure..setLatitude(52.0);
//
//        nonSpatialProc = (ProcedureEntity) new ProcedureEntity();
//        nonSpatialProc.setFormat(PROCEDURE_DESCRIPTION_FORMAT);
//        nonSpatialProc.setIdentifier(PROCEDURE_IDENTIFIER);
//
//        PROCEDURE_DESCRIPTION_NON_SPATIAL =
//                String.format("The %s with the id %s observes the following properties: %s.", "procedure",
//                        PROCEDURE_IDENTIFIER, Joiner.on(",").join(OBSERVABLE_PROPERTIES));
//
//        METHOD_DESCRIPTION = String.format(
//                "The procedure %s generates the following output(s): %s. The input(s) is/are unknown (this description is generated).",
//                PROCEDURE_IDENTIFIER, Joiner.on(",").join(OBSERVABLE_PROPERTIES));
//
//        CONTENT_CACHE = new InMemoryCacheImpl();
//
////        I18NDAORepository i18NDAORepository = new I18NDAORepository();
////        DaoFactory daoFactory = new DaoFactory();
////        daoFactory.setI18NDAORepository(i18NDAORepository);
////        GeometryHandler geometryHandler = new GeometryHandler();
////        initGeometryHandler(geometryHandler);
////        ProfileHandler profileHandler = new DefaultProfileHandler();
////        HibernateProcedureDescriptionGeneratorFactorySml101 hibernateProcedureDescriptionGeneratorSml101 =
////                new HibernateProcedureDescriptionGeneratorFactorySml101(daoFactory, new SettingsServiceImpl(), geometryHandler,
////                        i18NDAORepository, new ContentCacheControllerImpl(), profileHandler);
//        FACTORY_REPOSITORY = new HibernateProcedureDescriptionGeneratorFactoryRepository();
//        CONVERTER_REPOSITORY = new ConverterRepository();
//        CONVERTER_REPOSITORY.setComponents(Optional
//                .of(Lists.newArrayList(new SensorML20SensorML101Converter(), new SensorML20UrlMimeTypeConverter())));
//        CONVERTER_REPOSITORY.init();
//
//        SERVICE_METADATA_REPO = new OwsServiceMetadataRepositoryMock();
//
//    }
//
//    private static void initGeometryHandler(GeometryHandler geometryHandler) {
//        geometryHandler.setAuthority("EPSG");
//        geometryHandler.setStorageEpsg(4326);
//        geometryHandler.init();
//    }
//
//    @BeforeClass
//    public static void initConverterMockup() throws OwsExceptionReport, ConverterException {
//        SESSION = mock(Session.class);
//
//        DecoderRepository decoderRepository = mock(DecoderRepository.class);
//
//        I18NDAORepository i18nr = mock(I18NDAORepository.class);
//        DaoFactory daoFactory = mock(DaoFactory.class);
//        ConverterRepository converterRepository = mock(ConverterRepository.class);
//        GeometryHandler geometryHandler = mock(GeometryHandler.class);
//        BindingRepository bindingRepository = mock(BindingRepository.class);
//        ServiceOperatorRepository serviceOperatorRepository = mock(ServiceOperatorRepository.class);
//        ContentCacheController contentCacheController = mock(ContentCacheController.class);
//        ProcedureDescriptionSettings procedureSettings = mock(ProcedureDescriptionSettings.class);
//
//        HibernateProcedureCreationContext ctxMock = mock(HibernateProcedureCreationContext.class);
//
//        HibernateProcedureCreationContext hibernateProcedureCreationContext = new HibernateProcedureCreationContext(SERVICE_METADATA_REPO, decoderRepository, FACTORY_REPOSITORY, i18nr,
//                daoFactory, converterRepository, geometryHandler, bindingRepository, serviceOperatorRepository,
//                contentCacheController, procedureSettings);
////        when(converter.get(Mockito.anyString())).thenReturn(OBSERVABLE_PROPERTIES);
////
////        when(converter.getServiceProvider()).thenReturn(mock(SosServiceProvider.class));
////
////        when(converter.getExampleObservation(anyString(), anyString(), any(Session.class)))
////                .thenReturn(new NumericObservation());
////
////        when(converter.getServiceConfig()).thenReturn(mock(ServiceConfiguration.class));
////        when(converter.getEnvelope((Collection<SosOffering>) any())).thenReturn(new SosEnvelope());
////        when(converter.createSosProcedureDescription(any(ProcedureEntity.class), anyString(), anyString(),
////                any(Session.class))).thenCallRealMethod();
////        when(converter.createSosProcedureDescription(any(ProcedureEntity.class), anyString(), anyString(),
////                anyMapOf(String.class, Procedure.class), any(Session.class))).thenCallRealMethod();
////
////        doNothing().when(converter).checkOutputFormatWithDescriptionFormat(any(ProcedureEntity.class), anyString(),
////                anyString());
//
//        when(ctxMock.getCache()).thenReturn(CONTENT_CACHE);
//        when(ctxMock.getFactoryRepository()).thenReturn(FACTORY_REPOSITORY);
//        when(ctxMock.getConverterRepository()).thenReturn(CONVERTER_REPOSITORY);
//        when(ctxMock.getServiceMetadataRepository()).thenReturn(SERVICE_METADATA_REPO);
//
//        converter = new HibernateProcedureConverter(ctxMock);
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
//        final SensorML smlDesc = (SensorML) description.getProcedureDescription();
//        assertThat(smlDesc.getMembers().get(0), instanceOf(System.class));
//    }
//
//    @Test
//    public void should_return_sml_process_model_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final SosProcedureDescription description =
//                converter.createSosProcedureDescription(nonSpatialProc, NS_SML, SERVICE_VERSION, SESSION);
//        assertThat(description, is(instanceOf(SensorML.class)));
//        final SensorML smlDesc = (SensorML) description.getProcedureDescription();
//        assertThat(smlDesc.getMembers().get(0), instanceOf(ProcessModel.class));
//    }
//
//    @Test
//    public void should_set_description_for_smlProcessModel() throws OwsExceptionReport, ConverterException {
//        final AbstractProcess process = setupAbstractProcess();
//        assertThat(process.getDescription(), notNullValue());
//        assertThat(process.getDescription(), is(PROCEDURE_DESCRIPTION_NON_SPATIAL));
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
//    public void should_set_longname_and_shortname_identifier_for_smlProcessModel()
//            throws OwsExceptionReport, ConverterException {
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
//    public void should_set_processMethod_description_for_smlProcessModel()
//            throws OwsExceptionReport, ConverterException {
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
//        final ProcedureDescriptionSettings sdgs = new ProcedureDescriptionSettings();
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
//            if (classifier.getName().equalsIgnoreCase(SmlClassifier.INTENDED_APPLICATION)) {
//                assertThat(classifier.getDefinition(), is(intendedApplicationDefinition));
//                assertThat(classifier.getValue(), is(intendedApplicationValue));
//            } else if (classifier.getName().equalsIgnoreCase(SmlClassifier.PROCEDURE_TYPE)) {
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
//                (SensorML) converter.createSosProcedureDescription(nonSpatialProc, NS_SML, SERVICE_VERSION, SESSION)
//                        .getProcedureDescription();
//        assertThat(description.getMembers().size(), is(1));
//        final AbstractProcess process = description.getMembers().get(0);
//        return process;
//    }

}
