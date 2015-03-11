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
package org.n52.sos.ds.datasource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.sos.ds.hibernate.entities.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.CountObservation;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.NumericObservation;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.ds.hibernate.entities.TFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.TObservableProperty;
import org.n52.sos.ds.hibernate.entities.TOffering;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.TextObservation;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.Constants;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * @since 4.0.0
 * 
 */
@Deprecated
// TODO remove or move to an implementation of HibernateTestDataManager
public class HibernateTestDataHandler {
    private static final String[] OFFERING_IDS = new String[] { "http://www.52north.org/test/offering/1",
            "http://www.52north.org/test/offering/2", "http://www.52north.org/test/offering/3",
            "http://www.52north.org/test/offering/4", "http://www.52north.org/test/offering/5",
            "http://www.52north.org/test/offering/6", "http://www.52north.org/test/offering/7",
            "http://www.52north.org/test/offering/8" };

    private static final String[] PROCEDURE_IDS = new String[] { "http://www.52north.org/test/procedure/1",
            "http://www.52north.org/test/procedure/2", "http://www.52north.org/test/procedure/3",
            "http://www.52north.org/test/procedure/4", "http://www.52north.org/test/procedure/5",
            "http://www.52north.org/test/procedure/6", "http://www.52north.org/test/procedure/7",
            "http://www.52north.org/test/procedure/8" };

    private static final String[] OBSERVABLE_PROPERTY_IDS = new String[] {
            "http://www.52north.org/test/observableProperty/1", "http://www.52north.org/test/observableProperty/2",
            "http://www.52north.org/test/observableProperty/3", "http://www.52north.org/test/observableProperty/4",
            "http://www.52north.org/test/observableProperty/5", "http://www.52north.org/test/observableProperty/6",
            "http://www.52north.org/test/observableProperty/7", "http://www.52north.org/test/observableProperty/8" };

    private static final String[] FEATURE_IDS = new String[] { "http://www.52north.org/test/featureOfInterest/1",
            "http://www.52north.org/test/featureOfInterest/2", "http://www.52north.org/test/featureOfInterest/3",
            "http://www.52north.org/test/featureOfInterest/4", "http://www.52north.org/test/featureOfInterest/5",
            "http://www.52north.org/test/featureOfInterest/6", "http://www.52north.org/test/featureOfInterest/7",
            "http://www.52north.org/test/featureOfInterest/8" };

    private static final String[] UNIT_IDS = new String[] { "test_unit_1", "test_unit_2", "test_unit_3",
            "test_unit_4", "test_unit_5", "test_unit_6", "test_unit_7", "test_unit_8" };

    private List<ObservationType> observationTypes;

    private List<FeatureOfInterestType> featureOfInterestTypes;

    private List<ProcedureDescriptionFormat> procedureDescriptionFormats;

    private List<TOffering> offerings;

    private List<TFeatureOfInterest> features;

    private List<Unit> units;

    private List<TObservableProperty> observableProperties;

    private List<TProcedure> procedures;

    private Session session;

    private HibernateTestDataHandler(Session session) {
        observationTypes = new ArrayList<ObservationType>();
        featureOfInterestTypes = new ArrayList<FeatureOfInterestType>();
        procedureDescriptionFormats = new ArrayList<ProcedureDescriptionFormat>();
        offerings = new ArrayList<TOffering>();
        features = new ArrayList<TFeatureOfInterest>();
        units = new ArrayList<Unit>();
        observableProperties = new ArrayList<TObservableProperty>();
        procedures = new ArrayList<TProcedure>();

        this.session = session;
    }

    public static boolean hasTestData() {
        Set<String> procedures = Configurator.getInstance().getCache().getProcedures();
        for (String procedureId : procedures) {
            if (procedureId.equals(PROCEDURE_IDS[0])) {
                return true;
            }
        }

        return false;
    }

    public static void insertTestData(Session session) throws Exception {
        HibernateTestDataHandler handler = new HibernateTestDataHandler(session);
        handler.insertTestData();
    }

    private void insertTestData() throws Exception {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            createObservationTypes();
            createFeatureTypes();
            createProcedureDescriptionFormats();
            createOfferings();
            createFeatures();
            createUnits();
            createObservableProperties();
            createProcedures();
            createValidProcedureTimes();
            createObservationConstellations();
            createNumericObservations(0);
            createCountObservations();
            createBooleanObservations();
            createCategoryObservations();
            createTextObservations();
            createResultTemplates();
            createSpecialObservations();
            createNumericObservations(6);
            createNumericObservations(7);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private void createObservationTypes() {
        String[] types = new String[] { "OM_CountObservation",
                "OM_Measurement",
                "OM_SWEArrayObservation",
                "OM_TruthObservation",
                "OM_CategoryObservation",
                "OM_TextObservation" };
        String prefix = "http://www.opengis.net/def/observationType/OGC-OM/2.0/";

        for (String type : types) {
            ObservationType observationType = new ObservationType(prefix + type);

            session.save(observationType);
            observationTypes.add(observationType);
        }
    }

    private void createFeatureTypes() {
        String[] types =
                new String[] { "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve",
                        "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingSurface",
                        "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint",
                        "http://www.opengis.net/def/nil/OGC/0/unknown" };

        for (String type : types) {
            FeatureOfInterestType featureType = new FeatureOfInterestType();
            featureType.setFeatureOfInterestType(type);

            session.save(featureType);
            featureOfInterestTypes.add(featureType);
        }
    }

    private void createProcedureDescriptionFormats() {
        String format = "http://www.opengis.net/sensorML/1.0.1";
        ProcedureDescriptionFormat procedureDescriptionFormat = new ProcedureDescriptionFormat();
        procedureDescriptionFormat.setProcedureDescriptionFormat(format);

        session.save(procedureDescriptionFormat);
        procedureDescriptionFormats.add(procedureDescriptionFormat);
    }

    private void createOfferings() {
        String[] observationTypeIds =
                new String[] { "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_SWEArrayObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement", };

        // Same feature id for all offerings
        String featureTypeId = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint";

        for (int i = 0; i < OFFERING_IDS.length; i++) {
            TOffering offering = new TOffering();
            offering.setIdentifier(OFFERING_IDS[i]);
            offering.setName(OFFERING_IDS[i]);
            offering.getFeatureOfInterestTypes().add(getFeatureOfInterestType(featureTypeId));
            offering.getObservationTypes().add(getObservationType(observationTypeIds[i]));

            session.save(offering);
            offerings.add(offering);
        }
    }

    private void createFeatures() {
        GeometryFactory gf = new GeometryFactory();

        // Same type for all features
        String type = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint";

        Point[] points =
                new Point[] { gf.createPoint(new Coordinate(7.727958, 51.883906)),
                        gf.createPoint(new Coordinate(-117.1957110000000, 34.056517)),
                        gf.createPoint(new Coordinate(6.1320144042060925, 50.78570661296184)),
                        gf.createPoint(new Coordinate(7.593655600000034, 51.9681661)),
                        gf.createPoint(new Coordinate(13.72375999999997, 51.02881)),
                        gf.createPoint(new Coordinate(7.270806, 51.447722)),
                        gf.createPoint(new Coordinate(4.283393599999954, 52.0464393)),
                        gf.createPoint(new Coordinate(10.94306000000006, 50.68606)) };

        for (Point point : points) {
            point.setSRID(Constants.EPSG_WGS84);
        }

        String[] names =
                new String[] { "con terra", "ESRI", "Kisters", "IfGI", "TU-Dresden", "Hochschule Bochum", "ITC",
                        "DLZ-IT" };

        for (int i = 0; i < FEATURE_IDS.length; i++) {
            TFeatureOfInterest feature = new TFeatureOfInterest();

            feature.setName(names[i]);
            feature.setGeom(points[i]);
            feature.setIdentifier(FEATURE_IDS[i]);
            feature.setFeatureOfInterestType(getFeatureOfInterestType(type));

            feature.setDescriptionXml("<sams:SF_SpatialSamplingFeature "
                    + "xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                    + "xmlns:sams=\"http://www.opengis.net/samplingSpatial/2.0\" "
                    + "xmlns:sf=\"http://www.opengis.net/sampling/2.0\" "
                    + "xmlns:gml=\"http://www.opengis.net/gml/3.2\" gml:id=\"ssf_"
                    + FEATURE_IDS[i]
                    + "\"> "
                    + "<gml:identifier codeSpace=\"\">"
                    + FEATURE_IDS[i]
                    + "</gml:identifier>"
                    + "<gml:name>"
                    + names[i]
                    + " </gml:name>"
                    + "<sf:type xlink:href=\"http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint\"/>"
                    + "<sf:sampledFeature xlink:href=\"http://www.opengis.net/def/nil/OGC/0/unknown\"/>"
                    + "<sams:shape>"
                    + "<gml:Point gml:id=\"pSsf_"
                    + FEATURE_IDS[i]
                    + "\">"
                    + "<gml:pos srsName=\"http://www.opengis.net/def/crs/EPSG/0/Constants.EPSG_WGS84\">"
                    + points[i].getY()
                    + " "
                    + points[i].getX()
                    + "</gml:pos>"
                    + "</gml:Point>"
                    + "</sams:shape>"
                    + "</sams:SF_SpatialSamplingFeature>");

            session.save(feature);
            features.add(feature);
        }
    }

    private void createUnits() {
        for (String id : UNIT_IDS) {
            Unit unit = new Unit();
            unit.setUnit(id);

            session.save(unit);
            units.add(unit);
        }
    }

    private void createObservableProperties() {
        for (String id : OBSERVABLE_PROPERTY_IDS) {
            TObservableProperty observableProperty = new TObservableProperty();
            observableProperty.setDescription(id);
            observableProperty.setIdentifier(id);

            session.save(observableProperty);
            observableProperties.add(observableProperty);
        }
    }

    private void createProcedures() {
        // Same format for all procedures
        String formatId = "http://www.opengis.net/sensorML/1.0.1";

        for (int i = 0; i < PROCEDURE_IDS.length; i++) {
            TProcedure procedure = new TProcedure();
            ProcedureDescriptionFormat procDescFormat = getProcedureDescriptionFormat(formatId);
            procedure.setProcedureDescriptionFormat(procDescFormat);
            procedure.setIdentifier(PROCEDURE_IDS[i]);

            session.save(procedure);
            procedures.add(procedure);
        }
    }

    private void createValidProcedureTimes() {
        // Same start date for all valid procedure times
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDate = null;
        try {
            startDate = dateFormat.parse("2012-11-19 13:00");
        } catch (ParseException e) {
            // this should never happen
        }

        double[] x =
                new double[] { 7.727958, -117.195711, 6.1320144042060925, 7.593655600000034, 13.72375999999997,
                        7.270806, 4.283393599999954, 10.94306000000006 };
        double[] y =
                new double[] { 51.883906, 34.056517, 50.78570661296184, 51.9681661, 51.02881, 51.447722, 52.0464393,
                        50.68606 };
        String[] longNames =
                new String[] {
                        "con terra GmbH (www.conterra.de)",
                        "ESRI (www.esri.com)",
                        "Kisters AG (www.kisters.de)",
                        "Institute for Geoinformatics (http://ifgi.uni-muenster.de/en)",
                        "Technical University Dresden (http://tu-dresden.de/en)",
                        "Hochschule Bochum - Bochum University of Applied Sciences (http://www.hochschule-bochum.de/en/)",
                        "ITC - University of Twente (http://www.itc.nl/)",
                        "Bundesanstalt für IT-Dienstleistungen im Geschäftsbereich des BMVBS (http://www.dlz-it.de)" };
        String[] shortNames =
                new String[] { "con terra", "ESRI", "Kisters", "IfGI", "TU-Dresden", "Hochschule Bochum", "ITC",
                        "DLZ-IT" };

        for (int i = 0; i < PROCEDURE_IDS.length; i++) {
            ValidProcedureTime validProcedureTime = new ValidProcedureTime();
            validProcedureTime.setProcedure(getProcedure(PROCEDURE_IDS[i]));
            validProcedureTime.setStartTime(startDate);
            validProcedureTime.setDescriptionXml(createSensorDescription(PROCEDURE_IDS[i], OBSERVABLE_PROPERTY_IDS[i],
                    x[i], y[i], 0.0, longNames[i], shortNames[i]));

            session.save(validProcedureTime);
        }
    }

    private void createObservationConstellations() {
        String[] observationTypeIds =
                new String[] { "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_SWEArrayObservation",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                        "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement" };

        for (int i = 0; i < PROCEDURE_IDS.length; i++) {
            ObservationConstellation oc = new ObservationConstellation();
            oc.setProcedure(getProcedure(PROCEDURE_IDS[i]));
            oc.setObservableProperty(getObservableProperty(OBSERVABLE_PROPERTY_IDS[i]));
            oc.setOffering(getOffering(OFFERING_IDS[i]));
            oc.setObservationType(getObservationType(observationTypeIds[i]));

            session.save(oc);
        }
    }

    private <T extends Observation> T setObservation(T observation, String procedure, String observableProperty,
            String feature, String unit, String offering, Date date) {
        observation.setProcedure(getProcedure(procedure));
        observation.setObservableProperty(getObservableProperty(observableProperty));
        observation.setFeatureOfInterest(getFeatureOfInterest(feature));
        observation.setUnit(getUnit(unit));
        observation.getOfferings().add(getOffering(offering));

        observation.setPhenomenonTimeStart(date);
        observation.setPhenomenonTimeEnd(date);
        observation.setResultTime(date);

        return observation;
    }

    private void createNumericObservations(int identifierIndex) {
        double[] values = new double[] { 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1 };

        GregorianCalendar calendar = new GregorianCalendar(2012, 11, 19, 13, 00);
        for (int i = 0; i < values.length; i++) {
            NumericObservation observation = new NumericObservation();
            setObservation(observation, PROCEDURE_IDS[identifierIndex], OBSERVABLE_PROPERTY_IDS[identifierIndex],
                    FEATURE_IDS[identifierIndex], UNIT_IDS[identifierIndex], OFFERING_IDS[identifierIndex],
                    calendar.getTime());
            calendar.add(Calendar.MINUTE, 1);
            observation.setValue(values[i]);

            session.save(observation);
        }
    }

    private void createCountObservations() {
        Integer[] values = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        GregorianCalendar calendar = new GregorianCalendar(2012, 11, 19, 13, 00);
        for (int i = 0; i < values.length; i++) {
            CountObservation observation = new CountObservation();
            setObservation(observation, PROCEDURE_IDS[1], OBSERVABLE_PROPERTY_IDS[1], FEATURE_IDS[1], UNIT_IDS[1],
                    OFFERING_IDS[1], calendar.getTime());
            calendar.add(Calendar.MINUTE, 1);
            observation.setValue(values[i]);

            session.save(observation);
        }
    }

    private void createBooleanObservations() {
        boolean[] values = new boolean[] { true, false, false, true, false, true, true, false, false, true };

        GregorianCalendar calendar = new GregorianCalendar(2012, 11, 19, 13, 00);
        for (int i = 0; i < values.length; i++) {
            BooleanObservation observation = new BooleanObservation();
            setObservation(observation, PROCEDURE_IDS[2], OBSERVABLE_PROPERTY_IDS[2], FEATURE_IDS[2], UNIT_IDS[2],
                    OFFERING_IDS[2], calendar.getTime());
            calendar.add(Calendar.MINUTE, 1);
            observation.setValue(values[i]);

            session.save(observation);
        }
    }

    private void createCategoryObservations() {
        String[] values =
                new String[] { "test_category_1", "test_category_2", "test_category_1", "test_category_5",
                        "test_category_4", "test_category_3", "test_category_1", "test_category_2", "test_category_1",
                        "test_category_6" };

        GregorianCalendar calendar = new GregorianCalendar(2012, 11, 19, 13, 00);
        for (int i = 0; i < values.length; i++) {
            CategoryObservation observation = new CategoryObservation();
            setObservation(observation, PROCEDURE_IDS[3], OBSERVABLE_PROPERTY_IDS[3], FEATURE_IDS[3], UNIT_IDS[3],
                    OFFERING_IDS[3], calendar.getTime());
            calendar.add(Calendar.MINUTE, 1);
            observation.setValue(values[i]);

            session.save(observation);
        }
    }

    private void createTextObservations() {
        String[] values =
                new String[] { "test_text_0", "test_text_1", "test_text_2", "test_text_3", "test_text_4",
                        "test_text_5", "test_text_6", "test_text_7", "test_text_8", "test_text_10" };

        GregorianCalendar calendar = new GregorianCalendar(2012, 11, 19, 13, 00);
        for (int i = 0; i < values.length; i++) {
            TextObservation observation = new TextObservation();
            setObservation(observation, PROCEDURE_IDS[0], OBSERVABLE_PROPERTY_IDS[4], FEATURE_IDS[4], UNIT_IDS[4],
                    OFFERING_IDS[4], calendar.getTime());
            calendar.add(Calendar.MINUTE, 1);
            observation.setValue(values[i]);

            session.save(observation);
        }
    }

    private void createResultTemplates() {

        ResultTemplate template = new ResultTemplate();
        template.setProcedure(getProcedure(PROCEDURE_IDS[5]));
        template.setObservableProperty(getObservableProperty(OBSERVABLE_PROPERTY_IDS[5]));
        template.setOffering(getOffering(OFFERING_IDS[5]));
        template.setFeatureOfInterest(getFeatureOfInterest(FEATURE_IDS[5]));
        template.setIdentifier(PROCEDURE_IDS[5] + "/template/1");
        template.setResultStructure("<swe:DataRecord xmlns:swe=\"http://www.opengis.net/swe/2.0\" "
                + "xmlns:xlink=\"http://www.w3.org/1999/xlink\">" + "<swe:field name=\"phenomenonTime\">"
                + "<swe:Time definition=\"http://www.opengis.net/def/property/OGC/0/PhenomenonTime\">"
                + "<swe:uom xlink:href=\"http://www.opengis.net/def/uom/ISO-8601/0/Gregorian\"/>" + "</swe:Time>"
                + "</swe:field>" + "<swe:field name=\"" + OBSERVABLE_PROPERTY_IDS[5] + "\">"
                + "<swe:Quantity definition=\"" + OBSERVABLE_PROPERTY_IDS[5] + "\">"
                + "<swe:uom code=\"test_unit_6\"/>" + "</swe:Quantity>" + "</swe:field>" + "</swe:DataRecord>");
        template.setResultEncoding("<swe:TextEncoding xmlns:swe=\"http://www.opengis.net/swe/2.0\""
                + " tokenSeparator=\"#\" blockSeparator=\"@\"/>");
        session.save(template);
    }

    private void createSpecialObservations() {
        NumericObservation o1 =
                createSpecialObservation("http://www.52north.org/test/observation/1", new GregorianCalendar(2012, 11,
                        19, 13, 10));
        NumericObservation o2 =
                createSpecialObservation("http://www.52north.org/test/observation/2", new GregorianCalendar(2012, 11,
                        19, 13, 15));

        session.save(o1);
        session.save(o2);
    }

    private NumericObservation createSpecialObservation(String id, GregorianCalendar calendar) {
        NumericObservation observation = new NumericObservation();
        observation.setProcedure(getProcedure(PROCEDURE_IDS[0]));
        observation.setObservableProperty(getObservableProperty(OBSERVABLE_PROPERTY_IDS[0]));
        observation.setFeatureOfInterest(getFeatureOfInterest(FEATURE_IDS[0]));
        observation.setUnit(getUnit(UNIT_IDS[0]));
        observation.getOfferings().add(getOffering(OFFERING_IDS[0]));

        observation.setPhenomenonTimeStart(calendar.getTime());
        calendar.add(Calendar.MINUTE, 5);
        observation.setPhenomenonTimeEnd(calendar.getTime());
        calendar.add(Calendar.MINUTE, 1);
        observation.setResultTime(calendar.getTime());

        observation.setIdentifier(id);
        observation.setValue(3.5);

        return observation;
    }

    private FeatureOfInterestType getFeatureOfInterestType(String type) {
        for (FeatureOfInterestType featureType : featureOfInterestTypes) {
            if (featureType.getFeatureOfInterestType().equals(type)) {
                return featureType;
            }
        }

        return null;
    }

    private ObservationType getObservationType(String type) {
        for (ObservationType observationType : observationTypes) {
            if (observationType.getObservationType().equals(type)) {
                return observationType;
            }
        }

        return null;
    }

    private TProcedure getProcedure(String id) {
        for (TProcedure procedure : procedures) {
            if (procedure.getIdentifier().equals(id)) {
                return procedure;
            }
        }

        return null;
    }

    private ProcedureDescriptionFormat getProcedureDescriptionFormat(String id) {
        for (ProcedureDescriptionFormat format : procedureDescriptionFormats) {
            if (format.getProcedureDescriptionFormat().equals(id)) {
                return format;
            }
        }

        return null;
    }

    private TObservableProperty getObservableProperty(String id) {
        for (TObservableProperty observableProperty : observableProperties) {
            if (observableProperty.getIdentifier().equals(id)) {
                return observableProperty;
            }
        }

        return null;
    }

    private TOffering getOffering(String id) {
        for (TOffering offering : offerings) {
            if (offering.getIdentifier().equals(id)) {
                return offering;
            }
        }

        return null;
    }

    private TFeatureOfInterest getFeatureOfInterest(String id) {
        for (TFeatureOfInterest feature : features) {
            if (feature.getIdentifier().equals(id)) {
                return feature;
            }
        }

        return null;
    }

    private Unit getUnit(String id) {
        for (Unit unit : units) {
            if (unit.getUnit().equals(id)) {
                return unit;
            }
        }

        return null;
    }

    private static String createSensorDescription(String uniqueId, String observableProperty, double x, double y,
            double z, String longName, String shortName) {
        return "<sml:SensorML version=\"1.0.1\" " + "xmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\" "
                + "xmlns:gml=\"http://www.opengis.net/gml\" " + "xmlns:swe=\"http://www.opengis.net/swe/1.0.1\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "<sml:member>" + "<sml:System >"
                + "<sml:identification>" + "<sml:IdentifierList>" + "<sml:identifier name=\"uniqueID\">"
                + "<sml:Term definition=\"urn:ogc:def:identifier:OGC:1.0:uniqueID\">" + "<sml:value>"
                + uniqueId
                + "</sml:value>"
                + "</sml:Term>"
                + "</sml:identifier>"
                + "<sml:identifier name=\"longName\">"
                + "<sml:Term definition=\"urn:ogc:def:identifier:OGC:1.0:longName\">"
                + "<sml:value>"
                + longName
                + "</sml:value>"
                + "</sml:Term>"
                + "</sml:identifier>"
                + "<sml:identifier name=\"shortName\">"
                + "<sml:Term definition=\"urn:ogc:def:identifier:OGC:1.0:shortName\">"
                + "<sml:value>"
                + shortName
                + "</sml:value>"
                + "</sml:Term>"
                + "</sml:identifier>"
                + "</sml:IdentifierList>"
                + "</sml:identification>"
                + "<sml:position name=\"sensorPosition\">"
                + "<swe:Position referenceFrame=\"urn:ogc:def:crs:EPSG::4326\">"
                + "<swe:location>"
                + "<swe:Vector gml:id=\"STATION_LOCATION\">"
                + "<swe:coordinate name=\"easting\">"
                + "<swe:Quantity axisID=\"x\">"
                + "<swe:uom code=\"degree\"/>"
                + "<swe:value>"
                + x
                + "</swe:value>"
                + "</swe:Quantity>"
                + "</swe:coordinate>"
                + "<swe:coordinate name=\"northing\">"
                + "<swe:Quantity axisID=\"y\">"
                + "<swe:uom code=\"degree\"/>"
                + "<swe:value>"
                + y
                + "</swe:value>"
                + "</swe:Quantity>"
                + "</swe:coordinate>"
                + "<swe:coordinate name=\"altitude\">"
                + "<swe:Quantity axisID=\"z\">"
                + "<swe:uom code=\"m\"/>"
                + "<swe:value>"
                + z
                + "</swe:value>"
                + "</swe:Quantity>"
                + "</swe:coordinate>"
                + "</swe:Vector>"
                + "</swe:location>"
                + "</swe:Position>"
                + "</sml:position>"
                + "<sml:inputs>"
                + "<sml:InputList>"
                + "<sml:input name=\"\">"
                + "<swe:ObservableProperty definition=\""
                + observableProperty
                + "/>"
                + "</sml:input>"
                + "</sml:InputList>"
                + "</sml:inputs>"
                + "<sml:outputs>"
                + "<sml:OutputList>"
                + "<sml:output name=\"\">"
                + "<swe:Quantity  definition=\""
                + uniqueId
                + "\">"
                + "<swe:uom code=\"NOTDEFINED\"/>"
                + "</swe:Quantity>"
                + "</sml:output>"
                + "</sml:OutputList>"
                + "</sml:outputs>" + "</sml:System>" + "</sml:member>" + "</sml:SensorML>";
    }
}
