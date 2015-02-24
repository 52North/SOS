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
package org.n52.sos.service.admin.request.operator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.ds.OperationDAORepository;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.AdministratorException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.OperationNotSupportedException;
import org.n52.sos.exception.ows.concrete.EncoderResponseUnsupportedException;
import org.n52.sos.exception.ows.concrete.ErrorWhileSavingResponseToOutputStreamException;
import org.n52.sos.exception.ows.concrete.NoEncoderForResponseException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.DCP;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.ows.OwsOperationsMetadata;
import org.n52.sos.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosCapabilities;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.admin.AdministratorConstants.AdministratorParams;
import org.n52.sos.service.admin.request.AdminRequest;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.HTTPMethods;
import org.n52.sos.util.http.MediaTypes;


/**
 * @since 4.0.0
 *
 */
public class SosAdminRequestOperator implements AdminRequestOperator {

    /*
     * To support full dynamic loading of a new JAR, the Tomcat context.xml file has to be modified.
     * Add the attribute 'reloadable="true"' to <Context>.
     * Or you have to reload the Webapp.
     * Maybe there are other solution: CLassLoader, ...
     */
    private static final String KEY = "SOS";
    public static final String REQUEST_GET_CAPABILITIES = "GetCapabilities";
    public static final String REQUEST_UPDATE = "Update";
    public static final String UPDATE_ENCODER = "Encoder";
    public static final String UPDATE_DECODER = "Decoder";
    public static final String UPDATE_OPERATIONS = "Operations";
    public static final String UPDATE_SERVICES = "Services";
    public static final String UPDATE_BINDINGS = "Bindings";
    public static final String UPDATE_CONFIGURATION = "Configuration";
    private static final List<String> PARAMETERS = CollectionHelper.list(UPDATE_BINDINGS,
                                                                         UPDATE_CONFIGURATION,
                                                                         UPDATE_DECODER,
                                                                         UPDATE_ENCODER,
                                                                         UPDATE_OPERATIONS,
                                                                         UPDATE_SERVICES);

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public ServiceResponse receiveRequest(AdminRequest request) throws AdministratorException, OwsExceptionReport {
        try {
            if (request.getRequest().equalsIgnoreCase(REQUEST_GET_CAPABILITIES)) {
                return createCapabilities();
            } else if (request.getRequest().equalsIgnoreCase(REQUEST_UPDATE)) {
                return handleUpdateRequest(request);
            } else {
                throw new OperationNotSupportedException(request.getRequest());
            }
        } catch (ConfigurationException e) {
            throw new AdministratorException(e);
        }
    }

    private ServiceResponse handleUpdateRequest(AdminRequest request) throws ConfigurationException,
                                                                             OwsExceptionReport {
        String[] parameters = request.getParameters();
        if (parameters != null && parameters.length > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("The following resources are successful updated: ");
            CompositeOwsException exceptions = new CompositeOwsException();
            for (String parameter : parameters) {
                if (parameter.equalsIgnoreCase(UPDATE_BINDINGS)) {
                    BindingRepository.getInstance().update();
                    builder.append("Bindings");
                } else if (parameter.equalsIgnoreCase(UPDATE_CONFIGURATION)) {
                	BindingRepository.getInstance().update();
                	OperationDAORepository.getInstance().update();
                    CodingRepository.getInstance().updateDecoders();
                    CodingRepository.getInstance().updateEncoders();
                    ServiceOperatorRepository.getInstance().update();
                    builder.append("Configuration");
                } else if (parameter.equalsIgnoreCase(UPDATE_DECODER)) {
                	CodingRepository.getInstance().updateDecoders();
                    builder.append("Decoder");
                } else if (parameter.equalsIgnoreCase(UPDATE_ENCODER)) {
                	CodingRepository.getInstance().updateEncoders();
                    builder.append("Encoder");
                } else if (parameter.equalsIgnoreCase(UPDATE_OPERATIONS)) { 
                	RequestOperatorRepository.getInstance().update();
                    builder.append("Supported Operations");
                } else if (parameter.equalsIgnoreCase(UPDATE_SERVICES)) {
                	ServiceOperatorRepository.getInstance().update();
                    builder.append("Supported Services");
                } else {
                    exceptions.add(new InvalidParameterValueException(AdministratorParams.parameter, parameter));
                }
                builder.append(", ");
            }
            exceptions.throwIfNotEmpty();
            builder.delete(builder.lastIndexOf(", "), builder.length());
            return createServiceResponse(builder.toString());
        } else {
            throw new MissingParameterValueException(AdministratorParams.parameter);
        }
    }

    private ServiceResponse createCapabilities() throws OwsExceptionReport {
        GetCapabilitiesResponse response = new GetCapabilitiesResponse();
        response.setService(SosConstants.SOS);
        SosCapabilities sosCapabilities = new SosCapabilities(Sos2Constants.SERVICEVERSION);
        OwsOperationsMetadata operationsMetadata = new OwsOperationsMetadata();
        List<OwsOperation> opsMetadata = new ArrayList<OwsOperation>(2);
        opsMetadata.add(getOpsMetadataForCapabilities());
        opsMetadata.add(getOpsMetadataForUpdate());
        operationsMetadata.setOperations(opsMetadata);
        operationsMetadata.addCommonValue(AdministratorParams.service.name(), new OwsParameterValuePossibleValues(KEY));
        sosCapabilities.setOperationsMetadata(operationsMetadata);
        response.setCapabilities(sosCapabilities);
        return createServiceResponse(response);
    }

    private OwsOperation getOpsMetadataForCapabilities() {
        OwsOperation opsMeta = new OwsOperation();
        opsMeta.setOperationName(REQUEST_GET_CAPABILITIES);
        opsMeta.setDcp(getDCP());
        opsMeta.addAnyParameterValue(AdministratorParams.parameter);
        return opsMeta;
    }

    private OwsOperation getOpsMetadataForUpdate() {
        OwsOperation opsMeta = new OwsOperation();
        opsMeta.setOperationName(REQUEST_UPDATE);
        opsMeta.setDcp(getDCP());
        opsMeta.addPossibleValuesParameter(AdministratorParams.parameter, PARAMETERS);
        return opsMeta;
    }

    private Map<String, Set<DCP>> getDCP() {
        return Collections.singletonMap(HTTPMethods.GET, Collections.singleton(
        		new DCP(ServiceConfiguration.getInstance().getServiceURL() + "/admin?")));
    }

    private ServiceResponse createServiceResponse(String string) throws OwsExceptionReport {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(string.getBytes());
            return new ServiceResponse(baos, MediaTypes.TEXT_PLAIN);
        } catch (IOException e) {
            throw new ErrorWhileSavingResponseToOutputStreamException(e);
        }
    }

    private ServiceResponse createServiceResponse(GetCapabilitiesResponse response) throws OwsExceptionReport {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            EncoderKey key = new XmlEncoderKey(Sos2Constants.NS_SOS_20, GetCapabilitiesResponse.class);
            Encoder<?, GetCapabilitiesResponse> encoder = CodingRepository.getInstance().getEncoder(key);
            if (encoder != null) {
                Object encodedObject = encoder.encode(response);
                if (encodedObject instanceof XmlObject) {
                    ((XmlObject) encodedObject).save(baos, XmlOptionsHelper.getInstance().getXmlOptions());
                    return new ServiceResponse(baos,  MediaTypes.TEXT_XML);
                } else if (encodedObject instanceof ServiceResponse) {
                    return (ServiceResponse) encodedObject;
                } else {
                    throw new EncoderResponseUnsupportedException();
                }
            } else {
                throw new NoEncoderForResponseException();
            }

        } catch (IOException ioe) {
            throw new ErrorWhileSavingResponseToOutputStreamException(ioe);
        }
    }
}
