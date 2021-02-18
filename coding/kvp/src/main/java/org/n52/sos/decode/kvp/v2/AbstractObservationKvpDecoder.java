/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.decode.kvp.v2;

import java.util.Collection;
import java.util.function.Supplier;

import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosConstants.Operations;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.sos.decode.kvp.AbstractSosKvpDecoder;
import org.n52.svalbard.decode.DecoderKey;

public abstract class AbstractObservationKvpDecoder<R extends AbstractObservationRequest>
        extends AbstractSosKvpDecoder<R> {

    public AbstractObservationKvpDecoder(Supplier<? extends R> supplier, DecoderKey... keys) {
        super(supplier, keys);
    }

    public AbstractObservationKvpDecoder(Supplier<? extends R> supplier, String version, Operations operation) {
        super(supplier, version, operation);
    }

    public AbstractObservationKvpDecoder(Supplier<? extends R> supplier, Collection<? extends DecoderKey> keys) {
        super(supplier, keys);
    }

    public AbstractObservationKvpDecoder(Supplier<? extends R> supplier, String service, String version,
            String operation) {
        super(supplier, service, version, operation);
    }

    public AbstractObservationKvpDecoder(Supplier<? extends R> supplier, String service, String version,
            Enum<?> operation) {
        super(supplier, service, version, operation);
    }

    public AbstractObservationKvpDecoder(Supplier<? extends R> supplier, String version, String operation) {
        super(supplier, version, operation);
    }

    public AbstractObservationKvpDecoder(Supplier<? extends R> supplier, String version, Enum<?> operation) {
        super(supplier, version, operation);
    }

    @Override
    protected void getRequestParameterDefinitions(Builder<R> builder) {
        builder.add(SosConstants.GetObservationParams.resultType, AbstractObservationRequest::setResultModel);
        builder.add(SosConstants.GetObservationParams.responseFormat, AbstractObservationRequest::setResponseFormat);
    }

}
