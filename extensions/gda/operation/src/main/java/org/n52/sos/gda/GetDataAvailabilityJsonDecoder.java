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
package org.n52.sos.gda;

import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.svalbard.coding.json.JSONConstants;
import org.n52.svalbard.coding.json.SchemaConstants;
import org.n52.svalbard.decode.json.AbstractSosRequestDecoder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class GetDataAvailabilityJsonDecoder extends AbstractSosRequestDecoder<GetDataAvailabilityRequest> {
    public GetDataAvailabilityJsonDecoder() {
        super(GetDataAvailabilityRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                GetDataAvailabilityConstants.OPERATION_NAME);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.GET_DATA_AVAILABILITY;
    }

    @Override
    protected GetDataAvailabilityRequest decodeRequest(JsonNode node) {
        GetDataAvailabilityRequest req = new GetDataAvailabilityRequest();
        if (node.has(JSONConstants.PROCEDURE)) {
            parseStringOrStringList(node.path(JSONConstants.PROCEDURE)).forEach(req::addProcedure);
        }
        if (node.has(JSONConstants.OBSERVED_PROPERTY)) {
            parseStringOrStringList(node.path(JSONConstants.OBSERVED_PROPERTY)).forEach(req::addObservedProperty);
        }
        if (node.has(JSONConstants.FEATURE_OF_INTEREST)) {
            parseStringOrStringList(node.path(JSONConstants.FEATURE_OF_INTEREST)).forEach(req::addFeatureOfInterest);
        }
        if (node.has(JSONConstants.OFFERING)) {
            parseStringOrStringList(node.path(JSONConstants.OFFERING)).forEach(req::addOffering);
        }
        return req;
    }
}
