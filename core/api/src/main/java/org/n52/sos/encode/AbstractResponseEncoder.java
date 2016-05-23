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
package org.n52.sos.encode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @param <T>
 *            the response type
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public abstract class AbstractResponseEncoder<T extends AbstractServiceResponse> extends AbstractXmlEncoder<T>
        implements StreamingEncoder<XmlObject, T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResponseEncoder.class);

    private final Set<EncoderKey> encoderKeys;

    private final String namespace;

    private final String prefix;

    private final String version;

    private final Class<T> responseType;

    private final boolean validate;

    /**
     * constructor
     *
     * @param service
     *            Service
     * @param version
     *            Service version
     * @param operation
     *            Service operation name
     * @param namespace
     *            Service XML schema namespace
     * @param prefix
     *            Service XML schema prefix
     * @param responseType
     *            Response type
     * @param validate
     *            Indicator if the created/encoded object should be validated
     */
    public AbstractResponseEncoder(String service, String version, String operation, String namespace, String prefix,
            Class<T> responseType, boolean validate) {
        OperationKey key = new OperationKey(service, version, operation);
        this.encoderKeys =
                Sets.newHashSet(new XmlEncoderKey(namespace, responseType), new OperationEncoderKey(key,
                        MediaTypes.TEXT_XML), new OperationEncoderKey(key, MediaTypes.APPLICATION_XML));
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(encoderKeys));
        this.namespace = namespace;
        this.prefix = prefix;
        this.version = version;
        this.responseType = responseType;
        this.validate = validate;
    }

    /**
     * constructor
     *
     * @param service
     *            Service
     * @param version
     *            Service version
     * @param operation
     *            Service operation name
     * @param namespace
     *            Service XML schema namespace
     * @param prefix
     *            Service XML schema prefix
     * @param responseType
     *            Response type
     */
    public AbstractResponseEncoder(String service, String version, String operation, String namespace, String prefix,
            Class<T> responseType) {
        this(service, version, operation, namespace, prefix, responseType, ServiceConfiguration.getInstance()
                .isValidateResponse());
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(encoderKeys);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        if (nameSpacePrefixMap != null) {
            nameSpacePrefixMap.put(this.namespace, this.prefix);
        }
    }

    @Override
    public XmlObject encode(T response) throws OwsExceptionReport {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        final Map<HelperValues, String> additionalValues = new EnumMap<HelperValues, String>(HelperValues.class);
        additionalValues.put(HelperValues.VERSION, this.version);
        return encode(response, additionalValues);
    }

    @Override
    public XmlObject encode(T response, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        XmlObject xml = create(response);
        setSchemaLocations(xml);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encoded object {} is valid: {}", xml.schemaType().toString(),
                    XmlHelper.validateDocument(xml));
        } else {
            if (validate) {
                LOGGER.warn("Encoded object {} is valid: {}", xml.schemaType().toString(),
                        XmlHelper.validateDocument(xml));
            }
        }
        return xml;
    }

    @Override
    public void encode(T element, OutputStream outputStream) throws OwsExceptionReport {
        encode(element, outputStream, new EncodingValues());
    }

    @Override
    public void encode(T response, OutputStream outputStream, EncodingValues encodingValues) throws OwsExceptionReport {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        create(response, outputStream, encodingValues);
    }
    
    @Override
    public boolean forceStreaming() {
    	return false;
    }

    private void setSchemaLocations(XmlObject document) {
        Map<String, SchemaLocation> schemaLocations = Maps.newHashMap();
        for (String ns : N52XmlHelper.getNamespaces(document)) {
            for (SchemaLocation sl : CodingRepository.getInstance().getSchemaLocation(ns)) {
                schemaLocations.put(sl.getNamespace(), sl);
            }
        }
        for (SchemaLocation sl : getSchemaLocations()) {
            schemaLocations.put(sl.getNamespace(), sl);
        }
        // override default schema location with concrete URL's
        for (SchemaLocation sl : getConcreteSchemaLocations()) {
            schemaLocations.put(sl.getNamespace(), sl);
        }
        N52XmlHelper.setSchemaLocationsToDocument(document, schemaLocations.values());
    }

    protected XmlOptions getXmlOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }

    /**
     * Get the concrete schema locations for this
     * {@link AbstractServiceResponse} encoder
     *
     * @return the concrete schema locations
     */
    protected abstract Set<SchemaLocation> getConcreteSchemaLocations();

    /**
     * Create an {@link XmlObject} from the {@link AbstractServiceResponse}
     * object
     *
     * @param response
     *            {@link AbstractServiceResponse} to encode
     * @return XML encoded {@link AbstractServiceResponse}
     * @throws OwsExceptionReport
     *             If an error occurs during the encoding
     */
    protected abstract XmlObject create(T response) throws OwsExceptionReport;

    /**
     * Override this method in concrete response encoder if streaming is
     * supported for this operations.
     *
     * @param response
     *            Implementation of {@link AbstractServiceResponse}
     * @param outputStream
     *            {@link OutputStream} to write
     * @param encodingValues
     *            {@link EncodingValues} with additional indicators for encoding
     * @throws OwsExceptionReport
     *             If an error occurs during encoding/writing to stream
     */
    protected void create(T response, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        try {
            XmlOptions xmlOptions = XmlOptionsHelper.getInstance().getXmlOptions();
            if (encodingValues.isEmbedded()) {
                xmlOptions.setSaveNoXmlDecl();
            }
            writeIndent(encodingValues.getIndent(), outputStream);
            XmlObject xmlObject = create(response);
            setSchemaLocations(xmlObject);
            xmlObject.save(outputStream, xmlOptions);
        } catch (IOException ioe) {
            throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while writing element to stream!");
        } finally {
            if (encodingValues.isEmbedded()) {
                XmlOptionsHelper.getInstance().getXmlOptions().remove(XmlOptions.SAVE_NO_XML_DECL);
            }
        }
    }

    /**
     * Write indent to stream if the response is encoded with XmlBeans
     *
     * @param level
     *            Level of indent
     * @param outputStream
     *            {@link OutputStream} to write indent
     * @throws IOException
     *             If an error occurs when writing to stream
     */
    protected void writeIndent(int level, OutputStream outputStream) throws IOException {
        byte[] indent = new String("  ").getBytes();
        for (int i = 0; i < level; i++) {
            outputStream.write(indent);
        }
    }

    protected Class<T> getResponseType() {
        return responseType;
    }

}
