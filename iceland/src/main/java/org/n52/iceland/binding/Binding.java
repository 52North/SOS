/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.binding;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.iceland.coding.OperationKey;
import org.n52.iceland.exception.HTTPException;
import org.n52.iceland.service.ConformanceClass;
import org.n52.iceland.util.http.HTTPStatus;
import org.n52.iceland.util.http.MediaType;

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
