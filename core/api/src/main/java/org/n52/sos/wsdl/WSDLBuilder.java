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
package org.n52.sos.wsdl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.wsdl.WSDLConstants.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class WSDLBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(WSDLBuilder.class);

    private static final String SOAP_LITERAL_USE = "literal";

    private static final String REQUEST_SUFFIX = "RequestMessage";

    private static final String RESPONSE_SUFFIX = "ResponseMessage";

    private static final String SOS_SOAP_12_PORT = "SosSoap12Port";

    private static final String SOS_KVP_PORT = "SosKvpPort";

    private static final String SOS_POX_PORT = "SosPoxPort";
    
    private static final String SOAP_ENPOINT_URL_PLACEHOLDER = "SOAP_ENDPOINT_URL";

    // private final WSDLFactory factory;
    //
    // private final ExtensionRegistry extensionRegistry;
    //
    // private final Definition definitions;
    //
    // private Message faultMessage;
    //
    // private Service service;
    //
    // private Types types;
    //
    // private PortType postPortType, getPortType;
    //
    // private Binding soapBinding, kvpBinding, poxBinding;
    //
    // private Port soapPort, kvpPort, poxPort;

    private URI soapEndpoint, poxEndpoint, kvpEndpoint;

    public WSDLBuilder() {
    }

    // public WSDLBuilder() {
    // this.factory = WSDLFactory.newInstance();
    // this.extensionRegistry = getFactory().newPopulatedExtensionRegistry();
    // this.definitions = getFactory().newDefinition();
    // this.setDefaultNamespaces();
    // this.setDefaultImports();
    // }
    //
    // private WSDLFactory getFactory() {
    // return this.factory;
    // }
    //
    // private ExtensionRegistry getExtensionRegistry() {
    // return this.extensionRegistry;
    // }
    //
    // private Definition getDefinitions() {
    // return this.definitions;
    // }
    //
    // private Input createInput(Message message) {
    // Input input = getDefinitions().createInput();
    // input.setName(message.getQName().getLocalPart());
    // input.setMessage(message);
    // return input;
    // }
    //
    // private Input createInput(URI action, Message message) {
    // Input input = createInput(message);
    // input.setExtensionAttribute(QN_WSAM_ACTION, action.toString());
    // return input;
    // }
    //
    // private Output createOutput(Message message) {
    // Output output = getDefinitions().createOutput();
    // output.setName(message.getQName().getLocalPart());
    // output.setMessage(message);
    // return output;
    // }
    //
    // private Output createOutput(URI action, Message message) {
    // Output output = createOutput(message);
    // output.setExtensionAttribute(QN_WSAM_ACTION, action.toString());
    // return output;
    // }
    //
    // private Fault createFault(String name, Message message) {
    // Fault fault = getDefinitions().createFault();
    // fault.setName(name);
    // fault.setMessage(message);
    // return fault;
    // }
    //
    // private Fault createFault(String name, URI action, Message message) {
    // Fault fault = createFault(name, message);
    // fault.setExtensionAttribute(QN_WSAM_ACTION, action.toString());
    // return fault;
    // }
    //
    // private Fault createFault(WSDLFault fault) {
    // return createFault(fault.getName(), fault.getAction());
    // }
    //
    // private Fault createFault(String name, URI action) {
    // return createFault(name, action, getFaultMessage());
    // }
    //
    // private Operation addPostOperation(String name, QName request, QName
    // response, Collection<Fault> faults) {
    // Message requestMessage = createMessage(name + REQUEST_SUFFIX, request);
    // Message responseMessage = createMessage(name + RESPONSE_SUFFIX,
    // response);
    // Input input = createInput(requestMessage);
    // Output output = createOutput(responseMessage);
    // return addOperation(getPostPortType(), name, input, output, faults);
    // }
    //
    // private Operation addPostOperation(String name, QName request, QName
    // response, URI requestAction,
    // URI responseAction, Collection<Fault> faults) {
    // Message requestMessage = createMessage(name + REQUEST_SUFFIX, request);
    // Message responseMessage = createMessage(name + RESPONSE_SUFFIX,
    // response);
    // Input input = createInput(requestAction, requestMessage);
    // Output output = createOutput(responseAction, responseMessage);
    // return addOperation(getPostPortType(), name, input, output, faults);
    // }
    //
    // private Operation addGetOperation(String name, QName request, QName
    // response, Collection<Fault> faults) {
    // Message requestMessage = createMessage(name + REQUEST_SUFFIX, request);
    // Message responseMessage = createMessage(name + RESPONSE_SUFFIX,
    // response);
    // Input input = createInput(requestMessage);
    // Output output = createOutput(responseMessage);
    // return addOperation(getGetPortType(), name, input, output, faults);
    // }
    //
    // private Operation addOperation(PortType portType, String name, Input
    // input, Output output, Collection<Fault> faults) {
    // Operation operation = portType.getOperation(name, input.getName(),
    // output.getName());
    // if (operation == null) {
    // operation = getDefinitions().createOperation();
    // operation.setName(name);
    // operation.setInput(input);
    // operation.setOutput(output);
    // operation.setUndefined(false);
    // for (Fault fault : faults) {
    // operation.addFault(fault);
    // }
    // portType.addOperation(operation);
    // }
    // return operation;
    // }
    //
    // private PortType getPostPortType() {
    // if (this.postPortType == null) {
    // this.postPortType = getDefinitions().createPortType();
    // this.postPortType.setQName(QN_SOSW_POST_PORT_TYPE);
    // this.postPortType.setUndefined(false);
    // getDefinitions().addPortType(this.postPortType);
    // }
    // return this.postPortType;
    // }
    //
    // private PortType getGetPortType() {
    // if (this.getPortType == null) {
    // this.getPortType = getDefinitions().createPortType();
    // this.getPortType.setQName(QN_SOSW_GET_PORT_TYPE);
    // this.getPortType.setUndefined(false);
    // getDefinitions().addPortType(this.getPortType);
    // }
    // return this.getPortType;
    // }
    //
    // private Types getTypes() {
    // if (this.types == null) {
    // this.types = getDefinitions().createTypes();
    // getDefinitions().setTypes(this.types);
    // }
    // return this.types;
    // }
    //
    // private Service getService() {
    // if (this.service == null) {
    // this.service = getDefinitions().createService();
    // this.service.setQName(QN_SOSW_SERVICE);
    // getDefinitions().addService(this.service);
    // }
    // return this.service;
    // }

    // private void setDefaultImports() {
    // addSchemaImport(NS_SOS_20, SCHEMA_LOCATION_URL_SOS);
    // addSchemaImport(NS_OWS, SCHEMA_LOCATION_URL_OWS);
    // addSchemaImport(SwesConstants.NS_SWES_20,
    // SwesConstants.SCHEMA_LOCATION_URL_SWES_20);
    // }
    //
    // public WSDLBuilder addSchemaImport(String namespace, String
    // schemaLocation) {
    // // getDefinitions().addImport(createSchemaImport(namespace,
    // schemaLocation));
    // getTypes().addExtensibilityElement(createExtensibilityElement(namespace,
    // schemaLocation));
    // return this;
    // }
    //
    // private void setDefaultNamespaces() {
    // getDefinitions().setTargetNamespace(NS_SOSW);
    // addNamespace(NS_SOSW_PREFIX, NS_SOSW);
    // addNamespace(NS_XSD_PREFIX, NS_XSD);
    // addNamespace(NS_WSDL_PREFIX, NS_WSDL);
    // addNamespace(NS_SOAP_12_PREFIX, NS_SOAP_12);
    // addNamespace(NS_WSAM_PREFIX, NS_WSAM);
    // addNamespace(NS_MIME_PREFIX, NS_MIME);
    // addNamespace(NS_HTTP_PREFIX, NS_HTTP);
    // addNamespace(NS_OWS_PREFIX, NS_OWS);
    // addNamespace(NS_SOS_PREFIX, NS_SOS_20);
    // addNamespace(SwesConstants.NS_SWES_PREFIX, SwesConstants.NS_SWES_20);
    // }
    //
    // public WSDLBuilder addNamespace(String prefix, String namespace) {
    // getDefinitions().addNamespace(prefix, namespace);
    // return this;
    // }
    //
    // private Message createMessage(String name, QName qname) {
    // Message message = getDefinitions().createMessage();
    // Part part = getDefinitions().createPart();
    // part.setElementName(qname);
    // part.setName(MESSAGE_PART);
    // message.addPart(part);
    // message.setQName(new QName(NS_SOSW, name));
    // message.setUndefined(false);
    // getDefinitions().addMessage(message);
    // return message;
    // }
    //
    // private Message getFaultMessage() {
    // if (this.faultMessage == null) {
    // this.faultMessage = getDefinitions().createMessage();
    // Part part = getDefinitions().createPart();
    // part.setElementName(QN_EXCEPTION);
    // part.setName("fault");
    // this.faultMessage.addPart(part);
    // this.faultMessage.setQName(new QName(NS_SOSW, "ExceptionMessage"));
    // this.faultMessage.setUndefined(false);
    // getDefinitions().addMessage(this.faultMessage);
    // }
    // return this.faultMessage;
    // }
    //
    // private Import createSchemaImport(String namespace, String
    // schemaLocation) {
    // Import wsdlImport = getDefinitions().createImport();
    // wsdlImport.setLocationURI(schemaLocation);
    // wsdlImport.setNamespaceURI(namespace);
    // return wsdlImport;
    // }
    //
    // private ExtensibilityElement createExtensibilityElement(String namespace,
    // String schemaLocation) {
    // Schema schema = (Schema)
    // getExtensionRegistry().createExtension(Types.class, QN_XSD_SCHEMA);
    // SchemaReference ref = schema.createInclude();
    // ref.setReferencedSchema(schema);
    // ref.setSchemaLocationURI(schemaLocation);
    // ref.setId(namespace);
    // schema.setElementType(QN_XSD_SCHEMA);
    // schema.setElement(buildSchemaImport(namespace, schemaLocation));
    // schema.addInclude(ref);
    // return schema;
    // }
    //
    // private Element buildSchemaImport(String namespace, String
    // schemaLocation) {
    // try {
    // DocumentBuilderFactory documentFactory =
    // DocumentBuilderFactory.newInstance();
    // DocumentBuilder builder = documentFactory.newDocumentBuilder();
    // Document document = builder.newDocument();
    // Element schema = document.createElementNS(NS_XSD, EN_XSD_SCHEMA);
    // Element include = document.createElementNS(NS_XSD, EN_XSD_INCLUDE);
    // include.setAttribute(AN_XSD_SCHEMA_LOCATION, schemaLocation);
    // include.setPrefix(NS_XSD_PREFIX);
    // schema.setAttribute(AN_XSD_TARGET_NAMESPACE, namespace);
    // schema.setAttribute(AN_XSD_ELEMENT_FORM_DEFAULT,
    // QUALIFIED_ELEMENT_FORM_DEFAULT);
    // schema.setPrefix(NS_XSD_PREFIX);
    // schema.appendChild(include);
    // return schema;
    // } catch (ParserConfigurationException ex) {
    // throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
    // ex.getMessage(), ex);
    // }
    // }

    public String build() {
        // WSDLWriter wsdlWriter = getFactory().newWSDLWriter();
        // StringWriter writer = new StringWriter();
        // wsdlWriter.writeWSDL(getDefinitions(), writer);
        XmlObject xmlObject = null;
        try {
            xmlObject = read("/wsdl.xml");
            if (xmlObject != null) {
                String wsdl = xmlObject.xmlText();
                wsdl = wsdl.replaceAll(SOAP_ENPOINT_URL_PLACEHOLDER, getSoapEndpoint().toString());
                return wsdl;
            } else {
                return getDefault();
            }
        } catch (OwsExceptionReport e) {
            LOGGER.error("Error while loading WSDL file!", e);
            return getDefault();
        }
    }

    private String getDefault() {
        XmlString xmlObject = XmlString.Factory.newInstance();
        xmlObject.setStringValue("Due to extensive refactoring the WSDL feature is temporary not supported!");
        return xmlObject.xmlText();
    }

    private InputStream getDocumentAsStream(String filename) {
        return Configurator.getInstance().getClass().getResourceAsStream(filename);
    }

    private XmlObject read(String path) throws OwsExceptionReport {
        InputStream stream = null;
        try {
            stream = getDocumentAsStream(path);
            String string = StringHelper.convertStreamToString(stream);
            XmlObject xml = XmlHelper.parseXmlString(string);
            return xml;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LOGGER.error("Error while closing InputStream!", e);
                }
            }
        }
    }

    @VisibleForTesting
    ServiceConfiguration getServiceConfig() {
        return ServiceConfiguration.getInstance();
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

    private String getName(WSDLOperation o) {
        return o.getName() + ((o.getVersion() != null) ? o.getVersion().replace(".", "") : "");
    }

    // public WSDLBuilder addPoxOperation(WSDLOperation o) {
    // List<Fault> faults = new ArrayList<Fault>(o.getFaults().size());
    // for (WSDLFault f : o.getFaults()) {
    // faults.add(createFault(f));
    // }
    // return addPoxOperation(getName(o), o.getRequest(), o.getResponse(),
    // faults);
    // }
    //
    // public WSDLBuilder addKvpOperation(WSDLOperation o) {
    // List<Fault> faults = new ArrayList<Fault>(o.getFaults().size());
    // for (WSDLFault f : o.getFaults()) {
    // faults.add(createFault(f));
    // }
    // return addKvpOperation(getName(o), o.getRequest(), o.getResponse(),
    // faults);
    // }
    //
    // public WSDLBuilder addSoapOperation(WSDLOperation o) {
    // List<Fault> faults = new ArrayList<Fault>(o.getFaults().size());
    // for (WSDLFault f : o.getFaults()) {
    // faults.add(createFault(f));
    // }
    // return addSoapOperation(getName(o), o.getRequest(), o.getResponse(),
    // o.getRequestAction(),
    // o.getResponseAction(), faults);
    // }
    //
    // private WSDLBuilder addSoapOperation(String name, QName request, QName
    // response, URI requestAction,
    // URI responseAction, Collection<Fault> faults) {
    // Operation operation = addPostOperation(name, request, response,
    // requestAction, responseAction, faults);
    // addSoap12BindingOperation(name, operation, requestAction, faults);
    // addSoap12Port();
    // return this;
    // }
    //
    // private WSDLBuilder addPoxOperation(String name, QName request, QName
    // response, Collection<Fault> faults) {
    // Operation operation = addPostOperation(name, request, response, faults);
    // addPoxBindingOperation(name, operation, faults);
    // addPoxPort();
    // return this;
    // }
    //
    // private WSDLBuilder addKvpOperation(String name, QName request, QName
    // response, Collection<Fault> faults) {
    // Operation operation = addGetOperation(name, request, response, faults);
    // addKvpBindingOperation(name, operation, faults);
    // addKvpPort();
    // return this;
    // }
    //
    // private void addSoapPort() {
    // if (this.soapPort == null) {
    // this.soapPort = getDefinitions().createPort();
    // this.soapPort.setBinding(getSoap12Binding());
    // this.soapPort.setName(SOS_SOAP_12_PORT);
    // SOAPAddress soapAddress =
    // (SOAPAddress) getExtensionRegistry().createExtension(Port.class,
    // QN_SOAP_12_ADDRESS);
    // soapAddress.setLocationURI(getSoapEndpoint().toString());
    // this.soapPort.addExtensibilityElement(soapAddress);
    // getService().addPort(this.soapPort);
    // }
    // }
    //
    // private void addSoap12Port() {
    // if (this.soapPort == null) {
    // this.soapPort = getDefinitions().createPort();
    // this.soapPort.setBinding(getSoap12Binding());
    // this.soapPort.setName(SOS_SOAP_12_PORT);
    // SOAP12Address soapAddress =
    // (SOAP12Address) getExtensionRegistry().createExtension(Port.class,
    // QN_SOAP_12_ADDRESS);
    // soapAddress.setLocationURI(getSoapEndpoint().toString());
    // this.soapPort.addExtensibilityElement(soapAddress);
    // getService().addPort(this.soapPort);
    // }
    // }
    //
    // private void addPoxPort() {
    // if (this.poxPort == null) {
    // this.poxPort = getDefinitions().createPort();
    // this.poxPort.setBinding(getPoxBinding());
    // this.poxPort.setName(SOS_POX_PORT);
    // HTTPAddress httpAddress =
    // (HTTPAddress) getExtensionRegistry().createExtension(Port.class,
    // QN_HTTP_ADDRESS);
    // httpAddress.setLocationURI(getPoxEndpoint().toString());
    // this.poxPort.addExtensibilityElement(httpAddress);
    // getService().addPort(this.poxPort);
    // }
    // }
    //
    // private void addKvpPort() {
    // if (this.kvpPort == null) {
    // this.kvpPort = getDefinitions().createPort();
    // this.kvpPort.setBinding(getKvpBinding());
    // this.kvpPort.setName(SOS_KVP_PORT);
    // HTTPAddress httpAddress =
    // (HTTPAddress) getExtensionRegistry().createExtension(Port.class,
    // QN_HTTP_ADDRESS);
    // httpAddress.setLocationURI(getKvpEndpoint().toString());
    // this.kvpPort.addExtensibilityElement(httpAddress);
    // getService().addPort(this.kvpPort);
    // }
    // }
    //
    // private BindingOperation addSoapBindingOperation(String name, Operation
    // operation, URI action,
    // Collection<Fault> faults) {
    // BindingOperation bindingOperation =
    // getDefinitions().createBindingOperation();
    // bindingOperation.setName(name);
    //
    // SOAPOperation soapOperation =
    // (SOAPOperation)
    // getExtensionRegistry().createExtension(BindingOperation.class,
    // QN_SOAP_OPERATION);
    // soapOperation.setStyle(SOAP_DOCUMENT_STYLE);
    // soapOperation.setSoapActionURI(action.toString());
    // bindingOperation.addExtensibilityElement(soapOperation);
    //
    // bindingOperation.setOperation(operation);
    //
    // BindingInput bindingInput = getDefinitions().createBindingInput();
    // SOAPBody bindingInputSoapBody =
    // (SOAPBody) getExtensionRegistry().createExtension(BindingInput.class,
    // QN_SOAP_12_BODY);
    // bindingInputSoapBody.setUse(SOAP_LITERAL_USE);
    // bindingInput.addExtensibilityElement(bindingInputSoapBody);
    // bindingOperation.setBindingInput(bindingInput);
    //
    // BindingOutput bindingOutput = getDefinitions().createBindingOutput();
    // SOAPBody bindingOutputSoapBody =
    // (SOAPBody) getExtensionRegistry().createExtension(BindingInput.class,
    // QN_SOAP_12_BODY);
    // bindingOutputSoapBody.setUse(SOAP_LITERAL_USE);
    // bindingOutput.addExtensibilityElement(bindingOutputSoapBody);
    // bindingOperation.setBindingOutput(bindingOutput);
    //
    // for (Fault fault : faults) {
    // BindingFault bindingFault = getDefinitions().createBindingFault();
    // bindingFault.setName(fault.getName());
    // SOAPFault soapFault =
    // (SOAPFault) getExtensionRegistry().createExtension(BindingFault.class,
    // QN_SOAP_12_FAULT);
    // soapFault.setUse(SOAP_LITERAL_USE);
    // soapFault.setName(fault.getName());
    // bindingFault.addExtensibilityElement(soapFault);
    // bindingOperation.addBindingFault(bindingFault);
    // }
    //
    // getSoap12Binding().addBindingOperation(bindingOperation);
    // return bindingOperation;
    // }
    //
    // private BindingOperation addSoap12BindingOperation(String name, Operation
    // operation, URI action,
    // Collection<Fault> faults) {
    // BindingOperation bindingOperation =
    // getDefinitions().createBindingOperation();
    // bindingOperation.setName(name);
    //
    // SOAP12Operation soapOperation =
    // (SOAP12Operation)
    // getExtensionRegistry().createExtension(BindingOperation.class,
    // QN_SOAP_OPERATION);
    // soapOperation.setStyle(SOAP_DOCUMENT_STYLE);
    // soapOperation.setSoapActionURI(action.toString());
    // bindingOperation.addExtensibilityElement(soapOperation);
    //
    // bindingOperation.setOperation(operation);
    //
    // BindingInput bindingInput = getDefinitions().createBindingInput();
    // SOAP12Body bindingInputSoapBody =
    // (SOAP12Body) getExtensionRegistry().createExtension(BindingInput.class,
    // QN_SOAP_12_BODY);
    // bindingInputSoapBody.setUse(SOAP_LITERAL_USE);
    // bindingInput.addExtensibilityElement(bindingInputSoapBody);
    // bindingOperation.setBindingInput(bindingInput);
    //
    // BindingOutput bindingOutput = getDefinitions().createBindingOutput();
    // SOAP12Body bindingOutputSoapBody =
    // (SOAP12Body) getExtensionRegistry().createExtension(BindingInput.class,
    // QN_SOAP_12_BODY);
    // bindingOutputSoapBody.setUse(SOAP_LITERAL_USE);
    // bindingOutput.addExtensibilityElement(bindingOutputSoapBody);
    // bindingOperation.setBindingOutput(bindingOutput);
    //
    // for (Fault fault : faults) {
    // BindingFault bindingFault = getDefinitions().createBindingFault();
    // bindingFault.setName(fault.getName());
    // SOAP12Fault soapFault =
    // (SOAP12Fault) getExtensionRegistry().createExtension(BindingFault.class,
    // QN_SOAP_12_FAULT);
    // soapFault.setUse(SOAP_LITERAL_USE);
    // soapFault.setName(fault.getName());
    // bindingFault.addExtensibilityElement(soapFault);
    // bindingOperation.addBindingFault(bindingFault);
    // }
    //
    // getSoap12Binding().addBindingOperation(bindingOperation);
    // return bindingOperation;
    // }
    //
    // private BindingOperation addPoxBindingOperation(String name, Operation
    // operation, Collection<Fault> faults)
    // {
    // BindingOperation bindingOperation =
    // getDefinitions().createBindingOperation();
    // bindingOperation.setName(name);
    // bindingOperation.setOperation(operation);
    //
    // HTTPOperation httpOperation =
    // (HTTPOperation)
    // getExtensionRegistry().createExtension(BindingOperation.class,
    // QN_HTTP_OPERATION);
    // httpOperation.setLocationURI("");
    // bindingOperation.addExtensibilityElement(httpOperation);
    //
    // BindingInput bindingInput = getDefinitions().createBindingInput();
    // MIMEMimeXml inputmime =
    // (MIMEMimeXml) getExtensionRegistry().createExtension(BindingInput.class,
    // QN_MIME_MIME_XML);
    // bindingInput.addExtensibilityElement(inputmime);
    //
    // bindingOperation.setBindingInput(bindingInput);
    //
    // BindingOutput bindingOutput = getDefinitions().createBindingOutput();
    //
    // MIMEMimeXml outputmime =
    // (MIMEMimeXml) getExtensionRegistry().createExtension(BindingInput.class,
    // QN_MIME_MIME_XML);
    // bindingOutput.addExtensibilityElement(outputmime);
    //
    // bindingOperation.setBindingOutput(bindingOutput);
    //
    // for (Fault fault : faults) {
    // BindingFault bindingFault = getDefinitions().createBindingFault();
    // bindingFault.setName(fault.getName());
    // bindingOperation.addBindingFault(bindingFault);
    // }
    //
    // getPoxBinding().addBindingOperation(bindingOperation);
    // return bindingOperation;
    // }
    //
    // private BindingOperation addKvpBindingOperation(String name, Operation
    // operation, Collection<Fault> faults)
    // {
    // BindingOperation bindingOperation =
    // getDefinitions().createBindingOperation();
    // bindingOperation.setName(name);
    // bindingOperation.setOperation(operation);
    //
    // HTTPOperation httpOperation =
    // (HTTPOperation)
    // getExtensionRegistry().createExtension(BindingOperation.class,
    // QN_HTTP_OPERATION);
    // httpOperation.setLocationURI("");
    // bindingOperation.addExtensibilityElement(httpOperation);
    //
    // BindingInput bindingInput = getDefinitions().createBindingInput();
    // HTTPUrlEncoded urlEncoded =
    // (HTTPUrlEncoded)
    // getExtensionRegistry().createExtension(BindingInput.class,
    // QN_HTTP_URL_ENCODED);
    // bindingInput.addExtensibilityElement(urlEncoded);
    //
    // bindingOperation.setBindingInput(bindingInput);
    //
    // BindingOutput bindingOutput = getDefinitions().createBindingOutput();
    //
    // MIMEMimeXml mimeXml =
    // (MIMEMimeXml) getExtensionRegistry().createExtension(BindingInput.class,
    // QN_MIME_MIME_XML);
    // bindingOutput.addExtensibilityElement(mimeXml);
    //
    // bindingOperation.setBindingOutput(bindingOutput);
    //
    // for (Fault fault : faults) {
    // BindingFault bindingFault = getDefinitions().createBindingFault();
    // bindingFault.setName(fault.getName());
    // bindingOperation.addBindingFault(bindingFault);
    // }
    //
    // getKvpBinding().addBindingOperation(bindingOperation);
    // return bindingOperation;
    // }
    //
    // private Binding getSoapBinding() {
    // if (this.soapBinding == null) {
    // this.soapBinding = getDefinitions().createBinding();
    // SOAPBinding sb = (SOAPBinding)
    // getExtensionRegistry().createExtension(Binding.class,
    // QN_SOAP_12_BINDING);
    // sb.setStyle(SOAP_DOCUMENT_STYLE);
    // sb.setTransportURI(SOAP_BINDING_HTTP_TRANSPORT);
    // this.soapBinding.addExtensibilityElement(sb);
    // this.soapBinding.setPortType(getPostPortType());
    // this.soapBinding.setQName(QN_SOSW_SOAP_BINDING);
    // this.soapBinding.setUndefined(false);
    //
    // getDefinitions().addBinding(this.soapBinding);
    // }
    // return this.soapBinding;
    // }
    //
    // private Binding getSoap12Binding() {
    // if (this.soapBinding == null) {
    // this.soapBinding = getDefinitions().createBinding();
    // SOAP12Binding sb = (SOAP12Binding)
    // getExtensionRegistry().createExtension(Binding.class,
    // QN_SOAP_12_BINDING);
    // sb.setStyle(SOAP_DOCUMENT_STYLE);
    // sb.setTransportURI(SOAP_12_BINDING_HTTP_TRANSPORT);
    // this.soapBinding.addExtensibilityElement(sb);
    // this.soapBinding.setPortType(getPostPortType());
    // this.soapBinding.setQName(QN_SOSW_SOAP_BINDING);
    // this.soapBinding.setUndefined(false);
    //
    // getDefinitions().addBinding(this.soapBinding);
    // }
    // return this.soapBinding;
    // }
    //
    // private Binding getPoxBinding() {
    // if (this.poxBinding == null) {
    // this.poxBinding = getDefinitions().createBinding();
    // this.poxBinding.setPortType(getPostPortType());
    // this.poxBinding.setQName(QN_SOSW_POX_BINDING);
    // this.poxBinding.setUndefined(false);
    // HTTPBinding hb = (HTTPBinding)
    // getExtensionRegistry().createExtension(Binding.class, QN_HTTP_BINDING);
    // hb.setVerb(POX_HTTP_VERB);
    // this.poxBinding.addExtensibilityElement(hb);
    // getDefinitions().addBinding(this.poxBinding);
    // }
    // return this.poxBinding;
    // }
    //
    // private Binding getKvpBinding() {
    // if (this.kvpBinding == null) {
    // this.kvpBinding = getDefinitions().createBinding();
    // this.kvpBinding.setPortType(getGetPortType());
    // this.kvpBinding.setQName(QN_SOSW_KVP_BINDING);
    // this.kvpBinding.setUndefined(false);
    // HTTPBinding hb = (HTTPBinding)
    // getExtensionRegistry().createExtension(Binding.class, QN_HTTP_BINDING);
    // hb.setVerb(KVP_HTTP_VERB);
    // this.kvpBinding.addExtensibilityElement(hb);
    // getDefinitions().addBinding(this.kvpBinding);
    // }
    // return this.kvpBinding;
    // }

    public static void main(String[] args) throws ParserConfigurationException {
        WSDLBuilder b =
                new WSDLBuilder().setSoapEndpoint(URI.create("http://localhost:8080/52n-sos-webapp/sos/soap"))
                        .setKvpEndpoint(URI.create("http://localhost:8080/52n-sos-webapp/sos/kvp"))
                        .setPoxEndpoint(URI.create("http://localhost:8080/52n-sos-webapp/sos/pox"));
        for (WSDLOperation o : new WSDLOperation[] { Operations.DELETE_SENSOR, Operations.DESCRIBE_SENSOR,
                Operations.GET_CAPABILITIES, Operations.GET_FEATURE_OF_INTEREST, Operations.GET_OBSERVATION,
                Operations.GET_OBSERVATION_BY_ID, Operations.GET_RESULT, Operations.GET_RESULT_TEMPLATE,
                Operations.INSERT_OBSERVATION, Operations.INSERT_RESULT, Operations.INSERT_RESULT_TEMPLATE,
                Operations.INSERT_SENSOR, Operations.UPDATE_SENSOR_DESCRIPTION }) {
            // b.addPoxOperation(o);
            // b.addKvpOperation(o);
            // b.addSoapOperation(o);
        }
        System.out.println(b.build());
    }

    public void addSoapOperation(WSDLOperation sosOperationDefinition) {
        // TODO Auto-generated method stub
    }

    public void addPoxOperation(WSDLOperation sosOperationDefinition) {
        // TODO Auto-generated method stub
    }

    public void addKvpOperation(WSDLOperation sosOperationDefinition) {
        // TODO Auto-generated method stub
    }

    public void addNamespace(String key, String value) {
        // TODO Auto-generated method stub
    }

    public void addSchemaImport(String key, String value) {
        // TODO Auto-generated method stub
    }
}
