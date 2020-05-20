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

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.convert.ConverterException;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Lists;

public class InsertObservationInsertDAOTest extends AbstractObservationInsertDAOTest {

    @Before
    public void setUp() throws OwsExceptionReport, ConverterException, EncodingException {
        super.setUp();
        Session session = null;
        try {
            session = getSession();
            HibernateMetadataCache.init(session);
            session = getSession();
            HibernateMetadataCache.init(session);
            insertSensor(PROCEDURE1, OFFERING1, OBSPROP1, null, OmConstants.OBS_TYPE_MEASUREMENT);
            insertSensor(PROCEDURE2, OFFERING2, OBSPROP2, PROCEDURE1, OmConstants.OBS_TYPE_MEASUREMENT);
            insertSensor(PROCEDURE3, OFFERING3, OBSPROP3, PROCEDURE2, OmConstants.OBS_TYPE_MEASUREMENT);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testInsertObservation() throws OwsExceptionReport, InterruptedException, ConverterException {
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

        obs.setResultTime(new TimeInstant(OBS_TIME));
        SingleObservationValue<BigDecimal> obsVal = new SingleObservationValue<BigDecimal>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME));
        obsVal.setValue(new QuantityValue(Double.valueOf(OBS_VAL), TEMP_UNIT));
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();

        // TODO requests for the parent procedures fail?
        // checkObservation(OFFERING1, PROCEDURE1, OBSPROP3, OBS_TIME,
        // PROCEDURE3, OBSPROP3, FEATURE3,
        // OBS_VAL, TEMP_UNIT);
        // checkObservation(OFFERING2, PROCEDURE2, OBSPROP3, OBS_TIME,
        // PROCEDURE3, OBSPROP3, FEATURE3,
        // OBS_VAL, TEMP_UNIT);
        checkObservation(OFFERING1, PROCEDURE3, OBSPROP3, OBS_TIME, PROCEDURE3, OBSPROP3, FEATURE3, OBS_VAL,
                TEMP_UNIT);
        checkObservation(OFFERING2, PROCEDURE3, OBSPROP3, OBS_TIME, PROCEDURE3, OBSPROP3, FEATURE3, OBS_VAL,
                TEMP_UNIT);
        checkObservation(OFFERING3, PROCEDURE3, OBSPROP3, OBS_TIME, PROCEDURE3, OBSPROP3, FEATURE3, OBS_VAL,
                TEMP_UNIT);
    }


    @Test
    public void testInsertObservationWithSamplingGeometry()
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

        obs.setResultTime(new TimeInstant(OBS_TIME_SP));
        SingleObservationValue<BigDecimal> obsVal = new SingleObservationValue<BigDecimal>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME_SP));
        obsVal.setValue(new QuantityValue(BigDecimal.valueOf(OBS_VAL), TEMP_UNIT));
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        obs.addParameter(createSamplingGeometry(GEOMETRY));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
        checkSamplingGeometry(OFFERING1, PROCEDURE3, OBSPROP3, FEATURE3, OBS_TIME_SP, GEOMETRY);
    }

    @Test
    public void testInsertObservationWithOmParameter()
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

        obs.setResultTime(new TimeInstant(OBS_TIME_PARAM));
        SingleObservationValue<BigDecimal> obsVal = new SingleObservationValue<BigDecimal>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME_PARAM));
        obsVal.setValue(new QuantityValue(Double.valueOf(OBS_VAL), TEMP_UNIT));
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        addParameter(obs);
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
        checkOmParameter(OFFERING3, PROCEDURE3, OBSPROP3, FEATURE3, OBS_TIME_PARAM);
    }

    @Test(expected = OwsExceptionReport.class)
    public void testInsertDuplicateObservation() throws OwsExceptionReport, ConverterException, InterruptedException {
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
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
        InsertObservationResponse resp2 = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp2));
        assertInsertionAftermathBeforeAndAfterCacheReload();
    }

}
