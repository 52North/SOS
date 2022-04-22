/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.Mockito;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCachePersistenceStrategy;
import org.n52.iceland.cache.WritableContentCache;
import org.n52.iceland.cache.ctrl.CompleteCacheUpdateFactory;
import org.n52.iceland.cache.ctrl.ContentCacheFactory;
import org.n52.iceland.coding.SupportedTypeRepository;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepositoryImpl;
import org.n52.iceland.ogc.ows.OwsServiceProviderFactory;
import org.n52.janmayen.event.EventBus;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.da.sos.SOSHibernateSessionHolder;
import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.StreamingValue;
import org.n52.shetland.ogc.om.features.SfConstants;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosInsertionMetadata;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.DeleteSensorRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.sos.response.DeleteSensorResponse;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.InsertSensorResponse;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.cache.InMemoryCacheImpl;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.cache.ctrl.DefaultContentModificationListener;
import org.n52.sos.cache.ctrl.SosContentCacheControllerImpl;
import org.n52.sos.ds.SosCacheFeederHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.GetObservationDaoImpl;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.ds.hibernate.util.observation.AdditionalObservationCreatorRepository;
import org.n52.sos.ds.hibernate.util.observation.OmObservationCreatorContext;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureCreationContext;
import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.event.events.SensorDeletion;
import org.n52.sos.event.events.SensorInsertion;
import org.n52.sos.request.operator.SosInsertObservationOperatorV20;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.SosHelper;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.GmlDecoderv311;
import org.n52.svalbard.decode.GmlDecoderv321;
import org.n52.svalbard.decode.SensorMLDecoderV101;
import org.n52.svalbard.decode.SensorMLDecoderV20;
import org.n52.svalbard.decode.SweCommonDecoderV101;
import org.n52.svalbard.decode.SweCommonDecoderV20;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.GmlEncoderv311;
import org.n52.svalbard.encode.GmlEncoderv321;
import org.n52.svalbard.encode.SensorMLEncoderv101;
import org.n52.svalbard.encode.SensorMLEncoderv20;
import org.n52.svalbard.encode.SweCommonEncoderv101;
import org.n52.svalbard.encode.SweCommonEncoderv20;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.util.CodingHelper;
import org.n52.svalbard.util.SweHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.opengis.sensorML.x101.SystemDocument;
import net.opengis.swe.x20.DataRecordDocument;
import net.opengis.swe.x20.QuantityDocument;
import net.opengis.swe.x20.TextEncodingDocument;

/**
 * Test various Insert*DAOs using a common set of test data with hierarchical
 * procedures. NOTE: These tests fail intermittently. They have been excluded
 * from the normal build and set up to run multiple (100) times. They can be run
 * directly from Eclipse or via Maven on the command line with the dao-test
 * profile (mvn -P dao-test clean install)
 *
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 *
 * @since 4.0.0
 *
 */
@RunWith(Parameterized.class)
public abstract class AbstractInsertDAOTest extends HibernateTestCase {
    public static final String OFFERING1 = "offering1";

    public static final String OFFERING2 = "offering2";

    public static final String OFFERING3 = "offering3";

    public static final String PROCEDURE1 = "procedure1";

    public static final String PROCEDURE2 = "procedure2";

    public static final String PROCEDURE3 = "procedure3";

    public static final String OBSPROP1 = "obsprop1";

    public static final String OBSPROP2 = "obsprop2";

    public static final String OBSPROP3 = "obsprop3";

    public static final String FEATURE3 = "feature3";

    public static final String RESULT_TEMPLATE = "result_template";

    public static final DateTime TIME1 = new DateTime("2013-07-18T00:00:00Z");

    public static final DateTime TIME2 = new DateTime("2013-07-18T01:00:00Z");

    public static final DateTime TIME3 = new DateTime("2013-07-18T02:00:00Z");

    public static final DateTime OBS_TIME = new DateTime("2013-07-18T03:00:00Z");

    public static final DateTime OBS_TIME_SP = new DateTime("2015-07-18T03:00:00Z");

    public static final DateTime OBS_TIME_PARAM = new DateTime("2015-07-18T04:00:00Z");

    public static final DateTime OBS_TIME_HEIGHT = new DateTime("2015-07-18T05:00:00Z");

    public static final DateTime OBS_TIME_DEPTH = new DateTime("2015-07-18T06:00:00Z");

    public static final Double VAL1 = 19.1;

    public static final Double VAL2 = 19.8;

    public static final Double VAL3 = 20.4;

    public static final Double OBS_VAL = 20.8;

    public static final String TOKEN_SEPARATOR = ",";

    public static final String DECIMAL_SEPARATOR = ".";

    public static final String BLOCK_SEPARATOR = "#";

    public static final String TEMP_UNIT = "Cel";

    public static final GeometryFactory FACTORY =
            new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING_SINGLE), 4326);

    public static final Geometry GEOMETRY = FACTORY.createPoint(new Coordinate(52.7, 7.52));

    // om:parameter values
    public static final String BOOLEAN_PARAM_NAME = "booleanParamName";

    public static final boolean BOOLEAN_PARAM_VALUE = true;

    public static final String CATEGORY_PARAM_NAME = "categoryParamName";

    public static final String CATEGORY_PARAM_VALUE = "categoryParamValue";

    public static final String CATEGORY_PARAM_UNIT = "categoryParamUnit";

    public static final String COUNT_PARAM_NAME = "countParamName";

    public static final int COUNT_PARAM_VALUE = 123;

    public static final String QUANTITY_PARAM_NAME = "quantityParamName";

    public static final BigDecimal QUANTITY_PARAM_VALUE = BigDecimal.valueOf(12.3);

    public static final String QUANTITY_PARAM_UNIT = "m";

    public static final String TEXT_PARAM_NAME = "textParamName";

    public static final String TEXT_PARAM_VALUE = "textParamNValue";

    public static final BigDecimal HEIGHT_DEPTH_VALUE = BigDecimal.valueOf(10.0);

    public static final BigDecimal HEIGHT_DEPTH_VALUE_2 = BigDecimal.valueOf(20.0);

    public static final String HEIGHT_DEPTH_UNIT = "m";

    protected EventBus serviceEventBus = new EventBus();

    /* FIXTURES */
    protected final InsertSensorHandler insertSensorDAO = new InsertSensorHandler();

    protected final DeleteSensorHandler deleteSensorDAO = new DeleteSensorHandler();

    protected final InsertObservationHandler insertObservationDAO = new InsertObservationHandler();

    protected final InsertResultTemplateHandler insertResultTemplateDAO = new InsertResultTemplateHandler();

    protected final InsertResultHandler insertResultDAO = new InsertResultHandler();

    protected final GetObservationDaoImpl getObsDAO = new GetObservationDaoImpl();

    protected final GetResultTemplateHandler getResultTemplateHandler = new GetResultTemplateHandler();

    protected final GetResultHandler getResultHandler = new GetResultHandler();

    protected final SosInsertObservationOperatorV20 insertObservationOperatorv2 = new SosInsertObservationOperatorV20();

    protected final I18NDAORepository i18NDAORepository = new I18NDAORepository();

    protected final DaoFactory daoFactory = new DaoFactory();

    protected final EncoderRepository encoderRepository = new EncoderRepository();

    protected final DecoderRepository decoderRepository = new DecoderRepository();

    protected final ConverterRepository converterRepository = new ConverterRepository();

    protected final BindingRepository bindingRepository = new BindingRepository();

    protected final AdditionalObservationCreatorRepository additionalObservationCreatorRepository =
            new AdditionalObservationCreatorRepository();

    protected final HibernateProcedureDescriptionGeneratorFactoryRepository factoryRepository =
            new HibernateProcedureDescriptionGeneratorFactoryRepository();

    protected final OwsServiceMetadataRepositoryImpl serviceMetadataRepository =
            Mockito.mock(OwsServiceMetadataRepositoryImpl.class);

    protected HibernateProcedureCreationContext ctx;

    protected OmObservationCreatorContext observationCtx;

    protected final SosCacheFeederHandler cacheFeeder = new SosCacheFeederHandler();

    protected final InMemoryCacheImpl cache = new InMemoryCacheImpl();

    protected final TestingSosContentCacheControllerImpl contentCacheController =
            new TestingSosContentCacheControllerImpl();

    // optionally run these tests multiple times to expose intermittent faults
    // (use -DrepeatDaoTest=x)
    @Parameterized.Parameters
    public static List<Object[]> data() {
        int repeatDaoTest = 1;
        String repeatDaoTestStr = java.lang.System.getProperty("repeatDaoTest");

        if (repeatDaoTestStr != null) {
            repeatDaoTest = Integer.parseInt(repeatDaoTestStr);
        }
        return Arrays.asList(new Object[repeatDaoTest][0]);
    }

    public void setUp() throws OwsExceptionReport, ConverterException, EncodingException {
        GEOMETRY.setSRID(4326);
        SOSHibernateSessionHolder holder = new SOSHibernateSessionHolder();
        holder.setConnectionProvider(this);
        daoFactory.setSweHelper(new SweHelper());
        SosHelper sosHelper = new SosHelper();
        sosHelper.setServiceURL(URI.create("http://test.org"));
        daoFactory.setSosHelper(sosHelper);
        GeometryHandler geometryHandler = new GeometryHandler();
        initGeometryHandler(geometryHandler);
        daoFactory.setGeometryHandler(geometryHandler);
        HibernateFeatureQueryHandler featureQueryHandler = new HibernateFeatureQueryHandler();
        featureQueryHandler.setDaoFactory(daoFactory);
        featureQueryHandler.setI18NDAORepository(i18NDAORepository);
        featureQueryHandler.setGeometryHandler(geometryHandler);
        featureQueryHandler.setContentCacheController(contentCacheController);
        daoFactory.setFeatureQueryHandler(featureQueryHandler);
        daoFactory.setDecoderRepository(decoderRepository);
        daoFactory.setEncoderRepository(encoderRepository);
        daoFactory.setI18NDAORepository(i18NDAORepository);
        daoFactory.setSweHelper(initSweHelper());

        cacheFeeder.setConnectionProvider(holder);
        cacheFeeder.setI18NDAORepository(i18NDAORepository);
        cacheFeeder.setGeometryHandler(geometryHandler);
        initEncoder();
        initDecoder();
        bindingRepository.setComponentFactories(Optional.empty());
        bindingRepository.setComponents(Optional.empty());
        additionalObservationCreatorRepository.setComponentFactories(Optional.empty());
        additionalObservationCreatorRepository.setComponents(Optional.empty());

        contentCacheController.setPersistenceStrategy(Mockito.mock(ContentCachePersistenceStrategy.class));
        contentCacheController.setCacheFactory(Mockito.mock(ContentCacheFactory.class));
        contentCacheController.setCompleteCacheUpdateFactory(Mockito.mock(CompleteCacheUpdateFactory.class));
        contentCacheController.setCache(cache);
        cache.setSupportedTypeRepository(Mockito.mock(SupportedTypeRepository.class));

        i18NDAORepository.init();
        decoderRepository.init();
        converterRepository.init();
        factoryRepository.init();
        bindingRepository.init();
        additionalObservationCreatorRepository.init();
        DefaultContentModificationListener defaultContentModificationListener =
                new DefaultContentModificationListener(cacheFeeder, contentCacheController);
        defaultContentModificationListener.setConverterRepository(converterRepository);
        serviceEventBus.register(defaultContentModificationListener);
        ctx = new HibernateProcedureCreationContext(serviceMetadataRepository, decoderRepository, factoryRepository,
                i18NDAORepository, daoFactory, converterRepository, null, bindingRepository, null,
                contentCacheController, Mockito.mock(ProcedureDescriptionSettings.class));

        observationCtx = new OmObservationCreatorContext(serviceMetadataRepository, i18NDAORepository, daoFactory,
                new ProfileHanlderMock(), additionalObservationCreatorRepository, contentCacheController,
                featureQueryHandler, converterRepository, factoryRepository, geometryHandler, decoderRepository, null,
                bindingRepository);
        observationCtx.setDefaultLanguage("eng");
        Session session = null;
        try {
            session = getSession();
            HibernateMetadataCache.init(session);
        } finally {
            returnSession(session);
        }
        initDaos();
    }

    protected SweHelper initSweHelper() {
        SweHelper helper = new SweHelper();
        helper.setDecimalSeparator(DECIMAL_SEPARATOR);
        helper.setTokenSeparator(TOKEN_SEPARATOR);
        helper.setTupleSeparator(BLOCK_SEPARATOR);
        helper.setNorthingNames(SweConstants.SweCoordinateNames.LATITUDE);
        helper.setEastingNames(SweConstants.SweCoordinateNames.LONGITUDE);
        return helper;
    }

    @After
    public void tearDown() throws OwsExceptionReport, InterruptedException {
        H2Configuration.truncate();
    }

    @AfterClass
    public static void cleanUp() {
        H2Configuration.recreate();
    }

    private void initGeometryHandler(GeometryHandler geometryHandler) {
        geometryHandler.setAuthority("EPSG");
        geometryHandler.setStorageEpsg(4326);
        geometryHandler.setSpatialDatasource(true);
        geometryHandler.init();
    }

    private void initDaos() {
        insertSensorDAO.initForTesting(daoFactory, this);
        insertSensorDAO.setCacheController(contentCacheController);
        insertSensorDAO.init();
        deleteSensorDAO.initForTesting(daoFactory, this);
        deleteSensorDAO.setCacheController(contentCacheController);
        deleteSensorDAO.init();
        insertObservationDAO.initForTesting(daoFactory, this);
        insertObservationDAO.setCacheController(contentCacheController);
        insertObservationDAO.init();
        insertResultTemplateDAO.initForTesting(daoFactory, this);
        insertResultTemplateDAO.setCacheController(contentCacheController);
        insertResultTemplateDAO.init();
        insertResultDAO.initForTesting(daoFactory, this);
        insertResultDAO.setCacheController(contentCacheController);
        insertResultDAO.setDecoderRepository(decoderRepository);
        insertResultDAO.init();
        getObsDAO.setConnectionProvider(this);
        getObsDAO.setDaoFactory(daoFactory);
        getObsDAO.setEncoderRepository(encoderRepository);
        getObsDAO.setDefaultLanguage("eng");
        getObsDAO.setOmObservationCreatorContext(observationCtx);
        getResultTemplateHandler.setConnectionProvider(this);
        getResultTemplateHandler.setDecoderRepository(decoderRepository);
        getResultTemplateHandler.setDaoFactory(daoFactory);
        getResultTemplateHandler.init();
        getResultHandler.setConnectionProvider(this);
        getResultHandler.setDecoderRepository(decoderRepository);
        getResultHandler.setDaoFactory(daoFactory);
        getResultHandler.setProfileHandler(new ProfileHanlderMock());
        getResultHandler.init();
    }

    private void initEncoder() {
        GmlEncoderv321 gmlEncoderv321 = new GmlEncoderv321();
        gmlEncoderv321.setEncoderRepository(encoderRepository);
        gmlEncoderv321.setXmlOptions(XmlOptions::new);

        SensorMLEncoderv20 sensorMLEncoderv20 = new SensorMLEncoderv20();
        sensorMLEncoderv20.setXmlOptions(XmlOptions::new);
        sensorMLEncoderv20.setEncoderRepository(encoderRepository);

        SweCommonEncoderv20 sweCommonEncoderv20 = new SweCommonEncoderv20();
        sweCommonEncoderv20.setEncoderRepository(encoderRepository);
        sweCommonEncoderv20.setXmlOptions(XmlOptions::new);

        GmlEncoderv311 gmlEncoderv311 = new GmlEncoderv311();
        gmlEncoderv311.setEncoderRepository(encoderRepository);
        gmlEncoderv311.setXmlOptions(XmlOptions::new);

        SensorMLEncoderv101 sensorMLEncoderv101 = new SensorMLEncoderv101();
        sensorMLEncoderv101.setXmlOptions(XmlOptions::new);
        sensorMLEncoderv101.setEncoderRepository(encoderRepository);

        SweCommonEncoderv101 sweCommonEncoderv101 = new SweCommonEncoderv101();
        sweCommonEncoderv101.setEncoderRepository(encoderRepository);
        sweCommonEncoderv101.setXmlOptions(XmlOptions::new);

        encoderRepository.setEncoders(Arrays.asList(gmlEncoderv321, sensorMLEncoderv20, sweCommonEncoderv20,
                gmlEncoderv311, sensorMLEncoderv101, sweCommonEncoderv101));
        encoderRepository.init();
    }

    private void initDecoder() {
        GmlDecoderv321 gmlDecoderv321 = new GmlDecoderv321();
        gmlDecoderv321.setDecoderRepository(decoderRepository);
        gmlDecoderv321.setXmlOptions(XmlOptions::new);

        SensorMLDecoderV20 sensorMLDecoderv20 = new SensorMLDecoderV20();
        sensorMLDecoderv20.setXmlOptions(XmlOptions::new);
        sensorMLDecoderv20.setDecoderRepository(decoderRepository);

        SweCommonDecoderV20 sweCommonDecoderv20 = new SweCommonDecoderV20();
        sweCommonDecoderv20.setDecoderRepository(decoderRepository);
        sweCommonDecoderv20.setXmlOptions(XmlOptions::new);

        GmlDecoderv311 gmlDecoderv311 = new GmlDecoderv311();
        // gmlDecoderv311.setDecoderRepository(decoderRepository);
        // gmlDecoderv311.setXmlOptions(XmlOptions::new);

        SensorMLDecoderV101 sensorMLDecoderv101 = new SensorMLDecoderV101();
        sensorMLDecoderv101.setXmlOptions(XmlOptions::new);
        sensorMLDecoderv101.setDecoderRepository(decoderRepository);

        SweCommonDecoderV101 sweCommonDecoderv101 = new SweCommonDecoderV101();
        sweCommonDecoderv101.setDecoderRepository(decoderRepository);
        sweCommonDecoderv101.setXmlOptions(XmlOptions::new);

        decoderRepository.setDecoders(Arrays.asList(gmlDecoderv321, sensorMLDecoderv20, sweCommonDecoderv20,
                gmlDecoderv311, sensorMLDecoderv101, sweCommonDecoderv101));
        decoderRepository.init();
    }

    protected void insertSensor(String procedure, String offering, String obsProp, String parentProcedure, String observationType)
            throws OwsExceptionReport, EncodingException {
        InsertSensorRequest req = new InsertSensorRequest();
        req.setAssignedProcedureIdentifier(procedure);
        List<SosOffering> assignedOfferings = Lists.newLinkedList();
        assignedOfferings.add(new SosOffering(offering, offering));
        req.setObservableProperty(CollectionHelper.list(obsProp));
        req.setProcedureDescriptionFormat(SensorMLConstants.NS_SML);
        SosInsertionMetadata meta = new SosInsertionMetadata();
        meta.setObservationTypes(Sets.newHashSet(observationType));
        meta.setFeatureOfInterestTypes(Sets.newHashSet(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT));
        req.setMetadata(meta);

        org.n52.shetland.ogc.sensorML.System system = new org.n52.shetland.ogc.sensorML.System();
        SosProcedureDescription procedureDescription = new SosProcedureDescription(system);
        system.setIdentifier(procedure);
        procedureDescription.setIdentifier(procedure);
        if (parentProcedure != null) {
            procedureDescription.setParentProcedure(new ReferenceType(parentProcedure, parentProcedure));
        }
        SystemDocument xbSystemDoc = SystemDocument.Factory.newInstance();
        xbSystemDoc.addNewSystem().set(encodeObjectToXml(SensorMLConstants.NS_SML, system));
        system.setXml(xbSystemDoc.xmlText());
        req.setProcedureDescription(procedureDescription);
        req.setAssignedOfferings(assignedOfferings);
        InsertSensorResponse resp = insertSensorDAO.insertSensor(req);
        this.serviceEventBus.submit(new SensorInsertion(req, resp));
    }

    protected void modifySystem(org.n52.shetland.ogc.sensorML.System system) {
        // nothing to do here
    }

    protected SweTextEncoding getTextEncoding() throws EncodingException {
        SweTextEncoding sweTextEncoding = new SweTextEncoding();
        sweTextEncoding.setDecimalSeparator(DECIMAL_SEPARATOR);
        sweTextEncoding.setTokenSeparator(TOKEN_SEPARATOR);
        sweTextEncoding.setBlockSeparator(BLOCK_SEPARATOR);
        sweTextEncoding.setXml(createTextEncodingString(sweTextEncoding));
        return sweTextEncoding;
    }

    protected String createTextEncodingString(SweTextEncoding textEncoding) throws EncodingException {
        TextEncodingDocument xbTextEncDoc = TextEncodingDocument.Factory.newInstance();
        xbTextEncDoc.addNewTextEncoding().set(encodeObjectToXml(SweConstants.NS_SWE_20, textEncoding));
        return xbTextEncDoc.xmlText();
    }

    protected String createDataRecordString(SweDataRecord dataRecord) throws EncodingException {
        DataRecordDocument xbDataRecordDoc = DataRecordDocument.Factory.newInstance();
        xbDataRecordDoc.addNewDataRecord().set(encodeObjectToXml(SweConstants.NS_SWE_20, dataRecord));
        return xbDataRecordDoc.xmlText();
    }

    protected String createQuantityString(SweQuantity quantity) throws EncodingException {
        QuantityDocument xbQuantitydDoc = QuantityDocument.Factory.newInstance();
        xbQuantitydDoc.addNewQuantity().set(encodeObjectToXml(SweConstants.NS_SWE_20, quantity));
        return xbQuantitydDoc.xmlText();
    }

    protected XmlObject encodeObjectToXml(String ns, Object o) throws EncodingException {
        return (XmlObject) encoderRepository.getEncoder(CodingHelper.getEncoderKey(ns, o)).encode(o);
    }

    @SuppressWarnings("unused")
    protected void deleteSensor(String procedure) throws OwsExceptionReport {
        DeleteSensorRequest req = new DeleteSensorRequest();
        req.setProcedureIdentifier(procedure);
        DeleteSensorResponse resp = deleteSensorDAO.deleteSensor(req);
        this.serviceEventBus.submit(new SensorDeletion(req, resp));
    }

    protected SosContentCache getCache() {
        return cache;
    }

    protected void updateCache() throws OwsExceptionReport {
        cacheFeeder.updateCache(cache);
    }

    protected OmObservationConstellation getOmObsConst(String procedureId, String obsPropId, String unit,
            String offeringId, String featureId, String obsType, Session session)
            throws OwsExceptionReport, ConverterException {
        OmObservationConstellation obsConst = new OmObservationConstellation();
        ProcedureEntity procedure = daoFactory.getProcedureDAO().getProcedureForIdentifier(procedureId, session);
        OwsServiceProviderFactory serviceProviderFactory = Mockito.mock(OwsServiceProviderFactory.class);
        SosProcedureDescription spd = new HibernateProcedureConverter(ctx).createSosProcedureDescription(procedure,
                SensorMLConstants.NS_SML, Sos2Constants.SERVICEVERSION, session);
        obsConst.setProcedure(spd);
        OmObservableProperty omObservableProperty = new OmObservableProperty(obsPropId);
        omObservableProperty.setUnit(unit);
        obsConst.setObservableProperty(omObservableProperty);
        obsConst.setFeatureOfInterest(new SamplingFeature(new CodeWithAuthority(featureId)));

        Set<String> offerings = new HashSet<String>();
        offerings.add(offeringId);
        obsConst.setOfferings(offerings);
        obsConst.setObservationType(obsType);
        return obsConst;
    }

    protected abstract void assertInsertionAftermath(boolean afterCacheUpdate) throws OwsExceptionReport;

    protected void assertInsertionAftermathBeforeAndAfterCacheReload() throws OwsExceptionReport, InterruptedException {
        // check once for cache changes triggered by sos event
        assertInsertionAftermath(false);

        // run a cache update
        updateCache();

        // check again after cache is reloaded
        assertInsertionAftermath(true);
    }

    protected void addParameter(OmObservation obs) {
        obs.addParameter(createBooleanParameter(BOOLEAN_PARAM_NAME, BOOLEAN_PARAM_VALUE));
        obs.addParameter(createCategoryParameter(CATEGORY_PARAM_NAME, CATEGORY_PARAM_VALUE, CATEGORY_PARAM_UNIT));
        obs.addParameter(createCountParameter(COUNT_PARAM_NAME, COUNT_PARAM_VALUE));
        obs.addParameter(createQuantityParameter(QUANTITY_PARAM_NAME, QUANTITY_PARAM_VALUE, QUANTITY_PARAM_UNIT));
        obs.addParameter(createTextParameter(TEXT_PARAM_NAME, TEXT_PARAM_VALUE));
    }

    protected void checkOmParameter(String offering, String procedure, String obsprop, String feature,
            DateTime obsTimeParam) throws OwsExceptionReport {
        GetObservationRequest getObsReq =
                createDefaultGetObservationRequest(offering, procedure, obsprop, obsTimeParam, feature);
        GetObservationResponse getObsResponse =
                getObsDAO.queryObservationData(getObsReq, getGetObservationRequest(getObsReq));
        assertThat(getObsResponse, notNullValue());
        assertThat(getObsResponse.getObservationCollection().hasNext(), is(true));

        OmObservation omObservation = getObservation(getObsResponse);
        assertThat(omObservation.isSetParameter(), is(true));
        assertThat(omObservation.getParameter().size(), is(5));
        for (NamedValue<?> namedValue : omObservation.getParameter()) {
            assertThat(namedValue.isSetName(), is(true));
            assertThat(namedValue.getName().isSetHref(), is(true));
            if (BOOLEAN_PARAM_NAME.equals(namedValue.getName().getHref())) {
                checkNamedValue(namedValue, BOOLEAN_PARAM_NAME, BOOLEAN_PARAM_VALUE, null);
            } else if (CATEGORY_PARAM_NAME.equals(namedValue.getName().getHref())) {
                checkNamedValue(namedValue, CATEGORY_PARAM_NAME, CATEGORY_PARAM_VALUE, CATEGORY_PARAM_UNIT);
            } else if (COUNT_PARAM_NAME.equals(namedValue.getName().getHref())) {
                checkNamedValue(namedValue, COUNT_PARAM_NAME, COUNT_PARAM_VALUE, null);
            } else if (QUANTITY_PARAM_NAME.equals(namedValue.getName().getHref())) {
                checkNamedValue(namedValue, QUANTITY_PARAM_NAME, QUANTITY_PARAM_VALUE, QUANTITY_PARAM_UNIT);
            } else if (TEXT_PARAM_NAME.equals(namedValue.getName().getHref())) {
                checkNamedValue(namedValue, TEXT_PARAM_NAME, TEXT_PARAM_VALUE, null);
            }
        }
    }

    protected OmObservation getObservation(GetObservationResponse getObsResponse)
            throws NoSuchElementException, OwsExceptionReport {
        OmObservation observation = getObsResponse.getObservationCollection().next();
        if (observation.getValue() instanceof StreamingValue) {
            assertThat(((StreamingValue) observation.getValue()).hasNext(), is(true));
            OmObservation omObservation = ((StreamingValue) observation.getValue()).next();
            assertNotNull(omObservation);
            while (((StreamingValue) observation.getValue()).hasNext()) {
                ((StreamingValue) observation.getValue()).next();
            }
            return omObservation;
        }
        return observation;
    }

    protected GetObservationResponse getGetObservationRequest(GetObservationRequest req) {
        return new GetObservationResponse(req.getService(), req.getVersion());
    }

    protected NamedValue<?> createSamplingGeometry(Geometry geometry) {
        final NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
        final ReferenceType referenceType = new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        namedValue.setName(referenceType);
        // TODO add lat/long version
        namedValue.setValue(new GeometryValue(geometry));
        return namedValue;
    }

    protected OmObservation checkSamplingGeometry(String offering, String procedure, String obsprop, String feature,
            DateTime time, Geometry geometry) throws OwsExceptionReport {
        GetObservationRequest getObsReq =
                createDefaultGetObservationRequest(offering, procedure, obsprop, time, feature);
        GetObservationResponse getObsResponse =
                getObsDAO.queryObservationData(getObsReq, getGetObservationRequest(getObsReq));
        assertThat(getObsResponse, notNullValue());
        OmObservation omObservation = getObservation(getObsResponse);
        assertThat(omObservation.isSetParameter(), is(true));
        assertThat(omObservation.isSetSpatialFilteringProfileParameter(), is(true));
        checkNamedValue(omObservation.getSpatialFilteringProfileParameter(), OmConstants.PARAM_NAME_SAMPLING_GEOMETRY,
                geometry, null);
        return omObservation;
    }

    protected GetObservationRequest createDefaultGetObservationRequest(String reqOffering, String reqProcedure,
            String reqObsProp, DateTime time, String obsFeature) {
        GetObservationRequest getObsReq = new GetObservationRequest();
        getObsReq.setOfferings(
                getCache().getChildOfferings(reqOffering, true, true).stream().collect(Collectors.toList()));
        getObsReq.setProcedures(
                getCache().getChildProcedures(reqProcedure, true, true).stream().collect(Collectors.toList()));
        getObsReq.setObservedProperties(CollectionHelper.list(reqObsProp));
        getObsReq.setFeatureIdentifiers(
                getCache().getChildFeatures(obsFeature, true, true).stream().collect(Collectors.toList()));
        getObsReq.setResponseFormat(OmConstants.NS_OM_2);
        TemporalFilter tempFilter = new TemporalFilter(FilterConstants.TimeOperator.TM_Equals, new TimeInstant(time),
                TemporalRestrictions.PHENOMENON_TIME_VALUE_REFERENCE);
        getObsReq.setTemporalFilters(CollectionHelper.list(tempFilter));
        getObsReq.setService(SosConstants.SOS);
        getObsReq.setVersion(Sos2Constants.SERVICEVERSION);
        return getObsReq;
    }

    protected void checkObservation(String reqOffering, String reqProcedure, String reqObsProp, DateTime time,
            String obsProcedure, String obsObsProp, String obsFeature, Double obsVal, String obsUnit)
            throws OwsExceptionReport {
        GetObservationRequest getObsReq =
                createDefaultGetObservationRequest(reqOffering, reqProcedure, reqObsProp, time, obsFeature);
        GetObservationResponse getObsResponse =
                getObsDAO.queryObservationData(getObsReq, getGetObservationRequest(getObsReq));
        assertThat(getObsResponse, notNullValue());
        assertThat(getObsResponse.getObservationCollection().hasNext(), is(true));
        OmObservation omObservation = getObservation(getObsResponse);
        assertThat(omObservation.getObservationConstellation(), notNullValue());
        OmObservationConstellation obsConst = omObservation.getObservationConstellation();
        assertThat(obsConst.getProcedure().getIdentifier(), is(obsProcedure));
        assertThat(obsConst.getObservableProperty().getIdentifier(), is(obsObsProp));

        // TODO this fails
        // assertThat(obsConst.getFeatureOfInterest().getIdentifier().getValue(),
        // is(obsFeature));

        checkValue(omObservation, time, obsVal, obsUnit);
    }

    protected void checkValue(OmObservation omObservation, DateTime time, Double obsVal, String obsUnit) {
        assertThat(omObservation.getValue(), notNullValue());
        ObservationValue<?> value = omObservation.getValue();
        assertThat(value.getValue(), instanceOf(QuantityValue.class));
        assertThat(value.getPhenomenonTime(), instanceOf(TimeInstant.class));
        TimeInstant timeInstant = (TimeInstant) value.getPhenomenonTime();
        assertThat(timeInstant.getValue().toDate(), is(time.toDate()));
        QuantityValue quantityValue = (QuantityValue) value.getValue();
        if (obsVal != null) {
            assertTrue(quantityValue.isSetValue());
            assertThat(quantityValue.getValue().doubleValue(), is(obsVal));
        } else {
            assertFalse(quantityValue.isSetValue());
        }
        assertThat(quantityValue.getUnit(), is(obsUnit));
    }

    protected void checkNamedValue(NamedValue<?> namedValue, String name, Object value, String unit) {
        assertThat(namedValue.isSetName(), is(true));
        assertThat(namedValue.getName().isSetHref(), is(true));
        assertThat(namedValue.getName().getHref(), is(name));
        assertThat(namedValue.isSetValue(), is(true));
        assertThat(namedValue.getValue().isSetValue(), is(true));
        if (namedValue.getValue().getValue() instanceof BigDecimal) {
            assertTrue(((BigDecimal) namedValue.getValue().getValue()).compareTo((BigDecimal) value) == 0);
        } else {
            assertThat(namedValue.getValue().getValue(), is(value));
        }
        if (!Strings.isNullOrEmpty(unit)) {
            assertThat(namedValue.getValue().isSetUnit(), is(true));
            assertThat(namedValue.getValue().getUnit(), is(unit));
        }
    }

    protected NamedValue<?> createBooleanParameter(String name, boolean value) {
        final NamedValue<Boolean> namedValue = new NamedValue<Boolean>();
        final ReferenceType referenceType = new ReferenceType(name);
        namedValue.setName(referenceType);
        namedValue.setValue(new BooleanValue(value));
        return namedValue;
    }

    protected NamedValue<?> createCategoryParameter(String name, String value, String unit) {
        final NamedValue<String> namedValue = new NamedValue<String>();
        final ReferenceType referenceType = new ReferenceType(name);
        namedValue.setName(referenceType);
        namedValue.setValue(new CategoryValue(value, unit));
        return namedValue;
    }

    protected NamedValue<?> createCountParameter(String name, int value) {
        final NamedValue<Integer> namedValue = new NamedValue<Integer>();
        final ReferenceType referenceType = new ReferenceType(name);
        namedValue.setName(referenceType);
        namedValue.setValue(new CountValue(value));
        return namedValue;
    }

    protected NamedValue<?> createQuantityParameter(String name, BigDecimal value, String unit) {
        final NamedValue<BigDecimal> namedValue = new NamedValue<BigDecimal>();
        final ReferenceType referenceType = new ReferenceType(name);
        namedValue.setName(referenceType);
        namedValue.setValue(new QuantityValue(value, unit));
        return namedValue;
    }

    protected NamedValue<?> createTextParameter(String name, String value) {
        final NamedValue<String> namedValue = new NamedValue<String>();
        final ReferenceType referenceType = new ReferenceType(name);
        namedValue.setName(referenceType);
        namedValue.setValue(new TextValue(value));
        return namedValue;
    }

    protected void printXml(Object o, String namespace) throws EncodingException {
        Encoder<XmlObject, Object> encoder = encoderRepository.getEncoder(new XmlEncoderKey(namespace, o.getClass()));
        if (encoder != null) {
            System.out.println(encoder.encode(o).xmlText(new XmlOptions()));
        }
    }

    private class TestingSosContentCacheControllerImpl extends SosContentCacheControllerImpl {

        @Override
        protected void setCache(WritableContentCache wcc) {
            super.setCache(wcc);
        }
    }

}
