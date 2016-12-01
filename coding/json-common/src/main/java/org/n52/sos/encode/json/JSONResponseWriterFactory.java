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
package org.n52.sos.encode.json;


import org.n52.iceland.coding.encode.ResponseWriter;
import org.n52.iceland.coding.encode.ResponseWriterFactory;
import org.n52.iceland.coding.encode.ResponseWriterKey;
import org.n52.janmayen.component.SingleTypeComponentFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * {@link ResponseWriterFactory} implementation for {@link JsonNode} and
 * {@link JSONResponseWriter}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public class JSONResponseWriterFactory
        implements ResponseWriterFactory,
                   SingleTypeComponentFactory<ResponseWriterKey, ResponseWriter<?>> {

    private static final ResponseWriterKey RESPONSE_WRITER_KEY
            = new ResponseWriterKey(JsonNode.class);

    @Override
    public ResponseWriterKey getKey() {
        return RESPONSE_WRITER_KEY;
    }

    @Override
    public JSONResponseWriter create() {
        return new JSONResponseWriter();
    }

}
