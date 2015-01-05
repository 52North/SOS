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
package org.n52.sos.response;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.BatchResponse.ExceptionOrResponse;
import org.n52.sos.util.BatchConstants;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class BatchResponse extends AbstractServiceResponse implements Iterable<ExceptionOrResponse> {
    private final List<ExceptionOrResponse> responses;

    public BatchResponse(List<ExceptionOrResponse> responses) {
        this.responses = checkNotNull(responses);
    }

    public BatchResponse() {
        this(new LinkedList<ExceptionOrResponse>());
    }

    private static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    @Override
    public String getOperationName() {
        return BatchConstants.OPERATION_NAME;
    }

    public List<ExceptionOrResponse> getResponses() {
        return Collections.unmodifiableList(responses);
    }

    public void add(OwsExceptionReport e) {
        this.responses.add(new ExceptionOrResponse(e));
    }

    public void add(AbstractServiceResponse r) {
        this.responses.add(new ExceptionOrResponse(r));
    }

    public void add(ExceptionOrResponse eor) {
        this.responses.add(checkNotNull(eor));
    }

    public boolean isEmpty() {
        return getResponses().isEmpty();
    }

    @Override
    public Iterator<ExceptionOrResponse> iterator() {
        return getResponses().iterator();
    }

    public static class ExceptionOrResponse {
        private final OwsExceptionReport exception;

        private final AbstractServiceResponse response;

        private ExceptionOrResponse(OwsExceptionReport exception, AbstractServiceResponse response) {
            this.exception = exception;
            this.response = response;
        }

        public ExceptionOrResponse(AbstractServiceResponse response) {
            this(null, checkNotNull(response));
        }

        public ExceptionOrResponse(OwsExceptionReport exception) {
            this(checkNotNull(exception), null);
        }

        public boolean isException() {
            return exception != null;
        }

        public OwsExceptionReport getException() {
            return exception;
        }

        public AbstractServiceResponse getResponse() {
            return response;
        }
    }
}
