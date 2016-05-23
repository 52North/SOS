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
package org.n52.sos.cache;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.n52.sos.ogc.om.OmConstants.OBS_TYPE_MEASUREMENT;
import static org.n52.sos.ogc.om.OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION;
import static org.n52.sos.ogc.om.features.SfConstants.FT_SAMPLINGPOINT;
import static org.n52.sos.util.builder.DataRecordBuilder.aDataRecord;
import static org.n52.sos.util.builder.InsertObservationRequestBuilder.aInsertObservationRequest;
import static org.n52.sos.util.builder.InsertResultTemplateRequestBuilder.anInsertResultTemplateRequest;
import static org.n52.sos.util.builder.InsertResultTemplateResponseBuilder.anInsertResultTemplateResponse;
import static org.n52.sos.util.builder.InsertSensorRequestBuilder.anInsertSensorRequest;
import static org.n52.sos.util.builder.InsertSensorResponseBuilder.anInsertSensorResponse;
import static org.n52.sos.util.builder.ObservablePropertyBuilder.aObservableProperty;
import static org.n52.sos.util.builder.ObservationBuilder.anObservation;
import static org.n52.sos.util.builder.ObservationConstellationBuilder.anObservationConstellation;
import static org.n52.sos.util.builder.ProcedureDescriptionBuilder.aSensorMLProcedureDescription;
import static org.n52.sos.util.builder.QuantityObservationValueBuilder.aQuantityValue;
import static org.n52.sos.util.builder.QuantityValueBuilder.aQuantitiy;
import static org.n52.sos.util.builder.SamplingFeatureBuilder.aSamplingFeature;
import static org.n52.sos.util.builder.SweDataArrayBuilder.aSweDataArray;
import static org.n52.sos.util.builder.SweDataArrayValueBuilder.aSweDataArrayValue;
import static org.n52.sos.util.builder.SweTimeBuilder.aSweTime;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sos.cache.ctrl.ContentCacheControllerImpl;
import org.n52.sos.cache.ctrl.action.ObservationInsertionUpdate;
import org.n52.sos.cache.ctrl.action.ResultInsertionUpdate;
import org.n52.sos.cache.ctrl.action.ResultTemplateInsertionUpdate;
import org.n52.sos.cache.ctrl.action.SensorDeletionUpdate;
import org.n52.sos.cache.ctrl.action.SensorInsertionUpdate;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.swes.SwesFeatureRelationship;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DeleteSensorRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertResultTemplateRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.InsertResultTemplateResponse;
import org.n52.sos.response.InsertSensorResponse;
import org.n52.sos.util.Constants;
import org.n52.sos.util.builder.DeleteSensorRequestBuilder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a> Test after DeleteObservation => not possible with
 *         InMemory because of bounding box issues, for example.
 * 
 * @since 4.0.0
 */
public class InMemoryCacheControllerTest extends AbstractCacheControllerTest {
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

    private AbstractServiceRequest request;

    private ContentCacheControllerImpl controller;

    private AbstractServiceResponse response;

    private OmObservation observation;

    @Before
    public void initController() {
        controller = new TestableInMemoryCacheController();
    }

    @After
    public void setAllFixturesToNullAfterEachTest() {
        request = null;
        controller = null;
        observation = null;
        response = null;
    }

    @Test
    public void should_update_global_temporal_BoundingBox_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);
        final DateTime phenomenonTime =
                ((TimeInstant) ((InsertObservationRequest) request).getObservations().get(0).getPhenomenonTime())
                        .getValue();

        assertEquals("maxtime", getCache().getMaxPhenomenonTime(), phenomenonTime);

        assertEquals("mintime", getCache().getMinPhenomenonTime(), phenomenonTime);
    }

    @Test
    public void should_contain_procedure_after_InsertObservation() throws OwsExceptionReport {
        insertObservationPreparation();

        assertTrue("procedure NOT in cache", getCache().getProcedures().contains(getSensorIdFromInsertObservation()));

        assertTrue("offering -> procedure relation not in cache",
                getCache().getProceduresForOffering(getFirstOffering()).contains(getSensorIdFromInsertObservation()));

        assertTrue(
                "observable-property -> procedure relation NOT in cache",
                getCache().getProceduresForObservableProperty(getObservablePropertyFromInsertObservation()).contains(
                        getSensorIdFromInsertObservation()));

        assertTrue(
                "procedure -> observable-property relation NOT in cache",
                getCache().getObservablePropertiesForProcedure(getSensorIdFromInsertObservation()).contains(
                        getObservablePropertyFromInsertObservation()));

        assertTrue("procedure -> offering relation NOT in cache",
                getCache().getOfferingsForProcedure(getSensorIdFromInsertObservation()).contains(getFirstOffering()));

    }

    private void insertObservationPreparation() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        updateCacheWithSingleObservation(PROCEDURE);
    }

    @Test
    public void should_contain_FeatureOfInterest_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        assertTrue("feature NOT in cache",
                getCache().getFeaturesOfInterest().contains(getFoiIdFromInsertObservationRequest()));

        assertTrue(
                "feature -> procedure relation NOT in cache",
                getCache().getProceduresForFeatureOfInterest(getFoiIdFromInsertObservationRequest()).contains(
                        getSensorIdFromInsertObservation()));

        assertTrue(
                "no parent features for feature",
                getCache().getParentFeatures(Collections.singleton(getFoiIdFromInsertObservationRequest()), true,
                        false).isEmpty());

        assertTrue(
                "no child features for feature",
                getCache().getParentFeatures(Collections.singleton(getFoiIdFromInsertObservationRequest()), true,
                        false).isEmpty());

        assertTrue("offering -> feature relation", getCache().getFeaturesOfInterestForOffering(getFirstOffering())
                .contains(getFoiIdFromInsertObservationRequest()));

    }

    @Test
    public void should_contain_envelopes_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        assertEquals("global envelope", getCache().getGlobalEnvelope(),
                getSosEnvelopeFromObservation(((InsertObservationRequest) request).getObservations().get(0)));

        assertEquals("offering envelop", getCache().getEnvelopeForOffering(getFirstOffering()),
                getSosEnvelopeFromObservation(((InsertObservationRequest) request).getObservations().get(0)));

        assertTrue("spatial bounding box of offering NOT contained in cache",
                getCache().getEnvelopeForOffering(getFirstOffering()).isSetEnvelope());

        assertEquals("spatial bounding box of offering NOT same as feature envelope", getCache()
                .getEnvelopeForOffering(getFirstOffering()),
                getSosEnvelopeFromObservation(((InsertObservationRequest) request).getObservations().get(0)));
    }

    @Test
    public void should_contain_observation_timestamp_in_temporal_envelope_of_offering_after_InsertObservation()
            throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        assertTrue(
                "temporal envelope of offering does NOT contain observation timestamp",
                (getCache().getMinPhenomenonTimeForOffering(getFirstOffering()).isBefore(
                        getPhenomenonTimeFromObservation()) || getCache().getMinPhenomenonTimeForOffering(
                        getFirstOffering()).isEqual(getPhenomenonTimeFromObservation()))
                        && (getCache().getMaxPhenomenonTimeForOffering(getFirstOffering()).isAfter(
                                getPhenomenonTimeFromObservation()) || getCache().getMaxPhenomenonTimeForOffering(
                                getFirstOffering()).isEqual(getPhenomenonTimeFromObservation())));
    }

    @Test
    public void should_contain_observation_timestamp_in_temporal_envelope_of_procedure_after_InsertObservation()
            throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        assertTrue(
                "temporal envelope of procedure does NOT contain observation timestamp",
                (getCache().getMinPhenomenonTimeForProcedure(getProcedure()).isBefore(
                        getPhenomenonTimeFromObservation()) || getCache().getMinPhenomenonTimeForProcedure(
                        getProcedure()).isEqual(getPhenomenonTimeFromObservation()))
                        && (getCache().getMaxPhenomenonTimeForProcedure(getProcedure()).isAfter(
                                getPhenomenonTimeFromObservation()) || getCache().getMaxPhenomenonTimeForProcedure(
                                getProcedure()).isEqual(getPhenomenonTimeFromObservation())));
    }

    @Test
    public void should_contain_observable_property_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        assertTrue(
                "offering -> observable property NOT in cache",
                getCache().getObservablePropertiesForOffering(getFirstOffering()).contains(
                        getObservablePropertyFromInsertObservation()));

        assertTrue(
                "observable property -> offering NOT in cache",
                getCache().getOfferingsForObservableProperty(getObservablePropertyFromInsertObservation()).contains(
                        getFirstOffering()));
    }

    @Test
    public void should_contain_offering_observation_type_relation_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithSingleObservation(PROCEDURE);

        assertTrue(
                "offering -> observation type relation NOT in cache",
                getCache().getObservationTypesForOffering(getFirstOffering()).contains(
                        getObservationTypeFromFirstObservation()));
    }

    @Test
    public void should_contain_procedure_after_InsertSensor() throws OwsExceptionReport {

        updateCacheWithInsertSensor(PROCEDURE);

        assertTrue("procedure NOT in cache", getCache().getProcedures().contains(getSensorIdFromInsertSensorRequest()));
    }

    @Test
    public void should_contain_procedure_offering_relations_after_InsertSensor() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);

        assertTrue(
                "offering -> procedure relation NOT in cache",
                getCache().getProceduresForOffering(getAssignedOfferingId()).contains(
                        getSensorIdFromInsertSensorRequest()));

        assertTrue(
                "procedure -> offering relation NOT in cache",
                getCache().getOfferingsForProcedure(getSensorIdFromInsertSensorRequest()).contains(
                        getAssignedOfferingId()));
    }

    @Test
    public void should_contain_observable_property_relations_after_InsertSensor() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);

        assertTrue(
                "observable property -> procedure relation NOT in cache",
                getCache().getProceduresForObservableProperty(getObservablePropertyFromInsertSensor()).contains(
                        getAssignedProcedure()));

        assertTrue(
                "procedure -> observable property relation NOT in cache",
                getCache().getObservablePropertiesForProcedure(getAssignedProcedure()).contains(
                        getObservablePropertyFromInsertSensor()));

        assertTrue(
                "observable property -> offering relation NOT in cache",
                getCache().getOfferingsForObservableProperty(getObservablePropertyFromInsertSensor()).contains(
                        getAssignedOfferingId()));

        assertTrue(
                "offering -> observable property relation NOT in cache",
                getCache().getObservablePropertiesForOffering(getAssignedOfferingId()).contains(
                        getObservablePropertyFromInsertSensor()));

    }

    @Test
    public void should_contain_offering_name_after_InsertSensor() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);

        assertTrue("offering NOT in cache", getCache().getOfferings().contains(getAssignedOfferingId()));
    }

    @Test
    public void should_contain_allowed_observation_types_after_InsertSensor() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);

        for (String observationType : ((InsertSensorRequest) request).getMetadata().getObservationTypes()) {
            assertTrue("observation type NOT in cache",
                    getCache().getAllowedObservationTypesForOffering(getAssignedOfferingId())
                            .contains(observationType));
        }
    }

    @Test
    public void should_contain_related_features_after_InsertObservation() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);

        assertFalse("offering -> related feature relations NOT in cache",
                getCache().getRelatedFeaturesForOffering(getAssignedOfferingId()).isEmpty());

        for (SwesFeatureRelationship relatedFeature : ((InsertSensorRequest) request).getRelatedFeatures()) {
            assertTrue(
                    "single \"offering -> related features relation\" NOT in cache",
                    getCache().getRelatedFeaturesForOffering(getAssignedOfferingId()).contains(
                            relatedFeature.getFeature().getIdentifierCodeWithAuthority().getValue()));

            assertTrue("single \"related feature -> role relation\" NOT in cache",
                    getCache().getRolesForRelatedFeature(relatedFeature.getFeature().getIdentifierCodeWithAuthority().getValue())
                            .contains(relatedFeature.getRole()));
        }
    }

    @Test
    public void should_not_contain_procedure_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertFalse("procedure STILL in cache", getCache().getProcedures().contains(getProcedureIdentifier()));

    }

    @Test
    public void should_not_contain_procedure_offering_relations_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertFalse("offering -> procedure relation STILL in cache",
                getCache().getProceduresForOffering(getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION)
                        .contains(getProcedureIdentifier()));

        assertFalse(
                "procedure -> offering relation STILL in cache",
                getCache().getOfferingsForProcedure(getProcedureIdentifier()).contains(
                        getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION));
    }

    @Test
    public void should_not_contain_observable_properties_relations_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue(
                "observable property -> procedure relation STILL in cache",
                getCache().getProceduresForObservableProperty(OBSERVABLE_PROPERTY) == null
                        || !getCache().getProceduresForObservableProperty(OBSERVABLE_PROPERTY).contains(
                                getProcedureIdentifier()));

        assertTrue(
                "procedure -> observable property relation STILL in cache",
                getCache().getObservablePropertiesForProcedure(getProcedureIdentifier()) == null
                        || !getCache().getObservablePropertiesForProcedure(getProcedureIdentifier()).contains(
                                OBSERVABLE_PROPERTY));

        assertTrue(
                "observable property -> offering relation STILL in cache",
                getCache().getOfferingsForObservableProperty(OBSERVABLE_PROPERTY) == null
                        || !getCache().getOfferingsForObservableProperty(OBSERVABLE_PROPERTY).contains(
                                getProcedureIdentifier()));

        assertTrue(
                "offering -> observable property relation STILL in cache",
                getCache().getObservablePropertiesForOffering(getProcedureIdentifier()) == null
                        || !getCache().getObservablePropertiesForOffering(getProcedureIdentifier()).contains(
                                OBSERVABLE_PROPERTY));
    }

    @Test
    public void should_not_contain_parent_procedures_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue(
                "parent procedures STILL available in cache",
                getCache().getParentProcedures(PROCEDURE, true, false) == null
                        || getCache().getParentProcedures(PROCEDURE, true, false).isEmpty());
    }

    @Test
    public void should_not_contain_child_procedures_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue(
                "parent procedures STILL available in cache",
                getCache().getChildProcedures(PROCEDURE, true, false) == null
                        || getCache().getChildProcedures(PROCEDURE, true, false).isEmpty());
    }

    @Test
    public void should_not_contain_an_envlope_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue("envolpe for offering STILL in cache",
                getCache().getEnvelopeForOffering(getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION) == null);
    }

    @Test
    public void should_not_contain_global_envelope_if_deleted_sensor_was_last_one_available()
            throws OwsExceptionReport {
        deleteSensorPreparation();

        assertFalse("global envelope STILL in cache after deletion of last sensor",
                getCache().getGlobalEnvelope() != null && getCache().getGlobalEnvelope().isSetEnvelope());
    }

    @Test
    public void should_not_contain_temporal_bounding_box_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue(
                "temporal bounding box STILL in cache",
                getCache().getMaxPhenomenonTimeForOffering(getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION) == null
                        && getCache().getMinPhenomenonTimeForOffering(
                                getProcedureIdentifier() + OFFERING_IDENTIFIER_EXTENSION) == null);
    }

    @Test
    public void should_not_contain_global_temporal_bouding_box_if_deleted_sensor_was_last_one_available()
            throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE_2);
        deleteSensorPreparation();

        assertTrue("global temporal bounding box still in cache after deletion of last sensor", getCache()
                .getMaxPhenomenonTime() == null && getCache().getMinPhenomenonTime() == null);
    }

    @Test
    public void should_not_contain_related_features_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue("related features for offering STILL in cache", getCache().getRelatedFeaturesForOffering(OFFERING)
                .isEmpty());
    }

    @Test
    public void should_not_contain_roles_for_deleted_related_features_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue("roles for deleted related features are STILL in cache", onlyValidRelatedFeaturesAreInRoleMap());
    }

    @Test
    public void should_not_contain_offering_names_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertNull("offering name STILL in cache", getCache().getNameForOffering(OFFERING));
    }

    @Test
    public void should_not_contain_composite_phenomenons_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue("composite phenomenons STILL in cache for deleted sensor", getCache()
                .getCompositePhenomenonsForOffering(OFFERING) == null
                || getCache().getCompositePhenomenonsForOffering(OFFERING).isEmpty());
    }

    @Test
    public void should_not_contain_features_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue("features STILL related to offering", getCache().getFeaturesOfInterestForOffering(OFFERING)
                .isEmpty());

        assertFalse("features STILL in cache", getCache().getFeaturesOfInterest().contains(FEATURE));
    }

    @Test
    public void should_not_contain_observation_types_for_the_offerings_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue("observation types for offering STILL in cache", getCache()
                .getObservationTypesForOffering(OFFERING).isEmpty());
    }

    @Test
    public void should_not_contain_feature_to_procedure_relations_after_DeleteSensor() throws OwsExceptionReport {
        deleteSensorPreparation();

        assertFalse("feature -> procedure relation STILL in cache",
                getCache().getProceduresForFeatureOfInterest(FEATURE).contains(PROCEDURE));
    }

    @Test
    public void should_not_contain_offering_observable_property_relations_after_DeleteSensor()
            throws OwsExceptionReport {
        deleteSensorPreparation();

        assertTrue("offering to observable property relation STILL in cache", getCache()
                .getObservablePropertiesForOffering(OFFERING).isEmpty());

        assertTrue("observable property to offering relation STILL in cache", getCache()
                .getOfferingsForObservableProperty(OFFERING).isEmpty());
    }

    @Test
    public void should_not_contain_related_result_templates_after_DeleteSenosr() throws OwsExceptionReport {
        updateCacheWithInsertResultTemplate(RESULT_TEMPLATE_IDENTIFIER);
        deleteSensorPreparation();

        assertTrue("offering -> result templates relations STILL in cache",
                getCache().getResultTemplatesForOffering(OFFERING) == null
                        || getCache().getResultTemplatesForOffering(OFFERING).isEmpty());

        assertFalse("result template identifier STILL in cache",
                getCache().getResultTemplates().contains(RESULT_TEMPLATE_IDENTIFIER));
    }

    @Test
    public void should_reset_global_temporal_bounding_box_after_DeleteSensor_of_not_last_sensor()
            throws OwsExceptionReport {

        long phenomenonTime = 0l;

        updateCacheWithInsertSensor(PROCEDURE_2);
        updateCacheWithSingleObservation(PROCEDURE_2, phenomenonTime);

        assertThat(getCache().getMaxPhenomenonTime(), is(notNullValue()));
        assertThat(getCache().getMinPhenomenonTime(), is(notNullValue()));

        deleteSensorPreparation();

        assertThat(getCache().getMaxPhenomenonTime(), is(notNullValue()));
        assertThat(getCache().getMinPhenomenonTime(), is(notNullValue()));
    }

    @Test
    public void should_reset_global_spatial_bounding_box_after_DeleteSensor_of_not_last_sensor()
            throws OwsExceptionReport {
        double xCoord = -55.0;
        double yCoord = -66.0;
        int epsgCode = Constants.EPSG_WGS84;
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), epsgCode);
        Geometry geom = geometryFactory.createPoint(new Coordinate(xCoord, yCoord));
        SosEnvelope offering2Envelope = new SosEnvelope(geom.getEnvelopeInternal(), epsgCode);

        updateCacheWithInsertSensor(PROCEDURE_2);
        updateCacheWithSingleObservation(PROCEDURE_2, xCoord, yCoord, epsgCode, FEATURE_2);

        deleteSensorPreparation();

        assertThat(getCache().getGlobalEnvelope(), is(offering2Envelope));
    }

    @Test
    public void should_not_contain_result_template_to_feature_relations_after_DeleteSensor() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        updateCacheWithInsertResultTemplate(RESULT_TEMPLATE_IDENTIFIER);
        insertResultPreparation();
        deleteSensorPreparation();

        assertTrue(getCache().getFeaturesOfInterestForResultTemplate(RESULT_TEMPLATE_IDENTIFIER).isEmpty());
    }

    @Test
    public void should_not_contain_result_template_to_observable_property_relation_after_DeleteSensor()
            throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        updateCacheWithInsertResultTemplate(RESULT_TEMPLATE_IDENTIFIER);
        insertResultPreparation();
        deleteSensorPreparation();

        assertTrue(getCache().getObservablePropertiesWithResultTemplate() == null
                || getCache().getObservablePropertiesWithResultTemplate().isEmpty());
    }

    /* Update after InsertResultTemplate */
    @Test
    public void should_contain_resulttemplate_identifier_after_InsertResultTemplate() throws OwsExceptionReport {
        insertResultTemplatePreparation();

        assertTrue("result template identifier NOT in cache",
                getCache().getResultTemplates().contains(RESULT_TEMPLATE_IDENTIFIER));
    }

    @Test
    public void should_contain_result_template_to_offering_relation_after_InsertResultTemplate()
            throws OwsExceptionReport {
        insertResultTemplatePreparation();

        assertTrue("offering -> result template relation NOT in cache",
                getCache().getResultTemplatesForOffering(OFFERING) != null
                        && getCache().getResultTemplatesForOffering(OFFERING).contains(RESULT_TEMPLATE_IDENTIFIER));
    }

    @Test
    public void should_update_global_temporal_BoundingBox_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();

        assertEquals("maxtime", getCache().getMaxPhenomenonTime(),
                ((TimePeriod) observation.getPhenomenonTime()).getEnd());

        assertEquals("mintime", getCache().getMinPhenomenonTime(),
                ((TimePeriod) observation.getPhenomenonTime()).getStart());
    }

    @Test
    public void should_contain_procedure_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();

        assertTrue("procedure NOT in cache", getCache().getProcedures().contains(PROCEDURE));

        assertTrue("offering -> procedure relation not in cache", getCache().getProceduresForOffering(OFFERING)
                .contains(PROCEDURE));

        assertTrue("procedure -> offering relation NOT in cache", getCache().getOfferingsForProcedure(PROCEDURE)
                .contains(OFFERING));

    }

    @Test
    public void should_contain_FeatureOfInterest_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();

        assertTrue("feature NOT in cache", getCache().getFeaturesOfInterest().contains(FEATURE));

        assertTrue("feature -> procedure relation NOT in cache", getCache().getProceduresForFeatureOfInterest(FEATURE)
                .contains(PROCEDURE));

        assertTrue("no parent features for feature",
                getCache().getParentFeatures(Collections.singleton(FEATURE), true, false).isEmpty());

        assertTrue("no child features for feature",
                getCache().getParentFeatures(Collections.singleton(FEATURE), true, false).isEmpty());

        assertTrue("offering -> feature relation",
                getCache().getFeaturesOfInterestForOffering(OFFERING).contains(FEATURE));

    }

    @Test
    public void should_contain_envelopes_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();

        assertEquals("global envelope", getCache().getGlobalEnvelope(), getSosEnvelopeFromObservation(observation));
        final String offering = OFFERING;

        assertEquals("offering envelop", getCache().getEnvelopeForOffering(offering),
                getSosEnvelopeFromObservation(observation));

        assertTrue("spatial bounding box of offering NOT contained in cache",
                getCache().getEnvelopeForOffering(offering).isSetEnvelope());

        assertEquals("spatial bounding box of offering NOT same as feature envelope", getCache()
                .getEnvelopeForOffering(offering), getSosEnvelopeFromObservation(observation));
    }

    @Test
    public void should_contain_observation_timestamp_in_temporal_envelope_of_offering_after_InsertResult()
            throws OwsExceptionReport {
        insertResultPreparation();
        final DateTime end = ((TimePeriod) observation.getPhenomenonTime()).getEnd();
        final DateTime start = ((TimePeriod) observation.getPhenomenonTime()).getStart();
        final DateTime minTimeForOffering = getCache().getMinPhenomenonTimeForOffering(OFFERING);
        final DateTime maxTimeForOffering = getCache().getMaxPhenomenonTimeForOffering(OFFERING);

        assertNotNull("minTimeForOffering is null", minTimeForOffering);
        assertNotNull("maxTimeForOffering is null", maxTimeForOffering);
        assertTrue(
                "temporal envelope of does NOT contain observation timestamp",
                (minTimeForOffering.isBefore(start) || minTimeForOffering.isEqual(start))
                        && (maxTimeForOffering.isAfter(end) || maxTimeForOffering.isEqual(end)));
    }

    @Test
    public void should_contain_observable_property_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();

        assertTrue("offering -> observable property NOT in cache",
                getCache().getObservablePropertiesForOffering(OFFERING).contains(OBSERVABLE_PROPERTY));

        assertTrue("observable property -> offering NOT in cache",
                getCache().getOfferingsForObservableProperty(OBSERVABLE_PROPERTY).contains(OFFERING));

        assertTrue("observable-property -> procedure relation NOT in cache", getCache()
                .getProceduresForObservableProperty(OBSERVABLE_PROPERTY).contains(PROCEDURE));

        assertTrue("procedure -> observable-property relation NOT in cache", getCache()
                .getObservablePropertiesForProcedure(PROCEDURE).contains(OBSERVABLE_PROPERTY));
    }

    @Test
    public void should_contain_offering_observation_type_relation_after_InsertResult() throws OwsExceptionReport {
        insertResultPreparation();

        assertTrue("offering -> observation type relation NOT in cache",
                getCache().getObservationTypesForOffering(OFFERING).contains(OBS_TYPE_SWE_ARRAY_OBSERVATION));
    }

    @Test
    public void should_contain_result_template_to_observed_property_relation_after_InsertResult()
            throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        insertResultPreparation();
        final boolean CONTAINED = true;

        assertThat(
                getCache().getObservablePropertiesForResultTemplate(RESULT_TEMPLATE_IDENTIFIER).contains(
                        OBSERVABLE_PROPERTY), is(CONTAINED));
    }

    @Test
    public void should_contain_result_template_to_feature_relation_after_InsertResult() throws OwsExceptionReport {
        updateCacheWithInsertSensor(PROCEDURE);
        insertResultPreparation();

        assertTrue(getCache().getFeaturesOfInterestForResultTemplate(RESULT_TEMPLATE_IDENTIFIER).contains(FEATURE));
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
        request = anInsertResultTemplateRequest().setOffering(offeringForResultTemplate).build();
    }

    private void insertResultPreparation() throws OwsExceptionReport {
        observation =
                anObservation()
                        .setObservationConstellation(
                                anObservationConstellation()
                                        .setProcedure(aSensorMLProcedureDescription().setIdentifier(PROCEDURE).build())
                                        .addOffering(OFFERING)
                                        .setFeature(
                                                aSamplingFeature().setIdentifier(FEATURE)
                                                        .setFeatureType(FT_SAMPLINGPOINT)
                                                        .setGeometry(11.0, 11.0, Constants.EPSG_WGS84).build())
                                        .setObservableProperty(
                                                aObservableProperty().setIdentifier(OBSERVABLE_PROPERTY).build())
                                        .setObservationType(OBS_TYPE_SWE_ARRAY_OBSERVATION).build())
                        .setValue(
                                aSweDataArrayValue().setSweDataArray(
                                        aSweDataArray()
                                                .setElementType(aDataRecord().addField(aSweTime().build()).build())
                                                .setEncoding("text", "@", ";", ".")
                                                .addBlock("2013-02-06T10:28:00", "2.5").build()).build())
                        .setIdentifier(CODESPACE, OBSERVATION_ID).build();

        controller.update(new ResultInsertionUpdate(RESULT_TEMPLATE_IDENTIFIER, observation));
    }

    private void insertResultTemplateResponse(String resultTemplateIdentifier) {
        response = anInsertResultTemplateResponse().setTemplateIdentifier(resultTemplateIdentifier).build();
    }

    private boolean onlyValidRelatedFeaturesAreInRoleMap() {
        Set<String> allowedRelatedFeatures = getCache().getRelatedFeatures();
        for (String relatedFeatureWithRole : ((WritableCache) getCache()).getRolesForRelatedFeaturesMap().keySet()) {
            if (!allowedRelatedFeatures.contains(relatedFeatureWithRole)) {
                return false;
            }
        }
        return true;
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
        controller.update(new SensorDeletionUpdate(((DeleteSensorRequest) request)));
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
        controller.update(new SensorInsertionUpdate((InsertSensorRequest) request, (InsertSensorResponse) response));
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
        response =
                anInsertSensorResponse().setOffering(procedureIdentifier + OFFERING_IDENTIFIER_EXTENSION)
                        .setProcedure(procedureIdentifier).build();
    }

    private void insertSensorRequestExample(String procedureIdentifier) {
        request =
                anInsertSensorRequest()
                        .setProcedure(
                                aSensorMLProcedureDescription()
                                        .setIdentifier(procedureIdentifier)
                                        .setOffering(procedureIdentifier + OFFERING_IDENTIFIER_EXTENSION,
                                                procedureIdentifier + OFFERING_NAME_EXTENSION).build())
                        .addObservableProperty(OBSERVABLE_PROPERTY)
                        .addObservationType(OBSERVATION_TYPE)
                        .addObservationType(OBSERVATION_TYPE_2)
                        .addFeatureOfInterestType(FEATURE_OF_INTEREST_TYPE)
                        .addRelatedFeature(aSamplingFeature().setIdentifier(FEATURE).build(), RELATED_FEATURE_ROLE)
                        .addRelatedFeature(aSamplingFeature().setIdentifier(FEATURE_2).build(), RELATED_FEATURE_ROLE_2)
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

    private SosEnvelope getSosEnvelopeFromObservation(OmObservation sosObservation) {
        return new SosEnvelope(((SamplingFeature) sosObservation.getObservationConstellation().getFeatureOfInterest())
                .getGeometry().getEnvelopeInternal(), getCache().getDefaultEPSGCode());
    }

    private String getFoiIdFromInsertObservationRequest() {
        return ((InsertObservationRequest) request).getObservations().get(0).getObservationConstellation()
                .getFeatureOfInterest().getIdentifierCodeWithAuthority().getValue();
    }

    private void insertObservationRequestExample(String procedure) {
        insertObservationRequestExample(procedure, System.currentTimeMillis());
    }

    private void insertObservationRequestExample(String procedure, long phenomenonTime) {
        insertObservationRequestExample(procedure, 11.0, 22.0, Constants.EPSG_WGS84, FEATURE, phenomenonTime);
    }

    private void insertObservationRequestExample(String procedure, double xCoord, double yCoord, int epsgCode,
            String feature, long phenomenonTime) {
        request =
                aInsertObservationRequest()
                        .setProcedureId(procedure)
                        .addOffering(procedure + OFFERING_IDENTIFIER_EXTENSION)
                        .addObservation(
                                anObservation()
                                        .setObservationConstellation(
                                                anObservationConstellation()
                                                        .setFeature(
                                                                aSamplingFeature().setIdentifier(FEATURE)
                                                                        .setFeatureType(FT_SAMPLINGPOINT)
                                                                        .setGeometry(yCoord, xCoord, epsgCode).build())
                                                        .setProcedure(
                                                                aSensorMLProcedureDescription().setIdentifier(
                                                                        procedure).build())
                                                        .setObservationType(OBS_TYPE_MEASUREMENT)
                                                        .setObservableProperty(
                                                                aObservableProperty().setIdentifier(
                                                                        OBSERVABLE_PROPERTY).build()).build())
                                        .setValue(
                                                aQuantityValue()
                                                        .setValue(
                                                                aQuantitiy().setValue(2.0)
                                                                        .setUnit("m").build())
                                                        .setPhenomenonTime(phenomenonTime).build())
                                        .setIdentifier(CODESPACE, OBSERVATION_ID).build()).build();
    }

    private String getObservablePropertyFromInsertObservation() {
        return ((InsertObservationRequest) request).getObservations().get(0).getObservationConstellation()
                .getObservableProperty().getIdentifier();
    }

    private String getSensorIdFromInsertSensorRequest() {
        return ((InsertSensorRequest) request).getProcedureDescription().getIdentifier();
    }

    protected ContentCache getCache() {
        return controller.getCache();
    }

}
