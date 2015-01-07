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
package org.n52.sos.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class ThrowableFunction<F, T> implements Function<F, T> {

    private List<Exception> errors = new LinkedList<>();

    @Override
    public T apply(F input) {
        try {
            return applyThrowable(input);
        } catch (Exception ex) {
            this.errors.add(ex);
            return null;
        }
    }

    public List<Exception> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    public Exception getFirstError() {
        return hasErrors() ? this.errors.iterator().next() : null;
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public <X extends Throwable> void propagateIfPossible(Class<X> declaredType)
            throws X {
        Throwables.propagateIfPossible(getFirstError(), declaredType);
    }

    protected abstract T applyThrowable(F input)
            throws Exception;

}
