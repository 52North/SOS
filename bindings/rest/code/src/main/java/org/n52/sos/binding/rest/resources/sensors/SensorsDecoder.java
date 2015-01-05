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
package org.n52.sos.binding.rest.resources.sensors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sosREST.x10.SensorDocument;
import net.opengis.sosREST.x10.SensorType;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.SimpleDataRecordType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.Constants;
import org.n52.sos.binding.rest.decode.ResourceDecoder;
import org.n52.sos.binding.rest.requests.BadRequestException;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.resources.OptionsRestRequest;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OperationNotSupportedException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.request.UpdateSensorRequest;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 */
public class SensorsDecoder extends ResourceDecoder {

    public SensorsDecoder() {
        bindingConstants = Constants.getInstance();
    }

    protected RestRequest decodeGetRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        // 1 identify type of request: global resource OR with id
        if (pathPayload != null && !pathPayload.isEmpty() && httpRequest.getQueryString() == null) {
            // 2.1 with id
            
            // 2.1.1 build describe sensor request with id pathPayload
            // create DescribeSensorRequest with service=SOS and version=2.0.0
            DescribeSensorRequest describeSensorRequest = createDescribeSensorRequest(pathPayload);

            return new GetSensorByIdRequest(describeSensorRequest);

        } else if (pathPayload == null && httpRequest.getQueryString() == null) {
            
            GetCapabilitiesRequest capabilitiesRequest = createGetCapabilitiesRequestWithContentSectionOnly();
            
            return new GetSensorsRequest(capabilitiesRequest);
            
        } else {
            String errorMsg = createBadGetRequestMessage(bindingConstants.getResourceSensors(),true,true,false);
            BadRequestException bR = new BadRequestException(errorMsg);
            throw new NoApplicableCodeException().causedBy(bR); 
        }
    }

    @Override
    protected RestRequest decodeDeleteRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
    	throw new OperationNotSupportedException(String.format("HTTP-DELETE + \"%s\"",
                bindingConstants.getResourceSensors()));
    }

    @Override
    protected RestRequest decodePostRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        if (isContentOfPostRequestValid(httpRequest))
        {
            InsertSensorRequest insertSensorRequest = new InsertSensorRequest();
            insertSensorRequest.setVersion(bindingConstants.getSosVersion());
            insertSensorRequest.setService(bindingConstants.getSosService());
            insertSensorRequest.setProcedureDescriptionFormat(bindingConstants.getDefaultDescribeSensorOutputFormat());

            // parse request in xml object and get procedure description
            XmlObject sensorPostContent = XmlHelper.parseXmlSosRequest(httpRequest);
            if (sensorPostContent instanceof SensorDocument)
            {
                SensorDocument xb_SensorRestDoc = (SensorDocument) sensorPostContent;
                SensorType xb_SensorRest = xb_SensorRestDoc.getSensor();
                AbstractProcessType xb_ProcessRest = xb_SensorRest.getProcess();

                SystemType xb_system = (SystemType) xb_ProcessRest.substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);

                SosProcedureDescription procedureDescription = createSosProcedureDescriptionFromSmlSystem(xb_system);

                insertSensorRequest.setProcedureDescription(procedureDescription);

                // xmlobject class sosREST:Sensor + links
                // requires:
                SosInsertionMetadata insertionMetadata = setInsertionMetadata(insertSensorRequest, xb_system);

                insertSensorRequest.setMetadata(insertionMetadata);

                return new SensorsPostRequest(insertSensorRequest,xb_system);
            }
        }
        return null;
    }

    private SosInsertionMetadata setInsertionMetadata(InsertSensorRequest insertSensorRequest,
            SystemType xb_system) throws OwsExceptionReport
    {
        // - one observable property
        insertSensorRequest.setObservableProperty(getObservablePropertiesFromSmlSystem(xb_system));
        
        SosInsertionMetadata insertionMetadata = new SosInsertionMetadata();
        Capabilities xb_insertionMetadata = null;
        
        if (xb_system.getCapabilitiesArray().length > 0) {
            for (Capabilities xb_Capability : xb_system.getCapabilitiesArray()) { 
                if (xb_Capability.isSetName() && xb_Capability.getName().equalsIgnoreCase(bindingConstants.getSmlCapabilityInsertMetadataName())) {
                    xb_insertionMetadata = xb_Capability;
                    break;
                }
            }
        }
        
        if (xb_insertionMetadata == null) {
        	throw new MissingParameterValueException(bindingConstants.getSmlCapabilityInsertMetadataName());
        }
        setAdditionalMetadata(insertionMetadata,xb_insertionMetadata);
        
        return insertionMetadata;
    }

    private void setAdditionalMetadata(SosInsertionMetadata insertionMetadata,
            Capabilities xb_insertionMetadata)
    {
        List<String> observationTypes = new ArrayList<String>();
        List<String> featureTypes = new ArrayList<String>();
        if (xb_insertionMetadata.isSetAbstractDataRecord()) {
            // substitute to swe:field with swe:Text
            SimpleDataRecordType xb_sdrt = (SimpleDataRecordType) xb_insertionMetadata.getAbstractDataRecord().substitute(SweConstants.QN_SIMPLEDATARECORD_SWE_101, SimpleDataRecordType.type);
            if (xb_sdrt.getFieldArray().length > 0) {
                for (AnyScalarPropertyType xb_fieldElement : xb_sdrt.getFieldArray()) {
                    if (xb_fieldElement.isSetText()) {
                        String name = xb_fieldElement.getName();
                        if (!xb_fieldElement.getText().getValue().isEmpty() && name != null) {
                            if (name.equalsIgnoreCase(bindingConstants.getSmlCapabilityObservationTypeName())) {
                                observationTypes.add(xb_fieldElement.getText().getValue());
                            } else if (name.equalsIgnoreCase(bindingConstants.getSmlCapabilityFeatureOfInterestTypeName())) {
                                featureTypes.add(xb_fieldElement.getText().getValue());
                            }
                        }
                    }
                }
            }
        }
        insertionMetadata.setFeatureOfInterestTypes(featureTypes);
        insertionMetadata.setObservationTypes(observationTypes);
    }

    private List<String> getObservablePropertiesFromSmlSystem(SystemType xb_system)
    {
        ArrayList<String> observableProperties = new ArrayList<String>();
        // first check outputs and than inputs
        if (isOutputListAvailable(xb_system)) {
            
            IoComponentPropertyType[] xb_Outputs = xb_system.getOutputs().getOutputList().getOutputArray();
            
            for (IoComponentPropertyType xb_Output : xb_Outputs) {
                if (isObservablePropertySetAndNotEmptyString(xb_Output)) {
                    observableProperties.add(xb_Output.getObservableProperty().getDefinition());
                }
            }
        }
        if (isInputListAvailable(xb_system)) {
            
            IoComponentPropertyType[] xb_Inputs = xb_system.getInputs().getInputList().getInputArray();
            
            for (IoComponentPropertyType xb_Input : xb_Inputs) {
                if (isObservablePropertySetAndNotEmptyString(xb_Input)) {
                    observableProperties.add(xb_Input.getObservableProperty().getDefinition());
                }
            }
        }
        observableProperties.trimToSize();
        return observableProperties;
    }

    private boolean isObservablePropertySetAndNotEmptyString(IoComponentPropertyType xb_Output)
    {
        return xb_Output.isSetObservableProperty() && xb_Output.getObservableProperty().isSetDefinition() && !xb_Output.getObservableProperty().getDefinition().isEmpty();
    }

    private boolean isOutputListAvailable(SystemType xb_system)
    {
        return xb_system.isSetOutputs() && xb_system.getOutputs().isSetOutputList() && xb_system.getOutputs().getOutputList().getOutputArray().length > 0;
    }
    
    private boolean isInputListAvailable(SystemType xb_system)
    {
        return xb_system.isSetInputs() && xb_system.getInputs().isSetInputList() && xb_system.getInputs().getInputList().getInputArray().length > 0;
    }

    private AbstractSensorML createSosProcedureDescriptionFromSmlSystem(SystemType xb_system) throws OwsExceptionReport
    {
        // TODO add some error handling
    	Object decodedObject = CodingHelper.decodeXmlObject(xb_system);
    	if (decodedObject instanceof AbstractSensorML)
    	{
    		return (AbstractSensorML) decodedObject;
    	}
        throw new NoApplicableCodeException().causedBy(
        		new IllegalArgumentException(
        				String.format("SystemType '%s' could not be decoded",
        						decodedObject!=null?decodedObject.getClass().getName():"null")));
    }

    @Override
    protected RestRequest decodePutRequest(HttpServletRequest httpRequest,
            String pathPayload) throws OwsExceptionReport
    {
        if (pathPayload != null) {
            UpdateSensorRequest updateSensorRequest = new UpdateSensorRequest();
            updateSensorRequest.setVersion(bindingConstants.getSosVersion());
            updateSensorRequest.setService(bindingConstants.getSosService());
            updateSensorRequest.setProcedureDescriptionFormat(bindingConstants.getDefaultDescribeSensorOutputFormat());
            updateSensorRequest.setProcedureIdentifier(pathPayload);
            
            XmlObject sensorPostContent = XmlHelper.parseXmlSosRequest(httpRequest);
            if(sensorPostContent instanceof SensorDocument) {
                SensorDocument xb_RestSensorDoc = (SensorDocument) sensorPostContent;
                SensorType xb_RestSensor = xb_RestSensorDoc.getSensor();
                AbstractProcessType xb_AbstractProcess = xb_RestSensor.getProcess();
                if (xb_AbstractProcess instanceof SystemType) {
                    updateSensorRequest.addProcedureDescriptionString(createSosProcedureDescriptionFromSmlSystem((SystemType) xb_AbstractProcess));
                }
                return new SensorsPutRequest(updateSensorRequest,(SystemType) xb_AbstractProcess);
            }
        }
        return null;
    }
    
    private DescribeSensorRequest createDescribeSensorRequest(String procedureId)
    {
        DescribeSensorRequest describeSensorRequest = new DescribeSensorRequest();
        describeSensorRequest.setVersion(bindingConstants.getSosVersion());
        describeSensorRequest.setService(bindingConstants.getSosService());
        describeSensorRequest.setProcedureDescriptionFormat(bindingConstants.getDefaultDescribeSensorOutputFormat());
        describeSensorRequest.setProcedure(procedureId);
        return describeSensorRequest;
    }
    
    @Override
    protected RestRequest decodeOptionsRequest(HttpServletRequest httpRequest,
            String pathPayload)
    {
        boolean isGlobal = false, isCollection = false;
        if (httpRequest != null && httpRequest.getQueryString() == null && pathPayload == null)
        {
            isGlobal = true;
            isCollection = true;
        }
        return new OptionsRestRequest(bindingConstants.getResourceSensors(),isGlobal,isCollection);
    }
    

}
