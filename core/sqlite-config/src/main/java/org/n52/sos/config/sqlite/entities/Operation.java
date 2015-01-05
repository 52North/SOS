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
package org.n52.sos.config.sqlite.entities;

import javax.persistence.Entity;

import org.n52.sos.request.operator.RequestOperatorKey;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
@Entity(name = "operations")
public class Operation extends Activatable<OperationKey, Operation> {
    private static final long serialVersionUID = 6816894177423976948L;
    public Operation() {
        this(null, null, null);
    }

    public Operation(RequestOperatorKey key) {
        this(key.getOperationName(),
             key.getServiceOperatorKey().getService(),
             key.getServiceOperatorKey().getVersion());
    }

    public Operation(String operation, String service, String version) {
        super(new OperationKey()
                .setOperationName(operation)
                .setService(service)
                .setVersion(version));
    }

    public String getOperationName() {
        return getKey().getOperationName();
    }

    public Operation setOperationName(String operationName) {
        getKey().setOperationName(operationName);
        return this;
    }

    public String getService() {
        return getKey().getService();
    }

    public Operation setService(String service) {
        getKey().setService(service);
        return this;
    }

    public String getVersion() {
        return getKey().getVersion();
    }

    public Operation setVersion(String version) {
        getKey().setVersion(version);
        return this;
    }
}
