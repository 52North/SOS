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
package org.n52.iceland.ogc.ows;

import java.util.Iterator;
import java.util.List;

import org.n52.iceland.exception.CodedException;
import org.n52.iceland.util.http.HTTPStatus;

/**
 * Implementation of the ows service exception. The exception codes are defined
 * according the ows common spec. version 1.1.0
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public abstract class OwsExceptionReport extends Exception {

    private static final long serialVersionUID = 52L;

    private HTTPStatus status;

    private String version;

    /**
     * @return Returns the ExceptionTypes of this exception
     */
    public abstract List<? extends CodedException> getExceptions();

    /**
     * Set SOS version
     * 
     * @param version
     *            the version to set
     * 
     * @return this
     */
    public OwsExceptionReport setVersion(final String version) {
        this.version = version;
        return this;
    }

    /**
     * Get SOS version
     * 
     * @return SOS version
     */
    public String getVersion() {
        if (version == null) {
            this.version = OWSConstants.VERSION;
        }
        return version;
    }

    public String getNamespace() {
        return OWSConstants.NS_OWS;
    }

    @Override
    public String getMessage() {
        final StringBuilder faultString = new StringBuilder();
        final Iterator<? extends CodedException> i = getExceptions().iterator();
        boolean first = true;
        while (i.hasNext()) {
            if (first) {
                first = false;
            } else {
                faultString.append('\n');
            }

            CodedException e = i.next();
            faultString.append(e.getMessage());
            if (e.getCause() != null) {
                faultString.append(" (caused by ").append(e.getCause().getMessage()).append(")");
            }
        }
        return faultString.toString();
    }

    /**
     * @return the HTTP response code of this {@code OwsExceptionReport} or<br>
     *         {@code getExceptions().get(0).getStatus()} if it is not set and
     *         {@code getExceptions().get(0) != this}.
     */
    public HTTPStatus getStatus() {
        if (status == null && getExceptions().get(0) != this) {
            return getExceptions().get(0).getStatus();
        }
        return status;
    }

    /**
     * @return <tt>true</tt>, if a HTTP response code for this
     *         {@code OwsExceptionReport} or any sub exception is available
     */
    public boolean hasStatus() {
        return getStatus() != null;
    }

    /**
     * Sets the HTTP response code for this {@code OwsExceptionReport}.
     * 
     * @param status
     *            the code
     * 
     * @return this (for method chaining)
     */
    public OwsExceptionReport setStatus(final HTTPStatus status) {
        this.status = status;
        return this;
    }
}
