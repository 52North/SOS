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

import org.n52.sos.request.ResponseFormat;
import org.n52.sos.util.http.MediaType;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @author CarstenHollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 */
public interface ResponseWriter<T> {

    /**
     * Get the current contentType
     * 
     * @return the contenType
     */
    MediaType getContentType();

    /**
     * Set the contentType
     * 
     * @param contentType
     *            to set
     */
    void setContentType(MediaType contentType);
    
    MediaType getEncodedContentType(ResponseFormat responseFormat);

    /**
     * Write object t to {@link OutputStream} out
     * 
     * @param t
     *            Object to write
     * @param out
     *            {@link OutputStream} to be written to
     * @param responseProxy
     *            {@link ResponseProxy} giving access to header and content length setters            
     * @throws IOException
     *             If an error occurs during writing
     */
    void write(T t, OutputStream out, ResponseProxy responseProxy) throws IOException;

    /**
     * Check if GZip is supported by this writer
     * 
     * @param t
     *            Object to write
     * @return <code>true</code>, if GZip is supported
     */
    boolean supportsGZip(T t);  
}
