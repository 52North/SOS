/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.wsdl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.swes.SwesConstants;
import org.n52.shetland.util.StringHelper;
import org.n52.shetland.w3c.wsdl.Binding;
import org.n52.shetland.w3c.wsdl.BindingFault;
import org.n52.shetland.w3c.wsdl.BindingInput;
import org.n52.shetland.w3c.wsdl.BindingOperation;
import org.n52.shetland.w3c.wsdl.BindingOutput;
import org.n52.shetland.w3c.wsdl.Definitions;
import org.n52.shetland.w3c.wsdl.Fault;
import org.n52.shetland.w3c.wsdl.Import;
import org.n52.shetland.w3c.wsdl.Include;
import org.n52.shetland.w3c.wsdl.Input;
import org.n52.shetland.w3c.wsdl.Message;
import org.n52.shetland.w3c.wsdl.Operation;
import org.n52.shetland.w3c.wsdl.Output;
import org.n52.shetland.w3c.wsdl.Param;
import org.n52.shetland.w3c.wsdl.Part;
import org.n52.shetland.w3c.wsdl.Port;
import org.n52.shetland.w3c.wsdl.PortType;
import org.n52.shetland.w3c.wsdl.Schema;
import org.n52.shetland.w3c.wsdl.Service;
import org.n52.shetland.w3c.wsdl.Types;
import org.n52.shetland.w3c.wsdl.WSDLConstants;
import org.n52.shetland.w3c.wsdl.http.HttpAddress;
import org.n52.shetland.w3c.wsdl.http.HttpBinding;
import org.n52.shetland.w3c.wsdl.http.HttpOperation;
import org.n52.shetland.w3c.wsdl.http.HttpUrlEncoded;
import org.n52.shetland.w3c.wsdl.mime.MimeXml;
import org.n52.shetland.w3c.wsdl.soap.SoapAddress;
import org.n52.shetland.w3c.wsdl.soap.SoapBinding;
import org.n52.shetland.w3c.wsdl.soap.SoapBody;
import org.n52.shetland.w3c.wsdl.soap.SoapFault;
import org.n52.shetland.w3c.wsdl.soap.SoapOperation;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderFlags;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.EncodingContext;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class WSDLBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(WSDLBuilder.class);

    private static final String SOAP_LITERAL_USE = "literal";

    private static final String REQUEST_SUFFIX = "RequestMessage";

    private static final String RESPONSE_SUFFIX = "ResponseMessage";

    private static final String SOS_SOAP_12_PORT = "SosSoap12Port";

    private static final String SOS_KVP_PORT = "SosKvpPort";

    private static final String SOS_POX_PORT = "SosPoxPort";

    private static final String SOAP_ENPOINT_URL_PLACEHOLDER = "SOAP_ENDPOINT_URL";

    private final Definitions definitions;

    private Message faultMessage;

    private Service service;

    private Types types;

    private PortType postPortType;

    private PortType getPortType;

    private Binding soapBinding;

    private Binding kvpBinding;

    private Binding poxBinding;

    private Port soapPort;

    private Port kvpPort;

    private Port poxPort;

    private URI soapEndpoint;

    private URI poxEndpoint;

    private URI kvpEndpoint;

    private EncoderRepository encoderRepository;

    public WSDLBuilder(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
        this.definitions = new Definitions();
        this.setDefaultNamespaces();
        this.setDefaultImports();
    }

    private Definitions getDefinitions() {
        return this.definitions;
    }

    private Input createInput(Message requestMessage) {
        Input input = new Input();
        addParamValues(input, requestMessage);
        return input;
    }

    private Input createInput(Message requestMessage, URI action) {
        Input input = new Input();
        addParamValues(input, action, requestMessage);
        return input;
    }

    private Output createOutput(Message requestMessage) {
        Output output = new Output();
        addParamValues(output, requestMessage);
        return output;
    }

    private Output createOutput(Message requestMessage, URI action) {
        Output output = new Output();
        addParamValues(output, action, requestMessage);
        return output;
    }

    private Param addParamValues(Param param, Message message) {
        param.setName(message.getName());
        param.setMessage(new QName(WSDLConstants.NS_SOSW, message.getName(), WSDLConstants.NS_SOSW_PREFIX));
        return param;
    }

    private Param addParamValues(Param param, URI action, Message message) {
        addParamValues(param, message);
        param.setExtensionAttribute(WSDLConstants.QN_WSAM_ACTION, action.toString());
        return param;
    }

    private Fault createFault(String name, URI action, Message message) {
        Fault fault = new Fault(name, action, message.getQName());
        fault.setExtensionAttribute(WSDLConstants.QN_WSAM_ACTION, action.toString());
        return fault;
    }

    private Fault createFault(Fault fault) {
        return createFault(fault.getName(), fault.getAction());
    }

    private Fault createFault(String name, URI action) {
        return createFault(name, action, getFaultMessage());
    }

    private Operation addPostOperation(String name, QName request, QName response, Collection<Fault> faults) {
        Message requestMessage = createMessage(name + REQUEST_SUFFIX, request);
        Message responseMessage = createMessage(name + RESPONSE_SUFFIX, response);
        Input input = createInput(requestMessage);
        Output output = createOutput(responseMessage);
        return addOperation(getPostPortType(), name, input, output, faults);
    }

    private Operation addPostOperation(String name, QName request, QName response, URI requestAction,
            URI responseAction, Collection<Fault> faults) {
        Message requestMessage = createMessage(name + REQUEST_SUFFIX, request);
        Message responseMessage = createMessage(name + RESPONSE_SUFFIX, response);
        Input input = createInput(requestMessage, requestAction);
        Output output = createOutput(responseMessage, responseAction);
        return addOperation(getPostPortType(), name, input, output, faults);
    }

    private Operation addGetOperation(String name, QName request, QName response, Collection<Fault> faults) {
        Message requestMessage = createMessage(name + REQUEST_SUFFIX, request);
        Message responseMessage = createMessage(name + RESPONSE_SUFFIX, response);
        Input input = createInput(requestMessage);
        Output output = createOutput(responseMessage);
        return addOperation(getGetPortType(), name, input, output, faults);
    }

    private Operation addOperation(PortType portType, String name, Input input, Output output,
            Collection<Fault> faults) {
        Operation operation = portType.getOperation(name, input.getName(), output.getName());
        if (operation == null) {
            operation = new Operation(name);
            operation.setInput(input);
            operation.setOutput(output);
            for (Fault fault : faults) {
                operation.addFault(fault);
            }
            portType.addOperation(operation);
        }
        return operation;
    }

    private PortType getPostPortType() {
        if (this.postPortType == null) {
            this.postPortType = new PortType(WSDLConstants.EN_SOSW_SOS_POST_PORT_TYPE);
            getDefinitions().addPortType(this.postPortType);
        }
        return this.postPortType;
    }

    private PortType getGetPortType() {
        if (this.getPortType == null) {
            this.getPortType = new PortType(WSDLConstants.EN_SOSW_SOS_GET_PORT_TYPE);
            getDefinitions().addPortType(this.getPortType);
        }
        return this.getPortType;
    }

    private Types getTypes() {
        if (this.types == null) {
            this.types = new Types();
            getDefinitions().addType(this.types);
        }
        return this.types;
    }

    private Service getService() {
        if (this.service == null) {
            this.service = new Service(SosConstants.SOS);
            getDefinitions().addService(this.service);
        }
        return this.service;
    }

    private void setDefaultImports() {
        addSchemaImport(Sos2Constants.NS_SOS_20, Sos2Constants.SCHEMA_LOCATION_URL_SOS);
        addSchemaImport(OWSConstants.NS_OWS, OWSConstants.SCHEMA_LOCATION_URL_OWS);
        addSchemaImport(SwesConstants.NS_SWES_20, SwesConstants.SWES_20_SCHEMA_LOCATION.getSchemaFileUrl());
    }

    public WSDLBuilder addSchemaImport(String namespace, String schemaLocation) {
        getDefinitions().addImport(createSchemaImport(namespace, schemaLocation));
        getTypes().addExtensibilityElement(new Schema(namespace, new Include(namespace, schemaLocation)));
        return this;
    }

    private void setDefaultNamespaces() {
        getDefinitions().setTargetNamespace(WSDLConstants.NS_SOSW);
        addNamespace(WSDLConstants.NS_SOSW_PREFIX, WSDLConstants.NS_SOSW);
        addNamespace(WSDLConstants.NS_XSD_PREFIX, WSDLConstants.NS_XSD);
        addNamespace(WSDLConstants.NS_WSDL_PREFIX, WSDLConstants.NS_WSDL);
        addNamespace(WSDLConstants.NS_SOAP_12_PREFIX, WSDLConstants.NS_SOAP_12);
        addNamespace(WSDLConstants.NS_WSAM_PREFIX, WSDLConstants.NS_WSAM);
        addNamespace(WSDLConstants.NS_MIME_PREFIX, WSDLConstants.NS_MIME);
        addNamespace(WSDLConstants.NS_HTTP_PREFIX, WSDLConstants.NS_HTTP);
        addNamespace(WSDLConstants.NS_SOAP_PREFIX, WSDLConstants.NS_SOAP);
        addNamespace(OWSConstants.NS_OWS_PREFIX, OWSConstants.NS_OWS);
        addNamespace(Sos2Constants.NS_SOS_PREFIX, Sos2Constants.NS_SOS_20);
        addNamespace(SwesConstants.NS_SWES_PREFIX, SwesConstants.NS_SWES_20);
    }

    public WSDLBuilder addNamespace(String prefix, String namespace) {
        getDefinitions().addNamespace(prefix, namespace);
        return this;
    }

    private Message createMessage(String name, QName qname) {
        Part part = new Part(WSDLConstants.MESSAGE_PART);
        part.setElement(qname);
        Message message = new Message(name);
        message.addPart(part);
        getDefinitions().addMessage(message);
        return message;
    }

    private Message getFaultMessage() {
        if (this.faultMessage == null) {
            Part part = new Part("fault");
            part.setElement(OWSConstants.QN_EXCEPTION);
            this.faultMessage = new Message("ExceptionMessage");
            this.faultMessage.addPart(part);
            getDefinitions().addMessage(this.faultMessage);
        }
        return this.faultMessage;
    }

    private Import createSchemaImport(String namespace, String schemaLocation) {
        Import wsdlImport = new Import();
        wsdlImport.setLocationURI(schemaLocation);
        wsdlImport.setNamespaceURI(namespace);
        return wsdlImport;
    }

    public String build() {
        XmlObject xmlObject = null;
        try {
            Encoder<XmlObject, Definitions> encoder =
                    encoderRepository.getEncoder(new XmlEncoderKey(WSDLConstants.NS_WSDL, Definitions.class));
            if (encoder != null) {
                xmlObject = encoder.encode(getDefinitions(),
                        EncodingContext.of(EncoderFlags.ENCODER_REPOSITORY, encoderRepository));
                if (xmlObject != null) {
                    return xmlObject.xmlText();
                }
            } else {
                xmlObject = read("/wsdl.xml");
                if (xmlObject != null) {
                    String wsdl = xmlObject.xmlText();
                    wsdl = wsdl.replaceAll(SOAP_ENPOINT_URL_PLACEHOLDER, getSoapEndpoint().toString());
                    return wsdl;
                }
            }
            return getDefault();
        } catch (EncodingException | DecodingException | IOException ex) {
            LOGGER.error("Error while loading WSDL file!", ex);
            return getDefault();
        }
    }

    private String getDefault() {
        XmlString xmlObject = XmlString.Factory.newInstance();
        xmlObject.setStringValue("Due to extensive refactoring the WSDL feature is temporary not supported!");
        return xmlObject.xmlText();
    }

    private InputStream getDocumentAsStream(String filename) {
        return this.getClass()
                .getResourceAsStream(filename);
    }

    private XmlObject read(String path) throws DecodingException, IOException {
        try (InputStream stream = getDocumentAsStream(path)) {
            String string = StringHelper.convertStreamToString(stream);
            return XmlHelper.parseXmlString(string);
        }
    }

    public WSDLBuilder setSoapEndpoint(URI endpoint) {
        this.soapEndpoint = endpoint;
        return this;
    }

    public WSDLBuilder setPoxEndpoint(URI endpoint) {
        this.poxEndpoint = endpoint;
        return this;
    }

    public WSDLBuilder setKvpEndpoint(URI endpoint) {
        this.kvpEndpoint = endpoint;
        return this;
    }

    private URI getSoapEndpoint() {
        return this.soapEndpoint;
    }

    private URI getKvpEndpoint() {
        return this.kvpEndpoint;
    }

    private URI getPoxEndpoint() {
        return this.poxEndpoint;
    }

    private String getName(Metadata m) {
        return m.getName() + ((m.getVersion() != null) ? m.getVersion()
                .replace(".", "") : "");
    }

    public WSDLBuilder addPoxOperation(Metadata m) {
        List<Fault> faults = new ArrayList<Fault>(m.getFaults()
                .size());
        for (Fault f : m.getFaults()) {
            faults.add(createFault(f));
        }
        return addPoxOperation(getName(m), m.getRequest(), m.getResponse(), faults);
    }

    private WSDLBuilder addPoxOperation(String name, QName request, QName response, Collection<Fault> faults) {
        addPostOperation(name, request, response, faults);
        addPoxBindingOperation(name, faults);
        addPoxPort();
        return this;
    }

    public WSDLBuilder addKvpOperation(Metadata m) {
        List<Fault> faults = new ArrayList<Fault>(m.getFaults()
                .size());
        for (Fault f : m.getFaults()) {
            faults.add(createFault(f));
        }
        return addKvpOperation(getName(m), m.getRequest(), m.getResponse(), faults);
    }

    private WSDLBuilder addKvpOperation(String name, QName request, QName response, Collection<Fault> faults) {
        addGetOperation(name, request, response, faults);
        addKvpBindingOperation(name, faults);
        addKvpPort();
        return this;
    }

    public WSDLBuilder addSoapOperation(Metadata m) {
        List<Fault> faults = new ArrayList<Fault>(m.getFaults()
                .size());
        for (Fault f : m.getFaults()) {
            faults.add(createFault(f));
        }
        return addSoapOperation(getName(m), m.getRequest(), m.getResponse(), m.getRequestAction(),
                m.getResponseAction(), faults);
    }

    private WSDLBuilder addSoapOperation(String name, QName request, QName response, URI requestAction,
            URI responseAction, Collection<Fault> faults) {
        addPostOperation(name, request, response, requestAction, responseAction, faults);
        addSoap12BindingOperation(name, requestAction, faults);
        addSoap12Port();
        return this;
    }

    private void addSoapPort() {
        if (this.soapPort == null) {
            this.soapPort = new Port();
            this.soapPort.setBinding(WSDLConstants.QN_SOSW_SOAP_BINDING);
            this.soapPort.setName(SOS_SOAP_12_PORT);
            this.soapPort.addExtensibilityElement(new SoapAddress(getSoapEndpoint()));
            getService().addPort(this.soapPort);
        }
    }

    private void addSoap12Port() {
        if (this.soapPort == null) {
            this.soapPort = new Port();
            this.soapPort.setBinding(WSDLConstants.QN_SOSW_SOAP_BINDING);
            this.soapPort.setName(SOS_SOAP_12_PORT);
            this.soapPort.addExtensibilityElement(new SoapAddress(getSoapEndpoint()));
            getService().addPort(this.soapPort);
        }
    }

    private void addPoxPort() {
        if (this.poxPort == null) {
            this.poxPort = new Port();
            this.poxPort.setBinding(WSDLConstants.QN_SOSW_POX_BINDING);
            this.poxPort.setName(SOS_POX_PORT);
            this.poxPort.addExtensibilityElement(new HttpAddress(getPoxEndpoint()));
            getService().addPort(this.poxPort);
        }
    }

    private void addKvpPort() {
        if (this.kvpPort == null) {
            this.kvpPort = new Port();
            this.kvpPort.setBinding(WSDLConstants.QN_SOSW_KVP_BINDING);
            this.kvpPort.setName(SOS_KVP_PORT);
            this.kvpPort.addExtensibilityElement(new HttpAddress(getKvpEndpoint()));
            getService().addPort(this.kvpPort);
        }
    }

    private BindingOperation addSoapBindingOperation(String name, Operation operation, URI action,
            Collection<Fault> faults) {
        BindingOperation bindingOperation = new BindingOperation(name);

        bindingOperation.addExtensibilityElement(new SoapOperation(WSDLConstants.SOAP_DOCUMENT_STYLE, action));

        BindingInput input = new BindingInput();
        input.addExtensibilityElement(new SoapBody(SOAP_LITERAL_USE));
        bindingOperation.setInput(input);

        BindingOutput output = new BindingOutput();
        output.addExtensibilityElement(new SoapBody(SOAP_LITERAL_USE));
        bindingOperation.setOutput(output);

        for (Fault fault : faults) {
            BindingFault bindingFault = new BindingFault(fault.getName());
            bindingFault.addExtensibilityElement(new SoapFault(fault.getName(), SOAP_LITERAL_USE));
            bindingOperation.addBindingFault(bindingFault);
        }

        getSoap12Binding().addBindingOperation(bindingOperation);
        return bindingOperation;
    }

    private BindingOperation addSoap12BindingOperation(String name, URI action, Collection<Fault> faults) {
        BindingOperation bindingOperation = new BindingOperation(name);

        bindingOperation.addExtensibilityElement(new SoapOperation(WSDLConstants.SOAP_DOCUMENT_STYLE, action));

        BindingInput input = new BindingInput();
        input.addExtensibilityElement(new SoapBody(SOAP_LITERAL_USE));
        bindingOperation.setInput(input);

        BindingOutput output = new BindingOutput();
        output.addExtensibilityElement(new SoapBody(SOAP_LITERAL_USE));
        bindingOperation.setOutput(output);

        for (Fault fault : faults) {
            BindingFault bindingFault = new BindingFault(fault.getName());
            bindingFault.addExtensibilityElement(new SoapFault(fault.getName(), SOAP_LITERAL_USE));
            bindingOperation.addBindingFault(bindingFault);
        }

        getSoap12Binding().addBindingOperation(bindingOperation);
        return bindingOperation;
    }

    private BindingOperation addPoxBindingOperation(String name, Collection<Fault> faults) {
        BindingOperation bindingOperation = new BindingOperation(name);

        bindingOperation.addExtensibilityElement(new HttpOperation(""));

        BindingInput input = new BindingInput();
        input.addExtensibilityElement(new HttpUrlEncoded());
        bindingOperation.setInput(input);

        BindingOutput output = new BindingOutput();
        output.addExtensibilityElement(new MimeXml());
        bindingOperation.setOutput(output);

        for (Fault fault : faults) {
            BindingFault bindingFault = new BindingFault(fault.getName());
            bindingOperation.addBindingFault(bindingFault);
        }

        getPoxBinding().addBindingOperation(bindingOperation);
        return bindingOperation;
    }

    private BindingOperation addKvpBindingOperation(String name, Collection<Fault> faults) {
        BindingOperation bindingOperation = new BindingOperation(name);

        bindingOperation.addExtensibilityElement(new HttpOperation(""));

        BindingInput input = new BindingInput();
        input.addExtensibilityElement(new HttpUrlEncoded());
        bindingOperation.setInput(input);

        BindingOutput output = new BindingOutput();
        output.addExtensibilityElement(new MimeXml());
        bindingOperation.setOutput(output);

        for (Fault fault : faults) {
            BindingFault bindingFault = new BindingFault(fault.getName());
            bindingOperation.addBindingFault(bindingFault);
        }

        getKvpBinding().addBindingOperation(bindingOperation);
        return bindingOperation;
    }

    private Binding getSoapBinding() {
        if (this.soapBinding == null) {
            this.soapBinding =
                    new Binding(WSDLConstants.EN_SOSW_SOS_SOAP_BINDING, WSDLConstants.QN_SOSW_POST_PORT_TYPE);
            this.soapBinding.addExtensibilityElement(
                    new SoapBinding(WSDLConstants.SOAP_DOCUMENT_STYLE, WSDLConstants.SOAP_BINDING_HTTP_TRANSPORT));
            getDefinitions().addBinding(this.soapBinding);
        }
        return this.soapBinding;
    }

    private Binding getSoap12Binding() {
        if (this.soapBinding == null) {
            this.soapBinding =
                    new Binding(WSDLConstants.EN_SOSW_SOS_SOAP_BINDING, WSDLConstants.QN_SOSW_POST_PORT_TYPE);
            this.soapBinding.addExtensibilityElement(
                    new SoapBinding(WSDLConstants.SOAP_DOCUMENT_STYLE, WSDLConstants.SOAP_12_BINDING_HTTP_TRANSPORT));
            getDefinitions().addBinding(this.soapBinding);
        }
        return this.soapBinding;
    }

    private Binding getPoxBinding() {
        if (this.poxBinding == null) {
            this.poxBinding = new Binding(WSDLConstants.EN_SOSW_SOS_POX_BINDING, WSDLConstants.QN_SOSW_POST_PORT_TYPE);
            this.poxBinding.addExtensibilityElement(new HttpBinding(WSDLConstants.POX_HTTP_VERB));
            getDefinitions().addBinding(this.poxBinding);
        }
        return this.poxBinding;
    }

    private Binding getKvpBinding() {
        if (this.kvpBinding == null) {
            this.kvpBinding = new Binding(WSDLConstants.EN_SOSW_SOS_KVP_BINDING, WSDLConstants.QN_SOSW_GET_PORT_TYPE);
            this.kvpBinding.addExtensibilityElement(new HttpBinding(WSDLConstants.KVP_HTTP_VERB));
            getDefinitions().addBinding(this.kvpBinding);
        }
        return this.kvpBinding;
    }

}
