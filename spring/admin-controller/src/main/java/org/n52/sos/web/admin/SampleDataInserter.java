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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.MissingServiceOperatorException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.request.operator.RequestOperator;
import org.n52.sos.request.operator.RequestOperatorRepository;
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

    private static final Logger LOG = LoggerFactory.getLogger(SampleDataInserter.class);

    private static final ServiceOperatorKey SERVICE_OPERATOR_KEY = new ServiceOperatorKey(SOS, SERVICEVERSION);

    private Properties sampleDataProperties = new Properties();

    private RequestOperator insertSensorOperator;

    private RequestOperator insertObservationOperator;

    private File[] sensorDescriptionFiles;

    private File sampleDataFolder;

    List<InsertSensorRequest> insertSensorRequests;

    private Map<String, String> insertedSensors;

    private boolean insertedData;

    public boolean insertSampleData() throws UnsupportedEncodingException, IOException, MissingServiceOperatorException, URISyntaxException, OwsExceptionReport, XmlException {
        checkRequestOperators();
        sampleDataProperties.load(this.getClass().getResourceAsStream("/sample-data/sample-data.properties"));
        createInsertSensorRequests();
        insertSensors();
        insertObservations();
        return insertedData;
    }

    private void insertObservations()
            throws UnsupportedEncodingException, IOException, XmlException, OwsExceptionReport {
        // send request to SosInsertObservationOperatorV20
        SwesExtension<?> extension = new SwesExtensionImpl<SweBoolean>()
                .setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name())
                .setValue((SweBoolean) new SweBoolean()
                        .setValue(true)
                        .setDefinition(Sos2Constants.Extensions.SplitDataArrayIntoObservations.name()));
        

        final File[] observationFiles = getFilesBySuffix("_obs.xml");
        
        for (File observationFile : observationFiles) {
            String xmlString = new String(Files.readAllBytes(Paths.get(observationFile.getAbsolutePath())),"UTF-8");
            final String procedureId = observationFile.getName().replace("_obs.xml", "");
            // TODO update date1 with last month and date2 with this month (problem with month length to consider)
            GetObservationResponseDocument decodedXmlObject = (GetObservationResponseDocument) XmlObject.Factory.parse(xmlString);
            ObservationData[] observations = decodedXmlObject.getGetObservationResponse().getObservationDataArray();
            
            List<OmObservation> observation = Lists.newLinkedList();
            
            for (ObservationData observationData : observations) {
                observation.add((OmObservation)CodingHelper.decodeXmlElement(observationData.getOMObservation()));
            }
            
            InsertObservationRequest insertObservationRequest = (InsertObservationRequest) new InsertObservationRequest()
                    .setOfferings(Collections.singletonList(insertedSensors.get(procedureId)))
                    .setObservation(observation)
                    .addExtension(extension)
                    .setService(SOS)
                    .setVersion(SERVICEVERSION);
            InsertObservationResponse insertObservationResponse = 
                    (InsertObservationResponse) insertObservationOperator.receiveRequest(insertObservationRequest);
            if (insertObservationResponse != null) {
                insertedData = true;
            }
        }
    }

    private void insertSensors() throws OwsExceptionReport {
        insertedSensors = Maps.newHashMap();
        for (InsertSensorRequest request : insertSensorRequests) {
            InsertSensorResponse response = (InsertSensorResponse) insertSensorOperator.receiveRequest(request);
            insertedSensors.put(response.getAssignedProcedure(),response.getAssignedOffering());
        }
    }

    private void createInsertSensorRequests()
            throws URISyntaxException, UnsupportedEncodingException, IOException {
        /*
         * SENSOR DESCRIPTIONS
         */
        // create all required requests
        insertSensorRequests = Lists.newArrayList();
        sampleDataFolder = Paths.get(new URI( this.getClass().getResource("/sample-data/").toString()).getPath()).toFile();
        sensorDescriptionFiles = getFilesBySuffix("_sensor-desc.xml");
        for (File sensorDescriptionFile : sensorDescriptionFiles) {
            String description = new String(Files.readAllBytes(Paths.get(sensorDescriptionFile.getAbsolutePath())),"UTF-8");
            final String procedureId = sensorDescriptionFile.getName().replace("_sensor-desc.xml", "");
            final SosProcedureDescription procedure = new SensorML()
                    .setSensorDescriptionXmlString(description)
                    .setIdentifier(procedureId);
            InsertSensorRequest insertSensorRequest = (InsertSensorRequest) new InsertSensorRequest()
                    .setProcedureDescriptionFormat("http://www.opengis.net/sensorml/2.0")
                    .setProcedureDescription(procedure)
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
        /*
         * REQUEST OPERATOR
         */
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

    private List<String> getPropertyList(final String properyId) {
        return Arrays.asList(sampleDataProperties.getProperty(properyId).split(","));
    }

    private void missingServiceOperator(final String service, final String version, final String operation) throws MissingServiceOperatorException {
        String msg = String.format("Could not load request operator for: %s, %s, %s. Please activate the according operation in the <a href=\"./operations\">settings</a>.", service, version,
                operation);
        LOG.error(msg);
        throw new MissingServiceOperatorException(msg);
    }

}
