/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
import java.util.Collections;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class WSDLOperation {

    private final String name;
    private final String version;
    private final URI requestAction;
    private final URI responseAction;
    private final QName request;
    private final QName response;
    private final Collection<WSDLFault> faults;

    public WSDLOperation(String name, String version, URI requestAction, URI responseAction, QName request,
            QName response, Collection<WSDLFault> faults) {
        this.name = name;
        this.version = version;
        this.requestAction = requestAction;
        this.responseAction = responseAction;
        this.request = request;
        this.response = response;
        this.faults = faults;
    }

    public String getName() {
        return name;
    }

    public URI getRequestAction() {
        return requestAction;
    }

    public URI getResponseAction() {
        return responseAction;
    }

    public QName getRequest() {
        return request;
    }

    public QName getResponse() {
        return response;
    }

    public String getVersion() {
        return version;
    }

    public Collection<WSDLFault> getFaults() {
        return Collections.unmodifiableCollection(faults);
    }

    public static WSDLOperationBuilder newWSDLOperation() {
        return new WSDLOperationBuilder();
    }
}
