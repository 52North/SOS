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
package org.n52.sos.exception;

import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.ows.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public abstract class CodedException extends OwsExceptionReport {
    private static final long serialVersionUID = 52L;

    private final List<CodedException> exceptions = Collections.singletonList(this);

    private final ExceptionCode code;

    private String locator;

    private String message;

    public CodedException(final ExceptionCode code) {
        this.code = code;
    }

    public ExceptionCode getCode() {
        return code;
    }

    public String getLocator() {
        return locator;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public boolean hasMessage() {
        return getMessage() != null && !getMessage().isEmpty();
    }

    @Override
    public List<CodedException> getExceptions() {
        return exceptions;
    }

    public CodedException at(final String locator) {
        this.locator = locator;
        return this;
    }

    public CodedException at(final Enum<?> locator) {
        return at(locator.name());
    }

    /**
     * @param message
     *            the message format
     * @param args
     *            the optional formatting arguments
     * 
     * @return this
     * 
     * @see String#format(java.lang.String, java.lang.Object[])
     */
    public CodedException withMessage(final String message, final Object... args) {
        if ((args != null) && (args.length > 0)) {
            this.message = String.format(message, args);
        } else {
            this.message = message;
        }
        return this;
    }

    public CodedException causedBy(final Throwable exception) {
        return (CodedException) initCause(exception);
    }
}
