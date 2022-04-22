/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.request.operator.RequestOperator;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.janmayen.GroupedAndNamedThreadFactory;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;
import org.n52.shetland.ogc.ows.service.OwsServiceRequestContext;
import org.n52.shetland.ogc.sensorML.v20.PhysicalSystem;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosInsertionMetadata;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.ifoi.InsertFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.ifoi.InsertFeatureOfInterestResponse;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.shetland.ogc.sos.response.InsertSensorResponse;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swes.SwesExtension;
import org.n52.sos.exception.MissingServiceOperatorException;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.opengis.om.x20.OMObservationType;
import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.4.0
 *
 *        Inserts sample data into the database using the configuration files in
 *        the "sample-data" folder
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class SampleDataInserter implements Sos2Constants {

    private static final String PROPERTY_FILE = "/sample-data/sample-data.properties";

    private static final String OBS_XML_FILE_ENDING = "_obs.xml";

    private static final String SENSOR_XML_FILE_ENDING = "_sensor-desc.xml";

    private static final String UTF_8 = "UTF-8";

    private static final int THREADPOOL_SLEEP_BETWEEN_CHECKS = 1000;

    private static final String CURRENT_YEAR_AND_MONTH;

    private static final OwsServiceKey SERVICE_OPERATOR_KEY = new OwsServiceKey(SOS, SERVICEVERSION);

    private static final String LOG_EXPECTED_EXCEPTION_CACHED = "Expected exception catched.";

    private static final Logger LOG = LoggerFactory.getLogger(SampleDataInserter.class);

    private Properties sampleDataProperties = new Properties();

    private RequestOperator insertSensorOperator;

    private RequestOperator insertObservationOperator;

    private RequestOperator insertFeatureOperator;

    private File sampleDataFolder;

    private List<InsertSensorRequest> insertSensorRequests;

    private Map<String, String> insertedSensors;

    private boolean insertedData;

    static {
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        CURRENT_YEAR_AND_MONTH =
                String.format("%04d-%02d", cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH) + 1);
    }

    private final Extension<?> extension;

    private final CompositeOwsException exceptions;

    private final OwsServiceRequestContext requestContext;

    private DecoderRepository decoderRepository;

    private RequestOperatorRepository requestOperatorRepository;

    public SampleDataInserter(OwsServiceRequestContext owsServiceRequestContext, DecoderRepository decoderRepository,
            RequestOperatorRepository requestOperatorRepository) throws IOException {
        extension = new SwesExtension<>()
                .setValue((SweBoolean) new SweBoolean().setValue(true)
                        .setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name()))
                .setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name());
        try (InputStream is = this.getClass().getResourceAsStream(PROPERTY_FILE)) {
            sampleDataProperties.load(is);
        }
        exceptions = new CompositeOwsException();
        this.requestContext = owsServiceRequestContext;
        this.decoderRepository = decoderRepository;
        this.requestOperatorRepository = requestOperatorRepository;
    }

    public synchronized boolean insertSampleData() throws UnsupportedEncodingException, IOException,
            MissingServiceOperatorException, URISyntaxException, OwsExceptionReport, XmlException, DecodingException {
        LOG.debug("Start sample data insertion!");
        long start = System.currentTimeMillis();
        checkRequestOperators();
        insertSensors();
        insertFeatures();
        insertObservations();
        LOG.debug("Finished sample data insertion in " + (System.currentTimeMillis() - start) / 1000 + "s!");
        return getInsertedData();
    }

    private boolean getInsertedData() {
        return insertedData;
    }

    private void setInsertedData() {
        this.insertedData = true;
    }

    private void checkRequestOperators() throws MissingServiceOperatorException {
        final String insertSensor = Sos2Constants.Operations.InsertSensor.name();
        insertSensorOperator = requestOperatorRepository
                .getRequestOperator(new RequestOperatorKey(SERVICE_OPERATOR_KEY, insertSensor, false));
        if (insertSensorOperator == null) {
            missingServiceOperator(SOS, SERVICEVERSION, insertSensor);
        }
        final String insertObservation = SosConstants.Operations.InsertObservation.name();
        insertObservationOperator = requestOperatorRepository
                .getRequestOperator(new RequestOperatorKey(SERVICE_OPERATOR_KEY, insertObservation, false));
        if (insertObservationOperator == null) {
            missingServiceOperator(SOS, SERVICEVERSION, insertObservation);
        }
        final String insertFeature = "InsertFeatureOfInterest";
        insertFeatureOperator = requestOperatorRepository
                .getRequestOperator(new RequestOperatorKey(SERVICE_OPERATOR_KEY, insertFeature, false));
        if (insertFeatureOperator == null) {
            missingServiceOperator(SOS, SERVICEVERSION, insertFeature);
        }
    }

    private void insertSensors() throws OwsExceptionReport, UnsupportedEncodingException, URISyntaxException,
            IOException, XmlException, DecodingException {
        createInsertSensorRequests();
        insertedSensors = Maps.newHashMap();
        ExecutorService threadPool =
                Executors.newFixedThreadPool(5, new GroupedAndNamedThreadFactory("52n-sample-data-insert-sensors"));
        for (final InsertSensorRequest request : insertSensorRequests) {
            threadPool.submit(new InsertSensorTask(request));
        }
        try {
            threadPool.shutdown();
            while (!threadPool.isTerminated()) {
                Thread.sleep(THREADPOOL_SLEEP_BETWEEN_CHECKS);
            }
        } catch (InterruptedException e) {
            LOG.error("Insert sensors thread was interrupted!", e);
        }
        exceptions.throwIfNotEmpty();
    }

    private void insertFeatures() throws CompositeOwsException {
        final File[] featureFiles = getFilesBySuffix("_feature.xml");
        ExecutorService threadPool =
                Executors.newFixedThreadPool(5, new GroupedAndNamedThreadFactory("52n-sample-data-insert-features"));
        for (File featureFile : featureFiles) {
            threadPool.submit(new InsertFeatureTask(featureFile));
        }
        try {
            threadPool.shutdown();
            while (!threadPool.isTerminated()) {
                Thread.sleep(THREADPOOL_SLEEP_BETWEEN_CHECKS);
            }
        } catch (InterruptedException e) {
            LOG.error("Insert features thread was interrupted!", e);
        }
        exceptions.throwIfNotEmpty();
    }

    private void insertObservations()
            throws UnsupportedEncodingException, IOException, XmlException, OwsExceptionReport {
        // send request to SosInsertObservationOperatorV20
        ExecutorService threadPool = Executors.newFixedThreadPool(5,
                new GroupedAndNamedThreadFactory("52n-sample-data-insert-observations"));
        for (File observationFile : getFilesBySuffix(OBS_XML_FILE_ENDING)) {
            threadPool.submit(new InsertObservationTask(observationFile));
        }
        try {
            threadPool.shutdown();
            while (!threadPool.isTerminated()) {
                Thread.sleep(THREADPOOL_SLEEP_BETWEEN_CHECKS);
            }
        } catch (InterruptedException e) {
            LOG.error("Insert obervations thread was interrupted!", e);
        }
        exceptions.throwIfNotEmpty();
    }

    private void createInsertSensorRequests() throws URISyntaxException, UnsupportedEncodingException, IOException,
            OwsExceptionReport, XmlException, DecodingException {
        insertSensorRequests = Lists.newArrayList();
        sampleDataFolder =
                Paths.get(getUri(new File(new URI(this.getClass().getResource("/sample-data/").toString()).getPath())))
                        .toFile();
        for (File sensorDescriptionFile : getFilesBySuffix(SENSOR_XML_FILE_ENDING)) {
            String description = new String(Files.readAllBytes(Paths.get(getUri(sensorDescriptionFile))), UTF_8);
            final String procedureId = sensorDescriptionFile.getName().replace(SENSOR_XML_FILE_ENDING, "");
            XmlObject xml = XmlObject.Factory.parse(description);
            PhysicalSystem physicalSystem =
                    (PhysicalSystem) decoderRepository.getDecoder(CodingHelper.getDecoderKey(xml)).decode(xml);
            InsertSensorRequest insertSensorRequest = (InsertSensorRequest) new InsertSensorRequest()
                    .setProcedureDescriptionFormat("http://www.opengis.net/sensorml/2.0")
                    .setProcedureDescription(new SosProcedureDescription<AbstractFeature>(physicalSystem))
                    .setObservableProperty(getPropertyList(procedureId + "_observedProperties"))
                    .setMetadata(new SosInsertionMetadata()
                            .setObservationTypes(getPropertyList(procedureId + "_observationTypes"))
                            .setFeatureOfInterestTypes(getPropertyList(procedureId + "_featureTypes")))
                    .setRequestContext(requestContext).setService(SOS).setVersion(SERVICEVERSION);
            insertSensorRequests.add(insertSensorRequest);
        }
    }

    private File[] getFilesBySuffix(final String suffix) {
        final File[] files = sampleDataFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                return pathname.isFile() && pathname.canRead() && pathname.getName().endsWith(suffix);
            }
        });
        return files;
    }

    private List<String> getPropertyList(final String propertyId) throws CodedException {
        if (!sampleDataProperties.containsKey(propertyId)
                || !sampleDataProperties.get(propertyId).getClass().isAssignableFrom(String.class)) {
            throw new NoApplicableCodeException().withMessage("Property '%s' not defined in %s. Please update!",
                    propertyId, PROPERTY_FILE);
        }
        if (sampleDataProperties.get(propertyId).toString().isEmpty()) {
            throw new NoApplicableCodeException().withMessage("Property '%s' MUST not be empty in %s. Please update!",
                    propertyId, PROPERTY_FILE);
        }
        return Arrays.asList(sampleDataProperties.getProperty(propertyId).split(","));
    }

    private void missingServiceOperator(final String service, final String version, final String operation)
            throws MissingServiceOperatorException {
        String msg = String.format(
                "Could not load request operator for: %s, %s, %s. Please activate the according "
                        + "operation in the <a href=\"../admin/operations\">settings</a>.",
                service, version, operation);
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
            LOG.trace(LOG_EXPECTED_EXCEPTION_CACHED, ipe);
        }
        if (uri.toString().startsWith("file:///")) {
            try {
                uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(),
                        uri.getQuery(), uri.getFragment());
            } catch (URISyntaxException use) {
                LOG.debug("Could not convert '{}' to URI.", file);
                LOG.trace(LOG_EXPECTED_EXCEPTION_CACHED, use);
            }
        }
        return uri;
    }

    private class InsertSensorTask implements Runnable {

        private final InsertSensorRequest request;

        InsertSensorTask(InsertSensorRequest request) {
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
                if (e.getMessage()
                        .equals("The offering with the identifier '"
                                + request.getProcedureDescription().getIdentifier()
                                + "' still exists in this service and it is not allowed to "
                                + "insert more than one procedure to an offering!")) {
                    insertedSensors.put(request.getProcedureDescription().getIdentifier(),
                            request.getProcedureDescription().getIdentifier());
                } else {
                    exceptions.add(e);
                }
            }
        }
    }

    private class InsertFeatureTask implements Runnable, SosConstants, Sos2Constants {

        private File featureFile;

        InsertFeatureTask(File featureFile) {
            this.featureFile = featureFile;
        }

        @Override
        public void run() {
            try {
                if ((InsertFeatureOfInterestResponse) insertFeatureOperator
                        .receiveRequest((InsertFeatureOfInterestRequest) new InsertFeatureOfInterestRequest()
                                .addFeatureMember(decodeXmlObject(
                                        new String(Files.readAllBytes(Paths.get(getUri(featureFile))), UTF_8)))
                                .setRequestContext(requestContext).setService(SOS)
                                .setVersion(SERVICEVERSION)) == null) {
                    exceptions
                            .add(new NoApplicableCodeException().withMessage("Could not insert feature of interest."));
                }
            } catch (XmlException | DecodingException e) {
                exceptions.add(new NoApplicableCodeException().causedBy(e));
            } catch (IOException e) {
                exceptions.add(new NoApplicableCodeException().causedBy(e)
                        .withMessage("Could not read file '{}' containing feature of interest.", featureFile)
                        .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR));
            } catch (OwsExceptionReport e) {
                exceptions.add(e);
            }
        }

        private AbstractFeature decodeXmlObject(String string) throws XmlException, DecodingException {
            XmlObject xml = XmlObject.Factory.parse(string);
            return (AbstractFeature) decoderRepository.getDecoder(CodingHelper.getDecoderKey(xml)).decode(xml);
        }
    }

    private class InsertObservationTask implements Runnable {

        private final File observationFile;

        InsertObservationTask(File observationFile) {
            this.observationFile = observationFile;
        }

        @Override
        public void run() {
            if (exceptions.hasExceptions()) {
                return;
            }
            try {
                String xmlString = new String(Files.readAllBytes(Paths.get(getUri(observationFile))), UTF_8);
                xmlString = xmlString.replaceAll("2016-05", CURRENT_YEAR_AND_MONTH);
                LOG.trace(xmlString);
                final String procedureId = observationFile.getName().replace(OBS_XML_FILE_ENDING, "");
                GetObservationResponseDocument decodedXmlObject =
                        (GetObservationResponseDocument) XmlObject.Factory.parse(xmlString);
                ObservationData[] observations =
                        decodedXmlObject.getGetObservationResponse().getObservationDataArray();

                ExecutorService threadPool = Executors.newFixedThreadPool(5,
                        new GroupedAndNamedThreadFactory(Thread.currentThread().getName() + "-sub"));
                for (ObservationData observationData : observations) {
                    threadPool.submit(new InsertObservationSubTask(procedureId,
                            decodeXmlElement(observationData.getOMObservation())));
                }
                try {
                    threadPool.shutdown();
                    while (!threadPool.isTerminated()) {
                        Thread.sleep(THREADPOOL_SLEEP_BETWEEN_CHECKS);
                    }
                } catch (InterruptedException e) {
                    LOG.error("Thread was interrupted!", e);
                }
            } catch (DecodingException e) {
                exceptions.add(new NoApplicableCodeException().causedBy(e));
            } catch (XmlException e) {
                exceptions.add(new NoApplicableCodeException().causedBy(e)
                        .withMessage("Could not parse content of file '{}' to valid XML.", observationFile)
                        .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR));
            } catch (IOException e) {
                exceptions.add(new NoApplicableCodeException().causedBy(e)
                        .withMessage("Could not read file '{}' containing observations.", observationFile)
                        .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR));
            }
        }

        private OmObservation decodeXmlElement(OMObservationType xml) throws DecodingException {
            return (OmObservation) decoderRepository.getDecoder(CodingHelper.getDecoderKey(xml)).decode(xml);
        }
    }

    private class InsertObservationSubTask implements Runnable {

        private final OmObservation observationData;

        private final String procedureId;

        InsertObservationSubTask(String procedureId, OmObservation observationData) {
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
                        .setObservation(Collections.singletonList(observationData)).addExtension(extension)
                        .setRequestContext(requestContext).setService(SOS).setVersion(SERVICEVERSION);
                InsertObservationResponse insertObservationResponse =
                        (InsertObservationResponse) insertObservationOperator.receiveRequest(request);
                if (insertObservationResponse != null) {
                    setInsertedData();
                }
            } catch (OwsExceptionReport e) {
                exceptions.add(e);
            }
        }
    }
}
