/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode.exi;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.janmayen.http.MediaTypes;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.ows.service.OwsOperationKey;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.sos.exi.EXIObject;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.OperationResponseEncoderKey;

/**
 * Abstract response encoder class for {@link EXIObject}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 */
public class ExiResponseEncoder extends ExiEncoder<OwsServiceResponse> implements Constructable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExiResponseEncoder.class);
    private EncoderKey key;
    private String version;
    private String service;
    private String operation;

    @Inject
    public void setVersion(String version) {
        this.version = version;
    }

    @Inject
    public void setService(String service) {
        this.service = service;
    }

    @Inject
    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public void init() {
        this.key
                = new OperationResponseEncoderKey(this.service, this.version, this.operation, MediaTypes.APPLICATION_EXI);
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", this.key);
    }

    @Override
    public Set<EncoderKey> getKeys() {
        return Collections.singleton(this.key);
    }

    @Override
    protected EncoderKey getKey(OwsServiceResponse response) {
        return new OperationResponseEncoderKey(new OwsOperationKey(response), getEncodedContentType(response));
    }

    @Override
    public String toString() {
        return String.format("%s{key=%s}", ExiResponseEncoder.class.getName(), key);
    }

}
