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

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.n52.iceland.convert.ConverterException;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Lists;

@Ignore
public class DataArrayObservationInsertDAOTest extends AbstractInsertDAOTest {

    public static final Integer NANO1 = 200;

    public static final Integer NANO2 = 250;

    public static final Integer NANO3 = 300;

    @Before
    public void setUp() throws OwsExceptionReport, ConverterException, EncodingException {
        super.setUp();
        Session session = null;
        try {
            session = getSession();
            HibernateMetadataCache.init(session);
            insertSensor(PROCEDURE3, OFFERING3, OBSPROP3, null, OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void test() {

    }

    @Test
    @Ignore
    public void testInsertDataArrayObservation() throws OwsExceptionReport, InterruptedException, ConverterException, EncodingException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        OmObservation obs = new OmObservation();

        Session session = null;
        try {
            session = getSession();
            obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                    OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION, session));
        } finally {
            returnSession(session);
        }

        obs.setResultTime(new TimeInstant(OBS_TIME));
        SingleObservationValue<SweDataArray> obsVal = new SingleObservationValue<SweDataArray>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME));
        SweDataArrayValue sweDataArrayValue = new SweDataArrayValue();
        SweDataArray sweDataArray = new SweDataArray();
        sweDataArray.setElementCount(new SweCount().setValue(3));
//        SweDataRecord sweDataRecord = new SweDataRecord();

        SweQuantity temp = new SweQuantity();
        temp.setDefinition(OBSPROP3);
        temp.setUom(TEMP_UNIT);
        temp.setXml(createQuantityString(temp));
        sweDataArray.setElementType(temp);
        sweDataArray.setEncoding(getTextEncoding());

        // add values
        sweDataArray.add(CollectionHelper.list(Double.toString(VAL1)));
        sweDataArray.add(CollectionHelper.list(Double.toString(VAL2)));
        sweDataArray.add(CollectionHelper.list(Double.toString(VAL3)));

        sweDataArrayValue.setValue(sweDataArray);

        obsVal.setValue(sweDataArrayValue);
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
    }

    @Test
    @Ignore
    public void testInsertComplexDataArrayObservation()
            throws OwsExceptionReport, InterruptedException, ConverterException, EncodingException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        OmObservation obs = new OmObservation();

        Session session = null;
        try {
            session = getSession();
            obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                    OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION, session));
        } finally {
            returnSession(session);
        }

        obs.setResultTime(new TimeInstant(OBS_TIME));
        SingleObservationValue<SweDataArray> obsVal = new SingleObservationValue<SweDataArray>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME));
        SweDataArrayValue sweDataArrayValue = new SweDataArrayValue();
        SweDataArray sweDataArray = new SweDataArray();
        sweDataArray.setElementCount(new SweCount().setValue(3));
        SweDataRecord sweDataRecord = new SweDataRecord();

        SweCount nano = new SweCount();
        nano.setDefinition("nanometer");
        nano.setLabel("nm");
        SweField nanoField = new SweField(OmConstants.EN_OBSERVED_PROPERTY, nano);
        sweDataRecord.addField(nanoField);

        SweQuantity temp = new SweQuantity();
        temp.setDefinition(OBSPROP3);
        temp.setUom(TEMP_UNIT);
        SweField tempField = new SweField(OmConstants.EN_OBSERVED_PROPERTY, temp);
        sweDataRecord.addField(tempField);
        sweDataRecord.setXml(createDataRecordString(sweDataRecord));
        sweDataArray.setElementType(sweDataRecord);
        sweDataArray.setEncoding(getTextEncoding());

        // add values
        sweDataArray.add(CollectionHelper.list(Integer.toString(NANO1), Double.toString(VAL1)));
        sweDataArray.add(CollectionHelper.list(Integer.toString(NANO2), Double.toString(VAL2)));
        sweDataArray.add(CollectionHelper.list(Integer.toString(NANO3), Double.toString(VAL3)));

        sweDataArrayValue.setValue(sweDataArray);

        obsVal.setValue(sweDataArrayValue);
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
    }

    @Override
    protected void assertInsertionAftermath(boolean afterCacheUpdate) throws OwsExceptionReport {
        // check observation types
        assertThat(getCache().getObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION));

        // check offerings for procedure
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE3), containsInAnyOrder(OFFERING3));

        assertThat(getCache().getProceduresForOffering(OFFERING3), contains(PROCEDURE3));
        assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING3), empty());

        // check allowed observation types for offering
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION));
        // check parent procedures
        assertThat(getCache().getParentProcedures(PROCEDURE3, true, false),
                empty());

        // check child procedures
        assertThat(getCache().getChildProcedures(PROCEDURE3, true, false), empty());

        // check features of interest for offering, procedure
        if (afterCacheUpdate) {
         // check features of interest for offering
            assertThat(getCache().getFeaturesOfInterestForOffering(OFFERING3), contains(FEATURE3));
            // check procedure for features of interest
            assertThat(getCache().getProceduresForFeatureOfInterest(FEATURE3), contains(PROCEDURE3));
            // check offering for features of interest
            assertThat(getCache().getOfferingsForFeatureOfInterest(FEATURE3), contains(OFFERING3));
        }

        // check obsprops for offering
        if (afterCacheUpdate) {
            assertThat(getCache().getObservablePropertiesForOffering(OFFERING3),
                    containsInAnyOrder(OBSPROP3));
        } else {
            assertThat(getCache().getObservablePropertiesForOffering(OFFERING3), contains(OBSPROP3));
        }

        // check offering for obsprops
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP3), containsInAnyOrder(OFFERING3));

        // check obsprops for procedure
        // TODO child procedure obsprops are not currently set for parents.
        // should they be?
        if (afterCacheUpdate) {
            assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE3),
                    containsInAnyOrder(OBSPROP3));
        } else {
            assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE3), contains(OBSPROP3));
        }

        // check procedures for obsprop
        assertThat(getCache().getProceduresForObservableProperty(OBSPROP3), contains(PROCEDURE3));
    }


}
