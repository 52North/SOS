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
package org.n52.sos.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.n52.sos.service.CommunicationObjectWithSoapHeader;
import org.n52.sos.service.SoapHeader;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @since 4.0.0
 */
public class ServiceResponse implements CommunicationObjectWithSoapHeader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceResponse.class);

    /**
     * output stream of document
     */
    private final ByteArrayOutputStream byteArrayOutputStream;

    /**
     * the HTTP response code as specified in {@link HttpServletResponse}
     */
    private HTTPStatus status;

    /**
     * the content type of this response
     */
    private MediaType contentType;

    /**
     * whether the service response should be gzipped
     */
    private boolean supportsGZip = true;
    
    /**
     * the header field and values to be set in the {@link HttpServletResponse}
     */
    private final Map<String, String> headerMap = Maps.newHashMap();

    private final List<SoapHeader> soapHeaderMap = Lists.newArrayList();

    /**
     * constructor with content and response code
     * 
     * @param baos
     *            Output stream of the SOS response
     * @param contentType
     *            Content type
     * @param status
     *            the HTTP response code as specified in
     *            {@link HttpServletResponse}
     */
    public ServiceResponse(ByteArrayOutputStream baos, MediaType contentType, HTTPStatus status) {
        this.byteArrayOutputStream = baos;
        this.contentType = contentType;
        this.status = status;
    }

    /**
     * constructor with content but not specified response code
     * 
     * @param baos
     *            Output stream of the SOS response
     * @param contentType
     *            Content type
     */
    public ServiceResponse(ByteArrayOutputStream baos, MediaType contentType) {
        this(baos, contentType, null);
    }

    /**
     * constructor without content type but with specified response code
     * 
     * @param contentType
     *            Content type
     * @param status
     *            the HTTP response code as specified in
     *            {@link HttpServletResponse}
     */
    public ServiceResponse(MediaType contentType, HTTPStatus status) {
        this(null, contentType, status);
    }

    /**
     * @return Returns the content type of this response
     */
    public MediaType getContentType() {
        return contentType;
    }

    /**
     * @return <b>true</b> if as minimum one header value is contained in the
     *         map
     */
    public boolean isSetHeaderMap() {
        return !headerMap.isEmpty();
    }

    public void setHeader(String headerIdentifier, String headerValue) {
        headerMap.put(headerIdentifier, headerValue);
    }

    public Map<String, String> getHeaderMap() {
        return Collections.unmodifiableMap(headerMap);
    }
    
    /**
     * @param outputStream
     *            The stream the content of this response is written to
     * 
     * @see #isContentLess()
     */
    public void writeToOutputStream(OutputStream outputStream) {
        if (byteArrayOutputStream == null) {
            LOGGER.error("no response to write to.");
            return;
        }
        try {

            byteArrayOutputStream.writeTo(outputStream);
            byteArrayOutputStream.flush();

        } catch (IOException ioe) {
            LOGGER.error("doResponse", ioe);
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }

            } catch (IOException ioe) {
                LOGGER.error("doSoapResponse, close streams", ioe);
            }
        }
    }

    /**
     * Check, if this response contains content to be written.
     * 
     * @return <code>true</code>, if content is <b>NOT</b> available,<br />
     *         else <code>false</code>, if content is available
     * 
     * @see #writeToOutputStream(OutputStream).
     */
    public boolean isContentLess() {
        return byteArrayOutputStream == null;
    }

    /**
     * Get the content length of the byte stream
     * @return content length
     */
    public int getContentLength() {
        return byteArrayOutputStream == null ? -1 : byteArrayOutputStream.toByteArray().length;
    }

    /**
     * @return the status code
     */
    public HTTPStatus getStatus() {
        return status != null ? status : isContentLess() ? HTTPStatus.NO_CONTENT : HTTPStatus.OK;
    }

    public void setStatus(HTTPStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "ServiceResponse [byteArrayOutputStream=%s, httpResponseCode=%s, contentType=%s, headerMap=%s]",
                byteArrayOutputStream, status, contentType, headerMap);
    }

    @Override
    public List<SoapHeader> getSoapHeader() {
        return Collections.unmodifiableList(this.soapHeaderMap);
    }

    @Override
    public void setSoapHeader(List<SoapHeader> header) {
        this.soapHeaderMap.clear();
        if (header != null) {
            this.soapHeaderMap.addAll(header);
        }
    }

    @Override
    public boolean isSetSoapHeader() {
        return !this.soapHeaderMap.isEmpty();
    }
    
    /**
     * @return Whether or not the response can be gzipped
     */
    public boolean supportsGZip() {
        return this.supportsGZip;
    }

    public void setSupportsGZip(boolean supportsGZip) {
        this.supportsGZip = supportsGZip;
    }        
}
