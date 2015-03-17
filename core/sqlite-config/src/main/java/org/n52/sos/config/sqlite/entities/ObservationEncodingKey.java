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

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.n52.sos.encode.ResponseFormatKey;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 */
@Embeddable
public class ObservationEncodingKey implements Serializable {
    private static final long serialVersionUID = 1746777293931177130L;
    private String service;
    private String version;
    private String responseFormat;

    public ObservationEncodingKey(String service, String version, String responseFormat) {
        this.service = service;
        this.version = version;
        this.responseFormat = responseFormat;
    }

    public ObservationEncodingKey(ResponseFormatKey key) {
        this(key.getService(), key.getVersion(), key.getResponseFormat());
    }

    public ObservationEncodingKey() {
        this(null, null, null);
    }

    public String getService() {
        return service;
    }

    public ObservationEncodingKey setService(String service) {
        this.service = service;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ObservationEncodingKey setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getEncoding() {
        return responseFormat;
    }

    public ObservationEncodingKey setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
        return this;
    }
}
