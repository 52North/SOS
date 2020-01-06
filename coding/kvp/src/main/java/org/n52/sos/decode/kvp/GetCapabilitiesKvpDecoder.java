/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.decode.kvp;

import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.OWSConstants.GetCapabilitiesParams;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.svalbard.decode.OperationDecoderKey;

/**
 * @since 4.0.0
 *
 */
public class GetCapabilitiesKvpDecoder extends AbstractSosKvpDecoder<GetCapabilitiesRequest> {

    public GetCapabilitiesKvpDecoder() {
        super(GetCapabilitiesRequest::new,
                new OperationDecoderKey(SosConstants.SOS, null, OWSConstants.Operations.GetCapabilities,
                        MediaTypes.APPLICATION_KVP),
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                        OWSConstants.Operations.GetCapabilities, MediaTypes.APPLICATION_KVP),
                new OperationDecoderKey(null, Sos2Constants.SERVICEVERSION, OWSConstants.Operations.GetCapabilities,
                        MediaTypes.APPLICATION_KVP),
                // FIXME isn't this the only one needed?
                new OperationDecoderKey(null, null, OWSConstants.Operations.GetCapabilities,
                        MediaTypes.APPLICATION_KVP));
    }

    @Override
    protected void getRequestParameterDefinitions(Builder<GetCapabilitiesRequest> builder) {
        builder.add(GetCapabilitiesParams.Sections, decodeList(GetCapabilitiesRequest::setSections));
        builder.add(GetCapabilitiesParams.updateSequence, GetCapabilitiesRequest::setUpdateSequence);
        builder.add(GetCapabilitiesParams.AcceptFormats, decodeList(GetCapabilitiesRequest::setAcceptFormats));
        builder.add(GetCapabilitiesParams.AcceptVersions, decodeList(GetCapabilitiesRequest::setAcceptVersions));
        builder.add(GetCapabilitiesParams.CapabilitiesId, GetCapabilitiesRequest::setCapabilitiesId);
    }

}
