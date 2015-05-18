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
package org.n52.iceland.exception;

import java.util.Collections;
import java.util.List;

import org.n52.iceland.ogc.ows.ExceptionCode;
import org.n52.iceland.ogc.ows.OwsExceptionReport;

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
