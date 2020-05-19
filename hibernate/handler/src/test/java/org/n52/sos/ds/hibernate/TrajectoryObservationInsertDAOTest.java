/*
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
package org.n52.sos.ds.hibernate;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.n52.iceland.convert.ConverterException;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.System;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilities;
import org.n52.shetland.ogc.sensorML.elements.SmlCapability;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.shetland.ogc.swe.SweConstants.SweCoordinateNames;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.SweVector;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Lists;

public class TrajectoryObservationInsertDAOTest extends AbstractInsertDAOTest {

    public static final Geometry TRAJ_GEOMETRY_1 = FACTORY.createPoint(new Coordinate(52.7, 7.52));

    public static final Geometry TRAJ_GEOMETRY_2 = FACTORY.createPoint(new Coordinate(52.8, 7.53));

    public static final Geometry TRAJ_GEOMETRY_3 = FACTORY.createPoint(new Coordinate(52.9, 7.54));

    public static final Geometry TRAJ_GEOMETRY_4 = FACTORY.createPoint(new Coordinate(53.0, 7.55));

    public static final Geometry TRAJ_GEOMETRY_5 = FACTORY.createPoint(new Coordinate(53.1, 7.56));

    public static final DateTime TRAJ_OBS_TIME_1 = new DateTime("2013-07-18T03:00:00Z");

    public static final DateTime TRAJ_OBS_TIME_2 = new DateTime("2013-07-18T04:00:00Z");

    public static final DateTime TRAJ_OBS_TIME_3 = new DateTime("2013-07-18T05:00:00Z");

    public static final DateTime TRAJ_OBS_TIME_4 = new DateTime("2013-07-18T06:00:00Z");

    public static final DateTime TRAJ_OBS_TIME_5 = new DateTime("2013-07-18T07:00:00Z");

    public static final Double TRAJ_OBS_VALUE_1 = 1.0;

    public static final Double TRAJ_OBS_VALUE_2 = 2.0;

    public static final Double TRAJ_OBS_VALUE_3 = 3.0;

    public static final Double TRAJ_OBS_VALUE_4 = 4.0;

    public static final Double TRAJ_OBS_VALUE_5 = 5.0;

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

    @Override
    protected void modifySystem(System system) {
        SmlCapabilities caps = new SmlCapabilities("metadata");
        SmlCapability insitu = new SmlCapability("insitu");
        insitu.setAbstractDataComponent(new SweBoolean().setValue(true).setDefinition("insitu"));
        caps.addCapability(insitu);
        SmlCapability mobile = new SmlCapability("mobile");
        mobile.setAbstractDataComponent(new SweBoolean().setValue(true).setDefinition("mobile"));
        caps.addCapability(mobile);
        system.addCapabilities(Lists.newArrayList(caps));
    }

    @Test
    public void testInsertObservation() throws OwsExceptionReport, InterruptedException, ConverterException {
        inserObservationData();
        assertInsertionAftermathBeforeAndAfterCacheReload();
        checkValue(checkSamplingGeometry(OFFERING3, PROCEDURE3, OBSPROP3, FEATURE3, TRAJ_OBS_TIME_1, TRAJ_GEOMETRY_1),
                TRAJ_OBS_TIME_1, TRAJ_OBS_VALUE_1, TEMP_UNIT);
        checkValue(checkSamplingGeometry(OFFERING3, PROCEDURE3, OBSPROP3, FEATURE3, TRAJ_OBS_TIME_2, TRAJ_GEOMETRY_2),
                TRAJ_OBS_TIME_2, TRAJ_OBS_VALUE_2, TEMP_UNIT);
        checkValue(checkSamplingGeometry(OFFERING3, PROCEDURE3, OBSPROP3, FEATURE3, TRAJ_OBS_TIME_3, TRAJ_GEOMETRY_3),
                TRAJ_OBS_TIME_3, TRAJ_OBS_VALUE_3, TEMP_UNIT);
        checkValue(checkSamplingGeometry(OFFERING3, PROCEDURE3, OBSPROP3, FEATURE3, TRAJ_OBS_TIME_4, TRAJ_GEOMETRY_4),
                TRAJ_OBS_TIME_4, TRAJ_OBS_VALUE_4, TEMP_UNIT);
        checkValue(checkSamplingGeometry(OFFERING3, PROCEDURE3, OBSPROP3, FEATURE3, TRAJ_OBS_TIME_5, TRAJ_GEOMETRY_5),
                TRAJ_OBS_TIME_5, TRAJ_OBS_VALUE_5, TEMP_UNIT);
    }
    
    @Test
    public void testGeneratedGetResultTemplate()
            throws OwsExceptionReport, ConverterException, EncodingException {
        inserObservationData();
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
        SweField samp = record.getFieldByIdentifier("samplingGeometry");
        assertThat(samp, notNullValue());
        assertThat(samp.getElement(), notNullValue());
        assertThat(samp.getElement(), instanceOf(SweVector.class));
        SweVector sampVector = (SweVector) samp.getElement();
        assertThat(sampVector.getDefinition(), is(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY));
        assertThat(sampVector.getCoordinates().size(), is(2));
        assertThat(sampVector.getCoordinates().get(0).getName(), is(SweCoordinateNames.LATITUDE));
        assertThat(sampVector.getCoordinates().get(1).getName(), is(SweCoordinateNames.LONGITUDE));
        SweField field = record.getFieldByIdentifier(OBSPROP3);
        assertThat(field, notNullValue());
        assertThat(field.getElement(), notNullValue());
        assertThat(field.getElement(), instanceOf(SweQuantity.class));
    }

    @Test
    public void testGeneratedGetResult() throws OwsExceptionReport, ConverterException {
        inserObservationData();
        GetResultRequest request = new GetResultRequest();
        request.setObservedProperty(OBSPROP3);
        request.setOffering(OFFERING3);
        GetResultResponse response = getResultHandler.getResult(request);
        assertThat(response, notNullValue());
        String resultValues = response.getResultValues();
        assertThat(resultValues, is(
                "5#2013-07-18T03:00:00.000Z,2013-07-18T03:00:00.000Z,1.0000000000,52.7,7.52#"
                + "2013-07-18T04:00:00.000Z,2013-07-18T04:00:00.000Z,2.0000000000,52.8,7.53#"
                + "2013-07-18T05:00:00.000Z,2013-07-18T05:00:00.000Z,3.0000000000,52.9,7.54#"
                + "2013-07-18T06:00:00.000Z,2013-07-18T06:00:00.000Z,4.0000000000,53.0,7.55#"
                + "2013-07-18T07:00:00.000Z,2013-07-18T07:00:00.000Z,5.0000000000,53.1,7.56"));
    }
    
    private List<OmObservation> createDefaultObservation() throws OwsExceptionReport, ConverterException {
        OmObservation obs = new OmObservation();

        Session session = null;
        try {
            session = getSession();
            obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                    OmConstants.OBS_TYPE_MEASUREMENT, session));
        } finally {
            returnSession(session);
        }
        List<OmObservation> observations = new LinkedList<>();
        observations.add(addData(obs.copyTo(new OmObservation()), TRAJ_GEOMETRY_1, TRAJ_OBS_TIME_1, TRAJ_OBS_VALUE_1));
        observations.add(addData(obs.copyTo(new OmObservation()), TRAJ_GEOMETRY_2, TRAJ_OBS_TIME_2, TRAJ_OBS_VALUE_2));
        observations.add(addData(obs.copyTo(new OmObservation()), TRAJ_GEOMETRY_3, TRAJ_OBS_TIME_3, TRAJ_OBS_VALUE_3));
        observations.add(addData(obs.copyTo(new OmObservation()), TRAJ_GEOMETRY_4, TRAJ_OBS_TIME_4, TRAJ_OBS_VALUE_4));
        observations.add(addData(obs.copyTo(new OmObservation()), TRAJ_GEOMETRY_5, TRAJ_OBS_TIME_5, TRAJ_OBS_VALUE_5));
        return observations;
    }
    
    private void inserObservationData() throws OwsExceptionReport, ConverterException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        req.setObservation(createDefaultObservation());
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
    }

    private OmObservation addData(OmObservation obs, Geometry geometry, DateTime time, Double value) {
        obs.addParameter(createSamplingGeometry(geometry));
        obs.setResultTime(new TimeInstant(time));
        SingleObservationValue<BigDecimal> obsVal = new SingleObservationValue<BigDecimal>();
        obsVal.setPhenomenonTime(new TimeInstant(time));
        obsVal.setValue(new QuantityValue(Double.valueOf(value), TEMP_UNIT));
        obs.setValue(obsVal);
        return obs;
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
