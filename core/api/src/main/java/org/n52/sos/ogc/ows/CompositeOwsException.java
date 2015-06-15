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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.exception.CodedException;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class CompositeOwsException extends OwsExceptionReport {
    private static final long serialVersionUID = -4876354677532448922L;

    private List<CodedException> exceptions = new LinkedList<CodedException>();

    public CompositeOwsException(OwsExceptionReport... exceptions) {
        add(exceptions);
    }

    public CompositeOwsException(Collection<? extends OwsExceptionReport> exceptions) {
        add(exceptions);
    }

    public CompositeOwsException() {
    }

    public CompositeOwsException add(Collection<? extends OwsExceptionReport> exceptions) {
        if (exceptions != null) {
            for (OwsExceptionReport e : exceptions) {
                this.exceptions.addAll(e.getExceptions());
            }
            if (getCause() == null && !this.exceptions.isEmpty()) {
                initCause(this.exceptions.get(0));
            }
        }
        return this;
    }

    public CompositeOwsException add(OwsExceptionReport... exceptions) {
        return add(Arrays.asList(exceptions));
    }

    @Override
    public List<? extends CodedException> getExceptions() {
        return Collections.unmodifiableList(this.exceptions);
    }

    public void throwIfNotEmpty() throws CompositeOwsException {
        if (hasExceptions()) {
            throw this;
        }
    }

    public int size() {
        return this.exceptions.size();
    }

    public boolean hasExceptions() {
        return !this.exceptions.isEmpty();
    }
}
