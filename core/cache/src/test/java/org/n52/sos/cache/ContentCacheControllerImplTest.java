/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.n52.faroe.ConfigurationError;
import org.n52.iceland.cache.ctrl.ContentCacheControllerImpl;
import org.n52.iceland.cache.ctrl.ContentCacheFactory;
import org.n52.iceland.cache.ctrl.persistence.ImmediatePersistenceStrategy;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.features.SfConstants;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.request.DeleteSensorRequest;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.sos.response.InsertResultTemplateResponse;
import org.n52.shetland.ogc.sos.response.InsertSensorResponse;
import org.n52.shetland.ogc.swes.SwesFeatureRelationship;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.cache.ctrl.CompleteCacheUpdateFactoryImpl;
import org.n52.sos.cache.ctrl.action.ObservationInsertionUpdate;
import org.n52.sos.cache.ctrl.action.ResultInsertionUpdate;
import org.n52.sos.cache.ctrl.action.ResultTemplateInsertionUpdate;
import org.n52.sos.cache.ctrl.action.SensorDeletionUpdate;
import org.n52.sos.cache.ctrl.action.SensorInsertionUpdate;
import org.n52.sos.ds.CacheFeederHandler;
import org.n52.sos.ds.MockCacheFeederDAO;
import org.n52.sos.util.builder.DataRecordBuilder;
import org.n52.sos.util.builder.DeleteSensorRequestBuilder;
import org.n52.sos.util.builder.InsertObservationRequestBuilder;
import org.n52.sos.util.builder.InsertResultTemplateRequestBuilder;
import org.n52.sos.util.builder.InsertResultTemplateResponseBuilder;
import org.n52.sos.util.builder.InsertSensorRequestBuilder;
import org.n52.sos.util.builder.InsertSensorResponseBuilder;
import org.n52.sos.util.builder.ObservablePropertyBuilder;
import org.n52.sos.util.builder.ObservationBuilder;
import org.n52.sos.util.builder.ObservationConstellationBuilder;
import org.n52.sos.util.builder.ProcedureDescriptionBuilder;
import org.n52.sos.util.builder.QuantityObservationValueBuilder;
import org.n52.sos.util.builder.QuantityValueBuilder;
import org.n52.sos.util.builder.SamplingFeatureBuilder;
import org.n52.sos.util.builder.SweDataArrayBuilder;
import org.n52.sos.util.builder.SweDataArrayValueBuilder;
import org.n52.sos.util.builder.SweTimeBuilder;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a> Test after DeleteObservation => not possible with
 *         InMemory because of bounding box issues, for example.
 *
 * @since 4.0.0
 */
public class ContentCacheControllerImplTest {
    /* FIXTURES */
    private static final String RELATED_FEATURE_ROLE_2 = "test-role-2";

    private static final String RELATED_FEATURE_ROLE = "test-role-1";

    private static final String FEATURE_2 = "test-related-feature-2";

    private static final String OBSERVATION_TYPE_2 = "test-observation-type-2";

    private static final String OBSERVATION_TYPE = "test-observation-type";

    private static final String FEATURE_OF_INTEREST_TYPE = "test-featureOfInterest-type";

    private static final String OFFERING_NAME_EXTENSION = "-offering-name";

    private static final String OFFERING_IDENTIFIER_EXTENSION = "-offering-identifier";

    private static final String OBSERVATION_ID = "test-observation-id";

    private static final String CODESPACE = "test-codespace";

    private static final String FEATURE = "test-feature";

    private static final String OBSERVABLE_PROPERTY = "test-observable-property";

    private static final String PROCEDURE = "test-procedure";

    private static final String PROCEDURE_2 = "test-procedure-2";

    private static final String RESULT_TEMPLATE_IDENTIFIER = "test-result-template";

    private static final String OFFERING = PROCEDURE + OFFERING_IDENTIFIER_EXTENSION;

    private static final String MAX_TIME = "maxtime";

    private static final String MIN_TIME = "mintime";

    private static final int WGS84 = 4326;

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private OwsServiceRequest request;

    private ContentCacheControllerImpl controller;

    private OwsServiceResponse response;

    private OmObservation observation;

    private ConverterRepository converter;

    @Before
    public void initController() {
        this.controller = createController();
        converter = new ConverterRepository();
        converter.init();
    }

    @After
    public void tearDown() {
        this.controller.destroy();
    }

    private ContentCacheControllerImpl createController() throws ConfigurationError {
        ImmediatePersistenceStrategy persistenceStrategy = new ImmediatePersistenceStrategy();
        persistenceStrategy.setConfigLocationProvider(tempFolder.getRoot()::getAbsolutePath);
        persistenceStrategy.init();
        CompleteCacheUpdateFactoryImpl cacheUpdateFactory = new CompleteCacheUpdateFactoryImpl();
        CacheFeederHandler cacheFeederHandler = new NoOpCacheFeederHandler();
        cacheUpdateFactory.setCacheFeederHandler(cacheFeederHandler);
        ContentCacheFactory cacheFactory = InMemoryCacheImpl::new;

        ContentCacheControllerImpl ccc = new ContentCacheControllerImpl();
        ccc.setCacheFactory(cacheFactory);
        ccc.setPersistenceStrategy(persistenceStrategy);
        ccc.setCompleteCacheUpdateFactory(cacheUpdateFactory);
        ccc.setUpdateInterval(0);
        ccc.init();
        return ccc;
    }

    @Test
    public void testSerialization() throws IOException {
        File tempFile =
                new File(tempFolder.getRoot().toPath().resolve("WEB-INF").resolve("tmp").toFile(), "cache.tmp");

        Files.deleteIfExists(tempFile.toPath());
       MatcherAssert.assertThat(tempFile, Matchers.is(Matchers.not(Existing.existing())));
        this.controller = createController();
       MatcherAssert.assertThat(getCache().getFeaturesOfInterest(), Matchers.is(Matchers.empty()));
        getCache().addFeatureOfInterest(FEATURE);
       MatcherAssert.assertThat(getCache().getFeaturesOfInterest(), Matchers.contains(FEATURE));
        this.controller.destroy();

       MatcherAssert.assertThat(tempFile, Matchers.is(Existing.existing()));
        this.controller = createController();
       MatcherAssert.assertThat(tempFile, Matchers.is(Existing.existing()));
       MatcherAssert.assertThat(getCache().getFeaturesOfInterest(), Matchers.contains(FEATURE));
    }

    @After
    public void setAllFixturesToNullAfterEachTest() {
        this.controller.destroy();
        request = null;
        controller = null;
        observation = null;
        response = null;
    }

    @Test
    public void should_update_global_temporal_BoundingBox_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);
        final DateTime phenomenonTime = DateTimeHelper.toUTC(
                ((TimeInstant) ((InsertObservationRequest) request).getObservations().get(0).getPhenomenonTime())
                        .getValue());

        Assert.assertEquals(MAX_TIME, getCache().getMaxPhenomenonTime(), phenomenonTime);

        Assert.assertEquals(MIN_TIME, getCache().getMinPhenomenonTime(), phenomenonTime);
    }

    @Test
    public void should_contain_procedure_after_InsertObservation() throws OwsExceptionReport {
        insertObservationPreparation();
        checkProcedureNotInCache(getSensorIdFromInsertObservation());
        checkNotOfferingProcedureRelation(getCache().getProceduresForOffering(getFirstOffering()),
                getSensorIdFromInsertObservation());
        checkNotObservablePropertyProcedureRelation(
                getCache().getProceduresForObservableProperty(getObservablePropertyFromInsertObservation()),
                getSensorIdFromInsertObservation());
        checkNotProcedureObservablePropertyRelation(
                getCache().getObservablePropertiesForProcedure(getSensorIdFromInsertObservation()),
                getObservablePropertyFromInsertObservation());
        checkNotProcedureOfferingRelation(getCache().getOfferingsForProcedure(getSensorIdFromInsertObservation()),
                getFirstOffering());
    }

    private void insertObservationPreparation() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        updateCacheWithSingleObservation(PROCEDURE);
    }

    @Test
    public void should_contain_FeatureOfInterest_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        checkFeatureNotInCache(getFoiIdFromInsertObservationRequest());

        checkNotFeatureProcedureRelation(
                getCache().getProceduresForFeatureOfInterest(getFoiIdFromInsertObservationRequest()),
                getSensorIdFromInsertObservation());

        checkParentFeatureForFeature(getFoiIdFromInsertObservationRequest());

        checkChildFeatureForFeature(getFoiIdFromInsertObservationRequest());

        checkOfferingFeatureRelation(getCache().getFeaturesOfInterestForOffering(getFirstOffering()),
                getFoiIdFromInsertObservationRequest());

    }

    @Test
    public void should_contain_envelopes_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        checkGlobalEnvelope(
                getReferencedEnvelopeFromObservation(((InsertObservationRequest) request).getObservations().get(0)));
        checkOfferingEnvelope(getFirstOffering(),
                getReferencedEnvelopeFromObservation(((InsertObservationRequest) request).getObservations().get(0)));
        checkNoEnvelopForOffering(getFirstOffering());
        checkEnvelopForOfferingDiffsForFeature(getFirstOffering(),
                getReferencedEnvelopeFromObservation(((InsertObservationRequest) request).getObservations().get(0)));
    }

    @Test
    public void should_contain_observation_timestamp_in_temporal_envelope_of_offering_after_InsertObservation()
            throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        Assert.assertTrue("temporal envelope of offering does NOT contain observation timestamp",
                (getCache().getMinPhenomenonTimeForOffering(getFirstOffering())
                        .isBefore(getPhenomenonTimeFromObservation())
                        || getCache().getMinPhenomenonTimeForOffering(getFirstOffering())
                                .isEqual(getPhenomenonTimeFromObservation()))
                        && (getCache().getMaxPhenomenonTimeForOffering(getFirstOffering())
                                .isAfter(getPhenomenonTimeFromObservation())
                                || getCache().getMaxPhenomenonTimeForOffering(getFirstOffering())
                                        .isEqual(getPhenomenonTimeFromObservation())));
    }

    @Test
    public void should_contain_observation_timestamp_in_temporal_envelope_of_procedure_after_InsertObservation()
            throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        Assert.assertTrue("temporal envelope of procedure does NOT contain observation timestamp",
                (getCache().getMinPhenomenonTimeForProcedure(getProcedure())
                        .isBefore(getPhenomenonTimeFromObservation())
                        || getCache().getMinPhenomenonTimeForProcedure(getProcedure())
                                .isEqual(getPhenomenonTimeFromObservation()))
                        && (getCache().getMaxPhenomenonTimeForProcedure(getProcedure())
                                .isAfter(getPhenomenonTimeFromObservation())
                                || getCache().getMaxPhenomenonTimeForProcedure(getProcedure())
                                        .isEqual(getPhenomenonTimeFromObservation())));
    }

    @Test
    public void should_contain_observable_property_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);
        checkNotOfferingObservablePropertyRelation(getCache().getObservablePropertiesForOffering(getFirstOffering()),
                getObservablePropertyFromInsertObservation());
        checkNotObservablePropertyOfferinRelation(
                getCache().getOfferingsForObservableProperty(getObservablePropertyFromInsertObservation()),
                getFirstOffering());
    }

    @Test
    public void should_contain_offering_observation_type_relation_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);
        checkNotOfferingObservationTypeRelation(getCache().getObservationTypesForOffering(getFirstOffering()),
                getObservationTypeFromFirstObservation());
    }

    @Test
    public void should_contain_procedure_after_InsertSensor() throws OwsExceptionReport {

        updateCacheWithInsertSensor(PROCEDURE);

    }

    @Test
    public void should_contain_procedure_offering_relations_after_InsertSensor() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        checkNotOfferingProcedureRelation(getCache().getProceduresForOffering(getAssignedOfferingId()),
                getSensorIdFromInsertSensorRequest());
        checkNotProcedureOfferingRelation(getCache()
                .getOfferingsForProcedure(getSensorIdFromInsertSensorRequest()), getAssignedOfferingId());
    }

    @Test
    public void should_contain_observable_property_relations_after_InsertSensor() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);

        Assert.assertTrue("observable property -> procedure relation NOT in cache",
                getCache().getProceduresForObservableProperty(getObservablePropertyFromInsertSensor())
                        .contains(getAssignedProcedure()));

        Assert.assertTrue("procedure -> observable property relation NOT in cache",
                getCache().getObservablePropertiesForProcedure(getAssignedProcedure())
                        .contains(getObservablePropertyFromInsertSensor()));

        Assert.assertTrue("observable property -> offering relation NOT in cache",
                getCache().getOfferingsForObservableProperty(getObservablePropertyFromInsertSensor())
                        .contains(getAssignedOfferingId()));

        Assert.assertTrue("offering -> observable property relation NOT in cache",
                getCache().getObservablePropertiesForOffering(getAssignedOfferingId())
                        .contains(getObservablePropertyFromInsertSensor()));

    }

    @Test
    public void should_contain_offering_name_after_InsertSensor() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);

        Assert.assertTrue("offering NOT in cache", getCache().getOfferings().contains(getAssignedOfferingId()));
    }

    @Test
    public void should_contain_allowed_observation_types_after_InsertSensor() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);

        for (String observationType : ((InsertSensorRequest) request).getMetadata().getObservationTypes()) {
            Assert.assertTrue("observation type NOT in cache", getCache()
                    .getAllowedObservationTypesForOffering(getAssignedOfferingId()).contains(observationType));
        }
    }

    @Test
    public void should_contain_related_features_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);

        Assert.assertFalse("offering -> related feature relations NOT in cache",
                getCache().getRelatedFeaturesForOffering(getAssignedOfferingId()).isEmpty());

        for (SwesFeatureRelationship relatedFeature : ((InsertSensorRequest) request).getRelatedFeatures()) {
            Assert.assertTrue("single \"offering -> related features relation\" NOT in cache",
                    getCache().getRelatedFeaturesForOffering(getAssignedOfferingId())
                            .contains(relatedFeature.getFeature().getIdentifierCodeWithAuthority().getValue()));

            Assert.assertTrue("single \"related feature -> role relation\" NOT in cache",
                    getCache()
                            .getRolesForRelatedFeature(
                                    relatedFeature.getFeature().getIdentifierCodeWithAuthority().getValue())
                            .contains(relatedFeature.getRole()));
        }
    }

    @Test
    public void should_not_contain_procedure_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertFalse("procedure STILL in cache", getCache().getProcedures().contains(getProcedureIdentifier()));

    }

    @Test
    public void should_not_contain_procedure_offering_relations_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertFalse("offering -> procedure relation STILL in cache",
                getCache().getProceduresForOffering(getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION)
                        .contains(getProcedureIdentifier()));

        Assert.assertFalse("procedure -> offering relation STILL in cache",
                getCache().getOfferingsForProcedure(getProcedureIdentifier())
                        .contains(getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION));
    }

    @Test
    public void should_not_contain_observable_properties_relations_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertTrue("observable property -> procedure relation STILL in cache",
                getCache().getProceduresForObservableProperty(OBSERVABLE_PROPERTY) == null || !getCache()
                        .getProceduresForObservableProperty(OBSERVABLE_PROPERTY).contains(getProcedureIdentifier()));

        Assert.assertTrue("procedure -> observable property relation STILL in cache",
                getCache().getObservablePropertiesForProcedure(getProcedureIdentifier()) == null || !getCache()
                        .getObservablePropertiesForProcedure(getProcedureIdentifier()).contains(OBSERVABLE_PROPERTY));

        Assert.assertTrue("observable property -> offering relation STILL in cache",
                getCache().getOfferingsForObservableProperty(OBSERVABLE_PROPERTY) == null || !getCache()
                        .getOfferingsForObservableProperty(OBSERVABLE_PROPERTY).contains(getProcedureIdentifier()));

        Assert.assertTrue("offering -> observable property relation STILL in cache",
                getCache().getObservablePropertiesForOffering(getProcedureIdentifier()) == null || !getCache()
                        .getObservablePropertiesForOffering(getProcedureIdentifier()).contains(OBSERVABLE_PROPERTY));
    }

    @Test
    public void should_not_contain_parent_procedures_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertTrue("parent procedures STILL available in cache",
                getCache().getParentProcedures(PROCEDURE, true, false) == null
                        || getCache().getParentProcedures(PROCEDURE, true, false).isEmpty());
    }

    @Test
    public void should_not_contain_child_procedures_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertTrue("child procedures STILL available in cache",
                getCache().getChildProcedures(PROCEDURE, true, false) == null
                        || getCache().getChildProcedures(PROCEDURE, true, false).isEmpty());
    }

    @Test
    public void should_not_contain_an_envlope_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertTrue("envolpe for offering STILL in cache",
                getCache().getEnvelopeForOffering(getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION) == null);
    }

    @Test
    public void should_not_contain_global_envelope_if_deleted_sensor_was_last_one_available()
            throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertFalse("global envelope STILL in cache after deletion of last sensor",
                getCache().getGlobalEnvelope() != null && getCache().getGlobalEnvelope().isSetEnvelope());
    }

    @Test
    public void should_not_contain_temporal_bounding_box_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertTrue("temporal bounding box STILL in cache",
                getCache().getMaxPhenomenonTimeForOffering(
                        getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION) == null
                        && getCache().getMinPhenomenonTimeForOffering(
                                getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION) == null);
    }

    @Test
    public void should_not_contain_global_temporal_bouding_box_if_deleted_sensor_was_last_one_available()
            throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE_2);
        deleteSensorPreparation();

        Assert.assertTrue("global temporal bounding box still in cache after deletion of last sensor",
                getCache().getMaxPhenomenonTime() == null && getCache().getMinPhenomenonTime() == null);
    }

    @Test
    public void should_not_contain_related_features_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertTrue("related features for offering STILL in cache",
                getCache().getRelatedFeaturesForOffering(OFFERING).isEmpty());
    }

    @Test
    public void should_not_contain_offering_names_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertNull("offering name STILL in cache", getCache().getNameForOffering(OFFERING));
    }

    @Test
    public void should_not_contain_features_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertTrue("features STILL related to offering",
                getCache().getFeaturesOfInterestForOffering(OFFERING).isEmpty());

        Assert.assertFalse("features STILL in cache", getCache().getFeaturesOfInterest().contains(FEATURE));
    }

    @Test
    public void should_not_contain_observation_types_for_the_offerings_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertTrue("observation types for offering STILL in cache",
                getCache().getObservationTypesForOffering(OFFERING).isEmpty());
    }

    @Test
    public void should_not_contain_feature_to_procedure_relations_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertFalse("feature -> procedure relation STILL in cache",
                getCache().getProceduresForFeatureOfInterest(FEATURE).contains(PROCEDURE));
    }

    @Test
    public void should_not_contain_offering_observable_property_relations_after_DeleteSensor()
            throws OwsExceptionReport {
        deleteSensorPreparation();

        Assert.assertTrue("offering to observable property relation STILL in cache",
                getCache().getObservablePropertiesForOffering(OFFERING).isEmpty());

        Assert.assertTrue("observable property to offering relation STILL in cache",
                getCache().getOfferingsForObservableProperty(OFFERING).isEmpty());
    }

    @Test
    public void should_not_contain_related_result_templates_after_DeleteSensor() throws OwsExceptionReport {
        updateCacheWithInsertResultTemplate(RESULT_TEMPLATE_IDENTIFIER);
        deleteSensorPreparation();

        Assert.assertTrue("offering -> result templates relations STILL in cache",
                getCache().getResultTemplatesForOffering(OFFERING) == null
                        || getCache().getResultTemplatesForOffering(OFFERING).isEmpty());

        Assert.assertFalse("result template identifier STILL in cache",
                getCache().getResultTemplates().contains(RESULT_TEMPLATE_IDENTIFIER));
    }

    @Test
    public void should_reset_global_temporal_bounding_box_after_DeleteSensor_of_not_last_sensor()
            throws OwsExceptionReport {

        long phenomenonTime = 0L;

        updateCacheWithInsertSensor(PROCEDURE_2);
        updateCacheWithSingleObservation(PROCEDURE_2, phenomenonTime);

       MatcherAssert.assertThat(getCache().getMaxPhenomenonTime(), Matchers.is(CoreMatchers.notNullValue()));
       MatcherAssert.assertThat(getCache().getMinPhenomenonTime(), Matchers.is(CoreMatchers.notNullValue()));

        deleteSensorPreparation();

       MatcherAssert.assertThat(getCache().getMaxPhenomenonTime(), Matchers.is(CoreMatchers.notNullValue()));
       MatcherAssert.assertThat(getCache().getMinPhenomenonTime(), Matchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void should_reset_global_spatial_bounding_box_after_DeleteSensor_of_not_last_sensor()
            throws OwsExceptionReport {
        double xCoord = -55.0;
        double yCoord = -66.0;
        int epsgCode = WGS84;
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), epsgCode);
        Geometry geom = geometryFactory.createPoint(new Coordinate(xCoord, yCoord));
        ReferencedEnvelope offering2Envelope = new ReferencedEnvelope(geom.getEnvelopeInternal(), epsgCode);

        updateCacheWithInsertSensor(PROCEDURE_2);
        updateCacheWithSingleObservation(PROCEDURE_2, xCoord, yCoord, epsgCode, FEATURE_2);

        deleteSensorPreparation();

       MatcherAssert.assertThat(getCache().getGlobalEnvelope(), Matchers.is(offering2Envelope));
    }

    @Test
    public void should_not_contain_result_template_to_feature_relations_after_DeleteSensor()
            throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        updateCacheWithInsertResultTemplate(RESULT_TEMPLATE_IDENTIFIER);
        insertResultPreparation();
        deleteSensorPreparation();

        Assert.assertTrue(getCache().getFeaturesOfInterestForResultTemplate(RESULT_TEMPLATE_IDENTIFIER).isEmpty());
    }

    @Test
    public void should_not_contain_result_template_to_observable_property_relation_after_DeleteSensor()
            throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        updateCacheWithInsertResultTemplate(RESULT_TEMPLATE_IDENTIFIER);
        insertResultPreparation();
        deleteSensorPreparation();

        Assert.assertTrue(getCache().getObservablePropertiesWithResultTemplate() == null
                || getCache().getObservablePropertiesWithResultTemplate().isEmpty());
    }

    /* Update after InsertResultTemplate */
    @Test
    public void should_contain_resulttemplate_identifier_after_InsertResultTemplate() throws OwsExceptionReport {
        insertResultTemplatePreparation();

        Assert.assertTrue("result template identifier NOT in cache",
                getCache().getResultTemplates().contains(RESULT_TEMPLATE_IDENTIFIER));
    }

    @Test
    public void should_contain_result_template_to_offering_relation_after_InsertResultTemplate()
            throws OwsExceptionReport {
        insertResultTemplatePreparation();

        Assert.assertTrue("offering -> result template relation NOT in cache",
                getCache().getResultTemplatesForOffering(OFFERING) != null
                        && getCache().getResultTemplatesForOffering(OFFERING).contains(RESULT_TEMPLATE_IDENTIFIER));
    }

    @Test
    public void should_update_global_temporal_BoundingBox_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();

        Assert.assertEquals(MAX_TIME, getCache().getMaxPhenomenonTime(),
                ((TimePeriod) observation.getPhenomenonTime()).getEnd());

        Assert.assertEquals(MIN_TIME, getCache().getMinPhenomenonTime(),
                ((TimePeriod) observation.getPhenomenonTime()).getStart());
    }

    @Test
    public void should_contain_procedure_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();
        checkProcedureNotInCache(PROCEDURE);
        checkNotOfferingProcedureRelation(getCache().getProceduresForOffering(OFFERING), PROCEDURE);
        checkNotProcedureOfferingRelation(getCache().getOfferingsForProcedure(PROCEDURE), OFFERING);

    }

    @Test
    public void should_contain_FeatureOfInterest_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();
        checkFeatureNotInCache(FEATURE);
        checkNotFeatureProcedureRelation(getCache().getProceduresForFeatureOfInterest(FEATURE), PROCEDURE);
        checkParentFeatureForFeature(FEATURE);
        checkChildFeatureForFeature(FEATURE);
        checkOfferingFeatureRelation(getCache().getFeaturesOfInterestForOffering(OFFERING), FEATURE);
    }

    @Test
    public void should_contain_envelopes_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();
        checkGlobalEnvelope(getReferencedEnvelopeFromObservation(observation));
        final String offering = OFFERING;
        checkOfferingEnvelope(offering, getReferencedEnvelopeFromObservation(observation));
        checkNoEnvelopForOffering(offering);
        checkEnvelopForOfferingDiffsForFeature(offering, getReferencedEnvelopeFromObservation(observation));
    }

    @Test
    public void should_contain_observation_timestamp_in_temporal_envelope_of_offering_after_InsertResult()
            throws OwsExceptionReport {
        insertResultPreparation();
        final DateTime end = ((TimePeriod) observation.getPhenomenonTime()).getEnd();
        final DateTime start = ((TimePeriod) observation.getPhenomenonTime()).getStart();
        final DateTime minTimeForOffering = getCache().getMinPhenomenonTimeForOffering(OFFERING);
        final DateTime maxTimeForOffering = getCache().getMaxPhenomenonTimeForOffering(OFFERING);

        Assert.assertNotNull("minTimeForOffering is null", minTimeForOffering);
        Assert.assertNotNull("maxTimeForOffering is null", maxTimeForOffering);
        Assert.assertTrue("temporal envelope of does NOT contain observation timestamp",
                (minTimeForOffering.isBefore(start) || minTimeForOffering.isEqual(start))
                        && (maxTimeForOffering.isAfter(end) || maxTimeForOffering.isEqual(end)));
    }

    @Test
    public void should_contain_observable_property_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();
        checkNotOfferingObservablePropertyRelation(getCache().getObservablePropertiesForOffering(OFFERING),
                OBSERVABLE_PROPERTY);
        checkNotObservablePropertyOfferinRelation(getCache().getOfferingsForObservableProperty(OBSERVABLE_PROPERTY),
                OFFERING);
        checkNotObservablePropertyProcedureRelation(getCache().getProceduresForObservableProperty(OBSERVABLE_PROPERTY),
                PROCEDURE);
        checkNotProcedureObservablePropertyRelation(getCache().getObservablePropertiesForProcedure(PROCEDURE),
                OBSERVABLE_PROPERTY);
    }

    @Test
    public void should_contain_offering_observation_type_relation_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();
        checkNotOfferingObservationTypeRelation(getCache().getObservationTypesForOffering(OFFERING),
                OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
    }

    @Test
    public void should_contain_result_template_to_observed_property_relation_after_InsertResult()
            throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        insertResultPreparation();
        final boolean CONTAINED = true;

       MatcherAssert.assertThat(getCache().getObservablePropertiesForResultTemplate(RESULT_TEMPLATE_IDENTIFIER)
                .contains(OBSERVABLE_PROPERTY), Matchers.is(CONTAINED));
    }

    @Test
    public void should_contain_result_template_to_feature_relation_after_InsertResult() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        insertResultPreparation();

        Assert.assertTrue(
                getCache().getFeaturesOfInterestForResultTemplate(RESULT_TEMPLATE_IDENTIFIER).contains(FEATURE));
    }

    /* HELPER */
    private void insertResultTemplatePreparation() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        updateCacheWithInsertResultTemplate(RESULT_TEMPLATE_IDENTIFIER);
    }

    private void updateCacheWithInsertResultTemplate(String resultTemplateIdentifier) throws OwsExceptionReport {
        insertResultTemplateResponse(resultTemplateIdentifier);
        insertResultTemplateRequest(OFFERING);
        controller.update(new ResultTemplateInsertionUpdate((InsertResultTemplateRequest) request,
                (InsertResultTemplateResponse) response));
    }

    private void insertResultTemplateRequest(String offeringForResultTemplate) {
        request = InsertResultTemplateRequestBuilder.anInsertResultTemplateRequest()
                .setOffering(offeringForResultTemplate).build();
    }

    private void insertResultPreparation() throws OwsExceptionReport {
        observation = ObservationBuilder.anObservation().setObservationConstellation(ObservationConstellationBuilder
                .anObservationConstellation()
                .setProcedure(
                        ProcedureDescriptionBuilder.aSensorMLProcedureDescription().setIdentifier(PROCEDURE).build())
                .addOffering(OFFERING)
                .setFeature(SamplingFeatureBuilder.aSamplingFeature().setIdentifier(FEATURE)
                        .setFeatureType(SfConstants.FT_SAMPLINGPOINT).setGeometry(11.0, 11.0, WGS84).build())
                .setObservableProperty(
                        ObservablePropertyBuilder.aObservableProperty().setIdentifier(OBSERVABLE_PROPERTY).build())
                .setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION).build())
                .setValue(SweDataArrayValueBuilder.aSweDataArrayValue()
                        .setSweDataArray(SweDataArrayBuilder.aSweDataArray()
                                .setElementType(DataRecordBuilder.aDataRecord()
                                        .addField(SweTimeBuilder.aSweTime().build()).build())
                                .setEncoding("text", "@", ";", ".").addBlock("2013-02-06T10:28:00", "2.5").build())
                        .build())
                .setIdentifier(CODESPACE, OBSERVATION_ID).build();

        controller.update(new ResultInsertionUpdate(RESULT_TEMPLATE_IDENTIFIER, Arrays.asList(observation)));
    }

    private void insertResultTemplateResponse(String resultTemplateIdentifier) {
        response = InsertResultTemplateResponseBuilder.anInsertResultTemplateResponse()
                .setTemplateIdentifier(resultTemplateIdentifier).build();
    }

    private String getProcedureIdentifier() {
        return ((DeleteSensorRequest) request).getProcedureIdentifier();
    }

    private void deleteSensorPreparation() throws OwsExceptionReport {
        insertObservationPreparation();
        updateCacheWithDeleteSensor();
    }

    private void updateCacheWithDeleteSensor() throws OwsExceptionReport {
        request = DeleteSensorRequestBuilder.aDeleteSensorRequest().setProcedure(PROCEDURE).build();
        controller.update(new SensorDeletionUpdate(new MockCacheFeederDAO(), (DeleteSensorRequest) request));
    }

    private void updateCacheWithSingleObservation(String procedure) throws OwsExceptionReport {
        insertObservationRequestExample(procedure);
        controller.update(new ObservationInsertionUpdate((InsertObservationRequest) request));
    }

    private void updateCacheWithSingleObservation(String procedure, long phenomenonTime) throws OwsExceptionReport {
        insertObservationRequestExample(procedure, phenomenonTime);
        controller.update(new ObservationInsertionUpdate((InsertObservationRequest) request));
    }

    private void updateCacheWithSingleObservation(String procedure, double xCoord, double yCoord, int epsgCode,
            String feature) throws OwsExceptionReport {
        insertObservationRequestExample(procedure, xCoord, yCoord, epsgCode, feature, System.currentTimeMillis());
        controller.update(new ObservationInsertionUpdate((InsertObservationRequest) request));
    }

    private void updateCacheWithInsertSensor(String procedureIdentifier) throws OwsExceptionReport {
        insertSensorRequestExample(procedureIdentifier);
        insertSensorResponseExample(procedureIdentifier);
        controller.update(
                new SensorInsertionUpdate((InsertSensorRequest) request, (InsertSensorResponse) response, converter));
    }

    private DateTime getPhenomenonTimeFromObservation() {
        return ((TimeInstant) ((InsertObservationRequest) request).getObservations().get(0).getPhenomenonTime())
                .getValue();
    }

    private String getAssignedProcedure() {
        return ((InsertSensorResponse) response).getAssignedProcedure();
    }

    private String getObservablePropertyFromInsertSensor() {
        return ((InsertSensorRequest) request).getObservableProperty().get(0);
    }

    private String getAssignedOfferingId() {
        return ((InsertSensorResponse) response).getAssignedOffering();
    }

    private void insertSensorResponseExample(String procedureIdentifier) {
        response = InsertSensorResponseBuilder.anInsertSensorResponse()
                .setOffering(procedureIdentifier + OFFERING_IDENTIFIER_EXTENSION).setProcedure(procedureIdentifier)
                .build();
    }

    private void insertSensorRequestExample(String procedureIdentifier) {
        request =
                InsertSensorRequestBuilder.anInsertSensorRequest()
                        .setProcedure(ProcedureDescriptionBuilder.aSensorMLProcedureDescription()
                                .setIdentifier(procedureIdentifier)
                                .setOffering(procedureIdentifier + OFFERING_IDENTIFIER_EXTENSION,
                                        procedureIdentifier + OFFERING_NAME_EXTENSION)
                                .build())
                        .addObservableProperty(OBSERVABLE_PROPERTY).addObservationType(OBSERVATION_TYPE)
                        .addObservationType(OBSERVATION_TYPE_2).addFeatureOfInterestType(FEATURE_OF_INTEREST_TYPE)
                        .addRelatedFeature(SamplingFeatureBuilder.aSamplingFeature().setIdentifier(FEATURE).build(),
                                RELATED_FEATURE_ROLE)
                        .addRelatedFeature(SamplingFeatureBuilder.aSamplingFeature().setIdentifier(FEATURE_2).build(),
                                RELATED_FEATURE_ROLE_2)
                        .build();
    }

    private String getSensorIdFromInsertObservation() {
        return ((InsertObservationRequest) request).getAssignedSensorId();
    }

    private String getObservationTypeFromFirstObservation() {
        return ((InsertObservationRequest) request).getObservations().get(0).getObservationConstellation()
                .getObservationType();
    }

    private String getFirstOffering() {
        return ((InsertObservationRequest) request).getOfferings().get(0);
    }

    private String getProcedure() {
        return ((InsertObservationRequest) request).getAssignedSensorId();
    }

    private ReferencedEnvelope getReferencedEnvelopeFromObservation(OmObservation sosObservation) {
        return new ReferencedEnvelope(
                ((SamplingFeature) sosObservation.getObservationConstellation().getFeatureOfInterest()).getGeometry()
                        .getEnvelopeInternal(),
                getCache().getDefaultEPSGCode());
    }

    private String getFoiIdFromInsertObservationRequest() {
        return ((InsertObservationRequest) request).getObservations().get(0).getObservationConstellation()
                .getFeatureOfInterest().getIdentifierCodeWithAuthority().getValue();
    }

    private void insertObservationRequestExample(String procedure) throws OwsExceptionReport {
        insertObservationRequestExample(procedure, System.currentTimeMillis());
    }

    private void insertObservationRequestExample(String procedure, long phenomenonTime) throws OwsExceptionReport {
        insertObservationRequestExample(procedure, 11.0, 22.0, WGS84, FEATURE, phenomenonTime);
    }

    private void insertObservationRequestExample(String procedure, double xCoord, double yCoord, int epsgCode,
            String feature, long phenomenonTime) throws OwsExceptionReport {
        request = InsertObservationRequestBuilder.aInsertObservationRequest().setProcedureId(procedure)
                .addOffering(procedure + OFFERING_IDENTIFIER_EXTENSION)
                .addObservation(ObservationBuilder.anObservation()
                        .setObservationConstellation(ObservationConstellationBuilder.anObservationConstellation()
                                .setFeature(SamplingFeatureBuilder.aSamplingFeature().setIdentifier(FEATURE)
                                        .setFeatureType(SfConstants.FT_SAMPLINGPOINT)
                                        .setGeometry(yCoord, xCoord, epsgCode).build())
                                .setProcedure(ProcedureDescriptionBuilder.aSensorMLProcedureDescription()
                                        .setIdentifier(procedure).build())
                                .setObservationType(OmConstants.OBS_TYPE_MEASUREMENT)
                                .setObservableProperty(ObservablePropertyBuilder.aObservableProperty()
                                        .setIdentifier(OBSERVABLE_PROPERTY).build())
                                .build())
                        .setValue(QuantityObservationValueBuilder.aQuantityValue()
                                .setValue(QuantityValueBuilder.aQuantitiy().setValue(2.0).setUnit("m").build())
                                .setPhenomenonTime(phenomenonTime).build())
                        .setIdentifier(CODESPACE, OBSERVATION_ID).build())
                .build();
    }

    private String getObservablePropertyFromInsertObservation() {
        return ((InsertObservationRequest) request).getObservations().get(0).getObservationConstellation()
                .getObservableProperty().getIdentifier();
    }

    private String getSensorIdFromInsertSensorRequest() {
        return ((InsertSensorRequest) request).getProcedureDescription().getIdentifier();
    }

    protected SosWritableContentCache getCache() {
        return (SosWritableContentCache) controller.getCache();
    }

    private void checkGlobalEnvelope(ReferencedEnvelope envelope) {
        Assert.assertEquals("global envelope", getCache().getGlobalEnvelope(),
                envelope);
    }

    private void checkProcedureNotInCache(String procedure) {
        Assert.assertTrue("procedure NOT in cache",
                getCache().getProcedures().contains(procedure));
    }

    private void checkFeatureNotInCache(String feature) {
        Assert.assertTrue("feature NOT in cache",
                getCache().getFeaturesOfInterest().contains(feature));
    }

    private void checkParentFeatureForFeature(String feature) {
        Assert.assertTrue("no parent features for feature",
                getCache().getParentFeatures(Collections.singleton(feature), true, false).isEmpty());
    }

    private void checkChildFeatureForFeature(String feature) {
        Assert.assertTrue("no child features for feature",
                getCache().getParentFeatures(Collections.singleton(feature), true, false).isEmpty());
    }


    private void checkOfferingFeatureRelation(Set<String> offeringFeatures, String feature) {
        Assert.assertTrue("offering -> feature relation", offeringFeatures.contains(feature));
    }

    private void checkNotOfferingProcedureRelation(Set<String> offeringProcedures, String procedure) {
        Assert.assertTrue("offering -> procedure relation not in cache", offeringProcedures.contains(procedure));
    }

    private void checkNotFeatureProcedureRelation(Set<String> featureProcedures, String procedure) {
        Assert.assertTrue("feature -> procedure relation NOT in cache", featureProcedures.contains(procedure));
    }

    private void checkNotObservablePropertyProcedureRelation(Set<String> observablePropertyProcedures,
            String procedure) {
        Assert.assertTrue("observable-property -> procedure relation NOT in cache",
                observablePropertyProcedures.contains(procedure));
    }

    private void checkNotProcedureObservablePropertyRelation(Set<String> procedureObservableProperty,
            String observableProperty) {
        Assert.assertTrue("procedure -> observable-property relation NOT in cache",
                procedureObservableProperty.contains(observableProperty));
    }

    private void checkNotProcedureOfferingRelation(Set<String> procedureOffering, String offering) {
        Assert.assertTrue("procedure -> offering relation NOT in cache", procedureOffering.contains(offering));
    }

    private void checkNotOfferingObservablePropertyRelation(Set<String> offeringObservableProperty,
            String observableProperty) {
        Assert.assertTrue("offering -> observable property NOT in cache",
                offeringObservableProperty.contains(observableProperty));
    }

    private void checkNotObservablePropertyOfferinRelation(Set<String> observablePropertyOfferings,
            String offering) {
        Assert.assertTrue("observable property -> offering NOT in cache",
                observablePropertyOfferings.contains(offering));
    }

    private void checkNotOfferingObservationTypeRelation(Set<String> offeringObservationTypes,
            String observationType) {
        Assert.assertTrue("offering -> observation type relation NOT in cache",
                offeringObservationTypes.contains(observationType));
    }

    private void checkOfferingEnvelope(String offering, ReferencedEnvelope envelope) {
        Assert.assertEquals("offering envelop", getCache().getEnvelopeForOffering(offering),
                envelope);
    }

    private void checkNoEnvelopForOffering(String offering) {
        Assert.assertTrue("spatial bounding box of offering NOT contained in cache",
                getCache().getEnvelopeForOffering(offering).isSetEnvelope());
    }

    private void checkEnvelopForOfferingDiffsForFeature(String offering,  ReferencedEnvelope envelope) {
        Assert.assertEquals("spatial bounding box of offering NOT same as feature envelope",
                getCache().getEnvelopeForOffering(offering),
                envelope);
    }

}
