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
package org.n52.sos.binding;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sos.coding.OperationKey;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.service.ConformanceClass;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.MediaType;

/**
 * Abstract Super class for binding implementations<br />
 * 
 * Context:<br />
 * The <code>Binding.check*()</code> methods are called during GetCapabilities
 * processing when collecting the operations metadata.
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * 
 * @since 4.0.0
 */
public abstract class Binding implements ConformanceClass {
    /**
     * HTTP DELETE request handling method
     * 
     * @param request
     *            HTTP DELETE request
     * 
     * @param response
     *            HTTP DELETE response
     * 
     * 
     * @throws HTTPException
     *             if the encoding of an exception failed
     * @throws IOException
     *             if an IO error occurs
     */
    public void doDeleteOperation(HttpServletRequest request, HttpServletResponse response) throws HTTPException,
            IOException {
        throw new HTTPException(HTTPStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * HTTP GET request handling method
     * 
     * @param request
     *            HTTP GET request
     * 
     * @param response
     *            HTTP GET response
     * 
     * 
     * @throws HTTPException
     *             if the encoding of an exception failed
     * @throws IOException
     *             if an IO error occurs
     */
    public void doGetOperation(HttpServletRequest request, HttpServletResponse response) throws HTTPException,
            IOException {
        throw new HTTPException(HTTPStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * HTTP DELETE request handling method
     * 
     * @param request
     *            HTTP DELETE request
     * 
     * @param response
     *            HTTP DELETE response
     * 
     * 
     * @throws HTTPException
     *             if the encoding of an exception failed
     * @throws IOException
     *             if an IO error occurs
     */
    public void doOptionsOperation(HttpServletRequest request, HttpServletResponse response) throws HTTPException,
            IOException {
        throw new HTTPException(HTTPStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * HTTP POST request handling method
     * 
     * @param request
     *            HTTP POST request
     * 
     * @param response
     *            HTTP POST response
     * 
     * 
     * @throws HTTPException
     *             if the encoding of an exception failed
     * @throws IOException
     *             if an IO error occurs
     */
    public void doPostOperation(HttpServletRequest request, HttpServletResponse response) throws HTTPException,
            IOException {
        throw new HTTPException(HTTPStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * HTTP PUT request handling method
     * 
     * @param request
     *            HTTP PUT request
     * 
     * @param response
     *            HTTP PUT response
     * 
     * 
     * @throws HTTPException
     *             if the encoding of an exception failed
     * @throws IOException
     *             if an IO error occurs
     */
    public void doPutOperation(HttpServletRequest request, HttpServletResponse response) throws HTTPException,
            IOException {
        throw new HTTPException(HTTPStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Get URL pattern for the operator.<br />
     * The URL pattern MUST start with "/sos", MUST NOT contain any additional
     * "/", and MUST be unique over all bindings present in the SOS at runtime.<br />
     * For example, a kvp binding could have the pattern "/sos/kvp".
     * 
     * @return URL pattern
     */
    public abstract String getUrlPattern();

    /**
     * Check, if the operation is supported by the decoder by the HTTP-Delete
     * method.
     * 
     * @param decoderKey
     *            identifier of the decoder
     * 
     * @return true, if the decoder <code>decoderKey</code> supports HTTP-Delete
     *         for * operation <code>operationName</code>
     * 
     * 
     * @throws HTTPException
     */
    public boolean checkOperationHttpDeleteSupported(OperationKey decoderKey) throws HTTPException {
        return false;
    }

    /**
     * Check, if the operation is supported by the decoder by the HTTP-Get
     * method.
     * 
     * @param decoderKey
     *            identifier of the decoder
     * 
     * @return true, if the decoder <code>decoderKey</code> supports HTTP-Get
     *         for operation <code>operationName</code>
     * 
     * 
     * @throws HTTPException
     */
    public boolean checkOperationHttpGetSupported(OperationKey decoderKey) throws HTTPException {
        return false;
    }

    /**
     * Check, if the operation is supported by the decoder by the HTTP-Post
     * method.
     * 
     * @param decoderKey
     *            identifier of the decoder
     * 
     * @return true, if the decoder <code>decoderKey</code> supports HTTP-Post
     *         for operation <code>operationName</code>
     * 
     * 
     * @throws HTTPException
     */
    public boolean checkOperationHttpPostSupported(OperationKey decoderKey) throws HTTPException {
        return false;
    }

    /**
     * Check, if the operation is supported by the decoder by the HTTP-Options
     * method.
     * 
     * @param decoderKey
     *            identifier of the decoder
     * 
     * @return true, if the decoder <code>decoderKey</code> supports HTTP-Post
     *         for operation <code>operationName</code>
     * 
     * 
     * @throws HTTPException
     */
    public boolean checkOperationHttpOptionsSupported(OperationKey decoderKey) throws HTTPException {
        return false;
    }

    /**
     * Check, if the operation is supported by the decoder by the HTTP-Put
     * method.
     * 
     * @param decoderKey
     *            identifier of the decoder
     * 
     * @return true, if the decoder <code>decoderKey</code> supports HTTP-Put
     *         for operation <code>operationName</code>
     * 
     * 
     * @throws HTTPException
     */
    public boolean checkOperationHttpPutSupported(OperationKey decoderKey) throws HTTPException {
        return false;
    }

    /**
     * @return the message encoding used as a constraint for the DCP
     */
    public Set<MediaType> getSupportedEncodings() {
        return null;
    }
}
