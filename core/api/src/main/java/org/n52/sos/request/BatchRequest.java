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
package org.n52.sos.request;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.sos.util.BatchConstants;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class BatchRequest extends AbstractServiceRequest implements Iterable<AbstractServiceRequest> {
    private final List<AbstractServiceRequest> requests;

    private boolean stopAtFailure = false;

    public BatchRequest(List<AbstractServiceRequest> requests) {
        super(null, null, BatchConstants.OPERATION_NAME);
        this.requests = Objects.requireNonNull(requests);
    }

    public BatchRequest() {
        this(new LinkedList<AbstractServiceRequest>());
    }

    public List<AbstractServiceRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    public void add(AbstractServiceRequest request) {
        this.requests.add(Objects.requireNonNull(request));
    }

    public boolean isEmpty() {
        return getRequests().isEmpty();
    }

    @Override
    public Iterator<AbstractServiceRequest> iterator() {
        return getRequests().iterator();
    }

    public boolean isStopAtFailure() {
        return stopAtFailure;
    }

    public void setStopAtFailure(boolean stopAtFailure) {
        this.stopAtFailure = stopAtFailure;
    }

}
