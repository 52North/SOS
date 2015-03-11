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
package org.n52.sos.request;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.response.BatchResponse;
import org.n52.sos.util.BatchConstants;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@SuppressWarnings("rawtypes")
public class BatchRequest extends AbstractServiceRequest<BatchResponse> implements Iterable<AbstractServiceRequest> {
    private final List<AbstractServiceRequest> requests;

    private boolean stopAtFailure = false;

    public BatchRequest(List<AbstractServiceRequest> requests) {
        this.requests = checkNotNull(requests);
    }

    public BatchRequest() {
        this(new LinkedList<AbstractServiceRequest>());
    }

    @Override
    public String getOperationName() {
        return BatchConstants.OPERATION_NAME;
    }

    public List<AbstractServiceRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    public void add(AbstractServiceRequest request) {
        this.requests.add(checkNotNull(request));
    }

    public boolean isEmpty() {
        return getRequests().isEmpty();
    }

    @Override
    public Iterator<AbstractServiceRequest> iterator() {
        return getRequests().iterator();
    }

    private static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    public boolean isStopAtFailure() {
        return stopAtFailure;
    }

    public void setStopAtFailure(boolean stopAtFailure) {
        this.stopAtFailure = stopAtFailure;
    }

    @Override
    public BatchResponse getResponse() {
        return (BatchResponse) new BatchResponse().set(this);
    }
}
