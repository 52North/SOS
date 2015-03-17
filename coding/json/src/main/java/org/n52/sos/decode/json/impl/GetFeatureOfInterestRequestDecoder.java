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

import java.util.List;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetFeatureOfInterestRequestDecoder extends AbstractSosRequestDecoder<GetFeatureOfInterestRequest> {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(GetFeatureOfInterestRequestDecoder.class);

    public GetFeatureOfInterestRequestDecoder() {
        super(GetFeatureOfInterestRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.GetFeatureOfInterest);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.GET_FEATURE_OF_INTEREST;
    }

    @Override
    protected GetFeatureOfInterestRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        GetFeatureOfInterestRequest req = new GetFeatureOfInterestRequest();
        req.setFeatureIdentifiers(decodeFeatureOfInterests(node));
        req.setProcedures(decodeProcedures(node));
        req.setObservedProperties(decodeObservedProperties(node));
        req.setSpatialFilters(decodeSpatialFilters(node));
        req.setTemporalFilters(decodeTemporalFilters(node));
        return req;
    }

    private List<SpatialFilter> decodeSpatialFilters(JsonNode node) throws OwsExceptionReport {
        JsonNode path = node.path(JSONConstants.SPATIAL_FILTER);
        return decodeJsonToObjectList(path, SpatialFilter.class);
    }

    private List<TemporalFilter> decodeTemporalFilters(JsonNode node) throws OwsExceptionReport {
        JsonNode path = node.path(JSONConstants.TEMPORAL_FILTER);
        return decodeJsonToObjectList(path, TemporalFilter.class);
    }

    private List<String> decodeObservedProperties(JsonNode node) {
        JsonNode path = node.path(JSONConstants.OBSERVED_PROPERTY);
        return parseStringOrStringList(path);
    }

    private List<String> decodeProcedures(JsonNode node) {
        JsonNode path = node.path(JSONConstants.PROCEDURE);
        return parseStringOrStringList(path);
    }

    private List<String> decodeFeatureOfInterests(JsonNode node) {
        JsonNode path = node.path(JSONConstants.IDENTIFIER);
        return parseStringOrStringList(path);
    }
}
