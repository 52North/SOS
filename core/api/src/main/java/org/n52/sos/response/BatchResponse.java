/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import java.util.Objects;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.sos.response.BatchResponse.ExceptionOrResponse;
import org.n52.sos.util.BatchConstants;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class BatchResponse extends OwsServiceResponse implements Iterable<ExceptionOrResponse> {
    private final List<ExceptionOrResponse> responses;

    public BatchResponse() {
        this(new LinkedList<>());
    }

    public BatchResponse(List<ExceptionOrResponse> responses) {
        super(null, null, BatchConstants.OPERATION_NAME);
        this.responses = responses;
    }

    public BatchResponse(String service, String version, List<ExceptionOrResponse> responses) {
        super(service, version, BatchConstants.OPERATION_NAME);
        this.responses = responses;
    }

    public BatchResponse(String service, String version, String operationName, List<ExceptionOrResponse> responses) {
        super(service, version, operationName);
        this.responses = responses;
    }

    public List<ExceptionOrResponse> getResponses() {
        return Collections.unmodifiableList(responses);
    }

    public void add(OwsExceptionReport e) {
        this.responses.add(new ExceptionOrResponse(e));
    }

    public void add(OwsServiceResponse r) {
        this.responses.add(new ExceptionOrResponse(r));
    }

    public void add(ExceptionOrResponse eor) {
        this.responses.add(Objects.requireNonNull(eor));
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

        private final OwsServiceResponse response;

        private ExceptionOrResponse(OwsExceptionReport exception, OwsServiceResponse response) {
            this.exception = exception;
            this.response = response;
        }

        public ExceptionOrResponse(OwsServiceResponse response) {
            this(null, Objects.requireNonNull(response));
        }

        public ExceptionOrResponse(OwsExceptionReport exception) {
            this(Objects.requireNonNull(exception), null);
        }

        public boolean isException() {
            return exception != null;
        }

        public OwsExceptionReport getException() {
            return exception;
        }

        public OwsServiceResponse getResponse() {
            return response;
        }
    }
}
