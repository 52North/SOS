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
package org.n52.sos.ds.hibernate;

import java.math.BigDecimal;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.convert.ConverterException;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Lists;

public class VerticalObservationInsertDAOTest extends AbstractInsertDAOTest {

    @Before
    public void setUp() throws OwsExceptionReport, ConverterException, EncodingException {
        super.setUp();
        Session session = null;
        try {
            session = getSession();
            HibernateMetadataCache.init(session);
            insertSensor(PROCEDURE3, OFFERING3, OBSPROP3, null, OmConstants.OBS_TYPE_MEASUREMENT);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testInsertObservationWithHeightParameter()
            throws OwsExceptionReport, ConverterException, InterruptedException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        OmObservation obs = new OmObservation();

        Session session = null;
        try {
            session = getSession();
            obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                    OmConstants.OBS_TYPE_MEASUREMENT, session));
        } finally {
            returnSession(session);
        }

        obs.setResultTime(new TimeInstant(OBS_TIME_HEIGHT));
        SingleObservationValue<BigDecimal> obsVal = new SingleObservationValue<BigDecimal>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME_HEIGHT));
        obsVal.setValue(new QuantityValue(Double.valueOf(OBS_VAL), TEMP_UNIT));
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        obs.addParameter(createHeight(HEIGHT_DEPTH_VALUE));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
        checkHeightParameter(OFFERING3, PROCEDURE3, OBSPROP3, FEATURE3, OBS_TIME_HEIGHT);
    }

    @Test
    public void testInsertObservationWithDepthParameter()
            throws OwsExceptionReport, ConverterException, InterruptedException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        OmObservation obs = new OmObservation();

        Session session = null;
        try {
            session = getSession();
            obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                    OmConstants.OBS_TYPE_MEASUREMENT, session));
        } finally {
            returnSession(session);
        }

        obs.setResultTime(new TimeInstant(OBS_TIME_DEPTH));
        SingleObservationValue<BigDecimal> obsVal = new SingleObservationValue<BigDecimal>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME_DEPTH));
        obsVal.setValue(new QuantityValue(Double.valueOf(OBS_VAL), TEMP_UNIT));
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        obs.addParameter(createDepth(HEIGHT_DEPTH_VALUE));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
        checkDepthParameter(OFFERING3, PROCEDURE3, OBSPROP3, FEATURE3, OBS_TIME_DEPTH);
    }

    @Test(expected = OwsExceptionReport.class)
    public void testInsertDuplicateObservationWithDepthParameter()
            throws OwsExceptionReport, ConverterException, InterruptedException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        OmObservation obs = new OmObservation();

        Session session = null;
        try {
            session = getSession();
            obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                    OmConstants.OBS_TYPE_MEASUREMENT, session));
        } finally {
            returnSession(session);
        }

        obs.setResultTime(new TimeInstant(OBS_TIME_DEPTH));
        SingleObservationValue<BigDecimal> obsVal = new SingleObservationValue<BigDecimal>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME_DEPTH));
        obsVal.setValue(new QuantityValue(Double.valueOf(OBS_VAL), TEMP_UNIT));
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        obs.addParameter(createDepth(HEIGHT_DEPTH_VALUE));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
        obs.addParameter(createDepth(HEIGHT_DEPTH_VALUE));
        InsertObservationResponse resp2 = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp2));
    }

    @Test(expected = OwsExceptionReport.class)
    public void testInsertDuplicateObservationWithHeightParameter()
            throws OwsExceptionReport, ConverterException, InterruptedException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        OmObservation obs = new OmObservation();

        Session session = null;
        try {
            session = getSession();
            obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                    OmConstants.OBS_TYPE_MEASUREMENT, session));
        } finally {
            returnSession(session);
        }

        obs.setResultTime(new TimeInstant(OBS_TIME_HEIGHT));
        SingleObservationValue<BigDecimal> obsVal = new SingleObservationValue<BigDecimal>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME_HEIGHT));
        obsVal.setValue(new QuantityValue(Double.valueOf(OBS_VAL), TEMP_UNIT));
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        obs.addParameter(createHeight(HEIGHT_DEPTH_VALUE));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
        obs.addParameter(createDepth(HEIGHT_DEPTH_VALUE));
        InsertObservationResponse resp2 = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp2));
    }


    private void checkHeightParameter(String offering, String procedure, String obsprop, String feature, DateTime time)
            throws OwsExceptionReport {
        GetObservationRequest getObsReq =
                createDefaultGetObservationRequest(offering, procedure, obsprop, time, feature);
        GetObservationResponse getObsResponse =
                getObsDAO.queryObservationData(getObsReq, getGetObservationRequest(getObsReq));
        assertThat(getObsResponse, notNullValue());
        assertThat(getObsResponse.getObservationCollection().hasNext(), is(true));
        OmObservation omObservation = getObservation(getObsResponse);
        assertThat(omObservation.isSetParameter(), is(true));
        assertThat(omObservation.isSetHeightParameter(), is(true));
        checkNamedValue(omObservation.getHeightParameter(), OmConstants.PARAMETER_NAME_HEIGHT, HEIGHT_DEPTH_VALUE,
                HEIGHT_DEPTH_UNIT);
    }

    private void checkDepthParameter(String offering, String procedure, String obsprop, String feature, DateTime time)
            throws OwsExceptionReport {
        GetObservationRequest getObsReq =
                createDefaultGetObservationRequest(offering, procedure, obsprop, time, feature);
        GetObservationResponse getObsResponse =
                getObsDAO.queryObservationData(getObsReq, getGetObservationRequest(getObsReq));
        assertThat(getObsResponse, notNullValue());
        assertThat(getObsResponse.getObservationCollection().hasNext(), is(true));
        OmObservation omObservation = getObservation(getObsResponse);
        assertThat(omObservation.isSetParameter(), is(true));
        assertThat(omObservation.isSetDepthParameter(), is(true));
        checkNamedValue(omObservation.getDepthParameter(), OmConstants.PARAMETER_NAME_DEPTH, HEIGHT_DEPTH_VALUE,
                HEIGHT_DEPTH_UNIT);
    }

    private NamedValue<?> createHeight(BigDecimal value) {
        return createQuantityParameter(OmConstants.PARAMETER_NAME_HEIGHT, value, HEIGHT_DEPTH_UNIT);
    }

    private NamedValue<?> createDepth(BigDecimal value) {
        return createQuantityParameter(OmConstants.PARAMETER_NAME_DEPTH, value, HEIGHT_DEPTH_UNIT);
    }

    @Override
    protected void assertInsertionAftermath(boolean afterCacheUpdate) throws OwsExceptionReport {
        // check observation types
        assertThat(getCache().getObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_MEASUREMENT));

        // check offerings for procedure
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE3), containsInAnyOrder(OFFERING3));

        // check procedures and hidden child procedures for offering
        assertThat(getCache().getProceduresForOffering(OFFERING3), contains(PROCEDURE3));
        assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING3), empty());

        // check allowed observation types for offering
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_MEASUREMENT));

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
