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
package org.n52.iceland.encode;

import java.io.IOException;
import java.io.OutputStream;

import org.n52.iceland.request.ResponseFormat;
import org.n52.iceland.util.http.MediaType;

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
