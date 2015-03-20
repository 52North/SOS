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
package org.n52.sos.ds.hibernate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.opengis.sensorML.x101.SystemDocument;
import net.opengis.swe.x20.DataRecordDocument;
import net.opengis.swe.x20.TextEncodingDocument;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.sos.event.events.ResultInsertion;
import org.n52.sos.event.events.ResultTemplateInsertion;
import org.n52.sos.event.events.SensorDeletion;
import org.n52.sos.event.events.SensorInsertion;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingObservation;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.request.DeleteSensorRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertResultRequest;
import org.n52.sos.request.InsertResultTemplateRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.request.operator.SosInsertObservationOperatorV20;
import org.n52.sos.response.DeleteSensorResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.response.InsertResultResponse;
import org.n52.sos.response.InsertResultTemplateResponse;
import org.n52.sos.response.InsertSensorResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
public class InsertDAOTest extends HibernateTestCase {
    private static final String OFFERING1 = "offering1";

    private static final String OFFERING2 = "offering2";

    private static final String OFFERING3 = "offering3";

    private static final String PROCEDURE1 = "procedure1";

    private static final String PROCEDURE2 = "procedure2";

    private static final String PROCEDURE3 = "procedure3";

    private static final String OBSPROP1 = "obsprop1";

    private static final String OBSPROP2 = "obsprop2";

    private static final String OBSPROP3 = "obsprop3";

    private static final String FEATURE3 = "feature3";

    private static final String RESULT_TEMPLATE = "result_template";

    private static final DateTime TIME1 = new DateTime("2013-07-18T00:00:00Z");

    private static final DateTime TIME2 = new DateTime("2013-07-18T01:00:00Z");

    private static final DateTime TIME3 = new DateTime("2013-07-18T02:00:00Z");

    private static final DateTime OBS_TIME = new DateTime("2013-07-18T03:00:00Z");

    private static final Double VAL1 = 19.1;

    private static final Double VAL2 = 19.8;

    private static final Double VAL3 = 20.4;

    private static final Double OBS_VAL = 20.8;

    private static final String TOKEN_SEPARATOR = ",";

    private static final String DECIMAL_SEPARATOR = ".";

    private static final String BLOCK_SEPARATOR = "#";

    private static final String TEMP_UNIT = "Cel";

    /* FIXTURES */
    private InsertSensorDAO insertSensorDAO = new InsertSensorDAO();

    private DeleteSensorDAO deleteSensorDAO = new DeleteSensorDAO();

    private InsertObservationDAO insertObservationDAO = new InsertObservationDAO();

    private InsertResultTemplateDAO insertResultTemplateDAO = new InsertResultTemplateDAO();

    private InsertResultDAO insertResultDAO = new InsertResultDAO();

    private GetObservationDAO getObsDAO = new GetObservationDAO();

    private SosInsertObservationOperatorV20 insertObservationOperatorv2 = new SosInsertObservationOperatorV20();

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

    @Before
    public void setUp() throws OwsExceptionReport, ConverterException {
        Session session = getSession();
        insertSensor(PROCEDURE1, OFFERING1, OBSPROP1, null);
        insertSensor(PROCEDURE2, OFFERING2, OBSPROP2, PROCEDURE1);
        insertSensor(PROCEDURE3, OFFERING3, OBSPROP3, PROCEDURE2);
        insertResultTemplate(RESULT_TEMPLATE, PROCEDURE3, OFFERING3, OBSPROP3, FEATURE3, getSession());
        returnSession(session);
    }

    @After
    public void tearDown() throws OwsExceptionReport, InterruptedException {
        H2Configuration.truncate();
    }

    @AfterClass
    public static void cleanUp() {
        H2Configuration.recreate();
        SettingsManager.getInstance().cleanup();
    }

    private void insertSensor(String procedure, String offering, String obsProp, String parentProcedure)
            throws OwsExceptionReport {
        InsertSensorRequest req = new InsertSensorRequest();
        req.setAssignedProcedureIdentifier(procedure);
        List<SosOffering> assignedOfferings = Lists.newLinkedList();
        assignedOfferings.add(new SosOffering(offering, offering));
        req.setObservableProperty(CollectionHelper.list(obsProp));
        req.setProcedureDescriptionFormat(SensorMLConstants.NS_SML);
        SosInsertionMetadata meta = new SosInsertionMetadata();
        meta.setObservationTypes(Sets.newHashSet(OmConstants.OBS_TYPE_MEASUREMENT));
        meta.setFeatureOfInterestTypes(Sets.newHashSet(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT));
        req.setMetadata(meta);
        System system = new System();
        system.setIdentifier(procedure);
        if (parentProcedure != null) {
            system.addParentProcedure(parentProcedure);
            for (String hierarchyParentProc : getCache().getParentProcedures(parentProcedure, true, true)) {
                for (String parentProcOffering : getCache().getOfferingsForProcedure(hierarchyParentProc)) {
                    SosOffering sosOffering = new SosOffering(parentProcOffering, parentProcOffering);
                    sosOffering.setParentOfferingFlag(true);
                    assignedOfferings.add(sosOffering);
                }
            }
        }
        SystemDocument xbSystemDoc = SystemDocument.Factory.newInstance();
        xbSystemDoc.addNewSystem().set(CodingHelper.encodeObjectToXml(SensorMLConstants.NS_SML, system));
        system.setSensorDescriptionXmlString(xbSystemDoc.xmlText());
        req.setProcedureDescription(system);
        req.setAssignedOfferings(assignedOfferings);
        InsertSensorResponse resp = insertSensorDAO.insertSensor(req);
        SosEventBus.fire(new SensorInsertion(req, resp));
    }

    @SuppressWarnings("unused")
    private void deleteSensor(String procedure) throws OwsExceptionReport {
        DeleteSensorRequest req = new DeleteSensorRequest();
        req.setProcedureIdentifier(procedure);
        DeleteSensorResponse resp = deleteSensorDAO.deleteSensor(req);
        SosEventBus.fire(new SensorDeletion(req, resp));
    }

    private void insertResultTemplate(String identifier, String procedureId, String offeringId, String obsPropId,
            String featureId, Session session) throws OwsExceptionReport, ConverterException {
        InsertResultTemplateRequest req = new InsertResultTemplateRequest();
        req.setIdentifier(identifier);
        req.setObservationTemplate(getOmObsConst(procedureId, obsPropId, TEMP_UNIT, offeringId, featureId,
                OmConstants.OBS_TYPE_MEASUREMENT, session));

        SweTextEncoding textEncoding = new SweTextEncoding();
        textEncoding.setCollapseWhiteSpaces(false);
        textEncoding.setDecimalSeparator(DECIMAL_SEPARATOR);
        textEncoding.setTokenSeparator(TOKEN_SEPARATOR);
        textEncoding.setBlockSeparator(BLOCK_SEPARATOR);
        SosResultEncoding resultEncoding = new SosResultEncoding();
        resultEncoding.setEncoding(textEncoding);
        TextEncodingDocument xbTextEncDoc = TextEncodingDocument.Factory.newInstance();
        xbTextEncDoc.addNewTextEncoding().set(CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, textEncoding));
        resultEncoding.getEncoding().setXml(xbTextEncDoc.xmlText());
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
        SosResultStructure resultStructure = new SosResultStructure();
        resultStructure.setResultStructure(dataRecord);
        DataRecordDocument xbDataRecordDoc = DataRecordDocument.Factory.newInstance();
        xbDataRecordDoc.addNewDataRecord().set(CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, dataRecord));
        resultStructure.getResultStructure().setXml(xbDataRecordDoc.xmlText());
        req.setResultStructure(resultStructure);
        InsertResultTemplateResponse resp = insertResultTemplateDAO.insertResultTemplate(req);
        SosEventBus.fire(new ResultTemplateInsertion(req, resp));
    }

    private ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    private void updateCache() throws OwsExceptionReport {
        Configurator.getInstance().getCacheController().update();
    }

    private OmObservationConstellation getOmObsConst(String procedureId, String obsPropId, String unit,
            String offeringId, String featureId, String obsType, Session session) throws OwsExceptionReport,
            ConverterException {
        OmObservationConstellation obsConst = new OmObservationConstellation();
        Procedure procedure = new ProcedureDAO().getProcedureForIdentifier(procedureId, session);
        SosProcedureDescription spd =
                new HibernateProcedureConverter().createSosProcedureDescription(procedure, SensorMLConstants.NS_SML,
                        Sos2Constants.SERVICEVERSION, session);
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

    private void assertInsertionAftermathBeforeAndAfterCacheReload() throws OwsExceptionReport, InterruptedException {
        // check once for cache changes triggered by sos event
        assertInsertionAftermath();

        // run a cache update
        updateCache();

        // check again after cache is reloaded
        assertInsertionAftermath();
    }

    private void assertInsertionAftermath() throws OwsExceptionReport {
        // check observation types
//        assertThat(getCache().getObservationTypesForOffering(OFFERING1), contains(OmConstants.OBS_TYPE_MEASUREMENT));
//        assertThat(getCache().getObservationTypesForOffering(OFFERING2), contains(OmConstants.OBS_TYPE_MEASUREMENT));
        assertThat(getCache().getObservationTypesForOffering(OFFERING3), contains(OmConstants.OBS_TYPE_MEASUREMENT));

        // check offerings for procedure
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE1), contains(OFFERING1));
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE2), containsInAnyOrder(OFFERING1, OFFERING2));
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE3),
                containsInAnyOrder(OFFERING1, OFFERING2, OFFERING3));

        // check procedures and hidden child procedures for offering
        assertThat(getCache().getProceduresForOffering(OFFERING1), contains(PROCEDURE1));
        assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING1),
                containsInAnyOrder(PROCEDURE2, PROCEDURE3));

        assertThat(getCache().getProceduresForOffering(OFFERING2), contains(PROCEDURE2));
        assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING2), contains(PROCEDURE3));

        assertThat(getCache().getProceduresForOffering(OFFERING3), contains(PROCEDURE3));
        assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING3), empty());

        // check allowed observation types for offering
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING1),
                contains(OmConstants.OBS_TYPE_MEASUREMENT));
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING2),
                contains(OmConstants.OBS_TYPE_MEASUREMENT));
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_MEASUREMENT));

        // check parent procedures
        assertThat(getCache().getParentProcedures(PROCEDURE1, true, false), empty());
        assertThat(getCache().getParentProcedures(PROCEDURE2, true, false), contains(PROCEDURE1));
        assertThat(getCache().getParentProcedures(PROCEDURE3, true, false), containsInAnyOrder(PROCEDURE1, PROCEDURE2));

        // check child procedures
        assertThat(getCache().getChildProcedures(PROCEDURE1, true, false), containsInAnyOrder(PROCEDURE2, PROCEDURE3));
        assertThat(getCache().getChildProcedures(PROCEDURE2, true, false), contains(PROCEDURE3));
        assertThat(getCache().getChildProcedures(PROCEDURE3, true, false), empty());

        // check features of interest for offering
        // TODO add geometries to features, check bounds, etc
        // TODO investigate these, getting guid back instead of assigned
        // identifier
        // assertThat(getCache().getFeaturesOfInterestForOffering(OFFERING1),
        // contains(FEATURE3));
        // assertThat(getCache().getFeaturesOfInterestForOffering(OFFERING2),
        // contains(FEATURE3));
        // assertThat(getCache().getFeaturesOfInterestForOffering(OFFERING3),
        // contains(FEATURE3));

        // check obsprops for offering
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING1),
                containsInAnyOrder(OBSPROP1, OBSPROP2, OBSPROP3));
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING2), containsInAnyOrder(OBSPROP2, OBSPROP3));
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING3), contains(OBSPROP3));

        // check offering for obsprops
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP1), contains(OFFERING1));
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP2), containsInAnyOrder(OFFERING1, OFFERING2));
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP3),
                containsInAnyOrder(OFFERING1, OFFERING2, OFFERING3));

        // check obsprops for procedure
        // TODO child procedure obsprops are not currently set for parents.
        // should they be?
        // assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE1),
        // containsInAnyOrder(OBSPROP1, OBSPROP2, OBSPROP3));
        // assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE2),
        // containsInAnyOrder(OBSPROP2, OBSPROP3));
        assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE3), contains(OBSPROP3));

        // check procedures for obsprop
        // TODO child procedure obsprops are not currently set for parents.
        // should they be?
        assertThat(getCache().getProceduresForObservableProperty(OBSPROP1), contains(PROCEDURE1));
        // assertThat(getCache().getProceduresForObservableProperty(OBSPROP2),
        // containsInAnyOrder(PROCEDURE1, PROCEDURE2));
        // assertThat(getCache().getProceduresForObservableProperty(OBSPROP3),
        // containsInAnyOrder(PROCEDURE1, PROCEDURE2, PROCEDURE3));

        // check procedures for feature
        // TODO child procedure features are not currently set for parents.
        // should they be?
        // assertThat(getCache().getProceduresForFeatureOfInterest(FEATURE3),
        // containsInAnyOrder(PROCEDURE1, PROCEDURE2, PROCEDURE3));
    }

    @Test
    public void testCacheContents() throws OwsExceptionReport {
        assertThat(getCache().getProcedures(), containsInAnyOrder(PROCEDURE1, PROCEDURE2, PROCEDURE3));
        assertThat(getCache().getOfferings(), containsInAnyOrder(OFFERING1, OFFERING2, OFFERING3));
        assertThat(getCache().getObservableProperties(), containsInAnyOrder(OBSPROP1, OBSPROP2, OBSPROP3));
        assertThat(getCache().getParentProcedures(PROCEDURE3, true, false), containsInAnyOrder(PROCEDURE1, PROCEDURE2));
        assertThat(getCache().getResultTemplatesForOffering(OFFERING3).size(), is(1));
    }

    @Test
    public void testInsertObservation() throws OwsExceptionReport, InterruptedException, ConverterException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        OmObservation obs = new OmObservation();

        Session session = getSession();
        obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                OmConstants.OBS_TYPE_MEASUREMENT, session));
        returnSession(session);

        obs.setResultTime(new TimeInstant(OBS_TIME));
        SingleObservationValue<Double> obsVal = new SingleObservationValue<Double>();
        obsVal.setPhenomenonTime(new TimeInstant(OBS_TIME));
        obsVal.setValue(new QuantityValue(Double.valueOf(OBS_VAL), TEMP_UNIT));
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        InsertObservationResponse resp = insertObservationDAO.insertObservation(req);
        SosEventBus.fire(new ObservationInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();

        // TODO requests for the parent procedures fail?
        // checkObservation(OFFERING1, PROCEDURE1, OBSPROP3, OBS_TIME,
        // PROCEDURE3, OBSPROP3, FEATURE3,
        // OBS_VAL, TEMP_UNIT);
        // checkObservation(OFFERING2, PROCEDURE2, OBSPROP3, OBS_TIME,
        // PROCEDURE3, OBSPROP3, FEATURE3,
        // OBS_VAL, TEMP_UNIT);
        checkObservation(OFFERING1, PROCEDURE3, OBSPROP3, OBS_TIME, PROCEDURE3, OBSPROP3, FEATURE3, OBS_VAL, TEMP_UNIT);
        checkObservation(OFFERING2, PROCEDURE3, OBSPROP3, OBS_TIME, PROCEDURE3, OBSPROP3, FEATURE3, OBS_VAL, TEMP_UNIT);
        checkObservation(OFFERING3, PROCEDURE3, OBSPROP3, OBS_TIME, PROCEDURE3, OBSPROP3, FEATURE3, OBS_VAL, TEMP_UNIT);
    }

    /**
     * Check results of an InsertObservation request with
     * SplitDataArrayIntoObservations
     */
    // TODO should this test live in another module, since it involves
    // transactional-v20?
    // however, it also tests functionality.
    @Test
    public void testInsertObservationWithSplit() throws OwsExceptionReport, InterruptedException, ConverterException {
        InsertObservationRequest req = new InsertObservationRequest();
        req.setService(SosConstants.SOS);
        req.setVersion(Sos2Constants.SERVICEVERSION);

        SwesExtension<SweBoolean> splitExt = new SwesExtensionImpl<SweBoolean>();
        splitExt.setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name());
        splitExt.setValue(new SweBoolean().setValue(Boolean.TRUE));
        SwesExtensions swesExtensions = new SwesExtensions();
        swesExtensions.addSwesExtension(splitExt);
        req.setExtensions(swesExtensions);

        req.setAssignedSensorId(PROCEDURE3);
        req.setOfferings(Lists.newArrayList(OFFERING3));
        OmObservation obs = new OmObservation();

        Session session = getSession();
        obs.setObservationConstellation(getOmObsConst(PROCEDURE3, OBSPROP3, TEMP_UNIT, OFFERING3, FEATURE3,
                OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION, session));
        returnSession(session);

        obs.setResultTime(new TimeInstant(null, TimeIndeterminateValue.template));
        SweDataArrayValue sweDataArrayValue = new SweDataArrayValue();
        SweDataArray sweDataArray = new SweDataArray();
        sweDataArray.setElementCount(new SweCount().setValue(3));
        SweDataRecord sweDataRecord = new SweDataRecord();

        SweTime time = new SweTime();
        time.setDefinition(OmConstants.PHENOMENON_TIME);
        time.setUom(OmConstants.PHEN_UOM_ISO8601);
        SweField timeField = new SweField(OmConstants.PHENOMENON_TIME_NAME, time);
        sweDataRecord.addField(timeField);

        SweQuantity temp = new SweQuantity();
        temp.setDefinition(OBSPROP3);
        temp.setUom(TEMP_UNIT);
        SweField tempField = new SweField(OmConstants.EN_OBSERVED_PROPERTY, temp);
        sweDataRecord.addField(tempField);

        sweDataArray.setElementType(sweDataRecord);

        SweTextEncoding sweTextEncoding = new SweTextEncoding();
        sweTextEncoding.setBlockSeparator("#");
        sweTextEncoding.setDecimalSeparator(".");
        sweTextEncoding.setTokenSeparator("@");
        sweDataArray.setEncoding(sweTextEncoding);

        // add values
        sweDataArray.add(CollectionHelper.list(TIME1.toString(), Double.toString(VAL1)));
        sweDataArray.add(CollectionHelper.list(TIME2.toString(), Double.toString(VAL2)));
        sweDataArray.add(CollectionHelper.list(TIME3.toString(), Double.toString(VAL3)));

        sweDataArrayValue.setValue(sweDataArray);

        SingleObservationValue<SweDataArray> obsVal = new SingleObservationValue<SweDataArray>();
        obsVal.setPhenomenonTime(new TimeInstant(null, TimeIndeterminateValue.template));
        obsVal.setValue(sweDataArrayValue);
        obs.setValue(obsVal);
        req.setObservation(Lists.newArrayList(obs));
        insertObservationOperatorv2.receive(req);
        assertInsertionAftermathBeforeAndAfterCacheReload();

        checkObservation(OFFERING3, PROCEDURE3, OBSPROP3, TIME1, PROCEDURE3, OBSPROP3, FEATURE3, VAL1, TEMP_UNIT);
        checkObservation(OFFERING3, PROCEDURE3, OBSPROP3, TIME2, PROCEDURE3, OBSPROP3, FEATURE3, VAL2, TEMP_UNIT);
        checkObservation(OFFERING3, PROCEDURE3, OBSPROP3, TIME3, PROCEDURE3, OBSPROP3, FEATURE3, VAL3, TEMP_UNIT);
    }

    @Test
    public void testInsertResult() throws OwsExceptionReport, InterruptedException {
        InsertResultRequest req = new InsertResultRequest();
        req.setTemplateIdentifier(RESULT_TEMPLATE);
        req.setResultValues(makeResultValueString(CollectionHelper.list(TIME1, TIME2, TIME3),
                CollectionHelper.list(VAL1, VAL2, VAL3)));
        InsertResultResponse resp = insertResultDAO.insertResult(req);
        SosEventBus.fire(new ResultInsertion(req, resp));
        assertInsertionAftermathBeforeAndAfterCacheReload();

        checkObservation(OFFERING1, PROCEDURE3, OBSPROP3, TIME1, PROCEDURE3, OBSPROP3, FEATURE3, VAL1, TEMP_UNIT);
        checkObservation(OFFERING1, PROCEDURE3, OBSPROP3, TIME2, PROCEDURE3, OBSPROP3, FEATURE3, VAL2, TEMP_UNIT);
        checkObservation(OFFERING1, PROCEDURE3, OBSPROP3, TIME3, PROCEDURE3, OBSPROP3, FEATURE3, VAL3, TEMP_UNIT);
    }

    private void checkObservation(String reqOffering, String reqProcedure, String reqObsProp, DateTime time,
            String obsProcedure, String obsObsProp, String obsFeature, Double obsVal, String obsUnit)
            throws OwsExceptionReport {
        GetObservationRequest getObsReq = new GetObservationRequest();
        getObsReq.setOfferings(CollectionHelper.list(reqOffering));
        getObsReq.setProcedures(CollectionHelper.list(reqProcedure));
        getObsReq.setObservedProperties(CollectionHelper.list(reqObsProp));
        getObsReq.setResponseFormat(OmConstants.NS_OM_2);
        TemporalFilter tempFilter =
                new TemporalFilter(FilterConstants.TimeOperator.TM_Equals, new TimeInstant(time),
                        TemporalRestrictions.PHENOMENON_TIME_VALUE_REFERENCE);
        getObsReq.setTemporalFilters(CollectionHelper.list(tempFilter));
        getObsReq.setService(SosConstants.SOS);
        getObsReq.setVersion(Sos2Constants.SERVICEVERSION);
        GetObservationResponse getObsResponse = getObsDAO.getObservation(getObsReq);
        assertThat(getObsResponse, notNullValue());
        assertThat(getObsResponse.getObservationCollection().isEmpty(), is(false));
        OmObservation omObservation = getObsResponse.getObservationCollection().get(0);
        if (omObservation.getValue() instanceof StreamingObservation) {
            assertThat(((StreamingObservation)omObservation.getValue()).hasNextValue(), is(true));
            omObservation = ((StreamingObservation)omObservation.getValue()).nextSingleObservation();
            assertThat(omObservation.getObservationConstellation(), notNullValue());
            OmObservationConstellation obsConst = omObservation.getObservationConstellation();
            assertThat(obsConst.getProcedure().getIdentifier(), is(obsProcedure));
            assertThat(obsConst.getObservableProperty().getIdentifier(), is(obsObsProp));

            // TODO this fails
            // assertThat(obsConst.getFeatureOfInterest().getIdentifier().getValue(),
            // is(obsFeature));

            assertThat(omObservation.getValue(), notNullValue());
            ObservationValue<?> value = omObservation.getValue();
            assertThat(value.getValue(), instanceOf(QuantityValue.class));
            assertThat(value.getPhenomenonTime(), instanceOf(TimeInstant.class));
            TimeInstant timeInstant = (TimeInstant) value.getPhenomenonTime();
            assertThat(timeInstant.getValue().toDate(), is(time.toDate()));
            QuantityValue quantityValue = (QuantityValue) value.getValue();
            assertThat(quantityValue.getValue().doubleValue(), is(obsVal));
            assertThat(quantityValue.getUnit(), is(obsUnit));
            // TODO
        } else if (omObservation.getValue() instanceof StreamingValue) {
            assertThat(((StreamingValue)omObservation.getValue()).hasNextValue(), is(true));
            omObservation = ((StreamingValue)omObservation.getValue()).nextSingleObservation();
            assertThat(omObservation.getObservationConstellation(), notNullValue());
            OmObservationConstellation obsConst = omObservation.getObservationConstellation();
            assertThat(obsConst.getProcedure().getIdentifier(), is(obsProcedure));
            assertThat(obsConst.getObservableProperty().getIdentifier(), is(obsObsProp));

            // TODO this fails
            // assertThat(obsConst.getFeatureOfInterest().getIdentifier().getValue(),
            // is(obsFeature));

            assertThat(omObservation.getValue(), notNullValue());
            ObservationValue<?> value = omObservation.getValue();
            assertThat(value.getValue(), instanceOf(QuantityValue.class));
            assertThat(value.getPhenomenonTime(), instanceOf(TimeInstant.class));
            TimeInstant timeInstant = (TimeInstant) value.getPhenomenonTime();
            assertThat(timeInstant.getValue().toDate(), is(time.toDate()));
            QuantityValue quantityValue = (QuantityValue) value.getValue();
            assertThat(quantityValue.getValue().doubleValue(), is(obsVal));
            assertThat(quantityValue.getUnit(), is(obsUnit));
        } else {
            assertThat(omObservation.getObservationConstellation(), notNullValue());
            OmObservationConstellation obsConst = omObservation.getObservationConstellation();
            assertThat(obsConst.getProcedure().getIdentifier(), is(obsProcedure));
            assertThat(obsConst.getObservableProperty().getIdentifier(), is(obsObsProp));

            // TODO this fails
            // assertThat(obsConst.getFeatureOfInterest().getIdentifier().getValue(),
            // is(obsFeature));

            assertThat(omObservation.getValue(), notNullValue());
            ObservationValue<?> value = omObservation.getValue();
            assertThat(value.getValue(), instanceOf(QuantityValue.class));
            assertThat(value.getPhenomenonTime(), instanceOf(TimeInstant.class));
            TimeInstant timeInstant = (TimeInstant) value.getPhenomenonTime();
            assertThat(timeInstant.getValue().toDate(), is(time.toDate()));
            QuantityValue quantityValue = (QuantityValue) value.getValue();
            assertThat(quantityValue.getValue().doubleValue(), is(obsVal));
            assertThat(quantityValue.getUnit(), is(obsUnit));
        }

        
    }
}
