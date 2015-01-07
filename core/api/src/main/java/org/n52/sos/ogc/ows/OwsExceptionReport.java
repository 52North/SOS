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
package org.n52.sos.ogc.ows;

import java.util.Iterator;
import java.util.List;

import org.n52.sos.exception.CodedException;
import org.n52.sos.util.http.HTTPStatus;

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
