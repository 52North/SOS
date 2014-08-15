/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
import java.util.Map;

import org.n52.sos.util.http.MediaType;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public interface ResponseWriter<T> {

    /**
     * Get the type this {@link ResponseWriter} supports
     * 
     * @return the supported type
     */
    Class<T> getType();

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

    /**
     * Write object t to {@link OutputStream} out
     * 
     * @param t
     *            Object to write
     * @param out
     *            {@link OutputStream} to be written to
     * @throws IOException
     *             If an error occurs during writing
     */
    void write(T t, OutputStream out) throws IOException;

    /**
     * Check if GZip is supported by this writer
     * 
     * @param t
     *            Object to write
     * @return <code>true</code>, if GZip is supported
     */
    boolean supportsGZip(T t);

    /**
     * Return type specific response headers (e.g. filename for downloadable binary attachments)
     * 
     * @return Map of response headers to add to response
     */
    Map<String,String> getResponseHeaders(T t);
    
    /**
     * Return content length of written response, or -1 for unknown
     * 
     * @return Content length of written response, or -1 for unknown
     */
    //TODO return content length in write(T t, OutputStream out) instead?
    int getContentLength(T t);    
}