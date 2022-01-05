/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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

import java.util.List;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.convert.ConverterException;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.sos.request.InsertResultRequest;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.InsertResultResponse;
import org.n52.shetland.ogc.sos.response.InsertResultTemplateResponse;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweTime;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.sos.event.events.ResultInsertion;
import org.n52.sos.event.events.ResultTemplateInsertion;
import org.n52.svalbard.encode.exception.EncodingException;

import net.opengis.swe.x20.DataRecordDocument;
import net.opengis.swe.x20.TextEncodingDocument;

public class InsertResultInsertDAOTest extends AbstractObservationInsertDAOTest {

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
    public void testInsertResult() throws OwsExceptionReport, InterruptedException, EncodingException, ConverterException {
        insertResultTemplate(RESULT_TEMPLATE, PROCEDURE3, OFFERING3, OBSPROP3, FEATURE3);
        InsertResultRequest req = new InsertResultRequest();
        req.setTemplateIdentifier(RESULT_TEMPLATE);
        req.setResultValues(makeResultValueString(CollectionHelper.list(TIME1, TIME2, TIME3),
                CollectionHelper.list(VAL1, VAL2, VAL3)));
        InsertResultResponse resp = insertResultDAO.insertResult(req);
        this.serviceEventBus.submit(new ResultInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();

        checkObservation(OFFERING1, PROCEDURE3, OBSPROP3, TIME1, PROCEDURE3, OBSPROP3, FEATURE3, VAL1, TEMP_UNIT);
        checkObservation(OFFERING2, PROCEDURE3, OBSPROP3, TIME2, PROCEDURE3, OBSPROP3, FEATURE3, VAL2, TEMP_UNIT);
        checkObservation(OFFERING3, PROCEDURE3, OBSPROP3, TIME3, PROCEDURE3, OBSPROP3, FEATURE3, VAL3, TEMP_UNIT);
    }

    private void insertResultTemplate(String identifier, String procedureId, String offeringId, String obsPropId,
            String featureId) throws OwsExceptionReport, ConverterException, EncodingException {
        InsertResultTemplateRequest req = new InsertResultTemplateRequest();
        req.setIdentifier(identifier);
        Session session = null;
        try {
            session = getSession();
            req.setObservationTemplate(getOmObsConst(procedureId, obsPropId, TEMP_UNIT, offeringId, featureId,
                    OmConstants.OBS_TYPE_MEASUREMENT, session));
        } finally {
            returnSession(session);
        }
        SweTextEncoding textEncoding = new SweTextEncoding();
        textEncoding.setCollapseWhiteSpaces(false);

        SosResultEncoding resultEncoding = createResultEncoding(getTextEncoding());
        req.setResultEncoding(resultEncoding);

        SweDataRecord dataRecord = new SweDataRecord();
        SweTime sweTime = new SweTime();
        sweTime.setUom(OmConstants.PHEN_UOM_ISO8601);
        sweTime.setDefinition(OmConstants.PHENOMENON_TIME);
        dataRecord.addField(new SweField("time", sweTime));
        SweQuantity airTemp = new SweQuantity();
        airTemp.setDefinition(obsPropId);
        airTemp.setUom(TEMP_UNIT);
        dataRecord.addField(new SweField("air_temperature", airTemp));
        SosResultStructure resultStructure = createResultStructure(dataRecord);
        req.setResultStructure(resultStructure);
        InsertResultTemplateResponse resp = insertResultTemplateDAO.insertResultTemplate(req);
        this.serviceEventBus.submit(new ResultTemplateInsertion(req, resp));
    }

    private SosResultEncoding createResultEncoding(SweTextEncoding textEncoding) throws EncodingException {
        return new SosResultEncoding(textEncoding, createTextEncodingString(textEncoding));
    }

    private SosResultStructure createResultStructure(SweDataRecord dataRecord) throws EncodingException {
        return new SosResultStructure(dataRecord, createDataRecordString(dataRecord));
    }

    private String makeResultValueString(List<DateTime> times, List<Double> values) {
        if (times.size() != values.size()) {
            throw new RuntimeException("times and values must be the same length (times: " + times.size()
                    + ", values: " + values.size() + ")");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times.size(); i++) {
            if (i > 0) {
                sb.append(BLOCK_SEPARATOR);
            }
            sb.append(times.get(i));
            sb.append(TOKEN_SEPARATOR);
            sb.append(values.get(i));
        }
        return sb.toString();
    }

}

