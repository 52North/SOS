/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.wsdl;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.n52.shetland.w3c.wsdl.Fault;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class MetadataBuilder {
    private String name;

    private String version;

    private URI requestAction;

    private URI responseAction;

    private QName request;

    private QName response;

    private Collection<Fault> faults = new LinkedList<>();

    public MetadataBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MetadataBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public MetadataBuilder setRequestAction(URI requestAction) {
        this.requestAction = requestAction;
        return this;
    }

    public MetadataBuilder setResponseAction(URI responseAction) {
        this.responseAction = responseAction;
        return this;
    }

    public MetadataBuilder setRequest(QName request) {
        this.request = request;
        return this;
    }

    public MetadataBuilder setRequest(String namespace, String localpart) {
        return setRequest(new QName(namespace, localpart));
    }

    public MetadataBuilder setResponse(QName response) {
        this.response = response;
        return this;
    }

    public MetadataBuilder setResponse(String namespace, String localpart) {
        return setResponse(new QName(namespace, localpart));
    }

    public MetadataBuilder setFaults(Collection<Fault> faults) {
        this.faults = new LinkedList<>(faults);
        return this;
    }

    public MetadataBuilder addFault(Fault fault) {
        this.faults.add(fault);
        return this;
    }

    public MetadataBuilder addFault(String name, URI action) {
        return addFault(new Fault(name, action));
    }

    public Metadata build() {
        return new Metadata(name, version, requestAction, responseAction, request, response, faults);
    }
}
