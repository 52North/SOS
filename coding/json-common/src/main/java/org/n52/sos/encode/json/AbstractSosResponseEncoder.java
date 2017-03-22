/**
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
package org.n52.sos.encode.json;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractSosResponseEncoder<T extends AbstractServiceResponse> extends JSONEncoder<T> {
    public AbstractSosResponseEncoder(Class<T> type, String operation) {
        super(type, new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, operation,
                MediaTypes.APPLICATION_JSON));
    }

    public AbstractSosResponseEncoder(Class<T> type, Enum<?> operation) {
        this(type, operation.name());
    }

    @Override
    public JsonNode encodeJSON(T t) throws OwsExceptionReport {
        ObjectNode n = JSONUtils.nodeFactory().objectNode();
        n.put(JSONConstants.REQUEST, t.getOperationName());
        n.put(JSONConstants.VERSION, t.getVersion());
        n.put(JSONConstants.SERVICE, t.getService());
        encodeResponse(n, t);
        return n;
    }

    protected abstract void encodeResponse(ObjectNode json, T t) throws OwsExceptionReport;
}
