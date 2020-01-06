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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.convert.ConverterException;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.shetland.ogc.swe.SweAbstractDataRecord;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Lists;

public class ComplexObservationInsertDAOTest extends AbstractInsertDAOTest {

    private static final String OBSPROP_VALUE1 = "value1";
    private static final String OBSPROP_VALUE2 = "value2";
    private static final String OBSPROP_VALUE3 = "value3";

    @Before
    public void setUp() throws OwsExceptionReport, ConverterException, EncodingException {
        super.setUp();
        Session session = null;
        try {
            session = getSession();
            HibernateMetadataCache.init(session);
            insertSensor(PROCEDURE3, OFFERING3, OBSPROP3, null, OmConstants.OBS_TYPE_COMPLEX_OBSERVATION);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testInsertComplexObservation() throws OwsExceptionReport, InterruptedException, ConverterException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        OmObservation obs = new OmObservation();

        Session session = null;
        try {
            session = getSession();
            obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                    OmConstants.OBS_TYPE_COMPLEX_OBSERVATION, session));
        } finally {
            returnSession(session);
        }

        obs.setResultTime(new TimeInstant(OBS_TIME));
        SingleObservationValue<SweAbstractDataRecord> obsVal = new SingleObservationValue<SweAbstractDataRecord>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME));
        SweDataRecord record = new SweDataRecord();
        record.addField(
                new SweField(OBSPROP_VALUE1, new SweQuantity(BigDecimal.valueOf(VAL1), TEMP_UNIT).setDefinition(OBSPROP_VALUE1)));
        record.addField(
                new SweField(OBSPROP_VALUE2, new SweQuantity(BigDecimal.valueOf(VAL2), TEMP_UNIT).setDefinition(OBSPROP_VALUE2)));
        record.addField(
                new SweField(OBSPROP_VALUE3, new SweQuantity(BigDecimal.valueOf(VAL3), TEMP_UNIT).setDefinition(OBSPROP_VALUE3)));

        obsVal.setValue(new ComplexValue(record));
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        this.serviceEventBus.submit(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();
        checkObservation();
    }

    protected void checkObservation() throws OwsExceptionReport {
        GetObservationRequest getObsReq =
                createDefaultGetObservationRequest(OFFERING3, PROCEDURE3, OBSPROP3, OBS_TIME, FEATURE3);
        GetObservationResponse getObsResponse =
                getObsDAO.queryObservationData(getObsReq, getGetObservationRequest(getObsReq));
        assertThat(getObsResponse, notNullValue());
        assertThat(getObsResponse.getObservationCollection().hasNext(), is(true));
        OmObservation omObservation = getObservation(getObsResponse);
        assertThat(omObservation.getObservationConstellation(), notNullValue());
        OmObservationConstellation obsConst = omObservation.getObservationConstellation();
        assertThat(obsConst.getProcedure().getIdentifier(), is(PROCEDURE3));
        assertThat(obsConst.getObservableProperty().getIdentifier(), is(OBSPROP3));
        assertThat(obsConst.getFeatureOfInterest().getIdentifier(), is(FEATURE3));
        assertThat(omObservation.getValue(), notNullValue());
        ObservationValue<?> value = omObservation.getValue();
        assertThat(value.getValue(), instanceOf(ComplexValue.class));
        assertThat(value.getPhenomenonTime(), instanceOf(TimeInstant.class));
        TimeInstant timeInstant = (TimeInstant) value.getPhenomenonTime();
        assertThat(timeInstant.getValue().toDate(), is(OBS_TIME.toDate()));

        assertThat(value.getValue().getValue() instanceof SweAbstractDataRecord, is(true));
        SweAbstractDataRecord record = (SweAbstractDataRecord) value.getValue().getValue();
        assertThat(record.getFields().size(), is(3));
        checkField(record.getFields().get(0), OBSPROP_VALUE1, VAL1);
        checkField(record.getFields().get(1), OBSPROP_VALUE2, VAL2);
        checkField(record.getFields().get(2), OBSPROP_VALUE3, VAL3);
    }

    private void checkField(SweField field, String definition, Double value) {
        assertEquals(field.getName().getValue(), definition);
        assertNotNull(field.getElement());
        assertEquals(field.getElement().getDefinition(), definition);
        assertThat(field.getElement() instanceof SweQuantity, is(true));
        SweQuantity quantityValue = (SweQuantity) field.getElement();
        assertThat(quantityValue.getValue().doubleValue(), is(value));
        assertThat(quantityValue.getUom(), is(TEMP_UNIT));
    }

    @Override
    protected void assertInsertionAftermath(boolean afterCacheUpdate) throws OwsExceptionReport {
        // check observation types
        assertThat(getCache().getObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION));

        // check offerings for procedure
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE3), containsInAnyOrder(OFFERING3));

        assertThat(getCache().getProceduresForOffering(OFFERING3), contains(PROCEDURE3));
        assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING3), empty());

        // check allowed observation types for offering
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION));
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
                    containsInAnyOrder(OBSPROP3, OBSPROP_VALUE1, OBSPROP_VALUE2, OBSPROP_VALUE3));
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
                    containsInAnyOrder(OBSPROP3, OBSPROP_VALUE1, OBSPROP_VALUE2, OBSPROP_VALUE3));
        } else {
            assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE3), contains(OBSPROP3));
        }

        // check procedures for obsprop
        assertThat(getCache().getProceduresForObservableProperty(OBSPROP3), contains(PROCEDURE3));
    }

}
