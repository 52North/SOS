/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.coding.encode;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import org.n52.iceland.coding.encode.SchemaRepository;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.service.ConformanceClass;
import org.n52.iceland.service.ServiceSettings;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.EncodingContext;
import org.n52.svalbard.SosHelperValues;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;
import org.n52.svalbard.xml.AbstractXmlEncoder;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 *
 * @param <T>
 */
public abstract class AbstractXmlResponseEncoder<T>
        extends AbstractXmlEncoder<XmlObject, T>
        implements StreamingEncoder<XmlObject, T>,
                   ConformanceClass {

    private final String namespace;
    private final String prefix;
    private final String version;
    private final Class<T> responseType;
    private boolean validate;
    private SchemaRepository schemaRepository;

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
    public AbstractXmlResponseEncoder(String service, String version, String operation, String namespace, String prefix, Class<T> responseType, boolean validate) {
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
    public AbstractXmlResponseEncoder(String service, String version, String operation, String namespace, String prefix, Class<T> responseType) {
        this(service, version, operation, namespace, prefix, responseType, false);
    }

    @Inject
    public void setSchemaRepository(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    public SchemaRepository getSchemaRepository() {
        return schemaRepository;
    }

    @Setting(ServiceSettings.VALIDATE_RESPONSE)
    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        if (nameSpacePrefixMap != null) {
            nameSpacePrefixMap.put(this.namespace, this.prefix);
        }
    }

    @Override
    public XmlObject encode(T response) throws EncodingException {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        return encode(response, EncodingContext.of(SosHelperValues.VERSION, this.version));
    }

    @Override
    public XmlObject encode(T response, EncodingContext additionalValues) throws EncodingException {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        XmlObject xml = create(response);
        setSchemaLocations(xml);
        if (validate) {
            XmlHelper.validateDocument(xml, EncodingException::new);
        }
        return xml;
    }

    private void setSchemaLocations(XmlObject document) {
        Map<String, SchemaLocation> schemaLocations = getSchemaLocations(document).collect(toMap(SchemaLocation::getNamespace, identity()));
        schemaLocations.putAll(getSchemaLocations().stream().collect(toMap(SchemaLocation::getNamespace, identity())));
        schemaLocations.putAll(getConcreteSchemaLocations().stream().collect(toMap(SchemaLocation::getNamespace, identity())));
        N52XmlHelper.setSchemaLocationsToDocument(document, schemaLocations.values());
    }

    private Stream<SchemaLocation> getSchemaLocations(XmlObject document) {
        return N52XmlHelper.getNamespaces(document).stream()
                .map(this.schemaRepository::getSchemaLocation)
                .filter(Objects::nonNull).flatMap(Set::stream);
    }

    /**
     * Get the concrete schema locations for this
     * {@link OwsServiceResponse} encoder
     *
     * @return the concrete schema locations
     */
    protected abstract Set<SchemaLocation> getConcreteSchemaLocations();

    /**
     * Create an {@link XmlObject} from the {@link OwsServiceResponse}
     * object
     *
     * @param response
     *            {@link OwsServiceResponse} to encode
     * @return XML encoded {@link OwsServiceResponse}
     * @throws EncodingException
     *             If an error occurs during the encoding
     */
    protected abstract XmlObject create(T response) throws EncodingException;

    /**
     * Override this method in concrete response encoder if streaming is
     * supported for this operations.
     *
     * @param response
     *            Implementation of {@link OwsServiceResponse}
     * @param outputStream
     *            {@link OutputStream} to write
     * @param encodingValues
     *            {@link EncodingValues} with additional indicators for encoding
     * @throws EncodingException
     *             If an error occurs during encoding/writing to stream
     */
    protected void create(T response, OutputStream outputStream, EncodingValues encodingValues)
            throws EncodingException {
        try {
            XmlOptions xmlOptions = new XmlOptions(getXmlOptions());
            if (encodingValues.isEmbedded()) {
                xmlOptions.setSaveNoXmlDecl();
            }
            writeIndent(encodingValues.getIndent(), outputStream);
            XmlObject xmlObject = create(response);
            setSchemaLocations(xmlObject);
            xmlObject.save(outputStream, xmlOptions);
        } catch (IOException ioe) {
            throw new EncodingException("Error while writing element to stream!", ioe);
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
        byte[] indent = "  ".getBytes();
        for (int i = 0; i < level; i++) {
            outputStream.write(indent);
        }
    }

    protected Class<T> getResponseType() {
        return responseType;
    }

}
