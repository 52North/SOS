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
package org.n52.sos.ds.hibernate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.convert.ConverterException;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.ProfileLevel;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProfileObservationInsertDAOTest extends AbstractInsertDAOTest {

    private static final Double PROFILE_VAL1 = 2.5;

    private static final Double PROFILE_VAL2 = 7.5;

    private static final Double PROFILE_VAL3 = 12.5;

    private static final Double PROFILE_VAL4 = 17.5;

    private static final Double VERTICAL_0 = 0.0;

    private static final Double VERTICAL_5 = 5.0;

    private static final Double VERTICAL_10 = 10.0;

    private static final Double VERTICAL_15 = 15.0;

    private static final Double VERTICAL_20 = 20.0;

    private static final String DEPTH = "depth";

    private static final String FROM = "from";

    private static final String TO = "to";

    private static final String VERTICAL_UNIT = "m";

    @Before
    public void setUp() throws OwsExceptionReport, ConverterException, EncodingException {
        super.setUp();
        Session session = null;
        try {
            session = getSession();
            HibernateMetadataCache.init(session);
            insertSensor(PROCEDURE3, OFFERING3, OBSPROP3, null, OmConstants.OBS_TYPE_PROFILE_OBSERVATION);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testInsertProfileIntervalObservation()
            throws OwsExceptionReport, InterruptedException, ConverterException {
        insertProfileIntervalObservationData();
        assertInsertionAftermathBeforeAndAfterCacheReload();
        checkObservation(true);
    }

    @Test
    public void testGeneratedProfileIntervalGetResultTemplate() throws OwsExceptionReport, ConverterException {
        insertProfileIntervalObservationData();
        GetResultTemplateRequest request = new GetResultTemplateRequest();
        request.setObservedProperty(OBSPROP3);
        request.setOffering(OFFERING3);
        GetResultTemplateResponse response = getResultTemplateHandler.getResultTemplate(request);
        assertThat(response, notNullValue());
        assertThat(response.getResultStructure(), notNullValue());
        assertThat(response.getResultStructure().get().isPresent(), is(true));
        SweAbstractDataComponent sweAbstractDataComponent = response.getResultStructure().get().get();
        assertThat(sweAbstractDataComponent, instanceOf(SweDataRecord.class));
        SweDataRecord record = (SweDataRecord) sweAbstractDataComponent;
        assertThat(record.getFields().size(), is(4));
        SweField param = record.getFieldByIdentifier("om:parameter");
        assertThat(param, notNullValue());
        assertThat(param.getElement(), notNullValue());
        assertThat(param.getElement(), instanceOf(SweDataRecord.class));
        SweDataRecord paramRecord = (SweDataRecord) param.getElement();
        assertThat(paramRecord.getDefinition(), is(OmConstants.OM_PARAMETER));
        assertThat(paramRecord.getFields().size(), is(2));
        assertThat(paramRecord.getFields().get(0).getElement().getDefinition(), is(FROM));
        assertThat(paramRecord.getFields().get(1).getElement().getDefinition(), is(TO));
        SweField field = record.getFieldByIdentifier(OBSPROP3);
        assertThat(field, notNullValue());
        assertThat(field.getElement(), notNullValue());
        assertThat(field.getElement(), instanceOf(SweQuantity.class));
    }

    @Test
    public void testGeneratedProfileIntervalGetResult() throws OwsExceptionReport, ConverterException {
        insertProfileIntervalObservationData();
        GetResultRequest request = new GetResultRequest();
        request.setObservedProperty(OBSPROP3);
        request.setOffering(OFFERING3);
        GetResultResponse response = getResultHandler.getResult(request);
        assertThat(response, notNullValue());
        String resultValues = response.getResultValues();
        assertThat(resultValues, is(
                "1#2013-07-18T03:00:00.000Z,2013-07-18T03:00:00.000Z,0.0000000000,5.0000000000,2.5000000000#"
                + "2013-07-18T03:00:00.000Z,2013-07-18T03:00:00.000Z,5.0000000000,10.0000000000,7.5000000000#"
                + "2013-07-18T03:00:00.000Z,2013-07-18T03:00:00.000Z,10.0000000000,15.0000000000,12.5000000000#"
                + "2013-07-18T03:00:00.000Z,2013-07-18T03:00:00.000Z,15.0000000000,20.0000000000,17.5000000000"));
    }

    private void insertProfileIntervalObservationData() throws OwsExceptionReport, ConverterException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        req.setObservation(Lists.newArrayList(createDefaultObservation(createProfileValue(true))));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
    }

    @Test
    public void testInsertProfileObservation() throws OwsExceptionReport, InterruptedException, ConverterException {
        insertProfileObservationData();
        assertInsertionAftermathBeforeAndAfterCacheReload();
        checkObservation(false);
    }

    @Test
    public void testGeneratedProfileGetResultTemplate()
            throws OwsExceptionReport, ConverterException, EncodingException {
        insertProfileObservationData();
        GetResultTemplateRequest request = new GetResultTemplateRequest();
        request.setObservedProperty(OBSPROP3);
        request.setOffering(OFFERING3);
        GetResultTemplateResponse response = getResultTemplateHandler.getResultTemplate(request);
        assertThat(response, notNullValue());
        assertThat(response.getResultStructure(), notNullValue());
        assertThat(response.getResultStructure().get().isPresent(), is(true));
        SweAbstractDataComponent sweAbstractDataComponent = response.getResultStructure().get().get();
        assertThat(sweAbstractDataComponent, instanceOf(SweDataRecord.class));
        SweDataRecord record = (SweDataRecord) sweAbstractDataComponent;
        assertThat(record.getFields().size(), is(4));
        SweField param = record.getFieldByIdentifier("om:parameter");
        assertThat(param, notNullValue());
        assertThat(param.getElement(), notNullValue());
        assertThat(param.getElement(), instanceOf(SweDataRecord.class));
        SweDataRecord paramRecord = (SweDataRecord) param.getElement();
        assertThat(paramRecord.getDefinition(), is(OmConstants.OM_PARAMETER));
        assertThat(paramRecord.getFields().size(), is(1));
        assertThat(paramRecord.getFields().get(0).getElement().getDefinition(), is(DEPTH));
        SweField field = record.getFieldByIdentifier(OBSPROP3);
        assertThat(field, notNullValue());
        assertThat(field.getElement(), notNullValue());
        assertThat(field.getElement(), instanceOf(SweQuantity.class));
    }

    @Test
    public void testGeneratedProfileGetResult() throws OwsExceptionReport, ConverterException {
        insertProfileObservationData();
        GetResultRequest request = new GetResultRequest();
        request.setObservedProperty(OBSPROP3);
        request.setOffering(OFFERING3);
        GetResultResponse response = getResultHandler.getResult(request);
        assertThat(response, notNullValue());
        String resultValues = response.getResultValues();
        assertThat(resultValues, is(
                "1#2013-07-18T03:00:00.000Z,2013-07-18T03:00:00.000Z,5.0000000000,2.5000000000#"
                + "2013-07-18T03:00:00.000Z,2013-07-18T03:00:00.000Z,10.0000000000,7.5000000000#"
                + "2013-07-18T03:00:00.000Z,2013-07-18T03:00:00.000Z,15.0000000000,12.5000000000#"
                + "2013-07-18T03:00:00.000Z,2013-07-18T03:00:00.000Z,20.0000000000,17.5000000000"));
    }

    private OmObservation createDefaultObservation(ProfileValue profileValue)
            throws OwsExceptionReport, ConverterException {
        OmObservation obs = new OmObservation();
        Session session = null;
        try {
            session = getSession();
            obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                    OmConstants.OBS_TYPE_PROFILE_OBSERVATION, session));
        } finally {
            returnSession(session);
        }
        obs.setResultTime(new TimeInstant(OBS_TIME));
        SingleObservationValue<List<ProfileLevel>> obsVal = new SingleObservationValue<List<ProfileLevel>>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME));
        obsVal.setValue(profileValue);
        obs.setValue(obsVal);
        return obs;
    }

    private void insertProfileObservationData() throws OwsExceptionReport, ConverterException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        req.setObservation(Lists.newArrayList(createDefaultObservation(createProfileValue(false))));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
    }

    protected void checkObservation(boolean interval) throws OwsExceptionReport {
        GetObservationRequest getObsReq =
                createDefaultGetObservationRequest(OFFERING3, PROCEDURE3, OBSPROP3, OBS_TIME, FEATURE3);
        GetObservationResponse getObsResponse =
                getObsDAO.queryObservationData(getObsReq, getGetObservationResponse(getObsReq));
        OmObservation omObservation = getObservation(getObsResponse);
        assertThat(omObservation.getObservationConstellation(), notNullValue());
        OmObservationConstellation obsConst = omObservation.getObservationConstellation();
        assertThat(obsConst.getProcedure()
                .getIdentifier(), is(PROCEDURE3));
        assertThat(obsConst.getObservableProperty()
                .getIdentifier(), is(OBSPROP3));
        assertThat(obsConst.getFeatureOfInterest()
                .getIdentifier(), is(FEATURE3));
        assertThat(omObservation.getValue(), notNullValue());
        ObservationValue<?> value = omObservation.getValue();
        assertThat(value.getValue(), instanceOf(ProfileValue.class));
        assertThat(value.getPhenomenonTime(), instanceOf(TimeInstant.class));
        TimeInstant timeInstant = (TimeInstant) value.getPhenomenonTime();
        assertThat(timeInstant.getValue()
                .toDate(), is(OBS_TIME.toDate()));

        assertNotNull(value.getValue());
        assertThat(value.getValue() instanceof ProfileValue, is(true));
        ProfileValue profile = (ProfileValue) value.getValue();
        checkProfileValue(profile, interval);
    }

    private void checkProfileValue(ProfileValue profile, boolean interval) {
        assertNotNull(profile.getFromLevel());
        assertTrue(profile.isSetFromLevel());
        checkQuantity(profile.getFromLevel(), VERTICAL_0);
        assertNotNull(profile.getToLevel());
        assertTrue(profile.isSetToLevel());
        checkQuantity(profile.getToLevel(), VERTICAL_20);
        assertNotNull(profile.getValue());
        assertTrue(profile.isSetValue());
        assertThat(profile.getValue()
                .size(), is(4));
        checkProfileLevel(profile.getValue()
                .get(0), PROFILE_VAL1, interval, VERTICAL_0, VERTICAL_5);
        checkProfileLevel(profile.getValue()
                .get(1), PROFILE_VAL2, interval, VERTICAL_5, VERTICAL_10);
        checkProfileLevel(profile.getValue()
                .get(2), PROFILE_VAL3, interval, VERTICAL_10, VERTICAL_15);
        checkProfileLevel(profile.getValue()
                .get(3), PROFILE_VAL4, interval, VERTICAL_15, VERTICAL_20);
    }

    private void checkProfileLevel(ProfileLevel level, Double value, boolean interval, Double from, Double to) {
        if (interval) {
            assertNotNull(level.getLevelStart());
            assertTrue(level.isSetLevelStart());
            checkQuantity(level.getLevelStart(), from);
            assertNotNull(level.getLevelEnd());
            assertTrue(level.isSetLevelEnd());
            checkQuantity(level.getLevelEnd(), to);
        } else {
            assertNotNull(level.getLevelEnd());
            assertTrue(level.isSetLevelEnd());
            checkQuantity(level.getLevelEnd(), to);
        }
        assertTrue(level.isSetValue());
        assertThat(level.getSimpleValue() instanceof QuantityValue, is(true));
        checkQuantityValue((QuantityValue) level.getSimpleValue(), value);
    }

    private void checkQuantityValue(QuantityValue quantity, Double value) {
        assertNotNull(quantity.getValue());
        assertThat(quantity.getValue()
                .doubleValue(), is(value));
        assertNotNull(quantity.getUom());
        assertThat(quantity.getUom(), is(TEMP_UNIT));
    }

    private void checkQuantity(SweQuantity quantity, Double value) {
        assertNotNull(quantity.getValue());
        assertThat(quantity.getValue()
                .doubleValue(), is(value));
        assertNotNull(quantity.getUom());
        assertThat(quantity.getUom(), is(VERTICAL_UNIT));
    }

    private ProfileValue createProfileValue(boolean interval) {
        ProfileValue profileValue = new ProfileValue("");
        profileValue.setGmlId("pv_1");
        UoM uom = new UoM(VERTICAL_UNIT);
        if (interval) {
            profileValue.setFromLevel(createQuantityValue(VERTICAL_0, uom, FROM));
            profileValue.setToLevel(createQuantityValue(VERTICAL_20, uom, TO));
        } else {
            profileValue.setFromLevel(createQuantityValue(VERTICAL_0, uom, DEPTH));
            profileValue.setToLevel(createQuantityValue(VERTICAL_20, uom, DEPTH));
        }
        profileValue.setValue(createProfileLevel(interval));
        return profileValue;
    }

    private QuantityValue createQuantityValue(Double vertical, UoM uom, String definition) {
        QuantityValue quantityValue = new QuantityValue(vertical, uom);
        quantityValue.setDefinition(definition);
        return quantityValue;
    }

    private List<ProfileLevel> createProfileLevel(boolean interval) {
        Map<BigDecimal, ProfileLevel> map = Maps.newTreeMap();

        ProfileLevel profileLevel_1 = new ProfileLevel();
        if (interval) {
            profileLevel_1.setLevelStart(getLevel(BigDecimal.valueOf(VERTICAL_0), FROM));
            profileLevel_1.setLevelEnd(getLevel(BigDecimal.valueOf(VERTICAL_5), TO));
            profileLevel_1.setPhenomenonTime(new TimeInstant(OBS_TIME));
        } else {
            profileLevel_1.setLevelEnd(getLevel(BigDecimal.valueOf(VERTICAL_5), DEPTH));
        }
        profileLevel_1.addValue(new QuantityValue(Double.valueOf(PROFILE_VAL1), TEMP_UNIT));
        map.put(BigDecimal.valueOf(VERTICAL_0), profileLevel_1);

        ProfileLevel profileLevel_2 = new ProfileLevel();
        if (interval) {
            profileLevel_2.setLevelStart(getLevel(BigDecimal.valueOf(VERTICAL_5), FROM));
            profileLevel_2.setLevelEnd(getLevel(BigDecimal.valueOf(VERTICAL_10), TO));
            profileLevel_2.setPhenomenonTime(new TimeInstant(OBS_TIME));
        } else {
            profileLevel_2.setLevelEnd(getLevel(BigDecimal.valueOf(VERTICAL_10), DEPTH));
        }
        profileLevel_2.addValue(new QuantityValue(Double.valueOf(PROFILE_VAL2), TEMP_UNIT));
        map.put(BigDecimal.valueOf(VERTICAL_5), profileLevel_2);

        ProfileLevel profileLevel_3 = new ProfileLevel();
        if (interval) {
            profileLevel_3.setLevelStart(getLevel(BigDecimal.valueOf(VERTICAL_10), FROM));
            profileLevel_3.setLevelEnd(getLevel(BigDecimal.valueOf(VERTICAL_15), TO));
            profileLevel_3.setPhenomenonTime(new TimeInstant(OBS_TIME));
        } else {
            profileLevel_3.setLevelEnd(getLevel(BigDecimal.valueOf(VERTICAL_15), DEPTH));
        }
        profileLevel_3.addValue(new QuantityValue(Double.valueOf(PROFILE_VAL3), TEMP_UNIT));
        map.put(BigDecimal.valueOf(VERTICAL_10), profileLevel_3);

        ProfileLevel profileLevel_4 = new ProfileLevel();
        if (interval) {
            profileLevel_4.setLevelStart(getLevel(BigDecimal.valueOf(VERTICAL_15), FROM));
            profileLevel_4.setLevelEnd(getLevel(BigDecimal.valueOf(VERTICAL_20), TO));
            profileLevel_4.setPhenomenonTime(new TimeInstant(OBS_TIME));
        } else {
            profileLevel_4.setLevelEnd(getLevel(BigDecimal.valueOf(VERTICAL_20), DEPTH));
        }
        profileLevel_4.addValue(new QuantityValue(Double.valueOf(PROFILE_VAL4), TEMP_UNIT));
        map.put(BigDecimal.valueOf(VERTICAL_15), profileLevel_4);

        return (List<ProfileLevel>) Lists.newArrayList(map.values());
    }

    private QuantityValue getLevel(BigDecimal v, String n) {
        QuantityValue value = new QuantityValue(v);
        value.setDefinition(n);
        value.setName(n);
        value.setUnit(new UoM(VERTICAL_UNIT).setName("meter"));
        return value;
    }

    @Override
    protected void assertInsertionAftermath(boolean afterCacheUpdate) throws OwsExceptionReport {
        // check observation types
        assertThat(getCache().getObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_PROFILE_OBSERVATION));

        // check offerings for procedure
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE3), containsInAnyOrder(OFFERING3));

        // check procedures and hidden child procedures for offering
        assertThat(getCache().getProceduresForOffering(OFFERING3), contains(PROCEDURE3));
        assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING3), empty());

        // check allowed observation types for offering
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_PROFILE_OBSERVATION));

        // check parent procedures
        assertThat(getCache().getParentProcedures(PROCEDURE3, true, false), empty());

        // check child procedures
        assertThat(getCache().getChildProcedures(PROCEDURE3, true, false), empty());

        if (afterCacheUpdate) {
            // check features of interest for offering
            assertThat(getCache().getFeaturesOfInterestForOffering(OFFERING3), contains(FEATURE3));
            // check procedure for features of interest
            assertThat(getCache().getProceduresForFeatureOfInterest(FEATURE3), contains(PROCEDURE3));
            // check offering for features of interest
            assertThat(getCache().getOfferingsForFeatureOfInterest(FEATURE3), contains(OFFERING3));
        }

        // check obsprops for offering
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING3), contains(OBSPROP3));

        // check offering for obsprops
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP3), contains(OFFERING3));

        // check obsprops for procedure
        assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE3), contains(OBSPROP3));

        // check procedures for obsprop
        assertThat(getCache().getProceduresForObservableProperty(OBSPROP3), contains(PROCEDURE3));
    }

}
