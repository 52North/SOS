/**
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
package org.n52.sos.web.admin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.MissingServiceOperatorException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.CompositeOwsException;
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
import org.n52.sos.request.RequestContext;
import org.n52.sos.request.operator.RequestOperator;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.response.InsertFeatureOfInterestResponse;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.response.InsertSensorResponse;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.GroupedAndNamedThreadFactory;
import org.n52.sos.util.http.HTTPStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * @since 4.4.0
 * 
 * Inserts sample data into the database using the configuration files in the "sample-data" folder
 */
public class SampleDataInserter implements SosConstants, Sos2Constants {

    private static final String PROPERTY_FILE = "/sample-data/sample-data.properties";

    private static final int THREADPOOL_SLEEP_BETWEEN_CHECKS = 1000;

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

    private static final String CURRENT_YEAR_AND_MONTH;

    static {
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        CURRENT_YEAR_AND_MONTH =  String.format("%04d-%02d",
                cal.get(GregorianCalendar.YEAR),
                (cal.get(GregorianCalendar.MONTH)+1));
    }

    private final SwesExtension<?> extension;
    private final CompositeOwsException exceptions;
    private final RequestContext requestContext;

    public SampleDataInserter(RequestContext requestContext) throws IOException {
        extension = new SwesExtensionImpl<>()
                .setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name())
                .setValue((SweBoolean) new SweBoolean()
                        .setValue(true)
                        .setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name()));
        sampleDataProperties.load(this.getClass().getResourceAsStream(PROPERTY_FILE));
        exceptions = new CompositeOwsException();
        this.requestContext = requestContext;
    }

    public synchronized boolean insertSampleData() throws UnsupportedEncodingException, IOException, 
            MissingServiceOperatorException, URISyntaxException, OwsExceptionReport, XmlException {
        checkRequestOperators();
        insertSensors();
        insertFeatures();
        insertObservations();
        return insertedData;
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

    private void insertSensors() throws OwsExceptionReport,
            UnsupportedEncodingException, URISyntaxException, IOException,
            XmlException {
        createInsertSensorRequests();
        insertedSensors = Maps.newHashMap();
        ExecutorService threadPool = Executors.newFixedThreadPool(5,
                new GroupedAndNamedThreadFactory("52n-sample-data-insert-sensors"));
        for (final InsertSensorRequest request : insertSensorRequests) {
            threadPool.submit(new InsertSensorTask(request));
        }
        try {
            threadPool.shutdown();
            while (!threadPool.isTerminated()) {
                Thread.sleep(THREADPOOL_SLEEP_BETWEEN_CHECKS);
            }
        } catch (InterruptedException e) {}
        exceptions.throwIfNotEmpty();
    }

    private void insertFeatures() throws CompositeOwsException {
        final File[] featureFiles = getFilesBySuffix("_feature.xml");
        ExecutorService threadPool = Executors.newFixedThreadPool(5,
                new GroupedAndNamedThreadFactory("52n-sample-data-insert-features"));
        for (File featureFile : featureFiles) {
            threadPool.submit(new InsertFeatureTask(featureFile));
        }
        try {
            threadPool.shutdown();
            while (!threadPool.isTerminated()) {
                Thread.sleep(THREADPOOL_SLEEP_BETWEEN_CHECKS);
            }
        } catch (InterruptedException e) {}
        exceptions.throwIfNotEmpty();
    }

    private void insertObservations()
            throws UnsupportedEncodingException, IOException, XmlException, OwsExceptionReport {
        // send request to SosInsertObservationOperatorV20
        ExecutorService threadPool = Executors.newFixedThreadPool(5,
                new GroupedAndNamedThreadFactory("52n-sample-data-insert-observations"));
        for (File observationFile : getFilesBySuffix("_obs.xml")) {
            threadPool.submit(new InsertObservationTask(observationFile));
        }
        try {
            threadPool.shutdown();
            while (!threadPool.isTerminated()) {
                Thread.sleep(THREADPOOL_SLEEP_BETWEEN_CHECKS);
            }
        } catch (InterruptedException e) {}
        exceptions.throwIfNotEmpty();
    }

    private void createInsertSensorRequests()
            throws URISyntaxException, UnsupportedEncodingException, IOException, OwsExceptionReport, XmlException {
        insertSensorRequests = Lists.newArrayList();
        sampleDataFolder = Paths.get(getUri(new File(new URI(this.getClass().getResource("/sample-data/").toString()).getPath()))).toFile();
        for (File sensorDescriptionFile : getFilesBySuffix("_sensor-desc.xml")) {
            String description = new String(Files.readAllBytes(
                    Paths.get(getUri(sensorDescriptionFile))),"UTF-8");
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
                    .setRequestContext(requestContext)
                    .setService(SOS)
                    .setVersion(SERVICEVERSION);
            insertSensorRequests.add(insertSensorRequest);
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
                + "operation in the <a href=\"../operations\">settings</a>.", service, version,
                operation);
        LOG.error(msg);
        throw new MissingServiceOperatorException(msg);
    }

    private URI getUri(File file) {
        URI uri;
        try {
            uri = file.toPath().toUri();
        } catch (InvalidPathException ipe) {
            uri = file.toURI();
            LOG.debug("Cannot convert '{}'. Falling back to '{}'.", file, uri);
            LOG.trace("Expected exception catched.", ipe);
        }
        if (uri.toString().startsWith("file:///")) {
            try {
                uri =  new URI(uri.getScheme(),
                        uri.getUserInfo(),
                        uri.getHost(),
                        uri.getPort(),
                        uri.getPath(),
                        uri.getQuery(),uri.getFragment()
                );
            } catch (URISyntaxException use) {
                LOG.debug("Could not convert '{}' to URI.", file);
                LOG.trace("Expected exception catched.", use);
            }
        }
        return uri;
    }

    private class InsertSensorTask implements Runnable{

        private final InsertSensorRequest request;

        public InsertSensorTask(InsertSensorRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            if (exceptions.hasExceptions()) {
                return;
            }
            try {
                InsertSensorResponse response = (InsertSensorResponse) insertSensorOperator.receiveRequest(request);
                insertedSensors.put(response.getAssignedProcedure(), response.getAssignedOffering());
            } catch (OwsExceptionReport e) {
                exceptions.add(e);
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
                                                        Paths.get(getUri(featureFile))),"UTF-8")))
                                .setRequestContext(requestContext)
                                .setService(SOS)
                                .setVersion(SERVICEVERSION)) == null) {
                    exceptions.add(
                            new NoApplicableCodeException().withMessage("Could not insert feature of interest."));
                }
            } catch (OwsExceptionReport e) {
                exceptions.add(e);
            } catch (IOException e) {
                exceptions.add(new NoApplicableCodeException()
                .causedBy(e)
                .withMessage("Could not read file '{}' containing feature of interest.", featureFile)
                .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR));
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
            if (exceptions.hasExceptions()) {
                return;
            }
            try {
                String xmlString = new String(Files.readAllBytes(Paths.get(getUri(observationFile))),"UTF-8");
                xmlString = xmlString.replaceAll("2016-05", CURRENT_YEAR_AND_MONTH);
                LOG.trace(xmlString);
                final String procedureId = observationFile.getName().replace("_obs.xml", "");
                GetObservationResponseDocument decodedXmlObject =
                        (GetObservationResponseDocument) XmlObject.Factory.parse(xmlString);
                ObservationData[] observations = decodedXmlObject.getGetObservationResponse().getObservationDataArray();

                ExecutorService threadPool = Executors.newFixedThreadPool(5,
                        new GroupedAndNamedThreadFactory(Thread.currentThread().getName() + "-sub"));
                for (ObservationData observationData : observations) {
                    threadPool.submit(new InsertObservationSubTask(procedureId, 
                            (OmObservation)CodingHelper.decodeXmlElement(observationData.getOMObservation())));
                }
                try {
                    threadPool.shutdown();
                    while (!threadPool.isTerminated()) {
                        Thread.sleep(THREADPOOL_SLEEP_BETWEEN_CHECKS);
                    }
                } catch (InterruptedException e) {}
            } catch (OwsExceptionReport e) {
                exceptions.add(e);
            } catch (XmlException e) {
                exceptions.add(new NoApplicableCodeException()
                .causedBy(e)
                .withMessage("Could not parse content of file '{}' to valid XML.",
                        observationFile)
                .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR));
            } catch (IOException e) {
                exceptions.add(new NoApplicableCodeException()
                .causedBy(e)
                .withMessage("Could not read file '{}' containing observations.",
                        observationFile)
                .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR));
            }
        }
    }

    private class InsertObservationSubTask implements Runnable {

        private final OmObservation observationData;
        private final String procedureId;

        public InsertObservationSubTask(String procedureId, OmObservation observationData) {
            this.procedureId = procedureId;
            this.observationData = observationData;
        }

        @Override
        public void run() {
            if (exceptions.hasExceptions()) {
                return;
            }
            try {
                InsertObservationRequest request = (InsertObservationRequest) new InsertObservationRequest()
                        .setOfferings(Collections.singletonList(insertedSensors.get(procedureId)))
                        .setObservation(Collections.singletonList(observationData))
                        .addExtension(extension)
                        .setRequestContext(requestContext)
                        .setService(SOS)
                        .setVersion(SERVICEVERSION);
                InsertObservationResponse insertObservationResponse =
                        (InsertObservationResponse) insertObservationOperator.receiveRequest(request);
                if (insertObservationResponse != null) {
                    insertedData = true;
                }
            } catch (OwsExceptionReport e) {
                exceptions.add(e);
            }
        }
    }
}
