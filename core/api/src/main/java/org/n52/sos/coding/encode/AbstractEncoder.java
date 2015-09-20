/*
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
package org.n52.sos.coding.encode;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.iceland.coding.encode.SchemaRepository;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.iceland.ogc.ows.OWSConstants.HelperValues;
import org.n52.iceland.response.AbstractServiceResponse;
import org.n52.iceland.service.ServiceSettings;
import org.n52.iceland.w3c.SchemaLocation;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 *
 * @param <T>
 */
public abstract class AbstractEncoder<T> extends AbstractXmlEncoder<T> implements StreamingEncoder<XmlObject, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResponseEncoder.class);

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
    public AbstractEncoder(String service, String version, String operation, String namespace, String prefix, Class<T> responseType, boolean validate) {
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
    public AbstractEncoder(String service, String version, String operation, String namespace, String prefix, Class<T> responseType) {
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
        final Map<HelperValues, String> additionalValues = new EnumMap<>(HelperValues.class);
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
            XmlOptions xmlOptions = new XmlOptions(getXmlOptions());
            if (encodingValues.isEmbedded()) {
                xmlOptions.setSaveNoXmlDecl();
            }
            writeIndent(encodingValues.getIndent(), outputStream);
            XmlObject xmlObject = create(response);
            setSchemaLocations(xmlObject);
            xmlObject.save(outputStream, xmlOptions);
        } catch (IOException ioe) {
            throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while writing element to stream!");
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
