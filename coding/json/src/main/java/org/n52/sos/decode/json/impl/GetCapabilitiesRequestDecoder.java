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
package org.n52.sos.decode.json.impl;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.JsonDecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.Operations;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetCapabilitiesRequestDecoder extends AbstractSosRequestDecoder<GetCapabilitiesRequest> {
    private static final Operations OP = SosConstants.Operations.GetCapabilities;

    private static final MediaType MT = MediaTypes.APPLICATION_JSON;

    private static final String V2 = Sos2Constants.SERVICEVERSION;

    private static final String V1 = Sos1Constants.SERVICEVERSION;

    private static final String SOS = SosConstants.SOS;

    public GetCapabilitiesRequestDecoder() {
        super(Sets.<DecoderKey> newHashSet(new JsonDecoderKey(GetCapabilitiesRequest.class), new OperationDecoderKey(
                SOS, null, OP, MT), new OperationDecoderKey(SOS, V2, OP, MT),
                new OperationDecoderKey(null, V2, OP, MT), new OperationDecoderKey(null, null, OP, MT),
                new OperationDecoderKey(SOS, V1, OP, MT), new OperationDecoderKey(null, V1, OP, MT)));
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.GET_CAPABILITIES;
    }

    @Override
    protected GetCapabilitiesRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        GetCapabilitiesRequest req = new GetCapabilitiesRequest();
        req.setAcceptFormats(parseStringOrStringList(node.path(JSONConstants.ACCEPT_FORMATS)));
        req.setAcceptVersions(parseStringOrStringList(node.path(JSONConstants.ACCEPT_VERSIONS)));
        req.setSections(parseStringOrStringList(node.path(JSONConstants.SECTIONS)));
        req.setUpdateSequence(node.path(JSONConstants.UPDATE_SEQUENCE).textValue());
        return req;
    }

}
