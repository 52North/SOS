/**
 * ﻿Copyright (C) 2017
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.web.admin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.MissingServiceOperatorException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.InsertFeatureOfInterestRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.request.operator.RequestOperator;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.response.InsertFeatureOfInterestResponse;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.response.InsertSensorResponse;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * @since 4.4.0
 * 
 * Inserts sample data into the database using the configuration files in the "sample-data" folder
 */
public class SampleDataInserter implements SosConstants, Sos2Constants {

    private static final String PROPERTY_FILE = "/sample-data/sample-data.properties";

    private static final Logger LOG = LoggerFactory.getLogger(SampleDataInserter.class);

    private static final ServiceOperatorKey SERVICE_OPERATOR_KEY = new ServiceOperatorKey(SOS, SERVICEVERSION);

    private Properties sampleDataProperties = new Properties();

    private RequestOperator insertSensorOperator;

    private RequestOperator insertObservationOperator;

    private RequestOperator insertFeatureOperator;

    private File sampleDataFolder;

    private List<InsertSensorRequest> insertSensorRequests;

    private Map<String, String> insertedSensors;

    private boolean insertedData;

    private List<Exception> insertSensorExceptions;

    private List<Exception> insertObservationExceptions;

    private static String currentYearAndMonth;

    static {
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        currentYearAndMonth =  String.format("%04d-%02d",
                cal.get(GregorianCalendar.YEAR),
                (cal.get(GregorianCalendar.MONTH)+1));
    }

    private final SwesExtension<?> extension = new SwesExtensionImpl<SweBoolean>()
            .setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name())
            .setValue((SweBoolean) new SweBoolean()
                    .setValue(true)
                    .setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name()));

    private List<Exception> insertFeatureExceptions;

    public synchronized boolean insertSampleData() throws UnsupportedEncodingException, IOException, 
            MissingServiceOperatorException, URISyntaxException, OwsExceptionReport, XmlException {
        checkRequestOperators();
        sampleDataProperties.load(this.getClass().getResourceAsStream(PROPERTY_FILE));
        insertSensors();
        insertFeatures();
        insertObservations();
        return insertedData;
    }

    private void insertFeatures() throws CodedException {
        final File[] featureFiles = getFilesBySuffix("_feature.xml");
        insertFeatureExceptions = Lists.newArrayList();
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (File featureFile : featureFiles) {
            threadPool.submit(new InsertFeatureTask(featureFile));
        }
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(180, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        if (!insertFeatureExceptions.isEmpty()) {
            throw createException(insertFeatureExceptions.get(0), "InsertFeature", insertFeatureExceptions.size());
        }
    }

    private CodedException createException(Exception e, String operation, int count) {
        return new NoApplicableCodeException()
        .causedBy(e)
        .withMessage("%s failed during sample data insertion! Showing 1. of %s exceptions: %s",
                operation,
                count,
                e.getMessage());
    }

    private void insertObservations()
            throws UnsupportedEncodingException, IOException, XmlException, OwsExceptionReport {
        // send request to SosInsertObservationOperatorV20
        insertObservationExceptions = Lists.newArrayList();
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (File observationFile : getFilesBySuffix("_obs.xml")) {
            threadPool.submit(new InsertObservationTask(observationFile));
        }
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(180, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        if (!insertObservationExceptions.isEmpty()) {
            throw createException(insertObservationExceptions.get(0), "InsertObservation",
                    insertObservationExceptions.size());
        }
    }

    private void insertSensors() throws OwsExceptionReport, UnsupportedEncodingException, URISyntaxException,
            IOException, XmlException {
        createInsertSensorRequests();
        insertedSensors = Maps.newHashMap();
        insertSensorExceptions = Lists.newArrayList();
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (final InsertSensorRequest request : insertSensorRequests) {
            threadPool.submit(new InsertSensorTask(request));
        }
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(180, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        if (!insertSensorExceptions.isEmpty()) {
            throw createException(insertSensorExceptions.get(0), "InsertSensor", insertSensorExceptions.size());
        }
    }

    private void createInsertSensorRequests()
            throws URISyntaxException, UnsupportedEncodingException, IOException, OwsExceptionReport, XmlException {
        insertSensorRequests = Lists.newArrayList();
        sampleDataFolder = Paths.get(
                new URI( this.getClass().getResource("/sample-data/").toString()).getPath()).toFile();
        for (File sensorDescriptionFile : getFilesBySuffix("_sensor-desc.xml")) {
            String description = new String(Files.readAllBytes(
                    Paths.get(sensorDescriptionFile.getAbsolutePath())),"UTF-8");
            final String procedureId = sensorDescriptionFile.getName().replace("_sensor-desc.xml", "");
            PhysicalSystem physicalSystem =
                    (PhysicalSystem) CodingHelper.decodeXmlElement(XmlObject.Factory.parse(description));
            InsertSensorRequest insertSensorRequest = (InsertSensorRequest) new InsertSensorRequest()
                    .setProcedureDescriptionFormat("http://www.opengis.net/sensorml/2.0")
                    .setProcedureDescription(physicalSystem)
                    .setObservableProperty(getPropertyList(procedureId + "_observedProperties"))
                    .setMetadata(new SosInsertionMetadata()
                            .setObservationTypes(getPropertyList(procedureId + "_observationTypes"))
                            .setFeatureOfInterestTypes(getPropertyList(procedureId + "_featureTypes")))
                    .setService(SOS)
                    .setVersion(SERVICEVERSION);
            insertSensorRequests.add(insertSensorRequest);
        }
    }

    private void checkRequestOperators() throws MissingServiceOperatorException {
        final String insertSensor = Sos2Constants.Operations.InsertSensor.name();
        insertSensorOperator = RequestOperatorRepository.getInstance()
                .getRequestOperator(SERVICE_OPERATOR_KEY, insertSensor);
        if (insertSensorOperator == null) {
            missingServiceOperator(SOS, SERVICEVERSION, insertSensor);
        }
        final String insertObservation = SosConstants.Operations.InsertObservation.name();
        insertObservationOperator = RequestOperatorRepository.getInstance()
                .getRequestOperator(SERVICE_OPERATOR_KEY, insertObservation);
        if (insertObservationOperator == null) {
            missingServiceOperator(SOS, SERVICEVERSION, insertObservation);
        }
        final String insertFeature = "InsertFeatureOfInterest";
        insertFeatureOperator = RequestOperatorRepository.getInstance()
                .getRequestOperator(SERVICE_OPERATOR_KEY, insertFeature);
        if (insertFeatureOperator == null) {
            missingServiceOperator(SOS, SERVICEVERSION, insertFeature);
        }
    }

    private File[] getFilesBySuffix(final String suffix) {
        final File[] files = sampleDataFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                return pathname.isFile() &&
                        pathname.canRead() &&
                        pathname.getName().endsWith(suffix);
            }
        });
        return files;
    }

    private List<String> getPropertyList(final String propertyId) throws CodedException {
        if (!sampleDataProperties.containsKey(propertyId) ||
                !sampleDataProperties.get(propertyId).getClass().isAssignableFrom(String.class)) {
            throw new NoApplicableCodeException().withMessage("Property '%s' not defined in %s. Please update!",
                    propertyId,
                    PROPERTY_FILE);
        }
        if (sampleDataProperties.get(propertyId).toString().isEmpty()) {
            throw new NoApplicableCodeException().withMessage("Property '%s' MUST not be empty in %s. Please update!",
                    propertyId,
                    PROPERTY_FILE);
        }
        return Arrays.asList(sampleDataProperties.getProperty(propertyId).split(","));
    }

    private void missingServiceOperator(final String service, final String version, final String operation)
            throws MissingServiceOperatorException {
        String msg = String.format("Could not load request operator for: %s, %s, %s. Please activate the according "
                + "operation in the <a href=\"./operations\">settings</a>.", service, version,
                operation);
        LOG.error(msg);
        throw new MissingServiceOperatorException(msg);
    }

    private class InsertSensorTask implements Runnable{

        private final InsertSensorRequest request;

        public InsertSensorTask(InsertSensorRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            if (!insertSensorExceptions.isEmpty()) {
                return;
            }
            try {
                InsertSensorResponse response = (InsertSensorResponse) insertSensorOperator.receiveRequest(request);
                insertedSensors.put(response.getAssignedProcedure(),response.getAssignedOffering());
            } catch (OwsExceptionReport e) {
                insertSensorExceptions.add(e);
            }
        }
    }

    private class InsertFeatureTask implements Runnable, SosConstants, Sos2Constants {
    
        private File featureFile;

        public InsertFeatureTask(File featureFile) {
            this.featureFile = featureFile;
        }

        @Override
        public void run() {
            try {
                if ((InsertFeatureOfInterestResponse) insertFeatureOperator
                        .receiveRequest((InsertFeatureOfInterestRequest) new InsertFeatureOfInterestRequest()
                                .addFeatureMember(
                                        (AbstractFeature)CodingHelper.decodeXmlObject(
                                                new String(Files.readAllBytes(
                                                        Paths.get(featureFile.getAbsolutePath())),"UTF-8")))
                                .setService(SOS)
                                .setVersion(SERVICEVERSION)) == null) {
                    insertFeatureExceptions.add(
                            new NoApplicableCodeException().withMessage("Could not insert feature of interest."));
                }
            } catch (OwsExceptionReport|IOException e) {
                insertFeatureExceptions.add(e);
            }
        }
    }

    private class InsertObservationTask implements Runnable {

        private final File observationFile;

        public InsertObservationTask(File observationFile) {
            this.observationFile = observationFile;
        }

        @Override
        public void run() {
            if (!insertObservationExceptions.isEmpty()) {
                return;
            }
            try {
                String xmlString = new String(Files.readAllBytes(Paths.get(observationFile.getAbsolutePath())),"UTF-8");
                xmlString = xmlString.replaceAll("2016-05", currentYearAndMonth);
                final String procedureId = observationFile.getName().replace("_obs.xml", "");
                // TODO update DATE with last month
                GetObservationResponseDocument decodedXmlObject =
                        (GetObservationResponseDocument) XmlObject.Factory.parse(xmlString);
                ObservationData[] observations = decodedXmlObject.getGetObservationResponse().getObservationDataArray();

                ExecutorService threadPool = Executors.newFixedThreadPool(10);
                for (ObservationData observationData : observations) {
                    threadPool.submit(new InsertObservationSubTask(procedureId, observationData));
                }
                try {
                    threadPool.shutdown();
                    threadPool.awaitTermination(180, TimeUnit.SECONDS);
                } catch (InterruptedException e) {}
            } catch (IOException|XmlException e) {
                insertObservationExceptions.add(e);
            }
        }
    }

    private class InsertObservationSubTask implements Runnable {

        private ObservationData observationData;
        private String procedureId;

        public InsertObservationSubTask(String procedureId, ObservationData observationData) {
            this.procedureId = procedureId;
            this.observationData = observationData;
        }

        @Override
        public void run() {
            if (!insertObservationExceptions.isEmpty()) {
                return;
            }
            try {
                InsertObservationRequest request = (InsertObservationRequest) new InsertObservationRequest()
                        .setOfferings(Collections.singletonList(insertedSensors.get(procedureId)))
                        .setObservation(Collections.singletonList(
                                (OmObservation)CodingHelper.decodeXmlElement(observationData.getOMObservation())))
                        .addExtension(extension)
                        .setService(SOS)
                        .setVersion(SERVICEVERSION);
                InsertObservationResponse insertObservationResponse =
                        (InsertObservationResponse) insertObservationOperator.receiveRequest(request);
                if (insertObservationResponse != null) {
                    insertedData = true;
                }
            } catch (OwsExceptionReport e) {
                insertObservationExceptions.add(e);
            }
        }
    }
}
